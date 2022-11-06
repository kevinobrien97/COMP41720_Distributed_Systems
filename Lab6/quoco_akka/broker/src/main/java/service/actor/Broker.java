package service.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import java.util.List;

import java.util.ArrayList;
import service.messages.QuotationRequest;
import service.messages.QuotationResponse;
import service.messages.ApplicationRequest;
import service.messages.ApplicationResponse;
// import service.core.ClientInfo;
import java.util.HashMap;
import java.util.Map;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import service.messages.RequestDeadline;

public class Broker extends AbstractActor {
    // store all found Quotation services
    static List<ActorRef> actorRefs = new ArrayList<>();
    // cache to store ApplicationResponses alongside IDs
    static Map<Integer, ApplicationResponse> cache = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            // listen for strings
            .match(String.class,
                msg -> {
                    // ignore unless the message is register
                    if (!msg.equals("register")) return;

                    System.out.println("Adding "+getSender());
                    // testing for task 3
                    // getSender().tell(new QuotationRequest(1, new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1")), getSelf());
                    // add the actor to the list
                    actorRefs.add(getSender());
                }
            )
            // listen for ApplicationRequests
            .match(ApplicationRequest.class,
                application -> {
                    // add it to the list and instantiate the response
                    cache.put(application.getID(), new ApplicationResponse(application.getID(), application.getInfo()));

                    // loop through all quotation services
                    for (ActorRef ref : actorRefs) {
                        // send a QuotationRequest to each
                        ref.tell(
                            new QuotationRequest(application.getID(), application.getInfo()), getSelf()
                        );
                    }
                    // add a timer by creating instance of RequestDeadline that will be sent back to broker in 2 seconds
                    getContext().system().scheduler().scheduleOnce(
                        Duration.create(2, TimeUnit.SECONDS), getSelf(), new RequestDeadline(application.getID(), getSender()), getContext().dispatcher(), null); 
                }
            )

            // listen for Quotation Responses
            .match(QuotationResponse.class,
                response -> {
                    // from task 3
                    // System.out.println(response.getId() + " received");
                    // get the associated ID and the application, then add the quote to the application stored in the brokers cache
                    int applicationID = response.getId();
                    ApplicationResponse application = cache.get(applicationID);
                    application.getQuotations().add(response.getQuotation());
                }
            )
            
            // listen for RequestDeadlines that are returned 2 seconds after receiving an ApplicationRequest
            .match(RequestDeadline.class,
                deadline -> {
                    // get the actor that sent the request, the ID of the application, the application and send the completed application back to sender
                    int seed = deadline.getID();
                    ActorRef ref = deadline.getRef();
                    ApplicationResponse application = cache.get(seed);
                    ref.tell(application, getSelf());
                }
            )
            .build();
    }
}

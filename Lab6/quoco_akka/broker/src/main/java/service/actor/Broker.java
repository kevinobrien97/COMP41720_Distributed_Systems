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

    static List<ActorRef> actorRefs = new ArrayList<>();
    static Map<Integer, ApplicationResponse> cache = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class,
                msg -> {
                    if (!msg.equals("register")) return;

                    System.out.println("Adding "+getSender());
                    // getSender().tell(new QuotationRequest(1, new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1")), getSelf());
                    actorRefs.add(getSender());
                }
            )
            .match(ApplicationRequest.class,
                application -> {
                    cache.put(application.getID(), new ApplicationResponse(application.getID(), application.getInfo()));

                    for (ActorRef ref : actorRefs) {
                        ref.tell(
                            new QuotationRequest(application.getID(), application.getInfo()), getSelf()
                        );
                    }

                    getContext().system().scheduler().scheduleOnce(
                        Duration.create(2, TimeUnit.SECONDS), getSelf(), new RequestDeadline(application.getID(), getSender()), getContext().dispatcher(), null); 
                }
            )

            // .match(QuotationRequest.class,
            //     msg -> {
            //         for (ActorRef qService : actorRefs) {
            //             qService.tell(new QuotationRequest(1, new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1")), getSelf());
            //         }
            //     }
            // )
            .match(QuotationResponse.class,
                response -> {
                    // System.out.println(response.getId() + " received");
                    int applicationID = response.getId();
                    ApplicationResponse application = cache.get(applicationID);
                    application.getQuotations().add(response.getQuotation());
                }
            )
            
            .match(RequestDeadline.class,
                deadline -> {
                    int seed = deadline.getID();
                    ActorRef ref = deadline.getRef();
                    ApplicationResponse application = cache.get(seed);
                    ref.tell(application, getSelf());
                }
            )
            .build();
    }
}

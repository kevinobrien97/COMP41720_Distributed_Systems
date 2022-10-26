package service.actor;

import service.core.Quotation;
import service.core.QuotationService;
import akka.actor.AbstractActor;
import service.messages.QuotationRequest;
import service.messages.QuotationResponse;
import akka.actor.ActorRef;
import java.util.List;
import java.util.ArrayList;
import service.messages.QuotationRequest;
import service.messages.QuotationResponse;
import service.core.ClientInfo;


public class Broker extends AbstractActor {

    static List<ActorRef> actorRefs = new ArrayList<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class,
                msg -> {
                    if (!msg.equals("register")) return;
                        System.out.println("Adding "+getSender());
                        getSender().tell(new QuotationRequest(1, new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1")), getSelf());
                        actorRefs.add(getSender());
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
                msg -> {
                    System.out.println(msg.getId() + " received");
                }
            )
            .build();
    }
}

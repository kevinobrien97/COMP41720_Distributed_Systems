package service.actor;

import service.core.Quotation;
import service.core.QuotationService;
import akka.actor.AbstractActor;
import service.messages.QuotationRequest;
import service.messages.QuotationResponse;

public class Quoter extends AbstractActor {
    private QuotationService service;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            // search for quotation requests
            .match(QuotationRequest.class,
                msg -> {
                    // once found use the quotation service to generate a quotation
                    Quotation quotation =
                        service.generateQuotation(msg.getClientInfo());
                    // send the quotation back to sender of the quotation request 
                    getSender().tell(
                        new QuotationResponse(msg.getId(), quotation), getSelf());
                    }
            )
            // search for messages of Init class and return the quotation service
            .match(Init.class,
                msg -> {
                    service = msg.getQuotationService();
                }
            )   
            .build();
    } 
}

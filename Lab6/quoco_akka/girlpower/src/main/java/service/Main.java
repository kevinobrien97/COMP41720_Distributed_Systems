package service;

import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import service.girlpower.GPQService;
import service.actor.Init;
import service.actor.Quoter;


public class Main {
    // main method that the pom.xml points to as its main.class
    public static void main(String[] args) {
        // create the local actor system
        ActorSystem system = ActorSystem.create();
        // create and initialise the quoter actor
        ActorRef ref = system.actorOf(Props.create(Quoter.class), "girlpower");
        ref.tell(new Init(new GPQService()), null);

        // send message to the broker telling it about this quotation service
        ActorSelection selection = system.actorSelection("akka.tcp://default@127.0.0.1:2551/user/broker");
        selection.tell("register", ref);
    } 
}

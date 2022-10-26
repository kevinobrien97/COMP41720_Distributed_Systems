package service;

import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import service.actor.Broker;


public class Main {
    // main method that the pom.xml points to as its main.class
    public static void main(String[] args) {
        // create the local actor system
        ActorSystem system = ActorSystem.create();
        // create and initialise the broker actor
        ActorRef ref = system.actorOf(Props.create(Broker.class), "broker");
    } 
}

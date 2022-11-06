package service;

import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import service.actor.Client;
import akka.actor.ActorSelection;

public class Main {
    // main method that the pom.xml points to as its main.class
    public static void main(String[] args) {
        // create the local actor system
        ActorSystem system = ActorSystem.create();

        // create a link to broker that will be passed to the Client.java file 
        ActorSelection broker = system.actorSelection("akka.tcp://default@127.0.0.1:2551/user/broker");


        // create and initialise the client actor, passing it a link to the broker
        ActorRef ref = system.actorOf(Props.create(Client.class, broker), "client");
        // tell the client to send the clients to the broker
        ref.tell("send_clients", null);
    } 
}

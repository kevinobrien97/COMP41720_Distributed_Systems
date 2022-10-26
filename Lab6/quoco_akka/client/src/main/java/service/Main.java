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

        ActorSelection broker = system.actorSelection("akka.tcp://default@127.0.0.1:2551/user/broker");


        // create and initialise the client actor
        ActorRef ref = system.actorOf(Props.create(Client.class, broker), "client");
        ref.tell("send_clients", null);
    } 
}

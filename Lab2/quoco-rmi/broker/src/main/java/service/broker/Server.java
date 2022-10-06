package main.java.service.broker;

import service.core.BrokerService;
import service.core.Constants;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) {
        BrokerService bService = new LocalBrokerService();
        try {
            // creating the registry that all other objects will bind to
            Registry registry = null;
            registry = LocateRegistry.createRegistry(1099);
            

            // Create the Remote Object
            BrokerService brokerService = (BrokerService)
             UnicastRemoteObject.exportObject(bService,0);
            // Register the object with the RMI Registry
            registry.bind(Constants.BROKER_SERVICE, brokerService);

            System.out.println("STARTING BROKER SERVICE");
            while (true) {
                Thread.sleep(1000); 
            }
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }
}

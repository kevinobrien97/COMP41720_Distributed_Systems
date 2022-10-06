package service.auldfellas;

import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import service.auldfellas.AFQService;
import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;
import service.core.Constants;

public class Server {
    public static void main(String[] args) {
        // initialise the quotation service
        QuotationService afqService = new AFQService();
        String host;
        
        try {
            // use default word if no args passed, else use arg
            if (args.length == 0) {
                host= "localhost";
            }
            else {
                host = args[0];
            }
            // Connect to the RMI Registry - creating the registry will be the
            // responsibility of the broker.
            Registry registry = null;
            registry = LocateRegistry.getRegistry(host, 1099);

            // Create the Remote Object and cast as quotation service class
            QuotationService quotationService = (QuotationService)
             UnicastRemoteObject.exportObject(afqService,0);

            // create broker service instance so that I can register quotation service on the registry it creates
            BrokerService bService = (BrokerService) registry.lookup(Constants.BROKER_SERVICE);

            // Register the object with the RMI Registry
            bService.register(Constants.AULD_FELLAS_SERVICE, quotationService);
            // commented out code that registered directly with registry on same machine
            // registry.bind(Constants.AULD_FELLAS_SERVICE, quotationService);

            System.out.println("STOPPING AULDFELLAS SERVER SHUTDOWN");
            while (true) {
                Thread.sleep(1000); 
            }
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }
}

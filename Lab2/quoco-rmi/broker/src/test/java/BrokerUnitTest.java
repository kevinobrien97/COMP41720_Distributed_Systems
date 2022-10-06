package test.java;

import service.core.BrokerService;
import service.core.Constants;
import main.java.service.broker.LocalBrokerService;
import java.rmi.registry.Registry;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import service.core.Quotation;
import java.util.List;
import service.core.ClientInfo;
import java.util.LinkedList;


public class BrokerUnitTest {
    // declare registry variable
    private static Registry registry;
    
    // method to instantiate objects required for test (broker service, registry and then 
    // register broker service on registry)
    // different approach to Server files as cannot depend on registry being built by broker
    @BeforeClass
    public static void setup() {
        BrokerService bService = new LocalBrokerService();
        try {
            registry = null;
            registry = LocateRegistry.createRegistry(1099);
            BrokerService brokerService = (BrokerService)
            UnicastRemoteObject.exportObject(bService,0);
            // Register the object with the RMI Registry
            registry.bind(Constants.BROKER_SERVICE, brokerService);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }

    // test the connection by seeing if broker service has been bound to the registry
    @Test
    public void connectionTest() throws Exception {
        BrokerService brokerService = (BrokerService)
         registry.lookup(Constants.BROKER_SERVICE);
        assertNotNull(brokerService);
    }

    // use the bound broker service to invoke the get quotations method
    // the get quotations method checks all the other registered objects on the registry
    // there are no other bound objects (and therefore no other bound quotation services)
    // as the registry was only made in the before class method above
    // therefore testing for empty list - could instantiate quoatation services as well to see if quotes are returned
    // but this was not asked for
    @Test
    public void generateBrokerTest() throws Exception {
        BrokerService brokerService = (BrokerService)
         registry.lookup(Constants.BROKER_SERVICE);
        List<Quotation> quotations = new LinkedList<Quotation>();
        ClientInfo sampleInfo = new ClientInfo("Mary", 'F', 23, 8, 0, "09KY373");
        quotations = brokerService.getQuotations(sampleInfo);
        assertTrue(quotations.isEmpty());
    }
}

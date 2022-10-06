package test.java;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import service.core.Constants;
import service.core.QuotationService;
import service.girlpower.GPQService;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import service.core.ClientInfo;
import static org.junit.Assert.assertTrue;
import service.core.Quotation;


public class GirlpowerUnitTest {
    // declare registry variable
    private static Registry registry;

    // method to instantiate objects required for test (quotation service, registry and then 
    // register quotation service on registry)
    // different approach to Server files as cannot depend on registry being built by broker
    @BeforeClass
    public static void setup() {
        QuotationService gpqService = new GPQService();
        try {
            registry = LocateRegistry.createRegistry(1099);
            QuotationService quotationService = (QuotationService)
            UnicastRemoteObject.exportObject(gpqService,0);
            registry.bind(Constants.GIRL_POWER_SERVICE, quotationService);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }

    // test the connection by seeing if quotation service has been bound to the registry
    @Test
    public void connectionTest() throws Exception {
        QuotationService service = (QuotationService)
         registry.lookup(Constants.GIRL_POWER_SERVICE);
        assertNotNull(service);
    }

    // use the bound quotation service to invoke the generate quotation method
    // testing if the returned object has a type of service.core.Quotation
    @Test
    public void generateQuotationTest() throws Exception {
        QuotationService quotationService = (QuotationService)
         registry.lookup(Constants.GIRL_POWER_SERVICE);
        ClientInfo sampleGal = new ClientInfo("Biddy", 'F', 32, 0, 8, "18MN1532");
        Quotation quote = quotationService.generateQuotation(sampleGal);
        assertTrue(quote.getClass().getName() == "service.core.Quotation" );
    }
} 
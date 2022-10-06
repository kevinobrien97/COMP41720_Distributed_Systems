package test.java;

import java.rmi.registry.Registry;
import java.beans.Transient;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import service.core.ClientInfo;
import service.core.Constants;
import service.core.Quotation;
import service.core.QuotationService;
import service.auldfellas.AFQService;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class AuldfellasUnitTest {
    // declare registry variable
    private static Registry registry;

    // method to instantiate objects required for test (quotation service, registry and then 
    // register quotation service on registry)
    // different approach to Server files as cannot depend on registry being built by broker
    @BeforeClass
    public static void setup() {
        QuotationService afqService = new AFQService();
        try {
            registry = LocateRegistry.createRegistry(1099);
            QuotationService quotationService = (QuotationService)
            UnicastRemoteObject.exportObject(afqService,0);
            registry.bind(Constants.AULD_FELLAS_SERVICE, quotationService);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }

    // test the connection by seeing if quotation service has been bound to the registry
    @Test
    public void connectionTest() throws Exception {
        QuotationService service = (QuotationService)
         registry.lookup(Constants.AULD_FELLAS_SERVICE);
        assertNotNull(service);
    }

    // use the bound quotation service to invoke the generate quotation method
    // testing if the returned object has a type of service.core.Quotation
    @Test
    public void generateQuotationTest() throws Exception {
        QuotationService quotationService = (QuotationService)
         registry.lookup(Constants.AULD_FELLAS_SERVICE);
        ClientInfo sampleLad = new ClientInfo("Kevin", 'M', 63, 6, 0, "07MH874");
        Quotation quote = quotationService.generateQuotation(sampleLad);
        assertTrue(quote.getClass().getName() == "service.core.Quotation" );
    }
} 
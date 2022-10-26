import service.core.Quotation;
import service.core.QuotationService;
import service.messages.QuotationRequest;
import service.messages.QuotationResponse;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.junit.*;
import akka.testkit.javadsl.*;
import service.auldfellas.AFQService;
import service.actor.Init;
import service.actor.Quoter;
import service.core.ClientInfo;

public class AFQUnitTest {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }


    @Test
    public void testQuoter() {
        ActorRef quoterRef = system.actorOf(Props.create(Quoter.class), "test");
        TestKit probe = new TestKit(system);
        quoterRef.tell(new Init(new AFQService()), null);

        quoterRef.tell(new QuotationRequest(1, new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1")), probe.getRef());
        
        probe.awaitCond(probe::msgAvailable);
        probe.expectMsgClass(QuotationResponse.class);
    }    
}

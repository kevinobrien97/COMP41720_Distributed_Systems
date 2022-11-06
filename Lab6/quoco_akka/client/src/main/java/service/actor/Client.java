package service.actor;

import java.text.NumberFormat;
import akka.actor.AbstractActor;
import service.core.ClientInfo;
import service.core.Quotation;
import service.messages.ApplicationRequest;
import service.messages.ApplicationResponse;
import akka.actor.ActorSelection;

public class Client extends AbstractActor {
    // initialise the broker variable that will be used to contact the broker
    public ActorSelection broker;
    // unique IDs for applications
    static int SEED_ID = 0;

    // constructor so that the broker can be passed 
    public Client(ActorSelection broker) {
        this.broker = broker;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
        // listen for strings
        .match(String.class,
            msg -> {
                // if it isn't the correct keyword return
                if (!msg.equals("send_clients")) return;

                // send each client to the broker as part of a new application
                for (ClientInfo client : clients) {
                    ApplicationRequest request = new ApplicationRequest(SEED_ID++, client);
                    broker.tell(request, getSelf());
                }
            }
        )        

        // listen for ApplicationResponses
        .match(ApplicationResponse.class,
            response -> {
                // display the client info and quotations for the client
                ClientInfo info = response.getInfo();
                displayProfile(info);

                for (Quotation quotation : response.getQuotations()) {
                    displayQuotation(quotation);
                }
                System.out.println("\n");
            }
    )
    .build();
    }


    	/**
	 * Display the client info nicely.
	 * 
	 * @param info
	 */
	public static void displayProfile(ClientInfo info) {
		System.out.println("|=================================================================================================================|");
		System.out.println("|                                     |                                     |                                     |");
		System.out.println(
				"| Name: " + String.format("%1$-29s", info.getName()) + 
				" | Gender: " + String.format("%1$-27s", (info.getGender()==ClientInfo.MALE?"Male":"Female")) +
				" | Age: " + String.format("%1$-30s", info.getAge())+" |");
		System.out.println(
				"| License Number: " + String.format("%1$-19s", info.getLicenseNumber()) + 
				" | No Claims: " + String.format("%1$-24s", info.getNoClaims()+" years") +
				" | Penalty Points: " + String.format("%1$-19s", info.getPoints())+" |");
		System.out.println("|                                     |                                     |                                     |");
		System.out.println("|=================================================================================================================|");
	}

	/**
	 * Display a quotation nicely - note that the assumption is that the quotation will follow
	 * immediately after the profile (so the top of the quotation box is missing).
	 * 
	 * @param quotation
	 */
	public static void displayQuotation(Quotation quotation) {
		System.out.println(
				"| Company: " + String.format("%1$-26s", quotation.getCompany()) + 
				" | Reference: " + String.format("%1$-24s", quotation.getReference()) +
				" | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.getPrice()))+" |");
		System.out.println("|=================================================================================================================|");
	}
	
	/**
	 * Test Data
	 */
	public static final ClientInfo[] clients = {
		new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1"),
		new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 0, 2, "ABC123/4"),
		new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 16, 10, 0, "HMA304/9"),
		new ClientInfo("Rem Collier", ClientInfo.MALE, 44, 5, 3, "COL123/3"),
		new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 4, 7, "QUN987/4"),
		new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 5, 2, "XYZ567/9")		
	};
}

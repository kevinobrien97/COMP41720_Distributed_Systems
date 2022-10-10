package client;

import java.text.NumberFormat;

import service.core.ClientInfo;
import service.core.Quotation;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Session;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import service.message.QuotationRequestMessage;
import java.util.HashMap;
import java.util.Map;
import service.message.ClientApplicationMessage;

public class Main {

	// class variable that will be used to identify clients sent to broker
	static long SEED_ID = 0;
	// hashmap to associate seeds with clients
    static Map<Long, ClientInfo> cache = new HashMap<>();
	
	/**
	 * This is the starting point for the application. Here, we must
	 * get a reference to the Broker Service and then invoke the
	 * getQuotations() method on that service.
	 * 
	 * Finally, you should print out all quotations returned
	 * by the service.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// default value for host - so that it runs outside of a docker container
		String host = "localhost";
	
		// this will assign it the hostname provided by the docker-compose
		if (args.length > 0) {
			host = args[0];
		}
		try {
			// set up connection
			ConnectionFactory factory =
						new ActiveMQConnectionFactory("failover://tcp://"+host+":61616");
			Connection connection = factory.createConnection();
			connection.setClientID("client");
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE); 

			// set up the two queues that interact with the broker
			Queue applicationRequestQueue = session.createQueue("REQUEST");
            Queue completedApplicationQueue = session.createQueue("RESPONSES");

			MessageProducer applicationProducer = session.createProducer(applicationRequestQueue);
			MessageConsumer applicationConsumer = session.createConsumer(completedApplicationQueue); 

			// initial code for part 3 commented out
			// Queue queue = session.createQueue("QUOTATIONS");
			// Topic topic = session.createTopic("APPLICATIONS");
			// MessageProducer producer = session.createProducer(topic);
			// MessageConsumer consumer = session.createConsumer(queue); 
			connection.start();

			// loop over the clients to send each to the broker via the REQUEST queue
			for (ClientInfo client : clients) {
				// generate a QuotationRequestMessage with a unique ID and the client details
				QuotationRequestMessage quotationRequest = new QuotationRequestMessage(SEED_ID++, client);
				Message request = session.createObjectMessage(quotationRequest);
				// add the ID and client as an object to the cache
				cache.put(quotationRequest.id, quotationRequest.info);
				applicationProducer.send(request);
			}
			
			// continuously search for responses on the RESPONSES queue once all clients sent to broker
			while (true) {
				Message message = applicationConsumer.receive();
				if (message instanceof ObjectMessage) {
					Object content = ((ObjectMessage) message).getObject();
					if (content instanceof ClientApplicationMessage) {
						ClientApplicationMessage response = (ClientApplicationMessage) content;
						// get the client info for the ID being dealt with 
						ClientInfo info = cache.get(response.id);
						// display the client info and successively show each quote
						displayProfile(info);
						for (Quotation quote : response.quotations) {
							displayQuotation(quote);
						}
						System.out.println("\n");
					}
					// remove from queue
					message.acknowledge();
				} else {
					System.out.println("Unknown message type received in Client: " +
					message.getClass().getCanonicalName());
				} 
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}
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
				"| Name: " + String.format("%1$-29s", info.name) + 
				" | Gender: " + String.format("%1$-27s", (info.gender==ClientInfo.MALE?"Male":"Female")) +
				" | Age: " + String.format("%1$-30s", info.age)+" |");
		System.out.println(
				"| License Number: " + String.format("%1$-19s", info.licenseNumber) + 
				" | No Claims: " + String.format("%1$-24s", info.noClaims+" years") +
				" | Penalty Points: " + String.format("%1$-19s", info.points)+" |");
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
				"| Company: " + String.format("%1$-26s", quotation.company) + 
				" | Reference: " + String.format("%1$-24s", quotation.reference) +
				" | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price))+" |");
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

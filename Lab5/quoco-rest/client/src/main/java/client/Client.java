package client;

import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;

import org.springframework.http.HttpEntity;
import java.util.HashMap;
import java.util.Map;
import service.core.ClientApplication;
import service.core.ClientInfo;
import service.core.Quotation;

// this solution simply prints out the output of the post requests which contains the quotations
// it would also be feasible to use the cache to send GET requests for each ID to print out the applications, or request all applications
public class Client {

	// hashmap to associate an ID with the ClientApplication - not used in this solution but it is easy to imagine use cases in an extended application
	static Map<Long, ClientApplication> clientCache = new HashMap<>();
    public static void main(String[] args) {
		String host = "localhost";
            int port = 8080;
            // More Advanced flag-based configuration

            // in the docker-compose it will be passed the broker localhost
            try {
              
                int i = 0;
                while (i < args.length) {
                    String flag = args[i++];
                    switch (flag) {
                        case "-h":
                            host = args[i++];
                            break;
                        case "-p":
                            port = Integer.parseInt(args[i++]);
                            break;
                        default:
                            throw new Exception("Invalid Argument: " + flag);
                    }
                }
            } catch (Throwable th) {
                System.out.println( "\nThis program only accepts:\n\n"+
                "-h <hostname>\tChange the default hostname fo the WSDL document\n"+
                "-p <port>\tChange the default port for the WSDL document.");
                System.out.println("Issue: " + th.getMessage());
                System.exit(0);
            }


		// set up the rest template
        RestTemplate restTemplate = new RestTemplate();
		// loop through the clients
		for (ClientInfo info : clients) {
			HttpEntity<ClientInfo> request = new HttpEntity<>(info);
			try {
				// send the client to the broker
				ClientApplication quotes =
					restTemplate.postForObject("http://"+host+":"+port+"/applications",
						request, ClientApplication.class);
				// if a quote has been returned
				if (quotes!=null) {
					// adding completed applications to cache - don't actually use them in this solution but it is easy to imagine use cases in an extended application
					clientCache.put(quotes.id, quotes);
					displayProfile(info);
					// display each quotation
					for (Quotation quote : quotes.quotations) {
						displayQuotation(quote);
					}
				}
				else {
					System.out.println("Nothing returned");
				}
			} catch(Exception exception) {
				System.out.println("Error: " + exception);
			}
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

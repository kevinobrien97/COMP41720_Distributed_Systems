import service.core.ClientInfo;
import service.core.Quotation;
import service.core.BrokerService;
import service.core.ClientInfo;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.text.NumberFormat;


public class Client {
    public static void main(String[] args) {
        try {
            // when running client via the docker compose file it is necessary to sleep the client 
            Thread.sleep(15000);
            String host = "0.0.0.0";
            int port = 9000;
            // More Advanced flag-based configuration

            // in the docker-compose it will be passed the broker localhost
            // if running it using the client image the local network will be passed using the command mentiond in the readme
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
        
        
            // set up access to the broker
            URL wsdlUrl = new
                URL("http://" + host + ":" + port + "/broker?wsdl");

            QName serviceName =
                new QName("http://core.service/", "BrokerService");

            Service service = Service.create(wsdlUrl, serviceName);

            QName portName = new QName("http://core.service/", "BrokerPort");
            BrokerService bService =
                service.getPort(portName, BrokerService.class);
            
            // loop through each of the clients
            for (ClientInfo info : clients) {
                displayProfile(info);
                
                // pass the broker a client and loop through the returned quotations to show them on screen
                for (Quotation quotation : bService.generateQuotations(info)) {
                    displayQuotation(quotation);
                }
                System.out.println("\n");
            }
        } catch (Exception e) {
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

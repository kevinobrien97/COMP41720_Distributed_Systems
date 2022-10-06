package service.core;

import service.core.AbstractQuotationService;
import service.core.ClientInfo;
import service.core.Quotation;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.jws.WebMethod;
import javax.xml.ws.Endpoint;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import javax.jmdns.JmDNS;
import java.net.InetAddress;
import javax.jmdns.ServiceInfo;

/**
 * Implementation of the AuldFellas insurance quotation service.
 * 
 * @author Rem
 *
 */

@WebService
@SOAPBinding(style=Style.RPC, use=Use.LITERAL)
public class Quoter extends AbstractQuotationService {
	// All references are to be prefixed with an AF (e.g. AF001000)
	public static final String PREFIX = "AF";
	public static final String COMPANY = "Auld Fellas Ltd.";

	public static void main(String[] args) {
		try {
			// default value for host - so that it runs outside of a docker container
			String host = "localhost";
			
			// this will assign it the hostname provided by the docker-compose
			if (args.length > 0) {
				host = args[0];
			}

			// create the webservice on a unique port
			Endpoint endpoint = Endpoint.create(new Quoter());
			HttpServer server = HttpServer.create(new InetSocketAddress(9001), 5);
			server.setExecutor(Executors.newFixedThreadPool(5));
			HttpContext context = server.createContext("/quotation");
			endpoint.publish(context);
			server.start();

			// it will take some time for the endpoint to be created and therefore it will be a few seconds before it is advertised
			// therefore in the client it is forced to sleep before it looks to iterate over the services that have been located

			// advertise the service using below method so that the broker can find it and use the quotation service via the client
			jmdnsAdvertise(host);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// method to advertise the webservice using jmdns so that broker can pick it up
	private static void jmdnsAdvertise(String host) {
		try {

			// url to host the webservice
			String config = "path=http://"+host+":9001/quotation?wsdl";
			// invoke an instance of JmDNS that will be used to advertise the quotation service
			JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
	
			// Register a service
			ServiceInfo serviceInfo =
					ServiceInfo.create("_http._tcp.local.", "afq", 9001, config);

			// have manually provided the link to ServiceInfo, however the below relative link could also be used to avoid passing host names
			// ServiceInfo serviceInfo = ServiceInfo.create(
            // "_http._tcp.local.", "gp", 9001, "path=/quotation?wsdl"
        	// );
			jmdns.registerService(serviceInfo);
	
			// Wait a bit
			Thread.sleep(100000);
	
			// Unregister all services
			jmdns.unregisterAllServices();
		} catch (Exception e) {
			System.out.println("Problem Advertising Service: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Quote generation:
	 * 30% discount for being male
	 * 2% discount per year over 60
	 * 20% discount for less than 3 penalty points
	 * 50% penalty (i.e. reduction in discount) for more than 60 penalty points 
	 */
	@WebMethod
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 600 and 1200
		double price = generatePrice(600, 600);
		
		// Automatic 30% discount for being male
		int discount = (info.gender == ClientInfo.MALE) ? 30:0;
		
		// Automatic 2% discount per year over 60...
		discount += (info.age > 60) ? (2*(info.age-60)) : 0;
		
		// Add a points discount
		discount += getPointsDiscount(info);
		
		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
	}

	private int getPointsDiscount(ClientInfo info) {
		if (info.points < 3) return 20;
		if (info.points <= 6) return 0;
		return -50;
	}

}

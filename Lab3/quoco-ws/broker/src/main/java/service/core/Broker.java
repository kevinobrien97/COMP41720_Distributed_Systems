package service.core;

import java.util.LinkedList;
import java.util.List;
import service.core.ClientInfo;
import service.core.Quotation;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.jws.WebMethod;
import javax.xml.ws.Endpoint;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.net.URL;
import java.net.UnknownHostException;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;


/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem
 *
 */
@WebService
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL)
public class Broker {

	// creating a class variable to store list of URLs that are advertised and discovered via jdmns
	// and then looped over to get quotations
	static List<URL> urls = new LinkedList<URL>();

	public static void main(String[] args) {
		try {

			// create the broker webservice on a unique port
			Endpoint endpoint = Endpoint.create(new Broker());
			HttpServer server = HttpServer.create(new InetSocketAddress(9000), 5);
			server.setExecutor(Executors.newFixedThreadPool(5));
			HttpContext context = server.createContext("/broker");
			endpoint.publish(context);
			server.start();

			// the broker needs to discover (i.e. listen for) the quotation services that are advertised in each of the QS projects
			try {
				// Create a JmDNS instance
				JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
				// Add a service listener, using the class defined below
				jmdns.addServiceListener("_http._tcp.local.", new JmdnsClient());
				// Wait a bit
				Thread.sleep(30000);

			// explicitly catch certain exceptions - didn't bother adding to QSs
			} catch (UnknownHostException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// when this class is used above the below serviceResolved method is automatically invoked and listens for all advertised services
	// some of the print statements can lead to a somewhat messy output but they can be commented out as they are just informative
	public static class JmdnsClient implements ServiceListener {
		@Override
		public void serviceAdded(ServiceEvent event) {
			System.out.println("Service added: " + event.getInfo());
		}

		@Override
		public void serviceRemoved(ServiceEvent event) {
			System.out.println("Service removed: " + event.getInfo());
		}

		@Override
		public void serviceResolved(ServiceEvent event) {
			// when a service has been it is passed to this method
			System.out.println("Service resolved: " + event.getInfo());
			// putting the service into a String type to be later converted to a URL
			String path = event.getInfo().getPropertyString("path");
			if (path != null) {
				try {
					// converting the path to a URL
					URL newUrl = new URL(path);
					// only adding the path if it has not already been discovered before
					// failing to do this check results in many quotes from the same quotation service being returned
					if (!urls.contains(newUrl)) {
						urls.add(newUrl);
					}
				} catch (Exception e) {
					System.out.println("Problem with service: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	// the broker will be passed an instance of the ClientInfo class, and it must then look for a quotation from each QS for this instance
	@WebMethod
	public List<Quotation> generateQuotations(ClientInfo info) {
		// new list of quotations for each instance
		// will add quotations from each QS to this
		List<Quotation> quotations = new LinkedList<Quotation>();

		// hardcoded port list used for task 3
		// int[] ports = new  int[]{9001,9002,9003};

		try {
			// for task 3
			// for (int port : ports) {
			// 	URL url = new URL("http://localhost:" + port + "/quotation?wsdl");

			// looping through list of URLs that were discovered by ServiceListener and added to list by serviceResolved method
			for (URL url: urls) {
				// accessing the quotation service
				QName serviceName = new QName("http://core.service/", "QuoterService");
				Service service = Service.create(url, serviceName);
	
				QName portName = new QName("http://core.service/", "QuoterPort");

				QuoterService serviceQuote = 
					service.getPort(portName, QuoterService.class);
				
				// generate a quotation from the QS and add it the list of quotations which will be returned once the loop is complete
				quotations.add(serviceQuote.generateQuotation(info));
			}
            
        } catch (Exception e) {
            System.out.println("Trouble: " + e.getMessage());
            e.printStackTrace();
        }
		
		return quotations;
	
	}
}

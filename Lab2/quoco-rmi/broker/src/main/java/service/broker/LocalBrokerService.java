package main.java.service.broker;

import java.util.LinkedList;
import java.rmi.NotBoundException;
import java.util.List;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.lang.String;

/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem
 *
 */
public class LocalBrokerService implements BrokerService {
	public List<Quotation> getQuotations(ClientInfo info) throws RemoteException, NotBoundException {
		List<Quotation> quotations = new LinkedList<Quotation>();

		// getting registry as opposed to having a constructor and passing it the registry
		String host= "localhost";
		Registry registry = LocateRegistry.getRegistry(host, 1099);

		// looping through the objects bound to the registry beginning with "qs" and calling generate quote method
		// will be all of the quotation services based on the constants used
		for (String name : registry.list()) {
			if (name.startsWith("qs-")) {
				QuotationService service = (QuotationService)registry.lookup(name);
				quotations.add(service.generateQuotation(info));
			}
		}
		return quotations;
	}

	// method to register objects when using >1 Docker images
	// unable to directly bind objects on a different machine so use proxy
	public void register(String serviceName, Remote service) throws RemoteException {
		String host= "localhost";
		Registry registry = LocateRegistry.getRegistry(host, 1099);

		// bind the passed object to registry 
		try {
			registry.bind(serviceName, service);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
}

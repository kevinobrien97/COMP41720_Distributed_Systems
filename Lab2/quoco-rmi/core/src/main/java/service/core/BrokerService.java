package service.core;

import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.lang.String;
import java.rmi.NotBoundException;

/**
 * Interface for defining the behaviours of the broker service
 * @author Rem
 *
 */
public interface BrokerService extends Remote {
	public List<Quotation> getQuotations(ClientInfo info) throws RemoteException, NotBoundException;
	public void register(String serviceName, Remote service) throws RemoteException;
}

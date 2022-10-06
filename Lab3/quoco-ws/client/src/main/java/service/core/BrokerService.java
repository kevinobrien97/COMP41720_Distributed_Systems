package service.core;

import javax.jws.WebService;
import javax.jws.WebMethod;
import service.core.ClientInfo;
import java.util.List;

// interface defines what a BrokerService can do and allows us to access the generateQuotations method from the broker
@WebService
public interface BrokerService {
    @WebMethod List<Quotation> generateQuotations(ClientInfo info);
    
}

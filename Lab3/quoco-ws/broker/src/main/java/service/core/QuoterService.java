package service.core;

import javax.jws.WebService;
import javax.jws.WebMethod;
import service.core.ClientInfo;

// interface defines what a QuoterService can do and allows us to access the generateQuotation method from the quotation services
@WebService
public interface QuoterService {
    @WebMethod Quotation generateQuotation(ClientInfo info);
}
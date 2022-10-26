package service.messages;
import service.core.ClientInfo;
import service.core.Quotation;

import java.util.ArrayList;
import java.util.List;

public class ApplicationResponse implements MyInterface {
    private ClientInfo info;
    private List<Quotation> quotations;
    private int id;

    public ApplicationResponse(int id, ClientInfo info) {
        this.quotations = new ArrayList<>();
        this.info = info;
        this.id = id;
    }
    
    public ApplicationResponse(){}

    public ClientInfo getInfo() {
        return this.info;
    }

    public void setInfo(ClientInfo info) {
        this.info = info;
    }

    public List<Quotation> getQuotations() {
        return this.quotations;
    }

    public void setQuotations(List<Quotation> quotations) {
        this.quotations = quotations;
    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }
}

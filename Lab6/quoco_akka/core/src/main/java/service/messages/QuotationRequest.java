package service.messages;
import service.core.ClientInfo;

public class QuotationRequest implements MyInterface {
    private int id;
    private ClientInfo clientInfo;
    public QuotationRequest(int id, ClientInfo clientInfo) {
        this.id = id;
        this.clientInfo = clientInfo;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo info) {
        this.clientInfo = info;
    }
} 

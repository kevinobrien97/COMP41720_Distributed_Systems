package service.message;

import java.io.Serializable;
import service.core.ClientInfo;

public class QuotationRequestMessage implements Serializable {
    public long id;
    public ClientInfo info;

    public QuotationRequestMessage(long id, ClientInfo info) {
        this.id = id;
        this.info = info;
    }
}
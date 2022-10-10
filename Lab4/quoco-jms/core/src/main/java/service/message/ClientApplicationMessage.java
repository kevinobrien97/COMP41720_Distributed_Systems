package service.message;

import java.io.Serializable;
import service.core.ClientInfo;
import service.core.Quotation;

import java.util.ArrayList;
import java.util.List;

public class ClientApplicationMessage implements Serializable {
    public ClientInfo info;
    public long id;
    public List<Quotation> quotations;

    public ClientApplicationMessage(long id) {
        this.quotations = new ArrayList<>();
        this.id = id;
    }
}

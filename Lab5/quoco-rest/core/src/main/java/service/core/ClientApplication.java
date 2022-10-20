package service.core;

import java.util.ArrayList;

public class ClientApplication {
    public long id;
    public ClientInfo info;
    public ArrayList<Quotation> quotations;

    public ClientApplication(long id, ClientInfo info) {
        this.quotations = new ArrayList<>();
        this.id = id;
        this.info = info;
    }

    public ClientApplication(){}
}
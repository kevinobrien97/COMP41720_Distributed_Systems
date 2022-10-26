package service.messages;
import service.core.ClientInfo;

public class ApplicationRequest implements MyInterface {
    private ClientInfo info;
    private int id;

    public ApplicationRequest(int id, ClientInfo info) {
        this.id = id;
        this.info = info;
    }
    public ApplicationRequest(){}

    public ClientInfo getInfo() {
        return this.info;
    }

    public void setInfo(ClientInfo info) {
        this.info = info;
    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }
}

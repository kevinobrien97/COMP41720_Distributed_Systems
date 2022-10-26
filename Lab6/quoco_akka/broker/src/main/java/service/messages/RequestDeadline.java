package service.messages;

import akka.actor.ActorRef;

public class RequestDeadline {
    private int SEED_ID;
    private ActorRef ref; 

    public RequestDeadline(int id, ActorRef ref) {
        this.SEED_ID = id;
        this.ref = ref;
    }

    public int getID() {
        return this.SEED_ID;
    }

    public void setID(int id) {
        this.SEED_ID = id;
    }

    public ActorRef getRef(){
        return this.ref;
    }

    public void setRef(ActorRef ref) {
        this.ref = ref;
    }

}

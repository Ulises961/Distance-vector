
public interface Deliverable<M> {

    void send(M message);
    void receive(M message);

    void subscribe(Deliverer<Message> inbox);

    void unsubscribe(Deliverer<Message> inbox);
}

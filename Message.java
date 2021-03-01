
public class Message {
    private final int cost;
    private final Table table;
    private final Node receiver;
    private final Node sender;

    public Table getTable() {
        return table;
    }

    public Node getReceiver() {
        return receiver;
    }


    public Node getSender() {
        return sender;
    }

    public Message(int cost, Table table, Node sender, Node receiver) {
        this.cost = cost;
        this.table = table;
        this.receiver = receiver;
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "cost=" + cost +
                ", table=" + table +
                ", receiver=" + receiver +
                '}';
    }

    public int getCost() {
        return cost;
    }

}

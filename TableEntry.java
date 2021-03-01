
public class TableEntry {


    private Node nextNode;
    private int cost;

    public TableEntry( Node nextNode, int cost) {

        this.nextNode = nextNode;
        this.cost = cost;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }



    @Override
    public String toString() {
        return String.format("%s" , nextNode);
    }



}

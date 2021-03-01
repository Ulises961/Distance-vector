
public class Link  {
    
    private final Node node1;
    private final Node node2;
    private final int cost;

   public Link(Node node1, Node node2, int cost){
    this.node1 = node1;
    this.node2 = node2;
    this.cost = cost;
    }
    public int getCost() {
        return cost;
    }
    public Node getNode1() {
        return node1;
    }
    public Node getNode2() {
        return node2;
    }

    public boolean contains(Node currentNode){
        
        if (this.getNode1().getName().equals(currentNode.getName()))
        return true;

        return this.getNode2().getName().equals(currentNode.getName());

    }
    public Node getNextHop( Node currentNode){
        if (node1.getName().equals(currentNode.getName()))
            return node2;
        return node1;
    }
    @Override
    public String toString() {
       
        return node1 +  " " + node2 + " " + cost;
    }

  
    
}

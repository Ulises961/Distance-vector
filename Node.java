
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Node implements Deliverable<Message>{

    private final String name;

    private Table table;

    List<Link> graph;

    private Deliverer<Message> mailbox;

    private List<Node> directNeighbours;

    public Node (String node){
        this.name = node;

        directNeighbours = new LinkedList<>();

        table = new Table();
    }

    public String getName() {

        return name;
    }

    @Override
    public String toString() {

        return  name ;
    }

    @Override
    public int hashCode() {

        final int prime = 31;

        int result = 1;

        result = prime * result + ((name == null) ? 0 : name.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)

            return true;

        if (obj == null)

            return false;

        if (getClass() != obj.getClass())

            return false;

        Node other = (Node) obj;

        if (name == null) {

            return other.name == null;

        } else return name.equals(other.name);
    }

    private void setDirectNeighbours( List<Link> graph) {

        Node directNeighbour;

        for (Link link : graph) {

            if (link.contains(this)){

                directNeighbour = link.getNextHop(this);

                directNeighbours.add(directNeighbour);

                table.put(directNeighbour, new TableEntry( directNeighbour,link.getCost()));
            }

        }
        directNeighbours = directNeighbours.stream().distinct().collect(Collectors.toList());

    }



    public void initNode(List<Link> graph, List<Node> finalNodes){

        this.graph = graph;

        table.put(this, new TableEntry(new Node("direct"), 0));
        setDirectNeighbours(graph);

        table.initTable(finalNodes);

    }

    public  void sendMessages(){

        for(Node receiver : directNeighbours){

            int costToSend = graph.stream().filter(x -> x.contains(receiver) && x.contains(this)).findFirst().get().getCost();

            Message message = new Message(costToSend,table, this, receiver);
            System.out.printf("\nNode %s is sending its table to Node %s\n\n", this,receiver);
            send(message);

        }
    }

    @Override
    public void subscribe(Deliverer<Message> inbox) {
        this.mailbox = inbox;
        ((Mailbox)inbox).addToMailingList(this);
    }

    @Override
    public void unsubscribe(Deliverer<Message> inbox) {
        this.mailbox = null;
        ((Mailbox)inbox).removeFromMailingList(this);

    }

    public void send( Message message){

        mailbox.receive(message);
    }

    @Override
    public void receive(Message message) {

        int costToReceive = message.getCost();

        Table foreignTable = message.getTable();

        Node sender = message.getSender();

        updateTable(costToReceive,foreignTable, sender);

    }

    public void updateTable(int costToReceive, Table foreignTable, Node sender){

            foreignTable.forEach((key,entryToCompare) -> {

                TableEntry originalEntry = table.get(key);

                if(!( entryToCompare == null || entryToCompare.getCost() == 0)){

                    int netCost = entryToCompare.getCost() + costToReceive;

                    if( originalEntry == null){

                        originalEntry = new TableEntry(sender,netCost);

                        originalEntry.setCost( netCost);
                    }

                    else if( originalEntry.getCost() > netCost){

                        originalEntry.setNextNode(sender);

                        originalEntry.setCost(netCost);
                    }
                }
                table.replace(key, originalEntry);
            });

        System.out.printf("The updated table of %s is\n %s",this,table);
        }


    public  void printToFile() {

        String file = "./" + this.getName() + ".txt";

        try (Writer writer = new BufferedWriter(new FileWriter(file))) {

            table.forEach((k, v) -> {

                if (v != null) {

                    try {

                        writer.write(k + " "+v.toString()+"\n");

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

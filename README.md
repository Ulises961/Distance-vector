# Distance-vector

In this project I try to implement the algorithm used to compute the path of a network using the Distance vector
approach. This is done through message passing and by comparison of the own table with the ones received from the adjacent
nodes. In this assignment we try to emulate the messaging of the nodes, the comparison of the tables and the update of the
information.
The resulting program computes successfully the Distance Vector algorithm using message passing implemented in java.
The structure of the program partially follows the “observer” programming pattern and implements some interfaces that
possibly helps to create some modularity besides making it extensible.
## METHODOLOGY
To emulate the state of a network we have used simple table with a pair of letters that represent the nodes and an integer
that represent the cost to go from one letter to the next one, this table is a .txt file that is passed as an argument when we
initialize the program, we pass as well another .txt file that represents the sequence in which the nodes of the network send their own table, with this two pieces of information we proceed to discover the topology of our network. To this end we declare
several classes that will represent the different elements of this communication between nodes. Let us see:
### Node 
    This class represents the actual node or router of a network, it contains the following fields.
1. Name
    A String that uniquely identifies the node.
1. Table
    A HashMap<Node, TableEntry> key = Nodes in the graph, value = the shortest path to reach the node
represented by the key.
1. Mailbox:  
    An instance of Mailbox class where it receives and from which it sends the messages to the adjacent nodes.
1. Graph: 
    The topology of the original graph with the costs of all the links involved.
### Table 
    This class represents the table that stores the best paths to the nodes stored in the Node. It is a wrapper class of a HashMap<K,V>. 
### Mailbox
    This class represents a dedicated space within each node where the message passing between nodes is computed, in this space is where the comparison between tables is done. At its core we find a LinkedList that stores the Nodes that have subscribed to it. It uses this list to deliver the messages pushed to it to the intended receiver.
### TableEntry 
    This class represents the information stored as an entry in the tables, composed by the nodes composing the link
and the cost to traverse the link.
### Link 
    Its fields are two Nodes and an int value. All methods in this class are static.
### LinkState 
    This class contains the entry point of our program. It invokes the startUp method from the SetUp class passing as arguments the paths passed as arguments when starting the program.
## RESULTS
The program in action: The program starts by retrieving the arguments from the command line and passing them as string values to the static method startUp in class StartUp. As soon as that method returns the program will be complete and the line "done!" Is printed to the console 
```java 
public static void main(String[] args) {
        String file = args[0];
        String sequence = args[1];
        SetUp.startUp(file, sequence);
        System.out.println("Done!");

    }
```

### StartUp
In this method the program is directed. Generate elements produces the Node Objects, the list Sequence in which the nodes will send the messages and the representation of the graph. Mailbox is the the object where the node exchange the messages, the nodes subscribe to a mailing list in that mailbox and from there can pick up the messages that are sent to them. They also send the messages to the nodes that are in the mailing list of that specific mailbox object. The list finalNodes is a list where every Node object from the given topology is stored. In this for each block we initialize every node with their direct links and the corresponding direct neighbours. In this block we also subscribe all the nodes to the same mailbox so that they communicate directly. If the sequence of letters  given as parameter starting the program is empty this part is not executed. We will jump to the last lines shown in the picture and only print the nodes as they were initialized in the previous for each block. If the sequence is not empty then each node in the sequence will send its table.
```java 
public class SetUp {

  public static void startUp(String pathToNodes, String pathToSequence) {

        List<Link> links = new LinkedList<>();

        List<Node> finalNodes = new LinkedList<>(); // The set of generated nodes created from the uniqueLetters list.

        generateElements(pathToNodes, pathToSequence, links, finalNodes);

        Deliverer<Message> mailbox = new Mailbox();

        for (Node node : finalNodes) {

            node.initNode(links, finalNodes);

            node.subscribe(mailbox);

        }
        System.out.println("\nGenerating Nodes...\n");

        if(!sequence.isEmpty()){
            System.out.println("Sending messages...\n");
            for (String letter : sequence) {

                Node senderNode = finalNodes.stream().filter(x -> x.getName().contains(letter)).findFirst().get();

                senderNode.sendMessages();
            }
        }

        for (Node node : finalNodes)
            node.printToFile();

            System.out.println("\nPrinting Nodes to files...\n");
    }
    } 
``` 
### Node class
setDirectNeighbours: This method finds in the graph the nodes that are linked to “this node”, we insert them in the table as an entry consisting of the key which is the neighbour node, and a Table entry object that is composed by the neighbour node and the cost of the link. At the end of the method we also store the direct neighbours in a list that is stored within the node object.
#### InitNode
 in this method we start by putting in the table owned by the node the node itself as
“direct” with cost 0 , we set the direct neighbours as explained above, and we initialize the table for the remaining nodes in the graph that are not directly connected with value null. 

```java

    public void initNode(List<Link> graph, List<Node> finalNodes){

        this.graph = graph;

        table.put(this, new TableEntry(new Node("direct"), 0));
        setDirectNeighbours(graph);

        table.initTable(finalNodes);

    }

```
#### sendMessages
in this method we iterate the list of direct neighbours of the node and send them the specific message that must be sent. To do that we prepare a Message object, composed of the cost to send it through the direct link, the table of the sender, the sender itself, and the receiver. This
message is put in the mailbox to which the node is subscribed. 
```java
    public  void sendMessages(){

        for(Node receiver : directNeighbours){

            int costToSend = graph.stream().filter(x -> x.contains(receiver) && x.contains(this)).findFirst().get().getCost();

            Message message = new Message(costToSend,table, this, receiver);
            System.out.printf("\nNode %s is sending its table to Node %s\n\n", this,receiver);
            send(message);

        }
    }

```

#### Subscribe/ unsubscribe
These methods add/remove the node to/from the mailbox through which it communicates with the other nodes, as explained in the methodology we can see that the Mailbox is determined by the Deliverer interface.
Send: this method pushes the message to the mailbox to which the
node is associated. 
```java
    @Override
    public void subscribe(Deliverer<Message> inbox) {
        this.mailbox = inbox;
        ((Mailbox)inbox).addToMailingList(this);
    }

```

#### Receive
This method is invoked by the receiver node. The receiver find the cost of the direct link, the foreign table and who is the sender, with all this information it can compare the tables and update its own accordingly.
```java
    @Override
    public void receive(Message message) {

        int costToReceive = message.getCost();

        Table foreignTable = message.getTable();

        Node sender = message.getSender();

        updateTable(costToReceive,foreignTable, sender);

    }

```
#### UpdateTable
For each entry in the foreign table the node compares its own corresponding entry, we do that by calling the same key on both tables.
If the foreign entry is null or is the a node directly connected to the destination network we ignore it as the receiver node can not better up the cost of 0 and is not interested in null entries.
The net cost that is proposed by the sender node is composed by the cost of the direct link between the nodes plus the cost of to reach the destination node. This cost will be compared with the own cost that is stored in the specific entry. If the entry in the receiver node is null we know that the sender node entry is already better, so that the next hop is set to the sender and the net cost is added as an entry to the
receiver table. Instead if the receiver entry is not null but has a higher cost than the net cost proposed by the sender we accept it
and set the sender as the next hop and put the net cost as the new cost to reach the destination. After doing so we update the table for that specific entry. At the end of the loop we print the resulting updated table. 
```java
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

```

### Table class
#### initTable
Used in the Node class to initialize the unknown nodes of the graph to null.
```java
 public void initTable(List<Node> listOfNodes) {

           for( Node node : listOfNodes){
                   if( !this.containsKey(node))
                       this.put(node, null);
               }
    }
```

### Mailbox class
#### receive
The node receiver node is extracted from the message passed, we find it in the list of nodes subscriber to the mailing list and we call on it
the receive method of the Node class. There the message will be dissembled and the tables compared.
```java
 public void receive(Message mail) {

            Node recipient =  mailingList.get(mailingList.indexOf(mail.getReceiver()));

            recipient.receive(mail);
    }
```
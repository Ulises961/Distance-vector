

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class SetUp {

   public static List<String> sequence;

    public static List<String> deMarshall(String pathToFile) {

        List<String> lines = new LinkedList<>();

        try (BufferedReader input = new BufferedReader(new FileReader(pathToFile))) {

            String str;

            while ((str = input.readLine()) != null) {

                lines.add(str);
            }

        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }

        return  lines;
    }

    public static void generateElements(String pathToFile, String pathToSequence,  List<Link> links,  List<Node> finalNodes ) {

        List<String> lines = deMarshall(pathToFile);

        sequence = deMarshall(pathToSequence);

        String regex = " ";

        List<String> rawLetters = new LinkedList<>(); // Letters extracted for the txt file. 

        List<String> uniqueLetters = new LinkedList<>(); // The set of distinct letters extracted from the txt file

        if (!sequence.isEmpty())
          sequence = Arrays.asList(sequence.get(0).split(regex));


        for( String str : lines) {

            String[] line = str.split(regex);

            rawLetters.add(line[0]);

            rawLetters.add(line[1]);
            
            uniqueLetters = rawLetters.stream().distinct().collect(Collectors.toList());
        }

      
        for (String node : uniqueLetters) {
            Node genericNode = new Node(node);
            finalNodes.add(genericNode);
        }

        for (String line : lines) {
            String[] subStrings = line.split(regex);
            Node node1 = finalNodes.stream().filter(x -> x.getName().equals(subStrings[0])).findFirst().get();
            Node node2 = finalNodes.stream().filter(x -> x.getName().equals(subStrings[1])).findFirst().get();
            int cost = Integer.parseInt(subStrings[2]);

            Link link = new Link(node1, node2, cost);
            links.add(link);
        }
    }

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

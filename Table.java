
import java.util.HashMap;
import java.util.List;


public class Table extends HashMap<Node,TableEntry> {

        public void initTable (List<Node> listOfNodes){

           for( Node node : listOfNodes){
                   if( !this.containsKey(node))
                       this.put(node, null);
               }
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();
        str.append("Dest, next hop, cost\n");
        this.forEach((k,v) -> {
           if(v != null) {

             str.append(String.format(" %s%s %d\n", k, (v.getNextNode().getName().equals("direct"))?

                       "\t"+ v.getNextNode()+"\t" : ("\t"+v.getNextNode()+"\t"),v.getCost()));
           }
       });
        return str.toString();

    }
}


import java.util.LinkedList;
import java.util.List;

public class Mailbox implements  Deliverer<Message>{

    List<Node> mailingList = new LinkedList<>();

    @Override
    public void receive(Message mail) {

            Node recipient =  mailingList.get(mailingList.indexOf(mail.getReceiver()));

            recipient.receive(mail);
    }

    public void addToMailingList(Node node){
        mailingList.add(node);

    }

    public void  removeFromMailingList( Node node){
        mailingList.remove(node);
    }


}

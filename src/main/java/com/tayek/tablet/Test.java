package com.tayek.tablet;
import java.io.IOException;
import com.tayek.tablet.model.Message;
public class Test {
    public static void main(String[] args) throws IOException,InterruptedException {
        Group group=new Group(1,Group.tabletsTwo);
        Tablet<Message> tablet=new Tablet<>(group,1);
        tablet.startListening();
        Tablet<Message> tablet2=new Tablet<>(group,2);
        tablet2.startListening();
        Message message;
        // message=Message.start(group,3);
        // InetAddress inetAddress=InetAddress.getLocalHost();
        // Client client=new Client(inetAddress,Server.port(1));
        // client.send(message);
        // Client client2=new Client(inetAddress,Server.port(2));
        // client.send(message);
        System.out.println("-----------");
        tablet.send(Message.dummy,0);  // won't know who he is from?
        Thread.sleep(100);
        tablet2.send(Message.dummy,0);
        Thread.sleep(100);
        tablet.stopListening();
        tablet2.stopListening();
    }
}

package com.tayek.tablet;
import java.io.IOException;
import java.net.*;
import com.tayek.tablet.model.Message;
public class Test {
    public static void main(String[] args) throws IOException,InterruptedException {
        Group group=new Group(1,Group.tabletsTwo);
        Tablet tablet=new Tablet(group,1);
        tablet.startListening();
        Tablet tablet2=new Tablet(group,2);
        tablet2.startListening();
        Message message;
        // message=Message.start(group,3);
        // InetAddress inetAddress=InetAddress.getLocalHost();
        // Client client=new Client(inetAddress,Server.port(1));
        // client.send(message);
        // Client client2=new Client(inetAddress,Server.port(2));
        // client.send(message);
        System.out.println("-----------");
        message=Message.start(group,1);
        tablet.broadcast(message);
        Thread.sleep(100);
        message=Message.start(group,2);
        tablet2.broadcast(message);
        Thread.sleep(100);
        tablet.stopListening();
        tablet2.stopListening();
    }
}

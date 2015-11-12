package com.tayek.tablet;
import java.io.IOException;
import java.net.*;
import com.tayek.tablet.model.Message;
import com.tayek.tablet.model.Message.Type;
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        Group group=new Group(1,Group.tabletsTwoOnLinksys42);
        Tablet tablet=new Tablet(group,1);
        tablet.start();
        Tablet tablet2=new Tablet(group,2);
        tablet2.start();
        InetAddress inetAddress=InetAddress.getLocalHost();
        Message message=Message.start(group,3);
        //Client client=new Client(inetAddress,Server.port(1));
        //client.send(message);
       // Client client2=new Client(inetAddress,Server.port(2));
        //client.send(message);
        System.out.println("-----------");
        message=Message.start(group,1);
        tablet.broadcast(message);
        Thread.sleep(100);
        message=Message.start(group,2);
        tablet2.broadcast(message);
        Thread.sleep(100);
        tablet.stop();
        tablet2.stop();
    }
}

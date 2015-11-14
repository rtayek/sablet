package com.tayek.tablet;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import com.tayek.tablet.model.*;
import com.tayek.tablet.model.Message.*;
// http://www.instructables.com/id/How-To-Setup-Eclipse-for-Android-App-Development/step9/Access-ADT-Plugin-Preferences/
public class Tablet {
    public Tablet(Group group,int tabletId) {
        this(group,tabletId,"Room: "+tabletId);
    }
    public Tablet(Group group,int tabletId,String name) {
        this.group=group;
        this.tabletId=tabletId;
        this.name=name;
    }
    public boolean startListening() {
        try {
            String host=group.idToHost().get(tabletId);
            System.out.println("host: "+host);
            SocketAddress socketAddress=new InetSocketAddress(host,Server.port(tabletId));
            System.out.println("socketAddress: "+socketAddress);
            server=new Server(socketAddress,group.model);
            server.start();
            return true;
        } catch(BindException e) {
            logger.warning("caught: "+e);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void stopListening() {
        server.stopServer();
    }
    public void broadcast(final Message message) {
        Thread thread=new Thread(new Runnable() {
            @Override public void run() {
                Map<Integer,String> map=group.idToHost();
                logger.info("map: "+map);
                for(Integer destinationTabletId:map.keySet())
                    if(!destinationTabletId.equals(tabletId)) {
                        String host=map.get(destinationTabletId);
                        InetAddress inetAddress;
                        try {
                            inetAddress=InetAddress.getByName(host);
                            logger.info("host: "+host+":"+Server.port(destinationTabletId));
                            Client client=new Client(inetAddress,Server.port(destinationTabletId));
                            if(client.send(message)) logger.fine("send worked");
                        } catch(UnknownHostException e) {
                            e.printStackTrace();
                        }
                        Thread.yield();
                    }
            }
        },"broadcast");
        thread.start();
    }
    public String name() {
        return name;
    }
    @Override public String toString() {
        return name+" "+group.model;
    }
    public static void main(String[] arguments) throws IOException,InterruptedException {
        System.getProperties().list(System.out);
    }
    final String name;
    public final Group group;
    public final Integer tabletId;
    Server server;
    public final Logger logger=Logger.getLogger(getClass().getName());
    public static final Set<Class<?>> loggers=new LinkedHashSet<>();
    static {
        loggers.add(Tablet.class);
        loggers.add(Client.class);
        loggers.add(Server.class);
        loggers.add(Group.class);
        loggers.add(Message.class);
        loggers.add(Sender.class);
        loggers.add(Receiver.class);
        loggers.add(Model.class);
    }
}

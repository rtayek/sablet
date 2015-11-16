package com.tayek.tablet;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import com.tayek.*;
import com.tayek.io.IO.*;
import com.tayek.tablet.model.*;
// http://www.instructables.com/id/How-To-Setup-Eclipse-for-Android-App-Development/step9/Access-ADT-Plugin-Preferences/
// add a method or a class to exercise random presses!
public class Tablet<T> implements Sender<T> {
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
            // this guy should be able to get his own ip address.
            // use this to allow a foreign visitor/monitor
            // he should send an "add me" or ask for
            // visiting privileges.
            SocketAddress socketAddress=new InetSocketAddress(host,Server.port(tabletId));
            System.out.println("socketAddress: "+socketAddress);
            server=new Server<Message>(socketAddress,group.model,Message.dummy);
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
    @Override public boolean send(final T t,int unused) {
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
                            Client<T> client=new Client<>(inetAddress,Server.port(destinationTabletId));
                            if(client.send(t,tabletId)) logger.fine("send worked");
                        } catch(UnknownHostException e) {
                            e.printStackTrace();
                        }
                        Thread.yield();
                    }
            }
        },"broadcast");
        thread.start();
        return true; //lie!
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
    Server<Message> server;
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

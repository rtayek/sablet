package com.tayek.tablet;
import java.util.*;
import java.util.logging.Level;
import com.tayek.tablet.model.Message;
import com.tayek.tablet.model.Message.Type;
import com.tayek.utilities.LoggingHandler;
public class Run {
    public static void main(String[] args) throws InterruptedException {
        Main.log.setLevel(Level.WARNING);
        Map<Integer,String> map=new TreeMap<>();
        for(int i=1;i<=20;i++)
            map.put(i,"192.168.1.2");
        System.out.println(map);
        Map<Integer,Tablet> tablets=new TreeMap<>();
        for(int tabletId:map.keySet()) {
            Tablet tablet=new Tablet(new Group(1,map),tabletId);
            tablets.put(tabletId,tablet);
        }
        System.out.println(tablets);
        System.out.println("start");
        for(Tablet tablet:tablets.values()) {
            tablet.startListening();
            Thread.yield();
        }
        System.out.println("broadcast");
        for(Tablet tablet:tablets.values()) {
            Message message=new Message(tablet,Type.startup,0);
            tablet.broadcast(message);
            Thread.yield();
        }
        System.out.println("sleep");
        Thread.sleep(2*map.size()*map.size());
        System.out.println("awake");
        System.out.println("received");
        for(Tablet tablet:tablets.values()) {
            System.out.println(tablet.server.received());
        }
        System.out.println("check");
        for(Tablet tablet:tablets.values()) {
            //System.out.println(tablet.server.received());
            if(tablet.server.received()!=map.size()-1) System.out.println(tablet+" received: "+tablet.server.received()+" instead of "+(map.size()-1));
            tablet.stopListening();
        }
        for(Tablet tablet:tablets.values()) {
            tablet.server.join();
        }
        System.out.println("joined");
        Thread.sleep(0);
        Client.printThreads();
    }
}

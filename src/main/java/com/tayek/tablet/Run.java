package com.tayek.tablet;
import java.util.*;
import java.util.logging.Level;
import com.tayek.tablet.model.Message;
public class Run {
    @SuppressWarnings("unchecked") public static void main(String[] args) throws InterruptedException {
        Main.log.setLevel(Level.WARNING);
        Map<Integer,String> map=new TreeMap<>();
        for(int i=1;i<=20;i++)
            map.put(i,"192.168.1.2");
        System.out.println(map);
        Map<Integer,Tablet<Message>> tablets=new TreeMap<>();
        for(int tabletId:map.keySet()) {
            Tablet<Message> tablet=new Tablet<>(new Group(1,map),tabletId);
            tablets.put(tabletId,tablet);
        }
        System.out.println(tablets);
        System.out.println("start");
        for(Tablet<Message> tablet:tablets.values()) {
            tablet.startListening();
            Thread.yield();
        }
        System.out.println("broadcast");
        for(Tablet<Message> tablet:tablets.values()) {
            tablet.send(Message.dummy,0);
            Thread.yield();
        }
        System.out.println("sleep");
        Thread.sleep(2*map.size()*map.size());
        System.out.println("awake");
        System.out.println("received");
        for(Tablet<Message> tablet:tablets.values()) {
            System.out.println(tablet.server.received());
        }
        System.out.println("check");
        for(Tablet<Message> tablet:tablets.values()) {
            //System.out.println(tablet.server.received());
            if(tablet.server.received()!=map.size()-1) System.out.println(tablet+" received: "+tablet.server.received()+" instead of "+(map.size()-1));
            tablet.stopListening();
        }
        for(Tablet<Message> tablet:tablets.values()) {
            tablet.server.join();
        }
        System.out.println("joined");
        Thread.sleep(0);
        com.tayek.io.IO.printThreads();
    }
}

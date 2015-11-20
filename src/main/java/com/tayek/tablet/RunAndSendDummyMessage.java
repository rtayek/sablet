package com.tayek.tablet;
import static com.tayek.tablet.io.IO.*;
import java.util.*;
import java.util.logging.Level;
import com.tayek.tablet.model.Message;
public class RunAndSendDummyMessage {
    @SuppressWarnings("unchecked") public static void main(String[] args) throws InterruptedException {
        Main.log.init();
        Map<Integer,Group.Info> map=new TreeMap<>();
        for(int i=1;i<=20;i++)
            map.put(i,new Group.Info("192.168.1.2","Tablet: "+i+" om PC"));
        p(map.toString());
        Map<Integer,Tablet> tablets=new TreeMap<>();
        for(int tabletId:map.keySet()) {
            Tablet tablet=new Tablet(new Group(1,map),tabletId);
            tablets.put(tabletId,tablet);
        }
        p(tablets.toString());
        p("start");
        for(Tablet tablet:tablets.values()) {
            tablet.startListening();
            Thread.yield();
        }
        p("broadcast");
        for(Tablet tablet:tablets.values()) {
            tablet.send(Message.dummy,0);
            Thread.yield();
        }
        p("sleep");
        Thread.sleep(2*map.size()*map.size());
        p("awake");
        p("received");
        if(false) for(Tablet tablet:tablets.values())
            p(tablet.server.received().toString());
        p("check");
        for(Tablet tablet:tablets.values()) {
            if(tablet.server.received()!=map.size()-1) p(tablet+" received: "+tablet.server.received()+" instead of "+(map.size()-1));
            tablet.stopListening();
        }
        for(Tablet tablet:tablets.values()) {
            tablet.server.join();
        }
        p("joined");
        Thread.sleep(0);
        com.tayek.tablet.io.IO.printThreads();
    }
}

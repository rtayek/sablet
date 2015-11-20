package com.tayek.tablet;
import static com.tayek.tablet.io.IO.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import com.tayek.tablet.model.*;
import com.tayek.utilities.Utility;
// http://www.instructables.com/id/How-To-Setup-Eclipse-for-Android-App-Development/step9/Access-ADT-Plugin-Preferences/
// add a method or a class to exercise random presses!
// maybe we have no tabletid
// we have an inet address and port or maybe an inetSocketAddresss.
public class Tablet  {
    public enum MenuItem {
        Reset,Ping,Disconnect,Connect,Log,Sound,Simulate;
        public void doItem(Tablet tablet) {
            doItem(this,tablet);
        }
        public static boolean isIem(int ordinal) {
            return 0<=ordinal&&ordinal<values().length?true:false;
        }
        // called from android
        public static void doItem(int ordinal,Tablet tablet) {
            if(tablet!=null) if(0<=ordinal&&ordinal<values().length) values()[ordinal].doItem(tablet);
            else p(ordinal+" is invalid ordinal for!");
            else p("tablet is null in do item!");
        }
        public static void doItem(MenuItem tabletMenuItem,Tablet tablet) {
            // maybe move
            switch(tabletMenuItem) {
                case Reset:
                    tablet.group.model.reset();
                    break;
                case Ping:
                    tablet.send(Message.dummy,0);
                    break;
                case Disconnect:
                    tablet.stopListening();
                    break;
                case Connect:
                    if(!tablet.startListening()) p(Utility.method()+" startListening() failed!");
                    break;
                case Log:
                    // gui.textView.setVisible(!gui.textView.isVisible());
                    break;
                case Sound:
                    Main.sound=!Main.sound;
                    break;
                case Simulate:
                    if(tablet.timer==null) Tablet.startSimulating(tablet);
                    else tablet.stopSimulating();
                    break;
            }
        }
    }
    public Tablet(Group group,int tabletId) {
        this.group=group;
        this.tabletId=tabletId;
        if(!group.tablets().contains(tabletId))
            logger.severe("tablet: "+tabletId+" is not a member of group: "+group);
        setName(group.info(tabletId).name);
    }
    public boolean startListening() {
        try {
            String host=group.info(tabletId()).host;
            // this guy should be able to get his own ip address.
            // use this to allow a foreign visitor/monitor
            // he should send an "add me" or ask for
            // visiting privileges.
            SocketAddress socketAddress=new InetSocketAddress(host,Server.port(tabletId()));
            server=new Server(this,socketAddress,group.model);
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
    public boolean send(final Message message,int unused) {
        // the client send() starts a thread and waits,
        // so let's do this on a thread also.
        Thread thread=new Thread(new Runnable() {
            @Override public void run() {
                for(Integer destinationTabletId:group.tablets())
                    if(!destinationTabletId.equals(tabletId())) {
                        String host=group.info(destinationTabletId).host;
                        InetAddress inetAddress;
                        try {
                            inetAddress=InetAddress.getByName(host);
                            logger.info("host: "+host+":"+Server.port(destinationTabletId));
                            Client client=new Client(inetAddress,Server.port(destinationTabletId));
                            if(client.send(message,tabletId())) logger.fine("send worked");
                        } catch(UnknownHostException e) {
                            e.printStackTrace();
                        }
                        Thread.yield();
                    }
            }
        },"broadcast");
        thread.start();
        return true; // lie!
    }
    void setName(String name) {
        this.name=name;
    }
    public String name() {
        return name;
    }
    public Integer tabletId() {
        return tabletId;
    }
    @Override public String toString() {
        return name+" "+group.model;
    }
    public static void startSimulating(final Tablet tablet) {
        // add code to start and stop the simulator
        if(tablet.timer!=null) tablet.stopListening();
        ArrayList<Integer> ids=new ArrayList<>(tablet.group.tablets());
        if(true) {
            p(""+System.currentTimeMillis());
            final long t0=1_447_900_000_000l;
            final int dt=300;
            p("before wait, time: "+System.currentTimeMillis());
            while(System.currentTimeMillis()%1000>10)
                ;
            p("after wait, time: "+System.currentTimeMillis());
            tablet.timer=new Timer();
            tablet.timer.schedule(new TimerTask() {
                @Override public void run() {
                    Message message=Group.randomToggle(tablet);
                    p(""+(System.currentTimeMillis()-t0)+" "+tablet+" "+message);
                    tablet.send(message,0);
                    tablet.group.model.receive(message);
                }
            },1_000+ids.indexOf(tablet.tabletId())*dt,dt*tablet.group.tablets().size());
        }
    }
    public void stopSimulating() {
        if(timer!=null) {
            timer.cancel();
            timer=null;
        }
    }
    public static void main(String[] arguments) throws IOException,InterruptedException {
        System.getProperties().list(System.out);
    }
    private Timer timer;
    private String name;
    public final Group group;
    private final Integer tabletId;
    public Server server;
    public final Logger logger=Logger.getLogger(getClass().getName());
}

package com.tayek.tablet;
import static com.tayek.tablet.io.IO.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import com.tayek.io.*;
import com.tayek.tablet.io.Toaster;
import com.tayek.tablet.io.Toaster.*;
import com.tayek.tablet.io.gui.swing.Gui;
import com.tayek.tablet.model.*;
import com.tayek.tablet.model.Message.Type;
public class Group implements Cloneable {
    static class Address {
        Address(String prefix) { // why exactly do i need this now?
            // now we can use the convention that ipaddress=tabletid
            //
            this.prefix=prefix;
        }
        int leastSignificantOctet(String address) {
            if(address.startsWith(prefix)) return Integer.valueOf(address.substring(prefix.length()));
            else return 0;
        }
        String fromLeastSignificantOctet(int n) {
            return prefix+""+n%256;
        }
        public final String prefix;
    }
    public static class Info {
        public Info(String host,String name) {
            this.host=host;
            if(name==null||name.isEmpty()) throw new RuntimeException("name me!");
            this.name=name;
        }
        // maybe just put in a map
        // instead of making a class!
        public final String host;
        public final String name;
    }
    public Group(Integer groupId,Map<Integer,Info> idToHost) {
        this(groupId,idToHost,++serialNumbers,new ToasterImpl());
    }
    private Group(Integer groupId,Map<Integer,Info> idToHost,Integer serialNumber,Toaster toaster) {
        // group now always has home in it with a tablet id of zero!
        this.serialNumber=serialNumber;
        this.groupId=groupId;
        this.idToHost=idToHost;
        this.toaster=toaster;
    }
    public Set<Integer> tablets() {
        synchronized(idToHost) {
            return idToHost.keySet();
        }
    }
    public void check(int tabletId,InetAddress inetAddress) {}
    public List<InetAddress> checkHost(String host) {
        List<InetAddress> hosts=new ArrayList<>();
        try {
            for(Enumeration<NetworkInterface> networkInterfaces=NetworkInterface.getNetworkInterfaces();networkInterfaces.hasMoreElements();) {
                NetworkInterface networkInterface=networkInterfaces.nextElement();
                for(Enumeration<InetAddress> as=networkInterface.getInetAddresses();as.hasMoreElements();) {
                    InetAddress a=as.nextElement();
                    if(a.isSiteLocalAddress()) hosts.add(a);
                }
            }
        } catch(SocketException e) {
            logger.warning("caught: "+e);
        }
        return hosts;
    }
    public Info info(int tabletId) {
        synchronized(idToHost) {
            return idToHost.get(tabletId);
        }
    }
    public void checkForNewInetAddress(int from,InetAddress inetAddress) {
        String host=info(from).host;
        if(!inetAddress.equals(host)) {
            p(host+"!="+inetAddress);
            logger.warning(host+"!="+inetAddress);
        }
    }
    public String buttonName(int tabletId,int buttonId) {
        return info(tabletId).name+"/button "+buttonId;
    }
    public void print(Integer tabletId) {
        p("group: "+groupId+"("+serialNumber+"):"+tabletId);
        Map<Integer,Info> copy=idToHost();
        for(int i:copy.keySet())
            p("\t"+i+": "+copy.get(i));
    }
    private Map<Integer,Info> idToHost() { // replace call to this with
                                           // host();
        Map<Integer,Info> copy=new TreeMap<>();
        synchronized(idToHost) {
            copy.putAll(idToHost);
        }
        return Collections.unmodifiableMap(copy);
    }
    public static Message random(Tablet tablet) {
        int buttonId=random.nextInt(tablet.group.model.buttons)+1;
        boolean state=random.nextBoolean();
        // tablet.group.model.setState(buttonId,state);
        return new Message(Type.normal,tablet.group.groupId,tablet.tabletId(),buttonId,tablet.group.model.toCharacters());
    }
    public static Message randomToggle(Tablet tablet) {
        int buttonId=random.nextInt(tablet.group.model.buttons)+1;
        System.out.println(Thread.currentThread()+" sync "+tablet);
        boolean state=!tablet.group.model.state(buttonId);
        String string=tablet.group.model.toCharacters();
        int index=buttonId-1;
        Character c=Model.toCharacter(state);
        String newString=string.substring(0,index)+c+string.substring(index+1,string.length());
        // tablet.group.model.setState(buttonId,state);
        System.out.println(Thread.currentThread()+" sync "+tablet+" completed");
        return new Message(Type.normal,tablet.group.groupId,tablet.tabletId(),buttonId,newString);
    }
    public static final Random random=new Random();
    @Override public String toString() {
        return "group: "+groupId+"("+serialNumber+"): "+idToHost().keySet();
    }
    public static void main(String[] arguments) throws IOException,InterruptedException {
        Main.log.init();
        Main.log.setLevel(Level.WARNING);
        Map<Integer,Group.Info> map=Group.g7;
        // Tablet.simulate(tablet);
        // add code to check that these guys are in sync!
        Map<Integer,Tablet> tablets=new LinkedHashMap<>();
        for(int tabletId:map.keySet())
            tablets.put(tabletId,Gui.runGui(map,tabletId));
        Thread.sleep(100);
        for(Tablet tablet:tablets.values())
            Tablet.startSimulating(tablet);
    }
    public final Integer serialNumber;
    public final Model model=new Model(defaultButtons); // default for now
    public final Integer groupId;
    private final Map<Integer,Info> idToHost; // usually 1-n
    public Toaster toaster;
    public final Logger logger=Logger.getLogger(getClass().getName());
    private static int serialNumbers;
    public static final Integer defaultButtons=5;
    public static final Integer defaultTablets=defaultButtons+2;
    public static final Integer maxTablets=50;
    public static final Integer maxButtons=20;
    public static final Map<String,Map<Integer,Info>> groups=new LinkedHashMap<>();
    public static Map<Integer,Info> g1=new TreeMap<>();
    static {
        g1.put(1,new Info("192.168.1.2","Tablet: "+1+"' on PC"));
        groups.put("g1",g1);
    }
    public static Map<Integer,Info> g2=new TreeMap<>();
    static {
        g2.put(4,new Info("192.168.1.2","Tablet: "+4+"' on PC"));
        g2.put(5,new Info("192.168.1.2","Tablet: "+5+"' on PC"));
        groups.put("g2",g2);
    };
    public static Map<Integer,Info> g7=new TreeMap<>();
    static {
        for(int i=1;i<=7;i++)
            g7.put(i,new Info("192.168.1.2","Tablet: "+i+"' on PC"));
        groups.put("g7",g7);
    };
    public static Map<Integer,Info> g50=new TreeMap<>();
    static {
        for(int i=1;i<=50;i++)
            g50.put(i,new Info("192.168.1.2","Tablet: "+i+"' on PC"));
        groups.put("g50",g50);
    };
    public static Map<Integer,Info> g0=new TreeMap<>();
    static {
        g0.put(1,new Info("192.168.1.21","Fire"));
        g0.put(2,new Info("192.168.1.22","Fire"));
        g0.put(3,new Info("192.168.1.88","Conrad's Nexus 7"));
        g0.put(4,new Info("192.168.1.2","4 on PC"));
        g0.put(5,new Info("192.168.1.2","5 on PC"));
        g0.put(7,new Info("192.168.1.77","At&T"));
        g0.put(99,new Info("192.168.1.99","Ray's Nexus 4"));
        groups.put("g0",g0);
    }
    public static final Map<String,Integer> androidIds=new LinkedHashMap<>();
    static {
        androidIds.put("7643fc99c2f8eb5c",1); // fire
        androidIds.put("fa37f2329a84e09d",2); // fire
        androidIds.put("1a105b0b3ab6a4d4",3); // nexus 7
        androidIds.put("e72b0e7deece08ce",7); // at&t
        androidIds.put("6f9a6936f633542a",99); // nexus 4
        // e72b0e7deece08ce
    }
}

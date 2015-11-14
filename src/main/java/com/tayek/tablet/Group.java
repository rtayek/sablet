package com.tayek.tablet;
import java.util.*;
import java.util.logging.Logger;
import com.tayek.tablet.gui.common.Toaster;
import com.tayek.tablet.model.*;
public class Group implements Cloneable {
    public static class Triple<F,S,T> {
        public Triple(F f,S s,T t) {
            this.f=f;
            this.s=s;
            this.t=t;
        }
        public final F f;
        public final S s;
        public final T t;
    }
    public Group(Integer groupId,Map<Integer,String> idToHost) {
        // nobody should call this ctor except for home!
        this(groupId,idToHost,++serialNumbers,new ToasterImpl());
    }
    private Group(Integer groupId,Map<Integer,String> idToHost,Integer serialNumber,Toaster toaster) {
        // group now always has home in it with a tablet id of zero!
        this.serialNumber=serialNumber;
        this.groupId=groupId;
        this.idToHost=idToHost;
        this.toaster=toaster;
    }
    public void print(Integer tabletId) {
        System.out.println("group: "+groupId+"("+serialNumber+"):"+tabletId);
        Map<Integer,String> copy=idToHost();
        for(int i:copy.keySet())
            System.out.println("\t"+i+": "+copy.get(i));
    }
    public Map<Integer,String> idToHost() {
        Map<Integer,String> copy=new TreeMap<>();
        synchronized(idToHost) {
            copy.putAll(idToHost);
        }
        return Collections.unmodifiableMap(copy);
    }
    @Override public String toString() {
        return "group: "+groupId+"("+serialNumber+"): "+idToHost().keySet();
    }
    public final Integer serialNumber;
    public final Model model=new Model(defaultButtons); // default for now
    public final Integer groupId;
    private final Map<Integer,String> idToHost; // usually 1-n
    public Toaster toaster;
    public final Logger logger=Logger.getLogger(getClass().getName());
    private static int serialNumbers;
    public static final Integer defaultButtons=5;
    public static final Integer defaultTablets=defaultButtons+2;
    public static final Integer maxTablets=50;
    public static final Integer maxButtons=20;
    public static Map<Integer,String> tabletsOne=new TreeMap<>();
    static {
        tabletsOne.put(1,"192.168.1.2");
    }
    public static Map<Integer,String> tabletsTwo=new TreeMap<>();
    static {
        tabletsTwo.put(1,"192.168.1.2");
        tabletsTwo.put(2,"192.168.1.2");
    };
    public static Map<Integer,String> tabletsSeven=new TreeMap<>();
    static {
        for(int i=1;i<=7;i++)
            tabletsSeven.put(i,"192.168.1.2");
    };
    public static Map<Integer,String> tabletsFifty=new TreeMap<>();
    static {
        for(int i=1;i<=50;i++)
            tabletsFifty.put(i,"192.168.1.2");
    };
    public static Map<Integer,String> tabletsFive=new TreeMap<>();
    static {
        tabletsFive.put(1,"192.168.1.21"); // fire
        tabletsFive.put(2,"192.168.1.22"); // fire
        tabletsFive.put(3,"192.168.1.88"); // nexus 7
        tabletsFive.put(4,"192.168.1.2"); // pc
        // tablets4.put(5,"192.168.1.2");
        tabletsFive.put(7,"192.168.1.77"); // at&t
        tabletsFive.put(99,"192.168.1.99"); // nexus 4
    }
    public static Map<String,Integer> androidIds=new LinkedHashMap<>();
    static {
        androidIds.put("7643fc99c2f8eb5c",1); // fire
        androidIds.put("fa37f2329a84e09d",2); // fire
        androidIds.put("1a105b0b3ab6a4d4",3); // nexus 7
        androidIds.put("e72b0e7deece08ce",7); // at&t
        androidIds.put("6f9a6936f633542a",99); // nexus 4
        
        //e72b0e7deece08ce
    }
}

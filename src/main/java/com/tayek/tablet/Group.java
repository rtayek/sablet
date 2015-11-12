package com.tayek.tablet;
import java.util.*;
import java.util.logging.Logger;
import com.tayek.tablet.model.*;
public class Group implements Cloneable {
    public Group(Integer groupId,Map<Integer,String> idToHost) {
        // nobody should call this ctor except for home!
        this(groupId,idToHost,++serialNumbers);
    }
    private Group(Integer groupId,Map<Integer,String> idToHost,Integer serialNumber) {
        // group now always has home in it with a tablet id of zero!
        this.serialNumber=serialNumber;
        this.groupId=groupId;
        this.idToHost=idToHost;
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
    public final Logger logger=Logger.getLogger(getClass().getName());
    private static int serialNumbers;
    public static final Integer defaultButtons=5;
    public static final Integer defaultTablets=defaultButtons+2;
    public static final Integer maxTablets=50;
    public static final Integer maxButtons=20;
    public static Map<Integer,String> tabletsOneOnLinksys42=new TreeMap<>();
    static {
        tabletsOneOnLinksys42.put(1,"192.168.1.2");
    }
    public static Map<Integer,String> tabletsTwoOnLinksys42=new TreeMap<>();
    static {
        tabletsTwoOnLinksys42.put(1,"192.168.1.2");
        tabletsTwoOnLinksys42.put(2,"192.168.1.2");
    };
    public static Map<Integer,String> tabletsSevenOnLinksys42=new TreeMap<>();
    static {
        tabletsSevenOnLinksys42.put(1,"192.168.1.2");
        tabletsSevenOnLinksys42.put(2,"192.168.1.2");
        tabletsSevenOnLinksys42.put(3,"192.168.1.2");
        tabletsSevenOnLinksys42.put(4,"192.168.1.2");
        tabletsSevenOnLinksys42.put(5,"192.168.1.2");
        tabletsSevenOnLinksys42.put(6,"192.168.1.2");
        tabletsSevenOnLinksys42.put(7,"192.168.1.2");
    };
    public static Map<Integer,String> tabletsFiftyOnLinksys42=new TreeMap<>();
    static {
        for(int i=1;i<=50;i++)
            tabletsFiftyOnLinksys42.put(i,"192.168.1.2");
    };
    public static Map<Integer,String> tabletsTwoFiresAndOneJavaOnLinksys42=new TreeMap<>();
    static {
        tabletsTwoFiresAndOneJavaOnLinksys42.put(1,"192.168.1.21");
        tabletsTwoFiresAndOneJavaOnLinksys42.put(2,"192.168.1.22");
        tabletsTwoFiresAndOneJavaOnLinksys42.put(3,"192.168.1.2");
    }
    public static Map<Integer,String> tabletsTwoFiresAndOneNexus4OnLinksys42=new TreeMap<>();
    static {
        tabletsTwoFiresAndOneNexus4OnLinksys42.put(1,"192.168.1.21"); // ray's 2'nd fire
        tabletsTwoFiresAndOneNexus4OnLinksys42.put(2,"192.168.1.22"); // conrad's fire
        tabletsTwoFiresAndOneNexus4OnLinksys42.put(99,"192.168.1.99"); // ray's nexus 4
    }
}

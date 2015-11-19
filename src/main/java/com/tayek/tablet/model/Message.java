package com.tayek.tablet.model;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import com.tayek.*;
import com.tayek.tablet.*;
import static com.tayek.io.IO.*;

public class Message implements From<Message>,java.io.Serializable {
    // put message factory in tablet or client?
    // everything has an id (a small integer) and some have names
    // type|group|tablet|button|data
    // state - include all
    // where is reset? and what about the S? (buzzer)
    public enum Type {
        // ask for current state
        // other? - control - turn off simulation
        // can i view (foreigner)
        // add ip to list and send to?
        // small language here?
        // broadcast a name change? or ip change? - we should be able to do this
        dummy,error,normal;
        public boolean isNormal() {
            return this.equals(normal);
        }
        public boolean isControl() {
            return !this.equals(normal);
        }
    }
    public Message(Type type,Integer groupId,Integer from,Integer button,String string) {
        this.type=type;
        this.groupId=groupId;
        this.tabletId=from;
        this.button=button;
        this.string=string;
    }
    public boolean isNormal() {
        return type.equals(Type.normal);
    }
    public boolean isControl() {
        return !type.equals(Type.normal);
    }
    public static Boolean fromCharacter(Character character) {
        return character==null?null:character=='T'?true:character=='F'?false:null;
    }
    public Boolean state(int buttonId) {
        return fromCharacter(string.charAt(buttonId-1));
    }
    public Boolean[] states() {
        Boolean[] states=new Boolean[string.length()];
        for(int i=1;i<=states.length;i++)
            states[i-1]=state(i);
        return states;
    }
    @Override public Message from(String string) {
        return staticFrom(string);
    }
    @Override public String toString() {
        return type.name()+delimiter+groupId+delimiter+tabletId+delimiter+button+delimiter+string;
    }
    public static Message staticFrom(String string) {
        if(string==null) {
            staticLogger.warning("string is null!");
            return null;
        }
        String[] parts=string.split(Pattern.quote(""+delimiter));
        if(parts.length<5) { return error("too short!"); }
        Type type=null;
        try {
            type=Type.valueOf(parts[0]);
            if(type.equals(Type.error)) {
                p("from constructed and error message!");
                return error(parts[4]);
            }
        } catch(IllegalArgumentException e) {
            p(string+" caught: "+e);
            e.printStackTrace();
            return error(string+" threw: "+e);
        }
        Integer groupId=new Integer(parts[1]);
        Integer fromId=new Integer(parts[2]);
        Integer button=new Integer(parts[3]);
        String stringPart=parts[4];
        if(type.equals(Type.normal)) for(int i=0;i<stringPart.length();i++)
            if(!(stringPart.charAt(i)=='T'||stringPart.charAt(i)=='F')) return error(string+" has bad state value(s)!");
        return new Message(type,groupId,fromId,button,stringPart);
    }
    // not used, get rid of it?
    public static Message error(String string) {
        return new Message(Type.error,0,0,0,string);
    }
    public static Message process(int groupId,Receiver<Message> receiver,SocketAddress socketAddress,String string) {
        Message message=Message.staticFrom(string);
        if(message!=null) {
            if(message.groupId.equals(groupId)) {
                if(receiver!=null) try {
                    receiver.receive(message,null);
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
                else staticLogger.warning("receiver is null!");
            } else staticLogger.warning("received a message from another group: "+message.groupId);
        } else staticLogger.warning("received a null messagefrom : "+socketAddress);
        return message;
    }
    public final Type type;
    public final Integer groupId;
    public final Integer tabletId;
    public final Integer button;
    public final String string;
    public static final Character delimiter='|';
    public static final Message dummy=new Message(Type.dummy,0,0,0,"dummy");
    public static final Set<Class<?>> set=new LinkedHashSet<>();
    private static final long serialVersionUID=1L;
    public final Logger logger=Logger.getLogger(getClass().getName());
    public static final Logger staticLogger=Logger.getLogger(Message.class.getName());
}

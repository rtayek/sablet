package com.tayek.tablet.model;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import com.tayek.tablet.*;
import com.tayek.tablet.model.Message.Receiver;
public class Message implements java.io.Serializable {
    public enum Type {
        normal,startup,hello,goodbye;
        public boolean isNormal() {
            return this.equals(normal);
        }
        public boolean isControl() {
            return !this.equals(normal);
        }
    }
    public static Message start(Group group,int from) {
        return new Message(group,from,Type.startup,0);
    }
    public Message(Tablet tablet,Type type,int extra) {
        this(tablet.group,tablet.tabletId,type,extra);
    }
    public Message(Group group,int from,Type type,int extra) {
        this(group.groupId,from,type,extra);
    }
    public Message(Integer groupId,int from,Type type,int extra) {
        this(groupId,from,type,extra,false);
    }
    public Message(Tablet tablet,Type type,Integer button,boolean state) {
        this(tablet.group,tablet.tabletId,type,button,state);
    }
    public Message(Group group,Integer from,Type type,Integer button,boolean state) {
        this(group.groupId,from,type,button,state);
    }
    public Message(Integer groupId,Integer from,Type type,Integer button,boolean state) {
        this.groupId=groupId;
        this.tabletId=from;
        this.type=type;
        this.button=button;
        this.state=state;
    }
    public interface Sender {
        Integer tabletId();
        void send(Message message) throws IOException;
        Logger logger=Logger.getLogger(Sender.class.getName());
    }
    public interface Receiver<T> {
        void receive(T message) /*throws IOException*/;
        Logger logger=Logger.getLogger(Receiver.class.getName());
        public static class DummyReceiver<T> implements Receiver<T> {
            @Override public void receive(T t) {
                this.t=t;
            }
            public T t;
        }
    }
    public boolean isNormal() {
        return type.equals(Type.normal);
    }
    public boolean isControl() {
        return !type.equals(Type.normal);
    }
    @Override public String toString() {
        return groupId+" "+tabletId+" "+type+" "+button+" "+state;
    }
    public static Message from(String string) {
        if(string==null) {
            staticLogger.warning("string is null!");
            // maybe invent a no-op? or null?
            return null;
        }
        String[] parts=string.split(" ");
        if(parts.length!=5) staticLogger.severe("bad message: "+string);
        Integer groupId=new Integer(parts[0]);
        Integer fromId=new Integer(parts[1]);
        Type type=Type.valueOf(parts[2]);
        Integer button=null;
        if(type.isControl()) {
            if(parts[3].startsWith("/")) ;// button=Utility.i
        }
        // add error checking so the value of's can never through
        button=Integer.valueOf(parts[3]);
        Boolean state=Boolean.valueOf(parts[4]);
        Message message=new Message(groupId,fromId,type,button,state);
        return message;
    }
    public static Message process(int groupId,Receiver<Message> receiver,SocketAddress socketAddress,String string) {
        Message message=Message.from(string);
        if(message!=null) {
            if(message.groupId.equals(groupId)) {
                if(receiver!=null) try {
                    receiver.receive(message);
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
                else staticLogger.warning("receiver is null!");
            } else staticLogger.warning("received a message from another group: "+message.groupId);
        } else staticLogger.warning("received a null messagefrom : "+socketAddress);
        return message;
    }
    public final Integer groupId;
    public final Integer tabletId;
    public final Type type;
    public /*final*/ Integer button; // hack for address;
    public final Boolean state;
    public static final Set<Class<?>> set=new LinkedHashSet<>();
    private static final long serialVersionUID=1L;
    public final Logger logger=Logger.getLogger(getClass().getName());
    public static final Logger staticLogger=Logger.getLogger(Message.class.getName());
}

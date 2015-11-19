package com.tayek.tablet.model;
import static com.tayek.io.IO.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import com.tayek.io.Audio;
import com.tayek.io.Audio.*;
import com.sun.imageio.plugins.common.I18N;
import com.tayek.*;
import com.tayek.tablet.Main;
import com.tayek.tablet.model.Message;
import com.tayek.tablet.model.Message.*;
import com.tayek.tablet.view.View;
public class Model extends Observable implements Receiver<Message>,Cloneable {
    public Model(int buttons) {
        this(buttons,++ids);
    }
    private Model(int buttons,int serialNumber) {
        this.serialNumber=serialNumber; // so clones will have the same serial
                                        // number
        this.buttons=buttons;
        states=new Boolean[buttons];
        reset();
    }
    public void reset() {
        synchronized(states) {
            for(int i=1;i<=buttons;i++)
                setState(i,false);
        }
    }
    public void setChangedAndNotify(Object object) {
        setChanged();
        notifyObservers(object);
    }
    public int fromBoolean(boolean state) {
        return !state?0:1;
    }
    public void setState(Integer id,Boolean state) {
        synchronized(states) {
            states[id-1]=state;
            setChangedAndNotify(id);
        }
    }
    public Integer lastOnFrom(Integer id) {
        synchronized(idToLastOnFrom) {
            return idToLastOnFrom.get(id);
        }
    }
    @Override public void receive(Message message,Object extra) {
        if(message!=null) {
            logger.fine("received message: "+message);
            if(extra!=null&&extra instanceof InetAddress) { // bad place for this!
                p("from inet address: "+extra);
                //InetAddress inetAddress=InetAddress.
                // ((SocketAddress)extra).
            }
            switch(message.type) {
                case normal: // assume that the button id is the one he pushed.
                    // sync this?
                    for(int i=1;i<=Math.min(buttons,message.string.length());i++) {
                        if(i==message.button) if(message.state(i)) {
                            synchronized(idToLastOnFrom) {
                                idToLastOnFrom.put(i,message.tabletId);
                            }
                            if(!state(i).equals(message.state(i))) {
                                int n=random.nextInt(Sound.values().length);
                                setChangedAndNotify("sound:"+n);
                            } else logger.finest("no change");
                        }
                        setState(i,message.state(i));
                    }
                    break;
                case dummy:
                    break;
                default:
                    throw new RuntimeException("message type: "+message.type+" was not handled!");
            }
        } else logger.warning(this+"received null message!");
    }
    public Boolean state(Integer id) {
        synchronized(states) {
            return states[id-1];
        }
    }
    public Boolean[] states() {
        Boolean[] copy=new Boolean[buttons];
        synchronized(states) {
            System.arraycopy(states,0,copy,0,buttons);
            return copy;
        }
    }
    public static Character toCharacter(Boolean state) {
        return state==null?null:state?'T':'F';
    }
    public String toCharacters() {
        String s="";
        for(boolean state:states)
            s+=toCharacter(state);
        return s;
    }
    @Override public String toString() {
        String s="("+serialNumber+"): {";
        synchronized(states) {
            s+=toCharacters();
            s+='}';
            return s;
        }
    }
    /* String f="\u22A5"; */
    public boolean areAllButtonsInTheSameState(Model model) {
        return areAllButtonsInTheSameState(this,model);
    }
    public static synchronized boolean areAllButtonsInTheSameState(Model model,Model model2) {
        // this may not require sync, since states() is sync'ed.
        boolean areEqual=true;
        final Boolean[] states=model.states(),states2=model2.states();
        for(int i=0;i<model.buttons;i++)
            if(!states[i].equals(states2[i])) {
                areEqual=false;
                break;
            }
        return areEqual;
    }
    public static void main(String[] args) throws Exception {
        // model.addObserver(ModelObserver.instance);
        Model model=new Model(7);
        p(model.toString());
        Message message=new Message(Type.normal,1,1,1,"FFTFFF");
        model.receive(message,null);
        message=new Message(Type.normal,1,1,1,"FFTFFFF");
        model.receive(message,null);
        message=new Message(Type.normal,1,1,1,"FFTFFFFF");
        model.receive(message,null);
        p(model.toString());
    }
    public Object clone() {
        Model clone=new Model(buttons,serialNumber);
        return clone;
    }
    public final int serialNumber;
    public final Integer buttons;
    private final Boolean[] states;
    private final Map<Integer,Integer> idToLastOnFrom=new TreeMap<>();
    final Random random=new Random();
    public final Logger logger=Logger.getLogger(getClass().getName());
    public static final Logger staticLogger=Logger.getLogger(Model.class.getName());
    static int ids=0;
}

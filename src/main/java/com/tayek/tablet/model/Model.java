package com.tayek.tablet.model;
import java.util.*;
import java.util.logging.Logger;
import com.tayek.io.Audio;
import com.tayek.io.Audio.*;
import com.tayek.*;
import com.tayek.tablet.Main;
import com.tayek.tablet.model.Message;
import com.tayek.tablet.model.Message.*;
public class Model extends Observable implements Receiver<Message>,Cloneable {
    public static class Observer implements java.util.Observer {
        public Observer(Model model) {
            this.model=model;
        }
        @Override public void update(Observable model,Object hint) {
            System.out.println("hint: "+hint);
            if(model instanceof Model) if(model.equals(this.model)) if(hint instanceof Sound) audioPlayer.play((Sound)hint);
            else System.out.println("not our hint: "+hint);
            else System.out.println("not our model!");
            else System.out.println("not a model!");
        }
        private final Model model;
        private static final Audio audioPlayer=Audio.factory.create();
    }
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
        System.out.println(countObservers());
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
    @Override public void receive(Message message) {
        if(message!=null) {
            logger.fine("received message: "+message);
            switch(message.type) {
                case normal:
                    if(message.state.equals(true)) {
                        synchronized(idToLastOnFrom) {
                            idToLastOnFrom.put(message.button,message.tabletId);
                        }
                        if(!state(message.button).equals(message.state)) {
                            // hint from set state is/was button id.
                            // new hint should be state changed
                            // (boolean,who)
                            int n=random.nextInt(Sound.values().length);
                            if(false) setChangedAndNotify("sound:"+n);
                            else Main.audio.play(Sound.values()[n]);
                        } else logger.finest("no change");
                    }
                    setState(message.button,message.state);
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
    @Override public String toString() {
        String s="("+serialNumber+"): {";
        synchronized(states) {
            for(boolean state:states)
                s+=state?'T':"F";
            s+='}';
            return s;
        }
    }
    /* String f="\u22A5"; */
    public boolean areAllButtonsInTheSameState(Model model) {
        return areAllButtonsInTheSameState(this,model);
    }
    public static synchronized boolean areAllButtonsInTheSameState(Model model,Model model2) {
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
        Model model=new Model(7);
        // model.addObserver(ModelObserver.instance);
        System.out.println(model);
        Message message=new Message(1,1,Type.normal,1,true);
        model.receive(message);
        System.out.println(model);
    }
    public Object clone() {
        Model clone=new Model(buttons,serialNumber);
        return clone;
    }
    public final int serialNumber;
    public final Integer buttons;
    private final Boolean[] states;
    public final Map<Integer,Integer> idToLastOnFrom=new TreeMap<>();
    final Random random=new Random();
    public final Logger logger=Logger.getLogger(getClass().getName());
    static int ids=0;
}

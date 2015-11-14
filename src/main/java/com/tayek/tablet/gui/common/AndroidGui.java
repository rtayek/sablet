package com.tayek.tablet.gui.common;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.tayek.tablet.Tablet;
import com.tayek.tablet.model.Message;
import com.tayek.tablet.model.Message.Type;
// could be more than one instance
// make our gui use this
// our gui should probably ro on awt/edt thread!
public class AndroidGui { // this is probably a bridge/mediator??
    public AndroidGui(Tablet tablet,Toaster toaster,GuiAdapterABC guiAdapterABC) {
        this.tablet=tablet;
        this.toaster=toaster;
        this.guiAdapterABC=guiAdapterABC;
    }
    // not the same as the one click in the android code
    // ??? - need to make this work the same way
    public void onClick(final int buttonId,final boolean state) {
        logger.info("on click in "+this+" for tablet"+tablet);
        toaster.toast("on click in "+this);
        tablet.group.model.setState(buttonId,state); // maybe move up?
        Thread thread=new Thread(new Runnable() {
            @Override public void run() {
                Message message=new Message(tablet,Message.Type.normal,buttonId,state);
                tablet.broadcast(message);
            }
        },"pre-broadcast");
        thread.start();
        // join(thread);
    }
    public void start() {
        toaster.toast("start server in "+this);
        thread=new Thread(new Runnable() {
            @Override public void run() {
                try {
                    Thread.sleep(100);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                tablet.startListening();
            }
        },"start server");
        thread.start();
        // join(thread);
    }
    // maybe we should always run this on a thread!
    // we need to for android!
    public void sendMessage(Message message) {
        thread=new Thread(new Runnable() {
            @Override public void run() {
                Message message=new Message(tablet,Type.startup,0);
                // wtf! why am i creating a start message instead of sending
                // what the caller gave me?
                tablet.broadcast(message);
            }
        },"pre-broadcast");
        thread.start();
        // join(thread);
    }
    final Toaster toaster;
    Thread thread;
    public GuiAdapterABC guiAdapterABC;
    public final Tablet tablet;
    public final Map<Integer,Object> idToButton=new LinkedHashMap<>();
    public final Logger logger=Logger.getLogger(getClass().getName());
    public static final Logger staticLogger=Logger.getLogger(AndroidGui.class.getName());
}

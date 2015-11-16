package com.tayek.tablet.io.gui.common;
import com.tayek.tablet.Tablet;
import com.tayek.tablet.model.Message;
import com.tayek.utilities.Utility;
public enum TabletMenuItem {
    Reset,Ping,Disconnect,Connect,Log;
    public void doItem(Tablet<Message> tablet) {
        doItem(this,tablet);
    }
    public static boolean isIem(int ordinal) {
        return 0<=ordinal&&ordinal<values().length?true:false;
    }
    public static void doItem(int ordinal,Tablet<Message> tablet) { // this one is called
                                                           // from android!
        if(tablet!=null) if(0<=ordinal&&ordinal<values().length) values()[ordinal].doItem(tablet);
        else System.out.println(ordinal+" is invalid ordinal for!");
        else System.out.println("tablet is null in do item!");
    }
    public static void doItem(TabletMenuItem tabletMenuItem,Tablet<Message> tablet) {
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
                if(!tablet.startListening())
                    System.out.println(Utility.method()+" startListening() failed!");
                break;
            case Log:
                // gui.textView.setVisible(!gui.textView.isVisible());
                break;
        }
    }
}

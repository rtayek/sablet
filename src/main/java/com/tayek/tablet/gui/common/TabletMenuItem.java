package com.tayek.tablet.gui.common;
import com.tayek.tablet.Tablet;
import com.tayek.tablet.model.Message;
public enum TabletMenuItem {
    Reset,Ping,Disconnect,Connect,Log;
    public void doItem(Tablet tablet) {
        doItem(this,tablet);
    }
    public static boolean isIem(int ordinal) {
        return 0<=ordinal&&ordinal<values().length?true:false;
    }
    public static void doItem(int ordinal,Tablet tablet) { // this one is called
                                                           // from android!
        if(tablet!=null) if(0<=ordinal&&ordinal<values().length) values()[ordinal].doItem(tablet);
        else System.out.println(ordinal+" is invalid ordinal for!");
        else System.out.println("tablet is null in do item!");
    }
    public static void doItem(TabletMenuItem tabletMenuItem,Tablet tablet) {
        // maybe move
        switch(tabletMenuItem) {
            case Reset:
                tablet.group.model.reset();
                break;
            case Ping:
                Message message=new Message(tablet,Message.Type.startup,0);
                tablet.broadcast(message);
                break;
            case Disconnect:
                tablet.stop();
                break;
            case Connect:
                tablet.start();
                break;
            case Log:
                // gui.textView.setVisible(!gui.textView.isVisible());
                break;
        }
    }
}

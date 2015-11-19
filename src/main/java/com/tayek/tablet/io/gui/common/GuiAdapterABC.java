package com.tayek.tablet.io.gui.common;
import java.util.Observable;
import java.util.Observer;
import com.tayek.tablet.Tablet;
import com.tayek.tablet.model.*;
import com.tayek.tablet.view.View;
public abstract class GuiAdapterABC implements GuiAdapter,View {
    public GuiAdapterABC(Tablet<Message> tablet) {
        this.tablet=tablet;
    }
    @Override public void update(Observable o,Object hint) {
        // sets state and text for all of the buttons widgets
        // maybe button needs a name
        // default tablet + buttonId
        // config names themes on the fly for demo?
        for(int buttonId=1;buttonId<=tablet.group.model.buttons;buttonId++) {
            setButtonState(buttonId,tablet.group.model.state(buttonId));
            String ourName=tablet.group.buttonName(tablet.tabletId(),buttonId);
            if(tablet.group.model.state(buttonId).equals(true)) {
                Integer lastOnFrom=tablet.group.model.lastOnFrom(buttonId);
                // work here on the names!
                if(lastOnFrom!=null){ // assume button id = tablet id!
                    String hisName=tablet.group.buttonName(lastOnFrom,buttonId);
                    setButtonText(buttonId,pad(ourName+" ("+hisName+")"));
                }
                else setButtonText(buttonId,pad(ourName));
            } else setButtonText(buttonId,pad(ourName));
        }
    }
    static int length=40;
    public static String pad(String string) {
        for(;string.length()<length;string+=' ')
            ;
        return string;
    }
    final Tablet<Message> tablet;
}

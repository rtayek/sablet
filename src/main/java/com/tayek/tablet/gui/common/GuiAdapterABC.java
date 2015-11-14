package com.tayek.tablet.gui.common;
import java.util.Observable;
import java.util.Observer;
import com.tayek.tablet.model.Model;
public abstract class GuiAdapterABC implements GuiAdapter,Observer {
    public GuiAdapterABC(Model model) {
        this.model=model;
    }
    @Override public void update(Observable o,Object hint) {
        // sets state and text for all of the buttons widgets
        for(int buttonId=1;buttonId<=model.buttons;buttonId++) {
            setButtonState(buttonId,model.state(buttonId));
            if(model.state(buttonId).equals(true)) {
                Integer lastOnFrom=model.idToLastOnFrom.get(buttonId);
                if(lastOnFrom==null) {
                    setButtonText(buttonId,pad("Room "+buttonId));
                } else setButtonText(buttonId,pad("Room "+buttonId+ "(from: "+model.idToLastOnFrom.get(buttonId))+")");
            } else setButtonText(buttonId,pad("Room "+buttonId));
        }
    }
    static int length=20;
    public static String pad(String string) {
        for(;string.length()<length;string+=' ')
            ;
        return string;
    }
    final Model model;
}

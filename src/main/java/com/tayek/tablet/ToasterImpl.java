package com.tayek.tablet;
import com.tayek.tablet.gui.common.Toaster;
public class ToasterImpl implements Toaster {
    @Override public void toast(String string) {
        System.out.println("toast: "+string);
    }
}

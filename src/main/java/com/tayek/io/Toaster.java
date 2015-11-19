package com.tayek.io;
import java.util.logging.Logger;
public interface Toaster {
    void toast(String string);
    public static class ToasterImpl implements Toaster {
        @Override public void toast(String string) {
            logger.info("toast: "+string);
        }
    }
    Logger logger=Logger.getLogger(Toaster.class.getName());
}

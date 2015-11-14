package com.tayek.tablet;
import java.util.Map;
import java.util.logging.*;
import com.tayek.tablet.gui.common.Toaster;
import com.tayek.utilities.LoggingHandler;
public enum Main {
    log {
        @Override public void init() {
            if(!once) {
                map=LoggingHandler.makeMapAndSetLevels(Tablet.loggers);
                System.out.println("logging was initialized.");
                once=true;
            }
        }
        public void setLevel(Level level) {
            if(!once) init();
            for(Logger logger:map.values())
                logger.setLevel(level);
        }
        private Map<Class<?>,Logger> map;
        private boolean once;
    },
    next {
        @Override public void init() {
            System.out.println("next");
        }
    };
    abstract public void init();
    public void setLevel(Level level) {
        log.setLevel(level);
    }
    public static Toaster toaster=new ToasterImpl();
}

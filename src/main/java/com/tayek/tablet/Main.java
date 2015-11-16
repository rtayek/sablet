package com.tayek.tablet;
import java.util.Map;
import java.util.logging.*;
import com.tayek.io.*;
import com.tayek.io.Toaster.*;
// root fires
// logging initialization and to file or sever
// random unattended execution
// fancy buttons
// move to other router
// http://cs.nyu.edu/~yap/prog/cygwin/FAQs.html
public enum Main { // http://c2.com/cgi/wiki?GodClass
    log { // how to log to file or some server?
        // use logging.properties
        // figure out on pc and port to android
        // see: http://www.javapractices.com/topic/TopicAction.do?Id=143
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
    public static final Audio audio=Audio.factory.create();
    public static Toaster toaster=new ToasterImpl();
}

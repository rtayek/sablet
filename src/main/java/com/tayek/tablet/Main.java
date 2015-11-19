package com.tayek.tablet;
import static com.tayek.io.IO.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.*;
import com.tayek.*;
import com.tayek.io.*;
import com.tayek.io.Toaster.*;
import com.tayek.tablet.Group;
import com.tayek.tablet.controller.CommandLine;
import com.tayek.tablet.io.gui.swing.Gui;
import com.tayek.tablet.model.*;
import com.tayek.utilities.Dispatcher;
// maybe change my ip? http://19216811.mobi/192.168.0.1...en8
// since 192.168.1.2 may be taken by someone else
// root fires
// logging initialization and to file or sever
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
                map=LoggingHandler.makeMapAndSetLevels(loggers);
                p("logging was initialized.");
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
            p("next");
        }
    };
    abstract public void init();
    public void setLevel(Level level) {
        log.setLevel(level);
    }
    public static void main(String[] arguments)
            throws IllegalAccessException,IllegalArgumentException,InvocationTargetException,NoSuchMethodException,SecurityException,IOException {
        Dispatcher dispatcher=new Dispatcher(arguments) {
            {
                while(entryPoints.size()>0)
                    remove(1);
                add(CommandLine.class);
                add(Tablet.class);
                add(Gui.class);
            }
        };
        dispatcher.run();
        printThreads();
    }
public static boolean sound;
    public static final String hostPrefix="192.168.1.";
    public static final Map<Integer,Group> groups=new TreeMap<>();
    public static final Audio audio=Audio.factory.create();
    public static Toaster toaster=new ToasterImpl();
    public static final Set<Class<?>> loggers=new LinkedHashSet<>();
    static {
        loggers.add(Tablet.class);
        loggers.add(Client.class);
        loggers.add(Server.class);
        loggers.add(IO.Client.class);
        loggers.add(IO.Server.class);
        loggers.add(Group.class);
        loggers.add(Message.class);
        loggers.add(Sender.class);
        loggers.add(Receiver.class);
        loggers.add(Model.class);
        loggers.add(Toaster.class);
    }

}

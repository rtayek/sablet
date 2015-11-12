package com.tayek.tablet;
import com.tayek.utilities.*;
public class TabletLoggingHandler extends LoggingHandler {
    static void init() {
        LoggingHandler.makeMapAndSetLevels(Tablet.loggers);
        System.out.println("logging was initialized.");
    }
}

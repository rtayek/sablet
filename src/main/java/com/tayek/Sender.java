package com.tayek;
import java.util.logging.Logger;
public interface Sender<T> {
    boolean send(T t,int information); // consumer
    Logger logger=Logger.getLogger(Sender.class.getName());
}

package com.tayek;
import java.util.logging.Logger;
// this is Consumer<T>
public interface Receiver<T> { // same as typed callback?
    void receive(T t); // consumer
    Logger logger=Logger.getLogger(Receiver.class.getName());
    public static class DummyReceiver<T> implements Receiver<T> {
        @Override public void receive(T t) {
            this.t=t;
        }
        public T t;
    }
}

package com.tayek.tablet;
import java.util.logging.Logger;
import com.tayek.tablet.model.Message;
public interface Receiver {
    void receive(Message message);
    Logger logger=Logger.getLogger(Receiver.class.getName());
    public static class DummyReceiver implements Receiver {
        @Override public void receive(Message message) {
            this.message=message;
        }
        public Message message;
    }
}

package com.tayek.tablet;
import static org.junit.Assert.*;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import org.junit.*;
import org.junit.Test;
import com.tayek.tablet.model.Message;
import com.tayek.tablet.model.Message.*;
import com.tayek.tablet.model.Message.Receiver.*;
import com.tayek.utilities.LoggingHandler;
public class TcpTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {
        TabletLoggingHandler.init();
        LoggingHandler.setLevel(Level.OFF);
    }
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testOnce() throws Exception {
        sendAndReceiveOneMessage(12345);
    }
    @Test public void testManyTimes() throws Exception {
        for(int i=1;i<=1000000;i++) {
            // System.out.println(i);
            if(!(sendAndReceiveOneMessage(20000+i))) fail("failed at: "+i);
            Thread.sleep(10);
            if(i%100==0)
                System.out.println(i);
        }
    }
    private boolean sendAndReceiveOneMessage(int service) throws UnknownHostException,IOException,InterruptedException {
        InetAddress inetAddress=InetAddress.getLocalHost();
        DummyReceiver<Message> receiver=new DummyReceiver<>();
        Server server=new Server(inetAddress,service,receiver);
        server.start();
        Client client=new Client(inetAddress,service,10);
        Message message=new Message(1,1,Type.startup,0);
        client.send(message);
        Thread.sleep(10);
        server.stopServer();
        server.join();
        // System.out.println(receiver.t);
        if(receiver.t==null)
            System.out.println("null");
        boolean isOk=receiver.t!=null&&message.toString().equals(receiver.t.toString());
        return isOk;
    }
}

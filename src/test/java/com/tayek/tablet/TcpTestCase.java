package com.tayek.tablet;
import static com.tayek.tablet.io.IO.*;
import static org.junit.Assert.*;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import org.junit.*;
import org.junit.Test;
import com.tayek.tablet.Receiver.DummyReceiver;
import com.tayek.tablet.model.Message;
public class TcpTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {
        Main.log.setLevel(Level.OFF);
    }
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testOnce() throws Exception {
        sendAndReceiveOneMessage(12345);
    }
    @Test public void testManyTimes() throws Exception {
        for(Integer i=1;i<=100;i++) {
            // p(i);
            if(!(sendAndReceiveOneMessage(20_000+i))) fail("failed at: "+i);
            Thread.sleep(10);
            if(i%1000==0)
                p(i.toString());
        }
    }
    private boolean sendAndReceiveOneMessage(int service) throws UnknownHostException,IOException,InterruptedException {
        InetAddress inetAddress=InetAddress.getLocalHost();
        DummyReceiver receiver=new DummyReceiver();
        Server server=new Server(null,inetAddress,service,receiver);
        server.start();
        Client client=new Client(inetAddress,service,10);
        client.send(Message.dummy,1);
        Thread.sleep(10);
        server.stopServer();
        server.join();
        // p(receiver.t);
        if(receiver.message==null)
            p("null");
        boolean isOk=receiver.message!=null&&Message.dummy.toString().equals(receiver.message.toString());
        return isOk;
    }
}

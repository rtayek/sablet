package com.tayek.tablet;
import static org.junit.Assert.*;
import java.net.*;
import java.util.logging.Level;
import org.junit.*;
import org.junit.Test;
import com.tayek.tablet.model.Message;
import com.tayek.utilities.LoggingHandler;
public class TabletTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {
        Main.log.setLevel(Level.OFF);
    }
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test() throws InterruptedException,UnknownHostException {
        Group group=new Group(1,Group.tabletsTwo);
        Tablet tablet=new Tablet(group,1);
        tablet.startListening();
        Tablet tablet2=new Tablet(group,2);
        tablet2.startListening();
        InetAddress inetAddress=InetAddress.getLocalHost();
        Message message=Message.start(group,3);
        // Client client=new Client(inetAddress,Server.port(1));
        // client.send(message);
        // Client client2=new Client(inetAddress,Server.port(2));
        // client.send(message);
        System.out.println("-----------");
        message=Message.start(group,1);
        tablet.broadcast(message);
        message=Message.start(group,2);
        tablet2.broadcast(message);
        Thread.sleep(200);
        System.out.println(tablet.server.received());
        System.out.println(tablet2.server.received());
        tablet.stopListening();
        tablet2.stopListening();
    }
}

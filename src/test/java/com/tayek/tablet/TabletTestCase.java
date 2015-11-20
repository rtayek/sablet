package com.tayek.tablet;
import static com.tayek.tablet.io.IO.*;
import static org.junit.Assert.*;
import java.net.*;
import java.util.Iterator;
import java.util.logging.Level;
import org.junit.*;
import org.junit.Test;
import com.tayek.tablet.model.Message;
public class TabletTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {
        Main.log.setLevel(Level.OFF);
    }
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test() throws InterruptedException,UnknownHostException {
        Group group=new Group(1,Group.g2);
        Iterator<Integer> i=group.tablets().iterator();
        Tablet tablet=new Tablet(group,i.next());
        tablet.startListening();
        Tablet tablet2=new Tablet(group,i.next());
        tablet2.startListening();
        InetAddress inetAddress=InetAddress.getLocalHost();
        p("-----------");
        tablet.send(Message.dummy,0);
        tablet2.send(Message.dummy,0);
        Thread.sleep(200);
        p(tablet.server.received().toString());
        p(tablet2.server.received().toString());
        tablet.stopListening();
        tablet2.stopListening();
    }
}

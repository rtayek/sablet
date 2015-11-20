package com.tayek.tablet.model;
import static org.junit.Assert.*;
import java.util.logging.Level;
import org.junit.*;
import com.tayek.tablet.Main;
import com.tayek.tablet.model.Message.Type;
public class MessageTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {
        Main.log.init();
        Main.log.setLevel(Level.OFF);
    }
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testNormalMessageWithTrue() {
        Message message=new Message(Type.normal,1,2,3,"FTFF");
        String expected=message.toString();
        Message m=Message.staticFrom(expected);
        String actual=m.toString();
        assertEquals(expected,actual);
    }
    @Test public void testNormalMessageWithFalse() {
        Message message=new Message(Type.normal,1,2,3,"FFFF");
        String expected=message.toString();
        Message m=Message.staticFrom(expected);
        String actual=m.toString();
        assertEquals(expected,actual);
    }
    @Test public void testDummyMessage() {
        Message message=Message.dummy;
        String expected=message.toString();
        Message m=Message.staticFrom(expected);
        String actual=m.toString();
        assertEquals(expected,actual);
    }
    @Test public void testErrorMessage() {
        Message message=Message.error("foo");
        String expected=message.toString();
        Message m=Message.staticFrom(expected);
        String actual=m.toString();
        assertEquals(expected,actual);
    }
}

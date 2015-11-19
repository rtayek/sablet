package com.tayek.tablet.model;

import static org.junit.Assert.*;
import org.junit.*;
import com.tayek.tablet.model.Message.Type;

public class ModelTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testSort() {
        model=new Model(5);
        message=new Message(Type.normal,1,1,1,"FTFF");
        model.receive(message,null);
        assertTrue(model.state(2));
    }
    @Test public void testJustRight() {
        model=new Model(5);
        message=new Message(Type.normal,1,1,1,"FTFFFFF");
        model.receive(message,null);
        assertTrue(model.state(2));
    }
    @Test public void testTooLong() {
        model=new Model(5);
        message=new Message(Type.normal,1,1,1,"FTFFFFFFFFFFFFFFFFFFFFFF");
        model.receive(message,null);
        assertTrue(model.state(2));
    }
    Model model;
    Message message;
}

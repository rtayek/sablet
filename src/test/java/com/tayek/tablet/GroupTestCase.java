package com.tayek.tablet;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.Test;
public class GroupTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test() {
        Group.Address address=new Group.Address("192.168.1.");
        for(int expected=0;expected<256;expected++) {
            String a=address.fromLeastSignificantOctet(expected);
            int actual=address.leastSignificantOctet(a);
            assertEquals(expected,actual);
        }
    }
}

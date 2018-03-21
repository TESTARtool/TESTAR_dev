package org.fruit;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Verify the Assert Utility cless has the proper behaviour.
 * Created by floren on 27-4-2017.
 */
public class AssertTest {
    @Test
    public void testTextNull()  {

        try{
            Assert.hasText(null);
            fail("An exception was expected when text is null");
        } catch (Exception e) {
            assertTrue("The method shall throw an illegal argument exception",e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testTextNotEmpty()  {
        try{
            Assert.hasText("");
            fail("An exception was expected when text is empty");
        } catch (Exception e) {
            assertTrue("The method shall throw an illegal argument exception",e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testTextIsOK()  {
        try{
            Assert.hasText("Good text");
        } catch (Exception e) {
            fail("An exception is not expected");
        }
    }

}
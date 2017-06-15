package org.fruit;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for Pair class
 * Created by floren on 7-5-2017.
 */
public class PairTest {

    private Integer leftElement = 10;
    private Integer rightElement = 20;
    private Pair<Object,Object> referenceObject = new Pair<>(leftElement,rightElement);

    @Test
    public void from()  {
        Pair<Integer,Integer> result = Pair.from(leftElement,rightElement);
        assertEquals("The leftelement:", 10, result.left().intValue());
        assertEquals( "The rightelement:", 20,result.right().intValue());
    }

    @Test
    public void testEquality() {
        assertTrue(referenceObject.equals(Pair.from(leftElement,rightElement)));
        assertTrue(referenceObject.equals(referenceObject));
    }

}
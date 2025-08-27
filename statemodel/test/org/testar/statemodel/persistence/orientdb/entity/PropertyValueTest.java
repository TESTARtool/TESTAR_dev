package org.testar.statemodel.persistence.orientdb.entity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

public class PropertyValueTest {

    @Test
    public void testStringValue() {
        PropertyValue property = new PropertyValue(OType.STRING, "test-value");

        assertEquals(OType.STRING, property.getType());
        assertEquals("test-value", property.getValue());
    }

    @Test
    public void testIntegerValue() {
        PropertyValue property = new PropertyValue(OType.INTEGER, 11);

        assertEquals(OType.INTEGER, property.getType());
        assertEquals(11, property.getValue());
    }

    @Test
    public void testDoubleValue() {
        PropertyValue property = new PropertyValue(OType.DOUBLE, 3.14);
        assertEquals(OType.DOUBLE, property.getType());
        assertEquals(3.14, (Double) property.getValue(), 0.0001);
    }

    @Test
    public void testFloatValue() {
        PropertyValue property = new PropertyValue(OType.FLOAT, 2.5f);
        assertEquals(OType.FLOAT, property.getType());
        assertEquals(2.5f, (Float) property.getValue(), 0.0001f);
    }

    @Test
    public void testEmbeddedSetValue() {
        Set<String> embeddedSet = new HashSet<>();
        embeddedSet.add("A");
        embeddedSet.add("B");

        PropertyValue property = new PropertyValue(OType.EMBEDDEDSET, embeddedSet);
        assertEquals(OType.EMBEDDEDSET, property.getType());
        assertEquals(embeddedSet, property.getValue());
    }

    @Test
    public void testNullValue() {
        PropertyValue property = new PropertyValue(OType.STRING, null);

        assertEquals(OType.STRING, property.getType());
        assertNull(property.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullOType() {
        new PropertyValue(null, "test-value");
    }

    @Test
    public void testConstructorWithAllowedNullValue() {
        PropertyValue property = new PropertyValue(OType.STRING, null);
        assertEquals(OType.STRING, property.getType());
        assertNull(property.getValue());
    }
}

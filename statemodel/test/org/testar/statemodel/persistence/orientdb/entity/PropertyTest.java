package org.testar.statemodel.persistence.orientdb.entity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyTest {

    @Test
    public void testConstructorWithoutChildType() {
        Property property = new Property("name", OType.STRING);

        assertEquals("name", property.getPropertyName());
        assertEquals(OType.STRING, property.getPropertyType());
        assertNull(property.getChildType());

        // Defaults
        assertFalse(property.isMandatory());
        assertFalse(property.isReadOnly());
        assertTrue(property.isNullable());
        assertFalse(property.isIdentifier());
        assertFalse(property.isAutoIncrement());
        assertFalse(property.isIndexAble());
    }

    @Test
    public void testConstructorWithChildType() {
        Property property = new Property("tags", OType.EMBEDDEDSET, OType.STRING);

        assertEquals("tags", property.getPropertyName());
        assertEquals(OType.EMBEDDEDSET, property.getPropertyType());
        assertEquals(OType.STRING, property.getChildType());
    }

    @Test
    public void testSettersAndGetters() {
        Property property = new Property("id", OType.INTEGER);

        property.setMandatory(true);
        property.setReadOnly(true);
        property.setNullable(false);
        property.setIdentifier(true);
        property.setAutoIncrement(true);
        property.setIndexAble(true);

        assertTrue(property.isMandatory());
        assertTrue(property.isReadOnly());
        assertFalse(property.isNullable());
        assertTrue(property.isIdentifier());
        assertTrue(property.isAutoIncrement());
        assertTrue(property.isIndexAble());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullPropertyName() {
        new Property(null, OType.STRING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptyPropertyName() {
        new Property("", OType.STRING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorBlankPropertyName() {
        new Property("    ", OType.STRING);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullPropertyType() {
        new Property("name", null);
    }

}

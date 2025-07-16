package org.testar.statemodel.persistence.orientdb.entity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.junit.Test;

import static org.junit.Assert.*;

public class DocumentEntityTest {

    @Test
    public void testConstructorInitializesFields() {
        EntityClass entityClass = new EntityClass("TestEntity", EntityClass.EntityType.Vertex);
        DocumentEntity document = new DocumentEntity(entityClass) {};

        assertNotNull(document.getEntityClass());
        assertEquals("TestEntity", document.getEntityClass().getClassName());
        assertTrue(document.getPropertyNames().isEmpty());
        assertTrue(document.updateEnabled());
    }

    @Test
    public void testAddAndGetPropertyValue() {
        EntityClass entityClass = new EntityClass("TestEntity", EntityClass.EntityType.Vertex);
        DocumentEntity document = new DocumentEntity(entityClass) {};

        PropertyValue value = new PropertyValue(OType.STRING, "example");
        document.addPropertyValue("key1", value);

        assertEquals(value, document.getPropertyValue("key1"));
        assertTrue(document.getPropertyNames().contains("key1"));
    }

    @Test
    public void testGetMissingPropertyReturnsNull() {
        EntityClass entityClass = new EntityClass("TestEntity", EntityClass.EntityType.Vertex);
        DocumentEntity document = new DocumentEntity(entityClass) {};

        assertNull(document.getPropertyValue("missing"));
    }

    @Test
    public void testEnableUpdateFlag() {
        EntityClass entityClass = new EntityClass("TestEntity", EntityClass.EntityType.Vertex);
        DocumentEntity document = new DocumentEntity(entityClass) {};

        document.enableUpdate(false);
        assertFalse(document.updateEnabled());

        document.enableUpdate(true);
        assertTrue(document.updateEnabled());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullEntityClass() {
        new DocumentEntity(null) {};
    }
}

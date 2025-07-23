package org.testar.statemodel.persistence.orientdb.entity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class EntityClassTest {

    private EntityClass vertexClass;
    private EntityClass edgeClass;

    @Before
    public void setUp() {
        vertexClass = new EntityClass("MyVertex", EntityClass.EntityType.Vertex);
        edgeClass = new EntityClass("MyEdge", EntityClass.EntityType.Edge);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("MyVertex", vertexClass.getClassName());
        assertEquals(EntityClass.EntityType.Vertex, vertexClass.getEntityType());
        assertTrue(vertexClass.getProperties().isEmpty());
    }

    @Test
    public void testSuperClassName() {
        vertexClass.setSuperClassName("BaseClass");
        assertEquals("BaseClass", vertexClass.getSuperClassName());
    }

    @Test
    public void testAddAndRetrieveProperties() {
        Property prop = new Property("name", OType.STRING);
        vertexClass.addProperty(prop);

        Set<Property> props = vertexClass.getProperties();
        assertEquals(1, props.size());
        assertTrue(props.contains(prop));
    }

    @Test
    public void testGetIdentifierReturnsCorrectProperty() {
        Property idProp = new Property("id", OType.INTEGER);
        idProp.setIdentifier(true);
        Property nameProp = new Property("name", OType.STRING);

        vertexClass.addProperty(nameProp);
        vertexClass.addProperty(idProp);

        Property identifier = vertexClass.getIdentifier();
        assertNotNull(identifier);
        assertEquals("id", identifier.getPropertyName());
    }

    @Test
    public void testGetIdentifierReturnsNullWhenNoneSet() {
        vertexClass.addProperty(new Property("name", OType.STRING));
        assertNull(vertexClass.getIdentifier());
    }

    @Test
    public void testIsVertex() {
        assertTrue(vertexClass.isVertex());
        assertFalse(vertexClass.isEdge());
    }

    @Test
    public void testIsEdge() {
        assertTrue(edgeClass.isEdge());
        assertFalse(edgeClass.isVertex());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullClassName() {
        new EntityClass(null, EntityClass.EntityType.Vertex);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyClassName() {
        new EntityClass("", EntityClass.EntityType.Vertex);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithBlankClassName() {
        new EntityClass("    ", EntityClass.EntityType.Vertex);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullEntityType() {
        new EntityClass("MyVertex", null);
    }

}

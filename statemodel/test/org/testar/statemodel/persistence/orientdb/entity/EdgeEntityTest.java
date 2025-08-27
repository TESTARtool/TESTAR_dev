package org.testar.statemodel.persistence.orientdb.entity;

import org.junit.Before;
import org.junit.Test;

import com.orientechnologies.orient.core.metadata.schema.OType;

import static org.junit.Assert.*;

public class EdgeEntityTest {

    private VertexEntity source;
    private VertexEntity target;
    private EdgeEntity edge;

    @Before
    public void setUp() {
        EntityClass vertexClass = new EntityClass("VertexClass", EntityClass.EntityType.Vertex);
        source = new VertexEntity(vertexClass);
        target = new VertexEntity(vertexClass);

        EntityClass edgeClass = new EntityClass("EdgeClass", EntityClass.EntityType.Edge);
        edge = new EdgeEntity(edgeClass, source, target);
    }

    @Test
    public void testConstructorInitializesFields() {
        assertNotNull(edge.getEntityClass());
        assertEquals("EdgeClass", edge.getEntityClass().getClassName());
        assertTrue(edge.getEntityClass().isEdge());
        assertEquals(source, edge.getSourceEntity());
        assertEquals(target, edge.getTargetEntity());
    }

    @Test
    public void testGetSourceEntity() {
        assertSame(source, edge.getSourceEntity());
    }

    @Test
    public void testGetTargetEntity() {
        assertSame(target, edge.getTargetEntity());
    }

    @Test
    public void testPropertiesAreInitiallyEmpty() {
        assertTrue(edge.getPropertyNames().isEmpty());
    }

    @Test
    public void testAddAndRetrievePropertyValue() {
        PropertyValue value = new PropertyValue(OType.STRING, "test");
        edge.addPropertyValue("key", value);

        assertEquals(value, edge.getPropertyValue("key"));
        assertTrue(edge.getPropertyNames().contains("key"));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullEntityClass() {
        new EdgeEntity(null, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullSourceVertex() {
        EntityClass edgeClass = new EntityClass("EdgeClass", EntityClass.EntityType.Edge);
        new EdgeEntity(edgeClass, null, target);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullTargetVertex() {
        EntityClass edgeClass = new EntityClass("EdgeClass", EntityClass.EntityType.Edge);
        new EdgeEntity(edgeClass, source, null);
    }

}

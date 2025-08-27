package org.testar.statemodel.persistence.orientdb.entity;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class VertexEntityTest {

    private VertexEntity vertex;
    private VertexEntity source;
    private VertexEntity target;
    private EdgeEntity incoming;
    private EdgeEntity outgoing;

    @Before
    public void setUp() {
        EntityClass vertexClass = new EntityClass("TestVertex", EntityClass.EntityType.Vertex);
        vertex = new VertexEntity(vertexClass);

        source = new VertexEntity(new EntityClass("SourceVertex", EntityClass.EntityType.Vertex));
        target = new VertexEntity(new EntityClass("TargetVertex", EntityClass.EntityType.Vertex));

        incoming = new EdgeEntity(new EntityClass("IncomingEdge", EntityClass.EntityType.Edge), source, vertex);
        outgoing = new EdgeEntity(new EntityClass("OutgoingEdge", EntityClass.EntityType.Edge), vertex, target);
    }

    @Test
    public void testConstructorInitializesEmptyEdgeSets() {
        assertNotNull(vertex.getIncomingEdges());
        assertNotNull(vertex.getOutgoingEdges());
        assertTrue(vertex.getIncomingEdges().isEmpty());
        assertTrue(vertex.getOutgoingEdges().isEmpty());
    }

    @Test
    public void testAddOutgoingEdge() {
        vertex.addOutgoingEdge(outgoing);
        Set<EdgeEntity> edges = vertex.getOutgoingEdges();
        assertEquals(1, edges.size());
        assertTrue(edges.contains(outgoing));
    }

    @Test
    public void testAddIncomingEdge() {
        vertex.addIncomingEdge(incoming);
        Set<EdgeEntity> edges = vertex.getIncomingEdges();
        assertEquals(1, edges.size());
        assertTrue(edges.contains(incoming));
    }

    @Test
    public void testEntityClassMetadataIsPreserved() {
        assertEquals("TestVertex", vertex.getEntityClass().getClassName());
        assertTrue(vertex.getEntityClass().isVertex());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullEntityClass() {
        new VertexEntity(null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullOutgoingEdge() {
        vertex.addOutgoingEdge(null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullIncomingEdge() {
        vertex.addIncomingEdge(null);
    }

}

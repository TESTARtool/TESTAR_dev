package org.testar.statemodel.persistence.orientdb.entity;

import org.junit.Test;
import static org.junit.Assert.*;

public class EntityClassFactoryTest {

    @Test
    public void testCreateAbstractStateEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractState);
        assertNotNull(entityClass);
        assertEquals("AbstractState", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Vertex, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateAbstractActionEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractAction);
        assertNotNull(entityClass);
        assertEquals("AbstractAction", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Edge, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateAbstractStateModelEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractStateModel);
        assertNotNull(entityClass);
        assertEquals("AbstractStateModel", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Vertex, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateConcreteStateEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteState);
        assertNotNull(entityClass);
        assertEquals("ConcreteState", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Vertex, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateConcreteActionEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteAction);
        assertNotNull(entityClass);
        assertEquals("ConcreteAction", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Edge, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateWidgetEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.Widget);
        assertNotNull(entityClass);
        assertEquals("Widget", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Vertex, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateIsParentOfEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.isParentOf);
        assertNotNull(entityClass);
        assertEquals("isParentOf", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Edge, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateIsChildOfEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.isChildOf);
        assertNotNull(entityClass);
        assertEquals("isChildOf", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Edge, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateIsAbstractedByEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.isAbstractedBy);
        assertNotNull(entityClass);
        assertEquals("isAbstractedBy", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Edge, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateBlackHoleEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.BlackHole);
        assertNotNull(entityClass);
        assertEquals("BlackHole", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Vertex, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateUnvisitedAbstractActionEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.UnvisitedAbstractAction);
        assertNotNull(entityClass);
        assertEquals("UnvisitedAbstractAction", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Edge, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateTestSequenceEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.TestSequence);
        assertNotNull(entityClass);
        assertEquals("TestSequence", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Vertex, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateSequenceNodeEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.SequenceNode);
        assertNotNull(entityClass);
        assertEquals("SequenceNode", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Vertex, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateSequenceStepEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.SequenceStep);
        assertNotNull(entityClass);
        assertEquals("SequenceStep", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Edge, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateAccessedEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.Accessed);
        assertNotNull(entityClass);
        assertEquals("Accessed", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Edge, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

    @Test
    public void testCreateFirstNodeEntity() {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.FirstNode);
        assertNotNull(entityClass);
        assertEquals("FirstNode", entityClass.getClassName());
        assertEquals(EntityClass.EntityType.Edge, entityClass.getEntityType());
        assertFalse(entityClass.getProperties().isEmpty());
    }

}

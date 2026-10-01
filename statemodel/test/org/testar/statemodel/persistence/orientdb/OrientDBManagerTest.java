package org.testar.statemodel.persistence.orientdb;

import org.junit.Before;
import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.persistence.orientdb.entity.DocumentEntity;
import org.testar.statemodel.persistence.orientdb.entity.EdgeEntity;
import org.testar.statemodel.persistence.orientdb.entity.EntityManager;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;
import org.testar.statemodel.sequence.SequenceNode;
import org.testar.statemodel.util.EventHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class OrientDBManagerTest {

    private EntityManager entityManager;
    private OrientDBManager orientDBManager;
    private Map<String, Map<String, Object>> persistedConcreteStates;

    @Before
    public void setUp() {
        entityManager = mock(EntityManager.class);
        persistedConcreteStates = new HashMap<>();
        doAnswer(invocation -> {
            capturePersistedConcreteState(invocation.getArgument(0));
            return null;
        }).when(entityManager).saveEntity(any(DocumentEntity.class));
        orientDBManager = new OrientDBManager(new EventHelper(), entityManager);
    }

    @Test
    public void testPersistSequenceNodePreservesFirstConcreteStateVerdicts() {
        AbstractState abstractState = new AbstractState("abstract-state", Collections.emptySet());
        abstractState.setModelIdentifier("model-identifier");

        List<Verdict> faultVerdicts = Collections.singletonList(
                new Verdict(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT, "Accessibility issue"));
        List<Verdict> okVerdicts = Collections.singletonList(Verdict.OK);

        ConcreteState failingState = new ConcreteState("concrete-state", abstractState);
        failingState.addAttribute(Tags.OracleVerdicts, faultVerdicts);
        ConcreteState laterState = new ConcreteState("concrete-state", abstractState);
        laterState.addAttribute(Tags.OracleVerdicts, okVerdicts);

        orientDBManager.persistSequenceNode(new SequenceNode(
                "sequence-id", 1, failingState, null, Collections.emptySet()));
        orientDBManager.persistSequenceNode(new SequenceNode(
                "sequence-id", 2, laterState, null, Collections.emptySet()));

        Map<String, Object> persistedState = persistedConcreteStates.get("model-identifier-concrete-state");
        assertNotNull(persistedState);
        assertEquals(faultVerdicts, persistedState.get("OracleVerdicts"));
    }

    private void capturePersistedConcreteState(DocumentEntity documentEntity) {
        if (!(documentEntity instanceof EdgeEntity)) {
            return;
        }

        EdgeEntity edgeEntity = (EdgeEntity) documentEntity;
        if (!"Accessed".equals(edgeEntity.getEntityClass().getClassName())) {
            return;
        }

        VertexEntity stateEntity = edgeEntity.getTargetEntity();
        if (!"ConcreteState".equals(stateEntity.getEntityClass().getClassName())) {
            return;
        }

        String stateKey = (String) stateEntity.getPropertyValue("widgetId").getValue();
        if (persistedConcreteStates.containsKey(stateKey) && !stateEntity.updateEnabled()) {
            return;
        }

        Map<String, Object> properties = new HashMap<>();
        for (String propertyName : stateEntity.getPropertyNames()) {
            PropertyValue value = stateEntity.getPropertyValue(propertyName);
            properties.put(propertyName, value == null ? null : value.getValue());
        }
        persistedConcreteStates.put(stateKey, properties);
    }
}

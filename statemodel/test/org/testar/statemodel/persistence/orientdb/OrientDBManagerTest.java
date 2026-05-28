package org.testar.statemodel.persistence.orientdb;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
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
import static org.junit.Assert.assertFalse;
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
            DocumentEntity documentEntity = invocation.getArgument(0);
            capturePersistedConcreteState(documentEntity);
            return null;
        }).when(entityManager).saveEntity(any(DocumentEntity.class));
        orientDBManager = new OrientDBManager(new EventHelper(), entityManager);
    }

    @Test
    public void testPersistSequenceNodeDoesNotEnableConcreteStateUpdates() {
        AbstractState abstractState = new AbstractState("abstract-state", Collections.emptySet());
        abstractState.setModelIdentifier("model-identifier");

        List<Verdict> failVerdicts = Collections.singletonList(new Verdict(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT, "Accessibility issue"));
        List<Verdict> okVerdicts = Collections.singletonList(Verdict.OK);

        ConcreteState failingConcreteState = new ConcreteState("concrete-state", abstractState);
        failingConcreteState.addAttribute(Tags.OracleVerdicts, failVerdicts);

        ConcreteState okConcreteState = new ConcreteState("concrete-state", abstractState);
        okConcreteState.addAttribute(Tags.OracleVerdicts, okVerdicts);

        SequenceNode failingSequenceNode = new SequenceNode("sequence-id", 1, failingConcreteState, null, Collections.emptySet());
        SequenceNode okSequenceNode = new SequenceNode("sequence-id", 2, okConcreteState, null, Collections.emptySet());

        orientDBManager.persistSequenceNode(failingSequenceNode);
        orientDBManager.persistSequenceNode(okSequenceNode);

        Map<String, Object> persistedConcreteState = persistedConcreteStates.get("model-identifier-concrete-state");

        assertNotNull(persistedConcreteState);
        assertFalse(okVerdicts.equals(persistedConcreteState.get("OracleVerdicts")));
        assertEquals(failVerdicts, persistedConcreteState.get("OracleVerdicts"));
    }

    private void capturePersistedConcreteState(DocumentEntity documentEntity) {
        if (!(documentEntity instanceof EdgeEntity)) {
            return;
        }

        EdgeEntity edgeEntity = (EdgeEntity) documentEntity;
        if (!"Accessed".equals(edgeEntity.getEntityClass().getClassName())) {
            return;
        }

        VertexEntity targetEntity = edgeEntity.getTargetEntity();
        if (!"ConcreteState".equals(targetEntity.getEntityClass().getClassName())) {
            return;
        }

        String concreteStateKey = (String) targetEntity.getPropertyValue("widgetId").getValue();
        if (persistedConcreteStates.containsKey(concreteStateKey) && !targetEntity.updateEnabled()) {
            return;
        }

        Map<String, Object> persistedProperties = new HashMap<>();
        for (String propertyName : targetEntity.getPropertyNames()) {
            PropertyValue propertyValue = targetEntity.getPropertyValue(propertyName);
            persistedProperties.put(propertyName, propertyValue == null ? null : propertyValue.getValue());
        }
        persistedConcreteStates.put(concreteStateKey, persistedProperties);
    }
}

package org.testar.statemodel.persistence.orientdb;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.ConcreteAction;
import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.ConcreteStateTransition;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.export.ModelExportOptions;
import org.testar.statemodel.persistence.orientdb.entity.Config;
import org.testar.statemodel.persistence.orientdb.entity.DocumentEntity;
import org.testar.statemodel.persistence.orientdb.entity.EntityClass;
import org.testar.statemodel.persistence.orientdb.entity.EntityClassFactory;
import org.testar.statemodel.persistence.orientdb.entity.EntityManager;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.util.EventHelper;
import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.analysis.export.StaticGraphExporter;
import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class OrientDBManagerIntegrationTest {

    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    private Config config;
    private EntityManager entityManager;
    private OrientDBManager persistence;

    @Before
    public void createDatabase() throws Exception {
        Path storage = temporary.newFolder("database").toPath();
        OrientDBConfig options = OrientDBConfig.builder().addConfig(OGlobalConfiguration.DISK_CACHE_SIZE, 16).build();
        try (OrientDB database = new OrientDB("plocal:" + storage, options)) {
            database.execute("create database testar plocal users (admin identified by 'admin' role admin)").close();
        }
        config = new Config();
        config.setConnectionType("plocal");
        config.setDatabaseDirectory(storage.toString());
        config.setDatabase("testar");
        config.setUser("admin");
        config.setPassword("admin");
        entityManager = new EntityManager(config);
        persistence = new OrientDBManager(new EventHelper(), entityManager);
    }

    @After
    public void closeDatabase() {
        if (persistence != null) {
            persistence.shutdown();
            persistence = null;
        }
    }

    @Test
    public void repeatedObservationsRetainEveryConcreteActionAfterReopeningForExport() throws Exception {
        AbstractStateModel model = new AbstractStateModel("model-1", "Example", "1", Collections.emptySet(), persistence);
        AbstractAction observation = new AbstractAction("AA_OBSERVATION");
        AbstractState abstractState = new AbstractState("SA1", Set.of(observation));
        ConcreteState[] states = {
                new ConcreteState("SC1", abstractState), new ConcreteState("SC2", abstractState),
                new ConcreteState("SC3", abstractState), new ConcreteState("SC4", abstractState)
        };
        Set<String> expectedIds = new HashSet<>();

        for (int index = 0; index < states.length - 1; index++) {
            String actionId = "AC_OBSERVATION_" + states[index].getId() + "_" + states[index + 1].getId();
            expectedIds.add(actionId);
            observation.addConcreteActionId(actionId);
            model.addTransition(abstractState, abstractState, observation);
            persistence.persistConcreteStateTransition(new ConcreteStateTransition(
                    states[index], states[index + 1], new ConcreteAction(actionId, observation)));
        }

        EntityClass actionClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractAction);
        Set<DocumentEntity> storedActions = entityManager.retrieveAllOfClass(actionClass, Collections.emptyMap());
        assertEquals(1, storedActions.size());
        assertEquals(expectedIds, storedActions.iterator().next().getPropertyValue("concreteActionIds").getValue());
        closeDatabase();

        Path graphs = temporary.newFolder("graphs").toPath();
        AnalysisManager analysis = new AnalysisManager(config, graphs.toString());
        try {
            for (String format : new String[] {"abstract", "hybrid"}) {
                JsonNode snapshot = analysis.fetchExportSnapshot("model-1", new ModelExportOptions(format, false, false));
                Set<String> concreteIds = new HashSet<>();
                Set<String> abstractConcreteIds = new HashSet<>();
                int abstractEdges = 0;
                for (JsonNode element : snapshot.path("elements")) {
                    JsonNode data = element.path("data");
                    if ("AA_OBSERVATION".equals(data.path("actionId").asText())) {
                        abstractEdges++;
                        String ids = data.path("concreteActionIds").asText();
                        abstractConcreteIds.addAll(Arrays.asList(ids.substring(1, ids.length() - 1).split(",\\s*")));
                    } else if (expectedIds.contains(data.path("actionId").asText())) {
                        concreteIds.add(data.path("actionId").asText());
                    }
                }
                assertEquals(1, abstractEdges);
                assertEquals(expectedIds, abstractConcreteIds);
                assertEquals(expectedIds, concreteIds);
            }
        } finally {
            analysis.shutdown();
        }
    }

    @Test
    public void finalVerdictsSurviveReopeningAndStaticExportWithoutChangingConcreteStates() throws Exception {
        AbstractStateModel model = new AbstractStateModel("model-1", "Example", "1", Collections.emptySet(), persistence);
        AbstractState abstractState = new AbstractState("SA1", Set.of());
        model.addState(abstractState);
        ConcreteState state = new ConcreteState("SC1", abstractState);
        persistence.persistConcreteState(state);
        List<StateModelEvent> events = new ArrayList<>();
        StateModelEventListener listener = mock(StateModelEventListener.class);
        doAnswer(invocation -> {
            events.add(invocation.getArgument(0));
            return null;
        }).when(listener).eventReceived(any());
        SequenceManager sequences = new SequenceManager(Set.of(persistence, listener), "model-1");
        sequences.startNewSequence();
        sequences.notifyStateReached(state, null);
        ConcreteAction action = new ConcreteAction("AC1", new AbstractAction("AA1"));
        action.addAttribute(Tags.Desc, "Click again");
        sequences.notifyStateReached(state, action);
        sequences.stopSequence(List.of(new Verdict(Verdict.Severity.LLM_COMPLETE, "Goal achieved: caf\u00e9 \"mode\"."),
                new Verdict(Verdict.Severity.WARNING, "Additional observation.")));
        Sequence first = (Sequence) events.get(events.size() - 1).getPayload();

        closeDatabase();
        try (OrientDB database = new OrientDB("plocal:" + config.getDatabaseDirectory(), OrientDBConfig.defaultConfig());
                ODatabaseSession session = database.open(config.getDatabase(), config.getUser(), config.getPassword())) {
            // An existing datastore can lack the newly declared optional properties.
            session.getClass("TestSequence").dropProperty("finalVerdicts");
            session.getClass("TestSequence").dropProperty("finalStateOccurrenceId");
        }
        entityManager = new EntityManager(config);
        persistence = new OrientDBManager(new EventHelper(), entityManager);
        sequences = new SequenceManager(Set.of(persistence, listener), "model-1");
        sequences.startNewSequence();
        sequences.notifyStateReached(state, null);
        sequences.stopSequence(List.of(new Verdict(Verdict.Severity.LLM_INVALID, "Another goal failed.")));
        Sequence second = (Sequence) events.get(events.size() - 1).getPayload();
        closeDatabase();

        TaggableBase settings = new TaggableBase();
        settings.set(StateModelTags.StateModelInference, true);
        settings.set(StateModelTags.DataStore, "OrientDB");
        settings.set(StateModelTags.DataStoreMode, "instant");
        settings.set(StateModelTags.DataStoreType, "plocal");
        settings.set(StateModelTags.DataStoreDirectory, config.getDatabaseDirectory());
        settings.set(StateModelTags.DataStoreDB, "testar");
        settings.set(StateModelTags.DataStoreUser, "admin");
        settings.set(StateModelTags.DataStorePassword, "admin");
        Path output = temporary.newFolder("output", "webdriver_generic").toPath();
        Path run = Files.createDirectory(output.resolve("run_cli"));
        settings.set(ConfigTags.OutputDir, output.toString());
        Path viewer = new StaticGraphExporter(settings, "model-1", () -> run).export();
        assertNotNull(viewer);
        JsonNode staticElements = new ObjectMapper().readTree(viewer.getParent().resolve("model/elements.json").toFile());
        Path graphs = temporary.newFolder("graphs").toPath();
        AnalysisManager analysis = new AnalysisManager(config, graphs.toString());
        try {
            JsonNode liveElements = analysis.fetchExportSnapshot("model-1", new ModelExportOptions("traces", false, false)).path("elements");
            for (JsonNode elements : List.of(staticElements, liveElements)) {
                Map<String, JsonNode> sequencesById = new HashMap<>();
                Map<String, JsonNode> occurrencesById = new HashMap<>();
                for (JsonNode element : elements) {
                    JsonNode data = element.path("data");
                    if (data.has("finalVerdicts")) {
                        assertTrue(data.path("finalVerdicts").isArray());
                        sequencesById.put(data.path("sequenceId").asText(), data);
                    } else if (data.has("nodeId")) {
                        occurrencesById.put(data.path("nodeId").asText(), data);
                    }
                    if (data.has("stateId")) {
                        assertFalse(data.has("finalVerdicts"));
                        if ("SC1".equals(data.path("stateId").asText())) {
                            assertEquals("1", data.path("oracleVerdictCode").asText());
                        }
                    }
                }
                assertEquals(2, sequencesById.size());
                for (Sequence sequence : List.of(first, second)) {
                    JsonNode stored = sequencesById.get(sequence.getCurrentSequenceId());
                    assertEquals(sequence.getFinalStateOccurrenceId(), stored.path("finalStateOccurrenceId").asText());
                    assertEquals("SC1", occurrencesById.get(sequence.getFinalStateOccurrenceId()).path("concreteStateId").asText());
                    assertEquals(sequence.getFinalVerdicts().size(), stored.path("finalVerdicts").size());
                    for (int index = 0; index < sequence.getFinalVerdicts().size(); index++) {
                        Verdict expected = sequence.getFinalVerdicts().get(index);
                        JsonNode result = stored.path("finalVerdicts").get(index);
                        assertEquals(expected.verdictSeverityTitle(), result.path("Severity").asText());
                        assertEquals(expected.severity(), result.path("SeverityValue").asDouble(), 0.0);
                        assertEquals(expected.info(), result.path("Info").asText());
                    }
                }
            }
        } finally {
            analysis.shutdown();
        }
    }

    @Test
    public void edgeWithUpdatesDisabledPreservesStoredProperties() throws Exception {
        AbstractStateModel model = new AbstractStateModel("model-1", "Example", "1", Collections.emptySet(), persistence);
        AbstractAction action = new AbstractAction("AA_CLICK");
        action.addConcreteActionId("AC1");
        AbstractState source = new AbstractState("SA1", Set.of(action));
        AbstractState target = new AbstractState("SA2", Collections.emptySet());
        model.addTransition(source, target, action);

        EntityClass actionClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractAction);
        DocumentEntity stored = entityManager.retrieveAllOfClass(actionClass, Collections.emptyMap()).iterator().next();
        stored.enableUpdate(false);
        stored.addPropertyValue("concreteActionIds", new PropertyValue(OType.EMBEDDEDSET, Set.of("AC2")));
        entityManager.saveEntity(stored);

        Set<DocumentEntity> storedActions = entityManager.retrieveAllOfClass(actionClass, Collections.emptyMap());
        assertEquals(1, storedActions.size());
        assertEquals(Set.of("AC1"), storedActions.iterator().next().getPropertyValue("concreteActionIds").getValue());
    }
}

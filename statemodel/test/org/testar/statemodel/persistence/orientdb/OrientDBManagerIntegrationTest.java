package org.testar.statemodel.persistence.orientdb;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
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

import static org.junit.Assert.assertEquals;

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

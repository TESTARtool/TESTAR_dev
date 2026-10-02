package org.testar.statemodel.analysis.export;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.core.tag.TaggableBase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StaticGraphExporterIntegrationTest {

    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void exportsPersistedOrientDbGraphAndReleasesTheDatabase() throws Exception {
        Path storage = temporary.newFolder("database").toPath();
        Path output = temporary.newFolder("output", "webdriver_generic").toPath();
        Path run = Files.createDirectory(output.resolve("run_generate_example"));
        OrientDBConfig databaseOptions = OrientDBConfig.builder().addConfig(OGlobalConfiguration.DISK_CACHE_SIZE, 16).build();
        try (OrientDB database = new OrientDB("plocal:" + storage, databaseOptions)) {
            database.execute("create database testar plocal users (admin identified by 'admin' role admin)").close();
            try (ODatabaseSession session = database.open("testar", "admin", "admin")) {
                for (String name : new String[] {"AbstractState", "ConcreteState", "TestSequence", "SequenceNode", "BlackHole"}) {
                    session.createVertexClass(name);
                }
                for (String name : new String[] {"AbstractAction", "ConcreteAction", "SequenceStep", "FirstNode",
                        "isAbstractedBy", "Accessed", "UnvisitedAbstractAction"}) {
                    session.createEdgeClass(name);
                }
                OVertex abstractState = session.newVertex("AbstractState");
                abstractState.setProperty("modelIdentifier", "model-1");
                abstractState.setProperty("stateId", "abstract-1");
                abstractState.setProperty("isInitial", true);
                session.save(abstractState);
                ORecordBytes screenshot = new ORecordBytes(Base64.getDecoder().decode(
                        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+j4JcAAAAASUVORK5CYII="));
                session.save(screenshot);
                OVertex concreteState = session.newVertex("ConcreteState");
                concreteState.setProperty("stateId", "concrete-1");
                concreteState.setProperty("title", "caf\u00e9 \u4e2d\u6587");
                concreteState.setProperty("screenshot", screenshot);
                session.save(concreteState);
                session.save(concreteState.addEdge(abstractState, "isAbstractedBy"));
                OEdge concreteAction = concreteState.addEdge(concreteState, "ConcreteAction");
                concreteAction.setProperty("screenshot", screenshot);
                concreteAction.setProperty("actionDescription", "click");
                session.save(concreteAction);
                session.save(abstractState.addEdge(abstractState, "AbstractAction"));
                OVertex sequence = session.newVertex("TestSequence");
                sequence.setProperty("modelIdentifier", "model-1");
                sequence.setProperty("sequenceId", "sequence-1");
                session.save(sequence);
                OVertex firstNode = session.newVertex("SequenceNode");
                OVertex secondNode = session.newVertex("SequenceNode");
                session.save(firstNode);
                session.save(secondNode);
                session.save(sequence.addEdge(firstNode, "FirstNode"));
                session.save(firstNode.addEdge(concreteState, "Accessed"));
                session.save(secondNode.addEdge(concreteState, "Accessed"));
                session.save(firstNode.addEdge(secondNode, "SequenceStep"));
            }
        }

        TaggableBase settings = new TaggableBase();
        settings.set(StateModelTags.StateModelInference, true);
        settings.set(StateModelTags.DataStore, "OrientDB");
        settings.set(StateModelTags.DataStoreMode, "instant");
        settings.set(StateModelTags.DataStoreType, "plocal");
        settings.set(StateModelTags.DataStoreDirectory, storage.toString());
        settings.set(StateModelTags.DataStoreDB, "testar");
        settings.set(StateModelTags.DataStoreUser, "admin");
        settings.set(StateModelTags.DataStorePassword, "admin");
        settings.set(ConfigTags.OutputDir, output.toString());

        Path viewer = new StaticGraphExporter(settings, "model-1", () -> run).export();

        assertNotNull(viewer);
        JsonNode graph = new ObjectMapper().readTree(viewer.getParent().resolve("model/elements.json").toFile());
        assertTrue(graph.toString().contains("AbstractState"));
        assertTrue(graph.toString().contains("ConcreteState"));
        assertTrue(graph.toString().contains("ConcreteAction"));
        assertTrue(graph.toString().contains("TestSequence"));
        assertTrue(graph.toString().contains("SequenceStep"));
        assertTrue(graph.toString().contains("isAbstractedBy"));
        assertTrue(graph.toString().contains("hasScreenshot"));
        assertTrue(graph.toString().contains("caf\u00e9 \u4e2d\u6587"));
        assertTrue(Files.readString(viewer.getParent().resolve("model/images.js")).contains("data:image/png;base64,"));
        try (OrientDB reopened = new OrientDB("plocal:" + storage, databaseOptions)) {
            assertTrue(reopened.exists("testar"));
            reopened.drop("testar");
        }
    }
}

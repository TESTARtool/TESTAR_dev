package org.testar.statemodel.analysis.export;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;

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
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.persistence.orientdb.entity.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

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
                session.createVertexClass("Widget");
                session.getMetadata().getSchema().createClass("ConcreteState", session.getMetadata().getSchema().getClass("Widget"));
                for (String name : new String[] {"AbstractState", "TestSequence", "SequenceNode", "BlackHole"}) {
                    session.createVertexClass(name);
                }
                for (String name : new String[] {"AbstractAction", "ConcreteAction", "SequenceStep", "FirstNode",
                        "isAbstractedBy", "isChildOf", "Accessed", "UnvisitedAbstractAction"}) {
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
                concreteState.setProperty("AbstractID", "abstract-1");
                concreteState.setProperty("ConcreteID", "concrete-1");
                concreteState.setProperty("title", "caf\u00e9 \u4e2d\u6587");
                concreteState.setProperty("screenshot", screenshot);
                session.save(concreteState);
                OVertex child = session.newVertex("Widget");
                child.setProperty("AbstractID", "widget-abstract-1");
                child.setProperty("ConcreteID", "widget-concrete-1");
                child.setProperty("WebName", "Click me");
                session.save(child);
                session.save(child.addEdge(concreteState, "isChildOf"));
                session.save(concreteState.addEdge(abstractState, "isAbstractedBy"));
                OEdge concreteAction = concreteState.addEdge(concreteState, "ConcreteAction");
                concreteAction.setProperty("screenshot", screenshot);
                concreteAction.setProperty("actionDescription", "click");
                concreteAction.setProperty("actionId", "action-concrete-1");
                concreteAction.setProperty("uid", "loop-concrete-1");
                concreteAction.setProperty("AbstractID", "widget-abstract-1");
                concreteAction.setProperty("ConcreteID", "widget-concrete-1");
                concreteAction.setProperty("InputText", "Saab \u4e2d\u6587");
                concreteAction.setProperty("binding", "retained action property");
                session.save(concreteAction);
                OEdge abstractAction = abstractState.addEdge(abstractState, "AbstractAction");
                abstractAction.setProperty("actionId", "action-abstract-1");
                abstractAction.setProperty("concreteActionIds", Set.of("action-concrete-1"));
                session.save(abstractAction);
                OVertex sequence = session.newVertex("TestSequence");
                sequence.setProperty("modelIdentifier", "model-1");
                sequence.setProperty("sequenceId", "sequence-1");
                sequence.setProperty("startDateTime", new Date(1000));
                sequence.setProperty("verdict", "OK");
                session.save(sequence);
                OVertex firstNode = session.newVertex("SequenceNode");
                OVertex secondNode = session.newVertex("SequenceNode");
                OVertex thirdNode = session.newVertex("SequenceNode");
                OVertex unresolvedNode = session.newVertex("SequenceNode");
                int nodeNr = 0;
                for (OVertex node : new OVertex[] {firstNode, secondNode, thirdNode, unresolvedNode}) {
                    node.setProperty("sequenceId", "sequence-1");
                    node.setProperty("nodeId", "occurrence-" + nodeNr);
                    node.setProperty("nodeNr", nodeNr);
                    node.setProperty("timestamp", new Date(1000L + nodeNr * 1000L));
                    node.setProperty("concreteStateId", node == unresolvedNode ? "missing-state" : "concrete-1");
                    session.save(node);
                    nodeNr++;
                }
                session.save(sequence.addEdge(firstNode, "FirstNode"));
                session.save(firstNode.addEdge(concreteState, "Accessed"));
                session.save(secondNode.addEdge(concreteState, "Accessed"));
                session.save(thirdNode.addEdge(concreteState, "Accessed"));
                OVertex[] occurrences = {firstNode, secondNode, thirdNode, unresolvedNode};
                for (int index = 0; index < occurrences.length - 1; index++) {
                    OEdge step = occurrences[index].addEdge(occurrences[index + 1], "SequenceStep");
                    step.setProperty("stepId", "step-" + index);
                    step.setProperty("timestamp", new Date(2000L + index * 1000L));
                    step.setProperty("concreteActionId", "action-concrete-1");
                    step.setProperty("concreteActionUid", "loop-concrete-1");
                    step.setProperty("actionDescription", "Repeated click");
                    session.save(step);
                }
            }
        }

        TaggableBase settings = new TaggableBase();
        settings.set(StateModelTags.StateModelInference, true);
        settings.set(StateModelTags.StateModelExportStaticGraphIncludeWidgetTrees, true);
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
        assertTrue(graph.toString().contains("caf\u00e9 \u4e2d\u6587"));
        assertTrue(graph.toString().contains("Saab \u4e2d\u6587"));
        assertTrue(graph.toString().contains("retained action property"));
        JsonNode trees = new ObjectMapper().readTree(viewer.getParent().resolve("model/widget-trees.json").toFile());
        assertTrue(trees.toString().contains("Click me"));
        assertTrue(trees.toString().contains("isChildOf"));
        assertTrue(Files.readString(viewer.getParent().resolve("model/widget-trees.js")).startsWith("window.__TESTAR_WIDGET_TREES__"));
        assertTrue(Files.readString(viewer.getParent().resolve("model/images.js")).contains("data:image/png;base64,"));
        Config config = new Config();
        config.setConnectionType("plocal");
        config.setDatabaseDirectory(storage.toString());
        config.setDatabase("testar");
        config.setUser("admin");
        config.setPassword("admin");
        Path liveGraphs = temporary.newFolder("live-graphs").toPath();
        AnalysisManager analysis = new AnalysisManager(config, liveGraphs.toString());
        try {
            JsonNode inventory = analysis.fetchExportSnapshot("model-1", new ModelExportOptions("inventory", false, false));
            assertTrue(inventory.path("propertyInventory").path("widgets").toString().contains("WebName"));
            assertFalse(inventory.has("elements"));
            assertFalse(inventory.has("widgetTrees"));
            assertFalse(inventory.has("images"));
            assertTrue(inventory.path("propertyInventory").path("sequences").toString().contains("verdict"));
            assertTrue(inventory.path("propertyInventory").path("transitions").toString().contains("actionDescription"));
            assertFalse(inventory.path("propertyInventory").path("sequences").toString().contains("timestamp"));
            JsonNode excluded = analysis.fetchExportSnapshot("model-1", new ModelExportOptions("hybrid", false, false));
            assertFalse(excluded.has("widgetTrees"));
            assertTrue(excluded.path("images").isEmpty());
            JsonNode representative = analysis.fetchExportSnapshot("model-1", new ModelExportOptions("abstract", true, false));
            assertTrue(representative.path("widgetTrees").toString().contains("Click me"));
            JsonNode concrete = analysis.fetchExportSnapshot("model-1", new ModelExportOptions("concrete", false, false));
            assertFalse(concrete.has("widgetTrees"));
            assertTrue(concrete.path("images").isEmpty());
            ArrayList<ModelExportProgress> progress = new ArrayList<>();
            JsonNode traces = analysis.fetchExportSnapshot("model-1", new ModelExportOptions("traces", true, true), progress::add);
            assertTrue(progress.contains(new ModelExportProgress("trees", 0, 1)));
            assertTrue(progress.contains(new ModelExportProgress("trees", 1, 1)));
            assertTrue(progress.contains(new ModelExportProgress("screenshots", 0, 2)));
            assertTrue(progress.contains(new ModelExportProgress("screenshots", 2, 2)));
            int occurrences = 0;
            int steps = 0;
            for (JsonNode element : traces.path("elements")) {
                if (element.path("classes").toString().contains("SequenceNode")) {
                    occurrences++;
                    assertEquals("sequence-1", element.path("data").path("sequenceId").asText());
                    assertFalse(element.path("data").path("timestamp").asText().isEmpty());
                }
                if (element.path("classes").toString().contains("SequenceStep")) {
                    steps++;
                    assertEquals("loop-concrete-1", element.path("data").path("concreteActionUid").asText());
                }
            }
            assertEquals(4, occurrences);
            assertEquals(3, steps);
            assertTrue(traces.path("elements").toString().contains("missing-state"));
            assertEquals(1, traces.path("widgetTrees").size());
            assertEquals(2, traces.path("images").size());
            try (Stream<Path> files = Files.walk(liveGraphs)) {
                assertFalse(files.anyMatch(path -> path.toString().endsWith(".png")));
            }
            try {
                analysis.fetchExportSnapshot("model-1", new ModelExportOptions("traces", true, true), update -> {
                    if (update.phase().equals("trees") && update.completed() > 0) {
                        throw new IllegalStateException("Browser connection closed");
                    }
                });
                org.junit.Assert.fail("A disconnected export must release the datastore instead of continuing");
            } catch (IllegalStateException expected) {
                assertEquals("Browser connection closed", expected.getMessage());
            }
        } finally {
            analysis.shutdown();
        }
        Path lightweightRun = Files.createDirectory(output.resolve("run_lightweight"));
        settings.set(StateModelTags.StateModelExportStaticGraphIncludeWidgetTrees, false);
        Path lightweightViewer = new StaticGraphExporter(settings, "model-1", () -> lightweightRun).export();
        assertNotNull(lightweightViewer);
        assertFalse(Files.exists(lightweightViewer.getParent().resolve("model/widget-trees.json")));
        JsonNode lightweightMetadata = new ObjectMapper().readTree(lightweightViewer.getParent().resolve("run.json").toFile());
        assertFalse(lightweightMetadata.path("widgetTreesCaptured").asBoolean());
        assertTrue(Files.readString(lightweightViewer.getParent().resolve("model/elements.json")).contains("SequenceStep"));
        try (OrientDB reopened = new OrientDB("plocal:" + storage, databaseOptions)) {
            assertTrue(reopened.exists("testar"));
            reopened.drop("testar");
        }
    }
}

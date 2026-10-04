package org.testar.statemodel.analysis.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.jsonformat.Element;
import org.testar.statemodel.analysis.jsonformat.Vertex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyList;

public class ModelExportSnapshotTest {

    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void packagesAllLayersTreesAndAvailableScreenshotsWithoutDatabaseConfiguration() throws Exception {
        Path root = temporary.newFolder("graphs").toPath();
        Path model = Files.createDirectory(root.resolve("model-1"));
        Files.writeString(model.resolve("graph.json"), "["
                + "{\"data\":{\"id\":\"n2\",\"stateId\":\"SC2\"},\"classes\":\"ConcreteState\"},"
                + "{\"data\":{\"id\":\"n1\",\"stateId\":\"SC1\"},\"classes\":[\"ConcreteState\"]},"
                + "{\"data\":{\"id\":\"e1\"},\"classes\":[\"ConcreteAction\"]}]");
        Files.write(model.resolve("n1.png"), new byte[] {1, 2, 3});
        Files.write(model.resolve("e1.png"), new byte[] {4, 5, 6});
        AnalysisManager analysis = mock(AnalysisManager.class);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false, false)).thenReturn("graph.json");
        when(analysis.fetchExportImages(List.of("e1", "n1", "n2"))).thenReturn(
                Map.of("n1", "data:image/png;base64,AQID", "e1", "data:image/png;base64,BAUG"));
        Vertex widget = new Vertex("n1");
        widget.addProperty("WebName", "Example");
        when(analysis.fetchWidgetTrees(List.of("n1", "n2"))).thenReturn(
                Map.of("n1", List.of(new Element(Element.GROUP_NODES, widget, "Widget")), "n2", List.of()));

        JsonNode snapshot = ModelExportSnapshot.read(analysis, root, "model-1", new ModelExportOptions("hybrid", true, true));

        assertEquals("model-1", snapshot.path("metadata").path("modelIdentifier").asText());
        assertEquals("accumulated", snapshot.path("metadata").path("modelScope").asText());
        assertEquals(3, snapshot.path("elements").size());
        assertEquals("Example", snapshot.path("widgetTrees").path("n1").get(0).path("data").path("WebName").asText());
        assertTrue(snapshot.path("widgetTrees").path("n2").isEmpty());
        assertEquals("data:image/png;base64,AQID", snapshot.path("images").path("n1").asText());
        assertEquals("data:image/png;base64,BAUG", snapshot.path("images").path("e1").asText());
        assertFalse(snapshot.path("images").has("n2"));
        assertFalse(snapshot.has("databaseConfig"));
        verify(analysis).fetchWidgetTrees(List.of("n1", "n2"));
        verify(analysis).fetchExportImages(List.of("e1", "n1", "n2"));
    }

    @Test
    public void excludesTreeQueriesAndImageBytesWhilePreservingEveryGraphLayer() throws Exception {
        Path root = temporary.newFolder("graphs").toPath();
        Path model = Files.createDirectory(root.resolve("model-1"));
        String graph = "[{\"data\":{\"id\":\"n1\"},\"classes\":\"ConcreteState\"},"
                + "{\"data\":{\"id\":\"sequence1\"},\"classes\":\"TestSequence\"}]";
        Files.writeString(model.resolve("graph.json"), graph);
        Files.writeString(model.resolve("n1.png"), "not an image");
        AnalysisManager analysis = mock(AnalysisManager.class);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false, false)).thenReturn("graph.json");

        JsonNode snapshot = ModelExportSnapshot.read(analysis, root, "model-1", new ModelExportOptions("snapshot", false, false));

        assertEquals(new ObjectMapper().readTree(graph), snapshot.path("elements"));
        assertFalse(snapshot.has("widgetTrees"));
        assertTrue(snapshot.path("images").isEmpty());
        assertFalse(snapshot.path("metadata").path("widgetTreesCaptured").asBoolean());
        assertFalse(snapshot.path("metadata").path("screenshotsCaptured").asBoolean());
        verify(analysis, never()).fetchWidgetTrees(anyList());
        verify(analysis, never()).fetchExportImages(anyList());
        verify(analysis).fetchGraphForModel("model-1", true, true, true, false, false);
    }

    @Test
    public void abstractFormatLoadsOnlyDeterministicRepresentativeTrees() throws Exception {
        Path root = temporary.newFolder("graphs").toPath();
        Path model = Files.createDirectory(root.resolve("model-1"));
        Files.writeString(model.resolve("graph.json"), "["
                + "{\"data\":{\"id\":\"n2\",\"AbstractID\":\"SA1\"},\"classes\":\"ConcreteState\"},"
                + "{\"data\":{\"id\":\"n1\",\"AbstractID\":\"SA1\"},\"classes\":\"ConcreteState\"},"
                + "{\"data\":{\"id\":\"n3\",\"AbstractID\":\"SA2\"},\"classes\":\"ConcreteState\"}]");
        AnalysisManager analysis = mock(AnalysisManager.class);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false, false)).thenReturn("graph.json");
        when(analysis.fetchWidgetTrees(List.of("n1", "n3"))).thenReturn(Map.of("n1", List.of(), "n3", List.of()));

        JsonNode snapshot = ModelExportSnapshot.read(analysis, root, "model-1", new ModelExportOptions("abstract", true, false));

        verify(analysis).fetchWidgetTrees(List.of("n1", "n3"));
        assertEquals(3, snapshot.path("elements").size());
        assertFalse(snapshot.path("widgetTrees").has("n2"));
    }

    @Test
    public void abstractScreenshotsMatchRepresentativesByActionAndStateEndpoints() throws Exception {
        Path root = temporary.newFolder("graphs").toPath();
        Path model = Files.createDirectory(root.resolve("model-1"));
        Files.writeString(model.resolve("graph.json"), "["
                + "{\"classes\":\"AbstractState\",\"data\":{\"id\":\"as1\",\"stateId\":\"SA1\"}},"
                + "{\"classes\":\"AbstractState\",\"data\":{\"id\":\"as2\",\"stateId\":\"SA2\"}},"
                + "{\"classes\":\"ConcreteState\",\"data\":{\"id\":\"n1\",\"AbstractID\":\"SA1\"}},"
                + "{\"classes\":\"ConcreteState\",\"data\":{\"id\":\"n2\",\"AbstractID\":\"SA1\"}},"
                + "{\"classes\":\"ConcreteState\",\"data\":{\"id\":\"n3\",\"AbstractID\":\"SA2\"}},"
                + "{\"classes\":\"AbstractAction\",\"data\":{\"id\":\"aa1\",\"source\":\"as1\",\"target\":\"as2\","
                + "\"actionId\":\"AAclick\",\"concreteActionIds\":\"[AC1, AC10]\"}},"
                + "{\"classes\":\"AbstractAction\",\"data\":{\"id\":\"aa2\",\"source\":\"as2\",\"target\":\"as1\","
                + "\"actionId\":\"AAback\",\"concreteActionIds\":[\"AC1\"]}},"
                + "{\"classes\":\"ConcreteAction\",\"data\":{\"id\":\"e2\",\"source\":\"n2\",\"target\":\"n3\",\"actionId\":\"AC10\"}},"
                + "{\"classes\":\"ConcreteAction\",\"data\":{\"id\":\"e3\",\"source\":\"n3\",\"target\":\"n1\",\"actionId\":\"AC1\"}},"
                + "{\"classes\":\"ConcreteAction\",\"data\":{\"id\":\"e1\",\"source\":\"n1\",\"target\":\"n3\",\"actionId\":\"AC1\"}}]");
        AnalysisManager analysis = mock(AnalysisManager.class);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false, false)).thenReturn("graph.json");
        when(analysis.fetchExportImages(List.of("e1", "e3", "n1", "n3"))).thenReturn(Map.of());

        ModelExportSnapshot.read(analysis, root, "model-1", new ModelExportOptions("abstract", false, true));

        verify(analysis).fetchExportImages(List.of("e1", "e3", "n1", "n3"));
        verify(analysis, never()).fetchWidgetTrees(anyList());
    }

    @Test
    public void inventoryReadsPropertyNamesWithoutTreesOrScreenshots() throws Exception {
        Path root = temporary.newFolder("graphs").toPath();
        Path model = Files.createDirectory(root.resolve("model-1"));
        Files.writeString(model.resolve("graph.json"), "[{\"data\":{\"id\":\"n1\"},\"classes\":\"ConcreteState\"}]");
        AnalysisManager analysis = mock(AnalysisManager.class);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false, false)).thenReturn("graph.json");
        when(analysis.fetchWidgetPropertyNames("model-1")).thenReturn(List.of("WebName", "CustomTag"));

        JsonNode snapshot = ModelExportSnapshot.read(analysis, root, "model-1", new ModelExportOptions("inventory", false, false));

        assertEquals("CustomTag", snapshot.path("propertyInventory").path("widgets").get(0).asText());
        assertFalse(snapshot.has("elements"));
        assertFalse(snapshot.has("images"));
        assertFalse(snapshot.has("widgetTrees"));
        verify(analysis, never()).fetchWidgetTrees(anyList());
        verify(analysis, never()).fetchExportImages(anyList());
    }

    @Test
    public void concreteFormatRetrievesAllConcreteRecordsWithIndependentArtifactChoices() throws Exception {
        Path root = temporary.newFolder("graphs").toPath();
        Path model = Files.createDirectory(root.resolve("model-1"));
        Files.writeString(model.resolve("graph.json"), """
                [
                    {"classes":"ConcreteState","data":{"id":"cs1","stateId":"SC1"}},
                    {"classes":"ConcreteState","data":{"id":"cs2","stateId":"SC2"}},
                    {"classes":"ConcreteAction","data":{"id":"ca1","source":"cs1","target":"cs2","actionId":"AC1"}},
                    {"classes":"ConcreteAction","data":{"id":"ca2","source":"cs1","target":"cs1","actionId":"AC1"}}
                ]
                """);
        AnalysisManager analysis = mock(AnalysisManager.class);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false, false)).thenReturn("graph.json");
        when(analysis.fetchWidgetTrees(List.of("cs1", "cs2"))).thenReturn(Map.of());
        JsonNode snapshot = ModelExportSnapshot.read(analysis, root, "model-1", new ModelExportOptions("concrete", true, false));
        assertEquals(4, snapshot.path("elements").size());
        verify(analysis).fetchWidgetTrees(List.of("cs1", "cs2"));
        verify(analysis, never()).fetchExportImages(anyList());
    }

    @Test
    public void tracesRetrieveOnlyUnambiguouslyReferencedRecordsAndPreserveUnresolvedOccurrences() throws Exception {
        Path root = temporary.newFolder("graphs").toPath();
        Path model = Files.createDirectory(root.resolve("model-1"));
        Files.writeString(model.resolve("graph.json"), """
                [
                    {"classes":"ConcreteState","data":{"id":"cs1","stateId":"SC1"}},
                    {"classes":"ConcreteState","data":{"id":"cs2","stateId":"SC2"}},
                    {"classes":"ConcreteState","data":{"id":"cs3","stateId":"SC3"}},
                    {"classes":"ConcreteAction","data":{"id":"ca1","uid":"loop","source":"cs1","target":"cs1","actionId":"AC1"}},
                    {"classes":"ConcreteAction","data":{"id":"ca2","uid":"another","source":"cs1","target":"cs1","actionId":"AC10"}},
                    {"classes":"ConcreteAction","data":{"id":"ca3","uid":"unused","source":"cs2","target":"cs3","actionId":"AC1"}},
                    {"classes":"SequenceNode","data":{"id":"sn1","concreteStateId":"SC1"}},
                    {"classes":"SequenceNode","data":{"id":"sn2","concreteStateId":"SC1"}},
                    {"classes":"SequenceNode","data":{"id":"sn3","concreteStateId":"SC1"}},
                    {"classes":"SequenceNode","data":{"id":"sn4","concreteStateId":"SC2"}},
                    {"classes":"SequenceNode","data":{"id":"sn5","concreteStateId":"missing"}},
                    {"classes":"Accessed","data":{"id":"access1","source":"sn1","target":"cs1"}},
                    {"classes":"Accessed","data":{"id":"access2","source":"sn2","target":"cs1"}},
                    {"classes":"SequenceStep","data":{"id":"ss1","source":"sn1","target":"sn2","concreteActionId":"AC1","concreteActionUid":"loop"}},
                    {"classes":"SequenceStep","data":{"id":"ss2","source":"sn2","target":"sn3","concreteActionId":"AC1","concreteActionUid":"loop"}},
                    {"classes":"SequenceStep","data":{"id":"ss3","source":"sn3","target":"sn4","concreteActionId":"AC1","concreteActionUid":"unused"}},
                    {"classes":"SequenceStep","data":{"id":"ss4","source":"sn4","target":"sn5","concreteActionId":"AC1"}}
                ]
                """);
        AnalysisManager analysis = mock(AnalysisManager.class);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false, false)).thenReturn("graph.json");
        when(analysis.fetchWidgetTrees(List.of("cs1", "cs2"))).thenReturn(Map.of());
        when(analysis.fetchExportImages(List.of("ca1", "cs1", "cs2"))).thenReturn(Map.of());

        JsonNode snapshot = ModelExportSnapshot.read(analysis, root, "model-1", new ModelExportOptions("traces", true, true));

        verify(analysis).fetchWidgetTrees(List.of("cs1", "cs2"));
        verify(analysis).fetchExportImages(List.of("ca1", "cs1", "cs2"));
        assertEquals(4, java.util.stream.StreamSupport.stream(snapshot.path("elements").spliterator(), false)
                .filter(element -> element.path("classes").asText().equals("SequenceStep")).count());
        assertTrue(snapshot.path("elements").toString().contains("missing"));
    }

    @Test
    public void ambiguousTraceActionsDoNotRetrieveAnArbitraryScreenshot() throws Exception {
        Path root = temporary.newFolder("graphs").toPath();
        Path model = Files.createDirectory(root.resolve("model-1"));
        Files.writeString(model.resolve("graph.json"), """
                [
                    {"classes":"ConcreteState","data":{"id":"cs1","stateId":"SC1"}},
                    {"classes":"ConcreteAction","data":{"id":"ca1","uid":"first","source":"cs1","target":"cs1","actionId":"AC1"}},
                    {"classes":"ConcreteAction","data":{"id":"ca2","uid":"second","source":"cs1","target":"cs1","actionId":"AC1"}},
                    {"classes":"SequenceNode","data":{"id":"sn1","concreteStateId":"SC1"}},
                    {"classes":"SequenceNode","data":{"id":"sn2","concreteStateId":"SC1"}},
                    {"classes":"SequenceStep","data":{"id":"ss1","source":"sn1","target":"sn2","concreteActionId":"AC1"}}
                ]
                """);
        AnalysisManager analysis = mock(AnalysisManager.class);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false, false)).thenReturn("graph.json");
        when(analysis.fetchExportImages(List.of("cs1"))).thenReturn(Map.of());

        JsonNode snapshot = ModelExportSnapshot.read(analysis, root, "model-1", new ModelExportOptions("traces", false, true));

        verify(analysis).fetchExportImages(List.of("cs1"));
        verify(analysis, never()).fetchWidgetTrees(anyList());
        assertEquals(6, snapshot.path("elements").size());
    }

    @Test
    public void invalidModelAndGeneratedFilePathsAreRejected() throws Exception {
        AnalysisManager analysis = mock(AnalysisManager.class);
        Path root = temporary.newFolder("graphs").toPath();
        try {
            ModelExportSnapshot.read(analysis, root, "../outside", new ModelExportOptions("hybrid", true, true));
            fail("Unsafe model paths must be rejected");
        } catch (IOException exception) {
            assertEquals("Invalid model identifier.", exception.getMessage());
        }
        verifyNoInteractions(analysis);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false, false)).thenReturn("../outside.json");
        try {
            ModelExportSnapshot.read(analysis, root, "model-1", new ModelExportOptions("hybrid", true, true));
            fail("Unsafe graph paths must be rejected");
        } catch (IOException exception) {
            assertTrue(exception.getMessage().contains("valid graph file"));
        }
    }
}

package org.testar.statemodel.analysis.export;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.core.tag.TaggableBase;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.persistence.orientdb.entity.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;

public class StaticGraphExporterTest {

    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    private TaggableBase settings;
    private Path workspaceOutput;
    private Path run;
    private AnalysisManager analysis;

    @Before
    public void prepareRun() throws IOException {
        workspaceOutput = temporary.newFolder("output", "webdriver_generic").toPath().toAbsolutePath().normalize();
        run = Files.createDirectory(workspaceOutput.resolve("2026_generate_example"));
        settings = new TaggableBase();
        settings.set(StateModelTags.StateModelInference, true);
        settings.set(StateModelTags.DataStore, "OrientDB");
        settings.set(StateModelTags.DataStoreMode, "delayed");
        settings.set(StateModelTags.DataStoreType, "plocal");
        settings.set(StateModelTags.DataStoreDirectory, temporary.getRoot().toPath().resolve("datastore").toString());
        settings.set(StateModelTags.DataStorePassword, "secret-never-exported");
        settings.set(ConfigTags.OutputDir, workspaceOutput.toString());
        settings.set(ConfigTags.ApplicationName, "Example \"SUT\" caf\u00e9");
        settings.set(ConfigTags.ApplicationVersion, "1");
        analysis = mock(AnalysisManager.class);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false)).thenReturn("graph.json");
    }

    @Test
    public void packagesPortableViewerGraphImagesAndCurrentRunMetadata() throws Exception {
        Files.createDirectories(run.resolve("reports"));
        Files.writeString(run.resolve("reports/sequence_1_V001_SUSPICIOUS_TAG.html"), "report");
        StaticGraphExporter exporter = exporter(true);
        // The connection and application settings belong to the session, not later settings edits.
        settings.set(ConfigTags.ApplicationName, "changed later");
        settings.set(StateModelTags.DataStoreDirectory, "changed later");

        Path viewer = exporter.export();

        assertEquals(run.resolve("state-model/index.html"), viewer);
        Path snapshot = viewer.getParent();
        assertTrue(Files.readString(viewer).contains("model/elements.js"));
        assertTrue(Files.readString(viewer).contains("model/images.js"));
        assertTrue(Files.isRegularFile(snapshot.resolve("js/cytoscape.min.js")));
        assertTrue(Files.isRegularFile(snapshot.resolve("js/cola.min.js")));
        assertTrue(Files.isRegularFile(snapshot.resolve("js/cytoscape-cola.js")));
        assertTrue(Files.isRegularFile(snapshot.resolve("css/style.css")));
        assertEquals("[{\"group\":\"nodes\",\"data\":{\"id\":\"n1\"},\"classes\":\"ConcreteState\"}]",
                Files.readString(snapshot.resolve("model/elements.json")));
        assertTrue(Files.readString(snapshot.resolve("model/elements.js")).startsWith("window.__TESTAR_ELEMENTS__ = ["));
        assertTrue(Files.readString(snapshot.resolve("model/images.js")).contains("data:image/png;base64,"));
        assertTrue(Files.isRegularFile(snapshot.resolve("model/n1.png")));
        assertTrue(Files.isRegularFile(snapshot.resolve("model/e1.png")));
        JsonNode metadata = new ObjectMapper().readTree(snapshot.resolve("run.json").toFile());
        assertEquals("model-1", metadata.get("modelIdentifier").asText());
        assertEquals("Example \"SUT\" caf\u00e9", metadata.get("applicationName").asText());
        assertEquals("sequence_1_V001_SUSPICIOUS_TAG.html", metadata.get("reports").get(0).asText());
        assertTrue(metadata.get("modelScope").asText().contains("earlier runs"));
        assertFalse(Files.readString(snapshot.resolve("run.json")).contains("secret-never-exported"));
        assertTrue(Files.readString(workspaceOutput.resolve("state-models.html")).contains("2026_generate_example/state-model/index.html"));
        assertFalse(Files.exists(snapshot.resolve("graph")));
        verify(analysis, times(1)).shutdown();
        try (Stream<Path> directories = Files.list(run)) {
            assertFalse(directories.anyMatch(path -> path.getFileName().toString().startsWith(".state-model-")));
        }
    }

    @Test
    public void missingScreenshotsStillProduceAUsableViewer() throws Exception {
        Path viewer = exporter(false).export();

        assertNotNull(viewer);
        assertEquals("window.__TESTAR_IMAGES__ = {};\n", Files.readString(viewer.getParent().resolve("model/images.js")));
        String script = Files.readString(viewer.getParent().resolve("js/viewer.js"));
        assertTrue(script.contains("screenshot unavailable."));
        assertTrue(script.contains("ConcreteAction"));
        assertFalse(script.contains("runMultiQuery"));
    }

    @Test
    public void disabledInferenceAndNonPersistedModelsSkipAnalysis() {
        settings.set(StateModelTags.StateModelInference, false);
        assertNull(exporter(false).export());
        settings.set(StateModelTags.StateModelInference, true);
        settings.set(StateModelTags.DataStoreMode, "none");
        assertNull(exporter(false).export());
        verifyNoInteractions(analysis);
        assertFalse(Files.exists(run.resolve("state-model")));
    }

    @Test
    public void missingRunOrDifferentWorkspaceSkipsAnalysis() throws Exception {
        StaticGraphExporter missing = new StaticGraphExporter(settings, "model-1", () -> null, (config, directory) -> analysis);
        assertNull(missing.export());
        Path otherRun = temporary.newFolder("another-workspace", "run").toPath();
        StaticGraphExporter wrongWorkspace = new StaticGraphExporter(settings, "model-1", () -> otherRun,
                (config, directory) -> analysis);
        assertNull(wrongWorkspace.export());
        verifyNoInteractions(analysis);
        assertFalse(Files.exists(otherRun.resolve("state-model")));
    }

    @Test
    public void graphFailureClosesAnalysisAndRemovesPartialSnapshot() throws Exception {
        StaticGraphExporter exporter = exporter(false);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false)).thenThrow(new IllegalStateException("query failed"));

        String output = tapSystemOut(() -> assertNull(exporter.export()));

        verify(analysis).shutdown();
        assertTrue(output.contains(StaticGraphExporter.EXPORT_STARTED_SIGNAL));
        assertTrue(output.contains(StaticGraphExporter.EXPORT_FINISHED_SIGNAL));
        assertFalse(Files.exists(run.resolve("state-model")));
        assertFalse(Files.exists(workspaceOutput.resolve("state-models.html")));
        try (Stream<Path> files = Files.list(run)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    public void generatedFileMustExistAndExistingSnapshotIsPreserved() throws Exception {
        StaticGraphExporter exporter = exporter(false);
        when(analysis.fetchGraphForModel("model-1", true, true, true, false)).thenReturn("missing.json");
        assertNull(exporter.export());
        verify(analysis).shutdown();
        assertFalse(Files.exists(run.resolve("state-model")));

        Path viewer = Files.createDirectories(run.resolve("state-model")).resolve("index.html");
        Files.writeString(viewer, "previous snapshot");
        assertNull(exporter.export());
        assertEquals("previous snapshot", Files.readString(viewer));
        verify(analysis, times(1)).shutdown();
    }

    @Test
    public void emptyGraphDoesNotPublishASnapshot() throws Exception {
        StaticGraphExporter exporter = new StaticGraphExporter(settings, "model-1", () -> run, (config, directory) -> {
            try {
                Path model = Files.createDirectories(directory.resolve("model-1"));
                Files.writeString(model.resolve("graph.json"), "[]");
                return analysis;
            } catch (IOException exception) {
                throw new IllegalStateException(exception);
            }
        });

        assertNull(exporter.export());

        verify(analysis).shutdown();
        assertFalse(Files.exists(run.resolve("state-model")));
    }

    @Test
    public void workspaceIndexIncludesOnlyCompletedSnapshotsAndEscapesNames() throws Exception {
        Path completed = Files.createDirectories(workspaceOutput.resolve("run & example/state-model"));
        Files.writeString(completed.resolve("index.html"), "viewer");
        Files.writeString(completed.resolve("run.json"), "{}");
        Files.createDirectories(workspaceOutput.resolve("incomplete/state-model"));

        StaticGraphIndex.writeWorkspaceIndex(workspaceOutput);

        String index = Files.readString(workspaceOutput.resolve("state-models.html"));
        assertTrue(index.contains("run &amp; example"));
        assertTrue(index.contains("run%20&amp;%20example/state-model/index.html"));
        assertFalse(index.contains("incomplete"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void invalidModelIdentifierDoesNotResolveRunOrConnect() {
        Supplier<Path> directory = mock(Supplier.class);
        BiFunction<Config, Path, AnalysisManager> factory = mock(BiFunction.class);

        assertNull(new StaticGraphExporter(settings, "../outside", directory, factory).export());

        verifyNoInteractions(directory, factory);
    }

    private StaticGraphExporter exporter(boolean includeImages) {
        return new StaticGraphExporter(settings, "model-1", () -> run, (config, directory) -> {
            try {
                assertEquals(temporary.getRoot().toPath().resolve("datastore").toString(), config.getDatabaseDirectory());
                Path model = Files.createDirectories(directory.resolve("model-1"));
                Files.writeString(model.resolve("graph.json"),
                        "[{\"group\":\"nodes\",\"data\":{\"id\":\"n1\"},\"classes\":\"ConcreteState\"}]", StandardCharsets.UTF_8);
                if (includeImages) {
                    Files.write(model.resolve("n1.png"), new byte[] {1, 2, 3});
                    Files.write(model.resolve("e1.png"), new byte[] {4, 5, 6});
                }
                return analysis;
            } catch (IOException exception) {
                throw new IllegalStateException(exception);
            }
        });
    }
}

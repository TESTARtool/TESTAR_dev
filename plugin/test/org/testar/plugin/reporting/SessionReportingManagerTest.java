package org.testar.plugin.reporting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.OutputStructure;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Pair;
import org.testar.core.action.Type;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class SessionReportingManagerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testFinishWritesSequenceTraceJson() throws IOException {
        Settings settings = settings();
        SessionReportingManager manager = SessionReportingManager.start(
                settings,
                "https://example.org/login"
        );

        StateStub initialState = new StateStub();
        initialState.set(Tags.Title, "Login page");
        initialState.set(Tags.Desc, "Login");
        WidgetStub emailInput = new WidgetStub();
        emailInput.setParent(initialState);
        emailInput.setRoot(initialState);
        initialState.addChild(emailInput);
        emailInput.set(Tags.Title, "Email");
        emailInput.set(Tags.Desc, "Email field");

        Type action = new Type("secret");
        action.set(Tags.Desc, "Type password");
        action.set(Tags.InputText, "secret");
        action.mapOriginWidget(emailInput);

        manager.addState(initialState);
        manager.addSelectedAction(initialState, action);
        manager.finish();

        File reportsDirectory = new File(OutputStructure.htmlOutputDir);
        File[] jsonFiles = reportsDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        Assert.assertNotNull(jsonFiles);
        Assert.assertEquals(1, jsonFiles.length);

        String json = Files.readString(jsonFiles[0].toPath());
        Assert.assertTrue(json.contains("\"target\": \"https://example.org/login\""));
        Assert.assertTrue(json.contains("\"title\": \"Login page\""));
        Assert.assertTrue(json.contains("\"description\": \"Type password\""));
        Assert.assertTrue(json.contains("\"input\": \"secret\""));
    }

    private Settings settings() {
        List<Pair<?, ?>> tags = new ArrayList<>();
        tags.add(Pair.from(ConfigTags.OutputDir, temporaryFolder.getRoot().getAbsolutePath()));
        tags.add(Pair.from(ConfigTags.ApplicationName, "testSequenceTrace"));
        tags.add(Pair.from(ConfigTags.ReportInHTML, false));
        tags.add(Pair.from(ConfigTags.ReportInPlainText, false));
        tags.add(Pair.from(ConfigTags.SUTConnectorValue, "https://example.org/login"));
        return new Settings(tags, new Properties());
    }
}

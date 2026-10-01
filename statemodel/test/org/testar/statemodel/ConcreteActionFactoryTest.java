package org.testar.statemodel;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.core.action.Type;
import org.testar.core.tag.Tags;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConcreteActionFactoryTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void copiesInputTextAndExistingScreenshot() throws Exception {
        Path screenshot = temporaryFolder.newFile("action.png").toPath();
        byte[] image = {1, 2, 3};
        Files.write(screenshot, image);
        Type action = new Type("typed text");
        action.set(Tags.ConcreteID, "action-1");
        action.set(Tags.InputText, "typed text");
        action.set(Tags.ActionScreenshotPath, screenshot.toString());

        ConcreteAction concreteAction = ConcreteActionFactory.createConcreteAction(action, new AbstractAction("abstract-1"));

        assertEquals("typed text", concreteAction.getAttributes().get(Tags.InputText));
        assertArrayEquals(image, concreteAction.getScreenshot());
    }

    @Test
    public void missingScreenshotLeavesActionWithoutImage() {
        Type action = new Type("typed text");
        action.set(Tags.ConcreteID, "action-1");

        ConcreteAction concreteAction = ConcreteActionFactory.createConcreteAction(action, new AbstractAction("abstract-1"));

        assertNull(concreteAction.getScreenshot());
    }
}

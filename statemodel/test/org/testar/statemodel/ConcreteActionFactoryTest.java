package org.testar.statemodel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.core.CodingManager;
import org.testar.core.action.Type;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

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

    @Test
    public void concreteInputVariantsRetainTheirSharedAbstractAction() {
        StateStub state = new StateStub();
        state.set(Tags.AbstractID, "state-abstract");
        state.set(Tags.ConcreteID, "state-concrete");
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.AbstractID, "widget-abstract");
        widget.set(Tags.ConcreteID, "widget-concrete");
        Type first = new Type("first");
        Type second = new Type("second");
        first.mapOriginWidget(widget);
        second.mapOriginWidget(widget);
        CodingManager.buildIDs(state, Set.of(first, second));
        AbstractAction abstractAction = new AbstractAction(first.get(Tags.AbstractID));

        ConcreteAction firstModelAction = ConcreteActionFactory.createConcreteAction(first, abstractAction);
        ConcreteAction secondModelAction = ConcreteActionFactory.createConcreteAction(second, abstractAction);
        assertEquals(first.get(Tags.AbstractID), second.get(Tags.AbstractID));
        assertEquals(first.get(Tags.ConcreteID), firstModelAction.getId());
        assertEquals(second.get(Tags.ConcreteID), secondModelAction.getId());
        assertNotEquals(firstModelAction.getId(), secondModelAction.getId());
        assertSame(abstractAction, firstModelAction.getAbstractAction());
        assertSame(abstractAction, secondModelAction.getAbstractAction());
    }
}

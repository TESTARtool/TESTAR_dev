package org.testar.core.action;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.CRC32;

import org.junit.Before;
import org.junit.Test;
import org.testar.core.CodingManager;
import org.testar.core.alayer.AbsolutePosition;
import org.testar.core.alayer.OrthogonalPosition;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Roles;
import org.testar.core.alayer.StdAbstractor;
import org.testar.core.devices.KBKeys;
import org.testar.core.devices.MouseButtons;
import org.testar.core.exceptions.NoSuchTagException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.WidgetPosition;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ActionIdentityTest {

    private StateStub state;
    private WidgetStub widget;

    @Before
    public void prepareIdentifiedStateAndWidget() {
        state = new StateStub();
        state.set(Tags.AbstractID, "state-abstract");
        state.set(Tags.ConcreteID, "state-concrete");
        widget = new WidgetStub();
        widget.setParent(state);
        state.addChild(widget);
        widget.set(Tags.AbstractID, "widget-abstract");
        widget.set(Tags.ConcreteID, "widget-concrete");
        widget.set(Tags.Role, Roles.Button);
        widget.set(Tags.Shape, Rect.from(0, 0, 100, 40));
    }

    @Test
    public void equivalentActionsKeepIdsAcrossOrderingAndAdditionalActions() {
        Action first = new PasteText("same");
        first.mapOriginWidget(widget);
        CodingManager.buildIDs(state, Collections.singleton(first));

        Action another = new Type("another");
        another.mapOriginWidget(widget);
        Action equivalent = new PasteText("same");
        equivalent.mapOriginWidget(widget);
        Set<Action> forward = new LinkedHashSet<>();
        forward.add(another);
        forward.add(equivalent);
        CodingManager.buildIDs(state, forward);
        assertEquals(first.get(Tags.AbstractID), equivalent.get(Tags.AbstractID));
        assertEquals(first.get(Tags.ConcreteID), equivalent.get(Tags.ConcreteID));

        Set<Action> reverse = new LinkedHashSet<>();
        reverse.add(equivalent);
        reverse.add(another);
        CodingManager.buildIDs(state, reverse);
        assertEquals(first.get(Tags.AbstractID), equivalent.get(Tags.AbstractID));
        assertEquals(first.get(Tags.ConcreteID), equivalent.get(Tags.ConcreteID));
    }

    @Test
    public void inputValuesShareAbstractIdentityButNotConcreteIdentity() {
        Action first = new Type("first");
        Action second = new Type("second");
        first.mapOriginWidget(widget);
        second.mapOriginWidget(widget);
        CodingManager.buildIDs(state, Set.of(first, second));
        assertEquals(first.get(Tags.AbstractID), second.get(Tags.AbstractID));
        assertNotEquals(first.get(Tags.ConcreteID), second.get(Tags.ConcreteID));

        Action pasteFirst = new PasteText("first");
        Action pasteSecond = new PasteText("second");
        pasteFirst.mapOriginWidget(widget);
        pasteSecond.mapOriginWidget(widget);
        CodingManager.buildIDs(state, Set.of(pasteFirst, pasteSecond));
        assertEquals(pasteFirst.get(Tags.AbstractID), pasteSecond.get(Tags.AbstractID));
        assertNotEquals(pasteFirst.get(Tags.ConcreteID), pasteSecond.get(Tags.ConcreteID));
        assertNotEquals(first.get(Tags.AbstractID), pasteFirst.get(Tags.AbstractID));
    }

    @Test
    public void updatedInputTextTagChangesOnlyConcreteIdentity() {
        Action type = new Type("initial");
        Action paste = new PasteText("initial");
        CodingManager.buildIDs(state, Set.of(type, paste));
        String typeAbstractId = type.get(Tags.AbstractID);
        String typeConcreteId = type.get(Tags.ConcreteID);
        String pasteAbstractId = paste.get(Tags.AbstractID);
        String pasteConcreteId = paste.get(Tags.ConcreteID);

        type.set(Tags.InputText, "updated");
        paste.set(Tags.InputText, "updated");
        CodingManager.buildIDs(state, Set.of(type, paste));
        assertEquals(typeAbstractId, type.get(Tags.AbstractID));
        assertNotEquals(typeConcreteId, type.get(Tags.ConcreteID));
        assertEquals(pasteAbstractId, paste.get(Tags.AbstractID));
        assertNotEquals(pasteConcreteId, paste.get(Tags.ConcreteID));
    }

    @Test
    public void nestedTypingKeepsInputAbstractionAndReplaceAppendDistinction() {
        AnnotatingActionCompiler compiler = new AnnotatingActionCompiler();
        Action first = compiler.clickTypeInto(widget, "first", true);
        Action second = compiler.clickTypeInto(widget, "second", true);
        Action append = compiler.clickTypeInto(widget, "first", false);
        CodingManager.buildIDs(state, Set.of(first, second, append));

        assertEquals(first.get(Tags.AbstractID), second.get(Tags.AbstractID));
        assertNotEquals(first.get(Tags.ConcreteID), second.get(Tags.ConcreteID));
        assertNotEquals(first.get(Tags.AbstractID), append.get(Tags.AbstractID));
    }

    @Test
    public void mouseButtonsKeysAndCompoundOrderDistinguishBehavior() {
        Action left = new MouseDown(MouseButtons.BUTTON1);
        Action right = new MouseDown(MouseButtons.BUTTON3);
        Action enter = new KeyDown(KBKeys.VK_ENTER);
        Action escape = new KeyDown(KBKeys.VK_ESCAPE);
        Action forward = new CompoundAction(left, enter);
        Action reverse = new CompoundAction(enter, left);
        CodingManager.buildIDs(state, Set.of(left, right, enter, escape, forward, reverse));

        assertNotEquals(left.get(Tags.AbstractID), right.get(Tags.AbstractID));
        assertNotEquals(left.get(Tags.ConcreteID), right.get(Tags.ConcreteID));
        assertNotEquals(enter.get(Tags.AbstractID), escape.get(Tags.AbstractID));
        assertNotEquals(forward.get(Tags.AbstractID), reverse.get(Tags.AbstractID));
        assertNotEquals(forward.get(Tags.ConcreteID), reverse.get(Tags.ConcreteID));
    }

    @Test
    public void compoundTimingDistinguishesClickFromLongClick() {
        Action click = new CompoundAction.Builder().add(new MouseDown(MouseButtons.BUTTON1), 0)
                .add(new MouseUp(MouseButtons.BUTTON1), 1).build(widget);
        Action longClick = new CompoundAction.Builder().add(new MouseDown(MouseButtons.BUTTON1), 1)
                .add(new MouseUp(MouseButtons.BUTTON1), 1).build(widget);
        CodingManager.buildIDs(state, Set.of(click, longClick));

        assertEquals(click.get(Tags.Role), longClick.get(Tags.Role));
        assertNotEquals(click.get(Tags.AbstractID), longClick.get(Tags.AbstractID));
        assertNotEquals(click.get(Tags.ConcreteID), longClick.get(Tags.ConcreteID));
    }

    @Test
    public void originAndStateIdsContributeAtTheirRespectiveAbstractionLevels() {
        Action action = new Type("same");
        action.mapOriginWidget(widget);
        CodingManager.buildIDs(state, Collections.singleton(action));
        String abstractId = action.get(Tags.AbstractID);
        String concreteId = action.get(Tags.ConcreteID);

        widget.set(Tags.ConcreteID, "changed-widget-concrete");
        CodingManager.buildIDs(state, Collections.singleton(action));
        assertEquals(abstractId, action.get(Tags.AbstractID));
        assertNotEquals(concreteId, action.get(Tags.ConcreteID));
        concreteId = action.get(Tags.ConcreteID);

        state.set(Tags.ConcreteID, "changed-state-concrete");
        CodingManager.buildIDs(state, Collections.singleton(action));
        assertEquals(abstractId, action.get(Tags.AbstractID));
        assertNotEquals(concreteId, action.get(Tags.ConcreteID));

        widget.set(Tags.AbstractID, "changed-widget-abstract");
        CodingManager.buildIDs(state, Collections.singleton(action));
        assertNotEquals(abstractId, action.get(Tags.AbstractID));
        abstractId = action.get(Tags.AbstractID);
        state.set(Tags.AbstractID, "changed-state-abstract");
        CodingManager.buildIDs(state, Collections.singleton(action));
        assertNotEquals(abstractId, action.get(Tags.AbstractID));
    }

    @Test
    public void roleAndImplementationDistinguishSameWidgetActions() {
        Action type = new Type("same");
        Action paste = new PasteText("same");
        type.mapOriginWidget(widget);
        paste.mapOriginWidget(widget);
        type.set(Tags.Role, ActionRoles.Type);
        paste.set(Tags.Role, ActionRoles.Type);
        CodingManager.buildIDs(state, Set.of(type, paste));
        assertNotEquals(type.get(Tags.AbstractID), paste.get(Tags.AbstractID));

        String previousId = type.get(Tags.AbstractID);
        type.set(Tags.Role, ActionRoles.Paste);
        CodingManager.buildIDs(state, Collections.singleton(type));
        assertNotEquals(previousId, type.get(Tags.AbstractID));
    }

    @Test
    public void environmentActionsUseTheSameSchemeAndNeedNoWidgetPath() {
        Action action = new ActivateSystem();
        CodingManager.buildIDs(state, Collections.singleton(action));
        assertNotNull(action.get(Tags.AbstractID));
        assertNotNull(action.get(Tags.ConcreteID));
        String abstractId = action.get(Tags.AbstractID);
        String concreteId = action.get(Tags.ConcreteID);
        CodingManager.buildEnvironmentActionIDs(state, action);
        assertEquals(abstractId, action.get(Tags.AbstractID));
        assertEquals(concreteId, action.get(Tags.ConcreteID));

        action.mapOriginWidget(state);
        CodingManager.buildIDs(state, Collections.singleton(action));
        abstractId = action.get(Tags.AbstractID);
        CodingManager.buildEnvironmentActionIDs(state, action);
        assertEquals(abstractId, action.get(Tags.AbstractID));
    }

    @Test
    public void processTargetsAndAbsolutePositionsDoNotCollapse() {
        Action firstProcess = KillProcess.byName("first", 0);
        Action secondProcess = KillProcess.byName("second", 0);
        Action firstPosition = new MouseMove(10, 20);
        Action secondPosition = new MouseMove(20, 10);
        Action firstOrthogonal = new MouseMove(new OrthogonalPosition(new AbsolutePosition(0, 0), new AbsolutePosition(10, 10), 1, 0));
        Action secondOrthogonal = new MouseMove(new OrthogonalPosition(new AbsolutePosition(0, 0), new AbsolutePosition(10, 10), 2, 0));
        CodingManager.buildIDs(state, Set.of(firstProcess, secondProcess, firstPosition, secondPosition, firstOrthogonal, secondOrthogonal));

        assertNotEquals(firstProcess.get(Tags.AbstractID), secondProcess.get(Tags.AbstractID));
        assertNotEquals(firstProcess.get(Tags.ConcreteID), secondProcess.get(Tags.ConcreteID));
        assertNotEquals(firstPosition.get(Tags.AbstractID), secondPosition.get(Tags.AbstractID));
        assertNotEquals(firstPosition.get(Tags.ConcreteID), secondPosition.get(Tags.ConcreteID));
        assertNotEquals(firstOrthogonal.get(Tags.AbstractID), secondOrthogonal.get(Tags.AbstractID));
    }

    @Test
    public void relativePositionsRemainStableWhenCachedGeometryChanges() {
        WidgetPosition position = new WidgetPosition(new StdAbstractor().apply(widget), Tags.Shape, 0.5, 0.5, false);
        Action action = new MouseMove(position);
        action.mapOriginWidget(widget);
        CodingManager.buildIDs(state, Collections.singleton(action));
        String abstractId = action.get(Tags.AbstractID);
        String concreteId = action.get(Tags.ConcreteID);

        widget.set(Tags.Shape, Rect.from(100, 200, 100, 40));
        position.apply(state);
        CodingManager.buildIDs(state, Collections.singleton(action));
        assertEquals(abstractId, action.get(Tags.AbstractID));
        assertEquals(concreteId, action.get(Tags.ConcreteID));

        Action otherOffset = new MouseMove(new WidgetPosition(new StdAbstractor().apply(widget), Tags.Shape, 0.1, 0.5, false));
        otherOffset.mapOriginWidget(widget);
        CodingManager.buildIDs(state, Collections.singleton(otherOffset));
        assertNotEquals(abstractId, otherOffset.get(Tags.AbstractID));
    }

    @Test
    public void dragDestinationIdentityDistinguishesSameOriginAndRole() {
        WidgetStub firstDestination = new WidgetStub();
        firstDestination.setParent(state);
        state.addChild(firstDestination);
        firstDestination.set(Tags.AbstractID, "first-target-abstract");
        firstDestination.set(Tags.ConcreteID, "first-target-concrete");
        firstDestination.set(Tags.Shape, Rect.from(200, 100, 50, 40));
        WidgetStub secondDestination = new WidgetStub();
        secondDestination.setParent(state);
        state.addChild(secondDestination);
        secondDestination.set(Tags.AbstractID, "second-target-abstract");
        secondDestination.set(Tags.ConcreteID, "second-target-concrete");
        secondDestination.set(Tags.Shape, Rect.from(300, 100, 50, 40));
        AnnotatingActionCompiler compiler = new AnnotatingActionCompiler();
        Action first = compiler.dragFromTo(widget, firstDestination);
        Action second = compiler.dragFromTo(widget, secondDestination);
        CodingManager.buildIDs(state, Set.of(first, second));

        assertEquals(first.get(Tags.OriginWidget), second.get(Tags.OriginWidget));
        assertEquals(first.get(Tags.Role), second.get(Tags.Role));
        assertNotEquals(first.get(Tags.AbstractID), second.get(Tags.AbstractID));
        assertNotEquals(first.get(Tags.ConcreteID), second.get(Tags.ConcreteID));
    }

    @Test
    public void concreteParametersDoNotUseObjectToStringOrDescription() {
        Action first = new ParameterAction("same");
        Action second = new ParameterAction("same");
        first.set(Tags.Desc, "first description");
        second.set(Tags.Desc, "second description");
        CodingManager.buildIDs(state, Set.of(first, second));
        assertEquals(first.get(Tags.AbstractID), second.get(Tags.AbstractID));
        assertEquals(first.get(Tags.ConcreteID), second.get(Tags.ConcreteID));

        Action different = new ParameterAction("different");
        CodingManager.buildIDs(state, Collections.singleton(different));
        assertNotEquals(first.get(Tags.ConcreteID), different.get(Tags.ConcreteID));
    }

    @Test
    public void fieldBoundariesCannotCollideByConcatenation() {
        Action action = new Type("same");
        action.mapOriginWidget(widget);
        state.set(Tags.ConcreteID, "ab");
        widget.set(Tags.ConcreteID, "c");
        CodingManager.buildIDs(state, Collections.singleton(action));
        String concreteId = action.get(Tags.ConcreteID);
        state.set(Tags.ConcreteID, "a");
        widget.set(Tags.ConcreteID, "bc");
        CodingManager.buildIDs(state, Collections.singleton(action));
        assertNotEquals(concreteId, action.get(Tags.ConcreteID));
        assertNotEquals(ActionIdentity.encode("ab", "c"), ActionIdentity.encode("a", "bc"));
    }

    @Test(expected = NoSuchTagException.class)
    public void unidentifiedOriginWidgetIsNotSilentlyTreatedAsEnvironmentAction() {
        Action action = new Type("value");
        action.mapOriginWidget(new WidgetStub());
        CodingManager.buildIDs(state, Collections.singleton(action));
    }

    @Test
    public void unicodeActionParametersUseUtf8RegardlessOfPlatformCharset() {
        Action action = new PasteText("caf\u00e9 \u4e2d\u6587");
        action.mapOriginWidget(widget);
        String identity = ActionIdentity.encode(state.get(Tags.ConcreteID), ActionIdentity.describe(state, action, false));
        CRC32 crc32 = new CRC32();
        crc32.update(identity.getBytes(StandardCharsets.UTF_8));
        String expectedId = "AC" + Integer.toUnsignedString(identity.hashCode(), Character.MAX_RADIX)
                + Integer.toHexString(identity.length()) + crc32.getValue();

        CodingManager.buildIDs(state, Collections.singleton(action));
        assertEquals(expectedId, action.get(Tags.ConcreteID));
    }

    private static final class ParameterAction extends TaggableBase implements Action {

        private static final long serialVersionUID = 1L;
        private final String parameter;

        private ParameterAction(String parameter) {
            this.parameter = parameter;
        }

        @Override
        public void run(SUT system, State state, double duration) { }

        @Override
        public String toShortString() {
            return "parameter action";
        }

        @Override
        public String toParametersString() {
            return parameter;
        }

        @Override
        public String toString(Role... discardParameters) {
            return toString();
        }
    }
}

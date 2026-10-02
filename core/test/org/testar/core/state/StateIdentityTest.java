package org.testar.core.state;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testar.core.CodingManager;
import org.testar.core.StateManagementTags;
import org.testar.core.alayer.Roles;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.util.IdentityEncoding;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class StateIdentityTest {

    private Tag<?>[] previousAbstractTags;
    private Tag<?>[] previousConcreteTags;
    private StateIdentity identity;

    @Before
    public void prepareIdentity() {
        previousAbstractTags = CodingManager.getCustomTagsForAbstractId();
        previousConcreteTags = CodingManager.getCustomTagsForConcreteId();
        identity = new StateIdentity(new Tag<?>[] {Tags.Title, Tags.Path}, new Tag<?>[] {Tags.Title, Tags.Path});
    }

    @After
    public void restoreConfiguration() {
        CodingManager.setCustomTagsForAbstractId(previousAbstractTags);
        CodingManager.setCustomTagsForConcreteId(previousConcreteTags);
    }

    @Test
    public void attributeBoundariesDistinguishPreviouslyAmbiguousValues() {
        StateStub state = new StateStub();
        WidgetStub first = new WidgetStub();
        first.setParent(state);
        first.set(Tags.Path, "ab");
        first.set(Tags.Title, "c");
        WidgetStub second = new WidgetStub();
        second.setParent(state);
        second.set(Tags.Path, "a");
        second.set(Tags.Title, "bc");

        identity.buildIDs(first);
        identity.buildIDs(second);

        assertNotEquals(first.get(Tags.AbstractID), second.get(Tags.AbstractID));
        assertNotEquals(first.get(Tags.ConcreteID), second.get(Tags.ConcreteID));
    }

    @Test
    public void missingValuesDifferFromLiteralNullAndEmptyText() {
        StateStub state = new StateStub();
        WidgetStub missing = new WidgetStub();
        missing.setParent(state);
        WidgetStub literal = new WidgetStub();
        literal.setParent(state);
        literal.set(Tags.Title, "null");
        WidgetStub empty = new WidgetStub();
        empty.setParent(state);
        empty.set(Tags.Title, "");

        identity.buildIDs(missing);
        identity.buildIDs(literal);
        identity.buildIDs(empty);

        assertNotEquals(missing.get(Tags.AbstractID), literal.get(Tags.AbstractID));
        assertNotEquals(missing.get(Tags.ConcreteID), literal.get(Tags.ConcreteID));
        assertNotEquals(missing.get(Tags.AbstractID), empty.get(Tags.AbstractID));
        assertNotEquals(missing.get(Tags.ConcreteID), empty.get(Tags.ConcreteID));
        assertNotEquals(literal.get(Tags.AbstractID), empty.get(Tags.AbstractID));
        assertNotEquals(literal.get(Tags.ConcreteID), empty.get(Tags.ConcreteID));
    }

    @Test
    public void attributeNamesAndTypesArePartOfIdentity() {
        StateStub state = new StateStub();
        state.set(Tags.Title, "same");
        state.set(Tags.Path, "same");
        identity.buildIDs(state);
        String original = state.get(Tags.AbstractID);
        new StateIdentity(new Tag<?>[] {Tags.Title}, new Tag<?>[] {Tags.Title}).buildIDs(state);
        String titleOnly = state.get(Tags.AbstractID);
        new StateIdentity(new Tag<?>[] {Tags.Path}, new Tag<?>[] {Tags.Path}).buildIDs(state);

        assertNotEquals(original, titleOnly);
        assertNotEquals(titleOnly, state.get(Tags.AbstractID));
        Tag<String> textTag = Tag.from("value", String.class);
        Tag<Integer> numberTag = Tag.from("value", Integer.class);
        state.set(textTag, "1");
        state.set(numberTag, 1);
        new StateIdentity(new Tag<?>[] {textTag}, new Tag<?>[] {textTag}).buildIDs(state);
        String textId = state.get(Tags.AbstractID);
        new StateIdentity(new Tag<?>[] {numberTag}, new Tag<?>[] {numberTag}).buildIDs(state);
        assertNotEquals(textId, state.get(Tags.AbstractID));
    }

    @Test
    public void repeatedInitializationResetsEmptyAndInvalidSelectionsToDefault() {
        CodingManager.initCodingManager(List.of("WebWidgetId"));
        CodingManager.initCodingManager(Collections.emptyList());
        assertArrayEquals(CodingManager.getDefaultAbstractStateTags(), CodingManager.getCustomTagsForAbstractId());

        CodingManager.initCodingManager(List.of("AndroidWidgetResourceId"));
        CodingManager.initCodingManager(List.of("unknown-attribute"));
        assertArrayEquals(CodingManager.getDefaultAbstractStateTags(), CodingManager.getCustomTagsForAbstractId());
        assertEquals(StateManagementTags.getAllTags().size(), CodingManager.getCustomTagsForConcreteId().length);
    }

    @Test
    public void configurationDefensivelyCopiesInputAndOutputArrays() {
        Tag<?>[] abstractTags = {Tags.Title, Tags.Path};
        Tag<?>[] concreteTags = {Tags.Title};
        CodingManager.setCustomTagsForAbstractId(abstractTags);
        CodingManager.setCustomTagsForConcreteId(concreteTags);
        assertArrayEquals(new Tag<?>[] {Tags.Title, Tags.Path}, abstractTags);
        abstractTags[0] = Tags.Role;
        concreteTags[0] = Tags.Role;
        CodingManager.getCustomTagsForAbstractId()[0] = Tags.Role;
        CodingManager.getCustomTagsForConcreteId()[0] = Tags.Role;
        CodingManager.getDefaultAbstractStateTags()[0] = Tags.Role;

        assertArrayEquals(new Tag<?>[] {Tags.Path, Tags.Title}, CodingManager.getCustomTagsForAbstractId());
        assertArrayEquals(new Tag<?>[] {Tags.Title}, CodingManager.getCustomTagsForConcreteId());
        assertArrayEquals(new Tag<?>[] {StateManagementTags.WidgetControlType}, CodingManager.getDefaultAbstractStateTags());
    }

    @Test
    public void attributeSelectionOrderDoesNotChangeIds() {
        StateStub state = new StateStub();
        state.set(Tags.Title, "Submit");
        state.set(Tags.Path, "root");
        identity.buildIDs(state);
        String abstractId = state.get(Tags.AbstractID);
        String concreteId = state.get(Tags.ConcreteID);

        new StateIdentity(new Tag<?>[] {Tags.Path, Tags.Title}, new Tag<?>[] {Tags.Path, Tags.Title}).buildIDs(state);

        assertEquals(abstractId, state.get(Tags.AbstractID));
        assertEquals(concreteId, state.get(Tags.ConcreteID));
    }

    @Test
    public void rootAttributesContributeEvenWhenStateHasNoWidgets() {
        StateStub state = new StateStub();
        state.set(Tags.Title, "first-page");
        identity.buildIDs(state);
        String abstractId = state.get(Tags.AbstractID);
        String concreteId = state.get(Tags.ConcreteID);

        state.set(Tags.Title, "second-page");
        identity.buildIDs(state);

        assertNotEquals(abstractId, state.get(Tags.AbstractID));
        assertNotEquals(concreteId, state.get(Tags.ConcreteID));
    }

    @Test
    public void unselectedRootAttributeChangesOnlyConcreteIdentity() {
        StateIdentity selection = new StateIdentity(new Tag<?>[] {Tags.Role}, new Tag<?>[] {Tags.Role, Tags.Title});
        StateStub state = new StateStub();
        state.set(Tags.Role, Roles.Widget);
        state.set(Tags.Title, "first-page");
        selection.buildIDs(state);
        String abstractId = state.get(Tags.AbstractID);
        String concreteId = state.get(Tags.ConcreteID);
        state.set(Tags.Title, "second-page");
        selection.buildIDs(state);

        assertEquals(abstractId, state.get(Tags.AbstractID));
        assertNotEquals(concreteId, state.get(Tags.ConcreteID));
    }

    @Test
    public void parentChildStructureDistinguishesTheSameFlatWidgetSequence() {
        StateStub flat = new StateStub();
        WidgetStub first = new WidgetStub();
        first.setParent(flat);
        first.set(Tags.Title, "first");
        WidgetStub second = new WidgetStub();
        second.setParent(flat);
        second.set(Tags.Title, "second");
        flat.addChild(first);
        flat.addChild(second);
        identity.buildIDs(flat);

        StateStub nested = new StateStub();
        WidgetStub parent = new WidgetStub();
        parent.setParent(nested);
        parent.set(Tags.Title, "first");
        WidgetStub child = new WidgetStub();
        child.setParent(parent);
        child.set(Tags.Title, "second");
        nested.addChild(parent);
        parent.addChild(child);
        identity.buildIDs(nested);

        assertEquals(first.get(Tags.AbstractID), parent.get(Tags.AbstractID));
        assertEquals(second.get(Tags.AbstractID), child.get(Tags.AbstractID));
        assertNotEquals(flat.get(Tags.AbstractID), nested.get(Tags.AbstractID));
        assertNotEquals(flat.get(Tags.ConcreteID), nested.get(Tags.ConcreteID));
    }

    @Test
    public void siblingOrderRemainsMeaningful() {
        StateStub forward = new StateStub();
        WidgetStub first = new WidgetStub();
        first.setParent(forward);
        first.set(Tags.Title, "first");
        WidgetStub second = new WidgetStub();
        second.setParent(forward);
        second.set(Tags.Title, "second");
        forward.addChild(first);
        forward.addChild(second);
        identity.buildIDs(forward);

        StateStub reverse = new StateStub();
        WidgetStub reverseFirst = new WidgetStub();
        reverseFirst.setParent(reverse);
        reverseFirst.set(Tags.Title, "first");
        WidgetStub reverseSecond = new WidgetStub();
        reverseSecond.setParent(reverse);
        reverseSecond.set(Tags.Title, "second");
        reverse.addChild(reverseSecond);
        reverse.addChild(reverseFirst);
        identity.buildIDs(reverse);

        assertNotEquals(forward.get(Tags.AbstractID), reverse.get(Tags.AbstractID));
        assertNotEquals(forward.get(Tags.ConcreteID), reverse.get(Tags.ConcreteID));
    }

    @Test
    public void stateIteratorDoesNotDefineIdentity() {
        StateStub normal = new StateStub();
        WidgetStub first = new WidgetStub();
        first.setParent(normal);
        first.set(Tags.Title, "Submit");
        normal.addChild(first);
        identity.buildIDs(normal);

        StateStub alternateIterator = new StateStub() {
            @Override
            public Iterator<Widget> iterator() {
                return Collections.emptyIterator();
            }
        };
        WidgetStub equivalent = new WidgetStub();
        equivalent.setParent(alternateIterator);
        equivalent.set(Tags.Title, "Submit");
        alternateIterator.addChild(equivalent);
        identity.buildIDs(alternateIterator);

        assertEquals(normal.get(Tags.AbstractID), alternateIterator.get(Tags.AbstractID));
        assertEquals(normal.get(Tags.ConcreteID), alternateIterator.get(Tags.ConcreteID));
        assertEquals(first.get(Tags.AbstractID), equivalent.get(Tags.AbstractID));
    }

    @Test
    public void deeplyNestedTreesDoNotRequireRecursiveIdentification() {
        StateStub state = new StateStub();
        WidgetStub parent = state;
        for (int index = 0; index < 3000; index++) {
            WidgetStub child = new WidgetStub();
            child.setParent(parent);
            parent.addChild(child);
            parent = child;
        }
        identity.buildIDs(state);
        assertTrue(state.get(Tags.AbstractID).startsWith("SA"));
        assertTrue(parent.get(Tags.ConcreteID).startsWith("WC"));
    }

    @Test
    public void unicodeWidgetIdentityUsesUtf8() {
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        widget.setParent(state);
        widget.set(Tags.Title, "caf\u00e9 \u4e2d\u6587");
        StateIdentity titleIdentity = new StateIdentity(new Tag<?>[] {Tags.Title}, new Tag<?>[] {Tags.Title});
        String encoded = IdentityEncoding.encode(Tags.Title.name(), String.class.getName(), "caf\u00e9 \u4e2d\u6587");
        CRC32 crc32 = new CRC32();
        crc32.update(encoded.getBytes(StandardCharsets.UTF_8));
        String expected = "WC" + Integer.toUnsignedString(encoded.hashCode(), Character.MAX_RADIX)
                + Integer.toHexString(encoded.length()) + crc32.getValue();

        titleIdentity.buildIDs(widget);

        assertEquals(expected, widget.get(Tags.ConcreteID));
    }

    @Test
    public void modelHashPreservesApplicationFieldBoundariesAndAttributeOrder() {
        StateIdentity reordered = new StateIdentity(new Tag<?>[] {Tags.Path, Tags.Title}, new Tag<?>[] {Tags.Title});
        assertEquals(identity.modelHash("application", "1"), reordered.modelHash("application", "1"));
        assertNotEquals(identity.modelHash("ab", "c"), identity.modelHash("a", "bc"));
    }

    @Test
    public void selectedControlPatternIncludesItsChildValues() {
        StateIdentity patternIdentity = new StateIdentity(new Tag<?>[] {StateManagementTags.WidgetValuePattern},
                new Tag<?>[] {StateManagementTags.WidgetValuePattern});
        StateStub state = new StateStub();
        state.set(StateManagementTags.WidgetValuePattern, true);
        state.set(StateManagementTags.WidgetValueValue, "first");
        patternIdentity.buildIDs(state);
        String abstractId = state.get(Tags.AbstractID);
        String concreteId = state.get(Tags.ConcreteID);
        state.set(StateManagementTags.WidgetValueValue, "second");
        patternIdentity.buildIDs(state);

        assertNotEquals(abstractId, state.get(Tags.AbstractID));
        assertNotEquals(concreteId, state.get(Tags.ConcreteID));
    }

    @Test
    public void unicodeRootAndModelHashesUseUtf8() {
        StateIdentity titleIdentity = new StateIdentity(new Tag<?>[] {Tags.Title}, new Tag<?>[] {Tags.Title});
        StateStub state = new StateStub();
        state.set(Tags.Title, "caf\u00e9 \u4e2d\u6587");
        String attributeData = IdentityEncoding.encode(Tags.Title.name(), String.class.getName(), "caf\u00e9 \u4e2d\u6587");
        CRC32 crc32 = new CRC32();
        crc32.update(attributeData.getBytes(StandardCharsets.UTF_8));
        String rootWidgetId = "WC" + Integer.toUnsignedString(attributeData.hashCode(), Character.MAX_RADIX)
                + Integer.toHexString(attributeData.length()) + crc32.getValue();
        String treeData = IdentityEncoding.encode(rootWidgetId, "0");
        crc32.reset();
        crc32.update(treeData.getBytes(StandardCharsets.UTF_8));
        String expectedStateId = "SC" + Integer.toUnsignedString(treeData.hashCode(), Character.MAX_RADIX)
                + Integer.toHexString(treeData.length()) + crc32.getValue();
        titleIdentity.buildIDs(state);
        assertEquals(expectedStateId, state.get(Tags.ConcreteID));

        String modelData = IdentityEncoding.encode("caf\u00e9 \u4e2d\u6587", "1")
                + IdentityEncoding.encode(Tags.Title.name(), String.class.getName());
        crc32.reset();
        crc32.update(modelData.getBytes(StandardCharsets.UTF_8));
        String expectedModelId = Integer.toUnsignedString(modelData.hashCode(), Character.MAX_RADIX)
                + Integer.toHexString(modelData.length()) + crc32.getValue();
        assertEquals(expectedModelId, titleIdentity.modelHash("caf\u00e9 \u4e2d\u6587", "1"));
    }
}

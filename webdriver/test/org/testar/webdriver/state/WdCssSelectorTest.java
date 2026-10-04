package org.testar.webdriver.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.ConcreteStateFactory;
import org.testar.webdriver.action.WdSelectListAction;
import org.testar.webdriver.action.WebdriverSelectListSupport;
import org.testar.webdriver.tag.WdTags;

public class WdCssSelectorTest {

    @Test
    public void exposesPackedSelectorAsAWidgetTag() {
        Map<String, Object> packed = packedSelect();
        packed.put("cssSelector", "#shipping > select:nth-of-type(2)");
        WdElement element = new WdElement(packed, null, null);
        WdState state = new WdState(new WdRootElement());
        WdWidget widget = state.addChild(state, element);

        assertEquals("#shipping > select:nth-of-type(2)", element.cssSelector);
        assertEquals(element.cssSelector, widget.get(WdTags.WebCssSelector));
        assertTrue(StreamSupport.stream(widget.tags().spliterator(), false).anyMatch(WdTags.WebCssSelector::equals));
        assertTrue(WdTags.tagSet().contains(WdTags.WebCssSelector));
    }

    @Test
    public void exposesEmptySelectorWhenMissingOrIgnored() {
        Map<String, Object> packed = packedSelect();
        WdState state = new WdState(new WdRootElement());
        WdWidget widget = state.addChild(state, new WdElement(packed, null, null));
        assertEquals("", widget.get(WdTags.WebCssSelector));

        packed.put("cssSelector", "");
        widget = state.addChild(state, new WdElement(packed, null, null));
        assertEquals("", widget.get(WdTags.WebCssSelector));
    }

    @Test
    public void preservesSelectorWhenAStateIsSerialized() throws Exception {
        Map<String, Object> packed = packedSelect();
        packed.put("cssSelector", "#cars");
        WdState state = new WdState(new WdRootElement());
        state.addChild(state, new WdElement(packed, null, null));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(buffer)) {
            output.writeObject(state);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()))) {
            WdState restored = (WdState) input.readObject();
            assertEquals("#cars", restored.child(0).get(WdTags.WebCssSelector));
        }
    }

    @Test
    public void copiesSelectorIntoTheStoredModelWidgetTree() {
        Map<String, Object> packed = packedSelect();
        packed.put("cssSelector", "#cars");
        WdState state = new WdState(new WdRootElement());
        state.set(Tags.ConcreteID, "SC1");
        WdWidget widget = state.addChild(state, new WdElement(packed, null, null));
        widget.set(Tags.ConcreteID, "WC1");

        ConcreteState stored = ConcreteStateFactory.createConcreteState(state, new AbstractState("SA1", Set.of()), true);

        assertEquals("#cars", stored.getChildren().get(0).getAttributes().get(WdTags.WebCssSelector));
    }

    @Test
    public void createsCssTargetedSelectionFromThePackedNamelessElement() {
        Map<String, Object> packed = packedSelect();
        packed.put("cssSelector", "#shipping > select");
        packed.put("innerHTML", "<option value=\"saab\">Saab</option>");
        WdState state = new WdState(new WdRootElement());
        WdWidget widget = state.addChild(state, new WdElement(packed, null, null));
        assertEquals("Saab", widget.get(WdTags.WebName));

        WdSelectListAction action = (WdSelectListAction) WebdriverSelectListSupport.createActionForInput(widget, "Saab");

        assertEquals(WdSelectListAction.JsTargetMethod.CSS, action.getTargetMethod());
        assertEquals("#shipping > select", action.getTarget());
        assertEquals("saab", action.getValue());
    }

    private Map<String, Object> packedSelect() {
        Map<String, Object> packed = new HashMap<>();
        packed.put("attributeMap", Map.of());
        packed.put("tagName", "select");
        packed.put("textContent", "Saab");
        packed.put("zIndex", 0L);
        packed.put("rect", List.of(0L, 0L, 100L, 30L));
        packed.put("dimensions", Map.ofEntries(
                Map.entry("overflowX", "visible"), Map.entry("overflowY", "visible"), Map.entry("scrollSnapType", "none"),
                Map.entry("clientWidth", 100L), Map.entry("clientHeight", 30L),
                Map.entry("offsetWidth", 100L), Map.entry("offsetHeight", 30L),
                Map.entry("scrollWidth", 100L), Map.entry("scrollHeight", 30L),
                Map.entry("scrollLeft", 0L), Map.entry("scrollTop", 0L),
                Map.entry("borderWidth", 0L), Map.entry("borderHeight", 0L)));
        packed.put("isBlocked", false);
        packed.put("isClickable", false);
        packed.put("isShadowElement", false);
        packed.put("hasKeyboardFocus", false);
        packed.put("wrappedChildren", List.of());
        return packed;
    }
}

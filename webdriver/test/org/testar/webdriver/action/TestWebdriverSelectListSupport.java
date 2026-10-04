package org.testar.webdriver.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testar.core.tag.Tags;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.tag.WdTags;

public class TestWebdriverSelectListSupport {

    @Test
    public void resolvesTargetsInIdNameCssOrder() {
        WidgetStub widget = createSelectWidget("<option value=\"saab\">Saab</option>");
        widget.set(WdTags.WebId, "cars-id");
        widget.set(WdTags.WebName, "cars-name");
        widget.set(WdTags.WebCssSelector, "#cars");

        WdSelectListAction byId = (WdSelectListAction) WebdriverSelectListSupport.createActionForInput(widget, "Saab");
        assertEquals(WdSelectListAction.JsTargetMethod.ID, byId.getTargetMethod());
        assertEquals("cars-id", byId.getTarget());

        widget.set(WdTags.WebId, "");
        WdSelectListAction byName = (WdSelectListAction) WebdriverSelectListSupport.createActionForInput(widget, "Saab");
        assertEquals(WdSelectListAction.JsTargetMethod.NAME, byName.getTargetMethod());
        assertEquals("cars-name", byName.getTarget());

        widget.set(WdTags.WebName, "");
        WdSelectListAction byCss = (WdSelectListAction) WebdriverSelectListSupport.createActionForInput(widget, "Saab");
        assertEquals(WdSelectListAction.JsTargetMethod.CSS, byCss.getTargetMethod());
        assertEquals("#cars", byCss.getTarget());
        assertEquals("saab", byCss.getValue());
        assertEquals("saab", byCss.get(Tags.InputText));
    }

    @Test
    public void ignoresGeneratedWebNameWhenTheDomNameIsAbsent() {
        WidgetStub widget = createSelectWidget("<option value=\"saab\">Saab</option>");
        widget.set(WdTags.WebName, "Saab");
        widget.set(WdTags.WebAttributeMap, Map.of());
        widget.set(WdTags.WebCssSelector, "#shipping > select");

        WdSelectListAction action = (WdSelectListAction) WebdriverSelectListSupport.createSelectAction(widget);

        assertEquals(WdSelectListAction.JsTargetMethod.CSS, action.getTargetMethod());
        assertEquals("#shipping > select", action.getTarget());
        assertEquals("saab", action.getValue());
    }

    @Test
    public void usesActualDomNameInsteadOfGeneratedWebName() {
        WidgetStub widget = createSelectWidget("<option value=\"saab\">Saab</option>");
        widget.set(WdTags.WebName, "Saab");
        widget.set(WdTags.WebAttributeMap, Map.of("name", "cars"));

        WdSelectListAction action = (WdSelectListAction) WebdriverSelectListSupport.createSelectAction(widget);

        assertEquals(WdSelectListAction.JsTargetMethod.NAME, action.getTargetMethod());
        assertEquals("cars", action.getTarget());
    }

    @Test
    public void usesCapturedElementWhenIdNameAndCssAreUnavailable() {
        WidgetStub widget = createSelectWidget("<option value=\"saab\">Saab</option>");
        widget.set(WdTags.WebElementSelenium, Mockito.mock(RemoteWebElement.class));

        WdSelectListAction action = (WdSelectListAction) WebdriverSelectListSupport.createActionForInput(widget, "Saab");

        assertEquals(WdSelectListAction.JsTargetMethod.ELEMENT, action.getTargetMethod());
        assertEquals("saab", action.getValue());
    }

    @Test
    public void doesNotCreateActionWithoutAnyUsableTarget() {
        WidgetStub widget = createSelectWidget("<option value=\"saab\">Saab</option>");

        assertNull(WebdriverSelectListSupport.createSelectAction(widget));
        assertNull(WebdriverSelectListSupport.createActionForInput(widget, "Saab"));
    }

    @Test
    public void mapsExactCaseDistinctOptionLabelsToTheirOwnValues() {
        WidgetStub widget = createSelectWidget("<option value=\"upper\">Saab</option><option value=\"lower\">saab</option>");

        assertEquals("upper", WebdriverSelectListSupport.mapInputToOptionValue(widget, "Saab"));
        assertEquals("lower", WebdriverSelectListSupport.mapInputToOptionValue(widget, "saab"));
    }

    @Test
    public void mapsUniqueCaseInsensitiveMatchToOptionValue() {
        WidgetStub widget = createSelectWidget("<option value=\"saab\">Saab</option>");

        assertEquals("saab", WebdriverSelectListSupport.mapInputToOptionValue(widget, "SAAB"));
    }

    @Test
    public void preservesInputWhenCaseInsensitiveMatchIsAmbiguous() {
        WidgetStub widget = createSelectWidget("<option value=\"upper\">Saab</option><option value=\"lower\">saab</option>");

        assertEquals("SAAB", WebdriverSelectListSupport.mapInputToOptionValue(widget, "SAAB"));
    }

    private WidgetStub createSelectWidget(String innerHtml) {
        WidgetStub widget = new WidgetStub();
        widget.set(WdTags.WebInnerHTML, innerHtml);
        widget.set(Tags.Desc, "select widget");
        return widget;
    }
}

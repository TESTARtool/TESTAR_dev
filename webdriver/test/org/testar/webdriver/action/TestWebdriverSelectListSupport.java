package org.testar.webdriver.action;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.tag.WdTags;

public class TestWebdriverSelectListSupport {

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

package org.testar.webdriver.action;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.engine.action.resolver.DescriptionActionResolver;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public class TestWebdriverSelectActionResolution {

    @Test
    public void testResolvedSelectActionPreservesIdentityTags() {
        WidgetStub widget = createSelectWidget();
        Action templateAction = new WdSelectListAction(
                "select_12345_13011_54321",
                "12345",
                widget,
                WdSelectListAction.JsTargetMethod.ID
        );
        templateAction.set(Tags.AbstractID, "AAselect123");
        templateAction.set(Tags.ConcreteID, "CCselect123");
        templateAction.set(Tags.Desc, "Set ComboBox 'select_12345_13011_54321' to one of the following values: 12345,13011,54321,");

        DescriptionActionResolver resolver = new DescriptionActionResolver();
        ResolvedAction resolvedAction = resolver.resolve(
                Collections.singleton(templateAction),
                List.of("select", "select_12345_13011", "13011")
        );

        Assert.assertTrue(resolvedAction.action() instanceof WdSelectListAction);
        Assert.assertEquals("AAselect123", resolvedAction.action().get(Tags.AbstractID, ""));
        Assert.assertEquals("CCselect123", resolvedAction.action().get(Tags.ConcreteID, ""));
        Assert.assertEquals("13011", ((WdSelectListAction) resolvedAction.action()).getValue());
    }

    private WidgetStub createSelectWidget() {
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);
        widget.set(Tags.Shape, Rect.fromCoordinates(1, 1, 1, 1));
        widget.set(Tags.Role, WdRoles.WdSELECT);
        widget.set(WdTags.WebTagName, "select");
        widget.set(WdTags.WebId, "select_12345_13011_54321");
        widget.set(WdTags.WebInnerHTML,
                "<option value=\"12345\">12345</option><option value=\"13011\">13011</option><option value=\"54321\">54321</option>");
        widget.set(Tags.Desc, "select_12345_13011_54321");
        return widget;
    }
}

package oracle_objects;

import java.util.List;
import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.WidgetStub;

public class TestGrammarHelperWidget {

	@Test
	public void test_GrammarHelper_getWidgetKeys() {
		WidgetStub widget = new WidgetStub();

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");

		List<String> widgetKeys = GrammarHelperWidget.getWidgetKeys(widget);
		Assert.isTrue(widgetKeys.contains("Role"));
		Assert.isTrue(widgetKeys.contains("WebId"));
	}

	@Test
	public void test_GrammarHelper_getWidgetValues() {
		WidgetStub widget = new WidgetStub();

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");

		List<String> widgetValues = GrammarHelperWidget.getWidgetValues(widget);
		Assert.isTrue(widgetValues.contains("Button"));
		Assert.isTrue(widgetValues.contains("formButton"));
	}

	@Test
	public void test_GrammarHelper_getWidgetPairs() {
		WidgetStub widget = new WidgetStub();

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");

		List<Pair<String, String>> widgetPairs = GrammarHelperWidget.getWidgetPairs(widget);
		Assert.isTrue(widgetPairs.contains(new Pair<String, String>("Role","Button")));
		Assert.isTrue(widgetPairs.contains(new Pair<String, String>("WebId","formButton")));
	}

	@Test
	public void test_GrammarHelper_getWidgetChildren() {
		WidgetStub parent = new WidgetStub();
		parent.set(Tags.Role, Roles.Button);

		WidgetStub child = new WidgetStub();
		child.set(WdTags.WebId, "formButton");

		parent.addChild(child);

		List<Widget> widgetChildren = GrammarHelperWidget.getWidgetChildren(parent);
		Assert.isTrue(widgetChildren.size() == 1);
		Assert.isTrue(widgetChildren.get(0).equals(child));
	}

}

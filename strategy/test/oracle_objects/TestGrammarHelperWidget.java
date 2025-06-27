package oracle_objects;

import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import java.util.List;

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
	
	@Test
	public void test_GrammarHelper_findWidgets()
	{
		StateStub  state = new StateStub();
		WidgetStub w1    = new WidgetStub();
		w1.set(Tags.Role, Roles.Widget);
		w1.set(WdTags.WebId, "formFrame");
		w1.set(WdTags.WebValue, "form");
		WidgetStub w2    = new WidgetStub();
		w2.set(Tags.Role, Roles.Button);
		w2.set(WdTags.WebId, "resetButton");
		w2.set(WdTags.WebValue, "reset");
		WidgetStub w3    = new WidgetStub();
		w3.set(Tags.Role, Roles.Button);
		w3.set(WdTags.WebId, "saveButton");
		w3.set(WdTags.WebValue, "save");
		WidgetStub w4    = new WidgetStub();
		w4.set(Tags.Role, Roles.Button);
		w4.set(WdTags.WebId, "submitButton");
		w4.set(WdTags.WebValue, "submit");
		WidgetStub w5    = new WidgetStub();
		w5.set(Tags.Role, Roles.Dialog);
		w5.set(WdTags.WebId, "submitDialog");
		w5.set(WdTags.WebTitle, "submit");
		state.addChild(w1);
		state.addChild(w2);
		state.addChild(w3);
		state.addChild(w4);
		state.addChild(w5);
		
		List<Widget> widgetsFound = GrammarHelperWidget.findWidgets(state, "button", "reset");
		Assert.isTrue(widgetsFound.size() >= 1);
		for(Widget widget : widgetsFound)
			System.out.println("Widget " + widget.get(WdTags.WebId));
	}
}

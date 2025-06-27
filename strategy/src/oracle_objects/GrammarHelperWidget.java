package oracle_objects;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GrammarHelperWidget {

	public static List<String> getWidgetKeys(Widget widget) {
		List<String> widgetTagNames = new ArrayList<>();
		for(Tag<?> tag : widget.tags()) {
			widgetTagNames.add(tag.name());
		}
		return widgetTagNames;
	}

	public static List<String> getWidgetValues(Widget widget) {
		List<String> widgetTagValues = new ArrayList<>();
		for(Tag<?> tag : widget.tags()) {
			if(widget.get(tag, null) != null) {
				// TODO: This cast to String needs to be improved
				try {
					widgetTagValues.add((String) widget.get(tag));
				} catch (ClassCastException e) {
					widgetTagValues.add(widget.get(tag).toString());
				}
			}
		}
		return widgetTagValues;
	}

	public static List<Pair<String, String>> getWidgetPairs(Widget widget){
		List<Pair<String, String>> widgetTagValuePairs = new ArrayList<>();
		for(Tag<?> tag : widget.tags()) {
			Pair<String, String> tagValuePair = new Pair<>(tag.name(), "");
			if(widget.get(tag, null) != null){
				// TODO: This cast to String needs to be improved
				try {
					tagValuePair = new Pair<>(tag.name(), (String) widget.get(tag));
				} catch (ClassCastException e) {
					tagValuePair = new Pair<>(tag.name(), widget.get(tag).toString());
				}
			}
			widgetTagValuePairs.add(tagValuePair);
		}
		return widgetTagValuePairs;
	}

	public static List<Widget> getWidgetChildren(Widget widget){
		List<Widget> widgetChildren = new ArrayList<>();
		for(int i = 0; i < widget.childCount(); i++) {
			widgetChildren.add(widget.child(i));
		}
		return widgetChildren;
	}
	
	public static List<Widget> getWidgetsOfState(State state){
		List<Widget> widgetsOfState = new ArrayList<>();
		for(Widget widget : state) {
			widgetsOfState.add(widget);
		}
		return widgetsOfState;
	}
	
	
	
	
	public static List<Widget> findWidgets(State state, String roleString, String string)
	{
		List<Widget>	  widgets 		= new ArrayList<>();
		Role              role    		= elementToRole.get(roleString);
		List<Tag<String>> tagList 		= selectorStringToTags.get(roleString);
		List<Widget>      foundWidgets 	= new ArrayList<>();
		
		// get widgets with the correct role
		for(Widget widget : state)
		{
			if(widget.get(Tags.Role, null) != null)
			{
				if(widget.get(Tags.Role) == role)
					widgets.add(widget);
			}
		}
		
		for (Tag<String> tag : tagList)
		{
			for(Widget widget : widgets)
			{
				if(widget.get(tag, null) != null && !foundWidgets.contains(widget))
					if(widget.get(tag).equals(string))
						foundWidgets.add(widget);
			}
		}
		return foundWidgets;
	}
	
	static Map<String, List<Tag<String>>> selectorStringToTags = Map.ofEntries(
			Map.entry("button", Stream.of(Tags.Title, WdTags.WebGenericTitle, WdTags.WebValue).collect(Collectors.toList())),
			Map.entry("input_text", Stream.of(Tags.Title, WdTags.WebName, WdTags.WebGenericTitle).collect(Collectors.toList())),
			Map.entry("static_text", Stream.of(Tags.Title, WdTags.WebTextContent, WdTags.WebGenericTitle).collect(Collectors.toList())),
			Map.entry("alert", Stream.of(Tags.Title, WdTags.WebGenericTitle).collect(Collectors.toList())),
			Map.entry("dropdown", Stream.of(Tags.Title, WdTags.WebGenericTitle).collect(Collectors.toList())),
			Map.entry("checkbox", Stream.of(Tags.Title, WdTags.WebGenericTitle).collect(Collectors.toList())),
			Map.entry("radio", Stream.of(Tags.Title, WdTags.WebGenericTitle).collect(Collectors.toList())),
			Map.entry("image", Stream.of(WdTags.WebAlt, Tags.Desc, WdTags.WebTitle, WdTags.WebGenericTitle).collect(Collectors.toList())),
			Map.entry("link", Stream.of(Tags.Title, WdTags.WebHref, WdTags.WebGenericTitle).collect(Collectors.toList())),
			Map.entry("label", Stream.of(Tags.Title, WdTags.WebGenericTitle).collect(Collectors.toList())),
			Map.entry("element", Stream.of(Tags.Title, WdTags.WebGenericTitle).collect(Collectors.toList()))
	);
	
	
	static Map<String, Role> elementToRole = Map.ofEntries(
		Map.entry("button", Roles.Button),
		Map.entry("input_text", Roles.Text),
//		Map.entry("static_text", Roles.Text),
		Map.entry("alert", Roles.Dialog),
		Map.entry("dropdown", Roles.ItemContainer)
//			Map.entry("checkbox", Roles.CheckBox),
//			Map.entry("radio", Roles.RadioButton),
//			Map.entry("image", Roles.Image),
//			Map.entry("link", Roles.Hyperlink),
//			Map.entry("label", Roles.Label),
//			Map.entry("element", Roles.Unknown) //??
														  );
	
	
	public static List<Widget> findWidget(State state, String roleString, String string)
	{
		List<Widget>	  widgets 		= new ArrayList<>();
		Role              role    		= elementToRole.get(roleString);
		List<Tag<String>> tagList 		= selectorStringToTags.get(roleString);
		List<Widget>      foundWidgets 	= new ArrayList<>();
		
		// get widgets with the correct role
		for(Widget widget : state)
		{
			if (widget.get(Tags.Role).equals(role))
				widgets.add(widget);
		}
		
		for (Tag<String> tag : tagList)
		{
			for(Widget widget : widgets)
			{
				if(widget.get(tag).equals(string))
					foundWidgets.add(widget);
			}
		}
		return foundWidgets;
	}
}

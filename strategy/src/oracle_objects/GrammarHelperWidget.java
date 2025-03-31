package oracle_objects;

import java.util.ArrayList;
import java.util.List;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Widget;

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
				if(widget.get(tag) instanceof Role) {
					widgetTagValues.add(widget.get(tag).toString());
				} else {
					widgetTagValues.add((String) widget.get(tag));
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
				if(widget.get(tag) instanceof Role) {
					tagValuePair = new Pair<>(tag.name(), widget.get(tag).toString());
				} else {
					tagValuePair = new Pair<>(tag.name(), (String) widget.get(tag));
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

}

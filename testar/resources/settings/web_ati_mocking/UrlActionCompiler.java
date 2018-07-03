package web_ati_mocking;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.Abstractor;
import org.fruit.alayer.Action;
import org.fruit.alayer.Finder;
import org.fruit.alayer.Position;
import org.fruit.alayer.StdAbstractor;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.WidgetPosition;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.KeyDown;
import org.fruit.alayer.actions.KeyUp;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.Type;
import org.fruit.alayer.actions.CompoundAction.Builder;
import org.fruit.alayer.devices.KBKeys;

public class UrlActionCompiler extends StdActionCompiler {
	private Abstractor abstractor;
	
	public UrlActionCompiler() {	
		this(new StdAbstractor()); 
		}

	public UrlActionCompiler(Abstractor abstractor) {
		this.abstractor = abstractor;
	}

	public Action clickTypeUrl(final Position position, final String text, int currentUrlLength) {
		Assert.notNull(position, text);
		// Go to the beginning and click there
		Builder builder = new CompoundAction.Builder()
			.add(leftClickAt(position), 1)
			.add(new KeyDown(KBKeys.VK_HOME), 1).add(new KeyUp(KBKeys.VK_HOME), 1);
		// Delete the current text
		for ( int i = 0; i < currentUrlLength + 10; i++) {
			builder.add(new KeyDown(KBKeys.VK_DELETE), 1).add(new KeyUp(KBKeys.VK_DELETE), 1);
		}
		builder.add(new Type(text), 1);
		builder.add(new KeyDown(KBKeys.VK_ENTER), 1);
		return builder.build();
	}

	public Action clickTypeUrl(Widget widget, String text) {
		return clickTypeUrl(widget, 0.5, 0.5, text);
	}
	
	public Action clickTypeUrl(Widget widget, double relX, double relY, String text) {
		String value = widget.get(Tags.ValuePattern);
		int currentUrlLength = value.length();
		Finder wf = abstractor.apply(widget);
		Action ret = clickTypeUrl(new WidgetPosition(wf, Tags.Shape, relX, relY, true), text, currentUrlLength);
		ret.set(Tags.Targets, Util.newArrayList(wf));
		ret.set(Tags.TargetID, widget.get(Tags.ConcreteID));
		return ret;
	}
}

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.Shape;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

public class WidgetElement {
	private ProtocolWebGWT protocol;
	private Widget widget;
	private int timesSelected = 0;
	

	public WidgetElement(ProtocolWebGWT protocol, Widget widget) {
		this.protocol = protocol;
		this.widget = widget;
	}

	public Widget getWidget() {
		return widget;
	}

	public String getConcreteId() {
		return widget.get(Tags.ConcreteID);
	}

	public String getParentId() {
		if (widget.parent() != null) {
			return widget.parent().get(Tags.ConcreteID, null);
		} else {
			return null;
		}
	}

	public String getTitle() {
		return widget.get(Tags.Title, "");
	}

	public Role getRole() {
		return widget.get(Tags.Role, Roles.Widget);
	}

	public Shape getShape() {
		return widget.get(Tags.Shape, null);
	}

	public boolean isClickableOrTypeable() {
		return widget.get(Enabled, true)
				&& !widget.get(Blocked, false)
				&& !protocol.blackListed(widget)
				&& (protocol.isClickable(widget) || protocol.isTypeable(widget));	
	}

	public void incrementSelected() {
		timesSelected++;
	}
	
	public int getTimesSelected() {
		return timesSelected;
	}
}
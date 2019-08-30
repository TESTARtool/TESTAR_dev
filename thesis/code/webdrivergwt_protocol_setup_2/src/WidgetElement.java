import org.fruit.alayer.Widget;

public class WidgetElement {
	private Widget widget;
	private int timesSelected = 0;
	

	public WidgetElement(Widget widget) {
		this.widget = widget;
	}

	public Widget getWidget() {
		return widget;
	}

	public void incrementSelected() {
		timesSelected++;
	}
	
	public int getTimesSelected() {
		return timesSelected;
	}
}
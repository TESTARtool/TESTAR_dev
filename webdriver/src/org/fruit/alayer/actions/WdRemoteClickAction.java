package org.fruit.alayer.actions;

import org.fruit.Assert;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.PositionException;
import org.fruit.alayer.webdriver.WdWidget;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class WdRemoteClickAction extends TaggableBase implements Action {

    protected WdWidget widget;

    private static final Pen LClickPen = Pen.newPen().setColor(Color.Green)
            .setFillPattern(FillPattern.Solid).setStrokeWidth(3).build();

    public static class ClickVisualizer implements Visualizer {
        private static final long serialVersionUID = -2006402344810634504L;
        
        private final double width, height;
        private final Pen pen;
        private final Rect rec;

        public ClickVisualizer(Rect rec, Pen pen, double width, double height){
            Assert.notNull(rec, pen);
            this.width = width;
            this.height = height;
            this.pen = pen;
            this.rec = rec;
        }

        public void run(State state, Canvas canvas, Pen pen) {
            Assert.notNull(state, canvas, pen);
            pen = Pen.merge(pen, this.pen);
            try {
                double mx = rec.x() + (rec.width() / 2.0);
                double my = rec.y() + (rec.height() / 2.0);
                canvas.ellipse(pen, mx - width * .5, my - height * .5, width, height);
            } catch (PositionException pe) {}
        }
    }

    public WdRemoteClickAction(WdWidget widget) {
        this.widget = widget;
        this.set(Tags.OriginWidget, widget);
        Role role = widget.get(Tags.Role, Roles.Widget);
        if (!role.equals(WdRoles.WdOPTION)) {
            this.set(Tags.Visualizer, new ClickVisualizer(widget.element.rect, LClickPen, 10, 10));
        }
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            RemoteWebElement remoteElement = widget.element.remoteWebElement;
            RemoteWebDriver d = (RemoteWebDriver)remoteElement.getWrappedDriver();
            d.executeScript("arguments[0].scrollIntoView(true)", remoteElement);
            org.fruit.Util.pause(0.1);
            remoteElement.click();
        }
        catch (Exception e) {
            System.out.println("ELEMENT " + widget.element.remoteWebElement.getId() + " IS STALE");
        }
    }

    @Override
    public String toShortString() {
        return "Remote click " + widget.element.remoteWebElement.getId();
    }

    @Override
    public String toParametersString() {
        return toShortString();
    }

    @Override
    public String toString(Role... discardParameters) {
        return toShortString();
    }
}

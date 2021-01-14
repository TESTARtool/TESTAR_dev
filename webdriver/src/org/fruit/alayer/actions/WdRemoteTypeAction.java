package org.fruit.alayer.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.s;
import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.PositionException;
import org.fruit.alayer.webdriver.WdWidget;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class WdRemoteTypeAction extends TaggableBase implements Action {

    protected WdWidget widget;
    protected CharSequence keys;
    protected static final Logger logger = LogManager.getLogger();

    private static final Pen TypePen = Pen.newPen().setColor(Color.Blue)
            .setFillPattern(FillPattern.None).setStrokeWidth(3).build();
    
    public static class TypeVisualizer implements Visualizer {

        private static final long serialVersionUID = 856304220974950751L;

        final String text;
        final Pen pen;
        final Rect rect;

        public TypeVisualizer(Rect rect, String text, Pen pen){
            Assert.notNull(rect, text, pen);
            this.text = text;
            this.pen = pen;
            this.rect = rect;
        }

        public void run(State state, Canvas cv, Pen pen) {
            Assert.notNull(state, cv, pen);
            pen = Pen.merge(pen, this.pen);
            try { 
                Pair<Double, Double> m = cv.textMetrics(pen, text);
                double mx = rect.x() + (rect.width() / 2.0);
                double my = rect.y() + (rect.height() / 2.0);
                
                cv.text(pen, mx - (m.left() / 2.0), my - (m.right() / 2.0), 0, text);
            } catch (PositionException pe) {}
        }
    }

    public WdRemoteTypeAction(WdWidget widget, CharSequence keys) {
        this.widget = widget;
        this.keys = keys;

        this.set(Tags.OriginWidget, widget);
        this.set(Tags.Visualizer, new TypeVisualizer(widget.get(WdTags.WebBoundingRectangle), keys.toString(), TypePen));
    }
                                                                                            
    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            RemoteWebElement remoteElement = widget.element.remoteWebElement;
            RemoteWebDriver d = (RemoteWebDriver)remoteElement.getWrappedDriver();
            d.executeScript("arguments[0].scrollIntoView(true)", remoteElement);
            remoteElement.clear();
            org.fruit.Util.pause(0.05);
            remoteElement.sendKeys(keys);
        }
        catch (Exception e) {
            logger.warn("Remote type action failed", e);
        }
    }

    @Override
    public String toShortString() {
        return "Remote type " + keys + " " + widget.element.remoteWebElement.getId();
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

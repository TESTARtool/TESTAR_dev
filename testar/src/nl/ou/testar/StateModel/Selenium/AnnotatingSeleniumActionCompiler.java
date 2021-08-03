package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.visualizers.EllipseVisualizer;
import org.fruit.alayer.visualizers.TrajectoryVisualizer;
import org.fruit.alayer.webdriver.WdWidget;

public class AnnotatingSeleniumActionCompiler extends SeleniumActionCompiler {

    public AnnotatingSeleniumActionCompiler(State state) {
        super(state);
    }

    @Override
    public Action leftClickAt(WdWidget w) {
        final Action result = super.leftClickAt(w);
        result.set(Tags.Desc, "Left Click at '" + w.get(Tags.Desc, "<no description>") + "'");
        result.set(Tags.OriginWidget, w);
        return result;
    }

    @Override
    public Action leftDoubleClickAt(WdWidget w) {
        final Action result = super.leftDoubleClickAt(w);
        result.set(Tags.Desc, "Double-Click at '" + w.get(Tags.Desc, "<no description>") + "'");
        result.set(Tags.OriginWidget, w);
        result.set(Tags.Role, ActionRoles.SeleniumDoubleClick);
        return result;
    }

    @Override
    public Action leftTripleClickAt(WdWidget w) {
        final Action result = super.leftTripleClickAt(w);
        result.set(Tags.Desc, "Triple-Click at '" + w.get(Tags.Desc, "<no description>") + "'");
//        result.set(Tags.Visualizer, new EllipseVisualizer(position, DoubleLClickPen, 30, 30));
        result.set(Tags.OriginWidget, w);
        return result;
    }

    @Override
    public Action dragFromTo(WdWidget from, WdWidget to) {
        final Action result = super.dragFromTo(from, to);
        result.set(Tags.Desc, "Drag '" + from.get(Tags.Desc, "<no description>") + "' To '" + to.get(Tags.Desc, "<no description>") + "'");
        result.set(Tags.OriginWidget, from);
        return result;
    }

    @Override
    public Action slideFromTo(Position from, Position to, WdWidget widget) {
        final Action result = super.slideFromTo(from, to, widget);
        result.set(Tags.Desc, "Slide '" + widget.get(Tags.Desc, "<no description>") + "' " + from.toString() + " To " + to.toString());
        result.set(Tags.OriginWidget, widget);
        return result;
    }

    @Override
    public Action clickTypeInto(WdWidget w, String text, boolean replaceText) {
        final Action result = super.clickTypeInto(w, text, replaceText);
        result.set(Tags.Desc, "Click '" + w.get(Tags.Desc, "<no description>") + "' and type '" + text + "' , replace: " + (replaceText ? "yes" : "no"));
        result.set(Tags.OriginWidget, w);
        return result;
    }

    @Override
    public Action pasteTextInto(WdWidget w, String text, boolean replaceText) {
        final Action result = super.pasteTextInto(w, text, replaceText);
        result.set(Tags.Desc, "Pasted to '" + w.get(Tags.Desc, "<no description>") + "' text '" + text + "' , replace: " + (replaceText ? "yes" : "no"));
        result.set(Tags.OriginWidget, w);
        return result;
    }
}
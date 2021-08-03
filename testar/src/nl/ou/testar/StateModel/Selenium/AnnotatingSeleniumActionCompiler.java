package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.visualizers.EllipseVisualizer;
import org.fruit.alayer.visualizers.TrajectoryVisualizer;
import org.fruit.alayer.webdriver.WdWidget;

public class AnnotatingSeleniumActionCompiler extends SeleniumActionCompiler {

    private static final Pen LClickPen = Pen.newPen().setColor(Color.Green)
            .setFillPattern(FillPattern.Solid).setStrokeWidth(3).build();
    private static final Pen DoubleLClickPen = Pen.newPen().setColor(Color.Green)
            .setFillPattern(FillPattern.None).setStrokeWidth(5).build();
    private static final Pen TripleLClickPen = Pen.newPen().setColor(Color.LimeGreen)
            .setFillPattern(FillPattern.None).setStrokeWidth(5).build();
    private static final Pen DragDropPen = Pen.newPen().setColor(Color.CornflowerBlue)
            .setFillPattern(FillPattern.None).setStrokeWidth(2).setStrokeCaps(StrokeCaps._Arrow).build();
    private static final Pen TypePen = Pen.newPen().setColor(Color.Blue)
            .setFillPattern(FillPattern.None).setStrokeWidth(3).build();

    private Abstractor abstractor;

    public AnnotatingSeleniumActionCompiler(State state) {
        super(state);
        abstractor = new StdAbstractor();
    }

    @Override
    public Action leftClickAt(WdWidget w) {
        final Action result = super.leftClickAt(w);
        result.set(Tags.Desc, "Left Click at '" + w.get(Tags.Desc, "<no description>") + "'");

        final Finder wf = abstractor.apply(w);
        final Position position = new WidgetPosition(wf, Tags.Shape, 0.5, 0.5, true);
        result.set(Tags.Visualizer, new EllipseVisualizer(position, LClickPen, 30, 30));

        result.set(Tags.OriginWidget, w);
        return result;
    }

    @Override
    public Action leftDoubleClickAt(WdWidget w) {
        final Action result = super.leftDoubleClickAt(w);
        result.set(Tags.Desc, "Double-Click at '" + w.get(Tags.Desc, "<no description>") + "'");

        final Finder wf = abstractor.apply(w);
        final Position position = new WidgetPosition(wf, Tags.Shape, 0.5, 0.5, true);
        result.set(Tags.Visualizer, new EllipseVisualizer(position, DoubleLClickPen, 30, 30));

        result.set(Tags.OriginWidget, w);
        result.set(Tags.Role, ActionRoles.SeleniumDoubleClick);
        return result;
    }

    @Override
    public Action leftTripleClickAt(WdWidget w) {
        final Action result = super.leftTripleClickAt(w);
        result.set(Tags.Desc, "Triple-Click at '" + w.get(Tags.Desc, "<no description>") + "'");

        final Finder wf = abstractor.apply(w);
        final Position position = new WidgetPosition(wf, Tags.Shape, 0.5, 0.5, true);
        result.set(Tags.Visualizer, new EllipseVisualizer(position, TripleLClickPen, 30, 30));

        result.set(Tags.OriginWidget, w);
        return result;
    }

    @Override
    public Action dragFromTo(WdWidget from, WdWidget to) {
        final Action result = super.dragFromTo(from, to);
        result.set(Tags.Desc, "Drag '" + from.get(Tags.Desc, "<no description>") + "' To '" + to.get(Tags.Desc, "<no description>") + "'");

        final Finder wf_from = abstractor.apply(from);
        final Finder wf_to = abstractor.apply(to);
        final Position pos_from = new WidgetPosition(wf_from, Tags.Shape, 0.5, 0.5, true);
        final Position pos_to = new WidgetPosition(wf_to, Tags.Shape, 0.5, 0.5, true);
        result.set(Tags.Visualizer, new TrajectoryVisualizer(DragDropPen, pos_from, new OrthogonalPosition(pos_from, pos_to, 0.2, 0), pos_to));

        result.set(Tags.OriginWidget, from);
        return result;
    }

    @Override
    public Action slideFromTo(Position from, Position to, WdWidget widget) {
        final Action result = super.slideFromTo(from, to, widget);
        result.set(Tags.Desc, "Slide '" + widget.get(Tags.Desc, "<no description>") + "' " + from.toString() + " To " + to.toString());

        final Finder wf = abstractor.apply(widget);
        final Position position = new WidgetPosition(wf, Tags.Shape, 0.5, 0.5, true);
        result.set(Tags.Visualizer, new TrajectoryVisualizer(DragDropPen, position, new OrthogonalPosition(position, to, 0.2, 0), to));

        result.set(Tags.OriginWidget, widget);
        return result;
    }

    @Override
    public Action clickTypeInto(WdWidget w, String text, boolean replaceText) {
        final Action result = super.clickTypeInto(w, text, replaceText);
        result.set(Tags.Desc, "Click '" + w.get(Tags.Desc, "<no description>") + "' and type '" + text + "' , replace: " + (replaceText ? "yes" : "no"));

        final Finder wf = abstractor.apply(w);
        final Position position = new WidgetPosition(wf, Tags.Shape, 0.5, 0.5, true);
        result.set(Tags.Visualizer, new EllipseVisualizer(position, TypePen, 30, 30));

        result.set(Tags.OriginWidget, w);
        return result;
    }

    @Override
    public Action pasteTextInto(WdWidget w, String text, boolean replaceText) {
        final Action result = super.pasteTextInto(w, text, replaceText);
        result.set(Tags.Desc, "Pasted to '" + w.get(Tags.Desc, "<no description>") + "' text '" + text + "' , replace: " + (replaceText ? "yes" : "no"));

        final Finder wf = abstractor.apply(w);
        final Position position = new WidgetPosition(wf, Tags.Shape, 0.5, 0.5, true);
        result.set(Tags.Visualizer, new EllipseVisualizer(position, TypePen, 30, 30));

        result.set(Tags.OriginWidget, w);
        return result;
    }
}
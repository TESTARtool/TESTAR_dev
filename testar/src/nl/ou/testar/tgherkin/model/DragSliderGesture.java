package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.Set;

import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Abstractor;
import org.fruit.alayer.Action;
import org.fruit.alayer.Finder;
import org.fruit.alayer.Point;
import org.fruit.alayer.StdAbstractor;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;


/**
 * Class responsible for handling slider drags.
 *
 */
public class DragSliderGesture extends Gesture {

    /**
     * DragSliderGesture constructor.
     * @param parameterBase container for parameters
     */
    public DragSliderGesture(ParameterBase parameterBase) {
      super(parameterBase);
    }


    @Override
    public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
      return super.gesturePossible(widget, proxy, dataTable) && widget.scrollDrags(Gesture.SCROLL_ARROW_SIZE, Gesture.SCROLL_THICKNESS) != null;
    }

    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    Set<Action> actions = new HashSet<Action>();
      StdActionCompiler ac = new AnnotatingActionCompiler();
    Drag[] drags = widget.scrollDrags(SCROLL_ARROW_SIZE,SCROLL_THICKNESS);
    if (drags != null) {
      for (Drag drag: drags) {
        Action action = ac.dragFromTo(
            new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
            new AbsolutePosition(Point.from(drag.getToX(),drag.getToY()))
          );
        // add action target
        action.set(Tags.TargetID, widget.get(Tags.ConcreteID));
        Abstractor abstractor = new StdAbstractor();
        Finder wf = abstractor.apply(widget);
        action.set(Tags.Targets, Util.newArrayList(wf));
        actions.add(action);
      }
    }
      return actions;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
       result.append("dragSlider");
       result.append(getParameterBase().toString());
      return result.toString();
    }
}

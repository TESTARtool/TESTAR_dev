package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.Abstractor;
import org.fruit.alayer.Action;
import org.fruit.alayer.Finder;
import org.fruit.alayer.StdAbstractor;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;


/**
 * Class responsible for handling mouse moves.
 *
 */
public class MouseMoveGesture extends Gesture {

    /**
     * MouseMoveGesture constructor.
     * @param parameterBase container for parameters
     */
    public MouseMoveGesture(ParameterBase parameterBase) {
      super(parameterBase);
    }

    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    Set<Action> actions = new HashSet<Action>();
      StdActionCompiler ac = new AnnotatingActionCompiler();
    Action action = ac.mouseMove(widget);
    // add action target
      action.set(Tags.TargetID, widget.get(Tags.ConcreteID));
    Abstractor abstractor = new StdAbstractor();
    Finder wf = abstractor.apply(widget);
    action.set(Tags.Targets, Util.newArrayList(wf));
    actions.add(action);
      return actions;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
       result.append("mouseMove");
       result.append(getParameterBase().toString());
      return result.toString();
    }
}

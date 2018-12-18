package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;

/**
 * Class responsible for handling right clicks.
 *
 */
public class RightClickGesture extends Gesture {

    /**
     * RightClickGesture constructor.
     * @param parameterBase container for parameters
     */
    public RightClickGesture(ParameterBase parameterBase) {
      super(parameterBase);
    }

    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    Set<Action> actions = new HashSet<Action>();
      StdActionCompiler ac = new AnnotatingActionCompiler();
      actions.add(ac.rightClickAt(widget));
      return actions;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
       result.append("rightClick");
       result.append(getParameterBase().toString());
      return result.toString();
    }
}

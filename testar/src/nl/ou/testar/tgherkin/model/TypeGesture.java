package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.Set;
import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;

/**
 * Class responsible for handling typing in text.
 *
 */
public class TypeGesture extends Gesture {

  /**
   * TypeGesture constructor.
   * @param parameterBase container for parameters
   */
  public TypeGesture(ParameterBase parameterBase) {
    super(parameterBase);
  }

  @Override
  public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    return proxy.isTypeable(widget);
  }

  @Override
  public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    Set<Action> actions = new HashSet<Action>();
    StdActionCompiler ac = new AnnotatingActionCompiler();
    if (getParameterBase().size() > 0) {
      String text = getParameterBase().get(Parameters.TEXT, dataTable);
      // type action does not allow typing of an empty string
      if ("".equals(text)) {
        actions.add(ac.clickTypeInto(widget, "''", true));
      } else {
        actions.add(
            ac.clickTypeInto(widget, getParameterBase().get(Parameters.TEXT, dataTable), true));
      }
    } else {
      // no arguments: generate random text
      actions.add(ac.clickTypeInto(widget, proxy.getRandomText(widget), true));
    }
    return actions;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("type");
    result.append(getParameterBase().toString());
    return result.toString();
  }
}

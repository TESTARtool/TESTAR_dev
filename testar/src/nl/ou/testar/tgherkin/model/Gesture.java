package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;

/**
 * Abstract class that represents a gesture.
 *
 */
public abstract class Gesture {

  /**
   * Scroll arrow size.
   */
  public static final double SCROLL_ARROW_SIZE = 36;

  /**
   * Scroll thickness.
   */
  public static final double SCROLL_THICKNESS = 16;

  private final ParameterBase parameterBase;

    /**
     * Gesture constructor.
     * @param parameterBase container for parameters
     */
    public Gesture(ParameterBase parameterBase) {
      Assert.notNull(parameterBase);
      this.parameterBase = parameterBase;
    }

  /**
     * Retrieve parameter base.
     * @return parameter base
     */
    public ParameterBase getParameterBase() {
        return parameterBase;
    }

  /**
   * Retrieve whether gesture is possible on a given widget.
   * @param widget to be assessed widget
   * @param proxy document protocol proxy
   * @param dataTable data table contained in the examples section of a scenario outline
   * @return true if gesture is possible on widget, otherwise false
   */
  public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    return proxy.isUnfiltered(widget);
  }

  /**
     * Retrieve actions.
     * @param widget widget for which actions should be derived
   * @param proxy document protocol proxy
   * @param dataTable data table contained in the examples section of a scenario outline
     * @return set of actions
     */
    public abstract Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable);

  /**
     * Check gesture.
     * @param dataTable data table contained in the examples section of a scenario outline
     * @return list of error descriptions, empty list if no errors exist
     */
  public List<String> check(DataTable dataTable) {
    List<String> list = new ArrayList<String>();
    for (String placeholder: getParameterBase().getPlaceholders()) {
      if (dataTable == null) {
        list.add(getClass().getSimpleName() + " validation error - no data table found for string placeholder: " + placeholder + System.getProperty("line.separator"));
      } else {
        // check whether the placeholder is a column name of the data table
        if (!dataTable.isColumnName(placeholder)) {
          list.add(getClass().getSimpleName() + " validation error - invalid parameter placeholder: " + placeholder + "\n");
        }
      }
    }
    return list;
  }

    @Override
    public abstract String toString();

    /*
     * Determine whether this Gesture instance is equal to the passed object.
     * @return true if this Gesture instance is equal to the passed object, otherwise false.
     */
    @Override
    public boolean equals(Object object) {
      if (object == null) {
        return false;
      }
      if (getClass() != object.getClass()) {
        return false;
      }
      return true;
    }

    /*
     * Get hashcode.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
      }

}

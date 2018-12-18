package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of a when clause.
 */
public class WhenClause {

  private final List<ConditionalGesture> conditionalGestures;
  private final WidgetTreeCondition nopCondition;

  /**
     * WhenClause constructor.
     * @param conditionalGestures list of conditional gestures that defines the When clause
     * @param nopCondition widget tree condition that defines derivation of a NOP (no operation) action
     */
    public WhenClause(List<ConditionalGesture> conditionalGestures, WidgetTreeCondition nopCondition) {
        this.conditionalGestures = Collections.unmodifiableList(conditionalGestures);
        this.nopCondition = nopCondition;
    }

  /**
   * Retrieve conditional gestures.
   * @return list of conditional gestures
   */
  public List<ConditionalGesture> getConditionalGestures() {
    return conditionalGestures;
  }

  /**
   * Retrieve NOP (no operation) condition.
   * @return NOP (no operation) condition
   */
  public WidgetTreeCondition getNOPCondition() {
    return nopCondition;
  }

  /**
     * Check.
     * @param dataTable data table contained in the examples section of a scenario outline
     * @return list of error descriptions
     */
  public List<String> check(DataTable dataTable) {
    List<String> list = new ArrayList<String>();
       for (ConditionalGesture conditionalGesture: getConditionalGestures()) {
      list.addAll(conditionalGesture.check(dataTable));
       }
    if (getNOPCondition() != null) {
      list.addAll(getNOPCondition().check(dataTable));
    }
    return list;
  }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
      if (getConditionalGestures().size() > 0) {
        result.append("When ");
      }
       for (ConditionalGesture conditionalGesture: getConditionalGestures()) {
         result.append(conditionalGesture.toString());
       }
      if (getNOPCondition() != null) {
        result.append("NOP ");
        result.append(getNOPCondition().toString());
      }
      return result.toString();
    }

}

package nl.ou.testar.tgherkin.model;

import java.util.List;

/**
 * Class that represents a Tgherkin background.
 *
 */
public class Background extends ScenarioDefinition {

    /**
     * Background constructor.
     * @param title summary description
     * @param narrative detailed description
     * @param selection list of conditional gestures that defines a filter on the set of derivable gestures
     * @param oracle widget tree condition that serves as an oracle verdict
     * @param steps list of consecutive steps
     */
  public Background(String title, String narrative, List<ConditionalGesture> selection, WidgetTreeCondition oracle, List<Step> steps) {
        super(title, narrative, selection, oracle, steps);
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
      // keyword
      result.append(getClass().getSimpleName());
      result.append(":");
      if (getTitle() != null) {
        result.append(getTitle());
      }
    result.append(System.getProperty("line.separator"));
      if (getNarrative() != null) {
        result.append(getNarrative());
        result.append(System.getProperty("line.separator"));
      }
      if (getSelection().size() > 0) {
        result.append("Selection:");
      }
       for (ConditionalGesture conditionalGesture: getSelection()) {
         result.append(conditionalGesture.toString());
       }
      if (getOracle() != null) {
        result.append("Oracle:");
        result.append(getOracle().toString());
      }
      for (Step step: getSteps()) {
        result.append(step.toString());
      }
      return result.toString();
    }

}

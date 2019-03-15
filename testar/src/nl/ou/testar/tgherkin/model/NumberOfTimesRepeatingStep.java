package nl.ou.testar.tgherkin.model;

import java.util.List;
import java.util.Random;

/**
 * Step that repeats itself a fixed or random number of times.
 */
public class NumberOfTimesRepeatingStep extends RepeatingStep {

  private final int fromRange;
  private final int toRange;
  private int targetedActions;

  /**
     * NumerOfTimesRepeatingStep constructor.
     * @param title summary description
     * @param fromRange from range that defines the minimum number of iterations
     * @param toRange to range that defines the maximum number of iterations
     * @param givenCondition widget tree condition that defines the Given clause
     * @param whenClause definition of the When clause
     * @param thenCondition widget tree condition that defines the Then clause
     */
    public NumberOfTimesRepeatingStep(String title, int fromRange, int toRange, WidgetTreeCondition givenCondition, WhenClause whenClause, WidgetTreeCondition thenCondition) {
        super(title, givenCondition, whenClause, thenCondition);
      this.fromRange = fromRange;
      this.toRange = toRange;
    }

    /**
     * Retrieve from range.
     * @return from range
     */
    public int getFromRange() {
    return fromRange;
  }

    /**
     * Retrieve to range.
     * @return to range
     */
  public int getToRange() {
    return toRange;
  }

  @Override
  public List<String> check(DataTable dataTable) {
    List<String> list = super.check(dataTable);
    if (fromRange > toRange) {
      list.add("Validation error - invalid range for step " + getTitle() + ": from range " + fromRange + " greater then To range " + toRange + "\n");
    }
    return list;
  }

  @Override
  public void beginSequence() {
    super.beginSequence();
    if (fromRange == toRange) {
      targetedActions = fromRange;
    } else {
      // pick a random number within the range
      Random random = new Random();
      targetedActions = toRange - random.nextInt(toRange - fromRange + 1);
    }
  }

  @Override
    protected boolean hasNextAction(ProtocolProxy proxy, DataTable dataTable) {
      return getCurrentAction() < targetedActions || isRetryMode();
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
      // keyword
      result.append("Step:");
      if (getTitle() != null) {
        result.append(getTitle());
      }
      result.append(System.getProperty("line.separator"));
    result.append("Range " + getFromRange() + " "  + getToRange());
      result.append(System.getProperty("line.separator"));
      if (getGivenCondition() != null) {
        result.append("Given ");
        result.append(getGivenCondition().toString());
      }
      result.append(getWhenClause().toString());
      if (getThenCondition() != null) {
        result.append("Then ");
        result.append(getThenCondition().toString());
      }
      return result.toString();
    }

}

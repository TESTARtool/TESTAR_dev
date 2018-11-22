package nl.ou.testar.tgherkin.model;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.ou.testar.tgherkin.protocol.Report;
import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;

/**
 * Representation of a Tgherkin feature.
 *
 */
public class Feature {

  private final List<Tag> tags;
  private final String title;
  private final String narrative;
  private final List<ConditionalGesture> selection; 
  private final WidgetTreeCondition oracle; 
  private final Background background;
  private final List<ScenarioDefinition> scenarioDefinitions;
  private int index;
  
  private boolean backgroundRun;

  /**
   * Feature constructor.
   * @param tags list of tags
   * @param title summary description
   * @param narrative detailed description
   * @param selection list of conditional gestures that defines a filter on the set 
   *     of derivable gestures
   * @param oracle widget tree condition that serves as an oracle verdict
   * @param background background that will be executed before execution of each scenario
   * @param scenarioDefinitions list of scenario definitions 
   */
  public Feature(List<Tag> tags, String title, String narrative, 
      List<ConditionalGesture> selection, WidgetTreeCondition oracle, 
      Background background, List<ScenarioDefinition> scenarioDefinitions) {
    Assert.notNull(tags);
    Assert.notNull(title);
    Assert.notNull(selection);
    Assert.notNull(scenarioDefinitions);
    this.tags = Collections.unmodifiableList(tags);
    this.title = title;
    this.narrative = narrative;
    this.selection = Collections.unmodifiableList(selection);
    this.oracle = oracle;
    this.background = background; 
    this.scenarioDefinitions = Collections.unmodifiableList(scenarioDefinitions);
    index = -1;
  }

  /**
   * Retrieve tags.
   * @return list of tags
   */
  public List<Tag> getTags() {
    return tags;
  }

  /**
   * Retrieve title.
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Retrieve narrative.
   * @return narrative
   */
  public String getNarrative() {
    return narrative;
  }

  /**
   * Retrieve selection.
   * @return list of conditional gestures
   */
  public List<ConditionalGesture> getSelection() {
    return selection;
  }

  /**
   * Retrieve oracle.
   * @return widget tree condition
   */
  public WidgetTreeCondition getOracle() {
    return oracle;
  }

  /**
   * Retrieve background.
   * @return background
   */
  public Background getBackground() {
    return background;
  }

  /**
   * Retrieve scenario definitions.
   * @return list of scenario definitions 
   */
  public List<ScenarioDefinition> getScenarioDefinitions() {
    return scenarioDefinitions;
  }

  /**
   * Check whether more actions exist.
   * @param proxy document protocol proxy
   * @return true if more actions exist, otherwise false
   */
  public boolean moreActions(ProtocolProxy proxy) {
    if (backgroundRun && background.moreActions(proxy)) {
      return true;
    }
    return currentScenarioDefinition().moreActions(proxy);
  }
  
  /**
   * Check whether more sequences exist.
   * @return true if more sequences exist, otherwise false
   */
  public boolean moreSequences() {
    return hasNextScenarioDefinition() || currentScenarioDefinition().moreSequences();
  }

  /**
   * Begin sequence.
   */
  public void beginSequence() {
    if (background != null) {
      backgroundRun = true;
      background.beginSequence();
    } else {
      backgroundRun = false;
    }
    if (currentScenarioDefinition() != null && currentScenarioDefinition().moreSequences()) {
      // scenario outline: each table row instance is a new sequence
      currentScenarioDefinition().beginSequence();
    } else {
      nextScenarioDefinition().beginSequence();
    }
  }

  /**    
   * Evaluate given condition.
   * @param proxy document protocol proxy
   * @return true if condition is applicable, otherwise false 
   */
  public boolean evaluateGivenCondition(ProtocolProxy proxy) {
    Report.appendReportDetail(Report.StringColumn.FEATURE,getTitle());
    Report.appendReportDetail(Report.StringColumn.SCENARIO,currentScenarioDefinition().getTitle());
    if (backgroundRun) {
      if (background.moreActions(proxy)) {
        Report.appendReportDetail(Report.StringColumn.TYPE,background.getClass().getSimpleName());
        return background.evaluateGivenCondition(proxy);
      }  
      backgroundRun = false;
    }
    Report.appendReportDetail(Report.StringColumn.TYPE,
        currentScenarioDefinition().getClass().getSimpleName());
    return currentScenarioDefinition().evaluateGivenCondition(proxy);
  }
  
  /**    
   * Evaluate when condition.
   * @param proxy document protocol proxy
   * @return set of derived actions, empty set if no actions were derived
   */
  public Set<Action> evaluateWhenCondition(ProtocolProxy proxy) {
    Map<Widget,List<Gesture>> map = new HashMap<Widget, List<Gesture>>();
    List<Gesture> list;
    // for gestures only look at top widgets
    for (Widget widget : proxy.getTopWidgets(proxy.getState())) {
      // always test whether enabled and not blocked
      if (widget.get(Enabled, true) && !widget.get(Blocked, false)) {
        list = new ArrayList<Gesture>();
        if (selection.size() == 0) {
          // no selection defined: all possible gestures are in scope
          Gesture gesture = new AnyGesture(new ParameterBase());
          ConditionalGesture conditionalGesture = new ConditionalGesture(null, gesture); 
          if (conditionalGesture.isCandidate(proxy, widget, null)) {
            list.add(gesture);
            map.put(widget, list);
          }
        } else {
          for (ConditionalGesture conditionalGesture: selection) {
            if (conditionalGesture.isCandidate(proxy, widget, null)) {
              list.add(conditionalGesture.getGesture());
            }
          }
          if (list.size() > 0) {
            map.put(widget, list);
          }
        }
      }
    }
            
    if (backgroundRun) {
      return background.evaluateWhenCondition(proxy, map);
    }
    
    Set<Action> testSet = new HashSet<Action>();
    ScenarioDefinition sd = currentScenarioDefinition();
    testSet = sd.evaluateWhenCondition(proxy, map);
    return testSet;
  }  

  /**    
   * Get verdict.
   * @param proxy document protocol proxy
   * @return oracle verdict, which determines whether the state is erroneous and why 
   */
  public Verdict getVerdict(ProtocolProxy proxy) {
    // feature level
    if (oracle != null && !oracle.evaluate(proxy, null)) {
      if (backgroundRun) {
        background.setFailed();
      } else {
        currentScenarioDefinition().setFailed();
      }
      Report.appendReportDetail(Report.BooleanColumn.THEN,false);
      return new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin feature oracle failure!");
    }
    // scenario level
    if (backgroundRun) {
      return background.getVerdict(proxy);
    }
    return currentScenarioDefinition().getVerdict(proxy);
  }
  
  /**
     * Retrieve whether current action resulted in a failure.
     * @return true if current action failed, otherwise false 
     */
  public boolean hasFailed() {
    if (backgroundRun) {
      return background.hasFailed();
    }
    if (currentScenarioDefinition() != null) {
      return currentScenarioDefinition().hasFailed();
    }
    return false;
  }

  /**
   * Reset feature.
   */
  public void reset() {
    index = -1;
    if (getBackground() != null) {
      getBackground().reset();
    }
    for (ScenarioDefinition scenarioDefinition : getScenarioDefinitions()) {
      scenarioDefinition.reset(); 
    }
  }

  /**
     * Check.
     * @return list of error descriptions, empty list if no errors exist
     */
  public List<String> check() {
    List<String> list = new ArrayList<String>();
    for (ConditionalGesture conditionalGesture: getSelection()) {
      list.addAll(conditionalGesture.check(null));
    }
    if (getOracle() != null) {
      list.addAll(getOracle().check(null));
    }
    if (getBackground() != null) {
      list.addAll(getBackground().check());
    }
    for (ScenarioDefinition scenarioDefinition : getScenarioDefinitions()) {
      list.addAll(scenarioDefinition.check());
    }
    return list;
  }
  
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (Tag tag : getTags()) {
      result.append(tag.toString());
      result.append(System.getProperty("line.separator"));
    }
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
    for (ConditionalGesture conditionalGesture : getSelection()) {
      result.append(conditionalGesture.toString());
    }
    if (getOracle() != null) {
      result.append("Oracle:");
      result.append(getOracle().toString());
    }      
    if (getBackground() != null) {
      result.append(getBackground().toString());
      result.append(System.getProperty("line.separator"));
    }
    for (ScenarioDefinition scenarioDefinition : getScenarioDefinitions()) {
      result.append(scenarioDefinition.toString());
    }
    return result.toString();
  }
    
  private boolean hasNextScenarioDefinition() {
    return index + 1 < scenarioDefinitions.size();
  }

  private ScenarioDefinition nextScenarioDefinition() {
    index++; 
    return scenarioDefinitions.get(index);
  }
  
  private ScenarioDefinition currentScenarioDefinition() {
    if (index < 0 || index >= scenarioDefinitions.size()) {
      return null;
    }
    return scenarioDefinitions.get(index);
  }  
}

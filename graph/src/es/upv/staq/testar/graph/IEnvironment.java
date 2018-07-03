/***************************************************************************************************
*
* Copyright (c) 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/


package es.upv.staq.testar.graph;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;


/**
 * Graph environment.
 *
 * @author Urko Rueda Molina (alias: urueda)
 */
public interface IEnvironment {

  /**
   * Sets the starting graph node.
   *
   * @param startNode The start node.
   */
  public void setStartingNode (IGraphState startNode);

  /**
   * Retrieves next movement.
   *
   * @return The movement.
   */
  public Movement getMovement ();

  /**
   * Retrieves the corresponding graph state.
   *
   * @param state A state.
   * @return The corresponding graph state.
   */
  public IGraphState get (State state);

  /**
   * Retrieves an existing graph state.
   *
   * @param stateID The state ID.
   * @return The graph state.
   */
  public IGraphState getState (String stateID);

  /**
   * Retrieves the corresponding graph action.
   *
   * @param action An action.
   * @return The corresponding graph action.
   */
  public IGraphAction get (Action action);

  /**
   * Retrieves an existing graph action.
   *
   * @param actionID The action ID.
   * @return The graph action.
   */
  public IGraphAction getAction (String actionID);

  /**
   * Populates the graph with movement information.
   *
   * @param fromState Graph state before action execution.
   * @param action    Executed action, abstracted for graph.
   * @param toState   Graph state after action execution.
   */
  public void populateEnvironment (IGraphState fromState,
                                   IGraphAction action, IGraphState toState);

  /**
   * Retrieves information about an action set for a SUT state.
   *
   * @param state   A SUT state.
   * @param actions The actions available for the state.
   */
  public void notifyEnvironment (State state, Set<Action> actions);

  /**
   * Retrieves the graph states.
   * @return graph state
   */
  public Collection<IGraphState> getGraphStates ();

  /**
   * Retrieves the graph actions.
   * @return graph actions
   */
  public Collection<IGraphAction> getGraphActions ();

  /**
   * Retrieves the source state of an action.
   *
   * @param action A graph action.
   * @return The source graph state or 'null' if the action is not at graph.
   */
  public IGraphState getSourceState (IGraphAction action);

  /**
   * Retrieves the target states of an action.
   *
   * @param action A graph action.
   * @return The target graph states or 'null' if the action is not at graph.
   */
  public IGraphState[] getTargetStates (IGraphAction action);

  /**
   * Retrieves the incoming edges to a graph state.
   *
   * @param state A graph state.
   * @return The incoming edges.
   */
  public Collection<GraphEdge> getIncomingActions (IGraphState state);

  /**
   * Retrieves the outgoing edges from a graph state.
   *
   * @param state A graph state.
   * @return The outgoing edges.
   */
  public Collection<GraphEdge> getOutgoingActions (IGraphState state);

  /**
   * Computes the number of times an abstract action was executed.
   *
   * @param graphState  A graph state.
   * @param graphAction A state action.
   * @return The count.
   */
  public int getAbstractExecutionCount (IGraphState graphState, IGraphAction graphAction);

  /**
   * Gets the walked count for a graph action/edge.
   *
   * @param action The graph action.
   * @return [0] = The number of times the state' action was walked in the graph.
   * [1] = The number of times the action type was walked in the graph.
   */
  public int[] getWalkedCount (IGraphAction action);

  /**
   * Returns the number of left clicks in a state.
   *
   * @param state A state.
   * @return The number of left clicks.
   */
  public int getLeftClicks (IGraphState state);

  /**
   * Returns the number of types in a state.
   *
   * @param state A state.
   * @return The number of types.
   */
  public int getTypesInto (IGraphState state);

  /**
   * Retrieves all the scanned actions for a SUT state by decreased reward.
   * @param state The SUT graph state.
   * @return Actions osrted by decreased reward.
   */
  //public IGraphAction[] getSortedStateActionsByDecReward(IGraphState state);

  /**
   * Retrieves a list of incrementally ordered actions by their test execution order.
   * Examples:
   * * Full list: fromOder = 1, toOrder = executed_actions_number
   * * Sublist: fromOder = 1 &lt; X &lt; executed_actions_number, toOrder = X &lt; Y &lt; executed_actions_number.
   * * Single element: 1 &lt;= fromOrder = toOrder &lt;= executed_actions_number.
   *
   * @param fromOrder Retrieves the list from:  1 .. executed_actions_number.
   * @param toOrder   Retrieves the list to: fromOrder .. executed_actions_number.
   * @return The list of actions (by concrete ID).
   */
  public GraphEdge[] getSortedActionsByOrder (int fromOrder, int toOrder);

  /**
   * Retrieves a list of incrementally ordered actions by their test execution order.
   *
   * @return A forward iterator.
   */
  public Iterator<GraphEdge> getForwardActions ();

  /**
   * Retrieves a list of decreasing ordered actions by their test execution order.
   *
   * @return A backward iterator.
   */
  public ListIterator<GraphEdge> getBackwardActions ();

  /**
   * Checks whether the graph already contains a state.
   *
   * @param gs A graph state.
   * @return 'true' if contained, 'false' otherwise.
   */
  public boolean stateAtGraph (IGraphState gs);

  /**
   * Checks whether the graph already contains an action.
   *
   * @param ga A graph action.
   * @return 'true' if contained, 'false' otherwise.
   */
  public boolean actionAtGraph (IGraphAction ga);

  /**
   * Retrieves graph states grouped by clusters.
   *
   * @return Clusters of related UI states (abstract_R -> Set<concrete>).
   */
  public HashMap<String, Set<String>> getGraphStateClusters ();

  /**
   * Retrieves graph actions grouped by clusters.
   *
   * @return Clusters of related UI actions.
   */
  public HashMap<String, Set<String>> getGraphActionClusters ();

  /**
   * Finish the graph environment with ending test sequence.
   *
   * @param walkStatus   Test verdict: 'true' test OK, 'false' test FAIL.
   * @param lastState    Last grah state.
   * @param lastAction   Last graph action.
   * @param walkEndState SUT state after executing last action from the last state.
   */
  public void finishGraph (boolean walkStatus, IGraphState lastState, IGraphAction lastAction, State walkEndState);

  /**
   * Retrieves coverage metrics.
   *
   * @return Minimum and maximum coverage for all states.
   */
  public CoverageMetrics getCoverageMetrics ();

  /**
   * Retrieves graph resuming metrics.
   *
   * @return Known states, revisited states and new states.
   */
  public int[] getGraphResumingMetrics ();

  /**
   * Retrieves data for the exploration curve.
   *
   * @return list of ResumingMetrics
   */
  public List<ResumingMetrics> getExplorationCurve ();

  /**
   * Gets the last sample from the GUI exploration curve.
   *
   * @return A text representation of the sample.
   */
  public String getExplorationCurveSample ();

  /**
   * Gest the last sample coverage from known UI space.
   *
   * @return The coverage as %.
   */
  public int getExplorationCurveSampleCvg ();

  /**
   * Gets the last sample known UI space scale.
   *
   * @return The known UI space scale.
   */
  public int getExplorationCurveSampleScale ();

  /**
   * Converts the K value of KCVG to a short string.
   *
   * @param k Known UI space (total numbers of discovered UI actions from all discovered UI states).
   * @return A short string representation with 3 chars.
   */
  public String convertKCVG (int k);

  /**
   * Retrieves the current longest path in the GUI explored space.
   *
   * @return A text representation of the list of states in the longest path.
   */
  public String getLongestPath ();

  /**
   * Retrieves the current longest path length in the GUI explored space.
   *
   * @return The length of the longest path.
   */
  public int getLongestPathLength ();

  /**
   * Retrieves a path of states between two graph states.
   *
   * @param from Starting graph state.
   * @param to   Ending graph state.
   * @return The list of ordered states between states or 'null' if no path exists.
   */
  public List<IGraphState> getPath (IGraphState from, IGraphState to);

  /**
   * Gets the previous state from the execution.
   *
   * @param graphState A graph state.
   * @return The previous state.
   */
  public IGraphState getPrevious (IGraphState graphState);

  /**
   * Retrieves the N ancestors of a state.
   *
   * @param graphState The graph state.
   * @param n          The number N of ancestors to retrieve.
   * @return The list of N ancestors.
   */
  public List<IGraphState> getAncestors (IGraphState graphState, int n);

  /**
   * Gets the execution number of a state' action.
   *
   * @param graphAction The graph action executed at a state.
   * @return The number of times the action was executed at a state.
   */
  public int getExecutionNumber (IGraphAction graphAction);

  /**
   * Retrieves the number of different actions that were executed in a state.
   *
   * @param graphState The graph state.
   * @return The number of different actions executed at the state.
   */
  public int getExecutedActionNumber (IGraphState graphState);

  /**
   * Retrieves the number of actions that were not executed in a state.
   * These actions were discovered, but never executed.
   *
   * @param graphState The graph state.
   * @return The number of unexecuted actions in the state.
   */
  public int getUnexecutedActionNumber (IGraphState graphState);

  /**
   * Loads a graph from xml.
   *
   * @param xmlPath The xml file path.
   * @return Number of graph movements from the loaded graph.
   */
  public int loadFromXML (String xmlPath);

  /**
   * Get the test report.
   *
   * @return null or: [0] = clusters, [1] = test table,
   *                  [2] = exploration curve, [3] = UI exploration data
   */
  public String[] getReport (int firstSequenceActionNumber);

  class ResumingMetrics {
    // states number
    private int stateNumber;
    // actions number
    private int actionNumber;
    // abstract states number
    private int abstractStateNumber;
    // abstract actions number
    private int abstractActionNumber;
    // unexplored (known) actions number
    private int unexploredActionNumber;
    private int longesthPath;
    private int minCvg;
    private int maxCvg;
    // KCVG - CVG value (coverage of known UI space)
    private int coverageUIspace;
    // KCVG - K value (known UI space scale)
    private int spaceScale;

    public ResumingMetrics (int stateNumber,
                            int actionNumber,
                            int abstractStateNumber,
                            int abstractActionNumber,
                            int unexploredActionNumber,
                            int longesthPath,
                            int minCvg,
                            int maxCvg,
                            int coverageUIspace,
                            int spaceScale) {
      this.stateNumber = stateNumber;
      this.actionNumber = actionNumber;
      this.abstractStateNumber = abstractStateNumber;
      this.abstractActionNumber = abstractActionNumber;
      this.unexploredActionNumber = unexploredActionNumber;
      this.longesthPath = longesthPath;
      this.minCvg = minCvg;
      this.maxCvg = maxCvg;
      this.coverageUIspace = coverageUIspace;
      this.spaceScale = spaceScale;
    }

    public int getStateNumber () {
      return stateNumber;
    }

    public int getActionNumber () {
      return actionNumber;
    }

    public int getAbstractStateNumber () {
      return abstractStateNumber;
    }

    public int getAbstractActionNumber () {
      return abstractActionNumber;
    }

    public int getUnexploredActionNumber () {
      return unexploredActionNumber;
    }

    public int getLongesthPath () {
      return longesthPath;
    }

    public int getMinCvg () {
      return minCvg;
    }

    public int getMaxCvg () {
      return maxCvg;
    }

    public int getCoverageUIspace () {
      return coverageUIspace;
    }

    public int getSpaceScale () {
      return spaceScale;
    }

    public String getExplorationCurveString () {
      return String.format("ExplorationCurve (unique states/actions, " 
    	+ "abstract states/actions, unexplored_count): "
    	+ "%d, %d, %d, %d; %d, %d, %d, %d",
          stateNumber, actionNumber, abstractStateNumber, abstractActionNumber,
          unexploredActionNumber, longesthPath, minCvg, maxCvg);
    }
  }

  class CoverageMetrics {
    private double minCoverage;
    private double maxCoverage;
    private double knownSpaceCvgs;
    private double knownSpaceScale;
    private double totalKnownUnexploredActions;

    public CoverageMetrics (double minCoverage,
                            double maxCoverage,
                            double knownSpaceCvgs,
                            double knownSpaceScale,
                            double totalKnownUnexploredActions) {
      this.minCoverage = minCoverage;
      this.maxCoverage = maxCoverage;
      this.knownSpaceCvgs = knownSpaceCvgs;
      this.knownSpaceScale = knownSpaceScale;
      this.totalKnownUnexploredActions = totalKnownUnexploredActions;
    }

    public double getMinCoverage () {
      return minCoverage;
    }

    public double getMaxCoverage () {
      return maxCoverage;
    }

    public double getKnownSpaceCvgs () {
      return knownSpaceCvgs;
    }

    public double getKnownSpaceScale () {
      return knownSpaceScale;
    }

    public double getTotalKnownUnexploredActions () {
      return totalKnownUnexploredActions;
    }
  }
}

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

import es.upv.staq.testar.graph.reporting.GraphReporter;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.actions.ActionRoles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


/**
 * @author Urko Rueda Molina (urueda)
 */
public class TESTAREnvironment implements IEnvironment {
  private TESTARGraph g;

  // Concrete state ID -> actions
  private HashMap<String, Set<Action>> discoveredStateActions;
  private Map<String, IGraphState> cachedGetStates;
  private Map<String, IGraphAction> cachedGetActions;
  private List<ResumingMetrics> explorationCurve;

  public TESTAREnvironment (String testSequencePath) {
    this(TESTARGraph.buildEmptyGraph());
    this.discoveredStateActions = new HashMap<>();
    this.cachedGetStates = new WeakHashMap<>();
    this.cachedGetActions = new WeakHashMap<>();
    this.explorationCurve = new ArrayList<>();
  }

  private TESTAREnvironment (TESTARGraph g) {
    this.g = g;
  }

  @Override
  public Movement getMovement () {
    return Grapher.getMovement();
  }

  private synchronized IGraphState existsState (String concreteID) {
    if (g.stateAtGraph(concreteID)) {
      return g.getState(concreteID);
    }
    else {
      return null;
    }
  }

  private synchronized IGraphAction existsAction (String concreteID) {
    if (g.actionAtGraph(concreteID)) {
      return g.getAction(concreteID);
    }
    else {
      return null;
    }
  }

  @Override
  public synchronized IGraphState get (State state) {
    String id = state.get(Tags.ConcreteID);
    IGraphState returnGS = this.cachedGetStates.get(id);
    if (returnGS != null) {
      return returnGS;
    }

    IGraphState gs = existsState(id);
    if (gs != null) {
      returnGS = gs;
    }
    else {
      returnGS = new GraphState(state);
    }
    this.cachedGetStates.put(id, returnGS);

    return returnGS;
  }

  @Override
  public IGraphState getState (String stateID) {
    return g.getState(stateID);
  }

  @Override
  public synchronized IGraphAction get (Action action) {
    String id = action.get(Tags.ConcreteID);
    IGraphAction returnGA = this.cachedGetActions.get(id);
    if (returnGA != null) {
      return returnGA;
    }

    IGraphAction ga = existsAction(id);
    if (ga != null) {
      returnGA = ga;
    }
    else {
      returnGA = new GraphAction(action);
    }
    this.cachedGetActions.put(id, returnGA);

    return returnGA;
  }

  @Override
  public IGraphAction getAction (String actionID) {
    return g.getAction(actionID);
  }

  private void updateAvailableActions (IGraphState graphState) {
    Set<Action> actions = discoveredStateActions.get(graphState.getConcreteID());
    if (actions != null) {
      Set<String> exploredActions = new HashSet<String>();
      for (Object aid : g.outgoingEdgesOf(graphState.getConcreteID()).toArray()) {
        exploredActions.add(this.getAction(((GraphEdge) aid).getActionID()).getAbstractID());
      }
      graphState.updateUnexploredActions(this, actions, exploredActions);
      discoveredStateActions.remove(graphState.getConcreteID());
    }
  }

  @Override
  public void setStartingNode (IGraphState startNode) {
    g.addVertex(this, startNode);
  }

  @Override
  public void populateEnvironment (IGraphState fromState, IGraphAction action, IGraphState toState) {
    g.addVertex(this, toState);
    updateAvailableActions(toState);
    g.addEdge(this, fromState, toState, action);
    fromState.actionExplored(action.getAbstractID());
    if (!Grapher.GRAPH_LOADING_TASK) {
      sampleExploration();
    }
  }

  @Override
  public void notifyEnvironment (State state, Set<Action> actions) {
    discoveredStateActions.put(state.get(Tags.ConcreteID), actions);
    IGraphState gs = this.existsState(state.get(Tags.ConcreteID));
    if (gs != null && this.stateAtGraph(gs)) {
      this.updateAvailableActions(gs);
    }
  }

  @Override
  public Collection<IGraphState> getGraphStates () {
    return this.g.vertexStates();
  }

  @Override
  public Collection<IGraphAction> getGraphActions () {
    return this.g.edgeActions();
  }

  @Override
  public IGraphState getSourceState (IGraphAction action) {
    return this.g.getState(action.getSourceStateID());
  }

  @Override
  public IGraphState[] getTargetStates (IGraphAction action) {
    Set<String> targetStates = action.getTargetStateIDs();
    if (targetStates.isEmpty()) {
      return new IGraphState[]{}; // empty;
    }
    Set<IGraphState> graphStates = new HashSet<IGraphState>();
    IGraphState gs;
    for (String ts : targetStates) {
      gs = this.g.getState(ts);
      if (gs != null) {
        graphStates.add(gs);
      }
    }
    if (graphStates.size() < targetStates.size()) {
      System.out.println("WARNING - action <" + action.getConcreteID() +
          "> from <" + action.getSourceStateID() +
          "> has <" + (targetStates.size() - graphStates.size()) +
          "> null targets: " + targetStates.toString());
    }
    return graphStates.toArray(new IGraphState[graphStates.size()]);
  }

  @Override
  public Collection<GraphEdge> getIncomingActions (IGraphState state) {
    if (g.containsVertex(state.getConcreteID())) {
      return g.incomingEdgesOf(state.getConcreteID());
    }
    else {
      return new ArrayList<GraphEdge>();
    }
  }

  @Override
  public Collection<GraphEdge> getOutgoingActions (IGraphState state) {
    if (g.containsVertex(state.getConcreteID())) {
      return g.outgoingEdgesOf(state.getConcreteID());
    }
    else {
      return new ArrayList<GraphEdge>();
    }
  }

  @Override
  public int getAbstractExecutionCount (IGraphState graphState, IGraphAction graphAction) {
    String absID = graphAction.getAbstractID();
    IGraphAction outGA;
    int c = 0;
    for (GraphEdge edge : this.getOutgoingActions(graphState)) {
      outGA = g.getAction(edge.getActionID());
      if (absID.equals(outGA.getAbstractID())) {
        c++;
      }
    }
    return c;
  }

  @Override
  public int[] getWalkedCount (IGraphAction action) {
    int[] walkC = new int[2];
    walkC[0] = action.getCount();
    walkC[1] = 0;
    Set<String> c = this.g.getActionClusters().get(action.getAbstractID());
    if (c != null) {
      walkC[1] = c.size();
    }
    return walkC;
  }

  private int getActionsNumber (Set<String> actionTypes, IGraphState state) {
    int an = 0;
    if (g.containsVertex(state.getConcreteID())) {
      for (GraphEdge edge : this.getOutgoingActions(state)) {
        if (actionTypes.contains(this.getAction(edge.getActionID()).getRole())) {
          an++;
        }
      }
    }
    return an;
  }

  @Override
  public int getLeftClicks (IGraphState state) {
    @SuppressWarnings("serial")
    Set<String> set = new HashSet<String>() {{
      add(ActionRoles.LeftClick.name());
      add(ActionRoles.LeftClickAt.name());
    }};
    return getActionsNumber(set, state);
  }

  @Override
  public int getTypesInto (IGraphState state) {
    @SuppressWarnings("serial")
    Set<String> set = new HashSet<String>() {{
      add(ActionRoles.ClickTypeInto.name());
      add(ActionRoles.Type.name());
    }};
    return getActionsNumber(set, state);
  }

  @Override
  public GraphEdge[] getSortedActionsByOrder (int fromOrder, int toOrder) {
    return g.getSortedActionsByOrder(fromOrder, toOrder);
  }

  @Override
  public Iterator<GraphEdge> getForwardActions () {
    return g.getForwardActions();
  }

  @Override
  public ListIterator<GraphEdge> getBackwardActions () {
    return g.getBackwardActions();
  }

  @Override
  public boolean stateAtGraph (IGraphState gs) {
    return g.stateAtGraph(gs);
  }

  @Override
  public boolean actionAtGraph (IGraphAction ga) {
    return g.actionAtGraph(ga);
  }

  @Override
  public HashMap<String, Set<String>> getGraphStateClusters () {
    return this.g.getStateClusters();
  }

  @Override
  public HashMap<String, Set<String>> getGraphActionClusters () {
    return this.g.getActionClusters();
  }

  @Override
  public void finishGraph (boolean walkStatus,
                           IGraphState lastState,
                           IGraphAction lastAction,
                           State walkEndState) {
    if (lastState == null) {
      return; // unable to decide where to attach the graph end
    }
    IGraphState weState = lastState;
    if (lastAction != null && walkEndState != null) {
      weState = get(walkEndState);
      populateEnvironment(lastState, lastAction, weState);
    }
    if (!this.stateAtGraph(weState)) {
      g.addVertex(this, weState);
    }
    IGraphState v = new GraphState(walkStatus ? Grapher.GRAPH_NODE_PASS : Grapher.GRAPH_NODE_FAIL);
    g.addVertex(this, v);
    g.addEdge(this, weState, v, new GraphAction(Grapher.GRAPH_ACTION_STOP));
  }

  /**
   * &lt;[0],[1]&gt; = &lt;min%,max%&gt; coverage (for all states)
   * [2] = % coverage from the known' explored UI space
   * [3] = known UI space scale
   * [4] = known unexplored actions
   */
  @Override
  public CoverageMetrics getCoverageMetrics () {
    int exploredActionsCount, unexploredActionsCount;
    final double MAXCVG_UNKNOWN_SPACE = .999; // by urueda (actions space is unknown)
    double sumActions, // by urueda
        coverage = 0.0, minCoverage = MAXCVG_UNKNOWN_SPACE, maxCoverage = 0.0;
    int totalKnownActions = 0, totalKnownExecutedActions = 0, totalKnownUnexploredActions = 0; // by urueda
    for (IGraphState v : g.vertexStates()) {
      if (v.getConcreteID().equals(Grapher.GRAPH_NODE_ENTRY) ||
          v.getConcreteID().equals(Grapher.GRAPH_NODE_PASS) ||
          v.getConcreteID().equals(Grapher.GRAPH_NODE_FAIL)) {
        continue;
      }
      exploredActionsCount = getOutgoingActions(v).size();
      unexploredActionsCount = v.getUnexploredActionsSize();
      sumActions = exploredActionsCount + unexploredActionsCount;
      totalKnownActions += sumActions;
      totalKnownExecutedActions += exploredActionsCount;
      totalKnownUnexploredActions += v.getUnexploredActionsSize();
      // == 1? => ignore single action ESC, KillProcess, ActivateSystem, ...
      if (sumActions > 1) {
        coverage = (double) exploredActionsCount / sumActions;
      }
      minCoverage = Math.min(minCoverage, coverage);
      maxCoverage = Math.max(maxCoverage, coverage);
      maxCoverage = Math.min(MAXCVG_UNKNOWN_SPACE, maxCoverage);
    }

    double knownSpaceCvgs = 0.0; // % coverage from the known' explored UI space
    double knownSpaceScale = 0.0;
    if (totalKnownActions > 0) {
      knownSpaceCvgs = (int) (totalKnownExecutedActions * 100.0 / totalKnownActions);
      knownSpaceScale = totalKnownActions;
    }

    return new CoverageMetrics(minCoverage * 100, maxCoverage * 100,
        knownSpaceCvgs, knownSpaceScale, totalKnownUnexploredActions);
  }

  @Override
  public int[] getGraphResumingMetrics () {
    int knownStates = 0, revisitedStates = 0;
    for (IGraphState v : g.vertexStates()) {
      if (v.getConcreteID().equals(Grapher.GRAPH_NODE_ENTRY) ||
          v.getConcreteID().equals(Grapher.GRAPH_NODE_PASS) ||
          v.getConcreteID().equals(Grapher.GRAPH_NODE_FAIL)) {
        continue;
      }
      if (v.knowledge()) {
        knownStates++;
        if (v.revisited()) {
          revisitedStates++;
        }
      }
    }
    return new int[]{knownStates, revisitedStates,
        g.vertexSet().size() - 2 - knownStates};
  }

  private int sampleExplorationCount = 0;

  private void sampleExploration () {
    sampleExplorationCount++;
    if (sampleExplorationCount % Grapher.EXPLORATION_SAMPLE_INTERVAL == 0) {
      CoverageMetrics cvgMetrics = getCoverageMetrics();
      ResumingMetrics sample = new ResumingMetrics(
          g.vertexStates().size() - 1,
          g.edgeSet().size() - 1,
          getGraphStateClusters().size(),
          getGraphActionClusters().size(),
          (int) cvgMetrics.getTotalKnownUnexploredActions(),
          getLongestPathLength(),
          (int) cvgMetrics.getMinCoverage(),
          (int) cvgMetrics.getMaxCoverage(),
          (int) cvgMetrics.getKnownSpaceCvgs(),
          (int) cvgMetrics.getKnownSpaceScale());
      explorationCurve.add(sample);
    }
  }

  @Override
  public List<ResumingMetrics> getExplorationCurve () {
    return explorationCurve;
  }

  @Override
  public String getExplorationCurveSample () {
    if (explorationCurve != null && !explorationCurve.isEmpty()) {
      ResumingMetrics lastSample = explorationCurve.get(explorationCurve.size() - 1);
      return lastSample.getExplorationCurveString();
    }
    else {
      return "Cannot get exploration curve sample - initialisation missing";
    }
  }

  @Override
  public int getExplorationCurveSampleCvg () {
    if (explorationCurve != null && !explorationCurve.isEmpty()) {
      ResumingMetrics lastSample = explorationCurve.get(explorationCurve.size() - 1);
      return lastSample.getCoverageUIspace();
    }
    else {
      return 0;
    }
  }

  @Override
  public int getExplorationCurveSampleScale () {
    if (explorationCurve != null && !explorationCurve.isEmpty()) {
      ResumingMetrics lastSample = explorationCurve.get(explorationCurve.size() - 1);
      return lastSample.getSpaceScale();
    }
    else {
      return 0;
    }
  }

  @Override
  public String convertKCVG (int k) {
    if (k < 100) {
      return k + "";
    }
    if (k < 1000) // 1K
    {
      return k / 10 + "a"; // x 10
    }
    if (k < 10000) // 10K
    {
      return k / 100 + "b"; // x 100
    }
    if (k < 100000) // 100K
    {
      return k / 1000 + "c"; // x 1000 (1K)
    }
    if (k < 1000000) // 1M
    {
      return k / 10000 + "d"; // x 10000 (10K)
    }
    if (k < 10000000) // 10M
    {
      return k / 100000 + "e"; // x 100000 (100K)
    }
    if (k < 100000000) // 100M
    {
      return k / 1000000 + "f"; // x 1000000 (1M)
    }
    if (k < 1000000000) // 1G
    {
      return k / 10000000 + "g"; // x 10000000 (10M)
    }
    return k + "o"; // overflow
  }

  @Override
  public String getLongestPath () {
    return g.getLongestPath();
  }

  @Override
  public int getLongestPathLength () {
    return g.getLongestPathArray().size();
  }

  @Override
  public List<IGraphState> getPath (IGraphState from, IGraphState to) {
    if (!g.containsVertex(from.getConcreteID()) || !g.containsVertex(to.getConcreteID())) {
      return null;
    }
    Set<GraphEdge> edges = this.g.getAllEdges(from.getConcreteID(), to.getConcreteID());
    if (edges == null || edges.isEmpty()) {
      return null;
    }
    else {
      List<GraphEdge> path = new ArrayList<GraphEdge>();
      IGraphState cs = from;
      while (cs != to) {
        for (GraphEdge edge : this.g.outgoingEdgesOf(cs.getConcreteID())) {
          if (edges.contains(edge)) {
            path.add(edge);
            edges.remove(edge);
            cs = g.getState(this.g.getEdgeTarget(edge));
            break;
          }
        }
      }
      List<IGraphState> pathStates = new ArrayList<IGraphState>();
      for (GraphEdge p : path) {
        pathStates.add(g.getState(g.getEdgeTarget(p)));
      }
      return pathStates;
    }
  }

  @Override
  public IGraphState getPrevious (IGraphState graphState) {
    if (!g.containsVertex(graphState.getConcreteID())) {
      return null;
    }
    Collection<GraphEdge> inActions = this.getIncomingActions(graphState);
    String lastOrder, maxOrder = "0";
    IGraphAction lastAction = null, ga;
    for (GraphEdge edge : inActions) {
      ga = g.getAction(edge.getActionID());
      lastOrder = ga.getLastOrder(edge.getTargetStateID());
      if (lastOrder != null) {
        if (lastOrder.compareTo(maxOrder) > 0) {
          maxOrder = lastOrder;
          lastAction = ga;
        }
      }
    }
    if (lastAction == null) {
      return null;
    }
    else {
      return this.g.getState(lastAction.getSourceStateID());
    }
  }

  @Override
  public List<IGraphState> getAncestors (IGraphState graphState, int n) {
    List<IGraphState> list = new ArrayList<IGraphState>();
    if (n > 0) {
      IGraphState previousState = getPrevious(graphState);
      if (previousState != null) {
        list.add(0, previousState);
        list.addAll(getAncestors(previousState, n - 1));
      }
    }
    return list;
  }

  @Override
  public int getExecutionNumber (IGraphAction graphAction) {
    return graphAction.getCount();
  }

  @Override
  public int getExecutedActionNumber (IGraphState graphState) {
    return this.getOutgoingActions(graphState).size();
  }

  @Override
  public int getUnexecutedActionNumber (IGraphState graphState) {
    return graphState.getUnexploredActionsSize();
  }

  @Override
  public int loadFromXML (String xmlPath) {
    return this.g.loadFromXML(xmlPath, this);
  }

  @Override
  public String[] getReport (int firstSequenceActionNumber) {
    // null or:
    // [0] = clusters,
    // [1] = test table,
    // [2] = exploration curve,
    // [3] = UI exploration data
    return GraphReporter.getReport(this, g, firstSequenceActionNumber);
  }
}

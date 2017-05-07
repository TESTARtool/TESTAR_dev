/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This graph project is distributed FREE of charge under the TESTAR license, as an open *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.WeakHashMap;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.actions.ActionRoles;

import es.upv.staq.testar.graph.reporting.GraphReporter;

/**
 * 
 * @author Urko Rueda Molina (urueda)
 *
 */
public class TESTAREnvironment implements IEnvironment {

	private TESTARGraph g;
		
	private HashMap<String,Set<Action>> discoveredStateActions; // Concrete state ID -> actions
	
	private WeakHashMap<String,IGraphState> cachedGetStates = null;
	private WeakHashMap<String,IGraphAction> cachedGetActions = null;
	
	private HashMap<String,Set<String>> stateClusters; // abstract_R -> Set<concrete>
	private HashMap<String,Set<String>> actionClusters; // abstract -> Set<concrete>
		
	// [0] unique_states, [1] unique_actions, [2] abstract_states, [3] abstract_actions, [4] longestPath, [5] minCvg, [6] maxCvg
	private List<int[]> explorationCurve;
	
	public TESTAREnvironment(String testSequencePath){
		this(TESTARGraph.buildEmptyGraph());
		this.discoveredStateActions = new HashMap<String,Set<Action>>();
		this.cachedGetStates = new WeakHashMap<String,IGraphState>();;
		this.cachedGetActions = new WeakHashMap<String,IGraphAction>();;
		this.stateClusters = new HashMap<String,Set<String>>();
		this.actionClusters = new HashMap<String,Set<String>>();
		this.explorationCurve = new ArrayList<int[]>();
	}
	
	public TESTAREnvironment(TESTARGraph g){
		this.g = g;
	}
	
	@Override
	public Movement getMovement() {
		return Grapher.getMovement();
	}	
	
	private synchronized IGraphState existsState(String concreteID){
		if (g.stateAtGraph(concreteID))
			return g.getState(concreteID);
		else
			return null;
	}
	
	private synchronized IGraphAction existsAction(String concreteID){
		if (g.actionAtGraph(concreteID))
			return g.getAction(concreteID);
		else
			return null;	
	}
	
	@Override
	public synchronized IGraphState get(State state){
		String id = state.get(Tags.ConcreteID);
		IGraphState returnGS = this.cachedGetStates.get(id);
		if (returnGS != null)
			return returnGS;
		
		IGraphState gs = existsState(id);
		if (gs != null)
			returnGS = gs;
		else
			returnGS = new GraphState(state);		
		this.cachedGetStates.put(id, returnGS);
		
		return returnGS;
	}
	
	@Override
	public IGraphState getState(String stateID){
		return g.getState(stateID);
	}
	
	@Override
	public synchronized IGraphAction get(Action action){
		String id = action.get(Tags.ConcreteID);
		IGraphAction returnGA = this.cachedGetActions.get(id);
		if (returnGA != null)
			return returnGA;
		
		IGraphAction ga = existsAction(id);
		if (ga != null)
			returnGA = ga;
		else
			returnGA = new GraphAction(action);
		this.cachedGetActions.put(id,returnGA);
		
		return returnGA;
	}
	
	@Override
	public IGraphAction getAction(String actionID){
		return g.getAction(actionID);
	}
	
	private void updateAvailableActions(IGraphState graphState){
		Set<Action> actions = discoveredStateActions.get(graphState.getConcreteID());
		if (actions != null){
			Set<String> exploredActions = new HashSet<String>();
			for (Object aid : g.outgoingEdgesOf(graphState.getConcreteID()).toArray())
				exploredActions.add(this.getAction((String)aid).getAbstractID());
			graphState.updateUnexploredActions(this, actions, exploredActions);
			discoveredStateActions.remove(graphState.getConcreteID());
		}		
	}

	private void updateCluster(IGraphState gs){
		Set<String> c = this.stateClusters.get(gs.getAbstract_R_ID());
		if (c == null){
			c = new HashSet<String>();
			this.stateClusters.put(gs.getAbstract_R_ID(), c);
		}
		c.add(gs.getConcreteID());
	}
	
	private void updateCluster(IGraphAction ga){
		if (ga.getConcreteID().equals(Grapher.GRAPH_ACTION_START))
			return;
		Set<String> c = this.actionClusters.get(ga.getAbstractID());
		if (c == null){
			c = new HashSet<String>();
			this.actionClusters.put(ga.getAbstractID(), c);
		}
		c.add(ga.getConcreteID());
	}

	@Override
	public void populateEnvironment(IGraphState fromState, IGraphAction action, IGraphState toState) {
		if (fromState.getConcreteID().equals(Grapher.GRAPH_NODE_ENTRY)) // is graph starting?
			g.addVertex(this,fromState);
		if (g.addVertex(this,toState))
			updateCluster(toState);
		updateAvailableActions(toState);
		if (g.addEdge(this, fromState, toState, action))
			updateCluster(action);
		fromState.actionExplored(action.getAbstractID());
		if (!Grapher.GRAPH_LOADING_TASK) sampleExploration();		
	}	
	
	@Override
	public void notifyEnvironment(State state, Set<Action> actions){
		discoveredStateActions.put(state.get(Tags.ConcreteID),actions);
		IGraphState gs = this.existsState(state.get(Tags.ConcreteID));
		if (gs != null && this.stateAtGraph(gs))
			this.updateAvailableActions(gs);
	}
	
	@Override
	public Collection<IGraphState> getGraphStates(){
		return this.g.vertexStates();
	}
	
	@Override
	public Collection<IGraphAction> getGraphActions(){
		return this.g.edgeActions();
	}
	
	@Override
	public IGraphState getSourceState(IGraphAction action){
		if (g.containsEdge(action.getConcreteID()))
			return g.getState(g.getEdgeSource(action.getConcreteID()));
		else
			return null;
	}
	
	@Override
	public IGraphState getTargetState(IGraphAction action){
		if (g.containsEdge(action.getConcreteID()))
			return g.getState(g.getEdgeTarget(action.getConcreteID()));
		else
			return null;
	}

	@Override
	public Collection<String> getIncomingActions(IGraphState state){
		if (g.containsVertex(state.getConcreteID()))		
			return g.incomingEdgesOf(state.getConcreteID());
		else
			return new ArrayList<String>();
	}
	
	@Override
	public Collection<String> getOutgoingActions(IGraphState state){
		if (g.containsVertex(state.getConcreteID()))
			return g.outgoingEdgesOf(state.getConcreteID());
		else
			return new ArrayList<String>();
	}
	
	@Override
	public int getAbstractExecutionCount(IGraphState graphState, IGraphAction graphAction){
		String absID = graphAction.getAbstractID();
		IGraphAction outGA;
		int c = 0;
		for (String outActionID : this.getOutgoingActions(graphState)){
			outGA = g.getAction(outActionID);
			if (absID.equals(outGA.getAbstractID()))
				c++;
		}
		return c;
	}
	
	@Override
	public int[] getWalkedCount(IGraphAction action) {
		int[] walkC = new int[2];
		walkC[0] = action.getCount();
		walkC[1] = 0;
		Set<String> c = actionClusters.get(action.getAbstractID());
		if (c != null)			
			walkC[1] = c.size();		
		return walkC;
	}

	private int getActionsNumber(Set<String> actionTypes, IGraphState state){
		int an = 0;
		if (g.containsVertex(state.getConcreteID())){
			for (String actionID : this.getOutgoingActions(state)){
				if (actionTypes.contains(this.getAction(actionID).getRole()))
					an++;
			}
		}
		return an;		
	}
	
	@Override
	public int getLeftClicks(IGraphState state){
		@SuppressWarnings("serial")
		Set<String> set = new HashSet<String>(){{
			add(ActionRoles.LeftClick.name());
			add(ActionRoles.LeftClickAt.name());
		}};
		return getActionsNumber(set,state);
	}

	@Override
	public int getTypesInto(IGraphState state){
		@SuppressWarnings("serial")
		Set<String> set = new HashSet<String>(){{
			add(ActionRoles.ClickTypeInto.name());
			add(ActionRoles.Type.name());
		}};
		return getActionsNumber(set,state);
	}
	
	/*@Override
	public IGraphAction[] getSortedStateActionsByDecReward(IGraphState state) {
		Set<IGraphAction> actions = g.outgoingEdgesOf(state);
		IGraphAction[] sortedActions = actions.toArray(new IGraphAction[actions.size()]);
		Arrays.sort(sortedActions, new Comparator<Object>() {
			@Override
			public int compare(Object graphAction1, Object graphAction2) {
				return (new Double(((IGraphAction)graphAction2).getActionReward())).compareTo(
					    new Double(((IGraphAction)graphAction1).getActionReward())); // descending order
				//return (new Double(((IGraphAction)graphAction1).getActionReward())).compareTo(
				//	    new Double(((IGraphAction)graphAction2).getActionReward())); // ascending order
			}
		});
		return sortedActions;
	}*/
	
	@Override
	public IGraphAction[] getSortedActionsByOrder(int fromOrder, int toOrder) {
		return g.getSortedActionsByOrder(fromOrder,toOrder);
	}
	
	public Iterator<IGraphAction> getForwardActions(){
		return g.getForwardActions();
	}
	
	public ListIterator<IGraphAction> getBackwardActions(){
		return g.getBackwardActions();
	}
	
	/*private boolean stateAtGraph(String concreteID){
		for (IGraphState gs : g.vertexSet()){
			if (gs.getConcreteID().equals(concreteID))
				return true;
		}
		return false;
	}	
	
	private boolean actionAtGraph(String concreteID){
		for (IGraphAction ga : g.edgeSet()){
			if (ga.getConcreteID().equals(concreteID))
				return true;
		}
		return false;
	}*/

	@Override
	public boolean stateAtGraph(IGraphState gs){
		return g.stateAtGraph(gs);
	}

	@Override
	public boolean actionAtGraph(IGraphAction ga){
		return g.actionAtGraph(ga);
	}

	@Override
	public HashMap<String,Set<String>> getGraphStateClusters(){
		return this.stateClusters;
	}
		
	@Override
	public HashMap<String,Set<String>> getGraphActionClusters(){
		return this.actionClusters;
	}	
	
	@Override
	public void finishGraph(boolean walkStatus,
							IGraphState lastState,
							IGraphAction lastAction,
							State walkEndState) {
		if (lastState == null)
			return; // unable to decide where to attach the graph end
		IGraphState weState = lastState;
		if (lastAction != null && walkEndState != null){
			weState = get(walkEndState);
			populateEnvironment(lastState,lastAction,weState);
		}
		if (!this.stateAtGraph(weState))
			g.addVertex(this, weState);
		IGraphState v = new GraphState(walkStatus ? Grapher.GRAPH_NODE_PASS : Grapher.GRAPH_NODE_FAIL);
		g.addVertex(this, v);
		g.addEdge(this, weState, v, new GraphAction(Grapher.GRAPH_ACTION_STOP));
	}
	
	private static final double LOG10_2 = Math.log10(2.);
	
	/**
	 *  &lt;[0],[1]&gt; = &lt;min%,max%&gt; coverage (for all states)
	 *  [2] = % coverage from the known' explored UI space
	 *  [3] = known UI space scale
	 *  [4] = known unexplored actions
	 */
	@Override
	public double[] getCoverageMetrics(){
		int exploredActionsCount, unexploredActionsCount;
		final double MAXCVG_UNKNOWN_SPACE = .999; // by urueda (actions space is unknown)
		double sumActions, // by urueda
			   coverage = 0.0, minCoverage = MAXCVG_UNKNOWN_SPACE, maxCoverage = 0.0;
		int totalKnownActions = 0, totalKnownExecutedActions = 0, totalKnownUnexploredActions = 0; // by urueda
		for (IGraphState v : g.vertexStates()){
			if (v.getConcreteID().equals(Grapher.GRAPH_NODE_ENTRY) ||
				v.getConcreteID().equals(Grapher.GRAPH_NODE_PASS) ||
				v.getConcreteID().equals(Grapher.GRAPH_NODE_FAIL))
				continue;
			exploredActionsCount = getOutgoingActions(v).size();
			unexploredActionsCount = v.getUnexploredActionsSize();
			// begin by urueda
			sumActions = exploredActionsCount + unexploredActionsCount;
			totalKnownActions += sumActions;
			totalKnownExecutedActions += exploredActionsCount;
			totalKnownUnexploredActions += v.getUnexploredActionsSize();
			if (sumActions > 1) // == 1? => ignore single action ESC, KillProcess, ActivateSystem, ...
				coverage = (double) exploredActionsCount / sumActions;
				// end by urueda
			if (coverage < minCoverage)
				minCoverage = coverage;
			if (coverage > maxCoverage)
				maxCoverage = coverage;
			// begin by urueda
			if (maxCoverage > MAXCVG_UNKNOWN_SPACE)
				maxCoverage = MAXCVG_UNKNOWN_SPACE;
		}
		double knownSpaceCvgs = 0.0, // % coverage from the known' explored UI space
			   knownSpaceScale = 0.0;
		if (totalKnownActions > 0){
			knownSpaceCvgs = (int) (totalKnownExecutedActions * 100.0 / totalKnownActions);
			knownSpaceScale = Math.log10((double)totalKnownActions) / LOG10_2; // log base 2
		}
		return new double[]{minCoverage * 100, maxCoverage * 100, knownSpaceCvgs, knownSpaceScale, totalKnownUnexploredActions};
		// end by urueda
	}
	
	@Override
	public int[] getGraphResumingMetrics(){
		int knownStates = 0, revisitedStates = 0; 
		for (IGraphState v : g.vertexStates()){
			if (v.getConcreteID().equals(Grapher.GRAPH_NODE_ENTRY) ||
				v.getConcreteID().equals(Grapher.GRAPH_NODE_PASS) ||
				v.getConcreteID().equals(Grapher.GRAPH_NODE_FAIL))
				continue;
			if (v.knowledge()){
				knownStates++;
				if (v.revisited())
					revisitedStates++;
			}
		}
		return new int[]{knownStates,
						 revisitedStates,
						 g.vertexSet().size() - 2 - knownStates};
	}
	
	private int sampleExplorationCount = 0;
	private void sampleExploration(){
		sampleExplorationCount++;
		if (sampleExplorationCount % Grapher.EXPLORATION_SAMPLE_INTERVAL == 0){
			double[] cvgMetrics = getCoverageMetrics();
			int[] sample = new int[]{
				g.vertexStates().size() - 1, // states number (without start state)
				g.edgeSet().size() - 1, // actions number (without start edges)
				getGraphStateClusters().size(), // abstract states number
				getGraphActionClusters().size(), // abstract actions number
				(int) cvgMetrics[4], // known unexplored actions number
				getLongestPathLength(), // longest path
				(int) cvgMetrics[0], // min coverage
				(int) cvgMetrics[1], // max coverage
				(int) cvgMetrics[2], // % cvg from known UI space
				(int) cvgMetrics[3]  // known UI space scale
			};
			explorationCurve.add(sample);
		}
	}
	
	@Override
	public List<int[]> getExplorationCurve(){
		return explorationCurve;
	}
	
	@Override
	public String getExplorationCurveSample(){
		if (explorationCurve != null && !explorationCurve.isEmpty()){
			int[] sample = explorationCurve.get(explorationCurve.size()-1);
			return "ExplorationCurve (unique states/actions, abstract states/actions, unexplored_count): " + 
					sample[0] + ", " + sample[1] + ", " + sample[2] + ", " + sample[3] + "; " + sample[4] + ", " + sample[5] + ", " + sample[6] + ", " + sample[7];
		} else
			return "Cannot get exploration curve sample - initialisation missing";
	}

	@Override
	public int getExplorationCurveSampleCvg(){
		if (explorationCurve != null && !explorationCurve.isEmpty())
			return explorationCurve.get(explorationCurve.size()-1)[8];
		else
			return 0;	
	}

	@Override
	public int getExplorationCurveSampleScale(){
		if (explorationCurve != null && !explorationCurve.isEmpty())
			return explorationCurve.get(explorationCurve.size()-1)[9];
		else
			return 0;	
	}
	
	@Override
	public String getLongestPath(){
		return g.getLongestPath();
	}
	
	@Override
	public int getLongestPathLength(){
		return g.getLongestPathArray().size();
	}
	
	@Override
	public List<IGraphState> getPath(IGraphState from, IGraphState to){
		if (!g.containsVertex(from.getConcreteID()) || !g.containsVertex(to.getConcreteID()))
			return null;
		Set<String> edges = this.g.getAllEdges(from.getConcreteID(),to.getConcreteID());
		if (edges == null || edges.isEmpty())
			return null;
		else {
			List<IGraphAction> path = new ArrayList<IGraphAction>();
			IGraphState cs = from;
			IGraphAction ga;
			while (cs != to){
				for (String gaID : this.g.outgoingEdgesOf(cs.getConcreteID())){
					if (edges.contains(gaID)){
						ga = g.getAction(gaID);
						path.add(ga);
						edges.remove(ga);
						cs = g.getState(this.g.getEdgeTarget(gaID));
						break;
					}
				}
			}
			List<IGraphState> pathStates = new ArrayList<IGraphState>();
			for (IGraphAction pga : path)
				pathStates.add(g.getState(g.getEdgeTarget(pga.getConcreteID())));
			return pathStates;
		}
	}
	
	@Override
	public IGraphState getPrevious(IGraphState graphState){
		if (!g.containsVertex(graphState.getConcreteID()))
			return null;
		Collection<String> inActions = this.getIncomingActions(graphState);
		String lastOrder, maxOrder = "0";
		IGraphAction lastAction = null, ga;
		for (String gaID : inActions){
			ga = g.getAction(gaID);
			lastOrder = ga.getLastOrder();
			if (lastOrder != null){
				if (lastOrder.compareTo(maxOrder) > 0){
					maxOrder = lastOrder;
					lastAction = ga;
				}
			}
		}
		if (lastAction == null)
			return null;
		else
			return this.g.getState(g.getEdgeSource(lastAction.getConcreteID()));
	}
	
	@Override
	public List<IGraphState> getAncestors(IGraphState graphState, int n){
		List<IGraphState> list = new ArrayList<IGraphState>();
		if (n > 0){
			IGraphState previousState = getPrevious(graphState);
			if (previousState != null){
				list.add(0,previousState);
				list.addAll(getAncestors(previousState,n-1));
			}
		}
		return list;
	}
	
	@Override
	public int getExecutionNumber(IGraphAction graphAction){
		return graphAction.getCount();
	}
	
	@Override
	public int getExecutedActionNumber(IGraphState graphState){
		return this.getOutgoingActions(graphState).size();
	}

	@Override
	public int getUnexecutedActionNumber(IGraphState graphState){
		return graphState.getUnexploredActionsSize();
	}
	
	@Override
	public String toString(){
		return GraphReporter.PrintResults(this, g);
	}

	@Override
	public int loadFromXML(String xmlPath){
		return this.g.loadFromXML(xmlPath, this);
	}
	
}

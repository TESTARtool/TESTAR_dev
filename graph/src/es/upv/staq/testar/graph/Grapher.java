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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import es.upv.staq.testar.algorithms.JSONWalker;
import es.upv.staq.testar.algorithms.IWalker;
import es.upv.staq.testar.algorithms.MaxCoverageWalker;
import es.upv.staq.testar.algorithms.PrologWalker;
import es.upv.staq.testar.algorithms.QLearningRestartsWalker;
import es.upv.staq.testar.algorithms.QLearningWalker;
import es.upv.staq.testar.algorithms.RandomRestartsWalker;
import es.upv.staq.testar.algorithms.RandomWalker;
import es.upv.staq.testar.algorithms.forms.FormsFilling;
import es.upv.staq.testar.graph.reporting.GraphReporter;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.LogSerialiser.LogLevel;

/**
 * Graphing utility for TESTAR' tests.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 */
public class Grapher implements Runnable {

	public static final String RANDOM_GENERATOR 			= "random"; // default
	public static final String RANDOM_RESTARTS_GENERATOR 	= "random+";
	public static final String QLEARNING_GENERATOR 			= "qlearning";
	public static final String QLEARNING_RESTARTS_GENERATOR = "qlearning+";
	public static final String MAXCOVERAGE_GENERATOR 		= "maxcoverage";
	public static final String PROLOG_GENERATOR 			= "prolog";
	public static final String EVOLUTIONARY_GENERATOR 		= "evolutionary"; //by fraalpe2
		
	// QLEARNING parameters needs calibration
	// -> SUT UI space exploration capability (note: being worse at exploration might be good at concrete UI parts as these parts are more exercised): 
	public static double QLEARNING_DISCOUNT_PARAM = .95; // non fnal to allow calibration (.95 Sebastian base)
	public static double QLEARNING_MAXREWARD_PARAM =  9999999.0; // non final to allow calibration (9999999.0 Sebastian base)
	
	public static final boolean QLEARNING_CALIBRATION = false; // how-to retrieve from logs: findstr "CALIBRATION" log_file_name.log
	private static double MAX_MAXREWARD = 9999999.0;
	
	public static String testGenerator = RANDOM_GENERATOR;

	public static int EXPLORATION_SAMPLE_INTERVAL = 10;
	public static boolean GRAPHS_ACTIVATED = true;
	public static boolean PROLOG_ACTIVATED = false;
	public static boolean GRAPH_RESUMING_ACTIVATED = false;
	
	public static final String GRAPH_NODE_ENTRY = "ENTRY";
	public static final String GRAPH_NODE_PASS = "PASS";
	public static final String GRAPH_NODE_FAIL = "FAIL";
	public static final String GRAPH_ACTION_START = "START";
	public static final String GRAPH_ACTION_STOP = "STOP";
	
	public static boolean GRAPH_LOADING_TASK = false;
	public static int GRAPH_LOADING_MOVEMENTS, graphLoadingMovement;
	
	public static boolean FORMS_TYPING_ENHANCEMENT;
	
	public static int TYPING_TEXTS_FOR_EXECUTED_ACTION;
	
	private static Grapher singletonGrapher = new Grapher();	
			
	private static LinkedList<Movement> movementsFIFO = new LinkedList<Movement>();
	private static List<Integer> movementsSync = new ArrayList<Integer>();
		
	private static IEnvironment env = null;
	
	private static String testSequencePath = null;
	private static int testSequenceLength = 0;
	
	private static IWalker walker = null;
	private static WalkStopper walkStopper = null;
	
	private static boolean graphing = false; // true while graphing, false otherwise
	
	private static JIPrologWrapper jipWrapper = null;
	
	public static boolean offlineGraphConversion = false;
	
	private static transient ExecutorService exeSrv;
	
	private Grapher(){}
	
	public static String[] getRegisteredAlgorithms(){
		return new String[]{
			RANDOM_GENERATOR
			//RANDOM_RESTARTS_GENERATOR,
			//QLEARNING_GENERATOR,
			//QLEARNING_RESTARTS_GENERATOR,
			//MAXCOVERAGE_GENERATOR
			//EVOLUTIONARY_GENERATOR
		};
	}
	
	/**
	 * Run a new TESTAR grapher.
	 * @param testGenerator A valid generator is expected.
	 */
	public static void grapher(String testSequencePath, int sequenceLength, boolean formsFilling, int typingTexts,
							   String testGenerator, Double maxReward, Double discount,
							   Integer explorationSampleInterval, boolean graphsActivated, boolean prologActivated,
							   boolean graphResumingActivated, boolean offlineGraphConversion,
							   JIPrologWrapper jipWrapper) {
		try {
			synchronized(env){
				while (env != null){ // wait until a previous test sequence grapher finishes ...
					try {
						env.wait();
					} catch (InterruptedException e) {
						System.out.println("TESTAR grapher sync interruped\n" + e.toString());
					}
				}
			}
		} catch (Exception e){} // env may be set to null when we try to sync on it
		Grapher.testSequencePath = testSequencePath;
		Grapher.testSequenceLength = sequenceLength;
		Grapher.FORMS_TYPING_ENHANCEMENT = formsFilling;
		Grapher.TYPING_TEXTS_FOR_EXECUTED_ACTION = typingTexts;
		if (!graphsActivated && !testGenerator.equals(Grapher.RANDOM_GENERATOR)){
			System.out.println("Cannot use <" + testGenerator + "> test generator as GRAPHS are not activated (switching to <" + Grapher.RANDOM_GENERATOR + ">)");
			Grapher.testGenerator = Grapher.RANDOM_GENERATOR;			
		} else if (!prologActivated && testGenerator.equals(Grapher.PROLOG_GENERATOR)){
			System.out.println("Cannot use <" + testGenerator + "> test generator as PROLOG is not activated (switching to <" + Grapher.RANDOM_GENERATOR + ">)");
			Grapher.testGenerator = Grapher.RANDOM_GENERATOR;
		} else
			Grapher.testGenerator = testGenerator;
		Grapher.QLEARNING_MAXREWARD_PARAM = maxReward.doubleValue();
		Grapher.QLEARNING_DISCOUNT_PARAM = discount.doubleValue();
		Grapher.EXPLORATION_SAMPLE_INTERVAL = explorationSampleInterval.intValue();
		Grapher.GRAPHS_ACTIVATED = graphsActivated;
		Grapher.PROLOG_ACTIVATED = false;
		Grapher.GRAPH_RESUMING_ACTIVATED = graphResumingActivated;
		Grapher.offlineGraphConversion = offlineGraphConversion;
		Grapher.jipWrapper = jipWrapper;
		exeSrv = Executors.newFixedThreadPool(1);
		exeSrv.execute(singletonGrapher);
	}
	
	public static void waitEnvironment(){
		long now = System.currentTimeMillis();
		final long WAIT_START = now,
				   WAIT_THRESHOLD = 10000; // 10 seconds
		while ((Grapher.GRAPHS_ACTIVATED && Grapher.GRAPH_LOADING_TASK &&
				(Grapher.GRAPH_LOADING_MOVEMENTS == Integer.MAX_VALUE  || Grapher.graphLoadingMovement < Grapher.GRAPH_LOADING_MOVEMENTS)
			   ) || (Grapher.env == null && (now - WAIT_START < WAIT_THRESHOLD))
			  ){
			synchronized(Grapher.testSequencePath){
				try {
					Grapher.testSequencePath.wait(100); // 0,1 second
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			now = System.currentTimeMillis();
		}
		if (Grapher.env == null)
			LogSerialiser.log("Graph environment is NULL!", LogLevel.Critical);
		Grapher.GRAPH_LOADING_TASK = false;
	}
	
	/**
	 * Requisite: synchronize with single waitEnvironment() before any notification call.
	 * Notification about a SUT state, not considered a Movement.
	 * This notification is for graphs sync with TESTAR status.
	 * @param state Last known SUT state.
	 * @param stateshotPath SUT state screenshot.
	 */
	public static void notify(State state, String stateshotPath){
		if (Grapher.GRAPHS_ACTIVATED){
			IGraphState graphState = env.get(state);
			graphState.setStateshot(stateshotPath);
			synchronized(movementsFIFO){
				movementsFIFO.add(new Movement(graphState,null)); // Movements PRODUCER
				movementsFIFO.notifyAll(); // awake CONSUMER
			}
		}
	}		
	
	/**
	 * Notification about an executed action from a SUT state, which is considered a Movement.
	 * This movement is added to a FIFO queue, which can be consumed synchronously.
	 * @param state SUT state before action execution.
	 * @param stateshotPath SUT state screenshot before action execution.
	 * @param action Executed action.
	 * @param actionshotPath SUT state screenshot after action execution.
	 * @param actionRepresentation A text representation for the action.
	 * @param memUsage SUT memory usage in KB.
	 * @param cpuUsage SUT CPU usage (user x system in ms.
	 */
	public static void notify(State state, String stateshotPath,
										   Action action, String actionshotPath, String actionRepresentation,
										   int memUsage, long[] cpuUsage){
		if (Grapher.GRAPHS_ACTIVATED){
			//System.out.println("TESTAR grapher notified: ACTION_" +
			//				   CodingManager.codify(action) + " (stateaction_" +
			//				   CodingManager.codify(state,action) + ") [state_" +
			//				   CodingManager.codify(state) + "]");
			IGraphState graphState = env.get(state);
			IGraphAction graphAction = env.get(action);
			graphAction.setDetailedName(actionRepresentation);
			graphState.setStateshot(stateshotPath);
			graphAction.setStateshot(actionshotPath);
			graphAction.setMemUsage(memUsage);		
			graphAction.setCPUsage(cpuUsage);
			Grapher.notify(graphState,graphAction);
		}
	}
	
	/**
	 * Notification about an executed action from a SUT state, which is considered a Movement.
	 * This movement is added to a FIFO queue, which can be consumed synchronously.
	 * @param graphState SUT graph state before action execution.
	 * @param graphAction Executed graph action.
	 */
	public static void notify(IGraphState graphState, IGraphAction graphAction){
		if (Grapher.GRAPHS_ACTIVATED){
			synchronized(movementsFIFO){
				movementsFIFO.add(new Movement(graphState,graphAction)); // Movements PRODUCER
				movementsFIFO.notifyAll(); // awake CONSUMER
			}
		}
	}
	
	/**
	 * Get notification about the end of a test sequence.
	 * @param status 'true' walk finished OK, 'false' walk suddenly STOPPED.
	 * @param endState Ending SUT state.
	 * @param scrshotPath The state screenshot.
	 */
	public static void walkFinished(final boolean status, final State endState, final String scrshotPath){
		if (Grapher.GRAPHS_ACTIVATED && walkStopper != null){
			if (endState != null)
				env.get(endState).setStateshot(scrshotPath);
			walkStopper.stopWalk(status,endState);
			graphing = false;
			synchronized(movementsFIFO){
				movementsFIFO.notifyAll(); // awake CONSUMER
			}
		}
	}
	
	/**
	 * Consumes a pair of &lt;State,Action&gt; from a FIFO queue, synchronously.
	 * @return Next non consumed movement.
	 */
	public static Movement getMovement(){
		graphing = false;
		Movement movement = null;
		synchronized(movementsFIFO){
			while(movementsFIFO.isEmpty()){
				try {
					if (walkStopper != null && walkStopper.continueWalking())
						movementsFIFO.wait();
					else
						return null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			movement = movementsFIFO.removeFirst();; // Movements CONSUMER
			graphing = true;
			//System.out.println(movementsFIFO.size() + " pending movements while graphing: " + movement.toString());
			movementsSync.add(movementsFIFO.size());
			movementsFIFO.notifyAll();
		}
		if (Grapher.GRAPH_LOADING_TASK) Grapher.graphLoadingMovement++;
		return movement;
	}
	
	public static List<Integer> getMovementsSync(){
		return movementsSync;
	}
	
	/**
	 * Sync TESTAR movements productions and graph movements consumption.
	 */
	public static void syncMovements(){
		synchronized(movementsFIFO){
			while(!movementsFIFO.isEmpty()){
				try {
					movementsFIFO.wait();
				} catch (InterruptedException e) {}
			}
		}
		while(graphing){
			try {
				TimeUnit.MILLISECONDS.sleep(1);
			} catch (InterruptedException e) {}
		}
		return;
	}
	    
	/**
	 * Selects an action to be executed from a set of available actions for a SUT state.
	 * @param state SUT state.
	 * @param actions SUT state UI actions.
	 * @return
	 */
	public static Action selectAction(State state, Set<Action> actions){

		Set<Action> filteredActions = FORMS_TYPING_ENHANCEMENT ?
			FormsFilling.filterFormActions(state,actions) // prioritize typing actions for text inputs dependent behaviors
			: actions;
		if (Grapher.GRAPHS_ACTIVATED) {
			env.notifyEnvironment(state, filteredActions);
		}
		Action selectedAction = walker.selectAction(env, state, filteredActions, jipWrapper);
		if (FORMS_TYPING_ENHANCEMENT)
			FormsFilling.updateFormActions(state,selectedAction); // update typing actions management
		return selectedAction;
	}

	// try to resume previous test sequence graph
	private void resumeGraph(){
		String prevSeq = null;
		if (testSequencePath.startsWith("sequence")){
			int ps = 0;
			try {
				ps = new Integer(testSequencePath.substring(8)).intValue() - 1;
				if (ps > 0)
					prevSeq = "sequence" + ps;
			} catch(NumberFormatException nfe){}
		}
		if (prevSeq != null){
			File f = new File("output/graphs/" + prevSeq);
			String[] list = f.list(new FilenameFilter() {
			    @Override
			    public boolean accept(File dir, String name) {
			        return name.matches("graph_[0-9]+.xml");
			    }
			});
			if (list != null && list.length == 1){
				String path = prevSeq + "/" + list[0];
				//System.out.println("Resuming previous graph: " + path);
				new Thread(){
					public void run() {
						Grapher.GRAPH_LOADING_MOVEMENTS = env.loadFromXML("output/graphs/" + path);
					}
				}.start();
			} else Grapher.GRAPH_LOADING_TASK = false;
		} else
			Grapher.GRAPH_LOADING_TASK = false;
	}
	
	@Override
	public void run() {
		long graphTime = System.currentTimeMillis();
		GraphReporter.useGraphData(graphTime,testSequencePath);
		//WalkReport wr = new WalkReport("Q-Learning", 0, 0, 0, 0, 0, 0);
		//System.out.println(wr);
		
		if (testGenerator.equals(QLEARNING_GENERATOR)){
			if (QLEARNING_CALIBRATION){
				QLEARNING_DISCOUNT_PARAM = Math.random(); // 0.0 .. 1.0
				QLEARNING_MAXREWARD_PARAM = Math.random() * MAX_MAXREWARD; // 0.0 .. MAX_MAXREWARD
			}
			walker = new QLearningWalker(QLEARNING_DISCOUNT_PARAM, QLEARNING_MAXREWARD_PARAM);
			System.out.println("<Q-Learning> test generator enabled (" +
							   "discount = " + QLEARNING_DISCOUNT_PARAM + ", maxReward = " + QLEARNING_MAXREWARD_PARAM + ")");
		} else if (testGenerator.equals(QLEARNING_RESTARTS_GENERATOR)){
			walker = new QLearningRestartsWalker(QLEARNING_DISCOUNT_PARAM, QLEARNING_MAXREWARD_PARAM, Grapher.testSequenceLength);
			System.out.println("<Q-Learning +> test generator enabled");			
		} else if (testGenerator.equals(MAXCOVERAGE_GENERATOR)){
			walker = new MaxCoverageWalker(new Random(graphTime), Grapher.testSequenceLength);
			System.out.println("<MaxCoverage> test generator enabled");
		} else if (testGenerator.equals(PROLOG_GENERATOR)){
			walker = new PrologWalker(new Random(graphTime));
			System.out.println("<Prolog> test generator enabled");
		} else if (testGenerator.equals(EVOLUTIONARY_GENERATOR)){ // by fraalpe2
			walker = new JSONWalker(new Random (graphTime)); // by urueda
			System.out.println("<Evolutionary> test generator enabled"); // by urueda
		} else if (testGenerator.equals(RANDOM_RESTARTS_GENERATOR)){
			walker = new RandomRestartsWalker(new Random (graphTime), Grapher.testSequenceLength);
			System.out.println("<Random +> test generator enabled");
		} else{ // default: RANDOM_GENERATOR
			walker = new RandomWalker(new Random(graphTime));
			//System.out.println("<Random> test generator enabled");
		}

		if (Grapher.GRAPHS_ACTIVATED){
			Grapher.GRAPH_LOADING_TASK = Grapher.GRAPH_RESUMING_ACTIVATED;
			if (Grapher.GRAPH_RESUMING_ACTIVATED){
				Grapher.GRAPH_LOADING_MOVEMENTS = Integer.MAX_VALUE;
				Grapher.graphLoadingMovement = 0;
			}
			env = new TESTAREnvironment(testSequencePath);
			if (Grapher.GRAPH_RESUMING_ACTIVATED) resumeGraph();
		}

		if (Grapher.GRAPHS_ACTIVATED){
			walkStopper = new WalkStopper();
			walker.walk(env, walkStopper);
		}
	}
	
	/**
	 * Retrieves the active walker.
	 * @return The walker.
	 */
	public static IWalker getWalker(){
		return Grapher.walker;		
	}
	
	// null or: [0] = clusters, [1] = test table, [2] = exploration curve, [3] = UI exploration data
	public static String[] getReport(int firstSequenceActionNumber){
		if (!Grapher.GRAPHS_ACTIVATED)
			return null;
		//System.out.println("TESTAR sequence graph dump on way ...");
		try {
			while (walkStopper != null && walkStopper.continueWalking()){
				synchronized(walkStopper){
					try {
						walkStopper.wait(100); // ms
					} catch (InterruptedException e) {}
				}
			}
			String[] report = env.getReport(firstSequenceActionNumber);
			//System.out.println("... finished TESTAR sequence graph dump");
			return report;
		} catch(java.lang.NullPointerException npe){ // premature test end <- env == null
			System.out.println("Grapher exception caught:");
			npe.printStackTrace();
			//resetGrapherFields();
			return null;
		} finally {
	        // begin - sync with following test sequence grapher (if any)
			if (env != null){
		    	IEnvironment notifyEnv = env;    
				resetGrapherFields(); // env = null; // let next test sequence grapher start
		    	synchronized(notifyEnv){
		    		notifyEnv.notifyAll();
		    	}
			}
		    // end - sync				
		}
	}
	
	public static IEnvironment getEnvironment(){
		return env;
	}
	
	public static String getExplorationCurveSample(){
		if (env != null)
			return env.getExplorationCurveSample();
		else
			return "Cannot get the exploration curve sample - Graph environment not initialised";
	}
	
	public static String getLongestPath(){
		if (env != null)
			return env.getLongestPath();
		else
			return "Cannot get the longest path - Graph environment not initialised";
	}
	
	public static void exit(){
		if (Grapher.GRAPHS_ACTIVATED){
			try {
				synchronized(env){
					while (env != null){
						try {
							env.wait(500);
						} catch (InterruptedException e) {
							System.out.println("TESTAR grapher exit interrupted");
						}
					}
				}
			} catch (Exception e) {} // env may be set to null when we try to sync on it
		}
	}
	
	private static void resetGrapherFields(){
		//System.out.println("TESTAR grapher reset");
		movementsFIFO.clear();
		movementsSync.clear();
		env = null;
		graphing = false;
		walkStopper = null;	
		jipWrapper = null;
	}
	
	@Override
	public void finalize(){
		resetGrapherFields();
		if (exeSrv != null){
			exeSrv.shutdown();
			exeSrv = null;
		}
	}
	
}

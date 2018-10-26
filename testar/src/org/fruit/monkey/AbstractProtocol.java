/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
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


/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import es.upv.staq.testar.ActionStatus;
import es.upv.staq.testar.AdhocServer;
import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.EventHandler;
import es.upv.staq.testar.FlashFeedback;
import es.upv.staq.testar.IEventListener;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.ProtocolUtil;
import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.GraphDB;
import nl.ou.testar.ProcessInfo;
import nl.ou.testar.SutVisualization;
import org.fruit.Assert;
import org.fruit.UnProc;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Taggable;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.actions.ActivateSystem;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.KillProcess;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.devices.AWTMouse;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.devices.MouseButtons;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fruit.alayer.Tags.SystemState;
import static org.fruit.monkey.ConfigTags.LogLevel;
import static org.fruit.monkey.ConfigTags.OutputDir;

public abstract class AbstractProtocol implements UnProc<Settings>,
		//TODO move IEventListerener out of abstract protocol
		IEventListener	{

	public enum Modes{
		Spy,
		GenerateManual,
		Generate, GenerateDebug, Quit, View, AdhocTest, Replay, ReplayDebug;
	}

	protected boolean faultySequence;
	
	private Set<KBKeys> pressed = EnumSet.noneOf(KBKeys.class);
	protected Settings settings;
	protected Modes mode;
	protected Mouse mouse = AWTMouse.build();
	private boolean saveStateSnapshot = false;
	public void setSaveStateSnapshot(boolean saveStateSnapshot) {
		this.saveStateSnapshot = saveStateSnapshot;
	}
	public boolean isSaveStateSnapshot() {
		return saveStateSnapshot;
	}
	protected boolean markParentWidget = false;
	protected int actionCount;
	protected int sequenceCount;
	protected int firstSequenceActionNumber;
	protected int lastSequenceActionNumber;
	double startTime;
	protected List<ProcessInfo> contextRunningProcesses = null;
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractProtocol.class);

	protected double passSeverity = Verdict.SEVERITY_OK;
	protected int generatedSequenceNumber = -1;
	protected Object[] userEvent = null;
	protected Action lastExecutedAction = null;
	protected long lastStamp = -1;
	protected ProtocolUtil protocolUtil = new ProtocolUtil();
	protected EventHandler eventHandler;
	protected Canvas cv;

	protected Pattern clickFilterPattern = null;
	protected Map<String,Matcher> clickFilterMatchers = new WeakHashMap<String,Matcher>();
	protected Pattern suspiciousTitlesPattern = null;
	protected Map<String,Matcher> suspiciousTitlesMatchers = new WeakHashMap<String,Matcher>();

	protected JIPrologWrapper jipWrapper;
	protected double delay = Double.MIN_VALUE;
	private final static double SLOW_MOTION = 2.0;

	protected String forceKillProcess = null;
	protected boolean forceToForeground = false,
			forceNextActionESC = false;

	protected boolean forceToSequenceLengthAfterFail = false;
	protected int testFailTimes = 0;
	protected final int TEST_RETRY_THRESHOLD = 32; // prevent recursive overflow
	protected GraphDB graphDB;

	protected boolean nonSuitableAction = false;

	//TODO: key commands come through java.awt.event but are the key codes same for all OS? if they are the same, then move to platform independent protocol?
	//TODO move to TestarControlKeyCommands
	//TODO: Investigate better shortcut combinations to control TESTAR that does not interfere with SUT
	// (e.g. SHIFT + 1 puts an ! in the notepad and hence interferes with SUT state, but the
	// event is not recorded as a user event).
	/**
	 * Override the default keylistener to implement the TESTAR shortcuts
	 * SHIFT + SPACE
	 * SHIFT + ARROW-UP
	 * SHIFT + ARROW-RIGHT
	 * SHIFT + ARROW-LEFT
	 * SHIFT + ARROW-DOWN
	 * SHIFT + {0, 1, 2, 3, 4}
	 * SHIFT + ALT
	 * @param key
	 */
	@Override
	public void keyDown(KBKeys key){
		pressed.add(key);

		//  SHIFT + SPACE are pressed --> Toggle slow motion test
		if (pressed.contains(KBKeys.VK_SHIFT) && key == KBKeys.VK_SPACE){
			if (this.delay == Double.MIN_VALUE){
				this.delay = settings().get(ConfigTags.TimeToWaitAfterAction).doubleValue();
				settings().set(ConfigTags.TimeToWaitAfterAction, SLOW_MOTION);
			} else{
				settings().set(ConfigTags.TimeToWaitAfterAction, this.delay);
				delay = Double.MIN_VALUE;
			}
		}

		//  SHIFT + ARROW-UP are pressed --> set variable to make a state snapshot
		if(key == KBKeys.VK_UP && pressed.contains(KBKeys.VK_SHIFT))
			saveStateSnapshot = true;

		// SHIFT + ARROW-RIGHT --> go to the next mode
		else if(key == KBKeys.VK_RIGHT && pressed.contains(KBKeys.VK_SHIFT))
			nextMode(true);

		// SHIFT + ARROW-LEFT --> go to the previous mode
		else if(key == KBKeys.VK_LEFT && pressed.contains(KBKeys.VK_SHIFT))
			nextMode(false);

		// SHIFT + ARROW-DOWN --> panic stop
		else if(key == KBKeys.VK_DOWN && pressed.contains(KBKeys.VK_SHIFT)){
			LogSerialiser.log("User requested to stop monkey!\n", LogSerialiser.LogLevel.Info);
			mode = Modes.Quit;
			AdhocServer.stopAdhocServer();
		}

		// SHIFT + 1 --> toggle action visualization
		else if(key == KBKeys.VK_1 && pressed.contains(KBKeys.VK_SHIFT))
			settings().set(ConfigTags.VisualizeActions, !settings().get(ConfigTags.VisualizeActions));

		// SHIFT + 2 --> toggle showing accessibility properties of the widget
		else if(key == KBKeys.VK_2 && pressed.contains(KBKeys.VK_SHIFT))
			settings().set(ConfigTags.DrawWidgetUnderCursor, !settings().get(ConfigTags.DrawWidgetUnderCursor));

		// SHIFT + 3 --> toggle basic or all accessibility properties of the widget
		else if(key == KBKeys.VK_3 && pressed.contains(KBKeys.VK_SHIFT))
			settings().set(ConfigTags.DrawWidgetInfo, !settings().get(ConfigTags.DrawWidgetInfo));

		// SHIFT + 4 --> toggle the widget tree
		else if (key == KBKeys.VK_4  && pressed.contains(KBKeys.VK_SHIFT))
			settings().set(ConfigTags.DrawWidgetTree, !settings.get(ConfigTags.DrawWidgetTree));

		// SHIFT + 0 --> undocumented feature
		else if (key == KBKeys.VK_0  && pressed.contains(KBKeys.VK_SHIFT))
			System.setProperty("DEBUG_WINDOWS_PROCESS_NAMES","true");

		// TODO: Find out if this commented code is anything useful
		/*else if (key == KBKeys.VK_ENTER && pressed.contains(KBKeys.VK_SHIFT)){
			AdhocServer.startAdhocServer();
			mode = Modes.AdhocTest;
			LogSerialiser.log("'" + mode + "' mode active.\n", LogSerialiser.LogLevel.Info);
		}*/

		// In GenerateManual mode you can press any key except SHIFT to add a user keyboard
		// This is because SHIFT is used for the TESTAR shortcuts
		// This is not ideal, because now special characters and capital letters and other events that needs SHIFT
		// cannot be recorded as an user event in GenerateManual....
		else if (!pressed.contains(KBKeys.VK_SHIFT) && mode() == Modes.GenerateManual && userEvent == null){
			//System.out.println("USER_EVENT key_down! " + key.toString());
			userEvent = new Object[]{key}; // would be ideal to set it up at keyUp
		}

		// SHIFT + ALT --> Toggle widget-tree hieracrhy display
		if (pressed.contains(KBKeys.VK_ALT) && pressed.contains(KBKeys.VK_SHIFT))
			markParentWidget = !markParentWidget;
	}

	//TODO: jnativehook is platform independent, but move to TestarControlKeyCommands OR/AND recording user actions
	@Override
	public void keyUp(KBKeys key){
		pressed.remove(key);
	}

	//TODO: jnativehook is platform independent, but move to TestarControlKeyCommands OR/AND recording user actions
	/**
	 * TESTAR does not listen to mouse down clicks in any mode
	 * @param btn
	 * @param x
	 * @param y
	 */
	@Override
	public void mouseDown(MouseButtons btn, double x, double y){}

	//TODO: jnativehook is platform independent, but move to TestarControlKeyCommands OR/AND recording user actions
	/**
	 * In GenerateManual the user can add user events by clicking and the ecent is added when releasing the mouse
	 * @param btn
	 * @param x
	 * @param y
	 */
	@Override
	public void mouseUp(MouseButtons btn, double x, double y){
		// In GenerateManual the user can add user events by clicking
		if (mode() == Modes.GenerateManual && userEvent == null){
			//System.out.println("USER_EVENT mouse_up!");
			userEvent = new Object[]{
					btn,
					new Double(x),
					new Double(y)
			};
		}
	}

	/**
	 * Return the mode TESTAR is currently in
	 * @return
	 */
	public synchronized Modes mode(){ return mode; }

	//TODO think how the modes should be implemented
	/**
	 * Implement the SHIFT + ARROW-LEFT or SHIFT + ARROW-RIGHT toggling mode feature
	 * Show the flashfeedback in the upperleft corner of the screen
	 * @param forward is set in keyDown method
	 */
	private synchronized void nextMode(boolean forward){
		if(forward){
			switch(mode){
			//case Spy: mode = Modes.Generate; break;
			case Spy: userEvent = null; mode = Modes.GenerateManual; break;
			case GenerateManual: mode = Modes.Generate; break;
			case Generate: mode = Modes.GenerateDebug; break;
			case GenerateDebug: mode = Modes.Spy; break;
			case AdhocTest: mode = Modes.Spy; AdhocServer.stopAdhocServer(); break;
			case Replay: mode = Modes.ReplayDebug; break;
			case ReplayDebug: mode = Modes.Replay; break;
			default: break;
			}		
		}else{
			switch(mode){
			case Spy: mode = Modes.GenerateDebug; break;
			//case Generate: mode = Modes.Spy; break;
			case GenerateManual: mode = Modes.Spy; break;
			case Generate: userEvent = null; mode = Modes.GenerateManual; break;
			case GenerateDebug: mode = Modes.Generate; break;
			case AdhocTest: mode = Modes.Spy; AdhocServer.stopAdhocServer(); break;
			case Replay: mode = Modes.ReplayDebug; break;
			case ReplayDebug: mode = Modes.Replay; break;
			default: break;
			}		
		}

		// Add some logging
		// Add the FlashFeedback about the mode you are in in the upper left corner.
		String modeParamS = "";
		if (mode == Modes.GenerateManual)
			modeParamS = " (" + settings.get(ConfigTags.TimeToWaitAfterAction) + " wait time between actions)";

		String modeNfo = "'" + mode + "' mode active." + modeParamS;
		LogSerialiser.log(modeNfo + "\n", LogSerialiser.LogLevel.Info);
		FlashFeedback.flash(modeNfo);
	}

	/**
	 * Set the mode with the given parameter value
	 * @param mode
	 */
	protected synchronized void setMode(Modes mode){
		if (mode() == mode) return;
		List<Modes> modesList = Arrays.asList(Modes.values());
		while (mode() != mode)
			nextMode(modesList.indexOf(mode) > modesList.indexOf(mode()));
	}

	protected final double timeElapsed(){ return Util.time() - startTime; }
	protected Settings settings(){ return settings; }
	protected final GraphDB graphDB(){ return graphDB; }
	protected void beginSequence(SUT system, State state) {}
	protected void finishSequence(File recordedSequence) {}
	protected final int actionCount(){ return actionCount; }
	protected final int sequenceCount(){ return sequenceCount; }
	protected void initialize(Settings settings){}
	protected final int generatedSequenceCount() {return generatedSequenceNumber;}
	protected final Action lastExecutedAction() {return lastExecutedAction;}

	//TODO think about creating pre- and post- methods, for example preSelectAction(), postSelectAction()
	//abstract methods for TESTAR flow:
	protected abstract SUT startSystem() throws SystemStartException;
	protected abstract void stopSystem(SUT system);
	protected abstract State getState(SUT system) throws StateBuildException;
	protected abstract Verdict getVerdict(State state);
	protected abstract Set<Action> deriveActions(SUT system, State state) throws ActionBuildException;
	protected abstract Canvas buildCanvas();
	protected abstract boolean moreActions(State state);
	protected abstract boolean moreSequences();
	protected abstract void processListeners(SUT system);
	protected abstract void waitUserActionLoop(Canvas cv, SUT system, State state, ActionStatus actionStatus);
	protected abstract boolean waitAdhocTestEventLoop(State state, ActionStatus actionStatus);
	protected abstract boolean waitAutomaticAction(SUT system, State state, Taggable fragment, ActionStatus actionStatus);
	protected abstract Action mapUserEvent(State state);
	protected abstract boolean runAction(Canvas cv, SUT system, State state, Taggable fragment);
	protected abstract void runGenerate(SUT system);
	protected abstract void runSpy(SUT system);
	protected abstract void replay();
	protected abstract void detectLoopMode(SUT system);
	protected abstract void quitSUT(SUT system);

	
	// TODO: The methods below are all about visualization of the state, widgets and actions. They need to be moved out of the Abstract Protocol

	protected String lastPrintParentsOf = "null-id";

	/**
	 * This is used for synchronizing the loops of TESTAR between automated and manual (and between two automated loops)
	 *
	 * Action execution listeners override.
	 *
	 * @param system
	 * @param state
	 * @param action
	 */
	protected abstract void actionExecuted(SUT system, State state, Action action);

	//TODO move to SutProfiler, cannot be static as keeps the values
	protected double sutRAMbase;
	protected double sutRAMpeak;
	protected double sutCPUpeak;
	protected double testRAMpeak;
	protected double testCPUpeak;

	private void debugResources(){
		long nowStamp = System.currentTimeMillis();
		double testRAM =  Runtime.getRuntime().totalMemory()/1048576.0;
		if (testRAM > testRAMpeak)
			testRAMpeak = testRAM;
		double testCPU = (nowStamp - lastStamp)/1000.0;
		if (testCPU > testCPUpeak && actionCount != firstSequenceActionNumber)
			testCPUpeak = testCPU;
		//System.out.print("TC: " + String.format("%.3f", testCPU) + // TC = TESTAR_CPU
		//				 " s / TR: " + testRAM + " MB"); // TR = TESTAR_RAM
		lastStamp = nowStamp;
	}


	//TODO move to reporting helper or FileHandling, but cannot be static
	protected void saveSequenceMetrics(String testSequenceName, boolean problems){
		if (Grapher.GRAPHS_ACTIVATED){
			try {
				PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
						settings.get(OutputDir) + File.separator + "metrics" + File.separator + (problems ? "fail_" : "") + testSequenceName + ".csv"))));
				String heading = String.format("%1$7s,%2$5s,%3$9s,%4$8s,%5$7s,%6$12s,%7$15s,%8$13s,%9$12s,%10$10s,%11$9s,%12$11s,%13$10s,%14$7s",
						"verdict",	 // test verdict
						"FAILS",	 // test FAIL count
						"minCvg(%)", // min coverage
						"maxCvg(%)", // max coverage
						"maxpath",	 // longest path
						"graph-states",	 // graph states
						"abstract-states", // abstract states
						"graph-actions", // graph actions
						"test-actions", // test actions
						"SUTRAM(KB)",	 // SUT RAM peak
						"SUTCPU(%)",	 // SUT CPU peak
						"TestRAM(MB)",	 // TESTAR RAM peak
						"TestCPU(s)",	 // TESTAR CPU peak
						"fitness"		 // fitness
						);
				ps.println(heading);
				IEnvironment env = Grapher.getEnvironment();
				IEnvironment.CoverageMetrics cvgMetrics = env.getCoverageMetrics();
				final int VERDICT_WEIGHT = 	1000,
						CVG_WEIGHT = 		  10,
						PATH_WEIGHT = 	 100,
						STATES_WEIGHT = 	  10,
						ACTIONS_WEIGHT = 	1000,
						SUT_WEIGHT = 		   1,
						TEST_WEIGHT = 	1000;
				double fitness = 1 / // 0.0 (best) .. 1.0 (worse)
						((problems ? 1 : 0) * VERDICT_WEIGHT +
								cvgMetrics.getMinCoverage() + cvgMetrics.getMaxCoverage() * CVG_WEIGHT +
								env.getLongestPathLength() * PATH_WEIGHT +
								(env.getGraphStates().size() - 2) * STATES_WEIGHT +
								(1 / (env.getGraphActions().size() + 1) * ACTIONS_WEIGHT) + // avoid division by 0
								(sutRAMpeak + sutCPUpeak) * SUT_WEIGHT +
								(1 / (1 + testRAMpeak + testCPUpeak*1000)) * TEST_WEIGHT // avoid division by 0
								);
				String metrics = String.format("%1$7s,%2$5s,%3$9s,%4$9s,%5$7s,%6$12s,%7$15s,%8$13s,%9$12s,%10$10s,%11$9s,%12$11s,%13$10s,%14$7s",
						(problems ? "FAIL" : "PASS"),		  // verdict
						this.testFailTimes,					  // test FAIL count
						String.format("%.2f", cvgMetrics.getMinCoverage()),
						String.format("%.2f", cvgMetrics.getMaxCoverage()),
						env.getLongestPathLength(), 			  // longest path
						env.getGraphStates().size() - 2,	  // graph states
						env.getGraphStateClusters().size(),	  // abstract states
						env.getGraphActions().size() - 2,	  // graph actions
						this.actionCount - 1,                 // test actions
						sutRAMpeak,						  	  // SUT RAM peak
						String.format("%.2f",sutCPUpeak),	  // SUT CPU peak
						testRAMpeak,						  // TESTAR RAM peak
						String.format("%.3f",testCPUpeak), 	  // TESTAR CPU peak
						fitness		  // fitness
						);
				ps.print(metrics);
				ps.close();
				//System.out.println(heading + "\n" + metrics);
			} catch (NoSuchTagException | FileNotFoundException e) {
				LogSerialiser.log("Metrics serialisation exception" + e.getMessage(), LogSerialiser.LogLevel.Critical);
				//} catch (FileNotFoundException e) {
				//	LogSerialiser.log("Metrics serialisation exception" + e.getMessage(), LogSerialiser.LogLevel.Critical);
			}
		}
	}

	/**
	 * Initializing the TESTAR loop
	 *
	 * @param settings
	 */
	public final void run(final Settings settings) {		
		startTime = Util.time();
		this.settings = settings;
		mode = settings.get(ConfigTags.Mode);
		initialize(settings);
		//TODO move eventlistener out of abstract protocol - maybe a new protocol layer:
		eventHandler = new EventHandler(this);

		graphDB = new GraphDB(settings.get(ConfigTags.GraphDBEnabled),
				settings.get(ConfigTags.GraphDBUrl),
				settings.get(ConfigTags.GraphDBUser),
				settings.get(ConfigTags.GraphDBPassword));

		try {
			if (!settings.get(ConfigTags.UnattendedTests)) {
				LogSerialiser.log("Registering keyboard and mouse hooks\n", LogSerialiser.LogLevel.Debug);
				Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
				logger.setLevel(Level.OFF);
				logger.setUseParentHandlers(false);

				if (GlobalScreen.isNativeHookRegistered()) {
					GlobalScreen.unregisterNativeHook();
				}
				GlobalScreen.registerNativeHook();
				GlobalScreen.addNativeKeyListener(eventHandler);
				GlobalScreen.addNativeMouseListener(eventHandler);
				GlobalScreen.addNativeMouseMotionListener(eventHandler);
				LogSerialiser.log("Successfully registered keyboard and mouse hooks!\n", LogSerialiser.LogLevel.Debug);
			}

			LogSerialiser.log("'" + mode() + "' mode active.\n", LogSerialiser.LogLevel.Info);

			if(mode() == Modes.View){
				new SequenceViewer(settings).run();
			}else if(mode() == Modes.Replay || mode() == Modes.ReplayDebug){
				replay();
			}else {
				SUT system = null;
				detectLoopMode(system);
			}

		} catch (NativeHookException e) {
			LogSerialiser.log("Unable to install keyboard and mouse hooks!\n", LogSerialiser.LogLevel.Critical);
			throw new RuntimeException("Unable to install keyboard and mouse hooks!", e);
		}finally{
			try {
				if (!settings.get(ConfigTags.UnattendedTests)) {
					if (GlobalScreen.isNativeHookRegistered()) {
						LogSerialiser.log("Unregistering keyboard and mouse hooks\n", LogSerialiser.LogLevel.Debug);
						GlobalScreen.removeNativeMouseMotionListener(eventHandler);
						GlobalScreen.removeNativeMouseListener(eventHandler);
						GlobalScreen.removeNativeKeyListener(eventHandler);
						GlobalScreen.unregisterNativeHook();
					}
				}
				AdhocServer.stopAdhocServer();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void storeWidget(String stateID, Widget widget) {
		graphDB.addWidget(stateID, widget);
	}

}

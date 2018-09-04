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
	//TODO move eventListener out of abstract
	IEventListener {

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
	protected boolean markParentWidget = false;
	protected int actionCount;
	protected int sequenceCount;
	protected int firstSequenceActionNumber;
	protected int lastSequenceActionNumber;
	double startTime;
	protected List<ProcessInfo> contextRunningProcesses = null;

	// TODO: DATE-FORMAT
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// Verdict severities
	// PASS
	protected static final double SEVERITY_WARNING = 		   0.00000001; // must be less than FAULT THRESHOLD @test.settings
	protected static final double SEVERITY_SUSPICIOUS_TITLE = 0.00000009; // suspicious title
	// FAIL
	protected static final double SEVERITY_NOT_RESPONDING =   0.99999990; // unresponsive
	protected static final double SEVERITY_NOT_RUNNING =	   0.99999999; // crash? unexpected close?

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

	//TODO think about creating pre- and post- methods, for example preSelectAction(), postSelectAction()
	//abstract methods for TESTAR flow:
	protected final double timeElapsed(){ return Util.time() - startTime; }
	protected Settings settings(){ return settings; }
	protected final GraphDB graphDB(){ return graphDB; }
	protected void beginSequence(SUT system, State state) {}
	protected void finishSequence(File recordedSequence) {}
	protected abstract SUT startSystem() throws SystemStartException;
	protected abstract void stopSystem(SUT system);
	protected abstract State getState(SUT system) throws StateBuildException;
	protected abstract Verdict getVerdict(State state);
	protected abstract Set<Action> deriveActions(SUT system, State state) throws ActionBuildException;
	protected abstract Canvas buildCanvas();
	protected abstract boolean moreActions(State state);
	protected abstract boolean moreSequences();
	protected final int actionCount(){ return actionCount; }
	protected final int sequenceCount(){ return sequenceCount; }
	protected void initialize(Settings settings){}
	protected final int generatedSequenceCount() {return generatedSequenceNumber;}
	protected final Action lastExecutedAction() {return lastExecutedAction;}
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

	//TODO is this process handling Windows specific? move to SystemProcessHandling and call from Default protocol
	/**
	 * If unwanted processes need to be killed, the action returns an action to do that. If the SUT needs
	 * to be put in the foreground, then the action that is returned is putting it in the foreground.
	 * Otherwise it returns null.
	 * @param state
	 * @param actions
	 * @return null if no preSelected actions are needed.
	 */
	protected Action preSelectAction(State state, Set<Action> actions){
		Assert.isTrue(actions != null && !actions.isEmpty());

		//If deriveActions indicated that there are processes that need to be killed
		//because they are in the process filters
		//Then here we will select the action to do that killing

		if (this.forceKillProcess != null){
			System.out.println("DEBUG: preActionSelection, forceKillProcess="+forceKillProcess);
			LogSerialiser.log("Forcing kill-process <" + this.forceKillProcess + "> action\n", LogSerialiser.LogLevel.Info);
			Action a = KillProcess.byName(this.forceKillProcess, 0);
			a.set(Tags.Desc, "Kill Process with name '" + this.forceKillProcess + "'");
			CodingManager.buildIDs(state, a);
			this.forceKillProcess = null;
			return a;
		}
		//If deriveActions indicated that the SUT should be put back in the foreground
		//Then here we will select the action to do that

		else if (this.forceToForeground){
			LogSerialiser.log("Forcing SUT activation (bring to foreground) action\n", LogSerialiser.LogLevel.Info);
			Action a = new ActivateSystem();
			a.set(Tags.Desc, "Bring the system to the foreground.");
			CodingManager.buildIDs(state, a);
			this.forceToForeground = false;
			return a;
		}

		//TODO: This seems not to be used yet...
		// It is set in a method actionExecuted that is not being called anywhere (yet?)
		else if (this.forceNextActionESC){
			System.out.println("DEBUG: Forcing ESC action in preActionSelection");
			LogSerialiser.log("Forcing ESC action\n", LogSerialiser.LogLevel.Info);
			Action a = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
			CodingManager.buildIDs(state, a);
			this.forceNextActionESC = false;
			return a;
		} else
			return null;
	}

	//TODO move to default protocol (platform independent?)
	/**
	 * Returns the next action that will be selected. If unwanted processes need to be killed, the action kills them. If the SUT needs
	 * to be put in the foreground, then the action is putting it in the foreground. Otherwise the action is selected according to
	 * action selection mechanism selected.
	 * @param state
	 * @param actions
	 * @return
	 */
	protected Action selectAction(State state, Set<Action> actions){
		Assert.isTrue(actions != null && !actions.isEmpty());

		Action a = preSelectAction(state, actions);
		if (a != null){
			return a;
		} else
			return Grapher.selectAction(state,actions);
	}


	final static double MAX_ACTION_WAIT_FRAME = 1.0; // (seconds)
	//TODO move the CPU metric to another helper class that is not default "TrashBinCode" or "SUTprofiler"
	//TODO check how well the CPU usage based waiting works
	protected boolean executeAction(SUT system, State state, Action action){
		double waitTime = settings.get(ConfigTags.TimeToWaitAfterAction);
		try{
			double halfWait = waitTime == 0 ? 0.01 : waitTime / 2.0; // seconds
			Util.pause(halfWait); // help for a better match of the state' actions visualization
			action.run(system, state, settings.get(ConfigTags.ActionDuration));
			int waitCycles = (int) (MAX_ACTION_WAIT_FRAME / halfWait);
			long actionCPU;
			do {
				long CPU1[] = NativeLinker.getCPUsage(system);
				Util.pause(halfWait);
				long CPU2[] = NativeLinker.getCPUsage(system);
				actionCPU = ( CPU2[0] + CPU2[1] - CPU1[0] - CPU1[1] );
				waitCycles--;
			} while (actionCPU > 0 && waitCycles > 0);
			return true;
		}catch(ActionFailedException afe){
			return false;
		}
	}

	//TODO move away from abstract, to helper class, call from Default protocol with a setting to turn on/off
	/**
	 * Creates a file out of the given state.
	 * could be more interesting as XML instead of Java Serialisation
	 * @param state
	 */
	protected void saveStateSnapshot(final State state){
		try{
			if(saveStateSnapshot){
				//System.out.println(Utils.treeDesc(state, 2, Tags.Role, Tags.Desc, Tags.Shape, Tags.Blocked));
				Taggable taggable = new TaggableBase();
				taggable.set(SystemState, state);
				LogSerialiser.log("Saving state snapshot...\n", LogSerialiser.LogLevel.Debug);
				File file = Util.generateUniqueFile(settings.get(ConfigTags.OutputDir), "state_snapshot");
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
				oos.writeObject(taggable);
				oos.close();
				saveStateSnapshot = false;
				LogSerialiser.log("Saved state snapshot to " + file.getAbsolutePath() + "\n", LogSerialiser.LogLevel.Info);
			}
		}catch(IOException ioe){
			throw new RuntimeException(ioe);
		}
	}



	//TODO: Is this method needed??
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

	//TODO move to KeyControl or user action recording
	protected boolean isESC(Action action){
		Role r = action.get(Tags.Role, null);
		if (r != null && r.isA(ActionRoles.HitKey)){
			String desc = action.get(Tags.Desc, null);
			if (desc != null && desc.contains("VK_ESCAPE"))
				return true;
		}
		return false;
	}

	//TODO move to KeyControl or user action recording
	protected boolean isNOP(Action action){
		String as = action.toString();
		if (as != null && as.equals(NOP.NOP_ID))
			return true;
		else
			return false;
	}

	protected long stampLastExecutedAction = -1;
	protected long[] lastCPU; // user x system x frame
	protected int escAttempts = 0;
	protected int nopAttempts = 0;
	protected static final int MAX_ESC_ATTEMPTS = 99;
	protected static final int MAX_NOP_ATTEMPTS = 99;
	protected static final long NOP_WAIT_WINDOW = 100; // ms
	protected double sutRAMbase;
	protected double sutRAMpeak;
	protected double sutCPUpeak;
	protected double testRAMpeak;
	protected double testCPUpeak;




	protected void visualizeActions(Canvas canvas, State state, Set<Action> actions){
		SutVisualization.visualizeActions(mode(), settings(), canvas, state, actions);
	}







	//TODO move to profiler helper
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



	//TODO move to reporting helper etc
	/**
	 * Making a log file of a sequence
	 *
	 * @param generatedSequence
	 * @param fileSuffix
	 * @param page
	 */
	private void saveReportPage(String generatedSequence, String fileSuffix, String page){
		try {
			LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
					settings.get(OutputDir) + File.separator + "logs" + File.separator + generatedSequence + "_" + fileSuffix + ".log")))),
					settings.get(LogLevel));
		} catch (NoSuchTagException e3) {
			e3.printStackTrace();
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		}
		LogSerialiser.log(page, LogSerialiser.LogLevel.Critical);
		LogSerialiser.flush(); LogSerialiser.finish(); LogSerialiser.exit();
	}

	//TODO move to reporting helper etc
	protected void saveReport(String[] reportPages, String generatedSequence){
		this.saveReportPage(generatedSequence, "clusters", reportPages[0]);
		this.saveReportPage(generatedSequence, "testable", reportPages[1]);
		this.saveReportPage(generatedSequence, "curve", reportPages[2]);
		this.saveReportPage(generatedSequence, "stats", reportPages[3]);
	}

	//TODO move to reporting helper etc
	protected void copyClassifiedSequence(String generatedSequence, File currentSeq, Verdict verdict){
		String targetFolder = "";
		final double sev = verdict.severity();
		if (sev == Verdict.SEVERITY_OK)
			targetFolder = "sequences_ok";
		else if (sev == SEVERITY_WARNING)
			targetFolder = "sequences_warning";
		else if (sev == SEVERITY_SUSPICIOUS_TITLE)
			targetFolder = "sequences_suspicioustitle";
		else if (sev == SEVERITY_NOT_RESPONDING)
			targetFolder = "sequences_unresponsive";
		else if (sev == SEVERITY_NOT_RUNNING)
			targetFolder = "sequences_unexpectedclose";
		else if (sev == Verdict.SEVERITY_FAIL)
			targetFolder = "sequencces_fail";
		else
			targetFolder = "sequences_other";
		LogSerialiser.log("Copying classified sequence (\"" + generatedSequence + "\") to " + targetFolder + " folder...\n", LogSerialiser.LogLevel.Info);
		try {
			Util.copyToDirectory(currentSeq.getAbsolutePath(), 
					settings.get(ConfigTags.OutputDir) + File.separator + targetFolder, 
					generatedSequence,
					true);
		} catch (NoSuchTagException e) {
			LogSerialiser.log("No such tag exception copying classified test sequence\n", LogSerialiser.LogLevel.Critical);
		} catch (IOException e) {
			LogSerialiser.log("I/O exception copying classified test sequence\n", LogSerialiser.LogLevel.Critical);
		}
		LogSerialiser.log("Copied classified sequence to output <" + targetFolder + "> directory!\n", LogSerialiser.LogLevel.Debug);		
	}


	//TODO move to reporting helper etc
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

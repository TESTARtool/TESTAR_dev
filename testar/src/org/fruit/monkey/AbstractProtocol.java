/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.EventHandler;
import es.upv.staq.testar.FlashFeedback;
import es.upv.staq.testar.IEventListener;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.protocols.ProtocolUtil;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;
import nl.ou.testar.GraphDB;
import nl.ou.testar.SutVisualization;
import org.fruit.Assert;
import org.fruit.UnProc;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Finder;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Taggable;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.fruit.alayer.UsedResources;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Visualizer;
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
import org.fruit.alayer.devices.ProcessHandle;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.SystemStopException;
import org.fruit.alayer.exceptions.WidgetNotFoundException;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static org.fruit.alayer.Tags.ActionDelay;
import static org.fruit.alayer.Tags.ActionDuration;
import static org.fruit.alayer.Tags.ActionSet;
import static org.fruit.alayer.Tags.Desc;
import static org.fruit.alayer.Tags.ExecutedAction;
import static org.fruit.alayer.Tags.OracleVerdict;
import static org.fruit.alayer.Tags.Role;
import static org.fruit.alayer.Tags.SystemState;
import static org.fruit.alayer.Tags.Visualizer;
import static org.fruit.monkey.ConfigTags.LogLevel;
import static org.fruit.monkey.ConfigTags.OutputDir;

public abstract class AbstractProtocol implements UnProc<Settings>,
//TODO move eventListener out of abstract
												  IEventListener {
	
	public static enum Modes{
		Spy,
		GenerateManual,
		Generate, GenerateDebug, Quit, View, AdhocTest, Replay, ReplayDebug;
	}
	
	protected boolean faultySequence;

	private Set<KBKeys> pressed = EnumSet.noneOf(KBKeys.class);
	private Settings settings;
	private Modes mode;
	protected Mouse mouse = AWTMouse.build();
	private boolean saveStateSnapshot = false,
					markParentWidget = false;
	private int actionCount, sequenceCount,
		firstSequenceActionNumber;
	protected int lastSequenceActionNumber;
	double startTime;

	// 2018-06-18: changed the date format into more standard ISO format:
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// Verdict severities
	// PASS
	protected static final double SEVERITY_WARNING = 		   0.00000001; // must be less than FAULT THRESHOLD @test.settings
	protected static final double SEVERITY_SUSPICIOUS_TITLE = 0.00000009; // suspicious title
	// FAIL
	protected static final double SEVERITY_NOT_RESPONDING =   0.99999990; // unresponsive
	protected static final double SEVERITY_NOT_RUNNING =	   0.99999999; // crash? unexpected close?

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractProtocol.class);

	protected double passSeverity = Verdict.SEVERITY_OK;
	private int generatedSequenceNumber = -1;
	private Object[] userEvent = null;
	private Action lastExecutedAction = null;
    private long lastStamp = -1;
	protected ProtocolUtil protocolUtil = new ProtocolUtil();
	protected EventHandler eventHandler;
	protected Canvas cv;

    protected Pattern clickFilterPattern = null;
    protected Map<String,Matcher> clickFilterMatchers = new WeakHashMap<String,Matcher>();
    protected Pattern suspiciousTitlesPattern = null;
    protected Map<String,Matcher> suspiciousTitlesMatchers = new WeakHashMap<String,Matcher>();

	protected JIPrologWrapper jipWrapper;
	private double delay = Double.MIN_VALUE;
	private final static double SLOW_MOTION = 2.0;
    
	protected String forceKillProcess = null;
	protected boolean forceToForeground = false,
			forceNextActionESC = false;
    
	private boolean forceToSequenceLengthAfterFail = false;
	private int testFailTimes = 0;
    private final int TEST_RETRY_THRESHOLD = 32; // prevent recursive overflow
	protected GraphDB graphDB;
    
	protected boolean nonSuitableAction = false;

	//TODO is this process handling Windows specific? move to Windows specific protocol
	protected class ProcessInfo{
		public SUT sut;
		public long pid;
		public long handle;
		public String Desc;
		public ProcessInfo(SUT sut, long pid, long handle, String desc){
			this.sut = sut;
			this.pid = pid;
			this.handle = handle;
			this.Desc = desc;
		}
		public String toString(){
			return "PID <" + this.pid + "> HANDLE <" + this.handle + "> DESC <" + this.Desc + ">";
		}
	}
	protected List<ProcessInfo> contextRunningProcesses = null;

	//TODO native linker is used and that requires platform specific implementation. move to SystemProcessHandling class
	/**
	 * Retrieve a list of Running processes
	 * @param debugTag Tag used in debug output
	 * @return a list of running processes
	 */
	protected List<ProcessInfo> getRunningProcesses(String debugTag){
		List<ProcessInfo> runningProcesses = new ArrayList<ProcessInfo>();
		long pid, handle; String desc;
		List<SUT> runningP = NativeLinker.getNativeProcesses();
		System.out.println("[" + debugTag + "] " + "Running processes (" + runningP.size() + "):");
		int i = 1;
		for (SUT sut : runningP){
			//System.out.println("\t[" + (i++) +  "] " + sut.getStatus());
			pid = sut.get(Tags.PID, Long.MIN_VALUE);
			if (pid != Long.MIN_VALUE){
				handle = sut.get(Tags.HANDLE, Long.MIN_VALUE);
				desc = sut.get(Tags.Desc, null);
				runningProcesses.add(new ProcessInfo(sut,pid,handle,desc));
			}
		}
		return runningProcesses;
	}
	
	final static long MAX_KILL_WINDOW = 10000; // 10 seconds

	//TODO native linker is used and that requires platform specific implementation. move to SystemProcessHandling class
	protected void killTestLaunchedProcesses(){
		boolean kill;
		for (ProcessInfo pi1 : getRunningProcesses("END")){
			kill = true;
			for (ProcessInfo pi2 : this.contextRunningProcesses){
				if (pi1.pid == pi2.pid){
					kill = false;
					break;
				}
			}
			if (kill)
				killProcess(pi1,MAX_KILL_WINDOW);
		}
	}

	//TODO native linker is used and that requires platform specific implementation. move to SystemProcessHandling class
	/**
	 * Kills the SUT process. Also true if the process is not running anymore (killing might not happen)
	 * @param sut
	 * @param KILL_WINDOW
	 * @return
	 */
	protected boolean killRunningProcesses(SUT sut, long KILL_WINDOW){
		boolean allKilled = true;
		for(ProcessHandle ph : Util.makeIterable(sut.get(Tags.ProcessHandles, Collections.<ProcessHandle>emptyList().iterator()))){
			if (ph.name() != null && sut.get(Tags.Desc, "").contains(ph.name())){
				try{
					System.out.println("\tWill kill <" + ph.name() +"> with PID <" + ph.pid() + ">");
					ph.kill();
				} catch (SystemStopException e){
					System.out.println("Exception killing SUT running processes: " + e.getMessage());
					allKilled = false;
				}
			}
		}
		return allKilled;
	}

	//TODO native linker is used and that requires platform specific implementation. move to SystemProcessHandling class
	/**
	 * Kill process with info pi
	 * @param pi
	 * @param KILL_WINDOW indicates a time frame
	 * @return
	 */
	private boolean killProcess(ProcessInfo pi, long KILL_WINDOW){
		if (pi.sut.isRunning()){
			System.out.println("Will kill process: " + pi.toString());
			long now = System.currentTimeMillis(),
				 elapsed;
			do{
				elapsed = System.currentTimeMillis() - now;
				try {
					NativeLinker.getNativeProcessHandle(pi.pid).kill();
				} catch (Exception e){
					System.out.println("\tException trying to kill process: <" + e.getMessage() + "> after <" + elapsed + "> ms");
					Util.pauseMs(500);
				}
			} while (pi.sut.isRunning() && elapsed < KILL_WINDOW);
			return pi.sut.isRunning();
		} else{
			System.out.println("Did not kill process as it is not running: " + pi.toString());
			return true;
		}
	}



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
			protocolUtil.stopAdhocServer();
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
			protocolUtil.startAdhocServer();
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
			case AdhocTest: mode = Modes.Spy; protocolUtil.stopAdhocServer(); break;
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
			case AdhocTest: mode = Modes.Spy; protocolUtil.stopAdhocServer(); break;
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
	protected final Settings settings(){ return settings; }
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

	// TODO: The methods below are all about visualization of the state, widgets and actions. They need to be moved out of the Abstract Protocol

	private String lastPrintParentsOf = "null-id";

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
	private void saveStateSnapshot(final State state){
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

	//TODO move away from abstract, to Default protocol or ManualRecording helper class
	/**
	 * Records user action (for example for Generate-Manual)
	 *
	 * @param state
	 * @return
	 */
	private Action mapUserEvent(State state){
		Assert.notNull(userEvent);		
		if (userEvent[0] instanceof MouseButtons){ // mouse events
			double x = ((Double)userEvent[1]).doubleValue();
			double y = ((Double)userEvent[2]).doubleValue();	
			Widget w = null;
			try {
				w = Util.widgetFromPoint(state, x, y);
				x = 0.5; y = 0.5;
		        if (userEvent[0] == MouseButtons.BUTTON1) // left click
		        	return (new AnnotatingActionCompiler()).leftClickAt(w,x,y);
		        else if (userEvent[0] == MouseButtons.BUTTON3) // right click     
		        	return (new AnnotatingActionCompiler()).rightClickAt(w,x,y);
			} catch (WidgetNotFoundException we){
				System.out.println("Mapping user event ... widget not found @(" + x + "," + y + ")");
				return null;
			}
		} else if (userEvent[0] instanceof KBKeys) // key events
			return (new AnnotatingActionCompiler()).hitKey((KBKeys)userEvent[0]);
		else if (userEvent[0] instanceof String){ // type events
			if (lastExecutedAction == null)
				return null;
			List<Finder> targets = lastExecutedAction.get(Tags.Targets,null);
			if (targets == null || targets.size() != 1)
				return null;
			try {
				Widget w = targets.get(0).apply(state);
				return (new AnnotatingActionCompiler()).clickTypeInto(w,(String)userEvent[0]);
			} catch (WidgetNotFoundException we){
				return null;
			}
		}
			
		return null;
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
	private boolean isESC(Action action){
		Role r = action.get(Tags.Role, null);
		if (r != null && r.isA(ActionRoles.HitKey)){
			String desc = action.get(Tags.Desc, null);
			if (desc != null && desc.contains("VK_ESCAPE"))
				return true;
		}
		return false;
	}

	//TODO move to KeyControl or user action recording
	private boolean isNOP(Action action){
		String as = action.toString();
		if (as != null && as.equals(NOP.NOP_ID))
			return true;
		else
			return false;
	}

	private long stampLastExecutedAction = -1;
	private long[] lastCPU; // user x system x frame
	private int escAttempts = 0,
				nopAttempts = 0;
	private static final int MAX_ESC_ATTEMPTS = 99,
							 MAX_NOP_ATTEMPTS = 99;
	private static final long NOP_WAIT_WINDOW = 100; // ms
	private double sutRAMbase, sutRAMpeak, sutCPUpeak, testRAMpeak, testCPUpeak;


	//TODO move away from abstract, to Default protocol or ManualRecording helper class
	/**
	 * Waits for an user UI action.
	 * Requirement: Mode must be GenerateManual.
	 */
	private void waitUserActionLoop(Canvas cv, SUT system, State state, ActionStatus actionStatus){
		while (mode() == Modes.GenerateManual && !actionStatus.isUserEventAction()){
			if (userEvent != null){
				actionStatus.setAction(mapUserEvent(state));
				actionStatus.setUserEventAction((actionStatus.getAction() != null));
				userEvent = null;
			}
			synchronized(this){
				try {
					this.wait(10);
				} catch (InterruptedException e) {}
			}
			SutVisualization.visualizeState(mode, settings, markParentWidget, mouse, protocolUtil, lastPrintParentsOf, delay, cv, state, system);
			Set<Action> actions = deriveActions(system,state);
			CodingManager.buildIDs(state, actions);
			visualizeActions(cv, state, actions);
		}		
	}

	protected void visualizeActions(Canvas canvas, State state, Set<Action> actions){
		SutVisualization.visualizeActions(mode(), settings(), canvas, state, actions);
	}

	//TODO move away from abstract, to Default protocol or ManualRecording helper class
	/**
	 * Waits for an event (UI action) from adhoc-test.
	 * @param state
	 * @param actionStatus
	 * @return 'true' if problems were found.
	 */
	private boolean waitAdhocTestEventLoop(State state, ActionStatus actionStatus){
		while(protocolUtil.adhocTestServerReader == null || protocolUtil.adhocTestServerWriter == null){
			synchronized(this){
				try {
					this.wait(10);
				} catch (InterruptedException e) {}
			}
		}
		int adhocTestInterval = 10; // ms
		while (System.currentTimeMillis() < stampLastExecutedAction + adhocTestInterval){
			synchronized(this){
				try {
					this.wait(adhocTestInterval - System.currentTimeMillis() + stampLastExecutedAction + 1);
				} catch (InterruptedException e) {}
			}
		}
		do{
			System.out.println("AdhocTest waiting for event ...");
			try{
				protocolUtil.adhocTestServerWriter.write("READY\r\n");
				protocolUtil.adhocTestServerWriter.flush();
			} catch (Exception e){
				return true; // AdhocTest client disconnected?
			}
			try{
				String socketData = protocolUtil.adhocTestServerReader.readLine().trim(); // one event per line
				System.out.println("\t... AdhocTest event = " + socketData);
				userEvent = protocolUtil.compileAdhocTestServerEvent(socketData); // hack into userEvent
				if (userEvent == null){
					protocolUtil.adhocTestServerWriter.write("???\r\n"); // not found
					protocolUtil.adhocTestServerWriter.flush();									
				}else{
					actionStatus.setAction(mapUserEvent(state));
					if (actionStatus.getAction() == null){
						protocolUtil.adhocTestServerWriter.write("404\r\n"); // not found
						protocolUtil.adhocTestServerWriter.flush();
					}
				}
				userEvent = null;
			} catch (Exception e){
				userEvent = null;
				return true; // AdhocTest client disconnected?
			}
		} while (actionStatus.getAction() == null);
		CodingManager.buildIDs(state, actionStatus.getAction());
		return false;
	}

	//TODO part of generate loop, move to Default protocol
	/**
	 * Waits for an automatically selected UI action.
	 * @param system
	 * @param state
	 * @param fragment
	 * @param actionStatus
	 * @return
	 */
	private boolean waitAutomaticAction(SUT system, State state, Taggable fragment, ActionStatus actionStatus){
		Set<Action> actions = deriveActions(system, state);
		CodingManager.buildIDs(state,actions);
		
		if(actions.isEmpty()){
			if (mode() != Modes.Spy && escAttempts >= MAX_ESC_ATTEMPTS){
				LogSerialiser.log("No available actions to execute! Tryed ESC <" + MAX_ESC_ATTEMPTS + "> times. Stopping sequence generation!\n", LogSerialiser.LogLevel.Critical);
				actionStatus.setProblems(true); // problems found
			}
			//----------------------------------
			// THERE MUST ALMOST BE ONE ACTION!
			//----------------------------------
			// if we did not find any actions, then we just hit escape, maybe that works ;-)
			Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
			CodingManager.buildIDs(state, escAction);
			actions.add(escAction);
			escAttempts++;
		} else
			escAttempts = 0;
		
		fragment.set(ActionSet, actions);
		LogSerialiser.log("Built action set!\n", LogSerialiser.LogLevel.Debug);
		visualizeActions(cv, state, actions);

		if(mode() == Modes.Quit) return actionStatus.isProblems();
		LogSerialiser.log("Selecting action...\n", LogSerialiser.LogLevel.Debug);
		if(mode() == Modes.Spy) return false;
		actionStatus.setAction(selectAction(state, actions));
		
		if (actionStatus.getAction() == null){ // (no suitable actions?)
			nonSuitableAction = true;
			return true; // force test sequence end
		}

		actionStatus.setUserEventAction(false);
		
		return false;
	}
	
	// TODO move to default protocol
	private boolean runAction(Canvas cv, SUT system, State state, Taggable fragment){
		long tStart = System.currentTimeMillis();
		LOGGER.info("[RA} start runAction");
		ActionStatus actionStatus = new ActionStatus();
		waitUserActionLoop(cv,system,state,actionStatus);

		cv.begin(); Util.clear(cv);
		SutVisualization.visualizeState(mode, settings, markParentWidget, mouse, protocolUtil, lastPrintParentsOf, delay, cv, state, system);
		LogSerialiser.log("Building action set...\n", LogSerialiser.LogLevel.Debug);

		if (actionStatus.isUserEventAction()){ // user action
			CodingManager.buildIDs(state, actionStatus.getAction());
		} else if (mode() == Modes.AdhocTest){ // adhoc-test action
			if (waitAdhocTestEventLoop(state,actionStatus)){
				cv.end();
				return true; // problems
			}
		} else{ // automatically derived action
			if (waitAutomaticAction(system,state,fragment,actionStatus)){
				cv.end();
				return true; // problems
			} else if (actionStatus.getAction() == null && mode() == Modes.Spy){
				cv.end();
				return false;
			}
		}
		cv.end();

		if (actionStatus.getAction() == null)
			return true; // problems

		if (actionCount == firstSequenceActionNumber && isESC(actionStatus.getAction())){ // first action in the sequence an ESC?
			System.out.println("First action ESC? Switching to NOP to wait for SUT UI ... " + this.timeElapsed());
			Util.pauseMs(NOP_WAIT_WINDOW); // hold-on for UI to react (e.g. scenario: SUT loading ... logo)
			actionStatus.setAction(new NOP());
			CodingManager.buildIDs(state, actionStatus.getAction());
			nopAttempts++; escAttempts = 0;
		} else
			nopAttempts = 0;
		//System.out.println("Selected action: " + action.toShortString() + " ... count of ESC/NOP = " + escAttempts + "/" + nopAttempts);;

		LogSerialiser.log("Selected action '" + actionStatus.getAction() + "'.\n", LogSerialiser.LogLevel.Debug);

		SutVisualization.visualizeSelectedAction(mode, settings, cv, state, actionStatus.getAction());

		if(mode() == Modes.Quit) return actionStatus.isProblems();
		
		boolean isTestAction = nopAttempts >= MAX_NOP_ATTEMPTS || !isNOP(actionStatus.getAction());
			
		if(mode() != Modes.Spy){
			String[] actionRepresentation = Action.getActionRepresentation(state,actionStatus.getAction(),"\t");
			int memUsage = NativeLinker.getMemUsage(system);
			if (memUsage < sutRAMbase)
				sutRAMbase = memUsage;
			if (memUsage - sutRAMbase > sutRAMpeak)
				sutRAMpeak = memUsage - sutRAMbase;
			long currentCPU[] = NativeLinker.getCPUsage(system),
				 userms = currentCPU[0] - lastCPU[0],
				 sysms = currentCPU[1] - lastCPU[1],
				 cpuUsage[] = new long[]{ userms, sysms, currentCPU[2]}; // [2] = CPU frame
			lastCPU = currentCPU;
			if (isTestAction)
				Grapher.notify(state,state.get(Tags.ScreenshotPath, null),
							   actionStatus.getAction(),protocolUtil.getActionshot(state,actionStatus.getAction()),actionRepresentation[1],
						   	   memUsage, cpuUsage);
			LogSerialiser.log(String.format("Executing (%d): %s...", actionCount,
				actionStatus.getAction().get(Desc, actionStatus.getAction().toString())) + "\n", LogSerialiser.LogLevel.Debug);
			//if((actionSucceeded = executeAction(system, state, action))){
			if (actionStatus.isUserEventAction() ||
				(actionStatus.setActionSucceeded(executeAction(system, state, actionStatus.getAction())))){
				//logln(String.format("Executed (%d): %s...", actionCount, action.get(Desc, action.toString())), LogLevel.Info);

				cv.begin();
				Util.clear(cv);
				cv.end(); // (overlay is invalid until new state/actions scan)
				stampLastExecutedAction = System.currentTimeMillis();
				actionExecuted(system,state,actionStatus.getAction()); // notification
				if (actionStatus.isUserEventAction())
					Util.pause(settings.get(ConfigTags.TimeToWaitAfterAction)); // wait between actions
				double sutCPU = ((cpuUsage[0] + cpuUsage[1]) / (double)cpuUsage[2] * 100);
				if (sutCPU > sutCPUpeak)
					sutCPUpeak = sutCPU;
				String cpuPercent = String.format("%.2f", sutCPU) + "%";
				LogSerialiser.log(String.format("Executed [%d]: %s\n%s",
						actionCount,
						"action = " + actionStatus.getAction().get(Tags.ConcreteID) +
						" (" + actionStatus.getAction().get(Tags.AbstractID) + ") @state = " +
						state.get(Tags.ConcreteID) + " (" + state.get(Tags.Abstract_R_ID) + ")\n\tSUT_KB = " +
						memUsage + ", SUT_ms = " + cpuUsage[0] + " x " + cpuUsage[1] + " x " + cpuPercent,
						actionRepresentation[0]) + "\n",
						LogSerialiser.LogLevel.Info);

				/*System.out.print(String.format(
						"S[%1$" + (1 + (int)Math.log10((double)settings.get(ConfigTags.Sequences))) + "d=%2$" + (1 + (int)Math.log10((double)generatedSequenceNumber)) + "d]-" + // S = test Sequence
						"A[%3$" + (1 + (int)Math.log10((double)settings().get(ConfigTags.SequenceLength))) + // A = Action
						"d] <%4$3s@%5$3s KCVG>... SR = %6$8d KB / SC = %7$7s ... ", // KCVG = % CVG of Known UI space @ known UI space scale; SR = SUT_RAM; SC = SUT_CPU
						sequenceCount, generatedSequenceNumber, actionCount,
						Grapher.GRAPHS_ACTIVATED ? Grapher.getEnvironment().getExplorationCurveSampleCvg() : -1,
						Grapher.GRAPHS_ACTIVATED ? Grapher.getEnvironment().convertKCVG(Grapher.getEnvironment().getExplorationCurveSampleScale()) : -1,
						memUsage, cpuPercent)); debugResources();*/
				//System.out.print(" ... L/S/T: " + LogSerialiser.queueLength() + "/" + ScreenshotSerialiser.queueLength() + "/" + TestSerialiser.queueLength()); // L/S/T = Log/Scr/Test queues

				//System.out.print("\n");
				//logln(Grapher.getExplorationCurveSample(),LogLevel.Info);
				//logln(Grapher.getLongestPath() + "\n",LogLevel.Info);
				if (mode() == Modes.AdhocTest){
					try {
						protocolUtil.adhocTestServerWriter.write("OK\r\n"); // adhoc action executed
						protocolUtil.adhocTestServerWriter.flush();
					} catch (Exception e){} // AdhocTest client disconnected?
				}

				if (isTestAction && actionStatus.isActionSucceeded())
					actionCount++;
				fragment.set(ExecutedAction, actionStatus.getAction());
				fragment.set(ActionDuration, settings().get(ConfigTags.ActionDuration));
				fragment.set(ActionDelay, settings().get(ConfigTags.TimeToWaitAfterAction));
				LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);
				//oos.writeObject(fragment);
				TestSerialiser.write(fragment);
	
				LogSerialiser.log("Wrote fragment to sequence file!\n", LogSerialiser.LogLevel.Debug);
			}else{
				LogSerialiser.log("Execution of action failed!\n");
				try {
					protocolUtil.adhocTestServerWriter.write("FAIL\r\n"); // action execution failed
					protocolUtil.adhocTestServerWriter.flush();
				} catch (Exception e) {
					LogSerialiser.log("protocolUtil Failed!\n");
				} // AdhocTest client disconnected?
			}				
		}
		
		lastExecutedAction = actionStatus.getAction();
		lastExecutedAction.set(Tags.UsedResources, new UsedResources(lastCPU[0],lastCPU[1],sutRAMbase,sutRAMpeak).toString());
		lastExecutedAction.set(Tags.Representation, Action.getActionRepresentation(state,lastExecutedAction,"\t")[1]);
		State newState = getState(system);
		graphDB.addState(newState);

		if(lastExecutedAction.get(Tags.TargetID,"no_target").equals("no_target")) {
			//TODO this does not work in all cases check (the last executed action tag does not always have a description
			//System.out.println("No Target for Action: "+ lastExecutedAction.get(Tags.Desc, ""));
			graphDB.addActionOnState(state.get(Tags.ConcreteID),lastExecutedAction, newState.get(Tags.ConcreteID));
		} else {
			graphDB.addAction( lastExecutedAction, newState.get(Tags.ConcreteID));
		}
        LOGGER.info("[RA] runAction finished in {} ms",System.currentTimeMillis()-tStart);
		if(mode() == Modes.Quit) return actionStatus.isProblems();
		if(!actionStatus.isActionSucceeded()){
			return true;
		}
		
		return actionStatus.isProblems();
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

	// TODO move to default protocol
	private void runTest(){
		LogSerialiser.finish(); LogSerialiser.exit();
		sequenceCount = 1;
		lastStamp = System.currentTimeMillis();
		escAttempts = 0; nopAttempts = 0;
		boolean problems;
		while(mode() != Modes.Quit && moreSequences()){
			long tStart = System.currentTimeMillis();
			LOGGER.info("[RT] Runtest started for sequence {}",sequenceCount);

			//
			String generatedSequence = Util.generateUniqueFile(settings.get(ConfigTags.OutputDir) + File.separator + "sequences", "sequence").getName();
			generatedSequenceNumber = new Integer(generatedSequence.replace("sequence", ""));

			sutRAMbase = Double.MAX_VALUE; sutRAMpeak = 0.0; sutCPUpeak = 0.0; testRAMpeak = 0.0; testCPUpeak = 0.0;

			try {
				LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
					settings.get(OutputDir) + File.separator + "logs" + File.separator + generatedSequence + ".log")))),
					settings.get(LogLevel));
			} catch (NoSuchTagException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (FileNotFoundException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			if (mode() == Modes.GenerateManual)
				setMode(Modes.Spy);
			jipWrapper = new JIPrologWrapper();
			Grapher.grapher(generatedSequence,
							settings.get(ConfigTags.SequenceLength),
							settings.get(ConfigTags.AlgorithmFormsFilling),
							settings.get(ConfigTags.TypingTextsForExecutedAction).intValue(),
							settings.get(ConfigTags.TestGenerator),
							settings.get(ConfigTags.MaxReward),
							settings.get(ConfigTags.Discount),
							settings.get(ConfigTags.ExplorationSampleInterval),
							settings.get(ConfigTags.GraphsActivated),
							settings.get(ConfigTags.PrologActivated),
							settings.get(ConfigTags.ForceToSequenceLength) && this.forceToSequenceLengthAfterFail ?
									true :
									settings.get(ConfigTags.GraphResuming),
							settings.get(ConfigTags.OfflineGraphConversion),
							jipWrapper);
			Grapher.waitEnvironment();
			ScreenshotSerialiser.start(generatedSequence);
			
			problems = false;
			if (!forceToSequenceLengthAfterFail) passSeverity = Verdict.SEVERITY_OK;
			//actionCount = 0;
			if (this.forceToSequenceLengthAfterFail){
				this.forceToSequenceLengthAfterFail = false;
				this.testFailTimes++;
				this.lastSequenceActionNumber = settings().get(ConfigTags.SequenceLength);
			} else{
				if (settings.get(ConfigTags.GraphsActivated) && settings.get(ConfigTags.GraphResuming))
					actionCount = Grapher.getEnvironment().getGraphActions().size() + 1;
				else
					actionCount = 1;
				this.testFailTimes = 0;
				lastSequenceActionNumber = settings().get(ConfigTags.SequenceLength) + actionCount - 1;
			}
			firstSequenceActionNumber = actionCount;

			LogSerialiser.log("Creating new sequence file...\n", LogSerialiser.LogLevel.Debug);
			final File currentSeq = new File(settings.get(ConfigTags.TempDir) + File.separator + "tmpsequence");
			try {
				Util.delete(currentSeq);
			} catch (IOException e2) {
				LogSerialiser.log("I/O exception deleting <" + currentSeq + ">\n", LogSerialiser.LogLevel.Critical);
			}
			//oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(currentSeq), 50000000));
			//raf = new RandomAccessFile(currentSeq, "rw");
			//oos = new ObjectOutputStream(new FileOutputStream(raf.getFD()));
			try {
				//TestSerialiser.start(new RandomAccessFile(currentSeq, "rw"));
				TestSerialiser.start(new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(currentSeq))));
				LogSerialiser.log("Created new sequence file!\n", LogSerialiser.LogLevel.Debug);
			} catch (IOException e) {
				LogSerialiser.log("I/O exception creating new sequence file\n", LogSerialiser.LogLevel.Critical);
			}
			//} catch (FileNotFoundException e1) {
			//	LogSerialiser.log("File not found exception creating random test file\n", LogSerialiser.LogLevel.Critical);
			//}

			LogSerialiser.log("Building canvas...\n", LogSerialiser.LogLevel.Debug);
			//Canvas cv = buildCanvas();
			this.cv = buildCanvas();
			//logln(Util.dateString("dd.MMMMM.yyyy HH:mm:ss") + " Starting system...", LogLevel.Info);
			String startDateString = Util.dateString(DATE_FORMAT);
			LogSerialiser.log(startDateString + " Starting SUT ...\n", LogSerialiser.LogLevel.Info);

			SUT system = null;
			
			try{

				system = startSystem();

				lastCPU = NativeLinker.getCPUsage(system);
				
				//SUT system = WinProcess.fromProcName("firefox.exe");
				//logln("System is running!", LogLevel.Debug);
				LogSerialiser.log("SUT is running!\n", LogSerialiser.LogLevel.Debug);
				//logln("Starting sequence " + sequenceCount, LogLevel.Info);
				LogSerialiser.log("Obtaining system state before beginSequence...\n", LogSerialiser.LogLevel.Debug);
				State state = getState(system);
				LogSerialiser.log("Starting sequence " + sequenceCount + " (output as: " + generatedSequence + ")\n\n", LogSerialiser.LogLevel.Info);
				beginSequence(system, state);
				LogSerialiser.log("Obtaining system state after beginSequence...\n", LogSerialiser.LogLevel.Debug);
				state = getState(system);
				//Store ( initial )state
				graphDB.addState(state,true);
				LogSerialiser.log("Successfully obtained system state!\n", LogSerialiser.LogLevel.Debug);
				saveStateSnapshot(state);
	
				Taggable fragment = new TaggableBase();
				fragment.set(SystemState, state);
							
				Verdict verdict = state.get(OracleVerdict, Verdict.OK);
				if (faultySequence) problems = true;
				fragment.set(OracleVerdict, verdict);
				int waitCycleIdx = 0;
				long[] waitCycles = new long[]{1, 10, 25, 50}; // ms
				long spyCycle = -1;
				String stateID, lastStateID = state.get(Tags.ConcreteID);
				while(mode() != Modes.Quit && moreActions(state)){
					if (problems)
						faultySequence = true;
					else{
						problems = runAction(cv,system,state,fragment);
						if (mode() == Modes.Spy){
							stateID = state.get(Tags.ConcreteID);
							if (stateID.equals(lastStateID)){
								if (System.currentTimeMillis() - spyCycle > waitCycles[waitCycleIdx]){
									spyCycle = System.currentTimeMillis();
									if (waitCycleIdx < waitCycles.length - 1)
										waitCycleIdx++;
								} else
									continue;
							} else{
								lastStateID = stateID;
								waitCycleIdx = 0;
							}
						}
						LogSerialiser.log("Obtaining system state...\n", LogSerialiser.LogLevel.Debug);
						state = getState(system);
						graphDB.addState(state);
						if (faultySequence) problems = true;
						LogSerialiser.log("Successfully obtained system state!\n", LogSerialiser.LogLevel.Debug);
						if (mode() != Modes.Spy){
							saveStateSnapshot(state);
							verdict = state.get(OracleVerdict, Verdict.OK);
							fragment.set(OracleVerdict, verdict);
							fragment = new TaggableBase();
							fragment.set(SystemState, state);
						}
					}
				}
	
				LogSerialiser.log("Shutting down the SUT...\n", LogSerialiser.LogLevel.Info);
				stopSystem(system);
				//If stopSystem did not really stop the system, we will do it for you ;-)
				if (system != null && system.isRunning())
					system.stop();
				LogSerialiser.log("... SUT has been shut down!\n", LogSerialiser.LogLevel.Debug);
				
				ScreenshotSerialiser.finish();
				LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);
				TestSerialiser.write(fragment);
				TestSerialiser.finish();
				LogSerialiser.log("Wrote fragment to sequence file!\n", LogSerialiser.LogLevel.Debug);
				
				Grapher.walkFinished(!problems,
						 			 mode() == Modes.Spy ? null : state,
						 			 protocolUtil.getStateshot(state));
				
				LogSerialiser.log("Sequence " + sequenceCount + " finished.\n", LogSerialiser.LogLevel.Info);
				if(problems)
					LogSerialiser.log("Sequence contained problems!\n", LogSerialiser.LogLevel.Critical);
								
				finishSequence(currentSeq);
	
				//System.out.println("currentseq: " + currentSeq);
				
				Verdict finalVerdict = verdict.join(new Verdict(passSeverity,"",Util.NullVisualizer));
				
				if (!settings().get(ConfigTags.OnlySaveFaultySequences) ||
					finalVerdict.severity() >= settings().get(ConfigTags.FaultThreshold)){
					//String generatedSequence = Util.generateUniqueFile(settings.get(ConfigTags.OutputDir) + File.separator + "sequences", "sequence").getName();
					LogSerialiser.log("Copying generated sequence (\"" + generatedSequence + "\") to output directory...\n", LogSerialiser.LogLevel.Info);
					try {
						Util.copyToDirectory(currentSeq.getAbsolutePath(),
								settings.get(ConfigTags.OutputDir) + File.separator + "sequences", 
								generatedSequence,
								true);
						LogSerialiser.log("Copied generated sequence to output directory!\n", LogSerialiser.LogLevel.Debug);					
					} catch (NoSuchTagException e) {
						LogSerialiser.log("No such tag exception copying test sequence\n", LogSerialiser.LogLevel.Critical);
					} catch (IOException e) {
						LogSerialiser.log("I/O exception copying test sequence\n", LogSerialiser.LogLevel.Critical);
					}
					copyClassifiedSequence(generatedSequence, currentSeq, finalVerdict);
				}
				if(!problems)
					this.forceToSequenceLengthAfterFail = false;
	
				LogSerialiser.log("Releasing canvas...\n", LogSerialiser.LogLevel.Debug);
				cv.release();
				
				saveSequenceMetrics(generatedSequence,problems);
				
				ScreenshotSerialiser.exit(); final String[] reportPages = Grapher.getReport(this.firstSequenceActionNumber); // screenshots must be serialised
				if (reportPages == null)
					LogSerialiser.log("NULL report pages\n", LogSerialiser.LogLevel.Critical);
				TestSerialiser.exit();
				String stopDateString =  Util.dateString(DATE_FORMAT),
					   durationDateString = Util.diffDateString(DATE_FORMAT, startDateString, stopDateString);
				LogSerialiser.log("TESTAR stopped execution at " + stopDateString + "\n", LogSerialiser.LogLevel.Critical);
				LogSerialiser.log("Test duration was " + durationDateString + "\n", LogSerialiser.LogLevel.Critical);
				LogSerialiser.flush(); LogSerialiser.finish(); LogSerialiser.exit();

				if (reportPages != null) this.saveReport(reportPages, generatedSequence);; // save report

				sequenceCount++;

			} catch(Exception e){
				System.out.println("Thread: name="+Thread.currentThread().getName()+",id="+Thread.currentThread().getId()+", SUT throws exception");
				e.printStackTrace();
				this.killTestLaunchedProcesses();
				ScreenshotSerialiser.finish();
				TestSerialiser.finish();				
				Grapher.walkFinished(false, null, null);
				ScreenshotSerialiser.exit(); final String[] reportPages = Grapher.getReport(this.firstSequenceActionNumber);  // screenshots must be serialised
				if (reportPages == null)
					LogSerialiser.log("NULL report pages\n", LogSerialiser.LogLevel.Critical);
				LogSerialiser.log("Exception <" + e.getMessage() + "> has been caught\n", LogSerialiser.LogLevel.Critical); // screenshots must be serialised
				int i=1; StringBuffer trace = new StringBuffer();
				for(StackTraceElement t : e.getStackTrace())
				   trace.append("\n\t[" + i++ + "] " + t.toString());
				System.out.println("Exception <" + e.getMessage() + "> has been caught; Stack trace:" + trace.toString());
				stopSystem(system);
				if (system != null && system.isRunning())
					system.stop();
				TestSerialiser.exit();
				LogSerialiser.flush(); LogSerialiser.finish(); LogSerialiser.exit();
				if (reportPages != null) this.saveReport(reportPages, generatedSequence);; // save report
				this.mode = Modes.Quit; // System.exit(1);
			}
			LOGGER.info("[RT] Runtest finished for sequence {} in {} ms",sequenceCount,System.currentTimeMillis()-tStart);
		}
		if (settings().get(ConfigTags.ForceToSequenceLength).booleanValue() &&  // force a test sequence length in presence of FAIL
				this.actionCount <= settings().get(ConfigTags.SequenceLength) && mode() != Modes.Quit && testFailTimes < TEST_RETRY_THRESHOLD){
			this.forceToSequenceLengthAfterFail = true;
			System.out.println("Resuming test after FAIL at action number <" + this.actionCount + ">");
 			runTest(); // continue testing
		} else
			this.forceToSequenceLengthAfterFail = false;
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
	private void saveReport(String[] reportPages, String generatedSequence){
		this.saveReportPage(generatedSequence, "clusters", reportPages[0]);
		this.saveReportPage(generatedSequence, "testable", reportPages[1]);
		this.saveReportPage(generatedSequence, "curve", reportPages[2]);
		this.saveReportPage(generatedSequence, "stats", reportPages[3]);
	}

	//TODO move to reporting helper etc
	private void copyClassifiedSequence(String generatedSequence, File currentSeq, Verdict verdict){
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
	private void saveSequenceMetrics(String testSequenceName, boolean problems){
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
			}else if(mode() == Modes.Generate || mode() == Modes.Spy || mode() == Modes.GenerateDebug){
				runTest();
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
                protocolUtil.stopAdhocServer();
            } catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	//TODO move to default protocol
	private void replay(){
		boolean graphsActivated = Grapher.GRAPHS_ACTIVATED;
		Grapher.GRAPHS_ACTIVATED = false;
		actionCount = 1;
		boolean success = true;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		GZIPInputStream gis = null;
		ObjectInputStream ois = null;
		SUT system = startSystem();
		try{
			File seqFile = new File(settings.get(ConfigTags.PathToReplaySequence));
			//ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(seqFile)));
			fis = new FileInputStream(seqFile);
			bis = new BufferedInputStream(fis);
			gis = new GZIPInputStream(bis);
			ois = new ObjectInputStream(gis);

			Canvas cv = buildCanvas();
			State state = getState(system);

			String replayMessage;
			
			double rrt = settings.get(ConfigTags.ReplayRetryTime);
			
			while(success && mode() != Modes.Quit){
				Taggable fragment;
				try{
					fragment = (Taggable) ois.readObject();
				} catch(IOException ioe){
					success = true;
					break;
				}

				success = false;
				int tries = 0;
				double start = Util.time();

				while(!success && (Util.time() - start < rrt)){
					tries++;
					cv.begin(); Util.clear(cv);
					SutVisualization.visualizeState(mode, settings, markParentWidget, mouse, protocolUtil, lastPrintParentsOf, delay, cv, state, system);
					cv.end();

					if(mode() == Modes.Quit) break;
					Action action = fragment.get(ExecutedAction, new NOP());
					SutVisualization.visualizeSelectedAction(mode, settings, cv, state, action);
					if(mode() == Modes.Quit) break;

					double actionDuration = settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay) ? fragment.get(Tags.ActionDuration, 0.0) : settings.get(ConfigTags.ActionDuration);
					double actionDelay = settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay) ? fragment.get(Tags.ActionDelay, 0.0) : settings.get(ConfigTags.TimeToWaitAfterAction);

					try{
						if(tries < 2){
							replayMessage = String.format("Trying to execute (%d): %s... [time window = " + rrt + "]", actionCount, action.get(Desc, action.toString()));
							//System.out.println(replayMessage);
							LogSerialiser.log(replayMessage, LogSerialiser.LogLevel.Info);
						 }else{
							if(tries % 50 == 0)
								LogSerialiser.log(".\n", LogSerialiser.LogLevel.Info);
							else
								LogSerialiser.log(".", LogSerialiser.LogLevel.Info);
						}

						action.run(system, state, actionDuration);
						success = true;
						actionCount++;
						LogSerialiser.log("Success!\n", LogSerialiser.LogLevel.Info);
					} catch(ActionFailedException afe){}

					Util.pause(actionDelay);

					if(mode() == Modes.Quit) break;
					state = getState(system);
				}

			}

			cv.release();
			//ois.close();
			stopSystem(system);
			if (system != null && system.isRunning())
				system.stop();

		} catch(IOException ioe){
			throw new RuntimeException("Cannot read file.", ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException("Cannot read file.", cnfe);
		} finally {
			if (ois != null){
				try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (gis != null){
				try { gis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (bis != null){
				try { bis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (fis != null){
				try { fis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (cv != null)
				cv.release();
			if (system != null)
				system.stop();			
		}

		if(success){
			String msg = "Sequence successfully replayed!\n";
			System.out.println(msg);
			LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);
			
		} else{
			String msg = "Failed to replay sequence.\n";
			System.out.println(msg);
			LogSerialiser.log(msg, LogSerialiser.LogLevel.Critical);
		}
		LogSerialiser.finish();
		Grapher.GRAPHS_ACTIVATED = graphsActivated;
	}

	protected void storeWidget(String stateID, Widget widget) {
		graphDB.addWidget(stateID, widget);
	}

	
}

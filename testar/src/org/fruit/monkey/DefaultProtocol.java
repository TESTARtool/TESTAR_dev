/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 Open Universiteit - www.ou.nl
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


package org.fruit.monkey;

import static org.fruit.alayer.Tags.ActionDelay;
import static org.fruit.alayer.Tags.ActionDuration;
import static org.fruit.alayer.Tags.ActionSet;
import static org.fruit.alayer.Tags.Desc;
import static org.fruit.alayer.Tags.ExecutedAction;
import static org.fruit.alayer.Tags.IsRunning;
import static org.fruit.alayer.Tags.OracleVerdict;
import static org.fruit.alayer.Tags.SystemState;
import static org.fruit.alayer.Tags.Title;
import static org.fruit.monkey.ConfigTags.LogLevel;
import static org.fruit.monkey.ConfigTags.OutputDir;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.concurrent.Semaphore;

import es.upv.staq.testar.*;
import es.upv.staq.testar.graph.IEnvironment;
import nl.ou.testar.*;
import org.fruit.Assert;
import org.fruit.Drag;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Action;
import org.fruit.alayer.AutomationCache;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Finder;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.StateBuilder;
import org.fruit.alayer.StrokePattern;
import org.fruit.alayer.Taggable;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.fruit.alayer.UsedResources;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.AWTMouse;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.devices.MouseButtons;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.WidgetNotFoundException;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.managers.DataManager;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.LoggerFactory;

public class DefaultProtocol extends RuntimeControlsProtocol {

	protected boolean faultySequence;

	protected Mouse mouse = AWTMouse.build();
	protected State state = null,
			lastState = null;
	protected int nonReactingActionNumber;
	protected Semaphore semaphore = new Semaphore(1, true);
	private Verdict processVerdict= Verdict.OK;
	protected void setProcessVerdict(Verdict processVerdict) {
		this.processVerdict = processVerdict;
	}
	protected Verdict getProcessVerdict() {
		return this.processVerdict;
	}
	protected boolean processListenerEnabled;
	protected String lastPrintParentsOf = "null-id";
	protected int actionCount;
	protected final int actionCount(){ return actionCount; }
	protected int sequenceCount;
	protected final int sequenceCount(){ return sequenceCount; }
	protected int firstSequenceActionNumber;
	protected int lastSequenceActionNumber;
	double startTime;
	protected final double timeElapsed(){ return Util.time() - startTime; }
	protected List<ProcessInfo> contextRunningProcesses = null;
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractProtocol.class);
	protected double passSeverity = Verdict.SEVERITY_OK;
	protected int generatedSequenceNumber = -1;
	protected final int generatedSequenceCount() {return generatedSequenceNumber;}
	protected Action lastExecutedAction = null;
	protected final Action lastExecutedAction() {return lastExecutedAction;}
	protected long lastStamp = -1;
	protected ProtocolUtil protocolUtil = new ProtocolUtil();
	protected EventHandler eventHandler;
	protected Canvas cv;
	protected Pattern clickFilterPattern = null;
	protected Map<String,Matcher> clickFilterMatchers = new WeakHashMap<String,Matcher>();
	protected Pattern suspiciousTitlesPattern = null;
	protected Map<String,Matcher> suspiciousTitlesMatchers = new WeakHashMap<String,Matcher>();
	private StateBuilder builder;
	protected JIPrologWrapper jipWrapper;
	protected String forceKillProcess = null;
	protected boolean forceToForeground = false,
			forceNextActionESC = false;
	protected boolean forceToSequenceLengthAfterFail = false;
	protected int testFailTimes = 0;
	protected boolean nonSuitableAction = false;
	protected final int TEST_RETRY_THRESHOLD = 32; // prevent recursive overflow
	protected final GraphDB graphDB(){ return graphDB; }
	protected GraphDB graphDB;

	protected final static Pen RedPen = Pen.newPen().setColor(Color.Red).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build(),
			BluePen = Pen.newPen().setColor(Color.Blue).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build();


	protected void storeWidget(String stateID, Widget widget) {
		graphDB.addWidget(stateID, widget);
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

	protected void initialize(Settings settings){
		//builder = new UIAStateBuilder(settings.get(ConfigTags.TimeToFreeze));
		builder = NativeLinker.getNativeStateBuilder(
				settings.get(ConfigTags.TimeToFreeze),
				settings.get(ConfigTags.AccessBridgeEnabled),
				settings.get(ConfigTags.SUTProcesses)
				);
	}

	@Override
	protected void initTestSession() {

	}

	@Override
	protected void preSequencePreparations() {

	}

	protected Canvas buildCanvas() {
		//return GDIScreenCanvas.fromPrimaryMonitor(Pen.DefaultPen);
		return NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT);
	}

	@Override
	protected void beginSequence(SUT system, State state){
		faultySequence = false;
		nonReactingActionNumber = 0;
	}

	@Override
	protected void finishSequence(File recordedSequence){
		SystemProcessHandling.killTestLaunchedProcesses(this.contextRunningProcesses);
	}
	
	//TODO: Move out of DefaultProtocol?
	protected boolean enableProcessListeners(){
		//User doesn't want to enable
		if(!settings().get(ConfigTags.ProcessListenerEnabled))
			return false;
		//Only for SUTs executed with command_line
		if(!settings().get(ConfigTags.SUTConnector).equals("COMMAND_LINE"))
			return false;

		String path = settings().get(ConfigTags.SUTConnectorValue);
		//Disable for browsers
		if(path.contains("chrome.exe") || path.contains("iexplore.exe") || path.contains("firefox.exe") || path.contains("MicrosoftEdge"))
			return false;

		return true;
	}
	/**
	 * If SUT process is invoked through COMMAND_LINE,
	 * this method create threads to work with oracles at the process level.
	 */
	protected void processListeners(SUT system) {

		//Only if we enabled ProcessListener and executed SUT with command_line
		if(processListenerEnabled){
			final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

			//Process Oracles use SuspiciousProcessOutput regular expression from test settings file
			Pattern processOracles = Pattern.compile(settings().get(ConfigTags.SuspiciousProcessOutput), Pattern.UNICODE_CHARACTER_CLASS);
			//Process Logs use ProcessLogs regular expression from test settings file
			Pattern processLogs= Pattern.compile(settings().get(ConfigTags.ProcessLogs), Pattern.UNICODE_CHARACTER_CLASS);

			int seqn = generatedSequenceCount();
			//Create File to save the logs of these oracles
			File dir = new File("output/ProcessLogs");
			if(!dir.exists())
				dir.mkdirs();
			
			//Prepare runnable to read Error buffer
			Runnable readErrors = new Runnable() {
				public void run() {
					try {
						PrintWriter writerError;
						BufferedReader input = new BufferedReader(new InputStreamReader(system.get(Tags.StdErr)));
						String actionId = "";
						String ch;
						Matcher mOracles, mLogs;
						while (system.isRunning() && (ch = input.readLine()) != null)
						{	
							mOracles = processOracles.matcher(ch);
							mLogs= processLogs.matcher(ch);

							if(processOracles!=null && mOracles.matches()) {		

								/*try {
									semaphore.acquire();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/

								//Prepare Verdict report
								State state = getState(system);
								actionId=lastExecutedAction().get(Tags.ConcreteID);
								Visualizer visualizer = Util.NullVisualizer;
								// visualize the problematic state
								if(state.get(Tags.Shape, null) != null)
									visualizer = new ShapeVisualizer(RedPen, state.get(Tags.Shape), "Suspicious Title", 0.5, 0.5);
								Verdict verdict = new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE,
										"Process Listener suspicious title: '" + ch + ", on Action:	'"+actionId+".", visualizer);

								setProcessVerdict(verdict);

								faultySequence = true;
								
								//Prepare Log report
								String DateString = Util.dateString(DATE_FORMAT);
								System.out.println("SUT StdErr:	" +ch);

								writerError = new PrintWriter(new FileWriter(dir+"/sequence"+seqn+"_StdErr.log", true));
								if(lastExecutedAction()!=null)
									actionId=lastExecutedAction().get(Tags.ConcreteID);
								writerError.println(DateString+"	on Action:	"+actionId+"	SUT StdErr:	" +ch);
								writerError.flush();
								writerError.close();

								//semaphore.release();

							}
							//processOracle has priority
							else if(processLogs!=null && mLogs.matches()) {
								String DateString = Util.dateString(DATE_FORMAT);
								System.out.println("SUT Log StdErr:	" +ch);

								writerError = new PrintWriter(new FileWriter(dir+"/sequence"+seqn+"_StdErr.log", true));
								if(lastExecutedAction()!=null)
									actionId=lastExecutedAction().get(Tags.ConcreteID);
								writerError.println(DateString+"	on Action:	"+actionId+"	SUT StdErr:	" +ch);
								writerError.flush();
								writerError.close();
							}
						}
						
						input.close();
						//Thread.currentThread().interrupt();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			//Prepare runnable to read Output buffer
			Runnable readOutput = new Runnable() {
				public void run() {
					try {
						PrintWriter writerOut;
						BufferedReader input = new BufferedReader(new InputStreamReader(system.get(Tags.StdOut)));
						String actionId = "";
						String ch;
						Matcher mOracles, mLogs;
						while (system.isRunning() && (ch = input.readLine()) != null)
						{	
							mOracles = processOracles.matcher(ch);
							mLogs = processLogs.matcher(ch);
							
							if(processOracles!=null && mOracles.matches()) {	

								/*try {
									semaphore.acquire();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/

								//Prepare Verdict report
								State state = getState(system);
								actionId=lastExecutedAction().get(Tags.ConcreteID);
								Visualizer visualizer = Util.NullVisualizer;
								// visualize the problematic state
								if(state.get(Tags.Shape, null) != null)
									visualizer = new ShapeVisualizer(RedPen, state.get(Tags.Shape), "Suspicious Title", 0.5, 0.5);
								Verdict verdict = new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE,
										"Process Listener suspicious title: '" + ch + ", on Action:	'"+actionId+".", visualizer);

								setProcessVerdict(verdict);

								faultySequence = true;
								
								//Prepare Log report
								String DateString = Util.dateString(DATE_FORMAT);
								System.out.println("SUT StdOut:	" +ch);

								writerOut = new PrintWriter(new FileWriter(dir+"/sequence"+seqn+"_StdOut.log", true));
								if(lastExecutedAction()!=null)
									actionId=lastExecutedAction().get(Tags.ConcreteID);
								writerOut.println(DateString+"	on Action:	"+ actionId+"	SUT StdOut:	" +ch);
								writerOut.flush();
								writerOut.close();

								//semaphore.release();

							}
							//processOracle has priority
							else if(processLogs!=null && mLogs.matches()) {
								String DateString = Util.dateString(DATE_FORMAT);
								System.out.println("SUT Log StdOut:	" +ch);

								writerOut = new PrintWriter(new FileWriter(dir+"/sequence"+seqn+"_StdOut.log", true));
								if(lastExecutedAction()!=null)
									actionId=lastExecutedAction().get(Tags.ConcreteID);
								writerOut.println(DateString+"	on Action:	"+ actionId+"	SUT StdOut:	" +ch);
								writerOut.flush();
								writerOut.close();
							}
						}
						
						input.close();
						//Thread.currentThread().interrupt();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};  
			//TODO: When a Thread ends its code, it still existing in our TESTAR VM like Thread.State.TERMINATED
			//JVM GC should optimize the memory, but maybe we should implement a different way to create this Threads
			//¿ThreadPool? ExecutorService processListenerPool = Executors.newFixedThreadPool(2); ?
			
			new Thread(readErrors).start();
			new Thread(readOutput).start();

			/*int nbThreads =  Thread.getAllStackTraces().keySet().size();
			System.out.println("Number of Threads running on VM: "+nbThreads);*/
		}
	}

	// refactored
	protected SUT startSystem() throws SystemStartException{
		return startSystem(null);
	}

	/**
	 *
	 * @param mustContain Format is &lt;SUTConnector:string&gt; (e.g. SUT_PROCESS_NAME:proc_name or SUT_WINDOW_TITLE:window_title)
	 * @return
	 * @throws SystemStartException
	 */
	protected SUT startSystem(String mustContain) throws SystemStartException{
		return startSystem(mustContain, true, Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0));
	}


	protected SUT startSystem(String mustContain, boolean tryToKillIfRunning, long maxEngageTime) throws SystemStartException{
		this.contextRunningProcesses = SystemProcessHandling.getRunningProcesses("START");
		try{// refactored from "protected SUT startSystem() throws SystemStartException"
			for(String d : settings().get(ConfigTags.Delete))
				Util.delete(d);
			for(Pair<String, String> fromTo : settings().get(ConfigTags.CopyFromTo))
				Util.copyToDirectory(fromTo.left(), fromTo.right());
		}catch(IOException ioe){
			throw new SystemStartException(ioe);
		} // end refactoring
		String sutConnector = settings().get(ConfigTags.SUTConnector);
		if (mustContain != null && mustContain.startsWith(Settings.SUT_CONNECTOR_WINDOW_TITLE))
			return getSUTByWindowTitle(mustContain.substring(Settings.SUT_CONNECTOR_WINDOW_TITLE.length()+1));
		else if (mustContain != null && mustContain.startsWith(Settings.SUT_CONNECTOR_PROCESS_NAME))
			return getSUTByProcessName(mustContain.substring(Settings.SUT_CONNECTOR_PROCESS_NAME.length()+1));
		else if (sutConnector.equals(Settings.SUT_CONNECTOR_WINDOW_TITLE))
			return getSUTByWindowTitle(settings().get(ConfigTags.SUTConnectorValue));
		else if (sutConnector.startsWith(Settings.SUT_CONNECTOR_PROCESS_NAME))
			return getSUTByProcessName(settings().get(ConfigTags.SUTConnectorValue));
		else{ // Settings.SUT_CONNECTOR_CMDLINE
			Assert.hasText(settings().get(ConfigTags.SUTConnectorValue));
			processListenerEnabled = enableProcessListeners();
			SUT sut = NativeLinker.getNativeSUT(settings().get(ConfigTags.SUTConnectorValue), processListenerEnabled);
			//sut.setNativeAutomationCache();
			Util.pause(settings().get(ConfigTags.StartupTime));
			final long now = System.currentTimeMillis(),
					ENGAGE_TIME = tryToKillIfRunning ? Math.round(maxEngageTime / 2.0) : maxEngageTime; // half time is expected for the implementation
					State state;
					do{
						if (sut.isRunning()){
							System.out.println("SUT is running after <" + (System.currentTimeMillis() - now) + "> ms ... waiting UI to be accessible");
							state = builder.apply(sut);
							if (state != null && state.childCount() > 0){
								long extraTime = tryToKillIfRunning ? 0 : ENGAGE_TIME;
								System.out.println("SUT accessible after <" + (extraTime + (System.currentTimeMillis() - now)) + "> ms");
								return sut;
							}
						}
						Util.pauseMs(500);				
					} while (mode() != Modes.Quit && System.currentTimeMillis() - now < ENGAGE_TIME);
					if (sut.isRunning())
						sut.stop();
					// issue starting the SUT
					if (tryToKillIfRunning){
						System.out.println("Unable to start the SUT after <" + ENGAGE_TIME + "> ms");
						return tryKillAndStartSystem(mustContain, sut, ENGAGE_TIME);
					} else
						throw new SystemStartException("SUT not running after <" + Math.round(ENGAGE_TIME * 2.0) + "> ms!");							
		}
	}

	private SUT tryKillAndStartSystem(String mustContain, SUT sut, long pendingEngageTime) throws SystemStartException{
		// kill running SUT processes
		System.out.println("Trying to kill potential running SUT: <" + sut.get(Tags.Desc) + ">");
		if (SystemProcessHandling.killRunningProcesses(sut, Math.round(pendingEngageTime / 2.0))){ // All killed?
			// retry start system
			System.out.println("Retry SUT start: <" + sut.get(Tags.Desc) + ">");
			return startSystem(mustContain, false, pendingEngageTime); // no more try to kill
		} else // unable to kill SUT
			throw new SystemStartException("Unable to kill SUT <" + sut.get(Tags.Desc) + "> while trying to rerun it after <" + pendingEngageTime + "> ms!");
	}

	private SUT getSUTByProcessName(String processName) throws SystemStartException{
		Assert.hasText(processName);
		List<SUT> suts = null;
		long now = System.currentTimeMillis();
		final double MAX_ENGAGE_TIME = Math.round(settings().get(ConfigTags.StartupTime) * 1000.0);
		do{
			Util.pauseMs(100);
			suts = NativeLinker.getNativeProcesses();
			if (suts != null){
				String desc;
				for (SUT theSUT : suts){
					desc = theSUT.get(Tags.Desc, null);
					if (desc != null && desc.contains(processName)){
						System.out.println("SUT with Process Name -" + processName + "- DETECTED!");
						return theSUT;
					}
				}
			}
		} while (System.currentTimeMillis() - now < MAX_ENGAGE_TIME);
		throw new SystemStartException("SUT Process Name not found!: -" + processName + "-");	
	}

	private SUT getSUTByWindowTitle(String windowTitle) throws SystemStartException{
		Assert.hasText(windowTitle);
		List<SUT> suts = null;
		State state; Role role; String title;
		long now = System.currentTimeMillis();
		final double MAX_ENGAGE_TIME = Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0);
		do{
			Util.pauseMs(100);
			suts = NativeLinker.getNativeProcesses();
			if (suts != null){
				for (SUT theSUT : suts){
					state = getStateByWindowTitle(theSUT);
					if (state.get(Tags.Foreground)){
						for (Widget w : state){
							role = w.get(Tags.Role, null);
							if (role != null && Role.isOneOf(role, NativeLinker.getNativeRole_Window())){
								title = w.get(Tags.Title, null);
								if (title != null && title.contains(windowTitle)){
									System.out.println("SUT with Window Title -" + windowTitle + "- DETECTED!");
									return theSUT;
								}
							}
						}
					}
				}
			}
		} while (System.currentTimeMillis() - now < MAX_ENGAGE_TIME);
		throw new SystemStartException("SUT Window Title not found!: -" + windowTitle + "-");			
	}
	
	protected State getStateByWindowTitle(SUT system) throws StateBuildException{
		Assert.notNull(system);
		//State state = builder.apply(system);
		state = builder.apply(system);

		CodingManager.buildIDs(state);

		Shape viewPort = state.get(Tags.Shape, null);
		if(viewPort != null){
			//AWTCanvas scrShot = AWTCanvas.fromScreenshot(Rect.from(viewPort.x(), viewPort.y(), viewPort.width(), viewPort.height()), AWTCanvas.StorageFormat.PNG, 1);
			state.set(Tags.ScreenshotPath, protocolUtil.getStateshot(state));
		}

		calculateZIndices(state);
		
		return state;
	}

	@Override
	protected State getState(SUT system) throws StateBuildException{
		Assert.notNull(system);
		//State state = builder.apply(system);
		state = builder.apply(system);

		CodingManager.buildIDs(state);

		Shape viewPort = state.get(Tags.Shape, null);
		if(viewPort != null){
			//AWTCanvas scrShot = AWTCanvas.fromScreenshot(Rect.from(viewPort.x(), viewPort.y(), viewPort.width(), viewPort.height()), AWTCanvas.StorageFormat.PNG, 1);
			state.set(Tags.ScreenshotPath, protocolUtil.getStateshot(state));
		}

		calculateZIndices(state);
		Verdict verdict = getVerdict(state);
		state.set(Tags.OracleVerdict, verdict);
		if (mode() != Modes.Spy && verdict.severity() >= settings().get(ConfigTags.FaultThreshold)){
			faultySequence = true;
			LogSerialiser.log("Detected fault: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
			// this was added to kill the SUT if it is frozen:
			if(verdict.severity()==Verdict.SEVERITY_NOT_RESPONDING){
				//if the SUT is frozen, we should kill it!
				LogSerialiser.log("SUT frozen, trying to kill it!\n", LogSerialiser.LogLevel.Critical);
				SystemProcessHandling.killRunningProcesses(system, 100);
			}
		} else if (verdict.severity() != Verdict.SEVERITY_OK && verdict.severity() > passSeverity){
			passSeverity = verdict.severity();
			LogSerialiser.log("Detected warning: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
		}

		Grapher.notify(state, state.get(Tags.ScreenshotPath, null));

		return state;
	}

	@Override
	protected Verdict getVerdict(State state){
		Assert.notNull(state);
		//-------------------
		// ORACLES FOR FREE
		//-------------------		

		// if the SUT is not running, we assume it crashed
		if(!state.get(IsRunning, false))
			return new Verdict(Verdict.SEVERITY_NOT_RUNNING, "System is offline! I assume it crashed!");

		// if the SUT does not respond within a given amount of time, we assume it crashed
		if(state.get(Tags.NotResponding, false)){
			return new Verdict(Verdict.SEVERITY_NOT_RESPONDING, "System is unresponsive! I assume something is wrong!");
		}
		//------------------------
		// ORACLES ALMOST FOR FREE
		//------------------------

		if (this.suspiciousTitlesPattern == null)
			this.suspiciousTitlesPattern = Pattern.compile(settings().get(ConfigTags.SuspiciousTitles), Pattern.UNICODE_CHARACTER_CLASS);
		//System.out.println(this.suspiciousTitlesMatchers.size() + " suspiciousTitles matchers");
		Matcher m;
		// search all widgets for suspicious titles
		for(Widget w : state){
			String title = w.get(Title, "");
			if (title != null && !title.isEmpty()){
				m = this.suspiciousTitlesMatchers.get(title);
				if (m == null){
					m = this.suspiciousTitlesPattern.matcher(title);
					this.suspiciousTitlesMatchers.put(title, m);
				}
				if (m.matches()){
					Visualizer visualizer = Util.NullVisualizer;
					// visualize the problematic widget, by marking it with a red box
					if(w.get(Tags.Shape, null) != null)
						visualizer = new ShapeVisualizer(RedPen, w.get(Tags.Shape), "Suspicious Title", 0.5, 0.5);
					return new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE, "Discovered suspicious widget title: '" + title + "'.", visualizer);
				}
			}
		}

		if (this.nonSuitableAction){
			this.nonSuitableAction = false;
			return new Verdict(Verdict.SEVERITY_WARNING, "Non suitable action for state");
		}

		// if everything was OK ...
		return Verdict.OK;
	}

	/**
	 * This methods prepares for deriving actions, but does not really derive them yet. This is left for lower
	 * level protocols. Here the parameters are set in case unwanted processes need to be killed or the SUT needs to be brought back
	 * to foreground. The latter is then done by selectActions in the AbstractProtocol.
	 * @param system
	 * @param state
	 * @return
	 * @throws ActionBuildException
	 */
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{
		Assert.notNull(state);
		Set<Action> actions = new HashSet<Action>();	

		// create an action compiler, which helps us create actions, such as clicks, drag + drop, typing...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// If there is an unwanted process running, we need to kill it.
		// This is an unwanted process that is defined in the filter.
		// So we set this.forceKillProcess = process.right();
		// And then select action will take care of making the next action to select the killing of the process.
		String processRE = settings().get(ConfigTags.ProcessesToKillDuringTest);
		if (processRE != null && !processRE.isEmpty()){
			state.set(Tags.RunningProcesses, system.getRunningProcesses());
			for(Pair<Long, String> process : state.get(Tags.RunningProcesses, Collections.<Pair<Long,String>>emptyList())){
				if(process.left().longValue() != system.get(Tags.PID).longValue() &&
						process.right() != null && process.right().matches(processRE)){ // pid x name
					//actions.add(ac.killProcessByName(process.right(), 2));
					this.forceKillProcess = process.right();
					System.out.println("will kill unwanted process: " + process.left().longValue() + " (SYSTEM <" + system.get(Tags.PID).longValue() + ">)");
					return actions;
				}
			}
		}

		// If the system is in the background, we need to force it into the foreground!
		// We set this.forceToForeground to true and selectAction will make sure that the next action we will select
		// is putting the SUT back into the foreground.
		if(!state.get(Tags.Foreground, true) && system.get(Tags.SystemActivator, null) != null){
			//actions.add(ac.activateSystem());
			this.forceToForeground = true;
			return actions;
		}

		//Note this list is always empty in this deriveActions.
		return actions;
	}

	//TODO is this process handling Windows specific? move to SystemProcessHandling
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

	protected void visualizeActions(Canvas canvas, State state, Set<Action> actions){
		SutVisualization.visualizeActions(mode(), settings(), canvas, state, actions);
	}

	//TODO platform independent?
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

	protected String getRandomText(Widget w){
		return DataManager.getRandomData();
	}

	/**
	 * Calculate the max and the min ZIndex of all the widgets in a state
	 * @param state
	 */
	protected void calculateZIndices(State state) {
		double minZIndex = Double.MAX_VALUE,
				maxZIndex = Double.MIN_VALUE,
				zindex;
		for (Widget w : state){
			zindex = w.get(Tags.ZIndex).doubleValue();
			if (zindex < minZIndex)
				minZIndex = zindex;
			if (zindex > maxZIndex)
				maxZIndex = zindex;
		}
		state.set(Tags.MinZIndex, minZIndex);
		state.set(Tags.MaxZIndex, maxZIndex);
	}

	/**
	 * Return a list of widgets that have the maximal Zindex
	 * @param state
	 * @return
	 */
	protected List<Widget> getTopWidgets(State state){
		List<Widget> topWidgets = new ArrayList<>();
		double maxZIndex = state.get(Tags.MaxZIndex);
		for (Widget w : state)
			if (w.get(Tags.ZIndex) == maxZIndex)
				topWidgets.add(w);
		return topWidgets;
	}

	/**
	 * Adds sliding actions (like scroll, drag and drop) to the given Set of Actions
	 * @param actions
	 * @param ac
	 * @param scrollArrowSize
	 * @param scrollThick
	 * @param w
	 */
	protected void addSlidingActions(Set<Action> actions, StdActionCompiler ac, double scrollArrowSize, double scrollThick, Widget w){
		Drag[] drags = null;
		if((drags = w.scrollDrags(scrollArrowSize,scrollThick)) != null){
			for (Drag drag : drags){
				actions.add(ac.slideFromTo(
						new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
						new AbsolutePosition(Point.from(drag.getToX(),drag.getToY()))
						));
				storeWidget(state.get(Tags.ConcreteID), w);
			}
		}
	}

	/**
	 * Check whether widget w should be filtered based on
	 * its title (matching the regular expression of the Dialog --> clickFilterPattern)
	 * that is cannot be hit
	 * @param w
	 * @return
	 */
	protected boolean isUnfiltered(Widget w){
		//Check whether the widget can be hit
		// If not, it should be filtered
		if(!Util.hitTest(w, 0.5, 0.5))
			return false;

		//Check whether the widget has an empty title or no title
		//If it has, it is unfiltered
		//Because it cannot match the regular expression of the Action Filter.
		String title = w.get(Title, "");
		if (title == null || title.isEmpty())
			return true;

		//If no clickFilterPattern exists, then create it
		//Get the clickFilterPattern from the regular expression provided by the tester in the Dialog
		if (this.clickFilterPattern == null)
			this.clickFilterPattern = Pattern.compile(settings().get(ConfigTags.ClickFilter), Pattern.UNICODE_CHARACTER_CLASS);

		//Check whether the title matches any of the clickFilterPatterns
		Matcher m = this.clickFilterMatchers.get(title);
		if (m == null){
			m = this.clickFilterPattern.matcher(title);
			this.clickFilterMatchers.put(title, m);
		}
		return !m.matches();
	}

	/**
	 * Check whether a widget is clickable
	 * @param w
	 * @return
	 */
	protected boolean isClickable(Widget w){
		Role role = w.get(Tags.Role, Roles.Widget);
		if(Role.isOneOf(role, NativeLinker.getNativeClickableRoles()))
			return true;
		return false;
	}

	/**
	 * Check whether a widget is typeable
	 * @param w
	 * @return
	 */
	protected boolean isTypeable(Widget w){
		return NativeLinker.isNativeTypeable(w);
	}

	/**
	 * STOP criteria for selecting more actions for a sequence
	 * @param state
	 * @return
	 */
	protected boolean moreActions(State state) {
		return (!settings().get(ConfigTags.StopGenerationOnFault) || !faultySequence) && 
				state.get(Tags.IsRunning, false) && !state.get(Tags.NotResponding, false) &&
				//actionCount() < settings().get(ConfigTags.SequenceLength) &&
				actionCount() <= lastSequenceActionNumber &&
				timeElapsed() < settings().get(ConfigTags.MaxTime);
	}

	/**
	 * STOP criteria deciding whether more sequences are required in a test run
	 * @return
	 */
	protected boolean moreSequences() {	
		//return sequenceCount() < settings().get(ConfigTags.Sequences) &&
		return sequenceCount() <= settings().get(ConfigTags.Sequences) &&
				timeElapsed() < settings().get(ConfigTags.MaxTime);
	}

	// TODO: Is this method really used......??
	/**
	 *
	 * @param system
	 * @param state
	 * @param action
	 */
	protected void actionExecuted(SUT system, State state, Action action){
		if (this.lastState == null && state == null)
			this.nonReactingActionNumber++;
		else if (this.lastState != null && state != null &&
				this.lastState.get(Tags.ConcreteID).equals(state.get(Tags.ConcreteID)))
			this.nonReactingActionNumber++;
		this.lastState = state;
		if (this.nonReactingActionNumber > this.settings().get(ConfigTags.NonReactingUIThreshold).intValue()){
			this.nonReactingActionNumber = 0;
			this.forceNextActionESC = true;
			LogSerialiser.log("UI seems not reacting to actions ... should try ESC?\n", LogSerialiser.LogLevel.Info);
		}
	}

	@Override
	public void mouseMoved(double x, double y) {}

	@Override
	protected void stopSystem(SUT system) {
		if (system != null){
			AutomationCache ac = system.getNativeAutomationCache();
			if (ac != null)
				ac.releaseCachedAutomationElements();
		}
	}

	@Override
	protected void PostSequenceProcessing() {

	}

	@Override
	protected void CloseTestSession() {

	}

	//TODO move to ManualRecording helper class??
	/**
	 * Records user action (for example for Generate-Manual)
	 *
	 * @param state
	 * @return
	 */
	protected Action mapUserEvent(State state){
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

	//TODO move to ManualRecording helper class??
	/**
	 * Waits for an user UI action.
	 * Requirement: Mode must be GenerateManual.
	 */
	protected void waitUserActionLoop(Canvas cv, SUT system, State state, ActionStatus actionStatus){
		while (mode() == Modes.GenerateManual && !actionStatus.isUserEventAction()){
			if (userEvent != null){
				actionStatus.setAction(mapUserEvent(state));
				actionStatus.setUserEventAction((actionStatus.getAction() != null));
				userEvent = null;
			}
			synchronized(this){
				try {
					this.wait(100);
				} catch (InterruptedException e) {}
			}
			cv.begin(); Util.clear(cv);

			SutVisualization.visualizeState(mode, settings, markParentWidget, mouse, protocolUtil, lastPrintParentsOf, delay, cv, state, system);
			Set<Action> actions = deriveActions(system,state);
			CodingManager.buildIDs(state, actions);
			visualizeActions(cv, state, actions);

			cv.end();
		}		
	}

	//TODO move to ManualRecording helper class??
	/**
	 * Waits for an event (UI action) from adhoc-test.
	 * @param state
	 * @param actionStatus
	 * @return 'true' if problems were found.
	 */
	protected boolean waitAdhocTestEventLoop(State state, ActionStatus actionStatus){
		AdhocServer.waitReaderWriter(this);
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
				AdhocServer.adhocWrite("READY");
			} catch (Exception e){
				return true; // AdhocTest client disconnected?
			}
			try{
				String socketData = AdhocServer.adhocRead(); // one event per line
				System.out.println("\t... AdhocTest event = " + socketData);
				userEvent = AdhocServer.compileAdhocTestServerEvent(socketData); // hack into userEvent
				if (userEvent == null){
					AdhocServer.adhocWrite("???");
				}else{
					actionStatus.setAction(mapUserEvent(state));
					if (actionStatus.getAction() == null){
						AdhocServer.adhocWrite("404");
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


	protected int escAttempts = 0;
	protected static final int MAX_ESC_ATTEMPTS = 99;
	/**
	 * Waits for an automatically selected UI action.
	 * @param system
	 * @param state
	 * @param fragment
	 * @param actionStatus
	 * @return
	 */
	protected boolean waitAutomaticAction(SUT system, State state, Taggable fragment, ActionStatus actionStatus){
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

	protected boolean isNOP(Action action){
		String as = action.toString();
		if (as != null && as.equals(NOP.NOP_ID))
			return true;
		else
			return false;
	}

	protected boolean isESC(Action action){
		Role r = action.get(Tags.Role, null);
		if (r != null && r.isA(ActionRoles.HitKey)){
			String desc = action.get(Tags.Desc, null);
			if (desc != null && desc.contains("VK_ESCAPE"))
				return true;
		}
		return false;
	}

	//TODO move the variables into a separate class SutProfiler
	// variables for SUT profiling in runAction():
	protected long stampLastExecutedAction = -1;
	protected long[] lastCPU; // user x system x frame
	protected int nopAttempts = 0;
	protected static final int MAX_NOP_ATTEMPTS = 99;
	protected static final long NOP_WAIT_WINDOW = 100; // ms

	/**
	 * To be documented / refactored
	 *
	 * @param cv
	 * @param system
	 * @param state
	 * @param fragment
	 * @return
	 */
	protected boolean runAction(Canvas cv, SUT system, State state, Taggable fragment){
		long tStart = System.currentTimeMillis();
		LOGGER.info("[RA} start runAction");
		ActionStatus actionStatus = new ActionStatus();

		if (mode() == Modes.GenerateManual)
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

			if (actionStatus.isUserEventAction() ||
					(actionStatus.setActionSucceeded(executeAction(system, state, actionStatus.getAction())))){

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

				if (mode() == Modes.AdhocTest){
					try {
						AdhocServer.adhocWrite("OK");
					} catch (Exception e){} // AdhocTest client disconnected?
				}

				if (isTestAction && actionStatus.isActionSucceeded())
					actionCount++;
				fragment.set(ExecutedAction, actionStatus.getAction());
				fragment.set(ActionDuration, settings().get(ConfigTags.ActionDuration));
				fragment.set(ActionDelay, settings().get(ConfigTags.TimeToWaitAfterAction));
				LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);

				TestSerialiser.write(fragment);

				LogSerialiser.log("Wrote fragment to sequence file!\n", LogSerialiser.LogLevel.Debug);
			}else{
				LogSerialiser.log("Execution of action failed!\n");
				try {
					AdhocServer.adhocWrite("FAIL");
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

	/**
	 * Method to run TESTAR on Generate mode
	 * @param system
	 */
	protected void runGenerate(SUT system){
		LogSerialiser.finish(); LogSerialiser.exit();
		sequenceCount = 1;
		lastStamp = System.currentTimeMillis();
		escAttempts = 0; nopAttempts = 0;
		boolean problems;

		//New sequence starts
		while(mode() != Modes.Quit && moreSequences()){
			long tStart = System.currentTimeMillis();
			LOGGER.info("[RT] Runtest started for sequence {}",sequenceCount());

			String generatedSequence = Util.generateUniqueFile(settings.get(ConfigTags.OutputDir) + File.separator + "sequences", "sequence").getName();
			generatedSequenceNumber = new Integer(generatedSequence.replace("sequence", ""));

			sutRAMbase = Double.MAX_VALUE; sutRAMpeak = 0.0; sutCPUpeak = 0.0; testRAMpeak = 0.0; testCPUpeak = 0.0;

			try {
				LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
						settings.get(OutputDir) + File.separator + "logs" + File.separator + generatedSequence + ".log"), true))),
						settings.get(LogLevel));
			} catch (NoSuchTagException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (FileNotFoundException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}

			//Create a new Grapher for a new sequence
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
							true : settings.get(ConfigTags.GraphResuming),
					settings.get(ConfigTags.OfflineGraphConversion),
					settings.get(ConfigTags.OutputDir),
					jipWrapper);

			Grapher.waitEnvironment();
			ScreenshotSerialiser.start(settings.get(ConfigTags.OutputDir), generatedSequence);

			problems = false;
			if (!forceToSequenceLengthAfterFail) passSeverity = Verdict.SEVERITY_OK;
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
			try {
				TestSerialiser.start(new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(currentSeq, true))));
				LogSerialiser.log("Created new sequence file!\n", LogSerialiser.LogLevel.Debug);
			} catch (IOException e) {
				LogSerialiser.log("I/O exception creating new sequence file\n", LogSerialiser.LogLevel.Critical);
			}
			LogSerialiser.log("Building canvas...\n", LogSerialiser.LogLevel.Debug);

			String startDateString = Util.dateString(DATE_FORMAT);
			LogSerialiser.log(startDateString + " Starting SUT ...\n", LogSerialiser.LogLevel.Info);

			try{
				//If system it's null means that we have started TESTAR from the Generate mode
				if(system == null || !system.isRunning()) {
					system = null;
					system = startSystem();
					this.cv = buildCanvas();
				}
				processListeners(system);

				lastCPU = NativeLinker.getCPUsage(system);

				LogSerialiser.log("SUT is running!\n", LogSerialiser.LogLevel.Debug);
				LogSerialiser.log("Obtaining system state before beginSequence...\n", LogSerialiser.LogLevel.Debug);
				State state = getState(system);
				LogSerialiser.log("Starting sequence " + sequenceCount + " (output as: " + generatedSequence + ")\n\n", LogSerialiser.LogLevel.Info);
				beginSequence(system, state);
				LogSerialiser.log("Obtaining system state after beginSequence...\n", LogSerialiser.LogLevel.Debug);
				state = getState(system);
				graphDB.addState(state,true);
				LogSerialiser.log("Successfully obtained system state!\n", LogSerialiser.LogLevel.Debug);
				if(isSaveStateSnapshot()) {
					File file = Util.generateUniqueFile(settings.get(ConfigTags.OutputDir), "state_snapshot");
					FileHandling.saveStateSnapshot(state, file);
					setSaveStateSnapshot(false);
				}
				Taggable fragment = new TaggableBase();
				fragment.set(SystemState, state);

				Verdict verdict = state.get(OracleVerdict, Verdict.OK);
				Verdict processVerdict =  getProcessVerdict();
				if (faultySequence) problems = true;
				fragment.set(OracleVerdict, verdict);

				long spyTime;
				//We begin to make the number of actions indicated in our sequence
				while(mode() != Modes.Quit && moreActions(state)){

					//User wants to move to Spy mode, after that we will continue the actions
					if(mode() == Modes.Spy) {
						spyTime = System.currentTimeMillis();
						runSpy(system);
						LOGGER.info("[RA] User swap to Spy Mode {} ms",System.currentTimeMillis()-spyTime);
					}

					if (problems)
						faultySequence = true;
					else{
						problems = runAction(cv,system,state,fragment);

						LogSerialiser.log("Obtaining system state...\n", LogSerialiser.LogLevel.Debug);

						/*try {
							semaphore.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						
						state = getState(system);
						graphDB.addState(state);
						if (faultySequence) problems = true;
						LogSerialiser.log("Successfully obtained system state!\n", LogSerialiser.LogLevel.Debug);
						if (mode() != Modes.Spy){
							if(isSaveStateSnapshot()) {
								File file = Util.generateUniqueFile(settings.get(ConfigTags.OutputDir), "state_snapshot");
								FileHandling.saveStateSnapshot(state, file);
								setSaveStateSnapshot(false);
							}
							processVerdict = getProcessVerdict();
							verdict = state.get(OracleVerdict, Verdict.OK);
							fragment.set(OracleVerdict, verdict.join(processVerdict));
							fragment = new TaggableBase();
							fragment.set(SystemState, state);
						}
						
						//semaphore.release();
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

				Verdict stateVerdict = verdict.join(new Verdict(passSeverity,"",Util.NullVisualizer));
				Verdict finalVerdict;

				finalVerdict = stateVerdict.join(processVerdict);
				
				setProcessVerdict(Verdict.OK);

				if (!settings().get(ConfigTags.OnlySaveFaultySequences) ||
						finalVerdict.severity() >= settings().get(ConfigTags.FaultThreshold)){
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
					FileHandling.copyClassifiedSequence(generatedSequence, currentSeq, finalVerdict,settings.get(ConfigTags.OutputDir));
				}
				if(!problems)
					this.forceToSequenceLengthAfterFail = false;

				LogSerialiser.log("Releasing canvas...\n", LogSerialiser.LogLevel.Debug);
				cv.release();

				saveSequenceMetrics(generatedSequence,problems);

				ScreenshotSerialiser.exit();
				final String[] reportPages = Grapher.getReport(this.firstSequenceActionNumber); // screenshots must be serialised
				if (reportPages == null)
					LogSerialiser.log("NULL report pages\n", LogSerialiser.LogLevel.Critical);
				TestSerialiser.exit();
				String stopDateString =  Util.dateString(DATE_FORMAT),
						durationDateString = Util.diffDateString(DATE_FORMAT, startDateString, stopDateString);
				LogSerialiser.log("TESTAR stopped execution at " + stopDateString + "\n", LogSerialiser.LogLevel.Critical);
				LogSerialiser.log("Test duration was " + durationDateString + "\n", LogSerialiser.LogLevel.Critical);
				LogSerialiser.flush(); LogSerialiser.finish(); LogSerialiser.exit();

				if (reportPages != null) FileHandling.saveReport(reportPages, generatedSequence, settings.get(OutputDir), settings.get(LogLevel));; // save report

				sequenceCount++;

			} catch(Exception e){
				System.out.println("Thread: name="+Thread.currentThread().getName()+",id="+Thread.currentThread().getId()+", SUT throws exception");
				e.printStackTrace();
				SystemProcessHandling.killTestLaunchedProcesses(this.contextRunningProcesses);
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
				if (reportPages != null) FileHandling.saveReport(reportPages, generatedSequence,settings.get(OutputDir), settings.get(LogLevel));; // save report
				this.mode = Modes.Quit; // System.exit(1);
			}
			LOGGER.info("[RT] Runtest finished for sequence {} in {} ms",sequenceCount()-1,System.currentTimeMillis()-tStart);
		}
		if (settings().get(ConfigTags.ForceToSequenceLength).booleanValue() &&  // force a test sequence length in presence of FAIL
				this.actionCount <= settings().get(ConfigTags.SequenceLength) && 
				mode() != Modes.Quit && testFailTimes < TEST_RETRY_THRESHOLD){
			this.forceToSequenceLengthAfterFail = true;
			System.out.println("Resuming test after FAIL at action number <" + this.actionCount + ">");
			runGenerate(system); // continue testing
		} else
			this.forceToSequenceLengthAfterFail = false;
	}

	/**
	 * Method to run TESTAR on Spy Mode.
	 * @param system
	 */
	protected void runSpy(SUT system) {
		boolean startedSpy = false;

		//If system it's null means that we have started TESTAR from the Spy mode
		//We need to invoke the SUT & the canvas representation
		if(system == null) {
			system = startSystem();
			//processListeners(system);
			startedSpy = true;
			Grapher.GRAPHS_ACTIVATED = false;
			this.cv = buildCanvas();
		}
		//else, SUT & canvas exists (startSystem() & buildCanvas() created from runGenerate)

		while(mode() == Modes.Spy && system.isRunning()) {
			State state = getState(system);
			cv.begin(); Util.clear(cv);
			SutVisualization.visualizeState(mode, settings, markParentWidget, mouse, protocolUtil, lastPrintParentsOf, delay, cv, state, system);
			Set<Action> actions = deriveActions(system,state);
			CodingManager.buildIDs(state, actions);
			visualizeActions(cv, state, actions);
			cv.end();
		}

		if(startedSpy){
			//cv.release();
			detectLoopMode(system);
		}

	}

	protected void replay(){
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
	
	//Method to change between the different loops that represent the principal modes of execution on TESTAR
	protected void detectLoopMode(SUT system) {
		if(mode() == Modes.Spy ){
			runSpy(system);
		}else if(mode() == Modes.Generate || mode() == Modes.GenerateDebug || mode() == Modes.GenerateManual){
			runGenerate(system);
		}else if(mode() == Modes.Quit) {
			quitSUT(system);
		}
	}

	//close SUT because we're on Quit mode
	protected void quitSUT(SUT system) {

		SystemProcessHandling.killTestLaunchedProcesses(this.contextRunningProcesses);
		stopSystem(system);
		//If stopSystem did not really stop the system, we will do it for you ;-)
		if (system != null)
			system.stop();
	}


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
	public void saveSequenceMetrics(String testSequenceName, boolean problems){
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

}

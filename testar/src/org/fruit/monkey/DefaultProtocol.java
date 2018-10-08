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

<<<<<<< HEAD
import es.upv.staq.testar.ActionStatus;
import es.upv.staq.testar.AdhocServer;
import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.managers.DataManager;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;
import nl.ou.testar.ProcessInfo;
=======
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.concurrent.Semaphore;

>>>>>>> master
import nl.ou.testar.SutVisualization;
import nl.ou.testar.SystemProcessHandling;
import org.fruit.Assert;
import org.fruit.Drag;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.MouseButtons;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.WidgetNotFoundException;
import org.fruit.alayer.visualizers.ShapeVisualizer;
<<<<<<< HEAD
import org.fruit.alayer.windows.StateFetcher;
=======
import org.fruit.monkey.AbstractProtocol.Modes;
>>>>>>> master

import es.upv.staq.testar.ActionStatus;
import es.upv.staq.testar.AdhocServer;
import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.managers.DataManager;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;

public class DefaultProtocol extends AbstractProtocol {


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

	protected final static Pen RedPen = Pen.newPen().setColor(Color.Red).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build(),
			BluePen = Pen.newPen().setColor(Color.Blue).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build();

	protected void initialize(Settings settings){
		//builder = new UIAStateBuilder(settings.get(ConfigTags.TimeToFreeze));
		builder = NativeLinker.getNativeStateBuilder(
				settings.get(ConfigTags.TimeToFreeze),
				settings.get(ConfigTags.AccessBridgeEnabled),
				settings.get(ConfigTags.SUTProcesses)
				);
	}

	protected Canvas buildCanvas() {
		//return GDIScreenCanvas.fromPrimaryMonitor(Pen.DefaultPen);
		return NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT);
	}

	protected void beginSequence(SUT system, State state){
		super.beginSequence(system, state);
		faultySequence = false;
		nonReactingActionNumber = 0;
	}

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
								Verdict verdict = new Verdict(SEVERITY_SUSPICIOUS_TITLE,
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
								Verdict verdict = new Verdict(SEVERITY_SUSPICIOUS_TITLE,
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
//			System.out.println("DefaultProtocol: Starting SUT process");
			processesBeforeSUT = SystemProcessHandling.getRunningProcesses("Before starting SUT");
			visibleWindowsBeforeSUT = StateFetcher.visibleTopLevelWindows();
//			StateFetcher.printVisibleWindows(visibleWindowsBeforeSUT);
			Assert.hasText(settings().get(ConfigTags.SUTConnectorValue));
			processListenerEnabled = enableProcessListeners();
			SUT sut = NativeLinker.getNativeSUT(settings().get(ConfigTags.SUTConnectorValue), processListenerEnabled);
			//sut.setNativeAutomationCache();
			sutProcesses = SystemProcessHandling.getNewProcesses(processesBeforeSUT);
			// Waiting until a new window has found after starting the SUT:
			while(sutWindows.size()==0){
				Util.pauseMs(500);
				sutWindows = StateFetcher.getNewWindows(visibleWindowsBeforeSUT);
//				System.out.println("DEBUG: SUT windows: "+sutWindows.size());
			}
			Util.pause(settings().get(ConfigTags.StartupTime)); //TODO this code not needed anymore?
			final long now = System.currentTimeMillis(),
					ENGAGE_TIME = tryToKillIfRunning ? Math.round(maxEngageTime / 2.0) : maxEngageTime; // half time is expected for the implementation
					State state;
					do{
						if (sut.isRunning()){
							System.out.println("SUT is running after <" + (System.currentTimeMillis() - now) + "> ms ... waiting UI to be accessible");
							state = builder.apply(sut,sutWindows);
							if (state != null && state.childCount() > 0){
								long extraTime = tryToKillIfRunning ? 0 : ENGAGE_TIME;
								System.out.println("SUT accessible after <" + (extraTime + (System.currentTimeMillis() - now)) + "> ms");
								return sut;
							}else if(state == null){
//								System.out.println("DEBUG: state == null");
							}else if(state.childCount()==0){
//								System.out.println("DEBUG: state.childCount() == 0");
//								for(Tag t:state.tags()){
//									System.out.println("DEBUG: "+t+"="+state.get(t));
//								}
							}
						}else{
//							System.out.println("DEBUG: system not running, status="+sut.getStatus());
//							for(Tag t:sut.tags()){
//								System.out.println("DEBUG: "+t+"="+sut.get(t));
//							}
							sutProcesses = SystemProcessHandling.getNewProcesses(processesBeforeSUT);
							for(ProcessInfo pi:sutProcesses) {
								sut = pi.sut;
								if (sut.isRunning()) {
//									System.out.println("DEBUG: system is running - trying to build state, status=" + sut.getStatus());
									state = builder.apply(sut,sutWindows);
									if (state != null && state.childCount() > 0) {
										long extraTime = tryToKillIfRunning ? 0 : ENGAGE_TIME;
										System.out.println("SUT accessible after <" + (extraTime + (System.currentTimeMillis() - now)) + "> ms, SUT process: "+ sut.getStatus());
										return sut;
									}else if(state == null){
//										System.out.println("DEBUG: state == null");
									}else if(state.childCount()==0){
//										System.out.println("DEBUG: state.childCount() == 0");
//								for(Tag t:state.tags()){
//									System.out.println("DEBUG: "+t+"="+state.get(t));
//								}
									}
								}
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
			state.set(Tags.ScreenshotPath, super.protocolUtil.getStateshot(state));
		}

		calculateZIndices(state);
		
		return state;
	}

	@Override
	protected State getState(SUT system) throws StateBuildException{
        Assert.notNull(system);

        // Updating SUT processes and windows:
        updateSutProcessesAndWindows();

        state = builder.apply(system); // TODO why is state a class variable? especially since this function returns state
        CodingManager.buildIDs(state);

        if(!state.get(Tags.Foreground)){
            System.out.println("DefaultProtocol.getState(): SUT process is not foreground! Returning null state!");
            return null;
        }

		Shape viewPort = state.get(Tags.Shape, null);
		if(viewPort != null){
			//AWTCanvas scrShot = AWTCanvas.fromScreenshot(Rect.from(viewPort.x(), viewPort.y(), viewPort.width(), viewPort.height()), AWTCanvas.StorageFormat.PNG, 1);
			state.set(Tags.ScreenshotPath, super.protocolUtil.getStateshot(state));
		}

		calculateZIndices(state);
		Verdict verdict = getVerdict(state);
		state.set(Tags.OracleVerdict, verdict);
		if (mode() != Modes.Spy && verdict.severity() >= settings().get(ConfigTags.FaultThreshold)){
			faultySequence = true;
			LogSerialiser.log("Detected fault: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
			// this was added to kill the SUT if it is frozen:
			if(verdict.severity()==SEVERITY_NOT_RESPONDING){
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

    /**
     * Updates sutProcesses and sutWindows class variables
     */
    private void updateSutProcessesAndWindows(){
        // System.out.println("DefaultProtocol.checkNewProcessesAndWindows(): checking processes and windows");
        if(processesPreviouslyDetected==null){
            System.out.println("DefaultProtocol.checkNewProcessesAndWindows(): 1st time checking processes");
            //no action taken yet - or in SPY mode
            processesPreviouslyDetected = SystemProcessHandling.getRunningProcesses("update");
            sutProcesses = SystemProcessHandling.getNewProcesses(processesBeforeSUT);
        }else{
            //action taken, checking if action created new processes or windows
            sutProcesses = SystemProcessHandling.getNewProcesses(processesPreviouslyDetected);
            processesPreviouslyDetected = SystemProcessHandling.getRunningProcesses("update");
        }
        if(visibleWindowsPreviouslyDetected==null){
            System.out.println("DefaultProtocol.checkNewProcessesAndWindows(): 1st time checking windows");
            //no action taken yet - or in SPY mode
            visibleWindowsPreviouslyDetected = StateFetcher.visibleTopLevelWindows();
            sutWindows = StateFetcher.getNewWindows(visibleWindowsBeforeSUT);
        }else{
            //action taken, checking if action created new processes or windows
            sutWindows = StateFetcher.getNewWindows(visibleWindowsPreviouslyDetected);
            visibleWindowsPreviouslyDetected = StateFetcher.visibleTopLevelWindows();
        }
    }

	@Override
	protected Verdict getVerdict(State state){
		Assert.notNull(state);
		//-------------------
		// ORACLES FOR FREE
		//-------------------		

		// if the SUT is not running, we assume it crashed
		if(!state.get(IsRunning, false))
			return new Verdict(SEVERITY_NOT_RUNNING, "System is offline! I assume it crashed!");

		// if the SUT does not respond within a given amount of time, we assume it crashed
		if(state.get(Tags.NotResponding, false)){
			return new Verdict(SEVERITY_NOT_RESPONDING, "System is unresponsive! I assume something is wrong!");
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
					return new Verdict(SEVERITY_SUSPICIOUS_TITLE, "Discovered suspicious widget title: '" + title + "'.", visualizer);
				}
			}
		}

		if (this.nonSuitableAction){
			this.nonSuitableAction = false;
			return new Verdict(SEVERITY_WARNING, "Non suitable action for state");
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
	@Override
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

	/**
	 * Waits for an automatically selected UI action.
	 * @param system
	 * @param state
	 * @param fragment
	 * @param actionStatus
	 * @return boolean true if problems found
	 */
	protected boolean waitAutomaticAction(SUT system, State state, Taggable fragment, ActionStatus actionStatus){
        //----------------------------------
	    // Calling deriveActions() from protocol:
        //----------------------------------
		Set<Action> actions = deriveActions(system, state);
		CodingManager.buildIDs(state,actions);

		if(actions.isEmpty()){
            System.out.println("DefaultProtocol.waitAutomaticAction(): no actions found!");
			if (mode() != Modes.Spy && escAttempts >= MAX_ESC_ATTEMPTS){
				LogSerialiser.log("No available actions to execute! Tried ESC <" + MAX_ESC_ATTEMPTS + "> times. Stopping sequence generation!\n", LogSerialiser.LogLevel.Critical);
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
			escAttempts = 0; // resetting ESC attempts to 0

		fragment.set(ActionSet, actions);
		LogSerialiser.log("Built action set!\n", LogSerialiser.LogLevel.Debug);
		//TODO visualize actions does not work on generate mode, should be settings to make faster
		visualizeActions(cv, state, actions);

		if(mode() == Modes.Quit)
		    return actionStatus.isProblems();

		LogSerialiser.log("Selecting action...\n", LogSerialiser.LogLevel.Debug);

		if(mode() == Modes.Spy)
		    return false;

		//----------------------------------
		// Calling selectAction() from protocol:
        //----------------------------------
		actionStatus.setAction(selectAction(state, actions));

		if (actionStatus.getAction() == null){ // (no suitable actions?)
			nonSuitableAction = true;
			return true; // force test sequence end
		}

		actionStatus.setUserEventAction(false);

		return false;
	}

	protected boolean runAction(Canvas cv, SUT system, State state, Taggable fragment){
		long tStart = System.currentTimeMillis();
		LOGGER.info("[RA} start runAction");
		ActionStatus actionStatus = new ActionStatus();

		if (mode() == Modes.GenerateManual)
			waitUserActionLoop(cv,system,state,actionStatus);

		cv.begin(); Util.clear(cv);
		//TODO sut visualization does not work in generate mode:
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
            //----------------------------------
		    // Derive and select action:
            //----------------------------------
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
                    //----------------------------------
                    // Calling executeAction():
                    //----------------------------------
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
	protected void runGenerate(SUT system){ //TODO put SUT system into class variables and take out of function variables because the process may change during execution
		LogSerialiser.finish(); LogSerialiser.exit();
		sequenceCount = 1;
		lastStamp = System.currentTimeMillis();
		escAttempts = 0; nopAttempts = 0;
		boolean problems;

        //----------------------------------
		//New sequence starts
        //----------------------------------
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
				//If system is null, we have started TESTAR from the Generate mode and it is not yet started
				if(system == null || !system.isRunning()) {
                    //----------------------------------
					// Starting the system for the sequence
                    //----------------------------------
					system = startSystem();
					this.cv = buildCanvas();
				}
				processListeners(system);

				lastCPU = NativeLinker.getCPUsage(system);

				LogSerialiser.log("SUT is running!\n", LogSerialiser.LogLevel.Debug);
				LogSerialiser.log("Obtaining system state before beginSequence...\n", LogSerialiser.LogLevel.Debug);
				// Getting state:
				State state = getState(system);
                if(state == null){
                    // SUT process was not on foreground
                    System.out.println("DefaultProtocol.runGenerate(): SUT is not on foreground!");
                    if(sutProcesses.size()>0){
                        System.out.println("DefaultProtocol.runGenerate(): trying if one of other possible SUT processes is on foreground");
                        system = getRunningForegroundSUT();
                        state = getState(system);
                    }
                }
				LogSerialiser.log("Starting sequence " + sequenceCount + " (output as: " + generatedSequence + ")\n\n", LogSerialiser.LogLevel.Info);
				// Executing beginSequence if defined:
				beginSequence(system, state);
				LogSerialiser.log("Obtaining system state after beginSequence...\n", LogSerialiser.LogLevel.Debug);
				// Getting state again:
				state = getState(system);
                if(state == null){
                    // SUT process was not on foreground
                    System.out.println("DefaultProtocol.runGenerate(): SUT is not on foreground!");
                    if(sutProcesses.size()>0){
                        System.out.println("DefaultProtocol.runGenerate(): trying if one of other possible SUT processes is on foreground");
                        system = getRunningForegroundSUT();
                        state = getState(system);
                    }
                }
				graphDB.addState(state,true);
				LogSerialiser.log("Successfully obtained system state!\n", LogSerialiser.LogLevel.Debug);
				saveStateSnapshot(state);

				Taggable fragment = new TaggableBase();
				fragment.set(SystemState, state);

				Verdict verdict = state.get(OracleVerdict, Verdict.OK);
				Verdict processVerdict =  getProcessVerdict();
				if (faultySequence) problems = true;
				fragment.set(OracleVerdict, verdict);

				long spyTime; //for putting time of mode change into reports

				// Running this Generate loop until actions per sequence reached or mode changed by the user:
				while(mode() != Modes.Quit && moreActions(state)){

					//User wants to move to Spy mode, after that we will continue the actions
					if(mode() == Modes.Spy) {
						spyTime = System.currentTimeMillis();
						runSpy(system);
						LOGGER.info("[RA] User swap to Spy Mode {} ms",System.currentTimeMillis()-spyTime);
					}

					if (problems)
						faultySequence = true; //TODO no more actions if fault found? that should be in settings?
					else{
                        //----------------------------------
					    // Derive, select and execute action:
                        //----------------------------------
						problems = runAction(cv,system,state,fragment);

						LogSerialiser.log("Obtaining system state...\n", LogSerialiser.LogLevel.Debug);

						/*try {
							semaphore.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						
						state = getState(system);
						if(state == null){
						    // SUT process was not on foreground
                            System.out.println("DefaultProtocol.runGenerate(): SUT is not on foreground!");
                            if(sutProcesses.size()>0){
                                System.out.println("DefaultProtocol.runGenerate(): trying if one of other possible SUT processes is on foreground");
                                system = getRunningForegroundSUT();
                                state = getState(system);
                            }
                        }
						graphDB.addState(state);
						if (faultySequence) problems = true;
						LogSerialiser.log("Successfully obtained system state!\n", LogSerialiser.LogLevel.Debug);
						if (mode() != Modes.Spy){
							saveStateSnapshot(state);
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
				System.out.println("DefaultProtocol.runGenerate(): calling stopSystem()");
				stopSystem(system);
				//If stopSystem did not really stop the system, we will do it for you ;-)
				if (system != null && system.isRunning()) {
                    System.out.println("DefaultProtocol.runGenerate(): calling system.stop()");
                    system.stop();
                }
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


                //----------------------------------
                // Calling finishSequence() from the protocol
                //----------------------------------
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
					copyClassifiedSequence(generatedSequence, currentSeq, finalVerdict);
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

				if (reportPages != null) this.saveReport(reportPages, generatedSequence);; // save report

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
				if (reportPages != null) this.saveReport(reportPages, generatedSequence);; // save report
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

		while(mode() == Modes.Spy) {
			if(system.isRunning()){
				State state = getState(system);
				if(state==null) {
					System.out.println("DefaultProtocol.runSpy(): SUT process is not on foreground!");
					system = getRunningForegroundSUT(); //TODO system is not updated back to caller of runSpy()
					state = getState(system);
					//TODO what if state is still null?
				}
                visualizeSUT(system);
			}else{
				//System is not running, check if process has been changed and one of the SUT processes is on foreground
				System.out.println("DefaultProtocol.runSpy(): SUT process is not running, trying to find SUT process!");
				//TODO check what would be suitable waiting time:
				Util.pause(5);
				system = getRunningForegroundSUT();
				state = getState(system);
				if(system!=null){
					visualizeSUT(system);
				}
			}
		}

		if(startedSpy){
			//cv.release();
			detectLoopMode(system);
		}

	}

	private SUT getRunningForegroundSUT(){
		SUT system=null;
		for(ProcessInfo pi:sutProcesses) {
			system = pi.sut;
			if (system.isRunning()) {
//									System.out.println("DEBUG: system is running - trying to build state, status=" + sut.getStatus());
				state = builder.apply(system);
				if (state != null && state.childCount() > 0) {
					if(state.get(Tags.Foreground)==true) {
						System.out.println("DefaultProtocol.getRunningForegroundSUT(): possible SUT process found: "+pi.Desc);
						return system;
//						try{
//							deriveActions(system, state);
//						}catch(Exception e){
//							System.out.println("DefaultProtocol.getRunningForegroundSUT(): deriveActions() fails - not SUT process.");
//						}
					}else {
						System.out.println("DefaultProtocol.getRunningForegroundSUT(): SUT process is STILL not on foreground!");
					}
				}else if(state == null){
//										System.out.println("DEBUG: state == null");
				}else if(state.childCount()==0){
//										System.out.println("DEBUG: state.childCount() == 0");
//								for(Tag t:state.tags()){
//									System.out.println("DEBUG: "+t+"="+state.get(t));
//								}
				}
			}
		}
		System.out.println("DefaultProtocol.getRunningForegroundSUT(): checking all processes started after initial SUT startup.");
		sutProcesses = SystemProcessHandling.getNewProcesses(processesBeforeSUT);
		for(ProcessInfo pi:sutProcesses) {
			system = pi.sut;
			if (system.isRunning()) {
//									System.out.println("DEBUG: system is running - trying to build state, status=" + sut.getStatus());
				state = builder.apply(system);
				if (state != null && state.childCount() > 0) {
					if(state.get(Tags.Foreground)==true) {
						System.out.println("DefaultProtocol.getRunningForegroundSUT(): possible SUT process found: "+pi.Desc);
						return system;
//						try{
//							deriveActions(system, state);
//						}catch(Exception e){
//							System.out.println("DefaultProtocol.getRunningForegroundSUT(): deriveActions() fails - not SUT process.");
//						}
					}else {
						System.out.println("DefaultProtocol.getRunningForegroundSUT(): SUT process is STILL not on foreground!");
					}
				}else if(state == null){
//										System.out.println("DEBUG: state == null");
				}else if(state.childCount()==0){
//										System.out.println("DEBUG: state.childCount() == 0");
//								for(Tag t:state.tags()){
//									System.out.println("DEBUG: "+t+"="+state.get(t));
//								}
				}
			}
		}
		return system;
	}

	private void visualizeSUT(SUT system){
		cv.begin();
		Util.clear(cv);
		SutVisualization.visualizeState(mode, settings, markParentWidget, mouse, protocolUtil, lastPrintParentsOf, delay, cv, state, system);
		Set<Action> actions = deriveActions(system, state);
//			if(actions.size()==0){
//				System.out.println("DefaultProtocol.runSpy(): 0 actions found!");
//			}else{
//				System.out.println("DefaultProtocol.runSpy(): "+actions.size() +" found.");
////				for(Tag t:state.tags()){
////					System.out.println("DEBUG: Tag "+t+"="+state.get(t));
////				}
//			}
		CodingManager.buildIDs(state, actions);
		visualizeActions(cv, state, actions);
		cv.end();
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

}

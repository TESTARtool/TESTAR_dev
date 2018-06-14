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

import static org.fruit.alayer.Tags.IsRunning;
import static org.fruit.alayer.Tags.Title;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.StateBuilder;
import org.fruit.alayer.StrokePattern;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.visualizers.ShapeVisualizer;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.managers.DataManager;
import es.upv.staq.testar.serialisation.LogSerialiser;

public class DefaultProtocol extends AbstractProtocol{


	protected State state = null,
			lastState = null;
	protected int nonReactingActionNumber;


	private StateBuilder builder;

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
		//System.out.println("Finish sequence");
		
		this.killTestLaunchedProcesses();
	}
	
	/**
	 * If SUT process is invoked through COMMAND_LINE,
	 * this method create threads to work with oracles at the process level.
	 */
	protected void processListeners(SUT system, String specificSuspiciousTitle) {
		
		//Only if we executed SUT with command_line
		if(settings().get(ConfigTags.SUTConnector).equals("COMMAND_LINE")) {
			final String DATE_FORMAT = "dd.MMMMM.yyyy HH:mm:ss";
			
			Pattern defaultOracles= Pattern.compile(settings().get(ConfigTags.SuspiciousTitles)+"|"+specificSuspiciousTitle, Pattern.UNICODE_CHARACTER_CLASS);
			
			int seqn = generatedSequenceCount();
			File dir = new File("output/StdOutErr");
			if(!dir.exists())
				dir.mkdirs();
			
			Runnable readErrors = new Runnable() {
				public void run() {
					try {
						PrintWriter writerError;
						BufferedReader input = new BufferedReader(new InputStreamReader(system.get(Tags.StdErr)));
						String actionId = "";
						String ch;
						Matcher m;
						while ((ch = input.readLine()) != null)
						{	
							m= defaultOracles.matcher(ch);
							if(defaultOracles!=null & m.matches()) {
								String DateString = Util.dateString(DATE_FORMAT);
								System.out.println("SUT StdErr: " +ch);
								
								writerError = new PrintWriter(new FileWriter(dir+"/sequence"+seqn+"_StdErr.log", true));
								if(lastExecutedAction()!=null)
									actionId=lastExecutedAction().get(Tags.ConcreteID);
								writerError.println(DateString+"   on Action: "+actionId+"  SUT StdErr:  " +ch);
								writerError.flush();
								writerError.close();
							}
						}
						if(!system.isRunning()) {
							input.close();
							Thread.currentThread().interrupt();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			Runnable readOutput = new Runnable() {
				public void run() {
					try {
						PrintWriter writerOut;
						BufferedReader input = new BufferedReader(new InputStreamReader(system.get(Tags.StdOut)));
						String actionId = "";
						String ch;
						Matcher m;
						while ((ch = input.readLine()) != null)
						{	
							m = defaultOracles.matcher(ch);
							if(defaultOracles!=null & m.matches()) {
								String DateString = Util.dateString(DATE_FORMAT);
								System.out.println("SUT StdOut: " +ch);
								
								writerOut = new PrintWriter(new FileWriter(dir+"/sequence"+seqn+"_StdOut.log", true));
								if(lastExecutedAction()!=null)
									actionId=lastExecutedAction().get(Tags.ConcreteID);
								writerOut.println(DateString+"   on Action: "+ actionId+"  SUT StdOut:  " +ch);
								writerOut.flush();
								writerOut.close();
							}
						}
						if(!system.isRunning()) {
						input.close();
						Thread.currentThread().interrupt();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};

			new Thread(readErrors).start();
			new Thread(readOutput).start();
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
		this.contextRunningProcesses = getRunningProcesses("START");
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
			SUT sut = NativeLinker.getNativeSUT(settings().get(ConfigTags.SUTConnectorValue));
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
		if (this.killRunningProcesses(sut, Math.round(pendingEngageTime / 2.0))){ // All killed?
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
					state = getState(theSUT);
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

	@Override
	protected State getState(SUT system) throws StateBuildException{
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
		Verdict verdict = getVerdict(state);
		state.set(Tags.OracleVerdict, verdict);
		if (mode() != Modes.Spy && verdict.severity() >= settings().get(ConfigTags.FaultThreshold)){
			faultySequence = true;
			LogSerialiser.log("Detected fault: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
			// this was added to kill the SUT if it is frozen:
			if(verdict.severity()==SEVERITY_NOT_RESPONDING){
				//if the SUT is frozen, we should kill it!
				LogSerialiser.log("SUT frozen, trying to kill it!\n", LogSerialiser.LogLevel.Critical);
				killRunningProcesses(system, 100);
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

	//TODO: seperate Clickable from Unfiltered.
	/**
	 * Check whether a widget is clickable and NOT filtered by the regular expression in the TESTAR SettingsDialog
	 * @param w
	 * @return
	 */
	protected boolean isClickable(Widget w){
		Role role = w.get(Tags.Role, Roles.Widget);
		if(Role.isOneOf(role, NativeLinker.getNativeClickableRoles()))
			return isUnfiltered(w);
		return false;
		//FIXME: take the isUnfiltered out, we want to explicity say that in the user protocol
	}

	/**
	 * Check whether a widget is typeable
	 * @param w
	 * @return
	 */
	protected boolean isTypeable(Widget w){
		return NativeLinker.isNativeTypeable(w) && isUnfiltered(w);
		//FIXME: take the isUnfiltered out, we want to explicity say that in the user protocol
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

}

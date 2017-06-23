/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import static org.fruit.alayer.Tags.IsRunning;
import static org.fruit.alayer.Tags.OracleVerdict;
import static org.fruit.alayer.Tags.Title;
import static org.fruit.monkey.ConfigTags.ClickFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.Drag;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Action;
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
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.ProcessHandle;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import org.fruit.monkey.AbstractProtocol.Modes;
import org.fruit.monkey.AbstractProtocol.ProcessInfo;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.managers.DataManager;
import es.upv.staq.testar.prolog.PrologUtil;
import es.upv.staq.testar.serialisation.LogSerialiser;

public class DefaultProtocol extends AbstractProtocol{

	// begin by urueda
	
	protected State state = null,
			        lastState = null;
	protected int nonReactingActionNumber;
	
	// end by urueda
	
	private StateBuilder builder;

	protected final static Pen RedPen = Pen.newPen().setColor(Color.Red).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build(),
							   BluePen = Pen.newPen().setColor(Color.Blue).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build();

	protected void initialize(Settings settings){
		//builder = new UIAStateBuilder(settings.get(ConfigTags.TimeToFreeze));
		builder = NativeLinker.getNativeStateBuilder(settings.get(ConfigTags.TimeToFreeze)); // by urueda
	}
	
	protected Canvas buildCanvas() {
		//return GDIScreenCanvas.fromPrimaryMonitor(Pen.DefaultPen);
		return NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT); // by urueda
	}

	protected void beginSequence(){
		super.beginSequence(); // by urueda
		faultySequence = false;
		nonReactingActionNumber = 0; // by urueda
	}

	protected void finishSequence(File recordedSequence){
		System.out.println("Finish sequence");
		this.killTestLaunchedProcesses(); // by urueda
	}
	
	// refactored
	protected SUT startSystem() throws SystemStartException{
		return startSystem(null);
	}
		
	/**
	 * @author urueda
	 * @param mustContain Format is &lt;SUTConnector:string&gt; (e.g. SUT_PROCESS_NAME:proc_name or SUT_WINDOW_TITLE:window_title)
	 * @return
	 * @throws SystemStartException
	 */
	protected SUT startSystem(String mustContain) throws SystemStartException{
		return startSystem(mustContain, true, Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0));
	}
		
	// by urueda
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
			//Util.pause(settings().get(ConfigTags.StartupTime));
			final long now = System.currentTimeMillis(),
					   ENGAGE_TIME = tryToKillIfRunning ? Math.round(maxEngageTime / 2.0) : maxEngageTime; // half time is expected for the implementation
			State state;
			do{
				if (sut.isRunning()){
					System.out.println("SUT is running after <" + (System.currentTimeMillis() - now) + "> ms; waiting SUT to be ready ...");
					state = builder.apply(sut);
					if (state != null && state.childCount() > 0){
						long extraTime = tryToKillIfRunning ? 0 : ENGAGE_TIME;
						System.out.println("SUT ready after <" + (extraTime + (System.currentTimeMillis() - now)) + "> ms");
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
	
	// by urueda
	private SUT tryKillAndStartSystem(String mustContain, SUT sut, long pendingEngageTime) throws SystemStartException{
		// kill running SUT processes
		System.out.println("Trying to kill potential running SUT: <" + sut.get(Tags.Desc) + ">");
		if (this.killRunningProcesses(sut, Math.round(pendingEngageTime / 2.0))){ // All killed?
			// retry start system
			System.out.println("Retry SUT start: <" + sut.get(Tags.Desc) + ">");
			return startSystem(mustContain, false, pendingEngageTime); // no more try to kill
		} else
			throw new SystemStartException("SUT not running after <" + pendingEngageTime + "> miliseconds!");							
	}

	// by urueda
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

	// by urueda
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
		Assert.notNull(system); // by urueda
		//State state = builder.apply(system);
		state = builder.apply(system); // by urueda
		
		CodingManager.buildIDs(state); // by urueda

		Shape viewPort = state.get(Tags.Shape, null);
		if(viewPort != null){
			//AWTCanvas scrShot = AWTCanvas.fromScreenshot(Rect.from(viewPort.x(), viewPort.y(), viewPort.width(), viewPort.height()), AWTCanvas.StorageFormat.PNG, 1);
			state.set(Tags.ScreenshotPath, super.protocolUtil.getStateshot(state)); // by urueda
		}
		
		// begin by urueda
		Verdict verdict = getVerdict(state);
		state.set(Tags.OracleVerdict, verdict);
		if (mode() != Modes.Spy && verdict.severity() >= settings().get(ConfigTags.FaultThreshold)){
			faultySequence = true;
			LogSerialiser.log("Detected fault: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
		} else if (verdict.severity() != Verdict.SEVERITY_OK && verdict.severity() > passSeverity){ // begin by urueda
			passSeverity = verdict.severity();
			LogSerialiser.log("Detected warning: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
		}

		Grapher.notify(state, state.get(Tags.ScreenshotPath, null)); // by urueda				

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
		// end by urueda
		
		return state;
	}

	@Override // by urueda
	protected Verdict getVerdict(State state){
		Assert.notNull(state);
		//-------------------
		// ORACLES FOR FREE
		//-------------------		

		// if the SUT is not running, we assume it crashed
		if(!state.get(IsRunning, false))
			return new Verdict(SEVERITY_NOT_RUNNING, "System is offline! I assume it crashed!");

		// if the SUT does not respond within a given amount of time, we assume it crashed
		if(state.get(Tags.NotResponding, false))
			return new Verdict(SEVERITY_NOT_RESPONDING, "System is unresponsive! I assume something is wrong!");

		//------------------------
		// ORACLES ALMOST FOR FREE
		//------------------------
		
		String titleRegEx = settings().get(ConfigTags.SuspiciousTitles);
		// search all widgets for suspicious titles
		for(Widget w : state){
			String title = w.get(Title, "");
			if (title != null && !title.isEmpty() && title.matches(titleRegEx)){ // by urueda
				Visualizer visualizer = Util.NullVisualizer;
				// visualize the problematic widget, by marking it with a red box
				if(w.get(Tags.Shape, null) != null)
					visualizer = new ShapeVisualizer(RedPen, w.get(Tags.Shape), "Suspicious Title", 0.5, 0.5);
				return new Verdict(SEVERITY_SUSPICIOUS_TITLE, "Discovered suspicious widget title: '" + title + "'.", visualizer);
			}
		}
		
		if (this.nonSuitableAction){ // by urueda
			this.nonSuitableAction = false;
			return new Verdict(SEVERITY_WARNING, "Non suitable action for state");
		}
		
		// if everything was OK ...
		return Verdict.OK;
	}

	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{
		Assert.notNull(state);
		Set<Action> actions = new HashSet<Action>();	

		// create an action compiler, which helps us create actions, such as clicks, drag + drop, typing...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// if there is an unwanted process running, kill it
		String processRE = settings().get(ConfigTags.ProcessesToKillDuringTest);
		if (processRE != null && !processRE.isEmpty()){ // by urueda
			state.set(Tags.RunningProcesses, system.getRunningProcesses()); // by urueda
			for(Pair<Long, String> process : state.get(Tags.RunningProcesses, Collections.<Pair<Long,String>>emptyList())){
				if(process.left().longValue() != system.get(Tags.PID).longValue() && // by urueda
				   process.right() != null && process.right().matches(processRE)){ // pid x name
					//actions.add(ac.killProcessByName(process.right(), 2));
					this.forceKillProcess = process.right(); // by urueda
					System.out.println("will kill unwanted process: " + process.left().longValue() + " (SYSTEM <" + system.get(Tags.PID).longValue() + ">)");
					return actions;
				}
			}
		}

		// if the system is in the background force it into the foreground!
		if(!state.get(Tags.Foreground, true) && system.get(Tags.SystemActivator, null) != null){
			//actions.add(ac.activateSystem());
			this.forceToForeground = true; // bu urueda
			return actions;
		}
		
		// begin by urueda
		if(settings().get(ConfigTags.PrologActivated)){			
			List<List<String>> solutions = jipWrapper.setQuery(
				"action(A,'" + state.get(Tags.ConcreteID) + "',W," + ActionRoles.LeftClick + ",0)."
			);
			//PrologUtil.printSolutions(solutions);
			Widget w;
			for (String wid : PrologUtil.getSolutions("W", solutions)){
				w = getWidget(state,wid);
				if (w != null)
					actions.add(ac.leftClickAt(w));
			}
		}
		// end by urueda
		
		return actions;
	}
	
    // by urueda (random inputs)	
    protected String getRandomText(Widget w){
    	return DataManager.getRandomData();
    }
	
	// by urueda
	protected Set<Widget> getTopWidgets(State state){
		Set<Widget> topWidgets = new HashSet<Widget>();
		double maxZIndex = state.get(Tags.MaxZIndex);
		for (Widget w : state){
			if (w.get(Tags.ZIndex) == maxZIndex)
				topWidgets.add(w);
		}
		return topWidgets;
	}
	
	// by urueda
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
	
	// by urueda
	protected boolean isUnfiltered(Widget w){
		if(Util.hitTest(w, 0.5, 0.5) && !w.get(Title, "").matches(settings().get(ClickFilter)))
			return true;
		else
			return false;
	}
	
	// by urueda
	protected boolean isClickable(Widget w){
		Role role = w.get(Tags.Role, Roles.Widget);
		if(Role.isOneOf(role, NativeLinker.getNativeClickable()))
			return isUnfiltered(w);
		return false;
	}
	
	//by urueda
	protected boolean isTypeable(Widget w){
		Role role = w.get(Tags.Role, Roles.Widget);
		if(Role.isOneOf(role, NativeLinker.getNativeTypeable()) && NativeLinker.isNativeTypeable(w))
			return isUnfiltered(w);
		return false;
	}	

	protected boolean moreActions(State state) {
		return (!settings().get(ConfigTags.StopGenerationOnFault) || !faultySequence) && 
				state.get(Tags.IsRunning, false) && !state.get(Tags.NotResponding, false) &&
				//actionCount() < settings().get(ConfigTags.SequenceLength) &&
				actionCount() <= settings().get(ConfigTags.SequenceLength) && // by urueda
				timeElapsed() < settings().get(ConfigTags.MaxTime);
	}

	protected boolean moreSequences() {	
		//return sequenceCount() < settings().get(ConfigTags.Sequences) &&
		return sequenceCount() <= settings().get(ConfigTags.Sequences) && // by urueda		
				timeElapsed() < settings().get(ConfigTags.MaxTime);
	}

	// by urueda
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

	// by urueda
	@Override
	public void mouseMoved(double x, double y) {}

	// by urueda
	@Override
	protected void stopSystem(SUT system) {}
		
}
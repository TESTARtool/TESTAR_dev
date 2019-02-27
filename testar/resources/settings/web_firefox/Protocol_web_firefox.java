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
 *  @author (base) Sebastian Bauersfeld
 *  Web protocol (generic) authors: urueda, fraalpe2, mimarmu1
 *  @author Urko Rueda Molina (protocol refactor, cleanup and update to last TESTAR version)
 */
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

import nl.ou.testar.RandomActionSelector;
import org.fruit.Assert;
import org.fruit.Drag;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Pen;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Point;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import org.fruit.alayer.windows.UIATags;
import org.fruit.alayer.State;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.StrokePattern;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.KeyDown;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.Type;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Keyboard;

import static org.fruit.monkey.ConfigTags.*;

import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.ActionStatus;
import es.upv.staq.testar.NativeLinker;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.alayer.Tags;

import static org.fruit.alayer.Tags.NotResponding;
import static org.fruit.alayer.Tags.IsRunning;
import static org.fruit.alayer.Tags.RunningProcesses;
import static org.fruit.alayer.Tags.SystemActivator;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Title;
import static org.fruit.alayer.Tags.Foreground;
import static org.fruit.alayer.Tags.Enabled;

public class Protocol_web_firefox extends ClickFilterLayerProtocol {

	// This protocol expects Mozilla Firefox or Microsoft Internet Explorer on Windows10
	static final int BROWSER_IEXPLORER = 1;
	static final int BROWSER_FIREFOX = 2;
	static final int BROWSER_CHROME = 3;

	// browser dependent variables
	static int browser;
	static Role webText;
	static double browser_toolbar_filter;


	//Attributes for adding slide actions
	static double scrollArrowSize = 36; // sliding arrows (iexplorer)
	static double scrollThick = 16; // scroll thickness (iexplorer)
	
	/** 
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings   the current TESTAR settings as specified by the user.
	 */
	protected void initialize(Settings settings){
		super.initialize(settings);
		initBrowser();
		initialMode = settings.get(ConfigTags.Mode).toString();
	}

	/**
	 * Initialize the browser dependent variables.
	 * browser is BROWSER_IEXPLORER, BROWSER_FIREFOX, BROWSER_CHROME, or otherwise 0
	 * textfields on different browsers have different roles, webText indicates the role belonging to the
	 * browser that is used.
	 */
	private void initBrowser(){
		//Get the SUT connector string from the settings
		String sutPath = settings().get(ConfigTags.SUTConnectorValue);

		//Check which browser we use to connect to the web application
		//and set the browser dependent variables accordingly
		if (sutPath.contains("iexplore.exe")) {
			browser = BROWSER_IEXPLORER;
			webText = NativeLinker.getNativeRole("UIAEdit");
		}
		else if (sutPath.contains("firefox")) {
			browser = BROWSER_FIREFOX;
			webText = NativeLinker.getNativeRole("UIAText");
			}
			else if (sutPath.contains("chrome") || sutPath.contains("chromium")) {
				browser = BROWSER_CHROME;
				webText = NativeLinker.getNativeRole("UIAEdit");
				}
				else {
					browser = 0;
					webText = NativeLinker.getNativeRole("UIAEdit");
				}
	}
	
	/**
	 * This method is invoked each time TESTAR starts to generate a new sequence
	 */
	@Override
	protected void beginSequence(SUT system, State state){
		super.beginSequence(system, state);
	}

	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The method should
	 * take care of 
	 *   1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
	 *      out what executable to run)
	 *   2) bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 *      the SUT's configuratio files etc.)
	 *   3) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
	 *      seconds until they have finished loading)
	 *   4) bypassing a login screen by filling the username and password
	 * @return  a started SUT, ready to be tested.
	 */
	protected SUT startSystem() throws SystemStartException{

		SUT sut = super.startSystem();

		//wait a bit to give the UI some time and get focus
		Util.pause(2);

		//Enter username on .add(new Type(""),0.1)
		new CompoundAction.Builder()   
		.add(new Type(""),0.1)    
		.add(new KeyDown(KBKeys.VK_TAB),0.5)
		.build()
		.run(sut,null, 0.1); //assume next focusable field is pass   

		//wait a bit to give the UI some time and get focus
		Util.pause(1);

		//Enter password on .add(new Type(""),0.1)
		new CompoundAction.Builder()
		.add(new Type(""),0.1)
		.build()
		.run(sut, null, 0.1); 

		Util.pause(1);

		//login is performed by ENTER 
		new CompoundAction.Builder()
		.add(new KeyDown(KBKeys.VK_ENTER),0.5)
		.build()
		.run(sut, null, 0.1);

		Util.pause(2);

		return sut;

	}

	/**
	 * This method is called when TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine.
	 * @return  the current state of the SUT with attached oracle.
	 */
	protected State getState(SUT system) throws StateBuildException{

		State state = super.getState(system);

		for(Widget w : state){
			Role role = w.get(Tags.Role, Roles.Widget);
			if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAToolBar")}))
				browser_toolbar_filter = w.get(Tags.Shape,null).y() + w.get(Tags.Shape,null).height();

			if(w.get(Tags.Title,"").toString().contains("Help"))
				w.parent().set(Tags.Enabled, false);
			if(w.get(Tags.Title,"").toString().contains("LOGOUT"))
				w.parent().set(Tags.Enabled, false);

		}

		return state;

	}

	/**
	 * The getVerdict methods implements the online state oracles that
	 * examine the SUT's current state and returns an oracle verdict.
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	protected Verdict getVerdict(State state){

		// The super methods implements the implicit online state oracles for:
		// system crashes
		// non-responsiveness
		// suspicious titles
		Verdict verdict = super.getVerdict(state);

		//--------------------------------------------------------
		// MORE SOPHISTICATED STATE ORACLES CAN BE PROGRAMMED HERE
		//--------------------------------------------------------
		
		return verdict;
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return  a set of actions
	 */
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		Set<Action> actions = super.deriveActions(system,state);

		// To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		StdActionCompiler ac = new AnnotatingActionCompiler();
		
		actions.add(new KeyDown(KBKeys.VK_PAGE_DOWN));
		actions.add(new KeyDown(KBKeys.VK_PAGE_UP));

		// To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
		for(Widget w : state){
			//optional: iterate through top level widgets based on Z-index:
			//for(Widget w : getTopWidgets(state)){
			
			if(w.get(Tags.Title,"").toString().contains("El email no tiene un formato")) {
				LogSerialiser.log("WARNING: Ventana de alerta mostrada al usuario");
				Keyboard kb = AWTKeyboard.build();

				new CompoundAction.Builder()
				.add(new KeyDown(KBKeys.VK_ENTER),0.5)
				.build()
				.run(system,null, 0.5);

				kb.release(KBKeys.VK_ENTER);
			}
			//Check current browser tab, to close possible undesired tabs
			if(w.get(Tags.Title,"").toString().contains("Buscar con Google") && w.get(Tags.Role).toString().contains("UIAEdit")) {
				if(!w.get(Tags.ValuePattern,"").toString().contains("prodevelop")
						|| w.get(Tags.ValuePattern,"").toString().contains("exportarPdf")) {

					Keyboard kb = AWTKeyboard.build();
					CompoundAction cAction = new CompoundAction(new KeyDown(KBKeys.VK_CONTROL),new KeyDown(KBKeys.VK_W));

					executeAction(system, state, cAction);

					kb.release(KBKeys.VK_CONTROL);
					kb.release(KBKeys.VK_W);
				}
			}

			// Only consider enabled and non-blocked widgets
			if(w.get(Enabled, true) && !w.get(Blocked, false)){

				// Do not build actions for widgets on the blacklist
				// The blackListed widgets are those that have been filtered during the SPY mode with the
				//CAPS_LOCK + SHIFT + Click clickfilter functionality.
				if (!blackListed(w)){

					//For widgets that are:
					// - clickable
					// and
					// - unFiltered by any of the regular expressions in the Filter-tab, or
					// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
					// We want to create actions that consist of left clicking on them
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						//Store the widget in the Graphdatabase
						storeWidget(state.get(Tags.ConcreteID), w);
						//Create a left click action with the Action Compiler, and add it to the set of derived actions
						actions.add(ac.leftClickAt(w));
					}

					//For widgets that are:
					// - typeable
					// and
					// - unFiltered by any of the regular expressions in the Filter-tab, or
					// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
					// We want to create actions that consist of typing into them
					if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
						//Store the widget in the Graphdatabase
						storeWidget(state.get(Tags.ConcreteID), w);
						//Create a type action with the Action Compiler, and add it to the set of derived actions
						actions.add(ac.clickTypeInto(w, this.getRandomText(w),true));
					}
					//Add sliding actions (like scroll, drag and drop) to the derived actions
					//method defined below.
					addSlidingActions(actions,ac,scrollArrowSize,scrollThick,w, state);
				}
			}
		}
		//return the set of derived actions
		return actions;
	}

	/**
	 * Adds sliding actions (like scroll, drag and drop) to the given Set of Actions
	 * @param actions
	 * @param ac
	 * @param scrollArrowSize
	 * @param scrollThick
	 * @param w
	 */
	protected void addSlidingActions(Set<Action> actions, StdActionCompiler ac, double scrollArrowSize, double scrollThick, Widget w, State state){
		Drag[] drags = null;
		//If there are scroll (drags/drops) actions possible
		if((drags = w.scrollDrags(scrollArrowSize,scrollThick)) != null){
			//For each possible drag, create an action and add it to the derived actions
			for (Drag drag : drags){
				//Store the widget in the Graphdatabase
				storeWidget(state.get(Tags.ConcreteID), w);
				//Create a slide action with the Action Compiler, and add it to the set of derived actions
				actions.add(ac.slideFromTo(
						new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
						new AbsolutePosition(Point.from(drag.getToX(),drag.getToY()))
				));

			}
		}
	}
	
	protected boolean isClickable(Widget w){
		if (isAtBrowserCanvas(w))
			return super.isClickable(w);
		else
			return false;		
	} 

	protected boolean isTypeable(Widget w){
		if (!isAtBrowserCanvas(w))
			return false;	

		Role role = w.get(Tags.Role, null);
		if (role != null && Role.isOneOf(role, webText) && Role.isOneOf(role, NativeLinker.getNativeRole("UIAEdit")))
			return isUnfiltered(w);

		return false;
	}

	private boolean isAtBrowserCanvas(Widget w){
		Shape shape = w.get(Tags.Shape,null);
		if (shape != null && shape.y() > browser_toolbar_filter)
			return true;
		else
			return false;		
	}


	/**
	 * Select one of the available actions (e.g. at random)
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	protected Action selectAction(State state, Set<Action> actions){
		//Call the preSelectAction method from the AbstractProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		Action a = preSelectAction(state, actions);
		if (a!= null) {
			return a;
		} else
			//if no preSelected actions are needed, then implement your own strategy
			return RandomActionSelector.selectAction(actions);
	}

	/**
	 * Execute the selected action.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	protected boolean executeAction(SUT system, State state, Action action){
		//executeAction must be called only once time per action performed (best method to obtain URL?)
		String URL= "";
		for(Widget w: state) {
			if(w.get(Tags.Title,"").toString().contains("Buscar con Google") && w.get(Tags.Role).toString().contains("UIAEdit")) {
				//LogSerialiser.log(String.format("URL of state: %s\n",w.get(Tags.ValuePattern,"").toString()));
				if(!URL.equals("")) URL +=" ";
				URL += w.get(Tags.ValuePattern,"").toString();
			}
		}
		setURLofState(URL);
		
		return super.executeAction(system, state, action);
	}
	protected void waitUserActionLoop(Canvas cv, SUT system, State state, ActionStatus actionStatus){
		super.waitUserActionLoop(cv, system, state, actionStatus);
		String URL= "";
		for(Widget w: state) {
			if(w.get(Tags.Title,"").toString().contains("Buscar con Google") && w.get(Tags.Role).toString().contains("UIAEdit")) {
				//LogSerialiser.log(String.format("URL of state: %s\n",w.get(Tags.ValuePattern,"").toString()));
				if(!URL.equals("")) URL +=" ";
				URL += w.get(Tags.ValuePattern,"").toString();
			}
		}
		setURLofState(URL);
	}

	/**
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You can stop deriving more actions after:
	 * - a specified amount of executed actions, which is specified through the SequenceLength setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 * @return  if <code>true</code> continue generation, else stop
	 */
	protected boolean moreActions(State state) {
		return super.moreActions(state);
	}


	/**
	 * TESTAR uses this method to determine when to stop the entire test sequence
	 * You could stop the test after:
	 * - a specified amount of sequences, which is specified through the Sequences setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 * @return  if <code>true</code> continue test, else stop
	 */
	protected boolean moreSequences() {
		return super.moreSequences();
	}

	/**
	 * Here you can put graceful shutdown sequence for your SUT
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		super.stopSystem(system);
	}
	
	/**
	 * This method is called after each sequence, to allow for example checking the coverage of the sequence
	 */
	private String initialMode = "";
	protected void postSequenceProcessing(){

		super.postSequenceProcessing();
		
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS").format(new Date());
		//String mode = mode().toString(); //TESTAR can change into the testing loop, mode() not useful?
		String testCaseName = settings().get(ConfigTags.TestCaseName);
		String status = verdictSeverityTitle(getFinalVerdictInfo().severity());
		String statusInfo = getFinalVerdictInfo().info();
		
		String replayedSequence = "";
		if(initialMode.contains("Replay"))
			replayedSequence = new File(settings.get(ConfigTags.PathToReplaySequence)).getName();
		
		//Timestamp SequenceID Mode SequenceFileName "TestCaseName" Status "StatusInfo" "ReplayedSequence"
		DEBUGLOG.info("sequence"+sequenceCount()+" "+initialMode+" "+ getCurrentSequence()+" \""+testCaseName+"\" "+status+" \""+statusInfo+"\" \""+replayedSequence+"\"");
		System.out.println(timeStamp+" sequence"+sequenceCount()+" "+initialMode+" "+ getCurrentSequence()+" \""+testCaseName+"\" "+status+" \""+statusInfo+"\" \""+replayedSequence+"\"");
	}

	private String verdictSeverityTitle(double severity) {
		if(severity == Verdict.SEVERITY_MIN)
			return "OK";
		if(severity == Verdict.SEVERITY_WARNING)
			return "WARNING";
		if(severity == Verdict.SEVERITY_SUSPICIOUS_TITLE)
			return "SUSPICIOUS_TITLE";
		if(severity == Verdict.SEVERITY_NOT_RESPONDING)
			return "ERROR";
		if(severity == Verdict.SEVERITY_NOT_RUNNING)
			return "ERROR";
		
		return "WARNING";
	}
}

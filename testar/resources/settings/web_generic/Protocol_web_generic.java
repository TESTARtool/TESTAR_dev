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


import java.io.File;
import java.util.Set;

import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Action;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Point;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.KeyDown;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.Type;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Keyboard;

import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.RandomActionSelector;
import es.upv.staq.testar.ActionStatus;
import es.upv.staq.testar.NativeLinker;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.alayer.Tags;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

public class Protocol_web_generic extends ClickFilterLayerProtocol {

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
	}

	// check browser
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
	 * @return  a started SUT, ready to be tested.
	 */
	protected SUT startSystem() throws SystemStartException{

		SUT sut = super.startSystem();

		//wait a bit to give the UI some time and get focus
		Util.pause(5);

		//Enter password on .add(new Type(""),0.1)
		new CompoundAction.Builder()   
		.add(new Type("password"),0.5)
		.build()
		.run(sut,null, 0.5);   

		//wait a bit to give the UI some time and get focus
		Util.pause(1);

		//login is performed by ENTER
		new CompoundAction.Builder()
		.add(new KeyDown(KBKeys.VK_ENTER),0.5)
		.build()
		.run(sut, null, 0.5); 

		Util.pause(1);

		//Domain selection is performed by ENTER 
		new CompoundAction.Builder()
		.add(new KeyDown(KBKeys.VK_ENTER),0.5)
		.build()
		.run(sut, null, 0.5);

		//Wait x seconds
		Util.pause(5);

		return sut;

	}

	/**
	 * This method is called when TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle 
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the 
	 * state is erroneous and if so why.
	 * @return  the current state of the SUT with attached oracle.
	 */
	protected State getState(SUT system) throws StateBuildException{

		State state = super.getState(system);

		for(Widget w : state){
			Role role = w.get(Tags.Role, Roles.Widget);
			//Qualitate
			//w.get(Tags.Path).length()<15 is a temporal condition to filter only Web ToolBar and not all existing toolbar widgets
			if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAToolBar")}) && w.get(Tags.Path).length()<15)
				browser_toolbar_filter = w.get(Tags.Shape,null).y() + w.get(Tags.Shape,null).height();
		}

		return state;

	}

	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
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

		Set<Action> actions = super.deriveActions(system,state);
		// unwanted processes, force SUT to foreground, ... actions automatically derived!

		// create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// iterate through all widgets
		for(Widget w : getTopWidgets(state)){

			//Check current browser tab, to close possible undesired tabs
			/*if(w.get(Tags.Title,"").toString().contains("Address and search")) {
				if(!w.get(Tags.ValuePattern,"").toString().contains("vims-alm")) {

					Keyboard kb = AWTKeyboard.build();
					CompoundAction cAction = new CompoundAction(new KeyDown(KBKeys.VK_CONTROL),new KeyDown(KBKeys.VK_W));

					executeAction(system, state, cAction);

					kb.release(KBKeys.VK_CONTROL);
					kb.release(KBKeys.VK_W);
				}
			}*/
			
			
			// Only consider enabled and non-blocked widgets
			
			//Qualitate
			//Instead of disable Enabled property, force actions into "disabled" buttons
			if(/*w.get(Enabled, true) &&*/ !w.get(Blocked, false)){

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
						actions.add(ac.clickTypeInto(w, this.getRandomText(w)));
					}
					//Add sliding actions (like scroll, drag and drop) to the derived actions
					//method defined below.
					addSlidingActions(actions,ac,scrollArrowSize,scrollThick,w,state);
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
		if (role != null && Role.isOneOf(role, webText))
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
		//Save URL of web browser in the logs (Generate)
		/*for(Widget w: state) {
			if(w.get(Tags.Title,"").toString().contains("Address and search")) {
				LogSerialiser.log(String.format("URL of state: %s\n",w.get(Tags.ValuePattern,"").toString()));
				LOGGER.info(String.format("URL of state: %s",w.get(Tags.ValuePattern,"").toString()));
			}
		}*/
		
		return super.executeAction(system, state, action);
	}
	protected void waitUserActionLoop(Canvas cv, SUT system, State state, ActionStatus actionStatus){
		super.waitUserActionLoop(cv, system, state, actionStatus);
		//Save URL of web browser in the logs (Generate Manual)
		/*for(Widget w: state) {
			if(w.get(Tags.Title,"").toString().contains("Address and search")) {
				LogSerialiser.log(String.format("URL of state: %s\n",w.get(Tags.ValuePattern,"").toString()));
				LOGGER.info(String.format("URL of state: %s",w.get(Tags.ValuePattern,"").toString()));
			}
		}*/
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
	 * This method is invoked each time after TESTAR finished the generation of a sequence.
	 */
	protected void finishSequence(){

		super.finishSequence();

	}

}

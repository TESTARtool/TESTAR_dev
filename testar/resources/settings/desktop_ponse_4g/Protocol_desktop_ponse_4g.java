/***************************************************************************************************
*
* Copyright (c) 2018 Open Universiteit, www.ou.nl
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
import java.util.HashSet;
import java.util.Set;

import es.upv.staq.testar.CodingManager;
import nl.ou.testar.HtmlSequenceReport;
import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.SimpleGuiStateGraph.GuiStateGraphWithVisitedActions;
import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import org.fruit.alayer.windows.UIATags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

/**
 * This protocol is specifically implemented for Ponsse 4G GUI
 * It uses
 * - HTML sequence report
 * - SikuliX for image recognition (due to resolution and other stuff messing up the mouse coordinates)
 * - Simple GUI state graph for steering the action selection
 *
 */
public class Protocol_desktop_ponse_4g extends ClickFilterLayerProtocol {

	//Attributes for adding slide actions
	static double scrollArrowSize = 36; // sliding arrows
	static double scrollThick = 16; //scroll thickness

	private HtmlSequenceReport htmlReport;
	private GuiStateGraphWithVisitedActions stateGraphWithVisitedActions;

	/** 
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		//initializing the HTML sequence report:
		htmlReport = new HtmlSequenceReport();
		// initializing simple GUI state graph:
		stateGraphWithVisitedActions = new GuiStateGraphWithVisitedActions();
		super.initialize(settings);
	}

	/**
	 * This method is invoked each time the TESTAR starts to generate a new sequence
	 */
	 @Override
	protected void beginSequence(SUT system, State state){
		 // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		 StdActionCompiler ac = new AnnotatingActionCompiler();
	 	// driving the GUI into a state to start testing
		 for(Widget w : state){
		 	// the test users name is "test"
			 if(w.get(Tags.Title, "no title").equalsIgnoreCase("test")){
			 	Action a = ac.leftClickAt(w);
			 	//creating ConcreteID tag for the action:
				CodingManager.buildIDs(state,a);
			 	executeAction(system,state,a);
			 	//waiting for the GUI to load:
			 	Util.pause(10);
			 }
		 }
		 state = getState(system);
		 for(Widget w : state){
			 if(w.get(Tags.Title, "no title").equals("REPORTING")){
				 Action a = ac.leftClickAt(w);
				 //creating ConcreteID tag for the action:
				 CodingManager.buildIDs(state,a);
				 executeAction(system,state,a);
				 //waiting for the GUI to load:
				 Util.pause(1);
			 }
		 }
		 state = getState(system);
		 for(Widget w : state){
			 if(w.get(Tags.Title, "no title").equals("Reporting")){
				 Action a = ac.leftClickAt(w);
				 //creating ConcreteID tag for the action:
				 CodingManager.buildIDs(state,a);
				 executeAction(system,state,a);
				 //waiting for the GUI to load:
				 Util.pause(4);
			 }
		 }
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
	@Override
	protected SUT startSystem() throws SystemStartException{
		
		SUT sut = super.startSystem();
		
		return sut;

	}

	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle 
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the 
	 * state is erroneous and if so why.
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException{

		return super.getState(system);
	}

	/**
	 * The getVerdict methods implements the online state oracles that
	 * examine the SUT's current state and returns an oracle verdict.
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
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
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		Set<Action> actions = super.deriveActions(system,state);


		Set<Widget> widgets = deriveModalWidgets(state);
		if(widgets.size()>0){
			System.out.println("Ponsse protocol: there are "+widgets.size()+" modal widgets!");
		}else{
			System.out.println("Ponsse protocol: no modal widgets found.");
		}

		// Ponsse GUI has some modal pop-up dialogs - trying if getTopWidgets would handle those:
		for(Widget w : getTopWidgets(state)){
			Action action = derivePonsseAction(w);
			if(action!=null){
				actions.add(action);
			}
		}

		if(actions.isEmpty()){
			System.out.println("Ponsse protocol: found 0 actions from top level widgets, trying all widgets");
			for(Widget w: state){
				Action action = derivePonsseAction(w);
				if(action!=null){
					actions.add(action);
				}
			}
		}

		System.out.println("Ponsse protocol: found "+actions.size()+" actions (after filtering):");
		for(Action a:actions){
			System.out.println(a.get(Tags.Desc, "Desc not available"));
		}

		//return the set of derived actions
		return actions;
	}

	private Action derivePonsseAction(Widget w){
		// To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		StdActionCompiler ac = new AnnotatingActionCompiler();
		//optional: iterate through top level widgets based on Z-index:
		//for(Widget w : getTopWidgets(state)){

		//System.out.println("DEBUG: widget: "+w.toString());

		// Only consider enabled and non-blocked widgets
		if(w.get(Enabled, true)
			//&& !w.get(Blocked, false) //in Ponsse GUI all seem to be blocked
				){

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
				//if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) { //in Ponsse GUI apparently nothing is clickable
				try{
					if(w.get(Tags.Role).toString().equalsIgnoreCase("UIAPane") && w.get(UIATags.UIAName).length()>0) { //in Ponsse GUI all buttons seem to have role "UIAPane"
						// not pressing EcoDrive or Mittari buttons:
						if(w.get(Tags.Title, "no title").equalsIgnoreCase("EcoDrive")){
							// it seems in the Ponsse GUI there is no difference in the TESTAR parameters of "enabled" and "disabled" button
//								System.out.println("-------------- EcoDrive -------------");
//								for(Tag t:w.tags()){
//									System.out.println("DEBUG: "+t+"="+w.get(t));
//								}
//
						}else if(w.get(Tags.Title, "no title").equalsIgnoreCase("Mittari")){
//								System.out.println("-------------- Mittari -------------");
//								for(Tag t:w.tags()){
//									System.out.println("DEBUG: "+t+"="+w.get(t));
//								}
						}else if(w.get(Tags.Title, "no title").equalsIgnoreCase("Pienenna")){
							// removing this action
						}else if(w.get(Tags.Title, "no title").equalsIgnoreCase("OptiWin")){
							// removing this action
						}else if(w.get(Tags.Title, "no title").equalsIgnoreCase("OptiReport")){
							// removing this action from Reporting - seems to crash
						}
						//TODO skip following actions: drag action
						else{
							//Store the widget in the Graphdatabase
							storeWidget(state.get(Tags.ConcreteID), w);
							//Create a left click action with the Action Compiler, and add it to the set of derived actions
							return(ac.leftClickAt(w));
						}
					}
				}catch(Exception e){}

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
					return(ac.clickTypeInto(w, this.getRandomText(w)));
				}
				//Add sliding actions (like scroll, drag and drop) to the derived actions
				//method defined below.
				//addSlidingActions(actions,ac,scrollArrowSize,scrollThick,w);
			}
		}
		return null;
	}

	private Set<Widget> deriveModalWidgets(State state){
		Set<Widget> widgets = new HashSet<Widget>();
		StdActionCompiler ac = new AnnotatingActionCompiler();

		for(Widget w : state){
			if(w.get(Tags.Modal, false)){
				widgets.add(w);
			}
		}

		return widgets;
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

	/**
	 * Select one of the available actions (e.g. at random)
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		//adding state to the HTML sequence report:
		try {
			htmlReport.addState(state, actions, stateGraphWithVisitedActions.getConcreteIdsOfUnvisitedActions(state));
		}catch(Exception e){
			// catching null for the first state or any new state, when unvisited actions is still null
			htmlReport.addState(state, actions);
		}

		//Call the preSelectAction method from the AbstractProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		System.out.println("Number of available actions: "+actions.size());
		// disabling bring to foreground actions:
//		Action a = preSelectAction(state, actions);
//		if (a!= null) {
//			// returning pre-selected action
//		} else{
			//if no preSelected actions are needed, then implement your own action selection strategy
			// Maintaining memory of visited states and selected actions, and selecting randomly from unvisited actions:
		Action a = stateGraphWithVisitedActions.selectAction(state,actions);
			//a = RandomActionSelector.selectAction(actions);
//		}
		htmlReport.addSelectedAction(state.get(Tags.ScreenshotPath), a);
		System.out.println("Selected action:" + a.get(Tags.Desc, "Desc not available"));
		return a;
	}

	/**
	 * Execute the selected action.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action){
		double waitTime = settings().get(ConfigTags.TimeToWaitAfterAction);
		try{
			double halfWait = waitTime == 0 ? 0.01 : waitTime / 2.0; // seconds
			System.out.println("Executing action: "+action.get(Tags.Desc));
//			for(Tag t:action.tags()){
//				System.out.println("Debug: "+t+"="+action.get(t));
//			}
			if(action.toShortString().equalsIgnoreCase("LeftClickAt")){
				String widgetScreenshotPath = protocolUtil.getActionshot(state,action);
				Screen sikuliScreen = new Screen();
				try {
					//System.out.println("DEBUG: sikuli clicking ");
					while(!new File(widgetScreenshotPath).exists()){
						//System.out.println("Waiting for image file to exist");
						Util.pause(halfWait);
					}
					Util.pause(1);
					sikuliScreen.click(widgetScreenshotPath);
				} catch (FindFailed findFailed) {
					findFailed.printStackTrace();
					return false;
				}
			}else if(action.toShortString().contains("ClickTypeInto(")){
				String textToType = action.toShortString().substring(action.toShortString().indexOf("("), action.toShortString().indexOf(")"));
				//System.out.println("parsed text:"+textToType);
				String widgetScreenshotPath = protocolUtil.getActionshot(state,action);
				Util.pause(halfWait);
				Screen sikuliScreen = new Screen();
				try {
					//System.out.println("DEBUG: sikuli typing ");
					while(!new File(widgetScreenshotPath).exists()){
						//System.out.println("Waiting for image file to exist");
						Util.pause(halfWait);
					}
					Util.pause(1);
					sikuliScreen.type(widgetScreenshotPath,textToType);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			else {
				//System.out.println("DEBUG: TESTAR action");
				//System.out.println("DEBUG: action desc: "+action.get(Tags.Desc));
				action.run(system, state, settings().get(ConfigTags.ActionDuration));
			}return true;
		}catch(ActionFailedException afe){
			return false;
		}
	}

	/**
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You can stop deriving more actions after:
	 * - a specified amount of executed actions, which is specified through the SequenceLength setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 * @return  if <code>true</code> continue generation, else stop
	 */
	@Override
	protected boolean moreActions(State state) {
		return super.moreActions(state);
	}


	/**
	 * TESTAR uses this method to determine when to stop the entire test sequence
	 * You could stop the test after:
	 * - a specified amount of sequences, which is specified through the Sequences setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 * @return  if <code>true</code> continue test, else stop	 */
	@Override
	protected boolean moreSequences() {
		return super.moreSequences();
	}

	/**
	 * Here you can put graceful shutdown sequence for your SUT
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		htmlReport.close();
		super.stopSystem(system);
	}

}

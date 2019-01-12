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


import java.io.File;
import java.util.HashSet;
import java.util.Set;

import nl.ou.testar.HtmlSequenceReport;
import nl.ou.testar.RandomActionSelector;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol; 
import es.upv.staq.testar.NativeLinker;
import org.fruit.alayer.windows.UIARoles;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

/**
 * This protocol is being developed to demonstrate TESTAR on the public website of Kuveyt Turk
 * https://www.kuveytturk.com.tr/en/
 * 
 */
public class Protocol_web_kuveytturk extends ClickFilterLayerProtocol {
	
	// This protocol expects Mozilla Firefox or Microsoft Internet Explorer on Windows10

	static Role webText; // browser dependent
	static double browser_toolbar_filter;

	//Attributes for adding slide actions
	static double scrollArrowSize = 36; // sliding arrows (iexplorer)
	static double scrollThick = 16; // scroll thickness (iexplorer)

	private HtmlSequenceReport htmlReport;
	private Set<Action> previousActions;
	private Action previouslySelectedAction;

	/** 
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings   the current TESTAR settings as specified by the user.
	 */
	protected void initialize(Settings settings){
		//initializing the HTML sequence report:
		htmlReport = new HtmlSequenceReport();
		super.initialize(settings);
		initBrowser();
	}
	
	// check browser
	private void initBrowser(){
		webText = NativeLinker.getNativeRole("UIAEdit"); // just init with some value
		String sutPath = settings().get(ConfigTags.SUTConnectorValue);
		if (sutPath.contains("iexplore.exe"))
			webText = NativeLinker.getNativeRole("UIAEdit");
		else if (sutPath.contains("firefox"))
			webText = NativeLinker.getNativeRole("UIAText");
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
            if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAToolBar")}))
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
		
		Verdict verdict = super.getVerdict(state);
		// system crashes, non-responsiveness and suspicious titles automatically detected!
		
		//-----------------------------------------------------------------------------
		// MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
        //-----------------------------------------------------------------------------

		// ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...
		
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
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

		Set<Action> actions = super.deriveActions(system, state);
		// unwanted processes, force SUT to foreground, ... actions automatically derived!

		Set<Action> priorityActions = new HashSet<Action>();

		// create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

			//----------------------
			// BUILD CUSTOM ACTIONS
			//----------------------

			// iterate through all widgets of the state
			System.out.println("---------new state---------");
			for (Widget w : state) {
				Action action = null;
				if (w.get(Enabled, true) && !w.get(Blocked, false)) { // only consider enabled and non-blocked widgets
					if (!blackListed(w)) {  // do not build actions for click filtered widgets

						// links with ValuePattern
						if (w.get(Tags.ValuePattern, null) != null) { //ValuePattern is not null
							if (w.get(Tags.ValuePattern).contains("http")) { // ValuePattern contains "http"
								String URL = w.get(Tags.ValuePattern);
								String domain = URL.substring(URL.indexOf("//") + 2);
								domain = domain.substring(0, domain.indexOf("/"));
								String folder = URL.substring(URL.indexOf(domain) + domain.length() + 1);
								if (folder.endsWith("/")) {
									folder = folder.substring(0, folder.length() - 1);
								}
								//System.out.println("Link folder= "+folder);
								if (URL.equalsIgnoreCase("https://www.kuveytturk.com.tr/")) {
									// disabling Turkish language
								}else if (URL.startsWith("https://www.kuveytturk.com.tr/ar/")) {
									// disabling Armenian (?) language
								}else if (URL.startsWith("https://www.kuveytturk.com.tr/en/")) {
									if (folder.startsWith("en/")) {
										folder = folder.substring(3);
										//System.out.println("Link folder= "+folder);
									}
									if (folder.contains("/")) {
										//System.out.println("Internal link that is NOT visible, folder="+folder);
										if (previouslySelectedAction != null) {
											if (previouslySelectedAction.toShortString().startsWith("Left")) {
												String target = getClickTargetFromActionDesc(previouslySelectedAction.get(Tags.Desc));
												target = target.toLowerCase();
												if (folder.contains(target)) {
													//System.out.println("DEBUG: folder is the target of previous action");
													action = ac.leftClickAt(w);
												}
											}
										}
									} else {
										//System.out.println("Internal link that is visible, folder="+folder);
										action = ac.leftClickAt(w);
									}
								}
								// these links open new tabs (that are different processes?)
//								else if (URL.contains("isube")) {
//									//System.out.println("DEBUG: URL contains isube");
//									if (previouslySelectedAction != null) {
//										if(previouslySelectedAction.toShortString().startsWith("Left")){
//											String target = getClickTargetFromActionDesc(previouslySelectedAction.get(Tags.Desc));
//											if (target.equalsIgnoreCase("Internet Banking")) {
//												System.out.println("DEBUG: adding isube after internet banking");
//												action = ac.leftClickAt(w);
//											}
//										}
//									}
//								}
								// harshly keeping to the English area of the website, not going external websites:
//								else if (URL.contains("kuveytturk.com.tr")) {
//									//System.out.println("Internal link not in English area, address="+URL);
//									if (folder.contains("/")) {
//										//System.out.println("Internal link not in English area that is NOT visible, folder="+folder);
//									} else {
//										//System.out.println("Internal link not in English area that is visible, folder="+folder);
//										action = ac.leftClickAt(w);
//									}
//								} else {
//									//System.out.println("External link, address="+URL);
//									action = ac.leftClickAt(w);
//								}
								// Printing out all tags and their values of a single hidden menu "Accounts"
//								if(w.get(Tags.Title).equalsIgnoreCase("Accounts")){
//									System.out.println("DEBUG: -----------------------------");
//									for(Tag tag:w.tags()){
//										System.out.println("DEBUG: "+tag.toString()+"="+w.get(tag));
//									}
//								}
							}
						} // ValuePattern not null
						else if (w.get(Tags.Title, "").equalsIgnoreCase("Internet Banking")) {
							action = ac.leftClickAt(w);
							//System.out.println("DEBUG: adding Internet Banking button, target="+getClickTargetFromActionDesc(action.get(Tags.Desc)));
						}

						if (action != null) {
							actions.add(action);
							if (previousActions != null) {
								if (wasNotAvailableInPreviousState(action)) {
									//System.out.println("DEBUG: new action: "+action.get(Tags.Desc));
									priorityActions.add(action);
								}
							}
						}
						// left clicks
						//if(isClickable(w))
						//	actions.add(ac.leftClickAt(w));

						// type into text boxes
						//if(isTypeable(w))
						//	actions.add(ac.clickTypeInto(w, this.getRandomText(w)));

						// slides
						//addSlidingActions(actions,ac,scrollArrowSize,scrollThick,w);

					} // not black lister
				} // enabled and not blocked
			} // Iterating through all widgets

		System.out.println("Deriving actions, found "+actions.size()+" possible actions.");
		//updating this action set to be previous actions for next state:
		previousActions = actions;
		if(priorityActions.size()>0){
			System.out.println("Prioritizing "+priorityActions.size()+" new actions that were not available in previous state.");
			return priorityActions;
		}
		return actions;
		
	}

	private boolean wasNotAvailableInPreviousState(Action action){
		if(previousActions!=null){
			boolean similarActionFound = false;
			for(Action a:previousActions){
				if(a.get(Tags.Desc, null)!=null && action.get(Tags.Desc, null)!=null){
					if(a.get(Tags.Desc).equalsIgnoreCase(action.get(Tags.Desc))){
						//System.out.println("Similar actions: "+a.get(Tags.Desc)+"=="+action.get(Tags.Desc));
						similarActionFound = true;
					}
				}
			}
			if(!similarActionFound){
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean isClickable(Widget w){
		if (isAtBrowserCanvas(w))
			return super.isClickable(w);
		else
			return false;		
	} 

	@Override
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
	 * Select one of the possible actions (e.g. at random)
	 * @param state the SUT's current state
	 * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
	 * @return  the selected action (non-null!)
	 */
	protected Action selectAction(State state, Set<Action> actions){
		// Saving state and available actions into HTML report before selecting the action to execute
		htmlReport.addState(state, actions);

		//Call the preSelectAction method from the AbstractProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		Action a = preSelectAction(state, actions);
		if (a!= null) {
			// returning pre-selected action
		} else{
			//if no preSelected actions are needed, then implement your own action selection strategy
			a = RandomActionSelector.selectAction(actions);
		}
		htmlReport.addSelectedAction(state.get(Tags.ScreenshotPath), a);
		System.out.println("Selecting action to execute, selected: "+a.get(Tags.Desc, "Description unavailable"));
		previouslySelectedAction=a;
		return a;
	}

	private String getClickTargetFromActionDesc(String actionDesc){
		String clickTarget = actionDesc;
		clickTarget = clickTarget.substring(clickTarget.indexOf("'")+1);
		clickTarget = clickTarget.substring(0,clickTarget.indexOf("'"));
		return clickTarget;
	}

	/**
	 * Execute the selected action.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	protected boolean executeAction(SUT system, State state, Action action){
		
		return super.executeAction(system, state, action);
		
	}
	
	/**
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You could stop the sequence's generation after a given amount of executed
	 * actions or after a specific time etc.
	 * @return  if <code>true</code> continue generation, else stop
	 */
	protected boolean moreActions(State state) {
		
		return super.moreActions(state);
		
	}



	/**
	 * TESTAR uses this method to determine when to stop the entire test.
	 * You could stop the test after a given amount of generated sequences or
	 * after a specific time etc.
	 * @return  if <code>true</code> continue test, else stop	 */
	protected boolean moreSequences() {
		
		return super.moreSequences();
		
	}
		
}

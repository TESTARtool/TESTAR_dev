/***************************************************************************************************
*
* Copyright (c) 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
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


import java.util.Set;
import nl.ou.testar.SimpleGuiStateGraph.GuiStateGraphWithVisitedActions;
import nl.ou.testar.HtmlReporting.HtmlSequenceReport;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.monkey.Settings;
import org.fruit.alayer.Tags;
import org.testar.protocols.DesktopProtocol;


/**
 * This is a small change to Desktop Generic Protocol to use a simple in-memory state model
 * and prioritize unvisited actions to improve the action selection.
 *
 * It also creates an HTML report of the sequences with screenshots of the GUI states.
 *
 *  It changes the initialize() and selectAction() methods.
 */
public class Protocol_desktop_simple_stategraph extends DesktopProtocol {

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
		htmlReport = new HtmlSequenceReport(sequenceCount());
		// initializing simple GUI state graph:
		stateGraphWithVisitedActions = new GuiStateGraphWithVisitedActions();
		super.initialize(settings);
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
		Action a = preSelectAction(state, actions);
		if (a!= null) {
			// returning pre-selected action
		} else{
			//if no preSelected actions are needed, then implement your own action selection strategy
			// Maintaining memory of visited states and selected actions, and selecting randomly from unvisited actions:
			a = stateGraphWithVisitedActions.selectAction(state,actions);
			//a = RandomActionSelector.selectAction(actions);
		}
		htmlReport.addSelectedAction(state.get(Tags.ScreenshotPath), a);
		return a;
	}

}

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
 * Windows calculator desktop protocol
 * @author Urko Rueda Molina
 */

import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.util.Set;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

public class Protocol_desktop_wincalc extends ClickFilterLayerProtocol { // DefaultProtocol {

	/** 
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings   the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){

		super.initialize(settings);

	}
	

	/**
	 * This method is invoked each time the TESTAR starts to generate a new sequence
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
     * @return  a started SUT, ready to be tested.
	 */
	@Override
	protected SUT startSystem() throws SystemStartException{
		
		SUT sut = super.startSystem();

		// begin by urueda
		if (mode() != Modes.Spy){
			// bring the calculator to an initial state
	        new CompoundAction.Builder()
//	            .add(new KeyDown(VK_ALT),0.2)
//	            .add(new KeyDown(KBKeys.VK_1),0.1)
//	            .add(new KeyUp(KBKeys.VK_1),0.0)
//	            .add(new KeyUp(KBKeys.VK_ALT),0.0)
//	            .add(new NOP(),0.1)
//	            .add(new KeyDown(KBKeys.VK_CONTROL),0.2)
//	            .add(new KeyDown(KBKeys.VK_F4),0.1)
//	            .add(new KeyUp(KBKeys.VK_F4),0.0)
//	            .add(new KeyUp(KBKeys.VK_CONTROL),0.0)
	            .add(new NOP(),0.1)
	            .build()
	            .run(sut, null, 0.8);
		}
		Util.pause(0.1);
        // end by urueda
		
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
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state){

		Verdict verdict = super.getVerdict(state); // by urueda
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
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{
		
		Set<Action> actions = super.deriveActions(system,state); // by urueda
		// unwanted processes, force SUT to foreground, ... actions automatically derived!

		// create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();
		
		//----------------------
		// BUILD CUSTOM ACTIONS
		//----------------------
		
		if (!settings().get(ConfigTags.PrologActivated)){ // is prolog deactivated?
			
			// iterate through all widgets
			for(Widget w : getTopWidgets(state)){
				if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets
					if (!blackListed(w)){  // do not build actions for tabu widgets  
						
						// left clicks
						if(whiteListed(w) || isClickable(w))
							actions.add(ac.leftClickAt(w));
		
						// type into text boxes
						if(whiteListed(w) || isTypeable(w))
							actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					
					}
				}
			}			
			
		}
		
		return actions;

	}

	/**
	 * Select one of the possible actions (e.g. at random)
	 * @param state the SUT's current state
	 * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){ 

		return super.selectAction(state, actions);

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
		
		return super.executeAction(system, state, action);
		
	}
	

	/**
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You could stop the sequence's generation after a given amount of executed
	 * actions or after a specific time etc.
	 * @return  if <code>true</code> continue generation, else stop
	 */
	@Override
	protected boolean moreActions(State state) {

		return super.moreActions(state);

	}


	/** 
	 * This method is invoked each time after TESTAR finished the generation of a sequence.
	 */
	@Override
	protected void finishSequence(){
		
		super.finishSequence();
		
	}


	/**
	 * TESTAR uses this method to determine when to stop the entire test.
	 * You could stop the test after a given amount of generated sequences or
	 * after a specific time etc.
	 * @return  if <code>true</code> continue test, else stop	 */
	@Override
	protected boolean moreSequences() {

		return super.moreSequences();

	}
	
}

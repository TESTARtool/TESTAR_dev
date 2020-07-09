
/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

import org.fruit.Util;
import java.util.Set;
import nl.ou.testar.HtmlReporting.HtmlSequenceReport;
import nl.ou.testar.ScreenshotJsonFile.JsonUtils;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.RuntimeControlsProtocol.Modes;
import org.testar.protocols.DesktopProtocol;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Title;
import static org.fruit.alayer.Tags.Enabled;

import java.util.Set;

/**
 * This protocol provides default TESTAR behaviour to test Windows desktop applications.
 *
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_generic_pure_random_postman extends DesktopProtocol {

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);
	}

	/**
	 * This methods is called before each test sequence, before startSystem(),
	 * allowing for example using external profiling software on the SUT
	 *
	 * HTML sequence report will be initialized in the super.preSequencePreparations() for each sequence
	 */
	@Override
	protected void preSequencePreparations() {
		super.preSequencePreparations();
	}

	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The method should
	 * take care of
	 *   1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
	 *      out what executable to run)
	 *   2) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
	 *      seconds until they have finished loading)
	 * @return  a started SUT, ready to be tested.
	 */
	@Override
	protected SUT startSystem() throws SystemStartException{
		return super.startSystem();
	}

    int run = 1;

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	 @Override
	protected void beginSequence(SUT system, State state){
        // Give Postman some time to load.
        Util.pause(10);

        Keyboard kb = AWTKeyboard.build();
        
        Util.pause(5);

        state = getState(system);

        // If not signed in yet, sign in.
        for(Widget w :state) {    
            if(w.get(Tags.Title ,"").contains("Sign In") && isInMenuBar(w, state) && run == 1) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                Action a = ac.leftClickAt(w);
                executeAction(system , state , a);
                
                Util.pause(5);
                state = getState(system);
                                
                new CompoundAction.Builder()  
                .add(new Type("rick.holterman.rh"),0.5).build()
                .run(system, null, 1);
                
                kb.press(KBKeys.VK_SHIFT);
                kb.press(KBKeys.VK_2);
                kb.release(KBKeys.VK_2);
                kb.release(KBKeys.VK_SHIFT);
                
                Util.pause(2);
                state = getState(system);
                
                 new CompoundAction.Builder()  
                .add(new Type("gmail.com"),0.5).build()
                .run(system, null, 1);
                
                kb.press(KBKeys.VK_TAB);
            
                Util.pause(1);
            
                new CompoundAction.Builder()
                .add(new Type("TestarPostman"),0.5)   
                .add(new KeyDown(KBKeys.VK_ENTER),0.5).build()
                .run(system, null, 1);
                
                Util.pause(5);
                
                state = getState(system);
                
                // Click the "Sign In"-button".
                for(Widget w2 :state) {    
                    if(w2.get(Tags.Title ,"").contains("Sign In")) {
                        StdActionCompiler ac2 = new AnnotatingActionCompiler();
                        Action a2 = ac2.leftClickAt(w2);
                        executeAction(system , state , a2);
                    }
                }
                
                Util.pause(5);
            }    
        }
        
        Util.pause(5);
        
        if (run == 1) {
            run++;
            stopSystem(system);
            
            Util.pause(5);

            return;
        }
        
        // Press ctrl + T to open a new request-tab.
        kb.press(KBKeys.VK_CONTROL);
        kb.press(KBKeys.VK_T);
        kb.release(KBKeys.VK_T);
        kb.release(KBKeys.VK_CONTROL);
        
        Util.pause(6);
        
        state = getState(system);
        
        Util.pause(3);
        
        // Target the METHOD-field.
        for(Widget w :state) {   
            if(w.get(Tags.Title ,"").contains("METHOD")) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                Action a = ac.leftClickAt(w);
                executeAction(system , state , a);
            }
        }
        
        // Tab to the request URL field.
        kb.press(KBKeys.VK_TAB);
        kb.press(KBKeys.VK_TAB);
        
        // Fill out a URL to the Youtube REST-API.
        new CompoundAction.Builder()  
        .add(new Type("https"),0.5).build()
        .run(system, null, 1);
        
        kb.press(KBKeys.VK_SHIFT);
        kb.press(KBKeys.VK_SEMICOLON);
        kb.release(KBKeys.VK_SEMICOLON);
        kb.release(KBKeys.VK_SHIFT);
        
        new CompoundAction.Builder()  
        .add(new Type("//www.googleapis.com/youtube/v3/activities"),0.5).build()
        .run(system, null, 1);
        
        Util.pause(1);
        
        // Set a valid API-key as a query param.
        for(Widget w :state) {    
            if(w.get(Tags.Title ,"").contains("Key")) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                Action a = ac.clickTypeInto(w, "key", true);
                executeAction(system , state , a);
            }
        }

        for(Widget w :state) {    
            if(w.get(Tags.Title ,"").contains("Value")) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                Action a = ac.clickTypeInto(w, "AIzaSyCzcNSVwuukMcgkCUk98suvKexNC3wBsNk", true);
                executeAction(system , state , a);
            }
        }
        
        // Tab to the request URL field.
        kb.press(KBKeys.VK_TAB);
        kb.press(KBKeys.VK_TAB);
        
        // Set filter query param to retrieve activity for the OU's Youtube-channel.
         new CompoundAction.Builder()  
        .add(new Type("channelId"),0.5)
        .add(new KeyDown(KBKeys.VK_TAB),0.5).build()
        .run(system, null, 1);
    
        Util.pause(1);
    
        new CompoundAction.Builder()
        .add(new Type("UCjkqufrGMjBAntXE-EvyG1w"),0.5)   
        .add(new KeyDown(KBKeys.VK_ENTER),0.5).build()
        .run(system, null, 1);
        
        // Postman-shortcut to send the request.
        kb.press(KBKeys.VK_CONTROL);
        kb.press(KBKeys.VK_ENTER);
        kb.release(KBKeys.VK_ENTER);
        kb.release(KBKeys.VK_CONTROL);
        
        Util.pause(10); 
	}

    private boolean isInMenuBar(Widget w, State state){
        Shape shape = w.get(Tags.Shape, null);
        
        Widget menuItemAtSameYPosition = null;
        
        for(Widget w2 : state){
         if(w2.get(Tags.Title ,"").contains("New")) {
                menuItemAtSameYPosition = w2;
            }
        }
        
        Shape menuItemAtSameYPositionShape = menuItemAtSameYPosition.get(Tags.Shape, null);

        return shape != null ? shape.y() >= menuItemAtSameYPositionShape.y() - 15 && shape.y() <= menuItemAtSameYPositionShape.y() + 15 : false; 
    }

	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 *
	 * super.getState(system) puts the state information also to the HTML sequence report
	 *
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
    
        Set<Action> actions = super.deriveActions(system,state);
    
		actions = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, system, state);

		if(actions.isEmpty()){
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, system, state);
		}
    
        return actions;
    }

	/**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
	 * super.selectAction(state, actions) updates information to the HTML sequence report
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		return(super.selectAction(state, actions));
	}

	/**
	 * Execute the selected action.
	 *
	 * super.executeAction(system, state, action) is updating the HTML sequence report with selected action
	 *
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
	 * @return  if <code>true</code> continue test, else stop
	 */
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
		super.stopSystem(system);
	}
}

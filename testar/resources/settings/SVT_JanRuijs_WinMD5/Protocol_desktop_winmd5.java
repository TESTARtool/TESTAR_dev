/*****************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018, 2019 Universitat
 * Politecnica de Valencia - www.upv.es
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
 ****************************************************************************/


/**
 * OU, IM0903 SV&T
 * Assignment 4
 * June 2020
 * J. Ruys, 839006544
 * 
 * Protocol_desktop_winmd5.java
 * - adopted from Protocol_desktop_generic.java
 * 
 * Tested with TESTAR v2.2.7 (Win10)
 * 
 * Requirements:
 * - installation of WinMD5 in c:\winmd5free (in which file: WinMD5.exe)
 *   (downloadable from www.winmd5free.com)
 * 
 * Notes to this protocol:
 * 
 * - TESTAR seems to have a problem detecting the verify-textbox as an input
 *   action. Ignoring this textbox would not make sense in testing the
 *   application, so we tried to manually add this textbox as an action, by
 *   searching for its ConcreteID.
 *   Further tweaking may be needed (but time's up, for now).
 * 
 * - The SUT (winmd5) uses an OS-call to an open dialog for selecting a file,
 *   which TESTAR does not seem to detect properly. The test is not about
 *   OS open dialog windows, so this should not be a problem. However, on
 *   occasion we do want to have a file selected, for further testing. For
 *   this purpose we select our own SUT exe-file for input, since we know
 *   it's there. This auto filling has some randomness (auto fill or skip
 *   auto fill).
 */




import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.monkey.Settings;
import org.testar.protocols.DesktopProtocol;


import nl.ou.testar.PrioritizeNewActionsSelector;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
import org.fruit.alayer.State;
import org.fruit.alayer.devices.AWTKeyboard;

import java.lang.StringBuilder;
import java.util.Random;
import org.fruit.alayer.devices.*;
import java.util.HashSet;


/**
 * This protocol provides default TESTAR behaviour to test Windows desktop
 * applications.
 *
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_winmd5 extends DesktopProtocol {

    private static final boolean DEBUG = true;
    
    // track number of times we forced a filename upon sequence start:
    private int filenameFilledInCount; 
    
    // max number of times we should auto fill in a filename:
    private static final int AUTO_FILL_IN_MAX = 1; 

        
    private static final Random RANDOMIZER = new Random();

    private static PrioritizeNewActionsSelector selector = new PrioritizeNewActionsSelector();


    /**
     * Helper method for outputting debug stuff.
     */
    private static void debugMsg(String msg) {
        if (DEBUG) {
            System.out.println(" *DEBUG*: " + msg);
        }
    }


    /**
     * Helper method for converting all <code>tags</code> into a single
     * String. For debugging purposes.
     */
    private static String tagsToString(Iterable<Tag<?>> tags) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Tag tag : tags) {
            if (0 < stringBuilder.length()) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(tag.name());
            stringBuilder.append("=");
            stringBuilder.append(tag.type().toString());
        }
        return stringBuilder.toString();
    }



	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
     * 
	 * @param settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);

        filenameFilledInCount = 0;
	}


	/**
	 * This methods is called before each test sequence, before startSystem(),
	 * allowing for example using external profiling software on the SUT
	 *
	 * HTML sequence report will be initialized in the
     * super.preSequencePreparations() for each sequence
	 */
	@Override
	protected void preSequencePreparations() {
		super.preSequencePreparations();
	}

	/**
	 * This method is called when TESTAR starts the System Under Test (SUT).
     * The method should take care of
	 *   1) starting the SUT (you can use TESTAR's settings obtainable from
     *      <code>settings()</code> to find
	 *      out what executable to run)
	 *   2) waiting until the system is fully loaded and ready to be tested
     *      (with large systems, you might have to wait several seconds until
     *      they have finished loading)
     * 
	 * @return  a started SUT, ready to be tested.
	 */
	@Override
	protected SUT startSystem() throws SystemStartException {
		return super.startSystem();
	}






    private boolean selectSpecificFilename(SUT system, State state, Widget widget, String filename) {
        StdActionCompiler actionCompiler = new StdActionCompiler();
        Action action = actionCompiler.leftClickAt(widget);
        boolean succeeded = executeAction(system, state, action);
        debugMsg("open filename dialog action succeeded: " + succeeded);
        
        if (succeeded) {
            Util.pause(1);
             
            Action fillFilenameAction = actionCompiler.clickTypeInto(widget, filename, false);   
            succeeded = executeAction(system, state, fillFilenameAction);
            
            debugMsg("file name filled in action succeeded: " + succeeded);
            
            // We can't find the Open button (because of the system dialog?),
            // just hitting enter here works just fine:
            //
            Keyboard keyboard = AWTKeyboard.build();
            keyboard.press(KBKeys.VK_ENTER);           
            
            Util.pause(1);
        }
        return succeeded;
    }


	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate
     * a new sequence. This can be used for example for bypassing a login
     * screen by filling the username and password or bringing the system
     * into a specific start state which is identical on each start (e.g. one
     * has to delete or restore the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state) {
	 	super.beginSequence(system, state);

/*
        debugMsg("beginSequence(): state.tags()="
            + tagsToString(getState(system).tags())
        );
*/

        /*        
        for (Widget widget : state) {
            debugMsg(" widget title: " + widget.get(Tags.Title, "??"));
        }
        */
        
        
        if (filenameFilledInCount < AUTO_FILL_IN_MAX) {
            if (RANDOMIZER.nextBoolean()) {
                // We're randomizing whether we should auto fill upon
                // sequence start. We would rather had this done by using a
                // hook in executeAction(), but we could not determine
                // whether the Browse button was selected to be executed
                // (within our time boxed window for this assignment), so
                // where using this beginSequence() as a cheap hack and
                // randomize ourselves.
                //
                state = getState(system);
                
                for (Widget widget : state) {
                    if (widget.get(Tags.Title, "").contains("Browse")) {
                        String FILENAME = "winmd5.exe";
                        debugMsg("Auto filling in filename: " + FILENAME);
                        selectSpecificFilename(system, state, widget, FILENAME);
                        filenameFilledInCount++;
                    }
                }
            }
            else {
                debugMsg("Skipping Auto filling in filename (due to randomness)");
            }
        }
	}


    /**
     * This method returns some test values that make more sense to this SUT
     * (winMD5), that may either pass or fail its own test. (Yet, either is
     * fine, since we're actually looking for application crashes.)
     */
    private String getWinMD5TestValue() {
        if (RANDOMIZER.nextBoolean()) {
            return "n/a";
        }
        else if (RANDOMIZER.nextBoolean()) {
            return "944a1e869969dd8a4b64ca5e6ebc209a";  // wind5md.exe's own md5 hash :-)
        }
        else {
            return "944a1e869969dd8a4b64ca5e6ebc20a9";  // typo
        }
    }



    @Override
    protected String getRandomText(Widget widget) {
        // Note: parameter widget is ignored in super class, so do we
        if (RANDOMIZER.nextBoolean()) {
            String randomText = getWinMD5TestValue();
            debugMsg("Using specific test value: " + randomText);
            return randomText;
        }
        else {
            return super.getRandomText(widget);
        }
    }


	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write
     * your own state fetching routine. The state should have attached an
     * oracle (TagName: <code>Tags.OracleVerdict</code>) which describes
     * whether the state is erroneous and if so why.
	 *
	 * super.getState(system) puts the state information also to the HTML
     * sequence report
	 *
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		return super.getState(system);
	}

	/**
	 * The getVerdict methods implements the online state oracles that
	 * examine the SUT's current state and returns an oracle verdict.
     * 
	 * @return oracle verdict, which determines whether the state is erroneous
     *  and why.
	 */
	@Override
	protected Verdict getVerdict(State state){
    
		// The super methods implements the implicit online state oracles for:
		//   system crashes
		//   non-responsiveness
		//   suspicious titles
        //
		Verdict verdict = super.getVerdict(state);

		//--------------------------------------------------------
		// MORE SOPHISTICATED STATE ORACLES CAN BE PROGRAMMED HERE
		//--------------------------------------------------------

		return verdict;
	}


    private Widget getWidgetByTag(State state, Tag tag, String value) {
        for (Widget widget : state) {
            if (widget.get(tag, "!" + value).equals(value)) {
                return widget;
            }
        }
        return null;
    }

    private Set<Action> specificActions(SUT system, State state) throws ActionBuildException {
        Set<Action> actions = new HashSet<Action>();
        
        final String VERIFY_TEXTBOX_CONCRETE_ID = "WCjbuzfc114021979935";
        //state = getState(system);
        Widget verifyTextboxWidget = getWidgetByTag(state, Tags.ConcreteID, VERIFY_TEXTBOX_CONCRETE_ID);
        if (null != verifyTextboxWidget) {
            StdActionCompiler actionCompiler = new StdActionCompiler();
            Action action = actionCompiler.clickTypeInto(verifyTextboxWidget, getWinMD5TestValue(), true);
            actions.add(action);
            // debugMsg(" specific action added: " + action);
        }
        
        return actions;
    }

	/**
	 * This method is used by TESTAR to determine the set of currently
     * available actions. You can use the SUT's current state, analyze the
     * widgets and their properties to create a set of sensible actions, such
     * as: "Click every Button which is enabled" etc.
     * 
	 * The return value is supposed to be non-null. If the returned set is
     * empty, TESTAR will stop generation of the current action and continue
     * with the next one.
     * 
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return  a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

		// The super method returns a ONLY actions for killing unwanted
        // processes if needed, or bringing the SUT to the foreground. You
        // should add all other actions here yourself. These "special" actions
        // are prioritized over the normal GUI actions in
        // selectAction() / preSelectAction().
        //
		Set<Action> actions = super.deriveActions(system, state);


		// Derive left-click actions, click and type actions, and scroll
        // actions from top level (highest Z-index) widgets of the GUI:
        //
		//actions = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, system, state);

		if (actions.isEmpty()) {

			// If the top level widgets did not have any executable widgets,
            // try all widgets:
            //
//			System.out.println("No actions from top level widgets, changing to all widgets.");
    
			// Derive left-click actions, click and type actions, and scroll
            // actions from all widgets of the GUI:
            //
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, system, state);
		}

        // added 2020-06-25:
        actions = this.selector.getPrioritizedActions(actions);
        
        actions.addAll(specificActions(system, state));

		// return the set of derived actions:
		return actions;
	}

	/**
	 * Select one of the available actions using an action selection algorithm
     * (for example random action selection)
	 *
	 * super.selectAction(state, actions) updates information to the HTML
     * sequence report
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions) {
		//return(super.selectAction(state, actions));

        // added 2020-06-25:
        Action action = super.selectAction(state, actions);
        //selector.addExecutedAction(action);
        return action;
	}




	/**
	 * Execute the selected action.
	 *
	 * super.executeAction(system, state, action) is updating the HTML
     * sequence report with selected action
	 *
	 * @param system  the SUT
	 * @param state  the SUT's current state
	 * @param action  the action to execute
	 * @return whether or not the execution succeeded
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action) {
    /*
        debugMsg("executeAction(1:" + system
            + ",\n 1a:" + tagsToString(system.tags())
            + ",\n 2:" + state.toString()
        //    + ",\n 2a:" + tagsToString(state.tags())
            + ",\n 3:" + action
        //    + ",\n 3a:" + tagsToString(action.tags())
            + ")");
    */
    
		boolean succeeded = super.executeAction(system, state, action);
        return succeeded;
	}


	/**
	 * TESTAR uses this method to determine when to stop the generation of
     * actions for the current sequence. You can stop deriving more actions
     * after:
	 * - a specified amount of executed actions, which is specified through
     *   the SequenceLength setting, or
	 * - after a specific time, that is set in the MaxTime setting
     * 
	 * @return  if <code>true</code> continue generation, else stop
	 */
	@Override
	protected boolean moreActions(State state) {
		return super.moreActions(state);
	}


	/**
	 * TESTAR uses this method to determine when to stop the entire test
     * sequence. You could stop the test after:
	 * - a specified amount of sequences, which is specified through the
     *   Sequences setting, or
	 * - after a specific time, that is set in the MaxTime setting
     * 
	 * @return  if <code>true</code> continue test, else stop
	 */
	@Override
	protected boolean moreSequences() {
		return super.moreSequences();
	}

	/**
	 * Here you can put graceful shutdown sequence for your SUT
     * 
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		super.stopSystem(system);
	}

	/**
	 * This methods is called after each test sequence, allowing for example
     * using external profiling software on the SUT
	 *
	 * super.postSequenceProcessing() is adding test verdict into the HTML
     * sequence report
	 */
	@Override
	protected void postSequenceProcessing() {
		super.postSequenceProcessing();
	}
}

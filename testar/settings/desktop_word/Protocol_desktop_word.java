/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2017):                                                                       *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        * *                                                                                            * 
 *                                                                                            *
 **********************************************************************************************/

/**
 * A general Word protocol
 * @author: Urko Rueda Molina
 */
import static org.fruit.alayer.windows.UIARoles.*;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.fruit.Assert;
import org.fruit.Drag;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Point;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import org.fruit.alayer.State;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.StrokePattern;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.KBKeys;
import static org.fruit.alayer.windows.UIATags.*;
import static org.fruit.monkey.ConfigTags.*;
import org.fruit.monkey.DefaultProtocol;
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

import es.upv.staq.testar.protocols.ClickFilterLayerProtocol; 

import es.upv.staq.testar.NativeLinker;
import org.fruit.monkey.ConfigTags;

public class Protocol_desktop_word extends ClickFilterLayerProtocol {

	static double scrollArrowSize = 36; // sliding arrows
	static double scrollThick = 16; //scroll thickness
	
	/** 
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings   the current TESTAR settings as specified by the user.
	 */
	protected void initialize(Settings settings){
		super.initialize(settings);
	}
	
	/**
	 * This method is invoked each time TESTAR starts to generate a new sequence
	 */
	protected void beginSequence(){
		super.beginSequence();
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
		return super.startSystem();
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
		return super.getState(system);
	}

	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
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
							actions.add(ac.clickTypeInto(w, this.getRandomText(w)));

						// slides
						Drag[] drags = null;
						if((drags = w.scrollDrags(scrollArrowSize,scrollThick)) != null){
							for (Drag drag : drags){
								actions.add(ac.dragFromTo(
									new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
									new AbsolutePosition(Point.from(drag.getToX(),drag.getToY()))
								));
							}
						}
					
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
	 * This method is invoked each time after TESTAR finished the generation of a sequence.
	 */
	protected void finishSequence(File recordedSequence){
		super.finishSequence(recordedSequence);
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
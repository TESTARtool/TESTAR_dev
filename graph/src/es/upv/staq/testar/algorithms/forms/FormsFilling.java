/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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


package es.upv.staq.testar.algorithms.forms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Finder;
import org.fruit.alayer.Point;
import org.fruit.alayer.Position;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.ActionRoles;

/**
 * Forms filling algorithm utility.
 * 
 * Status: experimental
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class FormsFilling {

	private static final int WIDGET_TYPING_COUNTDOWN_THRESHOULD =
		100; // how many executed actions to wait for before retyping takes place?
	// typing actions prioritizing
	private static HashMap<String,Integer> widgetsTypingHistory = new HashMap< 
		String, // widget-id
		Integer // 0 => widget to be typed; >0 => widget typed with count-down on the #executed_ations for retyping
		>();  	
	// slides actions prioritizing (to reveal extra text-input fields in forms)
	private static HashMap<String,Integer> slidingHistory = new HashMap<
		String, // slide from->to 
		Integer // 0 => slide-to missing; >0 => slided with count-down on the #executed_ations for re-sliding{
		>();

    // -----------------------
    // ACTIONS CATEGORIZATION
    // -----------------------

	// get the target widgets of typing actions    
    public static HashMap<Widget,Action> getTypeableWidgets(State state, Set<Action> actions){
    	HashMap<Widget,Action> widgets = new HashMap<Widget,Action>();
    	List<Finder> targets;
		Set<Action> typingActions = getTypingActions(actions); 
		for (Action a : typingActions){
			targets = a.get(Tags.Targets,null);
			if (targets != null){
				widgets.put(targets.get(0).apply(state),a); // typing actions has exactly one target
			}
		}
		return widgets;
    }

    // filter actions that do type
    public static Set<Action> getTypingActions(Set<Action> actions){
    	Set<Action> typingActions = new HashSet<Action>();
    	Role r;
    	for (Action a : actions){
    		r = a.get(Tags.Role, null);
    		if (r != null && r.name().equals(ActionRoles.ClickTypeInto.name()))
    			typingActions.add(a);
    	}
    	return typingActions;
    }
    
    public static Set<Action> getSlides(Set<Action> actions){
    	Set<Action> slideActions = new HashSet<Action>();
    	Position[] slider;
    	for (Action a : actions){
    		slider = a.get(Tags.Slider, null);
    		if (slider != null)
    			slideActions.add(a);
    	}
    	if (slideActions.isEmpty())
    		return null;
    	else
    		return slideActions;
    }    

    // ----------
    // AUXILIARY
    // ----------
    
    private static String getSlideID(State state, Position[] slide){
		return (slide[0].apply(state)).toString() + // from
			   (slide[1].apply(state)).toString(); // to
    }
    
	// ------------------------------------
	// TEXT-INPUT FIELDS FILLING FOR FORMS
	// ------------------------------------

    // prioritize typing actions for text inputs dependent behaviors
    public static Set<Action> filterFormActions(State state, Set<Action> actions){
    	Set<Action> returnActions = new HashSet<Action>();
    	String widgetID;
    	Integer status;
    	HashMap<Widget,Action> typeableWidgets = getTypeableWidgets(state,actions); 
    	for (Widget w : typeableWidgets.keySet()){
    		widgetID = w.get(Tags.ConcreteID);
    		status = widgetsTypingHistory.get(widgetID);
    		if (status == null){ // new typeable widget?
    			status = new Integer(0);
    			widgetsTypingHistory.put(widgetID,status);
    		}
			if (status.intValue() <= 0) // widget to be typed
				returnActions.add(typeableWidgets.get(w));
    	}
    	if (returnActions.isEmpty()){
    		String slideS;
    		Point from, to;
    		Position[] slide;
    		Set<Action> slides = getSlides(actions); 
    		if (slides != null){
	    		for (Action s : slides){
	    			slide = s.get(Tags.Slider);
	    			slideS = getSlideID(state,slide);
	    			status = slidingHistory.get(slideS);
	    			if (status == null){ // new slide?
	    				status = new Integer(0);
	    				slidingHistory.put(slideS,status);
	    			}
	    			if (status.intValue() <= 0) // slide to performed
	    				returnActions.add(s);
	    		}
    		}
    		if (returnActions.isEmpty())
    			return actions;
    	}
    	return returnActions;
    }    

    // update typing actions management
    public static void updateFormActions(State state, Action selectedAction){
		Set<Action> as = new HashSet<Action>();
		as.add(selectedAction);
		HashMap<Widget,Action> tw = getTypeableWidgets(state,as);
		if (tw.size() == 1){ // widget typed?
			widgetsTypingHistory.put(tw.keySet().iterator().next().get(Tags.ConcreteID),
									new Integer(WIDGET_TYPING_COUNTDOWN_THRESHOULD + 1));
		}
		Set<Action> slides = getSlides(as);
		if (slides != null) // slide performed=
			slidingHistory.put(getSlideID(state,slides.iterator().next().get(Tags.Slider)),
							   new Integer(WIDGET_TYPING_COUNTDOWN_THRESHOULD + 1));
		Integer cd;
		for (String w : widgetsTypingHistory.keySet()){
			cd = widgetsTypingHistory.get(w);
			widgetsTypingHistory.put(w,new Integer(cd--));
		}
		for (String s : slidingHistory.keySet()){
			cd = slidingHistory.get(s);
			slidingHistory.put(s,new Integer(cd--));
		}
    }    
    
}

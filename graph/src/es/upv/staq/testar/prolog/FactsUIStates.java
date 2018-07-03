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


package es.upv.staq.testar.prolog;

import java.util.HashSet;
import java.util.Set;

import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

/**
 * Facts for TESTAR states (widget-trees).
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class FactsUIStates {
  
	public static final String FACT_STATE = "state"; 		// state(S)			:  S = SUT state/widget-tree 
	public static final String FACT_NOWSTATE = "nowstate";	// nowstate(Sn)		: Sn = Current SUT state
	public static final String FACT_WIDGET = "widget"; 		// widget(W,S)		:  W = widget of state S
	public static final String FACT_PARENT = "parent"; 		// parent(Wp,Wc)	: Wp = parent widget of Wc (child)
	public static final String FACT_ANCESTOR = "ancestor"; 	// ancestor(Wa,Wc)	: Wa = ancestor of widget Wc (child)
	public static final String FACT_ROLE = "role"; 			// role(W,R)		:  R = role of widget W
	public static final String FACT_TITLE = "title"; 		// title(W,T)		:  T = title of widget W
	public static final String FACT_PATH = "path"; 			// path(W,P)		:  P = path of widget W in the widget-tree
	
	public static Set<String> getRules() {
		Set<String> stateRules = new HashSet<String>();
		stateRules.add(FACT_ANCESTOR + "(Wa,Wc):-" + FACT_PARENT + "(Wa,Wc)."); // ancestor(Wa,Wc)
		stateRules.add(FACT_ANCESTOR + "(Wa,Wc):-" + FACT_PARENT + "(Wp,Wc)," + FACT_ANCESTOR + "(Wa,Wp).");
		return stateRules;
	}
	
    public static Set<String> getFacts(State state) {
    	Set<String> facts = new HashSet<String>();

    	// atoms
    	String S = state.get(Tags.ConcreteID);
		facts.add(FACT_STATE + "('" + S + "')."); // state(S)
		facts.add(FACT_NOWSTATE + "('" + S + "')."); // nowstate(Sn)
    	facts.addAll(getAtoms(S,S,state));
    	    	
    	return facts;
    }
        
    private static Set<String> getAtoms(String S, String P, Widget parenWidget) {
    	Set<String> facts = new HashSet<String>();
    	
		String W; 
		Widget childWidget;
		for (int i = 0; i < parenWidget.childCount(); i++) {
			childWidget = parenWidget.child(i);
			W = childWidget.get(Tags.ConcreteID);
			facts.add(FACT_WIDGET + "('" + W + "','" + S + "')."); // widget(W,S)
			facts.addAll(getPropertiesAtoms(W,childWidget));
			facts.add(FACT_PARENT + "('" + P + "','" + W + "')."); // parent(Wp,Wc)
			facts.addAll(getAtoms(S, W, childWidget));
		}

		return facts;
    }

    private static Set<String> getPropertiesAtoms(String widgetID, Widget w) {
    	Set<String> facts = new HashSet<String>();
    	
		// widget properties
		Role role = w.get(Tags.Role, null);
		if (role != null) {
			// sb.append("role(" + widgetID + ",\"" + role.name() + "\").\n"); // role(W,"Wr") - capitalized atoms need double quotes (normaliseQuotedAtoms(...) required)
			facts.add(FACT_ROLE + "('" + widgetID + "','" + role.name() + "')."); // role(W,'R') - capitalized atoms need single quotes
		}
		facts.add(FACT_TITLE + "('" + widgetID + "','" + w.get(Tags.Title,"") + "')."); // title(W,'T')
		facts.add(FACT_PATH + "('" + widgetID + "','" + w.get(Tags.Path) + "')."); // path(W,'P')
		
		return facts;
    }
    
	public static void debugQueries(JIPrologWrapper wrapper) {
		wrapper.debugQuery(FACT_STATE + "(S).");
		wrapper.debugQuery(FACT_WIDGET + "(W,S).");
		wrapper.debugQuery(FACT_PARENT + "(Wp,Wc).");		
		wrapper.debugQuery(FACT_ANCESTOR + "(Wa,Wc).");    
		wrapper.debugQuery(FACT_ROLE + "(W,R).");
		wrapper.debugQuery(FACT_TITLE + "(W,T).");
		wrapper.debugQuery(FACT_PATH + "(W,P).");
	}
	
}

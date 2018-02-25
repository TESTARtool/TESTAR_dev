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


package es.upv.staq.testar.oracles;

import java.util.ArrayList;
import java.util.List;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import es.upv.staq.testar.oracles.predicates.IPredicate;
import es.upv.staq.testar.oracles.predicates.TitleUpdatePredicate;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * An oracle context implementation.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */

public class OracleContext {

	private JIPrologWrapper jipWrapper;
		
	public OracleContext(State state){
		jipWrapper = new JIPrologWrapper();
		if (state != null)
			notifyState(state);
	}
	
	public void notifyState(State state){
		jipWrapper.addFacts(state);
	}
	
	public void notifyAction(State state, Action action){
		jipWrapper.addFacts(state, action);
	}
        
	public void setVerificationPoint(State state, Widget w){
		jipWrapper.addFactsNrules("verify('" + state.get(Tags.ConcreteID) + "','" + w.get(Tags.ConcreteID) + "')."); // verify(S,W).
	}
	
	/**
	 * Infer oracles for executed actions and target verification points.
	 */
	public List<Oracle> infer(){
		IPredicate predicate;
		List<Oracle> oracles = new ArrayList<Oracle>();
		// we try to identify dependencies between the executed actions and their impact on the verification point
		predicate = TitleUpdatePredicate.infer(jipWrapper);
		if (predicate != null)
			oracles.add(new Oracle(predicate));
		jipWrapper = null;
		return oracles;
	}
	
}

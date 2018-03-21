/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2016):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

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
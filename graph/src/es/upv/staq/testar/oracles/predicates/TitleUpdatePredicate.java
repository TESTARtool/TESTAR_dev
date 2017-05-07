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

package es.upv.staq.testar.oracles.predicates;

import java.util.List;

import es.upv.staq.testar.oracles.UIOperations;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.prolog.PrologUtil;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.LogSerialiser.LogLevel;

/**
 * A widget TITLE property UPDATE predicate.
 * 
 * status (alpha dev.):
 *   - target-app: windows calculator
 *   - procedure: hit a number button =&gt; set calculator display as verification point
 *   - predicate: display must/should change (previous text + last hit number button)
 *   - sandbox: a calculator that randomly shows '5' text or nothing in the display when hitting the '5' number button
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class TitleUpdatePredicate extends PredicateBase {
				
	private TitleUpdatePredicate(String refuteQuery){
		this.refuteQuery = refuteQuery;
		this.predicateContext = new Object[]{ UIOperations.W_PROPERTIES.TITLE, UIOperations.OP_TYPES.UPDATE };
	}
	
	public static IPredicate infer(JIPrologWrapper jipWrapper){
		// can we infer a predicate?
		List<List<String>> solutions = jipWrapper.setQuery(
			"verify(Sv,Wv),title(Wv,Tv),role(Wv,Rv),path(Wv,Pv)," + // info about the verification point
			"action(A,Sa,Wa,T,O),title(Wa,Ta),\\=(Sv,Sa)," + // info about an action that might affect the verification point
			"widget(Wva,Sa),title(Wva,Tva),role(Wva,Rv),path(Wva,Pv)," + // info about the verification point in the action state
			"\\=(Tv,Tva)." // the action produced a change to the verification point? (hence, a state change)
		);
		if (solutions == null || solutions.isEmpty())
			return null;
		else{
			String Wa = PrologUtil.getSolutions("Wa", solutions).get(0);
			// predicate exists => build a refuting predicate to use as verdict
			String refuteQuery = "action(A,Sa,Wa,T,O),Wa='" + Wa + "',source(Ss,A),target(St,A),==(Ss,St)."; // the predicate action did not change state?
			LogSerialiser.log("Title-Update oracle inferred:\n" + refuteQuery + "\n",LogLevel.Info);
			return new TitleUpdatePredicate(refuteQuery);
		}
	}
	
}
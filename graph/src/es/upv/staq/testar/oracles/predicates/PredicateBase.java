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

import org.fruit.Assert;
import org.fruit.alayer.Verdict;

import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.prolog.PrologUtil;

/**
 * Predicates base implementation.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public abstract class PredicateBase implements IPredicate {
			
	protected String refuteQuery = null;
	protected Object[] predicateContext = null;

	@Override
	public Verdict getVerdict(JIPrologWrapper jipWrapper){
		Assert.notNull(this.refuteQuery); Assert.notNull(this.predicateContext);
		Grapher.syncMovements(); // synchronize graph movements consumption			
		jipWrapper.updatePrologFactsNrules();
		// check predicate
		List<List<String>> solutions = jipWrapper.setQuery(this.refuteQuery);
		if (solutions != null && !solutions.isEmpty()){
			String refuteMsg = ""; for (Object o : this.predicateContext) refuteMsg += o.toString() + " ";
			refuteMsg += "predicate refuted!";
			System.out.println(refuteMsg); PrologUtil.printSolutions(solutions);
			return new Verdict(Verdict.SEVERITY_MAX, refuteMsg);
		}
		return Verdict.OK;  // cannot refute predicate
	}
	
}
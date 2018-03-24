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

import org.fruit.alayer.Verdict;

import es.upv.staq.testar.oracles.predicates.IPredicate;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * An oracle implementation.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */

public class Oracle {

	private IPredicate predicate = null;
	
	public Oracle(IPredicate predicate){
		this.predicate = predicate;
	}
	
	public Verdict getVerdict(JIPrologWrapper jipWrapper){
		if (predicate != null)
			return predicate.getVerdict(jipWrapper);
		else
			return Verdict.OK;
	}
	
}
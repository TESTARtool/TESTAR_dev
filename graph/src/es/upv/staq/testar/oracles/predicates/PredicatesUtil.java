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

import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Widget;

import es.upv.staq.testar.CodingManager;

/**
 * Utility methods for predicates.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class PredicatesUtil {

	public static Object getVerificationPointProperty(State state, String verificationPointID, Tag<?> property){
		Widget vp = CodingManager.find(state, verificationPointID, CodingManager.ABSTRACT_R_ID);
		if (vp != null)
			return vp.get(property, null);
		else
			return null;
	}
	
	/**
	 * @param prev
	 * @param next
	 * @return 'null' if equal.
	 */
	public static String diff(String prev, String next){
		if (prev.equals(next))
			return null;
		else{
			int idx = next.indexOf(prev,0);
			if (idx != -1)
				return next.substring(0,idx) + next.substring(idx + prev.length(), next.length());
			else{
				idx = prev.indexOf(next,0);
				if (idx != -1)
					return prev.substring(0,idx) + prev.substring(idx + next.length(), prev.length());
				else
					return prev + "_DIFFER_" + next;
			}
		}
	}	
	
}
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

package es.upv.staq.testar.graph;

import java.util.Set;

/**
 * Graph utility class.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class GraphUtil {

	public static int getActionClusterCount(IEnvironment env, IGraphAction graphAction){
		int cc = 1;
		String absID = graphAction.getAbstractID();
		Set<String> acluster = env.getGraphActionClusters().get(absID);
		if (acluster != null)
			return acluster.size();
		return cc;
	}	
	
}

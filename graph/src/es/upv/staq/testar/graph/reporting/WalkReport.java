/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This graph project is distributed FREE of charge under the TESTAR license, as an open *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.graph.reporting;

public class WalkReport {
	final String algoName;
	final int discoveredActions, executedActions, visitedStates, executionCount, pathCount;
	final double duration;
	
	public WalkReport(String algoName, int discoveredActions, int executedActions, int visitedStates, double duration, int executionCount, int pathCount){
		this.algoName = algoName;
		this.discoveredActions = discoveredActions;
		this.executedActions = executedActions;
		this.visitedStates = visitedStates;
		this.duration = duration;
		this.executionCount = executionCount;
		this.pathCount = pathCount;
	}
	
	public String toString(){
		return String.format("%1$s\ntime: %2$02e seconds\ndiscovered actions: %3$04d\nexecuted actions: %4$04d\nexecution count: %5$04d\nvisited states: %6$04d\npath count: %7$04d\n",
				algoName, duration, discoveredActions, executedActions, executionCount, visitedStates, pathCount);
	}
}

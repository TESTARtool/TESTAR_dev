/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR development team (www.testar.org):                       *
 * This graph project is distributed FREE of charge under the TESTAR license, as an open *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *
 *                                                                                       * 
 *****************************************************************************************/

package es.upv.staq.testar;

import org.fruit.alayer.Action;

/**
 * UI action status information.
 *
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class ActionStatus {

	private Action action;
	
	private boolean actionSucceeded,
					problems,
					userEventAction;
	
	/**
	 * Constructor.
	 */
	public ActionStatus(){
		this.action = null;
		this.actionSucceeded = true;
		this.problems = false;
		this.userEventAction = false;		
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public boolean isActionSucceeded() {
		return actionSucceeded;
	}

	public boolean setActionSucceeded(boolean actionSucceeded) {
		this.actionSucceeded = actionSucceeded;
		return actionSucceeded;
	}

	public boolean isProblems() {
		return problems;
	}

	public void setProblems(boolean problems) {
		this.problems = problems;
	}

	public boolean isUserEventAction() {
		return userEventAction;
	}

	public void setUserEventAction(boolean userEventAction) {
		this.userEventAction = userEventAction;
	}
					
}

/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer;

import java.io.Serializable;

import org.fruit.Assert;
import org.fruit.Util;

/**
 * A Verdict is the outcome of a test oracle. It determines whether an <code>SUT</code>'s state is erroneous and if so
 * provides a short explanation and a visualization of what is wrong. 
 */
public final class Verdict implements Serializable {
	private static final long serialVersionUID = 3517681535425699094L;

	//public static final Verdict OK = new Verdict(0.0, "No problem detected.", Util.NullVisualizer);

	// begin by urueda
	
	public static final double SEVERITY_MIN = 0.0;
	public static final double SEVERITY_MAX = 1.0;
	
	public static final double SEVERITY_OK = 			   SEVERITY_MIN;
	public static final double SEVERITY_FAIL =	   		   SEVERITY_MAX;
	
	public static final Verdict OK = new Verdict(SEVERITY_OK, "No problem detected.", Util.NullVisualizer);
	public static final Verdict FAIL = new Verdict(SEVERITY_FAIL, "SUT failed.", Util.NullVisualizer);
	
	// end by urueda
		
	private final String info;
	private final double severity;
	private final Visualizer visualizer;
	
	public Verdict(double severity, String info){
		this(severity, info, Util.NullVisualizer);
	}
	
	public Verdict(double severity, String info, Visualizer visualizer){
		//Assert.isTrue(severity >= 0 && severity <= 1.0);
		Assert.isTrue(severity >= SEVERITY_MIN && severity <= SEVERITY_MAX); // by urueda
		Assert.notNull(info, visualizer);
		this.severity = severity;
		this.info = info;
		this.visualizer = visualizer;
	}

	/**
	 * returns the likelihood of the state to be erroneous (value within interval [0, 1])
	 * @return value within [0, 1]
	 */
	public double severity(){ return severity; }
	
	/**
	 * returns a short description about whether the state is erroneous and if so, what part of it
	 * @return
	 */
	public String info(){ return info; }
		
	/**
	 * This visualizer should visualize the part of the state where the problem occurred.
	 * For example: If there is a suspicious control element, like f.e. a critical message box
	 * than this should be framed or pointed to with a big red arrow. 
	 * @return the visualizer which is guaranteed to be non-null
	 */
	public Visualizer visualizer(){ return visualizer; }
	
	public String toString(){ return "severity: " + severity + " info: " + info; }
	
	/**
	 * Retrieves the verdict result of joining two verdicts.
	 * @param verdict A verdict to join with current verdict.
	 * @return A new verdict that is the result of joining the current verdict with the provided verdict.
	 * @author: urueda
	 */
	public Verdict join(Verdict verdict){		
		return new Verdict(Math.max(this.severity, verdict.severity()),
						   (this.info.contains(verdict.info) ? this.info :
						    (this.severity == SEVERITY_OK ? "" : this.info + "\n") + verdict.info())												
		);		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Verdict) {
			Verdict other = (Verdict)o;
			return this.severity == other.severity
					&& this.info.equals(other.info)
					&& this.visualizer.equals(other.visualizer);
		}
		return false;
	}

}
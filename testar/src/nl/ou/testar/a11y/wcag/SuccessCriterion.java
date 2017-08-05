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
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This sample is distributed FREE of charge under the TESTAR license, as an open        *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package nl.ou.testar.a11y.wcag;

import org.fruit.alayer.Verdict;

/**
 * A WCAG success criterion
 * @author Davy Kager
 *
 */
public class SuccessCriterion extends ItemBase {
	
	protected final AbstractGuideline parent;
	protected final Level level;
	
	private static final int NLEVELS = Level.values().length;
	
	SuccessCriterion(int nr, String name, AbstractGuideline parent, Level level) {
		super(nr, name);
		this.parent = parent;
		this.level = level;
	}
	
	@Override
	public String getNr() {
		return parent.getNr() + "." + nr;
	}

	public Level getLevel() {
		return level;
	}
	
	public double getVerdictPriority() {
		final double STEP = (Verdict.SEVERITY_MAX - Verdict.SEVERITY_MIN) / NLEVELS;
		return Verdict.SEVERITY_MAX - (level.ordinal() * STEP);
	}
	
	@Override
	public String toString() {
		return getNr() + " " + getName() + " (Level " + getLevel() + ")";
	}
	
}

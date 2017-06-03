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

package org.fruit.a11y.wcag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A WCAG principle
 * @author Davy Kager
 *
 */
public class Principle extends ItemBase {
	
	private final List<AbstractGuideline> guidelines = new ArrayList<AbstractGuideline>();

	Principle(int nr, String name) {
		super(nr, name);
	}

	public List<AbstractGuideline> getGuidelines() {
		return Collections.unmodifiableList(guidelines);
	}
	
	void addGuideline(AbstractGuideline guideline) {
		guidelines.add(guideline);
	}
		
}

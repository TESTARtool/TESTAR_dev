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

import org.fruit.Assert;

/**
 * Base class for a WCAG item (principle, guideline, success criterion)
 * @author Davy Kager
 *
 */
abstract class ItemBase {
	
	protected final int nr;
	protected final String name;
	
	protected ItemBase(int nr, String name) {
		Assert.hasText(name);
		this.nr = nr;
		this.name = name;
	}

	public String getNr() {
		return Integer.toString(nr);
	}

	public String getName() {
		return name;
	}
	
}

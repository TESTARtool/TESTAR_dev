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

package nl.ou.testar.a11y.wcag2;

import org.fruit.Assert;

/**
 * Base class for a WCAG item (principle, guideline, success criterion)
 * @author Davy Kager
 *
 */
abstract class ItemBase {
	
	/**
	 * This item's numbe
	 */
	protected final int nr;
	
	/**
	 * This item's name.
	 */
	protected final String name;
	
	/**
	 * This item's parent
	 * This can be null if the item has no parent.
	 */
	protected final ItemBase parent;
	
	/**
	 * Constructs a new item
	 * @param nr Item number
	 * @param name Item name
	 */
	protected ItemBase(int nr, String name) {
		this(nr, name, null);
	}
	
	/**
	 * Constructs a new item
	 * @param nr Item number
	 * @param name Item name
	 * @param parent The parent, may be null
	 */
	protected ItemBase(int nr, String name, ItemBase parent) {
		Assert.hasText(name);
		this.nr = nr;
		this.name = name;
		this.parent = parent;
	}

	/**
	 * Gets the number
	 * If the item is not at the top of the hierarchy, this will also include the parent's number.
	 * For example: 1.2.3
	 * @return This item's number as a String
	 */
	public String getNr() {
		return parent == null ? Integer.toString(nr) : parent.getNr() + "." + nr;
	}

	/**
	 * Gets the name
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
}

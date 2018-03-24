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
 * This software is distributed FREE of charge under the TESTAR license, as an open      *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package org.fruit;

/**
 * ActionRoles brief map.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class Drag {
	
	private double fromX, fromY, toX, toY;
	
	public Drag(double fromX, double fromY, double toX, double toY){
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}

	public double getFromX() {
		return fromX;
	}

	public void setFromX(double fromX) {
		this.fromX = fromX;
	}

	public double getFromY() {
		return fromY;
	}

	public void setFromY(double fromY) {
		this.fromY = fromY;
	}

	public double getToX() {
		return toX;
	}

	public void setToX(double toX) {
		this.toX = toX;
	}

	public double getToY() {
		return toY;
	}

	public void setToY(double toY) {
		this.toY = toY;
	}

}

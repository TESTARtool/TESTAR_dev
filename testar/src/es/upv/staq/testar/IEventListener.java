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

package es.upv.staq.testar;

import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.MouseButtons;

/**
 * An event listener interface.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public interface IEventListener {

	public abstract void keyDown(KBKeys key);

	public abstract void keyUp(KBKeys key);

	public abstract void mouseDown(MouseButtons btn, double x, double y);

	public abstract void mouseUp(MouseButtons btn, double x, double y);

	public abstract void mouseMoved(double x, double y);
	
}

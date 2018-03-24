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

import org.fruit.alayer.exceptions.WidgetNotFoundException;

/**
 * A Finder's task is to find a particular widget within a widget tree. It starts it's search from a location within the tree
 * (typically the root). Finder's are abstract representations of widgets and implement a particular search strategy, e.g.
 * "find the widget with the title 'Save'" or "the widget which is the 3rd child of another widget of type 'Canvas'".
 * 
 * Finders must be serializable and are often used to implement actions, e.g. "click on the widget with title 'Save'".
 */
public interface Finder extends Serializable {
	
	/**
	 * Apply the search strategy implemented by this finder and start the search from start.
	 * @param start the node from where to start the search
	 * @return a non-null reference to the located widget.
	 * @throws WidgetNotFoundException if no widget has been found
	 */
	Widget apply(Widget start) throws WidgetNotFoundException;
	
	/**
	 * Retrieves cached widget, if caching was activated. 
	 * @return The widget, or 'null'.
	 * @author urueda
	 */
	Widget getCachedWidget();
	
}
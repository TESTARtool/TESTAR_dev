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

import org.fruit.Drag;

/**
 * A Widget is usually a control element of an <code>SUT</code>.
 * Widgets have exactly one parent and can have several children.
 * They are attached to a <code>State</code> and form a Widget Tree.
 * In fact a <code>State</code> is a Widget itself and is the root
 * of the Widget Tree.
 * 
 * @see State
 */
public interface Widget extends Taggable, Serializable {
	State root();
	Widget parent();
	Widget child(int i);
	int childCount();
	void remove();
	void moveTo(Widget p, int idx);
	Widget addChild();
	
	/**
	 * For scrollable widgets, compute drag segments of scrolling options.
	 * @param scrollArrowSize The size of scrolling arrows.
	 * @param scrollThick The scroller thickness.
	 * @return 'null' for non-scrollable widgets or a set of drags, from (x1,y1) to (x2,y2), otherwise.
	 * @author: urueda
	 */
	Drag[] scrollDrags(double scrollArrowSize, double scrollThick);
	
	/**
	 * @param tab tabulator for indentation.
	 * @return Computes a string representation for the widget.
	 * @author urueda
	 */
	public String getRepresentation(String tab);
	
	public abstract String toString(Tag<?>... tags);
	
}
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
package org.fruit.alayer.windows;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.fruit.alayer.Rect;

class UIAElement implements Serializable {
	private static final long serialVersionUID = -2561441199642411403L;
	List<UIAElement> children = Collections.emptyList();
	UIAElement parent;
	UIARootElement root;
	UIAWidget backRef;
	boolean blocked, enabled, ignore, isTopmostWnd, 
		isModal, hasKeyboardFocus, isKeyboardFocusable,
		isWndTopMost, isTopLevelContainer,
		scrollPattern, hScroll, vScroll; // by urueda
	long ctrlId, orientation, hwnd, wndInteractionState, wndVisualState;
	Rect rect;
	String name, helpText, automationId, className, providerDesc, frameworkId;
	double zindex,
		   hScrollViewSize, vScrollViewSize, hScrollPercent, vScrollPercent; // by urueda
	//int[] scrollbarInfo, scrollbarInfoH, scrollbarInfoV; // by urueda

	public UIAElement(){ this(null); }

	public UIAElement(UIAElement parent){
		this.parent = parent;
		if(parent != null)
			root = parent.root;
		enabled = true;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
	}
}
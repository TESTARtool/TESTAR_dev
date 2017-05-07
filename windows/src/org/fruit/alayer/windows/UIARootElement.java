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
import java.util.Map;

import org.fruit.Util;

final class UIARootElement extends UIAElement {
	private static final long serialVersionUID = -2561441199642411403L;
	long pid, timeStamp;
	boolean isRunning, isForeground, hasStandardMouse, hasStandardKeyboard;	
	transient Map<Long, UIAElement> hwndMap;
	ElementMap tlc;

	public UIARootElement(){
		super(null);
		root = this;
		hwndMap = Util.newHashMap();
		tlc = ElementMap.newBuilder().build();
	}

	public UIAElement at(double x, double y){
		throw new UnsupportedOperationException();
	}

	public boolean visibleAt(UIAElement el, double x, double y){
		if(el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y))
			return false;
		UIAElement topLevelContainer = tlc.at(x, y);
		return (topLevelContainer == null || topLevelContainer.zindex <= el.zindex) && !obscuredByChildren(el, x, y);
	}

	// begin by urueda
	
	public boolean visibleAt(UIAElement el, double x, double y, boolean obscuredByChildFeature){
		if(el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y))
			return false;
		UIAElement topLevelContainer = tlc.at(x, y);
		return (topLevelContainer == null || topLevelContainer.zindex <= el.zindex ||
				!obscuredByChildFeature || !obscuredByChildren(el, x, y));
	}
	
	// end by urueda

	boolean obscuredByChildren(UIAElement el, double x, double y){
		for(int i = 0; i < el.children.size(); i++){
			UIAElement child = el.children.get(i);
			if(child.rect != null && child.rect.contains(x, y) && child.zindex >= el.zindex)
				return true;
		}
		return false;
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
	}

	private void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	}
}
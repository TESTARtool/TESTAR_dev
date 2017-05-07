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

import java.util.Arrays;

import org.fruit.Assert;
import org.fruit.UnFunc;
import org.fruit.alayer.exceptions.WidgetNotFoundException;

public final class IndexFinder implements Searcher, Finder {
	private static final long serialVersionUID = 2879822217515069377L;
	final int indices[];
	transient YieldFirst yf;

	public IndexFinder(int indices[]){
		Assert.notNull(indices);
		this.indices = indices;		
	}

	public SearchFlag apply(Widget start, UnFunc<Widget, SearchFlag> visitor) {
		Assert.notNull(start, visitor);
		Widget current = start;
		for(int idx : indices){
			if(idx >= 0 && idx < current.childCount())
				current = current.child(idx);
			else
				return SearchFlag.OK;
		}
		return visitor.apply(current);
	}

	public String toString(){
		return "IndexSearcher: " + Arrays.toString(indices);
	}

	public Widget apply(Widget start) throws WidgetNotFoundException {
		if(yf == null)
			yf = new YieldFirst();
		apply(start, yf);
		return yf.result();
	}

	@Override
	public Widget getCachedWidget() {
		// TODO Auto-generated method stub
		return null;
	}
}
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.fruit.Assert;
import org.fruit.alayer.Rect;

public final class ElementMap implements Serializable {
	private static final long serialVersionUID = 8336577831205889395L;
	final List<UIAElement> elements;

	private static class ElementComp implements Comparator<UIAElement>{
		final static int WORSE = 1, BETTER = -1, EVEN = 0;
		public int compare(UIAElement o1, UIAElement o2) {
			if(o1.zindex < o2.zindex){
				return WORSE;
			}else if (o1.zindex > o2.zindex){
				return BETTER;
			}else{
				if(o1.rect != null){
					if(o2.rect != null){
						double area1 = Rect.area(o1.rect);
						double area2 = Rect.area(o2.rect);
						return area1 < area2 ? BETTER : (area1 > area2 ? WORSE : EVEN);
					}else{
						return BETTER;
					}
				}else{
					return WORSE;
				}
			}
		}
	}

	public static Builder newBuilder(){ return new Builder(); }

	public static final class Builder{
		final List<UIAElement> elements = new ArrayList<UIAElement>();

		public Builder addElement(UIAElement element){
			Assert.notNull(element);
			if(element.rect != null)
				elements.add(element);		
			return this;
		}

		public ElementMap build(){
			elements.sort(new ElementComp());
			return new ElementMap(this);
		}
	}


	private ElementMap(Builder builder){
		this.elements = builder.elements;
	}

	public UIAElement at(double x, double y){
		for(UIAElement element : elements){
			if(element.rect.contains(x, y))
				return element;
		}
		return null;
	}

	public boolean obstructed(UIAElement element, double x, double y){
		for(UIAElement obstacle : elements){
			if(obstacle.zindex <= element.zindex || obstacle == element)
				break;
			if(obstacle.rect.contains(x, y))
				return true;
		}
		return false;
	}
}
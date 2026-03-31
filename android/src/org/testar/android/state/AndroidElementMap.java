/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.state;

import org.testar.core.Assert;
import org.testar.core.alayer.Rect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AndroidElementMap implements Serializable {
	private static final long serialVersionUID = 8092258706210770379L;

	final List<AndroidElement> elements;

	private static class ElementComp implements Comparator<AndroidElement>{
		final static int WORSE = 1, BETTER = -1, EVEN = 0;
		public int compare(AndroidElement o1, AndroidElement o2) {
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
		final List<AndroidElement> elements = new ArrayList<>();

		public Builder addElement(AndroidElement element){
			Assert.notNull(element);
			if(element.rect != null)
				elements.add(element);		
			return this;
		}

		public AndroidElementMap build(){
			elements.sort(new ElementComp());
			return new AndroidElementMap(this);
		}
	}


	private AndroidElementMap(Builder builder){
		this.elements = builder.elements;
	}

	public AndroidElement at(double x, double y){
		for(AndroidElement element : elements){
			if(element.rect.contains(x, y))
				return element;
		}
		return null;
	}

	public boolean obstructed(AndroidElement element, double x, double y){
		for(AndroidElement obstacle : elements){
			if(obstacle.zindex <= element.zindex || obstacle == element)
				break;
			if(obstacle.rect.contains(x, y))
				return true;
		}
		return false;
	}
}

/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.monkey.alayer.android;

import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Rect;

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

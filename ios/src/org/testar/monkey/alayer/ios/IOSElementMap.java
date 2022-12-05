/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.ios;

import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Rect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IOSElementMap {
	final List<IOSElement> elements;

	private static class ElementComp implements Comparator<IOSElement>{
		final static int WORSE = 1, BETTER = -1, EVEN = 0;
		public int compare(IOSElement o1, IOSElement o2) {
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
		final List<IOSElement> elements = new ArrayList<>();

		public Builder addElement(IOSElement element){
			Assert.notNull(element);
			if(element.rect != null)
				elements.add(element);		
			return this;
		}

		public IOSElementMap build(){
			elements.sort(new ElementComp());
			return new IOSElementMap(this);
		}
	}


	private IOSElementMap(Builder builder){
		this.elements = builder.elements;
	}

	public IOSElement at(double x, double y){
		for(IOSElement element : elements){
			if(element.rect.contains(x, y))
				return element;
		}
		return null;
	}

	public boolean obstructed(IOSElement element, double x, double y){
		for(IOSElement obstacle : elements){
			if(obstacle.zindex <= element.zindex || obstacle == element)
				break;
			if(obstacle.rect.contains(x, y))
				return true;
		}
		return false;
	}
}

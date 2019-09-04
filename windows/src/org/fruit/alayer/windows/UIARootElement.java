/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
	transient Map<Long, UIAElement> windowHandleMap;
	ElementMap elementMap;

	public UIARootElement(){
		super(null);
		root = this;
		windowHandleMap = Util.newHashMap();
		elementMap = ElementMap.newBuilder().build();
		isForeground = false; // by urueda
	}

	public UIAElement at(double x, double y){
		throw new UnsupportedOperationException();
	}

	public boolean visibleAt(UIAElement el, double x, double y){		
		if(el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y))
			return false;
		
		UIAElement topLevelContainer = elementMap.at(x, y);
		return (topLevelContainer == null || topLevelContainer.zindex <= el.zindex) && !obscuredByChildren(el, x, y);
	}

	// begin by urueda
	
	public boolean visibleAt(UIAElement el, double x, double y, boolean obscuredByChildFeature){		
		if(el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y))
			return false;
				
		UIAElement topLevelContainer = elementMap.at(x, y);
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

/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.testar.core.util.Util;

public final class UIARootElement extends UIAElement {
	private static final long serialVersionUID = -2561441199642411403L;
	long pid, timeStamp;
	boolean isRunning, isForeground, hasStandardMouse, hasStandardKeyboard;	
	transient Map<Long, UIAElement> windowHandleMap;
	UIAElementMap elementMap;

	public UIARootElement(){
		super(null);
		root = this;
		windowHandleMap = Util.newHashMap();
		elementMap = UIAElementMap.newBuilder().build();
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

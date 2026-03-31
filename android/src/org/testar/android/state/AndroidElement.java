/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.state;

import org.testar.core.alayer.Rect;
import org.testar.core.tag.TaggableBase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AndroidElement extends TaggableBase implements Serializable {
	private static final long serialVersionUID = -2910535746470588590L;
	
	List<AndroidElement> children = new ArrayList<>();
	AndroidElement parent;
	public AndroidRootElement root;
	AndroidWidget backRef;
	
	boolean enabled;
	boolean ignore;
	boolean blocked;
	boolean isTopLevelContainer;

	double zindex;
	
	Rect rect;
	Rect bounds;
	
	int nodeIndex;
	String text;
	String hint;
	String resourceId;
	public String className;
	String packageName;
	String accessibilityID;
	String xPath;
	String abstractActionId;
	String activity;
	
	boolean checkable;
	boolean checked;
	boolean clickable; 
	boolean focusable;
	boolean focused;
	boolean scrollable;
	boolean longclicklable;
	boolean password;
	boolean selected;
	boolean displayed;
	
	public AndroidElement(){ this(null); }

	public AndroidElement(AndroidElement parent){
		this.parent = parent;
		if(parent != null) {
			root = parent.root;
		}
		enabled = true;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
	}
}

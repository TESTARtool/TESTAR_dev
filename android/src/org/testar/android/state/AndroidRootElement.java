/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.state;

public class AndroidRootElement extends AndroidElement {
	private static final long serialVersionUID = 7333122749170300870L;
	
	public long pid;
	public long windowsHandle;
	public long timeStamp;
	public boolean isRunning;
	public boolean isForeground;
	AndroidElementMap elementMap;

	public AndroidRootElement() {
		super(null);
		root = this;
		parent = this;
		isForeground = false;
		blocked = false;
		elementMap = AndroidElementMap.newBuilder().build();
	}

	public boolean visibleAt(AndroidElement el, double x, double y){
		if(el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y)) {
			return false;
		}

		AndroidElement topLevelContainer = elementMap.at(x, y);
		return (topLevelContainer == null || topLevelContainer.zindex <= el.zindex) && !obscuredByChildren(el, x, y);
	}

	public boolean visibleAt(AndroidElement el, double x, double y, boolean obscuredByChildFeature){
		if(el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y)) {
			return false;
		}

		AndroidElement topLevelContainer = elementMap.at(x, y);
		return (topLevelContainer == null || topLevelContainer.zindex <= el.zindex ||
				!obscuredByChildFeature || !obscuredByChildren(el, x, y));
	}

	boolean obscuredByChildren(AndroidElement el, double x, double y){
		for(int i = 0; i < el.children.size(); i++){
			AndroidElement child = el.children.get(i);
			if(child.rect != null && child.rect.contains(x, y) && child.zindex >= el.zindex) {
				return true;
			}
		}
		return false;
	}

}

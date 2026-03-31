/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.state;

import org.testar.android.AndroidAppiumFramework;
import org.testar.android.tag.AndroidTags;
import org.testar.android.util.AndroidNodeParser;
import org.testar.android.util.AndroidXpathUtil;
import org.testar.core.util.Util;
import org.testar.core.alayer.*;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.state.SUT;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class AndroidStateFetcher implements Callable<AndroidState> {

	private final SUT system;
	
	private Rect biggestRect = Rect.from(0, 0, 0, 0);

	private String androidActivityVar;

	public AndroidStateFetcher(SUT system) {
		this.system = system;
	}

	public static AndroidRootElement buildRoot(SUT system) throws StateBuildException {
		AndroidRootElement androidroot = new AndroidRootElement();
		androidroot.isRunning = system.isRunning();
		androidroot.timeStamp = System.currentTimeMillis();
		androidroot.pid = (long)-1;
		androidroot.isForeground = false; //TODO: android process

		return androidroot;
	}

	@Override
	public AndroidState call() throws Exception {
		
		AndroidRootElement rootElement = buildAndroidSkeleton(system);

		if (rootElement == null) {
			system.set(Tags.Desc, " ");
			return new AndroidState(null);
		}

		system.set(Tags.Desc, "Android system");

		AndroidState root = createWidgetTree(rootElement);
		root.set(Tags.Role, Roles.Process);
		root.set(Tags.NotResponding, false);

		// After create the widget tree, set widgets Path
		for (Widget w : root) {
		    w.set(Tags.Path, Util.indexString(w));
		    w.set(AndroidTags.AndroidXpath, AndroidXpathUtil.constructXpath(w));
		}

		return root;
	}

	private AndroidRootElement buildAndroidSkeleton(SUT system) {
		androidActivityVar = AndroidAppiumFramework.getActivity();

		AndroidRootElement rootElement = buildRoot(system);

		if(!rootElement.isRunning)
			return rootElement;

		rootElement.pid = system.get(Tags.PID, (long)-1);

		Document xmlAndroid;
		if((xmlAndroid = AndroidAppiumFramework.getAndroidPageSource()) != null) {
		    Node stateNode = xmlAndroid.getDocumentElement();

		    if(stateNode.hasChildNodes()) {
				int childNum = stateNode.getChildNodes().getLength();
				rootElement.children = new ArrayList<AndroidElement>(childNum);

				for(int i = 0; i < childNum; i++) {
					XmlNodeDescend(rootElement, stateNode.getChildNodes().item(i));
				}
			}

		}

		// Biggest Rect: after check widget tree, use biggest Rect as State Rect
		rootElement.rect = biggestRect;
		rootElement.bounds = biggestRect;

		rootElement.text = "Root";
		rootElement.className = "Root";

        rootElement.ignore = false;
        rootElement.enabled = true;
        rootElement.blocked = false;
        rootElement.zindex = 0;

		buildTLCMap(rootElement);

		return rootElement;
	}

	private void XmlNodeDescend(AndroidElement parent, Node xmlNode) {
		AndroidElement childElement = new AndroidElement(parent);
		parent.children.add(childElement);

		childElement.zindex = parent.zindex + 1;
		childElement.enabled = AndroidNodeParser.getBooleanAttribute(xmlNode, "enabled");
		childElement.ignore = false;
		childElement.blocked = AndroidNodeParser.getBooleanAttribute(xmlNode, "focusable"); 

		childElement.nodeIndex = AndroidNodeParser.getIntegerAttribute(xmlNode, "index");
		childElement.text = AndroidNodeParser.getStringAttribute(xmlNode, "text");
		childElement.hint = AndroidNodeParser.getStringAttribute(xmlNode, "hint");
		childElement.resourceId = AndroidNodeParser.getStringAttribute(xmlNode, "resource-id");
		childElement.className = AndroidNodeParser.getStringAttribute(xmlNode, "class");
		childElement.packageName = AndroidNodeParser.getStringAttribute(xmlNode, "package");
		childElement.checkable = AndroidNodeParser.getBooleanAttribute(xmlNode, "checkable");
		childElement.checked = AndroidNodeParser.getBooleanAttribute(xmlNode, "checked");
		childElement.clickable = AndroidNodeParser.getBooleanAttribute(xmlNode, "clickable");
		childElement.focusable = AndroidNodeParser.getBooleanAttribute(xmlNode, "focusable");
		childElement.focused = AndroidNodeParser.getBooleanAttribute(xmlNode, "focused");
		childElement.scrollable = AndroidNodeParser.getBooleanAttribute(xmlNode, "scrollable");
		childElement.longclicklable = AndroidNodeParser.getBooleanAttribute(xmlNode, "long-clicklable");
		childElement.password = AndroidNodeParser.getBooleanAttribute(xmlNode, "password");
		childElement.selected = AndroidNodeParser.getBooleanAttribute(xmlNode, "selected");
		childElement.accessibilityID = AndroidNodeParser.getStringAttribute(xmlNode, "content-desc");
		childElement.displayed = AndroidNodeParser.getBooleanAttribute(xmlNode, "displayed");

		childElement.rect = androidBoundsRect(AndroidNodeParser.getStringAttribute(xmlNode, "bounds"));
		childElement.bounds = androidBoundsRect(AndroidNodeParser.getStringAttribute(xmlNode, "bounds"));
		childElement.activity = androidActivityVar;
		
		// TODO: Check a better way to create State Rect?
		if(!Rect.contains(biggestRect, childElement.rect)) {
		    biggestRect = childElement.rect;
		}

		if(xmlNode.hasChildNodes()) {
			int childNum = xmlNode.getChildNodes().getLength();
			childElement.children = new ArrayList<AndroidElement>(childNum);

			for(int i = 0; i < childNum; i++) {
				XmlNodeDescend(childElement, xmlNode.getChildNodes().item(i));
			}
		}
	}

	private AndroidState createWidgetTree(AndroidRootElement root) {
		AndroidState state = new AndroidState(root);
		root.backRef = state;
		for (AndroidElement childElement : root.children) {
			if (!childElement.ignore) {
				createWidgetTree(state, childElement);
			}
		}
		return state;
	}

	private void createWidgetTree(AndroidWidget parent, AndroidElement element) {
		if (!element.enabled) {
			return;
		}

		AndroidWidget w = parent.root().addChild(parent, element);
		element.backRef = w;

		for (AndroidElement child : element.children) {
			createWidgetTree(w, child);
		}
	}

	private void buildTLCMap(AndroidRootElement root){
		AndroidElementMap.Builder builder = AndroidElementMap.newBuilder();
		buildTLCMap(builder, root);
		root.elementMap = builder.build();
	}

	private void buildTLCMap(AndroidElementMap.Builder builder, AndroidElement el){
		if(el.isTopLevelContainer) {
			builder.addElement(el);
		}

		for(int i = 0; i < el.children.size(); i++) {
			buildTLCMap(builder, el.children.get(i));
		}
	}

	/**
	 * Create a Rect of Android Elements bounds
	 * 
	 * Android bounds [24,182][96,254]
	 * 
	 * From X1 (24) to X2 (96)
	 * From Y1 (182) to Y2 (254)
	 * 
	 * @param bounds
	 * @return
	 */
	private Rect androidBoundsRect(String bounds) {
	    String x1 = "0";
	    String y1 = "0";
	    String x2 = "0";
	    String y2 = "0";

	    try {
	        x1 = bounds.substring(bounds.indexOf("[")+1, bounds.indexOf(","));
	        y1 = bounds.substring(bounds.indexOf(",")+1, bounds.indexOf("]"));

	        bounds = bounds.substring(bounds.lastIndexOf("["), bounds.lastIndexOf("]")+1);

	        x2 = bounds.substring(bounds.indexOf("[")+1, bounds.indexOf(","));
	        y2 = bounds.substring(bounds.indexOf(",")+1, bounds.indexOf("]"));

	        double x = Double.parseDouble(x1);
	        double y = Double.parseDouble(y1);
	        double width = Double.parseDouble(x2) - Double.parseDouble(x1);
	        double height = Double.parseDouble(y2) - Double.parseDouble(y1);

	        return Rect.from(x, y, width, height);
	    } catch(Exception e) {}

	    return Rect.from(0, 0, 0, 0);
	}
}

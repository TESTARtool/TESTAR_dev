/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.ios.enums.IOSTags;
import org.testar.monkey.alayer.ios.util.IOSNodeParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class IOSStateFetcher implements Callable<IOSState> {

	private final SUT system;
	
	private Rect biggestRect = Rect.from(0, 0, 0, 0);

	public IOSStateFetcher(SUT system) {
		this.system = system;
	}

	public static IOSRootElement buildRoot(SUT system) throws StateBuildException {
		IOSRootElement iosroot = new IOSRootElement();
		iosroot.isRunning = system.isRunning();
		iosroot.timeStamp = System.currentTimeMillis();
		iosroot.pid = (long)-1;
		iosroot.isForeground = false; //TODO: ios process

		return iosroot;
	}

	@Override
	public IOSState call() throws Exception {
		
		IOSRootElement rootElement = buildIOSSkeleton(system);

		if (rootElement == null) {
			system.set(Tags.Desc, " ");
			return new IOSState(null);
		}

		system.set(Tags.Desc, "IOS system");

		IOSState root = createWidgetTree(rootElement);
		root.set(Tags.Role, Roles.Process);
		root.set(Tags.NotResponding, false);

		// After create the widget tree, set widgets Path
		// get start time
		LocalTime startTime = LocalTime.now();
		System.out.println("start construct xpath: " + startTime);

		for (Widget w : root) {
		    w.set(Tags.Path, Util.indexString(w));
		    w.set(IOSTags.iosXpath, IOSProtocolUtil.constructXpath(w));
		}

		// get end time
		LocalTime endTime = LocalTime.now();
		System.out.println("end construct Xpath: " + endTime);

		// Compute difference start and end time
		long timeBetween = ChronoUnit.MILLIS.between(startTime, endTime);
		System.out.println("Time between construct Xpath: " + timeBetween);

		return root;
	}

	private IOSRootElement buildIOSSkeleton(SUT system) {
		IOSRootElement rootElement = buildRoot(system);

		if(!rootElement.isRunning)
			return rootElement;

		rootElement.pid = system.get(Tags.PID, (long)-1);

		Document xmlIOS;
		if((xmlIOS = IOSAppiumFramework.getIOSPageSource()) != null) {
		    Node stateNode = xmlIOS.getDocumentElement();

		    if(stateNode.hasChildNodes()) {
				int childNum = stateNode.getChildNodes().getLength();
				rootElement.children = new ArrayList<IOSElement>(childNum);

				for(int i = 0; i < childNum; i++) {
					XmlNodeDescend(rootElement, stateNode.getChildNodes().item(i));
				}
			}

		}

		// After check widget tree, use biggest Rect as State Rect
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

	private void XmlNodeDescend(IOSElement parent, Node xmlNode) {
		IOSElement childElement = new IOSElement(parent);
		parent.children.add(childElement);

		childElement.zindex = parent.zindex + 1;
		childElement.enabled = IOSNodeParser.getBooleanAttribute(xmlNode, "enabled");
		childElement.ignore = false;
		//childElement.blocked = IOSNodeParser.getBooleanAttribute(xmlNode, "focusable");

		childElement.nodeIndex = IOSNodeParser.getIntegerAttribute(xmlNode, "index");
		childElement.text = IOSNodeParser.getStringAttribute(xmlNode, "value");
		//childElement.resourceId = IOSNodeParser.getStringAttribute(xmlNode, "resource-id");
		childElement.className = IOSNodeParser.getStringAttribute(xmlNode, "type");

		//childElement.packageName = IOSNodeParser.getStringAttribute(xmlNode, "package");
		//childElement.checkable = IOSNodeParser.getBooleanAttribute(xmlNode, "checkable");
		//childElement.checked = IOSNodeParser.getBooleanAttribute(xmlNode, "checked");
		//childElement.clickable = IOSNodeParser.getBooleanAttribute(xmlNode, "clickable");
		//childElement.focusable = IOSNodeParser.getBooleanAttribute(xmlNode, "focusable");
		//childElement.focused = IOSNodeParser.getBooleanAttribute(xmlNode, "focused");
		//childElement.scrollable = IOSNodeParser.getBooleanAttribute(xmlNode, "scrollable");
		//childElement.longclicklable = IOSNodeParser.getBooleanAttribute(xmlNode, "long-clicklable");
		//childElement.password = IOSNodeParser.getBooleanAttribute(xmlNode, "password");
		//childElement.selected = IOSNodeParser.getBooleanAttribute(xmlNode, "selected");
		childElement.accessibilityId = IOSNodeParser.getStringAttribute(xmlNode, "name");
		childElement.visible = IOSNodeParser.getBooleanAttribute(xmlNode, "visible");
		childElement.x = IOSNodeParser.getIntegerAttribute(xmlNode, "x");
		childElement.y = IOSNodeParser.getIntegerAttribute(xmlNode, "y");
		childElement.width = IOSNodeParser.getIntegerAttribute(xmlNode, "width");
		childElement.height = IOSNodeParser.getIntegerAttribute(xmlNode, "height");
		childElement.label = IOSNodeParser.getStringAttribute(xmlNode, "label");

//		System.out.println("accessID: " + childElement.accessibilityId);
//
//		System.out.println("X: " + childElement.x);
//		System.out.println("Y: " + childElement.y);
//		System.out.println("WIDTH: " + childElement.width);
//		System.out.println("HEIGHT: " + childElement.height);

		String selfCreatedBounds = "[" + childElement.x + "," +
				childElement.y + "][" +
				childElement.width + "," +
				childElement.height + "]";

//		System.out.println("SELF CREATED BOUNDS: " + selfCreatedBounds);

		childElement.rect = iosBoundsRect(selfCreatedBounds);
		childElement.bounds = iosBoundsRect(selfCreatedBounds);

//		System.out.println("RECT: " + childElement.bounds);
		
		// TODO: Check a better way to create State Rect?
		if(!Rect.contains(biggestRect, childElement.rect)) {
		    biggestRect = childElement.rect;
		}

		if(xmlNode.hasChildNodes()) {
			int childNum = xmlNode.getChildNodes().getLength();
			childElement.children = new ArrayList<IOSElement>(childNum);

			for(int i = 0; i < childNum; i++) {
				XmlNodeDescend(childElement, xmlNode.getChildNodes().item(i));
			}
		}
	}

	private IOSState createWidgetTree(IOSRootElement root) {
		IOSState state = new IOSState(root);
		root.backRef = state;
		for (IOSElement childElement : root.children) {
			if (!childElement.ignore) {
				createWidgetTree(state, childElement);
			}
		}
		return state;
	}

	private void createWidgetTree(IOSWidget parent, IOSElement element) {
		if (!element.enabled) {
			return;
		}

		IOSWidget w = parent.root().addChild(parent, element);
		element.backRef = w;

		for (IOSElement child : element.children) {
			createWidgetTree(w, child);
		}
	}

	private void buildTLCMap(IOSRootElement root){
		IOSElementMap.Builder builder = IOSElementMap.newBuilder();
		buildTLCMap(builder, root);
		root.elementMap = builder.build();
	}

	private void buildTLCMap(IOSElementMap.Builder builder, IOSElement el){
		if(el.isTopLevelContainer) {
			builder.addElement(el);
		}

		for(int i = 0; i < el.children.size(); i++) {
			buildTLCMap(builder, el.children.get(i));
		}
	}

	/**
	 * Create a Rect of IOS Elements bounds
	 * 
	 * ios bounds [24,182][96,254]
	 * 
	 * From X1 (24) to X2 (96)
	 * From Y1 (182) to Y2 (254)
	 * 
	 * @param bounds
	 * @return
	 */
	private Rect iosBoundsRect(String bounds) {
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
	        double width = Double.parseDouble(x2);
	        double height = Double.parseDouble(y2);

	        return Rect.from(x, y, width, height);
	    } catch(Exception e) {
		}

	    return Rect.from(0, 0, 0, 0);
	}
}

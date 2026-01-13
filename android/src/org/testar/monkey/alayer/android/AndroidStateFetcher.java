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

import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.monkey.alayer.android.util.AndroidNodeParser;
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
		    w.set(AndroidTags.AndroidXpath, AndroidProtocolUtil.constructXpath(w));
		}

		return root;
	}

	private AndroidRootElement buildAndroidSkeleton(SUT system) {
		androidActivityVar = AndroidAppiumFramework.getActivity();

		AndroidRootElement rootElement = buildRoot(system);

		if(!rootElement.isRunning)
			return rootElement;

		rootElement.pid = system.get(Tags.PID, (long)-1);
		
		// 1 Option Screen: Screen size as State Rect
		//Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		//rootElement.rect = Rect.from(0, 0, screen.getWidth(), screen.getHeight());
		//rootElement.bounds = Rect.from(0, 0, screen.getWidth(), screen.getHeight());
		

		// 2 Option FrameLayout: Obtain specific internal Android FrameLayout as State Rect
		/*try {
		    MobileElement mainFrame = AppiumFramework.findElements(new By.ByClassName("android.widget.FrameLayout")).get(0);

		    rootElement.rect = Rect.from(
		            mainFrame.getRect().getX(),
		            mainFrame.getRect().getY(),
		            mainFrame.getRect().getWidth(),
		            mainFrame.getRect().getHeight());

		    rootElement.bounds = Rect.from(
		            mainFrame.getRect().getX(),
		            mainFrame.getRect().getY(),
		            mainFrame.getRect().getWidth(),
		            mainFrame.getRect().getHeight());

		} catch(Exception e) {
		    System.out.println("Error: findElements(new By.ByClassName(\"android.widget.FrameLayout\")");
		}
		*/

		Document xmlAndroid;
		if((xmlAndroid = AndroidAppiumFramework.getAndroidPageSource()) != null) {
		    Node stateNode = xmlAndroid.getDocumentElement();

		    // 3 Option mainNode: state node element seems to have empty bounds
		    //rootElement.rect = androidBoundsRect(AndroidNodeParser.getStringAttribute(stateNode, "bounds"));
		    //rootElement.bounds = androidBoundsRect(AndroidNodeParser.getStringAttribute(stateNode, "bounds"));

		    if(stateNode.hasChildNodes()) {
				int childNum = stateNode.getChildNodes().getLength();
				rootElement.children = new ArrayList<AndroidElement>(childNum);

				for(int i = 0; i < childNum; i++) {
					XmlNodeDescend(rootElement, stateNode.getChildNodes().item(i));
				}
			}

		}

		// 4 Option biggest Rect: after check widget tree, use biggest Rect as State Rect
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

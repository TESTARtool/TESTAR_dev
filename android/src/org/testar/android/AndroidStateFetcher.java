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

package org.testar.android;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.fruit.Util;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.windows.Windows;
import org.openqa.selenium.By;
import org.testar.android.emulator.AndroidEmulatorFetcher;
import org.testar.android.emulator.WindowsInitializer;
import org.testar.android.util.AndroidNodeParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import io.appium.java_client.MobileElement;

public class AndroidStateFetcher implements Callable<AndroidState> {

	private final SUT system;
	private final AndroidEmulatorFetcher windowsEmulator;
	private final WindowsInitializer windowsInitializer;

	public AndroidStateFetcher(SUT system) {
		this.system = system;
		this.windowsInitializer = new WindowsInitializer();
		this.windowsEmulator = new AndroidEmulatorFetcher(
				system,
				windowsInitializer.getAutomationPointer(),
				windowsInitializer.getCacheRequestPointer());
	}

	public static AndroidRootElement buildRoot(SUT system) throws StateBuildException {
		AndroidRootElement androidroot = new AndroidRootElement();
		androidroot.isRunning = system.isRunning();
		androidroot.timeStamp = System.currentTimeMillis();
		androidroot.pid = (long)-1;
		androidroot.isForeground = false; //TODO: Windows Emulator + android process

		return androidroot;
	}

	@Override
	public AndroidState call() throws Exception {
		Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);
		
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
		}

		Windows.CoUninitialize();

		return root;
	}

	private AndroidRootElement buildAndroidSkeleton(SUT system) {
		AndroidRootElement rootElement = buildRoot(system);

		if(!rootElement.isRunning)
			return rootElement;

		rootElement.pid = system.get(Tags.PID, (long)-1);

		// Windows API level
		for(long windowHandle : windowsEmulator.getVisibleTopLevelWindowHandles()) {
			String windowUIAName = Windows.GetProcessNameFromHWND(windowHandle);
			if(windowUIAName.contains("qemu-system")) {
				rootElement.windowsHandle = windowHandle;
				system.set(Tags.HWND, windowHandle);
				break;
			}
		}

		Rect emulatorRect = windowsEmulator.windowsEmulatorInternalPanel(system.get(Tags.HWND, (long) -1));
		rootElement.rect = emulatorRect;
		rootElement.bounds = emulatorRect;
		
		rootElement.text = "Windows Emulator Panel";
		rootElement.className = "Windows Emulator Panel";

		rootElement.ignore = false;
		rootElement.enabled = true;
		rootElement.blocked = false;
		rootElement.zindex = 0;

		Document xmlAndroid;
		if((xmlAndroid = AppiumFramework.getAndroidPageSource()) != null) {

			Node stateNode = xmlAndroid.getDocumentElement();

			/*
			 * Obtain internal Android DrawerLayout
			 * 
			 * Because we are using Windows UIA to obtain internal Emulator sub panel
			 * We don't need to use this implementation right now
			 * 
			 * try {
			MobileElement mainFrame = AppiumFramework.findElements(new By.ByClassName("androidx.drawerlayout.widget.DrawerLayout")).get(0);

			rootElement.rect = Rect.from(
					emulatorRect.x() + mainFrame.getRect().getX(),
					emulatorRect.y() + mainFrame.getRect().getY(),
					mainFrame.getRect().getWidth(),
					mainFrame.getRect().getHeight());

			rootElement.bounds = Rect.from(
					emulatorRect.x() + mainFrame.getRect().getX(),
					emulatorRect.y() + mainFrame.getRect().getY(),
					mainFrame.getRect().getWidth(),
					mainFrame.getRect().getHeight());

			}catch(Exception e) {}
			 */

			if(stateNode.hasChildNodes()) {
				int childNum = stateNode.getChildNodes().getLength();
				rootElement.children = new ArrayList<AndroidElement>(childNum);

				for(int i = 0; i < childNum; i++) {
					XmlNodeDescend(rootElement, stateNode.getChildNodes().item(i));
				}
			}

		}

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

		childElement.rect = androidBoundsRect(childElement.root.rect, AndroidNodeParser.getStringAttribute(xmlNode, "bounds"));
		childElement.bounds = androidBoundsRect(childElement.root.rect, AndroidNodeParser.getStringAttribute(xmlNode, "bounds"));

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
	 * Create a Rect with Android Elements bounds and rootRect bounds
	 * 
	 * Android bounds [24,182][96,254]
	 * 
	 * From X1 (24) to X2 (96)
	 * From Y1 (182) to Y2 (254)
	 * 
	 * @param rootRect
	 * @param bounds
	 * @return
	 */
	private Rect androidBoundsRect(Rect rootRect, String bounds) {
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
		} catch(Exception e) {
			return Rect.from(0, 0, 0, 0);
		}
		
		double x = Double.parseDouble(x1) + rootRect.x();
		double y = Double.parseDouble(y1) + rootRect.y();
		double width = Double.parseDouble(x2) - Double.parseDouble(x1);
		double height = Double.parseDouble(y2) - Double.parseDouble(y1);

		return Rect.from(x, y, width, height);
	}
}

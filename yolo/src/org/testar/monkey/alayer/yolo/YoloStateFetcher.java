/***************************************************************************************************
 *
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.yolo;

import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.windows.Windows;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;

public class YoloStateFetcher implements Callable<YoloState> {

	private final SUT system;
	private final YoloPythonModel yoloPyModel;

	public YoloStateFetcher(SUT system, YoloPythonModel yoloPyModel) {
		this.system = system;
		this.yoloPyModel = yoloPyModel;
	}

	public static YoloRootElement buildRoot(SUT system) throws StateBuildException {
		YoloRootElement Yoloroot = new YoloRootElement();
		Yoloroot.isRunning = system.isRunning();
		Yoloroot.timeStamp = System.currentTimeMillis();
		Yoloroot.pid = (long)-1;
		Yoloroot.isForeground = false; //TODO: Yolo process

		return Yoloroot;
	}

	@Override
	public YoloState call() throws Exception {

		YoloRootElement rootElement = buildYoloSkeleton(system);

		if (rootElement == null) {
			system.set(Tags.Desc, " ");
			return new YoloState(null);
		}

		system.set(Tags.Desc, "Yolo system");

		YoloState root = createWidgetTree(rootElement);
		root.set(Tags.Role, Roles.Process);
		root.set(Tags.NotResponding, false);

		// After create the widget tree, set widgets Path
		for (Widget w : root) {
			w.set(Tags.Path, Util.indexString(w));
		}

		return root;
	}

	private YoloRootElement buildYoloSkeleton(SUT system) {
		YoloRootElement rootElement = buildRoot(system);

		if(!rootElement.isRunning)
			return rootElement;

		rootElement.pid = system.get(Tags.PID, (long)-1);

		// Here we indicate to the Yolo model the desired screenshot to analyze
		// Then, we obtain the existing Yolo elements to create the widget tree

		//fetchElementsFromScreen(rootElement);

		fetchElementsFromWindow(rootElement);

		buildTLCMap(rootElement);

		return rootElement;
	}

	private void fetchElementsFromWindow(YoloRootElement rootElement) {
		Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);

		// Obtain the SUT window handle using the pid
		for(long windowHandle : getVisibleTopLevelWindowHandles()){
			if(Windows.GetWindowProcessId(windowHandle) == rootElement.pid) {
				rootElement.windowsHandle = windowHandle;
				rootElement.isForeground = rootElement.isForeground || isForeground(rootElement.pid);
			}
			windowHandle = Windows.GetNextWindow(windowHandle, Windows.GW_HWNDNEXT);
		}

		// Get the window rect of the SUT (x1, y1, x2, y2)
		long r[] = Windows.GetWindowRect(rootElement.windowsHandle);
		Rect rootRect = Rect.fromCoordinates((int)r[0], (int)r[1], (int)r[2], (int)r[3]);

		// The root element / state, uses the rect position obtained with the Windows API
		rootElement.rect = rootRect;

		// If the root element / state is in the foreground, extract children data
		if(rootElement.isForeground) {
			List<String> yoloElements = new ArrayList<>();
			try {
				// Create a Robot object
				Robot robot = new Robot();

				// Capture a screen image using the Robot's createScreenCapture method
				BufferedImage image = robot.createScreenCapture(new Rectangle((int)rootRect.x(), (int)rootRect.y(), (int)rootRect.width(), (int)rootRect.height()));

				// Send the screen image to Yolo to obtain the elements data
				yoloElements = yoloPyModel.processImageWithYolo(image);
			} catch (IOException | AWTException | InterruptedException e) {
				e.printStackTrace();
			}

			rootElement.children = new ArrayList<YoloElement>(yoloElements.size());
			for (String line : yoloElements) {
				YoloElement childElement = new YoloElement(rootElement);
				rootElement.children.add(childElement);
				childElement.zindex = 1.0;

				String[] values = line.split(" ");

				childElement.widgetType = values[0];

				childElement.normalizedRect = Rect.from(
						Double.parseDouble(values[1]), 
						Double.parseDouble(values[2]), 
						Double.parseDouble(values[3]), 
						Double.parseDouble(values[4]));

				// We need to denormalize the values to match the GUI screen
				double x = (Double.parseDouble(values[1]) * rootRect.width()) + rootRect.x();
				double y = (Double.parseDouble(values[2]) * rootRect.height()) + rootRect.y();
				double width = Double.parseDouble(values[3]) * rootRect.width();
				double height = Double.parseDouble(values[4]) * rootRect.height();

				childElement.rect = Rect.from(x, y, width, height);
			}
		}

		Windows.CoUninitialize();
	}

	private boolean isForeground(long pid){
		long hwnd = Windows.GetForegroundWindow();
		long wpid = Windows.GetWindowProcessId(hwnd);
		return !Windows.IsIconic(hwnd) && (wpid == pid);
	}

	/* lists all visible top level windows in ascending z-order (foreground window last) */
	private Iterable<Long> getVisibleTopLevelWindowHandles(){
		Deque<Long> ret = new ArrayDeque<Long>();
		long windowHandle = Windows.GetWindow(Windows.GetDesktopWindow(), Windows.GW_CHILD);

		while(windowHandle != 0){
			if(Windows.IsWindowVisible(windowHandle)){
				long exStyle = Windows.GetWindowLong(windowHandle, Windows.GWL_EXSTYLE);
				if((exStyle & Windows.WS_EX_TRANSPARENT) == 0 && (exStyle & Windows.WS_EX_NOACTIVATE) == 0){
					ret.addFirst(windowHandle);
				}				
			}
			windowHandle = Windows.GetNextWindow(windowHandle, Windows.GW_HWNDNEXT);
		}

		System.clearProperty("DEBUG_WINDOWS_PROCESS_NAMES");

		return ret;
	}

	private void fetchElementsFromScreen(YoloRootElement rootElement) {
		List<String> yoloElements = new ArrayList<>();

		// Get the screen resolution
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		try {
			// Create a Robot object
			Robot robot = new Robot();

			// Capture a screen image using the Robot's createScreenCapture method
			Rectangle screenRect = new Rectangle(screenSize);
			BufferedImage image = robot.createScreenCapture(screenRect);

			// Send the screen image to Yolo to obtain the elements data
			yoloElements = yoloPyModel.processImageWithYolo(image);
		} catch (IOException | AWTException | InterruptedException e) {
			e.printStackTrace();
		}

		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();

		rootElement.normalizedRect = Rect.from(0, 0, screenWidth, screenHeight);
		rootElement.rect = Rect.from(0, 0, screenWidth, screenHeight);

		rootElement.children = new ArrayList<YoloElement>(yoloElements.size());
		for (String line : yoloElements) {
			YoloElement childElement = new YoloElement(rootElement);
			rootElement.children.add(childElement);
			childElement.zindex = 1.0;

			String[] values = line.split(" ");

			childElement.widgetType = values[0];

			childElement.normalizedRect = Rect.from(
					Double.parseDouble(values[1]), 
					Double.parseDouble(values[2]), 
					Double.parseDouble(values[3]), 
					Double.parseDouble(values[4]));

			// We need to denormalize the values to match the GUI screen
			double x = Double.parseDouble(values[1]) * screenWidth;
			double y = Double.parseDouble(values[2]) * screenHeight;
			double width = Double.parseDouble(values[3]) * screenWidth;
			double height = Double.parseDouble(values[4]) * screenHeight;

			childElement.rect = Rect.from(x, y, width, height);
		}
	}

	private YoloState createWidgetTree(YoloRootElement root) {
		YoloState state = new YoloState(root);
		root.backRef = state;
		for (YoloElement childElement : root.children) {
			createWidgetTree(state, childElement);
		}
		return state;
	}

	private void createWidgetTree(YoloWidget parent, YoloElement element) {
		if (!element.enabled) {
			return;
		}

		YoloWidget w = parent.root().addChild(parent, element);
		element.backRef = w;

		for (YoloElement child : element.children) {
			createWidgetTree(w, child);
		}
	}

	private void buildTLCMap(YoloRootElement root){
		YoloElementMap.Builder builder = YoloElementMap.newBuilder();
		buildTLCMap(builder, root);
		root.elementMap = builder.build();
	}

	private void buildTLCMap(YoloElementMap.Builder builder, YoloElement el){
		for(int i = 0; i < el.children.size(); i++) {
			buildTLCMap(builder, el.children.get(i));
		}
	}
}

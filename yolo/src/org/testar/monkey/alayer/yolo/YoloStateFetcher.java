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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
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
		List<String> yoloElements = new ArrayList<>();
		try {
			yoloElements = yoloPyModel.processImageWithYolo();
		} catch (IOException | AWTException | InterruptedException e) {
			e.printStackTrace();
		}

		// Get the screen resolution
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();

		rootElement.normalizedRect = Rect.from(0, 0, screenWidth, screenHeight);

		rootElement.children = new ArrayList<YoloElement>(yoloElements.size());
		for (String line : yoloElements) {
			YoloElement childElement = new YoloElement(rootElement);
			rootElement.children.add(childElement);
			childElement.zindex = 1.0;

			String[] values = line.split(" ");
			String type = values[0];
			double x = Double.parseDouble(values[1]) * screenWidth;
			double y = Double.parseDouble(values[2]) * screenHeight;
			double width = Double.parseDouble(values[3]) * screenWidth;
			double height = Double.parseDouble(values[4]) * screenHeight;

			childElement.widgetType = type;
			childElement.normalizedRect = Rect.from(x, y, width, height);
		}

		buildTLCMap(rootElement);

		return rootElement;
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

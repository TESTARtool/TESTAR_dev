/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2024 Open Universiteit - www.ou.nl
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

import org.testar.ProtocolUtil;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;

import java.io.IOException;
import java.util.ArrayList;

public class AndroidProtocolUtil extends ProtocolUtil {

	public static String getActionshot(State state, Action action) {
		try {
			return org.testar.monkey.alayer.android.AndroidAppiumFramework.getScreenshotAction(state, action);
		} catch(Exception e) {
			System.err.println("Exception when taking action screenshot: " + e);
		}

		return ProtocolUtil.getActionshot(state, action);
	}

	public static String getStateshot(State state) {
		try {
			return AndroidAppiumFramework.getScreenshotState(state);
		} catch(Exception e) {
			System.err.println("Exception occured when trying to take a screenshot of the Android emulator: " + e);
		}

		return ProtocolUtil.getStateshot(state);
	}

	public static String getStateshotSpyMode(State state) {
		try {
			return org.testar.monkey.alayer.android.AndroidAppiumFramework.getScreenshotSpyMode(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"));
		} catch(Exception e) {
			System.err.println("Exception occured when trying to take a screenshot of the Android emulator: " + e);
		}

		return ProtocolUtil.getStateshot(state);
	}

	/**
	 * Method returns a binary representation of a state's screenshot.
	 * @param state
	 * @return
	 */
	public static AWTCanvas getStateshotBinary(State state, Widget widget) {
		// Obtain the binary screenshotfile through the appium driver.
		try {
			return AndroidAppiumFramework.getScreenshotBinary(state, widget);
		} catch (IOException e) {
			System.err.println("Exception occured when trying to take a binary screenshot of the Android emulator: " + e);
		}

		return ProtocolUtil.getStateshotBinary(state);
	}

	public static String getCurrentPackage() {
		return AndroidAppiumFramework.getCurrentPackage();
	}

	// Method which constructs the hierarchy xpath (absolute path)
	// This method is needed as this is the only way to uniquely identify an GUI object if it has no accessibilityID.
	public static String constructXpath(Widget w) {
		StringBuilder sb =  new StringBuilder();
		Widget parentWidget = w;

		while (parentWidget != w.root()) {
			String classTag = parentWidget.get(Tags.Desc);
			int indexNumber = parentWidget.get(AndroidTags.AndroidNodeIndex);

			if (classTag.equals("Root")) {
				break;
			}

			parentWidget = parentWidget.parent();

			ArrayList<Pair<String, Integer>> childClasses = new ArrayList<Pair<String, Integer>>();

			for (int i = 0; i < parentWidget.childCount(); i++) {
				childClasses.add(new Pair<String, Integer>(parentWidget.child(i).get(Tags.Desc), parentWidget.child(i).get(AndroidTags.AndroidNodeIndex)));
			}

			boolean checkDoubles = false;
			boolean incCounter = true;
			int counterOccur = 1;
			for (Pair childClass : childClasses) {
				String leftSide = (String) childClass.left();
				int rightSide = (int) childClass.right();
				if (leftSide.equals(classTag) && rightSide != indexNumber) {
					checkDoubles = true;
					if (incCounter) {
						counterOccur++;
					}
				}
				else if (leftSide.equals(classTag) && rightSide == indexNumber) {
					incCounter = false;
				}
			}

			if (checkDoubles) {
				String xpathComponent = "/" + classTag + "[" + counterOccur + "]";
				sb.insert(0, xpathComponent);
			}
			else {
				String xpathComponent = "/" + classTag;
				sb.insert(0, xpathComponent);
			}

		}

		sb.insert(0, "/hierarchy");

		return sb.toString();
	}

	public static boolean resolveEscapedApplication(String originalPackage) {
		AndroidAppiumFramework.clickBackButton();
		String currentState = AndroidAppiumFramework.getCurrentPackage();
		if (!originalPackage.equals(currentState)) {
			return false;
		}
		return true;
	}
}

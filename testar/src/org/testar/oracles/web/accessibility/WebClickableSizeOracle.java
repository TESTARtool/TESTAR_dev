/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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


package org.testar.oracles.web.accessibility;

import java.util.ArrayList;
import java.util.List;

import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;

/**
 * This oracle checks for clickable web elements that are smaller than the W3C recommended 
 * minimum touch target size of pixels. Small clickable elements can be difficult to 
 * interact with, especially for users with motor impairments or on touch devices.
 */
public class WebClickableSizeOracle implements Oracle {

	// Minimum recommended clickable size (W3C Web Content Accessibility Guidelines)
	private final int minClickableThreshold;

	public WebClickableSizeOracle() {
		this(24);
	}

	public WebClickableSizeOracle(int minClickableThreshold) {
		this.minClickableThreshold = minClickableThreshold;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public Verdict getVerdict(State state) {
		List<Widget> incorrectWidgets = new ArrayList<>();

		// Iterate over all widgets in the state
		for (Widget widget : state) {
			// Check if the widget is a clickable visible element
			if (Role.isOneOf(widget.get(Tags.Role, Roles.Widget), WdRoles.nativeClickableRoles())
					&& widget.get(WdTags.WebIsFullOnScreen, false)
					&& widget.get(Tags.Shape, null) != null) {

				// Get width and height of the clickable element
				Double width = ((Rect)widget.get(Tags.Shape)).width();
				Double height = ((Rect)widget.get(Tags.Shape)).height();

				// Check if the widget is smaller than the recommended pixels
				if (width < minClickableThreshold || height < minClickableThreshold) {
					// If so, save it as incorrect widget
					incorrectWidgets.add(widget);
				}
			}
		}

		// If exists one or more incorrect widgets
		if(!incorrectWidgets.isEmpty()) {
			// Create and return a WARNING_ACCESSIBILITY_FAULT verdict
			String verdictMsg = String.format(
					"Clickable web widgets %s are too small (Minimum: %s px).",
					getDescriptionOfWidgets(incorrectWidgets, WdTags.WebOuterHTML),
					minClickableThreshold
					);
			Visualizer visualizer = new RegionsVisualizer(
					getRedPen(), 
					getWidgetRegions(incorrectWidgets), 
					"Accessibility Fault", 
					0.5, 0.5);
			return new Verdict(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT, verdictMsg, visualizer);
		} else {
			return Verdict.OK;
		}

	}

}

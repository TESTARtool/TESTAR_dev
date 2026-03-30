/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.accessibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Roles;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.Visualizer;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.core.state.Widget;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.oracle.Oracle;

/**
 * This oracle checks for clickable web elements that are smaller than the W3C recommended 
 * minimum touch target size of pixels. Small clickable elements can be difficult to 
 * interact with, especially for users with motor impairments or on touch devices.
 */
public class WebAccessibilityClickableSizeOracle implements Oracle {

	// Minimum recommended clickable size (W3C Web Content Accessibility Guidelines)
	private final int minClickableThreshold;

	public WebAccessibilityClickableSizeOracle() {
		this(24);
	}

	public WebAccessibilityClickableSizeOracle(int minClickableThreshold) {
		this.minClickableThreshold = minClickableThreshold;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		List<Verdict> verdicts = new ArrayList<>();

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
					String verdictMsg = String.format(
							"Clickable web widget %s is too small (%sx%s px). Minimum: %s px.",
							getDescriptionOfWidgets(Collections.singletonList(widget), WdTags.WebOuterHTML),
							width.intValue(),
							height.intValue(),
							minClickableThreshold
							);
					Visualizer visualizer = new RegionsVisualizer(
							getRedPen(), 
							getWidgetRegions(Arrays.asList(widget)), 
							"Accessibility Fault", 
							0.5, 0.5);
					verdicts.add(new Verdict(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT, verdictMsg, visualizer));
				}
			}
		}

		if(!verdicts.isEmpty()) {
			return verdicts;
		}
		return Collections.singletonList(Verdict.OK);
	}

}

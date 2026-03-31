/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.accessibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testar.core.alayer.Roles;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.Visualizer;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;
import org.testar.core.state.Widget;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.oracle.Oracle;

/**
 * Test Oracle that checks for web image elements (<img>) that do not have alternative text. 
 * Missing alternative text can make content inaccessible for users relying on screen readers. 
 */
public class WebAccessibilityImagesAltOracle implements Oracle {

	public WebAccessibilityImagesAltOracle() {}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		List<Verdict> verdicts = new ArrayList<>();

		// Check if some widget of the state
		for(Widget widget : state) {
			//  Is a widget image (<img>) and if it lacks alternative text
			if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdIMG)
					&& (widget.get(WdTags.WebAlt, null) == null || widget.get(WdTags.WebAlt, "").isBlank())) {
				String verdictMsg = String.format(
						"Detected web image widget %s without alternative text!",
						getDescriptionOfWidgets(Collections.singletonList(widget), WdTags.WebOuterHTML)
						);
				Visualizer visualizer = new RegionsVisualizer(
						getRedPen(),
						getWidgetRegions(Collections.singletonList(widget)),
						"Accessibility Fault",
						0.5, 0.5);
				verdicts.add(new Verdict(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT, verdictMsg, visualizer));
			}
		}

		if(!verdicts.isEmpty()) {
			return verdicts;
		}
		return Collections.singletonList(Verdict.OK);
	}

}

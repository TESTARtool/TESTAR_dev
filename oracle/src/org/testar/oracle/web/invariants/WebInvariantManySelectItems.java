/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.invariants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testar.core.alayer.Roles;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.Visualizer;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.core.state.Widget;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.oracle.Oracle;
public class WebInvariantManySelectItems implements Oracle {

	private final int thresholdValue;

	public WebInvariantManySelectItems() {
		this(100);
	}

	public WebInvariantManySelectItems(int thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		List<Verdict> verdicts = new ArrayList<>();

		for (Widget w : state) {
			if (w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				try {
					String elementId = w.get(WdTags.WebId);
					String query = String.format("return ((document.getElementById('%s') != null) ? document.getElementById('%s').length : 1)", elementId, elementId);
					Long selectItemsLength = (Long) WdDriver.executeScript(query);

					// Check if the items of the select widget are more than the thresholdValue
					if (selectItemsLength != null && selectItemsLength.intValue() > thresholdValue) {
						String verdictMsg = String.format(
								"Detected Select widget %s which has %d items (threshold: %d)",
								getDescriptionOfWidgets(Collections.singletonList(w), WdTags.WebId),
								selectItemsLength.intValue(),
								thresholdValue
								);

						Visualizer visualizer = new RegionsVisualizer(
								getRedPen(),
								getWidgetRegions(Collections.singletonList(w)),
								"Invariant Fault",
								0.5, 0.5);

						verdicts.add(new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMsg, visualizer));
					}

				} catch (Exception e) {
					// Ignore webdriver execute script errors
				}
			}
		}

		if (!verdicts.isEmpty()) {
			return verdicts;
		}
		return Collections.singletonList(Verdict.OK);
	}
}

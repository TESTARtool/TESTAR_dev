/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.invariants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;

import org.testar.core.alayer.Role;
import org.testar.core.alayer.Roles;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.Visualizer;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.tag.WdTags;
import org.testar.core.state.Widget;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.oracle.Oracle;

public class WebInvariantDuplicateSelectItems implements Oracle {

	private final List<Role> roles;

	public WebInvariantDuplicateSelectItems() {
		this(List.of(WdRoles.WdSELECT));
	}

	public WebInvariantDuplicateSelectItems(List<Role> roles) {
		this.roles = roles;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		List<Verdict> verdicts = new ArrayList<>();

		for (Widget w : state) {
			if (roles.contains(w.get(Tags.Role, Roles.Widget)) && !w.get(WdTags.WebId, "").isEmpty()) {
				try {
					String elementId = w.get(WdTags.WebId);
					String query = String.format(
							"var el = document.getElementById('%s');" +
									"if (el && el.options && el.options.length > 1) {" +
									"    return [...el.options].map(o => o.text);" +
									"} else { return null; }", 
									elementId);
					@SuppressWarnings("unchecked")
					ArrayList<String> selectOptionsTextsList = (ArrayList<String>) WdDriver.executeScript(query);

					if (selectOptionsTextsList != null) {
						Set<String> duplicatesTexts = selectOptionsTextsList.stream()
								.filter(s -> Collections.frequency(selectOptionsTextsList, s) > 1)
								.collect(Collectors.toSet());

						if (!duplicatesTexts.isEmpty()) {
							String verdictMsg = String.format(
									"Detected Select widget %s with duplicate values: %s",
									getDescriptionOfWidgets(Collections.singletonList(w), WdTags.WebId),
									duplicatesTexts
									);

							Visualizer visualizer = new RegionsVisualizer(
									getRedPen(),
									getWidgetRegions(Collections.singletonList(w)),
									"Invariant Fault",
									0.5, 0.5);

							verdicts.add(new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMsg, visualizer));
						}
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

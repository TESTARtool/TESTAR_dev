/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.invariants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

public class WebInvariantDuplicateMenuItems implements Oracle {

	public WebInvariantDuplicateMenuItems() {}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		List<Verdict> verdicts = new ArrayList<>();

		for (Widget w : state) {
			// Check for UL elements with at least two children
			if (w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdUL) && w.childCount() > 1) {
				ArrayList<String> selectOptionsTextsList = new ArrayList<>();

				// Gather the text of each LI child
				for (int i = 0; i < w.childCount(); i++) {
					String itemText = w.child(i).get(WdTags.WebTextContent);

					// Only consider LI elements with non-empty text
					if (w.child(i).get(Tags.Role, Roles.Widget).equals(WdRoles.WdLI) && !itemText.isEmpty()) {
						selectOptionsTextsList.add(itemText);
					}
				}

				// Find duplicates in the list of item texts
				Set<String> duplicatesTexts = findDuplicates(selectOptionsTextsList);

				// If duplicates are found, prepare the verdict message
				if (duplicatesTexts.size() > 0) {
					String verdictMsg = String.format(
							"Detected a Unnumbered List (UL) web menu %s with duplicate option elements: %s",
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
		}

		if (!verdicts.isEmpty()) {
			return verdicts;
		}
		return Collections.singletonList(Verdict.OK);
	}

	// Helper method to find duplicates in a list
	private static Set<String> findDuplicates(List<String> list) {
		Set<String> set = new HashSet<>();
		Set<String> duplicates = new HashSet<>();
		for (String s : list) {
			// If the item is already in the set, it is a duplicate
			if (set.contains(s)) {
				duplicates.add(s);
			}
			// Add item to the set
			set.add(s);
		}
		return duplicates;
	}
}

/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.invariants;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.text.Collator;
import java.time.Month;

import org.testar.core.alayer.Role;
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

import com.google.common.collect.Comparators;

public class WebInvariantUnsortedSelectItems implements Oracle {

	private final List<Role> roles;

	public WebInvariantUnsortedSelectItems() {
		this(List.of(WdRoles.WdSELECT));
	}

	public WebInvariantUnsortedSelectItems(List<Role> roles) {
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
					ArrayList<String> selectOptionsList = (ArrayList<String>) WdDriver.executeScript(query);

					// Check if the options are sorted
					if (selectOptionsList != null && !isSorted(selectOptionsList)) {
						String verdictMsg = String.format(
								"Detected Select widget %s with unsorted elements!",
								getDescriptionOfWidgets(Collections.singletonList(w), WdTags.WebId)
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

	// Helper method to check if the list of strings is sorted
	private static boolean isSorted(List<String> listOfStrings) {
		// First check if the list is sorted normally
		if (Comparators.isInOrder(listOfStrings, Comparator.<String>naturalOrder())) {
			return true;
		} 
		// If not, check if the list is sorted using collator primary order
		else if (collatorPrimaryOrder(listOfStrings)) {
			return true;
		} 
		// Check if it's sorted by month order
		else if (monthComparator(listOfStrings)) {
			return true;
		}

		// If none of the conditions matched, the list is unsorted
		return false;
	}

	// This method checks if the list is sorted in primary collator order (ignoring case)
	private static boolean collatorPrimaryOrder(List<String> original) {
		List<String> copyListOfStrings = new ArrayList<>(original);
		Collator coll = Collator.getInstance(Locale.US);
		coll.setStrength(Collator.PRIMARY); // Ignores case differences
		Collections.sort(copyListOfStrings, coll);
		return original.equals(copyListOfStrings);
	}

	// This method checks if the list is sorted by months
	private static boolean monthComparator(List<String> original) {
		List<String> copyListOfStrings = new ArrayList<>(original);
		Comparator<String> comp = Comparator.comparing(s -> Month.valueOf(s.toUpperCase()));

		try {
			copyListOfStrings.sort(comp);
		} catch (IllegalArgumentException iae) {
			return false; // Return false if the comparison fails
		}

		return original.equals(copyListOfStrings);
	}
}

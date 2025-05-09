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

package org.testar.oracles.web.invariants;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.text.Collator;
import java.time.Month;

import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;

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
	public Verdict getVerdict(State state) {
		List<Widget> unsortedSelectWidgets = new ArrayList<>();

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
						unsortedSelectWidgets.add(w);
					}
				} catch (Exception e) {
					// Ignore webdriver execute script errors
				}
			}
		}

		if (!unsortedSelectWidgets.isEmpty()) {

			String verdictMsg = String.format(
					"Detected Select widgets %s with unsorted elements!",
					getDescriptionOfWidgets(unsortedSelectWidgets, WdTags.WebId)
					);

			Visualizer visualizer = new RegionsVisualizer(
					getRedPen(),
					getWidgetRegions(unsortedSelectWidgets),
					"Invariant Fault",
					0.5, 0.5);

			return new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMsg, visualizer);
		}

		return Verdict.OK;
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

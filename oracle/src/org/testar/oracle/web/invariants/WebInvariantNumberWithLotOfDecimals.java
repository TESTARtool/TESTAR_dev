/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.invariants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.Visualizer;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.core.state.Widget;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.oracle.Oracle;

public class WebInvariantNumberWithLotOfDecimals implements Oracle {

	private final int maxDecimals;

	public WebInvariantNumberWithLotOfDecimals() {
		this(2);
	}

	public WebInvariantNumberWithLotOfDecimals(int maxDecimals) {
		this.maxDecimals = maxDecimals;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		List<Verdict> verdicts = new ArrayList<>();

		for (Widget w : state) {
			// If the widget contains a web text that is a numeric value
			if (!w.get(WdTags.WebTextContent, "").isEmpty() && isNumeric(w.get(WdTags.WebTextContent))) {
				String number = w.get(WdTags.WebTextContent).replace(",", ".");
				number = number.trim().replace("\u0024", "").replace("\u20AC", "");

				if (number.contains(".")) {
					int decimalPlaces = number.length() - number.indexOf('.') - 1;

					if (decimalPlaces > maxDecimals) {
						String verdictMsg = String.format(
								"Detected widget %s with %d decimals (max: %d)!",
								getDescriptionOfWidgets(Collections.singletonList(w), WdTags.WebTextContent),
								decimalPlaces,
								maxDecimals
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
		}

		if (!verdicts.isEmpty()) {
			return verdicts;
		}
		return Collections.singletonList(Verdict.OK);
	}

	private boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		strNum = strNum.trim().replace("\u0024", "").replace("\u20AC", "");
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}

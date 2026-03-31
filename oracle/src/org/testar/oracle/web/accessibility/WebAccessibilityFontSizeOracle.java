/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.accessibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.Visualizer;
import org.testar.webdriver.tag.WdTags;
import org.testar.core.state.Widget;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.oracle.Oracle;

/**
 * This oracle checks for text web elements that are smaller than the W3C recommended 
 * minimum font size of pixels. 
 */
public class WebAccessibilityFontSizeOracle implements Oracle {

	// Minimum font size that can be considered a severe fault
	private final int minFontSizeThreshold;

	public WebAccessibilityFontSizeOracle() {
		this(12);
	}

	public WebAccessibilityFontSizeOracle(int minFontSizeThreshold) {
		this.minFontSizeThreshold = minFontSizeThreshold;
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
			// Check if the widget text is a visible font text element
			if (widget.get(WdTags.WebTextContent, null) != null
					&& !widget.get(WdTags.WebTextContent, "").isBlank()
					&& widget.get(WdTags.WebComputedFontSize, null) != null
					&& !widget.get(WdTags.WebComputedFontSize, "").isBlank()
					&& widget.get(WdTags.WebIsFullOnScreen, false)
					&& widget.get(Tags.Shape, null) != null) {

				// Get the computed font size (e.g., "11px") and extract the numerical value
				String textComputedFontSize = widget.get(WdTags.WebComputedFontSize);
				int fontSize = extractFontSize(textComputedFontSize);

				// Check if the font size is below the minimum threshold
				if (fontSize > 0 && fontSize < minFontSizeThreshold) {
					String verdictMsg = String.format(
							"Widget text %s is too small (%d px). Minimum recommended is %d px.",
							getDescriptionOfWidgets(Collections.singletonList(widget), WdTags.WebTextContent),
							fontSize,
							minFontSizeThreshold
							);
					Visualizer visualizer = new RegionsVisualizer(
							getRedPen(),
							getWidgetRegions(Collections.singletonList(widget)),
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

	/**
	 * Extracts the numerical font size from a string (e.g., "11px").
	 *
	 * @param fontSizeStr The font size string with "px" suffix.
	 * @return The numerical font size, or -1 if parsing fails.
	 */
	private int extractFontSize(String fontSizeStr) {
		try {
			return Integer.parseInt(fontSizeStr.replace("px", "").trim());
		} catch (NumberFormatException e) {
			return -1; // Return -1 if parsing fails
		}
	}

}

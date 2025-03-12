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

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;

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
	public Verdict getVerdict(State state) {
		List<Widget> incorrectWidgets = new ArrayList<>();

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
					// If so, save it as incorrect widget
					incorrectWidgets.add(widget);
				}
			}
		}

		// If exists one or more incorrect widgets
		if(!incorrectWidgets.isEmpty()) {
			// Create and return a WARNING_ACCESSIBILITY_FAULT verdict
			String verdictMsg = String.format(
					"These widgets Text %s are too small. Minimum recommended is %s px.",
					getDescriptionOfWidgets(incorrectWidgets, WdTags.WebTextContent), 
					minFontSizeThreshold
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

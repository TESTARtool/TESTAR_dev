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

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;

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
	public String getMessage() {
		return "";
	}

	@Override
	public Verdict getVerdict(State state) {
		List<Widget> incorrectDecimalWidgets = new ArrayList<>();

		for (Widget w : state) {
			// If the widget contains a web text that is a numeric value
			if (!w.get(WdTags.WebTextContent, "").isEmpty() && isNumeric(w.get(WdTags.WebTextContent))) {
				String number = w.get(WdTags.WebTextContent).replace(",", ".");
				number = number.trim().replace("\u0024", "").replace("\u20AC", "");

				if (number.contains(".")) {
					int decimalPlaces = number.length() - number.indexOf('.') - 1;

					if (decimalPlaces > maxDecimals) {
						incorrectDecimalWidgets.add(w);
					}
				}
			}
		}

		// If exists one or more incorrect widgets
		if (!incorrectDecimalWidgets.isEmpty()) {

			String verdictMsg = String.format(
					"Detected widgets %s with more than %d decimals!",
					getDescriptionOfWidgets(incorrectDecimalWidgets, WdTags.WebTextContent),
					maxDecimals
					);

			Visualizer visualizer = new RegionsVisualizer(
					getRedPen(), 
					getWidgetRegions(incorrectDecimalWidgets), 
					"Invariant Fault", 
					0.5, 0.5);

			return new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMsg, visualizer);
		}

		return Verdict.OK;
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

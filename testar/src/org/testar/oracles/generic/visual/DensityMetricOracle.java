/***************************************************************************************************
 *
 * Copyright (c) 2023 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2023 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.oracles.generic.visual;

import java.util.ArrayList;

import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;

/**
 * Calculates an aesthetic value between 0.00 (bad) and 100.0 (perfect) for the density of widgets,
 * and gives a warning if the threshold is breached.
 * Based on the work of "Towards an evaluation of graphical user interfaces aesthetics based on metrics" by
 * Zen, Mathieu ; Vanderdonckt, Jean.
 * 
 * The default threshold values are 25.0 (minimum) and 75.0 (maximum).
 */
public class DensityMetricOracle implements Oracle {

	private final double thresholdMinValue;
	private final double thresholdMaxValue;

	public DensityMetricOracle() {
		this(25.0, 75.0);
	}

	public DensityMetricOracle(double thresholdMinValue, double thresholdMaxValue) {
		this.thresholdMinValue = thresholdMinValue;
		this.thresholdMaxValue = thresholdMaxValue;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public Verdict getVerdict(State state) {
		if (state.childCount() == 0) {
			return Verdict.OK; // State has no children, no need for balance metric evaluation
		}

		Shape sutShape = state.child(0).get(Tags.Shape, null);
		if (sutShape == null) {
			return Verdict.OK; // SUT has no shape, no need for balance metric evaluation
		}

		Rect sutRect = (Rect) sutShape;
		if (sutRect.width() <= 0 || sutRect.height() <= 0) {
			return Verdict.OK; // Invalid shape dimensions, skip evaluation
		}

		ArrayList<Shape> regions = MetricsHelper.getRegions(state);

		double densityMetric = MetricsHelper.calculateDensity(regions, sutRect.width(), sutRect.height());

		Verdict widgetDensityVerdict = Verdict.OK;

		if (densityMetric < thresholdMinValue) {
			String verdictMsg = String.format("Density metric with value %f is below threshold minimum value %f! Design too simple.", densityMetric, thresholdMinValue);

			Visualizer visualizer = new RegionsVisualizer(getRedPen(), regions, "Density Warning - Too Simple", 0.5, 0.5);
			Verdict verdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, visualizer);
			widgetDensityVerdict = widgetDensityVerdict.join(verdict);
		}

		if (densityMetric > thresholdMaxValue) {
			String verdictMsg = String.format("Density metric with value %f is higher than threshold maximum value %f! Design too complex.", densityMetric, thresholdMaxValue);
			Visualizer visualizer = new RegionsVisualizer(getRedPen(), regions, "Density Warning - Too Complex", 0.5, 0.5);
			Verdict verdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, visualizer);
			widgetDensityVerdict = widgetDensityVerdict.join(verdict);
		}

		return widgetDensityVerdict;
	}

}

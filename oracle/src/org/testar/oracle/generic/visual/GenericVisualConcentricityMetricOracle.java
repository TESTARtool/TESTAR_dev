/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.generic.visual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Shape;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.Visualizer;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.oracle.Oracle;

/**
 * Calculates an aesthetic value between 0.00 (bad) and 100.0 (perfect) for the concentricity of widgets,
 * and gives a warning if the threshold is breached.
 * Based on the work of "Towards an evaluation of graphical user interfaces aesthetics based on metrics" by
 * Zen, Mathieu ; Vanderdonckt, Jean.
 * 
 * The default threshold value is 50.0.
 */
public class GenericVisualConcentricityMetricOracle implements Oracle {

	private final double thresholdValue;

	public GenericVisualConcentricityMetricOracle() {
		this(50.0);
	}

	public GenericVisualConcentricityMetricOracle(double thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		if (state.childCount() == 0) {
			return Collections.singletonList(Verdict.OK); // State has no children, no need for balance metric evaluation
		}

		Shape sutShape = state.child(0).get(Tags.Shape, null);
		if (sutShape == null) {
			return Collections.singletonList(Verdict.OK); // SUT has no shape, no need for balance metric evaluation
		}

		Rect sutRect = (Rect) sutShape;
		if (sutRect.width() <= 0 || sutRect.height() <= 0) {
			return Collections.singletonList(Verdict.OK); // Invalid shape dimensions, skip evaluation
		}

		ArrayList<Shape> regions = MetricsHelper.getRegions(state);

		double concentricityMetric = MetricsHelper.calculateConcentricity(regions, sutRect.width(), sutRect.height());

		if (concentricityMetric < thresholdValue) {
			String verdictMsg = String.format("Concentricity metric with value %f is below threshold value %f!", concentricityMetric, thresholdValue);
			Visualizer visualizer = new RegionsVisualizer(getRedPen(), regions, "Concentricity Metric Warning", 0.5, 0.5);
			return Collections.singletonList(new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, visualizer));
		}

		return Collections.singletonList(Verdict.OK);
	}

}

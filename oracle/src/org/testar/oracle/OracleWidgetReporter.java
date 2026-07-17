/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.testar.core.alayer.Color;
import org.testar.core.alayer.FillPattern;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Shape;
import org.testar.core.alayer.StrokePattern;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;

public interface OracleWidgetReporter {

	/**
	 * Provides a standard red pen for visual annotations. 
	 */
	default Pen getRedPen() {
		return Pen.newPen()
				.setColor(Color.Red)
				.setFillPattern(FillPattern.None)
				.setStrokePattern(StrokePattern.Solid)
				.build();
	}

	/**
	 * Constructs an HTML-escaped description of a list of widgets based on a specific tag.
	 *
	 * @param widgetsToDescribe The list of widgets to describe.
	 * @param tag The String tag whose values will be extracted for the description.
	 * @return A concatenated string describing the widgets.
	 */
	default String getDescriptionOfWidgets(List<Widget> widgetsToDescribe, Tag<String> tag) {
		if (widgetsToDescribe == null || widgetsToDescribe.isEmpty()) {
			return "No affected widgets.";
		}

		StringBuilder description = new StringBuilder();

		for (Widget widget : widgetsToDescribe) {
			String value = widget.get(tag, "").trim();
			if (!value.isEmpty()) {
				description.append("'").append(StringEscapeUtils.escapeHtml(value)).append("'").append(" , ");
			}
		}

		return description.toString().isEmpty() ? "No relevant descriptions found." : description.toString();
	}

	/**
	 * Retrieves the shape regions of widgets that contain a valid shape.
	 *
	 * @param widgets The list of widgets to extract shapes from.
	 * @return A list of Shape objects representing widget regions.
	 */
	default List<Shape> getWidgetRegions(List<Widget> widgets) {
		if (widgets == null || widgets.isEmpty()) {
			return new ArrayList<>();
		}

		return widgets.stream()
				.map(widget -> widget.get(Tags.Shape, null))
				.filter(shape -> shape != null)
				.filter(shape -> shape instanceof Rect)
				.collect(Collectors.toList());
	}

}

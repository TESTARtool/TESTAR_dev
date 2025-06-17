/***************************************************************************************************
 *
 * Copyright (c) 2022 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.oracles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.testar.monkey.alayer.Color;
import org.testar.monkey.alayer.FillPattern;
import org.testar.monkey.alayer.Pen;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.StrokePattern;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;

/**
 * This is the interface for oracles - modules that determine whether the
 * an error or problem has occurred in the SUT.
 */

public interface Oracle {

    /**
     * Initialize the Oracle
     */
    public abstract void initialize();

    /**
     * Request that the Oracle determine a verdict about the current state of the SUT.
     * This method would usually be called by the getVerdict method in the protocol.
     *
     * @param state
     * @return verdict
     */
    public abstract Verdict getVerdict(State state);

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

/***************************************************************************************************
 *
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.oracles.android.invariants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;

public class AndroidClashClickableWidgets implements Oracle {

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        // Prepare a list that contains all the Rectangles from the clickable (and displayed) widgets
        List<Pair<Widget, Rect>> clickableWidgetsRects = new ArrayList<>();
        for (Widget w : state) {
            if (w.get(AndroidTags.AndroidClickable, false)
                    && w.get(AndroidTags.AndroidDisplayed, false)
                    && w.get(Tags.Shape, null) != null) {
                clickableWidgetsRects.add(new Pair<Widget, Rect>(w, (Rect) w.get(Tags.Shape)));
            }
        }

        // Detect if the Rectangles of two clickable widgets are overlapping in an intersection
        for (int i = 0; i < clickableWidgetsRects.size(); i++) {
            for (int j = i + 1; j < clickableWidgetsRects.size(); j++) {
                Rect rectOne = clickableWidgetsRects.get(i).right();
                Rect rectTwo = clickableWidgetsRects.get(j).right();

                if (Rect.overlap(rectOne, rectTwo)) {

                    Widget firstWidget = clickableWidgetsRects.get(i).left();
                    Widget secondWidget = clickableWidgetsRects.get(j).left();

                    if (isAncestorOf(firstWidget, secondWidget) || isAncestorOf(secondWidget, firstWidget)) {
                        continue;
                    }

                    String firstWidgetDesc = widgetDesc(firstWidget);
                    String secondWidgetDesc = widgetDesc(secondWidget);

                    String verdictMsg = String.format(
                            "Two clickable widgets are overlapping. First: %s, Second: %s",
                            firstWidgetDesc, secondWidgetDesc);

                    Visualizer visualizer = new RegionsVisualizer(
                            getRedPen(),
                            getWidgetRegions(Arrays.asList(firstWidget, secondWidget)),
                            "Invariant Fault",
                            0.5, 0.5);

                    Verdict clashVerdict = new Verdict(
                            Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT,
                            verdictMsg,
                            visualizer);
                    finalVerdict = finalVerdict.join(clashVerdict);
                }
            }
        }

        return finalVerdict;
    }

    private String widgetDesc(Widget w) {
        String d = w.get(AndroidTags.AndroidResourceId, "");
        if (d.isEmpty())
            d = w.get(AndroidTags.AndroidAccessibilityId, "");
        if (d.isEmpty())
            d = w.get(AndroidTags.AndroidXpath, "");
        return d;
    }

    private boolean isAncestorOf(Widget ancestor, Widget widget) {
        Widget p = widget.parent();
        while (p != null) {
            if (p == ancestor)
                return true;
            p = p.parent();
        }
        return false;
    }

}

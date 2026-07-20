/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

import org.testar.core.alayer.Rect;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.core.visualizers.Visualizer;
import org.testar.oracle.Oracle;
import org.testar.webdriver.tag.WdTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebVisualLeafWidgetsOverlapOracle implements Oracle {

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = leafWidgetsOverlapping(state);

        if (!verdicts.isEmpty()) {
            return verdicts;
        }

        return Collections.singletonList(Verdict.OK);
    }

    private List<Verdict> leafWidgetsOverlapping(State state) {
        List<Verdict> overlapVerdicts = new ArrayList<Verdict>();
        List<Widget> leafWidgets = collectVisibleLeafWidgets(state);
        if (leafWidgets.size() > 1) {
            markAsNonVacuous();
        }

        for (int firstIndex = 0; firstIndex < leafWidgets.size(); firstIndex++) {
            Widget firstWidget = leafWidgets.get(firstIndex);
            Rect firstRect = (Rect) firstWidget.get(Tags.Shape, null);

            for (int secondIndex = firstIndex + 1; secondIndex < leafWidgets.size(); secondIndex++) {
                Widget secondWidget = leafWidgets.get(secondIndex);
                Rect secondRect = (Rect) secondWidget.get(Tags.Shape, null);

                if (Rect.overlap(firstRect, secondRect)) {
                    overlapVerdicts.add(overlapVerdict(firstWidget, secondWidget));
                }
            }
        }

        return overlapVerdicts;
    }

    private List<Widget> collectVisibleLeafWidgets(State state) {
        List<Widget> leafWidgets = new ArrayList<Widget>();

        for (Widget widget : state) {
            if (widget.get(WdTags.WebIsFullOnScreen, false)
                    && widget.childCount() < 1
                    && widget.get(Tags.Shape, null) instanceof Rect) {
                leafWidgets.add(widget);
            }
        }

        return leafWidgets;
    }

    private Verdict overlapVerdict(Widget firstWidget, Widget secondWidget) {
        String verdictMessage = String.format(
                "Two leaf widgets are overlapping. First: %s, Second: %s",
                firstWidget.get(WdTags.WebTextContent, ""),
                secondWidget.get(WdTags.WebTextContent, "")
        );

        Visualizer visualizer = new RegionsVisualizer(
                getRedPen(),
                getWidgetRegions(List.of(firstWidget, secondWidget)),
                "Invariant Fault",
                0.5,
                0.5
        );

        return new Verdict(
                Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT,
                verdictMessage,
                visualizer
        );
    }
}

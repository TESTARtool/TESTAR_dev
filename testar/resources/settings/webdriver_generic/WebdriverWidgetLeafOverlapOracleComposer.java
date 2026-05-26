/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

import java.util.ArrayList;
import java.util.List;

import org.testar.core.alayer.Color;
import org.testar.core.alayer.FillPattern;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Shape;
import org.testar.core.alayer.StrokePattern;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.core.visualizers.Visualizer;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.service.ScriptlessOracleComposer;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverWidgetLeafOverlapOracleComposer extends ScriptlessOracleComposer {

    private final ScriptlessOracleComposer delegate;

    public WebdriverWidgetLeafOverlapOracleComposer(ScriptlessOracleComposer delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Verdict> composeVerdicts(RuntimeContext runtimeContext, SUT system, State state, List<Verdict> verdicts) {
        List<Verdict> composedVerdicts = delegate.composeVerdicts(runtimeContext, system, state, verdicts);
        composedVerdicts.addAll(leafWidgetsOverlapping(state));
        return composedVerdicts;
    }

    private List<Verdict> leafWidgetsOverlapping(State state) {
        List<Verdict> overlapVerdicts = new ArrayList<Verdict>();
        List<Widget> leafWidgets = collectVisibleLeafWidgets(state);

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
                redPen(),
                getWidgetRegions(firstWidget, secondWidget),
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

    private Pen redPen() {
        return Pen.newPen()
                .setColor(Color.Red)
                .setFillPattern(FillPattern.None)
                .setStrokePattern(StrokePattern.Solid)
                .build();
    }

    private List<Shape> getWidgetRegions(Widget firstWidget, Widget secondWidget) {
        List<Shape> widgetRegions = new ArrayList<Shape>();
        addWidgetRegion(widgetRegions, firstWidget);
        addWidgetRegion(widgetRegions, secondWidget);
        return widgetRegions;
    }

    private void addWidgetRegion(List<Shape> widgetRegions, Widget widget) {
        Shape widgetShape = widget.get(Tags.Shape, null);
        if (widgetShape != null) {
            widgetRegions.add(widgetShape);
        }
    }
}

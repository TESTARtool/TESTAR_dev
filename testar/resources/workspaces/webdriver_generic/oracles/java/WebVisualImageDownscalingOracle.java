/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Roles;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.core.visualizers.Visualizer;
import org.testar.oracle.Oracle;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

/**
 * Detects raster images displayed below 40 percent of their natural width or
 * natural height.
 */
public class WebVisualImageDownscalingOracle implements Oracle {

    private static final long MINIMUM_IMAGE_WIDTH = 1L;
    private static final long MINIMUM_IMAGE_HEIGHT = 1L;
    private static final double MINIMUM_DISPLAY_SCALE = 0.40;

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<Verdict>();

        for (Widget widget : state) {
            if (isRelevantImage(widget)) {
                markAsNonVacuous();

                if (isScaledBelowMinimum(widget)) {
                    verdicts.add(downscalingVerdict(widget));
                }
            }
        }

        if (!verdicts.isEmpty()) {
            return verdicts;
        }

        return Collections.singletonList(Verdict.OK);
    }

    private boolean isRelevantImage(Widget widget) {
        String source = widget.get(WdTags.WebSrc, "");
        return widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdIMG)
                && !widget.get(WdTags.WebIsHidden, false)
                && !source.toLowerCase().contains("svg")
                && widget.get(Tags.Shape, null) instanceof Rect
                && hasMinimumSize(widget);
    }

    private boolean hasMinimumSize(Widget widget) {
        long naturalWidth = widget.get(WdTags.WebNaturalWidth, 0L);
        long naturalHeight = widget.get(WdTags.WebNaturalHeight, 0L);
        long displayedWidth = widget.get(WdTags.WebDisplayedWidth, 0L);
        long displayedHeight = widget.get(WdTags.WebDisplayedHeight, 0L);

        return naturalWidth >= MINIMUM_IMAGE_WIDTH
                && naturalHeight >= MINIMUM_IMAGE_HEIGHT
                && displayedWidth >= MINIMUM_IMAGE_WIDTH
                && displayedHeight >= MINIMUM_IMAGE_HEIGHT;
    }

    private boolean isScaledBelowMinimum(Widget widget) {
        return widthScale(widget) < MINIMUM_DISPLAY_SCALE
                || heightScale(widget) < MINIMUM_DISPLAY_SCALE;
    }

    private double widthScale(Widget widget) {
        return (double) widget.get(WdTags.WebDisplayedWidth, 0L)
                / widget.get(WdTags.WebNaturalWidth, 0L);
    }

    private double heightScale(Widget widget) {
        return (double) widget.get(WdTags.WebDisplayedHeight, 0L)
                / widget.get(WdTags.WebNaturalHeight, 0L);
    }

    private Verdict downscalingVerdict(Widget widget) {
        String verdictMessage = String.format(
                "Detected image displayed below 40%% of its natural size. Natural size: %d x %d px, displayed size: %d x %d px, width scale: %.2f%%, height scale: %.2f%%, src: %s, alt: %s",
                widget.get(WdTags.WebNaturalWidth, 0L),
                widget.get(WdTags.WebNaturalHeight, 0L),
                widget.get(WdTags.WebDisplayedWidth, 0L),
                widget.get(WdTags.WebDisplayedHeight, 0L),
                widthScale(widget) * 100.0,
                heightScale(widget) * 100.0,
                widget.get(WdTags.WebSrc, ""),
                widget.get(WdTags.WebAlt, "")
        );

        Visualizer visualizer = new RegionsVisualizer(
                getRedPen(),
                getWidgetRegions(Collections.singletonList(widget)),
                "Image Downscaling Fault",
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

/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.testar.core.Pair;
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
 * Test Oracle that checks whether visible data rows in native HTML tables or
 * div-based ARIA grids have duplicated content.
 */
public class WebInvariantDuplicatedRowsInAriaTable implements Oracle {

    public WebInvariantDuplicatedRowsInAriaTable() {}

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            if (!isTableContainer(widget)) {
                continue;
            }

            markAsNonVacuous();

            List<Pair<Widget, String>> rowDescriptions = new ArrayList<>();
            extractDataRowDescriptions(widget, widget, rowDescriptions);

            List<List<Pair<Widget, String>>> duplicatedDescriptions =
                    rowDescriptions.stream()
                    .filter(row -> !row.right().isEmpty())
                    .collect(Collectors.groupingBy(Pair::right))
                    .values().stream()
                    .filter(rows -> rows.size() > 1)
                    .collect(Collectors.toList());

            for (List<Pair<Widget, String>> duplicatedRows : duplicatedDescriptions) {
                String duplicatedDescription = duplicatedRows.get(0).right();
                String verdictMsg = String.format(
                        "Detected duplicated data row in a table/grid: %s",
                        duplicatedDescription);

                List<Widget> widgets = duplicatedRows.stream()
                        .map(Pair::left)
                        .collect(Collectors.toList());

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(widgets),
                        "Invariant Fault",
                        0.5, 0.5);

                verdicts.add(new Verdict(
                        Verdict.Severity.WARNING_WEB_INVARIANT_FAULT,
                        verdictMsg,
                        visualizer));
            }
        }

        if (!verdicts.isEmpty()) {
            return verdicts;
        }
        return Collections.singletonList(Verdict.OK);
    }

    /**
     * Recognizes both a native HTML table and the supplied div-based ARIA grid.
     */
    private boolean isTableContainer(Widget widget) {
        if (widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
            return true;
        }

        return widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdDIV)
                && "grid".equals(widget.get(WdTags.WebAriaRole, ""));
    }

    /**
     * Collects data rows only. Header rows are excluded because they contain
     * columnheader/TH descendants rather than gridcell/TD descendants.
     */
    private void extractDataRowDescriptions(
            Widget tableContainer,
            Widget widget,
            List<Pair<Widget, String>> rowDescriptions) {

        if (isDataRow(widget)) {
            String description = normalizeDescription(obtainWidgetTreeDescription(widget));
            rowDescriptions.add(new Pair<>(widget, description));
            return;
        }

        for (int i = 0; i < widget.childCount(); i++) {
            Widget child = widget.child(i);

            // Do not mix the rows of a nested table/grid with its parent.
            if (child != tableContainer && isTableContainer(child)) {
                continue;
            }

            extractDataRowDescriptions(tableContainer, child, rowDescriptions);
        }
    }

    private boolean isDataRow(Widget widget) {
        if (widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTR)) {
            return hasDescendantWithHtmlRole(widget, WdRoles.WdTD);
        }

        return widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdDIV)
                && "row".equals(widget.get(WdTags.WebAriaRole, ""))
                && hasDescendantWithAriaRole(widget, "gridcell");
    }

    private boolean hasDescendantWithHtmlRole(Widget widget, org.testar.core.alayer.Role role) {
        for (int i = 0; i < widget.childCount(); i++) {
            Widget child = widget.child(i);
            if (child.get(Tags.Role, Roles.Widget).equals(role)
                    || hasDescendantWithHtmlRole(child, role)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDescendantWithAriaRole(Widget widget, String ariaRole) {
        for (int i = 0; i < widget.childCount(); i++) {
            Widget child = widget.child(i);
            if (ariaRole.equals(child.get(WdTags.WebAriaRole, ""))
                    || hasDescendantWithAriaRole(child, ariaRole)) {
                return true;
            }
        }
        return false;
    }

    private String obtainWidgetTreeDescription(Widget widget) {
        StringBuilder description = new StringBuilder(widget.get(WdTags.WebTextContent, ""));

        for (int i = 0; i < widget.childCount(); i++) {
            description.append('_').append(obtainWidgetTreeDescription(widget.child(i)));
        }

        return description.toString();
    }

    private String normalizeDescription(String description) {
        return description
                .replaceAll("\\s+", " ")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "")
                .trim();
    }
}
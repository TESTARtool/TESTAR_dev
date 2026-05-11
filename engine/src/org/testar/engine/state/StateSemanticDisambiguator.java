/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

final class StateSemanticDisambiguator {

    List<SemanticWidgetProjection> disambiguate(List<SemanticWidgetProjection> projections) {
        Map<String, List<SemanticWidgetProjection>> projectionsByDescription = new LinkedHashMap<>();
        for (SemanticWidgetProjection projection : projections) {
            if (projection.description().isBlank()) {
                continue;
            }
            projectionsByDescription
                    .computeIfAbsent(projection.description(), key -> new ArrayList<>())
                    .add(projection);
        }

        for (List<SemanticWidgetProjection> duplicates : projectionsByDescription.values()) {
            if (duplicates.size() <= 1) {
                continue;
            }
            applyDescriptionDisambiguation(duplicates);
        }

        return projections;
    }

    private void applyDescriptionDisambiguation(List<SemanticWidgetProjection> duplicates) {
        Widget sharedParent = findLowestCommonAncestor(duplicates);
        duplicates.sort(Comparator
                .comparing(
                        (SemanticWidgetProjection projection) -> relativeIndexPath(sharedParent, projection.widget()),
                        StateSemanticDisambiguator::compareIndexPaths
                )
                .thenComparing(projection -> projection.widget().get(Tags.AbstractID, ""))
                .thenComparing(SemanticWidgetProjection::description));

        String sharedParentDescription = sharedParentDescription(sharedParent);
        for (int i = 0; i < duplicates.size(); i++) {
            SemanticWidgetProjection projection = duplicates.get(i);
            projection.setDescription(
                    projection.description() + disambiguationSuffix(sharedParentDescription, i + 1)
            );
        }
    }

    private Widget findLowestCommonAncestor(List<SemanticWidgetProjection> duplicates) {
        List<Widget> widgets = new ArrayList<>(duplicates.size());
        for (SemanticWidgetProjection projection : duplicates) {
            widgets.add(projection.widget());
        }

        List<Widget> sharedLineage = lineageFromRoot(widgets.get(0));
        for (int i = 1; i < widgets.size(); i++) {
            List<Widget> lineage = lineageFromRoot(widgets.get(i));
            int sharedLength = Math.min(sharedLineage.size(), lineage.size());
            int matchCount = 0;
            while (matchCount < sharedLength && sharedLineage.get(matchCount) == lineage.get(matchCount)) {
                matchCount++;
            }
            sharedLineage = new ArrayList<>(sharedLineage.subList(0, matchCount));
        }

        if (sharedLineage.isEmpty()) {
            return null;
        }

        return sharedLineage.get(sharedLineage.size() - 1);
    }

    private List<Widget> lineageFromRoot(Widget widget) {
        List<Widget> lineage = new ArrayList<>();
        Widget cursor = widget;
        while (cursor != null) {
            lineage.add(0, cursor);
            cursor = cursor.parent();
        }
        return lineage;
    }

    private List<Integer> relativeIndexPath(Widget sharedParent, Widget widget) {
        List<Integer> path = new ArrayList<>();
        Widget cursor = widget;
        while (cursor != null && cursor != sharedParent) {
            Widget parent = cursor.parent();
            if (parent == null) {
                break;
            }
            path.add(0, childIndex(parent, cursor));
            cursor = parent;
        }
        return path;
    }

    private int childIndex(Widget parent, Widget child) {
        for (int i = 0; i < parent.childCount(); i++) {
            if (parent.child(i) == child) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    private String sharedParentDescription(Widget sharedParent) {
        if (sharedParent == null || sharedParent == sharedParent.root()) {
            return "";
        }

        return sharedParent.get(Tags.Desc, "").trim();
    }

    private String disambiguationSuffix(String sharedParentDescription, int index) {
        if (!sharedParentDescription.isBlank()) {
            return " [within '" + sharedParentDescription + "' #" + index + "]";
        }
        return " [#" + index + "]";
    }

    private static int compareIndexPaths(List<Integer> left, List<Integer> right) {
        int length = Math.min(left.size(), right.size());
        for (int i = 0; i < length; i++) {
            int comparison = Integer.compare(left.get(i), right.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }
        return Integer.compare(left.size(), right.size());
    }

    static final class SemanticWidgetProjection {

        private String description;
        private final Widget widget;

        SemanticWidgetProjection(Widget widget, String description) {
            this.widget = widget;
            this.description = description;
        }

        Widget widget() {
            return widget;
        }

        String description() {
            return description;
        }

        void setDescription(String description) {
            this.description = description;
        }
    }
}

/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

final class ActionSemanticDisambiguator {

    void disambiguate(Set<Action> actions) {
        Map<String, List<Action>> actionsByDescription = new LinkedHashMap<>();
        for (Action action : actions) {
            String description = action.get(Tags.Desc, "");
            if (description.isBlank()) {
                continue;
            }
            actionsByDescription.computeIfAbsent(semanticKey(action), key -> new ArrayList<>()).add(action);
        }

        for (List<Action> duplicates : actionsByDescription.values()) {
            if (duplicates.size() <= 1) {
                continue;
            }
            applyDescriptionDisambiguation(duplicates);
        }
    }

    private void applyDescriptionDisambiguation(List<Action> duplicates) {
        List<ActionPosition> positions = new ArrayList<>(duplicates.size());
        List<Widget> widgets = new ArrayList<>(duplicates.size());

        for (Action action : duplicates) {
            Widget widget = action.get(Tags.OriginWidget, null);
            widgets.add(widget);
        }

        Widget sharedParent = findLowestCommonAncestor(widgets);
        for (Action action : duplicates) {
            Widget widget = action.get(Tags.OriginWidget, null);
            positions.add(new ActionPosition(action, relativeIndexPath(sharedParent, widget)));
        }

        positions.sort(Comparator
                .comparing(ActionPosition::path, ActionSemanticDisambiguator::compareIndexPaths)
                .thenComparing(position -> position.action().get(Tags.AbstractID, ""))
                .thenComparing(position -> position.action().toShortString()));

        String sharedParentDescription = sharedParentDescription(sharedParent);
        for (int i = 0; i < positions.size(); i++) {
            Action action = positions.get(i).action();
            String description = action.get(Tags.Desc, "");
            action.set(Tags.Desc, description + disambiguationSuffix(sharedParentDescription, i + 1));
        }
    }

    private String semanticKey(Action action) {
        String description = action.get(Tags.Desc, "");
        String inputText = action.get(Tags.InputText, "");
        if (inputText.isBlank()) {
            return description;
        }
        return description.replace(inputText, "<input>");
    }

    private Widget findLowestCommonAncestor(List<Widget> widgets) {
        if (widgets.isEmpty() || widgets.contains(null)) {
            return null;
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

        Widget lowestCommonAncestor = sharedLineage.get(sharedLineage.size() - 1);
        if (allWidgetsIdentical(widgets) && lowestCommonAncestor.parent() != null) {
            return lowestCommonAncestor.parent();
        }

        return lowestCommonAncestor;
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

    private boolean allWidgetsIdentical(List<Widget> widgets) {
        Widget first = widgets.get(0);
        for (int i = 1; i < widgets.size(); i++) {
            if (widgets.get(i) != first) {
                return false;
            }
        }
        return true;
    }

    private List<Integer> relativeIndexPath(Widget sharedParent, Widget widget) {
        List<Integer> path = new ArrayList<>();
        if (widget == null) {
            return path;
        }

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

    private record ActionPosition(Action action, List<Integer> path) {
    }
}

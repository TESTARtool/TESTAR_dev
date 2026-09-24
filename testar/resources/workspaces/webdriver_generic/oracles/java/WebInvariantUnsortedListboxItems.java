/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

import java.text.Collator;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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

public class WebInvariantUnsortedListboxItems implements Oracle {

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            if (!isListbox(widget) || widget.childCount() <= 1) {
                continue;
            }

            List<String> listItemTexts = listItemTexts(widget);
            if (listItemTexts.size() <= 1) {
                continue;
            }

            markAsNonVacuous();

            if (!isSorted(listItemTexts)) {
                String verdictMessage = String.format(
                    "Detected Unordered List (UL) widget %s with unsorted list items: %s",
                    getDescriptionOfWidgets(Collections.singletonList(widget), WdTags.WebId),
                    listItemTexts
                );

                Visualizer visualizer = new RegionsVisualizer(
                    getRedPen(),
                    getWidgetRegions(Collections.singletonList(widget)),
                    "Invariant Fault",
                    0.5,
                    0.5
                );

                verdicts.add(new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMessage, visualizer));
            }
        }

        if (!verdicts.isEmpty()) {
            return verdicts;
        }

        return Collections.singletonList(Verdict.OK);
    }

    private List<String> listItemTexts(Widget widget) {
        List<String> listItemTexts = new ArrayList<>();

        for (int index = 0; index < widget.childCount(); index++) {
            Widget child = widget.child(index);
            if (!WdRoles.WdLI.equals(child.get(Tags.Role, Roles.Widget))) {
                continue;
            }

            String itemText = widgetText(child).trim();
            if (!itemText.isEmpty()) {
                listItemTexts.add(itemText);
            }
        }

        return listItemTexts;
    }

    private boolean isListbox(Widget widget) {
        return WdRoles.WdUL.equals(widget.get(Tags.Role, Roles.Widget))
            && "listbox".equalsIgnoreCase(widget.get(WdTags.WebAriaRole, ""));
    }

    private String widgetText(Widget widget) {
        String text = widget.get(WdTags.WebTextContent, "").trim();
        if (!text.isEmpty()) {
            return text;
        }

        List<String> descendantTexts = new ArrayList<>();
        for (int index = 0; index < widget.childCount(); index++) {
            String childText = widgetText(widget.child(index));
            if (!childText.isEmpty()) {
                descendantTexts.add(childText);
            }
        }

        return String.join(" ", descendantTexts).trim();
    }

    private static boolean isSorted(List<String> values) {
        return isNaturallySorted(values)
            || isCollatorSorted(values)
            || isMonthSorted(values);
    }

    private static boolean isNaturallySorted(List<String> values) {
        return isSortedBy(values, Comparator.naturalOrder());
    }

    private static boolean isCollatorSorted(List<String> values) {
        Collator collator = Collator.getInstance(Locale.US);
        collator.setStrength(Collator.PRIMARY);
        return isSortedBy(values, collator::compare);
    }

    private static boolean isMonthSorted(List<String> values) {
        try {
            return isSortedBy(values, Comparator.comparing(value -> Month.valueOf(value.toUpperCase(Locale.US))));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static boolean isSortedBy(List<String> values, Comparator<String> comparator) {
        for (int index = 1; index < values.size(); index++) {
            if (comparator.compare(values.get(index - 1), values.get(index)) > 0) {
                return false;
            }
        }

        return true;
    }
}

/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WdConstants {

    private WdConstants() {}

    // List of default HTML tags that getStateTreeTestar should ignore
    // no widgets can be found here
    // this list can be updated by users using the "WebIgnoredTags" setting
    private static List<String> ignoredTags = Collections.emptyList();

    public static List<String> getIgnoredTags() {
        return Collections.unmodifiableList(ignoredTags);
    }

    public static void setIgnoredTags(Collection<String> values) {
        ignoredTags = sanitize(values);
    }

    // List of default web attributes to ignore when obtaining the getStateTreeTestar
    // These can be ignored to reduce state high-performance workloads
    // based on "webdriver/resources/web-extension/js/testar.state.js"
    // this list can be updated by users using the "WebIgnoredAttributes" setting
    private static List<String> ignoredAttributes = Collections.emptyList();

    public static List<String> getIgnoredAttributes() {
        return Collections.unmodifiableList(ignoredAttributes);
    }

    public static void setIgnoredAttributes(Collection<String> values) {
        ignoredAttributes = sanitize(values);
    }

    // Disable the state-canvas
    private static List<String> hiddenTags = new ArrayList<>(Arrays.asList("canvas"));

    public static List<String> getHiddenTags() {
        return Collections.unmodifiableList(hiddenTags);
    }

    // element.offsetWidth - element.clientWidth
    public static double scrollArrowSize = 36;
    public static double scrollThick = 15;

    /**
     * Sanitize a collection of strings:
     * - null returns empty list
     * - trims, removes nulls/blanks
     * - deduplicates preserving order
     */
    private static List<String> sanitize(Collection<String> input) {
        if (input == null) {
            return Collections.emptyList();
        }

        List<String> cleaned = input.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        return cleaned.isEmpty() ? Collections.emptyList() : cleaned;
    }
}

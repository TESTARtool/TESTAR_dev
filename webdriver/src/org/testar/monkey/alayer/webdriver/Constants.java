/**
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2025 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

package org.testar.monkey.alayer.webdriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Constants {

    private Constants() {}

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

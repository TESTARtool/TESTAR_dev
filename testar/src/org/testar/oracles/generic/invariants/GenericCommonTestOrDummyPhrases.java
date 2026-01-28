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

package org.testar.oracles.generic.invariants;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;

public class GenericCommonTestOrDummyPhrases implements Oracle {

    private final Pattern dummyPattern;
    private final Tag<String> textTag;

    public GenericCommonTestOrDummyPhrases() {
        this("(?i)\\b(dummy|test|debug)\\b");
    }

    public GenericCommonTestOrDummyPhrases(String dummyRegex) {
        this(dummyRegex, detectDefaultTextTag());
    }

    public GenericCommonTestOrDummyPhrases(String dummyRegex, Tag<String> textTag) {
        this.dummyPattern = Pattern.compile(dummyRegex);
        this.textTag = textTag;
    }

    private static Tag<String> detectDefaultTextTag() {
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
            return WdTags.WebTextContent;
        } else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.ANDROID)) {
            return AndroidTags.AndroidText;
        } else {
            return Tags.Title;
        }
    }

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String desc = w.get(textTag, "");
            if (dummyPattern.matcher(desc).find()) {

                String verdictMsg = String.format(
                        "Detected debug or test data values for widget role '%s' with text '%s'",
                        w.get(Tags.Role), w.get(textTag, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Arrays.asList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict dummyVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(dummyVerdict);
            }
        }

        return finalVerdict;
    }

}

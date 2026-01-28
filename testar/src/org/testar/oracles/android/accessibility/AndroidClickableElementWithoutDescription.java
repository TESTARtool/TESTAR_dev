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

package org.testar.oracles.android.accessibility;

import java.util.Arrays;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;

public class AndroidClickableElementWithoutDescription implements Oracle {

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            if (w.get(AndroidTags.AndroidClickable, false)
                    && w.get(AndroidTags.AndroidDisplayed, false)
                    && w.get(AndroidTags.AndroidAccessibilityId, "").trim().isEmpty()
                    && w.get(AndroidTags.AndroidResourceId, "").trim().isEmpty()
                    && w.get(AndroidTags.AndroidText, "").trim().isEmpty()
                    && w.get(AndroidTags.AndroidHint, "").trim().isEmpty()) {

                String verdictMsg = String.format("Detected Clickable widget without any description: %s",
                        w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Arrays.asList(w)),
                        "Accessibility Fault",
                        0.5, 0.5);

                Verdict accessibilityVerdict = new Verdict(
                        Verdict.Severity.WARNING_ACCESSIBILITY_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(accessibilityVerdict);
            }
        }

        return finalVerdict;
    }

}

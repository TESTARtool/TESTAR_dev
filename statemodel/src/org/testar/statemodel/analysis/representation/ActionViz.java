/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.statemodel.analysis.representation;

public class ActionViz {

    private String screenshotSource;

    private String screenshotTarget;

    private String actionDescription;

    private int counterSource;

    private int counterTarget;

    private boolean deterministic;

    public ActionViz(String screenshotSource, String screenshotTarget, String actionDescription, int counterSource, int counterTarget, boolean deterministic) {
        this.screenshotSource = screenshotSource;
        this.screenshotTarget = screenshotTarget;
        this.actionDescription = actionDescription;
        this.counterSource = counterSource;
        this.counterTarget = counterTarget;
        this.deterministic = deterministic;
    }

    public String getScreenshotSource() {
        return screenshotSource;
    }

    public String getScreenshotTarget() {
        return screenshotTarget;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public int getCounterSource() {
        return counterSource;
    }

    public int getCounterTarget() {
        return counterTarget;
    }

    public boolean isDeterministic() {
        return deterministic;
    }
}

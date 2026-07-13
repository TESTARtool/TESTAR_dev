/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 - 2026 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.monkey.alayer.android.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;

public class AndroidActionScroll extends TaggableBase implements Action {

    private static final long serialVersionUID = 6205133391190145934L;

    private static final int SCROLL_DISTANCE = 500;
    private final String accessibilityId;
    private final Widget widget;
    private final String widgetClass;
    private final String text;
    private final String xpath;

    public AndroidActionScroll(State state, Widget w) {
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.mapOriginWidget(w);
        this.accessibilityId = w.get(AndroidTags.AndroidAccessibilityId, "");
        this.widget = w;
        this.widgetClass = w.get(AndroidTags.AndroidClassName, "");
        this.text = w.get(AndroidTags.AndroidText, "");
        this.xpath = w.get(AndroidTags.AndroidXpath, "");
        this.set(Tags.Desc, toShortString());
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            AndroidAppiumFramework.scrollElementById(this.accessibilityId, this.widget, SCROLL_DISTANCE);
        } catch(Exception e) {
            System.out.println("Exception trying to scroll Element By Id : " + this.accessibilityId);
            System.out.println(e.getMessage());
            throw new ActionFailedException(toShortString());
        }
    }

    @Override
    public String toShortString() {
        return "Execute Android Scroll on the system under test";
    }

    @Override
    public String toParametersString() {
        String widgetConcreteId = this.widget.get(Tags.ConcreteID, "NoWidgetConcreteIdAvailable");
        return "role=" + this.get(Tags.Role, AndroidRoles.AndroidWidget)
                + ",widget=" + widgetConcreteId
                + ",widgetClass=" + this.widgetClass
                + ",text=" + this.text
                + ",accessibilityId=" + this.accessibilityId
                + ",xpath=" + this.xpath
                + ",scrollDistance=" + SCROLL_DISTANCE;
    }

    @Override
    public String toString(Role... discardParameters) {
        return this.toParametersString();
    }

    public Widget getWidget(){
        return this.widget;
    }
}

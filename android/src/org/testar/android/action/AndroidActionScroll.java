/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.action;

import org.testar.android.AndroidAppiumFramework;
import org.testar.android.alayer.AndroidRoles;
import org.testar.android.tag.AndroidTags;
import org.testar.core.alayer.*;
import org.testar.core.action.Action;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;

public class AndroidActionScroll extends TaggableBase implements Action {

    private static final long serialVersionUID = 6205133391190145934L;

    private final int scrollDistance = 500;
    private final String accessibilityId;
    private final Widget widget;

    public AndroidActionScroll(State state, Widget w) {
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.mapOriginWidget(w);
        this.accessibilityId = w.get(AndroidTags.AndroidAccessibilityId, "");
        this.widget = w;
        this.set(Tags.Desc, toShortString());
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            AndroidAppiumFramework.scrollElementById(this.accessibilityId, this.widget, this.scrollDistance);
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
        return "";
    }

    @Override
    public String toString(Role... discardParameters) {
        return "";
    }

    public Widget getWidget(){
        return widget;
    }
}

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

public class AndroidActionLongClick extends TaggableBase implements Action {

    private static final long serialVersionUID = -3768652886994573288L;

    private final String accessibilityId;
    private final Widget widget;
    private final String widgetClass;
    private final String xpath;
    private final String text;

    public AndroidActionLongClick(State state, Widget w) {
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.mapOriginWidget(w);
        this.accessibilityId = w.get(AndroidTags.AndroidAccessibilityId, "");
        this.widget = w;
        this.widgetClass = w.get(AndroidTags.AndroidClassName);
        this.xpath = w.get(AndroidTags.AndroidXpath);
        this.text = w.get(AndroidTags.AndroidText);
        this.set(Tags.Desc, toShortString());

    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            AndroidAppiumFramework.longClickElementById(this.accessibilityId, this.widget);
        } catch(Exception e) {
            System.out.println("Exception trying to LongClick Element By Id : " + this.accessibilityId);
            System.out.println(e.getMessage());
            throw new ActionFailedException(toShortString());
        }
    }

    @Override
    public String toShortString() {
        return "Execute Android Longclick on Widget of type: '" + this.widgetClass + "', with text: '" + this.text + "', with Id: '" + this.accessibilityId + "', with xPath: " + this.xpath;
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

/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.action;

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

/**
 * TODO: currently not working.
 */
public class AndroidActionPinch extends TaggableBase implements Action {

    private final String accessibilityId;
    private final Widget widget;
    private final boolean zoomIn;

    public AndroidActionPinch(State state, Widget w, boolean zoomIn) {
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.mapOriginWidget(w);
        this.accessibilityId = w.get(AndroidTags.AndroidAccessibilityId, "");
        this.widget = w;
        this.set(Tags.Desc, toShortString());
        this.zoomIn = zoomIn;

    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        //TODO Implement pinch (zoomin and zoomout)
//        int xValue = (int)(widget.get(AndroidTags.AndroidBounds).width()/2.0);
//        int yValue = (int)(widget.get(AndroidTags.AndroidBounds).height()/2.0);
//        Point center = new Point(xValue, yValue);
//        int distance = 300;
//        if (zoomIn) {
//            // Pinch for zooming in
//            try {
//                AndroidAppiumFramework.zoomIn(center, distance);
//            } catch(Exception e) {
//                System.out.println("Exception trying to zoom in Element By Id : " + this.accessibilityId);
//                System.out.println(e.getMessage());
//                throw new ActionFailedException(toShortString());
//            }
//
//        } else {
//            // Pinch for zooming out
//            try {
//                AndroidAppiumFramework.zoomOut(center, distance);
//            } catch(Exception e) {
//                System.out.println("Exception trying to zoom out Element By Id : " + this.accessibilityId);
//                System.out.println(e.getMessage());
//                throw new ActionFailedException(toShortString());
//            }
//        }
    }



    @Override
    public String toShortString() {
        return "Execute Android pinch in Widget: " + widget.get(AndroidTags.AndroidClassName) + " with Id: " + this.accessibilityId;
    }

    @Override
    public String toParametersString() {
        return "";
    }

    @Override
    public String toString(Role... discardParameters) {
        return "";
    }
}

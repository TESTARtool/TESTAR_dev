/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
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
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;

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

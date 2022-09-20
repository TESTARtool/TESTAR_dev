package org.testar.monkey.alayer.android.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;

public class AndroidActionPinch extends TaggableBase implements Action {
    //TODO: currently not working.

    private final String accessibilityId;
    private final Widget widget;
    private final boolean zoomIn;

    public AndroidActionPinch(State state, Widget w, String accessibilityID, boolean zoomIn) {
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.set(Tags.OriginWidget, w);
        this.accessibilityId = accessibilityID;
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

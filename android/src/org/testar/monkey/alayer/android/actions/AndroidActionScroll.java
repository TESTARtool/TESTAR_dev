package org.testar.monkey.alayer.android.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.enums.AndroidRoles;

public class AndroidActionScroll extends TaggableBase implements Action {

    private final int scrollDistance = 500;
    private final String accessibilityId;
    private final Widget widget;

    public AndroidActionScroll(State state, Widget w, String accessibilityID) {
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.set(Tags.OriginWidget, w);
        this.accessibilityId = accessibilityID;
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

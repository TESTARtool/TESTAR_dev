package org.testar.monkey.alayer.android.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.enums.AndroidRoles;

public class AndroidSystemActionCall extends TaggableBase implements Action {

    private final Widget widget;

    public AndroidSystemActionCall(State state, Widget widget) {
        this.widget = widget;
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.set(Tags.OriginWidget, widget);
        this.set(Tags.Desc, toShortString());
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            AndroidAppiumFramework.generatePhoneCall();
        } catch(Exception e) {
            System.out.println("Exception trying to generate phone call on SUT: ");
            System.out.println(e.getMessage());
            throw new ActionFailedException(toShortString());
        }
    }

    @Override
    public String toShortString() {
        return "Execute Android system event: Call";
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


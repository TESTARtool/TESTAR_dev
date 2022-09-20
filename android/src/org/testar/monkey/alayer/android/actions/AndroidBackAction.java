package org.testar.monkey.alayer.android.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.enums.AndroidRoles;

public class AndroidBackAction extends TaggableBase implements Action {

    public final Widget widget;

    public AndroidBackAction(State state, Widget widget) {
        this.widget = widget;
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.set(Tags.OriginWidget, widget);
        this.set(Tags.Desc, toShortString());
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            AndroidAppiumFramework.clickBackButton();
        } catch(Exception e) {
            System.out.println("Exception trying to click Android back button: ");
            System.out.println(e.getMessage());
            throw new ActionFailedException(toShortString());
        }
    }

    @Override
    public String toShortString() {
        return "Execute click Android back button";
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

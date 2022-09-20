package org.testar.monkey.alayer.android.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;

public class AndroidActionLongClick extends TaggableBase implements Action {

    private final String accessibilityId;
    private final Widget widget;
    private final String widgetClass;
    private final String xpath;
    private final String text;

    public AndroidActionLongClick(State state, Widget w, String accessibilityID) {
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.set(Tags.OriginWidget, w);
        this.accessibilityId = accessibilityID;
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

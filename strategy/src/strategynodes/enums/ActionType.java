package strategynodes.enums;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.actions.WdActionRoles;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

import java.util.*;

public enum ActionType
{
    CLICK ("click-action"),
    TYPING ("type-action"),
    DRAG ("drag-action"),
    SCROLL ("scroll-action"),
    HIT_KEY ("hit-key-action"),
    FORM_INPUT("form-input-action"),
    FORM_FIELD ("form-field-action"),
    FORM_SUBMIT("form-submit-action");

    public final String string;
    private static final Map<String, ActionType> STRINGS = new HashMap<>(); //lookup

    ActionType(String plainText) //constructor
    { this.string = plainText; }

    static //fill lookup with string values
    { Arrays.stream(values()).forEach(e -> STRINGS.put(e.string, e)); }

    // conversion
    public static ActionType toEnum(String string) {return STRINGS.get(string);}
    public String toString() {return this.string;}

    // comparers
    public boolean actionIsThisType(Action action)
    { return this == getActionType(action); }
    public static ActionType getActionType(Action action)
    {
        if (        action.get(Tags.Role,  null) == ActionRoles.ClickAt ||
                action.get(Tags.Role, null) == ActionRoles.LeftClickAt)
            return ActionType.CLICK;
        else if (   action.get(Tags.Role,  null) == ActionRoles.Type ||
                action.get(Tags.Role, null) == ActionRoles.ClickTypeInto ||
                action.get(Tags.Role, null) == ActionRoles.ClickAndTypeText ||
                action.get(Tags.Role, null) == ActionRoles.PasteTextInto)
            return ActionType.TYPING;
        else if (   action.get(Tags.Role,  ActionRoles.Action) == ActionRoles.Drag ||
                action.get(Tags.Role, ActionRoles.Action) == ActionRoles.LeftDrag)
            return ActionType.DRAG;
        else if (   action.get(Tags.Slider, null) != null ||
                action.get(Tags.Role,  ActionRoles.Action) == ActionRoles.HitKeyScrollDownAction)
            return ActionType.SCROLL;
        else if (   action.get(Tags.Role,  ActionRoles.Action) == ActionRoles.HitKey)
            return ActionType.HIT_KEY;
        else if (   isFormInput(action))
            return ActionType.FORM_INPUT;
        else if (   isFormField(action))
            return ActionType.FORM_FIELD;
        else if (   isFormSubmit(action))
            return ActionType.FORM_SUBMIT;
        else
            return null;
    }
    private static boolean isFormInput(Action action)
    {
        if(action.get(Tags.OriginWidget,null) == null)
            return false;
        Widget originWidget = action.get(Tags.OriginWidget, null);
        if(!isChildOfFormWidget(originWidget))
            return false;
        return originWidget.get(Tags.Role, ActionRoles.Action).equals(WdRoles.WdINPUT);
    }
    private static boolean isFormField(Action action)
    {
        if(action.get(Tags.OriginWidget,null) == null)
            return false;
        Widget originWidget = action.get(Tags.OriginWidget, null);
        if(!isChildOfFormWidget(originWidget))
            return false;
        else
        {
            // Some web SUT like Parabank contains Select dropdown elements in the form
            if(action.get(Tags.Role, ActionRoles.Action).equals(WdActionRoles.SelectListAction))
                return true;
                // Some web SUT like Parabank contains text area elements in the form
            else if(originWidget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTEXTAREA))
                return true;
        }
        return
                (
                    originWidget.get(Tags.Role, ActionRoles.Action).equals(WdRoles.WdINPUT) &&
                    (!originWidget.get(WdTags.WebType, "").equalsIgnoreCase("submit")) //ignore submit
                );
    }
    private static boolean isFormSubmit(Action action)
    {
        if(action.get(Tags.OriginWidget,null) == null)
            return false;
        Widget originWidget = action.get(Tags.OriginWidget, null);
        if(!isChildOfFormWidget(originWidget))
            return false;
        return originWidget.get(WdTags.WebType, "").equalsIgnoreCase("submit");
    }
    private static boolean isChildOfFormWidget(Widget widget)
    {
        if(widget.parent() == null) return false;
        else if (widget.parent().get(Tags.Role, Roles.Widget).equals(WdRoles.WdFORM)) return true;
        else return isChildOfFormWidget(widget.parent());
    }
}

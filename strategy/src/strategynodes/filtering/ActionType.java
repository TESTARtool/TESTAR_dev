package strategynodes.filtering;

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
    CLICK ("click-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return (
                            action.get(Tags.Role,  null) == ActionRoles.ClickAt ||
                            action.get(Tags.Role, null) == ActionRoles.LeftClickAt);
                }
            },
    TYPING ("type-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return (
                            action.get(Tags.Role,  null) == ActionRoles.Type ||
                            action.get(Tags.Role, null) == ActionRoles.ClickTypeInto ||
                            action.get(Tags.Role, null) == ActionRoles.ClickAndTypeText ||
                            action.get(Tags.Role, null) == ActionRoles.PasteTextInto);
                }
            },
    DRAG ("drag-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return (
                            action.get(Tags.Role,  ActionRoles.Action) == ActionRoles.Drag ||
                            action.get(Tags.Role, ActionRoles.Action) == ActionRoles.LeftDrag);
                }
            },
    SCROLL ("scroll-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return (
                            action.get(Tags.Slider, null) != null ||
                            action.get(Tags.Role,  ActionRoles.Action) == ActionRoles.HitKeyScrollDownAction
                    );
                }
            },
    HIT_KEY ("hit-key-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return action.get(Tags.Role,  ActionRoles.Action) == ActionRoles.HitKey;
                }
            },
    FORM_INPUT("form-input-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    if(action.get(Tags.OriginWidget,null) == null)
                        return false;
                    Widget originWidget = action.get(Tags.OriginWidget, null);
                    if(!isChildOfFormWidget(originWidget))
                        return false;
                    return originWidget.get(Tags.Role, ActionRoles.Action).equals(WdRoles.WdINPUT);
                }
            },
    FORM_FIELD ("form-field-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    if(action.get(Tags.OriginWidget,null) == null)
                        return false;
                    Widget originWidget = action.get(Tags.OriginWidget, null);
                    if(!isChildOfFormWidget(originWidget))
                        return false;
                    if(isChildOfFormWidget(originWidget))
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
                                (!originWidget.get(WdTags.WebType, "").equalsIgnoreCase("submit"))
                            );
                }
            },
    FORM_SUBMIT("form-submit-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    if(action.get(Tags.OriginWidget,null) == null)
                        return false;
                    Widget originWidget = action.get(Tags.OriginWidget, null);
                    if(!isChildOfFormWidget(originWidget))
                        return false;
                    return originWidget.get(WdTags.WebType, "").equalsIgnoreCase("submit");
                }
            };

    public abstract boolean actionIsThisType(Action action);


    public final String string;
    private static final Map<String, ActionType> FROM_STRING = new HashMap<>();


    ActionType(String plainText)                      {this.string = plainText;}
    public static ActionType toEnum(String plainText) {return FROM_STRING.get(plainText);}
    public String toString() {return this.string;}


    static
    { Arrays.stream(values()).forEach(e -> FROM_STRING.put(e.string, e)); }

    private static boolean isChildOfFormWidget(Widget widget)
    {
        if(widget.parent() == null) return false;
        else if (widget.parent().get(Tags.Role, Roles.Widget).equals(WdRoles.WdFORM)) return true;
        else return isChildOfFormWidget(widget.parent());
    }
}

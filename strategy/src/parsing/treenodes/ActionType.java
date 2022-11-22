package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.ActionRoles;
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
    INPUT ("input-action"),
    SUBMIT ("submit-action");
    
    public final String plainText;
    private static final Map<String, ActionType> FROM_PLAIN_TEXT = new HashMap<>();
    
    private ActionType(String plainText) {this.plainText = plainText;}
    public static ActionType stringToEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (ActionType e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
    
    public static boolean RoleMatchesType(Action action, ActionType actionType)
    {
        switch(actionType)
        {
            case CLICK:
                if(action.get(Tags.Role,  null) == ActionRoles.ClickAt || action.get(Tags.Role, null) == ActionRoles.LeftClickAt)
                    return true;
                break;
            case TYPING:
                if(action.get(Tags.Role,  null) == ActionRoles.Type || action.get(Tags.Role, null) == ActionRoles.ClickTypeInto)
                    return true;
                break;
            case DRAG:
                if(action.get(Tags.Role,  null) == ActionRoles.Drag || action.get(Tags.Role, null) == ActionRoles.LeftDrag)
                    return true;
                break;
            case SCROLL:
                if(action.get(Tags.Slider, null) != null)
                    return true;
                break;
            case HIT_KEY:
                if(action.get(Tags.Role,  null) == ActionRoles.HitKey)
                    return true;
                break;
            case INPUT:
                if(action.get(Tags.OriginWidget,null).get(Tags.Role,  null).equals(WdRoles.WdINPUT))
                    return true;
            case SUBMIT:
                if((action.get(Tags.OriginWidget,null).get(Tags.Role) == WdRoles.WdINPUT ||
                    action.get(Tags.OriginWidget,null).get(Tags.Role) == WdRoles.WdBUTTON) &&
                   action.get(Tags.OriginWidget,null).get(WdTags.WebType, "").equalsIgnoreCase("submit"))
                    return true;
        }
        return false;
    }
}

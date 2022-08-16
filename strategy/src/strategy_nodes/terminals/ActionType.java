package strategy_nodes.terminals;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.ActionRoles;

import java.util.*;

public enum ActionType
{
    NONE("none"),
    CLICK ("click-action"),
    TYPING ("type-action"),
    DRAG ("drag-action"),
    SCROLL ("scroll-action"),
    HIT_KEY ("hit-key-action"),
    INPUT ("input-action");
    
    public final String label;
    private static final Map<String, ActionType> BY_LABEL = new HashMap<>();
    
    private ActionType(String label) {this.label = label;}
    public static ActionType valueOfLabel(String label) {return BY_LABEL.get(label);}
    
    static {for (ActionType e: values()) {BY_LABEL.put(e.label, e);}}
    
    public String toString() {return this.label;}
    
    public static boolean RoleMatchesType(Action action, ActionType actionType)
    {
        switch(actionType)
        {
            case NONE:
                break;
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
            case INPUT: //todo
                return false;
//                if(action.get(Tags.Role,  null) == ActionRoles.HitKey)
//                    return true;
//                break;
        }
        return false;
    }
}

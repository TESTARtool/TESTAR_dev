package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.ActionRoles;
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
                            action.get(Tags.Role, null) == ActionRoles.ClickTypeInto);
                }
            },
    DRAG ("drag-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return (
                            action.get(Tags.Role,  null) == ActionRoles.Drag ||
                            action.get(Tags.Role, null) == ActionRoles.LeftDrag);
                }
            },
    SCROLL ("scroll-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return (action.get(Tags.Slider, null) != null);
                }
            },
    HIT_KEY ("hit-key-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return (
                            action.get(Tags.Role,  null) == ActionRoles.HitKey);
                }
            },
    INPUT ("input-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return (action.get(Tags.OriginWidget,null).get(Tags.Role,  null).equals(WdRoles.WdINPUT));
                }
            },
    SUBMIT ("submit-action")
            {
                @Override
                public boolean actionIsThisType(Action action)
                {
                    return (action.get(Tags.OriginWidget,null).get(WdTags.WebType, "").equalsIgnoreCase("submit") &&
                            (action.get(Tags.OriginWidget,null).get(Tags.Role) == WdRoles.WdINPUT ||
                             action.get(Tags.OriginWidget,null).get(Tags.Role) == WdRoles.WdBUTTON));
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
}

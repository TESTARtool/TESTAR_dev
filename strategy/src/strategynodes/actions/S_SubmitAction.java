package strategynodes.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.WdSubmitAction;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import strategynodes.basenodes.BaseActionNode;

import java.util.Map;
import java.util.Set;

public class S_SubmitAction extends BaseActionNode
{
    public S_SubmitAction(int weight, String name)
    {
        this.name = name;
        this.WEIGHT = weight;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo
    {
        for(Action action : actions)
        {
//            if(action instanceof WdSubmitAction) //if it is a submit action
//                return action;
            Widget widget = action.get(Tags.OriginWidget);
            if((widget.get(Tags.Role) == WdRoles.WdINPUT || widget.get(Tags.Role) == WdRoles.WdBUTTON) &&
               widget.get(WdTags.WebType, "").equalsIgnoreCase("submit"))
                return action;
        }
        return selectRandomAction(actions); // if there is no submit action, pick randomly
    }
    
    @Override
    public String toString() {return name;}
}
//    private static Boolean isSubmitButton(Widget submit_widget){
//        Role[] roles = new Role[]{WdRoles.WdINPUT, WdRoles.WdBUTTON};
//        return Role.isOneOf(submit_widget.get(Tags.Role, Roles.Widget), roles) && submit_widget.get(WdTags.WebType, "").equalsIgnoreCase("submit");
//    }
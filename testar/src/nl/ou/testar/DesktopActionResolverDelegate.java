package nl.ou.testar;

import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;

import java.util.Set;

public interface DesktopActionResolverDelegate extends ActionResolverDelegate {
    Action preSelectAction(State state, Set<Action> actions);
    DerivedActions deriveClickTypeScrollActionsFromAllWidgets(Set<Action> actions, State state);
    DerivedActions deriveClickTypeScrollActionsFromTopLevelWidgets(Set<Action> actions, State state);
    @Deprecated Set<Action> deriveClickTypeScrollActionsFromAllWidgetsOfState(Set<Action> actions, SUT system, State state);
    @Deprecated Set<Action> deriveClickTypeScrollActionsFromTopLevelWidgets(Set<Action> actions, SUT system, State state);
}

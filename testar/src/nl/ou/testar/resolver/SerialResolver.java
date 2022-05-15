package nl.ou.testar.resolver;

import com.google.common.collect.ImmutableSet;
import nl.ou.testar.ActionResolver;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.Settings;

import java.util.Set;

public abstract class SerialResolver<S> implements ActionResolver {

    private ActionResolver nextResolver;

    protected S service;
    protected Settings settings;
    protected Action selectedAction;

    public SerialResolver(S service, Settings settings) {
        this.service = service;
        this.settings = settings;
    }

    public abstract void startReplay(String reportTag) throws Exception;

    protected abstract Action nextAction(SUT system, State state);

    @Override
    public Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        Action selectedAction = nextAction(system, state);
        if (selectedAction == null) {
            return null;
        }
        return ImmutableSet.of(selectedAction);
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        // A single action supposed here
        if (actions == null || actions.size() == 0) {
            return null;
        }
        if (actions.size() > 1) {
            return nextResolver.selectAction(state, actions);
        }
        return actions.iterator().next();
    }

    @Override
    public ActionResolver nextResolver() {
        return nextResolver;
    }

    @Override
    public void setNextResolver(ActionResolver nextResolver) {
        this.nextResolver = nextResolver;
    }
}

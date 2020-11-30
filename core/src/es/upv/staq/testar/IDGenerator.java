package es.upv.staq.testar;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Widget;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public interface IDGenerator {
    public void buildIDs(Widget widget);
    public void buildIDs(State state, Set<Action> actions);
    public void buildIDs(State state, Action action);
    public void buildEnvironmentActionIDs(State state, Action action);
    public String getAbstractStateModelHash(String applicationName, String applicationVersion);
}

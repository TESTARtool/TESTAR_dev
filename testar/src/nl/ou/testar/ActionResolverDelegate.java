package nl.ou.testar;

import org.testar.monkey.alayer.Widget;
import org.testar.statemodel.StateModelManager;

public interface ActionResolverDelegate {
    StateModelManager getStateModelManager();
    boolean blackListed(Widget w);
    boolean whiteListed(Widget w);
}

package nl.ou.testar;

import nl.ou.testar.StateModel.StateModelManager;
import org.fruit.alayer.Widget;

public interface ActionResolverDelegate {
    StateModelManager getStateModelManager();
    boolean blackListed(Widget w);
    boolean whiteListed(Widget w);
}

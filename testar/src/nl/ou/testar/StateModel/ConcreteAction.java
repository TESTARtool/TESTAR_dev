package nl.ou.testar.StateModel;

import org.fruit.alayer.Tag;

import java.util.Set;

public class ConcreteAction {

    /**
     * The concrete action id.
     */
    private String actionId;

    /**
     * The abstract action that abstracts this concrete action.
     */
    private AbstractAction abstractAction;

    /**
     * Constructor.
     * @param actionId
     */
    public ConcreteAction(String actionId, AbstractAction abstractAction) {
        this.actionId = actionId;
        this.abstractAction = abstractAction;
    }

    public String getActionId() {
        return actionId;
    }

    public AbstractAction getAbstractAction() {
        return abstractAction;
    }
}

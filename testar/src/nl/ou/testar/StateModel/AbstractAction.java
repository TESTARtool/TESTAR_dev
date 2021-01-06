package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Persistence.Persistable;
import org.fruit.alayer.TaggableBase;

import java.util.HashSet;
import java.util.Set;

public class AbstractAction extends AbstractEntity implements Persistable {

    // collection of concrete actions that are abstracted by this action
    private Set<String> concreteActionIds;

    /**
     * Constructor
     * @param actionId
     */
    public AbstractAction(String actionId) {
        super(actionId);
        concreteActionIds = new HashSet<>();
    }

    /**
     * This method returns the `attributes` that have been added to this abstract state
     *
     * @return
     */
    @Override
    public TaggableBase getAttributes() {
        return super.getAttributes();
    }

    /**
     * This method returns the action id
     * @return id for this action
     */
    public String getActionId() {
        return getId();
    }

    /**
     * This method adds a concrete action id to this abstract action
     * @param concreteActionId the concrete action id to add
     */
    public void addConcreteActionId(String concreteActionId) {
        concreteActionIds.add(concreteActionId);
    }

    /**
     * This method returns the set of concrete action ids that are abstracted by this action
     * @return concrete action ids
     */
    public Set<String> getConcreteActionIds() {
        return concreteActionIds;
    }

    @Override
    public boolean canBeDelayed() {
        return false;
    }
}

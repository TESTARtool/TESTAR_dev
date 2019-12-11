package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventType;
import nl.ou.testar.StateModel.Persistence.Persistable;
import java.util.HashSet;
import java.util.Set;

public class AbstractAction extends AbstractEntity implements Persistable {

    // collection of concrete actions that are abstracted by this action
	private Set<String> concreteActionIds;
	// interest that users have in current action
	private int userInterest;

	/**
     * Constructor
     * @param actionId
     */
    public AbstractAction(String actionId) {
        super(actionId);
        concreteActionIds = new HashSet<>();
        userInterest = 0;
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
    
    /**
     * Return the current User Interest about this Abstract Action
     * @return userInterest
     */
    public int getUserInterest() {
		return userInterest;
	}

    /**
     * Initialize or update the User Interest about this Abstract Action
     * @param userInterest
     */
	public void setUserInterest(int userInterest) {
		this.userInterest = userInterest;
	}
	
	/**
	 * Increase the User Interest by one after listening the execution of this Abstract Action
	 */
	public void increaseUserInterest() {
		this.userInterest ++;
		emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_ACTION_UPDATED, this));
	}

    @Override
    public boolean canBeDelayed() {
        return false;
    }
}

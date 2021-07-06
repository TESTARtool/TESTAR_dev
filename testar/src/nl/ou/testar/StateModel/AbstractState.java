package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Persistence.Persistable;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Connection;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.orientechnologies.orient.core.db.OrientDB;

import org.fruit.alayer.Tags;

public class AbstractState extends AbstractEntity implements Persistable {

    // list of possible actions that can be executed from this state
    private Map<String, AbstractAction> actions;
    // list of possible actions that have not yet been executed from this state
    private Map<String, AbstractAction> unvisitedActions;
    // a list of actions that have been visited in this state
    private Map<String, AbstractAction> visitedActions;
    // a set of strings containing the concrete state ids that correspond to this abstract state
    private Set<String> concreteStateIds;
    // is this an initial state?
    private boolean isInitial = false;

    OrientDB database;
    Connection connection;

    /**
     * Constructor
     * @param stateId
     * @param actions
     */

     private boolean ActionExistsInDatabase(AbstractAction action)
     {
        
        try (var db = connection.getDatabaseSession()){
            // first check if they're in the databse              
        
            var result = db.query("select from abstractaction where actionId = '"+action.getActionId()+"'");
            return result.hasNext();
        }
        catch (Exception e)
        {
            System.out.println("Exception during databse");
        }
        return false;
     }
    public AbstractState(String stateId, Set<AbstractAction> actions) {
        super(stateId);
        
        this.actions = new HashMap<>();
        unvisitedActions = new HashMap<>();
        visitedActions = new HashMap<>();
        connection = EntityManager.getNewConnection(database); 
        System.out.println("AbstractState: database = "+database+"  connection = "+connection);   
        if (actions != null) {
            for(AbstractAction action:actions) {
                this.actions.put(action.getActionId(), action);
                if (!ActionExistsInDatabase(action)){
                    
                    unvisitedActions.put(action.getActionId(), action);
                } else {
                    System.out.println(action.getActionId()+" alreaady existed in abstract state; do not add to unvisited");
                }
                
            }
        }
        concreteStateIds = new HashSet<>();
    }

    /**
     * Adds a concrete state id that corresponds to this abstract state.
     * @param concreteStateId the concrete id to add
     */
    public void addConcreteStateId(String concreteStateId) {
        this.concreteStateIds.add(concreteStateId);
    }

    /**
     * This method returns the id for this abstract state
     * @return String
     */
    public String getStateId() {
        return getId();
    }

    /**
     * This method sets the given action id to status visited
     * @param action the visited action
     */
    public void addVisitedAction(AbstractAction action) {
        unvisitedActions.remove(action.getActionId());
        visitedActions.put(action.getActionId(), action);
    }

    /**
     * This method returns all the possible actions that can be executed in this state
     * @return executable actions for this state
     */
    public Set<String> getActionIds() {
        return actions.keySet();
    }

    /**
     * This method returns all the abstract actions that are executable from this abstract state
     * @return
     */
    public Set<AbstractAction> getActions() {
        return new HashSet<>(actions.values());
    }

    /**
     * This method returns the action for a given action identifier
     * @param actionId
     * @return
     * @throws ActionNotFoundException
     */
    public AbstractAction getAction(String actionId) throws ActionNotFoundException{
        if (!actions.containsKey(actionId)) {
            throw new ActionNotFoundException();
        }
        return actions.get(actionId);
    }

    /**
     * This method returns the concrete state ids that correspond to this abstract state.
     * @return
     */
    public Set<String> getConcreteStateIds() {
        return concreteStateIds;
    }

    /**
     * This method returns the actions that have not yet been visited from this state
     * @return
     */
    public Set<AbstractAction> getUnvisitedActions() {
        String myId = this.getId();        
        String sql = "select from abstractaction where out in (select @rid from abstractstate where stateId = '"
                + myId + "')";
        System.out.println("Update visited actions of this node by adding database values  sql = " + sql);
        
        try(var db = connection.getDatabaseSession()) {
            var results = db.query(sql);
            while (results.hasNext()) {
                String actionId = results.next().getProperty("actionId");
                System.out.println("ActionId " + actionId + "  was ook al gevonden volgens de database");
                try {
                    unvisitedActions.remove(actionId);
                    
                } catch (Exception e) {
                    System.out.println("Duplicaat? " + e);
                }

            }
        } 
        sql = "select from unvisitedabstractaction where in in (select from BeingExecuted)";
                
        try(var db = connection.getDatabaseSession()) {
            var results = db.query(sql);
            while (results.hasNext()) {
                String actionId = results.next().getProperty("actionId");
                System.out.println("ActionId " + actionId + "  is in BeingExecuted; remove as well from unvisited");
                try {
                    unvisitedActions.remove(actionId);
                    
                } catch (Exception e) {
                    System.out.println("Duplicaat? " + e);
                }

            }
        } 

        return new HashSet<>(unvisitedActions.values());
    }

    /**
     * This method returns all the actions for this abstract state that have been visited
     * @return
     */
    public Set<AbstractAction> getVisitedActions() {
        return new HashSet<>(visitedActions.values());
    }

    /**
     * Returns true if this is one of the starting states of the model. False otherwise.
     */
    public boolean isInitial() {
        return isInitial;
    }

    /**
     * Set to true if this is one of the starting states of the model
     * @param initial
     */
    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    @Override
    public void setModelIdentifier(String modelIdentifier) {
        super.setModelIdentifier(modelIdentifier);
        // set the identifier on the abstract actions for this state
        for (String key : actions.keySet()) {
            actions.get(key).setModelIdentifier(modelIdentifier);
            if (unvisitedActions.containsKey(key)) {
                unvisitedActions.get(key).setModelIdentifier(modelIdentifier);
            }
        }
    }

    /**
     * Add a new abstract action to the abstract state.
     * @param action
     */
    public void addNewAction(AbstractAction action) {
        if (!this.actions.containsKey(action.getActionId())) {
            action.setModelIdentifier(this.getModelIdentifier());
            actions.put(action.getActionId(), action);
            unvisitedActions.put(action.getActionId(), action);
        }
    }

    @Override
    public boolean canBeDelayed() {
        return false;
    }
}

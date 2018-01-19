package es.upv.staq.testar.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.exceptions.NoSuchTagException;

import es.upv.staq.testar.graph.IEnvironment;

public class StateManager {
	List<Action> actions = new ArrayList<Action>();
	State state = null;
	IEnvironment environment = null;
	List<Action> previousActions = new ArrayList<Action>();
	List<String> previousStates = new ArrayList<String>();
	TreeMap<String, Integer> executed = new TreeMap<String, Integer>();
	Random rnd = new Random(System.currentTimeMillis());
	
	
	public static enum Status {UNEX, LEAST, MOST};
	
	//public void setActions(List<Action> actions){
		//this.actions = actions;
	//}
	
	//public void addAction(Action action){
		//actions.add(action);
	//}

	public boolean isAvailable(Role actiontype) {
		System.out.print("Checking if actions of type "+actiontype.name() +" are available... ");
		boolean result = false;
		for (Action a : actions){
			if (a.get(Tags.Role) == actiontype){
				result = true;
			}
		}
		System.out.println(result);
		return result;
	}

	public int getNumberOfActions() {
		System.out.println("Returning number of actions: "+actions.size());
		return actions.size();
	}

	public int getNumberOfActions(Role actiontype) {
		if (actiontype == null){
			System.out.println("Actiontype is null, returning 0");
			return 0;
		}
		
		List<Action> a = getActionsOfType(actiontype);
		System.out.print("Returning number of actions of type "+actiontype.toString()+": "+a.size());
		
		return a.size();
	}

	public int getNumberOfActions(Role actiontype, String status) {
		Status st = Status.valueOf(status);
		int result = 0;
		switch(st){
		case UNEX:
			for (Action a : actions){
				if (a.get(Tags.Role) == actiontype && !(executed.keySet().contains(a.get(Tags.ConcreteID)))){
					result += 1;
				}
			}
			break;
		default:
			break;
		}
		System.out.println("Returning number of actions of type "+actiontype.toString()+" and status "+ status+": "+result);
		return result;
	}

	public Action getRandomAction(Role actiontype) {
		if(actiontype == null){
			System.out.println("Actiontype is null, returning null");
			return null;
		}
		System.out.println("Getting a random action of type "+actiontype.toString());
		List<Action> at = getActionsOfType(actiontype);
		if (at.size() == 0){
			System.out.println("Returning null, no actions of type "+actiontype.toString());
			return null;
		}
		else {
			System.out.println("Returning a random action of type "+actiontype.toString());
			return at.get(rnd.nextInt(at.size()));
		}
	}

	public Action getRandomAction() {
		System.out.println("Getting a random action");
		Action result = actions.get(rnd.nextInt(actions.size()));
		return result;
	}

	public Action getRandomActionOfTypeOtherThan(Role actiontype) {
		System.out.println("Getting a random action of type other than "+actiontype.toString());
		List<Action> at = new ArrayList<>();
		for (Action a : actions){
			if (!(a.get(Tags.Role) == actiontype)){
				at.add(a);
			}
		}
		if (at.size() == 0){
			System.out.println("Returning null, no actions of type other than "+actiontype.toString());
			return null;
		}
		else {
			System.out.println("Returning a random action of type other than "+actiontype.toString());
			return at.get(rnd.nextInt(at.size()));
		}
	}
	public Action getRandomAction(String status) {
		System.out.println("Getting a random action of status "+status);
		return getRandomAction(status, actions);
	}

	public Action getRandomAction(String status, List<Action> providedListofActions) {
		System.out.println("Getting a random action of status "+status+" from list of actions");
		if (executed.size() == 0){
			System.out.println("List of executed actions is empty, returning a random action from the list of actions.");
			return getRandomAction(providedListofActions);
		} else {
			Status s = Status.valueOf(status);
			int i = -1;
			List<Action> filteredListofActions = new ArrayList<>();
			
			switch (s){
				case UNEX:
					i = 0;
					break;
			case LEAST:
				for (Action a : actions){
					if (!(executed.containsKey(a.get(Tags.ConcreteID)))){
						i = 0;
						break;
					}
				}
				if (i != 0)
					i = Collections.min(executed.values());
				break;
			case MOST:
				i = Collections.max(executed.values());
				break;
			default:
				i = 0;
				break;
			}
			for (Action a : providedListofActions){
				if (i == 0){
					if (!executed.containsKey(a.get(Tags.ConcreteID))){
						filteredListofActions.add(a);
					}
				} else {
					if(executed.containsKey(a.get(Tags.ConcreteID)) && executed.get(a.get(Tags.ConcreteID)) == i){
						filteredListofActions.add(a);
					}
				}
			}
			if (filteredListofActions.size() == 0){
				System.out.println("Found no actions meeting the requirements, returning null.");
				return null;
			}else{ 
				System.out.println("Getting a random action from the found set with number of executions = "+i);			
				return filteredListofActions.get(rnd.nextInt(filteredListofActions.size()));
			}
		
		}
		
	}

	private Action getRandomAction(List<Action> providedListOfActions) {
		if (providedListOfActions.size() != 0){
			System.out.println("Getting a random action from the provided list.");
			return providedListOfActions.get(rnd.nextInt(providedListOfActions.size()));
		} else {
			System.out.println("Provided list is empty, returning null.");
			return null;
		}
	}

	public Action getRandomAction(Role actiontype, String status) {
		if (actiontype == null){
			System.out.println("Actiontype is null, returning null");
			return null;
		}
		System.out.println("Getting a random action of type "+actiontype.toString()+ " and status = "+status);
		return getRandomAction(status, getActionsOfType(actiontype));
	}
	
	public List<Action> getActionsOfType(Role actiontype){
		List<Action> actionsoftype = new ArrayList<>();
		for (Action a : actions){
			if (a.get(Tags.Role) == actiontype){
				actionsoftype.add(a);
			}
		}
		System.out.println("Returning all actions of type "+actiontype.toString()+": found "+actionsoftype.size());
		return actionsoftype;
	}

	public Action previousAction() {
		if (previousActions.size() != 0){
			System.out.println("Returning the previous action");
			return previousActions.get(previousActions.size()-1);
		
		}else{ 
			System.out.println("There are no previous actions, returning null.");
			return getRandomAction();
		}
	}

	public int getNumberOfPreviousActions() {
		System.out.println("Returning the number of previous actions: "+previousActions.size());
		return previousActions.size();
	}

	
	public void setState(IEnvironment env, State state, Set<Action> acts) {
		this.state = state;
		this.actions = new ArrayList<Action>(acts);
		this.environment = env;
	}

	public void setPreviousAction(Action previousAction) {
		System.out.println("Adding the selected action to the history...");
		try{
			previousActions.add(previousAction);
		
			int i = 1;
		
			String pa = previousAction.get(Tags.ConcreteID);
			if (executed.containsKey(pa))
				i = executed.get(pa) + 1;
			executed.put(pa, i);
		} catch (NoSuchTagException e){
			System.out.println("This was an irregular action, I've not added it to the history.");
		}
		
	}
	public boolean hasStateNotChanged(){
		return previousStates.size() >= 2 && previousStates.get(previousStates.size()-1) == previousStates.get(previousStates.size()-2);
	}
	public void setPreviousState(State st) {
		System.out.println("Adding state to the history...");
		if (previousStates.size() != 0 && previousStates.get(previousStates.size()-1) == state.get(Tags.ConcreteID)){
			System.out.println("Hmmm I'm still in the same state!");
		} else if (previousStates.contains(st.get(Tags.ConcreteID))){
			System.out.println("Hey, I've been here before!");
		}
		previousStates.add(st.get(Tags.ConcreteID));
		
	}

}

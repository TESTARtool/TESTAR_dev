package es.upv.staq.testar.strategyparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import es.upv.staq.testar.algorithms.StrategyWalker;

public class StrategyFactory {
	public Queue<Function> queue = new LinkedList<Function>();
	public enum Function {
		AND, CLICKACTION, DRAGACTION, DRAGACTIONSAVAILABLE, EQUALS, EQUALSTYPE, ESCAPE, GREATERTHAN, HITKEYACTION, IFTHENELSE, 
		LEFTCLICKSAVAILABLE, NOT, NUMBEROFACTIONS, NUMBEROFACTIONSOFTYPE, NUMBEROFDRAGACTIONS, 
		NUMBEROFLEFTCLICKS, NUMBEROFPREVIOUSACTIONS, NUMBEROFTYPEACTIONS, 
		NUMBEROFUNEXECUTEDDRAGACTIONS, NUMBEROFUNEXECUTEDLEFTCLICKS, 
		NUMBEROFUNEXECUTEDTYPEACTIONS, OR, PREVIOUSACTION, RANDOMACTION, RANDOMACTIONOFTYPE, 
		RANDOMACTIONOFTYPEOTHERTHAN, RANDOMLEASTEXECUTEDACTION, 
		RANDOMMOSTEXECUTEDACTION, RANDOMNUMBER, RANDOMUNEXECUTEDACTION, RANDOMUNEXECUTEDACTIONOFTYPE, 
		STATEHASNOTCHANGED, TYPEACTION,	TYPEACTIONSAVAILABLE, TYPEOFACTIONOF
	}

	public StrategyFactory(String strategy){
		if (strategy.contains(".txt")){
			makeQueue(getStrategyFromFile(strategy));
		} else {
			System.out.println("Strategy: "+strategy);
			makeQueue(strategy);
		}
	}
	
	private String getStrategyFromFile(String strategyFile) {
		String strategyFromFile = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("settings/"+strategyFile));
			strategyFromFile = br.readLine();
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found. I'm looking from this directory: "+System.getProperty("user.dir"));
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Strategy from file: "+strategyFromFile);
		return strategyFromFile;
	}

	public StrategyWalker getStrategyWalker(){
		StrategyNode main = getNode();
		StrategyWalker selector = new StrategyWalker(main);
		selector.print();
		return selector;
	}
	
	private StrategyNode getNode(){
		Function f = queue.poll();
		ArrayList<StrategyNode> children = new ArrayList<>();
		
		switch (f){
			
		case AND:
			children.add(getNode());
			children.add(getNode());
			return new SnAnd(children);
			
		case CLICKACTION:
			return new SnClickAction(children);
			
		case DRAGACTION:
			return new SnDragAction(children);
			
		case DRAGACTIONSAVAILABLE:
			return new SnDragActionsAvailable(children);
			
		case EQUALS:
			children.add(getNode());
			children.add(getNode());
			return new SnEquals(children);
			
		case EQUALSTYPE:
			children.add(getNode());
			children.add(getNode());
			return new SnEqualsType(children);
			
		case ESCAPE:
			return new SnEscape(children);
			
		case GREATERTHAN:
			children.add(getNode());
			children.add(getNode());
			return new SnGreaterThan(children);
		
		case HITKEYACTION:
			return new SnHitKeyAction(children);
			
		case IFTHENELSE:
			children.add(getNode());
			children.add(getNode());
			children.add(getNode());
			return new SnIfThenElse(children);
		
		case LEFTCLICKSAVAILABLE:
			return new SnLeftClicksAvailable(children);
			
		case NOT:
			children.add(getNode());
			return new SnNot(children);

		case NUMBEROFACTIONS:
			return new SnNumberOfActions(children);
			
		case NUMBEROFACTIONSOFTYPE:
			children.add(getNode());
			return new SnNumberOfActionsOfType(children);
		
		case NUMBEROFDRAGACTIONS:
			return new SnNumberOfDragActions(children);
			
		case NUMBEROFLEFTCLICKS:
			return new SnNumberOfLeftClicks(children);
			
		case NUMBEROFPREVIOUSACTIONS:
			return new SnNumberOfPreviousActions(children);
			
		case NUMBEROFTYPEACTIONS:
			return new SnNumberOfTypeActions(children);
		
		case NUMBEROFUNEXECUTEDDRAGACTIONS:
			return new SnNumberOfUnexecutedDragActions(children);
			
		case NUMBEROFUNEXECUTEDLEFTCLICKS:
			return new SnNumberOfUnexecutedLeftClicks(children);
			
		case NUMBEROFUNEXECUTEDTYPEACTIONS:
			return new SnNumberOfUnexecutedTypeActions(children);
			
		case OR:
			children.add(getNode());
			children.add(getNode());
			return new SnOr(children);
			
		case PREVIOUSACTION:
			return new SnPreviousAction(children);
			
		case RANDOMACTION:
			return new SnRandomAction(children);
			
		case RANDOMACTIONOFTYPE:
			children.add(getNode());
			return new SnRandomActionOfType(children);
			
		case RANDOMACTIONOFTYPEOTHERTHAN:
			children.add(getNode());
			return new SnRandomActionOfTypeOtherThan(children);
			
		case RANDOMLEASTEXECUTEDACTION:
			return new SnRandomLeastExecutedAction(children);
			
		case RANDOMMOSTEXECUTEDACTION:
			return new SnRandomMostExecutedAction(children);
			
		case RANDOMNUMBER:
			return new SnRandomNumber(children);
			
		case RANDOMUNEXECUTEDACTION:
			return new SnRandomUnexecutedAction(children);
			
		case RANDOMUNEXECUTEDACTIONOFTYPE:
			children.add(getNode());
			return new SnRandomUnexecutedActionOfType(children);
			
		case STATEHASNOTCHANGED:
			return new SnStateHasNotChanged(children);
			
		case TYPEACTION:
			return new SnTypeAction(children);
			
		case TYPEACTIONSAVAILABLE:
			return new SnTypeActionsAvailable(children);
			
		case TYPEOFACTIONOF:
			children.add(getNode());
			return new SnTypeOfActionOf(children);
		default:
			return null;
		}
		
		
	}
	
	private void makeQueue(String strategy){
		strategy = strategy.replace(" ", "");
		strategy = strategy.replace("(", "");
		strategy = strategy.replace(")", "");
        String[] list = strategy.split(":");
        for(String s : list){
        	s = s.replace("-", "");
        	s = s.toUpperCase();
        	
        	queue.add(Function.valueOf(s));
        }
	}
}

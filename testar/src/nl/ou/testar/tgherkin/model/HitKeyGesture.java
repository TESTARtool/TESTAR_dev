package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.linux.AtSpiTags;
import org.fruit.alayer.windows.UIATags;


/**
 * Tgherkin HitKeyGesture.
 *
 */
public class HitKeyGesture extends Gesture {

	/**
	 * HitKeyGesture constructor.
	 * @param arguments list of arguments
	 */
	public HitKeyGesture(List<Argument> arguments) {
		super(arguments);
	}


	@Override
	public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		return super.gesturePossible(widget, proxy, dataTable) && (widget.get(UIATags.UIAHasKeyboardFocus, false) || widget.get(AtSpiTags.AtSpiHasFocus, false));
	}

	@Override
	public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
		StdActionCompiler ac = new AnnotatingActionCompiler();
		switch (getArguments().size()) {
		case 0:
			actions.add(ac.hitShortcutKey(getRandomKeys()));
			break;
		case 1:
			actions.add(ac.hitKey(KBKeys.valueOf(getStringArgument(0, dataTable))));
			break;
		default:
			// 2 or more keys
			List<KBKeys> keys = new ArrayList<KBKeys>();
			for (int index = 0; index < getArguments().size(); index++) {
				keys.add(KBKeys.valueOf(getStringArgument(index, dataTable)));
			}
			actions.add(ac.hitShortcutKey(keys));
		}
		return actions;
	}

	private List<KBKeys> getRandomKeys() {
		List<KBKeys> keys = new ArrayList<KBKeys>();
		Random random = new Random();
		final int maxNrOfKeys = 3;
		// pick a random number within the range of 1, 2 or 3 keys
		int nrOfKeys  = 1 + random.nextInt(maxNrOfKeys);
		while (keys.size() < nrOfKeys) {
			// pick a random number within the range of KBKeys enum values
			KBKeys key = KBKeys.values()[random.nextInt(KBKeys.values().length)];
			// same key cannot be pressed more than once
			if (!keys.contains(key)) {
				keys.add(key);
			}
		}
		return keys;
	}

	/**
	 * Check gesture.
	 * @param dataTable given data table
	 * @return list of error descriptions
	 */
	@Override
	public List<String> check(DataTable dataTable) {
		List<String> list = new ArrayList<String>();
		List<KBKeys> keys = new ArrayList<KBKeys>();
		boolean placeholdersUsed = false;
		for (Argument argument : getArguments()) {
			if (argument instanceof PlaceholderArgument) {
				placeholdersUsed = true;
				String name = ((PlaceholderArgument)argument).getValue();
				if (dataTable == null){
					list.add(getClass().getSimpleName() + " validation error - no data table found for string placeholder : " + name + System.getProperty("line.separator"));
				}else{	
					// check whether the placeholder is a column name of the data table
					if (!dataTable.isColumnName(name)) {
						list.add(getClass().getSimpleName() + " validation error - invalid argument placeholder : " + name + "\n");
					}else {
						// check whether data table cells contain valid entries
						list.addAll(checkTableContent(dataTable, name));
					}
				}
			}else {
				// String argument with KBKeys 
				if (argument instanceof StringArgument) {
					String value = ((StringArgument)argument).getValue();
					KBKeys key = null;
					try {
						key = KBKeys.valueOf(value);
					}catch(IllegalArgumentException e) {
						list.add(getClass().getSimpleName() +  " validation error - key value " + value + " does not exist" + System.getProperty("line.separator"));					
					}
					if (key != null) {
						if (keys.contains(key)) {
							list.add(getClass().getSimpleName() +  " validation error - key value " + value + " occurs multiple times" + System.getProperty("line.separator"));							
						}else {
							keys.add(key);
						}

					}
				}
			}
		}
		// only further check for uniqueness if no errors found yet 
		if (list.isEmpty() && placeholdersUsed) {
			list.addAll(checkUniqueness(dataTable));
		}
		return list;
	}	

	private List<String> checkTableContent(DataTable dataTable, String columnName) {
		List<String> list = new ArrayList<String>();
		if (dataTable == null){
			return list;
		}	
		int rows = 0;
		while (dataTable.moreSequences()) {
			dataTable.beginSequence();
			rows++;
			String value = null;
			try {
				value = dataTable.getPlaceholderValue(columnName);
				try {
					KBKeys.valueOf(value);
				}catch(IllegalArgumentException e) {
					list.add(getClass().getSimpleName() +  " validation error - key value " + value + " does not exist at row " + rows + " for placeholder " + columnName + System.getProperty("line.separator"));					
				}
			}catch(Exception e) {
				list.add(getClass().getSimpleName() +  " validation error - invalid table value at row " + rows + " for placeholder " + columnName + System.getProperty("line.separator"));					
			}
		}
		dataTable.reset();
		return list;
	}

	//check for uniqueness data table rows and string arguments
	private List<String> checkUniqueness(DataTable dataTable) {
		List<String> list = new ArrayList<String>();
		if (dataTable == null){
			return list;
		}
		List<KBKeys> keys = null;
		// process for all data table elements
		int rows = 0;
		while (dataTable.moreSequences()) {
			keys = new ArrayList<KBKeys>();
			dataTable.beginSequence();
			rows++;
			for (Argument argument : getArguments()) {
				String value = null;
				if (argument instanceof PlaceholderArgument) {
					String name = ((PlaceholderArgument)argument).getValue();
					value = dataTable.getPlaceholderValue(name);
				}else {
					// String argument with KBKeys 
					if (argument instanceof StringArgument) {
						value = ((StringArgument)argument).getValue();
					}
				}
				KBKeys key = null;
				try {
					key = KBKeys.valueOf(value);
				}catch(IllegalArgumentException e) {
					list.add(getClass().getSimpleName() +  " validation error - key value " + value + " does not exist for data table row" + rows + System.getProperty("line.separator"));					
				}
				if (key != null) {
					if (keys.contains(key)) {
						list.add(getClass().getSimpleName() +  " validation error - key value " + value + " occurs multiple times  for data table row" + rows + System.getProperty("line.separator"));							
					}else {
						keys.add(key);
					}

				}
			}	
		}
		dataTable.reset();
		return list;
	}


	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("hitKey");
		result.append(argumentsToString());
		return result.toString();    	
	}
}

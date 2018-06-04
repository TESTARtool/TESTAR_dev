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
 * Class responsible for handling key board key presses.
 *
 */
public class HitKeyGesture extends Gesture {

	/**
	 * HitKeyGesture constructor.
     * @param parameterBase container for parameters
	 */
	public HitKeyGesture(ParameterBase parameterBase) {
		super(parameterBase);
	}


	@Override
	public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		return super.gesturePossible(widget, proxy, dataTable) && (widget.get(UIATags.UIAHasKeyboardFocus, false) || widget.get(AtSpiTags.AtSpiHasFocus, false));
	}
	
	@Override
	public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
		StdActionCompiler ac = new AnnotatingActionCompiler();
		List<String> keyList = getParameterBase().getList(Parameters.KBKEYS, dataTable);
		if (keyList == null) {
			return actions;
		}
		switch (keyList.size()) {
		case 0:
			actions.add(ac.hitShortcutKey(getRandomKeys()));
			break;
		case 1:
			actions.add(ac.hitKey(KBKeys.valueOf(keyList.get(0))));
			break;
		default:
			// 2 or more keys
			List<KBKeys> keys = new ArrayList<KBKeys>();
			for (String key : keyList) {
				keys.add(KBKeys.valueOf(key));
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
			// don't press the same key more than once
			if (!keys.contains(key)) {
				keys.add(key);
			}
		}
		return keys;
	}

	@Override
	public List<String> check(DataTable dataTable) {
		List<String> list = new ArrayList<String>();
		List<KBKeys> keys = new ArrayList<KBKeys>();
		for (String name : getParameterBase().getPlaceholders()) {
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
		}
		for (String value : getParameterBase().getNonPlaceholderValuesToString()) {
				// String argument with KBKeys 
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
				// elements are separated by spaces
				String[] elements = value.split("\\s+");
				for (String element : elements) {
					try {
						KBKeys.valueOf(element);
					}catch(IllegalArgumentException e) {
						list.add(getClass().getSimpleName() +  " validation error - key value element " + element + " does not exist at row " + rows + " for placeholder " + columnName + System.getProperty("line.separator"));					
					}
				}
			}catch(Exception e) {
				list.add(getClass().getSimpleName() +  " validation error - invalid table value at row " + rows + " for placeholder " + columnName + System.getProperty("line.separator"));					
			}
		}
		dataTable.reset();
		return list;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("hitKey");
		result.append(getParameterBase().toString());
		return result.toString();    	
	}
}

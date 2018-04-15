package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;


/**
 * Tgherkin Document.
 *
 */
public class Document {

    private List<Feature> features;
    private int index;
    
    /**
     * Document constructor.
     * @param features given list of features
     */
    public Document(List<Feature> features) {
    	Assert.notNull(features);
    	this.features = Collections.unmodifiableList(features);
    	index = -1;
    }

    
    /**
     * Retrieve features.
     * @return list of features
     */
    public List<Feature> getFeatures() {
        return features;        
    }
    
    /**
	 * Check whether more actions exist.
	 * @param settings given settings
	 * @return boolean true if more actions exist, otherwise false
	 */
	public boolean moreActions(Settings settings) {
		return !(currentFeature().hasFailed() && !settings.get(ConfigTags.ForceToSequenceLength)) && currentFeature().moreActions();
	}
    
	/**
	 * Check whether more sequences exist.
	 * @return boolean true if more sequences exist, otherwise false
	 */
	public boolean moreSequences() {
		return hasNextFeature() || currentFeature().moreSequences();
	}
	
	/**
	 * Begin sequence.
	 */
	public void beginSequence() {
		if (currentFeature() != null && currentFeature().moreSequences()) {
			// scenario outline: each table row instance is a new sequence 
			currentFeature().beginSequence();
		}else {
			nextFeature().beginSequence();
		}
	}
    
	/**
	 * Derive actions.
	 * @param state the SUT's current state
	 * @param settings given settings
	 * @param proxy given action widget proxy
	 * @return set of derived actions, empty set if no action was found 
	 */
	public Set<Action> deriveActions(State state, Settings settings, ActionWidgetProxy proxy) {
		currentFeature().evaluateGivenCondition(state, settings);
		return currentFeature().evaluateWhenCondition(state, settings, proxy);
	}
	

	/**	  
	 * Get verdict.
	 * @param state the SUT's current state
	 * @param settings given settings
	 * @return oracle verdict, which determines whether the state is erroneous and why 
	 */
	public Verdict getVerdict(State state, Settings settings) {
		return currentFeature().getVerdict(state, settings);
	}

	/**
     * Retrieve whether current action resulted in a failure.
     * @return true if current action failed otherwise false 
     */
	public boolean hasFailed() {
		if(currentFeature() != null) {
			return currentFeature().hasFailed();
		}
		return false;
	}
	
	
	/**
     * Reset document.
     */
	public void reset() {
		index = -1;
		for (Feature feature : getFeatures()) {
			feature.reset();
		}
	}

	/**
     * Check.
     * @return list of error descriptions
     */
	public List<String> check() {
		List<String> list = new ArrayList<String>();
		for (Feature feature : getFeatures()) {
			list.addAll(feature.check());
		}
		return list;
	}
	
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		for (Feature feature : getFeatures()) {
    		result.append(feature.toString());
    	}
    	return result.toString();    	
    }

    private boolean hasNextFeature() {
    	return index + 1 < features.size();
    }

    private Feature nextFeature() {
    	index++; 
        return features.get(index);
    }

    private Feature currentFeature() {
    	if (index < 0 || index >= features.size()) {
    		return null;
    	}
        return features.get(index);
    }
    
}

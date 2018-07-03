package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Verdict;
import org.fruit.monkey.ConfigTags;

/**
 * Representation of an executable Tgherkin document.
 *
 */
public class Document {

	/**
	 * Sequential execution mode.
	 */
	public static final String SEQUENTIAL_MODE = "sequential"; 
	private List<Feature> features;
	private int index;

	/**
	 * Document constructor.
	 * @param features list of features
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
	 * @param proxy document protocol proxy
	 * @return true if more actions exist, otherwise false
	 */
	public boolean moreActions(ProtocolProxy proxy) {
		return !(currentFeature().hasFailed() && !proxy.getSettings().get(ConfigTags.ForceToSequenceLength)) 
				&& currentFeature().moreActions(proxy);
	}

	/**
	 * Check whether more sequences exist.
	 * @return true if more sequences exist, otherwise false
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
		} else {
			nextFeature().beginSequence();
		}
	}

	/**
	 * Derive actions.
	 * @param proxy document protocol proxy
	 * @return set of derived actions, empty set if no actions were derived 
	 */
	public Set<Action> deriveActions(ProtocolProxy proxy) {
		currentFeature().evaluateGivenCondition(proxy);
		return currentFeature().evaluateWhenCondition(proxy);
	}


	/**	  
	 * Get verdict.
	 * @param proxy document protocol proxy
	 * @return oracle verdict, which determines whether the state is erroneous and why 
	 */
	public Verdict getVerdict(ProtocolProxy proxy) {
		return currentFeature().getVerdict(proxy);
	}

	/**
	 * Retrieve whether current action resulted in a failure.
	 * @return true if current action failed, otherwise false 
	 */
	public boolean hasFailed() {
		if (currentFeature() != null) {
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
	 * @return list of error descriptions, empty list if no errors exist
	 */
	public List<String> check() {
		List<String> list = new ArrayList<String>();
		for (Feature feature : getFeatures()) {
			list.addAll(feature.check());
		}
		return list;
	}

	/**
	 * Get registered execution modes.
	 * @return registered execution modes.
	 */
	public static String[] getRegisteredExecutionModes() {
		return new String[]{
				SEQUENTIAL_MODE
		};
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

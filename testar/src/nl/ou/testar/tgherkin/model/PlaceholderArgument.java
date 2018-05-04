package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Tgherkin PlaceholderArgument.
 * 
 */
public class PlaceholderArgument extends Argument {

	private final String value;
	
	/**
	 * PlaceholderArgument Constructor.
	 * @param value given value
	 */
	public PlaceholderArgument(String value){
		Assert.notNull(value);
		this.value = value;
	}
	
	/**
	 * Retrieve value.
	 * @return value
	 */
	@Override
	public String getValue() {
		return value;
	}
	
    @Override
    public String toString() {
    	return "<" + getValue() + ">";    	
    }
	
}

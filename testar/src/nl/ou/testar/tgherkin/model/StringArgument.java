package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Tgherkin StringArgument.
 * 
 */
public class StringArgument extends Argument {

	private final String value;
	
	/**
	 * StringArgument Constructor.
	 * @param value given value
	 */
	public StringArgument(String value){
		Assert.notNull(value);
		this.value = value;
	}
	
	/**
	 * Retrieve value.
	 * @return value
	 */
	public String getValue() {
		return value;
	}
	
    @Override
    public String toString() {
    	return "\"" + getValue() + "\"";    	
    }
	
}

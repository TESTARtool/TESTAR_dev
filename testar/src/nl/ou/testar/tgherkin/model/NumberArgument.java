package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Tgherkin NumberArgument.
 * 
 */
public class NumberArgument extends Argument {

	private final Double value;
	
	/**
	 * NumberArgument Constructor.
	 * @param value given value
	 */
	public NumberArgument(Double value){
		Assert.notNull(value);
		this.value = value;
	}
	
	/**
	 * Retrieve value.
	 * @return value
	 */
	public Double getValue() {
		return value;
	}
	
    @Override
    public String toString() {
    	return getValue().toString();    	
    }
	
}

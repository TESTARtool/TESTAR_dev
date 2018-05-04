package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Tgherkin BooleanArgument.
 * 
 */
public class BooleanArgument extends Argument {

	private final Boolean value;
	
	/**
	 * NumberArgument Constructor.
	 * @param value given value
	 */
	public BooleanArgument(Boolean value){
		Assert.notNull(value);
		this.value = value;
	}
	
	/**
	 * Retrieve value.
	 * @return value
	 */
	@Override
	public Boolean getValue() {
		return value;
	}
	
    @Override
    public String toString() {
    	return getValue().toString();    	
    }
	
}

package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Tgherkin WidgetConditionArgument.
 * 
 */
public class WidgetConditionArgument extends Argument {

	private final WidgetCondition value;
	
	/**
	 * NumberArgument Constructor.
	 * @param value given value
	 */
	public WidgetConditionArgument(WidgetCondition value){
		Assert.notNull(value);
		this.value = value;
	}
	
	/**
	 * Retrieve value.
	 * @return value
	 */
	@Override
	public WidgetCondition getValue() {
		return value;
	}
	
    @Override
    public String toString() {
    	return getValue().toString();    	
    }
	
}

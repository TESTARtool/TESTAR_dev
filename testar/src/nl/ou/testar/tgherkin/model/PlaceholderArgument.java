package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Tgherkin PlaceholderArgument.
 * 
 */
public class PlaceholderArgument extends Argument {

	private final String name;
	
	/**
	 * PlaceholderArgument Constructor.
	 * @param name given name
	 */
	public PlaceholderArgument(String name){
		Assert.notNull(name);
		this.name = name;
	}
	
	/**
	 * Retrieve name.
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
    @Override
    public String toString() {
    	return "<" + getName() + ">";    	
    }
	
}

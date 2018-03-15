package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Tgherkin Tag.
 *
 */
public class Tag {
    
	private final String name;

    /**
     * Tag constructor.
     * @param name given name
     */
    public Tag(String name) {
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
    	return getName();    	
    }
    
    
}

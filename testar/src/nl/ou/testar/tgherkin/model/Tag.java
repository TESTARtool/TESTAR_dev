package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Representation of a Tgherkin tag.
 *
 */
public class Tag {
    
	private final String name;

    /**
     * Tag constructor.
     * @param name tag name
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

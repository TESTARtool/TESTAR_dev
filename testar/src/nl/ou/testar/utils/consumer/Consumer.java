package nl.ou.testar.utils.consumer;

/**
 * Interface that defines a consumer of Items.
 *
 */
public interface Consumer {

	/**
	 * Consume item.
	 * @param item to be consumed item
	 * @return true if item consumed successfully, otherwise false.
	 */
	boolean consume(Item item);  

	/**
	 * Finish consumption. 
	 */
	void finishConsumption();  	
}

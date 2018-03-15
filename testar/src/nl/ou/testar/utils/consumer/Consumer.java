package nl.ou.testar.utils.consumer;

/**
 * Consumer interface.
 *
 */
public interface Consumer {

	/**
	 * Consume item.
	 * @param item given item
	 * @return true if item consumed successfully, otherwise false.
	 */
	boolean consume(Item item);  

	/**
	 * Finish consumption. 
	 */
	void finishConsumption();  	
}

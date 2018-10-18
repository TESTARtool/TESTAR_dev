package nl.ou.testar.utils.consumer;

import java.util.concurrent.BlockingQueue;  
import java.util.concurrent.TimeUnit;  

/**
 * Class responsible for processing an Item.
 *
 */
public class ItemProcessor implements Runnable  {

	/**
	 * Poll timeout in seconds.
	 */
	public static final long TIMEOUT = 10;
	private BlockingQueue<Item> jobQueue;  
	private volatile boolean keepProcessing;  

	/**
	 * Constructor.
	 * @param queue queue with to be processed items.
	 */
	public ItemProcessor(BlockingQueue<Item> queue) {  
		jobQueue = queue;  
		keepProcessing = true;  
	}  

	@Override
	public void run() {  
		while (keepProcessing || !jobQueue.isEmpty()) {  
			try {  
				Item item = jobQueue.poll(TIMEOUT, TimeUnit.SECONDS);  
				if (item != null) {  
					item.process();  
				}  
			} catch(InterruptedException ie) {  
				Thread.currentThread().interrupt();  
				return;  
			}  
		}  
	}  

	/**
	 * Cancel execution.
	 */
	public void cancelExecution() {  
		this.keepProcessing = false;  
	}  

}
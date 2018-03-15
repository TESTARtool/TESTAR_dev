package nl.ou.testar.utils.consumer;

import java.util.LinkedList;  
import java.util.List;  
import java.util.concurrent.BlockingQueue;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.LinkedBlockingQueue;  

/**
 * Consumer implementation.
 */
public class ConsumerImpl implements Consumer{

	private BlockingQueue< Item > itemQueue = new LinkedBlockingQueue< Item >();  
	private ExecutorService executorService = Executors.newCachedThreadPool();  
	private List<ItemProcessor> jobList = new LinkedList<ItemProcessor>();  
	private volatile boolean shutdownCalled = false;  

	/**
	 * Constructor.
	 * @param poolSize given pool size
	 */
	public ConsumerImpl(int poolSize){  
		for(int i = 0; i < poolSize; i++){  
			ItemProcessor jobThread = new ItemProcessor(itemQueue);  
			jobList.add(jobThread);  
			executorService.submit(jobThread);  
		}  
	}  

	/**
	 * Consume item.
	 * @param item given item
	 * @return true if item consumed successfully, otherwise false.
	 */
	public boolean consume(Item item){  
		if(!shutdownCalled){  
			try{  
				itemQueue.put(item);  
			}catch(InterruptedException ie){  
				Thread.currentThread().interrupt();  
				return false;  
			}  
			return true;  
		}  
		else{  
			return false;  
		}  
	}  

	/**
	 * Finish consumption. 
	 */
	public void finishConsumption(){  
		for(ItemProcessor itemProcessor : jobList){  
			itemProcessor.cancelExecution();  
		}  
		executorService.shutdown();  
	}  

}


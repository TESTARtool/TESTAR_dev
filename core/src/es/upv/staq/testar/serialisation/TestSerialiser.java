/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2016):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

package es.upv.staq.testar.serialisation;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import org.fruit.Assert;
import org.fruit.alayer.Taggable;

import es.upv.staq.testar.serialisation.LogSerialiser.LogLevel;

/**
 * Tests serialiser..
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class TestSerialiser extends Thread {

	private static ObjectOutputStream test;
	private static int fragmentTimes;
	private static final int FLUSH_INTERVAL = 16;
	private static LinkedList<Taggable> testSavingQueue =  new LinkedList<Taggable>();
	private static TestSerialiser singletonTestSerialiser;
	private static boolean alive;

	private TestSerialiser(){}
	
	public static void start(ObjectOutputStream test){
		Assert.isTrue(!alive);
		Assert.isTrue(testSavingQueue.isEmpty());
		TestSerialiser.test = test;
		fragmentTimes = 0;
		alive = true;
		//ExecutorService exeSrv = Executors.newFixedThreadPool(1);
		//exeSrv.execute(singletonTestSerialisationManager);
		singletonTestSerialiser = new TestSerialiser();
		singletonTestSerialiser.setPriority(Thread.MIN_PRIORITY);
		singletonTestSerialiser.start();		
	}
	
	public static void finish(){
		alive = false;
	}
	
	@Override
	public void run(){
		while (alive || !testSavingQueue.isEmpty()){
			while(alive && testSavingQueue.isEmpty()){
				try {
					Thread.sleep(1000); // 1 second
				} catch (InterruptedException e1) {}
			}
			if (!testSavingQueue.isEmpty()){
				Taggable fragment;
				synchronized(testSavingQueue){
					fragment = testSavingQueue.removeFirst();
				}
				writethis(fragment);
			}
		}
		try {
			test.flush();
			test.close();
		} catch (IOException e) {
			LogSerialiser.log("I/O exception serialising test file!\n", LogSerialiser.LogLevel.Critical);
		} finally{
			try {
				test.close();
			} catch (IOException e) {
				LogSerialiser.log("I/O exception closing serialisation of test file!\n", LogSerialiser.LogLevel.Critical);				
			}
		}
		synchronized(test){
			System.out.println("TestSerialiser finished");
			singletonTestSerialiser = null;
			test.notifyAll();
		}
	}
	
	public static void write(Taggable fragment){
		if (alive){
			synchronized(testSavingQueue){
				testSavingQueue.add(fragment);
			}
		}
	}

	private static void writethis(Taggable fragment){
		Assert.notNull(fragment);
		try {
			test.writeObject(fragment);
		} catch (IOException e) {
			LogSerialiser.log("TestSerialiser - exception writing fragment: " + e.getMessage(),LogLevel.Critical);
		}
		fragmentTimes++;
		if (fragmentTimes >= FLUSH_INTERVAL){
			fragmentTimes = 0;
			try {
				test.flush();
				test.reset();
			} catch (IOException e) {
				LogSerialiser.log("TestSerialiser - flushing exception: " + e.getMessage(),LogLevel.Critical);
			}
		}
	}

	public static void exit(){
		if (singletonTestSerialiser != null){
			try {
				synchronized(test){
					while (singletonTestSerialiser != null){
						try {
							test.wait();
						} catch (InterruptedException e) {
							System.out.println("TestSerialiser exit interrupted");
						}
					}
				}
			} catch (Exception e) {} // test may be set to null when we try to sync on it		
			//System.out.println("TestSerialisationManager exited");
			test = null;
		}
	}

	public static int queueLength(){
		return testSavingQueue.size();
	}

}
/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This software is distributed FREE of charge under the TESTAR license, as an open      *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.serialisation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.fruit.Assert;
import org.fruit.alayer.AWTCanvas;

/**
 * SUT screenshots serialiser
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class ScreenshotSerialiser extends Thread {
	
	private static final String OUT_DIR = "output/scrshots/";	
	private static String testSequenceFolder = null;
	private static LinkedList<ScrshotRecord> scrshotSavingQueue =  new LinkedList<ScrshotRecord>();
	private static final int QUEUE_LIMIT = 32;
	private static ScreenshotSerialiser singletonScreenshotSerialiser;
	private static boolean alive, queueBoost;

	private static class ScrshotRecord{
		String scrshotPath;
		AWTCanvas scrshot;
		public ScrshotRecord(String scrshotPath, AWTCanvas scrshot){this.scrshotPath = scrshotPath; this.scrshot = scrshot;}
	}
	
	private ScreenshotSerialiser(){}
	private ScreenshotSerialiser(String testSequenceFolder){
		ScreenshotSerialiser.testSequenceFolder = testSequenceFolder;
		(new File(OUT_DIR + testSequenceFolder)).mkdirs();
	}
		
	public static void start(String testSequenceFolder){
		Assert.isTrue(!alive);
		Assert.isTrue(scrshotSavingQueue.isEmpty());
		alive = true; queueBoost = false;
		singletonScreenshotSerialiser = new ScreenshotSerialiser(testSequenceFolder);
		singletonScreenshotSerialiser.setPriority(Thread.MIN_PRIORITY);
		singletonScreenshotSerialiser.start();
	}
	
	public static void finish(){
		alive = false;
	}
	
	@Override
	public void run(){		
		while (alive || !scrshotSavingQueue.isEmpty()){
			while(alive && scrshotSavingQueue.isEmpty()){
				try {
					Thread.sleep(1000); // 1 second
				} catch (InterruptedException e1) {}
			}
			if (!scrshotSavingQueue.isEmpty()){
				if (!queueBoost && scrshotSavingQueue.size() > QUEUE_LIMIT){
					this.setPriority(NORM_PRIORITY);
					queueBoost = true;
				} else if (queueBoost && scrshotSavingQueue.size() < 10){
					this.setPriority(MIN_PRIORITY);
					queueBoost = false;
				}
				ScrshotRecord r;
				synchronized(scrshotSavingQueue){
					r = scrshotSavingQueue.removeFirst();
				}
				try {
					r.scrshot.saveAsPng(r.scrshotPath);
				} catch (IOException e) {
					LogSerialiser.log("I/O exception saving screenshot <" + r.scrshotPath + ">\n", LogSerialiser.LogLevel.Critical);
				}
			}
		}
		synchronized(testSequenceFolder){
			System.out.println("ScreenshotSerialiser finished");
			singletonScreenshotSerialiser = null;
			testSequenceFolder.notifyAll();
		}
	}
	
	public static String saveStateshot(String stateID, AWTCanvas stateshot){
		String statePath = OUT_DIR + testSequenceFolder + "/" + stateID + ".png";
		if (!new File(statePath).exists())
			savethis(statePath,stateshot);
		return statePath;
	}
	
	public static String saveActionshot(String stateID, String actionID, final AWTCanvas actionshot){
		String actionPath = OUT_DIR + testSequenceFolder + "/" + stateID + "_" + actionID + ".png";
		if (!new File(actionPath).exists())
			savethis(actionPath,actionshot);
		return actionPath;
	}
		
	private static void savethis(String scrshotPath, AWTCanvas scrshot){
		if (alive){
			synchronized(scrshotSavingQueue){
				scrshotSavingQueue.add(new ScrshotRecord(scrshotPath,scrshot));
			}
		}
	}
	
	public static void exit(){
		if (singletonScreenshotSerialiser != null){
			try {
				synchronized(testSequenceFolder){
					while (singletonScreenshotSerialiser != null){
						try {
							testSequenceFolder.wait(10);
						} catch (InterruptedException e) {
							System.out.println("ScreenshotSerialiser exit interrupted");
						}
					}
				}
			} catch (Exception e) {} // testSequenceFolder may be set to null when we try to sync on it
			//System.out.println("ScreenshotManager exited");
			testSequenceFolder = null;
		}
	}	

	public static int queueLength(){
		return scrshotSavingQueue.size();
	}	
	
}

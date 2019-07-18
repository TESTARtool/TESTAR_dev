/***************************************************************************************************
 *
 * Copyright (c) 2015, 2016, 2017, 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/


package es.upv.staq.testar.serialisation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.fruit.Assert;
import org.fruit.alayer.AWTCanvas;

/**
 * SUT screenshots serialiser
 *
 */
public class ScreenshotSerialiser extends Thread {

	public static final String SCRSHOTS = "scrshots";
	private static String testSequenceFolder = null;
	private static String scrshotOutputFolder = null;
	private static LinkedList<ScrshotRecord> scrshotSavingQueue =  new LinkedList<ScrshotRecord>();
	private static final int QUEUE_LIMIT = 16;
	private static ScreenshotSerialiser singletonScreenshotSerialiser;
	private static boolean alive;
	private static boolean queueBoost;

	private static class ScrshotRecord{
		String scrshotPath;
		AWTCanvas scrshot;
		public ScrshotRecord(String scrshotPath, AWTCanvas scrshot){this.scrshotPath = scrshotPath; this.scrshot = scrshot;}
	}

	private ScreenshotSerialiser(){}
	private ScreenshotSerialiser(String scrshotOutputFolder, String testSequenceFolder){
		ScreenshotSerialiser.testSequenceFolder = testSequenceFolder;
		ScreenshotSerialiser.scrshotOutputFolder = scrshotOutputFolder;
		(new File(scrshotOutputFolder + File.separator + testSequenceFolder)).mkdirs();
	}

	public static void start(String outputFolder, String testSequenceFolder){
		Assert.isTrue(!alive);
		Assert.isTrue(scrshotSavingQueue.isEmpty());
		alive = true; queueBoost = false;
		singletonScreenshotSerialiser = new ScreenshotSerialiser(outputFolder, testSequenceFolder);
		singletonScreenshotSerialiser.setPriority(Thread.MIN_PRIORITY);
		singletonScreenshotSerialiser.start();
	}

	public static void finish(){
		alive = false;
	}

	public static boolean isSavingQueueEmpty() {
		if(scrshotSavingQueue.isEmpty())
			return true;
		return false;
	}

	@Override
	public void run(){		
		while (alive || !scrshotSavingQueue.isEmpty()){
			while(alive && scrshotSavingQueue.isEmpty()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {}
			}
			if (!scrshotSavingQueue.isEmpty()){
				if (!queueBoost && scrshotSavingQueue.size() > QUEUE_LIMIT){
					this.setPriority(NORM_PRIORITY);
					queueBoost = true;
				} else if (queueBoost && scrshotSavingQueue.size() < QUEUE_LIMIT/2){
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
			singletonScreenshotSerialiser = null;
			testSequenceFolder.notifyAll();
		}
	}

	public static String saveStateshot(String stateID, AWTCanvas stateshot){
		String statePath = scrshotOutputFolder + File.separator + testSequenceFolder + File.separator + stateID + ".png";
		if (!new File(statePath).exists())
			savethis(statePath,stateshot);
		return statePath;
	}

	public static String saveActionshot(String stateID, String actionID, final AWTCanvas actionshot){
		String actionPath = scrshotOutputFolder + File.separator + testSequenceFolder + File.separator + stateID + "_" + actionID + ".png";
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
			ScreenshotSerialiser.finish();
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
			testSequenceFolder = null;
		}
	}	

	public static int queueLength(){
		return scrshotSavingQueue.size();
	}	

}

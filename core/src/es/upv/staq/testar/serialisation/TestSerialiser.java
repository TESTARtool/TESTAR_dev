/***************************************************************************************************
 *
 * Copyright (c) 2016, 2017, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import org.fruit.Assert;
import org.fruit.alayer.TaggableBase;

import es.upv.staq.testar.serialisation.LogSerialiser.LogLevel;

/**
 * Tests serialiser..
 */
public class TestSerialiser extends Thread {

	private static ObjectOutputStream test;
	private static int fragmentTimes;
	private static final int FLUSH_INTERVAL = 16;
	private static LinkedList<TaggableBase> testSavingQueue =  new LinkedList<>();
	private static final int QUEUE_LIMIT = 16;
	private static TestSerialiser singletonTestSerialiser;
	private static boolean alive;
	private static boolean queueBoost;

	private TestSerialiser(){}

	public static void start(ObjectOutputStream test){
		Assert.isTrue(!alive);
		Assert.isTrue(testSavingQueue.isEmpty());
		TestSerialiser.test = test;
		fragmentTimes = 0;
		alive = true; queueBoost = false;
		singletonTestSerialiser = new TestSerialiser();
		singletonTestSerialiser.setPriority(Thread.MIN_PRIORITY);
		singletonTestSerialiser.start();		
	}

	public static void finish(){
		alive = false;
	}

	public static boolean isSavingQueueEmpty() {
		if(testSavingQueue.isEmpty())
			return true;
		return false;
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
				if (!queueBoost && testSavingQueue.size() > QUEUE_LIMIT){
					this.setPriority(NORM_PRIORITY);
					queueBoost = true;
				} else if (queueBoost && testSavingQueue.size() < QUEUE_LIMIT/2){
					this.setPriority(MIN_PRIORITY);
					queueBoost = false;
				}				
				TaggableBase fragment;
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
			//System.out.println("TestSerialiser finished");
			singletonTestSerialiser = null;
			test.notifyAll();
		}
	}

	public static void write(TaggableBase fragment){
		if (alive){
			synchronized(testSavingQueue){
				testSavingQueue.add(fragment);
			}
		}
	}

	private static void writethis(TaggableBase fragment){
		Assert.notNull(fragment);
		try {
			test.writeObject(fragment);
		} catch (IOException e) {
			LogSerialiser.log("TestSerialiser - exception writing fragment: " + e.getMessage(), LogLevel.Critical);
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
			TestSerialiser.finish();
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

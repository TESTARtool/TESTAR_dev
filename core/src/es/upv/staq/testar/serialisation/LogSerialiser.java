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

import java.io.PrintStream;
import java.util.LinkedList;

import org.fruit.Assert;

/**
 * Logs serialiser.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class LogSerialiser extends Thread {

	private static PrintStream log;
	private static int logLevel;
	private static int logTimes;
	private static final int FLUSH_INTERVAL = 100;
	private static LinkedList<LogRecord> logSavingQueue =  new LinkedList<LogRecord>();
	private static LogSerialiser singletonLogSerialiser;
	private static boolean alive;

	private LogSerialiser(){}
	
	public static void start(PrintStream log, int logLevel){
		Assert.isTrue(!alive);
		Assert.isTrue(logSavingQueue.isEmpty());
		LogSerialiser.log = log;
		LogSerialiser.logLevel = logLevel;
		logTimes = 0;
		alive = true;
		//ExecutorService exeSrv = Executors.newFixedThreadPool(1);
		//exeSrv.execute(singletonLogManager);	
		singletonLogSerialiser = new LogSerialiser();
		singletonLogSerialiser.setPriority(Thread.MIN_PRIORITY);
		singletonLogSerialiser.start();		
	}
	
	public static void finish(){
		alive = false;
	}
	
	// by Sebastian Bauersfeld
	public static enum LogLevel{ 
		Critical(0), Info(1), Debug(2);
		final int significance;
		LogLevel(int significance){ this.significance = significance; }
		public int significance(){ return significance; }
	}
	
	private static class LogRecord{
		public String logS; public LogLevel logL;
		public LogRecord(String logS, LogLevel logL){ this.logS = logS; this.logL = logL;}
	}
	
	@Override
	public void run(){
		while (alive || !logSavingQueue.isEmpty()){
			while(alive && logSavingQueue.isEmpty()){
				try {
					Thread.sleep(1000); // 1 second
				} catch (InterruptedException e1) {}
			}
			if (!logSavingQueue.isEmpty()){
				LogRecord logR;
				synchronized(logSavingQueue){
					logR = logSavingQueue.removeFirst();
				}
				logthis(logR.logS, logR.logL);
			}
		}
		log.flush();
		log.close();
		synchronized(log){
			System.out.println("<" + singletonLogSerialiser.getName() + "> LogSerialiser finished");
			singletonLogSerialiser = null;
			log.notifyAll();
		}
	}
	
	public static void log(String logS){
		if (alive)
			log(logS,LogLevel.Info);
	}
	
	public static void log(String logS, LogLevel logLevel){
		if(alive && logLevel.significance() <= LogSerialiser.logLevel){
			synchronized(logSavingQueue){
				logSavingQueue.add(new LogRecord(logS,logLevel));
			}
		}
	}
	
	private static void logthis(String string, LogLevel level){
		Assert.notNull(log);
		log.print(string);
		logTimes++;
		if (logTimes >= FLUSH_INTERVAL){
			logTimes = 0;
			log.flush();
		}
	}
	
	public static void flush(){
		log.flush();
	}
	
	public static PrintStream getLogStream(){
		return log;
	}
	
	public static void exit(){
		if (singletonLogSerialiser != null){
			LogSerialiser.finish();
			try {
				synchronized(log){
					while (singletonLogSerialiser != null){
						try {
							log.wait(10);
						} catch (InterruptedException e) {
							System.out.println("LogSerialiser exit interrupted");
						}
					}
				}
			} catch (Exception e) {} // log may be set to null when we try to sync on it	
			//System.out.println("LogManager exited");
			log = null;
		}
	}
	
	public static int queueLength(){
		return logSavingQueue.size();
	}
	
}

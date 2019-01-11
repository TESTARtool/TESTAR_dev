/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

  private LogSerialiser() {}

  public static void start(PrintStream log, int logLevel) {
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

  public static void finish() {
    alive = false;
  }

  // by Sebastian Bauersfeld
  public enum LogLevel{
    Critical(0), Info(1), Debug(2);
    final int significance;
    LogLevel(int significance) {
      this.significance = significance;
    }
    public int significance() {
      return significance;
    }
  }

  private static class LogRecord{
    public String logS;
    public LogLevel logL;
    LogRecord(String logS, LogLevel logL) {
      this.logS = logS; this.logL = logL;
    }
  }

  @Override
  public void run() {
    while (alive || !logSavingQueue.isEmpty()) {
      while (alive && logSavingQueue.isEmpty()) {
        try {
          Thread.sleep(1000); // 1 second
        } catch (InterruptedException e1) {}
      }
      if (!logSavingQueue.isEmpty()) {
        LogRecord logR;
        synchronized(logSavingQueue) {
          logR = logSavingQueue.removeFirst();
        }
        logthis(logR.logS, logR.logL);
      }
    }
    log.flush();
    log.close();
    synchronized(log) {
      singletonLogSerialiser = null;
      log.notifyAll();
    }
  }

  public static void log(String logS) {
    if (alive) {
      log(logS,LogLevel.Info);
    }
  }

  public static void log(String logS, LogLevel logLevel) {
    if (alive && logLevel.significance() <= LogSerialiser.logLevel) {
      synchronized(logSavingQueue) {
        logSavingQueue.add(new LogRecord(logS,logLevel));
      }
    }
  }

  private static void logthis(String string, LogLevel level) {
    Assert.notNull(log);
    log.print(string);
    logTimes++;
    if (logTimes >= FLUSH_INTERVAL) {
      logTimes = 0;
      log.flush();
    }
  }

  public static void flush() {
    log.flush();
  }

  public static PrintStream getLogStream() {
    return log;
  }

  public static void exit() {
    if (singletonLogSerialiser != null) {
      LogSerialiser.finish();
      try {
        synchronized(log) {
          while (singletonLogSerialiser != null) {
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

  public static int queueLength() {
    return logSavingQueue.size();
  }
}

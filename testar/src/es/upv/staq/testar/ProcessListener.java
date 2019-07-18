/***************************************************************************************************
 *
 * Copyright (c) 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
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


package es.upv.staq.testar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fruit.Util;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.DefaultProtocol;
import org.fruit.monkey.Settings;
import org.testar.OutputStructure;

public class ProcessListener{

	/**
	 * Check the settings parameters to see if the conditions to activate the process listener are correct
	 * 
	 * @param settings
	 * @return true or false
	 */
	public boolean enableProcessListeners(Settings settings){

		//User doesn't want to enable
		if(!settings.get(ConfigTags.ProcessListenerEnabled))
			return false;

		//Only for SUTs executed with command_line
		if(!settings.get(ConfigTags.SUTConnector).equals("COMMAND_LINE")) {
			System.out.println("INFO: Process Listeners only allowed for Desktop Aplications invoked by SUTConnector: COMMAND_LINE");
			return false;
		}

		String path = settings.get(ConfigTags.SUTConnectorValue);

		//Disabled for browsers
		if(path.contains("chrome.exe") || path.contains("iexplore.exe") || path.contains("firefox.exe") || path.contains("MicrosoftEdge")) {
			System.out.println("INFO: Process Listeners only allowed for Desktop Aplications not working with web browsers");
			return false;
		}

		System.out.println("Process Listener enabled correctly");

		return true;
	}

	/**
	 * If SUT process is invoked through COMMAND_LINE,
	 * this method create threads to work with oracles at the process level. 
	 * 
	 * @param system
	 * @param settings
	 */
	public synchronized void startListeners(SUT system, Settings settings) {
		final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

		//Process Oracles use SuspiciousProcessOutput regular expression from test settings file
		Pattern processOracles = Pattern.compile(settings.get(ConfigTags.SuspiciousProcessOutput), Pattern.UNICODE_CHARACTER_CLASS);
		//Process Logs use ProcessLogs regular expression from test settings file
		Pattern processLogs= Pattern.compile(settings.get(ConfigTags.ProcessLogs), Pattern.UNICODE_CHARACTER_CLASS);

		//Create File to save the logs of these oracles
		File dir = new File(OutputStructure.processListenerDir);
		
		String logProcessListenerName = OutputStructure.processListenerDir
				+ File.separator + OutputStructure.startInnerLoopDateString + "_"
				+ OutputStructure.executedSUTname + "_sequence_" + OutputStructure.sequenceInnerLoopCount;

		//Prepare runnable to read Error buffer
		Runnable readErrors = new Runnable() {
			public void run() {
				try {

					PrintWriter writerError;

					//Get process buffer of the SUT
					BufferedReader input = new BufferedReader(new InputStreamReader(system.get(Tags.StdErr)));

					String actionId = "unknown";
					String ch;
					Matcher matcherOracles, matcherLogs;

					while (system.isRunning() && (ch = input.readLine()) != null)
					{	
						matcherOracles = processOracles.matcher(ch);
						matcherLogs= processLogs.matcher(ch);

						//if the process buffer information matches with the Oracles
						if(processOracles!=null && matcherOracles.matches()) {		

							//Prepare Verdict report
							if(DefaultProtocol.lastExecutedAction!=null)
								actionId=DefaultProtocol.lastExecutedAction.get(Tags.ConcreteID);

							Verdict verdict = new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE,
									"Process Listener suspicious title: '" + ch + ", on Action:	'"+actionId+".");

							//Set that we found an error
							DefaultProtocol.processVerdict = verdict;

							DefaultProtocol.faultySequence = true;

							//Prepare Log report
							String DateString = Util.dateString(DATE_FORMAT);
							System.out.println("SUT StdErr:	" +ch);

							writerError = new PrintWriter(new FileWriter(logProcessListenerName + "_StdErr.log", true));

							writerError.println(DateString+"	on Action:	"+actionId+"	SUT StdErr:	" +ch);
							writerError.flush();
							writerError.close();

						}

						//read all the process buffer information (Previous Oracle has priority)
						else if(processLogs!=null && matcherLogs.matches()) {
							//Prepare Log report
							String DateString = Util.dateString(DATE_FORMAT);
							System.out.println("SUT Log StdErr:	" +ch);

							writerError = new PrintWriter(new FileWriter(logProcessListenerName + "_StdErr.log", true));

							if(DefaultProtocol.lastExecutedAction!=null)
								actionId=DefaultProtocol.lastExecutedAction.get(Tags.ConcreteID);

							writerError.println(DateString+"	on Action:	"+actionId+"	SUT StdErr:	" +ch);
							writerError.flush();
							writerError.close();
						}
					}

					input.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		//Prepare runnable to read Output buffer
		Runnable readOutput = new Runnable() {
			public void run() {
				try {

					PrintWriter writerOut;

					//Get process buffer of the SUT
					BufferedReader input = new BufferedReader(new InputStreamReader(system.get(Tags.StdOut)));

					String actionId = "unknown";
					String ch;
					Matcher matcherOracles, matcherLogs;

					while (system.isRunning() && (ch = input.readLine()) != null)
					{	
						matcherOracles = processOracles.matcher(ch);
						matcherLogs = processLogs.matcher(ch);

						//if the process buffer information matches with the Oracles
						if(processOracles!=null && matcherOracles.matches()) {	

							//Prepare Verdict report
							if(DefaultProtocol.lastExecutedAction!=null)
								actionId=DefaultProtocol.lastExecutedAction.get(Tags.ConcreteID);

							Verdict verdict = new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE,
									"Process Listener suspicious title: '" + ch + ", on Action:	'"+actionId+".");

							//Set that we found an error
							DefaultProtocol.processVerdict = verdict;

							DefaultProtocol.faultySequence = true;

							//Prepare Log report
							String DateString = Util.dateString(DATE_FORMAT);
							System.out.println("SUT StdOut:	" +ch);

							writerOut = new PrintWriter(new FileWriter(logProcessListenerName + "_StdOut.log", true));

							writerOut.println(DateString+"	on Action:	"+ actionId+"	SUT StdOut:	" +ch);
							writerOut.flush();
							writerOut.close();
						}

						//read all the process buffer information (Previous Oracle has priority)
						else if(processLogs!=null && matcherLogs.matches()) {
							//Prepare Log report
							String DateString = Util.dateString(DATE_FORMAT);
							System.out.println("SUT Log StdOut:	" +ch);

							writerOut = new PrintWriter(new FileWriter(logProcessListenerName + "_StdOut.log", true));

							if(DefaultProtocol.lastExecutedAction!=null)
								actionId=DefaultProtocol.lastExecutedAction.get(Tags.ConcreteID);

							writerOut.println(DateString+"	on Action:	"+ actionId+"	SUT StdOut:	" +ch);
							writerOut.flush();
							writerOut.close();
						}
					}

					input.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};  
		//TODO: When a Thread ends its code, it still existing in our TESTAR VM like Thread.State.TERMINATED
		//JVM GC should optimize the memory, but maybe we should implement a different way to create this Threads
		//ThreadPool? ExecutorService processListenerPool = Executors.newFixedThreadPool(2); ?

		new Thread(readErrors).start();
		new Thread(readOutput).start();

		/*int nbThreads =  Thread.getAllStackTraces().keySet().size();
				System.out.println("Number of Threads running on VM: "+nbThreads);*/
	}

}

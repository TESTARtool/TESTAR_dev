/***************************************************************************************************
 *
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
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


package nl.ou.testar;

import es.upv.staq.testar.serialisation.LogSerialiser;
import org.fruit.Util;
import org.fruit.alayer.State;
import org.fruit.alayer.Taggable;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.testar.OutputStructure;

import java.io.*;

import static org.fruit.alayer.Tags.SystemState;

public class FileHandling {


    //TODO move away from abstract, to helper class, call from Default protocol with a setting to turn on/off
    /**
     * Creates a file out of the given state.
     * could be more interesting as XML instead of Java Serialisation
     * @param state
     */
    public static void saveStateSnapshot(final State state, File file){
        try{
                Taggable taggable = new TaggableBase();
                taggable.set(SystemState, state);
                LogSerialiser.log("Saving state snapshot...\n", LogSerialiser.LogLevel.Debug);
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                oos.writeObject(taggable);
                oos.close();
                LogSerialiser.log("Saved state snapshot to " + file.getAbsolutePath() + "\n", LogSerialiser.LogLevel.Info);
        }catch(IOException ioe){
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Making a log file of a sequence
     *
     */
    public static void saveReportPage(String page, File outputFile, int logLevel){
        try {
            LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile))),logLevel);
        } catch (NoSuchTagException e3) {
            e3.printStackTrace();
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
        }
        LogSerialiser.log(page, LogSerialiser.LogLevel.Critical);
        LogSerialiser.flush(); LogSerialiser.finish(); LogSerialiser.exit();
    }

    public static void saveReport(String[] reportPages, String generatedSequence, String outputDir, int logLevel){
        saveReportPage(reportPages[0], new File(outputDir + File.separator + "logs" + File.separator + generatedSequence + "_" + "clusters" + ".log"), logLevel);
        saveReportPage(reportPages[1], new File(outputDir + File.separator + "logs" + File.separator + generatedSequence + "_" + "testable" + ".log"), logLevel);
        saveReportPage(reportPages[2], new File(outputDir + File.separator + "logs" + File.separator + generatedSequence + "_" + "curve" + ".log"), logLevel);
        saveReportPage(reportPages[3], new File(outputDir + File.separator + "logs" + File.separator + generatedSequence + "_" + "stats" + ".log"), logLevel);
    }


    public static void copyClassifiedSequence(String generatedSequence, File currentSeq, Verdict verdict){
        String targetFolder = "";
        final double sev = verdict.severity();
        if (sev == Verdict.SEVERITY_OK)
            targetFolder = "sequences_ok";
        else if (sev == Verdict.SEVERITY_WARNING)
            targetFolder = "sequences_warning";
        else if (sev == Verdict.SEVERITY_SUSPICIOUS_TITLE)
            targetFolder = "sequences_suspicioustitle";
        else if (sev == Verdict.SEVERITY_NOT_RESPONDING)
            targetFolder = "sequences_unresponsive";
        else if (sev == Verdict.SEVERITY_NOT_RUNNING)
            targetFolder = "sequences_unexpectedclose";
        else if (sev == Verdict.SEVERITY_FAIL)
            targetFolder = "sequences_fail";
        else
            targetFolder = "sequences_other";
        LogSerialiser.log("Copying classified sequence (\"" + generatedSequence + "\") to " + targetFolder + " folder...\n", LogSerialiser.LogLevel.Info);
        try {
        	
        	Util.copyToDirectory(currentSeq.getCanonicalPath(),
            		OutputStructure.outerLoopOutputDir + File.separator + "sequences", true);
        	
        	Util.copyToDirectory(currentSeq.getCanonicalPath(),
            		OutputStructure.outerLoopOutputDir + File.separator + targetFolder, true);
            
        } catch (NoSuchTagException e) {
            LogSerialiser.log("No such tag exception copying classified test sequence\n", LogSerialiser.LogLevel.Critical);
        } catch (IOException e) {
            LogSerialiser.log("I/O exception copying classified test sequence\n", LogSerialiser.LogLevel.Critical);
        }
        LogSerialiser.log("Copied classified sequence to output <" + targetFolder + "> directory!\n", LogSerialiser.LogLevel.Debug);
    }
}

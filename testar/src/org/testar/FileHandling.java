/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
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


package org.testar;

import org.testar.serialisation.LogSerialiser;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Taggable;
import org.testar.monkey.alayer.TaggableBase;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

import java.io.*;

import static org.testar.monkey.alayer.Tags.SystemState;

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

    public static void copyClassifiedSequence(String generatedSequence, File currentSeq, Verdict verdict) {
        // Generate target folder name based on severity title
        String targetFolder = "sequences_" + verdict.verdictSeverityTitle().toLowerCase();

        LogSerialiser.log(
                String.format("Copying classified sequence (\"%s\") to %s folder...\n", generatedSequence, targetFolder),
                LogSerialiser.LogLevel.Info
                );

        try {
            // Copy to general "sequences" folder
            copyToOutputDir(currentSeq, "sequences");
            // Copy to specific classification folder
            copyToOutputDir(currentSeq, targetFolder);
        } catch (NoSuchTagException | IOException e) {
            LogSerialiser.log("Error copying classified test sequence: " + e.getMessage() + "\n", LogSerialiser.LogLevel.Critical);
        }

        LogSerialiser.log(
                String.format("Copied classified sequence to output <%s> directory!\n", targetFolder),
                LogSerialiser.LogLevel.Debug
                );
    }

    /**
     * Helper method to copy the sequence file to the specified output directory.
     *
     * @param file The sequence file to copy.
     * @param folderName The target folder name.
     * @throws IOException If an I/O error occurs.
     * @throws NoSuchTagException If the specified tag does not exist.
     */
    private static void copyToOutputDir(File file, String folderName) throws IOException, NoSuchTagException {
        Util.copyToDirectory(
                file.getCanonicalPath(),
                OutputStructure.outerLoopOutputDir + File.separator + folderName,
                true
                );
    }
}

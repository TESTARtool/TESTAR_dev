/***************************************************************************************************
 *
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.protocols.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.OutputStructure;

import org.testar.serialisation.LogSerialiser;

public class WriterExperiments {

    private WriterExperiments() {}

    /**
     * Write the information metrics in the desired filename. 
     *
     */
    public static void writeMetrics(WriterExperimentsParams params) {
        try {
            String metricsFile = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() + File.separator 
                    + OutputStructure.outerLoopName + "_" + params.getFilename() + ".txt";
            FileWriter myWriter = new FileWriter(metricsFile, true);
            if(params.isNewLine()) myWriter.write(params.getInformation() + "\r\n");
            else myWriter.write(params.getInformation());
            myWriter.close();
        } catch (IOException e) {
            LogSerialiser.log("ERROR: Writing Metrics inside " + params.getFilename() + " text file", LogSerialiser.LogLevel.Info);
            System.err.println("ERROR: Writing Metrics inside " + params.getFilename() + " text file");
            e.printStackTrace();
        }
    }

    /**
     * Copy all existing metric files inside the destination Folder. 
     * 
     * @param settings
     * @param destFolder
     * @param ipAddress
     */
    public static void copyMetricsToFolder(Settings settings, String destFolder, String ipAddress) {
        Set<String> metricsFiles = getTxtMetricFiles();
        for(String filename : metricsFiles) {
            copyMetricsToFolder(filename, settings, destFolder, ipAddress);
        }
    }

    /**
     * Check the existing text files from the output folder to get the name of the metric files. 
     * 
     * @return setOfTxtMetricFiles
     */
    private static Set<String> getTxtMetricFiles() {
        Set<String> metricsFiles = new HashSet<>();

        File folder = new File(OutputStructure.outerLoopOutputDir);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().toLowerCase().endsWith(".txt")) {
                metricsFiles.add(listOfFiles[i].getName());
            }
        }

        return metricsFiles;
    }

    /**
     * Copy the specific metric file inside the destination Folder. 
     * 
     * @param filename
     * @param settings
     * @param destFolder
     * @param ipAddress
     */
    private static void copyMetricsToFolder(String filename, Settings settings, String destFolder, String ipAddress) {
        // Create a new directory inside desired destination to store all metrics
        String metricsFolder = destFolder + File.separator + "metrics" + File.separator + settings.get(ConfigTags.ApplicationName, "");
        try {
            Files.createDirectories(Paths.get(metricsFolder));
        } catch (IOException e) {
            LogSerialiser.log("ERROR copyMetricsToFolder: Creating new folder for metrics : " + metricsFolder, LogSerialiser.LogLevel.Info);
            System.err.println("ERROR copyMetricsToFolder: Creating new folder for metrics : " + metricsFolder);
            e.printStackTrace();
            return;
        }

        File srcMetrics = new File(OutputStructure.outerLoopOutputDir + File.separator + filename);
        File destMetrics = new File(metricsFolder + File.separator + ipAddress + "_" + filename);
        try {
            FileUtils.copyFile(srcMetrics, destMetrics);
            System.out.println(String.format("Sucessfull copy %s to %s", srcMetrics, destMetrics));
        } catch (IOException e) {
            LogSerialiser.log("ERROR copyMetricsToFolder: ERROR Metrics : " + destMetrics,
                    LogSerialiser.LogLevel.Info);
            System.err.println("ERROR copyMetricsToFolder: ERROR Metrics : " + destMetrics);
            e.printStackTrace();
        }
    }
}

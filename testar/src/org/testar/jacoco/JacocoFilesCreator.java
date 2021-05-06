/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2021 Open Universiteit - www.ou.nl
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

package org.testar.jacoco;

import java.io.File;
import java.io.IOException;
import org.testar.OutputStructure;

import es.upv.staq.testar.serialisation.LogSerialiser;

/**
 * This class allow users to extract jacoco.exec files from the JVM using MBeanClient.
 * And create the JaCoCO report that contains the coverage information.
 */
public class JacocoFilesCreator {

    /**
     * Call MBeanClient to dump a jacoco.exec action file.
     */
    public static String dumpAndGetJacocoActionFileName(String actionCount) {
        String jacocoFile = "";

        try {
            jacocoFile = MBeanClient.dumpJaCoCoActionStepReport(actionCount);
            if(!jacocoFile.isEmpty()) {
                System.out.println("Extracted: " + new File(jacocoFile).getCanonicalPath());
            } else {
                System.out.println("ERROR: MBeanClient was not able to dump the JaCoCo Action " + actionCount + "exec report");
            }
        } catch (Exception e) {
            System.err.println("ERROR: Reading jacocoFile path: " + jacocoFile);
        }

        return jacocoFile;
    }

    /**
     * Call MBeanClient to dump a jacoco.exec sequence file.
     * Use it if TESTAR has finished executing the actions.
     */
    public static String dumpAndGetJacocoSequenceFileName() {
        String jacocoFile = "";

        try {
            System.out.println("Extract JaCoCo report with MBeanClient...");
            jacocoFile = MBeanClient.dumpJaCoCoSequenceReport();
            if(!jacocoFile.isEmpty()) {
                System.out.println("Extracted: " + new File(jacocoFile).getCanonicalPath());
            } else {
                System.out.println("ERROR: MBeanClient was not able to dump the JaCoCo exec report");
            }
        } catch (Exception e) {
            System.err.println("ERROR: Reading jacocoFile path: " + jacocoFile);
        }

        return jacocoFile;
    }

    /**
     * With the dumped Action jacocoFile create the JaCoCo Action report files.
     * 
     * @param jacocoFile
     * @param actionCount
     */
    public static String createJacocoActionReport(String jacocoFile, String actionCount) {
        try {
            // JaCoCo Action report inside output\SUTexecuted folder
            String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() 
                    + File.separator + "jacoco_reports"
                    + File.separator + OutputStructure.startInnerLoopDateString 
                    + "_" + OutputStructure.executedSUTname
                    + "_sequence_" + OutputStructure.sequenceInnerLoopCount
                    + "_action_" + actionCount;

            return createJacocoReport(jacocoFile, reportDir);
        } catch (IOException e) {
            System.err.println("ERROR creating createJacocoActionReport coverage report");
            e.printStackTrace();
        }
        return "ERROR creating createJacocoActionReport coverage report";
    }

    /**
     * With the current Action jacocoFile and previous Action jacocoFile, 
     * merge both results and create a report file.
     * 
     * @param jacocoFile
     * @param actionCount
     */
    public static String createJacocoActionMergedReport(String jacocoFile, String actionCount) {
        try {
            // JaCoCo Merged report inside output\SUTexecuted folder
            String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() 
                    + File.separator + "jacoco_reports"
                    + File.separator + "merged_" + OutputStructure.startInnerLoopDateString 
                    + "_" + OutputStructure.executedSUTname
                    + "_sequence_" + OutputStructure.sequenceInnerLoopCount
                    + "_action_" + actionCount;

            return createJacocoReport(jacocoFile, reportDir);
        } catch (IOException e) {
            System.err.println("ERROR creating createJacocoActionMergedReport coverage report");
            e.printStackTrace();
        }
        return "ERROR creating createJacocoActionMergedReport coverage report";
    }

    /**
     * With the dumped Sequence jacocoFile create the JaCoCo Sequence report files.
     * 
     * @param jacocoFile
     */
    public static String createJacocoSequenceReport(String jacocoFile) {
        try {
            // JaCoCo Sequence report inside output\SUTexecuted folder
            String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() 
                    + File.separator + "jacoco_reports"
                    + File.separator + OutputStructure.startInnerLoopDateString 
                    + "_" + OutputStructure.executedSUTname
                    + "_sequence_" + OutputStructure.sequenceInnerLoopCount;

            return createJacocoReport(jacocoFile, reportDir);
        } catch (IOException e) {
            System.err.println("ERROR creating createJacocoSequenceReport coverage report");
            e.printStackTrace();
        }
        return "ERROR creating createJacocoSequenceReport coverage report";
    }

    /**
     * With the merged jacocoFile create the JaCoCo Merged report files.
     * 
     * @param jacocoFile
     */
    public static String createJacocoMergedReport(String jacocoFile) {
        try {
            // JaCoCo Merged report inside output\SUTexecuted folder
            String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() 
                    + File.separator + "jacoco_reports"
                    + File.separator + OutputStructure.startInnerLoopDateString 
                    + "_" + OutputStructure.executedSUTname
                    + "_TOTAL_MERGED";

            return createJacocoReport(jacocoFile, reportDir);
        } catch (IOException e) {
            System.err.println("ERROR creating createJacocoMergedReport coverage report");
            e.printStackTrace();
        }
        return "ERROR creating createJacocoMergedReport coverage report";
    }

    /**
     * With the specific jacoco.exec file and the build.xml file,
     * create the JaCoCO report files.
     * 
     * @param jacocoFile
     * @param reportDir
     */
    private static String createJacocoReport(String jacocoFile, String reportDir) {
        try {
            // Using "HTML destdir" inside build.xml -> Creates the directory automatically
            // But using only "CSV destfile" needs to create this directory first
            if(!new File(reportDir).exists()) {
                new File(reportDir).mkdirs();
            }

            // Launch JaCoCo report (build.xml) and overwrite desired parameters
            String antCommand = "cd jacoco && ant report"
                    + " -DjacocoFile=" + new File(jacocoFile).getCanonicalPath()
                    + " -DreportCoverageDir=" + reportDir;

            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", antCommand);
            Process p = builder.start();
            p.waitFor();

            if(!new File(reportDir + File.separator + "report_jacoco.csv").exists()) {
                System.out.println("************************************************");
                System.out.println("ERROR creating JaCoCo report");
                System.out.println("Check: If ant library is installed in the system");
                System.out.println("Command Line: ant -version");
                System.out.println("************************************************");
            } else {
                System.out.println("JaCoCo report created : " + reportDir);
                LogSerialiser.log("JaCoCo report created : " + reportDir, LogSerialiser.LogLevel.Info);
            }

            String coverageInfoCSV = JacocoReportReader.obtainCSVSummary(reportDir);
            System.out.println(coverageInfoCSV);
            LogSerialiser.log(coverageInfoCSV, LogSerialiser.LogLevel.Info);
            return coverageInfoCSV;

        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR creating JaCoCo coverage report");
            e.printStackTrace();
        }

        return "ERROR creating JaCoCo coverage report";
    }
}
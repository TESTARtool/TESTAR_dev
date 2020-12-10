/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.testar.OutputStructure;

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
			System.out.println("ERROR: Reading jacocoFile path: " + jacocoFile);
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
			System.out.println("ERROR: Reading jacocoFile path: " + jacocoFile);
		}

		return jacocoFile;
	}

	/**
	 * With the dumped Action jacocoFile create the JaCoCo Action report files.
	 * 
	 * @param jacocoFile
	 * @param actionCount
	 */
	public static void createJacocoActionReport(String jacocoFile, String actionCount) {
		try {
			// JaCoCo Action report inside output\SUTexecuted folder
			String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() 
					+ File.separator + "JaCoCo_reports"
					+ File.separator + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
					+ "_action_" + actionCount;

			createJacocoReport(jacocoFile, reportDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * With the dumped Sequence jacocoFile create the JaCoCo Sequence report files.
	 * 
	 * @param jacocoFile
	 */
	public static void createJacocoSequenceReport(String jacocoFile) {
		try {
			// JaCoCo Sequence report inside output\SUTexecuted folder
			String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() 
					+ File.separator + "JaCoCo_reports"
					+ File.separator + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname;

			createJacocoReport(jacocoFile, reportDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * With the merged jacocoFile create the JaCoCo Merged report files.
	 * 
	 * @param jacocoFile
	 */
	public static void createJacocoMergedReport(String jacocoFile) {
		try {
			// JaCoCo Merged report inside output\SUTexecuted folder
			String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() 
					+ File.separator + "JaCoCo_reports"
					+ File.separator + "TOTAL_MERGED";

			createJacocoReport(jacocoFile, reportDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * With the specific jacoco.exec file and the build.xml file,
	 * create the JaCoCO report files.
	 * 
	 * @param jacocoFile
	 * @param reportDir
	 */
	private static void createJacocoReport(String jacocoFile, String reportDir) {
		try {
			// Launch JaCoCo report (build.xml) and overwrite desired parameters
			String antCommand = "cd jacoco && ant report"
					+ " -DjacocoFile=" + new File(jacocoFile).getCanonicalPath()
					+ " -DreportCoverageDir=" + reportDir;

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", antCommand);
			Process p = builder.start();
			p.waitFor();

			System.out.println("JaCoCo report created : " + reportDir);

			String coverageInfo = new JacocoReportReader(reportDir).obtainHTMLSummary();
			System.out.println(coverageInfo);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Compress desired folder
	 * https://www.baeldung.com/java-compress-and-uncompress
	 * 
	 * @param jacocoReportFolder
	 */
	public static boolean compressJacocoReportFile(String jacocoReportFolder) {
		try {
			System.out.println("Compressing folder... " + jacocoReportFolder);

			String compressedFile = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() + File.separator + "JacocoReportCompress.zip";

			FileOutputStream fos = new FileOutputStream(compressedFile);
			ZipOutputStream zipOut = new ZipOutputStream(fos);
			File fileToZip = new File(jacocoReportFolder);

			zipFile(fileToZip, fileToZip.getName(), zipOut);
			zipOut.close();
			fos.close();

			System.out.println("OK! Compressed successfully : " + compressedFile);

			return true;
		} catch (Exception e) {
			System.out.println("ERROR Compressing folder: " + jacocoReportFolder);
			e.printStackTrace();
		}
		return false;
	}

	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}
}

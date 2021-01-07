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
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.testar.OutputStructure;

import com.google.common.io.Files;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class JacocoReportReader {
	
	private String jacocoDirReport;
	
	public JacocoReportReader(String jacocoDirReport) {
		this.jacocoDirReport = jacocoDirReport;
	}
	
	/**
	 * HTML report reader
	 * HTML output report creates lot of files because we are creating Action Coverage
	 */
	
	/*public String obtainHTMLSummary() {
		
		File indexHTML = new File(jacocoDirReport + File.separator + "index.html");
		
		try {
			
			String htmlContent = Files.asCharSource(indexHTML, StandardCharsets.UTF_8).read();
			
			Document doc = Jsoup.parse(htmlContent, "", Parser.xmlParser());
			
			Elements coverageTable = doc.getElementsByTag("tfoot");
			
			String tableResults = readCoverageTable(coverageTable);
			
			return tableResults;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "Summarized Coverage report not available";
	}
	
	private String readCoverageTable(Elements coverageTable) {
		Elements tableData = coverageTable.get(0).getElementsByTag("td");
		
		StringBuilder dataContent = new StringBuilder("");
		
		// Total | Missed Instructions | Cov. | Missed Branches | Cov. | Missed | Cxty | Missed | Lines | Missed | Methods | Missed | Classes
		dataContent.append(OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname);
		dataContent.append(": TOTAL | ");
		dataContent.append("Missed Instructions: " + tableData.get(1).ownText() + " | ");
		dataContent.append("Cov: " + tableData.get(2).ownText() + " | ");
		dataContent.append("Missed Branches: " + tableData.get(3).ownText() + " | ");
		dataContent.append("Cov: " + tableData.get(4).ownText() + " | ");
		dataContent.append("Missed: " + tableData.get(5).ownText() + " | ");
		dataContent.append("Cxty: " + tableData.get(6).ownText() + " | ");
		dataContent.append("Missed: " + tableData.get(7).ownText() + " | ");
		dataContent.append("Lines: " + tableData.get(8).ownText() + " | ");
		dataContent.append("Missed: " + tableData.get(9).ownText() + " | ");
		dataContent.append("Methods: " + tableData.get(10).ownText() + " | ");
		dataContent.append("Missed: " + tableData.get(11).ownText() + " | ");
		dataContent.append("Classes: " + tableData.get(12).ownText());
		
		return dataContent.toString();
	}*/
	
	/**
	 * CSV report reader
	 */

	public String obtainCSVSummary() {

	    File reportCSV = new File(jacocoDirReport + File.separator + "report_jacoco.csv");

	    //GROUP,      PACKAGE, CLASS,           INSTRUCTION_MISSED, INSTRUCTION_COVERED, BRANCH_MISSED, BRANCH_COVERED, LINE_MISSED, LINE_COVERED, COMPLEXITY_MISSED, COMPLEXITY_COVERED, METHOD_MISSED, METHOD_COVERED
	    //jacoco ant, default, FileChooserDemo, 191,                208,                 10,            0,              32,          43,           16,                6,                  11,            6
	    int[] totalValues = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // array for columns
	    
        int numberOfTotalClasses = 0;
        int numberOfMissedClasses = 0;

	    try {
	        CSVReader reader = new CSVReader(new FileReader(reportCSV));
	        String[] nextLine;

	        // Check and add all classes coverage values to get the TOTAL one
	        while((nextLine=reader.readNext()) != null) {
	            // extract values from columns 3 to 12
	            for(int i = 3; i <= 12; i++) {
	                if(isIntValue(nextLine[i])) {
	                    totalValues[i] = totalValues[i] + Integer.parseInt(nextLine[i]);
	                }
	            }
	            // Increase number of Total Classes if we have a numeric coverage line
	            if(isIntValue(nextLine[3])){
	                numberOfTotalClasses = numberOfTotalClasses + 1;
	            }
	            // If INSTRUCTION_COVERED is 0, increase Missed Classes
	            if(isIntValue(nextLine[4]) && Integer.parseInt(nextLine[4]) == 0) {
	                numberOfMissedClasses = numberOfMissedClasses + 1;
	            }
	        }

	        reader.close();

	        int INSTRUCTION_MISSED = totalValues[3]; //column 3
	        int INSTRUCTION_COVERED = totalValues[4]; //column 4
	        int TOTAL_INSTRUCTIONS = INSTRUCTION_MISSED + INSTRUCTION_COVERED;

	        int BRANCH_MISSED = totalValues[5]; //column 5
	        int BRANCH_COVERED = totalValues[6]; //column 6
	        int TOTAL_BRANCH = BRANCH_MISSED + BRANCH_COVERED;

	        int LINE_MISSED = totalValues[7]; //column 7
	        int LINE_COVERED = totalValues[8]; //column 8
	        int TOTAL_LINE = LINE_MISSED + LINE_COVERED;

	        int COMPLEXITY_MISSED = totalValues[9]; //column 9
	        int COMPLEXITY_COVERED = totalValues[10]; //column 10
	        int TOTAL_COMPLEXITY = COMPLEXITY_MISSED + COMPLEXITY_COVERED;

	        int METHOD_MISSED = totalValues[11]; //column 11
	        int METHOD_COVERED = totalValues[12]; //column 12
	        int TOTAL_METHOD = METHOD_MISSED + METHOD_COVERED;

	        StringBuilder dataContent = new StringBuilder("");

	        dataContent.append(OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname);
	        dataContent.append(" | ");

	        dataContent.append("Missed Instructions: " + INSTRUCTION_MISSED + " , ");
	        dataContent.append("Total Instructions: " + TOTAL_INSTRUCTIONS + " , ");
	        dataContent.append("Cov Instructions: " + (int)Math.round(INSTRUCTION_COVERED * 100 / TOTAL_INSTRUCTIONS) + "%");
	        dataContent.append(" | ");

	        dataContent.append("Missed Branches: " + BRANCH_MISSED + " , ");
	        dataContent.append("Total Branches: " + TOTAL_BRANCH + " , ");
	        dataContent.append("Cov Branches: " + (int)Math.round(BRANCH_COVERED * 100 / TOTAL_BRANCH) + "%");
	        dataContent.append(" | ");

	        dataContent.append("Missed Lines: " + LINE_MISSED + " , ");
	        dataContent.append("Total Lines: " + TOTAL_LINE + " , ");
	        dataContent.append("Cov Lines: " + (int)Math.round(LINE_COVERED * 100 / TOTAL_LINE) + "%");
	        dataContent.append(" | ");

	        dataContent.append("Missed Cxty: " + COMPLEXITY_MISSED + " , ");
	        dataContent.append("Total Cxty: " + TOTAL_COMPLEXITY + " , ");
	        dataContent.append("Cov Cxty: " + (int)Math.round(COMPLEXITY_COVERED * 100 / TOTAL_COMPLEXITY) + "%");
	        dataContent.append(" | ");

	        dataContent.append("Missed Methods: " + METHOD_MISSED + " , ");
	        dataContent.append("Total Methods: " + TOTAL_METHOD + " , ");
	        dataContent.append("Cov Methods: " + (int)Math.round(METHOD_COVERED * 100 / TOTAL_METHOD) + "%");
	        dataContent.append(" | ");

	        dataContent.append("Total Classes: " + numberOfTotalClasses + " , ");
	        dataContent.append("Missed Classes: " + numberOfMissedClasses);

	        return dataContent.toString();

	    } catch (CsvValidationException | IOException e) {
	        e.printStackTrace();
	    }

	    return "Summarized Coverage CSV report not available";
	}

	private boolean isIntValue(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch(NumberFormatException  e) {
	        // Exception, nothing but we return false
	    }
	    return false;
	}
}

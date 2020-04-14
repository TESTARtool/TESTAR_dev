package org.testar.jacoco;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.testar.OutputStructure;

import com.google.common.io.Files;

public class JacocoReportReader {
	
	private String jacocoDirReport;
	
	public JacocoReportReader(String jacocoDirReport) {
		this.jacocoDirReport = jacocoDirReport;
	}
	
	public String obtainHTMLSummary() {
		
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
	}
	

}

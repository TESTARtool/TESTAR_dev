/***************************************************************************************************
 *
 * Copyright (c) 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 Open Universiteit - www.ou.nl
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

package org.testar.monkey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ViewServerSummary extends HttpServlet {

	private static final long serialVersionUID = 3123000517851562890L;

	private String outputViewDir;
	private List<String> htmlReportsList;

	public ViewServerSummary(String outputViewDir) {
		this.outputViewDir = outputViewDir;
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Track all the selected htmlReportsList the user wants to summarize
		if(request.getAttribute("htmlReportsList") != null && request.getAttribute("htmlReportsList") instanceof List<?>) {
			htmlReportsList = (List<String>) request.getAttribute("htmlReportsList");
		}

		// Perform the filter duplicates of erroneous sequence feature
		String filterDuplicatesParam = request.getParameter("filter-duplicates");
		if(filterDuplicatesParam != null && Boolean.parseBoolean(filterDuplicatesParam)){
			List<String> duplicatedReports = getDuplicatedReports();

			// Write the duplicatedReports as a response
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");

			try (PrintWriter out = response.getWriter()) {
				for (String report : duplicatedReports) {
					out.println(report);
				}
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("/viewSummary.jsp");
		dispatcher.forward(request, response);
	}

	private List<String> getDuplicatedReports() {
		List<String> htmlErroneousReportsList = htmlReportsList.stream()
				.filter(report -> !report.endsWith("OK.html"))
				.collect(Collectors.toList());

		Set<String> uniqueVerdictTexts = new HashSet<>();
		List<String> uniqueHtmlErroneousReportsList = htmlErroneousReportsList.stream()
				.filter(report -> isNotDuplicated(report, uniqueVerdictTexts))
				.collect(Collectors.toList());

		return uniqueHtmlErroneousReportsList;
	}

	private boolean isNotDuplicated(String htmlReport, Set<String> uniqueVerdictTexts) {
		try {
			Document document = Jsoup.parse(new File(htmlReport));
			Element finalVerdictElement = document.getElementById("final-verdict");

			// The content of the "h2" element is the criteria for duplication
			String verdictText = finalVerdictElement.text();

			// Check if the verdict text is encountered for the first time
			if (uniqueVerdictTexts.add(verdictText)) {
				return true; // The report is not duplicated
			} else {
				return false; // The report is duplicated
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Exception parsing HTML Report file: " + htmlReport);
			return false;
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String selectedFile = request.getParameter("selectedFile");

		// Prepare the screenshots in the server folder
		prepareScreenshots(selectedFile);

		// Read the content of the selected HTML file
		String htmlContent = readHtmlContent(selectedFile);

		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(htmlContent);
	}

	private void prepareScreenshots(String filePath) throws IOException {
		File scrshotsServerDir = new File(outputViewDir + File.separator + "scrshots");

		if (!scrshotsServerDir.exists()) {
			scrshotsServerDir.mkdir();
		} else {
			deleteContents(scrshotsServerDir);
		}

		// Prepare the server screenshots
		File scrshotsOriginalDir = getScreenshotsDirectory(filePath);
		FileUtils.copyDirectory(scrshotsOriginalDir, scrshotsServerDir);
	}

	private void deleteContents(File directory) {
		File[] allContents = directory.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				if (file.isDirectory()) {
					deleteContents(file);
				}
				file.delete();
			}
		}
	}

	private File getScreenshotsDirectory(String path) {
		// the sibling scrshots directory is in the same directory as "reports"
		int indexOfReports = path.indexOf("/reports/");
		if (indexOfReports != -1) {
			int lastIndexOfSeparator = path.lastIndexOf("/");
			if (lastIndexOfSeparator != -1) {
				String siblingDirectoryPath = path.substring(0, lastIndexOfSeparator);
				return new File(siblingDirectoryPath.replace("/reports", "/scrshots"));
			}
		}
		return null; // Handle cases where the pattern doesn't match
	}

	private String readHtmlContent(String filePath) throws IOException {
		StringBuilder content = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
		}

		return content.toString();
	}

}

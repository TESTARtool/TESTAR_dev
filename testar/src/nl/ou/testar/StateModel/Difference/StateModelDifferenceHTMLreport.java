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

package nl.ou.testar.StateModel.Difference;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import nl.ou.testar.a11y.reporting.HTMLReporter;

public class StateModelDifferenceHTMLreport {

	private static final String[] HEADER = new String[] {
			"<!DOCTYPE html>",
			"<html>",
			"<style>",
			".container {display: flex;}",
			".float {display:inline-block;}",
			"</style>",
			"<head>",
			"<title>TESTAR State Model difference report</title>",
			"</head>",
			"<body>"
	};

	private String htmlFilename = "DifferenceReport.html";
	private PrintWriter out;

	public StateModelDifferenceHTMLreport(String htmlFilename) {
		this.htmlFilename = htmlFilename;
		try {
			this.out = new PrintWriter(new File(htmlFilename).getCanonicalPath(), HTMLReporter.CHARSET);

			for(String s : HEADER){
				out.println(s);
				out.flush();
			}
		} catch (IOException e) {
			System.out.println("ERROR: Unable to start the State Model Difference Report : " + htmlFilename);
			e.printStackTrace();
		}
	}

	public String getHtmlFileName() {
		return htmlFilename;
	}

	public void startDisappearedAbstractStates(int numberDisappearedAbstractStates) {
		out.println("<h2> Disappeared Abstract States: " + numberDisappearedAbstractStates + "</h2>");
		out.println("<div class=\"container\">");
		out.flush();
	}

	public void addDisappearedAbstractState(String disStateImage) {
		out.println("<div class=<\"float\">");
		out.println("<p><img src=\"" + disStateImage + "\"></p>");
		out.println("<h4> Disappeared Actions of this State, Concrete Description </h4>");
		out.flush();
	}

	public void startNewAbstractStates(int numberNewAbstractStates) {
		out.println("<h2> New Abstract States: " + numberNewAbstractStates + "</h2>");
		out.println("<div class=\"container\">");
		out.flush();
	}

	public void addNewAbstractState(String newStateImage) {
		out.println("<div class=<\"float\">");
		out.println("<p><img src=\"" + newStateImage + "\"></p>");
		out.println("<h4> New Actions Discovered on this State, Concrete Description </h4>");
		out.flush();
	}

	public void addActionDesc(String actionDesc) {
		out.println("<p style=\"color:red;\">" + actionDesc + "</p>");
		out.flush();
	}

	public void startSpecificStateChanges() {
		out.println("<h2> Specific State changes </h2>");
		out.flush();
	}

	public void addSpecificStateChange(String oldStateImage, String newStateImage, String diffStateImage) {
		out.println("<p><img src=\"" + oldStateImage + "\">");
		out.println("<img src=\"" + newStateImage + "\">");
		out.println("<img src=\"" + diffStateImage + "\"></p>");
		out.flush();
	}

	public void addSpecificActionReached(String actionDesc) {
		out.println("<p style=\"color:blue;\">" + "We have reached this State with Action: " + actionDesc + "</p>");
		out.flush();
	}

	public void addSpecificWidgetInfo(String widgetInfo) {
		out.println("<p style=\"color:black;\">" + widgetInfo + "</p>");
		out.flush();
	}

	public void addEndDiv() {
		out.println("</div>");
		out.flush();
	}

	public void closeHTMLreport() {
		out.flush();
		out.close();
	}
}

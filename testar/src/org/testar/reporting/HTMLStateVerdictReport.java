/**
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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
 *
 */

package org.testar.reporting;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

import org.testar.OutputStructure;
import org.testar.ProtocolUtil;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.android.AndroidProtocolUtil;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.alayer.ios.IOSProtocolUtil;
import org.testar.monkey.alayer.webdriver.WdProtocolUtil;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;

public class HTMLStateVerdictReport {

	private static PrintWriter out;
	private static final String REPORT_FILENAME_SEQ = "_sequence_";
	private static final String REPORT_FILENAME_ACT = "_action_";
	private static final String REPORT_FILENAME_AFT = ".html";
	private static String htmlFilename = "";

	private static String stateScreenshot;

	private static final String[] HEADER = new String[] {
			"<!DOCTYPE html>",
			"<html>",
			// Script function that allows to reverse the order of the states
			"<script>",
			"function reverse(){",
			"let direction = document.getElementById('main').style.flexDirection;",
			"if(direction === 'column') document.getElementById('main').style.flexDirection = 'column-reverse';",
			"else document.getElementById('main').style.flexDirection = 'column';}",
			"</script>",
			"<head>",
			"<title>TESTAR execution sequence report</title>",
			"</head>",
			"<body>"
	};

	public static void reportStateVerdict(int actionNumber, State state, Verdict verdict){
		createHtmlReport(actionNumber, verdict);
		writeStateIntoReport(actionNumber, state, verdict);
		writeVerdictIntoReport(state, verdict);
		close();
	}

	private static void createHtmlReport(int actionNumber, Verdict verdict) {
		try {
			htmlFilename = OutputStructure.htmlOutputDir + File.separator 
					+ OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname 
					+ REPORT_FILENAME_SEQ + OutputStructure.sequenceInnerLoopCount
					+ REPORT_FILENAME_ACT + actionNumber
					+ "_" + verdict.description()
					+ "_" + verdict.verdictSeverityTitle() + REPORT_FILENAME_AFT;

			out = new PrintWriter(htmlFilename, "UTF-8");

			for(String s:HEADER) {
				write(s);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private static void writeStateIntoReport(int actionNumber, State state, Verdict verdict){
		try {
			String screenshotName = state.get(Tags.ConcreteID, "NoConcreteIdAvailable") + "_" + actionNumber + "_" + verdict.description();
			stateScreenshot = prepareStateScreenshot(state, screenshotName);

			write("<div id='block' style='display:flex;flex-direction:column'>"); // Open state block container
			write("<h2>State</h2>");

			// Add state URL if exists (<a href="https://example.com" target="_blank">https://example.com</a>)
			if(!state.get(WdTags.WebHref, "").isEmpty()) {
				String stateURL = state.get(WdTags.WebHref);
				write("<a href=\"" + stateURL + "\"target=\"_blank\">" + stateURL + "</a>"); 
			}

			write("<h4>ConcreteID="+state.get(Tags.ConcreteID, "NoConcreteIdAvailable")+"</h4>");
			write("<h4>AbstractID="+state.get(Tags.AbstractID, "NoAbstractIdAvailable")+"</h4>");
			write("<p><img src=\""+stateScreenshot.replaceFirst(".\\/output(.*)scrshots", "../scrshots")+"\"></p>");
			write("</div>"); // Close state block container
		}catch(NullPointerException | NoSuchTagException | IOException e) {
			System.err.println("ERROR: Adding the State in the HTML report");
			write("<h2>ERROR Adding State </h2>");
		}
	}

	private static String prepareStateScreenshot(State state, String screenshotName) throws IOException {
		// Example of output directory "output\2023-03-01_11h44m50s_parabank_3\scrshots\2023-03-01_11h44m50s_parabank_3_sequence_1"
		String sequenceCountDir = "_sequence_" + OutputStructure.sequenceInnerLoopCount;
		String screenshotsDir = OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname + sequenceCountDir;
		String screenshotFile = OutputStructure.screenshotsOutputDir + File.separator + screenshotsDir + File.separator + screenshotName + ".png";

		Shape viewPort = state.get(Tags.Shape, null);
		if(viewPort != null){
			AWTCanvas screenshot;
			if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
				screenshot = WdProtocolUtil.getStateshotBinary(state);
			}
			else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.ANDROID)) {
				screenshot = AndroidProtocolUtil.getStateshotBinary(state);
			}
			else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.IOS)) {
				screenshot = IOSProtocolUtil.getStateshotBinary(state);
			}
			else{
				screenshot = ProtocolUtil.getStateshotBinary(state);
			}
			screenshot.saveAsPng(screenshotFile);
		}

		return screenshotFile;
	}

	private static void writeVerdictIntoReport(State state, Verdict verdict) {
		if(!verdict.getVisualtHighlights().isEmpty()) {
			highlightVerdictState(state, verdict);
		}

		String verdictInfo = verdict.info();
		if(verdict.severity() > Verdict.OK.severity())
			verdictInfo = verdictInfo.replace(Verdict.OK.info(), "");

		write("<div id='block' style='display:flex;flex-direction:column'>"); // Open verdict block container
		write("<h2>Test verdict for this sequence: </h2>");
		for(String info : verdictInfo.split("\n")) {
			write("<h2>" + info + "</h2>");
		}
		write("<h4>Severity: " + verdict.severity() + "</h4>");
		write("</div>"); // Close verdict block container
	}

	/**
	 * If a verdict highlight was defined, paint it in the last state screenshot.
	 * 
	 * @param state
	 */
	private static void highlightVerdictState(State state, Verdict verdict) {
		// Load the image path that exists in the output directory
		File imageFile = new File(stateScreenshot);
		while(!imageFile.exists()) {
			Util.pause(2);
		}
		try {
			// Draw in top of the state screenshot to highlight the erroneous widget
			BufferedImage img = ImageIO.read(imageFile);
			Graphics2D g2d = img.createGraphics();
			g2d.setColor(java.awt.Color.RED);
			g2d.setStroke(new BasicStroke(3));
			for(Rect r : verdict.getVisualtHighlights()) {
				g2d.drawRect((int)r.x(), (int)r.y(), (int)r.width(), (int)r.height());
			}
			g2d.dispose();
			// Save the new image
			ImageIO.write(img, "png", imageFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    private static final String[] FOOTER = new String[] {
            "</body>",
            "</html>"
    };

	private static void close() {
		write("</div>"); // Close the main div container
		for(String s : FOOTER){
			write(s);
		}
		out.close();
	}

	private static void write(String s) {
		out.println(s);
		out.flush();
	}

}
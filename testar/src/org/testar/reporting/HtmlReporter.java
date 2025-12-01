/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.reporting;

import org.apache.commons.lang.StringEscapeUtils;
import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

import javax.imageio.ImageIO;

public class HtmlReporter implements Reporting
{
    private HtmlFormatUtil htmlReportUtil;
    private int innerLoopCounter = 0;

    private final String openStateBlockContainer = "<div class='stateBlock' style='display:flex;flex-direction:column'>";
    private final String openDerivedBlockContainer = "<div class='derivedBlock' style='display:flex;flex-direction:column'>";
    private final String openSelectedBlockContainer = "<div class='selectedBlock' style='display:flex;flex-direction:column'>";
    private final String openVerdictBlockContainer = "<div class='verdictBlock' style='display:flex;flex-direction:column'>";
    
    private final String openBackgroundContainer = "<div class='background'>";
    private final String openCollapsibleContainer = "<div class='collapsibleContent'>";
    private final String closeContainer = "</div>";

    public HtmlReporter(String fileName, boolean replay) //replay or generate mode
    {
    	copyCentralHTML();
    	htmlReportUtil = new HtmlFormatUtil(fileName);

        // Start the header, scripts, and styles of the HTML report
        String headerTitle = "TESTAR execution sequence report";
        htmlReportUtil.addHeader(headerTitle, HtmlHelper.getHtmlScript(), HtmlHelper.getHtmlStyle());

        if(replay)  addReplayHeading();
        else        addGenerateHeading();

        addVideoControls();
    }

    private void copyCentralHTML() {
    	File sourceFile = new File(Main.outputDir + File.separator + "graphs" + File.separator + "!Directory_summarized.html");
    	File destFile = new File(OutputStructure.htmlOutputDir + File.separator + "!Directory_summarized.html");
    	try {
    		Files.copy(sourceFile.toPath(), destFile.toPath());
    	} catch (IOException e) { }
    }
    
    private void addReplayHeading()
    {
        htmlReportUtil.addHeading(1, "TESTAR replay sequence report for file " + ConfigTags.PathToReplaySequence);
    }

    private void addGenerateHeading()
    {
        htmlReportUtil.addHeading(1, "TESTAR execution sequence report for sequence " + OutputStructure.sequenceInnerLoopCount);
        // HTML button to invoke reverse function
        htmlReportUtil.addContent("<button id='reverseButton' onclick='reverse()'>Reverse order</button>");
        // Initialize the main div container to apply the reverse order
        htmlReportUtil.addContent("<div id='main' style='display:flex;flex-direction:column'>");
    }

    private void addVideoControls()
    {
        htmlReportUtil.addContent("<div>");
        htmlReportUtil.addContent("	<div class='topVerdictBlock'>");
        htmlReportUtil.addContent("	</div>");
        htmlReportUtil.addContent("	<div class='controls'>");
        htmlReportUtil.addContent("	  <button id='playPauseBtn' >Pause</button>");
        htmlReportUtil.addContent("	  <button id='prevFrameBtn' >&lt; Prev</button>");
        htmlReportUtil.addContent("	  <button id='nextFrameBtn' >&gt; Next</button>");
        htmlReportUtil.addContent("	  <label>Speed:");
        htmlReportUtil.addContent("		<select id='speedControl'>");
        htmlReportUtil.addContent("		  <option value='2000'>0.5x</option>");
        htmlReportUtil.addContent("		  <option value='1000' selected>1x</option>");
        htmlReportUtil.addContent("		  <option value='500'>2x</option>");
        htmlReportUtil.addContent("		  <option value='250'>4x</option>");
        htmlReportUtil.addContent("		</select>");
        htmlReportUtil.addContent("	  </label>");
        htmlReportUtil.addContent("	  <input type='range' id='frameSlider' min='0' max='0' value='0' />");
        htmlReportUtil.addContent("	</div>");
        htmlReportUtil.addContent("");
        htmlReportUtil.addContent("	<div class='video-block'>");
        htmlReportUtil.addContent("	  <h2>State</h2>");
        htmlReportUtil.addContent("	  <canvas id='stateCanvas' width='800' height='600'></canvas>");
        htmlReportUtil.addContent("	  <div id='stateText'></div>");
        htmlReportUtil.addContent("	</div>");
        htmlReportUtil.addContent("	");
        htmlReportUtil.addContent("	<div class='video-block'>");
        htmlReportUtil.addContent("	  <h2>Previous action</h2>");
        htmlReportUtil.addContent("	  <canvas id='prevActionCanvas' width='800' height='600'></canvas>");
        htmlReportUtil.addContent("	  <div id='prevActionText'></div>");
        htmlReportUtil.addContent("	</div>");
        htmlReportUtil.addContent("</div>");
    }

    @Override
    public void addState(State state)
    {
        String imagePath = prepareScreenshotImagePath(state.get(Tags.ScreenshotPath, "NoScreenshotPathAvailable"));
        String concreteID = state.get(Tags.ConcreteID, "NoConcreteIdAvailable");
        String abstractID = state.get(Tags.AbstractID, "NoAbstractIdAvailable");

        htmlReportUtil.addContent(openStateBlockContainer); // Open state block container
        htmlReportUtil.addContent(openBackgroundContainer); // Open background container

        htmlReportUtil.addHeading(2, "State " + innerLoopCounter);

        // Add state identifiers
        String stateIDs = "AbstractID=" + abstractID +  " || " + "ConcreteID=" + concreteID;
        htmlReportUtil.addHeading(4, stateIDs);

        // Add the timestamp this state was discovered
        Long stateTimeStamp = state.get(Tags.TimeStamp, 0L);
        Instant epochTimeStamp = Instant.ofEpochMilli(stateTimeStamp);
        String formattedTimestamp = DateTimeFormatter.ISO_INSTANT.format(epochTimeStamp);
        long epochMillis = epochTimeStamp.toEpochMilli();
        htmlReportUtil.addHeading(4, "TimeStamp: " + formattedTimestamp + " || Epoch: " + epochMillis);

        // Add state render time if available
        if(state.get(Tags.StateRenderTime, null) != null) {
            String stateRenderTime = "State Render Time: " + state.get(Tags.StateRenderTime) + " ms";
            htmlReportUtil.addHeading(4, stateRenderTime);
        }

        // Add state URL if exists
        if(!state.get(WdTags.WebHref, "").isEmpty()) {
            String stateURL = state.get(WdTags.WebHref, "");
            String htmlStateURL = "<a href='" + stateURL + "' target='_blank'>" + stateURL + "</a>";
            htmlReportUtil.addContent(htmlStateURL);
        }

        // Add state screenshot
        String altText = "screenshot: state=" + innerLoopCounter + ", ConcreteID=" + concreteID+", AbstractID=" + abstractID;
        htmlReportUtil.addParagraph("<img src=\"" + imagePath + "\" alt=\"" + altText + "\">");

        htmlReportUtil.addContent(closeContainer); // Close background container
        htmlReportUtil.addContent(closeContainer); // Close state block container

        innerLoopCounter++;
        htmlReportUtil.writeToFile();
    }

    private String prepareScreenshotImagePath(String path)
    {
        if(path.contains("./output"))
        {
            int indexStart = path.indexOf("./output");
            int indexScrn = path.indexOf("scrshots");
            String replaceString = path.substring(indexStart,indexScrn);
            path = path.replace(replaceString,"../");
        }
        return path.replace("\\", "/"); // ensure forward slashes
    }

    private String getActionString(Action action)
    {
        StringJoiner joiner = new StringJoiner(" || ");

        String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc, "NoActionDescriptionAvailable"));
        joiner.add("<b>"+ escaped +"</b>");
        joiner.add(StringEscapeUtils.escapeHtml(action.toString()));
        joiner.add("ConcreteID=" + action.get(Tags.ConcreteID, "NoConcreteIdAvailable"));
        joiner.add("AbstractID=" + action.get(Tags.AbstractID, "NoAbstractIdAvailable"));

        return joiner.toString();
    }

    @Override
    public void addActions(Set<Action> actions)
    {
        htmlReportUtil.addContent(openDerivedBlockContainer); // Open derived actions block container
        htmlReportUtil.addButton("collapsible", "Click to view the set of derived actions:");
        htmlReportUtil.addContent(openCollapsibleContainer); // Open actions collapsible container

        ArrayList<String> actionStrings = new ArrayList<>();
        for(Action action:actions)
            actionStrings.add(getActionString(action));

        htmlReportUtil.addList(false, actionStrings);

        htmlReportUtil.addContent(closeContainer); // Close actions collapsible container
        htmlReportUtil.addContent(closeContainer); // Close derived actions block container

        htmlReportUtil.writeToFile();
    }

    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions)
    {
        htmlReportUtil.addContent(openDerivedBlockContainer); // Open derived actions block container

        ArrayList<String> actionStrings = new ArrayList<>();
        if(actions.size()==concreteIdsOfUnvisitedActions.size())
        {
            htmlReportUtil.addHeading(4, "Set of actions (all unvisited - a new state):");
            for(Action action : actions)
                actionStrings.add(getActionString(action));
        }
        else if(concreteIdsOfUnvisitedActions.isEmpty())
        {
            htmlReportUtil.addHeading(4, "All actions have been visited, set of available actions:");
            for(Action action : actions)
                actionStrings.add(getActionString(action));
        }
        else
        {
            htmlReportUtil.addHeading(4, concreteIdsOfUnvisitedActions.size()+" out of "+actions.size()+" actions have not been visited yet:");
            for(Action action : actions)
            {
                if(concreteIdsOfUnvisitedActions.contains(action.get(Tags.ConcreteID, "NoConcreteIdAvailable")))
                {
                    //action is unvisited -> showing:
                    actionStrings.add(getActionString(action));
                }
            }
        }
        htmlReportUtil.addList(false, actionStrings);
        htmlReportUtil.addContent(closeContainer); // Close derived actions block container

        htmlReportUtil.writeToFile();
    }

    @Override
    public void addSelectedAction(State state, Action action)
    {
        String screenshotDir = prepareScreenshotImagePath(OutputStructure.screenshotsOutputDir);
        String stateConcreteID = state.get(Tags.ConcreteID, "NoConcreteIdAvailable");
        String actionAbstractID = action.get(Tags.AbstractID, "NoAbstractIdAvailable");
        String actionConcreteID = action.get(Tags.ConcreteID, "NoConcreteIdAvailable");

        String actionPath = screenshotDir + "/"
                + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
                + "_sequence_" + OutputStructure.sequenceInnerLoopCount
                + "/" + stateConcreteID
                + "_" + actionConcreteID + ".png";

        htmlReportUtil.addContent(openSelectedBlockContainer); // Open selected action block container
        htmlReportUtil.addContent(openBackgroundContainer); // Open background container

        htmlReportUtil.addHeading(2, "Selected Action "+innerLoopCounter+" leading to State "+innerLoopCounter);

        String actionIDs = "AbstractID=" + actionAbstractID +  " || " + "ConcreteID=" + actionConcreteID;
        String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc, "NoActionDescriptionAvailable"));
        actionIDs += " || " + escaped;
        htmlReportUtil.addHeading(4, actionIDs);

        if(actionPath.contains("./output"))
            actionPath = actionPath.replace("./output","..");

        actionPath = actionPath.replace("\\", "/");

        String altText = "screenshot: action, ConcreteID=" + actionConcreteID;

        htmlReportUtil.addParagraph("<img src=\"" + actionPath + "\" alt=\"" + altText + "\">");

        htmlReportUtil.addContent(closeContainer); // Close background container
        htmlReportUtil.addContent(closeContainer); // Close executed action block container

        htmlReportUtil.writeToFile();
    }

    @Override
    public void addTestVerdict(State state, Verdict verdict)
    {
    	if(!verdict.getVisualtHighlights().isEmpty()) {
    		highlightVerdictState(state, verdict);
    	}

        String verdictInfo = StringEscapeUtils.escapeHtml(verdict.info());

        if(verdict.severity() > Verdict.OK.severity())
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "").replace("\n", "");

        htmlReportUtil.addContent(openVerdictBlockContainer); // Open verdict block container
        htmlReportUtil.addHeading(2, "Test verdict for this sequence: " + verdictInfo);
        htmlReportUtil.addContent("<pre>");
        htmlReportUtil.addContent(verdict.description());
        htmlReportUtil.addContent("</pre>");
        htmlReportUtil.addHeading(4, "Severity: " + verdict.severity());
        htmlReportUtil.addContent("<h4 id='visualizer-rect' style='display: none;'>Visualizer: " + verdict.visualizer().getShapes() + "</h4>");
        htmlReportUtil.addContent(closeContainer); // Close verdict block container

        htmlReportUtil.appendToFileName("_" + verdict.verdictSeverityTitle());
        htmlReportUtil.writeToFile();
    }

    private void makeGrayScaleImg(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        for(int i=0; i<height; i++) {

           for(int j=0; j<width; j++) {

        	  java.awt.Color c = new java.awt.Color(image.getRGB(j, i));
              int red = (int)(c.getRed() * 0.299);
              int green = (int)(c.getGreen() * 0.587);
              int blue = (int)(c.getBlue() *0.114);
              java.awt.Color newColor = new java.awt.Color(red+green+blue,red+green+blue,red+green+blue);

              image.setRGB(j,i,newColor.getRGB());
           }
        }
    }

    /**
     * If a verdict highlight was defined, paint it in the last state screenshot.
     * 
     * @param state
     */
    private void highlightVerdictState(State state, Verdict verdict) {
    	// Load the image path that exists in the output directory
    	String imagePath = state.get(Tags.ScreenshotPath);
    	File imageFile = new File(imagePath);

        // Retry up to 10 times, pausing 1s between attempts
        for (int i = 0; i < 10 && !imageFile.exists(); i++) {
            Util.pause(1);
        }

        // If after retries the file still doesn't exist, stop
        if (!imageFile.exists()) {
            return;
        }

    	try {
    		// Draw in top of the state screenshot to highlight the erroneous widget
            BufferedImage img = ImageIO.read(imageFile);
            Graphics2D g2d = img.createGraphics();

           // Check if transparant color is used, then make screenshot gray
            for(Rect r : verdict.getVisualtHighlights()) {
    			// If the color is not opaque, fill a Rect with the color
    			if(r.getColor().getAlpha() > 1 && r.getColor().getAlpha() < 255) {
    				makeGrayScaleImg(img);
    				break;
    			}
            }

    		g2d.setStroke(new BasicStroke(3));
    		for(Rect r : verdict.getVisualtHighlights()) {
    			g2d.setColor(r.getColor());
    			// If the color is not opaque, fill a Rect with the color
    			if(r.getColor().getAlpha() > 1 && r.getColor().getAlpha() < 255) {
    				g2d.drawRect((int)r.x(), (int)r.y(), (int)r.width(), (int)r.height());
    				g2d.setColor(new java.awt.Color(r.getColor().getRed(), r.getColor().getGreen(), r.getColor().getBlue(), r.getColor().getAlpha() / 2));
    				g2d.fillRect((int)r.x(), (int)r.y(), (int)r.width(), (int)r.height());
    			} 
    			// Else (is opaque), draw a Rect with the color
    			else {
    				g2d.drawRect((int)r.x(), (int)r.y(), (int)r.width(), (int)r.height());
    			}
    		}
    		g2d.dispose();
    		// Save the new image
    		ImageIO.write(img, "png", imageFile);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    @Override
    public void finishReport()
    {
        htmlReportUtil.addContent("</div>"); // Close the main div container
        htmlReportUtil.addFooter();

        htmlReportUtil.writeToFile();
    }
}

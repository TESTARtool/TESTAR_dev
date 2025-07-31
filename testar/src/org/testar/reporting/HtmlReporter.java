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
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

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
        htmlReportUtil = new HtmlFormatUtil(fileName);

        // Start the header, scripts, and styles of the HTML report
        String headerTitle = "TESTAR execution sequence report";
        htmlReportUtil.addHeader(headerTitle, HtmlHelper.getHtmlScript(), HtmlHelper.getHtmlStyle());

        if(replay)  addReplayHeading();
        else        addGenerateHeading();
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
        htmlReportUtil.addContent("<div id='main' style='display:flex;flex-direction:column-reverse'>");
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
    public void addTestVerdict(Verdict verdict)
    {
        String verdictInfo = verdict.info();
        if(verdict.severity() > Verdict.OK.severity())
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "").replace("\n", "");

        htmlReportUtil.addContent(openVerdictBlockContainer); // Open verdict block container
        htmlReportUtil.addHeading(2, "Test verdict for this sequence: " + verdictInfo);
        htmlReportUtil.addHeading(4, "Severity: " + verdict.severity());
        htmlReportUtil.addContent("<h4 id='visualizer-rect' style='display: none;'>Visualizer: " + verdict.visualizer().getShapes() + "</h4>");
        htmlReportUtil.addContent(closeContainer); // Close verdict block container

        htmlReportUtil.appendToFileName("_" + verdict.verdictSeverityTitle());
        htmlReportUtil.writeToFile();
    }

    @Override
    public void finishReport()
    {
        htmlReportUtil.addContent("</div>"); // Close the main div container
        htmlReportUtil.addFooter();

        htmlReportUtil.writeToFile();
    }
}

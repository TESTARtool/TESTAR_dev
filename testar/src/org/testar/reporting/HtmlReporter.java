/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2024 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2024 Universitat Politecnica de Valencia - www.upv.es
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

import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

public class HtmlReporter implements Reporting
{
    private HtmlFormatUtil htmlReportUtil;
    private int innerLoopCounter = 0;
    
    private final String openBlockContainer = "<div class='block' style='display:flex;flex-direction:column'>";
    private final String closeBlockContainer = "</div>";
    
    public HtmlReporter(String fileName, boolean replay) //replay or generate mode
    {
        htmlReportUtil = new HtmlFormatUtil(fileName);
        
        startReport();
        if(replay)  addReplayHeading();
        else        addGenerateHeading();
    }
    
    private void startReport()
    {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("function reverse(){");
        joiner.add("let direction = document.getElementById('main').style.flexDirection;");
        joiner.add("if(direction === 'column') document.getElementById('main').style.flexDirection = " + "'column-reverse';");
        joiner.add("else document.getElementById('main').style.flexDirection = 'column';}");
        
        htmlReportUtil.addHeader("TESTAR execution sequence report", joiner.toString());
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
    
    @Override
    public void addState(State state)
    {
        String imagePath = prepareScreenshotImagePath(state.get(Tags.ScreenshotPath, "NoScreenshotPathAvailable"));
        String concreteID = state.get(Tags.ConcreteID, "NoConcreteIdAvailable");
        String abstractID = state.get(Tags.AbstractID, "NoAbstractIdAvailable");

        htmlReportUtil.addContent(openBlockContainer); // Open state block container
        htmlReportUtil.addHeading(2, "State " + innerLoopCounter);
        htmlReportUtil.addHeading(4, "ConcreteID=" + concreteID);
        htmlReportUtil.addHeading(4, "AbstractID=" + abstractID);

        String altText = "screenshot: state=" + innerLoopCounter + ", ConcreteID=" + concreteID+", AbstractID=" + abstractID;
        htmlReportUtil.addParagraph("<img src=\"" + imagePath + "\" alt=\"" + altText + "\">");
        htmlReportUtil.addContent(closeBlockContainer); // Close state block container
        
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
        htmlReportUtil.addContent(openBlockContainer); // Open derived actions block container
        htmlReportUtil.addHeading(4, "Set of actions:");
    
        ArrayList<String> actionStrings = new ArrayList<>();
        for(Action action:actions)
            actionStrings.add(getActionString(action));
        
        htmlReportUtil.addList(false, actionStrings);
        htmlReportUtil.addContent(closeBlockContainer); // Close derived actions block container
    
        htmlReportUtil.writeToFile();
    }
    
    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions)
    {
        htmlReportUtil.addContent(openBlockContainer); // Open derived actions block container
        
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
        htmlReportUtil.addContent(closeBlockContainer); // Close derived actions block container
    
        htmlReportUtil.writeToFile();
    }
    
    @Override
    public void addSelectedAction(State state, Action action)
    {
        String screenshotDir = prepareScreenshotImagePath(OutputStructure.screenshotsOutputDir);
        String stateConcreteID = state.get(Tags.ConcreteID, "NoConcreteIdAvailable");
        String actionConcreteID = action.get(Tags.ConcreteID, "NoConcreteIdAvailable");

        String actionPath = screenshotDir + "/"
                            + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
                            + "_sequence_" + OutputStructure.sequenceInnerLoopCount
                            + "/" + stateConcreteID
                            + "_" + actionConcreteID + ".png";
    
        htmlReportUtil.addContent(openBlockContainer); // Open executed action block container
        htmlReportUtil.addHeading(2, "Selected Action "+innerLoopCounter+" leading to State "+innerLoopCounter);
    
        String stateString = "ConcreteID=" + actionConcreteID;
        String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc, "NoActionDescriptionAvailable"));
        stateString += " || " + escaped;
        htmlReportUtil.addHeading(4, stateString);
    
    
        if(actionPath.contains("./output"))
            actionPath = actionPath.replace("./output","..");

        actionPath = actionPath.replace("\\", "/");

        String altText = "screenshot: action, ConcreteID=" + actionConcreteID;
    
        htmlReportUtil.addParagraph("<img src=\"" + actionPath + "\" alt=\"" + altText + "\">");
        htmlReportUtil.addContent(closeBlockContainer); // Close executed action block container
    
        htmlReportUtil.writeToFile();
    }
    
    @Override
    public void addTestVerdict(Verdict verdict)
    {
        String verdictInfo = verdict.info();
        if(verdict.severity() > Verdict.OK.severity())
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "").replace("\n", "");

        htmlReportUtil.addContent(openBlockContainer); // Open verdict block container
        htmlReportUtil.addHeading(2, "Test verdict for this sequence: " + verdictInfo);
        htmlReportUtil.addHeading(4, "Severity: " + verdict.severity());
        htmlReportUtil.addContent(closeBlockContainer); // Close verdict block container

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

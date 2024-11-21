/***************************************************************************************************
 *
 * Copyright (c) 2024 Open Universiteit - www.ou.nl
 * Copyright (c) 2024 Universitat Politecnica de Valencia - www.upv.es
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

public class PlainTextReporter implements Reporting
{
    private PlainTextFormatUtil plainTextReportUtil;
    private int innerLoopCounter = 0;
    public PlainTextReporter(String fileName, boolean replay) //replay or generate mode
    {
        plainTextReportUtil = new PlainTextFormatUtil(fileName);
        
        startReport();
        if(replay)  addReplayHeading();
        else        addGenerateHeading();
    }
    
    private void startReport()
    {
        plainTextReportUtil.addHeading(1, "TESTAR execution sequence report");
    }
    
    private void addReplayHeading()
    {
        plainTextReportUtil.addHeading(2, "TESTAR replay sequence report for file " + ConfigTags.PathToReplaySequence);
    }
    
    private void addGenerateHeading()
    {
        plainTextReportUtil.addHeading(2, "TESTAR execution sequence report for sequence " + OutputStructure.sequenceInnerLoopCount);
    }
    
    @Override
    public void addState(State state)
    {
        String imagePath = prepareScreenshotImagePath(state.get(Tags.ScreenshotPath, "NoScreenshotPathAvailable"));
        String concreteID = state.get(Tags.ConcreteID, "NoConcreteIdAvailable");
        String abstractID = state.get(Tags.AbstractID, "NoAbstractIdAvailable");
        
        plainTextReportUtil.addHeading(3, "State " + innerLoopCounter);
        plainTextReportUtil.addHeading(5, "ConcreteID=" + concreteID);
        plainTextReportUtil.addHeading(5, "AbstractID=" + abstractID);
        
        String altText = "screenshot: state=" + innerLoopCounter + ", ConcreteID=" + concreteID+", AbstractID=" + abstractID;
        plainTextReportUtil.addParagraph("Image: " + imagePath + "\n" + altText);
        
        innerLoopCounter++;
        plainTextReportUtil.writeToFile();
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
        joiner.add(escaped);
        joiner.add(StringEscapeUtils.escapeHtml(action.toString()));
        joiner.add("ConcreteID="+action.get(Tags.ConcreteID, "NoConcreteIdAvailable"));
        joiner.add("AbstractID="+action.get(Tags.AbstractID, "NoAbstractIdAvailable"));
        
        return joiner.toString();
    }
    
    @Override
    public void addActions(Set<Action> actions)
    {
        plainTextReportUtil.addHeading(4, "Set of actions:");
        
        ArrayList<String> actionStrings = new ArrayList<>();
        for(Action action:actions)
            actionStrings.add(getActionString(action));
    
        plainTextReportUtil.addList(false, actionStrings);
    
        plainTextReportUtil.writeToFile();
    }
    
    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions)
    {
        ArrayList<String> actionStrings = new ArrayList<>();
        if(actions.size()==concreteIdsOfUnvisitedActions.size())
        {
            plainTextReportUtil.addHeading(5, "Set of actions (all unvisited - a new state):");
            for(Action action : actions)
                actionStrings.add(getActionString(action));
        }
        else if(concreteIdsOfUnvisitedActions.isEmpty())
        {
            plainTextReportUtil.addHeading(5, "All actions have been visited, set of available actions:");
            for(Action action : actions)
                actionStrings.add(getActionString(action));
        }
        else
        {
            plainTextReportUtil.addHeading(5, concreteIdsOfUnvisitedActions.size()+" out of "+actions.size()+" actions have not been visited yet:");
            for(Action action : actions)
            {
                if(concreteIdsOfUnvisitedActions.contains(action.get(Tags.ConcreteID, "NoConcreteIdAvailable")))
                {
                    //action is unvisited -> showing:
                    actionStrings.add(getActionString(action));
                }
            }
        }
        plainTextReportUtil.addList(false, actionStrings);
    
        plainTextReportUtil.writeToFile();
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
        
        plainTextReportUtil.addHeading(3, "Selected Action "+innerLoopCounter+" leading to State "+innerLoopCounter);
        
        String stateString = "ConcreteID=" + actionConcreteID;
        String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc, "NoActionDescriptionAvailable"));
        stateString += " || " + escaped;
        plainTextReportUtil.addHeading(5, stateString);
        
        
        if(actionPath.contains("./output"))
            actionPath = actionPath.replace("./output","..");
        
        actionPath = actionPath.replace("\\", "/");
        
        String altText = "screenshot: action, ConcreteID=" + actionConcreteID;
        
        plainTextReportUtil.addParagraph("Image: " + actionPath + "\n" + altText);
    
    
        plainTextReportUtil.writeToFile();
    }
    
    @Override
    public void addTestVerdict(Verdict verdict)
    {
        String verdictInfo = verdict.info();
        if(verdict.severity() > Verdict.OK.severity())
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "");
        
        plainTextReportUtil.addHorizontalLine();
        plainTextReportUtil.addHeading(3, "Test verdict for this sequence: " + verdictInfo);
        plainTextReportUtil.addHeading(5, "Severity: " + verdict.severity());
    
        plainTextReportUtil.appendToFileName("_" + verdict.verdictSeverityTitle());
        plainTextReportUtil.writeToFile();
    }
    
    @Override
    public void finishReport()
    {
        plainTextReportUtil.writeToFile();
    }
}

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
    private PlainTextReportUtil plainTextReportUtil;
    private int innerLoopCounter = 0;
    public PlainTextReporter(String fileName, boolean replay) //replay or record mode
    {
        plainTextReportUtil = new PlainTextReportUtil(fileName);
        
        startReport();
        if(replay)  addReplayHeading();
        else        addRecordHeading();
    }
    
    private void startReport()
    {
        plainTextReportUtil.addHeading(1, "TESTAR execution sequence report");
    }
    
    private void addReplayHeading()
    {
        plainTextReportUtil.addHeading(2, "TESTAR replay sequence report for file " + ConfigTags.PathToReplaySequence);
    }
    
    private void addRecordHeading()
    {
        plainTextReportUtil.addHeading(2, "TESTAR execution sequence report for sequence " + OutputStructure.sequenceInnerLoopCount);
    }
    
    @Override
    public void addState(State state)
    {
        String imagePath = prepareScreenshotImagePath(state.get(Tags.ScreenshotPath));
        String concreteIDCustom = state.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable");
        String abstractIDCustom = state.get(Tags.AbstractIDCustom, "NoAbstractIdCustomAvailable");
        
        plainTextReportUtil.addHeading(3, "State " + innerLoopCounter);
        plainTextReportUtil.addHeading(5, "ConcreteIDCustom="+concreteIDCustom);
        plainTextReportUtil.addHeading(5, "AbstractIDCustom="+abstractIDCustom);
        
        String altText = "screenshot: state=" + innerLoopCounter + ", ConcreteIDCustom="+concreteIDCustom+", AbstractIDCustom="+abstractIDCustom;
        plainTextReportUtil.addParagraph("Image: " + imagePath + " - " + altText);
        
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
        
        if(action.get(Tags.Desc)!=null)
        {
            String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
            joiner.add(escaped);
        }
        joiner.add(StringEscapeUtils.escapeHtml(action.toString()));
        joiner.add("ConcreteIDCustom="+action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable"));
        
        if(action.get(Tags.AbstractIDCustom)!=null)
            joiner.add("AbstractIDCustom="+action.get(Tags.AbstractIDCustom));
        
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
                if(concreteIdsOfUnvisitedActions.contains(action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable")))
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
        String stateConcreteIDCustom = state.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable");
        String actionConcreteIDCustom = action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable");
        
        String actionPath = screenshotDir + "/"
                            + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
                            + "_sequence_" + OutputStructure.sequenceInnerLoopCount
                            + "/" + stateConcreteIDCustom
                            + "_" + actionConcreteIDCustom + ".png";
        
        plainTextReportUtil.addHeading(3, "Selected Action "+innerLoopCounter+" leading to State "+innerLoopCounter);
        
        String stateString = "ConcreteIDCustom="+actionConcreteIDCustom;
        if(action.get(Tags.Desc) != null)
        {
            String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
            stateString += " || " + escaped;
        }
        plainTextReportUtil.addHeading(5, stateString);
        
        
        if(actionPath.contains("./output"))
            actionPath = actionPath.replace("./output","..");
        
        actionPath = actionPath.replace("\\", "/");
        
        String altText = "screenshot: action, ConcreteIDCustom="+actionConcreteIDCustom;
        
        plainTextReportUtil.addParagraph("Image: " + actionPath + " - " + altText);
    
    
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
    
        plainTextReportUtil.appendToFileName("_" + verdict.verdictSeverityTitle() + "_poc_");
        plainTextReportUtil.writeToFile();
    }
    
    @Override
    public void finishReport()
    {
        plainTextReportUtil.writeToFile();
    }
}

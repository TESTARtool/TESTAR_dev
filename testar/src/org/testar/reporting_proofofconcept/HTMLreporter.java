package org.testar.reporting_proofofconcept;

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

public class HTMLreporter implements Reporting
{
    private HTMLreportUtil htmlReportUtil;
    private int innerLoopCounter = 0;
    
    private String openBlockContainer = "<div class='block' style='display:flex;flex-direction:column'>";
    private String closeBlockContainer = "</div>";
    
    public HTMLreporter(String fileName, boolean replay) //replay or record mode
    {
        htmlReportUtil = new HTMLreportUtil(fileName);
        
        startReport();
        if(replay)  addReplayHeading();
        else        addRecordHeading();
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
    
    private void addRecordHeading()
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
        String imagePath = state.get(Tags.ScreenshotPath);
        if(imagePath.contains("./output"))
        {
            int indexStart = imagePath.indexOf("./output");
            int indexScrn = imagePath.indexOf("scrshots");
            String replaceString = imagePath.substring(indexStart,indexScrn);
            imagePath = imagePath.replace(replaceString,"../");
        }
        htmlReportUtil.addContent(openBlockContainer); // Open state block container
        htmlReportUtil.addHeading(2, "State " + innerLoopCounter);
        htmlReportUtil.addHeading(4, "ConcreteIDCustom="+state.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable"));
        htmlReportUtil.addHeading(4, "AbstractIDCustom="+state.get(Tags.AbstractIDCustom, "NoAbstractIdCustomAvailable"));
        htmlReportUtil.addParagraph("<img src=\""+imagePath+"\">");
        htmlReportUtil.addContent(closeBlockContainer); // Close state block container
        
        innerLoopCounter++;
        htmlReportUtil.writeToFile();
    }
    
    
    private String getActionString(Action action)
    {
        StringJoiner joiner = new StringJoiner(" || ");
        
        if(action.get(Tags.Desc)!=null)
        {
            String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
            joiner.add("<b>"+ escaped +"</b>");
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
                if(concreteIdsOfUnvisitedActions.contains(action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable")))
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
        String screenshotDir = OutputStructure.screenshotsOutputDir;
    
        if(screenshotDir.contains("./output"))
        {
            int indexStart = screenshotDir.indexOf("./output");
            int indexScrn = screenshotDir.indexOf("scrshots");
            String replaceString = screenshotDir.substring(indexStart,indexScrn);
            screenshotDir = screenshotDir.replace(replaceString,"../");
        }
    
        String actionPath = screenshotDir + "/"
                            + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
                            + "_sequence_" + OutputStructure.sequenceInnerLoopCount
                            + "/" + state.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable")
                            + "_" + action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable") + ".png";
    
        htmlReportUtil.addContent(openBlockContainer); // Open executed action block container
        htmlReportUtil.addHeading(2, "Selected Action "+innerLoopCounter+" leading to State "+innerLoopCounter);
    
        String stateString = "ConcreteIDCustom="+action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable");
        if(action.get(Tags.Desc) != null)
        {
            String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
            stateString += " || " + escaped;
        }
        htmlReportUtil.addHeading(4, stateString);
    
    
        if(actionPath.contains("./output"))
            actionPath = actionPath.replace("./output","..");
    
        htmlReportUtil.addParagraph("<img src=\""+actionPath+"\">");
        htmlReportUtil.addContent(closeBlockContainer); // Close executed action block container
    
        htmlReportUtil.writeToFile();
    }
    
    @Override
    public void addTestVerdict(Verdict verdict)
    {
        String verdictInfo = verdict.info();
        if(verdict.severity() > Verdict.OK.severity())
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "");
    
        htmlReportUtil.addContent(openBlockContainer); // Open verdict block container
        htmlReportUtil.addHeading(2, "Test verdict for this sequence: " + verdictInfo);
        htmlReportUtil.addHeading(4, "Severity: " + verdict.severity());
        htmlReportUtil.addContent(closeBlockContainer); // Close verdict block container
        
        htmlReportUtil.appendToFileName("_" + verdict.verdictSeverityTitle() + "_poc_");
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

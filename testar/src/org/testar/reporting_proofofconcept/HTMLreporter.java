package org.testar.reporting_proofofconcept;

import org.apache.commons.lang.StringEscapeUtils;
import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

public class HTMLreporter implements Reporting
{
    private HTMLreportUtil htmlReportUtil;
    
//    private static final String REPORT_FILENAME_MID = "_sequence_";
    private static String       FINAL_VERDICT_FILENAME = "OK";
    private int innerLoopCounter = 0;
//    private static String       fileName;
    
    public HTMLreporter(String fileName, boolean replay) //replay or record mode
    {
        htmlReportUtil = new HTMLreportUtil(fileName);
        
        startReport();
        if(replay)  addReplayHeading();
        else        addRecordHeading();
    }
    
    private void startReport()
    {
        htmlReportUtil.addHeader();
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("function reverse(){");
        joiner.add("let direction = document.getElementById('main').style.flexDirection;");
        joiner.add("if(direction === 'column') document.getElementById('main').style.flexDirection = " + "'column-reverse';");
        joiner.add("else document.getElementById('main').style.flexDirection = 'column';}");
        htmlReportUtil.addScript(joiner.toString());
        htmlReportUtil.addTitle("TESTAR execution sequence report", true);
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
        htmlReportUtil.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open state block container
        htmlReportUtil.addHeading(2, "State " + innerLoopCounter);
        htmlReportUtil.addHeading(4, "ConcreteIDCustom="+state.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable"));
        htmlReportUtil.addHeading(4, "AbstractIDCustom="+state.get(Tags.AbstractIDCustom, "NoAbstractIdCustomAvailable"));
        htmlReportUtil.addParagraph("<img src=\""+imagePath+"\">");
        htmlReportUtil.addContent("</div>"); // Close state block container
        
        innerLoopCounter++;
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
        htmlReportUtil.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
        htmlReportUtil.addHeading(4, "Set of actions:");
    
        ArrayList<String> actionStrings = new ArrayList<>();
        for(Action action:actions)
            actionStrings.add(getActionString(action));
        
        htmlReportUtil.addList(false, actionStrings);
        htmlReportUtil.addContent("</div>"); // Close derived actions block container
    }
    
    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions)
    {
        htmlReportUtil.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
        
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
        htmlReportUtil.addContent("</div>"); // Close derived actions block container
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
    
        String actionPath = screenshotDir + File.separator
                            + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
                            + "_sequence_" + OutputStructure.sequenceInnerLoopCount
                            + File.separator + state.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable")
                            + "_" + action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable") + ".png";
    
        htmlReportUtil.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open executed action block container
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
        htmlReportUtil.addContent("</div>"); // Close executed action block container
    }
    
    @Override
    public void addTestVerdict(Verdict verdict)
    {
        String verdictInfo = verdict.info();
        if(verdict.severity() > Verdict.OK.severity())
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "");
    
        htmlReportUtil.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open verdict block container
        htmlReportUtil.addHeading(2, "Test verdict for this sequence: " + verdictInfo);
        htmlReportUtil.addHeading(4, "Severity: " + verdict.severity());
        htmlReportUtil.addContent("</div>"); // Close verdict block container
    
        FINAL_VERDICT_FILENAME = verdict.verdictSeverityTitle();
    }
    
    @Override
    public void generateReport()
    {
        htmlReportUtil.addContent("</div>"); // Close the main div container
        htmlReportUtil.addFooter();
        //rename file before writing report
        String fileName = htmlReportUtil.getFileName();
        String htmlFilenameVerdict = fileName.replace(".html", "_" + FINAL_VERDICT_FILENAME + "_New_" + ".html");
        htmlReportUtil.renameFile(htmlFilenameVerdict);
    
        htmlReportUtil.writeReport();
    }
}

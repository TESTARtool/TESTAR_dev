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

public class ReportManager
{
    private HTMLreporter htmlReporter;
    private PlainTextReporter plainTextReporter;
    
    private boolean firstStateAdded = false;
    private boolean firstActionsAdded = false;
    private static final String REPORT_FILENAME_MID = "_sequence_";
//    private static final String REPORT_FILENAME_AFT = ".html";
    private static String       filename;
    private static String       FINAL_VERDICT_FILENAME = "OK";
    
    private int innerLoopCounter = 0;
    
    public ReportManager(boolean replay, boolean html, boolean plainText)
    {
        //TODO put filename into settings, name with sequence number
        // creating a new file for the report
        filename = OutputStructure.htmlOutputDir + File.separator + OutputStructure.startInnerLoopDateString + "_"
        + OutputStructure.executedSUTname + REPORT_FILENAME_MID + OutputStructure.sequenceInnerLoopCount;
//            + REPORT_FILENAME_AFT; // <- file extension is taken care of by individual reporters
    
        if(html)
        {
            htmlReporter = new HTMLreporter(filename);
            startHtmlReport();
            if(replay)
            {
                htmlReporter.addHeading(1, "TESTAR replay sequence report for file " + ConfigTags.PathToReplaySequence);
            }
            else //record
            {
                htmlReporter.addHeading(1, "TESTAR execution sequence report for sequence " + OutputStructure.sequenceInnerLoopCount);
                // HTML button to invoke reverse function
                htmlReporter.addContent("<button id='reverseButton' onclick='reverse()'>Reverse order</button>");
                // Initialize the main div container to apply the reverse order
                htmlReporter.addContent("<div id='main' style='display:flex;flex-direction:column'>");
            }
        }
        if(plainText)
            plainTextReporter = new PlainTextReporter(filename);
        
    }
    
    public void generateReport()
    {
        if(htmlReporter != null)
        {
            htmlReporter.addContent("</div>"); // Close the main div container
            htmlReporter.addFooter();
            //rename file before writing report
            String htmlFilenameVerdict = filename.replace(".html", "_" + FINAL_VERDICT_FILENAME + ".html");
            htmlReporter.setFile(new File(htmlFilenameVerdict));
            
            htmlReporter.writeReport();
        }
        if(plainTextReporter != null)
            plainTextReporter.writeReport();
    }
    
    private void startHtmlReport()
    {
        htmlReporter.addHeader();
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("function reverse(){");
        joiner.add("let direction = document.getElementById('main').style.flexDirection;");
        joiner.add("if(direction === 'column') document.getElementById('main').style.flexDirection = 'column-reverse';");
        joiner.add("else document.getElementById('main').style.flexDirection = 'column';}");
        htmlReporter.addScript(joiner.toString());
        htmlReporter.addTitle("TESTAR execution sequence report", true);
    }
    
    
    public void addState(State state)
    {
        if(firstStateAdded)
        {
            if(firstActionsAdded)
                addStateIntoReport(state);
            else if(state.get(Tags.OracleVerdict, Verdict.OK).severity() > Verdict.SEVERITY_OK)
            {
                //if the first state contains a failure, write the same state in case it was a login
                addStateIntoReport(state);
            }
            else
            {
                //don't write the state as it is the same - getState is run twice in the beginning, before the first action
            }
        }
        else
        {
            firstStateAdded = true;
            addStateIntoReport(state);
        }
    }
    
    private void addStateIntoReport(State state)
    {
        String imagePath = state.get(Tags.ScreenshotPath);
        if(imagePath.contains("./output"))
        {
            int indexStart = imagePath.indexOf("./output");
            int indexScrn = imagePath.indexOf("scrshots");
            String replaceString = imagePath.substring(indexStart,indexScrn);
            imagePath = imagePath.replace(replaceString,"../");
        }
        htmlReporter.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open state block container
        htmlReporter.addHeading(2, "State "+innerLoopCounter);
        htmlReporter.addHeading(4, "ConcreteIDCustom="+state.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable"));
        htmlReporter.addHeading(4, "AbstractIDCustom="+state.get(Tags.AbstractIDCustom, "NoAbstractIdCustomAvailable"));
        htmlReporter.addParagraph("<img src=\""+imagePath+"\">");
        htmlReporter.addContent("</div>"); // Close state block container
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
    
    public void addActions(Set<Action> actions)
    {
        if(!firstActionsAdded) firstActionsAdded = true;
        htmlReporter.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
        htmlReporter.addHeading(4, "Set of actions:");
    
        ArrayList<String> actionStrings = new ArrayList<>();
        for(Action action:actions)
        {
            actionStrings.add(getActionString(action));
        }
        htmlReporter.addList(false, actionStrings);
        htmlReporter.addContent("</div>"); // Close derived actions block container
    }
    
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions)
    {
        if(!firstActionsAdded) firstActionsAdded = true;
        if(actions.size()==concreteIdsOfUnvisitedActions.size())
        {
            htmlReporter.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
            htmlReporter.addHeading(4, "Set of actions (all unvisited - a new state):");
    
            ArrayList<String> actionStrings = new ArrayList<>();
            for(Action action:actions)
            {
                actionStrings.add(getActionString(action));
            }
            htmlReporter.addList(false, actionStrings);
            htmlReporter.addContent("</div>"); // Close derived actions block container
        }
        else if(concreteIdsOfUnvisitedActions.isEmpty())
        {
            htmlReporter.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
            htmlReporter.addHeading(4, "All actions have been visited, set of available actions:");

            ArrayList<String> actionStrings = new ArrayList<>();
            for(Action action:actions)
            {
                actionStrings.add(getActionString(action));
            }
            htmlReporter.addList(false, actionStrings);
            htmlReporter.addContent("</div>"); // Close derived actions block container
        }
        else
        {
            htmlReporter.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
            htmlReporter.addHeading(4, concreteIdsOfUnvisitedActions.size()+" out of "+actions.size()+" actions have not been visited yet:");
            ArrayList<String> actionStrings = new ArrayList<>();
            for(Action action:actions)
            {
                if(concreteIdsOfUnvisitedActions.contains(action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable")))
                {
                    //action is unvisited -> showing:
                    actionStrings.add(getActionString(action));
                }
            }
            htmlReporter.addList(false, actionStrings);
            htmlReporter.addContent("</div>"); // Close derived actions block container
        }
    }
    
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
        
        htmlReporter.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open executed action block container
        htmlReporter.addHeading(2, "Selected Action "+innerLoopCounter+" leading to State "+innerLoopCounter);
        
        String stateString = "ConcreteIDCustom="+action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable");
        if(action.get(Tags.Desc) != null)
        {
            String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
            stateString += " || " + escaped;
        }
        htmlReporter.addHeading(4, stateString);
        
        
        if(actionPath.contains("./output"))
            actionPath = actionPath.replace("./output","..");
        
        htmlReporter.addParagraph("<img src=\""+actionPath+"\">");
        htmlReporter.addContent("</div>"); // Close executed action block container
    }
    
    public void addTestVerdict(Verdict verdict)
    {
        String verdictInfo = verdict.info();
        if(verdict.severity() > Verdict.OK.severity())
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "");
        
        htmlReporter.addContent("<div id='block' style='display:flex;flex-direction:column'>"); // Open verdict block container
        htmlReporter.addHeading(2, "Test verdict for this sequence: " + verdictInfo);
        htmlReporter.addHeading(4, "Severity: " + verdict.severity());
        htmlReporter.addContent("</div>"); // Close verdict block container
        
        FINAL_VERDICT_FILENAME = verdict.verdictSeverityTitle();
    }
}

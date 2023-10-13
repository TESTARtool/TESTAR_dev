package org.testar.reporting_proofofconcept;

import org.testar.OutputStructure;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;

import java.io.File;
import java.util.Set;

public class ReportManager implements Reporting
{
    private HTMLreporter                   htmlReporter;
//    private PlainTextReporter   plainTextReporter;
    
    private boolean firstStateAdded = false;
    private boolean firstActionsAdded = false;
    
    public ReportManager(boolean replay, boolean html, boolean plainText)
    {
        //TODO put filename into settings, name with sequence number
        // creating a new file for the report
        String fileName =
                OutputStructure.htmlOutputDir + "/" + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + "_sequence_" + OutputStructure.sequenceInnerLoopCount + "_poc"; //no File.separator
        
        if(html)
            htmlReporter = new HTMLreporter(fileName, replay);
        
//        if(plainText)
//            plainTextReporter = new PlainTextReporter(filename, replay);
    }
    
    public void finishReport()
    {
        if(htmlReporter != null)
            htmlReporter.finishReport();
        
//        if(plainTextReporter != null)
//            plainTextReporter.generateReport();
    }
    
    public void addState(State state)
    {
        if(firstStateAdded)
        {
            if(firstActionsAdded)
            {
                if(htmlReporter != null)
                    htmlReporter.addState(state);
            }
            else if(state.get(Tags.OracleVerdict, Verdict.OK).severity() > Verdict.SEVERITY_OK)
            {
                //if the first state contains a failure, write the same state in case it was a login
                if(htmlReporter != null)
                    htmlReporter.addState(state);
            }
            else
            {
                //don't write the state as it is the same - getState is run twice in the beginning, before the first action
            }
        }
        else
        {
            firstStateAdded = true;
            if(htmlReporter != null)
                htmlReporter.addState(state);
        }
    }
    
    
    public void addActions(Set<Action> actions)
    {
        firstActionsAdded = true;
    
        if(htmlReporter != null)
            htmlReporter.addActions(actions);
    }
    
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions)
    {
        firstActionsAdded = true;
    
        if(htmlReporter != null)
            htmlReporter.addActionsAndUnvisitedActions(actions, concreteIdsOfUnvisitedActions);
//        if(plainTextReporter != null)
//            plainTextReporter.addActionsAndUnvisitedActions(actions, concreteIdsOfUnvisitedActions);
    }
    
    public void addSelectedAction(State state, Action action)
    {
        if(htmlReporter != null)
            htmlReporter.addSelectedAction(state, action);
//        if(plainTextReporter != null)
//            plainTextReporter.addSelectedAction(state, action);
    }
    
    public void addTestVerdict(Verdict verdict)
    {
        if(htmlReporter != null)
            htmlReporter.addTestVerdict(verdict);
//        if(plainTextReporter != null)
//            plainTextReporter.addTestVerdict(verdict);
    }
}

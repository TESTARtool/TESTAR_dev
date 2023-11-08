package org.testar.reporting_proofofconcept;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;

import java.util.Set;

public class PlainTextReporter implements Reporting
{
    public PlainTextReporter(String fileName, boolean replay) {}
    
    @Override
    public void addState(State state)
    {
    
    }
    
    @Override
    public void addActions(Set<Action> actions)
    {
    
    }
    
    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions)
    {
    
    }
    
    @Override
    public void addSelectedAction(State state, Action action)
    {
    
    }
    
    @Override
    public void addTestVerdict(Verdict verdict)
    {
    
    }
    
    @Override
    public void finishReport()
    {
    
    }
}

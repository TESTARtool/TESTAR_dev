/***************************************************************************************************
 *
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.settings.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class ReportManager implements Reporting
{
    private ArrayList<Reporting> reporters;
    private boolean reportingEnabled = true;
    private boolean firstStateAdded = false;
    private boolean firstActionsAdded = false;
    private String fileName;

    public String getReportFileName()
    {
    	return fileName;
    }

    public ReportManager(boolean replay, Settings settings)
    {
        //TODO put filename into settings, name with sequence number
        // creating a new file for the report
        fileName = OutputStructure.htmlOutputDir + File.separator + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + "_sequence_" + OutputStructure.sequenceInnerLoopCount;
        
        boolean html = settings.get(ConfigTags.ReportInHTML);
        boolean plainText = settings.get(ConfigTags.ReportInPlainText);
        
        if(!Arrays.asList(html, plainText).contains(Boolean.TRUE)) //if none of the options are true
        {
            reportingEnabled = false;
        }
        else
        {
            reporters = new ArrayList<>();
    
            if(html)
                reporters.add(new HtmlReporter(fileName, replay));
            if(plainText)
                reporters.add(new PlainTextReporter(fileName, replay));
        }
    }
    
    public void finishReport()
    {
        if(reportingEnabled)
            for(Reporting reporter : reporters)
                reporter.finishReport();
    }
    
    public void addState(State state)
    {
        if(reportingEnabled)
        {
            if(firstStateAdded)
            {
                if(firstActionsAdded || (state.get(Tags.OracleVerdict, Verdict.OK).severity() > Verdict.Severity.OK.getValue()))
                { //if the first state contains a failure, write the same state in case it was a login
                    for(Reporting reporter : reporters)
                        reporter.addState(state);
                } //no else branch: don't write the state as it is the same - getState is run twice in the beginning, before the first action
            }
            else
            {
                firstStateAdded = true;
                for(Reporting reporter : reporters)
                    reporter.addState(state);
            }
        }
    }
    
    public void addActions(Set<Action> actions)
    {
        if(reportingEnabled)
        {
            firstActionsAdded = true;
    
            for(Reporting reporter : reporters)
                reporter.addActions(actions);
        }
    }
    
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions)
    {
        if(reportingEnabled)
        {
            firstActionsAdded = true;
    
            for(Reporting reporter : reporters)
                reporter.addActionsAndUnvisitedActions(actions, concreteIdsOfUnvisitedActions);
        }
    }
    
    public void addSelectedAction(State state, Action action)
    {
        if(reportingEnabled)
            for(Reporting reporter : reporters)
                reporter.addSelectedAction(state, action);
    }
    
    public void addTestVerdict(Verdict verdict)
    {
        if(reportingEnabled)
            for(Reporting reporter : reporters)
                reporter.addTestVerdict(verdict);
    }
}

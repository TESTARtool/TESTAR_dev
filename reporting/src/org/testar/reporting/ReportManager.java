/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.reporting;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.testar.OutputStructure;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;

public class ReportManager implements Reporting {
    private ArrayList<Reporting> reporters;
    private boolean reportingEnabled = true;
    private boolean firstStateAdded = false;
    private boolean firstActionsAdded = false;
    private String fileName;

    public String getReportFileName() {
        return fileName;
    }

    public ReportManager(Settings settings) {
        //TODO put filename into settings, name with sequence number
        // creating a new file for the report
        fileName = OutputStructure.htmlOutputDir + File.separator + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + "_sequence_" + OutputStructure.sequenceInnerLoopCount;

        boolean html = settings.get(ConfigTags.ReportInHTML);
        boolean plainText = settings.get(ConfigTags.ReportInPlainText);

        if (!Arrays.asList(html, plainText).contains(Boolean.TRUE)) {
            // if none of the options are true
            reportingEnabled = false;
        } else {
            reporters = new ArrayList<>();

            if (html) {
                reporters.add(new HtmlReporter(fileName));
            }
            if (plainText) {
                reporters.add(new PlainTextReporter(fileName));
            }
        }
    }

    public void finishReport() {
        if (reportingEnabled) {
            for (Reporting reporter : reporters) {
                reporter.finishReport();
            }
        }
    }

    public void addState(State state) {
        if (reportingEnabled) {
            if (firstStateAdded) {
                List<Verdict> verdicts = state.get(Tags.OracleVerdicts, Collections.singletonList(Verdict.OK));
                if (firstActionsAdded || !Verdict.helperAreAllVerdictsOK(verdicts)) {
                    //if the first state contains a failure, write the same state in case it was a login
                    for (Reporting reporter : reporters) {
                        reporter.addState(state);
                    }
                }
                //no else branch: don't write the state as it is the same - getState is run twice in the beginning, before the first action
            } else {
                firstStateAdded = true;
                for (Reporting reporter : reporters) {
                    reporter.addState(state);
                }
            }
        }
    }

    public void addActions(Set<Action> actions) {
        if (reportingEnabled) {
            firstActionsAdded = true;

            for (Reporting reporter : reporters) {
                reporter.addActions(actions);
            }
        }
    }

    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions) {
        if (reportingEnabled) {
            firstActionsAdded = true;

            for (Reporting reporter : reporters) {
                reporter.addActionsAndUnvisitedActions(actions, concreteIdsOfUnvisitedActions);
            }
        }
    }

    public void addSelectedAction(State state, Action action) {
        if (reportingEnabled) {
            for (Reporting reporter : reporters) {
                reporter.addSelectedAction(state, action);
            }
        }
    }

    public void addTestVerdicts(List<Verdict> verdicts) {
        if (reportingEnabled) {
            for (Reporting reporter : reporters) {
                reporter.addTestVerdicts(verdicts);
            }
        }
    }
}

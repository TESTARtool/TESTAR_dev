/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.commons.lang.StringEscapeUtils;
import org.testar.OutputStructure;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;

public class PlainTextReporter implements Reporting {
    private PlainTextFormatUtil plainTextReportUtil;
    private int innerLoopCounter = 0;
    private boolean deleteBaseReport = false;
    private String baseReportPath;

    public PlainTextReporter(String fileName) {
        plainTextReportUtil = new PlainTextFormatUtil(fileName);

        startReport();
        addGenerateHeading();
    }

    private void startReport() {
        plainTextReportUtil.addHeading(1, "TESTAR execution sequence report");
    }

    private void addGenerateHeading() {
        plainTextReportUtil.addHeading(2,
                "TESTAR execution sequence report for sequence " + OutputStructure.sequenceInnerLoopCount);
    }

    @Override
    public void addState(State state) {
        String imagePath = prepareScreenshotImagePath(state.get(Tags.ScreenshotPath, "NoScreenshotPathAvailable"));
        String concreteID = state.get(Tags.ConcreteID, "NoConcreteIdAvailable");
        String abstractID = state.get(Tags.AbstractID, "NoAbstractIdAvailable");

        plainTextReportUtil.addHeading(3, "State " + innerLoopCounter);
        plainTextReportUtil.addHeading(5, "ConcreteID=" + concreteID);
        plainTextReportUtil.addHeading(5, "AbstractID=" + abstractID);

        String altText = "screenshot: state=" + innerLoopCounter + ", ConcreteID=" + concreteID
                + ", AbstractID=" + abstractID;
        plainTextReportUtil.addParagraph("Image: " + imagePath + "\n" + altText);

        innerLoopCounter++;
        plainTextReportUtil.writeToFile();
    }

    private String prepareScreenshotImagePath(String path) {
        if (path.contains("./output")) {
            int indexStart = path.indexOf("./output");
            int indexScrn = path.indexOf("scrshots");
            String replaceString = path.substring(indexStart, indexScrn);
            path = path.replace(replaceString, "../");
        }
        return path.replace("\\", "/"); // ensure forward slashes
    }

    private String getActionString(Action action) {
        StringJoiner joiner = new StringJoiner(" || ");

        String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc, "NoActionDescriptionAvailable"));
        joiner.add(escaped);
        joiner.add(StringEscapeUtils.escapeHtml(action.toString()));
        joiner.add("ConcreteID=" + action.get(Tags.ConcreteID, "NoConcreteIdAvailable"));
        joiner.add("AbstractID=" + action.get(Tags.AbstractID, "NoAbstractIdAvailable"));

        return joiner.toString();
    }

    @Override
    public void addActions(Set<Action> actions) {
        plainTextReportUtil.addHeading(4, "Set of actions:");

        ArrayList<String> actionStrings = new ArrayList<>();
        for (Action action : actions) {
            actionStrings.add(getActionString(action));
        }

        plainTextReportUtil.addList(false, actionStrings);

        plainTextReportUtil.writeToFile();
    }

    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions) {
        ArrayList<String> actionStrings = new ArrayList<>();
        if (actions.size() == concreteIdsOfUnvisitedActions.size()) {
            plainTextReportUtil.addHeading(5, "Set of actions (all unvisited - a new state):");
            for (Action action : actions) {
                actionStrings.add(getActionString(action));
            }
        } else if (concreteIdsOfUnvisitedActions.isEmpty()) {
            plainTextReportUtil.addHeading(5, "All actions have been visited, set of available actions:");
            for (Action action : actions) {
                actionStrings.add(getActionString(action));
            }
        } else {
            plainTextReportUtil.addHeading(5, concreteIdsOfUnvisitedActions.size() + " out of " + actions.size()
                    + " actions have not been visited yet:");
            for (Action action : actions) {
                if (concreteIdsOfUnvisitedActions.contains(action.get(Tags.ConcreteID, "NoConcreteIdAvailable"))) {
                    //action is unvisited -> showing:
                    actionStrings.add(getActionString(action));
                }
            }
        }
        plainTextReportUtil.addList(false, actionStrings);

        plainTextReportUtil.writeToFile();
    }

    @Override
    public void addSelectedAction(State state, Action action) {
        String screenshotDir = prepareScreenshotImagePath(OutputStructure.screenshotsOutputDir);
        String stateConcreteID = state.get(Tags.ConcreteID, "NoConcreteIdAvailable");
        String actionConcreteID = action.get(Tags.ConcreteID, "NoConcreteIdAvailable");

        String actionPath = screenshotDir + "/"
                + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
                + "_sequence_" + OutputStructure.sequenceInnerLoopCount
                + "/" + stateConcreteID
                + "_" + actionConcreteID + ".png";

        plainTextReportUtil.addHeading(3,
                "Selected Action " + innerLoopCounter + " leading to State " + innerLoopCounter);

        String stateString = "ConcreteID=" + actionConcreteID;
        String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc, "NoActionDescriptionAvailable"));
        stateString += " || " + escaped;
        plainTextReportUtil.addHeading(5, stateString);

        if (actionPath.contains("./output")) {
            actionPath = actionPath.replace("./output", "..");
        }

        actionPath = actionPath.replace("\\", "/");

        String altText = "screenshot: action, ConcreteID=" + actionConcreteID;

        plainTextReportUtil.addParagraph("Image: " + actionPath + "\n" + altText);

        plainTextReportUtil.writeToFile();
    }

    @Override
    public void addTestVerdicts(List<Verdict> verdicts) {
        String baseFilePath = plainTextReportUtil.getFile().getAbsolutePath();
        deleteBaseReport = true;
        baseReportPath = baseFilePath;
        int index = 1;
        for (Verdict verdict : verdicts) {
            String suffixName = String.format("_V%03d_%s", index++, verdict.verdictSeverityTitle());
            String verdictFilePath = appendSuffixToFile(baseFilePath, suffixName);

            plainTextReportUtil.duplicateFile(verdictFilePath);

            PlainTextFormatUtil verdictUtil = new PlainTextFormatUtil(verdictFilePath);
            addVerdictBlock(verdictUtil, verdict);
            verdictUtil.writeToFile();
        }
    }

    private void addVerdictBlock(PlainTextFormatUtil util, Verdict verdict) {
        String verdictInfo = verdict.info();
        if (verdict.severity() > Verdict.OK.severity()) {
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "");
        }

        util.addHorizontalLine();
        util.addHeading(3, "Test verdict for this sequence: " + verdictInfo);
        util.addHeading(5, "Severity: " + verdict.severity());
    }

    private String appendSuffixToFile(String filePath, String suffixName) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1) {
            return filePath + suffixName;
        }
        return filePath.substring(0, dotIndex) + suffixName + filePath.substring(dotIndex);
    }

    @Override
    public void finishReport() {
        if (deleteBaseReport && baseReportPath != null) {
            new java.io.File(baseReportPath).delete();
            return;
        }
        plainTextReportUtil.writeToFile();
    }
}

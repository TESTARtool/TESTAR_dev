/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.reporting;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
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

public class HtmlReporter implements Reporting {
    private HtmlFormatUtil htmlReportUtil;
    private int innerLoopCounter = 0;
    private boolean deleteBaseReport = false;
    private String baseReportPath;

    private final String openStateBlockContainer = "<div class='stateBlock' style='display:flex;flex-direction:column'>";
    private final String openDerivedBlockContainer = "<div class='derivedBlock' style='display:flex;flex-direction:column'>";
    private final String openSelectedBlockContainer = "<div class='selectedBlock' style='display:flex;flex-direction:column'>";
    private final String openVerdictBlockContainer = "<div class='verdictBlock' style='display:flex;flex-direction:column'>";

    private final String openBackgroundContainer = "<div class='background'>";
    private final String openCollapsibleContainer = "<div class='collapsibleContent'>";
    private final String closeContainer = "</div>";

    public HtmlReporter(String fileName) {
        htmlReportUtil = new HtmlFormatUtil(fileName);

        // Start the header, scripts, and styles of the HTML report
        String headerTitle = "TESTAR execution sequence report";
        htmlReportUtil.addHeader(headerTitle, HtmlHelper.getHtmlScript(), HtmlHelper.getHtmlStyle());

        addGenerateHeading();
    }

    private void addGenerateHeading() {
        htmlReportUtil.addHeading(1,
                "TESTAR execution sequence report for sequence " + OutputStructure.sequenceInnerLoopCount);
        // HTML button to invoke reverse function
        htmlReportUtil.addContent("<button id='reverseButton' onclick='reverse()'>Reverse order</button>");
        // Initialize the main div container to apply the reverse order
        htmlReportUtil.addContent("<div id='main' style='display:flex;flex-direction:column'>");
    }

    @Override
    public void addState(State state) {
        String imagePath = prepareScreenshotImagePath(state.get(Tags.ScreenshotPath, "NoScreenshotPathAvailable"));
        String concreteID = state.get(Tags.ConcreteID, "NoConcreteIdAvailable");
        String abstractID = state.get(Tags.AbstractID, "NoAbstractIdAvailable");

        htmlReportUtil.addContent(openStateBlockContainer); // Open state block container
        htmlReportUtil.addContent(openBackgroundContainer); // Open background container

        htmlReportUtil.addHeading(2, "State " + innerLoopCounter);

        // Add state identifiers
        String stateIDs = "AbstractID=" + abstractID + " || " + "ConcreteID=" + concreteID;
        htmlReportUtil.addHeading(4, stateIDs);

        // Add the timestamp this state was discovered
        Long stateTimeStamp = state.get(Tags.TimeStamp, 0L);
        Instant epochTimeStamp = Instant.ofEpochMilli(stateTimeStamp);
        String formattedTimestamp = DateTimeFormatter.ISO_INSTANT.format(epochTimeStamp);
        long epochMillis = epochTimeStamp.toEpochMilli();
        htmlReportUtil.addHeading(4, "TimeStamp: " + formattedTimestamp + " || Epoch: " + epochMillis);

        // Add state render time if available
        if (state.get(Tags.StateRenderTime, null) != null) {
            String stateRenderTime = "State Render Time: " + state.get(Tags.StateRenderTime) + " ms";
            htmlReportUtil.addHeading(4, stateRenderTime);
        }

        // Add state link reference if exists
        if (!state.get(Tags.LinkReference, "").isEmpty()) {
            String stateURL = state.get(Tags.LinkReference, "");
            String htmlStateURL = "<a href='" + stateURL + "' target='_blank'>" + stateURL + "</a>";
            htmlReportUtil.addContent(htmlStateURL);
        }

        // Add state screenshot
        String altText = "screenshot: state=" + innerLoopCounter + ", ConcreteID=" + concreteID
                + ", AbstractID=" + abstractID;
        htmlReportUtil.addParagraph("<img src=\"" + imagePath + "\" alt=\"" + altText + "\">");

        htmlReportUtil.addContent(closeContainer); // Close background container
        htmlReportUtil.addContent(closeContainer); // Close state block container

        innerLoopCounter++;
        htmlReportUtil.writeToFile();
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
        joiner.add("<b>" + escaped + "</b>");
        joiner.add(StringEscapeUtils.escapeHtml(action.toString()));
        joiner.add("ConcreteID=" + action.get(Tags.ConcreteID, "NoConcreteIdAvailable"));
        joiner.add("AbstractID=" + action.get(Tags.AbstractID, "NoAbstractIdAvailable"));

        return joiner.toString();
    }

    @Override
    public void addActions(Set<Action> actions) {
        htmlReportUtil.addContent(openDerivedBlockContainer); // Open derived actions block container
        htmlReportUtil.addButton("collapsible", "Click to view the set of derived actions:");
        htmlReportUtil.addContent(openCollapsibleContainer); // Open actions collapsible container

        ArrayList<String> actionStrings = new ArrayList<>();
        for (Action action : actions) {
            actionStrings.add(getActionString(action));
        }

        htmlReportUtil.addList(false, actionStrings);

        htmlReportUtil.addContent(closeContainer); // Close actions collapsible container
        htmlReportUtil.addContent(closeContainer); // Close derived actions block container

        htmlReportUtil.writeToFile();
    }

    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions) {
        htmlReportUtil.addContent(openDerivedBlockContainer); // Open derived actions block container

        ArrayList<String> actionStrings = new ArrayList<>();
        if (actions.size() == concreteIdsOfUnvisitedActions.size()) {
            htmlReportUtil.addHeading(4, "Set of actions (all unvisited - a new state):");
            for (Action action : actions) {
                actionStrings.add(getActionString(action));
            }
        } else if (concreteIdsOfUnvisitedActions.isEmpty()) {
            htmlReportUtil.addHeading(4, "All actions have been visited, set of available actions:");
            for (Action action : actions) {
                actionStrings.add(getActionString(action));
            }
        } else {
            htmlReportUtil.addHeading(4, concreteIdsOfUnvisitedActions.size() + " out of " + actions.size()
                    + " actions have not been visited yet:");
            for (Action action : actions) {
                if (concreteIdsOfUnvisitedActions.contains(action.get(Tags.ConcreteID, "NoConcreteIdAvailable"))) {
                    //action is unvisited -> showing:
                    actionStrings.add(getActionString(action));
                }
            }
        }
        htmlReportUtil.addList(false, actionStrings);
        htmlReportUtil.addContent(closeContainer); // Close derived actions block container

        htmlReportUtil.writeToFile();
    }

    @Override
    public void addSelectedAction(State state, Action action) {
        String screenshotDir = prepareScreenshotImagePath(OutputStructure.screenshotsOutputDir);
        String stateConcreteID = state.get(Tags.ConcreteID, "NoConcreteIdAvailable");
        String actionAbstractID = action.get(Tags.AbstractID, "NoAbstractIdAvailable");
        String actionConcreteID = action.get(Tags.ConcreteID, "NoConcreteIdAvailable");

        String actionPath = screenshotDir + "/"
                + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
                + "_sequence_" + OutputStructure.sequenceInnerLoopCount
                + "/" + stateConcreteID
                + "_" + actionConcreteID + ".png";

        htmlReportUtil.addContent(openSelectedBlockContainer); // Open selected action block container
        htmlReportUtil.addContent(openBackgroundContainer); // Open background container

        htmlReportUtil.addHeading(2,
                "Selected Action " + innerLoopCounter + " leading to State " + innerLoopCounter);

        String actionIDs = "AbstractID=" + actionAbstractID + " || " + "ConcreteID=" + actionConcreteID;
        String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc, "NoActionDescriptionAvailable"));
        actionIDs += " || " + escaped;
        htmlReportUtil.addHeading(4, actionIDs);

        if (actionPath.contains("./output")) {
            actionPath = actionPath.replace("./output", "..");
        }

        actionPath = actionPath.replace("\\", "/");

        String altText = "screenshot: action, ConcreteID=" + actionConcreteID;

        htmlReportUtil.addParagraph("<img src=\"" + actionPath + "\" alt=\"" + altText + "\">");

        htmlReportUtil.addContent(closeContainer); // Close background container
        htmlReportUtil.addContent(closeContainer); // Close executed action block container

        htmlReportUtil.writeToFile();
    }

    @Override
    public void addTestVerdicts(List<Verdict> verdicts) {
        String baseFilePath = htmlReportUtil.getFile().getAbsolutePath();
        deleteBaseReport = true;
        baseReportPath = baseFilePath;
        int index = 1;
        for (Verdict verdict : verdicts) {
            String suffixName = String.format("_V%03d_%s", index++, verdict.verdictSeverityTitle());
            String verdictFilePath = appendSuffixToFile(baseFilePath, suffixName);

            htmlReportUtil.duplicateFile(verdictFilePath);

            HtmlFormatUtil verdictUtil = new HtmlFormatUtil(verdictFilePath);
            addVerdictBlock(verdictUtil, verdict);
            verdictUtil.addContent("</div>");
            verdictUtil.addFooter();
            verdictUtil.writeToFile();
        }
    }

    private void addVerdictBlock(HtmlFormatUtil util, Verdict verdict) {
        String verdictInfo = StringEscapeUtils.escapeHtml(verdict.info());
        if (verdict.severity() > Verdict.OK.severity()) {
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "").replace("\n", "");
        }

        util.addContent(openVerdictBlockContainer); // Open verdict block container
        util.addHeading(2, "Test verdict for this sequence: " + verdictInfo);
        util.addHeading(4, "Severity: " + verdict.severity());
        util.addContent(
                "<h4 id='visualizer-rect' style='display: none;'>Visualizer: "
                        + verdict.visualizer().getShapes() + "</h4>");
        util.addContent(closeContainer); // Close verdict block container
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
            new File(baseReportPath).delete();
            return;
        }
        htmlReportUtil.addContent("</div>"); // Close the main div container
        htmlReportUtil.addFooter();

        htmlReportUtil.writeToFile();
    }
}

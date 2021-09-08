/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2021 Universitat Politecnica de Valencia - www.upv.es
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


package nl.ou.testar.HtmlReporting;

import nl.ou.testar.a11y.reporting.HTMLReporter;
import nl.ou.testar.visualvalidation.matcher.CharacterMatchEntry;
import nl.ou.testar.visualvalidation.matcher.ContentMatchResult;
import nl.ou.testar.visualvalidation.matcher.LocationMatch;
import nl.ou.testar.visualvalidation.matcher.MatcherResult;
import org.apache.commons.lang.StringEscapeUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.testar.OutputStructure;

import java.io.File;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HtmlSequenceReport implements Reporting {

    private static final String[] HEADER = new String[]{
            "<!DOCTYPE html>",
            "<html>",
            "<style>",
            "th, td {",
            "border: 1px solid black;",
            "}",
            "th {",
            "text-align: right;",
            "}",
            "td {",
            "text-align: center;",
            "}",
            "</style>",
            "<head>",
            "<title>TESTAR execution sequence report</title>",
            "</head>",
            "<body>"
    };
    private static final String REPORT_FILENAME_MID = "_sequence_";
    private static final String REPORT_FILENAME_AFT = ".html";
    private boolean firstStateAdded = false;
    private boolean firstActionsAdded = false;
    private PrintWriter out;
    private int innerLoopCounter = 0;

    public HtmlSequenceReport() {
        try {
            //TODO put filename into settings, name with sequence number
            // creating a new file for the report
            String filename = OutputStructure.htmlOutputDir + File.separator + OutputStructure.startInnerLoopDateString + "_"
                    + OutputStructure.executedSUTname + REPORT_FILENAME_MID + OutputStructure.sequenceInnerLoopCount
                    + REPORT_FILENAME_AFT;

            out = new PrintWriter(filename, HTMLReporter.CHARSET);
            for (String s : HEADER) {
                write(s);
            }

            write("<h1>TESTAR execution sequence report for sequence " + OutputStructure.sequenceInnerLoopCount + "</h1>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for Replay mode.
     *
     * @param pathReplayedSequence
     */
    public HtmlSequenceReport(String pathReplayedSequence) {
        try {
            String filename = OutputStructure.htmlOutputDir + File.separator + OutputStructure.startInnerLoopDateString + "_"
                    + OutputStructure.executedSUTname + REPORT_FILENAME_MID + OutputStructure.sequenceInnerLoopCount
                    + REPORT_FILENAME_AFT;

            out = new PrintWriter(filename, HTMLReporter.CHARSET);
            for (String s : HEADER) {
                write(s);
            }

            write("<h1>TESTAR replay sequence report for file " + pathReplayedSequence + "</h1>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String correctScreenshotPath(String screenshotDir) {
        if (screenshotDir.contains("./output")) {
            int indexStart = screenshotDir.indexOf("./output");
            int indexScrn = screenshotDir.indexOf("scrshots");
            String replaceString = screenshotDir.substring(indexStart, indexScrn);
            screenshotDir = screenshotDir.replace(replaceString, "../");
        }
        return screenshotDir;
    }

    static private String getScreenshotPath(State state) {
        return correctScreenshotPath(state.get(Tags.ScreenshotPath));
    }

    static private String getActionScreenshotPath(State state, Action action) {
        final String screenshotDir = correctScreenshotPath(OutputStructure.screenshotsOutputDir);
        String actionPath = screenshotDir + File.separator + OutputStructure.startInnerLoopDateString + "_" +
                OutputStructure.executedSUTname + "_sequence_" + OutputStructure.sequenceInnerLoopCount +
                File.separator + state.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable") + "_" +
                action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable") + ".png";

        if (actionPath.contains("./output")) {
            actionPath = actionPath.replace("./output", "..");
        }
        return actionPath;
    }

    public void addTitle(int h, String text) {
        write("<h" + h + ">" + text + "</h" + h + ">");
    }

    public void addSequenceStep(State state, String actionImagePath) {
        try {
            String imagePath = state.get(Tags.ScreenshotPath);
            // repairing the file paths:
            if (imagePath.contains("./output")) {
                imagePath = imagePath.replace("./output", "../");
            }
            write("<h4>State:</h4>");
            write("<p><img src=\"" + imagePath + "\"></p>");
            write("<h4>Action:</h4>");
            write("<p><img src=\"" + actionImagePath + "\"></p>");
        } catch (Exception e) {
            System.out.println("ERROR: Adding the Sequence step " + innerLoopCounter + " in the HTML report");
            write("<h2>ERROR Adding current Sequence step " + innerLoopCounter + "</h2>");
        }
    }

    public void addState(State state) {
        if (firstStateAdded) {
            if (firstActionsAdded) {
                writeStateIntoReport(state);
            } else {
                //don't write the state as it is the same - getState is run twice in the beginning, before the first action
            }
        } else {
            firstStateAdded = true;
            writeStateIntoReport(state);
        }
    }

    private void writeStateIntoReport(State state) {
        try {
            write("<h2>State " + innerLoopCounter + "</h2>");
            write("<h4>concreteID=" + state.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable") + "</h4>");
            write("<h4>abstractID=" + state.get(Tags.AbstractID, "NoAbstractIdAvailable") + "</h4>");
            //        try{if(state.get(Tags.Abstract_R_ID)!=null) write("<h4>Abstract_R_ID="+state.get(Tags.Abstract_R_ID)+"</h4>");}catch(Exception e){}
            //        try{if(state.get(Tags.Abstract_R_T_ID)!=null) write("<h4>Abstract_R_T_ID="+state.get(Tags.Abstract_R_T_ID)+"</h4>");}catch(Exception e){}
            //        try{if(state.get(Tags.Abstract_R_T_P_ID)!=null) write("<h4>Abstract_R_T_P_ID="+state.get(Tags.Abstract_R_T_P_ID)+"</h4>");}catch(Exception e){}
            write("<p><img src=\"" + getScreenshotPath(state) + "\"></p>"); //<img src="smiley.gif" alt="Smiley face" height="42" width="42">
            // file:///E:/TESTAR/TESTAR_dev/testar/target/install/testar/bin/output/output/scrshots/sequence1/SC1padzu12af1193500371.png
            // statePath=./output\scrshots\sequence1\SC1y2bsuu2b02920826651.png
        } catch (Exception e) {
            System.out.println("ERROR: Adding the State number " + innerLoopCounter + " in the HTML report");
            write("<h2>ERROR Adding current State " + innerLoopCounter + "</h2>");
        }
        innerLoopCounter++;
    }

    public void addActions(Set<Action> actions) {
        if (!firstActionsAdded) firstActionsAdded = true;
        write("<h4>Set of actions:</h4><ul>");
        for (Action action : actions) {
            write("<li>");
//            try{if(action.get(Tags.Role)!=null) write("--Role="+action.get(Tags.Role));}catch(Exception e){}
//            try{if(action.get(Tags.Targets)!=null) write("--Targets="+action.get(Tags.Targets));}catch(Exception e){}
            try {
                if (action.get(Tags.Desc) != null) {
                    String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
                    write("<b>" + escaped + "</b>  || ");
                }
            } catch (Exception e) {
            }

            write(StringEscapeUtils.escapeHtml(action.toString()));
            write(" || ConcreteId=" + action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"));
            try {
                if (action.get(Tags.AbstractID) != null) write(" || AbstractId=" + action.get(Tags.AbstractID));
            } catch (Exception e) {
            }
            try {
                if (action.get(Tags.Abstract_R_ID) != null)
                    write(" || Abstract_R_ID=" + action.get(Tags.Abstract_R_ID));
            } catch (Exception e) {
            }
            try {
                if (action.get(Tags.Abstract_R_T_ID) != null)
                    write(" || Abstract_R_T_ID=" + action.get(Tags.Abstract_R_T_ID));
            } catch (Exception e) {
            }
            try {
                if (action.get(Tags.Abstract_R_T_P_ID) != null)
                    write(" || Abstract_R_T_P_ID=" + action.get(Tags.Abstract_R_T_P_ID));
            } catch (Exception e) {
            }
            write("</li>");
        }
        write("</ul>");
    }

    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions) {
        if (!firstActionsAdded) firstActionsAdded = true;
        if (actions.size() == concreteIdsOfUnvisitedActions.size()) {
            write("<h4>Set of actions (all unvisited - a new state):</h4><ul>");
            for (Action action : actions) {
                write("<li>");

                try {
                    if (action.get(Tags.Desc) != null) {
                        String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
                        write("<b>" + escaped + "</b>");
                    }
                } catch (Exception e) {
                }

                write(" || ConcreteID=" + action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable")
                        + " || " + StringEscapeUtils.escapeHtml(action.toString()));

                write("</li>");
            }
            write("</ul>");
        } else if (concreteIdsOfUnvisitedActions.size() == 0) {
            write("<h4>All actions have been visited, set of available actions:</h4><ul>");
            for (Action action : actions) {
                write("<li>");

                try {
                    if (action.get(Tags.Desc) != null) {
                        String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
                        write("<b>" + escaped + "</b>");
                    }
                } catch (Exception e) {
                }

                write(" || ConcreteID=" + action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable")
                        + " || " + StringEscapeUtils.escapeHtml(action.toString()));

                write("</li>");
            }
            write("</ul>");
        } else {
            write("<h4>" + concreteIdsOfUnvisitedActions.size() + " out of " + actions.size() + " actions have not been visited yet:</h4><ul>");
            for (Action action : actions) {
                if (concreteIdsOfUnvisitedActions.contains(action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"))) {
                    //action is unvisited -> showing:
                    write("<li>");

                    try {
                        if (action.get(Tags.Desc) != null) {
                            String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
                            write("<b>" + escaped + "</b>");
                        }
                    } catch (Exception e) {
                    }

                    write(" || ConcreteID=" + action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable")
                            + " || " + StringEscapeUtils.escapeHtml(action.toString()));

                    write("</li>");
                }
            }
            write("</ul>");
        }
    }

    public void addSelectedAction(State state, Action action) {
        write("<h2>Selected Action " + innerLoopCounter + " leading to State " + innerLoopCounter + "\"</h2>");
        write("<h4>concreteID=" + action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"));

        if (action.get(Tags.Desc, null) != null) {
            String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
            write(" || " + escaped);
        }

        write("</h4>");
        write("<p><img src=\"" + getActionScreenshotPath(state, action) + "\"></p>"); //<img src="smiley.gif" alt="Smiley face" height="42" width="42">
    }

    public void addTestVerdict(Verdict verdict) {
        String verdictInfo = verdict.info();
        if (verdict.severity() > Verdict.OK.severity())
            verdictInfo = verdictInfo.replace(Verdict.OK.info(), "");

        write("<h2>Test verdict for this sequence: " + verdictInfo + "</h2>");
        write("<h4>Severity: " + verdict.severity() + "</h4>");
    }

    @Override
    public void addVisualValidationResult(MatcherResult result, State state, @Nullable Action action) {
        if (result != null) {
            if (!result.getNoLocationMatches().isEmpty() || !result.getResult().isEmpty()) {
                write("<h2>Visual validation result:</h2>");

                // Add the annotated screenshot
                StringBuilder screenshotPath = new StringBuilder(action != null ?
                        getActionScreenshotPath(state, action) : getScreenshotPath(state));
                screenshotPath.insert(screenshotPath.indexOf(".png"), MatcherResult.ScreenshotPostFix);
                write("<p><img src=\"" + screenshotPath + "\"></p>");

                // List the expected elements without a location match.
                result.getNoLocationMatches().forEach(it ->
                        write("<h4>No match for: \"" + it._text + "\" at location: " + it._location + "</h4>")
                );

                // List the expected elements with a location match.
                composeMatchedResultTable(result);
            }
        } else {
            write("<h2>Visual validation result:</h2>");
            write("<h4>No results available.</h4>");
        }
    }

    private void composeMatchedResultTable(MatcherResult result) {

        for (ContentMatchResult contentResult : result.getResult()) {
            // Based on the location try lookup the OCR matched entries.
            Optional<LocationMatch> locationMatch = result.getLocationMatches().stream()
                    .filter(it -> it.location.location == contentResult.foundLocation).findFirst();
            if (locationMatch.isPresent()) {
                LocationMatch it = locationMatch.get();
                write("<dl>");
                write("<dt>Expect: \"" + it.expectedElement._text + "\" at location: " + it.location.location + "\" location matched with:</dt>");
                it.recognizedElements.forEach(recognizedElement ->
                        write("<dd>\"" + recognizedElement._text + "\" at location: " + recognizedElement._location + "\" confidence: " + recognizedElement._confidence + "</dd>"));
                write("</dl>");
            }

            // Write content match result.
            write("<table><caption>\"" + contentResult.expectedText + "\" " +
                    "[" + contentResult.totalMatched + "/" + contentResult.totalExpected + "] " + contentResult.matchedPercentage +"%</caption>"
            );
            writeTableRow(() -> {
                writeTableHeader("Result:");
                contentResult.expectedResult.getResult().forEach(it ->
                        writeTableCell(it.getMatchResult())
                );
                return true;
            });
            writeTableRow(() -> {
                writeTableHeader("Expected:");
                contentResult.expectedResult.getResult().forEach(it ->
                        writeTableCell(it.getCharacterMatch().getCharacter())
                );
                return true;
            });
            writeTableRow(() -> {
                writeTableHeader("Found:");
                contentResult.expectedResult.getResult().forEach(it ->
                        {
                            CharacterMatchEntry tmp = it.getCharacterMatch();
                            writeTableCell(tmp.isMatched() ? tmp.getCounterPart().getCharacter() : "");
                        }
                );
                return true;
            });
            write("</table>");
            if (contentResult.recognizedResult.getResult().stream().anyMatch(CharacterMatchEntry::isNotMatched)) {
                write("<h4>No match for OCR items:</h4>");
                write("<ul>");
                contentResult.recognizedResult.getResult().forEach(it -> {
                    if (it.isNotMatched()) {
                        write("<li>" + it.getCharacter() + "</li>");
                    }
                });
                write("</ul>");
            }
        }
    }

    private <T> void writeTableHeader(T data) {
        write("<th>" + data + "</th>");
    }

    private <T> void writeTableCell(T data) {
        write("<td>" + data + "</td>");
    }

    private void writeTableRow(Supplier<Boolean> data) {
        write("<tr>");
        data.get();
        write("</tr>");
    }

    public void close() {
        for (String s : HTMLReporter.FOOTER) {
            write(s);
        }
        out.close();
    }

    private void write(String s) {
        out.println(s);
        out.flush();
    }

    private String start(String tag) {
        return "<" + tag + ">";
    }

    private String end(String tag) {
        return "</" + tag + ">";
    }
}

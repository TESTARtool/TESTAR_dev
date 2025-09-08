/**
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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
 *
 */

package org.testar.verdicts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.rules.RuleMatch;
import org.testar.monkey.Main;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.verdicts.helpers.Metrics;

public class GenericVerdict {

    protected static final Logger logger = LogManager.getLogger();

    // By default consider that all the verdicts of the class are enabled
    public static List<String> enabledVerdicts = Arrays.stream(GenericVerdict.class.getDeclaredMethods())
            .filter(classMethod -> java.lang.reflect.Modifier.isPublic(classMethod.getModifiers()))
            .map(java.lang.reflect.Method::getName)
            .collect(Collectors.toList());

    // Load the ignore file from the settings directory
    public static String spellingIgnoreFile = Main.settingsDir + File.separator + "SpellingIgnoreList.txt";

    // Custom list to ignore spelling checker words
    private static List<String> spellingIgnoreList = new ArrayList<>();

    private static void loadSpellingIgnoreListFromFile()
    {
        try
        {
            spellingIgnoreList.clear();
            File file = new File(spellingIgnoreFile);
            if(file.exists() && !file.isDirectory()) 
            {
                Scanner s = new Scanner(file);

                while (s.hasNextLine()){
                    spellingIgnoreList.add(s.nextLine());
                }

                s.close();
            }
        }
        catch(java.io.FileNotFoundException ex)
        {
            // ignore errors
        }
        Collections.sort(spellingIgnoreList);
    }

    // TODO: It should be easy to open/edit the ignore from the Functional Verdicts tab.
    private static void saveSpellingIgnoreListToFile()
    {    
        try
        {
            Collections.sort(spellingIgnoreList);
            FileWriter writer = new FileWriter(spellingIgnoreFile); 
            for(String str: spellingIgnoreList) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        }
        catch(java.io.IOException ex)
        {
            // ignore errors
        }
    }

    protected static boolean isNotOnIgnoreList(String text)
    {
        return !text.isEmpty() && !spellingIgnoreList.contains(text);
    }

    public static Verdict SpellChecker(State state, Tag<String> tagTextChecker, Language languageChecker, String ignorePatternRegEx) {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        loadSpellingIgnoreListFromFile();

        Pattern ignorePattern = Pattern.compile(ignorePatternRegEx);

        // If it is enabled, then execute the verdict implementation
        Verdict spellCheckerVerdict = Verdict.OK;
        JLanguageTool langTool = new JLanguageTool(languageChecker);
        // Iterate through all the widgets of the state to apply the spell checker in the desired String Tag
        for(Widget w : state) {
            String tagTextContent = w.get(tagTextChecker, "");

            // If there is text that is not ignored, apply the spell checking
            if (isNotOnIgnoreList(tagTextContent)) {

                Matcher ignoreMatcher = ignorePattern.matcher(tagTextContent);

                if ((ignorePatternRegEx == "" || !ignoreMatcher.find())) {
                    try {
                        List<RuleMatch> matches = langTool.check(tagTextContent);
                        for (RuleMatch match : matches) {

                            String errorMsg = "Potential error at characters " + match.getFromPos() + "-" + match.getToPos() + " : " + match.getMessage();
                            String correctMsg = "Suggested correction(s): " + match.getSuggestedReplacements();

                            String verdictMsg = "Widget Title ( " + tagTextContent + " ) " + errorMsg + " " + correctMsg;
                            String spellingSuspect = "//" + tagTextContent;
                            if (!spellingIgnoreList.contains(spellingSuspect)) {
                                spellingIgnoreList.add(spellingSuspect);
                            }
                            spellCheckerVerdict = spellCheckerVerdict.join(new Verdict(Verdict.Severity.WARNING_UI_TRANSLATION_OR_SPELLING_ISSUE, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
                        }

                    }
                    catch (IOException ioe) {
                        System.err.println("JLanguageTool error checking widget: " + tagTextContent);
                        logger.log(Level.ERROR, "GenericVerdict SpellChecker exception", ioe);
                    }
                }
            }
        }

        spellCheckerVerdict.addDescription("spell_checking_verdict");
        saveSpellingIgnoreListToFile();
        return spellCheckerVerdict;
    }


    // Detect UI items that contain dummy, test or debug text
    // BAD:  Debug
    //       Dummy
    //       test
    // GOOD: 
    // When programmers debugging a program where they could only notice the problem in an other environment than the developer machine,
    // then they sometimes add code which show a message or value with a phrase or word such as 'debug' or 'test'. If the programmer
    // forget to remove that temporary code, then this text can be shown in a production release of the software.
    public static Verdict CommonTestOrDummyPhrases(State state, Tag<String> tagTextChecker)
    {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict verdict = Verdict.OK;

        try {
            String patternRegex = "[Dd]ummy|[Tt]est][Dd]ebug";
            Pattern pattern = Pattern.compile(patternRegex);

            for(Widget w : state) {
                String desc = w.get(tagTextChecker, "");
                Matcher matcher = pattern.matcher(desc);

                if (matcher.find()) {
                    String verdictMsg = String.format("Detected debug or test data values! Role: %s , Path: %s , %s: %s", 
                            w.get(Tags.Role), w.get(Tags.Path), tagTextChecker, desc);
                    verdict = verdict.join(new Verdict(Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
                }
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict CommonTestOrDummyPhrases exception", e);
        }

        return verdict;
    }

    // Detect UI items that contain sensitive texts, such as passwords
    // BAD:  Pa$$w0rd
    //       7n_8Q~DyBzoE4aqBBXA-B7gVdX~5zFCZk5yVvb7b
    // GOOD: *******
    // Sensitive data such as Passwords and ClientSecrets should not be visible in their original form the UI, database, configuration or log files.
    // This data should be encrypted if they must be shown or stored somewhere.
    // There are two options to provide a list of sensitive data:
    // 1. Fill a ../Settings/SensitiveDataList.txt file with all sensitive text.
    // 2. Give a regular expression with matches sensitive text
    // Using the SensitiveDataList.txt file is the preferred way, because the sensitive data is then not in the protocol and you don't have to rewrite the data as a regular expression.
    public static Verdict SensitiveData(State state, Tag<String> tagTextChecker, String sensitiveTextPatternRegEx)
    {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict verdict = Verdict.OK;

        try {
            Pattern pattern = Pattern.compile(sensitiveTextPatternRegEx);

            for(Widget w : state) {
                String desc = w.get(tagTextChecker, "");
                Matcher matcher = pattern.matcher(desc);

                if (!desc.isEmpty() && (sensitiveDataList.contains(desc) || matcher.find()))
                {
                    String verdictMsg = String.format("Detected sensitive data values!  Role: %s , Path: %s , %s: %s", 
                            w.get(Tags.Role), w.get(Tags.Path), tagTextChecker, desc);
                    verdict = verdict.join(new Verdict(Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
                }
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict SensitiveData exception", e);
        }

        return verdict;
    }

    // Load the sensitive data file from the settings directory
    private static String sensitiveDataListFile = Main.settingsDir + File.separator + "SensitiveDataList.txt";

    // Custom load sensitive data
    private static List<String> sensitiveDataList = new ArrayList<>();
    private static boolean isSensitiveDataListLoaded = false;

    private static void loadSensitiveDataListFromFile()
    {
        if (!isSensitiveDataListLoaded)
        {
            try
            {
                sensitiveDataList.clear();
                File file = new File(sensitiveDataListFile);
                if(file.exists() && !file.isDirectory()) 
                {
                    Scanner s = new Scanner(file);

                    while (s.hasNextLine()){
                        sensitiveDataList.add(s.nextLine());
                    }

                    s.close();
                }
            }
            catch(java.io.FileNotFoundException ex)
            {
                // ignore errors
            }
            Collections.sort(sensitiveDataList);
            isSensitiveDataListLoaded = true;
        }
    }

    // Detect the replacement character � (often displayed as a black rhombus with a white question mark) is a symbol found in the Unicode standard at code point U+FFFD in the Specials table. 
    // It is used to indicate problems when a system is unable to render a stream of data to correct symbols
    // https://en.wikipedia.org/wiki/Specials_(Unicode_block)
    // U+FFFD � REPLACEMENT CHARACTER used to replace an unknown, unrecognized, or unrepresentable character
    // BAD:  �
    //       f�r
    // GOOD: für
    public static Verdict UnicodeReplacementCharacter(State state, Tag<String> tagTextChecker)
    {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict verdict = Verdict.OK;

        try {
            String patternRegex = ".*\\uFFFD.*"; // Look for a Unicode Replacement character: .*\uFFFD.*
            Pattern pattern = Pattern.compile(patternRegex, Pattern.UNICODE_CHARACTER_CLASS);

            for(Widget w : state) {
                String desc = w.get(tagTextChecker, "");
                Matcher matcher = pattern.matcher(desc);

                if (matcher.find()) {
                    String verdictMsg = String.format("Detected Unicode Replacement Character in widget! Role: %s , Path: %s , %s: %s", 
                            w.get(Tags.Role), w.get(Tags.Path), tagTextChecker, desc);
                    verdict = verdict.join(new Verdict(Verdict.Severity.WARNING_UI_TRANSLATION_OR_SPELLING_ISSUE, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
                }
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict UnicodeReplacementCharacter exception", e);
        }

        return verdict;
    }

    private static ArrayList<Rect> getRegions(State state)
    {
        // Prepare a list that contains all the Rectangles from the leaf widgets
        ArrayList<Rect> regions = new ArrayList<Rect>();

        for(Widget w : state) {
            if(w.childCount() < 1 && w.get(Tags.Shape, null) != null) {
                regions.add((Rect)w.get(Tags.Shape));
            }
        }
        return regions;
    }

    // Calculates an aesthetic value between 0.00 (bad) and 100.0 (perfect) alignment of widgets and give warning if threshold is breached
    // based on the work of "Towards an evaluation of graphical user interfaces aesthetics based on metrics" Zen, Mathieu ; Vanderdonckt, Jean
    // A default threshold value is around 50.0   
    public static Verdict WidgetAlignmentMetric(State state, double thresholdValue) {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict widgetAlignmentMetricVerdict = Verdict.OK;

        try {
            ArrayList<Rect> regions = getRegions(state);

            // returns a value from 0.00 to 100.0. Lower is bad alignment.
            double alignmentMetric = Metrics.calculateAlignmentMetric(regions);

            if (alignmentMetric < thresholdValue)
            {
                String verdictMsg = String.format("Alignment metric with value %f is below threshold value %f!",  alignmentMetric, thresholdValue);
                Verdict verdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, regions);
                widgetAlignmentMetricVerdict = widgetAlignmentMetricVerdict.join(verdict);
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict WidgetAlignmentMetric exception", e);
        }

        return widgetAlignmentMetricVerdict;
    }

    // Calculates an aesthetic value between 0.00 (bad) and 100.0 (perfect) the balance of widgets and give warning if threshold is breached
    // based on the work of "Towards an evaluation of graphical user interfaces aesthetics based on metrics" Zen, Mathieu ; Vanderdonckt, Jean
    // A default threshold value is around 50.0
    public static Verdict WidgetBalanceMetric(State state, double tresholdValue) {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict widgetBalanceMetricVerdict = Verdict.OK;

        try {
            Rect sutRect = (Rect) state.child(0).get(Tags.Shape, null);
            if (sutRect.width() > 0 && sutRect.height() > 0)
            {
                ArrayList<Rect> regions = getRegions(state);

                double balanceMetric = Metrics.calculateBalanceMetric(regions, sutRect.width(), sutRect.height());

                if (balanceMetric < tresholdValue)
                {
                    String verdictMsg = String.format("Balance metric with value %f is below treshold value %f!",  balanceMetric, tresholdValue);
                    Verdict verdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, regions);
                    widgetBalanceMetricVerdict = widgetBalanceMetricVerdict.join(verdict);
                }
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict WidgetBalanceMetric exception", e);
        }

        return widgetBalanceMetricVerdict;
    }

    // Calculates an aesthetic value between 0.00 (bad) and 100.0 (perfect) center alignment of widgets and give warning if threshold is breached
    // based on the work of "Towards an evaluation of graphical user interfaces aesthetics based on metrics" Zen, Mathieu ; Vanderdonckt, Jean
    // A default threshold value is around 50.0
    public static Verdict WidgetCenterAlignmentMetric(State state, double tresholdValue) {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict widgetCenterAlignmentVerdict = Verdict.OK;

        try {
            ArrayList<Rect> regions = getRegions(state);

            // returns a value from 0.00 to 100.0. Lower is bad alignment.
            double metric = Metrics.calculateCenterAlignment(regions);

            if (metric < tresholdValue)
            {
                String verdictMsg = String.format("Center alignment metric with value %f is below treshold value %f!",  metric, tresholdValue);
                Verdict verdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, regions);
                widgetCenterAlignmentVerdict = widgetCenterAlignmentVerdict.join(verdict);
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict WidgetCenterAlignmentMetric exception", e);
        }

        return widgetCenterAlignmentVerdict;
    }

    // Calculates an aesthetic value between 0.00 (bad) and 100.0 (perfect) Concentricity of widgets and give warning if threshold is breached
    // based on the work of "Towards an evaluation of graphical user interfaces aesthetics based on metrics" Zen, Mathieu ; Vanderdonckt, Jean
    // A default threshold value is around 50.0
    public static Verdict WidgetConcentricityMetric(State state, double tresholdValue) {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict widgetConcentricityVerdict = Verdict.OK;

        try {
            Rect sutRect = (Rect) state.child(0).get(Tags.Shape, null);
            if (sutRect.width() > 0 && sutRect.height() > 0)
            {
                ArrayList<Rect> regions = getRegions(state);

                // returns a value from 0.00 to 100.0.
                double metric = Metrics.calculateConcentricity(regions, sutRect.width(), sutRect.height());

                if (metric < tresholdValue)
                {
                    String verdictMsg = String.format("Concentricity metric with value %f is below treshold value %f!",  metric, tresholdValue);
                    Verdict verdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, regions);
                    widgetConcentricityVerdict = widgetConcentricityVerdict.join(verdict);
                }
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict WidgetConcentricityMetric exception", e);
        }

        return widgetConcentricityVerdict;
    }

    // Calculates an aesthetic value between 0.00 (bad) and 100.0 (perfect) Density of widgets and give warning if threshold is breached
    // based on the work of "Towards an evaluation of graphical user interfaces aesthetics based on metrics" Zen, Mathieu ; Vanderdonckt, Jean
    // A default threshold value is around minValue: 25, maxValue 75
    public static Verdict WidgetDensityMetric(State state, double tresholdMinValue, double tresholdMaxValue) {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict widgetDensityVerdict = Verdict.OK;

        try {
            Rect sutRect = (Rect) state.child(0).get(Tags.Shape, null);
            if (sutRect.width() > 0 && sutRect.height() > 0)
            {
                ArrayList<Rect> regions = getRegions(state);

                // returns a value from 0.00 to 100.0.
                double metric = Metrics.calculateDensity(regions, sutRect.width(), sutRect.height());

                if (metric < tresholdMinValue)
                {
                    String verdictMsg = String.format("Density metric with value %f is below treshold minimum value %f! Design too simple.", metric, tresholdMinValue);
                    Verdict verdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, regions);
                    widgetDensityVerdict = widgetDensityVerdict.join(verdict);
                }

                if (metric > tresholdMaxValue)
                {
                    String verdictMsg = String.format("Density metric with value %f is higher then treshold maximum value %f! Design too complex.",  metric, tresholdMaxValue);
                    Verdict verdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, regions);
                    widgetDensityVerdict = widgetDensityVerdict.join(verdict);
                }
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict WidgetDensityMetric exception", e);
        }

        return widgetDensityVerdict;
    }

    // Calculates an aesthetic value between 0.00 (complex) and 100.0 (simple) simplicity of widgets and give warning if threshold is breached
    // based on the work of "Towards an evaluation of graphical user interfaces aesthetics based on metrics" Zen, Mathieu ; Vanderdonckt, Jean
    // A default threshold value is around 50.0
    public static Verdict WidgetSimplicityMetric(State state, double tresholdValue) {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict widgetSimplicityVerdict = Verdict.OK;

        try {
            Rect sutRect = (Rect) state.child(0).get(Tags.Shape, null);
            if (sutRect.width() > 0 && sutRect.height() > 0)
            {
                ArrayList<Rect> regions = getRegions(state);

                double metric = Metrics.calculateSimplicity(regions, sutRect.width(), sutRect.height());

                if (metric < tresholdValue)
                {
                    String verdictMsg = String.format("Simplicity metric with value %f is below treshold value %f!",  metric, tresholdValue);
                    Verdict verdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, regions);
                    widgetSimplicityVerdict = widgetSimplicityVerdict.join(verdict);
                }
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict WidgetSimplicityMetric exception", e);
        }

        return widgetSimplicityVerdict;
    }

    /**
     * Obtain all widgets of the State and verify if they overlap with other widgets except if they overlap with parent or child widgets. 
     * We can completely ignore widgets (by Role or Web Class) if the are part of an undesired sub-tree.
     * When there a lot of overlapping widgets in the same area, then you can set joinVerdicts to FALSE to report each overlap individually. 
     * When only interested in leaf widgets, then set checkLeafWidgetsOnly to TRUE, otherwise FALSE (default)
     * When the verdict is used for a Web application, then set checkWebStyles to TRUE, otherwise FALSE.
     * @param state
     * @param ignoreByRoles; whitelist widgets by Roles, such as widgets that do are not shown, such as WdCOL
     * @param ignoreByClasses; whitelist widget by Classes, such as widgets that are allowed to overlap other widgets, such as Datetime pickers, dropdownlists, menus, etc.
     * @param joinVerdicts
     * @param checkLeafWidgetsOnly 
     * @param checkWebStyles
     * @return
     */
    public static Verdict WidgetClashDetection(State state,  List<Role> ignoredRoles,  List<String> ignoredClasses, boolean joinVerdicts, boolean checkLeafWidgetsOnly, boolean checkWebStyles) {
        // If this method is NOT enabled, just return verdict OK
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

        Verdict widgetsClasDetectionVerdict = Verdict.OK;

        try {
            // Prepare a list that contains all the Rectangles from the leaf widgets
            List<Pair<Widget, Rect>> leafWidgetsRects = new ArrayList<>();

            for(Widget w : state) {
                // ignore or include leaf widgets. Widgets must have a shape. For web applications the opacity should be higher than zero
                if ((!checkLeafWidgetsOnly || checkLeafWidgetsOnly && w.childCount() < 1) && w.get(Tags.Shape, null) != null && 
                        (!checkWebStyles || checkWebStyles && w.get(WdTags.WebStyleOpacity) > 0.0))
                {
                    Rect rect = (Rect) w.get(Tags.Shape, null);

                    if(w.get(Tags.Shape, null) != null) {
                        // We can completely ignore the widget or sub-tree widgets that descend from these undesired Roles or Classes. 
                        if(isRoleWithoutAnyRealEstateOnCanvas(w) || isOrDescendFromRole(w, ignoredRoles) || isOrDescendFromClass(w, ignoredClasses)) {
                            continue;
                        } else {
                            Rect widgetRect = (Rect)w.get(Tags.Shape);
                            // Only include rect if it has surface
                            if (widgetRect.width() > 0 && widgetRect.height() > 0 ) {
                                boolean isContainedOrVisible = isContainedInAllParentsRectOrIsAllowedToBeOutsideParentRects(w,w, checkWebStyles);

                                //System.out.println("isContainedOrVisible: " + isContainedOrVisible + " Widget: " + clashedWidgetMsg(w, widgetRect));

                                if (isContainedOrVisible) {
                                    leafWidgetsRects.add(new Pair<Widget, Rect>(w, widgetRect));
                                } 
                            }
                        }
                    }
                }
            }

            List<String> reported = new ArrayList();
            for(int i = 0; i < leafWidgetsRects.size(); i++) {
                for(int j = i + 1; j < leafWidgetsRects.size(); j++) {
                    if(leafWidgetsRects.get(i) != leafWidgetsRects.get(j)) {
                        Rect rectOne = leafWidgetsRects.get(i).right();
                        Rect rectTwo = leafWidgetsRects.get(j).right();
                        Widget firstWidget = leafWidgetsRects.get(i).left();
                        Widget secondWidget = leafWidgetsRects.get(j).left();

                        // no parent/child relationship and intersect then report
                        if(!isChildOf(firstWidget,secondWidget) && !isChildOf(secondWidget, firstWidget) && checkRectIntersection(rectOne, rectTwo) && firstWidget != secondWidget 
                                /*&& firstWidget.get(WdTags.WebIsDisplayed) && secondWidget.get(WdTags.WebIsDisplayed)*/) {

                            String firstMsg = clashedWidgetMsg(firstWidget, rectOne);

                            String secondMsg = clashedWidgetMsg(secondWidget, rectTwo);

                            String verdictMsg = "Two widgets are clashing!" + " First in RED! " + firstMsg + ". Second in BLUE! " + secondMsg;

                            // Avoid duplicate message by checking of the reversed checking already is reported
                            if (!reported.contains(secondMsg + firstMsg))
                            {
                                int alpha = 50; // value between 0 (transparant) - 255 (solid)

                                // Custom colors of overlapping widgets
                                Rect firstWidgetRect = (Rect)firstWidget.get(Tags.Shape);

                                // When alpha channel is given for Color, then the screenshot will be made in gray colors with rectangles painted in colors
                                java.awt.Color transparentRed = new java.awt.Color(255, 0, 0, alpha);
                                firstWidgetRect.setColor(transparentRed);

                                Rect secondWidgetRect = (Rect)secondWidget.get(Tags.Shape);
                                java.awt.Color transparentBlue = new java.awt.Color(0, 0, 255, alpha);
                                secondWidgetRect.setColor(transparentBlue);

                                if (joinVerdicts) {
                                    widgetsClasDetectionVerdict = widgetsClasDetectionVerdict.join(new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, Arrays.asList(firstWidgetRect, secondWidgetRect)));
                                }
                                else {
                                    widgetsClasDetectionVerdict = new Verdict(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, Arrays.asList(firstWidgetRect, secondWidgetRect));
                                }
                                reported.add(firstMsg + secondMsg);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "GenericVerdict WidgetClashDetection exception", e);
        }

        return widgetsClasDetectionVerdict;
    }

    private static String clashedWidgetMsg(Widget widget, Rect rect)
    {
        return String.format("AbstractID : %s , Title: %s , WebTextContent: %s , Role: %s , Class: %s , WebId: %s , Width: %d, Height: %d", 
                widget.get(Tags.AbstractID, ""), widget.get(Tags.Title, ""), widget.get(WdTags.WebTextContent, ""), widget.get(Tags.Role), widget.get(WdTags.WebCssClasses, ""), widget.get(WdTags.WebId, ""), (long)rect.width(), (long)rect.height());
    }

    private static boolean isOrDescendFromRole(Widget widget, List<Role> roles) {
        if (roles.size() == 0) return false;
        if (roles.contains(widget.get(Tags.Role, Roles.Widget))) return true;
        else if(widget.parent() == null) return false;
        else return isOrDescendFromRole(widget.parent(), roles);
    }
    private static boolean isOrDescendFromClass(Widget widget, List<String> webClasses) {
        if (webClasses.size() == 0) return false;
        if (webClasses.stream().anyMatch(str -> widget.get(WdTags.WebCssClasses, "").contains(str))) return true;
        else if(widget.parent() == null) return false;
        else return isOrDescendFromClass(widget.parent(), webClasses);
    }

    private static boolean isRoleWithoutAnyRealEstateOnCanvas(Widget widget)
    {
        // Fixed roles that should be ignored, because they do not take any real estate on the canvas
        List<Role> ignoreSpecificRoles = Arrays.asList(WdRoles.WdCOL, WdRoles.WdCOLGROUP, WdRoles.WdTR, WdRoles.WdSPAN, WdRoles.WdUnknown);
        if (ignoreSpecificRoles.contains(widget.get(Tags.Role, Roles.Widget))) return true;
        return false;
    }

    private static boolean isChildOf(Widget widget, Widget searchParent)
    {
        if (widget == null || searchParent == null || widget == searchParent || widget.parent() == null) return false;
        if (widget.parent() == searchParent) return true;
        else return isChildOf(widget.parent(), searchParent);
    }

    private static boolean checkRectIntersection(Rect r1, Rect r2) {
        return !(r1.x() + r1.width() <= r2.x() ||
                r1.y() + r1.height() <= r2.y() ||
                r2.x() + r2.width() <= r1.x() ||
                r2.y() + r2.height() <= r1.y()); 
    }

    private static boolean isRect1ContainedInRect2(Rect r1, Rect r2) {
        return (r1.x() >= r2.x() && 
                r1.y() >= r2.y() &&
                ((r1.x() + r1.width()) <= (r2.x() + r2.width())) &&
                ((r1.y() + r1.height()) <= (r2.y() + r2.height())));
    }

    private static boolean isRect1OverflowingOnXOnRect2(Rect r1, Rect r2) {
        return  (r1.x() + r1.width()) > (r2.x() + r2.width());
    }

    private static boolean isRect1OverflowingOnYOnRect2(Rect r1, Rect r2) {
        return  (r1.y() + r1.height()) > (r2.y() + r2.height());
    }

    private static boolean isContainedInAllParentsRectOrIsAllowedToBeOutsideParentRects(Widget startWidget, Widget targetWidget, boolean checkWebStyles) {

        if (checkWebStyles) {
            if (!startWidget.get(WdTags.WebIsEnabled, true) || startWidget.get(WdTags.WebIsHidden)) return false;
        }

        Widget parent = startWidget.parent();
        if(parent == null) return true;

        Rect targetWidgetRect = (Rect) targetWidget.get(Tags.Shape, null);
        Rect parentRect = (Rect) parent.get(Tags.Shape, null);
        if (targetWidgetRect == null || parentRect == null) return true;

        // Some Widget have a height of 0, while the child widgets are still shown. Therefore we ignore height 0 and continue.
        if (parentRect.height() == 0 || isRect1ContainedInRect2(targetWidgetRect, parentRect))
            return isContainedInAllParentsRectOrIsAllowedToBeOutsideParentRects(parent, targetWidget, checkWebStyles);
        else
        { 
            if (checkWebStyles) {
                String overflowStyle = parent.get(WdTags.WebStyleOverflow, "");
                String overflowStyleX = parent.get(WdTags.WebStyleOverflowX, "");
                String overflowStyleY = parent.get(WdTags.WebStyleOverflowY, "");
                String positionStyle = startWidget.get(WdTags.WebStylePosition, "static");

                // Check overflow style properties. If value is default or visible, then element is not really contained, but the widget is allowed to be visible outside of the parent element and is visible in the UI.
                boolean isAllowedToOverflow = overflowStyle == "" || overflowStyle.equalsIgnoreCase("visible") || positionStyle.equalsIgnoreCase("fixed") ||
                        ((overflowStyleX == "" || overflowStyleX.equalsIgnoreCase("visible")) && isRect1OverflowingOnXOnRect2(targetWidgetRect, parentRect)) ||
                        ((overflowStyleY == "" || overflowStyleY.equalsIgnoreCase("visible")) && isRect1OverflowingOnYOnRect2(targetWidgetRect, parentRect));

                if (isAllowedToOverflow)
                    return isContainedInAllParentsRectOrIsAllowedToBeOutsideParentRects(parent, targetWidget, checkWebStyles);
            }

            return false;
        }
    }

}

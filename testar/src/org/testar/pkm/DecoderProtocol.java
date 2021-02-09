/***************************************************************************************************
 *
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
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

package org.testar.pkm;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.fruit.Pair;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.testar.OutputStructure;
import org.testar.json.JsonArtefactLogs;
import org.testar.json.JsonArtefactStateModel;
import org.testar.json.JsonArtefactTestResults;
import org.testar.json.object.StateModelDifferenceJsonObject;
import org.testar.protocols.GenericUtilsProtocol;

import nl.ou.testar.HtmlReporting.HtmlSequenceReport;
import nl.ou.testar.StateModel.Artefact.StateModelArtefactManager;
import nl.ou.testar.StateModel.Difference.StateModelDifferenceManager;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

public class DecoderProtocol extends GenericUtilsProtocol {

    protected HtmlSequenceReport htmlReport;
    protected State latestState;

    protected String verdictInfo;
    protected SortedSet<String> sequencesOutputDir = new TreeSet<>();
    protected SortedSet<String> htmlOutputDir = new TreeSet<>();
    protected SortedSet<String> logsOutputDir = new TreeSet<>();
    protected SortedSet<String> sequencesVerdicts = new TreeSet<>();

    protected SortedSet<String> coverageSummary = new TreeSet<>();
    protected SortedSet<String> coverageDir = new TreeSet<>();

    protected Object licenseSUT = "";

    protected String testResultsArtefactDirectory = "";
    protected String stateModelArtefactDirectory = "";
    protected String reportHTMLStateModelDifference = "";

    /**
     * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void preSequencePreparations() {
        //initializing the HTML sequence report:
        htmlReport = new HtmlSequenceReport();
    }

    /**
     * Overwriting to add HTML report writing into it
     *
     * @param state
     * @param actions
     * @return
     */
    @Override
    protected Action preSelectAction(State state, Set<Action> actions){
        // adding available actions into the HTML report:
        htmlReport.addActions(actions);
        return(super.preSelectAction(state, actions));
    }

    /**
     * Execute the selected action.
     * @param system the SUT
     * @param state the SUT's current state
     * @param action the action to execute
     * @return whether or not the execution succeeded
     */
    @Override
    protected boolean executeAction(SUT system, State state, Action action){
        // adding the action that is going to be executed into HTML report:
        htmlReport.addSelectedAction(state, action);
        return super.executeAction(system, state, action);
    }

    /**
     * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void postSequenceProcessing() {
        htmlReport.addTestVerdict(getVerdict(latestState).join(processVerdict));

        String sequencesPath = getGeneratedSequenceName();
        try {
            sequencesPath = new File(getGeneratedSequenceName()).getCanonicalPath();
        }catch (Exception e) {}

        String status = (getVerdict(latestState).join(processVerdict)).verdictSeverityTitle();
        String statusInfo = (getVerdict(latestState).join(processVerdict)).info();

        statusInfo = statusInfo.replace("\n"+Verdict.OK.info(), "");
        verdictInfo = OutputStructure.startInnerLoopDateString + " " + status + " " + statusInfo;

        //Timestamp(generated by logger) SUTname Mode SequenceFileObject Status "StatusInfo"
        INDEXLOG.info(OutputStructure.executedSUTname
                + " " + settings.get(ConfigTags.Mode, mode())
                + " " + sequencesPath
                + " " + status + " \"" + statusInfo + "\"" );

        sequencesOutputDir.add(getGeneratedSequenceName());
        logsOutputDir.add(getGeneratedLogName());
        htmlOutputDir.add(htmlReport.getGeneratedHTMLName());
        sequencesVerdicts.add(verdictInfo);

        htmlReport.close();
    }

    @Override
    protected void closeTestSession() {
        super.closeTestSession();

        // TODO: Allow Record mode when Listening mode implemented
        if(settings.get(ConfigTags.Mode) == Modes.Generate && !decoderExceptionThrown) {
            testResultsArtefactDirectory = JsonArtefactTestResults.createTestResultsArtefact(settings, licenseSUT,
                    sequencesOutputDir, logsOutputDir, htmlOutputDir, sequencesVerdicts, coverageSummary, coverageDir);

            if(settings.get(ConfigTags.StateModelEnabled, false)) {
                JsonArtefactStateModel jsonArtefactStateModel = StateModelArtefactManager.createAutomaticArtefact(settings, licenseSUT);

                // If StateModelDifferenceAutomaticReport setting is enabled try to create the report automatically
                // And extract the information in a JSON Object format
                StateModelDifferenceJsonObject stateModelDifferenceJsonObject = automaticStateModelDifference();
                jsonArtefactStateModel.setStateModelDifference(stateModelDifferenceJsonObject);

                // Save State Model Difference HTML report to associate to updateStateModelDifferenceJsonMap
                reportHTMLStateModelDifference = stateModelDifferenceJsonObject.getStateModelDifferenceReport();

                stateModelArtefactDirectory = jsonArtefactStateModel.createJsonFileStateModelArtefact();
            }
        }
        
        // Execute the POST request to insert and obtain the TestResults ArtefactId
        String artefactIdTestResults = insertTestResultsPKM(testResultsArtefactDirectory);

        if(settings.get(ConfigTags.StateModelEnabled, false)) {
            // Execute the POST request to insert and obtain the StateModels ArtefactId
            insertStateModelPKM(stateModelArtefactDirectory);
        }

        // Update TESTAR output run folder with artefact id to use the HttpReportServer web service
        updateOutputRunFolder(artefactIdTestResults);
    }

    /**
     * Automatically calculate the State Model Difference report if
     * StateModelDifferenceAutomaticReport test.setting is enabled.
     * 
     * Prioritize test settings parameters (PreviousApplicationName & PreviousApplicationVersion).
     * 
     * If no PreviousApplicationName specified, use current ApplicationName.
     * If no PreviousApplicationVersion specified, try to decrease Integer or Double ApplicationVersion.
     */
    protected StateModelDifferenceJsonObject automaticStateModelDifference() {

        // Create the JSON Object that contains the information about State Model Difference
        StateModelDifferenceJsonObject stateModelDifferenceJsonObject = new StateModelDifferenceJsonObject();

        if(settings.get(ConfigTags.Mode) == Modes.Generate 
                && settings.get(ConfigTags.StateModelEnabled, false)
                && settings.get(ConfigTags.StateModelDifferenceAutomaticReport, false)
                && !decoderExceptionThrown) {

            try {
                // Define current State Model version
                String currentApplicationName = settings.get(ConfigTags.ApplicationName,"");
                String currentVersion = settings.get(ConfigTags.ApplicationVersion,"");
                Pair<String,String> currentStateModel = new Pair<>(currentApplicationName, currentVersion);

                // By default we determine that we want to compare same ApplicationName
                String previousApplicationName = currentApplicationName;

                // Check if user selects a specific Application Name of previous State Model
                if(!settings.get(ConfigTags.PreviousApplicationName,"").isEmpty()) {
                    previousApplicationName = settings.get(ConfigTags.PreviousApplicationName,"");
                }

                String previousVersion = "";

                // Check if user selects a specific Application Version of previous State Model
                if(!settings.get(ConfigTags.PreviousApplicationVersion,"").isEmpty()) {
                    previousVersion = settings.get(ConfigTags.PreviousApplicationVersion,"");
                } else {
                    // Try automatic calculate version decrease
                    // Do we want to automatically compare in this way ?
                    // Or access to database and check all existing versions < currentVersion ?
                    if(StringUtils.isNumeric(currentVersion)) {
                        previousVersion = String.valueOf(Integer.parseInt(currentVersion) - 1);
                    }
                    else if (Pattern.matches("([0-9]*)\\.([0-9]*)", currentVersion)) {
                        previousVersion = String.valueOf(Double.parseDouble(currentVersion) - 1);
                    }
                    else {
                        System.out.println("WARNING: State Model Difference could not calculate previous application version automatically");
                        System.out.println("Try to use manual State Model Difference");

                        // We cannot obtain previous version, finish
                        return stateModelDifferenceJsonObject;
                    }
                }

                Pair<String,String> previousStateModel = new Pair<>(previousApplicationName, previousVersion);

                // Obtain Database Configuration, from Settings by default
                Config config = new Config();
                config.setConnectionType(settings.get(ConfigTags.DataStoreType,""));
                config.setServer(settings.get(ConfigTags.DataStoreServer,""));
                config.setDatabaseDirectory(settings.get(ConfigTags.DataStoreDirectory,""));
                config.setDatabase(settings.get(ConfigTags.DataStoreDB,""));
                config.setUser(settings.get(ConfigTags.DataStoreUser,""));
                config.setPassword(settings.get(ConfigTags.DataStorePassword,""));

                // State Model Difference Report Directory Name
                String dirName = OutputStructure.outerLoopOutputDir  + File.separator + "StateModelDifference_"
                        + previousStateModel.left() + "_" + previousStateModel.right() + "_vs_"
                        + currentStateModel.left() + "_" + currentStateModel.right();

                // Execute the State Model Difference to create an HTML report
                StateModelDifferenceManager modelDifferenceManager = new StateModelDifferenceManager(config, dirName);
                modelDifferenceManager.calculateModelDifference(config, previousStateModel, currentStateModel);

                // Update State Model Difference JSON Object with the Difference Report information
                stateModelDifferenceJsonObject.setPreviousStateModelAppName(previousApplicationName);
                stateModelDifferenceJsonObject.setPreviousStateModelAppVersion(previousVersion);
                stateModelDifferenceJsonObject.setExistsPreviousStateModel(modelDifferenceManager.existsPreviousStateModel());
                stateModelDifferenceJsonObject.setNumberDisappearedAbstractStates(modelDifferenceManager.getNumberDisappearedAbstractStates());
                stateModelDifferenceJsonObject.setNumberNewAbstractStates(modelDifferenceManager.getNumberNewAbstractStates());
                stateModelDifferenceJsonObject.setStateModelDifferenceReport(dirName);
                stateModelDifferenceJsonObject.setSpecificWidgetTreeDifference(modelDifferenceManager.getSpecificWidgetTreeDifference());

                if(modelDifferenceManager.existsPreviousStateModel()) {
                    JsonArtefactLogs.addMessage("TESTAR State Model Difference Report created");
                }

            } catch (Exception e) {
                System.err.println("ERROR: Trying to create an automatic State Model Difference");
                e.printStackTrace();
                JsonArtefactLogs.addWarning("ERROR: Trying to create an automatic State Model Difference");
            }
        }

        return stateModelDifferenceJsonObject;
    }

    /**
     * Insert TestResults Artefact in the PKM and return the ArtefactId. 
     * 
     * @param artefactTestResults
     * @return artefactId
     */
    protected String insertTestResultsPKM(String artefactTestResults) {
        // TODO: Allow Record mode when Listening mode implemented
        if(settings.get(ConfigTags.Mode) == Modes.Generate && !decoderExceptionThrown) {

            String artefactIdTestResults = PkmRequest.postArtefactTestResults(settings, artefactTestResults);

            if(artefactIdTestResults != "TestResultsErrorArtefactId") {
                return artefactIdTestResults;
            }

            decoderExceptionThrown = true;
            System.err.println("ERROR! Trying to Insert TestResults Artefact");
            JsonArtefactLogs.addWarning("ERROR! Trying to Insert TestResults Artefact");
        }

        return "TestResultsErrorArtefactId";
    }

    /**
     * Insert StateModel Artefact in the PKM and return the ArtefactId. 
     * 
     * @param artefactStateModel
     * @return artefactId
     */
    protected String insertStateModelPKM(String artefactStateModel) {
        // TODO: Allow Record mode when Listening mode implemented
        if(settings.get(ConfigTags.Mode) == Modes.Generate && !decoderExceptionThrown) {

            String artefactIdStateModel = PkmRequest.postArtefactStateModel(settings, artefactStateModel);

            if(artefactIdStateModel != "StateModelErrorArtefactId") {
                return artefactIdStateModel;
            }

            decoderExceptionThrown = true;
            System.err.println("ERROR! Trying to Insert StateModel Artefact");
            JsonArtefactLogs.addWarning("ERROR! Trying to Insert StateModel Artefact");
        }

        return "StateModelErrorArtefactId";
    }

    /**
     * DECODER needs a Map to have a relation between the TESTAR TestResults output results 
     * and the TESTAR TestResults ArtefactId. 
     * This method renames TESTAR TestResults OutputRunFolder with ArtefactId name.
     * 
     * @param artefactIdTestResults
     */
    protected void updateOutputRunFolder(String artefactIdTestResults) {

        // If we are not in Generate Mode we do not want to move the output folder
        if(settings.get(ConfigTags.Mode) != Modes.Generate || decoderExceptionThrown) {
            return;
        }
        
        JsonArtefactLogs.addMessage("Preparing TestResult OutputResults folder with ArtefactId");
        
        try {
            File artefactOutputFolder = new File(Main.testarDir + artefactIdTestResults + File.separator + "output" + File.separator);
            File outputRunFile = new File(OutputStructure.outerLoopOutputDir);
            FileUtils.moveDirectory(outputRunFile, artefactOutputFolder);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR moving OutputRunFolder with ArtefactId");
            JsonArtefactLogs.addWarning("ERROR preparing OutputRunFolder TestResult-ArtefactId");
        }

        // If permanent JettyServer exists, copy the folder inside resources directory
        try {
            String jettyPath = "JettyServer" + File.separator + "bin" + File.separator + "resources" + File.separator;
            if(new File(Main.testarDir + jettyPath).exists()) {
                FileUtils.copyDirectory(new File(Main.testarDir + artefactIdTestResults), new File(Main.testarDir + jettyPath + File.separator + artefactIdTestResults + File.separator));
            }
        } catch(Exception e) {
            System.out.println("ERROR preparing JettyServer resources folder");
            JsonArtefactLogs.addWarning("ERROR preparing JettyServer resources folder");
        }
    }

}

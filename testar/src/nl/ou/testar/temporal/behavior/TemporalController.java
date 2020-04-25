package nl.ou.testar.temporal.behavior;

import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.StateManagementTags;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;

import nl.ou.testar.temporal.foundation.PairBean;
import nl.ou.testar.temporal.foundation.ValStatus;
import nl.ou.testar.temporal.ioutils.CSVHandler;
import nl.ou.testar.temporal.ioutils.JSONHandler;
import nl.ou.testar.temporal.ioutils.SimpleLog;
import nl.ou.testar.temporal.model.*;
import nl.ou.testar.temporal.modelcheck.*;
import nl.ou.testar.temporal.oracle.*;
import nl.ou.testar.temporal.selector.APModelManager;
import nl.ou.testar.temporal.foundation.TagBean;
import nl.ou.testar.temporal.util.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.fruit.alayer.Tag;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static nl.ou.testar.temporal.util.Common.prettyCurrentTime;
import static org.fruit.monkey.ConfigTags.AbstractStateAttributes;


/**
 * Temporal Controller: orchestrates the Model Check function of TESTAR
 */
public class TemporalController {
    private String ApplicationName;
    private String ApplicationVersion;
    private String Modelidentifier;


    private String outputDir;
    private boolean ltlSPOTToWSLPath;
    private String ltlSPOTMCCommand;
    private boolean ltlSPOTEnabled;
    private boolean ctlITSToWSLPath;
    private String ctlITSMCCommand;
    private boolean ctlITSEnabled;
    private boolean ctlGALToWSLPath;
    private String ctlGALMCCommand;
    private boolean ctlGALEnabled;

    private boolean ltlITSToWSLPath;
    private String ltlITSMCCommand;
    private boolean ltlITSEnabled;
    private boolean ltlLTSMINToWSLPath;
    private String ltlLTSMINMCCommand;
    private boolean ltlLTSMINEnabled;
    private String APModelManagerFile;
    private String oracleFile;
    private boolean verbose;
    private boolean counterExamples;

    private boolean instrumentDeadlockState;

    private APModelManager apModelManager;
    private TemporalModel tModel;
    private TemporalDBManager tDBManager;
    private List<TemporalOracle> oracleColl;
    private SimpleLog simpleLog;
    private String logFileName;

    public TemporalController(final Settings settings,  String outputDir) {

        this.ApplicationName = settings.get(ConfigTags.ApplicationName);
        this.ApplicationVersion = settings.get(ConfigTags.ApplicationVersion);
        setModelidentifier(settings);
        if (outputDir.equals("")) {
            this.outputDir = createTemporalFolder(settings);
        } else {
            this.outputDir = outputDir;
        }
        logFileName=this.outputDir +"log.txt";
        simpleLog= new SimpleLog(logFileName,true);
        simpleLog.append(prettyCurrentTime() + " | " +"Temporal Component uses output folder: "+this.outputDir+"\n");
        tDBManager = new TemporalDBManager(settings);
        tModel = new TemporalModel();
        ltlSPOTToWSLPath = settings.get(ConfigTags.TemporalLTL_SPOTCheckerWSL);
        ltlSPOTMCCommand = settings.get(ConfigTags.TemporalLTL_SPOTChecker);
        ltlSPOTEnabled = settings.get(ConfigTags.TemporalLTL_SPOTChecker_Enabled);

        ctlITSToWSLPath = settings.get(ConfigTags.TemporalCTL_ITSCheckerWSL);
        ctlITSMCCommand = settings.get(ConfigTags.TemporalCTL_ITSChecker);
        ctlITSEnabled = settings.get(ConfigTags.TemporalCTL_ITSChecker_Enabled);
        ctlGALToWSLPath = settings.get(ConfigTags.TemporalCTL_GALCheckerWSL);
        ctlGALMCCommand = settings.get(ConfigTags.TemporalCTL_GALChecker);
        ctlGALEnabled = settings.get(ConfigTags.TemporalCTL_GALChecker_Enabled);
        ltlITSToWSLPath = settings.get(ConfigTags.TemporalLTL_ITSCheckerWSL);
        ltlITSMCCommand = settings.get(ConfigTags.TemporalLTL_ITSChecker);
        ltlITSEnabled = settings.get(ConfigTags.TemporalLTL_ITSChecker_Enabled);

        ltlLTSMINToWSLPath = settings.get(ConfigTags.TemporalLTL_LTSMINCheckerWSL);
        ltlLTSMINMCCommand = settings.get(ConfigTags.TemporalLTL_LTSMINChecker);
        ltlLTSMINEnabled = settings.get(ConfigTags.TemporalLTL_LTSMINChecker_Enabled);

        APModelManagerFile = settings.get(ConfigTags.TemporalAPModelManager);
        oracleFile = settings.get(ConfigTags.TemporalOracles);
        verbose = settings.get(ConfigTags.TemporalVerbose);
        counterExamples = settings.get(ConfigTags.TemporalCounterExamples);
        instrumentDeadlockState = settings.get(ConfigTags.TemporalInstrumentDeadlockState);

        setDefaultAPModelmanager();

    }

    public TemporalController(final Settings settings) {
        this(settings, "");
    }

    /**
     * no params
     * @return outputdirectory
     */
    public String getOutputDir() {
        return outputDir;
    }

    private void setTemporalModelMetaData(AbstractStateModel abstractStateModel) {
        if (abstractStateModel != null) {
            tModel.setApplicationName(abstractStateModel.getApplicationName());
            tModel.setApplicationVersion(abstractStateModel.getApplicationVersion());
            tModel.setApplication_ModelIdentifier(abstractStateModel.getModelIdentifier());
            tModel.setApplication_AbstractionAttributes(abstractStateModel.getAbstractionAttributes());
        }


    }

    private void setModelidentifier(Settings settings) {

        //assumption is that the model is created with the same abstraction pon concrete layer as on the abstract layer.
        // we can inspect the graphmodel for the abstract layer,
        // but we cannot inspect the graphmodel for the abstraction that used on the concretelayer.
        // for new models we enforce this by setting "TemporalConcreteEqualsAbstract = true" in the test.settings file
        // copied from Main.initcodingmanager
        if (!settings.get(ConfigTags.AbstractStateAttributes).isEmpty()) {
            Tag<?>[] abstractTags = settings.get(AbstractStateAttributes).stream().map(StateManagementTags::getTagFromSettingsString).filter(Objects::nonNull).toArray(Tag<?>[]::new);
            CodingManager.setCustomTagsForAbstractId(abstractTags);
        }
        //copied from StateModelManagerFactory
        // get the abstraction level identifier that uniquely identifies the state model we are testing against.
        this.Modelidentifier = CodingManager.getAbstractStateModelHash(ApplicationName, ApplicationVersion);

    }

    private String createTemporalFolder(final Settings settings) {
        String outputDir = settings.get(ConfigTags.OutputDir);
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + settings.get(ConfigTags.TemporalDirectory);

        if (settings.get(ConfigTags.TemporalSubDirectories)) {
            String runFolder = Common.CurrentDateToFolder();
            outputDir = outputDir + File.separator + runFolder;
        }
        new File(outputDir).mkdirs();
        outputDir = outputDir + File.separator;
        return outputDir;
    }


    public void saveAPModelManager(String filename) {
        simpleLog.append(prettyCurrentTime() + " | " + "generating APModelManager file: "+filename);
        JSONHandler.save(apModelManager, outputDir + filename, true);
    }

    private void loadApModelManager(String filename) {
        this.apModelManager = (APModelManager) JSONHandler.load(filename, apModelManager.getClass());
        tDBManager.setApModelManager(this.apModelManager);
    }

    public List<TemporalOracle> getOracleColl() {
        return oracleColl;
    }

    private void setOracleColl(List<TemporalOracle> oracleColl) {
        this.oracleColl = oracleColl;
        this.oracleColl.sort(Comparator.comparing(TemporalOracle::getPatternTemporalType)); //sort by type
    }

    private void updateOracleCollMetaData() {
        LocalDateTime localDateTime = LocalDateTime.now();
        for (TemporalOracle ora : oracleColl
        ) {
            ora.setApplicationName(tModel.getApplicationName());
            ora.setApplicationVersion(tModel.getApplicationVersion());
            ora.setApplication_ModelIdentifier(tModel.getApplication_ModelIdentifier());
            ora.setApplication_AbstractionAttributes(tModel.getApplication_AbstractionAttributes());
            ora.set_modifieddate(localDateTime.toString());
        }
    }

    public void setDefaultAPModelmanager() {
        this.apModelManager = new APModelManager(true);
        tDBManager.setApModelManager(apModelManager);
    }



    public String pingDB() {

        StringBuilder sb = new StringBuilder();
        List<AbstractStateModel> models = tDBManager.fetchAbstractModels();
        if (models.isEmpty()) {
            sb.append("model count: 0\n");
        } else {
            sb.append("model count: ").append(models.size()).append("\n");
            sb.append("Model info:\n");
            for (AbstractStateModel abs : models
            ) {
                sb.append("APP: ").append(abs.getApplicationName()).append(", VERSION: ")
                        .append(abs.getApplicationVersion()).append(", ID: ").append(abs.getModelIdentifier())
                        .append(", ABSTRACTION: ").append(abs.getAbstractionAttributes()).append("\n");
            }
        }
        String dbfilename = outputDir + "Databasemodels.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dbfilename))) {
            bw.write(sb.toString());
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        simpleLog.append("generated file: " + dbfilename);
        return "generated file: " + dbfilename;
    }


    //*********************************
    private void settModel(AbstractStateModel abstractStateModel, boolean instrumentTerminalState) {
        long start_time = System.currentTimeMillis();
        int runningWcount=0;
        int stateCount=0;
        int totalStates;
        int chunks=10;
        tDBManager.dbReopen();
        //candidate for refactoring as maintaining Oresultset is responsibility of TemporalDBManager
        OResultSet resultSet = tDBManager.getConcreteStatesFromOrientDb(abstractStateModel);
        totalStates=tDBManager.getConcreteStateCountFromOrientDb(abstractStateModel);
        Map<String,Integer> commentWidgetDistri = new HashMap<>();
        MultiValuedMap<String,String> logNonDeterministicTransitions = new HashSetValuedHashMap<>();
        List<String> logTerminalStates= new ArrayList<>();
        boolean firstTerminalState = true;
        StateEncoding terminalStateEnc;
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()) {
                Optional<OVertex> op = result.getVertex();
                if (!op.isPresent()) continue;
                OVertex stateVertex = op.get();
                StateEncoding senc = new StateEncoding(stateVertex.getIdentity().toString());
                Set<String> propositions = new LinkedHashSet<>();
                boolean terminalState;
                Iterable<OEdge> outedges = stateVertex.getEdges(ODirection.OUT, "ConcreteAction"); //could be a SQL- like query as well
                Iterator<OEdge> edgeiter = outedges.iterator();
                terminalState = !edgeiter.hasNext();

                if (terminalState) {
                    logTerminalStates.add(stateVertex.getIdentity().toString() );
                    //tModel.addLog("State: " + stateVertex.getIdentity().toString() + " is terminal.");
                    if (instrumentTerminalState && firstTerminalState) {
                        //add stateenc for 'Dead', inclusive dead transition selfloop;
                        terminalStateEnc = new StateEncoding("#" + TemporalModel.getDeadProposition());
                        Set<String> terminalStatePropositions = new LinkedHashSet<>();
                        //terminalStatePropositions.add("dead");   //redundant on transition based automatons
                        terminalStateEnc.setStateAPs(terminalStatePropositions);
                        TransitionEncoding deadTrenc = new TransitionEncoding();
                        deadTrenc.setTransition(TemporalModel.getDeadProposition() + "_selfloop");
                        deadTrenc.setTargetState("#" + TemporalModel.getDeadProposition());
                        Set<String> deadTransitionPropositions = new LinkedHashSet<>();
                        deadTransitionPropositions.add(TemporalModel.getDeadProposition());
                        deadTrenc.setTransitionAPs(deadTransitionPropositions);
                        List<TransitionEncoding> deadTrencList = new ArrayList<>();
                        deadTrencList.add(deadTrenc);
                        terminalStateEnc.setTransitionColl(deadTrencList);
                        tModel.addStateEncoding(terminalStateEnc, false);
                        firstTerminalState = false;
                    }
                    if (!instrumentTerminalState)
                        stateVertex.setProperty(TagBean.IsTerminalState.name(), true);  //candidate for refactoring
                }
                for (String propertyName : stateVertex.getPropertyNames()) {
                    tDBManager.computeAtomicPropositions(tModel.getApplication_BackendAbstractionAttributes(),propertyName, stateVertex, propositions, false);
                }
                PairBean<Set<String>,Integer> pb = tDBManager.getWidgetPropositions(senc.getState(), tModel.getApplication_BackendAbstractionAttributes());
                propositions.addAll(pb.left());// concrete widgets
                commentWidgetDistri.put(senc.getState(),pb.right());
                //tModel.addComments("#Widgets of State "+senc.getState()+" = "+);
                runningWcount=runningWcount+ pb.right();
                senc.setStateAPs(propositions);
                if (instrumentTerminalState && terminalState) {
                    TransitionEncoding deadTrenc = new TransitionEncoding();
                    deadTrenc.setTransition("#" + TemporalModel.getDeadProposition() + "_" + stateVertex.getIdentity().toString());
                    deadTrenc.setTargetState("#" + TemporalModel.getDeadProposition());
                    Set<String> deadTransitionPropositions = new LinkedHashSet<>();
                    deadTransitionPropositions.add(TemporalModel.getDeadProposition());
                    deadTrenc.setTransitionAPs(deadTransitionPropositions);
                    List<TransitionEncoding> deadTrencList = new ArrayList<>();
                    deadTrencList.add(deadTrenc);
                    senc.setTransitionColl(deadTrencList);
                } else senc.setTransitionColl(tDBManager.getTransitions(senc.getState(),tModel.getApplication_BackendAbstractionAttributes()));

                tModel.addStateEncoding(senc, false);
            }
        stateCount++;
        if (stateCount % (Math.floorDiv(totalStates, chunks)) == 0){
            simpleLog.append(prettyCurrentTime() + " | " + "States processed: "+Math.floorDiv((100*stateCount),totalStates)+"%");
        }
        }


        resultSet.close();
        tModel.finalizeTransitions(); //update once. this is a costly operation
        for (StateEncoding stenc : tModel.getStateEncodings()
        ) {
            List<String> encodedConjuncts = new ArrayList<>();
            for (TransitionEncoding tren : stenc.getTransitionColl()
            ) {
                String enc = tren.getEncodedTransitionAPConjunct();
                if (encodedConjuncts.contains(enc)) {
                    logNonDeterministicTransitions.put(stenc.getState(),tren.getTransition());
                    //tModel.addLog("State: " + stenc.getState() + " has  non-deterministic transition: " + tren.getTransition());
                } else encodedConjuncts.add(enc);
            }
        }

        tModel.addLog("Terminal States : "+logTerminalStates.toString());
        String mapAsString = commentWidgetDistri.keySet().stream()
                .map(key -> key + "->" + commentWidgetDistri.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        tModel.addComments("#Widgets per State : "+mapAsString);

        mapAsString = logNonDeterministicTransitions.keySet().stream()
                .map(key -> key + "->" + logNonDeterministicTransitions.get(key).toString())
                .collect(Collectors.joining(", ", "{", "}"));
        tModel.addLog("non-deterministic transitions per State: "+mapAsString);


        tModel.setTraces(tDBManager.fetchTraces(tModel.getApplication_ModelIdentifier()));
        List<String> initStates = new ArrayList<>();
        for (TemporalTrace trace : tModel.getTraces()
        ) {
            TemporalTraceEvent traceevent = trace.getTraceEvents().get(0);
            initStates.add(traceevent.getState());
        }

        tModel.setInitialStates(initStates);

        tModel.addComments("Total #Widgets = "+runningWcount);
        long end_time = System.currentTimeMillis();
        long difference = (end_time-start_time)/1000;
        tModel.addComments("Duration to create the model:"+difference +" (s)" );
        tDBManager.dbClose();
    }


    private AbstractStateModel getAbstractStateModel() {
        AbstractStateModel abstractStateModel;
        abstractStateModel = tDBManager.selectAbstractStateModelByModelId(Modelidentifier);
        if (abstractStateModel == null) {
            tModel.addLog("ERROR: Model with identifier : " + Modelidentifier + " was not found in the graph database <" + tDBManager.getDatabase()+">");
            simpleLog.append("ERROR: Model with identifier : " + Modelidentifier + " was not found in the graph database <" + tDBManager.getDatabase()+">");
        }
        return abstractStateModel;
    }


    public boolean saveToGraphMLFile(String file, boolean excludeWidget) {
        simpleLog.append(prettyCurrentTime() + " | " + "generating "+file+" file");
        AbstractStateModel abstractStateModel = getAbstractStateModel();
        if (abstractStateModel != null) {
            return tDBManager.saveToGraphMLFile(abstractStateModel, outputDir + file, excludeWidget);
        } else return false;
    }

    private void saveModelAsJSON(String toFile) {
        simpleLog.append(prettyCurrentTime() + " | " + "generating APEncodedModel file: "+toFile);
        JSONHandler.save(tModel, outputDir + toFile);
    }

    private void saveModelForChecker(TemporalFormalism tmptype, String file) {
        String contents = "";


        if (tmptype.equals(TemporalFormalism.LTL) || tmptype.equals(TemporalFormalism.LTL_SPOT)) {
            contents = tModel.makeHOAOutput();
        }
        if (tmptype.equals(TemporalFormalism.CTL) || tmptype.equals(TemporalFormalism.LTL_ITS) || tmptype.equals(TemporalFormalism.LTL_LTSMIN)) {
            contents = tModel.makeETFOutput(tmptype.supportsMultiInitialStates);
        }
        if (tmptype.equals(TemporalFormalism.CTL_GAL)) {  // **under construction ITS-GAL
            contents = tModel.makeGALOutput();
        }
            File output = new File(file);
            saveStringToFile(contents,output);
        }

    /**
     *
     * @param oracleColl nnn
     * @param output nnn
     * @param doTransformation nn
     * @link saveStringToFile()
     */
    private void saveFormulasForChecker(List<TemporalOracle> oracleColl, File output, boolean doTransformation) {

        String contents = tModel.validateAndMakeFormulas(oracleColl, doTransformation);
        TemporalController.saveStringToFile(contents, output);
    }

    /**
     * @see  #saveStringToFile(String, File)
     * @param contents jj
     * @param output jj
     */
    private static void saveStringToFile(String contents, File output) {

        try {

            if (output.exists() || output.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output.getAbsolutePath()), StandardCharsets.UTF_8));
                writer.append(contents);
                writer.close();
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }


    public void MCheck() {

        MCheck(APModelManagerFile, oracleFile, verbose, counterExamples, instrumentDeadlockState,
                ltlSPOTMCCommand, ltlSPOTToWSLPath, ltlSPOTEnabled,
                ctlITSMCCommand, ctlITSToWSLPath, ctlITSEnabled,
                ltlITSMCCommand, ltlITSToWSLPath, ltlITSEnabled,
                ltlLTSMINMCCommand, ltlLTSMINToWSLPath, ltlLTSMINEnabled,
                ctlGALMCCommand, ctlGALToWSLPath, ctlGALEnabled);

    }


    public void MCheck(String APModelManagerFile, String oracleFile,
                       boolean verbose, boolean counterExamples, boolean instrumentTerminalState,
                       String ltlSpotMCCommand, boolean ltlSpotWSLPath, boolean ltlSpotEnabled,
                       String ctlItsMCCommand,  boolean ctlItsWSLPath, boolean ctlItsEnabled,
                       String ltlItsMCCommand, boolean ltlItsWSLPath, boolean ltlItsEnabled,
                       String ltlLtsminMCCommand, boolean ltlLtsminWSLPath, boolean ltlltsminEnabled,
                       String ctlGalMCCommand, boolean ctlGalWSLPath, boolean ctlGalEnabled
                       ) {
        try {

            simpleLog.append(prettyCurrentTime() + " | " + "Temporal model-checking started");
            List<TemporalOracle> fromcoll = CSVHandler.load(oracleFile, TemporalOracle.class);
            if (fromcoll == null) {
                simpleLog.append(prettyCurrentTime()+"Error: verify the file at location '" + oracleFile + "'");
            } else {
                tModel = new TemporalModel();
                AbstractStateModel abstractStateModel = getAbstractStateModel();
                if (abstractStateModel == null){
                    simpleLog.append("Error: StateModel not available");
                }
                else {
                String OracleCopy = "copy_of_applied_" + Paths.get(oracleFile).getFileName().toString();
                if (verbose) {
                    Files.copy((new File(oracleFile).toPath()),
                            new File(outputDir + OracleCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                String strippedFile;
                String filename = Paths.get(oracleFile).getFileName().toString();
                if (filename.contains(".")) strippedFile = filename.substring(0, filename.lastIndexOf("."));
                else strippedFile = filename;
                File inputvalidatedFile = new File(outputDir + strippedFile + "_inputvalidation.csv");
                File modelCheckedFile = new File(outputDir + strippedFile + "_modelchecked.csv");


                makeTemporalModel(APModelManagerFile, verbose, instrumentTerminalState);
                setOracleColl(fromcoll);
                updateOracleCollMetaData();
                Map<TemporalFormalism, List<TemporalOracle>> oracleTypedMap =
                            fromcoll.stream().collect(Collectors.groupingBy(TemporalOracle::getPatternTemporalType));

                if (verbose) {
                saveToGraphMLFile("GraphML.XML", false);
                saveToGraphMLFile("GraphML_NoWidgets.XML", true);
                //saveModelAsJSON("APEncodedModel.json");
                }
                List<TemporalOracle> initialoraclelist = new ArrayList<>();
                List<TemporalOracle> finaloraclelist = new ArrayList<>();
                for (Map.Entry<TemporalFormalism, List<TemporalOracle>> oracleentry : oracleTypedMap.entrySet()
                ) {
                    List<TemporalOracle> modelCheckedOracles = null;
                    File automatonFile ;

                    //String oracleType = oracleentry.getKey().name();
                   TemporalFormalism oracleType = oracleentry.getKey();

                    List<TemporalOracle> oracleList = oracleentry.getValue();
                    automatonFile = new File(outputDir +oracleType+"_model."+oracleentry.getKey().fileExtension);

                    File formulaFile = new File(outputDir + oracleType + "_formulas.txt");
                    File resultsFile = new File(outputDir + oracleType + "_results.txt");
                    File syntaxformulaFile = new File(outputDir + oracleType + "_syntaxcheckedformulas.txt");
                    initialoraclelist.addAll(oracleList);
                    simpleLog.append(prettyCurrentTime() + " | " + oracleType + " invoking the " + "backend model-checker");
                    saveModelForChecker(oracleType, automatonFile.getAbsolutePath());

                    ModelChecker checker = CheckerFactory.getModelChecker(oracleentry.getKey());

                    if ((ltlItsEnabled && oracleType == TemporalFormalism.LTL_ITS)
                        ||(ltlltsminEnabled && oracleType == TemporalFormalism.LTL_LTSMIN)
                        ||(ltlSpotEnabled && (oracleType == TemporalFormalism.LTL ||
                            oracleType == TemporalFormalism.LTL_SPOT))){
                        //formula ltl model variant converter
                        // instrumentTerminalState will determine whether this return value is ""
                        String aliveprop = tModel.getPropositionIndex("!" + TemporalModel.getDeadProposition());
                        if (!aliveprop.equals("")) {
                            saveFormulasForChecker(oracleList, formulaFile, false);
                            List<String> tmpformulas =FormulaVerifier.verifyLTL(ltlSpotMCCommand, ltlSpotWSLPath,
                                                        formulaFile.getAbsolutePath(), syntaxformulaFile);
                            List<TemporalOracle> tmporacleList = new ArrayList<>();
                            int j = 0;
                            for (TemporalOracle ora : oracleList
                            ) {
                                TemporalOracle oraClone = ora.clone();
                                TemporalPatternBase pat = oraClone.getPatternBase();
                                pat.setPattern_Formula(tmpformulas.get(j));
                                tmporacleList.add(oraClone);
                                j++;
                            }
                            saveFormulasForChecker(tmporacleList, formulaFile, true);
                        } else {
                            saveFormulasForChecker(oracleList, formulaFile, true);
                        }
                    }else{
                        saveFormulasForChecker(oracleList, formulaFile,true);
                    }

                    if (ltlSpotEnabled && (oracleType == TemporalFormalism.LTL ||
                                        oracleType == TemporalFormalism.LTL_SPOT)) {
                        modelCheckedOracles = checker.check(ltlSpotMCCommand, ltlSpotWSLPath, counterExamples,
                        automatonFile.getAbsolutePath(), formulaFile.getAbsolutePath(),resultsFile,tModel,oracleList);

                    }
                     if (ltlItsEnabled && (oracleType == TemporalFormalism.LTL_ITS)){
                         modelCheckedOracles = checker.check(ltlItsMCCommand, ltlItsWSLPath, counterExamples,
                        automatonFile.getAbsolutePath(), formulaFile.getAbsolutePath(),resultsFile,tModel,oracleList);
                        }

                     if(ltlltsminEnabled && (oracleType == TemporalFormalism.LTL_LTSMIN)){
                        modelCheckedOracles = checker.check(ltlLtsminMCCommand, ltlLtsminWSLPath, counterExamples,
                        automatonFile.getAbsolutePath(), formulaFile.getAbsolutePath(),resultsFile,tModel,oracleList);
                     }
                    if (ctlItsEnabled &&  (  oracleType == TemporalFormalism.CTL ||
                                                    oracleType == TemporalFormalism.CTL_ITS)) {
                        modelCheckedOracles = checker.check(ctlItsMCCommand, ctlItsWSLPath, counterExamples,
                        automatonFile.getAbsolutePath(), formulaFile.getAbsolutePath(),resultsFile,tModel,oracleList);
                    }
                    if (ctlGalEnabled &&  (oracleType == TemporalFormalism.CTL_GAL )) {
                        if (tModel.getModelAPs().size()>250 ||tModel.getStateList().size()>25000){
                            simpleLog.append(prettyCurrentTime() + " | " + oracleType + " Warning:  real check is not executed: model too complex (propositions>250 or states>25000");
                        }
                        else{
                            modelCheckedOracles = checker.check(ctlGalMCCommand, ctlGalWSLPath, counterExamples,
                            automatonFile.getAbsolutePath(), formulaFile.getAbsolutePath(), resultsFile,tModel,oracleList);
                            simpleLog.append(prettyCurrentTime() + " | " + oracleType + " verifying results for this Model checker is not possible yet");
                        }
                    }

                    if(!(
                            (ltlSpotEnabled && ((oracleType == TemporalFormalism.LTL) || (oracleType == TemporalFormalism.LTL_SPOT)))||
                            (ltlltsminEnabled && (oracleType == TemporalFormalism.LTL_LTSMIN) )||
                            (ltlItsEnabled && (oracleType == TemporalFormalism.LTL_ITS))||
                            (ctlItsEnabled &&  ( oracleType == TemporalFormalism.CTL) ||  (oracleType == TemporalFormalism.CTL_ITS))||
                            (ctlGalEnabled &&  (oracleType == TemporalFormalism.CTL_GAL ))
                        ))
                    {
                        simpleLog.append(prettyCurrentTime() + " | " + oracleType + " Warning:  this oracle type is not implemented or disabled");
                    }

                    if (modelCheckedOracles != null) {
                        finaloraclelist.addAll(modelCheckedOracles);
                    }
                    else {
                        simpleLog.append(prettyCurrentTime() + " | " + oracleType + "  ** Error: no results from the model-checker");
                    }

                    if (!verbose) {
                        if ( automatonFile.exists()) Files.delete(automatonFile.toPath());
                        if (resultsFile.exists())Files.delete(resultsFile.toPath());
                        if (formulaFile.exists())Files.delete(formulaFile.toPath());
                        if (syntaxformulaFile.exists())Files.delete(syntaxformulaFile.toPath());
                        if (inputvalidatedFile.exists())Files.delete(inputvalidatedFile.toPath());
                    }
                    simpleLog.append(prettyCurrentTime() + " | " + oracleType + " model-checking completed");
                }
                CSVHandler.save(initialoraclelist, inputvalidatedFile.getAbsolutePath());
                if (finaloraclelist.size() != fromcoll.size()) {
                    simpleLog.append(prettyCurrentTime() + " | " + "** Warning: less oracle verdicts received than in original collection");
                    simpleLog.append(prettyCurrentTime() + " | " + "from file: "+ Paths.get(oracleFile).getFileName());
                }
                CSVHandler.save(finaloraclelist, modelCheckedFile.getAbsolutePath());
            }
            }
            simpleLog.append(prettyCurrentTime() + " | " + "Temporal model-checking completed");
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    public void makeTemporalModel(String APModelManagerFile, boolean verbose, boolean instrumentTerminalState) {
        try {
            simpleLog.append(prettyCurrentTime() + " | " + "compute temporal model started");
            tModel = new TemporalModel();

            AbstractStateModel abstractStateModel = getAbstractStateModel();
            if (abstractStateModel == null) {
                simpleLog.append("Error: StateModel not available");
            } else {
                setTemporalModelMetaData(abstractStateModel);
                if (APModelManagerFile.equals("")) {
                    setDefaultAPModelmanager();
                    saveAPModelManager("APModelManager_default.json");
                }
                else {
                    String APCopy = "copy_of_applied_" + Paths.get(APModelManagerFile).getFileName().toString();
                    if (verbose) {
                        Files.copy((new File(APModelManagerFile).toPath()),
                                new File(outputDir + APCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                loadApModelManager(APModelManagerFile);
                settModel(abstractStateModel, instrumentTerminalState);
                if (verbose) {
                    saveModelAsJSON("APEncodedModel.json");
                }

                simpleLog.append(prettyCurrentTime() + " | " + "compute temporal model completed");
            }
        } catch (Exception f) {
            f.printStackTrace();
        }

    }

    public void generateOraclesFromPatterns(String APModelManagerfile, String patternFile, String patternConstraintFile, int tactic_oraclesPerPattern) {
        try {
            simpleLog.append(" potential Oracle generator started \n");
            makeTemporalModel(APModelManagerfile, false, true);
            List<TemporalPattern> patterns = CSVHandler.load(patternFile, TemporalPattern.class);
            List<TemporalPatternConstraint> patternConstraints = null;
            if (!patternConstraintFile.equals("")) {
                patternConstraints = CSVHandler.load(patternConstraintFile, TemporalPatternConstraint.class);
            }

            File PotentialoracleFile = new File(outputDir + "TemporalPotentialOracles.csv");

            List<TemporalOracle> fromcoll;
            assert patterns != null;
            fromcoll = getPotentialOracles(patterns, patternConstraints, tactic_oraclesPerPattern);
            CSVHandler.save(fromcoll, PotentialoracleFile.getAbsolutePath());

            simpleLog.append(" potential Oracle generator completed \n");
        } catch (Exception f) {
            f.printStackTrace();
        }

    }


    private List<TemporalOracle> getPotentialOracles(List<TemporalPattern> patterns, List<TemporalPatternConstraint> patternConstraints, int tactic_oraclesPerPattern) {
        // there is no check on duplicate assignments:  a pattern can turn up as a oracle with exactly the same assignments.
        // the risk is remote due to the randomness on AP selection and e=randomness on constraint-set selection.
        List<TemporalOracle> potentialOracleColl = new ArrayList<>();
        List<String> modelAPSet = new ArrayList<>(tModel.getModelAPs());
        int trylimitConstraint = Math.min(250, 2 * modelAPSet.size());
        Random APRnd = new Random(5000000);
        for (TemporalPattern pat : patterns
        ) {
            Map<String, String> ParamSubstitutions;
            TemporalPatternConstraint patternConstraint = null;
            int patcIndex;
            TreeMap<Integer, Map<String, String>> constrainSets = null;
            boolean passConstraint = false;
            Random constraintRnd = new Random(6000000);
            int cSetindex = -1;
            Map<String, String> constraintSet;
            patcIndex = -1;
            if (patternConstraints != null) {
                for (int h = 0; h < patternConstraints.size(); h++) {
                    patternConstraint = patternConstraints.get(h);
                    if (pat.getPattern_Formula().equals(patternConstraint.getPattern_Formula())) {
                        patcIndex = h;
                        break;
                    }
                }
            }
            if (patcIndex != -1) {
                constrainSets = patternConstraint.getConstraintSets();
            }
            for (int i = 0; i < tactic_oraclesPerPattern; i++) {
                TemporalOracle potentialOracle = new TemporalOracle();
                if (constrainSets != null) {
                    cSetindex = constraintRnd.nextInt(constrainSets.size());//start set. constrainset number is 1,2,3,...
                }
                ParamSubstitutions = new HashMap<>();
                for (String param : pat.getPattern_Parameters()
                ) {
                    passConstraint = false;
                    String provisionalParamSubstitution;
                    if (constrainSets == null) {
                        provisionalParamSubstitution = modelAPSet.get(APRnd.nextInt(modelAPSet.size() - 1));
                        ParamSubstitutions.put(param, provisionalParamSubstitution);
                        passConstraint = true;  //virtually true
                    } else {
                        for (int k = 1; k < constrainSets.size() + 1; k++) {//constrainset number is 1,2,3,...
                            int ind = (k + cSetindex) % (constrainSets.size() + 1);
                            constraintSet = constrainSets.get(ind);
                            if (constraintSet.containsKey(param)) {
                                Pattern regexPattern = CachedRegexPatterns.addAndGet(constraintSet.get(param));
                                if (regexPattern == null) {
                                    continue; //no pass for this constraint-set due to invalid pattern
                                } else {
                                    for (int j = 0; j < trylimitConstraint; j++) {
                                        provisionalParamSubstitution = modelAPSet.get(APRnd.nextInt(modelAPSet.size() - 1));
                                        Matcher m = regexPattern.matcher(provisionalParamSubstitution);
                                        if (m.matches()) {
                                            ParamSubstitutions.put(param, provisionalParamSubstitution);
                                            passConstraint = true;
                                            break;// go to next parameter
                                        }
                                    }
                                }
                            } else {
                                provisionalParamSubstitution = modelAPSet.get(APRnd.nextInt(modelAPSet.size() - 1));
                                ParamSubstitutions.put(param, provisionalParamSubstitution);
                                passConstraint = true;  //virtually true
                                break;// go to next parameter
                            }
                            if (passConstraint) {
                                break;
                            }
                        }
                    }
                }
                potentialOracle.setPatternBase(pat); //downcasting of pat
                potentialOracle.setApplicationName(tModel.getApplicationName());
                potentialOracle.setApplicationVersion(tModel.getApplicationVersion());
                potentialOracle.setApplication_AbstractionAttributes(tModel.getApplication_AbstractionAttributes());
                potentialOracle.setApplication_ModelIdentifier(tModel.getApplication_ModelIdentifier());
                if (passConstraint) { //assignment found, save and go to next round for a pattern
                    if (cSetindex != -1) {
                        potentialOracle.setPattern_ConstraintSet(cSetindex + 1);// sets numbers from 1,2,3,...
                    }
                    MultiValuedMap<String, String> pattern_Substitutions = new HashSetValuedHashMap<>();
                    for (Map.Entry<String, String> paramsubst : ParamSubstitutions.entrySet()
                    ) {
                        pattern_Substitutions.put("PATTERN_SUBSTITUTION_" + paramsubst.getKey(), paramsubst.getValue());// improve?
                    }
                    potentialOracle.setPattern_Substitutions(pattern_Substitutions);
                    potentialOracle.setOracle_validationstatus(ValStatus.CANDIDATE);
                } else {
                    // no assignment found
                    potentialOracle.setOracle_validationstatus(ValStatus.ERROR);
                    potentialOracle.addLog("No valid assignment of substitutions found. Advise: review ConstraintSets");
                }
                potentialOracleColl.add(potentialOracle);
            }
        }
        return potentialOracleColl;
    }
}



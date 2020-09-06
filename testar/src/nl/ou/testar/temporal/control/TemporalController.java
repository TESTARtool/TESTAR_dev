 package nl.ou.testar.temporal.control;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.StateManagementTags;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;

import nl.ou.testar.temporal.foundation.ValStatus;
import nl.ou.testar.temporal.ioutils.CSVHandler;
import nl.ou.testar.temporal.ioutils.JSONHandler;
import nl.ou.testar.temporal.ioutils.SimpleLog;
import nl.ou.testar.temporal.model.*;
import nl.ou.testar.temporal.modelcheck.*;
import nl.ou.testar.temporal.oracle.*;
import nl.ou.testar.temporal.proposition.PropositionManager;
import nl.ou.testar.temporal.util.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.fruit.alayer.Tag;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static nl.ou.testar.temporal.util.OShelper.prettyCurrentTime;
import static org.fruit.monkey.ConfigTags.AbstractStateAttributes;


/**
 * Temporal Controller: orchestrates the Model Check function of TESTAR
 */
public class TemporalController {
    private  String ApplicationName;
    private  String ApplicationVersion;
    private String Modelidentifier;
    private  String outputDir;

    private  boolean ltlSPOTToWSLPath;
    private  String ltlSPOTMCCommand;
    private  boolean ltlSPOTEnabled;

    private  boolean ctlITSToWSLPath;
    private  String ctlITSMCCommand;
    private  boolean ctlITSEnabled;

    private  boolean ctlGALToWSLPath;
    private  String ctlGALMCCommand;
    private  boolean ctlGALEnabled;

    private  boolean ltlITSToWSLPath;
    private  String ltlITSMCCommand;
    private  boolean ltlITSEnabled;

    private  boolean ltlLTSMINToWSLPath;
    private  String ltlLTSMINMCCommand;
    private  boolean ltlLTSMINEnabled;

    private  boolean ctlLTSMINToWSLPath;
    private  String ctlLTSMINMCCommand;
    private  boolean ctlLTSMINEnabled;

    private  String propositionManagerFile;
    private  String oracleFile;
    private  boolean verbose;
    // GraphMLOnVerbose: when false=> Model-generation with even less overhead, despite verbosity. setting is NOT in GUI
    private  boolean exportGraphMLOnVerbose;
    private  boolean zip;

    private  boolean counterExamples;

    private  boolean instrumentDeadlockState;

    private  PropositionManager propositionManager;
    private  TemporalModel tModel;
    private  TemporalDBManager tDBManager;
    private  List<TemporalOracle> oracleColl;
    private  SimpleLog simpleLog;
    private boolean logDetailsInModelFile;


    public TemporalController(final Settings settings) {


        outputDir = createTemporalFolder(settings);
        String logFileName = outputDir + "log.txt";
        simpleLog= new SimpleLog(logFileName,true);
        simpleLog.append(prettyCurrentTime() + " | " +"Temporal Component uses output folder: "+outputDir+"\n");
        tDBManager = new TemporalDBManager(simpleLog);
        tModel = new TemporalModel();
        updateSettings(settings);
    }


    /**
     * no params
     * @return outputdirectory
     */
    public String getOutputDir() {
        return outputDir;
    }

    public void updateSettings(final Settings settings) {

        this.ApplicationName = settings.get(ConfigTags.ApplicationName);
        this.ApplicationVersion = settings.get(ConfigTags.ApplicationVersion);
        setModelidentifier(settings);

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

        ctlLTSMINToWSLPath = settings.get(ConfigTags.TemporalCTL_LTSMINCheckerWSL);
        ctlLTSMINMCCommand = settings.get(ConfigTags.TemporalCTL_LTSMINChecker);
        ctlLTSMINEnabled = settings.get(ConfigTags.TemporalCTL_LTSMINChecker_Enabled);

        propositionManagerFile = settings.get(ConfigTags.TemporalPropositionManager);

        oracleFile = settings.get(ConfigTags.TemporalOracles);
        verbose = settings.get(ConfigTags.TemporalVerbose);
        exportGraphMLOnVerbose = settings.get(ConfigTags.TemporalExportGraphMLOnVerbose);
        logDetailsInModelFile = settings.get(ConfigTags.TemporalLogDetailsInModelFile);

        zip= settings.get(ConfigTags.TemporalZipLargeFiles);
        counterExamples = settings.get(ConfigTags.TemporalCounterExamples);
        instrumentDeadlockState = settings.get(ConfigTags.TemporalInstrumentDeadlockState);
        tDBManager.updateSettings(settings);

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

        //assumption is that the model is created with the same abstraction on concrete layer as on the abstract layer.
        // we can inspect the graphmodel for the abstract layer,
        // but we cannot inspect the concretelayer metadata in the graphmodel for the abstraction that used.
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
            String runFolder = OShelper.CurrentDateToFolder();
            outputDir = outputDir + File.separator + runFolder;
        }
        //noinspection ResultOfMethodCallIgnored
        new File(outputDir).mkdirs();
        outputDir = outputDir + File.separator;
        return outputDir;
    }


    public void savePropositionManager(String filename) {
        simpleLog.append(prettyCurrentTime() + " | " + "generating Proposition Manager file: "+filename);
        JSONHandler.save(propositionManager, outputDir + filename, false);
    }

    private void loadPropositionManager(String filename) {
        this.propositionManager = (PropositionManager) JSONHandler.load(filename, PropositionManager.class);
        tDBManager.setPropositionManager(this.propositionManager);
    }

    public List<TemporalOracle> getOracleColl() {
        return oracleColl;
    }

    private void setOracleColl(List<TemporalOracle> oracleColl) {
        this.oracleColl = oracleColl;
        this.oracleColl.sort(Comparator.comparing(TemporalOracle::getPatternTemporalType)); //sort by type
    }


    public void setDefaultPropositionManager() {
        this.propositionManager = new PropositionManager(true);
        tDBManager.setPropositionManager(propositionManager);
    }



    public String pingDB() {
        String info =tDBManager.pingDB(); //Q & D
        String dbfilename = outputDir + "Databasemodels.csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dbfilename))) {
            bw.write(info);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        simpleLog.append("generated file: " + dbfilename);
        return "generated file: " + dbfilename;
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


    public boolean saveToGraphMLFile(String file, boolean excludeWidget, boolean zip) {
        simpleLog.append(prettyCurrentTime() + " | " + "generating "+file+" file");
        AbstractStateModel abstractStateModel = getAbstractStateModel();
        if (abstractStateModel != null) {
            return tDBManager.saveToGraphMLFile(abstractStateModel, outputDir + file, excludeWidget,zip);
        } else return false;
    }

    private void saveModelAsJSON(String toFile, boolean zip) {
        simpleLog.append(prettyCurrentTime() + " | " + "generating Model file: "+toFile);
        JSONHandler.save(tModel, outputDir + toFile,zip);
    }


    public void MCheck() {

        MCheck(propositionManagerFile, oracleFile, verbose,zip, counterExamples, instrumentDeadlockState,
                ltlSPOTMCCommand, ltlSPOTToWSLPath, ltlSPOTEnabled,
                ctlITSMCCommand, ctlITSToWSLPath, ctlITSEnabled,
                ltlITSMCCommand, ltlITSToWSLPath, ltlITSEnabled,
                ltlLTSMINMCCommand, ltlLTSMINToWSLPath, ltlLTSMINEnabled,
                ctlGALMCCommand, ctlGALToWSLPath, ctlGALEnabled,
                ctlLTSMINMCCommand, ctlLTSMINToWSLPath, ctlLTSMINEnabled,
                true,"");

    }


    public void MCheck(String propositionManagerFile,String oracleFile,
                       boolean verbose, boolean zip,boolean counterExamples, boolean instrumentTerminalState,
                       String ltlSpotMCCommand, boolean ltlSpotWSLPath, boolean ltlSpotEnabled,
                       String ctlItsMCCommand,  boolean ctlItsWSLPath, boolean ctlItsEnabled,
                       String ltlItsMCCommand, boolean ltlItsWSLPath, boolean ltlItsEnabled,
                       String ltlLtsminMCCommand, boolean ltlLtsminWSLPath, boolean ltlltsminEnabled,
                       String ctlGalMCCommand, boolean ctlGalWSLPath, boolean ctlGalEnabled,
                       String ctlLtsminMCCommand, boolean ctlLtsminWSLPath, boolean ctlltsminEnabled,
                       boolean sourceIsDb, String modelFile
                       ) {
        try {

            simpleLog.append(prettyCurrentTime() + " | " + "Temporal model-checking started");
            List<TemporalOracle> fromcoll = CSVHandler.load(oracleFile, TemporalOracle.class);

            if (fromcoll == null) {
                simpleLog.append(prettyCurrentTime()+"Error: verify the file at location '" + oracleFile + "'");
            } else {
                setTemporalModel(propositionManagerFile, verbose, instrumentTerminalState,zip,sourceIsDb,modelFile);
               if (tModel == null){
                    simpleLog.append("Error: StateModel not available");
                }
                else {

                if (verbose) {
                    String OracleCopy = "copy_of_applied_" + Paths.get(oracleFile).getFileName().toString();
                    Files.copy((new File(oracleFile).toPath()),
                            new File(outputDir + OracleCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                String strippedFile;
                String filename = Paths.get(oracleFile).getFileName().toString();
                if (filename.contains(".")) strippedFile = filename.substring(0, filename.lastIndexOf("."));
                else strippedFile = filename;
                File modelCheckedFile = new File(outputDir + strippedFile + "_modelchecked.csv");
                setOracleColl(fromcoll);
                Map<TemporalFormalism, List<TemporalOracle>> oracleTypedMap =fromcoll.stream().collect(Collectors.groupingBy(TemporalOracle::getPatternTemporalType));

                if (verbose && sourceIsDb & exportGraphMLOnVerbose) {
                saveToGraphMLFile("GraphML.XML", false,zip);
                saveToGraphMLFile("GraphML_NoWidgets.XML", true,zip);
                }
                //List<TemporalOracle> initialoraclelist = new ArrayList<>();
                List<TemporalOracle> finaloraclelist = new ArrayList<>();
                    FormulaVerifier.INSTANCE.setPathToExecutable(ltlSpotMCCommand);
                    FormulaVerifier.INSTANCE.setToWslPath(ltlSpotWSLPath);
                for (Map.Entry<TemporalFormalism, List<TemporalOracle>> oracleentry : oracleTypedMap.entrySet()
                ) {
                    List<TemporalOracle> modelCheckedOracles = null;
                   TemporalFormalism oracleType = oracleentry.getKey();
                    List<TemporalOracle> oracleList = oracleentry.getValue();
                    List<TemporalOracle> acceptedOracleList = oracleList.stream().
                                    filter(o -> (o.getOracle_validationstatus() ==ValStatus.ACCEPTED ||
                                    o.getOracle_validationstatus() ==ValStatus.CANDIDATE)).
                                    collect(Collectors.toList());
                    List<TemporalOracle> rejectedOracleList = oracleList.stream().
                            filter(o -> !(o.getOracle_validationstatus() ==ValStatus.ACCEPTED ||
                                    o.getOracle_validationstatus() ==ValStatus.CANDIDATE)).
                            collect(Collectors.toList());
                    //initialoraclelist.addAll(acceptedOracleList);
                    //initialoraclelist.addAll(rejectedOracleList);

                    simpleLog.append(prettyCurrentTime() + " | " + oracleType + " invoking the " + "backend model-checker");

                    ModelChecker checker = CheckerFactory.getModelChecker(oracleType);
                    checker.setupModel( verbose,counterExamples,outputDir,tModel,acceptedOracleList);

                    if (ltlSpotEnabled && ( oracleType == TemporalFormalism.LTL_SPOT)) {
                        checker.setExecutable(ltlSpotMCCommand, ltlSpotWSLPath);
                        modelCheckedOracles = checker.modelcheck();

                    }
                    if (ltlItsEnabled && (oracleType == TemporalFormalism.LTL_ITS)){
                         checker.setExecutable(ltlItsMCCommand, ltlItsWSLPath);
                         modelCheckedOracles = checker.modelcheck();
                        }

                    if(ltlltsminEnabled && (oracleType == TemporalFormalism.LTL_LTSMIN)){
                         checker.setExecutable(ltlLtsminMCCommand, ltlLtsminWSLPath);
                         modelCheckedOracles = checker.modelcheck();
                     }
                    if(ctlltsminEnabled && (oracleType == TemporalFormalism.CTL_LTSMIN)){
                        int maxap=450;
                        int maxstate=25000;
                        if (tModel.getAtomicPropositions().size()>maxap ||tModel.getStateList().size()>maxstate){
                            simpleLog.append(prettyCurrentTime() + " | " + oracleType +
                                    " Warning:  model check is not executed: explicit model too complex (propositions>"+maxap+" or states>"+maxstate);
                        }
                        else{
                            checker.setExecutable(ctlLtsminMCCommand, ctlLtsminWSLPath);
                            modelCheckedOracles = checker.modelcheck();
                            simpleLog.append(prettyCurrentTime() + " | " + oracleType + " verifying results for this Model checker is not possible yet");
                        }

                    }

                    if (ctlItsEnabled &&  ( oracleType == TemporalFormalism.CTL_ITS)) {
                        checker.setExecutable(ctlItsMCCommand, ctlItsWSLPath);
                        modelCheckedOracles = checker.modelcheck();
                    }
                    if (ctlGalEnabled &&  (oracleType == TemporalFormalism.CTL_GAL )) {
                        int maxap=200;
                        int maxstate=25000;
                        if (tModel.getAtomicPropositions().size()>maxap ||tModel.getStateList().size()>maxstate){
                            simpleLog.append(prettyCurrentTime() + " | " + oracleType +
                                    " Warning:  model check is not executed: explicit model too complex (propositions>"+maxap+" or states>"+maxstate);
                        }
                        else{
                            checker.setExecutable(ctlGalMCCommand, ctlGalWSLPath);
                            modelCheckedOracles = checker.modelcheck();
                            simpleLog.append(prettyCurrentTime() + " | " + oracleType + " verifying results for this Model checker is not possible yet");
                        }
                    }

                    if(!(
                            (ltlSpotEnabled && (oracleType == TemporalFormalism.LTL_SPOT))||
                            (ltlltsminEnabled && (oracleType == TemporalFormalism.LTL_LTSMIN))||
                            (ctlltsminEnabled && (oracleType == TemporalFormalism.CTL_LTSMIN))||
                            (ltlItsEnabled && (oracleType == TemporalFormalism.LTL_ITS))||
                            (ctlItsEnabled &&  (oracleType == TemporalFormalism.CTL_ITS))||
                            (ctlGalEnabled &&  (oracleType == TemporalFormalism.CTL_GAL))
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
                    finaloraclelist.addAll(rejectedOracleList);
                   // if (!verbose && inputvalidatedFile.exists())    Files.delete(inputvalidatedFile.toPath());
                    simpleLog.append(prettyCurrentTime() + " | " + oracleType + " model-checking completed");
                }
                //CSVHandler.save(initialoraclelist, inputvalidatedFile.getAbsolutePath());
                if (finaloraclelist.size() != fromcoll.size()) {
                    simpleLog.append(prettyCurrentTime() + " | " + "** Warning: less oracle verdicts " +
                            "received than requested in: "+ Paths.get(oracleFile).getFileName());
                }
                CSVHandler.save(finaloraclelist, modelCheckedFile.getAbsolutePath());
            }
            }
            simpleLog.append(prettyCurrentTime() + " | " + "Temporal model-checking completed");
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    public void setTemporalModel(String propositionManagerFile, boolean verbose, boolean instrumentTerminalState, boolean zip, boolean sourceIsDB, String modelFile) {
        try {
            simpleLog.append(prettyCurrentTime() + " | " + "compute temporal model started");
            if (verbose) {
                String APCopy = "copy_of_applied_" + Paths.get(propositionManagerFile).getFileName().toString();
                Files.copy((new File(propositionManagerFile).toPath()),
                        new File(outputDir + APCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            loadPropositionManager(propositionManagerFile);
            if (!sourceIsDB) {
                simpleLog.append(prettyCurrentTime() + " | " + "temporal model loading from file");
                tModel = (TemporalModel) JSONHandler.load(modelFile, TemporalModel.class);
                simpleLog.append(prettyCurrentTime() + " | " + "temporal model loaded from file");
                String modelCopy = "copy_of_applied_" + Paths.get(modelFile).getFileName().toString();
                if (verbose) {
                    Files.copy((new File(modelFile).toPath()),
                            new File(outputDir + modelCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

            } else {
                tModel = new TemporalModel();
                AbstractStateModel abstractStateModel = getAbstractStateModel();
                if (abstractStateModel == null) {
                    simpleLog.append("Error: StateModel not available");
                } else {
                    setTemporalModelMetaData(abstractStateModel);
                    simpleLog.append(prettyCurrentTime() + " | " + "Model= Name: "+tModel.getApplicationName()+" ; Ver: "+tModel.getApplicationVersion()+" ; ID: "+tModel.getApplication_ModelIdentifier());
                    tModel.setAtomicPropositionKeying(propositionManager.getPropositionKeying());
                    tDBManager.computeTemporalModel(abstractStateModel, tModel, instrumentTerminalState, logDetailsInModelFile);
                    simpleLog.append(prettyCurrentTime() + " | " + "compute temporal model completed");
                    if (verbose) {
                        saveModelAsJSON("PropositionEncodedModel.json",zip);
                    }
                }

            }
        } catch (Exception f) {
            f.printStackTrace();
        }

    }

    public void generateOraclesFromPatterns(String propositionManagerfile, String patternFile, String patternConstraintFile, int tactic_oraclesPerPattern,boolean sourceIsDB,String modelFile) {
        try {
            simpleLog.append(" potential Oracle generator started \n");
            setTemporalModel(propositionManagerfile, false, true,false,sourceIsDB,modelFile);
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
        // there is no check on duplicate assignments:  a pattern can emerge more than once with exactly the same assignments.
        // the likelyhood is few percent due to the randomness on AP selection and e=randomness on constraint-set selection.
        // the impact is low as a duplicate oracle will be executed , only twice!
        // refactor to Set? nr of oracles will then be less than the 'tactic'
        List<TemporalOracle> potentialOracleColl = new ArrayList<>();
        List<String> modelAPSet = new ArrayList<>(tModel.getAtomicPropositions());
        int trylimitConstraint = Math.min(250, 2 * modelAPSet.size());
        Random APRnd = new Random(5000000);
        Set<String> allParams=new HashSet<>();
        //get collection of all params, otherwise CSV file is crippled
        for (TemporalPattern pat : patterns
        ) {
            allParams.addAll(pat.getPattern_Parameters());
        }
        Map<String, String> defaultpattern_Substitutions = new HashMap<>();
        for (String param :allParams
        ) {
            defaultpattern_Substitutions.put("PATTERN_SUBSTITUTION_" + param, "");// no value
        }


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
                    pattern_Substitutions.putAll(defaultpattern_Substitutions);
                    for (Map.Entry<String, String> paramsubst : ParamSubstitutions.entrySet()
                    ) {
                        pattern_Substitutions.put("PATTERN_SUBSTITUTION_" + paramsubst.getKey(), paramsubst.getValue());// improve?
                        pattern_Substitutions.removeMapping("PATTERN_SUBSTITUTION_" + paramsubst.getKey(),"");
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

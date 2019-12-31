package  nl.ou.testar.temporal.behavior;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.StateManagementTags;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.temporal.structure.*;
import nl.ou.testar.temporal.util.*;
import org.fruit.alayer.Tag;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.fruit.monkey.ConfigTags.AbstractStateAttributes;

//import org.apache.tinkerpop.gremlin.structure.Graph;
//import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;

public class TemporalController {

    // orient db instance that will create database sessions
    private OrientDB orientDB;
    private Config dbConfig;
    private String ApplicationName;
    private String ApplicationVersion;
    private String Modelidentifier;
    private String outputDir;
    private ODatabaseSession db;
    private APSelectorManager apSelectorManager;
    private TemporalModel tModel;
    private TemporalDBHelper tDBHelper;
    private List<TemporalOracle> oracleColl;

    public TemporalController(final Settings settings) {
        this.ApplicationName= settings.get(ConfigTags.ApplicationName);
        this.ApplicationVersion= settings.get(ConfigTags.ApplicationVersion);
        setModelidentifier(settings);
        dbConfig = makeConfig(settings);
        String connectionString = dbConfig.getConnectionType() + ":/" + (dbConfig.getConnectionType().equals("remote") ?
                dbConfig.getServer() : dbConfig.getDatabaseDirectory());// +"/";
        //orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
        // orientDB = new OrientDB("plocal:C:\\orientdb-tp3-3.0.18\\databases", OrientDBConfig.defaultConfig());
        this.outputDir = makeOutputDir(settings);
        tDBHelper = new TemporalDBHelper(settings);
        tModel = new TemporalModel();
        setDefaultAPSelectormanager();

    }
    public void setTemporalModelMetaData(AbstractStateModel abstractStateModel){
        if (abstractStateModel != null) {
            tModel.setApplicationName(abstractStateModel.getApplicationName());
            tModel.setApplicationVersion(abstractStateModel.getApplicationVersion());
            tModel.setApplication_ModelIdentifier(abstractStateModel.getModelIdentifier());
            tModel.setApplication_AbstractionAttributes(abstractStateModel.getAbstractionAttributes());
        }


    }
    private void setModelidentifier(Settings settings){
        //copied from Main.initcodingmanager
        if (!settings.get(ConfigTags.AbstractStateAttributes).isEmpty()) {
            Tag<?>[] abstractTags = settings.get(AbstractStateAttributes).stream().map(StateManagementTags::getTagFromSettingsString).filter(tag -> tag != null).toArray(Tag<?>[]::new);
            CodingManager.setCustomTagsForAbstractId(abstractTags);
        }
        //copied from StateModelManagerFactory
        // get the abstraction level identifier that uniquely identifies the state model we are testing against.
        this.Modelidentifier = CodingManager.getAbstractStateModelHash(settings.get(ConfigTags.ApplicationName),
                settings.get(ConfigTags.ApplicationVersion));

    }
    private Config makeConfig(final Settings settings){
        // used here, but controlled on StateModelPanel

        String dataStoreText;
        String dataStoreServerDNS;
        String dataStoreDirectory;
        String dataStoreDBText;
        String dataStoreUser;
        String dataStorePassword;
        String dataStoreType;
        dataStoreText = settings.get(ConfigTags.DataStore); //assume orientdb
        dataStoreServerDNS = settings.get(ConfigTags.DataStoreServer);
        dataStoreDirectory = settings.get(ConfigTags.DataStoreDirectory);
        dataStoreDBText = settings.get(ConfigTags.DataStoreDB);
        dataStoreUser = settings.get(ConfigTags.DataStoreUser);
        dataStorePassword = settings.get(ConfigTags.DataStorePassword);
        dataStoreType = settings.get(ConfigTags.DataStoreType);
        Config dbconfig = new Config();
        dbconfig.setConnectionType(dataStoreType);
        dbconfig.setServer(dataStoreServerDNS);
        dbconfig.setDatabase(dataStoreDBText);
        dbconfig.setUser(dataStoreUser);
        dbconfig.setPassword(dataStorePassword);
        dbconfig.setDatabaseDirectory(dataStoreDirectory);
        return dbconfig;
    }
    private String makeOutputDir(final Settings settings){
        String outputDir = settings.get(ConfigTags.OutputDir);
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + settings.get(ConfigTags.TemporalDirectory);

        if(settings.get(ConfigTags.TemporalSubDirectories)) {
            String runFolder = Helper.CurrentDateToFolder();
            outputDir = outputDir + File.separator + runFolder;
        }
        new File(outputDir).mkdirs();
        outputDir = outputDir + File.separator;
        return outputDir;
    }


    public TemporalModel gettModel() {
        return tModel;
    }


    public void saveAPSelectorManager(String filename) {
        JSONHandler.save(apSelectorManager, outputDir + filename, true);
    }

    public void loadApSelectorManager(String filename) {
        this.apSelectorManager = (APSelectorManager) JSONHandler.load( filename, apSelectorManager.getClass());
        apSelectorManager.updateAPKey(tModel.getApplication_BackendAbstractionAttributes());
        tDBHelper.setApSelectorManager(apSelectorManager);
    }

    public List<TemporalOracle> getOracleColl() {
        return oracleColl;
    }

    public void setOracleColl(List<TemporalOracle> oracleColl) {
        this.oracleColl = oracleColl;
        this.oracleColl.sort(Comparator.comparing(TemporalOracle::getPattern_TemporalFormalism)); //sort by type
    }

    public void setDefaultAPSelectormanager() {
        List<Tag<?>> taglist = new ArrayList<>();
        List<String> APKey= new ArrayList<>();
        if(tModel!=null){
            APKey =tModel.getApplication_BackendAbstractionAttributes() ;}
        if (APKey!=null && !APKey.isEmpty()){
            this.apSelectorManager = new APSelectorManager(true, APKey);
        }
        else{
            this.apSelectorManager = new APSelectorManager(true);}
        tDBHelper.setApSelectorManager(apSelectorManager);
    }




    // @TODO: 2019-12-29 refactor db operations to dbhelper
    public void dbClose() {
       tDBHelper.dbClose();
    }
    public void dbReopen() {
      tDBHelper.dbReopen();
    }


    public String pingDB() {
        tDBHelper.dbReopen();
        StringBuilder sb = new StringBuilder();
        List<AbstractStateModel> models = tDBHelper.fetchAbstractModels();
        if ( models.isEmpty()){
             sb.append("model count: 0\n");
        }
        else {
            sb.append("model count: " + models.size() + "\n");
            sb.append("Model info:\n");
            for (AbstractStateModel abs : models
            ) {
                sb.append("APP: " + abs.getApplicationName() + ", VERSION: " + abs.getApplicationVersion() + ", ID: " + abs.getModelIdentifier() + ", ABSTRACTION: " + abs.getAbstractionAttributes() + "\n");
            }
        }
        tDBHelper.dbClose();
        return sb.toString();
    }



    //*********************************
    public void computeTemporalModel(AbstractStateModel abstractStateModel) {

        OResultSet resultSet = tDBHelper.getConcreteStatesFromOrientDb(abstractStateModel);

        if (abstractStateModel != null) {

            //Set selectedAttibutes = apSelectorManager.getSelectedSanitizedAttributeNames();
            boolean firstDeadState = true;
            StateEncoding deadStateEnc;
            while (resultSet.hasNext()) {
                OResult result = resultSet.next();
                // we're expecting a vertex
                if (result.isVertex()) {

                    Optional<OVertex> op = result.getVertex();
                    if (!op.isPresent()) continue;

                    OVertex stateVertex = op.get();
                    StateEncoding senc = new StateEncoding(stateVertex.getIdentity().toString());
                    Set<String> propositions = new LinkedHashSet<>();


                    boolean deadstate = false;

                    Iterable<OEdge> outedges = stateVertex.getEdges(ODirection.OUT, "ConcreteAction"); //could be a SQL- like query as well
                    Iterator<OEdge> edgeiter = outedges.iterator();
                    deadstate = !edgeiter.hasNext();

                    if (deadstate) {
                        if (firstDeadState) {
                            //add stateenc for 'Dead', inclusive dead transition selfloop;
                            deadStateEnc = new StateEncoding("#dead");
                            Set<String> deadStatePropositions = new LinkedHashSet<>();
                            //deadStatePropositions.add("dead");   //redundant on transitionbased automatons
                            deadStateEnc.setStateAPs(deadStatePropositions);

                            TransitionEncoding deadTrenc = new TransitionEncoding();
                            deadTrenc.setTransition("dead_selfloop");
                            deadTrenc.setTargetState("#dead");
                            Set<String> deadTransitionPropositions = new LinkedHashSet<>();
                            deadTransitionPropositions.add("dead");
                            deadTrenc.setTransitionAPs(deadTransitionPropositions);
                            List<TransitionEncoding> deadTrencList = new ArrayList<>();
                            deadTrencList.add(deadTrenc);
                            deadStateEnc.setTransitionColl(deadTrencList);
                            tModel.addStateEncoding(deadStateEnc, false);
                            firstDeadState = false;
                        }
                        stateVertex.setProperty(TagBean.IsDeadState.name(), true);  //candidate for refactoring

                        tModel.addLog("State: " + stateVertex.getIdentity().toString() + " has no outgoing transition. \n");
                    }
                    for (String propertyName : stateVertex.getPropertyNames()) {
                        tDBHelper.computeProps(propertyName, stateVertex, propositions, false, false);
                    }
                    propositions.addAll(tDBHelper.getWidgetPropositions(senc.getState()));// concrete widgets
                    senc.setStateAPs(propositions); // to be decided:  whether to include current AP's on a deadstate
                    if (deadstate) {
                        TransitionEncoding deadTrenc = new TransitionEncoding();
                        deadTrenc.setTransition("#dead_" + stateVertex.getIdentity().toString());
                        deadTrenc.setTargetState("#dead");
                        Set<String> deadTransitionPropositions = new LinkedHashSet<>();
                        deadTransitionPropositions.add("dead");
                        deadTrenc.setTransitionAPs(deadTransitionPropositions);
                        List<TransitionEncoding> deadTrencList = new ArrayList<>();
                        deadTrencList.add(deadTrenc);
                        senc.setTransitionColl(deadTrencList);
                    } else senc.setTransitionColl(tDBHelper.getTransitions(senc.getState()));

                    tModel.addStateEncoding(senc, false);
                }
            }
            tModel.updateTransitions(); //update once. this is a costly operation
            for (StateEncoding stenc : tModel.getStateEncodings()
            ) {
                List<String> encodedConjuncts = new ArrayList<>();
                for (TransitionEncoding tren : stenc.getTransitionColl()
                ) {
                    String enc = tren.getEncodedAPConjunct();
                    if (encodedConjuncts.contains(enc)) {
                        tModel.addLog("State: " + stenc.getState() + " has  non-deterministic transition: " + tren.getTransition());
                    } else encodedConjuncts.add(enc);
                }
            }
            tModel.setTraces(tDBHelper.fetchTraces(tModel.getApplication_ModelIdentifier()));
            List<String> initStates = new ArrayList<>();
            for (TemporalTrace trace : tModel.getTraces()
            ) {
                TemporalTraceEvent traceevent = trace.getTraceEvents().get(0);
                initStates.add(traceevent.getState());
            }
            tModel.setInitialStates(initStates);

            for (String ap : tModel.getModelAPs()    // check the resulting model for DeadStates
            ) {
                if (ap.contains(apSelectorManager.getApEncodingSeparator() + TagBean.IsDeadState.name())) {
                    tModel.addLog("WARNING: Model contains dead states (there are states without outgoing edges)");
                    break;
                }
            }

        }
    }



    private AbstractStateModel getAbstractStateModel() {
        AbstractStateModel abstractStateModel;

        //abstractStateModel = tDBHelper.selectAbstractStateModel(ApplicationName, ApplicationVersion);
        abstractStateModel = tDBHelper.selectAbstractStateModelByModelId(Modelidentifier);

        if (abstractStateModel == null) {
            tModel.addLog("ERROR: Model with identifier : "+Modelidentifier+" was not found in the graph database " + dbConfig.getDatabase());

        }
        return abstractStateModel;
    }




    public boolean saveToGraphMLFile(String file,boolean excludeWidget) {
        AbstractStateModel abstractStateModel = tDBHelper.selectAbstractStateModelByModelId(Modelidentifier);
        if (abstractStateModel != null) {
            return tDBHelper.saveToGraphMLFile(abstractStateModel,  outputDir +file, excludeWidget);
        }
        else return false;
    }

    private void saveModelAsJSON(String toFile) {
        JSONHandler.save(tModel, outputDir +toFile);
    }

    public boolean saveModelForChecker(TemporalType tmptype, String file) {
        boolean b = false;
        if (tmptype.equals(TemporalType.LTL)){
            saveModelAsHOA(file);
            b = true;
        }
        return b;
}

    private void saveModelAsHOA(String file){

        String contents = tModel.makeHOAOutput();
        try {
            File output = new File( file);
            if (output.exists() || output.createNewFile()) {
                BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output.getAbsolutePath()), StandardCharsets.UTF_8));
                writer.append(contents);
                writer.close();
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    };

    public void saveFormulaFiles(List<TemporalOracle> oracleColl, String file){
            File output = new File( file);
            saveFormulaFiles(oracleColl,output);
    }
    public void saveFormulaFiles(List<TemporalOracle> oracleColl, File output){

        String contents = tModel.makeFormulaOutput(oracleColl);
        try {

            if (output.exists() || output.createNewFile()) {
                BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output.getAbsolutePath()), StandardCharsets.UTF_8));
                writer.append(contents);
                writer.close();
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    public void ModelCheck(TemporalType tType,String pathToExecutable, String APSelectorFile,String oracleFile,boolean verbose) {
        try {
            System.out.println(tType+" model-checking started \n");
            tDBHelper.dbReopen();
            AbstractStateModel abstractStateModel = getAbstractStateModel();
            setTemporalModelMetaData(abstractStateModel);
            loadApSelectorManager(APSelectorFile);
            String strippedFile;
            String APCopy =  "copy_"+Paths.get(APSelectorFile).getFileName().toString();
            String OracleCopy = "copy_"+ Paths.get(oracleFile).getFileName().toString();
            if (verbose) {
                Files.copy((new File(APSelectorFile).toPath()),
                        new File(outputDir + APCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.copy((new File(oracleFile).toPath()),
                        new File(outputDir + OracleCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            String filename = Paths.get(oracleFile).getFileName().toString();
            if (filename.contains(".")) strippedFile = filename.substring(0, filename.lastIndexOf("."));
            else strippedFile = filename;

            File automatonFile = new File(outputDir + "LTL_model.hoa");
            File formulaFile = new File(outputDir + "LTL_formulas.txt");
            File resultsFile = new File(outputDir + "LTL_results.txt");
            File inputvalidatedFile = new File(outputDir + strippedFile + "_inputvalidation.csv");
            File modelCheckedFile = new File(outputDir + strippedFile + "_modelchecked.csv");

            computeTemporalModel(abstractStateModel);

            List<TemporalOracle> fromcoll;
            fromcoll = CSVHandler.load(oracleFile, TemporalOracle.class);
            if (fromcoll == null) {
                System.err.println("Error: verify the file at location '" + oracleFile + "'");
            } else {
               setOracleColl(fromcoll);
               saveFormulaFiles(fromcoll, formulaFile);
                CSVHandler.save(fromcoll, inputvalidatedFile.getAbsolutePath());
            }

            //from here on LTL specific logic
            //if (tType==TemporalType.LTL){}
            saveModelForChecker(TemporalType.LTL, automatonFile.getAbsolutePath());
            String aliveprop = gettModel().getAliveProposition("!dead");
            Helper.LTLModelCheck(pathToExecutable, automatonFile.getAbsolutePath(), formulaFile.getAbsolutePath(), aliveprop, resultsFile.getAbsolutePath());
            Spot_CheckerResultsParser sParse = new Spot_CheckerResultsParser(gettModel(), fromcoll);//decode results
            List<TemporalOracle> modelCheckedOracles = sParse.parse(resultsFile);
            if (modelCheckedOracles == null) {
                System.err.println("Error detected in obtained results from the model-checker");
            } else {
                CSVHandler.save(modelCheckedOracles, modelCheckedFile.getAbsolutePath());
                System.out.println(tType+" model-checking completed \n");
            }
            //above is LTL specific logic


            if (verbose) {
               saveToGraphMLFile( "GraphML.XML",false);
               saveToGraphMLFile( "GraphML_NoWidgets.XML",true);
               saveModelAsJSON( "APEncodedModel.json");
            } else {
                Files.delete(automatonFile.toPath());
                Files.delete(resultsFile.toPath());
                Files.delete(formulaFile.toPath());
                Files.delete(inputvalidatedFile.toPath());
            }
            tDBHelper.dbClose();
        } catch (Exception f) {
            f.printStackTrace();
        }

    }
    public void dumpTemporalModel(String APSelectorFile) {
        try {
            System.out.println(" dumping temporal model started \n");
            tDBHelper.dbReopen();
            AbstractStateModel abstractStateModel = getAbstractStateModel();
            setTemporalModelMetaData(abstractStateModel);
            if (APSelectorFile.equals("")) {
                setDefaultAPSelectormanager();
                saveAPSelectorManager("default_APSelectorManager.json");
            } else {
                loadApSelectorManager(APSelectorFile);
                String APCopy =  "copy_"+Paths.get(APSelectorFile).getFileName().toString();
                Files.copy((new File(APSelectorFile).toPath()),
                        new File(outputDir + APCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }


            computeTemporalModel(abstractStateModel);
            saveModelAsJSON( "APEncodedModel.json");
            tDBHelper.dbClose();
            System.out.println(" dumping temporal model completed \n");
        } catch (Exception f) {
            f.printStackTrace();
        }

    }
}

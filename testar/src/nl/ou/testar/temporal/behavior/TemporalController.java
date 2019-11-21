package  nl.ou.testar.temporal.behavior;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.temporal.structure.*;
import nl.ou.testar.temporal.util.*;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

//import org.apache.tinkerpop.gremlin.structure.Graph;
//import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;

public class TemporalController {

    // orient db instance that will create database sessions
    private OrientDB orientDB;
    private Config dbConfig;
    private String outputDir;
    private ODatabaseSession db;
    private APSelectorManager apSelectorManager;
    private TemporalModel tModel;
    private TemporalDBHelper tDBHelper;
    private List<TemporalOracle> oracleColl;


    public TemporalController(final Config config, String outputDir) {
        String connectionString = config.getConnectionType() + ":/" + (config.getConnectionType().equals("remote") ?
                config.getServer() : config.getDatabaseDirectory());// +"/";
        orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
        // orientDB = new OrientDB("plocal:C:\\orientdb-tp3-3.0.18\\databases", OrientDBConfig.defaultConfig());
        dbConfig = config;
        this.outputDir = outputDir;
        tDBHelper = new TemporalDBHelper();
        setDefaultAPSelectormanager();
        tModel = new TemporalModel();


        //dbReopen();// check if the credentials are valid?
    }

    public TemporalModel gettModel() {
        return tModel;
    }

    private void settModel(TemporalModel tModel) {
        this.tModel = tModel;
    }

    private void saveAPSelectorManager(String filename) {
        JSONHandler.save(apSelectorManager, outputDir + filename, true);
    }

    public void loadApSelectorManager(String filename) {
        this.apSelectorManager = (APSelectorManager) JSONHandler.load( filename, apSelectorManager.getClass());
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
        this.apSelectorManager = new APSelectorManager(true);
        tDBHelper.setApSelectorManager(apSelectorManager);
    }

    public void dbClose() {
        if (!db.isClosed())  {db.close();tDBHelper.setDb(db);orientDB.close();}
    }
    public void dbReopen() {
        if (db==null ||db.isClosed()) {
            db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword());
        }
        tDBHelper.setDb(db);
        db.activateOnCurrentThread();


    }
    public void ConnectionClose() {
        orientDB.close();
    }

    public String pingDB() {
        StringBuilder sb = new StringBuilder();
        List<AbstractStateModel> models = tDBHelper.fetchAbstractModels();
        sb.append("model count: " + models.size() + "\n");
        AbstractStateModel model = models.get(0);
        sb.append("Model0 info:" + model.getApplicationName() + ", " + model.getModelIdentifier() + "\n");
        return sb.toString();
    }


    //*********************************
    public void computeTemporalModel() {
        AbstractStateModel abstractStateModel = getFirstAbstractStateModel();
        String stmt;
        Map<String, Object> params = new HashMap<>();

        params.put("identifier", abstractStateModel.getModelIdentifier());
        // navigate from abstractstate to apply the filter.
        // stmt =  "SELECT FROM (TRAVERSE in() FROM (SELECT FROM AbstractState WHERE abstractionLevelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";
        stmt = "SELECT FROM (TRAVERSE in() FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";

        OResultSet resultSet = db.query(stmt, params);  //OResultSet resultSet = db.query(stmt);

        if (abstractStateModel != null) {
            tModel.setApplicationName(abstractStateModel.getApplicationName());
            tModel.setApplicationVersion(abstractStateModel.getApplicationVersion());
            tModel.setApplication_ModelIdentifier(abstractStateModel.getModelIdentifier());
            tModel.setApplication_AbstractionAttributes(abstractStateModel.getAbstractionAttributes());

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

    private AbstractStateModel getFirstAbstractStateModel() {
        List<AbstractStateModel> abstractStateModels = tDBHelper.fetchAbstractModels();
        AbstractStateModel abstractStateModel;
        if (abstractStateModels.size() == 0) {
            System.out.println("ERROR: Number of Models in the graph database " + db.toString() + " is ZERO");
            tModel.addLog("ERROR: Number of Models in the graph database " + db.toString() + " is ZERO");
            abstractStateModel = null;
        } else {
            abstractStateModel = abstractStateModels.get(0);
        }

        if (abstractStateModels.size() > 1) {
            System.out.println("WARNING: Number of Models in the graph database " + db.toString() + " is more than ONE. We try with the first model");
            tModel.addLog("WARNING: Number of Models in the graph database " + db.toString() + " is more than ONE. We try with the first model");
        }
        return abstractStateModel;
    }

    @Deprecated
    private void testgraphmlexport(String file) {  // inferior css 20190713
        String connectionString = dbConfig.getConnectionType() + ":/" + (dbConfig.getConnectionType().equals("remote") ?
                dbConfig.getServer() : dbConfig.getDatabaseDirectory());// +"/";
        String dbconnectstring = connectionString + "\\" + dbConfig.getDatabase();
        OrientGraph grap = new OrientGraph(dbconnectstring, dbConfig.getUser(), dbConfig.getPassword());
        System.out.println("debug connectionstring: " + dbconnectstring + " \n");

        //Graph graph = new OrientGraph(connectionString+"/"+dbConfig.getDatabase(),dbConfig.getUser(),dbConfig.getPassword());
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("blueprints.graph", "com.tinkerpop.blueprints.impls.orient.OrientGraph");
        conf.put("blueprints.orientdb.url", dbconnectstring);
        conf.put("blueprints.orientdb.username", dbConfig.getUser());
        conf.put("blueprints.orientdb.password", dbConfig.getPassword());

        //Graph graph = GraphFactory.open(conf);
        Graph graph = GraphFactory.open(conf);
        //GraphTraversalSource gts = graph.traversal();
        //final GraphWriter writer = graph.io(IoCore.graphson()).writer();
        // final OutputStream os = new FileOutputStream("tinkerpop-modern.json");
        // writer.writeObject(os, graph);


        System.out.println("debug writing graphml file \n");
        try {
            try {
                File output = new File(file);
                FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
                //GraphMLWriter writer = new GraphMLWriter(graph); //GraphMLWriter.outputGraph(grap,fos);
                GraphMLWriter writer = GraphMLWriter.build().create();
                writer.writeGraph(fos, graph);

                //writer.outputGraph(fos);

            } catch (IOException e) {
                e.printStackTrace();
            }


        } finally {
            try {
                grap.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean saveToGraphMLFile(String file,boolean excludeWidget) {

    return tDBHelper.saveToGraphMLFile(dbConfig.getDatabase(),file,excludeWidget);
    }

    public void saveModelAsJSON(String toFile) {
        JSONHandler.save(tModel, toFile);
    }

    public boolean exportModel(TemporalType tmptype, String file) {
        boolean b = true;
        if (tmptype.equals(TemporalType.LTL)) saveModelAsHOA(file);
        else b = false;
        return b;
}

    public void saveModelAsHOA(String file){

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

    public void ModelCheck(TemporalType tType,String command, String ApFile,String oracleFile,boolean verbose) {
        try {

            loadApSelectorManager(ApFile);
            String APCopy = "Copy_of_" + Paths.get(ApFile).getFileName().toString();
            String OracleCopy = "Copy_of_" + Paths.get(oracleFile).getFileName().toString();
            if (verbose) {
                Files.copy((new File(ApFile).toPath()),
                        new File(outputDir + APCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.copy((new File(oracleFile).toPath()),
                        new File(outputDir + OracleCopy).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            String strippedFile;
            String filename = Paths.get(oracleFile).getFileName().toString();
            if (filename.contains(".")) strippedFile = filename.substring(0, filename.lastIndexOf("."));
            else strippedFile = filename;

            File automatonFile = new File(outputDir + "LTL_model.hoa");
            File formulaFile = new File(outputDir + "LTL_formulas.txt");
            File resultsFile = new File(outputDir + "LTL_results.txt");
            File inputvalidatedFile = new File(outputDir + strippedFile + "_inputvalidation.csv");
            File modelCheckedFile = new File(outputDir + strippedFile + "_modelchecked.csv");
            dbReopen();
            computeTemporalModel();

            List<TemporalOracle> fromcoll;
            fromcoll = CSVHandler.load(oracleFile, TemporalOracle.class);
            if (fromcoll == null) {
                System.err.println("verify the file at location '" + oracleFile + "'");
            } else {
               setOracleColl(fromcoll);
               saveFormulaFiles(fromcoll, formulaFile);
                CSVHandler.save(fromcoll, inputvalidatedFile.getAbsolutePath());
            }

            //from here on LTL specific logic
            //if (tType==TemporalType.LTL){}
            exportModel(TemporalType.LTL, automatonFile.getAbsolutePath());
            String aliveprop = gettModel().getAliveProposition("!dead");
            Helper.LTLModelCheck(command, automatonFile.getAbsolutePath(), formulaFile.getAbsolutePath(), aliveprop, resultsFile.getAbsolutePath());
            Spot_CheckerResultsParser sParse = new Spot_CheckerResultsParser(gettModel(), fromcoll);//decode results
            List<TemporalOracle> modelCheckedOracles = sParse.parse(resultsFile);
            if (modelCheckedOracles == null) {
                System.err.println("Error detected in modelcheck results");
            } else {
                CSVHandler.save(modelCheckedOracles, modelCheckedFile.getAbsolutePath());
                System.out.println("Temporal model-checked results csv are saved: \n");
            }
            //above is LTL specific logic


            if (verbose) {
               saveToGraphMLFile(outputDir + "GraphML.XML",false);
               saveToGraphMLFile(outputDir + "GraphML_NoWidgets.XML",true);
               saveModelAsJSON(outputDir + "APEncodedModel.json");
            } else {
                Files.delete(automatonFile.toPath());
                Files.delete(resultsFile.toPath());
                Files.delete(formulaFile.toPath());
                Files.delete(inputvalidatedFile.toPath());
            }
            dbClose();
        } catch (Exception f) {
            f.printStackTrace();
        }

    }
}

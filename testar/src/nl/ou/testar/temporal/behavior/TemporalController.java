package nl.ou.testar.temporal.behavior;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.record.impl.OVertexDocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import nl.ou.testar.StateModel.Analysis.Json.Edge;
import nl.ou.testar.StateModel.Analysis.Json.Element;
import nl.ou.testar.StateModel.Analysis.Json.Vertex;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;
import nl.ou.testar.StateModel.Analysis.Representation.TestSequence;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.temporal.structure.*;
import nl.ou.testar.temporal.util.*;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.fruit.alayer.Tags;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
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


    /**
     * Constructor
     *
     * @param config
     * @param outputDir
     */
    public TemporalController(final Config config, String outputDir) {
        String connectionString = config.getConnectionType() + ":/" + (config.getConnectionType().equals("remote") ?
                config.getServer() : config.getDatabaseDirectory());// +"/";
        orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
        // orientDB = new OrientDB("plocal:C:\\orientdb-tp3-3.0.18\\databases", OrientDBConfig.defaultConfig());

        dbConfig = config;
        this.outputDir = outputDir;

        // check if the credentials are valid
        db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword());
        setDefaultAPSelectormanager();
    }
    public void saveAPSelectorManager (String filename) {
        JSONHandler.save(apSelectorManager, outputDir + filename,true);
    }

    public void loadApSelectorManager(String filename) {
        this.apSelectorManager =  (APSelectorManager) JSONHandler.load(outputDir + filename, apSelectorManager.getClass());
    }
    public void setDefaultAPSelectormanager(){
        this.apSelectorManager =new APSelectorManager(true);
    }
    /**
     * Shuts down the orientDB connection.
     */
    public void shutdown() {
        db.close();
        orientDB.close();
    }
    public String pingDB(){
       StringBuilder sb = new StringBuilder();
        List<AbstractStateModel> models = fetchAbstractModels();

        sb.append("model count: " + models.size()+"\n");
        AbstractStateModel model = models.get(0);
        sb.append("Model0 info:" + model.getApplicationName() + ", " + model.getModelIdentifier()+"\n");
        return sb.toString();
    }

    /**
     * This method fetches a list of the abstract state models in the current OrientDB data store.
     *
     * @return
     */
    private List<AbstractStateModel> fetchAbstractModels() {
        ArrayList<AbstractStateModel> abstractStateModels = new ArrayList<>();
        //try (ODatabaseSession db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())) {
        OResultSet resultSet = db.query("SELECT FROM AbstractStateModel");
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()) {
                Optional<OVertex> op = result.getVertex();
                if (!op.isPresent()) continue;
                OVertex modelVertex = op.get();

                String applicationName = (String) getConvertedValue(OType.STRING, modelVertex.getProperty("applicationName"));
                String applicationVersion = (String) getConvertedValue(OType.STRING, modelVertex.getProperty("applicationVersion"));
                String modelIdentifier = (String) getConvertedValue(OType.STRING, modelVertex.getProperty("modelIdentifier"));
                Set abstractionAttributes = (Set) getConvertedValue(OType.EMBEDDEDSET, modelVertex.getProperty("abstractionAttributes"));
                // fetch the test sequences
                List<TestSequence> sequenceList = fetchTestSequences(modelIdentifier, db);

                AbstractStateModel abstractStateModel = new AbstractStateModel(
                        applicationName, applicationVersion, modelIdentifier, abstractionAttributes, sequenceList
                );
                abstractStateModels.add(abstractStateModel);
            }
        }

        return abstractStateModels;
    }


    //*********************************
    public TemporalModel getTemporalModel( APSelectorManager Apmgr) {
        List<AbstractStateModel> abstractStateModels= fetchAbstractModels();
        AbstractStateModel abstractStateModel;
        if (abstractStateModels.size()==0 ) {
            System.out.println("ERROR: Number of Models in the graph database is ZERO");
            return null;
        }
        abstractStateModel =abstractStateModels.get(0);
        String stmt;
        Map<String, Object> params = new HashMap<>();

        if (abstractStateModels.size()>1){
            System.out.println("WARNING: Number of Models in the graph database is more than ONE. We try with the first model");
            params.put("identifier", abstractStateModel.getModelIdentifier());
            // navigate from abstractstate to apply the filter.
            stmt =  "SELECT FROM (TRAVERSE in() FROM (SELECT FROM AbstractState WHERE abstractionLevelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";
        }else{
            stmt = "SELECT FROM V WHERE @class = 'ConcreteState'";
        }
        OResultSet resultSet = db.query(stmt, params);  //OResultSet resultSet = db.query(stmt);

        TemporalModel tmodel = new TemporalModel(
                abstractStateModel.getApplicationName(), abstractStateModel.getApplicationVersion(),
                abstractStateModel.getModelIdentifier(), abstractStateModel.getAbstractionAttributes());

        //Set selectedAttibutes = apSelectorManager.getSelectedSanitizedAttributeNames();
        this.apSelectorManager = Apmgr;
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

                Iterable<OEdge> outedges = stateVertex.getEdges(ODirection.OUT);
                Iterator<OEdge> edgeiter = outedges.iterator();
                deadstate = !edgeiter.hasNext();

                if (deadstate) {
                    stateVertex.setProperty(TagBean.IsDeadState.name(), true);  //candidate for refactoring
                    System.out.println("State: " + stateVertex.getIdentity().toString() + " has as no outgoing edge. \n");
                }
                for (String propertyName : stateVertex.getPropertyNames()) {
                    computeProps(propertyName, stateVertex, propositions, false,false);
                }
                propositions.addAll(getWidgetPropositions(senc.getState()));// concrete widgets
                senc.setStateAPs(propositions);
                senc.setTransitionColl(getTransitions(senc.getState()));
                tmodel.addStateEncoding(senc, false);
            }
        }
        tmodel.updateTransitions(); //update once. this is a costly operation


        for (String ap : tmodel.getModelAPs()    // check the resulting model for DeadStates
        ) {
            //if (ap.matches(apSelectorManager.getApEncodingSeparator()+TagBean.IsDeadState)) {
            if (ap.contains(Apmgr.getApEncodingSeparator() + TagBean.IsDeadState.name())) {
                tmodel.addLog("WARNING: Model contains dead states (there are states without outgoing edges)");
                break;
            }
        }

        return tmodel;
    }




    private Set<String> getWidgetPropositions(String state) {


        // concrete widgets

        //stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').outE('ConcreteAction') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";
        // String stmt = "SELECT * FROM (TRAVERSE in('isChildOf') FROM (SELECT * FROM :state)) WHERE @class = 'Widget'";
        String stmt = "SELECT FROM (TRAVERSE in('isChildOf') FROM (SELECT FROM ConcreteState WHERE @rid = :state)) WHERE @class = 'Widget'";
        Map<String, Object> params = new HashMap<>();
        params.put("state", state);
        OResultSet resultSet = db.query(stmt, params);
        //***
        Set<String> propositions = new LinkedHashSet<>();
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()) {
                Optional<OVertex> op = result.getVertex();
                if (!op.isPresent()) continue;
                OVertex stateVertex = op.get();
                for (String propertyName : stateVertex.getPropertyNames()) {
                   computeProps(propertyName,stateVertex,propositions,true,false);
                }
            }
        }
        return propositions;
    }

    private List<TransitionEncoding> getTransitions(String state) {
        List<TransitionEncoding> trenclist = new ArrayList<>();


        // concrete states
        String stmt = "SELECT * FROM (TRAVERSE outE('ConcreteAction') FROM (SELECT FROM ConcreteState WHERE @rid = :state)) where @class='ConcreteAction'";
        Map<String, Object> params = new HashMap<>();
        params.put("state", state);
        OResultSet resultSet = db.query(stmt, params);
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isEdge()) {
                Optional<OEdge> op = result.getEdge();
                if (!op.isPresent()) {
                   // System.out.println("debug state;" + state + " waiting on edgde");
                    continue;
                }
                OEdge actionEdge = op.get();
                OVertexDocument source = actionEdge.getProperty("out");
                OVertexDocument target = actionEdge.getProperty("in");

                TransitionEncoding trenc = new TransitionEncoding();
                trenc.setEdge(actionEdge.getIdentity().toString());
                trenc.setTargetState(target.getIdentity().toString());
                Set<String> propositions = new LinkedHashSet<>();
                for (String propertyName : actionEdge.getPropertyNames()) {
                        computeProps(propertyName,actionEdge,propositions,false,true);
                    }
                trenc.setEdgeAPs(propositions);

                trenclist.add(trenc);
            }


        }
        return trenclist;
    }
//*********************************


    /**
     * This method fetches the test sequences for a given abstract state model.
     *
     * @param modelIdentifier
     * @param db
     * @return
     */
    private List<TestSequence> fetchTestSequences(String modelIdentifier, ODatabaseSession db) {
        List<TestSequence> sequenceList = new ArrayList<>();
        String sequenceStmt = "SELECT FROM TestSequence WHERE modelIdentifier = :identifier ORDER BY startDateTime ASC";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(sequenceStmt, params);
        while (resultSet.hasNext()) {
            OResult sequenceResult = resultSet.next();
            // we're expecting a vertex
            if (sequenceResult.isVertex()) {
                Optional<OVertex> sequenceOp = sequenceResult.getVertex();
                if (!sequenceOp.isPresent()) continue;
                OVertex sequenceVertex = sequenceOp.get();

                // fetch the nr of nodes for the sequence
                String nodeStmt = "SELECT COUNT(*) as nr FROM SequenceNode WHERE sequenceId = :sequenceId";
                params = new HashMap<>();
                params.put("sequenceId", getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId")));
                OResultSet nodeResultSet = db.query(nodeStmt, params);
                int nrOfNodes = 0;
                if (nodeResultSet.hasNext()) {
                    OResult nodeResult = nodeResultSet.next();
                    nrOfNodes = (int) getConvertedValue(OType.INTEGER, nodeResult.getProperty("nr"));
                    if (nrOfNodes > 0) {
                        nrOfNodes--;
                    }
                }

                String sequenceId = (String) getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId"));
                Date startDateTime = (Date) getConvertedValue(OType.DATETIME, sequenceVertex.getProperty("startDateTime"));
                sequenceList.add(new TestSequence(sequenceId, DateFormat.getDateTimeInstance().format(startDateTime), String.valueOf(nrOfNodes)));
            }
        }
        return sequenceList;
    }


    /**
     * This method fetches the elements in the sequence layer for a given abstract state model.
     *
     * @param modelIdentifier
     * @param db
     * @return
     */

    private List<Element> fetchSequenceLayer(String modelIdentifier, ODatabaseSession db, boolean showCompoundGraph) {
        ArrayList<Element> elements = new ArrayList<>();

        // optionally add a parent node for the sequence layer
        if (showCompoundGraph) {
            Vertex sequenceParent = new Vertex("S1");
            elements.add(new Element(Element.GROUP_NODES, sequenceParent, "Parent"));
        }

        // test sequence
        String stmt = "SELECT FROM TestSequence WHERE modelIdentifier = :identifier";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "TestSequence", showCompoundGraph ? "S1" : null, modelIdentifier));

        // sequence nodes
        stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').in('Accessed') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'SequenceNode'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "SequenceNode", showCompoundGraph ? "S1" : null, modelIdentifier));

        // sequence steps
        stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').in('Accessed').outE('SequenceStep') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'SequenceStep'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "SequenceStep"));

        // first node
        stmt = "SELECT FROM (TRAVERSE outE('FirstNode') FROM (SELECT FROM TestSequence WHERE modelIdentifier = :identifier)) WHERE @class = 'FirstNode'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "FirstNode"));

        return elements;
    }


    /**
     * This method transforms a resultset of nodes into elements.
     *
     * @param resultSet
     * @param className
     * @return
     */
    private ArrayList<Element> fetchNodes(OResultSet resultSet, String className, String parent, String modelIdentifier) {
        ArrayList<Element> elements = new ArrayList<>();

        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()) {
                Optional<OVertex> op = result.getVertex();
                if (!op.isPresent()) continue;
                OVertex stateVertex = op.get();
                Vertex jsonVertex = new Vertex("n" + formatId(stateVertex.getIdentity().toString()));
                for (String propertyName : stateVertex.getPropertyNames()) {
                    if (propertyName.contains("in_") || propertyName.contains("out_")) {
                        // these are edge indicators. Ignore
                        continue;
                    }
                    if (propertyName.equals("screenshot")) {
                        // process the screenshot separately
                        processScreenShot(stateVertex.getProperty("screenshot"), "n" + formatId(stateVertex.getIdentity().toString()), modelIdentifier);
                        continue;
                    }
                    jsonVertex.addProperty(propertyName, stateVertex.getProperty(propertyName).toString());
                }
                // optionally add a parent
                if (parent != null) {
                    jsonVertex.addProperty("parent", parent);
                }
                Element element = new Element(Element.GROUP_NODES, jsonVertex, className);
                if (stateVertex.getPropertyNames().contains("isInitial")) {
                    if ((Boolean) getConvertedValue(OType.BOOLEAN, stateVertex.getProperty("isInitial"))) {
                        element.addClass("isInitial");
                    }
                }
                elements.add(element);
            }
        }
        return elements;
    }

    /**
     * This method transforms a resultset of edges into elements.
     *
     * @param resultSet
     * @param className
     * @return
     */
    private ArrayList<Element> fetchEdges(OResultSet resultSet, String className) {
        ArrayList<Element> elements = new ArrayList<>();
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isEdge()) {
                Optional<OEdge> op = result.getEdge();
                if (!op.isPresent()) continue;
                OEdge actionEdge = op.get();
                OVertexDocument source = actionEdge.getProperty("out");
                OVertexDocument target = actionEdge.getProperty("in");
                Edge jsonEdge = new Edge("e" + formatId(actionEdge.getIdentity().toString()), "n" + formatId(source.getIdentity().toString()), "n" + formatId(target.getIdentity().toString()));
                for (String propertyName : actionEdge.getPropertyNames()) {
                    if (propertyName.contains("in") || propertyName.contains("out")) {
                        // these are edge indicators. Ignore
                        continue;
                    }
                    jsonEdge.addProperty(propertyName, actionEdge.getProperty(propertyName).toString());
                }
                elements.add(new Element(Element.GROUP_EDGES, jsonEdge, className));
            }
        }
        return elements;
    }

    /**
     * This method saves screenshots to disk.
     *
     * @param recordBytes
     * @param identifier
     */
    private void processScreenShot(ORecordBytes recordBytes, String identifier, String modelIdentifier) {
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }

        // see if we have a directory for the screenshots yet
        File screenshotDir = new File(outputDir + modelIdentifier + File.separator);

        if (!screenshotDir.exists()) {
            screenshotDir.mkdir();
        }

        // save the file to disk
        File screenshotFile = new File(screenshotDir, identifier + ".png");
        try {
            FileOutputStream outputStream = new FileOutputStream(screenshotFile);
            outputStream.write(recordBytes.toStream());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String formatId(String id) {
        if (id.indexOf("#") != 0) return id; // not an orientdb id
        id = id.replaceAll("[#]", "");
        return id.replaceAll("[:]", "_");
    }

    /**
     * Helper method that converts an object value based on a specified OrientDB data type.
     *
     * @param oType
     * @param valueToConvert
     * @return
     */
    private Object getConvertedValue(OType oType, Object valueToConvert) {
        Object convertedValue = null;
        switch (oType) {
            case BOOLEAN:
                convertedValue = OType.convert(valueToConvert, Boolean.class);
                break;

            case STRING:
                convertedValue = OType.convert(valueToConvert, String.class);
                break;

            case LINKBAG:
                // we don't process these as a separate attribute
                break;

            case EMBEDDEDSET:
                convertedValue = OType.convert(valueToConvert, Set.class);
                break;

            case INTEGER:
                convertedValue = OType.convert(valueToConvert, Integer.class);
                break;

            case DATETIME:
                convertedValue = OType.convert(valueToConvert, Date.class);
                break;
        }
        return convertedValue;
    }

    private void computeProps(String propertyName, OElement graphElement, Set<String> globalPropositions, boolean isWidget,boolean isEdge ) {
        computeProps(propertyName, graphElement,  globalPropositions, isWidget, isEdge, false);
    }

    private void computeProps(String propertyName, OElement graphElement, Set<String> globalPropositions, boolean isWidget, boolean isEdge, boolean isDeadState) {
        // isdeadstate is not used
        StringBuilder apkey = new StringBuilder();
        List<WidgetFilter> passedWidgetFilters;
        //compose APkey
        for (String k : apSelectorManager.getAPKey()
        ) {
            Object prop = graphElement.getProperty(k);
            if (prop == null) {
                String fallback;
                Object concreteprop;
                if (isWidget) {
                    concreteprop = graphElement.getProperty(Tags.ConcreteID.name()); // must exists for state/widget
                }else
                    concreteprop = graphElement.getProperty("actionId"); // must exists for concrete edge/action
                if (concreteprop == null) {
                    fallback = "undefined";
                } else {
                    fallback = concreteprop.toString();
                }
                apkey.append(fallback);
                apkey.append(apSelectorManager.getApEncodingSeparator());
            }
            else {
                apkey.append(prop);
                apkey.append(apSelectorManager.getApEncodingSeparator());
            }
        }
        if (isWidget) {
            passedWidgetFilters = apSelectorManager.passWidgetFilters(
                    graphElement.getProperty(Tags.Role.name().toString()),
                    graphElement.getProperty(Tags.Title.name().toString()),
                    graphElement.getProperty(Tags.Path.name().toString())
                    //graphElement.getProperty(Tags.Path.name().toString() // dummy, parenttitle is not implemented yet
            );

            if (passedWidgetFilters!=null && passedWidgetFilters.size()>0 ){
                for (WidgetFilter wf: passedWidgetFilters) // add the filter specific elected attributes and expressions
                {// candidate for refactoring as this requires a double iteration of widget filter
                    globalPropositions.addAll(wf.getAPsOfAttribute(apkey.toString(),propertyName,graphElement.getProperty(propertyName).toString()));
                }
            }
        }
        if (!isWidget && isEdge){
            globalPropositions.addAll(apSelectorManager.getTransitionFilter().getAPsOfAttribute(apkey.toString(),propertyName,graphElement.getProperty(propertyName).toString()));
        }
        if (!isWidget && !isEdge){
            globalPropositions.addAll(apSelectorManager.getStateFilter().getAPsOfAttribute(apkey.toString(),propertyName,graphElement.getProperty(propertyName).toString()));
        }

    }
    @Deprecated
    private void testgraphmlexport(String file){  // inferior css 20190713
        String connectionString = dbConfig.getConnectionType() + ":/" + (dbConfig.getConnectionType().equals("remote") ?
                dbConfig.getServer() : dbConfig.getDatabaseDirectory());// +"/";
String dbconnectstring = connectionString+"\\"+dbConfig.getDatabase();
        OrientGraph grap = new OrientGraph(dbconnectstring,dbConfig.getUser(),dbConfig.getPassword());
        System.out.println("debug connectionstring: "+dbconnectstring+" \n");

        //Graph graph = new OrientGraph(connectionString+"/"+dbConfig.getDatabase(),dbConfig.getUser(),dbConfig.getPassword());
        Map<String,Object> conf = new HashMap<String,Object>();
        conf.put("blueprints.graph", "com.tinkerpop.blueprints.impls.orient.OrientGraph");
        conf.put("blueprints.orientdb.url",dbconnectstring);
        conf.put("blueprints.orientdb.username",dbConfig.getUser());
        conf.put("blueprints.orientdb.password",dbConfig.getPassword());

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
                writer.writeGraph(fos,graph);

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
    public boolean saveToGraphMLFile(String file){


        //init

        List<AbstractStateModel> abstractStateModels= fetchAbstractModels();
        AbstractStateModel abstractStateModel;
        if (abstractStateModels.size()==0 ) {
            System.out.println("ERROR: Number of Models in the graph database is ZERO");
            return false;
        }
        abstractStateModel =abstractStateModels.get(0);
        String stmt;
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", abstractStateModel.getModelIdentifier());
        //!!!!!!!!!!!!!!!!!!!!!!get nodes , then get edges. this is required for postprocessing a graphml by python package networkx.
        List<String> stmtlist= new ArrayList<>();


        if (abstractStateModels.size()>1){// navigate from abstractstate to be able to apply the filter.
            System.out.println("WARNING: Number of Models in the graph database is more than ONE. We try with the first model");
           // stmtlist.add("SELECT  FROM  (TRAVERSE both() FROM (SELECT FROM AbstractState WHERE abstractionLevelIdentifier = :identifier))");
            //stmtlist.add("SELECT FROM AbstractState WHERE abstractionLevelIdentifier = :identifier"); // select abstractstate themselves
            stmtlist.add("SELECT FROM AbstractStateModel WHERE abstractionLevelIdentifier = :identifier OR modelIdentifier = :identifier" ); // select abstractstatemodel , this is an unconnected node
            // the "both()" in the next stmt is needed to invoke recursion.
            // apparently , the next result set contains first a list of all nodes, then of all edge: good !
            stmtlist.add("SELECT  FROM (TRAVERSE both(), bothE() FROM (SELECT FROM AbstractState WHERE abstractionLevelIdentifier = :identifier)) ");

        }else{
            stmtlist.add("SELECT  FROM (TRAVERSE both(), bothE() FROM (SELECT FROM AbstractState)) ");
            //stmtlist.add("SELECT  FROM V ");//  stmtlist.add("SELECT  FROM E ");
        }


        Set<GraphML_DocKey> docnodekeys=new HashSet<>() ;
        Set<GraphML_DocKey> docedgekeys=new HashSet<>() ;
        List<GraphML_DocNode> nodes = new ArrayList<>() ;
        List<GraphML_DocEdge> edges = new ArrayList<>() ;

        for (String stm:stmtlist
             ) {
            //Map<String, Object> params = new HashMap<>();
            //OResultSet resultSet = db.query(stmt, params);
            OResultSet resultSet = db.query(stm,params);
            String source = "";
            String target = "";
            String keyname;
            String attributeType;
            while (resultSet.hasNext()) {
                OResult result = resultSet.next();
                // we're expecting a node or edge
                if (result.isVertex() || result.isEdge()) {
                    Optional<OElement> op = result.getElement();

                    if (!op.isPresent()) continue;
                    OElement graphElement = op.get();
                    String eleId = graphElement.getIdentity().toString();
                    if (result.isEdge()) {
                        source = ((OVertexDocument) graphElement.getProperty("out")).getIdentity().toString();
                        target = ((OVertexDocument) graphElement.getProperty("in")).getIdentity().toString();
                    }
                    List<GraphML_DocEleProperty> eleProperties = new ArrayList<>();
                    for (String propertyName : graphElement.getPropertyNames()) {
                        keyname = propertyName;
                        String rawattributeType = graphElement.getProperty(propertyName).getClass().getSimpleName().toLowerCase();
                        //if(rawattributeType.equals("date")||rawattributeType.startsWith("orecord")||rawattributeType.startsWith("otracked")){
                        if(!rawattributeType.equals("boolean")&&
                                !rawattributeType.equals("long") &&
                                !rawattributeType.equals("double")&&
                                !rawattributeType.equals("string")){
                            attributeType="string"; // unknown types are converted to string
                        }else
                            attributeType=rawattributeType;

                        if (result.isEdge() && (propertyName.startsWith("in") || propertyName.startsWith("out"))) {
                            // these are probably edge indicators. Ignore
                            continue;
                        }
                        if (result.isVertex() &&(propertyName.contains("in_") || propertyName.contains("out_"))) {
                            // these are probably edge indicators. Ignore
                            continue;
                        }
                        if (result.isVertex()) {
                            docnodekeys.add(new GraphML_DocKey(keyname, "node", keyname, attributeType));

                        } else {
                            docedgekeys.add(new GraphML_DocKey(keyname, "edge", keyname, attributeType));

                        }
                        eleProperties.add(new GraphML_DocEleProperty(keyname, graphElement.getProperty(propertyName).toString()));
                    }
                    if (result.isVertex()) {

                        eleProperties.add(new GraphML_DocEleProperty("labelV", graphElement.getSchemaType().get().toString()));
                        nodes.add(new GraphML_DocNode(eleId, eleProperties));
                    } else {
                        eleProperties.add(new GraphML_DocEleProperty("labelE", graphElement.getSchemaType().get().toString()));
                        edges.add(new GraphML_DocEdge(eleId, source, target, eleProperties));

                    }

                }
            }

        }
        GraphML_DocGraph graph= new GraphML_DocGraph(dbConfig.getDatabase(),nodes,edges);
        Set<GraphML_DocKey> tempset = new LinkedHashSet<GraphML_DocKey>();
        docnodekeys.add(new GraphML_DocKey("labelV", "node", "labelV", "string"));
        docedgekeys.add(new GraphML_DocKey("labelE", "edge", "labelE", "string"));
        tempset.addAll(docnodekeys);
        tempset.addAll(docedgekeys);
        GraphML_DocRoot root = new GraphML_DocRoot(tempset,graph);
        XMLHandler.save(root,file);

return true;
    }

}

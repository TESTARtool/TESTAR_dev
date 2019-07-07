package nl.ou.testar.temporal.behavior;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OType;
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
import nl.ou.testar.temporal.structure.APSelectorManager;
import nl.ou.testar.temporal.structure.StateEncoding;
import nl.ou.testar.temporal.structure.TemporalModel;
import nl.ou.testar.temporal.structure.TransitionEncoding;
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
    private APSelectorManager Apmgr;

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

    }

    /**
     * Shuts down the orientDB connection.
     */
    public void shutdown() {
        orientDB.close();
    }

    /**
     * This method fetches a list of the abstract state models in the current OrientDB data store.
     *
     * @return
     */
    public List<AbstractStateModel> fetchModels() {
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
        //}
        return abstractStateModels;
    }


    //*********************************
    public TemporalModel getTemporalModel(AbstractStateModel abstractStateModel, APSelectorManager Apmgr) {
        this.Apmgr = Apmgr;

        TemporalModel tmodel = new TemporalModel(
                abstractStateModel.getApplicationName(), abstractStateModel.getApplicationVersion(),
                abstractStateModel.getModelIdentifier(), abstractStateModel.getAbstractionAttributes());
        // concrete states
        String stmt = "SELECT FROM V WHERE @class = 'ConcreteState'";
        //Map<String, Object> params = new HashMap<>();
        //OResultSet resultSet = db.query(stmt, params);
        OResultSet resultSet = db.query(stmt);
        //Set selectedAttibutes = Apmgr.getSelectedSanitizedAttributeNames();

        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()) {

                Optional<OVertex> op = result.getVertex();
                if (!op.isPresent()) continue;

                OVertex stateVertex = op.get();
                StateEncoding senc = new StateEncoding(stateVertex.getIdentity().toString());
                //List<String> props = new ArrayList<>();
                Set<String> props = new HashSet<>();
                //System.out.println("debug state;"+senc.getState());

                //compose key


                for (String propertyName : stateVertex.getPropertyNames()) {
                    computeProps(propertyName,stateVertex,props,false);

            }
                props.addAll(getWidgetPropositions(senc.getState()));// concrete widgets
                senc.setStateAPs(props);
                senc.setTransitionColl(getTransitions(senc.getState()));
                tmodel.addStateEncoding(senc, false);
            }
        }
        tmodel.updateTransitions(); //update once. this is a costly operation
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
        Set<String> props = new HashSet<>();
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()) {
                Optional<OVertex> op = result.getVertex();
                if (!op.isPresent()) continue;
                OVertex stateVertex = op.get();
                for (String propertyName : stateVertex.getPropertyNames()) {
                   computeProps(propertyName,stateVertex,props,true);
                }
            }
        }
        return props;
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
                Set<String> props = new HashSet<>();
                for (String propertyName : actionEdge.getPropertyNames()) {
                        computeProps(propertyName,actionEdge,props,true);
                    }
                trenc.setEdgeAPs(props);
                trenclist.add(trenc);
            }


        }
        return trenclist;
    }


//***********************************

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

    private void computeProps(String propertyName, OElement graphElement, Set<String> props, boolean isWidget) {

        //Set selectedAttibutes = Apmgr.getSelectedSanitizedAttributeNames();
        StringBuilder apkey = new StringBuilder();
        boolean pass=true;
         //System.out.println("debug passwidget filter: " + propertyName + " _ "+graphElement.toString()+" _ "+isWidget);
        if (isWidget) {
            pass = Apmgr.passWidgetFilters(
                    graphElement.getProperty(Tags.Role.name().toString()),
                    graphElement.getProperty(Tags.Title.name().toString()),
                    graphElement.getProperty(Tags.Path.name().toString())
                    //graphElement.getProperty(Tags.Path.name().toString() // dummy, parenttitle is not implemented yet
            );
        }
        if (pass){
            //compose key
            for (String k : Apmgr.getAPKey()
            ) {
                Object prop = graphElement.getProperty(k);
                if (prop == null) {
                    String fallback;
                    Object concreteprop = graphElement.getProperty(Tags.ConcreteID.name()); // must exists
                    if (concreteprop == null) {
                        fallback = "undefined";
                    } else {
                        fallback = concreteprop.toString();
                    }
                    apkey.append(fallback);
                    apkey.append(APEncodingSeparator.SECTIONSIGN.symbol);
                }
                else {
                    apkey.append(prop); apkey.append(APEncodingSeparator.SECTIONSIGN.symbol);
                }
            }
            props.addAll(Apmgr.getAPsOfAttribute(apkey.toString(),propertyName,graphElement.getProperty(propertyName).toString()));
        }

    }
    public void testgraphmlexport(String file){
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
    public void testmygraphmlexport(String file){

        //init
        Set<GraphML_DocKey> docnodekeys=new HashSet<>() ;
        Set<GraphML_DocKey> docedgekeys=new HashSet<>() ;


        List<GraphML_DocNode> nodes = new ArrayList<>() ;
        List<GraphML_DocEdge> edges = new ArrayList<>() ;


        //get nodes

        String stmt = "SELECT * FROM V ";
        //Map<String, Object> params = new HashMap<>();
        //OResultSet resultSet = db.query(stmt, params);
        OResultSet resultSet = db.query(stmt);
        System.out.println("resultset >0 : "+resultSet.hasNext()+"\n");
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()) {
                Optional<OVertex> op = result.getVertex();
                if (!op.isPresent()) continue;
                OVertex stateVertex = op.get();
                String nodeId = stateVertex.getIdentity().toString();
                List<GraphML_DocEleProperty> eleProperties=new ArrayList<>();

                String  keyname ;
                String attributeType;
                for (String propertyName : stateVertex.getPropertyNames()) {
                    if (propertyName.matches(".*class")){
                      keyname= result.isVertex() ? "labelV" : "labelE";
                      attributeType="string";

                    }else {
                        keyname=propertyName;
                        attributeType = stateVertex.getProperty(propertyName).getClass().getSimpleName();
                    }
                    if (propertyName.contains("in_") || propertyName.contains("out_")) {
                        // these are edge indicators. Ignore
                        continue;
                    }
                    if (result.isVertex()){
                        docnodekeys.add(new GraphML_DocKey(keyname,"node",keyname,attributeType));
                    }else{
                        docedgekeys.add(new GraphML_DocKey(keyname,"edge",keyname,attributeType));
                    }
                    eleProperties.add(new GraphML_DocEleProperty(keyname,stateVertex.getProperty(propertyName).toString()));
                }
                if (result.isVertex()){
                    nodes.add(new GraphML_DocNode(nodeId,eleProperties));
                }else{
                    edges.add(new GraphML_DocEdge(nodeId,"","",eleProperties));

                }

            }
        }



        //get edges

        stmt = "SELECT * FROM E ";
        //Map<String, Object> params = new HashMap<>();
        //OResultSet resultSet = db.query(stmt, params);
         resultSet = db.query(stmt);
        System.out.println("resultset >0 : "+resultSet.hasNext()+"\n");
        String source="";
        String target="";
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()|| result.isEdge() ){
                Optional<OElement> op = result.getElement();
                //Optional<OVertex> op = result.getVertex();
                if (!op.isPresent()) continue;
                OElement graphElement = op.get();
                String nodeId = graphElement.getIdentity().toString();
                if (result.isEdge() ) {
                    source = ((OVertexDocument) graphElement.getProperty("out")).getIdentity().toString();
                    target = ((OVertexDocument) graphElement.getProperty("in")).getIdentity().toString();
                }

                List<GraphML_DocEleProperty> eleProperties=new ArrayList<>();

                String  keyname ;
                String attributeType;
                for (String propertyName : graphElement.getPropertyNames()) {
                    if (propertyName.matches(".*class")){
                        keyname= result.isVertex() ? "labelV" : "labelE";
                        attributeType="string";

                    }else {
                        keyname=propertyName;
                        attributeType = graphElement.getProperty(propertyName).getClass().getSimpleName();
                    }
                    if (propertyName.startsWith("in") || propertyName.startsWith("out")) {
                        // these are edge indicators. Ignore
                        continue;
                    }
                    //edge source and target
                    if (result.isVertex()){
                        docnodekeys.add(new GraphML_DocKey(keyname,"node",keyname,attributeType));

                    }else{
                        docedgekeys.add(new GraphML_DocKey(keyname,"edge",keyname,attributeType));

                    }
                    eleProperties.add(new GraphML_DocEleProperty(keyname,graphElement.getProperty(propertyName).toString()));
                }
                if (result.isVertex()){
                    nodes.add(new GraphML_DocNode(nodeId,eleProperties));
                }else{
                    edges.add(new GraphML_DocEdge(nodeId,source,target,eleProperties));

                }

            }
        }
        GraphML_DocGraph graph= new GraphML_DocGraph(dbConfig.getDatabase(),nodes,edges);
        Set<GraphML_DocKey> tempset = new LinkedHashSet<GraphML_DocKey>();
        tempset.addAll(docnodekeys);
        tempset.addAll(docedgekeys);
        GraphML_DocRoot root = new GraphML_DocRoot(tempset,graph);
        XMLHandler.save(root,file);


    }
}

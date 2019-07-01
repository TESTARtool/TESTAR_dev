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
import org.fruit.alayer.Tags;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

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
                    computeProps(propertyName,stateVertex,props);

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
                    computeProps(propertyName,stateVertex,props);
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
                        computeProps(propertyName,actionEdge,props);
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

    private void computeProps(String propertyName, OElement graphElement, Set<String> props) {

        //Set selectedAttibutes = Apmgr.getSelectedSanitizedAttributeNames();
        StringBuilder apkey = new StringBuilder();

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
                apkey.append("_");
            }
            else {
                apkey.append(prop); apkey.append("_");
            }
        }
        props.addAll(Apmgr.getAPsOfAttribute(apkey.toString(),propertyName,graphElement.getProperty(propertyName).toString()));


/*        for (TagBean<?> tb : Apmgr.getSelectedAttributes()
        ) {
            if (Validation.sanitizeAttributeName(tb.name()) == propertyName) {
                // check if Boolean
                if (tb.type() == Boolean.class) {
                    props.add(apkey + propertyName + "__" + graphElement.getProperty(propertyName).toString());
                } else if (tb.type() == Long.class || tb.type() == Double.class || tb.type() == Integer.class) {
                    // add number value expressions
                    for (PairBean<InferrableExpression, String> pb : Apmgr.getValuedExpressions()
                    ) {
                        boolean b = true;
                        if (pb.left().typ == "number") {
                        }
                        Double val = Double.parseDouble(graphElement.getProperty(propertyName).toString());  //is a bit naieve, does not take actual type into account
                        Double refval = Double.parseDouble(pb.right());
                        //assume _lt
                        if (pb.left().name().contains("_eq")) b = val.longValue() == refval.longValue();
                        else b = val.longValue() < refval.longValue();
                        props.add(apkey.toString() + propertyName + "_" + pb.left().name() + pb.right() + "__" + b);
                    }

                } else if (tb.type() == Shape.class) {

                    // add spahe value expressions not ready yet CSS 20190630


                } else {       //assume string
                    for (PairBean<InferrableExpression, String> pb : Apmgr.getValuedExpressions()
                    ) {
                        boolean b = true;
                        if (pb.left().typ == "text") {

                            if (pb.left().name().contains("length")) {
                                long len = graphElement.getProperty(propertyName).toString().length();

                                Double refval = Double.parseDouble(pb.right());

                                if (pb.left().name().contains("_eq")) {
                                    b = len == refval.longValue();

                                } else {//assume _lt
                                    b = len < refval.longValue();
                                }

                            } else {
                                //assume a "text" match criterion
                                String val = graphElement.getProperty(propertyName).toString();
                                String match = pb.right();
                                b = val.matches(match);
                            }


                        }
                        props.add(apkey.toString() + propertyName + "_" + pb.left().name() + pb.right() + "__" + b);
                    }
                }
            }

        }*/
    }
}

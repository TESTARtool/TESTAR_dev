package nl.ou.testar.StateModel.Analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.record.impl.OVertexDocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import nl.ou.testar.StateModel.Analysis.Json.Edge;
import nl.ou.testar.StateModel.Analysis.Json.Element;
import nl.ou.testar.StateModel.Analysis.Json.Vertex;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;
import nl.ou.testar.StateModel.Analysis.Representation.ActionViz;
import nl.ou.testar.StateModel.Analysis.Representation.TestSequence;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Sequence.SequenceVerdict;

import java.io.*;
import java.text.DateFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class AnalysisManager {

    // orient db instance that will create database sessions
    private OrientDB orientDB;

    // orient db configuration object
    private Config dbConfig;

    // the location of the directory where we need to store output files
    private String outputDir;

    /**
     * Constructor
     * @param config
     * @param outputDir
     */
    public AnalysisManager(final Config config, String outputDir) {
        String connectionString = config.getConnectionType() + ":" + (config.getConnectionType().equals("remote") ?
                config.getServer() : config.getDatabaseDirectory()) + "/";
        orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
        dbConfig = config;
        this.outputDir = outputDir;

        // check if the credentials are valid
        try (ODatabaseSession db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())) {
            // if there is no connection possible this will throw an exception
        }
    }

    /**
     * Shuts down the orientDB connection.
     */
    public void shutdown() {
        orientDB.close();
    }

    /**
     * This method fetches a list of the abstract state models in the current OrientDB data store.
     * @return
     */
    public List<AbstractStateModel> fetchModels() {
        ArrayList<AbstractStateModel> abstractStateModels = new ArrayList<>();
        try (ODatabaseSession db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())) {
            OResultSet resultSet = db.query("SELECT FROM AbstractStateModel");
            while (resultSet.hasNext()) {
                OResult result = resultSet.next();
                // we're expecting a vertex
                if (result.isVertex()) {
                    Optional<OVertex> op = result.getVertex();
                    if (!op.isPresent()) continue;
                    OVertex modelVertex = op.get();

                    String applicationName = (String)getConvertedValue(OType.STRING, modelVertex.getProperty("applicationName"));
                    String applicationVersion = (String)getConvertedValue(OType.STRING, modelVertex.getProperty("applicationVersion"));
                    String modelIdentifier = (String)getConvertedValue(OType.STRING, modelVertex.getProperty("modelIdentifier"));
                    Set abstractionAttributes = (Set)getConvertedValue(OType.EMBEDDEDSET, modelVertex.getProperty("abstractionAttributes"));
                    // fetch the test sequences
                    List<TestSequence> sequenceList = fetchTestSequences(modelIdentifier, db);

                    AbstractStateModel abstractStateModel = new AbstractStateModel(
                            applicationName, applicationVersion, modelIdentifier, abstractionAttributes, sequenceList
                    );
                    abstractStateModels.add(abstractStateModel);
                }
            }
            resultSet.close();
        }
        return abstractStateModels;
    }

    /**
     * This method fetches the test sequences for a given abstract state model.
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
                    nrOfNodes = (int)getConvertedValue(OType.INTEGER, nodeResult.getProperty("nr"));
                    if (nrOfNodes > 0) {
                        nrOfNodes--;
                    }
                }
                nodeResultSet.close();

                String sequenceId = (String) getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId"));
                Date startDateTime = (Date) getConvertedValue(OType.DATETIME, sequenceVertex.getProperty("startDateTime"));

                // not the best piece of code, but it works for now
                int verdict;
                String verdictValue = (String) getConvertedValue(OType.ANY.STRING, sequenceVertex.getProperty("verdict"));
                if (verdictValue.equals(SequenceVerdict.COMPLETED_SUCCESFULLY.toString())) {
                    verdict =TestSequence.VERDICT_SUCCESS;
                }
                else if (verdictValue.equals(SequenceVerdict.INTERRUPTED_BY_USER.toString())) {
                    verdict = TestSequence.VERDICT_INTERRUPT_BY_USER;
                }
                else if (verdictValue.equals(SequenceVerdict.INTERRUPTED_BY_ERROR.toString())) {
                    verdict = TestSequence.VERDICT_INTERRUPT_BY_SYSTEM;
                }
                else {
                    verdict = TestSequence.VERDICT_UNKNOWN;
                }

                // fetch the number of errors that were encountered during the test run
                String errorStmt = "SELECT COUNT(*) as nr FROM(TRAVERSE out(\"FirstNode\"), out(\"SequenceStep\") FROM ( SELECT FROM TestSequence WHERE sequenceId = :sequenceId)) WHERE @class = \"SequenceNode\" AND containsErrors = true";
                params = new HashMap<>();
                params.put("sequenceId", getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId")));
                OResultSet errorResultSet = db.query(errorStmt, params);
                int nrOfErrors = 0;
                if (errorResultSet.hasNext()) {
                    OResult errorResult = errorResultSet.next();
                    nrOfErrors = (int)getConvertedValue(OType.INTEGER, errorResult.getProperty("nr"));
                }
                errorResultSet.close();

                // check if the sequence was deterministic
                String deterministicQuery = "SELECT FROM (TRAVERSE outE(\"SequenceStep\") FROM (\n" +
                        "\n" +
                        "TRAVERSE out(\"SequenceStep\")\n" +
                        "FROM(\n" +
                        "\n" +
                        "SELECT FROM (\n" +
                        "TRAVERSE out(\"FirstNode\")\n" +
                        "\n" +
                        "FROM (\n" +
                        "select from TestSequence where sequenceId = :sequenceId)) where @class = \"SequenceNode\"))) where @class = \"SequenceStep\" AND nonDeterministic = true";
                params = new HashMap<>();
                params.put("sequenceId", getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId")));
                OResultSet determinismResultSet = db.query(deterministicQuery, params);
                boolean sequenceIsDeterministic = !determinismResultSet.hasNext();
                determinismResultSet.close();

                TestSequence testSequence = new TestSequence(sequenceId, DateFormat.getDateTimeInstance().format(startDateTime), String.valueOf(nrOfNodes), verdict, sequenceIsDeterministic);
                testSequence.setNrOfErrors(nrOfErrors);

                sequenceList.add(testSequence);
            }
        }
        resultSet.close();
        return sequenceList;
    }

    public List<ActionViz> fetchTestSequence(String sequenceId) {
        List<ActionViz> visualizations = new ArrayList<>();
        // fetch the first sequence node belonging to a given test sequence
        try (ODatabaseSession db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())) {
            String sequenceStmt = "SELECT FROM(TRAVERSE out(\"FirstNode\") FROM ( SELECT FROM TestSequence WHERE sequenceId = :sequenceId)) WHERE @class = \"SequenceNode\"";
            Map<String, Object> params = new HashMap<>();
            params.put("sequenceId", sequenceId);
            OResultSet resultSet = db.query(sequenceStmt, params);

            if (!resultSet.hasNext()) {
                return visualizations; // no sequence node found
            }

            OResult nodeResult = resultSet.next();
            if (!nodeResult.isVertex()) return visualizations;
            Optional<OVertex> nodeVertexOptional = nodeResult.getVertex();
            if (!nodeVertexOptional.isPresent()) {
                return visualizations;
            }

            // alright, we have a node
            OVertex nodeVertex = nodeVertexOptional.get();
            int counterSource = 1;
            int counterTarget = 2;

            while(true) {
                // pffff..alright, now that we have our node vertex, we need to get two things:
                // 1) the concrete state node that is connected to our sequence node, as we need the screenshot
                // 2) the sequence step going out, for the description

                // first, see if we have another sequence step from this node
                OEdge sequenceStepEdge = null;
                for(OEdge edge : nodeVertex.getEdges(ODirection.OUT, "SequenceStep")) {
                    sequenceStepEdge = edge;
                    break; // there should at most be one edge
                }

                if (sequenceStepEdge == null) {
                    break; // nothing left to do
                }

                // next, get the vertex that is at the received end of the step edge
                OVertex targetVertex = sequenceStepEdge.getTo();
                // now, fetch the concrete states for both the vertices
                OVertex sourceState = null;
                OVertex targetState = null;
                for(OEdge edge : nodeVertex.getEdges(ODirection.OUT, "Accessed")) {
                    sourceState = edge.getTo();
                    break; // there should at most be one edge
                }
                for(OEdge edge: targetVertex.getEdges(ODirection.OUT, "Accessed")) {
                    targetState = edge.getTo();
                    break; // there should at most be one edge
                }
                if (sourceState == null || targetState == null) {
                    return visualizations;
                }

                String sourceScreenshot = "n" + formatId(sourceState.getIdentity().toString());
                processScreenShot(sourceState.getProperty("screenshot"), sourceScreenshot, sequenceId);
                String targetScreenshot = "n" + formatId(targetState.getIdentity().toString());
                processScreenShot(targetState.getProperty("screenshot"), targetScreenshot, sequenceId);
                String actionDescription = (String) getConvertedValue(OType.STRING, sequenceStepEdge.getProperty("actionDescription"));
                boolean deterministic = !(boolean)getConvertedValue(OType.BOOLEAN, sequenceStepEdge.getProperty("nonDeterministic"));
                ActionViz actionViz = new ActionViz(sourceScreenshot, targetScreenshot, actionDescription, counterSource, counterTarget, deterministic);
                visualizations.add(actionViz);
                nodeVertex = targetVertex;
                counterSource++;
                counterTarget++;
            }
            resultSet.close();
            return visualizations;
        }
    }

    /**
     * This model generates graph data for a given abstract state model and writes it to a json file.
     * @param modelIdentifier the abstract state model identifier
     * @param abstractLayerRequired true if the abstract state layer needs to be exported
     * @param concreteLayerRequired true if the concrete state layer needs to be exported
     * @param sequenceLayerRequired true if the sequence layer needs to be exported
     * @return
     */
    public String fetchGraphForModel(String modelIdentifier, boolean abstractLayerRequired, boolean concreteLayerRequired, boolean sequenceLayerRequired, boolean showCompoundGraph) {
        ArrayList<Element> elements = new ArrayList<>();
        if (abstractLayerRequired || concreteLayerRequired || sequenceLayerRequired) {
            try (ODatabaseSession db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())) {
                if (abstractLayerRequired) {
                    elements.addAll(fetchAbstractLayer(modelIdentifier, db, showCompoundGraph));
                }

                if (concreteLayerRequired) {
                    elements.addAll(fetchConcreteLayer(modelIdentifier, db, showCompoundGraph));
                }

                if (sequenceLayerRequired) {
                    elements.addAll(fetchSequenceLayer(modelIdentifier, db, showCompoundGraph));
                }

                if (abstractLayerRequired && concreteLayerRequired) {
                    elements.addAll(fetchAbstractConcreteConnectors(modelIdentifier, db));
                }

                if (concreteLayerRequired && sequenceLayerRequired) {
                    elements.addAll(fetchConcreteSequenceConnectors(modelIdentifier, db));
                }
            }
        }

        StringBuilder builder = new StringBuilder(modelIdentifier);
        builder.append("_");
        if (abstractLayerRequired) builder.append("A");
        if (concreteLayerRequired) builder.append("C");
        if (sequenceLayerRequired) builder.append("S");
        builder.append("_");
        builder.append(Instant.now().toEpochMilli());
        builder.append("_elements.json");
        String filename = builder.toString();
        return writeJson(elements, filename, modelIdentifier);
    }

    /**
     * This method fetches the elements in the abstract state layer for a given abstract state model.
     * @param modelIdentifier
     * @param db
     * @return
     */
    private List<Element> fetchAbstractLayer(String modelIdentifier, ODatabaseSession db, boolean showCompoundGraph) {
        ArrayList<Element> elements = new ArrayList<>();

        // optionally add a parent node for the abstract layer
        if (showCompoundGraph) {
            Vertex abstractStateParent = new Vertex("AbstractLayer");
            elements.add(new Element(Element.GROUP_NODES, abstractStateParent, "Parent"));
        }

        // abstract states
        String stmt = "SELECT FROM AbstractState WHERE modelIdentifier = :identifier";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "AbstractState", showCompoundGraph ? "AbstractLayer" : null, modelIdentifier));
        resultSet.close();

        // abstract actions
        stmt = "SELECT FROM AbstractAction WHERE modelIdentifier = :identifier";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "AbstractAction"));
        resultSet.close();

        // Black hole class
        stmt = "SELECT FROM (TRAVERSE out() FROM  (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'BlackHole'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "BlackHole", showCompoundGraph ? "AbstractLayer" : null, modelIdentifier));
        resultSet.close();


        // unvisited abstract actions
        stmt = "SELECT FROM UnvisitedAbstractAction WHERE modelIdentifier = :identifier";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "UnvisitedAbstractAction"));
        resultSet.close();

        return elements;
    }

    /**
     * This method fetches the elements in the concrete state layer for a given abstract state model.
     * @param modelIdentifier
     * @param db
     * @return
     */
    private List<Element> fetchConcreteLayer(String modelIdentifier, ODatabaseSession db, boolean showCompoundGraph) {
        ArrayList<Element> elements = new ArrayList<>();

        // optionally add a parent node for the concrete layer
        if (showCompoundGraph) {
            Vertex concreteStateParent = new Vertex("ConcreteLayer");
            elements.add(new Element(Element.GROUP_NODES, concreteStateParent, "Parent"));
        }

        // concrete states
        String stmt = "SELECT FROM (TRAVERSE in() FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "ConcreteState", showCompoundGraph ? "ConcreteLayer" : null, modelIdentifier));
        resultSet.close();

        // concrete actions
        stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').outE('ConcreteAction') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'ConcreteAction'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "ConcreteAction"));
        resultSet.close();

        return elements;
    }

    /**
     * This method fetches the elements in the sequence layer for a given abstract state model.
     * @param modelIdentifier
     * @param db
     * @return
     */
    private List<Element> fetchSequenceLayer(String modelIdentifier, ODatabaseSession db, boolean showCompoundGraph) {
        ArrayList<Element> elements = new ArrayList<>();

        // optionally add a parent node for the sequence layer
        if (showCompoundGraph) {
            Vertex sequenceParent = new Vertex("SequenceLayer");
            elements.add(new Element(Element.GROUP_NODES, sequenceParent, "Parent"));
        }

        // test sequence
        String stmt = "SELECT FROM TestSequence WHERE modelIdentifier = :identifier";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "TestSequence", showCompoundGraph ? "SequenceLayer" : null, modelIdentifier));
        resultSet.close();

        // sequence nodes
        stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').in('Accessed') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'SequenceNode'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "SequenceNode", showCompoundGraph ? "SequenceLayer" : null, modelIdentifier));
        resultSet.close();

        // sequence steps
        stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').in('Accessed').outE('SequenceStep') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'SequenceStep'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "SequenceStep"));
        resultSet.close();

        // first node
        stmt = "SELECT FROM (TRAVERSE outE('FirstNode') FROM (SELECT FROM TestSequence WHERE modelIdentifier = :identifier)) WHERE @class = 'FirstNode'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "FirstNode"));
        resultSet.close();

        return elements;
    }

    /**
     * This method fetches the edges between the abstract and concrete layers.
     * @param modelIdentifier
     * @param db
     * @return
     */
    private List<Element> fetchAbstractConcreteConnectors(String modelIdentifier, ODatabaseSession db) {
        ArrayList<Element> elements = new ArrayList<>();

        // abstractedBy relation
        String stmt = "SELECT FROM (TRAVERSE inE() FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'isAbstractedBy'";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "isAbstractedBy"));

        return elements;
    }

    /**
     * This method fetches the edges between the concrete and sequence layers.
     * @param modelIdentifier
     * @param db
     * @return
     */
    private List<Element> fetchConcreteSequenceConnectors(String modelIdentifier, ODatabaseSession db) {
        ArrayList<Element> elements = new ArrayList<>();

        // accessed relation
        String stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').inE('Accessed') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'Accessed'";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "Accessed"));
        resultSet.close();

        return elements;
    }

    public String fetchWidgetTree(String concreteStateIdentifier) {
        try (ODatabaseSession db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())) {
            ArrayList<Element> elements = new ArrayList<>();

            // convert the concrete state identifier to an internal id if needed
            String internalId = concreteStateIdentifier.indexOf("n") == 0 ? unformatId(concreteStateIdentifier) : concreteStateIdentifier;

            // first get all the widgets
            String stmt = "SELECT FROM (TRAVERSE IN('isChildOf') FROM (SELECT FROM Widget WHERE @RID = :rid))";
            Map<String, Object> params = new HashMap<>();
            params.put("rid", internalId);
            OResultSet resultSet = db.query(stmt, params);
            elements.addAll(fetchNodes(resultSet, "Widget", null, concreteStateIdentifier));
            resultSet.close();

            // then get the parent/child relationship between the widgets
            stmt = "SELECT FROM isChildOf WHERE in IN(SELECT @RID FROM (TRAVERSE in('isChildOf') FROM (SELECT FROM Widget WHERE @RID = :rid)))";
            resultSet = db.query(stmt, params);
            elements.addAll(fetchEdges(resultSet, "isChildOf"));
            resultSet.close();

            // create a filename
            StringBuilder builder = new StringBuilder(concreteStateIdentifier);
            builder.append("_");
            builder.append(Instant.now().toEpochMilli());
            builder.append("_elements.json");
            String filename = builder.toString();
            return writeJson(elements, filename, concreteStateIdentifier);
        }
    }

    /**
     * This method transforms a resultset of nodes into elements.
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
                if(stateVertex.getPropertyNames().contains("isInitial")) {
                    if ((Boolean)getConvertedValue(OType.BOOLEAN, stateVertex.getProperty("isInitial"))) {
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
        File screenshotFile = new File( screenshotDir, identifier + ".png");
        if (screenshotFile.exists()) {
            return;
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(screenshotFile);
            outputStream.write(recordBytes.toStream());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    // this helper method formats the @RID property into something that can be used in a web frontend
    private String formatId(String id) {
        if (id.indexOf("#") != 0) return id; // not an orientdb id
        id = id.replaceAll("[#]", "");
        return id.replaceAll("[:]", "_");
    }

    // and this helper method formats a web frontend node id into one that is useable for internal orientdb use
    private String unformatId(String id) {
        id = id.replaceAll("n", "#");
        return id.replaceAll("_", ":");
    }

    /**
     * Helper method that converts an object value based on a specified OrientDB data type.
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
        return  convertedValue;
    }

    // this helper method will write elements to a file in json format
    private String writeJson(ArrayList<Element> elements, String filename, String subFolderName) {
        // check if the subfolder already exists
        File subFolder = new File(outputDir + subFolderName);
        if (!subFolder.isDirectory() && !subFolder.mkdir()) {
            return "";
        }

        File output = new File(subFolder, filename);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String result = mapper.writeValueAsString(elements);
            // let's write the resulting json to a file
            if (output.exists() || output.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(output.getAbsolutePath()));
                writer.write(result);
                writer.close();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }
}

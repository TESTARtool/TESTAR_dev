package nl.ou.testar.StateModel.Analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.record.impl.OVertexDocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import nl.ou.testar.StateModel.Analysis.HttpServer.JettyServer;
import nl.ou.testar.StateModel.Analysis.Json.Edge;
import nl.ou.testar.StateModel.Analysis.Json.Element;
import nl.ou.testar.StateModel.Analysis.Json.Vertex;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;
import nl.ou.testar.StateModel.Analysis.Representation.TestSequence;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

import java.io.*;
import java.text.DateFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class AnalysisManager {

    // orient db instance that will create database sessions
    private OrientDB orientDB;

    private Config dbConfig;

    private String outputDir;

    public AnalysisManager(final Config config, String outputDir) {
        String connectionString = config.getConnectionType() + ":/" + (config.getConnectionType().equals("remote") ?
                config.getServer() : config.getDatabaseDirectory()) + "/";
        orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
        dbConfig = config;
//        // check if the output directory has a trailing line separator
//        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
//            outputDir += File.separator;
//        }
//        this.outputDir = outputDir + "graphs" + File.separator;
        this.outputDir = outputDir;

        // check if the credentials are valid
        try (ODatabaseSession db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())) {
            // if there is no connection possible this will throw an exception
        }
    }

    private void init() {

    }


    public void shutdown() {
        orientDB.close();
    }

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
        }
        return abstractStateModels;
    }

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

                String sequenceId = (String) getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId"));
                Date startDateTime = (Date) getConvertedValue(OType.DATETIME, sequenceVertex.getProperty("startDateTime"));
                sequenceList.add(new TestSequence(sequenceId, DateFormat.getDateTimeInstance().format(startDateTime), String.valueOf(nrOfNodes)));
            }
        }
        return sequenceList;
    }

    public String fetchGraphForModel(String modelIdentifier, boolean abstractLayerRequired, boolean concreteLayerRequired, boolean sequenceLayerRequired) {
        ArrayList<Element> elements = new ArrayList<>();
        if (abstractLayerRequired || concreteLayerRequired || sequenceLayerRequired) {
            try (ODatabaseSession db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())) {
                if (abstractLayerRequired) {
                    elements.addAll(fetchAbstractLayer(modelIdentifier, db));
                }

                if (concreteLayerRequired) {
                    elements.addAll(fetchConcreteLayer(modelIdentifier, db));
                }

                if (sequenceLayerRequired) {
                    elements.addAll(fetchSequenceLayer(modelIdentifier, db));
                }

                if (abstractLayerRequired && concreteLayerRequired) {
                    elements.addAll(fetchAbstractConcreteConnectors(modelIdentifier, db));
                }

                if (concreteLayerRequired && sequenceLayerRequired) {
                    fetchConcreteSequenceConnectors(modelIdentifier, db);
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
        File output = new File(outputDir + filename);
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

    private List<Element> fetchAbstractLayer(String modelIdentifier, ODatabaseSession db) {
        ArrayList<Element> elements = new ArrayList<>();

        // abstract states
        String stmt = "SELECT FROM AbstractState WHERE modelIdentifier = :identifier";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "AbstractState"));

        // abstract actions
        stmt = "SELECT FROM AbstractAction WHERE modelIdentifier = :identifier";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "AbstractAction"));

        // Black hole class
        stmt = "SELECT FROM (TRAVERSE out() FROM  (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'BlackHole'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "BlackHole"));


        // unvisited abstract actions
        stmt = "SELECT FROM UnvisitedAbstractAction WHERE modelIdentifier = :identifier";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "UnvisitedAbstractAction"));

        return elements;
    }

    private List<Element> fetchConcreteLayer(String modelIdentifier, ODatabaseSession db) {
        ArrayList<Element> elements = new ArrayList<>();

        // concrete states
        String stmt = "SELECT FROM (TRAVERSE in() FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "ConcreteState"));

        // concrete actions
        stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').outE('ConcreteAction') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'ConcreteAction'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "ConcreteAction"));

        return elements;
    }

    private List<Element> fetchSequenceLayer(String modelIdentifier, ODatabaseSession db) {
        ArrayList<Element> elements = new ArrayList<>();

        // test sequence
        String stmt = "SELECT FROM TestSequence WHERE modelIdentifier = :identifier";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "TestSequence"));

        // sequence nodes
        stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').in('Accessed') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'SequenceNode'";
        resultSet = db.query(stmt, params);
        elements.addAll(fetchNodes(resultSet, "SequenceNode"));

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

    private List<Element> fetchConcreteSequenceConnectors(String modelIdentifier, ODatabaseSession db) {
        ArrayList<Element> elements = new ArrayList<>();

        // accessed relation
        String stmt = "SELECT FROM (TRAVERSE in('isAbstractedBy').inE('Accessed') FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'Accessed'";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(stmt, params);
        elements.addAll(fetchEdges(resultSet, "Accessed"));

        return elements;
    }

    private ArrayList<Element> fetchNodes(OResultSet resultSet, String className) {
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
                        processScreenShot(stateVertex.getProperty("screenshot"), "n" + formatId(stateVertex.getIdentity().toString()));
                        continue;
                    }
                    jsonVertex.addProperty(propertyName, stateVertex.getProperty(propertyName).toString());
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

    public void processScreenShot(ORecordBytes recordBytes, String identifier) {
        // save the file to disk
        File screenshotFile = new File(outputDir + identifier + ".png");
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

    private String formatId(String id) {
        if (id.indexOf("#") != 0) return id; // not an orientdb id
        id = id.replaceAll("[#]", "");
        return id.replaceAll("[:]", "_");
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
}

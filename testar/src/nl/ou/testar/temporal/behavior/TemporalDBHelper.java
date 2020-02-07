package nl.ou.testar.temporal.behavior;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.config.OContextConfiguration;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.OrientDBConfigBuilder;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.record.impl.OVertexDocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;
import nl.ou.testar.StateModel.Analysis.Representation.TestSequence;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.temporal.structure.*;
import nl.ou.testar.temporal.util.*;
import org.fruit.alayer.Tags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

//import org.apache.tinkerpop.gremlin.structure.Graph;
//import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;

public  class TemporalDBHelper {
    private Config dbConfig;
    private OrientDB orientDB;
    private ODatabaseSession db;
    private APSelectorManager apSelectorManager;  // used by computeProps



    public TemporalDBHelper() {
    }

    public TemporalDBHelper(final Settings settings) {
        dbConfig = makeConfig(settings);
        initOrientDb();
    }

    private void initOrientDb() {
        String connectionString = dbConfig.getConnectionType() + ":/" + (dbConfig.getConnectionType().equals("remote") ?
                dbConfig.getServer() : dbConfig.getDatabaseDirectory());// +"/";
        OLogManager logmanager=OLogManager.instance();
        logmanager.setConsoleLevel("WARNING");
        orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
        // orientDB = new OrientDB("plocal:C:\\orientdb-tp3-3.0.18\\databases", OrientDBConfig.defaultConfig());

    }

    public void setDb(ODatabaseSession db) {
        this.db = db;
    }
    public void setApSelectorManager(APSelectorManager apSelectorManager) {
        this.apSelectorManager = apSelectorManager;

    }

    private Config makeConfig(final Settings settings) {
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

    public void dbClose() {
        if (!db.isClosed())  {
            db.close();
            orientDB.close();}
    }
    public void dbReopen() {
        if (db==null ||db.isClosed()) {
            initOrientDb();
            db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword());
        }
        db.activateOnCurrentThread();


    }

    /**
     * This method fetches a list of the abstract state models in the current OrientDB data store.
     *
     * @return
     */
    public List<AbstractStateModel> fetchAbstractModels() {
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
                Set<String> abstractionAttributes = (Set)getConvertedValue(OType.EMBEDDEDSET, modelVertex.getProperty("abstractionAttributes")); //@todo set or list?
                // fetch the test sequences
                List<TestSequence> sequenceList = new ArrayList<>(); // css, trace are fetched in our own way.just for reuse of AbstractStateModel

                AbstractStateModel abstractStateModel = new AbstractStateModel(
                        applicationName, applicationVersion, modelIdentifier, abstractionAttributes, sequenceList
                );
                abstractStateModels.add(abstractStateModel);
            }
        }
        resultSet.close();

        return abstractStateModels;
    }

    public AbstractStateModel selectAbstractStateModel(String ApplicationName, String ApplicationVersion) {

        List<AbstractStateModel> abstractStateModels = fetchAbstractModels();
        AbstractStateModel abstractStateModel = null;
        if (abstractStateModels.size() == 0) {
            System.out.println("ERROR: No Models in the graph database " + db.toString());
        } else {
            for (AbstractStateModel absModel:abstractStateModels
            ) {
                if (absModel.getApplicationName().equals(ApplicationName) && absModel.getApplicationVersion().equals(ApplicationVersion)){
                    abstractStateModel=absModel;
                    break;
                }
            }
            if (abstractStateModel==null){
                System.out.println("ERROR: Model with App. name : "+ApplicationName+" and version : "+ApplicationVersion+" was not found in the graph database " + db.getName());
            }
        }
        return abstractStateModel;
    }
    public AbstractStateModel selectAbstractStateModelByModelId(String ModelIdentifier) {
        dbReopen();
        List<AbstractStateModel> abstractStateModels = fetchAbstractModels();
        AbstractStateModel abstractStateModel = null;
        if (abstractStateModels.size() == 0) {
            System.out.println("ERROR: No Models in the graph database " + db.toString());
        } else {
            for (AbstractStateModel absModel:abstractStateModels
            ) {
                if (absModel.getModelIdentifier().equals(ModelIdentifier)){
                    abstractStateModel=absModel;
                    break;
                }
            }
            if (abstractStateModel==null){
                System.out.println("ERROR: Model with identifier : "+ModelIdentifier+" was not found in the graph database " + db.getName());
            }
        }
        dbClose();
        return abstractStateModel;
    }


    public OResultSet getConcreteStatesFromOrientDb(AbstractStateModel abstractStateModel){
        String stmt;
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", abstractStateModel.getModelIdentifier());
        // navigate from abstractstate to apply the filter.
        // stmt =  "SELECT FROM (TRAVERSE in() FROM (SELECT FROM AbstractState WHERE abstractionLevelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";
        stmt = "SELECT FROM (TRAVERSE in() FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";

        OResultSet resultSet = db.query(stmt, params);  //OResultSet resultSet = db.query(stmt); @todo refactor db to dbhelper
        resultSet.close();
        return resultSet;
    }
    public Set<String> getWidgetPropositions(String state, List<String> abstractionAttributes) {


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
                List<WidgetFilter> passedWidgetFilters=getPassingWidgetFilters(stateVertex,abstractionAttributes);
                for (String propertyName : stateVertex.getPropertyNames()) {
                    computeProps( propertyName, stateVertex, propositions,  passedWidgetFilters,true, false);
                }
            }
        }
        resultSet.close();
        return propositions;
    }
    private List<WidgetFilter> getPassingWidgetFilters(OElement graphElement,  List<String> abstractionAttributes){
        List<WidgetFilter> passedWidgetFilters;
        Map<String,String> attribmap=new HashMap<>();
        passedWidgetFilters=null;
        for (String attrib:abstractionAttributes
        ) {
            Object prop = graphElement.getProperty(attrib);
            if (prop == null) {
                System.out.println("Abstraction attribute: " + attrib + " not part of graphelement: " + graphElement.toString());
                //passedWidgetFilters=null;
                break;
            } else {
                attribmap.put(attrib, graphElement.getProperty(attrib));
            }
        }
//        System.out.println("DEBUG: checking widgetfilters for graphelement: " + graphElement.getIdentity().toString()+"     time: "+System.nanoTime());
        passedWidgetFilters = apSelectorManager.passWidgetFilters(attribmap);//
//        System.out.println("DEBUG: check done      time: "+System.nanoTime());

        return passedWidgetFilters;
    }

    public List<TransitionEncoding> getTransitions(String state) {
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
                trenc.setTransition(actionEdge.getIdentity().toString());
                trenc.setTargetState(target.getIdentity().toString());
                Set<String> propositions = new LinkedHashSet<>();
                for (String propertyName : actionEdge.getPropertyNames()) {
                    computeProps( propertyName, actionEdge, propositions, null,false, true);
                }
                trenc.setTransitionAPs(propositions);

                trenclist.add(trenc);
            }


        }
        resultSet.close();
        return trenclist;
    }
    //*********************************

    //get a list of edges in the trace:
    // SELECT FROM (TRAVERSE out('SequenceStep'),outE('SequenceStep') FROM #81:0 ) WHERE @class = 'SequenceStep' ORDER BY stepId ASC  // order by is optional
    //get a list of nodes in the trace. index[0] is the initial node !!
    // SELECT FROM (TRAVERSE out('SequenceStep'),outE('SequenceStep') FROM #81:0 ) WHERE @class = 'SequenceNode' ORDER BY stepId ASC   //order is mandatory?
    //SELECT FROM (TRAVERSE out('SequenceStep'),out('Accessed') FROM #81:0 ) WHERE @class ='ConcreteState'  // works
    // get firstnode : initalnode
    //SELECT FROM (TRAVERSE out('FirstNode') FROM (SELECT FROM TestSequence WHERE sequenceId = '15h4sa4152783694157'))  WHERE @class = 'SequenceNode'


    public List<TemporalTrace> fetchTraces(String modelIdentifier) {
        List<TemporalTrace> traces = new ArrayList<>();

        //String sequenceStmt = "SELECT FROM TestSequence WHERE abstractionLevelIdentifier = :identifier ORDER BY startDateTime ASC";
        String sequenceStmt = "SELECT FROM TestSequence WHERE modelIdentifier = :identifier ORDER BY startDateTime ASC";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", modelIdentifier);
        OResultSet resultSet = db.query(sequenceStmt, params);
        while (resultSet.hasNext()) {
            OResult sequenceResult = resultSet.next();
            TemporalTrace trace = new TemporalTrace();
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
                trace.setSequenceID(sequenceVertex.getProperty("sequenceId").toString());
                trace.setTransitionCount((long) nrOfNodes);
                trace.setRunDate(sequenceVertex.getProperty("startDateTime").toString());
                trace.setTestSequenceNode(sequenceVertex.getIdentity().toString());
                trace.setTraceEvents(fetchTraceEvents(trace));
                traces.add(trace);
                nodeResultSet.close();
            }

        }
        resultSet.close();
        return traces;
    }

    private List<TemporalTraceEvent> fetchTraceEvents(TemporalTrace trace) {
        List<TemporalTraceEvent> traceEvents = new ArrayList<>();
        String firstSequenceNode = getFirstSequenceNode(trace);
        List<String> ConcreteStates = new ArrayList<>();
        List<String> ConcreteActions = new ArrayList<>();
        String stmt;
        Map<String, Object> params = new HashMap<>();
        OResultSet resultSet;
        params.put("identifier", firstSequenceNode);
        //stmt = "SELECT @rid.asString() FROM (TRAVERSE out('SequenceStep'),out('Accessed') FROM @rid = :identifier ) WHERE @class ='ConcreteState'";// not yet :-)
        //stmt = "SELECT FROM (TRAVERSE out('SequenceStep'),out('Accessed') FROM (SELECT FROM SequenceNode WHERE @rid = :identifier) ) WHERE @class ='ConcreteState'";
        stmt = "SELECT FROM (TRAVERSE out('SequenceStep'),outE('SequenceStep') FROM (SELECT FROM SequenceNode WHERE @rid = :identifier) ) WHERE @class = 'SequenceNode'";
        //(SELECT FROM SequenceStep WHERE @rid = :identifier??
        // inner query is needed as the single query collapses multiple refeences to concretestate
        resultSet = db.query(stmt, params);
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting an element
            if (result.isElement()) {
                Optional<OElement> optele = result.getElement();
                if (!optele.isPresent()) continue;
                OElement ele = optele.get();
                params.put("identifier", ele.getIdentity().toString());
                stmt = "SELECT FROM (TRAVERSE out('Accessed') FROM (SELECT FROM SequenceNode WHERE @rid = :identifier) ) WHERE @class ='ConcreteState'";
                OResultSet subresultSet = db.query(stmt, params);
                while (subresultSet.hasNext()) {
                    OResult subresult = subresultSet.next();
                    // we're expecting an element
                    if (subresult.isElement()) {
                        Optional<OElement> suboptele = subresult.getElement();
                        if (!suboptele.isPresent()) continue;
                        OElement subele = suboptele.get();
                        ConcreteStates.add(subele.getIdentity().toString());
                    }
                }
                subresultSet.close();
            }
        }
        resultSet.close();

        stmt = "SELECT FROM (TRAVERSE out('SequenceStep'),outE('SequenceStep') FROM (SELECT FROM SequenceNode WHERE @rid = :identifier) ) WHERE @class = 'SequenceStep'";
        //concreteActionId needs to be updated css 20190721
        params.put("identifier", firstSequenceNode);
        resultSet = db.query(stmt, params);

        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting an element
            if (result.isElement()) {
                Optional<OElement> optele = result.getElement();
                if (!optele.isPresent()) continue;
                OElement ele = optele.get();
                params.put("identifier", ele.getProperty("concreteActionUid").toString());
                stmt = "SELECT FROM ConcreteAction WHERE uid = :identifier";    // LIMIT 1";
                //LIMIT 1 was a debug action css 20190722. actionId is NOT unique !!!!
                OResultSet subresultSet = db.query(stmt, params);
                while (subresultSet.hasNext()) {
                    OResult subresult = subresultSet.next();
                    // we're expecting an element
                    if (subresult.isElement()) {
                        Optional<OElement> suboptele = subresult.getElement();
                        if (!suboptele.isPresent()) continue;
                        OElement subele = suboptele.get();
                        ConcreteActions.add(subele.getIdentity().toString());
                    }
                }
                subresultSet.close();
            }
        }
        resultSet.close();


        if ((ConcreteStates.size() - ConcreteActions.size()) != 1) {
            System.out.println("debug: trace count not matching nodes and edges for sequence:  " + trace.getSequenceID());
            System.out.println("debug:  ConcreteStates.size(): " + ConcreteStates.size());
            System.out.println("debug:  ConcreteAction.size(): " + ConcreteActions.size());
        } else {
            int i = 0;
            for (String cs : ConcreteStates
            ) {
                TemporalTraceEvent traceEvent = new TemporalTraceEvent();
                traceEvent.setState(cs);
                String trans;

                if ((i + 1) == ConcreteStates.size()) {
                    trans = ""; // we end with a state :-)
                } else {
                    trans = ConcreteActions.get(i);
                }
                i++;
                traceEvent.setTransition(trans);
                traceEvents.add(traceEvent);
            }
        }


        return traceEvents;
    }

    private String getFirstSequenceNode(TemporalTrace trace) {
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", trace.getSequenceID());
        String stmt = "SELECT FROM (TRAVERSE out('FirstNode') FROM (SELECT FROM TestSequence WHERE sequenceId = :identifier))";

        //or:
        //params.put("identifier", trace.getTestSequenceNode();
        //String stmt = "SELECT FROM (TRAVERSE out('FirstNode') FROM @rid = :identifier)";

        OResultSet resultSet = db.query(stmt, params);
        String firstNode = "";
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()) {
                Optional<OElement> ele = result.getElement();
                if (!ele.isPresent()) continue;
                OElement firstsequenceNode = ele.get();
                firstNode = firstsequenceNode.getIdentity().toString();
            }
        }
        resultSet.close();
        return firstNode;
    }




    /**
     * This method saves screenshots to disk.
     *
     * @param recordBytes
     * @param identifier
     */
    private void processScreenShot(ORecordBytes recordBytes, String identifier, String modelIdentifier, String outputDir) {
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

    public  void computeProps(String propertyName, OElement graphElement, Set<String> globalPropositions, List<WidgetFilter> passedWidgetFilters, boolean isWidget, boolean isEdge) {
        computeProps(propertyName, graphElement, globalPropositions, passedWidgetFilters,isWidget, isEdge, false);
    }


    public  void computeProps(String propertyName, OElement graphElement, Set<String> globalPropositions, List<WidgetFilter> passedWidgetFilters, boolean isWidget, boolean isEdge, boolean isDeadState) {
        // isdeadstate is not used
        StringBuilder apkey = new StringBuilder();
        //List<WidgetFilter> passedWidgetFilters;

        //compose APkey
        for (String k : apSelectorManager.getAPKey()
        ) {
            Object prop = graphElement.getProperty(k);
            if (prop == null) {
                String fallback;
                Object concreteprop;
                if (isWidget) {
                    concreteprop = graphElement.getProperty(Tags.ConcreteID.name()); // must exists for state/widget
                } else
                    concreteprop = graphElement.getProperty("actionId"); // must exists for concrete edge/action
                if (concreteprop == null) {
                    fallback = "undefined";
                } else {
                    fallback = concreteprop.toString();
                }
                apkey.append(fallback);
                apkey.append(apSelectorManager.getApEncodingSeparator());
            } else {
                apkey.append(prop);
                apkey.append(apSelectorManager.getApEncodingSeparator());
            }
        }
        if (isWidget) {

            if (passedWidgetFilters != null && passedWidgetFilters.size() > 0) {
                for (WidgetFilter wf : passedWidgetFilters) // add the filter specific elected attributes and expressions
                {// candidate for refactoring as this requires a double iteration of widget filter
                    //globalPropositions.addAll(wf.getAPsOfAttribute(apkey.toString(), propertyName, graphElement.getProperty(propertyName).toString()));
                    globalPropositions.addAll(wf.getWidgetSelectorPart().getAPsOfAttribute(apkey.toString(), propertyName, graphElement.getProperty(propertyName).toString()));

                }
            }
        }
        if (!isWidget && isEdge) {
            globalPropositions.addAll(apSelectorManager.getTransitionFilter().getAPsOfAttribute(apkey.toString(), propertyName, graphElement.getProperty(propertyName).toString()));
        }
        if (!isWidget && !isEdge) {
            globalPropositions.addAll(apSelectorManager.getStateFilter().getAPsOfAttribute(apkey.toString(), propertyName, graphElement.getProperty(propertyName).toString()));
        }

    }

    public boolean saveToGraphMLFile(AbstractStateModel abstractStateModel,String file,boolean excludeWidget) {
        //init
        dbReopen();
        String graphXMLID=dbConfig.getDatabase();
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", abstractStateModel.getModelIdentifier());
        //!!!!!!!!!!!!!!!!!!!!!!get nodes , then get edges. this is required for postprocessing a graphml by python package networkx.
        List<String> stmtlist = new ArrayList<>();
        String excludeWidgets="";
        if (excludeWidget)
        {excludeWidgets = "WHERE NOT(@class= 'Widget' OR @class = 'isChildOf')";}  //  relation isChildOf is exclusively for widgets !
        stmtlist.add("SELECT FROM AbstractStateModel WHERE  modelIdentifier = :identifier"); // select abstractstatemodel , this is an unconnected node
        // the "both()" in the next stmt is needed to invoke recursion.
        // apparently , the next result set contains first a list of all nodes, then of all edge: good !
        stmtlist.add("SELECT  FROM (TRAVERSE both(), bothE() FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) " + excludeWidgets + "   ");


        Set<GraphML_DocKey> docnodekeys = new HashSet<>();
        Set<GraphML_DocKey> docedgekeys = new HashSet<>();
        List<GraphML_DocNode> nodes = new ArrayList<>();
        List<GraphML_DocEdge> edges = new ArrayList<>();

        for (String stm : stmtlist
        ) {

            OResultSet resultSet = db.query(stm, params);
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
                        if (!rawattributeType.equals("boolean") &&
                                !rawattributeType.equals("long") &&
                                !rawattributeType.equals("double") &&
                                !rawattributeType.equals("string")) {
                            attributeType = "string"; // unknown types are converted to string
                        } else
                            attributeType = rawattributeType;

                        if (result.isEdge() && (propertyName.startsWith("in") || propertyName.startsWith("out"))) {
                            // these are probably edge indicators. Ignore
                            continue;
                        }
                        if (result.isVertex() && (propertyName.contains("in_") || propertyName.contains("out_"))) {
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
            resultSet.close();

        }
        dbClose();
        GraphML_DocGraph graph = new GraphML_DocGraph(graphXMLID, nodes, edges);
        Set<GraphML_DocKey> tempset = new LinkedHashSet<GraphML_DocKey>();
        docnodekeys.add(new GraphML_DocKey("labelV", "node", "labelV", "string"));
        docedgekeys.add(new GraphML_DocKey("labelE", "edge", "labelE", "string"));
        tempset.addAll(docnodekeys);
        tempset.addAll(docedgekeys);
        GraphML_DocRoot root = new GraphML_DocRoot(tempset, graph);
        XMLHandler.save(root, file);

        return true;
    }


    ;

}

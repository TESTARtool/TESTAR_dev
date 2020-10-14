package nl.ou.testar.temporal.control;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.OVertexDocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import es.upv.staq.testar.CodingManager;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;
import nl.ou.testar.StateModel.Analysis.Representation.TestSequence;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.temporal.util.foundation.PairBean;
import nl.ou.testar.temporal.util.foundation.TagBean;
import nl.ou.testar.temporal.graphml.GraphML_DocEdge;
import nl.ou.testar.temporal.graphml.GraphML_DocEleProperty;
import nl.ou.testar.temporal.graphml.GraphML_DocGraph;
import nl.ou.testar.temporal.graphml.GraphML_DocKey;
import nl.ou.testar.temporal.graphml.GraphML_DocNode;
import nl.ou.testar.temporal.graphml.GraphML_DocRoot;
import nl.ou.testar.temporal.util.io.SimpleLog;
import nl.ou.testar.temporal.util.io.XMLHandler;
import nl.ou.testar.temporal.model.StateEncoding;
import nl.ou.testar.temporal.model.TemporalModel;
import nl.ou.testar.temporal.model.TemporalTrace;
import nl.ou.testar.temporal.model.TemporalTraceEvent;
import nl.ou.testar.temporal.model.TransitionEncoding;
import nl.ou.testar.temporal.model.proposition.PropositionConstants;
import nl.ou.testar.temporal.model.proposition.PropositionFilter;
import nl.ou.testar.temporal.model.proposition.PropositionFilterType;
import nl.ou.testar.temporal.model.proposition.PropositionManager;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.fruit.Pair;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.util.*;
import java.util.stream.Collectors;

import static nl.ou.testar.temporal.util.io.OShelper.prettyCurrentTime;

public  class TemporalDBManager {
    private Config dbConfig;
    private boolean dbEnabled;
    private OrientDB orientDB;
    private ODatabaseSession db;
    private PropositionManager propositionManager;  // used by computeProps
    private final SimpleLog simpleLog;


    public TemporalDBManager( SimpleLog simpleLog) {
        this.simpleLog=simpleLog;
    }
    public void updateSettings(final Settings settings) {
        dbConfig = makeConfig(settings);
        dbEnabled=settings.get(ConfigTags.StateModelEnabled);
        //initOrientDb();
    }

    private boolean initOrientDb() {
        boolean success ;
        if (dbEnabled) {
            String connectionString = dbConfig.getConnectionType()  + ":/" +
                    (dbConfig.getConnectionType().equals("remote") ?
                     dbConfig.getServer() : dbConfig.getDatabaseDirectory());// +"/";
            OLogManager logmanager = OLogManager.instance();
            logmanager.setConsoleLevel("WARNING");
            orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
            success = true;
            // orientDB = new OrientDB("plocal:C:\\orientdb-tp3-3.0.18\\databases", OrientDBConfig.defaultConfig());
        }else{
            simpleLog.append(prettyCurrentTime() + " | " + "Error : State Model not enabled");
            throw new DBManagerException("Error : State Model not enabled");
        }
        return success;
    }

    public void setPropositionManager(PropositionManager propositionManager) {
        this.propositionManager = propositionManager;

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

    private void dbClose() {
        if (!db.isClosed())  {
            db.close();
            orientDB.close(); }
    }
    private boolean dbReopen() {
        boolean success=false;
        if (db==null ||db.isClosed()) {
            success = initOrientDb();
            if (success) {
                try {
                    db = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword());
                } catch (Exception e) {
                    success = false;
                    simpleLog.append(prettyCurrentTime() + " | " + "Error : Cannot open State Model database (check settings)");
                    throw new DBManagerException("Error : Cannot open State Model database (check settings)",e );
                }
            }
        }
        db.activateOnCurrentThread();
        return success;
    }
    public String getDatabase(){
        return dbConfig.getDatabase();
    }

    /**
     * This method fetches a list of the abstract state models in the current OrientDB data store.
     *
     * @return List of AbstractStateModels
     */



    @SuppressWarnings("unchecked")
    public List<AbstractStateModel> fetchAbstractModels() {
        dbReopen();
        ArrayList<AbstractStateModel> abstractStateModels = new ArrayList<>();
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
                Set<String> abstractionAttributes = (Set<String>) getConvertedValue(OType.EMBEDDEDSET, modelVertex.getProperty("abstractionAttributes")); //@ set or list?
                // fetch the test sequences
                List<TestSequence> sequenceList = new ArrayList<>(); // css, trace are fetched in our own way.just for reuse of AbstractStateModel

                AbstractStateModel abstractStateModel = new AbstractStateModel(
                        applicationName, applicationVersion, modelIdentifier, abstractionAttributes, sequenceList
                );
                abstractStateModels.add(abstractStateModel);
            }
        }
        resultSet.close();
        dbClose();
        return abstractStateModels;
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


    public Pair<List<OResult>,Integer> getConcreteStatesFromOrientDb(AbstractStateModel abstractStateModel){
        // demanding query: combining the result-set and the count into one call
        String stmt;
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", abstractStateModel.getModelIdentifier());
        // navigate from abstractstate to apply the filter.
        stmt = "SELECT FROM (TRAVERSE in() FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";

        OResultSet resultSet = db.query(stmt, params);
        List<OResult> resultList=resultSet.stream().collect(Collectors.toList());
        int  stateCount = (int)  resultList.size();
        resultSet.close();


        Pair<List<OResult>, Integer> oResultSetIntegerPair = new Pair<>(resultList, stateCount);
        return oResultSetIntegerPair;
    }

    public int getConcreteStateCountFromOrientDb(AbstractStateModel abstractStateModel){
        String stmt;
        int stateCount=0;
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", abstractStateModel.getModelIdentifier());
        // navigate from abstractstate to apply the filter.
        stmt = "SELECT Count(*) FROM (TRAVERSE in() FROM (SELECT FROM AbstractState WHERE modelIdentifier = :identifier)) WHERE @class = 'ConcreteState'";

        OResultSet resultSet = db.query(stmt, params);
        if (resultSet.hasNext()) {
            OResult result = resultSet.next();

            stateCount= (int)(long)result.getProperty("Count(*)"); //from Long to long to int
        }
        resultSet.close();
        return stateCount;
    }

    public PairBean<Set<String>,Integer> getWidgetPropositions(String state, List<String> abstractionAttributes, Map<String,Set<String>> widgetKeyCollisionMap) {
        // concrete widgets
        String stmt = "SELECT FROM (TRAVERSE in('isChildOf') FROM (SELECT FROM ConcreteState WHERE @rid = :state)) WHERE @class = 'Widget'";
        Map<String, Object> params = new HashMap<>();
        params.put("state", state);
        OResultSet resultSet = db.query(stmt, params);
        int wCount=0;
        //***
        Set<String> propositions = new LinkedHashSet<>();
        while (resultSet.hasNext()) {
            OResult result = resultSet.next();
            // we're expecting a vertex
            if (result.isVertex()) {
                Optional<OVertex> op = result.getVertex();
                if (!op.isPresent()) continue;
                OVertex widgetVertex = op.get();
                wCount++;
                //List<PropositionFilter> passedPropositionFilters = getPassingPassingFilters(widgetVertex,abstractionAttributes);
                //List<PropositionFilter> passedPropositionFilters = getPassingFilters(PropositionFilterType.WIDGET,widgetVertex);


                Map<String,String> attribmap=new HashMap<>();
                for (String attrib:widgetVertex.getPropertyNames()) {
                    attribmap.put(attrib, widgetVertex.getProperty(attrib));
                }
                List<PropositionFilter> passedPropositionFilters = propositionManager.setPassingFilters(PropositionFilterType.WIDGET,attribmap);


                for (String propertyName : widgetVertex.getPropertyNames()) {
                    Set<String> props= computeAtomicPropositions( abstractionAttributes,propertyName, widgetVertex, passedPropositionFilters, widgetKeyCollisionMap);
                    propositions.addAll(props);
                }
            }
        }
        PairBean<Set<String>,Integer> pb= new PairBean<>();
        pb.setLeft(propositions);
        pb.setRight(wCount);
        resultSet.close();
        return pb;
    }


    private List<TransitionEncoding> getTransitions(String state,List<String> abstractionAttributes) {
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
                //OVertexDocument source = actionEdge.getProperty("out");
                OVertexDocument target = actionEdge.getProperty("in");

                TransitionEncoding trenc = new TransitionEncoding();
                trenc.setTransition(actionEdge.getIdentity().toString());
                trenc.setTargetState(target.getIdentity().toString());
                Set<String> propositions = new LinkedHashSet<>();
                // List<PropositionFilter> passedPropositionFilters = XetPassingPassingFilters(PropositionFilterType.TRANSITION,actionEdge);

                Map<String,String> attribmap=new HashMap<>();
                for (String attrib:actionEdge.getPropertyNames()) {
                    attribmap.put(attrib, actionEdge.getProperty(attrib));
                }
                List<PropositionFilter> passedPropositionFilters = propositionManager.setPassingFilters(PropositionFilterType.TRANSITION,attribmap);

                for (String propertyName : actionEdge.getPropertyNames()) {
                     Set<String> props=computeAtomicPropositions(abstractionAttributes ,propertyName, actionEdge, passedPropositionFilters,new HashMap<>());
                    propositions.addAll(props);
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


    public List<TemporalTrace> fetchTraces(String modelIdentifier,boolean logDetailsInModelFile) {
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
                trace.setSequenceID(sequenceVertex.getProperty("sequenceId").toString());
                trace.setRunDate(sequenceVertex.getProperty("startDateTime").toString());
                trace.setTestSequenceNode(sequenceVertex.getIdentity().toString());
                if(logDetailsInModelFile) {
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
                            nrOfNodes--;//minus one
                        }
                    }
                    nodeResultSet.close();
                    trace.setTransitionCount((long) nrOfNodes);
                }

                trace.setTraceEvents(fetchTraceEvents(trace,!logDetailsInModelFile));
                traces.add(trace);

            }

        }
        resultSet.close();
        return traces;
    }

    private List<TemporalTraceEvent> fetchTraceEvents(TemporalTrace trace,boolean initialNodeOnly) {
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
        if(initialNodeOnly){
           // stmt = "SELECT  FROM  SequenceNode WHERE @rid = :identifier AND nodeNr = 1"; //works as well
           stmt = "SELECT FROM (TRAVERSE out('SequenceStep'),outE('SequenceStep') FROM " +
                   "(SELECT FROM SequenceNode WHERE @rid = :identifier) MAXDEPTH 0) WHERE @class = 'SequenceNode'";
        }
        else{
            stmt = "SELECT FROM (TRAVERSE out('SequenceStep'),outE('SequenceStep') FROM " +
                    "(SELECT FROM SequenceNode WHERE @rid = :identifier) ) WHERE @class = 'SequenceNode'";
        }

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

        if(initialNodeOnly){
            stmt = "SELECT FROM (TRAVERSE out('SequenceStep'),outE('SequenceStep') FROM (SELECT FROM SequenceNode " +
                    "WHERE @rid = :identifier) MAXDEPTH 0)  WHERE @class = 'SequenceStep'";
        }
        else{
            stmt = "SELECT FROM (TRAVERSE out('SequenceStep'),outE('SequenceStep') FROM (SELECT FROM SequenceNode " +
                    "WHERE @rid = :identifier) ) WHERE @class = 'SequenceStep'";
        }
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
     * Helper method that converts an object value based on a specified OrientDB data type.
     *
     * @param oType orientDB data type
     * @param valueToConvert raw value to apply to converion on
     * @return converted object
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

    private Set<String> computeAtomicPropositions(List<String> keyingAttributes, String propertyName, OElement graphElement,
                                                  List<PropositionFilter> passedPropositionFilters, Map<String, Set<String>> widgetKeyCollisionMap)  {
        Set<String> props=new HashSet<>();
        if (passedPropositionFilters != null && passedPropositionFilters.size() > 0) {
            String key = getPropositionKey(keyingAttributes,graphElement);
            for (PropositionFilter wf : passedPropositionFilters) // add the filter-specific selected attributes and expressions
            {// candidate for refactoring as this requires a double iteration over filters
                props =wf.getPropositionsOfAttribute(key, propertyName, graphElement.getProperty(propertyName).toString());
            }
            if (!props.isEmpty() && (graphElement  instanceof OVertex)){
               String concreteID= graphElement.getProperty(CodingManager.CONCRETE_ID_CUSTOM).toString();
                Set<String>  widgetids = widgetKeyCollisionMap.getOrDefault(key,new HashSet<>());
                widgetids.add(concreteID);
                widgetKeyCollisionMap.put(key,widgetids);
            }
        }
        return props;
    }

    private String getPropositionKey(List<String> keyingAttributes,OElement graphElement){
        StringBuilder apkey = new StringBuilder();
        for (String k : keyingAttributes
        ) {
            Object prop = graphElement.getProperty(k);
            if (prop == null) {
                apkey.append("undefined");
            } else {
                apkey.append(prop);
            }
            apkey.append(propositionManager.getPropositionSubKeySeparator());
        }
//        if(apkey.toString().contains("UIAMenuItem_||_Help_||_    Application")){
//            System.out.println(apkey.toString()); // debug
//        }
        return apkey.toString();
    }

    public boolean saveToGraphMLFile(AbstractStateModel abstractStateModel,String file,boolean excludeWidget,boolean zip ) {
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
                    assert graphElement.getSchemaType().isPresent(); // Optional requires a check
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
        Set<GraphML_DocKey> tempset = new LinkedHashSet<>();
        docnodekeys.add(new GraphML_DocKey("labelV", "node", "labelV", "string"));
        docedgekeys.add(new GraphML_DocKey("labelE", "edge", "labelE", "string"));
        tempset.addAll(docnodekeys);
        tempset.addAll(docedgekeys);
        GraphML_DocRoot root = new GraphML_DocRoot(tempset, graph);
        XMLHandler.save(root, file,zip);
        return true;
    }
    public String pingDB() {

        StringBuilder sb = new StringBuilder();
        List<AbstractStateModel> models = fetchAbstractModels();
        sb.append("APPLICATIONNAME").append(";APPLICATIONVERSION").append(";MODELIDENTIFIER").append(";ABSTRACTIONATTRIBUTES").append("\n");
        if (models.isEmpty()) {
            sb.append("model count: 0\n");
        } else {

            for (AbstractStateModel abs : models
            ) {
                sb.append(abs.getApplicationName()).append(";").append(abs.getApplicationVersion()).append(";")
                  .append(abs.getModelIdentifier()).append(";").append(abs.getAbstractionAttributes()).append("\n");
            }
        }
        return sb.toString();
    }

    public void computeTemporalModel(AbstractStateModel abstractStateModel, TemporalModel tModel, boolean instrumentTerminalState,boolean logDetailsInModelFile) {
        long start_time = System.currentTimeMillis();
        int runningWcount = 0;
        int stateCount = 0;
        int totalStates;
        int chunks = 10;
        Map<String,String> statetreeKeyCollisionMap= new HashMap<>();
        dbReopen();
        Pair<List<OResult>,Integer> resultListIntegerPair= getConcreteStatesFromOrientDb(abstractStateModel);
        List<OResult> concreteStateList = resultListIntegerPair.left();
        totalStates = resultListIntegerPair.right();
        Map<String, Integer> commentWidgetDistri = new HashMap<>();
        MultiValuedMap<String, String> logNonDeterministicTransitions = new HashSetValuedHashMap<>();
        List<String> logTerminalStates = new ArrayList<>();
        boolean firstTerminalState = true;
        StateEncoding terminalStateEnc;
        terminalStateEnc = new StateEncoding("#" + PropositionConstants.SETTING.terminalProposition);
        simpleLog.append(prettyCurrentTime() + " | " + "States processed: " + 0 + "%");
        //while (resultSet.hasNext())
        for (OResult result : concreteStateList)
         {
            //OResult result = resultSet.next();
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
                    logTerminalStates.add(stateVertex.getIdentity().toString());
                    //tModel.addLog("State: " + stateVertex.getIdentity().toString() + " is terminal.");
                    if (instrumentTerminalState && firstTerminalState) {
                        //add stateenc for 'Dead', inclusive dead transition selfloop;
                        //terminalStateEnc = new StateEncoding("#" + TemporalModel.getDeadProposition());
                        Set<String> terminalStatePropositions = new LinkedHashSet<>();
                        //terminalStatePropositions.add("dead");   //redundant on transition based automatons
                        terminalStateEnc.setStateAPs(terminalStatePropositions);
                        TransitionEncoding deadTrenc = new TransitionEncoding();
                        deadTrenc.setTransition(PropositionConstants.SETTING.terminalProposition + "_selfloop");
                        deadTrenc.setTargetState(terminalStateEnc.getState());//"#" + TemporalModel.getDeadProposition());
                        Set<String> deadTransitionPropositions = new LinkedHashSet<>();
                        deadTransitionPropositions.add(PropositionConstants.SETTING.terminalProposition);
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

                Map<String, String> attribmap = new HashMap<>();
                for (String attrib : stateVertex.getPropertyNames()) {
                    attribmap.put(attrib, stateVertex.getProperty(attrib));
                }
                List<PropositionFilter> passedPropositionFilters = propositionManager.setPassingFilters(PropositionFilterType.STATE, attribmap);
                Map<String,Set<String>> widgetKeyCollisionMap= new HashMap<>();
                for (String propertyName : stateVertex.getPropertyNames()) {
                    Set<String> props= computeAtomicPropositions(tModel.getAtomicPropositionKeying(), propertyName, stateVertex, passedPropositionFilters,widgetKeyCollisionMap);
                    propositions.addAll(props);
                }

                PairBean<Set<String>, Integer> pb = getWidgetPropositions(senc.getState(), tModel.getAtomicPropositionKeying(),  widgetKeyCollisionMap);
                propositions.addAll(pb.left());// concrete widgets
                commentWidgetDistri.put(senc.getState(), pb.right());
                runningWcount = runningWcount + pb.right();
                senc.setStateAPs(propositions);
                if (instrumentTerminalState && terminalState) {
                    TransitionEncoding deadTrenc = new TransitionEncoding();
                    deadTrenc.setTransition(terminalStateEnc.getState() + "_" + stateVertex.getIdentity().toString());
                    deadTrenc.setTargetState(terminalStateEnc.getState());//"#" + TemporalModel.getDeadProposition());
                    Set<String> deadTransitionPropositions = new LinkedHashSet<>();
                    deadTransitionPropositions.add(PropositionConstants.SETTING.terminalProposition);
                    deadTrenc.setTransitionAPs(deadTransitionPropositions);
                    List<TransitionEncoding> deadTrencList = new ArrayList<>();
                    deadTrencList.add(deadTrenc);
                    senc.setTransitionColl(deadTrencList);
                } else
                    senc.setTransitionColl(getTransitions(senc.getState(), tModel.getAtomicPropositionKeying()));

                tModel.addStateEncoding(senc, false);
                if(logDetailsInModelFile) {//time intensive
                    String mapAsString = widgetKeyCollisionMap.entrySet().stream()
                            .filter(e -> e.getValue().size() > 1)
                            .map(e -> e.getKey() + "->" + e.getValue().toString())
                            .collect(Collectors.joining(", ", "{", "}"));
                    if (mapAsString.length() > 2) { // larger then "{}"
                        statetreeKeyCollisionMap.put("state " + stateVertex.getIdentity().toString() + " / " + stateVertex.getProperty(CodingManager.CONCRETE_ID_CUSTOM).toString(), mapAsString);
                    }
                }

            }
            stateCount++;
                if ((totalStates<=chunks) || stateCount % (Math.floorDiv(totalStates, chunks)) == 0) { //rely on lazy evaluation
                    simpleLog.append(prettyCurrentTime() + " | " + "States processed: " + Math.floorDiv((100 * stateCount), totalStates) + "%");
                }
            }

        //resultSet.close();
        simpleLog.append(prettyCurrentTime() + " | " + "Copying state propositions to transitions");
        tModel.finalizeTransitions(); //update once. this is a costly operation

        if(logDetailsInModelFile){
            simpleLog.append(prettyCurrentTime() + " | " + "Logging non-deterministic transitions");
            for (StateEncoding stenc : tModel.getStateEncodings()) {
                Map<String,TransitionEncoding> encodedConjuncts = new HashMap<>();
                for (TransitionEncoding tren : stenc.getTransitionColl()) {
                    String enc = tren.getEncodedTransitionAPConjunct();
                    if (encodedConjuncts.containsKey(enc)) {
                        TransitionEncoding trenTemp = encodedConjuncts.get(enc);
                        logNonDeterministicTransitions.put(stenc.getState(), trenTemp.getTransition()+"=>"+trenTemp.getTargetState());
                        logNonDeterministicTransitions.put(stenc.getState(), tren.getTransition()+"=>"+tren.getTargetState());
                    } else encodedConjuncts.put(enc, tren);
                }
            }
        }
        else{
            simpleLog.append(prettyCurrentTime() + " | " + "Logging non-deterministic transitions is disabled");
        }

        tModel.addLog("Terminal States : " + logTerminalStates.toString());
        String mapAsString = commentWidgetDistri.keySet().stream()
                .map(key -> key + "->" + commentWidgetDistri.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        tModel.addLog("#Widgets per State : " + mapAsString);
        tModel.addLog("Total #Widgets : " + runningWcount);
        mapAsString = logNonDeterministicTransitions.keySet().stream()
                .map(key -> key + "-> " + logNonDeterministicTransitions.get(key).toString())
                .collect(Collectors.joining(", ", "{", "}"));
        tModel.addLog("Non-deterministic transitions by State: " + mapAsString);

        if(logDetailsInModelFile) {//time intensive:e.g. 3000 states and 150000 widgets can take 20 minutes
            simpleLog.append(prettyCurrentTime() + " | " + "Logging PropositionKey Collisions");
            statetreeKeyCollisionMap.entrySet().stream()
                    .filter(e -> e.getValue().length() > 0)
                    .map(e -> "PropositionKey Collisions :  " + e.getKey() + "->" + e.getValue())
                    .forEachOrdered(tModel::addLog);
        }
        else{
             simpleLog.append(prettyCurrentTime() + " | " + "Logging PropositionKey Collisions is disabled");
        }

        simpleLog.append(prettyCurrentTime() + " | " + "Determining Initial States");
        List<TemporalTrace> traces = fetchTraces(tModel.getApplication_ModelIdentifier(), logDetailsInModelFile);
        Set<String> initStates = new HashSet<>();
        for (TemporalTrace trace : traces
        ) {
            TemporalTraceEvent traceevent = trace.getTraceEvents().get(0);
            initStates.add(traceevent.getState());
        }
        tModel.setInitialStates(initStates);
        simpleLog.append(prettyCurrentTime() + " | " + "Logging Test Sequences"+(logDetailsInModelFile ? "" :" is disabled"));
        tModel.setTraces((logDetailsInModelFile ? traces:null));
        simpleLog.append(prettyCurrentTime() + " | " + "Total States : " + tModel.getStateList().size());
        simpleLog.append(prettyCurrentTime() + " | " + "Total Widgets : " + runningWcount);
        simpleLog.append(prettyCurrentTime() + " | " + "Total Transitions : " + tModel.getTransitionList().size());
        simpleLog.append(prettyCurrentTime() + " | " + "Total Atomic Propositions : " + tModel.getAtomicPropositions().size());
        simpleLog.append(prettyCurrentTime() + " | " + "Model has " + (logTerminalStates.size() == 0 ? "no" : "" + logTerminalStates.size()) + " terminal states");
        simpleLog.append(prettyCurrentTime() + " | " + "Model has " + tModel.getInitialStates().size() + " initial states");
        simpleLog.append(prettyCurrentTime() + " | " + "Model is deterministic?: " + (!logDetailsInModelFile?"Not calculated":(logNonDeterministicTransitions.size()!=0?"No":"Yes")));

        long end_time = System.currentTimeMillis();
        long difference = (end_time - start_time) / 1000;
        tModel.addLog("Duration to create the model:" + difference + " (s)");
        dbClose();

        }
}


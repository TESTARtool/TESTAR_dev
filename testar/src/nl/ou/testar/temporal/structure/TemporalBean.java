package nl.ou.testar.temporal.structure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

//@JsonRootName(value="TemporalProperties")
public class TemporalBean {
protected static final String csvsep=";";
    @CsvBindByName
    private String applicationName;
    @CsvBindByName
    private String applicationVersion;
    @CsvBindByName
    private String application_ModelIdentifier;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = HashSet.class)
    private Set<String> application_AbstractionAttributes;
   //@CsvCustomBindByName( converter = CSVConvertMultiLine.class)
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> application_log;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    //@CsvCustomBindByName( converter = CSVConvertMultiLine.class)
    private List<String> _comments;
    @CsvBindByName
    private String _modifieddate;
    @CsvBindByName
    private String _formatVersion;



    public TemporalBean() {
        this.application_log = new ArrayList<String>();
        this._comments = new ArrayList<String>();
        _formatVersion ="20190629";
    }

    public TemporalBean(String applicationName, String applicationVersion, String modelIdentifier, Set abstractionAttributes) {
        this();
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.application_ModelIdentifier = modelIdentifier;
        this.application_AbstractionAttributes = abstractionAttributes;
        this._modifieddate = LocalDateTime.now().toString();


    }

        public String getApplicationName () {
            return applicationName;
        }

        public void setApplicationName (String applicationName){
            this.applicationName = applicationName;
        }

        public String getApplicationVersion () {
            return applicationVersion;
        }

        public void setApplicationVersion (String applicationVersion){
            this.applicationVersion = applicationVersion;
        }

        public String getApplication_ModelIdentifier() {
            return application_ModelIdentifier;
        }

        public void setApplication_ModelIdentifier(String application_ModelIdentifier){
            this.application_ModelIdentifier = application_ModelIdentifier;
        }

        public Set getApplication_AbstractionAttributes() {
            return application_AbstractionAttributes;
        }

        public void setApplication_AbstractionAttributes(Set application_AbstractionAttributes){
            this.application_AbstractionAttributes = application_AbstractionAttributes;
        }


        public List<String> getApplication_log() {
            return application_log;
        }

        public void setApplication_log(List < String > application_log) {
            this.application_log = application_log;
        }
    public void addLog ( String  log) {
        this.application_log.add( log);
    }

        public List<String> get_comments() {
            return _comments;
        }

        public void set_comments(List < String > _comments) {
            this._comments = _comments;
        }
    public void addComments ( String  comment) {
        this._comments.add( comment);
    }

        public String get_modifieddate() {
            return _modifieddate;
        }

        public void set_modifieddate(String _modifieddate){
            this._modifieddate = _modifieddate;
        }
    public String get_formatVersion() {
        return _formatVersion;
    }

    public void set_formatVersion(String _formatVersion) {
        this._formatVersion = _formatVersion;
    }
        //custom

//    public void save(String toFile) {        JSONHandler.save(this,toFile);    }

//    public  static TemporalBean load(String fromFile,)  { // CLASS method ?
//        return (TemporalBean) JSONHandler.load(fromFile, TemporalBean.class);
//    }


        public Map<String, Object> peekNode (String inFile)
        { // actually need proper error  handling... throws Exception {
            return peekNode(inFile, "", true, "");
        }
        public Map<String, Object> peekNode (String inFile, String nodeStartsWith)
        { // actually need proper error  handling... throws Exception {
            return peekNode(inFile, nodeStartsWith, true, "");
        }
        public Map<String, Object> peekNode (String inFile, Boolean primitvesOnly)
        { // actually need proper error  handling... throws Exception {
            return peekNode(inFile, "", primitvesOnly, "");
        }
        public Map<String, Object> peekNode (String inFile, String nodeStartsWith, Boolean primitvesOnly)
        { // actually need proper error  handling... throws Exception {
            return peekNode(inFile, nodeStartsWith, primitvesOnly, "");
        }
        //   public Map<String,Object> peekNode(String inFile,String nodeStartsWith,String subnode) { // actually need proper error  handling... throws Exception {
        //       return peekNode(inFile,nodeStartsWith,true,subnode);   //subnode is not working as expected
        //   }
        private Map<String, Object> peekNode (String inFile, String nodeStartsWith, Boolean primitvesOnly, String
        subnode){ // actually need proper error  handling... throws Exception {
            // peeks only one level deep from the root

            File jsonFile = new File(inFile).getAbsoluteFile();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(jsonFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (subnode != "") root = root.get(subnode);
            return getFields(root, nodeStartsWith, primitvesOnly);
        }
        private static Map<String, Object> getFields (JsonNode data, String nodeStartsWith, Boolean primitivesonly){
            Map<String, Object> attributes = new HashMap<>();
            for (Iterator<Map.Entry<String, JsonNode>> it = data.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> field = it.next();
                String key = field.getKey();
                if (key.startsWith(nodeStartsWith)) {
                    JsonNode value = field.getValue();
                    if (value.isBoolean()) {
                        attributes.put(key, value.asBoolean());
                    } else if (value.isLong()) {
                        attributes.put(key, value.asLong());
                    } else if (value.isDouble()) {
                        attributes.put(key, value.asDouble());
                    } else if (!primitivesonly && value.isArray()) {
                        attributes.put(key, value.toString());
                    } else if (!primitivesonly && value.isObject()) {
                        attributes.put(key, value.toString());

                    } else
                        attributes.put(key, value.asText());
                }
            }

            return attributes;
        }


    }

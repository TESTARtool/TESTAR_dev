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
    private String modelIdentifier;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = HashSet.class)
    private Set<String> abstractionAttributes;
   //@CsvCustomBindByName( converter = CSVConvertMultiLine.class)
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> log;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    //@CsvCustomBindByName( converter = CSVConvertMultiLine.class)
    private List<String> comments;
    @CsvBindByName
    private String modifieddate;
    @CsvBindByName
    private String formatVersion="20190629";



    public TemporalBean() {
        this.log = new ArrayList<String>();
        this.comments = new ArrayList<String>();
    }

    public TemporalBean(String applicationName, String applicationVersion, String modelIdentifier, Set abstractionAttributes) {
        this();
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.modelIdentifier = modelIdentifier;
        this.abstractionAttributes = abstractionAttributes;
        this.modifieddate = LocalDateTime.now().toString();


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

        public String getModelIdentifier () {
            return modelIdentifier;
        }

        public void setModelIdentifier (String modelIdentifier){
            this.modelIdentifier = modelIdentifier;
        }

        public Set getAbstractionAttributes () {
            return abstractionAttributes;
        }

        public void setAbstractionAttributes (Set abstractionAttributes){
            this.abstractionAttributes = abstractionAttributes;
        }


        public List<String> getLog () {
            return log;
        }

        public void setLog (List < String > log) {
            this.log = log;
        }
    public void addLog ( String  log) {
        this.log.add( log);
    }

        public List<String> getComments () {
            return comments;
        }

        public void setComments (List < String > comments) {
            this.comments = comments;
        }
    public void addComments ( String  comment) {
        this.comments.add( comment);
    }

        public String getModifieddate () {
            return modifieddate;
        }

        public void setModifieddate (String modifieddate){
            this.modifieddate = modifieddate;
        }
    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
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

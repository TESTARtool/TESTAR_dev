package nl.ou.testar.temporal.foundation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import com.opencsv.bean.CsvRecurse;
import es.upv.staq.testar.StateManagementTags;
import org.fruit.alayer.Tag;
import org.fruit.alayer.windows.UIAMapping;

import java.util.*;

public class TemporalBean {

    @CsvIgnore
    private static String version = "20200104";

    protected static final String csvsep=";";
    @CsvBindByName
    private String applicationName;
    @CsvBindByName
    private String applicationVersion;
    @CsvBindByName
    private String application_ModelIdentifier;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = HashSet.class)
    private Set<String> application_AbstractionAttributes;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = HashSet.class)
    private List<String> application_BackendAbstractionAttributes;

    @CsvRecurse
    private TemporalMeta metaData;



    public TemporalBean() {
        metaData = new TemporalMeta();
    }
    public static String getVersion() {
        return version;
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

        public Set<String> getApplication_AbstractionAttributes() {
            return application_AbstractionAttributes;
        }

        public void setApplication_AbstractionAttributes(Set<String> application_AbstractionAttributes){
            this.application_AbstractionAttributes = application_AbstractionAttributes;
            setApplication_BackendAbstractionAttributes();
        }

       public   List<String> getApplication_BackendAbstractionAttributes() {
            return application_BackendAbstractionAttributes;
        }

        private void setApplication_BackendAbstractionAttributes() {
            List<Tag<?>> taglist = new ArrayList<>();
            List<String> APKey = new ArrayList<>();
            Set<String> abstractionattributes = getApplication_AbstractionAttributes();
            if (abstractionattributes != null) { // no proper model found
                for (String attrib : abstractionattributes
                ) {
                    for (Tag<?> t : StateManagementTags.getAllTags()
                    ) {
                        if (t.name().equals(attrib)) {
                                taglist.add(t);
                            break;
                        }
                    }
                }
                //mapp once more
                for (Tag<?> t : taglist
                ) {
                    Tag<?> tempTag;
                    tempTag=StateManagementTags.getMappedTag(t,true);
                    //boolean is not used in implementation of getMappedTag
                    //usage...converts to testar classic tags. E.g. 'WidgetControlType' to 'Role':
                    if (tempTag!=null){
                        APKey.add(tempTag.name());
                    }
                    else{
                        tempTag = UIAMapping.getMappedStateTag(t);
                        if (tempTag != null) {
                            APKey.add(tempTag.name());
                        }
                    }

                }
            }
            application_BackendAbstractionAttributes = APKey;
        }



    public TemporalMeta getMetaData() {
        return metaData;
    }
    public void setMetaData(TemporalMeta metaData) {
        this.metaData = metaData;
    }

    @JsonIgnore
    public void set_comments(List < String > _comments) {
        metaData.set_comments(_comments);
    }
    public void addComments ( String  comment) {
        metaData.addComments( comment);
    }
    public String get_modifieddate() {
        return metaData.get_modifieddate();
    }
    @JsonIgnore
    public void set_modifieddate(String _modifieddate){
        metaData.set_modifieddate( _modifieddate);
    }

    public void addLog ( String  log) {
        metaData.addLog(log);
    }


    }


package nl.ou.testar.temporal.proposition;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.fruit.alayer.Tags;

import java.util.*;

@JsonPropertyOrder({  "comments","formatVersion", "propositionSubKeySeparator", "stateFilters", "widgetFilters","transitionFilters" })
//@JsonPropertyOrder(alphabetic = true)
public class PropositionManager {

    private String formatVersion="20200508";
    private List<String> comments = new ArrayList<>();
    private List<String> propositionKey = new ArrayList<>();
    private String propositionSubKeySeparator;
    private Set<PropositionFilter> stateFilters;
    private Set<PropositionFilter> transitionFilters;
    private Set<PropositionFilter> widgetFilters;



    public PropositionManager() {
        //super();
        transitionFilters = new LinkedHashSet<>();
        stateFilters = new LinkedHashSet<>();
        widgetFilters= new LinkedHashSet<>();

    }
    public PropositionManager(boolean initializeWithDefaults) {
         this(initializeWithDefaults,null);
    }

    public PropositionManager(boolean initializeWithDefaults, List<String> propositionKey) {
        this();
        if (initializeWithDefaults){
            comments.add("This is a Sample APModelManager with  filters: two edge-filters, two wdiget filters, one state-filters.");
            comments.add("An EMPTY list of conditionAttributes results in rejection. Use one '*' as wildcard");
            comments.add("Note: An entry in the map of modelAPs indicates that the proposition is true somewhere in the model. ");
            comments.add("In other words: if a property is always FALSE (i.e. in all states/edges)  then it is NOT regarded as a proposition");
            comments.add("the map is not guaranteed in lexicographic order: some new (true) properties can be discovered 'late'");

            PropositionFilter wf = new PropositionFilter();
            wf.setDefaultWidgetFilter();
            widgetFilters.add(wf);
            PropositionFilter wf1 = new PropositionFilter();
            wf1.setMinimalWidgetFilter();
            widgetFilters.add(wf1);

            PropositionFilter tfilter = new PropositionFilter();
            tfilter.setDefaultVKEdgeFilter();
            transitionFilters.add(tfilter);
            PropositionFilter tfilter1 = new PropositionFilter();
            tfilter1.setCatchAllEdgeFilter();
            transitionFilters.add(tfilter1);

            PropositionFilter sfilter = new PropositionFilter();
            sfilter.setDefaultStateFilter();
            stateFilters.add(sfilter);



            if (propositionKey ==null){
                setRoleTitlePathAsAPKey();  }
            else{
                this.propositionKey = propositionKey;}
            this.propositionSubKeySeparator = PropositionConstants.SETTING.subKeySeparator;
        }

    }
    //*********************



    @JsonGetter("transitionFilters")
   @SuppressWarnings("unused")
    private Set<PropositionFilter> getTransitionFilters() {
        return transitionFilters;
    }

    @SuppressWarnings("unused")
    public void setTransitionFilters(Set<PropositionFilter> transitionFilters) {
        this.transitionFilters=transitionFilters;
    }
    @SuppressWarnings("unused")
    public Set<PropositionFilter> getWidgetFilters() {
        return widgetFilters;
    }
    @SuppressWarnings("unused")
    public void setWidgetFilters(Set<PropositionFilter> widgetFilters) {
        this.widgetFilters = widgetFilters;
    }
    @SuppressWarnings("unused")
    public Set<PropositionFilter> getStateFilters() {
        return stateFilters;
    }
    @SuppressWarnings("unused")
    public void setStateFilters(Set<PropositionFilter> stateFilters) {
        this.stateFilters = stateFilters;
    }

    public String getPropositionSubKeySeparator() {
        return propositionSubKeySeparator;
    }
    @SuppressWarnings("unused")
    public void setPropositionSubKeySeparator(String propositionSubKeySeparator) {
        this.propositionSubKeySeparator = propositionSubKeySeparator;
    }


    public void updateAPKey(List<String> APKey) {
        this.propositionKey = APKey;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
    @SuppressWarnings("unused")
    public String getFormatVersion() {
        return formatVersion;
    }
    @SuppressWarnings("unused")
    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }



    public void setRoleTitlePathAsAPKey(){
        propositionKey.clear();
        propositionKey.add(Tags.Role.name());
        propositionKey.add(Tags.Title.name());
        propositionKey.add(Tags.Path.name());

    }

    public List<PropositionFilter> passPropositionConditions(Map<String,String> attribmap){
        List<PropositionFilter> passedPropositionFilters = new ArrayList<>();
        //pass if each entry in the attribmap matches at least one FilterPart ('disjunction of FilterParts')
        // all values in attribmap are non-empty

        boolean pass = false;
        for (PropositionFilter wf : stateFilters
        ) {
            for (Map.Entry<String, String> entry : attribmap.entrySet()
            ) {
                pass = false;
                for (PropositionConditionPart wfConditionPart : wf.getPropositionConditionParts()
                ) {
                    if (wfConditionPart.getSelectedAttributes().contains(entry.getKey())) {
                      //  System.out.println("DEBUG: checking expressions on Atrribute: "+entry.getKey()+","+entry.getValue()+"    time: "+System.nanoTime());
                        Set<String> dummy = wfConditionPart.getPropositionStrings("dummy", entry.getKey(), entry.getValue());
                      //  System.out.println("DEBUG: checking done    time: "+System.nanoTime());
                        if (dummy != null && !dummy.isEmpty()) {
                            pass = true;
                            break; // current 'entry' fulfills the condition
                        }
                    }
                }
                if (!pass) break; // entry doesn't fulfill condition, no need to check remaining entries on this filter
            }
            if (pass) {// all entries fulfill a condition
                passedPropositionFilters.add(wf);
            }
        }
        return passedPropositionFilters; //pass;
    }
    public List<PropositionFilter> setPassingFilters(PropositionFilterType filterType, Map<String,String> attribmap){
        List<PropositionFilter> passedPropositionFilters = new ArrayList<>();
        //pass if  all Conditions in a Filter are fulfilled. ('conjunct of FilterConditionParts')
        boolean pass = false;
        Set<PropositionFilter> filters;
        switch (filterType){
            case STATE:
                filters=stateFilters;
                break;
            case WIDGET:
                filters=widgetFilters;
                break;
            case TRANSITION:
                filters=transitionFilters;
                break;
            default:
                filters=null;
        }
        for (PropositionFilter wf : filters
        ) {
            pass = false;
            for (PropositionConditionPart wfConditionPart : wf.getPropositionConditionParts()
            ) {
                // a wildcard trumps other conditions
                if (wfConditionPart.getSelectedAttributes().contains("*")){
                    pass=true;
                    continue;
                }
                if (attribmap.keySet().containsAll(wfConditionPart.getSelectedAttributes())) {
                    for (String attribute : wfConditionPart.getSelectedAttributes()
                        //ConditionPart may contain multiple attributes, only one value-match is required to pass
                    ) {
                       pass = wfConditionPart.matchExists( attribute, attribmap.get(attribute));
                        if (pass) {
                            break; // current 'entry' fulfills the condition
                        }
                    }
                if (!pass) break; // doesn't fulfill this condition, no need to check remaining conditions of this filter
                }
            }



            if (pass) {// all entries fulfill a condition
                passedPropositionFilters.add(wf);
            }
        }
        return passedPropositionFilters; //pass;
    }
}
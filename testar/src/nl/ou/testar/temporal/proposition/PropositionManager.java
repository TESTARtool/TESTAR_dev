
package nl.ou.testar.temporal.proposition;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.fruit.alayer.Tags;

import java.util.*;

/**
 * The proposition manager contains the filter rules to be applied when a Model is retrieved from the Graph Db
 * to be transformed to an implementation independent format that can be used in the next phase
 * to be transformed to a model-checker dependant model.
 */
@JsonPropertyOrder({  "comments","formatVersion","propositionKeying",  "propositionSubKeySeparator", "stateFilters", "widgetFilters","transitionFilters" })

public class PropositionManager {

    private String formatVersion="20200508";
    private List<String> comments = new ArrayList<>();
    private List<String> propositionKeying = new ArrayList<>();
    private String propositionSubKeySeparator;
    private Set<PropositionFilter> stateFilters;
    private Set<PropositionFilter> transitionFilters;
    private Set<PropositionFilter> widgetFilters;


    public PropositionManager() {
        transitionFilters = new LinkedHashSet<>();
        stateFilters = new LinkedHashSet<>();
        widgetFilters= new LinkedHashSet<>();

    }

    /**
    *  candidate for refactoring: only used for writing a default proposition manager
    *  used for writing a default proposition manager
    * @param initializeWithDefaults is always true
    */
    public PropositionManager(boolean initializeWithDefaults) {
         this(initializeWithDefaults,null);
    }

    /**
     * candidate for refactoring: propositionKeying is no longer needed in a constructor
     * @param initializeWithDefaults
     * @param propositionKeying
     */
    private  PropositionManager(boolean initializeWithDefaults, List<String> propositionKeying) {
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

            if (propositionKeying ==null){ //canditate for refactoring
                this.propositionKeying.clear();
                this.propositionKeying.add(Tags.Role.name());
                this.propositionKeying.add(Tags.Title.name());
                this.propositionKeying.add(Tags.Path.name());
            }
            else{
                this.propositionKeying = propositionKeying;}
            this.propositionSubKeySeparator = PropositionConstants.SETTING.subKeySeparator;
        }

    }


    /**
     * used for writing a default proposition manager JSON file
     * @return Transition filters (Actions in TESTAR)
     */
    @JsonGetter("transitionFilters")
   @SuppressWarnings("unused")
    private Set<PropositionFilter> getTransitionFilters() {
        return transitionFilters;
    }

    /**
     * used when reading a proposition manager JSON file
     * @param transitionFilters
     */
    @SuppressWarnings("unused")
    public void setTransitionFilters(Set<PropositionFilter> transitionFilters) {
        this.transitionFilters=transitionFilters;
    }
    /**
     * used for writing a default proposition manager JSON file
     * @return widget filters
     */
    @SuppressWarnings("unused")
    public Set<PropositionFilter> getWidgetFilters() {
        return widgetFilters;
    }

    /**
     * used when reading a proposition manager JSON file
     * @param widgetFilters
     */
    @SuppressWarnings("unused")
    public void setWidgetFilters(Set<PropositionFilter> widgetFilters) {
        this.widgetFilters = widgetFilters;
    }
    /**
     * used for writing a default proposition manager JSON file
     * @return statefilters
     */
    @SuppressWarnings("unused")
    public Set<PropositionFilter> getStateFilters() {
        return stateFilters;
    }

    /**
     * used when reading a proposition manager JSON file
     * @param stateFilters
     */
    @SuppressWarnings("unused")
    public void setStateFilters(Set<PropositionFilter> stateFilters) {
        this.stateFilters = stateFilters;
    }

    /**
     * used for writing a default proposition manager JSON file
     * @return the subkey separator . E.g. "_||_"
     */
    public String getPropositionSubKeySeparator() {
        return propositionSubKeySeparator;
    }

    /**
     * used when reading a proposition manager JSON file
     * @param propositionSubKeySeparator
     */
    @SuppressWarnings("unused")
    public void setPropositionSubKeySeparator(String propositionSubKeySeparator) {
        this.propositionSubKeySeparator = propositionSubKeySeparator;
    }

    /**
     * used for writing a default proposition manager JSON file
     * @return keying of the proposition . E.g. Role-Title-Path
     */
    public List<String> getPropositionKeying() {
        return propositionKeying;
    }

    /**
     * used when reading a proposition manager JSON file
     * @param propositionKeying
     */
    public void setPropositionKeying(List<String> propositionKeying) {
        this.propositionKeying = propositionKeying;
    }


    /**
     * used for writing a default proposition manager JSON file
     * @return comments to write
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     * used when reading a proposition manager JSON file
     * @param comments
     */
    public void setComments(List<String> comments) {
        this.comments = comments;
    }
    @SuppressWarnings("unused")
    public String getFormatVersion() {
        return formatVersion;
    }

    /**
     * used when reading a proposition manager JSON file
     * @param formatVersion
     */
    @SuppressWarnings("unused")
    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }


    /**
     * get a list of all filters in the proposition manager that pass : the widget attribute-values pairs conform.
     * @param filterType filter is one of STATE, WIDGET, TRANSITION.
     * @param attribmap map of widget attributes and their corresponding values
     * @return
     */
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
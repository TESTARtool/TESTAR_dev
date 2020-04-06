
package nl.ou.testar.temporal.selector;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.ou.testar.temporal.foundation.APEncodingSeparator;
import org.fruit.alayer.Tags;

import java.util.*;


public class APModelManager {


    private  String freeFormatText;
    private  APSelector stateFilter;
    private  APSelector transitionFilter;
    private List<String> APKey= new ArrayList<>();
    private String apEncodingSeparator;
    private Set<WidgetFilter> widgetfilters;
    private String formatVersion="20200101";
    private List<String> comments = new ArrayList<>();


    public APModelManager() {
        super();
        stateFilter = new APSelector();
        transitionFilter = new APSelector();
        widgetfilters = new LinkedHashSet<>();
        comments.add(" !!!! if the stateFilter is EMPTY, then this will be enriched. 'selectedattributes' with 'Role,IsTerminalState' 'valuedexpression' with 'exists__' and  expressions");
        comments.add(" An EMPTY widget condition results in rejection");
        comments.add("Note that when you are inspecting an APEncodedModel:  An entry in the map of modelAPs indicates that the property is true somewhere in the model. ");
        comments.add("In other words: if a property is always FALSE( i.e. in all states/edges)  then it is NOT regarded as a modelAp");
        comments.add("Note that the map is not guaranteed in lexicographic order: some new (true) properties can be discovered 'late'");

    }
    public APModelManager(boolean initializeWithDefaults) {
         this(initializeWithDefaults,null);
    }

    public APModelManager(boolean initializeWithDefaults, List<String> APKey) {
        this();
        if (initializeWithDefaults){
            updateFreeFormatText("This is a Sample APModelManager with two widget filters.");
            stateFilter = new APSelector();
            transitionFilter = new APSelector();
            stateFilter.setSelectedStateAttributes(APSelector.useMinimalAttributes());
            stateFilter.setSelectedExpressions(APSelector.useMinimalSelectedExpressions());
            transitionFilter.setSelectedAttributes(APSelector.useMinimalTransAttributes());
            transitionFilter.setSelectedExpressions(APSelector.useMinimalTransSelectedExpressions());
            WidgetFilter wf = new WidgetFilter();
            wf.setDefaultWidgetFilter();
            widgetfilters.add(wf);
            WidgetFilter wf1 = new WidgetFilter();
            wf1.setMinimalWidgetFilter();
            widgetfilters.add(wf1);
            if (APKey==null){
                setRoleTitlePathAsAPKey();  }
            else{
                this.APKey=APKey;}
            this.apEncodingSeparator= APEncodingSeparator.CUSTOM.symbol;
        }

    }
    //*********************

    @JsonGetter("stateFilter")
    private  APSelector getStateFilter() {
        return stateFilter;
    }

    public Set<String> getAPsOfStateAttribute(String apkey, String transitionProperty, String value) {
        return getStateFilter().getAPsOfAttribute(apkey, transitionProperty, value);
    }
    @SuppressWarnings("unused")
    public void setStateFilter(APSelector stateFilter) {
        // let the AP selector check for the minimal set
        this.stateFilter.setSelectedStateAttributes(stateFilter.getSelectedAttributes());
        this.stateFilter.setSelectedExpressions(stateFilter.getSelectedExpressions());
    }
    @JsonGetter("transitionFilter")
    private   APSelector getTransitionFilter() {
        return transitionFilter;
    }

    public Set<String> getAPsOfTransitionAttribute(String apkey, String transitionProperty, String value) {
        return getTransitionFilter().getAPsOfAttribute(apkey, transitionProperty, value);
    }
    @SuppressWarnings("unused")
    public void setTransitionFilter(APSelector transitionFilter) {
        this.transitionFilter.setSelectedAttributes(transitionFilter.getSelectedAttributes());
        this.transitionFilter.setSelectedExpressions(transitionFilter.getSelectedExpressions());
    }
    public String getApEncodingSeparator() {
        return apEncodingSeparator;
    }
    @SuppressWarnings("unused")
    public void setApEncodingSeparator(String apEncodingSeparator) {
        this.apEncodingSeparator = apEncodingSeparator;
    }
    @JsonIgnore
    @SuppressWarnings("unused")
    private  List<String> getAPKey() {  // kept for compatability
       return APKey;
    }

    public void updateAPKey(List<String> APKey) {
        this.APKey = APKey;
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
    @SuppressWarnings("unused")
    public Set<WidgetFilter> getWidgetfilters() {
        return widgetfilters;
    }
    @SuppressWarnings("unused")
    public void setWidgetfilters(Set<WidgetFilter> widgetfilters) {
        this.widgetfilters = widgetfilters;
    }


    public void setRoleTitlePathAsAPKey(){
        APKey.clear();
        APKey.add(Tags.Role.name());
        APKey.add(Tags.Title.name());
        APKey.add(Tags.Path.name());

    }

    public String getFreeFormatText() {
        return freeFormatText;
    }


    public void updateFreeFormatText(String freeFormatText) {
        this.freeFormatText = freeFormatText;
    }
    @SuppressWarnings("unused")
    public void addWidgetFilter(WidgetFilter w){
        widgetfilters.add(w);
    }


    public List<WidgetFilter> passWidgetFilters(Map<String,String> attribmap){
        List<WidgetFilter> passedWidgetFilters= new ArrayList<>();
        //pass if each entry in the attribmap matches at least one FilterPart ('disjunction of FilterParts')
        // all values in attribmap are non-empty

        boolean pass = false;
        for (WidgetFilter wf : widgetfilters
        ) {
            for (Map.Entry<String, String> entry : attribmap.entrySet()
            ) {
                pass = false;
                for (WidgetConditionPart wfConditionPart : wf.getWidgetConditionParts()
                ) {
                    if (wfConditionPart.getSelectedAttributes().contains(entry.getKey())) {
                      //  System.out.println("DEBUG: checking expressions on Atrribute: "+entry.getKey()+","+entry.getValue()+"    time: "+System.nanoTime());
                        Set<String> dummy = wfConditionPart.getAPsOfAttribute("dummy", entry.getKey(), entry.getValue());
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
                passedWidgetFilters.add(wf);
            }
        }
        return passedWidgetFilters; //pass;
    }
}
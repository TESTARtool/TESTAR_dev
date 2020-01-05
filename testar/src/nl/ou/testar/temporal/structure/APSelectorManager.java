package nl.ou.testar.temporal.structure;

import nl.ou.testar.temporal.util.APEncodingSeparator;
import org.fruit.alayer.Tags;

import java.util.*;


public class APSelectorManager {


    private  String freeFormatText;
    private  APSelector stateFilter;
    private  APSelector transitionFilter;
    private List<String> APKey= new ArrayList<>();
    private String apEncodingSeparator;
    private Set<WidgetFilter> widgetfilters;
    private String formatVersion="20200101";
    private List<String> comments = new ArrayList<>();


    public APSelectorManager() {
        super();
        stateFilter = new APSelector();
        transitionFilter = new APSelector();
        widgetfilters = new LinkedHashSet<>();
        comments.add(" !!!!ONLY EMPTY 'selectedattributes' or' valuedexpression' will be enriched with 'Enabled,Role,IsDeadState' attributes and 'exists__' and isblank__' expressions");
       comments.add(" second widget filter lists the default protperties");
       comments.add(" EMPTY widget <ROLE-PATH-TITLE> filters will result in acceptance of  <ALL ROLE- ALL PATH -ALL TITLE>");
        comments.add("Consider when you are inspecting an APEncodedModel:  An entry in the map of modelAPs indicates that the property is at least somewhere true in the model. ");
        comments.add("In other words: if a property is always FALSE( i.e. in all states/edges)  then it is NOT regarded as a modelAp and is NOT listed in the map of modelAp's");
        comments.add("Note that the map is not guaranteed in lexicographic order: some new (true) properties can be discovered 'late'");
       // comments.add(" Apkey is considered read-only and is a copy of Application_BackendAbstractionAttributes from the APEncodedmodel");
        comments.add("this enables to distinguish 2 buttons with the same title in the relative window in 2 different states");
        comments.add("this is not functional yet. CSS 20190630");
    }
    public APSelectorManager(boolean initializeWithDefaults) {
         this(initializeWithDefaults,null);
    }

    public APSelectorManager(boolean initializeWithDefaults,List<String> APKey) {
        this();
        if (initializeWithDefaults){
            updateFreeFormatText("This is a Sample APSelectorManager with two widget filters");
            stateFilter = new APSelector();
            transitionFilter = new APSelector();
            stateFilter.setSelectedStateAttributes(APSelector.useMinimalAttributes());
            stateFilter.setSelectedExpressions(APSelector.useMinimalSelectedExpressions());
            transitionFilter.setSelectedAttributes(APSelector.useMinimalAttributes());
            transitionFilter.setSelectedExpressions(APSelector.useMinimalSelectedExpressions());
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


    public APSelector getStateFilter() {
        return stateFilter;
    }

    public void setStateFilter(APSelector stateFilter) {
        // let the AP selector check for the minimal set
        this.stateFilter.setSelectedStateAttributes(stateFilter.getSelectedAttributes());
        this.stateFilter.setSelectedExpressions(stateFilter.getSelectedExpressions());
    }

    public APSelector getTransitionFilter() {
        return transitionFilter;
    }

    public void setTransitionFilter(APSelector transitionFilter) {
        this.transitionFilter.setSelectedAttributes(transitionFilter.getSelectedAttributes());
        this.transitionFilter.setSelectedExpressions(transitionFilter.getSelectedExpressions());
    }
    public String getApEncodingSeparator() {
        return apEncodingSeparator;
    }

    public void setApEncodingSeparator(String apEncodingSeparator) {
        this.apEncodingSeparator = apEncodingSeparator;
    }

    public List<String> getAPKey() {
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

    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }

    public Set<WidgetFilter> getWidgetfilters() {
        return widgetfilters;
    }

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

    public void addWidgetFilter(WidgetFilter w){
        widgetfilters.add(w);
    }


    public List<WidgetFilter> passWidgetFilters(Map<String,String> attribmap){
        boolean pass=false;
        List<WidgetFilter> passedWidgetFilters= new ArrayList<>();
        //pass if each entry in the attribmap matches at least one FilterPart ('disjunction of FilterParts')
        // all values in attribmap are non-empty

        pass = false;
        for (WidgetFilter wf : widgetfilters
        ) {
            for (Map.Entry<String, String> entry : attribmap.entrySet()
            ) {
                pass = false;
                for (WidgetFilterPart wfpart : wf.getWidgetFilterParts()
                ) {
                    if (wfpart.getSelectedAttributes().contains(entry.getKey())) {
                      //  System.out.println("DEBUG: checking expressions on Atrribute: "+entry.getKey()+","+entry.getValue()+"    time: "+System.nanoTime());
                        Set<String> dummy = wfpart.getAPsOfAttribute("dummy", entry.getKey(), entry.getValue());
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
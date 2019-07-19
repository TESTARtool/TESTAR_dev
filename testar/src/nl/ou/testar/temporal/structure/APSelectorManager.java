package nl.ou.testar.temporal.structure;

import nl.ou.testar.temporal.util.APEncodingSeparator;
import org.fruit.alayer.Tags;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

//@JsonRootName(value="TemporalProperties")
public class APSelectorManager extends APSelector{



    private List<String> APKey= new ArrayList<>();



    private String apEncodingSeparator;
    private Set<WidgetFilter> widgetfilters;

    private String formatVersion="20190713";
    private List<String> comments = new ArrayList<>();




    public APSelectorManager() {
        super();

        widgetfilters = new LinkedHashSet<>();

       comments.add("An entry in the map of modelAPs indicates that the property is at least somewhere true in the model. ");
        comments.add("In other words: if a property is always FALSE( i.e. in all states/edges)  then it is NOT regarded as a modelAp and NOT in the map");
        comments.add("Note that the map is not guaranteed in lexicographic order: some new (true) properties can be discovered 'late' in the model");
        comments.add("relpos expressions are the quadrants  based on the position of the widget  in the parent window");
        comments.add("this enables to distinguish 2 buttons with the same title in the relative window in 2 different states");
        comments.add("this is not functional yet. CSS 20190630");
    }
    public APSelectorManager(boolean initializeWithDefaults) {
        this();
        if (initializeWithDefaults){
            setValuedExpressions(useStandardValuedExpressions());
            setSelectedAttributes(getEntireAttributeTagSet());
            WidgetFilter wf = new WidgetFilter();
            wf.setDefaultWidgetFilter();
            widgetfilters.add(wf);
            setRoleTitlePathAsAPKey();
            this.apEncodingSeparator= APEncodingSeparator.DOUBLEDAGGER.symbol;
        }

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

    public void setAPKey(List<String> APKey) {
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

    public void setRoleTitleAsAPKey(){
        APKey.clear();
        APKey.add(Tags.Role.name());
        APKey.add(Tags.Title.name());
    }

    public void addWidgetFilter(WidgetFilter w){
        widgetfilters.add(w);
    }


    //custom

    public List<WidgetFilter> passWidgetFilters(String role, String title, String path){
        boolean pass=false;
        List<WidgetFilter> passedWidgetFilters= new ArrayList<>();
        //pass if at least one of the roles match and at least one of the title matches and at least one of the path's matches
        // make disjunct with all widget filters
        if (role==null &&title==null && path==null) {// regain control actions by TESTAR itself, usually doe snot change state
            role = "TESTAR";
            title="TESTAR";
            path="TESTAR";
        }

        for (WidgetFilter wf:widgetfilters
        ) {
            pass = (wf.getWidgetRolesMatches().size() == 0) ;  // no filter means pass
            if (!pass) {
                for (String frole : wf.getWidgetRolesMatches()
                ) {
                    pass = role.matches(frole);
                    if (pass) break;
                }
            }

            if (!pass) continue;  //if still not passed, check the next widgetfilter as this one will never pass.
            pass = (wf.getWidgetTitleMatches().size() == 0) ;
            if (!pass) {
                for (String ftitle : wf.getWidgetTitleMatches()
                ) {
                    pass = title.matches(ftitle);
                    if (pass) break;
                }
            }

            if (!pass) continue;
            pass = (wf.getWidgetPathMatches().size() == 0) ;
            if (!pass) {

                for (String fpath : wf.getWidgetPathMatches()
                ) {
                    pass = path.matches(fpath);
                    if (pass) break;
                }
            }
//            if (!pass) continue;
//            pass= (wf.getWidgetParentTitleMatches().size()==0);
//            if (!pass) {
//
//                for (String fppath : wf.getWidgetParentTitleMatches()
//                ) {
//                    pass = role.matches(fppath);
//                    if (pass) break;
//                }
//            }

            if (pass) {
                passedWidgetFilters.add(wf);
               // break; // role, title and path matches, that is enough
            }
        }


        return passedWidgetFilters; //pass;
    }
}
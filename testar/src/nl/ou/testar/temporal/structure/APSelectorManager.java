package nl.ou.testar.temporal.structure;

import nl.ou.testar.StateModel.Persistence.OrientDB.Util.Validation;
import nl.ou.testar.temporal.util.APEncodingSeparator;
import nl.ou.testar.temporal.util.InferrableExpression;
import nl.ou.testar.temporal.util.PairBean;
import nl.ou.testar.temporal.util.TagBean;
import org.fruit.alayer.Shape;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.windows.UIATags;

import java.util.*;

//@JsonRootName(value="TemporalProperties")
public class APSelectorManager {



    private List<String> APKey= new ArrayList<>();
    private Set<TagBean<?>> selectedAttributes;
    private Set<TagBean<?>> entireAttributeSet ;
    private  Set<PairBean<InferrableExpression,String>> valuedExpressions = new LinkedHashSet<>();
    private String apEncodingSeparator;
    private Set<WidgetFilter> widgetfilters;
    public Set<PairBean<InferrableExpression, String>> getValuedExpressions() {
        return valuedExpressions;
    }
    private String formatVersion="20190713";
    private List<String> comments = new ArrayList<>();




    public APSelectorManager() {
        entireAttributeSet = getEntireTagSet();
        entireAttributeSet.add(createDeadStateTag());
        widgetfilters = new LinkedHashSet<>();
        selectedAttributes = new LinkedHashSet<TagBean<?>>();
        selectedAttributes.add(createDeadStateTag());
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
            setDefaultValuedExpressions();
            setDefaultAttributes();
            setDefaultWidgetFilter();
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
    public void setValuedExpressions(Set<PairBean<InferrableExpression, String>> valuedExpressions) {
        if (valuedExpressions!=null) this.valuedExpressions = valuedExpressions;
        this.valuedExpressions.add(new PairBean<>(InferrableExpression.is_blank_, ""));  // use always
        this.valuedExpressions.add(new PairBean<>(InferrableExpression.exists_, ""));
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



    private Set<TagBean<?>> getEntireTagSet(){


        // WORKAROUND CSS 20190629
        // the 2 dummy reads are required to ensure properly initialization of the classes: static method/property is used!
        // both classes Tags and UIATags inherit from abstract class TagBase
        //without this initialization, the call to .tagset() from either class collides into the same tagset content.
        // symptom: UIATags appears to have the same tags as Tags and we're missing out on the real UIATags.

        Tag<?> dummy = UIATags.UIAItemType;
        dummy=Tags.Enabled;

        Set<Tag<?>> tags = new HashSet<Tag<?>>();
        tags.addAll(Tags.tagSet());
        tags.addAll(UIATags.tagSet());//alternative for platform independent is : getNativetags ??
        Set<TagBean<?>> tmptagset=new LinkedHashSet<>();
        Iterator<Tag<?>> iterator;
        for (iterator = tags.iterator(); iterator.hasNext(); ) {
            Tag<?> t = iterator.next();
            TagBean<?> t1 = TagBean.from(Validation.sanitizeAttributeName(t.name()), t.type()); //orientdb style tags
            tmptagset.add(t1);
        }
        return  tmptagset;
    };

private TagBean<?> createDeadStateTag() {
    //TagBean<?> dtag = new TagBean<>("IsDeadState", Boolean.class); // refactoring candidate
    TagBean<?> dtag = TagBean.IsDeadState;

    return dtag;


}


    private  Set<String> retrieveSelectedSanitizedAttributeNames() {
        Set<String> tagNames = new HashSet<>();
        for (TagBean<?> tb:selectedAttributes
             ) {tagNames.add(Validation.sanitizeAttributeName(tb.name()));
        }
        return tagNames;
    }

    public Set<TagBean<?>> getSelectedAttributes() {
        return selectedAttributes;
    }
    public void setSelectedAttributes(Set<TagBean<?>> selectedAttributes) {

        this.selectedAttributes = selectedAttributes;
        this.selectedAttributes.add(createDeadStateTag()); // include always this custom 'tag'
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

    public void setDefaultWidgetFilter(){
        widgetfilters.add(new WidgetFilter(valuedExpressions));
    }
    public void addWidgetFilter(WidgetFilter w){
        widgetfilters.add(w);
    }


    public void setDefaultOnlyBooleanAttributes() {
        for (TagBean<?> tag : entireAttributeSet
        ) {
            if (tag.type() == Boolean.class) {
                selectedAttributes.add(tag);
            }
        }
    }
    public void setDefaultAttributes() {
        selectedAttributes = entireAttributeSet;
    }

    public void addAttribute(String attrib){

        for (TagBean<?> tag : entireAttributeSet
        ) {
            if ( tag.name().equals(attrib)) {
                selectedAttributes.add(tag);
                break;
            }
        }
    }

    public void setDefaultValuedExpressions() {
        //this.valuedExpressions = new InferrableValuedExpressions(true);
        useDefaultValuedExpressions();
    }

    public void removeAttribute(String attrib){
        for (TagBean<?> tag : entireAttributeSet
        ) {
            if ( tag.name().equals(attrib)) {
                selectedAttributes.remove(tag);
                break;
            }
        }
    }
    private TagBean<?> getTag(String attrib){
        TagBean<?> ret=null;
        for (TagBean<?> tag : selectedAttributes
        ) {
            if ( tag.name().equals(attrib)){
                ret=tag;
                break;
            }
        }
        return  ret;
    }
    private TagBean<?> getTag(String attrib,WidgetFilter wf){
        TagBean<?> ret=null;
        for (TagBean<?> tag : selectedAttributes
        ) {
            if ( tag.name().equals(attrib)){
                ret=tag;
                break;
            }
        }
        return  ret;
    }
    public boolean contains(String attrib){

        if(getTag(attrib)!=null){
            return true;
        }
        return false;
    }


    private void useDefaultValuedExpressions() {
        //valuedExpressions.clear();
        setValuedExpressions(null); //initialize and include mandatory ones
        valuedExpressions.addAll(WidgetFilter.useDefaultValuedExpressions());//new PairBean<>(InferrableExpression.value_eq_, "0"));

    }


    //custom
    public void addExpressionPattern(InferrableExpression ip, String value) {
        valuedExpressions.add(new PairBean<>(ip,value));
    }
    public void removeExpressionPattern(InferrableExpression ip, String value) {
        valuedExpressions.remove(new PairBean<>(ip,value));
    }

    public boolean addExpressionPattern(String patternStr) {
        boolean succes = false;  //remains certainly false if pattern is not found
        for (InferrableExpression iap : InferrableExpression.values()) {
            if (patternStr.startsWith(String.valueOf(iap))) {
                int iapSize = String.valueOf(iap).length();
                String value = patternStr.substring(iapSize);
                try {
                    double dbl = Double.parseDouble(value); //test if it is a number
                    valuedExpressions.add(new PairBean<>(iap, value));
                    succes = true;
                    break;
                } catch (NumberFormatException e) {
                    succes = false;
                    //e.printStackTrace();
                }

            }
        }
        return succes;
    }


    public Set<String> getAPsOfAttribute(String widgetkey, String attrib, String value) {
        //System.out.println("debug getAPOfAttributes entered with apkey: "+ widgetkey+ " attrib: "+attrib+" value: "+ value);
        Set<String> apset = new LinkedHashSet<>();
        TagBean<?> tag = getTag(attrib);

        if (tag != null) {   //this attribute is required as a(n) (set of) AP .. result is dependent on selectedattributes

            if (tag.type() == Boolean.class ) {
               // apset.add(widgetkey +  attrib + "_"+Boolean.parseBoolean(value)+"__"); //encode both TRUE  FALSE for genuine booleans
                if ( Boolean.parseBoolean(value)) {
                    apset.add(widgetkey + attrib + "__");
                }
            } else

                for (PairBean<InferrableExpression, String> iap : valuedExpressions
                ) {
                    if (iap.left().typ == "number" && (tag.type() == Double.class || tag.type() == Long.class || tag.type() == Integer.class)) {
                        int intVal = (int) Double.parseDouble(value);
                        if (((iap.left() == InferrableExpression.value_eq_) && (intVal == Integer.parseInt(iap.right()))) ||
                                ((iap.left() == InferrableExpression.value_lt_) && (intVal < Integer.parseInt(iap.right())))) {
                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "__");// just encode the TRUE/existence  and FALSE is then considered absence
                        }
                    }
                    if (iap.left().typ == "text" && (tag.type() == String.class)) {

                        if (((iap.left() == InferrableExpression.textmatch_) && value.matches(iap.right())) ||
                                ((iap.left() == InferrableExpression.textlength_eq_) && (value.length() == Integer.parseInt(iap.right()))) ||
                                ((iap.left() == InferrableExpression.textlength_lt_) && (value.length() < Integer.parseInt(iap.right())))
                        ) {
                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "__");
                        }

                    }
                    if (iap.left().typ == "shape" && (tag.type() == Shape.class)) {
                        //format:   <data key="Shape">Rect [x:459.0 y:243.0 w:116.0 h:18.0]</data>
                        //String[] shapecomponents=value.split("w:")[1];
                        String test = value.split("w:")[1].split(" ")[0];
                        int width = (int) Double.parseDouble(test);
                        test = value.split("h:")[1].split("]")[0];
                        int heigth = (int) Double.parseDouble(test);

                        if (((iap.left() == InferrableExpression.width_lt_) && (width < Integer.parseInt(iap.right()))) ||
                                ((iap.left() == InferrableExpression.heigth_lt_) && (heigth < Integer.parseInt(iap.right())))
                        ) {
                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "__");
                        }
                    }
                    if (iap.left().typ == "path" && (tag.type() == String.class)) {
                        //format:     <data key="Path">[0, 0, 8]</data>
                        if ((iap.left() == InferrableExpression.pathmatch_) && value.matches(iap.right())) {
                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "__");
                        }
                    }
                    if (iap.left().typ == "boolean") {   //add these regardless of the tag-type
                        //format:     <data key="Abc"></data>
                        if ((iap.left() == InferrableExpression.is_blank_) && value.matches("")) {
                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "_"); //note : 1 space only
                        }
                        if ((iap.left() == InferrableExpression.exists_) ) {
                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "_");
                        }

                    }
                }
        }
        return apset;
    }

    public boolean passWidgetFilters(String role, String title, String path){
        boolean pass=false;
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

            if (!pass) continue;  //if still not passed, chck the next widgetfilter
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

            if (pass) break; // role, title and path matches, that is enough
        }


        return pass;
    }
}
package nl.ou.testar.temporal.structure;

import nl.ou.testar.StateModel.Persistence.OrientDB.Util.Validation;
import nl.ou.testar.temporal.util.InferrableExpression;
import nl.ou.testar.temporal.util.PairBean;
import nl.ou.testar.temporal.util.TagBean;
import org.fruit.alayer.Shape;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.windows.UIARoles;
import org.fruit.alayer.windows.UIATags;

import java.util.*;

//@JsonRootName(value="TemporalProperties")
public class APSelectorManager {



    private List<String> APKey= new ArrayList<>();
    private Set<TagBean<?>> selectedAttributes;



    //private BiMap<String, Class<?>> selectedAttributes1;
    private Set<TagBean<?>> entireAttributeSet ;
    private  Set<PairBean<InferrableExpression,String>> valuedExpressions = new LinkedHashSet<>();
    private Set<WidgetFilter> widgetfilters;
    public Set<PairBean<InferrableExpression, String>> getValuedExpressions() {
        return valuedExpressions;
    }
    private String formatVersion="20190630 ";

    private List<String> comments = new ArrayList<>();




    public APSelectorManager() {
        entireAttributeSet = getEntireTagSet();
        widgetfilters = new LinkedHashSet<>();
        selectedAttributes = new LinkedHashSet<TagBean<?>>();
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
        }

    }

    public List<String> getAPKey() {
        return APKey;
    }
    /*public BiMap<String, Class<?> > getSelectedAttributes1() {
        return selectedAttributes1;
    }

    public void setSelectedAttributes1(BiMap<String, Class<?> > selectedAttributes1) {
        this.selectedAttributes1 = selectedAttributes1;
    }*/
    //public  String getAttributeName(Class<?> clazz) {  return selectedAttributes1.inverse().get(clazz);}

    //public Class<?> getAttributeType(String name) { return selectedAttributes1.get(name);   }


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
        // symptom: UIATags appears to have the same tags as Tags and we're mising out on the real UIATags.

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

    public boolean contains(String attrib){

        if(getTag(attrib)!=null){
            return true;
        }
        return false;
    }


    private void useDefaultValuedExpressions() {
        //valuedExpressions.clear();
        setValuedExpressions(null); //initialize
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_eq_, "0"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_eq_, "0"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_eq_, "1"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_eq_, "2"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "10"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "100"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "1000"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "10000"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "100000"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "1000000"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OK)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CANCEL)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:YES)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:NO)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:GO)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:RUN)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SAVE)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:EXIT)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CLOSE)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:REMOVE)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:ERROR)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SUBMIT)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OPEN)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:IGNORE)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:PROCEED)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:PRINT)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:VIEW)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:UP)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:DOWN)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:LEFT)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:RIGHT)"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "")); //no title
        valuedExpressions.add(new PairBean<>(InferrableExpression.pathmatch_, ".*\\[(\\d+, )*\\d+\\]"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.heigth_lt_, "50"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.heigth_lt_, "250"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.heigth_lt_, "500"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.heigth_lt_, "1000"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.width_lt_, "50"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.width_lt_, "250"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.width_lt_, "500"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.width_lt_, "1000"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textlength_eq_, "1"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textlength_eq_, "2"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textlength_eq_, "3"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "10"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "20"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "50"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "100"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "200"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.is_blank_, ""));
        valuedExpressions.add(new PairBean<>(InferrableExpression.exists_, ""));
        valuedExpressions.add(new PairBean<>(InferrableExpression.relpos_upleft_, ""));
        valuedExpressions.add(new PairBean<>(InferrableExpression.relpos_upright_, ""));
        valuedExpressions.add(new PairBean<>(InferrableExpression.relpos_downleft_, ""));
        valuedExpressions.add(new PairBean<>(InferrableExpression.relpos_downright_, ""));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAButton.toString()));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAWindow.toString()));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIACheckBox.toString()));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIARadioButton.toString()));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAEdit.toString()));

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

        if (tag != null) {   //this attribute is required as a(n) (set of) AP

            if (tag.type() == Boolean.class) {
                apset.add(widgetkey +  attrib + "_"+Boolean.parseBoolean(value)+"__"); //encode both TRUE  FALSE for genuine booleans
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
        //System.out.println("debug getAPOfAttributes exit with apset size: "+ apset.size());
      //  if (apset.size()>0) System.out.println("debug getAPOfAttributes exit with apset size and element0: "+apset.size()+" : "+ apset.iterator().next());
      //  if (apset.size()>0) System.out.println("debug getAPOfAttributes exit with apset size and element0: "+apset.size()+" : "+ apset.iterator().next());

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
//
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
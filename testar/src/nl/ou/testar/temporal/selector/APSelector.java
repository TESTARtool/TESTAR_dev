package nl.ou.testar.temporal.selector;

import nl.ou.testar.StateModel.Persistence.OrientDB.Util.Validation;
import nl.ou.testar.temporal.util.CachedRegexPatterns;
import nl.ou.testar.temporal.foundation.InferrableExpression;
import nl.ou.testar.temporal.foundation.PairBean;
import nl.ou.testar.temporal.foundation.TagBean;
import org.fruit.alayer.Shape;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.windows.UIARoles;
import org.fruit.alayer.windows.UIATags;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APSelector {
    private Set<String> selectedAttributes;
    private Set<PairBean<InferrableExpression,String>> selectedExpressions;



    public APSelector() {

        selectedAttributes=new LinkedHashSet<>();
        selectedExpressions =new LinkedHashSet<>();

    }

    public static final String deadStateTag() {
        //TagBean<?> dtag = new TagBean<>("IsDeadState", Boolean.class); // refactoring candidate
        String dtag = TagBean.IsDeadState.getName();
        return dtag;
    }

    public static final Set<String> getEntireAttributes(){
        // WORKAROUND CSS 20190629
        // the 2 dummy reads are required to ensure properly initialization of the classes: static method/property is used!
        // both classes Tags and UIATags inherit from abstract class TagBase
        //without this initialization, the call to .tagset() from either class collides into the same tagset content.
        // symptom: UIATags appears to have the same tags as Tags and we're missing out on the real UIATags.
        Tag<?> dummy = UIATags.UIAItemType;
        dummy= Tags.Enabled;
        Set<Tag<?>> tags = new HashSet<Tag<?>>();
        tags.addAll(Tags.tagSet());
        tags.addAll(UIATags.tagSet());//alternative for platform independent is : getNativetags ??
        Set<String> tmptagset=new LinkedHashSet<>();
        Iterator<Tag<?>> iterator;
        for (iterator = tags.iterator(); iterator.hasNext(); ) {
            Tag<?> t = iterator.next();
            String t1 = Validation.sanitizeAttributeName(t.name()); //orientdb style tags
            tmptagset.add(t1);
        }
        tmptagset.addAll(useMinimalAttributes());

        return  tmptagset;
    }
    public static final Set<String> getAllAttributeNames(){
        Set<TagBean<?>> tags = getAllAttributeTags();
        Set<String> tmptagset=new LinkedHashSet<>();
        Iterator<TagBean<?>> iterator;
        for (iterator = tags.iterator(); iterator.hasNext(); ) {
            TagBean<?> t = iterator.next();
            tmptagset.add(t.name());
        }
        tmptagset.addAll(useMinimalAttributes());
        return  tmptagset;
    }

    public static final Set<TagBean<?>> getAllAttributeTags(){
        // WORKAROUND CSS 20190629
        // the 2 dummy reads are required to ensure properly initialization of the classes: static method/property is used!
        // both classes Tags and UIATags inherit from abstract class TagBase
        //without this initialization, the call to .tagset() from either class collides into the same tagset content.
        // symptom: UIATags appears to have the same tags as Tags and we're missing out on the real UIATags.
        Tag<?> dummy = UIATags.UIAItemType;
        dummy= Tags.Enabled;
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
        tmptagset.add(TagBean.IsDeadState);
        return  tmptagset;
    }

    public static final Set<String> useMinimalAttributes(){
        Set<String> tmptagset=new LinkedHashSet<>();
        Tag<?> t = Tags.Role;// is the only attribute existing for ALL transactions. (e.g.ROLE doesn't exist for a Virtual Key)
        tmptagset.add(Validation.sanitizeAttributeName(t.name())); //orientdb style tags
        return  tmptagset;
    }
    public static final Set<String> useMinimalTransAttributes(){
        Set<String> tmptagset=new LinkedHashSet<>();
        Tag<?> t = Tags.Desc;// is the only attribute existing for ALL transactions. (e.g.ROLE doesn't exist for a Virtual Key)
        tmptagset.add(Validation.sanitizeAttributeName(t.name())); //orientdb style tags
        return  tmptagset;
    }

    public static final Set<String> useBasicAttributes() {
        Set<String> tmptagset=new LinkedHashSet<>();
        Set<String> basicset = new HashSet<>();
        basicset.add(Tags.Title.name());
        basicset.add(Tags.Path.name());
        basicset.add(Tags.Role.name());
        basicset.add(Tags.Desc.name());
        basicset.add(Tags.ZIndex.name());
        basicset.add(Tags.Blocked.name());
        basicset.add(Tags.Shape.name());

        for (String tag : getAllAttributeNames()
        ) {
            if (basicset.contains(tag)) {
                tmptagset.add(tag);
            }
        }
        tmptagset.addAll(useMinimalAttributes());
        return tmptagset;
    }


    public static final Set<PairBean<InferrableExpression,String>> useMinimalSelectedExpressions() {
        Set<PairBean<InferrableExpression, String>> minve = new LinkedHashSet<>();
        minve.add(new PairBean<>(InferrableExpression.exists_, ""));// use always
        return minve;
    }
    public static final Set<PairBean<InferrableExpression,String>> useMinimalTransSelectedExpressions() {
        // following list is derived by inspecting AnnotatingActionCompiler
        Set<PairBean<InferrableExpression, String>> minve = new LinkedHashSet<>();
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:Left Click)"));
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:Right Click)"));
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:Drag)"));
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:Left Double Click)"));
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:Replace ')"));
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:Append ')"));
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:Hit Shortcut Key)"));
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:Hit Key)"));
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:Bring the system to the foreground)"));
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i: Kill Process)"));
        return minve;
    }



    public static final Set<PairBean<InferrableExpression,String>> useRoleConditionalExpressions() {
        Set<PairBean<InferrableExpression, String>> minve = new LinkedHashSet<>();
        minve.add(new PairBean<>(InferrableExpression.textmatch_, UIARoles.UIAButton.name()+"|"+
                UIARoles.UIAMenu.name()+"|"+ UIARoles.UIAMenuItem.name()+"|"+ UIARoles.UIACheckBox.name()+"|"+
                UIARoles.UIARadioButton.name()+"|"+ UIARoles.UIAWindow.name()));  // use always
        return minve;
    }
    public static final Set<PairBean<InferrableExpression,String>> usePathConditionalExpressions() {
        Set<PairBean<InferrableExpression, String>> minve = new LinkedHashSet<>();
       minve.add(new PairBean<>(InferrableExpression.textmatch_, ".*\\[(\\d+, )*\\d+\\]"));// use always
        return minve;

    }

    public static final Set<PairBean<InferrableExpression, String>> useTitleConditionalExpressions() {
        Set<PairBean<InferrableExpression, String>> minve = new LinkedHashSet<>();
        minve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OK)|(?i:CANCEL)|(?i:YES)|(?i:NO)|" +
                "(?i:RUN)|(?i:SAVE)|(?i:EXIT)|(?i:CLOSE)|(?i:REMOVE)|(?i:ERROR)|" +
                "(?i:SUBMIT)|(?i:OPEN)|(?i:IGNORE)|(?i:PROCEED)|(?i:PRINT)|(?i:VIEW)|" +
                "(?i:UP)|(?i:DOWN)|(?i:LEFT)|(?i:RIGHT)"));
        return minve;
    }
    //

    public static final Set<PairBean<InferrableExpression,String>> useBasicSelectedExpressions() {
        Set<PairBean<InferrableExpression,String>> defve = new LinkedHashSet<>();
        defve.addAll(useMinimalSelectedExpressions());
        defve.add(new PairBean<>(InferrableExpression.value_eq_, "0"));
        defve.add(new PairBean<>(InferrableExpression.value_eq_, "1"));
        defve.add(new PairBean<>(InferrableExpression.value_eq_, "2"));
        defve.add(new PairBean<>(InferrableExpression.value_lt_, "10"));
        defve.add(new PairBean<>(InferrableExpression.value_lt_, "100"));
        defve.add(new PairBean<>(InferrableExpression.value_lt_, "1000"));
        defve.add(new PairBean<>(InferrableExpression.value_lt_, "10000"));
        defve.add(new PairBean<>(InferrableExpression.value_lt_, "100000"));
        defve.add(new PairBean<>(InferrableExpression.value_lt_, "1000000"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OK)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CANCEL)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:YES)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:NO)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:GO)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:RUN)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SAVE)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:EXIT)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CLOSE)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:REMOVE)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:ERROR)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SUBMIT)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OPEN)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:IGNORE)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:PROCEED)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:PRINT)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:VIEW)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:UP)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:DOWN)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:LEFT)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:RIGHT)"));
        defve.add(new PairBean<>(InferrableExpression.textmatch_, "")); //no title
        defve.add(new PairBean<>(InferrableExpression.textmatch_, ".*\\[(\\d+, )*\\d+\\]"));
        defve.add(new PairBean<>(InferrableExpression.heigth_lt_, "50"));
        defve.add(new PairBean<>(InferrableExpression.heigth_lt_, "250"));
        defve.add(new PairBean<>(InferrableExpression.heigth_lt_, "500"));
        defve.add(new PairBean<>(InferrableExpression.heigth_lt_, "1000"));
        defve.add(new PairBean<>(InferrableExpression.width_lt_, "50"));
        defve.add(new PairBean<>(InferrableExpression.width_lt_, "250"));
        defve.add(new PairBean<>(InferrableExpression.width_lt_, "500"));
        defve.add(new PairBean<>(InferrableExpression.width_lt_, "1000"));
        defve.add(new PairBean<>(InferrableExpression.textlength_eq_, "1"));
        defve.add(new PairBean<>(InferrableExpression.textlength_eq_, "2"));
        defve.add(new PairBean<>(InferrableExpression.textlength_eq_, "3"));
        defve.add(new PairBean<>(InferrableExpression.textlength_lt_, "10"));
        defve.add(new PairBean<>(InferrableExpression.textlength_lt_, "20"));
        defve.add(new PairBean<>(InferrableExpression.textlength_lt_, "50"));
        defve.add(new PairBean<>(InferrableExpression.textlength_lt_, "100"));
        defve.add(new PairBean<>(InferrableExpression.textlength_lt_, "200"));
        defve.add(new PairBean<>(InferrableExpression.is_blank_, ""));
        return defve;
    }

    public Set<String> getSelectedAttributes() {
        return selectedAttributes;
    }

    public void setSelectedAttributes(Set<String> selectedAttributes) {
        if (selectedAttributes.size()==0){
            this.selectedAttributes.addAll(useMinimalAttributes());
        }else {
            this.selectedAttributes = selectedAttributes; //responsibility of the test manager
        }
    }
    public void setSelectedStateAttributes(Set<String> selectedAttributes) {
        if (selectedAttributes.size()==0){
            this.selectedAttributes.addAll(useMinimalAttributes());
        }else {
            this.selectedAttributes = selectedAttributes; //responsibility of the test manager
            this.selectedAttributes.add(deadStateTag());
        }
    }

    public String getTag(String attrib){
        String ret=null;
        for (String tag : selectedAttributes  // consider to pass if selected atributes = empty?
        ) {
            if ( tag.equals(attrib)){
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

    public Set<PairBean<InferrableExpression, String>> getSelectedExpressions() {
        return selectedExpressions;
    }

    public void setSelectedExpressions(Set<PairBean<InferrableExpression, String>> selectedExpressions) {
        if (selectedExpressions.size()==0){
            this.selectedExpressions.addAll(useMinimalSelectedExpressions());
        }else{
            this.selectedExpressions = selectedExpressions; //responsibility of the test manager
        }
    }

    //custom

    public Set<String> getAPsOfAttribute(String widgetkey, String attrib, String value) {
        Set<String> apset = new LinkedHashSet<>();
        String stag = getTag(attrib);


        if (stag != null) {   //this attribute is not available in the model then no AP can be calculated.
            //lookup type
            TagBean<?> tag=null;  //refactor candidate
            for (TagBean<?> tg: getAllAttributeTags()
                 ) {
                if(tg.getName().equals(stag)){
                    tag=tg;
                    break;
                }
            }


            if (tag.type() == Boolean.class ) {
                // apset.add(widgetkey +  attrib + "_"+Boolean.parseBoolean(value)+"__"); //encode both TRUE  FALSE for genuine booleans
                if ( Boolean.parseBoolean(value)) {
                    apset.add(widgetkey + attrib + "__");
                }
            } else

                for (PairBean<InferrableExpression, String> iap : getSelectedExpressions()
                ) {
                    if (iap.left().typ == "number" && (tag.type() == Double.class || tag.type() == Long.class || tag.type() == Integer.class)) {
                        int intVal = (int) Double.parseDouble(value);
                        if (((iap.left() == InferrableExpression.value_eq_) && (intVal == Integer.parseInt(iap.right()))) ||
                                ((iap.left() == InferrableExpression.value_lt_) && (intVal < Integer.parseInt(iap.right())))) {
                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "__");// just encode the TRUE/existence  and FALSE is then considered absence
                        }
                    }
                    if (iap.left().typ == "text" ){ // && (tag.type() == String.class)) { // now also handles specific classes

                        if (((iap.left() == InferrableExpression.textlength_eq_) && (value.length() == Integer.parseInt(iap.right()))) ||
                                ((iap.left() == InferrableExpression.textlength_lt_) && (value.length() < Integer.parseInt(iap.right())))) {
                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "__");
                        }

                        if (((iap.left() == InferrableExpression.textmatch_) )) {
                            Pattern pat = CachedRegexPatterns.addAndGet(iap.right());
                            Matcher m=pat.matcher(value);
                            if (m.matches()){
                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "__");
                        }
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

                    if (iap.left().typ == "boolean") {   //add these regardless of the tag-type
                        //format:     <data key="Abc"></data>
                        if ((iap.left() == InferrableExpression.is_blank_) && value.equals("")) {
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

    public void addExpressionPattern(InferrableExpression ip, String value) {
        selectedExpressions.add(new PairBean<>(ip,value));
    }

    public boolean addExpressionPattern(String patternStr) {
        boolean succes = false;  //remains certainly false if pattern is not found
        for (InferrableExpression iap : InferrableExpression.values()) {
            if (patternStr.startsWith(String.valueOf(iap))) {
                int iapSize = String.valueOf(iap).length();
                String value = patternStr.substring(iapSize);
                try {
                    double dbl = Double.parseDouble(value); //test if it is a number
                    selectedExpressions.add(new PairBean<>(iap, value));
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

}


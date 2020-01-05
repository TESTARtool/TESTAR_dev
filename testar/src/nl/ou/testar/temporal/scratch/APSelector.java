package nl.ou.testar.temporal.scratch;

import nl.ou.testar.StateModel.Persistence.OrientDB.Util.Validation;
import nl.ou.testar.temporal.structure.WidgetFilter;
import nl.ou.testar.temporal.util.InferrableExpression;
import nl.ou.testar.temporal.util.PairBean;
import nl.ou.testar.temporal.util.TagBean;
import org.fruit.alayer.Roles;
import org.fruit.alayer.Shape;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.windows.UIARoles;
import org.fruit.alayer.windows.UIATags;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class APSelector {
    private Set<TagBean<?>> selectedAttributes;
    private Set<PairBean<InferrableExpression,String>> valuedExpressions;



    public APSelector() {

        selectedAttributes=new LinkedHashSet<>();
        valuedExpressions =new LinkedHashSet<>();

    }
    public static final String reflectFieldSelectedAttributes(){
        String name= APSelector.class.getDeclaredFields()[0].getName();
        return name;
    }
    public static final String reflectFieldvaluedExpressions(){
        String name= APSelector.class.getDeclaredFields()[1].getName();
        return name;
    }

    public static final TagBean<?> deadStateTag() {
        //TagBean<?> dtag = new TagBean<>("IsDeadState", Boolean.class); // refactoring candidate
        TagBean<?> dtag = TagBean.IsDeadState;

        return dtag;
    }

    public static final Set<TagBean<?>> getEntireAttributes(){
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
        tmptagset.addAll(getMinimalAttributes());
        return  tmptagset;
    }
    public static final Set<TagBean<?>> getMinimalAttributes(){
        // to prevent that edges are totally filtered away.
        Set<TagBean<?>> tmptagset=new LinkedHashSet<>();
        Tag<?> t= Tags.Enabled;
        tmptagset.add(TagBean.from(Validation.sanitizeAttributeName(t.name()), t.type())); //orientdb style tags
        t = Tags.Role;
        tmptagset.add(TagBean.from(Validation.sanitizeAttributeName(t.name()), t.type())); //orientdb style tags
        tmptagset.add(deadStateTag());  // actually only of interest for states
        return  tmptagset;
    }

    public static final Set<TagBean<?>> getBasicAttributes() {
        Set<TagBean<?>> tmptagset=new LinkedHashSet<>();
        Set<String> basicset = new HashSet<>();
        //basicset.add("Role");
        basicset.add("Title");
        basicset.add("Path");
        basicset.add("Desc");
        basicset.add("ZIndex");
        basicset.add("Blocked");
        basicset.add("Shape");

        for (TagBean<?> tag : getEntireAttributes()
        ) {
            if (basicset.contains(tag.name())) {
                tmptagset.add(tag);
            }
        }
        tmptagset.addAll(getMinimalAttributes());
        return tmptagset;
    }

    public static final Set<PairBean<InferrableExpression,String>> useBasicValuedExpressions() {
        Set<PairBean<InferrableExpression,String>> simve = new LinkedHashSet<>();
        simve.addAll(useMinimalValuedExpressions());
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OK)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CANCEL)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:YES)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:NO)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:RUN)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SAVE)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:EXIT)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CLOSE)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:ERROR)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SUBMIT)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OPEN)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:IGNORE)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:VIEW)"));
        simve.add(new PairBean<>(InferrableExpression.textmatch_, "")); //no title
        simve.add(new PairBean<>(InferrableExpression.width_lt_, "100"));
        simve.add(new PairBean<>(InferrableExpression.textlength_lt_, "20"));
        return simve;

    };
    public static final Set<PairBean<InferrableExpression,String>>  useMinimalValuedExpressions() {
        Set<PairBean<InferrableExpression, String>> minve = new LinkedHashSet<>();
        minve.add(new PairBean<>(InferrableExpression.is_blank_, ""));  // use always
        minve.add(new PairBean<>(InferrableExpression.exists_, ""));// use always
        return minve;
    }

    public static final Set<PairBean<InferrableExpression,String>>  useStandardValuedExpressions() {
        Set<PairBean<InferrableExpression,String>> defve = new LinkedHashSet<>();
        defve.addAll(useMinimalValuedExpressions());
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
//        defve.add(new PairBean<>(InferrableExpression.relpos_upleft_, ""));
//        defve.add(new PairBean<>(InferrableExpression.relpos_upright_, ""));
//        defve.add(new PairBean<>(InferrableExpression.relpos_downleft_, ""));
//        defve.add(new PairBean<>(InferrableExpression.relpos_downright_, ""));
        return defve;
    }

    public Set<TagBean<?>> getSelectedAttributes() {
        return selectedAttributes;
    }

    public void setSelectedAttributes(Set<TagBean<?>> selectedAttributes) {
        if (selectedAttributes.size()==0){
            this.selectedAttributes.addAll(getMinimalAttributes());
        }else {
            this.selectedAttributes = selectedAttributes; //responsibility of the test manager
            this.selectedAttributes.add(deadStateTag());
        }
    }

    public void setDefaultOnlyBooleanAttributes() {
        for (TagBean<?> tag : getEntireAttributes()
        ) {
            if (tag.type() == Boolean.class) {
                selectedAttributes.add(tag);
            }
        }
        selectedAttributes.add(deadStateTag());
    }

    public void addAttribute(String attrib){

        for (TagBean<?> tag : getEntireAttributes()
        ) {
            if ( tag.name().equals(attrib)) {
                selectedAttributes.add(tag);
                break;
            }
        }
    }

    public TagBean<?> getTag(String attrib){
        TagBean<?> ret=null;
        for (TagBean<?> tag : selectedAttributes  // consider to pass if selected atributes = emp ty?
        ) {
            if ( tag.name().equals(attrib)){
                ret=tag;
                break;
            }
        }
        return  ret;
    }

    public  TagBean<?> getTag(String attrib,WidgetFilter wf){
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

    public Set<PairBean<InferrableExpression, String>> getValuedExpressions() {
        return valuedExpressions;
    }

    public void setValuedExpressions(Set<PairBean<InferrableExpression, String>> valuedExpressions) {
        if (valuedExpressions.size()==0){
            this.valuedExpressions.addAll(useMinimalValuedExpressions());
        }else{
            this.valuedExpressions = valuedExpressions; //responsibility of the test manager
        }

    }



    //custom

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

                for (PairBean<InferrableExpression, String> iap : getValuedExpressions()
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
//                    if (iap.left().typ == "path" && (tag.type() == String.class)) {
//                        //format:     <data key="Path">[0, 0, 8]</data>
//                        if ((iap.left() == InferrableExpression.pathmatch_) && value.matches(iap.right())) {
//                            apset.add(widgetkey +  attrib + "_" + iap.left().name() + iap.right() + "__");
//                        }
//                    }
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




    public void addExpressionPattern(InferrableExpression ip, String value) {
        valuedExpressions.add(new PairBean<>(ip,value));
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

}


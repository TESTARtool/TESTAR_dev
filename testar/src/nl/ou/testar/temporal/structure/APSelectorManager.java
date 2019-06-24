package nl.ou.testar.temporal.structure;

import nl.ou.testar.temporal.util.InferrableExpression;
import nl.ou.testar.temporal.util.PairBean;
import nl.ou.testar.temporal.util.TagBean;
import org.fruit.alayer.Shape;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.windows.UIARoles;
import org.fruit.alayer.windows.UIATags;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

//@JsonRootName(value="TemporalProperties")
public class APSelectorManager {

    private Set<TagBean<?>> selectedAttributes;
    private Set<TagBean<?>> entireAttributeSet ;
    private  Set<PairBean<InferrableExpression,String>> valuedExpressions = new LinkedHashSet<>();
    private Set<WidgetFilter> widgetfilters;

    public Set<PairBean<InferrableExpression, String>> getValuedExpressions() {
        return valuedExpressions;
    }

    public void setValuedExpressions(Set<PairBean<InferrableExpression, String>> valuedExpressions) {
        this.valuedExpressions = valuedExpressions;
    }




    public APSelectorManager() {
        entireAttributeSet = setTestarTagSet();
        widgetfilters = new LinkedHashSet<>();
        selectedAttributes = new LinkedHashSet<TagBean<?>>();
    }


    public Set<TagBean<?>> getEntireAttributeSet() {
        return entireAttributeSet;
    }

    public void setEntireAttributeSet(Set<TagBean<?>> entireAttributeSet) {
        this.entireAttributeSet = entireAttributeSet;
    }

    public Set<WidgetFilter> getWidgetfilters() {
        return widgetfilters;
    }

    public void setWidgetfilters(Set<WidgetFilter> widgetfilters) {
        this.widgetfilters = widgetfilters;
    }



    private Set<TagBean<?>> setTestarTagSet(){
        Set<Tag<?>> tags = new HashSet<Tag<?>>();
        tags.addAll(Tags.tagSet());
        tags.addAll(UIATags.tagSet());
        Set<TagBean<?>> tagset=new LinkedHashSet<>();
System.out.println("debug length of UIAtagset:"+UIATags.tagSet().size());
        System.out.println("debug length of Tagsset:"+Tags.tagSet().size());
System.out.println("debug length of tags set:"+tags.size());
        Iterator<Tag<?>> iterator;
        for (iterator = tags.iterator(); iterator.hasNext(); ) {
            Tag<?> t = iterator.next();
            TagBean<?> t1 = TagBean.from(t.name(), t.type());
            tagset.add(t1);
        }
        // copy tags    Tags.tagSet();  //unmodifiable se
/*        for (Iterator<Tag<?>> iterator = Tags.tagSet().iterator(); iterator.hasNext(); ) {
            Tag<?> t = iterator.next();
            TagBean<?> t1 = TagBean.from(t.name(), t.type());
            tagset.add(t1);
        }*/

       // tagset.addAll(UIATags.tagSet());  //getNativetags ??
        return  tagset;
    };



    public Set<TagBean<?>> getSelectedAttributes() {
        return selectedAttributes;
    }

    public void setSelectedAttributes(Set<TagBean<?>> selectedAttributes) {
        this.selectedAttributes = selectedAttributes;
    }

    public void setDefaultWidgetFilter(){
        widgetfilters.add(new WidgetFilter(valuedExpressions));
    }
    public void addWidgetFilter(WidgetFilter w){
        widgetfilters.add(w);
    }


    public void setDefaultBooleanAttributes() {
        for (TagBean<?> tag : entireAttributeSet
        ) {
            if (tag.type() == Boolean.class) {
                selectedAttributes.add(tag);
            }
        }
    }
    public void setDefaultAllAttributes() {
        selectedAttributes = entireAttributeSet;
    }

    public void addAttribute(String attrib){

        for (TagBean<?> tag : entireAttributeSet
        ) {
            if ( tag.name() == attrib) {
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
            if ( tag.name() == attrib) {
                selectedAttributes.remove(tag);
                break;
            }
        }
    }
    private TagBean<?> getTag(String attrib){
        TagBean<?> ret=null;
        for (TagBean<?> tag : selectedAttributes
        ) {
            if ( tag.name() == attrib) {
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


    public void useDefaultValuedExpressions() {
        valuedExpressions.clear();
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
        valuedExpressions.add(new PairBean<>(InferrableExpression.pathmatch_, ".\\[(\\d+,)*\\d+\\]"));
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
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAButton.toString()));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAWindow.toString()));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIACheckBox.toString()));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIARadioButton.toString()));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAEdit.toString()));

    }


    //custom
    public void addPattern(InferrableExpression ip, String value) {
        valuedExpressions.add(new PairBean<>(ip,value));
    }
    public void removePattern(InferrableExpression ip, String value) {
        valuedExpressions.remove(new PairBean<>(ip,value));
    }

    public boolean addPattern(String patternStr) {
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
        Set<String> apset = new LinkedHashSet<>();
        TagBean<?> tag = null;
        tag = getTag(attrib);
        if (tag != null) {   //this attribute is required as a(n) (set of) AP
            if (tag.type() == Boolean.class) {
                apset.add(widgetkey + "_" + attrib + "__"); // just encode the TRUE/existence  and FALSE is then considered absence
            } else

                for (PairBean<InferrableExpression, String> iap : valuedExpressions
                ) {
                    if (iap.left().typ == "number" && (tag.type() == Double.class || tag.type() == Long.class || tag.type() == Integer.class)) {
                        int intVal = (int) Double.parseDouble(value);
                        if (((iap.left() == InferrableExpression.value_eq_) && (intVal == Integer.parseInt(iap.right()))) ||
                                ((iap.left() == InferrableExpression.value_lt_) && (intVal < Integer.parseInt(iap.right())))) {
                            apset.add(widgetkey + "_" + attrib + "_" + iap.left().name() + iap.right() + "__");
                        }
                    }
                    if (iap.left().typ == "text" && (tag.type() == Double.class)) {

                        if (((iap.left() == InferrableExpression.textmatch_) && value.matches(iap.right())) ||
                                ((iap.left() == InferrableExpression.textlength_eq_) && (value.length() == Integer.parseInt(iap.right()))) ||
                                ((iap.left() == InferrableExpression.textlength_lt_) && (value.length() < Integer.parseInt(iap.right())))
                        ) {
                            apset.add(widgetkey + "_" + attrib + "_" + iap.left().name() + iap.right() + "__");
                        }

                    }
                    if (iap.left().typ == "shape" && (tag.type() == Shape.class)) {
                        //format:   <data key="Shape">Rect [x:459.0 y:243.0 w:116.0 h:18.0]</data>
                        //String[] shapecomponents=value.split("w:")[1];
                        String test = value.split("w:")[1].split(" ")[0];
                        int width = (int) Double.parseDouble(test);
                        test = value.split("h:")[1].split(" ")[0];
                        int heigth = (int) Double.parseDouble(test);

                        if (((iap.left() == InferrableExpression.width_lt_) && (width < Integer.parseInt(iap.right()))) ||
                                ((iap.left() == InferrableExpression.heigth_lt_) && (heigth < Integer.parseInt(iap.right())))
                        ) {
                            apset.add(widgetkey + "_" + attrib + "_" + iap.left().name() + iap.right() + "__");
                        }
                    }
                    if (iap.left().typ == "path" && (tag.type() == String.class)) {
                        //format:     <data key="Path">[0, 0, 8]</data>
                        if ((iap.left() == InferrableExpression.pathmatch_) && value.matches(iap.right())) {
                            apset.add(widgetkey + "_" + attrib + "_" + iap.left().name() + iap.right() + "__");
                        }
                    }
                    if (iap.left().typ == "boolean") {   //add these regardless of the tag-type
                        //format:     <data key="Abc"></data>
                        if ((iap.left() == InferrableExpression.is_blank_) && value.matches("")) {
                            apset.add(widgetkey + "_" + attrib + "_" + iap.left().name() + iap.right() + "__");
                        }
                        if ((iap.left() == InferrableExpression.exists_) && value.matches("")) {
                            apset.add(widgetkey + "_" + attrib + "_" + iap.left().name() + iap.right() + "__");
                        }

                    }
                }

        }
        return apset;
    }

}
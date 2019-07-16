package nl.ou.testar.temporal.structure;

import nl.ou.testar.temporal.util.InferrableExpression;
import nl.ou.testar.temporal.util.PairBean;
import nl.ou.testar.temporal.util.TagBean;
import org.fruit.alayer.Role;
import org.fruit.alayer.windows.UIARoles;

import java.util.LinkedHashSet;
import java.util.Set;

public class WidgetFilter {
    private Set<String> widgetRolesMatches;
    private Set<String> widgetTitleMatches;
    private Set<String> widgetPathMatches;
    private Set<String> widgetParentTitleMatches;
    private Set<TagBean<?>> selectedAttributes;
    private static Set<PairBean<InferrableExpression,String>> defaultValuedExpressions= new LinkedHashSet<>();
    private  Set<PairBean<InferrableExpression,String>> valuedExpressions = new LinkedHashSet<>();


    public WidgetFilter() {
        widgetRolesMatches = new LinkedHashSet<>();
        widgetTitleMatches= new LinkedHashSet<>();
        widgetPathMatches= new LinkedHashSet<>();
        widgetParentTitleMatches= new LinkedHashSet<>();
    }
   public WidgetFilter(Set<PairBean<InferrableExpression,String>> valuedExpressions) {  //candidate fo refactoring: valueexpression
       this();

            this.setDefaultRoleMatches(valuedExpressions);
            this.setDefaultTitleMatches(valuedExpressions);
            this.setDefaultPathMatches(valuedExpressions);
            this.setDefaultParentTitleMatches(valuedExpressions);

    }
    public Set<TagBean<?>> getSelectedAttributes() {
        return selectedAttributes;
    }

    public void setSelectedAttributes(Set<TagBean<?>> selectedAttributes) {
        this.selectedAttributes = selectedAttributes;
    }

    public Set<PairBean<InferrableExpression, String>> getValuedExpressions() {
        return valuedExpressions;
    }


    public void setValuedExpressions(Set<PairBean<InferrableExpression, String>> valuedExpressions) {
        this.valuedExpressions = valuedExpressions;
    }

    public void setSimpleValuedExpressions() {
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
        valuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "")); //no title
        valuedExpressions.add(new PairBean<>(InferrableExpression.width_lt_, "100"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "20"));
        valuedExpressions.add(new PairBean<>(InferrableExpression.is_blank_, ""));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAButton.toString()));
        valuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAWindow.toString()));


    }

    public static Set<PairBean<InferrableExpression,String>>  useDefaultValuedExpressions() {
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.is_blank_, ""));  // use always
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.exists_, ""));// use always
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_eq_, "0"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_eq_, "0"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_eq_, "1"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_eq_, "2"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "10"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "100"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "1000"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "10000"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "100000"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.value_lt_, "1000000"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OK)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CANCEL)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:YES)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:NO)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:GO)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:RUN)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SAVE)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:EXIT)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CLOSE)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:REMOVE)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:ERROR)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SUBMIT)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OPEN)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:IGNORE)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:PROCEED)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:PRINT)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:VIEW)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:UP)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:DOWN)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:LEFT)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:RIGHT)"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textmatch_, "")); //no title
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.pathmatch_, ".*\\[(\\d+, )*\\d+\\]"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.heigth_lt_, "50"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.heigth_lt_, "250"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.heigth_lt_, "500"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.heigth_lt_, "1000"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.width_lt_, "50"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.width_lt_, "250"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.width_lt_, "500"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.width_lt_, "1000"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textlength_eq_, "1"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textlength_eq_, "2"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textlength_eq_, "3"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "10"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "20"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "50"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "100"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.textlength_lt_, "200"));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.is_blank_, ""));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.exists_, ""));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.relpos_upleft_, ""));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.relpos_upright_, ""));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.relpos_downleft_, ""));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.relpos_downright_, ""));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAButton.toString()));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAWindow.toString()));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIACheckBox.toString()));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIARadioButton.toString()));
        defaultValuedExpressions.add(new PairBean<>(InferrableExpression.rolematch_, UIARoles.UIAEdit.toString()));
return defaultValuedExpressions;
    }


    public Set<String> getWidgetRolesMatches() {
        return widgetRolesMatches;
    }



    public void setWidgetRolesMatches(Set<String> widgetRolesMatches) {
        this.widgetRolesMatches = widgetRolesMatches;
    }

    public Set<String> getWidgetTitleMatches() {
        return widgetTitleMatches;
    }

    public void setWidgetTitleMatches(Set<String> widgetTitleMatches) {
        this.widgetTitleMatches = widgetTitleMatches;
    }

    public Set<String> getWidgetPathMatches() {
        return widgetPathMatches;
    }

    public void setWidgetPathMatches(Set<String> widgetPathMatches) {
        this.widgetPathMatches = widgetPathMatches;
    }

    public Set<String> getWidgetParentTitleMatches() {
        return widgetParentTitleMatches;
    }

    public void setWidgetParentTitleMatches(Set<String> widgetParentTitleMatches) {
        this.widgetParentTitleMatches = widgetParentTitleMatches;
    }

    public void addWidgetRoleMatch(String expr){
        this.widgetRolesMatches.add(expr);
    }
    public void addWidgetTitleMatch(String expr){
        this.widgetTitleMatches.add(expr);
    }
    public void addWidgetPathMatch(String expr){
        this.widgetPathMatches.add(expr);
    }
    public void addWidgetparentTitleMatch(String expr){
        this.widgetParentTitleMatches.add(expr);
    }


    //custom
    public void setAllAvailableWidgetRoles(){
        for (Role r:UIARoles.rolesSet()
             ) {
            this.widgetRolesMatches.add(r.toString());
        }
    }

    public void setDefaultRoleMatches(Set<PairBean<InferrableExpression,String>> valuedExpressions){
        widgetRolesMatches.clear();
        for (PairBean<InferrableExpression,String> iap: valuedExpressions
        )        {
            if (iap.left()==InferrableExpression.rolematch_){
                widgetRolesMatches.add(iap.right());
            }
        }
    }

    public void setDefaultTitleMatches(Set<PairBean<InferrableExpression,String>> valuedExpressions){
        widgetTitleMatches.clear();
        for (PairBean<InferrableExpression,String> iap: valuedExpressions
             )        {
           if (iap.left()==InferrableExpression.textmatch_){
               widgetTitleMatches.add(iap.right());
           }
        }
    }
    public void setDefaultParentTitleMatches(Set<PairBean<InferrableExpression,String>> valuedExpressions){
        widgetParentTitleMatches.clear();
        for (PairBean<InferrableExpression,String> iap: valuedExpressions
        )        {
            if (iap.left()==InferrableExpression.textmatch_){
                widgetParentTitleMatches.add(iap.right());
            }
        }
    }
    public void setDefaultPathMatches(Set<PairBean<InferrableExpression,String>> valuedExpressions){
        widgetPathMatches.clear();
        for (PairBean<InferrableExpression,String> iap: valuedExpressions
        )        {
            if (iap.left()==InferrableExpression.pathmatch_){
                widgetPathMatches.add(iap.right());
            }
        }
    }

}


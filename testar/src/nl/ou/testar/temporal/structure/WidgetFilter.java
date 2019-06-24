package nl.ou.testar.temporal.structure;

import nl.ou.testar.temporal.util.InferrableExpression;
import nl.ou.testar.temporal.util.PairBean;
import org.fruit.alayer.Role;
import org.fruit.alayer.windows.UIARoles;

import java.util.LinkedHashSet;
import java.util.Set;

public class WidgetFilter {
    private Set<String> widgetRolesMatches;
    private Set<String> widgetTitleMatches;
    private Set<String> widgetPathMatches;
    private Set<String> widgetParentTitleMatches;


    public WidgetFilter() {
        widgetRolesMatches = new LinkedHashSet<>();
        widgetTitleMatches= new LinkedHashSet<>();
        widgetPathMatches= new LinkedHashSet<>();
        widgetParentTitleMatches= new LinkedHashSet<>();
    }
   public WidgetFilter(Set<PairBean<InferrableExpression,String>> valuedExpressions) {
       this();

            this.setDefaultRoleMatches(valuedExpressions);
            this.setDefaultTitleMatches(valuedExpressions);
            this.setDefaultPathMatches(valuedExpressions);
            this.setDefaultParentTitleMatches(valuedExpressions);

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


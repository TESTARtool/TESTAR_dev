package nl.ou.testar.temporal.structure;

import nl.ou.testar.temporal.util.InferrableExpression;
import org.fruit.Pair;

import java.util.HashSet;
import java.util.Set;

//@JsonRootName(value="TemporalProperties")
class InferrableValuedExpressions {


    public static Set<Pair<InferrableExpression,String>> APPatterns = new HashSet<>();

    public InferrableValuedExpressions() {
    }

    public void useDefaultAPs() {
        APPatterns.clear();
        APPatterns.add(new Pair(InferrableExpression.value_eq_, "0"));
        APPatterns.add(new Pair(InferrableExpression.value_eq_, "1"));
        APPatterns.add(new Pair(InferrableExpression.value_eq_, "2"));
        APPatterns.add(new Pair(InferrableExpression.value_lt_, "10"));
        APPatterns.add(new Pair(InferrableExpression.value_lt_, "100"));
        APPatterns.add(new Pair(InferrableExpression.value_lt_, "1000"));
        APPatterns.add(new Pair(InferrableExpression.value_lt_, "10000"));
        APPatterns.add(new Pair(InferrableExpression.value_lt_, "100000"));
        APPatterns.add(new Pair(InferrableExpression.value_lt_, "1000000"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/OK/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/CANCEL/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/YES/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/NO/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/GO/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/RUN/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/SAVE/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/EXIT/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/CLOSE/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/ADD/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/REMOVE/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/ERROR/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/SUBMIT/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/OPEN/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/IGNORE/i"));
        APPatterns.add(new Pair(InferrableExpression.textmatch_, "/PROCEED/i"));
        APPatterns.add(new Pair(InferrableExpression.heigth_lt_, "50"));
        APPatterns.add(new Pair(InferrableExpression.heigth_lt_, "250"));
        APPatterns.add(new Pair(InferrableExpression.heigth_lt_, "500"));
        APPatterns.add(new Pair(InferrableExpression.heigth_lt_, "1000"));
        APPatterns.add(new Pair(InferrableExpression.width_lt_, "50"));
        APPatterns.add(new Pair(InferrableExpression.width_lt_, "250"));
        APPatterns.add(new Pair(InferrableExpression.width_lt_, "500"));
        APPatterns.add(new Pair(InferrableExpression.width_lt_, "1000"));
        APPatterns.add(new Pair(InferrableExpression.textlength_eq_, "1"));
        APPatterns.add(new Pair(InferrableExpression.textlength_eq_, "2"));
        APPatterns.add(new Pair(InferrableExpression.textlength_eq_, "3"));
        APPatterns.add(new Pair(InferrableExpression.textlength_lt_, "10"));
        APPatterns.add(new Pair(InferrableExpression.textlength_lt_, "20"));
        APPatterns.add(new Pair(InferrableExpression.textlength_lt_, "50"));
        APPatterns.add(new Pair(InferrableExpression.textlength_lt_, "100"));
        APPatterns.add(new Pair(InferrableExpression.textlength_lt_, "200"));

    }


    public Set<Pair<InferrableExpression,String>> getPatterns() {
        return APPatterns;
    }

    public void setPatterns(Set<Pair<InferrableExpression,String>> patterns) {
        this.APPatterns = patterns;
    }


    //custom
    public void addPattern(InferrableExpression ip, String value) {
        APPatterns.add(new Pair(ip,value));
    }

    public boolean addPattern(String patternStr) {
        boolean succes = false;  //remains false if pattern is not found
        for (InferrableExpression iap : InferrableExpression.values()) {
            if (patternStr.startsWith(String.valueOf(iap))) {
                int iapSize = String.valueOf(iap).length();
                String value = patternStr.substring(iapSize);
                try {
                    double dbl = Double.parseDouble(value); //test if it is a number
                    APPatterns.add(new Pair(iap, value));
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
package nl.ou.testar.temporal.scratch;

import nl.ou.testar.temporal.util.InferrableExpression;
import nl.ou.testar.temporal.util.PairBean;

import java.util.LinkedHashSet;
import java.util.Set;

//@JsonRootName(value="TemporalProperties")
class InferrableValuedExpressions {


    private  Set<PairBean<InferrableExpression,String>> APPatterns = new LinkedHashSet<>();

    public InferrableValuedExpressions() {
    }
    public InferrableValuedExpressions(boolean useDefaults) {
        if(useDefaults){
            useDefaultValuedExpressions();
        }
    }

    public void useDefaultValuedExpressions() {
        APPatterns.clear();
        APPatterns.add(new PairBean<>(InferrableExpression.value_eq_, "0"));
        APPatterns.add(new PairBean<>(InferrableExpression.value_eq_, "0"));
        APPatterns.add(new PairBean<>(InferrableExpression.value_eq_, "1"));
        APPatterns.add(new PairBean<>(InferrableExpression.value_eq_, "2"));
        APPatterns.add(new PairBean<>(InferrableExpression.value_lt_, "10"));
        APPatterns.add(new PairBean<>(InferrableExpression.value_lt_, "100"));
        APPatterns.add(new PairBean<>(InferrableExpression.value_lt_, "1000"));
        APPatterns.add(new PairBean<>(InferrableExpression.value_lt_, "10000"));
        APPatterns.add(new PairBean<>(InferrableExpression.value_lt_, "100000"));
        APPatterns.add(new PairBean<>(InferrableExpression.value_lt_, "1000000"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OK)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CANCEL)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:YES)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:NO)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:GO)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:RUN)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SAVE)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:EXIT)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:CLOSE)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:REMOVE)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:ERROR)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:SUBMIT)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:OPEN)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:IGNORE)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:PROCEED)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:PRINT)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:VIEW)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:UP)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:DOWN)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:LEFT)"));
        APPatterns.add(new PairBean<>(InferrableExpression.textmatch_, "(?i:RIGHT)"));
        APPatterns.add(new PairBean<>(InferrableExpression.pathmatch_, ".\\[(\\d+,)*\\d+\\]"));
        APPatterns.add(new PairBean<>(InferrableExpression.heigth_lt_, "50"));
        APPatterns.add(new PairBean<>(InferrableExpression.heigth_lt_, "250"));
        APPatterns.add(new PairBean<>(InferrableExpression.heigth_lt_, "500"));
        APPatterns.add(new PairBean<>(InferrableExpression.heigth_lt_, "1000"));
        APPatterns.add(new PairBean<>(InferrableExpression.width_lt_, "50"));
        APPatterns.add(new PairBean<>(InferrableExpression.width_lt_, "250"));
        APPatterns.add(new PairBean<>(InferrableExpression.width_lt_, "500"));
        APPatterns.add(new PairBean<>(InferrableExpression.width_lt_, "1000"));
        APPatterns.add(new PairBean<>(InferrableExpression.textlength_eq_, "1"));
        APPatterns.add(new PairBean<>(InferrableExpression.textlength_eq_, "2"));
        APPatterns.add(new PairBean<>(InferrableExpression.textlength_eq_, "3"));
        APPatterns.add(new PairBean<>(InferrableExpression.textlength_lt_, "10"));
        APPatterns.add(new PairBean<>(InferrableExpression.textlength_lt_, "20"));
        APPatterns.add(new PairBean<>(InferrableExpression.textlength_lt_, "50"));
        APPatterns.add(new PairBean<>(InferrableExpression.textlength_lt_, "100"));
        APPatterns.add(new PairBean<>(InferrableExpression.textlength_lt_, "200"));
        APPatterns.add(new PairBean<>(InferrableExpression.is_blank_, ""));
        APPatterns.add(new PairBean<>(InferrableExpression.exists_, ""));

    }



    public Set<PairBean<InferrableExpression,String>> getPatterns() {
        return APPatterns;
    }

    public void setPatterns(Set<PairBean<InferrableExpression,String>> patterns) {
        this.APPatterns = patterns;
    }


    //custom
    public void addPattern(InferrableExpression ip, String value) {
        APPatterns.add(new PairBean<>(ip,value));
    }
    public void removePattern(InferrableExpression ip, String value) {
        APPatterns.remove(new PairBean<>(ip,value));
    }

    public boolean addPattern(String patternStr) {
        boolean succes = false;  //remains certainly false if pattern is not found
        for (InferrableExpression iap : InferrableExpression.values()) {
            if (patternStr.startsWith(String.valueOf(iap))) {
                int iapSize = String.valueOf(iap).length();
                String value = patternStr.substring(iapSize);
                try {
                    double dbl = Double.parseDouble(value); //test if it is a number
                    APPatterns.add(new PairBean<>(iap, value));
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
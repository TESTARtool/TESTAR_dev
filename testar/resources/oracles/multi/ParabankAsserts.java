
package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.DslOracle;

public class ParabankAsserts {
   
   
   public static class ImagesMustHaveAlternativeText$101 extends DslOracle {
     /*
      assert for all image 
        it has nonempty alttext 
        "Images must have alternative text".
     */
   
     @Override
     public void initialize() {
        
     }
   
     @Override
     public String getMessage() {
       return "Images must have alternative text";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Widget constraintWidget = getConstraintWidgetOrState(state);
        
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("image", constraintWidget)) {
          
          boolean cond$76 = evaluateHasAttribute($it, "alttext");
          if (!cond$76) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class WelcomeJohnSmithIsVisible$237 extends DslOracle {
     /*
      assert static_text "Welcome John Smith" is visible "Welcome John Smith is visible".
     */
   
     @Override
     public void initialize() {
        
        addSectionConstraint(java.util.Arrays.asList("Parabank | .*", "Account Services"));
        
     }
   
     @Override
     public String getMessage() {
       return "Welcome John Smith is visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Widget constraintWidget = getConstraintWidgetOrState(state);
        
        Verdict verdict = Verdict.OK;
        Widget widget$193$32 = getWidget("static_text", "Welcome John Smith", constraintWidget);
        if (widget$193$32 == null) {
          return Verdict.OK;
        }
        boolean cond$226 = evaluateIsStatus(widget$193$32, "visible");
        if (!cond$226) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$193$32)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class DollarValuesMustHaveTwoDecimals$427 extends DslOracle {
     /*
      assert for all table_data
        it.text matches "\\d+\\.\\d{2}$" when it.text contains "$" 
        "Dollar values must have two decimals".
     */
   
     @Override
     public void initialize() {
        
        addSectionConstraint(java.util.Arrays.asList("Parabank | Accounts Overview", "Accounts Overview"));
        
     }
   
     @Override
     public String getMessage() {
       return "Dollar values must have two decimals";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Widget constraintWidget = getConstraintWidgetOrState(state);
        
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_data", constraintWidget)) {
          
          Object widget$402$7 = getProperty($it, "text");
          boolean cond$410 = evaluateContains(widget$402$7, "$");
          if (cond$410) {
            
            Object widget$364$7 = getProperty($it, "text");
            boolean cond$372 = evaluateMatches(widget$364$7, "\\d+\\.\\d{2}$");
            if (!cond$372) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
     
}

package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.DslOracle;

public class ParabankAsserts {
   
   private static String $twoWords_RE = "^\\w+(?:\\s+\\w+){1,1}$";
   
   
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
   
   public static class AccountServicesLinksMustMatchTwoWords$622 extends DslOracle {
     /*
      assert for all link
          it.text matches twoWords
          "Account Services links must match two words".
     */
   
     @Override
     public void initialize() {
        
        addSectionConstraint(java.util.Arrays.asList("Parabank | .*", "Account Services"));
        
     }
   
     @Override
     public String getMessage() {
       return "Account Services links must match two words";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Widget constraintWidget = getConstraintWidgetOrState(state);
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("link", constraintWidget)) {
          
          Object widget$592$7 = getProperty($it, "text");
          boolean cond$600 = evaluateMatchesWithName(widget$592$7, $twoWords_RE, "twoWords");
          if (!cond$600) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
     
}

package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.DslOracle;

public class InvariantAsserts {
   
   public static class DollarValuesMustHaveTwoDecimals$144 extends DslOracle {
     /*
      assert for all table_data
        it.text matches "\\d+\\.\\d{2}$" when it.text contains "$" 
        "Dollar values must have two decimals".
     */
   
     @Override
     public String getMessage() {
       return "Dollar values must have two decimals";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_data", state)) {
          
          Object widget$119$7 = getProperty($it, "text");
          boolean cond$127 = evaluateContains(widget$119$7, "$");
          if (cond$127) {
            
            Object widget$81$7 = getProperty($it, "text");
            boolean cond$89 = evaluateMatches(widget$81$7, "\\d+\\.\\d{2}$");
            if (!cond$89) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class TextSizeMustBeBiggerThan9Pixels$328 extends DslOracle {
     /*
      assert for all menu_item, table_data, static_text, label
        it.fontsize matches "\\b(9|[1-9]\\d+?)px\\b" when it.fontsize contains "px"
        "Text size must be bigger than 9 pixels".
     */
   
     @Override
     public String getMessage() {
       return "Text size must be bigger than 9 pixels";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("menu_item", state)) {
          
          Object widget$299$11 = getProperty($it, "fontsize");
          boolean cond$311 = evaluateContains(widget$299$11, "px");
          if (cond$311) {
            
            Object widget$249$11 = getProperty($it, "fontsize");
            boolean cond$261 = evaluateMatches(widget$249$11, "\\b(9|[1-9]\\d+?)px\\b");
            if (!cond$261) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        for (Widget $it: getWidgets("table_data", state)) {
          
          Object widget$299$11 = getProperty($it, "fontsize");
          boolean cond$311 = evaluateContains(widget$299$11, "px");
          if (cond$311) {
            
            Object widget$249$11 = getProperty($it, "fontsize");
            boolean cond$261 = evaluateMatches(widget$249$11, "\\b(9|[1-9]\\d+?)px\\b");
            if (!cond$261) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        for (Widget $it: getWidgets("static_text", state)) {
          
          Object widget$299$11 = getProperty($it, "fontsize");
          boolean cond$311 = evaluateContains(widget$299$11, "px");
          if (cond$311) {
            
            Object widget$249$11 = getProperty($it, "fontsize");
            boolean cond$261 = evaluateMatches(widget$249$11, "\\b(9|[1-9]\\d+?)px\\b");
            if (!cond$261) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        for (Widget $it: getWidgets("label", state)) {
          
          Object widget$299$11 = getProperty($it, "fontsize");
          boolean cond$311 = evaluateContains(widget$299$11, "px");
          if (cond$311) {
            
            Object widget$249$11 = getProperty($it, "fontsize");
            boolean cond$261 = evaluateMatches(widget$249$11, "\\b(9|[1-9]\\d+?)px\\b");
            if (!cond$261) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class MenuChildrenMustBeEnabled$422 extends DslOracle {
     /*
      assert for all menu
        it.children are enabled "menu children must be enabled".
     */
   
     @Override
     public String getMessage() {
       return "menu children must be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("menu", state)) {
          
          Object widget$398$11 = getProperty($it, "children");
          boolean cond$410 = evaluateAreStatus((java.util.List<Object>)widget$398$11, "enabled");
          if (!cond$410) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class SubmitMustBeEnabledWhenAcceptTermsIsChecked$540 extends DslOracle {
     /*
      assert button "Submit" is enabled when checkbox "Accept Terms" 
          is checked "Submit must be enabled when Accept Terms is checked".
     */
   
     @Override
     public String getMessage() {
       return "Submit must be enabled when Accept Terms is checked";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$499$23 = getWidget("checkbox", "Accept Terms", state);
        if (widget$499$23 == null) {
          return Verdict.OK;
        }
        boolean cond$529 = evaluateIsStatus(widget$499$23, "checked");
        if (cond$529) {
          Widget widget$467$15 = getWidget("button", "Submit", state);
          if (widget$467$15 == null) {
            return Verdict.OK;
          }
          boolean cond$483 = evaluateIsStatus(widget$467$15, "enabled");
          if (!cond$483) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$467$15)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class ImagesMustHaveAlternativeText$654 extends DslOracle {
     /*
      assert for all image 
        it has nonempty alttext 
        "Images must have alternative text".
     */
   
     @Override
     public String getMessage() {
       return "Images must have alternative text";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("image", state)) {
          
          boolean cond$629 = evaluateHasAttribute($it, "alttext");
          if (!cond$629) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class FormsMustHaveAccessibilityTitle$746 extends DslOracle {
     /*
      assert for all form 
        it has nonempty title 
        "Forms must have accessibility title".
     */
   
     @Override
     public String getMessage() {
       return "Forms must have accessibility title";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("form", state)) {
          
          boolean cond$723 = evaluateHasAttribute($it, "title");
          if (!cond$723) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class DropdownOptionsMustNotBeEmpty$836 extends DslOracle {
     /*
      assert for all dropdown
        it not is empty
        "Dropdown options must not be empty".
     */
   
     @Override
     public String getMessage() {
       return "Dropdown options must not be empty";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("dropdown", state)) {
          
          boolean cond$820 = !(evaluateIsStatus($it, "empty"));
          if (!cond$820) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class DropdownOptionsMustNotHaveAUniqueValue$937 extends DslOracle {
     /*
      assert for all dropdown
        it.length not is equal to 1
        "Dropdown options must not have a unique value".
     */
   
     @Override
     public String getMessage() {
       return "Dropdown options must not have a unique value";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("dropdown", state)) {
          
          Object widget$906$9 = getProperty($it, "length");
          boolean cond$916 = !(evaluateIsEqualTo(widget$906$9, 1));
          if (!cond$916) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class ColorAndBackgroundColorMustBeDifferent$1068 extends DslOracle {
     /*
      assert for all static_text
        it.color not is equal to it.backgroundColor
        "Color and BackgroundColor must be different".
     */
   
     @Override
     public String getMessage() {
       return "Color and BackgroundColor must be different";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("static_text", state)) {
          
          Object widget$1021$8 = getProperty($it, "color");
          boolean cond$1030 = !(evaluateIsEqualTo(widget$1021$8, new java.util.function.Supplier<Object>() {
           public Object get() {
             
             Object widget$1046$18 = getProperty($it, "backgroundColor");
             return widget$1046$18; 
           }  
          }.get()));
          if (!cond$1030) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
     
}
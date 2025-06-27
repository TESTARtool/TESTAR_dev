
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class binaryAsserts {
   
   /*
    assert for all static_text
        it matches "\\?$" 
        "prompts must end in ?".
   */
   public static class PromptsMustEndIn implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "prompts must end in ?";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("static_text", state)) {
          
          boolean cond$34 = evaluateMatches($it, "\\?$");
          if (!cond$34) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert for all static_text
        it matches "^[A-Z]" 
        "prompts must be capitalized".
   */
   public static class PromptsMustBeCapitalized implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "prompts must be capitalized";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("static_text", state)) {
          
          boolean cond$114 = evaluateMatches($it, "^[A-Z]");
          if (!cond$114) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert for all static_text
        it not contains "!" 
        "prompts must not shout".
   */
   public static class PromptsMustNotShout implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "prompts must not shout";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("static_text", state)) {
          
          boolean cond$202 = !(evaluateContains($it, "!"));
          if (!cond$202) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert for all static_text
        it not matches "^[A-Z\\ !]+$" 
        "prompts must not use all caps".
   */
   public static class PromptsMustNotUseAllCaps implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "prompts must not use all caps";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("static_text", state)) {
          
          boolean cond$285 = !(evaluateMatches($it, "^[A-Z\\ !]+$"));
          if (!cond$285) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert for all input_text, input_numeric
        it not matches "^$" when it is readonly 
        "computed question must have a value".
   */
   public static class ComputedQuestionMustHaveAValue implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "computed question must have a value";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("input_text", state)) {
          
          boolean cond$425 = evaluateIsStatus($it, "readonly");
          if (cond$425) {
            boolean cond$400 = !(evaluateMatches($it, "^$"));
            if (!cond$400) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        for (Widget $it: getWidgets("input_numeric", state)) {
          
          boolean cond$425 = evaluateIsStatus($it, "readonly");
          if (cond$425) {
            boolean cond$400 = !(evaluateMatches($it, "^$"));
            if (!cond$400) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        return verdict;
     }
    
   }
   
   /*
    assert for all checkbox
        it.tooltip contains "yes" and contains "no"
        "checkbox's tooltips must mention yes and no".
   */
   public static class CheckboxSTooltipsMustMentionYesAndNo implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "checkbox's tooltips must mention yes and no";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("checkbox", state)) {
          
          Object property$510$10 = getProperty($it, "tooltip");
          boolean cond$521 = (evaluateContains(property$510$10, "yes") && evaluateContains(property$510$10, "no"));
          if (!cond$521) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert for all input_numeric
        it.tooltip contains "integer"
        "numeric tooltips must mention integer".
   */
   public static class NumericTooltipsMustMentionInteger implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "numeric tooltips must mention integer";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("input_numeric", state)) {
          
          Object property$639$10 = getProperty($it, "tooltip");
          boolean cond$650 = evaluateContains(property$639$10, "integer");
          if (!cond$650) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert for all input_text
        it.placeholder is equal to it.tooltip
        "placeholder and tooltip must be equal".
   */
   public static class PlaceholderAndTooltipMustBeEqual implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "placeholder and tooltip must be equal";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("input_text", state)) {
          
          Object property$745$14 = getProperty($it, "placeholder");
          boolean cond$760 = evaluateIsEqualTo(property$745$14, new java.util.function.Supplier<Object>() {
           public Object get() {
             
             Object property$772$10 = getProperty($it, "tooltip");
             return property$772$10; 
           }  
          }.get());
          if (!cond$760) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        
        }
        return verdict;
     }
    
   }
     
}
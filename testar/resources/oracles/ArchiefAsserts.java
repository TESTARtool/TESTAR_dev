
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class ArchiefAsserts {
   
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
   
   /*
    assert panel "discount_div_228" not is visible when panel "discount_div_288" is visible
      "discount_div_288 was displayed at same time as discount_div_228".
   */
   public static class Discount_div_288WasDisplayedAtSameTimeAsDiscount_div_228 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_288 was displayed at same time as discount_div_228";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$881$24 = getWidget("panel", "discount_div_288", state);
        if (widget$881$24 == null) {
          return Verdict.OK;
        }
        boolean cond$906 = evaluateIsStatus(widget$881$24, "visible");
        if (cond$906) {
          Widget widget$836$24 = getWidget("panel", "discount_div_228", state);
          if (widget$836$24 == null) {
            return Verdict.OK;
          }
          boolean cond$861 = !(evaluateIsStatus(widget$836$24, "visible"));
          if (!cond$861) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$836$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_228" not is visible when panel "discount_div_354" is visible
      "discount_div_354 was displayed at same time as discount_div_228".
   */
   public static class Discount_div_354WasDisplayedAtSameTimeAsDiscount_div_228 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_354 was displayed at same time as discount_div_228";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1039$24 = getWidget("panel", "discount_div_354", state);
        if (widget$1039$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1064 = evaluateIsStatus(widget$1039$24, "visible");
        if (cond$1064) {
          Widget widget$994$24 = getWidget("panel", "discount_div_228", state);
          if (widget$994$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1019 = !(evaluateIsStatus(widget$994$24, "visible"));
          if (!cond$1019) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$994$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_228" not is visible when panel "discount_div_406" is visible
      "discount_div_406 was displayed at same time as discount_div_228".
   */
   public static class Discount_div_406WasDisplayedAtSameTimeAsDiscount_div_228 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_406 was displayed at same time as discount_div_228";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1197$24 = getWidget("panel", "discount_div_406", state);
        if (widget$1197$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1222 = evaluateIsStatus(widget$1197$24, "visible");
        if (cond$1222) {
          Widget widget$1152$24 = getWidget("panel", "discount_div_228", state);
          if (widget$1152$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1177 = !(evaluateIsStatus(widget$1152$24, "visible"));
          if (!cond$1177) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1152$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_288" not is visible when panel "discount_div_228" is visible
      "discount_div_228 was displayed at same time as discount_div_288".
   */
   public static class Discount_div_228WasDisplayedAtSameTimeAsDiscount_div_288 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_228 was displayed at same time as discount_div_288";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1355$24 = getWidget("panel", "discount_div_228", state);
        if (widget$1355$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1380 = evaluateIsStatus(widget$1355$24, "visible");
        if (cond$1380) {
          Widget widget$1310$24 = getWidget("panel", "discount_div_288", state);
          if (widget$1310$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1335 = !(evaluateIsStatus(widget$1310$24, "visible"));
          if (!cond$1335) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1310$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_288" not is visible when panel "discount_div_354" is visible
      "discount_div_354 was displayed at same time as discount_div_288".
   */
   public static class Discount_div_354WasDisplayedAtSameTimeAsDiscount_div_288 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_354 was displayed at same time as discount_div_288";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1513$24 = getWidget("panel", "discount_div_354", state);
        if (widget$1513$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1538 = evaluateIsStatus(widget$1513$24, "visible");
        if (cond$1538) {
          Widget widget$1468$24 = getWidget("panel", "discount_div_288", state);
          if (widget$1468$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1493 = !(evaluateIsStatus(widget$1468$24, "visible"));
          if (!cond$1493) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1468$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_288" not is visible when panel "discount_div_406" is visible
      "discount_div_406 was displayed at same time as discount_div_288".
   */
   public static class Discount_div_406WasDisplayedAtSameTimeAsDiscount_div_288 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_406 was displayed at same time as discount_div_288";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1671$24 = getWidget("panel", "discount_div_406", state);
        if (widget$1671$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1696 = evaluateIsStatus(widget$1671$24, "visible");
        if (cond$1696) {
          Widget widget$1626$24 = getWidget("panel", "discount_div_288", state);
          if (widget$1626$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1651 = !(evaluateIsStatus(widget$1626$24, "visible"));
          if (!cond$1651) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1626$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_354" not is visible when panel "discount_div_228" is visible
      "discount_div_228 was displayed at same time as discount_div_354".
   */
   public static class Discount_div_228WasDisplayedAtSameTimeAsDiscount_div_354 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_228 was displayed at same time as discount_div_354";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1829$24 = getWidget("panel", "discount_div_228", state);
        if (widget$1829$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1854 = evaluateIsStatus(widget$1829$24, "visible");
        if (cond$1854) {
          Widget widget$1784$24 = getWidget("panel", "discount_div_354", state);
          if (widget$1784$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1809 = !(evaluateIsStatus(widget$1784$24, "visible"));
          if (!cond$1809) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1784$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_354" not is visible when panel "discount_div_288" is visible
      "discount_div_288 was displayed at same time as discount_div_354".
   */
   public static class Discount_div_288WasDisplayedAtSameTimeAsDiscount_div_354 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_288 was displayed at same time as discount_div_354";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1987$24 = getWidget("panel", "discount_div_288", state);
        if (widget$1987$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2012 = evaluateIsStatus(widget$1987$24, "visible");
        if (cond$2012) {
          Widget widget$1942$24 = getWidget("panel", "discount_div_354", state);
          if (widget$1942$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1967 = !(evaluateIsStatus(widget$1942$24, "visible"));
          if (!cond$1967) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1942$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_354" not is visible when panel "discount_div_406" is visible
      "discount_div_406 was displayed at same time as discount_div_354".
   */
   public static class Discount_div_406WasDisplayedAtSameTimeAsDiscount_div_354 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_406 was displayed at same time as discount_div_354";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2145$24 = getWidget("panel", "discount_div_406", state);
        if (widget$2145$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2170 = evaluateIsStatus(widget$2145$24, "visible");
        if (cond$2170) {
          Widget widget$2100$24 = getWidget("panel", "discount_div_354", state);
          if (widget$2100$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2125 = !(evaluateIsStatus(widget$2100$24, "visible"));
          if (!cond$2125) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2100$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_406" not is visible when panel "discount_div_228" is visible
      "discount_div_228 was displayed at same time as discount_div_406".
   */
   public static class Discount_div_228WasDisplayedAtSameTimeAsDiscount_div_406 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_228 was displayed at same time as discount_div_406";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2303$24 = getWidget("panel", "discount_div_228", state);
        if (widget$2303$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2328 = evaluateIsStatus(widget$2303$24, "visible");
        if (cond$2328) {
          Widget widget$2258$24 = getWidget("panel", "discount_div_406", state);
          if (widget$2258$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2283 = !(evaluateIsStatus(widget$2258$24, "visible"));
          if (!cond$2283) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2258$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_406" not is visible when panel "discount_div_288" is visible
      "discount_div_288 was displayed at same time as discount_div_406".
   */
   public static class Discount_div_288WasDisplayedAtSameTimeAsDiscount_div_406 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_288 was displayed at same time as discount_div_406";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2461$24 = getWidget("panel", "discount_div_288", state);
        if (widget$2461$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2486 = evaluateIsStatus(widget$2461$24, "visible");
        if (cond$2486) {
          Widget widget$2416$24 = getWidget("panel", "discount_div_406", state);
          if (widget$2416$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2441 = !(evaluateIsStatus(widget$2416$24, "visible"));
          if (!cond$2441) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2416$24)); }
        
        }
        return verdict;
     }
    
   }
   
   /*
    assert panel "discount_div_406" not is visible when panel "discount_div_354" is visible
      "discount_div_354 was displayed at same time as discount_div_406".
   */
   public static class Discount_div_354WasDisplayedAtSameTimeAsDiscount_div_406 implements Oracle {
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_354 was displayed at same time as discount_div_406";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2619$24 = getWidget("panel", "discount_div_354", state);
        if (widget$2619$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2644 = evaluateIsStatus(widget$2619$24, "visible");
        if (cond$2644) {
          Widget widget$2574$24 = getWidget("panel", "discount_div_406", state);
          if (widget$2574$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2599 = !(evaluateIsStatus(widget$2574$24, "visible"));
          if (!cond$2599) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2574$24)); }
        
        }
        return verdict;
     }
    
   }
     
}
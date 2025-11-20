
package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.DslOracle;

public class ArchiefAsserts {
   
   public static class PromptsMustEndIn$102 extends DslOracle {
     /*
      assert for all static_text
          it matches "\\?$" 
          "prompts must end in ?".
     */
   
     @Override
     public String getMessage() {
       return "prompts must end in ?";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("static_text", state)) {
          
          boolean cond$81 = evaluateMatches($it, "\\?$");
          if (!cond$81) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class PromptsMustBeCapitalized$188 extends DslOracle {
     /*
      assert for all static_text
          it matches "^[A-Z]" 
          "prompts must be capitalized".
     */
   
     @Override
     public String getMessage() {
       return "prompts must be capitalized";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("static_text", state)) {
          
          boolean cond$165 = evaluateMatches($it, "^[A-Z]");
          if (!cond$165) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class PromptsMustNotShout$280 extends DslOracle {
     /*
      assert for all static_text
          it not contains "!" 
          "prompts must not shout".
     */
   
     @Override
     public String getMessage() {
       return "prompts must not shout";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("static_text", state)) {
          
          boolean cond$257 = !(evaluateContains($it, "!"));
          if (!cond$257) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class PromptsMustNotUseAllCaps$377 extends DslOracle {
     /*
      assert for all static_text
          it not matches "^[A-Z\\ !]+$" 
          "prompts must not use all caps".
     */
   
     @Override
     public String getMessage() {
       return "prompts must not use all caps";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("static_text", state)) {
          
          boolean cond$344 = !(evaluateMatches($it, "^[A-Z\\ !]+$"));
          if (!cond$344) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class ComputedQuestionMustHaveAValue$507 extends DslOracle {
     /*
      assert for all input_text, input_numeric
          it not matches "^$" when it is readonly 
          "computed question must have a value".
     */
   
     @Override
     public String getMessage() {
       return "computed question must have a value";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("input_text", state)) {
          
          boolean cond$489 = evaluateIsStatus($it, "readonly");
          if (cond$489) {
            boolean cond$464 = !(evaluateMatches($it, "^$"));
            if (!cond$464) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        for (Widget $it: getWidgets("input_numeric", state)) {
          
          boolean cond$489 = evaluateIsStatus($it, "readonly");
          if (cond$489) {
            boolean cond$464 = !(evaluateMatches($it, "^$"));
            if (!cond$464) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class CheckboxSTooltipsMustMentionYesAndNo$627 extends DslOracle {
     /*
      assert for all checkbox
          it.tooltip contains "yes" and contains "no"
          "checkbox's tooltips must mention yes and no".
     */
   
     @Override
     public String getMessage() {
       return "checkbox's tooltips must mention yes and no";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("checkbox", state)) {
          
          Object widget$578$10 = getProperty($it, "tooltip");
          boolean cond$589 = (evaluateContains(widget$578$10, "yes") && evaluateContains(widget$578$10, "no"));
          if (!cond$589) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class NumericTooltipsMustMentionInteger$746 extends DslOracle {
     /*
      assert for all input_numeric
          it.tooltip contains "integer"
          "numeric tooltips must mention integer".
     */
   
     @Override
     public String getMessage() {
       return "numeric tooltips must mention integer";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("input_numeric", state)) {
          
          Object widget$711$10 = getProperty($it, "tooltip");
          boolean cond$722 = evaluateContains(widget$711$10, "integer");
          if (!cond$722) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class PlaceholderAndTooltipMustBeEqual$868 extends DslOracle {
     /*
      assert for all input_text
          it.placeholder is equal to it.tooltip
          "placeholder and tooltip must be equal".
     */
   
     @Override
     public String getMessage() {
       return "placeholder and tooltip must be equal";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("input_text", state)) {
          
          Object widget$825$14 = getProperty($it, "placeholder");
          boolean cond$840 = evaluateIsEqualTo(widget$825$14, new java.util.function.Supplier<Object>() {
           public Object get() {
             
             Object widget$852$10 = getProperty($it, "tooltip");
             return widget$852$10; 
           }  
          }.get());
          if (!cond$840) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_287WasDisplayedAtSameTimeAsDiscount_div_227$1003 extends DslOracle {
     /*
      assert panel "discount_div_227" not is visible when panel "discount_div_287" is visible
        "discount_div_287 was displayed at same time as discount_div_227".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_287 was displayed at same time as discount_div_227";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$964$24 = getWidget("panel", "discount_div_287", state);
        if (widget$964$24 == null) {
          return Verdict.OK;
        }
        boolean cond$989 = evaluateIsStatus(widget$964$24, "visible");
        if (cond$989) {
          Widget widget$919$24 = getWidget("panel", "discount_div_227", state);
          if (widget$919$24 == null) {
            return Verdict.OK;
          }
          boolean cond$944 = !(evaluateIsStatus(widget$919$24, "visible"));
          if (!cond$944) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$919$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_353WasDisplayedAtSameTimeAsDiscount_div_227$1164 extends DslOracle {
     /*
      assert panel "discount_div_227" not is visible when panel "discount_div_353" is visible
        "discount_div_353 was displayed at same time as discount_div_227".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_353 was displayed at same time as discount_div_227";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1125$24 = getWidget("panel", "discount_div_353", state);
        if (widget$1125$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1150 = evaluateIsStatus(widget$1125$24, "visible");
        if (cond$1150) {
          Widget widget$1080$24 = getWidget("panel", "discount_div_227", state);
          if (widget$1080$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1105 = !(evaluateIsStatus(widget$1080$24, "visible"));
          if (!cond$1105) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1080$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_405WasDisplayedAtSameTimeAsDiscount_div_227$1325 extends DslOracle {
     /*
      assert panel "discount_div_227" not is visible when panel "discount_div_405" is visible
        "discount_div_405 was displayed at same time as discount_div_227".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_405 was displayed at same time as discount_div_227";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1286$24 = getWidget("panel", "discount_div_405", state);
        if (widget$1286$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1311 = evaluateIsStatus(widget$1286$24, "visible");
        if (cond$1311) {
          Widget widget$1241$24 = getWidget("panel", "discount_div_227", state);
          if (widget$1241$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1266 = !(evaluateIsStatus(widget$1241$24, "visible"));
          if (!cond$1266) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1241$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_227WasDisplayedAtSameTimeAsDiscount_div_287$1486 extends DslOracle {
     /*
      assert panel "discount_div_287" not is visible when panel "discount_div_227" is visible
        "discount_div_227 was displayed at same time as discount_div_287".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_227 was displayed at same time as discount_div_287";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1447$24 = getWidget("panel", "discount_div_227", state);
        if (widget$1447$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1472 = evaluateIsStatus(widget$1447$24, "visible");
        if (cond$1472) {
          Widget widget$1402$24 = getWidget("panel", "discount_div_287", state);
          if (widget$1402$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1427 = !(evaluateIsStatus(widget$1402$24, "visible"));
          if (!cond$1427) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1402$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_353WasDisplayedAtSameTimeAsDiscount_div_287$1647 extends DslOracle {
     /*
      assert panel "discount_div_287" not is visible when panel "discount_div_353" is visible
        "discount_div_353 was displayed at same time as discount_div_287".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_353 was displayed at same time as discount_div_287";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1608$24 = getWidget("panel", "discount_div_353", state);
        if (widget$1608$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1633 = evaluateIsStatus(widget$1608$24, "visible");
        if (cond$1633) {
          Widget widget$1563$24 = getWidget("panel", "discount_div_287", state);
          if (widget$1563$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1588 = !(evaluateIsStatus(widget$1563$24, "visible"));
          if (!cond$1588) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1563$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_405WasDisplayedAtSameTimeAsDiscount_div_287$1808 extends DslOracle {
     /*
      assert panel "discount_div_287" not is visible when panel "discount_div_405" is visible
        "discount_div_405 was displayed at same time as discount_div_287".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_405 was displayed at same time as discount_div_287";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1769$24 = getWidget("panel", "discount_div_405", state);
        if (widget$1769$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1794 = evaluateIsStatus(widget$1769$24, "visible");
        if (cond$1794) {
          Widget widget$1724$24 = getWidget("panel", "discount_div_287", state);
          if (widget$1724$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1749 = !(evaluateIsStatus(widget$1724$24, "visible"));
          if (!cond$1749) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1724$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_227WasDisplayedAtSameTimeAsDiscount_div_353$1969 extends DslOracle {
     /*
      assert panel "discount_div_353" not is visible when panel "discount_div_227" is visible
        "discount_div_227 was displayed at same time as discount_div_353".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_227 was displayed at same time as discount_div_353";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1930$24 = getWidget("panel", "discount_div_227", state);
        if (widget$1930$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1955 = evaluateIsStatus(widget$1930$24, "visible");
        if (cond$1955) {
          Widget widget$1885$24 = getWidget("panel", "discount_div_353", state);
          if (widget$1885$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1910 = !(evaluateIsStatus(widget$1885$24, "visible"));
          if (!cond$1910) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1885$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_287WasDisplayedAtSameTimeAsDiscount_div_353$2130 extends DslOracle {
     /*
      assert panel "discount_div_353" not is visible when panel "discount_div_287" is visible
        "discount_div_287 was displayed at same time as discount_div_353".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_287 was displayed at same time as discount_div_353";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2091$24 = getWidget("panel", "discount_div_287", state);
        if (widget$2091$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2116 = evaluateIsStatus(widget$2091$24, "visible");
        if (cond$2116) {
          Widget widget$2046$24 = getWidget("panel", "discount_div_353", state);
          if (widget$2046$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2071 = !(evaluateIsStatus(widget$2046$24, "visible"));
          if (!cond$2071) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2046$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_405WasDisplayedAtSameTimeAsDiscount_div_353$2291 extends DslOracle {
     /*
      assert panel "discount_div_353" not is visible when panel "discount_div_405" is visible
        "discount_div_405 was displayed at same time as discount_div_353".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_405 was displayed at same time as discount_div_353";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2252$24 = getWidget("panel", "discount_div_405", state);
        if (widget$2252$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2277 = evaluateIsStatus(widget$2252$24, "visible");
        if (cond$2277) {
          Widget widget$2207$24 = getWidget("panel", "discount_div_353", state);
          if (widget$2207$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2232 = !(evaluateIsStatus(widget$2207$24, "visible"));
          if (!cond$2232) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2207$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_227WasDisplayedAtSameTimeAsDiscount_div_405$2452 extends DslOracle {
     /*
      assert panel "discount_div_405" not is visible when panel "discount_div_227" is visible
        "discount_div_227 was displayed at same time as discount_div_405".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_227 was displayed at same time as discount_div_405";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2413$24 = getWidget("panel", "discount_div_227", state);
        if (widget$2413$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2438 = evaluateIsStatus(widget$2413$24, "visible");
        if (cond$2438) {
          Widget widget$2368$24 = getWidget("panel", "discount_div_405", state);
          if (widget$2368$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2393 = !(evaluateIsStatus(widget$2368$24, "visible"));
          if (!cond$2393) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2368$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_287WasDisplayedAtSameTimeAsDiscount_div_405$2613 extends DslOracle {
     /*
      assert panel "discount_div_405" not is visible when panel "discount_div_287" is visible
        "discount_div_287 was displayed at same time as discount_div_405".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_287 was displayed at same time as discount_div_405";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2574$24 = getWidget("panel", "discount_div_287", state);
        if (widget$2574$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2599 = evaluateIsStatus(widget$2574$24, "visible");
        if (cond$2599) {
          Widget widget$2529$24 = getWidget("panel", "discount_div_405", state);
          if (widget$2529$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2554 = !(evaluateIsStatus(widget$2529$24, "visible"));
          if (!cond$2554) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2529$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
   
   public static class Discount_div_353WasDisplayedAtSameTimeAsDiscount_div_405$2774 extends DslOracle {
     /*
      assert panel "discount_div_405" not is visible when panel "discount_div_353" is visible
        "discount_div_353 was displayed at same time as discount_div_405".
     */
   
     @Override
     public String getMessage() {
       return "discount_div_353 was displayed at same time as discount_div_405";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2735$24 = getWidget("panel", "discount_div_353", state);
        if (widget$2735$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2760 = evaluateIsStatus(widget$2735$24, "visible");
        if (cond$2760) {
          Widget widget$2690$24 = getWidget("panel", "discount_div_405", state);
          if (widget$2690$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2715 = !(evaluateIsStatus(widget$2690$24, "visible"));
          if (!cond$2715) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2690$24)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
   }
     
}
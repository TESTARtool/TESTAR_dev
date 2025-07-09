
package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class ArchiefAsserts {
   
   public static class PromptsMustEndIn$74 implements Oracle {
     /*
      assert for all static_text
          it matches "\\?$" 
          "prompts must end in ?".
     */
   
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
          
          boolean cond$53 = evaluateMatches($it, "\\?$");
          if (!cond$53) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class PromptsMustBeCapitalized$160 implements Oracle {
     /*
      assert for all static_text
          it matches "^[A-Z]" 
          "prompts must be capitalized".
     */
   
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
          
          boolean cond$137 = evaluateMatches($it, "^[A-Z]");
          if (!cond$137) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class PromptsMustNotShout$252 implements Oracle {
     /*
      assert for all static_text
          it not contains "!" 
          "prompts must not shout".
     */
   
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
          
          boolean cond$229 = !(evaluateContains($it, "!"));
          if (!cond$229) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class PromptsMustNotUseAllCaps$349 implements Oracle {
     /*
      assert for all static_text
          it not matches "^[A-Z\\ !]+$" 
          "prompts must not use all caps".
     */
   
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
          
          boolean cond$316 = !(evaluateMatches($it, "^[A-Z\\ !]+$"));
          if (!cond$316) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class ComputedQuestionMustHaveAValue$479 implements Oracle {
     /*
      assert for all input_text, input_numeric
          it not matches "^$" when it is readonly 
          "computed question must have a value".
     */
   
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
          
          boolean cond$461 = evaluateIsStatus($it, "readonly");
          if (cond$461) {
            boolean cond$436 = !(evaluateMatches($it, "^$"));
            if (!cond$436) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        for (Widget $it: getWidgets("input_numeric", state)) {
          
          boolean cond$461 = evaluateIsStatus($it, "readonly");
          if (cond$461) {
            boolean cond$436 = !(evaluateMatches($it, "^$"));
            if (!cond$436) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        return verdict;
     }
   }
   
   public static class CheckboxSTooltipsMustMentionYesAndNo$599 implements Oracle {
     /*
      assert for all checkbox
          it.tooltip contains "yes" and contains "no"
          "checkbox's tooltips must mention yes and no".
     */
   
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
          
          Object widget$550$10 = getProperty($it, "tooltip");
          boolean cond$561 = (evaluateContains(widget$550$10, "yes") && evaluateContains(widget$550$10, "no"));
          if (!cond$561) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class NumericTooltipsMustMentionInteger$718 implements Oracle {
     /*
      assert for all input_numeric
          it.tooltip contains "integer"
          "numeric tooltips must mention integer".
     */
   
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
          
          Object widget$683$10 = getProperty($it, "tooltip");
          boolean cond$694 = evaluateContains(widget$683$10, "integer");
          if (!cond$694) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class PlaceholderAndTooltipMustBeEqual$836 implements Oracle {
     /*
      assert for all input_text
          it.placeholder is equal to it.tooltip
          "placeholder and tooltip must be equal".
     */
   
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
          
          Object widget$793$14 = getProperty($it, "placeholder");
          boolean cond$808 = evaluateIsEqualTo(widget$793$14, new java.util.function.Supplier<Object>() {
           public Object get() {
             
             Object widget$820$10 = getProperty($it, "tooltip");
             return widget$820$10; 
           }  
          }.get());
          if (!cond$808) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_288WasDisplayedAtSameTimeAsDiscount_div_228$971 implements Oracle {
     /*
      assert panel "discount_div_228" not is visible when panel "discount_div_288" is visible
        "discount_div_288 was displayed at same time as discount_div_228".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_288 was displayed at same time as discount_div_228";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$932$24 = getWidget("panel", "discount_div_288", state);
        if (widget$932$24 == null) {
          return Verdict.OK;
        }
        boolean cond$957 = evaluateIsStatus(widget$932$24, "visible");
        if (cond$957) {
          Widget widget$887$24 = getWidget("panel", "discount_div_228", state);
          if (widget$887$24 == null) {
            return Verdict.OK;
          }
          boolean cond$912 = !(evaluateIsStatus(widget$887$24, "visible"));
          if (!cond$912) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$887$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_354WasDisplayedAtSameTimeAsDiscount_div_228$1132 implements Oracle {
     /*
      assert panel "discount_div_228" not is visible when panel "discount_div_354" is visible
        "discount_div_354 was displayed at same time as discount_div_228".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_354 was displayed at same time as discount_div_228";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1093$24 = getWidget("panel", "discount_div_354", state);
        if (widget$1093$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1118 = evaluateIsStatus(widget$1093$24, "visible");
        if (cond$1118) {
          Widget widget$1048$24 = getWidget("panel", "discount_div_228", state);
          if (widget$1048$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1073 = !(evaluateIsStatus(widget$1048$24, "visible"));
          if (!cond$1073) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1048$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_406WasDisplayedAtSameTimeAsDiscount_div_228$1293 implements Oracle {
     /*
      assert panel "discount_div_228" not is visible when panel "discount_div_406" is visible
        "discount_div_406 was displayed at same time as discount_div_228".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_406 was displayed at same time as discount_div_228";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1254$24 = getWidget("panel", "discount_div_406", state);
        if (widget$1254$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1279 = evaluateIsStatus(widget$1254$24, "visible");
        if (cond$1279) {
          Widget widget$1209$24 = getWidget("panel", "discount_div_228", state);
          if (widget$1209$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1234 = !(evaluateIsStatus(widget$1209$24, "visible"));
          if (!cond$1234) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1209$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_228WasDisplayedAtSameTimeAsDiscount_div_288$1454 implements Oracle {
     /*
      assert panel "discount_div_288" not is visible when panel "discount_div_228" is visible
        "discount_div_228 was displayed at same time as discount_div_288".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_228 was displayed at same time as discount_div_288";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1415$24 = getWidget("panel", "discount_div_228", state);
        if (widget$1415$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1440 = evaluateIsStatus(widget$1415$24, "visible");
        if (cond$1440) {
          Widget widget$1370$24 = getWidget("panel", "discount_div_288", state);
          if (widget$1370$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1395 = !(evaluateIsStatus(widget$1370$24, "visible"));
          if (!cond$1395) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1370$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_354WasDisplayedAtSameTimeAsDiscount_div_288$1615 implements Oracle {
     /*
      assert panel "discount_div_288" not is visible when panel "discount_div_354" is visible
        "discount_div_354 was displayed at same time as discount_div_288".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_354 was displayed at same time as discount_div_288";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1576$24 = getWidget("panel", "discount_div_354", state);
        if (widget$1576$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1601 = evaluateIsStatus(widget$1576$24, "visible");
        if (cond$1601) {
          Widget widget$1531$24 = getWidget("panel", "discount_div_288", state);
          if (widget$1531$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1556 = !(evaluateIsStatus(widget$1531$24, "visible"));
          if (!cond$1556) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1531$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_406WasDisplayedAtSameTimeAsDiscount_div_288$1776 implements Oracle {
     /*
      assert panel "discount_div_288" not is visible when panel "discount_div_406" is visible
        "discount_div_406 was displayed at same time as discount_div_288".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_406 was displayed at same time as discount_div_288";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1737$24 = getWidget("panel", "discount_div_406", state);
        if (widget$1737$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1762 = evaluateIsStatus(widget$1737$24, "visible");
        if (cond$1762) {
          Widget widget$1692$24 = getWidget("panel", "discount_div_288", state);
          if (widget$1692$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1717 = !(evaluateIsStatus(widget$1692$24, "visible"));
          if (!cond$1717) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1692$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_228WasDisplayedAtSameTimeAsDiscount_div_354$1937 implements Oracle {
     /*
      assert panel "discount_div_354" not is visible when panel "discount_div_228" is visible
        "discount_div_228 was displayed at same time as discount_div_354".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_228 was displayed at same time as discount_div_354";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1898$24 = getWidget("panel", "discount_div_228", state);
        if (widget$1898$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1923 = evaluateIsStatus(widget$1898$24, "visible");
        if (cond$1923) {
          Widget widget$1853$24 = getWidget("panel", "discount_div_354", state);
          if (widget$1853$24 == null) {
            return Verdict.OK;
          }
          boolean cond$1878 = !(evaluateIsStatus(widget$1853$24, "visible"));
          if (!cond$1878) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1853$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_288WasDisplayedAtSameTimeAsDiscount_div_354$2098 implements Oracle {
     /*
      assert panel "discount_div_354" not is visible when panel "discount_div_288" is visible
        "discount_div_288 was displayed at same time as discount_div_354".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_288 was displayed at same time as discount_div_354";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2059$24 = getWidget("panel", "discount_div_288", state);
        if (widget$2059$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2084 = evaluateIsStatus(widget$2059$24, "visible");
        if (cond$2084) {
          Widget widget$2014$24 = getWidget("panel", "discount_div_354", state);
          if (widget$2014$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2039 = !(evaluateIsStatus(widget$2014$24, "visible"));
          if (!cond$2039) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2014$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_406WasDisplayedAtSameTimeAsDiscount_div_354$2259 implements Oracle {
     /*
      assert panel "discount_div_354" not is visible when panel "discount_div_406" is visible
        "discount_div_406 was displayed at same time as discount_div_354".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_406 was displayed at same time as discount_div_354";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2220$24 = getWidget("panel", "discount_div_406", state);
        if (widget$2220$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2245 = evaluateIsStatus(widget$2220$24, "visible");
        if (cond$2245) {
          Widget widget$2175$24 = getWidget("panel", "discount_div_354", state);
          if (widget$2175$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2200 = !(evaluateIsStatus(widget$2175$24, "visible"));
          if (!cond$2200) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2175$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_228WasDisplayedAtSameTimeAsDiscount_div_406$2420 implements Oracle {
     /*
      assert panel "discount_div_406" not is visible when panel "discount_div_228" is visible
        "discount_div_228 was displayed at same time as discount_div_406".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_228 was displayed at same time as discount_div_406";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2381$24 = getWidget("panel", "discount_div_228", state);
        if (widget$2381$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2406 = evaluateIsStatus(widget$2381$24, "visible");
        if (cond$2406) {
          Widget widget$2336$24 = getWidget("panel", "discount_div_406", state);
          if (widget$2336$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2361 = !(evaluateIsStatus(widget$2336$24, "visible"));
          if (!cond$2361) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2336$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_288WasDisplayedAtSameTimeAsDiscount_div_406$2581 implements Oracle {
     /*
      assert panel "discount_div_406" not is visible when panel "discount_div_288" is visible
        "discount_div_288 was displayed at same time as discount_div_406".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_288 was displayed at same time as discount_div_406";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2542$24 = getWidget("panel", "discount_div_288", state);
        if (widget$2542$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2567 = evaluateIsStatus(widget$2542$24, "visible");
        if (cond$2567) {
          Widget widget$2497$24 = getWidget("panel", "discount_div_406", state);
          if (widget$2497$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2522 = !(evaluateIsStatus(widget$2497$24, "visible"));
          if (!cond$2522) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2497$24)); }
        }
        return verdict;
     }
   }
   
   public static class Discount_div_354WasDisplayedAtSameTimeAsDiscount_div_406$2742 implements Oracle {
     /*
      assert panel "discount_div_406" not is visible when panel "discount_div_354" is visible
        "discount_div_354 was displayed at same time as discount_div_406".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "discount_div_354 was displayed at same time as discount_div_406";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2703$24 = getWidget("panel", "discount_div_354", state);
        if (widget$2703$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2728 = evaluateIsStatus(widget$2703$24, "visible");
        if (cond$2728) {
          Widget widget$2658$24 = getWidget("panel", "discount_div_406", state);
          if (widget$2658$24 == null) {
            return Verdict.OK;
          }
          boolean cond$2683 = !(evaluateIsStatus(widget$2658$24, "visible"));
          if (!cond$2683) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2658$24)); }
        }
        return verdict;
     }
   }
     
}
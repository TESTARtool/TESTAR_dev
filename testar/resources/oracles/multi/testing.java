
package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.DslOracle;

public class testing {
   
  public static class WelcomeJohnSmithIsVisible$79 extends DslOracle {
    /*
     assert static_text "Welcome John Smith" is visible "Welcome John Smith is visible".
    */
    
    @Override
    public String getMessage() {
      return "Welcome John Smith is visible";
    }
    
    @Override
    public Verdict getVerdict(State state) {
       Verdict verdict = Verdict.OK;
       Widget widget$35$32 = getWidget("static_text", "Welcome John Smith", state);
       if (widget$35$32 == null) {
         return Verdict.OK;
       }
       boolean cond$68 = evaluateIsStatus(widget$35$32, "visible");
       if (!cond$68) { 
         verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$35$32)); 
       }
       markAsNonVacuous();
       return verdict;
    }
  }
  
   public static class SubmitButtonMustBeEnabled$94 extends DslOracle {
     /*
      assert button "btn_submit" is enabled "submit button must be enabled".
     */
   
     @Override
     public String getMessage() {
       return "submit button must be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$63$19 = getWidget("button", "btn_submit", state);
        if (widget$63$19 == null) {
          return Verdict.OK;
        }
        boolean cond$83 = evaluateIsStatus(widget$63$19, "enabled");
        if (!cond$83) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$63$19)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class EmailInputMustBeFilled$170 extends DslOracle {
     /*
      assert input_text "input_email" is filled "email input must be filled".
     */
   
     @Override
     public String getMessage() {
       return "email input must be filled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$135$24 = getWidget("input_text", "input_email", state);
        if (widget$135$24 == null) {
          return Verdict.OK;
        }
        boolean cond$160 = evaluateIsStatus(widget$135$24, "filled");
        if (!cond$160) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$135$24)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class AgeInputMustBeEnabled$245 extends DslOracle {
     /*
      assert input_numeric "input_age" is enabled "age input must be enabled".
     */
   
     @Override
     public String getMessage() {
       return "age input must be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$208$25 = getWidget("input_numeric", "input_age", state);
        if (widget$208$25 == null) {
          return Verdict.OK;
        }
        boolean cond$234 = evaluateIsStatus(widget$208$25, "enabled");
        if (!cond$234) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$208$25)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class StaticTextMustContainWelcome$327 extends DslOracle {
     /*
      assert static_text "static_info" contains "Welcome" "static text must contain welcome".
     */
   
     @Override
     public String getMessage() {
       return "static text must contain welcome";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$282$25 = getWidget("static_text", "static_info", state);
        if (widget$282$25 == null) {
          return Verdict.OK;
        }
        boolean cond$308 = evaluateContains(widget$282$25, "Welcome");
        if (!cond$308) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$282$25)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class TermsMustBeAccepted$403 extends DslOracle {
     /*
      assert checkbox "chk_terms" is checked "terms must be accepted".
     */
   
     @Override
     public String getMessage() {
       return "terms must be accepted";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$371$20 = getWidget("checkbox", "chk_terms", state);
        if (widget$371$20 == null) {
          return Verdict.OK;
        }
        boolean cond$392 = evaluateIsStatus(widget$371$20, "checked");
        if (!cond$392) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$371$20)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class GenderMustHaveOptions$485 extends DslOracle {
     /*
      assert radiogroup "gender_group" has nonempty options "gender must have options".
     */
   
     @Override
     public String getMessage() {
       return "gender must have options";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$438$25 = getWidget("radiogroup", "gender_group", state);
        if (widget$438$25 == null) {
          return Verdict.OK;
        }
        boolean cond$464 = evaluateHasAttribute(widget$438$25, "options");
        if (!cond$464) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$438$25)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class CountrySelectMustHaveItems$568 extends DslOracle {
     /*
      assert dropdown "country_select" has nonempty options "country select must have items".
     */
   
     @Override
     public String getMessage() {
       return "country select must have items";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$521$25 = getWidget("dropdown", "country_select", state);
        if (widget$521$25 == null) {
          return Verdict.OK;
        }
        boolean cond$547 = evaluateHasAttribute(widget$521$25, "options");
        if (!cond$547) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$521$25)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class StatusLabelMustShowActive$663 extends DslOracle {
     /*
      assert label "lbl_status".text is equal to "Status: Active" "status label must show active".
     */
   
     @Override
     public String getMessage() {
       return "status label must show active";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$610$18 = getWidget("label", "lbl_status", state);
        if (widget$610$18 == null) {
          return Verdict.OK;
        }
        Object widget$610$23 = getProperty(widget$610$18, "text");
        boolean cond$634 = evaluateIsEqualTo(widget$610$23, "Status: Active");
        if (!cond$634) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$610$18)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class LogoImageMustBeVisible$733 extends DslOracle {
     /*
      assert image "img_logo" is onscreen "logo image must be visible".
     */
   
     @Override
     public String getMessage() {
       return "logo image must be visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$704$16 = getWidget("image", "img_logo", state);
        if (widget$704$16 == null) {
          return Verdict.OK;
        }
        boolean cond$721 = evaluateIsStatus(widget$704$16, "onscreen");
        if (!cond$721) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$704$16)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class HomeLinkMustBeClickable$801 extends DslOracle {
     /*
      assert link "lnk_home" is clickable "home link must be clickable".
     */
   
     @Override
     public String getMessage() {
       return "home link must be clickable";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$772$15 = getWidget("link", "lnk_home", state);
        if (widget$772$15 == null) {
          return Verdict.OK;
        }
        boolean cond$788 = evaluateIsStatus(widget$772$15, "clickable");
        if (!cond$788) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$772$15)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class AlertMustBeVisible$873 extends DslOracle {
     /*
      assert alert "alert_warning" is visible "alert must be visible".
     */
   
     @Override
     public String getMessage() {
       return "alert must be visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$840$21 = getWidget("alert", "alert_warning", state);
        if (widget$840$21 == null) {
          return Verdict.OK;
        }
        boolean cond$862 = evaluateIsStatus(widget$840$21, "visible");
        if (!cond$862) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$840$21)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class GenericElementVisibleIfEnabled$962 extends DslOracle {
     /*
      assert element "generic_element" is visible when it is enabled "generic element visible if enabled".
     */
   
     @Override
     public String getMessage() {
       return "generic element visible if enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$906$25 = getWidget("element", "generic_element", state);
        if (widget$906$25 == null) {
          return Verdict.OK;
        }
        boolean cond$951 = evaluateIsStatus(widget$906$25, "enabled");
        if (cond$951) {
          boolean cond$932 = evaluateIsStatus(widget$906$25, "visible");
          if (!cond$932) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$906$25)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class HiddenButtonMustNotBeVisible$1074 extends DslOracle {
     /*
      assert button "btn_hidden" not is visible "hidden button must not be visible".
     */
   
     @Override
     public String getMessage() {
       return "hidden button must not be visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1039$19 = getWidget("button", "btn_hidden", state);
        if (widget$1039$19 == null) {
          return Verdict.OK;
        }
        boolean cond$1059 = !(evaluateIsStatus(widget$1039$19, "visible"));
        if (!cond$1059) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1039$19)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class OffscreenButtonMustNotBeOnscreen$1158 extends DslOracle {
     /*
      assert button "btn_offscreen" not is onscreen "offscreen button must not be onscreen".
     */
   
     @Override
     public String getMessage() {
       return "offscreen button must not be onscreen";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1119$22 = getWidget("button", "btn_offscreen", state);
        if (widget$1119$22 == null) {
          return Verdict.OK;
        }
        boolean cond$1142 = !(evaluateIsStatus(widget$1119$22, "onscreen"));
        if (!cond$1142) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1119$22)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class DisabledButtonMustNotBeEnabled$1244 extends DslOracle {
     /*
      assert button "btn_disabled" not is enabled "disabled button must not be enabled".
     */
   
     @Override
     public String getMessage() {
       return "disabled button must not be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1207$21 = getWidget("button", "btn_disabled", state);
        if (widget$1207$21 == null) {
          return Verdict.OK;
        }
        boolean cond$1229 = !(evaluateIsStatus(widget$1207$21, "enabled"));
        if (!cond$1229) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1207$21)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class EmptyInputMustNotBeFilled$1330 extends DslOracle {
     /*
      assert input_text "input_empty" not is filled "empty input must not be filled".
     */
   
     @Override
     public String getMessage() {
       return "empty input must not be filled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1291$24 = getWidget("input_text", "input_empty", state);
        if (widget$1291$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1316 = !(evaluateIsStatus(widget$1291$24, "filled"));
        if (!cond$1316) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1291$24)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class DisabledCheckboxMustNotBeEnabled$1411 extends DslOracle {
     /*
      assert checkbox "chk_disabled" not is enabled "disabled checkbox must not be enabled".
     */
   
     @Override
     public String getMessage() {
       return "disabled checkbox must not be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1372$23 = getWidget("checkbox", "chk_disabled", state);
        if (widget$1372$23 == null) {
          return Verdict.OK;
        }
        boolean cond$1396 = !(evaluateIsStatus(widget$1372$23, "enabled"));
        if (!cond$1396) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1372$23)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class LabelTextMustBeVisible$1518 extends DslOracle {
     /*
      assert for all label it.color not is equal to it.backgroundColor "label text must be visible".
     */
   
     @Override
     public String getMessage() {
       return "label text must be visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("label", state)) {
          
          Object widget$1474$8 = getProperty($it, "color");
          boolean cond$1483 = !(evaluateIsEqualTo(widget$1474$8, new java.util.function.Supplier<Object>() {
           public Object get() {
             
             Object widget$1499$18 = getProperty($it, "backgroundColor");
             return widget$1499$18; 
           }  
          }.get()));
          if (!cond$1483) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class ImageMustLackAlttext$1596 extends DslOracle {
     /*
      assert for all image it.alttext is equal to "" "image must lack alttext".
     */
   
     @Override
     public String getMessage() {
       return "image must lack alttext";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("image", state)) {
          
          Object widget$1570$10 = getProperty($it, "alttext");
          boolean cond$1581 = evaluateIsEqualTo(widget$1570$10, "");
          if (!cond$1581) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class AriaLabeledButtonMustBeClickable$1692 extends DslOracle {
     /*
      assert button "btn_aria" is clickable "aria-labeled button must be clickable".
     */
   
     @Override
     public String getMessage() {
       return "aria-labeled button must be clickable";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1661$17 = getWidget("button", "btn_aria", state);
        if (widget$1661$17 == null) {
          return Verdict.OK;
        }
        boolean cond$1679 = evaluateIsStatus(widget$1661$17, "clickable");
        if (!cond$1679) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1661$17)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class AriaAlertMustBeVisible$1771 extends DslOracle {
     /*
      assert alert "aria_alert" is visible "aria alert must be visible".
     */
   
     @Override
     public String getMessage() {
       return "aria alert must be visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1741$18 = getWidget("alert", "aria_alert", state);
        if (widget$1741$18 == null) {
          return Verdict.OK;
        }
        boolean cond$1760 = evaluateIsStatus(widget$1741$18, "visible");
        if (!cond$1760) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1741$18)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class RequiredInputMustMatchPattern$1882 extends DslOracle {
     /*
      assert input_text "input_required" matches "[A-Z]{3}" "required input must match pattern".
     */
   
     @Override
     public String getMessage() {
       return "required input must match pattern";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1835$27 = getWidget("input_text", "input_required", state);
        if (widget$1835$27 == null) {
          return Verdict.OK;
        }
        boolean cond$1863 = evaluateMatches(widget$1835$27, "[A-Z]{3}");
        if (!cond$1863) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1835$27)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class AutofocusFieldMustBeFocused$1992 extends DslOracle {
     /*
      assert input_text "focus_field" is focused "autofocus field must be focused".
     */
   
     @Override
     public String getMessage() {
       return "autofocus field must be focused";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1956$24 = getWidget("input_text", "focus_field", state);
        if (widget$1956$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1981 = evaluateIsStatus(widget$1956$24, "focused");
        if (!cond$1981) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1956$24)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class EmailInputMustMatchFormat$2100 extends DslOracle {
     /*
      assert input_text "input_email2" matches ".+@.+" "email input must match format".
     */
   
     @Override
     public String getMessage() {
       return "email input must match format";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2058$25 = getWidget("input_text", "input_email2", state);
        if (widget$2058$25 == null) {
          return Verdict.OK;
        }
        boolean cond$2084 = evaluateMatches(widget$2058$25, ".+@.+");
        if (!cond$2084) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2058$25)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class PasswordMustNotBeEmpty$2193 extends DslOracle {
     /*
      assert input_text "input_password".text not is equal to "" "password must not be empty".
     */
   
     @Override
     public String getMessage() {
       return "password must not be empty";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2141$27 = getWidget("input_text", "input_password", state);
        if (widget$2141$27 == null) {
          return Verdict.OK;
        }
        Object widget$2141$32 = getProperty(widget$2141$27, "text");
        boolean cond$2174 = !(evaluateIsEqualTo(widget$2141$32, ""));
        if (!cond$2174) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2141$27)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class NestedDropdownMustNotBeEmpty$2301 extends DslOracle {
     /*
      assert dropdown "nested_dropdown" has nonempty options "nested dropdown must not be empty".
     */
   
     @Override
     public String getMessage() {
       return "nested dropdown must not be empty";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2253$26 = getWidget("dropdown", "nested_dropdown", state);
        if (widget$2253$26 == null) {
          return Verdict.OK;
        }
        boolean cond$2280 = evaluateHasAttribute(widget$2253$26, "options");
        if (!cond$2280) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2253$26)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class RadioInNestedPanelMustNotBeChecked$2379 extends DslOracle {
     /*
      assert radio "nested_r1" not is checked "radio in nested panel must not be checked".
     */
   
     @Override
     public String getMessage() {
       return "radio in nested panel must not be checked";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2346$17 = getWidget("radio", "nested_r1", state);
        if (widget$2346$17 == null) {
          return Verdict.OK;
        }
        boolean cond$2364 = !(evaluateIsStatus(widget$2346$17, "checked"));
        if (!cond$2364) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2346$17)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class DelayedElementMustAppearAfterTimeout$2519 extends DslOracle {
     /*
      assert element "delayed_element" is visible "delayed element must appear after timeout".
     */
   
     @Override
     public String getMessage() {
       return "delayed element must appear after timeout";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2482$25 = getWidget("element", "delayed_element", state);
        if (widget$2482$25 == null) {
          return Verdict.OK;
        }
        boolean cond$2508 = evaluateIsStatus(widget$2482$25, "visible");
        if (!cond$2508) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2482$25)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
     
}

package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class testing {
   
   public static class SubmitButtonMustBeEnabled$57 implements Oracle {
     /*
      assert button "btn_submit" is enabled "submit button must be enabled".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "submit button must be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$26$19 = getWidget("button", "btn_submit", state);
        if (widget$26$19 == null) {
          return Verdict.OK;
        }
        boolean cond$46 = evaluateIsStatus(widget$26$19, "enabled");
        if (!cond$46) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$26$19)); }
        return verdict;
     }
   }
   
   public static class EmailInputMustBeFilled$133 implements Oracle {
     /*
      assert input_text "input_email" is filled "email input must be filled".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "email input must be filled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$98$24 = getWidget("input_text", "input_email", state);
        if (widget$98$24 == null) {
          return Verdict.OK;
        }
        boolean cond$123 = evaluateIsStatus(widget$98$24, "filled");
        if (!cond$123) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$98$24)); }
        return verdict;
     }
   }
   
   public static class AgeInputMustBeEnabled$208 implements Oracle {
     /*
      assert input_numeric "input_age" is enabled "age input must be enabled".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "age input must be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$171$25 = getWidget("input_numeric", "input_age", state);
        if (widget$171$25 == null) {
          return Verdict.OK;
        }
        boolean cond$197 = evaluateIsStatus(widget$171$25, "enabled");
        if (!cond$197) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$171$25)); }
        return verdict;
     }
   }
   
   public static class StaticTextMustContainWelcome$290 implements Oracle {
     /*
      assert static_text "static_info" contains "Welcome" "static text must contain welcome".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "static text must contain welcome";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$245$25 = getWidget("static_text", "static_info", state);
        if (widget$245$25 == null) {
          return Verdict.OK;
        }
        boolean cond$271 = evaluateContains(widget$245$25, "Welcome");
        if (!cond$271) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$245$25)); }
        return verdict;
     }
   }
   
   public static class TermsMustBeAccepted$366 implements Oracle {
     /*
      assert checkbox "chk_terms" is checked "terms must be accepted".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "terms must be accepted";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$334$20 = getWidget("checkbox", "chk_terms", state);
        if (widget$334$20 == null) {
          return Verdict.OK;
        }
        boolean cond$355 = evaluateIsStatus(widget$334$20, "checked");
        if (!cond$355) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$334$20)); }
        return verdict;
     }
   }
   
   public static class GenderMustHaveOptions$448 implements Oracle {
     /*
      assert radiogroup "gender_group" has nonempty options "gender must have options".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "gender must have options";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$401$25 = getWidget("radiogroup", "gender_group", state);
        if (widget$401$25 == null) {
          return Verdict.OK;
        }
        boolean cond$427 = evaluateHasAttribute(widget$401$25, "options");
        if (!cond$427) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$401$25)); }
        return verdict;
     }
   }
   
   public static class CountrySelectMustHaveItems$531 implements Oracle {
     /*
      assert dropdown "country_select" has nonempty options "country select must have items".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "country select must have items";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$484$25 = getWidget("dropdown", "country_select", state);
        if (widget$484$25 == null) {
          return Verdict.OK;
        }
        boolean cond$510 = evaluateHasAttribute(widget$484$25, "options");
        if (!cond$510) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$484$25)); }
        return verdict;
     }
   }
   
   public static class StatusLabelMustShowActive$626 implements Oracle {
     /*
      assert label "lbl_status".text is equal to "Status: Active" "status label must show active".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "status label must show active";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$573$18 = getWidget("label", "lbl_status", state);
        if (widget$573$18 == null) {
          return Verdict.OK;
        }
        Object widget$573$23 = getProperty(widget$573$18, "text");
        boolean cond$597 = evaluateIsEqualTo(widget$573$23, "Status: Active");
        if (!cond$597) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$573$18)); }
        return verdict;
     }
   }
   
   public static class LogoImageMustBeVisible$696 implements Oracle {
     /*
      assert image "img_logo" is onscreen "logo image must be visible".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "logo image must be visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$667$16 = getWidget("image", "img_logo", state);
        if (widget$667$16 == null) {
          return Verdict.OK;
        }
        boolean cond$684 = evaluateIsStatus(widget$667$16, "onscreen");
        if (!cond$684) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$667$16)); }
        return verdict;
     }
   }
   
   public static class HomeLinkMustBeClickable$764 implements Oracle {
     /*
      assert link "lnk_home" is clickable "home link must be clickable".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "home link must be clickable";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$735$15 = getWidget("link", "lnk_home", state);
        if (widget$735$15 == null) {
          return Verdict.OK;
        }
        boolean cond$751 = evaluateIsStatus(widget$735$15, "clickable");
        if (!cond$751) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$735$15)); }
        return verdict;
     }
   }
   
   public static class AlertMustBeVisible$836 implements Oracle {
     /*
      assert alert "alert_warning" is visible "alert must be visible".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "alert must be visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$803$21 = getWidget("alert", "alert_warning", state);
        if (widget$803$21 == null) {
          return Verdict.OK;
        }
        boolean cond$825 = evaluateIsStatus(widget$803$21, "visible");
        if (!cond$825) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$803$21)); }
        return verdict;
     }
   }
   
   public static class PanelChildrenMustBeEnabled$907 implements Oracle {
     /*
      assert menu "menu_main".children are enabled "panel children must be enabled".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "panel children must be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$869$16 = getWidget("menu", "menu_main", state);
        if (widget$869$16 == null) {
          return Verdict.OK;
        }
        Object widget$869$25 = getProperty(widget$869$16, "children");
        boolean cond$895 = evaluateAreStatus((java.util.List<Object>)widget$869$25, "enabled");
        if (!cond$895) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$869$16)); }
        return verdict;
     }
   }
   
   public static class GenericElementVisibleIfEnabled$1005 implements Oracle {
     /*
      assert element "generic_element" is visible when it is enabled "generic element visible if enabled".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "generic element visible if enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$949$25 = getWidget("element", "generic_element", state);
        if (widget$949$25 == null) {
          return Verdict.OK;
        }
        boolean cond$994 = evaluateIsStatus(widget$949$25, "enabled");
        if (cond$994) {
          boolean cond$975 = evaluateIsStatus(widget$949$25, "visible");
          if (!cond$975) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$949$25)); }
        }
        return verdict;
     }
   }
   
   public static class HiddenButtonMustNotBeVisible$1117 implements Oracle {
     /*
      assert button "btn_hidden" not is visible "hidden button must not be visible".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "hidden button must not be visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1082$19 = getWidget("button", "btn_hidden", state);
        if (widget$1082$19 == null) {
          return Verdict.OK;
        }
        boolean cond$1102 = !(evaluateIsStatus(widget$1082$19, "visible"));
        if (!cond$1102) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1082$19)); }
        return verdict;
     }
   }
   
   public static class OffscreenButtonMustNotBeOnscreen$1201 implements Oracle {
     /*
      assert button "btn_offscreen" not is onscreen "offscreen button must not be onscreen".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "offscreen button must not be onscreen";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1162$22 = getWidget("button", "btn_offscreen", state);
        if (widget$1162$22 == null) {
          return Verdict.OK;
        }
        boolean cond$1185 = !(evaluateIsStatus(widget$1162$22, "onscreen"));
        if (!cond$1185) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1162$22)); }
        return verdict;
     }
   }
   
   public static class DisabledButtonMustNotBeEnabled$1287 implements Oracle {
     /*
      assert button "btn_disabled" not is enabled "disabled button must not be enabled".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "disabled button must not be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1250$21 = getWidget("button", "btn_disabled", state);
        if (widget$1250$21 == null) {
          return Verdict.OK;
        }
        boolean cond$1272 = !(evaluateIsStatus(widget$1250$21, "enabled"));
        if (!cond$1272) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1250$21)); }
        return verdict;
     }
   }
   
   public static class EmptyInputMustNotBeFilled$1373 implements Oracle {
     /*
      assert input_text "input_empty" not is filled "empty input must not be filled".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "empty input must not be filled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1334$24 = getWidget("input_text", "input_empty", state);
        if (widget$1334$24 == null) {
          return Verdict.OK;
        }
        boolean cond$1359 = !(evaluateIsStatus(widget$1334$24, "filled"));
        if (!cond$1359) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1334$24)); }
        return verdict;
     }
   }
   
   public static class DisabledCheckboxMustNotBeEnabled$1454 implements Oracle {
     /*
      assert checkbox "chk_disabled" not is enabled "disabled checkbox must not be enabled".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "disabled checkbox must not be enabled";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1415$23 = getWidget("checkbox", "chk_disabled", state);
        if (widget$1415$23 == null) {
          return Verdict.OK;
        }
        boolean cond$1439 = !(evaluateIsStatus(widget$1415$23, "enabled"));
        if (!cond$1439) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1415$23)); }
        return verdict;
     }
   }

   public static class LabelTextMustBeVisible$1518 implements Oracle {
       /*
        assert for all label it.color not is equal to it.backgroundColor "label text must be visible".
        */

       @Override
       public void initialize() { }

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
               if (!cond$1483) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
           }
           return verdict;
       }
   }

   public static class ImageMustLackAlttext$1637 implements Oracle {
     /*
      assert for all image it.alttext is equal to "" "image must lack alttext".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "image must lack alttext";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("image", state)) {
          
          Object widget$1611$10 = getProperty($it, "alttext");
          boolean cond$1622 = evaluateIsEqualTo(widget$1611$10, "");
          if (!cond$1622) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class AriaLabeledButtonMustBeClickable$1733 implements Oracle {
     /*
      assert button "btn_aria" is clickable "aria-labeled button must be clickable".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "aria-labeled button must be clickable";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1702$17 = getWidget("button", "btn_aria", state);
        if (widget$1702$17 == null) {
          return Verdict.OK;
        }
        boolean cond$1720 = evaluateIsStatus(widget$1702$17, "clickable");
        if (!cond$1720) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1702$17)); }
        return verdict;
     }
   }
   
   public static class AriaAlertMustBeVisible$1812 implements Oracle {
     /*
      assert alert "aria_alert" is visible "aria alert must be visible".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "aria alert must be visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1782$18 = getWidget("alert", "aria_alert", state);
        if (widget$1782$18 == null) {
          return Verdict.OK;
        }
        boolean cond$1801 = evaluateIsStatus(widget$1782$18, "visible");
        if (!cond$1801) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1782$18)); }
        return verdict;
     }
   }
   
   public static class RequiredInputMustMatchPattern$1923 implements Oracle {
     /*
      assert input_text "input_required" matches "[A-Z]{3}" "required input must match pattern".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "required input must match pattern";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1876$27 = getWidget("input_text", "input_required", state);
        if (widget$1876$27 == null) {
          return Verdict.OK;
        }
        boolean cond$1904 = evaluateMatches(widget$1876$27, "[A-Z]{3}");
        if (!cond$1904) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1876$27)); }
        return verdict;
     }
   }
   
   public static class AutofocusFieldMustBeFocused$2033 implements Oracle {
     /*
      assert input_text "focus_field" is focused "autofocus field must be focused".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "autofocus field must be focused";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1997$24 = getWidget("input_text", "focus_field", state);
        if (widget$1997$24 == null) {
          return Verdict.OK;
        }
        boolean cond$2022 = evaluateIsStatus(widget$1997$24, "focused");
        if (!cond$2022) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1997$24)); }
        return verdict;
     }
   }
   
   public static class EmailInputMustMatchFormat$2141 implements Oracle {
     /*
      assert input_text "input_email2" matches ".+@.+" "email input must match format".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "email input must match format";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2099$25 = getWidget("input_text", "input_email2", state);
        if (widget$2099$25 == null) {
          return Verdict.OK;
        }
        boolean cond$2125 = evaluateMatches(widget$2099$25, ".+@.+");
        if (!cond$2125) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2099$25)); }
        return verdict;
     }
   }
   
   public static class PasswordMustNotBeEmpty$2234 implements Oracle {
     /*
      assert input_text "input_password".text not is equal to "" "password must not be empty".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "password must not be empty";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2182$27 = getWidget("input_text", "input_password", state);
        if (widget$2182$27 == null) {
          return Verdict.OK;
        }
        Object widget$2182$32 = getProperty(widget$2182$27, "text");
        boolean cond$2215 = !(evaluateIsEqualTo(widget$2182$32, ""));
        if (!cond$2215) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2182$27)); }
        return verdict;
     }
   }
   
   public static class NestedDropdownMustNotBeEmpty$2342 implements Oracle {
     /*
      assert dropdown "nested_dropdown" has nonempty options "nested dropdown must not be empty".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "nested dropdown must not be empty";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2294$26 = getWidget("dropdown", "nested_dropdown", state);
        if (widget$2294$26 == null) {
          return Verdict.OK;
        }
        boolean cond$2321 = evaluateHasAttribute(widget$2294$26, "options");
        if (!cond$2321) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2294$26)); }
        return verdict;
     }
   }
   
   public static class RadioInNestedPanelMustNotBeSelected$2421 implements Oracle {
     /*
      assert radio "nested_r1" not is selected "radio in nested panel must not be selected".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "radio in nested panel must not be selected";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2387$17 = getWidget("radio", "nested_r1", state);
        if (widget$2387$17 == null) {
          return Verdict.OK;
        }
        boolean cond$2405 = !(evaluateIsStatus(widget$2387$17, "selected"));
        if (!cond$2405) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2387$17)); }
        return verdict;
     }
   }
   
   public static class DelayedElementMustAppearAfterTimeout$2562 implements Oracle {
     /*
      assert element "delayed_element" is visible "delayed element must appear after timeout".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "delayed element must appear after timeout";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2525$25 = getWidget("element", "delayed_element", state);
        if (widget$2525$25 == null) {
          return Verdict.OK;
        }
        boolean cond$2551 = evaluateIsStatus(widget$2525$25, "visible");
        if (!cond$2551) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2525$25)); }
        return verdict;
     }
   }
     
}
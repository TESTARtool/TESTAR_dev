
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class testing {

	/*
    assert button "btn_submit" is enabled "submit button must be enabled".
	 */
	public static class SubmitButtonMustBeEnabled implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "submit button must be enabled";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$23$19 = getWidget("button", "btn_submit", state);
			if (widget$23$19 == null) {
				return Verdict.OK;
			}
			boolean cond$43 = evaluateIsStatus(widget$23$19, "enabled");
			if (!cond$43) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$23$19)); }

			return verdict;
		}

	}

	/*
    assert input_text "input_email" is filled "email input must be filled".
	 */
	public static class EmailInputMustBeFilled implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "email input must be filled";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$94$24 = getWidget("input_text", "input_email", state);
			if (widget$94$24 == null) {
				return Verdict.OK;
			}
			boolean cond$119 = evaluateIsStatus(widget$94$24, "filled");
			if (!cond$119) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$94$24)); }

			return verdict;
		}

	}

	/*
    assert input_numeric "input_age" is enabled "age input must be enabled".
	 */
	public static class AgeInputMustBeEnabled implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "age input must be enabled";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$166$25 = getWidget("input_numeric", "input_age", state);
			if (widget$166$25 == null) {
				return Verdict.OK;
			}
			boolean cond$192 = evaluateIsStatus(widget$166$25, "enabled");
			if (!cond$192) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$166$25)); }

			return verdict;
		}

	}

	/*
    assert static_text "static_info" contains "Welcome" "static text must contain welcome".
	 */
	public static class StaticTextMustContainWelcome implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "static text must contain welcome";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$239$25 = getWidget("static_text", "static_info", state);
			if (widget$239$25 == null) {
				return Verdict.OK;
			}
			boolean cond$265 = evaluateContains(widget$239$25, "Welcome");
			if (!cond$265) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$239$25)); }

			return verdict;
		}

	}

	/*
    assert checkbox "chk_terms" is selected "terms must be accepted".
	 */
	public static class TermsMustBeAccepted implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "terms must be accepted";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$327$20 = getWidget("checkbox", "chk_terms", state);
			if (widget$327$20 == null) {
				return Verdict.OK;
			}
			boolean cond$348 = evaluateIsStatus(widget$327$20, "selected");
			if (!cond$348) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$327$20)); }

			return verdict;
		}

	}

	/*
    assert radiogroup "gender_group" has nonempty options "gender must have options".
	 */
	public static class GenderMustHaveOptions implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "gender must have options";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$393$25 = getWidget("radiogroup", "gender_group", state);
			if (widget$393$25 == null) {
				return Verdict.OK;
			}
			boolean cond$419 = evaluateHasAttribute(widget$393$25, "options");
			if (!cond$419) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$393$25)); }

			return verdict;
		}

	}

	/*
    assert dropdown "country_select" has nonempty elements "country select must have items".
	 */
	public static class CountrySelectMustHaveItems implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "country select must have items";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$475$25 = getWidget("dropdown", "country_select", state);
			if (widget$475$25 == null) {
				return Verdict.OK;
			}
			boolean cond$501 = evaluateHasAttribute(widget$475$25, "elements");
			if (!cond$501) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$475$25)); }

			return verdict;
		}

	}

	/*
    assert label "lbl_status" is equal to "Status: Active" "status label must show active".
	 */
	public static class StatusLabelMustShowActive implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "status label must show active";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$564$18 = getWidget("label", "lbl_status", state);
			if (widget$564$18 == null) {
				return Verdict.OK;
			}
			boolean cond$583 = evaluateIsEqualTo(widget$564$18, "Status: Active");
			if (!cond$583) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$564$18)); }

			return verdict;
		}

	}

	/*
    assert image "img_logo" is onscreen "logo image must be visible".
	 */
	public static class LogoImageMustBeVisible implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "logo image must be visible";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$652$16 = getWidget("image", "img_logo", state);
			if (widget$652$16 == null) {
				return Verdict.OK;
			}
			boolean cond$669 = evaluateIsStatus(widget$652$16, "onscreen");
			if (!cond$669) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$652$16)); }

			return verdict;
		}

	}

	/*
    assert link "lnk_home" is clickable "home link must be clickable".
	 */
	public static class HomeLinkMustBeClickable implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "home link must be clickable";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$718$15 = getWidget("link", "lnk_home", state);
			if (widget$718$15 == null) {
				return Verdict.OK;
			}
			boolean cond$734 = evaluateIsStatus(widget$718$15, "clickable");
			if (!cond$734) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$718$15)); }

			return verdict;
		}

	}

	/*
    assert alert "alert_warning" is visible "alert must be visible".
	 */
	public static class AlertMustBeVisible implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "alert must be visible";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$785$21 = getWidget("alert", "alert_warning", state);
			if (widget$785$21 == null) {
				return Verdict.OK;
			}
			boolean cond$807 = evaluateIsStatus(widget$785$21, "visible");
			if (!cond$807) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$785$21)); }

			return verdict;
		}

	}

	/*
    assert panel "panel_main".children has nonempty button "panel must contain a button".
	 */
	public static class PanelMustContainAButton implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "panel must contain a button";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$850$18 = getWidget("panel", "panel_main", state);
			if (widget$850$18 == null) {
				return Verdict.OK;
			}
			Object property$850$27 = getProperty(widget$850$18, "children");
			boolean cond$878 = evaluateHasAttribute(property$850$27, "button");
			if (!cond$878) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$850$18)); }

			return verdict;
		}

	}

	/*
    assert element "generic_element" is visible when it is enabled "generic element visible if enabled".
	 */
	public static class GenericElementVisibleIfEnabled implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "generic element visible if enabled";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$936$25 = getWidget("element", "generic_element", state);
			if (widget$936$25 == null) {
				return Verdict.OK;
			}
			boolean cond$981 = evaluateIsStatus(widget$936$25, "enabled");
			if (cond$981) {
				boolean cond$962 = evaluateIsStatus(widget$936$25, "visible");
				if (!cond$962) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$936$25)); }
			}
			return verdict;
		}

	}

	/*
    assert button "btn_hidden" not is visible "hidden button must not be visible".
	 */
	public static class HiddenButtonMustNotBeVisible implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "hidden button must not be visible";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$1066$19 = getWidget("button", "btn_hidden", state);
			if (widget$1066$19 == null) {
				return Verdict.OK;
			}
			boolean cond$1086 = !(evaluateIsStatus(widget$1066$19, "visible"));
			if (!cond$1086) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1066$19)); }

			return verdict;
		}

	}

	/*
    assert button "btn_offscreen" not is onscreen "offscreen button must not be onscreen".
	 */
	public static class OffscreenButtonMustNotBeOnscreen implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "offscreen button must not be onscreen";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$1145$22 = getWidget("button", "btn_offscreen", state);
			if (widget$1145$22 == null) {
				return Verdict.OK;
			}
			boolean cond$1168 = !(evaluateIsStatus(widget$1145$22, "onscreen"));
			if (!cond$1168) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1145$22)); }

			return verdict;
		}

	}

	/*
    assert button "btn_disabled" not is enabled "disabled button must not be enabled".
	 */
	public static class DisabledButtonMustNotBeEnabled implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "disabled button must not be enabled";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$1232$21 = getWidget("button", "btn_disabled", state);
			if (widget$1232$21 == null) {
				return Verdict.OK;
			}
			boolean cond$1254 = !(evaluateIsStatus(widget$1232$21, "enabled"));
			if (!cond$1254) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1232$21)); }

			return verdict;
		}

	}

	/*
    assert input_text "input_empty" not is filled "empty input must not be filled".
	 */
	public static class EmptyInputMustNotBeFilled implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "empty input must not be filled";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$1315$24 = getWidget("input_text", "input_empty", state);
			if (widget$1315$24 == null) {
				return Verdict.OK;
			}
			boolean cond$1340 = !(evaluateIsStatus(widget$1315$24, "filled"));
			if (!cond$1340) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1315$24)); }

			return verdict;
		}

	}

	/*
    assert checkbox "chk_disabled" not is enabled "disabled checkbox must not be enabled".
	 */
	public static class DisabledCheckboxMustNotBeEnabled implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "disabled checkbox must not be enabled";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$1395$23 = getWidget("checkbox", "chk_disabled", state);
			if (widget$1395$23 == null) {
				return Verdict.OK;
			}
			boolean cond$1419 = !(evaluateIsStatus(widget$1395$23, "enabled"));
			if (!cond$1419) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1395$23)); }

			return verdict;
		}

	}

	/*
    assert for all label it.color not is equal to background color "label text must be visible".
	 */
	public static class LabelTextMustBeVisible implements Oracle {

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

				Object property$1496$8 = getProperty($it, "color");
				boolean cond$1505 = !(evaluateIsEqualTo(property$1496$8, getBackgroundColor()));
				if (!cond$1505) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

			}
			return verdict;
		}

	}

	/*
    assert for all image it.alttext is equal to "" "image must lack alttext".
	 */
	public static class ImageMustLackAlttext implements Oracle {

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

				Object property$1589$10 = getProperty($it, "alttext");
				boolean cond$1600 = evaluateIsEqualTo(property$1589$10, "");
				if (!cond$1600) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

			}
			return verdict;
		}

	}

	/*
    assert button "btn_aria" is clickable "aria-labeled button must be clickable".
	 */
	public static class AriaLabeledButtonMustBeClickable implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "aria-labeled button must be clickable";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$1677$17 = getWidget("button", "btn_aria", state);
			if (widget$1677$17 == null) {
				return Verdict.OK;
			}
			boolean cond$1695 = evaluateIsStatus(widget$1677$17, "clickable");
			if (!cond$1695) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1677$17)); }

			return verdict;
		}

	}

	/*
    assert alert "aria_alert" is visible "aria alert must be visible".
	 */
	public static class AriaAlertMustBeVisible implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "aria alert must be visible";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$1756$18 = getWidget("alert", "aria_alert", state);
			if (widget$1756$18 == null) {
				return Verdict.OK;
			}
			boolean cond$1775 = evaluateIsStatus(widget$1756$18, "visible");
			if (!cond$1775) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1756$18)); }

			return verdict;
		}

	}

	/*
    assert input_text "input_required" matches "[A-Z]{3}" "required input must match pattern".
	 */
	public static class RequiredInputMustMatchPattern implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "required input must match pattern";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$1847$27 = getWidget("input_text", "input_required", state);
			if (widget$1847$27 == null) {
				return Verdict.OK;
			}
			boolean cond$1875 = evaluateMatches(widget$1847$27, "[A-Z]{3}");
			if (!cond$1875) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1847$27)); }

			return verdict;
		}

	}

	/*
    assert input_text "focus_field" is focused "autofocus field must be focused".
	 */
	public static class AutofocusFieldMustBeFocused implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "autofocus field must be focused";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$1965$24 = getWidget("input_text", "focus_field", state);
			if (widget$1965$24 == null) {
				return Verdict.OK;
			}
			boolean cond$1990 = evaluateIsStatus(widget$1965$24, "focused");
			if (!cond$1990) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1965$24)); }

			return verdict;
		}

	}

	/*
    assert input_text "input_email2" matches ".+@.+" "email input must match format".
	 */
	public static class EmailInputMustMatchFormat implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "email input must match format";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$2064$25 = getWidget("input_text", "input_email2", state);
			if (widget$2064$25 == null) {
				return Verdict.OK;
			}
			boolean cond$2090 = evaluateMatches(widget$2064$25, ".+@.+");
			if (!cond$2090) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2064$25)); }

			return verdict;
		}

	}

	/*
    assert input_text "input_password" not is equal to "" "password must not be empty".
	 */
	public static class PasswordMustNotBeEmpty implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "password must not be empty";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$2146$27 = getWidget("input_text", "input_password", state);
			if (widget$2146$27 == null) {
				return Verdict.OK;
			}
			boolean cond$2174 = !(evaluateIsEqualTo(widget$2146$27, ""));
			if (!cond$2174) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2146$27)); }

			return verdict;
		}

	}

	/*
    assert dropdown "nested_dropdown" has nonempty elements "nested dropdown must not be empty".
	 */
	public static class NestedDropdownMustNotBeEmpty implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "nested dropdown must not be empty";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$2250$26 = getWidget("dropdown", "nested_dropdown", state);
			if (widget$2250$26 == null) {
				return Verdict.OK;
			}
			boolean cond$2277 = evaluateHasAttribute(widget$2250$26, "elements");
			if (!cond$2277) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2250$26)); }

			return verdict;
		}

	}

	/*
    assert radio "nested_r1" not is selected "radio in nested panel must not be selected".
	 */
	public static class RadioInNestedPanelMustNotBeSelected implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "radio in nested panel must not be selected";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$2343$17 = getWidget("radio", "nested_r1", state);
			if (widget$2343$17 == null) {
				return Verdict.OK;
			}
			boolean cond$2361 = !(evaluateIsStatus(widget$2343$17, "selected"));
			if (!cond$2361) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2343$17)); }

			return verdict;
		}

	}

	/*
    assert element "delayed_element" is visible "delayed element must appear after timeout".
	 */
	public static class DelayedElementMustAppearAfterTimeout implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "delayed element must appear after timeout";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$2478$25 = getWidget("element", "delayed_element", state);
			if (widget$2478$25 == null) {
				return Verdict.OK;
			}
			boolean cond$2504 = evaluateIsStatus(widget$2478$25, "visible");
			if (!cond$2504) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2478$25)); }

			return verdict;
		}

	}

}
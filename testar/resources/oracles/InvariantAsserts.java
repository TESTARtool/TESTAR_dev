
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class InvariantAsserts {

	/*
    assert for all menu
      it.children are enabled "menu children must be enabled".
	 */
	public static class MenuChildrenMustBeEnabled implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "menu children must be enabled";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			for (Widget $it: getWidgets("menu", state)) {
				Object property$23$11 = getProperty($it, "children");
				boolean cond$35 = evaluateAreStatus((java.util.List<Object>)property$23$11, "enabled");
				if (!cond$35) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
			}
			return verdict;
		}

	}

	/*
	 assert for all table_data
	   it.text matches "\\d+\\.\\d{2}$" when it.text contains "$" "Dollar values must have two decimals".
	 */
	public static class DollarValuesMustHaveTwoDecimals implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "Dollar values must have two decimals";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			for (Widget $it: getWidgets("table_data", state)) {

				Object property$67$7 = getProperty($it, "text");
				boolean cond$75 = evaluateContains(property$67$7, "$");
				if (cond$75) {

					Object property$29$7 = getProperty($it, "text");
					boolean cond$37 = evaluateMatches(property$29$7, "\\d+\\.\\d{2}$");
					if (!cond$37) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

				}
			}
			return verdict;
		}

	}

	/*
	 assert for all dropdown
	   it not is empty
	   "Dropdown options must not be empty".
	 */
	public static class DropdownOptionsMustNotBeEmpty implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "Dropdown options must not be empty";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			for (Widget $it: getWidgets("dropdown", state)) {

				boolean cond$30 = !(evaluateIsStatus($it, "empty"));
				if (!cond$30) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

			}
			return verdict;
		}

	}

	/*
	 assert for all dropdown
	   it.length not is equal to 1
	   "Dropdown options must not have a unique value".
	 */
	public static class DropdownOptionsMustNotHaveAUniqueValue implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "Dropdown options must not have a unique value";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			for (Widget $it: getWidgets("dropdown", state)) {

				Object property$27$9 = getProperty($it, "length");
				boolean cond$37 = !(evaluateIsEqualTo(property$27$9, 1));
				if (!cond$37) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

			}
			return verdict;
		}

	}

	/*
	 assert for all form 
	   it has nonempty title 
	   "Forms must have accessibility title".
	 */
	public static class FormsMustHaveAccessibilityTitle implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "Forms must have accessibility title";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			for (Widget $it: getWidgets("form", state)) {

				boolean cond$27 = evaluateHasAttribute($it, "title");
				if (!cond$27) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

			}
			return verdict;
		}

	}

	/*
	 assert for all image 
	   it has nonempty alttext 
	   "Images must have alternative text".
	 */
	public static class ImagesMustHaveAlternativeText implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "Images must have alternative text";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			for (Widget $it: getWidgets("image", state)) {

				boolean cond$28 = evaluateHasAttribute($it, "alttext");
				if (!cond$28) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

			}
			return verdict;
		}

	}

	/*
	 assert button "Submit" is enabled when checkbox "Accept Terms" 
	     is checked "Submit must be enabled when Accept Terms is checked".
	 */
	public static class SubmitMustBeEnabledWhenAcceptTermsIsChecked implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "Submit must be enabled when Accept Terms is checked";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			Widget widget$39$23 = getWidget("checkbox", "Accept Terms", state);
			if (widget$39$23 == null) {
				return Verdict.OK;
			}
			boolean cond$69 = evaluateIsStatus(widget$39$23, "checked");
			if (cond$69) {
				Widget widget$7$15 = getWidget("button", "Submit", state);
				if (widget$7$15 == null) {
					return Verdict.OK;
				}
				boolean cond$23 = evaluateIsStatus(widget$7$15, "enabled");
				if (!cond$23) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$7$15)); }

			}
			return verdict;
		}

	}

	/*
    assert for all menu_item, table_data, static_text, label
      it.fontsize matches "\\b(9|[1-9]\\d+?)px\\b" when it.fontsize contains "px"
      "Text size must be bigger than 9 pixels".
	 */
	public static  class TextSizeMustBeBiggerThan9Pixels implements Oracle {

		@Override
		public void initialize() { }

		@Override
		public String getMessage() {
			return "Text size must be bigger than 9 pixels";
		}

		@Override
		public Verdict getVerdict(State state) {
			Verdict verdict = Verdict.OK;
			for (Widget $it: getWidgets("menu_item", state)) {

				Object property$110$11 = getProperty($it, "fontsize");
				boolean cond$122 = evaluateContains(property$110$11, "px");
				if (cond$122) {

					Object property$60$11 = getProperty($it, "fontsize");
					boolean cond$72 = evaluateMatches(property$60$11, "\\b(9|[1-9]\\d+?)px\\b");
					if (!cond$72) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

				}
			}
			for (Widget $it: getWidgets("table_data", state)) {

				Object property$110$11 = getProperty($it, "fontsize");
				boolean cond$122 = evaluateContains(property$110$11, "px");
				if (cond$122) {

					Object property$60$11 = getProperty($it, "fontsize");
					boolean cond$72 = evaluateMatches(property$60$11, "\\b(9|[1-9]\\d+?)px\\b");
					if (!cond$72) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

				}
			}
			for (Widget $it: getWidgets("static_text", state)) {

				Object property$110$11 = getProperty($it, "fontsize");
				boolean cond$122 = evaluateContains(property$110$11, "px");
				if (cond$122) {

					Object property$60$11 = getProperty($it, "fontsize");
					boolean cond$72 = evaluateMatches(property$60$11, "\\b(9|[1-9]\\d+?)px\\b");
					if (!cond$72) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

				}
			}
			for (Widget $it: getWidgets("label", state)) {

				Object property$110$11 = getProperty($it, "fontsize");
				boolean cond$122 = evaluateContains(property$110$11, "px");
				if (cond$122) {

					Object property$60$11 = getProperty($it, "fontsize");
					boolean cond$72 = evaluateMatches(property$60$11, "\\b(9|[1-9]\\d+?)px\\b");
					if (!cond$72) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }

				}
			}
			return verdict;
		}

	}

}


package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class InvariantAsserts {

	public static class DollarValuesMustHaveTwoDecimals$112 implements Oracle {
		/*
      assert for all table_data
        it.text matches "\\d+\\.\\d{2}$" when it.text contains "$" 
        "Dollar values must have two decimals".
		 */

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

				Object widget$87$7 = getProperty($it, "text");
				boolean cond$95 = evaluateContains(widget$87$7, "$");
				if (cond$95) {

					Object widget$49$7 = getProperty($it, "text");
					boolean cond$57 = evaluateMatches(widget$49$7, "\\d+\\.\\d{2}$");
					if (!cond$57) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
				}
			}
			return verdict;
		}
	}

	public static class TextSizeMustBeBiggerThan9Pixels$296 implements Oracle {
		/*
      assert for all menu_item, table_data, static_text, label
        it.fontsize matches "\\b(9|[1-9]\\d+?)px\\b" when it.fontsize contains "px"
        "Text size must be bigger than 9 pixels".
		 */

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

				Object widget$267$11 = getProperty($it, "fontsize");
				boolean cond$279 = evaluateContains(widget$267$11, "px");
				if (cond$279) {

					Object widget$217$11 = getProperty($it, "fontsize");
					boolean cond$229 = evaluateMatches(widget$217$11, "\\b(9|[1-9]\\d+?)px\\b");
					if (!cond$229) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
				}
			}
			for (Widget $it: getWidgets("table_data", state)) {

				Object widget$267$11 = getProperty($it, "fontsize");
				boolean cond$279 = evaluateContains(widget$267$11, "px");
				if (cond$279) {

					Object widget$217$11 = getProperty($it, "fontsize");
					boolean cond$229 = evaluateMatches(widget$217$11, "\\b(9|[1-9]\\d+?)px\\b");
					if (!cond$229) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
				}
			}
			for (Widget $it: getWidgets("static_text", state)) {

				Object widget$267$11 = getProperty($it, "fontsize");
				boolean cond$279 = evaluateContains(widget$267$11, "px");
				if (cond$279) {

					Object widget$217$11 = getProperty($it, "fontsize");
					boolean cond$229 = evaluateMatches(widget$217$11, "\\b(9|[1-9]\\d+?)px\\b");
					if (!cond$229) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
				}
			}
			for (Widget $it: getWidgets("label", state)) {

				Object widget$267$11 = getProperty($it, "fontsize");
				boolean cond$279 = evaluateContains(widget$267$11, "px");
				if (cond$279) {

					Object widget$217$11 = getProperty($it, "fontsize");
					boolean cond$229 = evaluateMatches(widget$217$11, "\\b(9|[1-9]\\d+?)px\\b");
					if (!cond$229) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
				}
			}
			return verdict;
		}
	}

	public static class MenuChildrenMustBeEnabled$390 implements Oracle {
		/*
      assert for all menu
        it.children are enabled "menu children must be enabled".
		 */

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

				Object widget$366$11 = getProperty($it, "children");
				boolean cond$378 = evaluateAreStatus((java.util.List<Object>)widget$366$11, "enabled");
				if (!cond$378) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
			}
			return verdict;
		}
	}

	public static class SubmitMustBeEnabledWhenAcceptTermsIsChecked$80 implements Oracle {
		/*
		      assert button "Submit" is enabled when checkbox "Accept Terms" 
		          is checked "Submit must be enabled when Accept Terms is checked".
		 */

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

	public static class ImagesMustHaveAlternativeText$555 implements Oracle {
		/*
      assert for all image 
        it has nonempty alttext 
        "Images must have alternative text".
		 */

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

				boolean cond$530 = evaluateHasAttribute($it, "alttext");
				if (!cond$530) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
			}
			return verdict;
		}
	}

	public static class FormsMustHaveAccessibilityTitle$647 implements Oracle {
		/*
      assert for all form 
        it has nonempty title 
        "Forms must have accessibility title".
		 */

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

				boolean cond$624 = evaluateHasAttribute($it, "title");
				if (!cond$624) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
			}
			return verdict;
		}
	}

	public static class DropdownOptionsMustNotBeEmpty$737 implements Oracle {
		/*
      assert for all dropdown
        it not is empty
        "Dropdown options must not be empty".
		 */

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

				boolean cond$721 = !(evaluateIsStatus($it, "empty"));
				if (!cond$721) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
			}
			return verdict;
		}
	}

	public static class DropdownOptionsMustNotHaveAUniqueValue$838 implements Oracle {
		/*
      assert for all dropdown
        it.length not is equal to 1
        "Dropdown options must not have a unique value".
		 */

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

				Object widget$807$9 = getProperty($it, "length");
				boolean cond$817 = !(evaluateIsEqualTo(widget$807$9, 1));
				if (!cond$817) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
			}
			return verdict;
		}
	}

}
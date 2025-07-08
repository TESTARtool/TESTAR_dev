
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class assert_menu_children_are_enabled {

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

}
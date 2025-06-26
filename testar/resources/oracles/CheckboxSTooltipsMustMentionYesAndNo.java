
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all checkbox
     it.tooltip contains "yes" and contains "no"
     "checkbox's tooltips must mention yes and no".
*/
public class CheckboxSTooltipsMustMentionYesAndNo implements Oracle {

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
       
       Object property$28$10 = getProperty($it, "tooltip");
       boolean cond$39 = (evaluateContains(property$28$10, "yes") && evaluateContains(property$28$10, "no"));
       if (!cond$39) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
     
     }
     return verdict;
  }
 
}

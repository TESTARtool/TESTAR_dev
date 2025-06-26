
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all input_numeric
     it.tooltip contains "integer"
     "numeric tooltips must mention integer".
*/
public class NumericTooltipsMustMentionInteger implements Oracle {

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
       
       Object property$33$10 = getProperty($it, "tooltip");
       boolean cond$44 = evaluateContains(property$33$10, "integer");
       if (!cond$44) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
     
     }
     return verdict;
  }
 
}

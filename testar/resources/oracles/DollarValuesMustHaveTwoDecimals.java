
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


public class DollarValuesMustHaveTwoDecimals implements Oracle {

  @Override
  public void initialize() { }

  @Override
  public String getMessage() {
    return "dollar values must have two decimals";
  }

  @Override
  public Verdict getVerdict(State state) {
     Verdict verdict = Verdict.OK;
     for (Widget $it: getWidgets("table_data", state)) {
       
       boolean cond$32 = evaluateMatches($it, "\\$(?:\\d+\\.(?!\\d{2}\\b)\\d+|\\d+)(?!\\.)\\b");
       verdict = (cond$32 && verdict == Verdict.OK) ? Verdict.OK : new Verdict(Verdict.Severity.FAIL, getMessage());
     }
     return verdict;
  }
}

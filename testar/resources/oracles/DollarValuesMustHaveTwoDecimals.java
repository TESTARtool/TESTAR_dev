
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all table_data
   it.text matches "\\d+\\.\\d{2}$" when it.text contains "$" "Dollar values must have two decimals".
*/
public class DollarValuesMustHaveTwoDecimals implements Oracle {

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

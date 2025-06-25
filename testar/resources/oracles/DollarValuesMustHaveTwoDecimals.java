
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all table_data
   it.text not matches "\\d+\\.\\d{2}$" when it.text contains "$" "dollar values must have two decimals".
*/
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
       
       Object property$71$7 = getProperty($it, "text");
       boolean cond$79 = evaluateContains(property$71$7, "$");
       if (cond$79) {
         
         Object property$29$7 = getProperty($it, "text");
         boolean cond$37 = !(evaluateMatches(property$29$7, "\\d+\\.\\d{2}$"));
         if (cond$37) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
       
       }
     }
     return verdict;
  }
 
}

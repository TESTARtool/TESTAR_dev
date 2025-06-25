
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all dropdown
   it is empty
   "Dropdown options must not be empty".
*/
public class DropdownOptionsMustNotBeEmpty implements Oracle {

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
       
       boolean cond$30 = evaluateIsStatus($it, "empty");
       if (cond$30) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
     
     }
     return verdict;
  }
 
}

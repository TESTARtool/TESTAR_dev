
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all dropdown
   it.length not is equal to 1
   "Dropdown options must not have a unique value".
*/
public class DropdownOptionsMustNotHaveAUniqueValue implements Oracle {

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

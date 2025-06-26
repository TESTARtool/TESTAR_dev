
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all form 
   it has nonempty title 
   "Forms must have accessibility title".
*/
public class FormsMustHaveAccessibilityTitle implements Oracle {

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

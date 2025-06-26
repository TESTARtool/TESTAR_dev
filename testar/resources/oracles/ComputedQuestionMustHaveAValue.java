
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all input_text, input_numeric
     it not matches "^$" when it is readonly 
     "computed question must have a value".
*/
public class ComputedQuestionMustHaveAValue implements Oracle {

  @Override
  public void initialize() { }

  @Override
  public String getMessage() {
    return "computed question must have a value";
  }

  @Override
  public Verdict getVerdict(State state) {
     Verdict verdict = Verdict.OK;
     for (Widget $it: getWidgets("input_text", state)) {
       
       boolean cond$73 = evaluateIsStatus($it, "readonly");
       if (cond$73) {
         boolean cond$48 = !(evaluateMatches($it, "^$"));
         if (!cond$48) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
       }
     }
     for (Widget $it: getWidgets("input_numeric", state)) {
       
       boolean cond$73 = evaluateIsStatus($it, "readonly");
       if (cond$73) {
         boolean cond$48 = !(evaluateMatches($it, "^$"));
         if (!cond$48) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
       }
     }
     return verdict;
  }
 
}


import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all static_text
     it not matches "^[A-Z\\ !]+$" 
     "prompts must not use all caps".
*/
public class PromptsMustNotUseAllCaps implements Oracle {

  @Override
  public void initialize() { }

  @Override
  public String getMessage() {
    return "prompts must not use all caps";
  }

  @Override
  public Verdict getVerdict(State state) {
     Verdict verdict = Verdict.OK;
     for (Widget $it: getWidgets("static_text", state)) {
       
       boolean cond$34 = !(evaluateMatches($it, "^[A-Z\\ !]+$"));
       if (!cond$34) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
     
     }
     return verdict;
  }
 
}


import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert button "Submit" not is enabled when checkbox "Accept Terms" 
     is checked "Submit must be enabled when Accept Terms is checked".
*/
public class SubmitMustBeEnabledWhenAcceptTermsIsChecked implements Oracle {

  @Override
  public void initialize() { }

  @Override
  public String getMessage() {
    return "Submit must be enabled when Accept Terms is checked";
  }

  @Override
  public Verdict getVerdict(State state) {
     Verdict verdict = Verdict.OK;
     Widget widget$43$23 = getWidget("checkbox", "Accept Terms", state);
     if (widget$43$23 == null) {
       return Verdict.OK;
     }
     boolean cond$73 = evaluateIsStatus(widget$43$23, "checked");
     if (cond$73) {
       Widget widget$7$15 = getWidget("button", "Submit", state);
       if (widget$7$15 == null) {
         return Verdict.OK;
       }
       boolean cond$23 = !(evaluateIsStatus(widget$7$15, "enabled"));
       if (cond$23) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$7$15)); }
     
     }
     return verdict;
  }
 
}

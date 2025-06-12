
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


public class ButtonSubmitMustBeEnabled implements Oracle {

  @Override
  public void initialize() { }

  @Override
  public String getMessage() {
    return "button Submit must be enabled";
  }

  @Override
  public Verdict getVerdict(State state) {
     Verdict verdict = Verdict.OK;
     Widget widget$14 = getWidget("button", "Submit", state);
     if (widget$14 == null) {
       return new Verdict(Verdict.Severity.WARNING, "Could not find button: " + "Submit");
     }
     boolean cond$23 = evaluateIsStatus(widget$14, "enabled");
     verdict = (cond$23 && verdict == Verdict.OK) ? Verdict.OK : new Verdict(Verdict.Severity.FAIL, getMessage(), widget$14);
     return verdict;
  }
}

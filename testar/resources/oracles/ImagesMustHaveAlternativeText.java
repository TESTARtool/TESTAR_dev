
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all image it not has nonempty alttext "images must have alternative text".
*/
public class ImagesMustHaveAlternativeText implements Oracle {

  @Override
  public void initialize() { }

  @Override
  public String getMessage() {
    return "images must have alternative text";
  }

  @Override
  public Verdict getVerdict(State state) {
     Verdict verdict = Verdict.OK;
     for (Widget $it: getWidgets("image", state)) {
       
       boolean cond$24 = !(evaluateHasAttribute($it, "alttext"));
       if (cond$24) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
     
     }
     return verdict;
  }
 
}

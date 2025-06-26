
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all input_text
     it.placeholder is equal to it.tooltip
     "placeholder and tooltip must be equal".
*/
public class PlaceholderAndTooltipMustBeEqual implements Oracle {

  @Override
  public void initialize() { }

  @Override
  public String getMessage() {
    return "placeholder and tooltip must be equal";
  }

  @Override
  public Verdict getVerdict(State state) {
     Verdict verdict = Verdict.OK;
     for (Widget $it: getWidgets("input_text", state)) {
       
       Object property$30$14 = getProperty($it, "placeholder");
       boolean cond$45 = evaluateIsEqualTo(property$30$14, new java.util.function.Supplier<Object>() {
        public Object get() {
          
          Object property$57$10 = getProperty($it, "tooltip");
          return property$57$10; 
        }  
       }.get());
       if (!cond$45) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
     
     }
     return verdict;
  }
 
}

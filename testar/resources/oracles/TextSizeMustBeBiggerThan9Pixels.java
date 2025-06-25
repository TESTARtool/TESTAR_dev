
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;


/*
 assert for all menu_item, table_data, static_text, label
   it.size not matches "\\b(9|[1-9]\\d+?)px\\b" when it.size contains "px"
   "Text size must be bigger than 9 pixels".
*/
public class TextSizeMustBeBiggerThan9Pixels implements Oracle {

  @Override
  public void initialize() { }

  @Override
  public String getMessage() {
    return "Text size must be bigger than 9 pixels";
  }

  @Override
  public Verdict getVerdict(State state) {
     Verdict verdict = Verdict.OK;
     for (Widget $it: getWidgets("menu_item", state)) {
       
       Object property$110$7 = getProperty($it, "size");
       boolean cond$118 = evaluateContains(property$110$7, "px");
       if (cond$118) {
         
         Object property$60$7 = getProperty($it, "size");
         boolean cond$68 = !(evaluateMatches(property$60$7, "\\b(9|[1-9]\\d+?)px\\b"));
         if (cond$68) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
       
       }
     }
     for (Widget $it: getWidgets("table_data", state)) {
       
       Object property$110$7 = getProperty($it, "size");
       boolean cond$118 = evaluateContains(property$110$7, "px");
       if (cond$118) {
         
         Object property$60$7 = getProperty($it, "size");
         boolean cond$68 = !(evaluateMatches(property$60$7, "\\b(9|[1-9]\\d+?)px\\b"));
         if (cond$68) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
       
       }
     }
     for (Widget $it: getWidgets("static_text", state)) {
       
       Object property$110$7 = getProperty($it, "size");
       boolean cond$118 = evaluateContains(property$110$7, "px");
       if (cond$118) {
         
         Object property$60$7 = getProperty($it, "size");
         boolean cond$68 = !(evaluateMatches(property$60$7, "\\b(9|[1-9]\\d+?)px\\b"));
         if (cond$68) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
       
       }
     }
     for (Widget $it: getWidgets("label", state)) {
       
       Object property$110$7 = getProperty($it, "size");
       boolean cond$118 = evaluateContains(property$110$7, "px");
       if (cond$118) {
         
         Object property$60$7 = getProperty($it, "size");
         boolean cond$68 = !(evaluateMatches(property$60$7, "\\b(9|[1-9]\\d+?)px\\b"));
         if (cond$68) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
       
       }
     }
     return verdict;
  }
 
}

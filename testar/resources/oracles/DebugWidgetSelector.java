import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;
import org.testar.oracles.OracleWidgetsMapping;
import java.util.List;


public class DebugWidgetSelector implements Oracle
{
    
    @Override
    public void initialize() { }
    
    @Override
    public String getMessage()
    {
        return "debug functions of the widget selector";
    }
    
    @Override
    public Verdict getVerdict(State state)
    {
        Verdict verdict = Verdict.OK;
    
        String roleString = "static_text";
        String string = "Account: ";
        
        List<Widget> widgets = runWidgetSelector(state, roleString, string);
    
        System.out.println("======================================");
        System.out.println("Selector: " + roleString + " & '" +  string + "'");
        for(Widget widget : widgets)
        {
            System.out.println("Widget: " + widget.get(WdTags.WebTextContent, ""));
        }
        
        return verdict;
    }
}

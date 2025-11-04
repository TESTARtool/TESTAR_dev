import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.DslOracle;
import org.testar.oracles.OracleWidgetsMapping;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;


public class DebugWidgetSelector extends DslOracle
{
    // testingTESTARDSL.html

    @Override
    public String getMessage()
    {
        return "debug functions of the widget selector";
    }

    @Override
    public Verdict getVerdict(State state)
    {
        Verdict verdict = Verdict.OK;

        List<SimpleEntry<String, String>> selectors = List.of(
                new SimpleEntry<>("button", "submit"),
                new SimpleEntry<>("input_text", "name"),
                new SimpleEntry<>("input_text", "age"),
                new SimpleEntry<>("checkbox", "I accept the terms"),
                new SimpleEntry<>("radiogroup", "gender"),
                new SimpleEntry<>("radio", "other"),
                new SimpleEntry<>("dropdown", "Netherlands"),
                new SimpleEntry<>("dropdown", "Option C"),
                new SimpleEntry<>("static_text", "enter your details"),
                //new SimpleEntry<>("label", "Status:"),
                new SimpleEntry<>("static_text", "Status:"), 
                new SimpleEntry<>("image", "placeholder image"),
                new SimpleEntry<>("link", "Go to homepage"),
                //new SimpleEntry<>("panel", "Settings"),
                new SimpleEntry<>("panel", "panel_main"),
                new SimpleEntry<>("static_text", "Settings"),
                new SimpleEntry<>("input_text", "Child input"),
                new SimpleEntry<>("checkbox", "Receive updates"),
                //new SimpleEntry<>("label", "container"),
                new SimpleEntry<>("static_text", "container"), 
                new SimpleEntry<>("input_text", "Generic text input")
        );

        System.out.println("======================================");

        for(SimpleEntry<String, String> entry : selectors)
        {
            PrintSelectorResult(state, entry.getKey(), entry.getValue());
        }

        return verdict;
    }

    private void PrintSelectorResult(State state, String roleString, String searchString)
    {
        List<Widget> widgets = runWidgetSelector(state, roleString, searchString);

        if(widgets.isEmpty())
            System.out.println("NOT FOUND Selector: " + roleString + " & '" +  searchString + "' didn't find any widgets");
        else
        {
            System.out.println("Found! Selector: " + roleString + " & '" + searchString + "'");
            for (Widget widget : widgets) {
                System.out.println("Widget: " + widget.get(Tags.Desc, ""));
            }
        }
    }
}

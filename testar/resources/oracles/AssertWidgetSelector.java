
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;
import org.testar.oracles.OracleWidgetsMapping;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class AssertWidgetSelector implements Oracle {
    private final List<SimpleEntry<String, String>> allAssertions = new ArrayList<>();

    @Override
    public void initialize() { }

    @Override
    public String getMessage() {
        return "asserts that all expected widgets can be found by selectors";
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        StringBuilder failureLog = new StringBuilder();

        collectAssertions();

        for (SimpleEntry<String, String> entry : allAssertions) {
            List<Widget> widgets = runWidgetSelector(state, entry.getKey(), entry.getValue());

            if (widgets.isEmpty()) {
                verdict = Verdict.FAIL;
                failureLog.append("FAILED: Selector [")
                .append(entry.getKey()).append(" : '")
                .append(entry.getValue()).append("'] not found\n");
            }
        }

        if (verdict == Verdict.FAIL) {
            System.out.println("=== AssertWidgetSelector Failures ===");
            System.out.println(failureLog);
        }

        return verdict;
    }

    private void collectAssertions() {
        // Form Inputs
        allAssertions.addAll(List.of(
                new SimpleEntry<>("static_text", "Form Inputs"),
                new SimpleEntry<>("static_text", "Text:"),
                new SimpleEntry<>("static_text", "Password:"),
                new SimpleEntry<>("static_text", "Email:"),
                new SimpleEntry<>("static_text", "Number:"),
                new SimpleEntry<>("static_text", "Search:"),
                new SimpleEntry<>("static_text", "Tel:"),
                new SimpleEntry<>("static_text", "URL:"),
                new SimpleEntry<>("static_text", "Range:"),
                new SimpleEntry<>("static_text", "File:"),
                new SimpleEntry<>("static_text", "Date:"),
                new SimpleEntry<>("static_text", "Time:"),
                new SimpleEntry<>("static_text", "Color:"),
                new SimpleEntry<>("static_text", "Textarea:"),

                new SimpleEntry<>("label", "Text:"),
                new SimpleEntry<>("label", "Password:"),
                new SimpleEntry<>("label", "Email:"),
                new SimpleEntry<>("label", "Number:"),
                new SimpleEntry<>("label", "Search:"),
                new SimpleEntry<>("label", "Tel:"),
                new SimpleEntry<>("label", "URL:"),
                new SimpleEntry<>("label", "Range:"),
                new SimpleEntry<>("label", "File:"),
                new SimpleEntry<>("label", "Date:"),
                new SimpleEntry<>("label", "Time:"),
                new SimpleEntry<>("label", "Color:"),
                new SimpleEntry<>("label", "Textarea:"),

                new SimpleEntry<>("radio", "Male"),
                new SimpleEntry<>("radio", "Female"),

                new SimpleEntry<>("checkbox", "Accept Terms"),

                new SimpleEntry<>("button", "Submit"),
                new SimpleEntry<>("button", "Reset"),
                new SimpleEntry<>("button", "Click Me"),

                new SimpleEntry<>("input_text", "Enter text")
                ));

        // Buttons and Select
        allAssertions.addAll(List.of(
                new SimpleEntry<>("static_text", "Buttons and Select"),

                new SimpleEntry<>("button", "Standard Button"),

                new SimpleEntry<>("dropdown", "Option 1"),
                new SimpleEntry<>("dropdown", "Option 2"),
                new SimpleEntry<>("dropdown", "Option 3")
                ));

        // Text and Display
        allAssertions.addAll(List.of(
                new SimpleEntry<>("static_text", "Text and Display"),
                new SimpleEntry<>("static_text", "Label"),
                new SimpleEntry<>("static_text", "paragraph"),
                new SimpleEntry<>("static_text", "bold"),
                new SimpleEntry<>("static_text", "italic"),
                new SimpleEntry<>("static_text", "Span element"),
                new SimpleEntry<>("static_text", "Div container"),
                new SimpleEntry<>("static_text", "Header 1"),
                new SimpleEntry<>("static_text", "Header 2"),
                new SimpleEntry<>("static_text", "Preformatted text"),
                new SimpleEntry<>("static_text", "console.log"),
                new SimpleEntry<>("static_text", "Hello World"),
                new SimpleEntry<>("static_text", "quote")
                ));

        // Navigation
        allAssertions.addAll(List.of(
                new SimpleEntry<>("static_text", "Navigation"),

                new SimpleEntry<>("link", "Home"),
                new SimpleEntry<>("link", "About"),
                new SimpleEntry<>("link", "Contact"),

                new SimpleEntry<>("menu_item", "List Item 1"),
                new SimpleEntry<>("menu_item", "List Item 2"),
                new SimpleEntry<>("menu_item", "Ordered Item 1"),
                new SimpleEntry<>("menu_item", "Ordered Item 1")
                ));

        // Tables
        allAssertions.addAll(List.of(
                new SimpleEntry<>("static_text", "Tables"),

                new SimpleEntry<>("table_data", "Name"),
                new SimpleEntry<>("table_data", "Age"),
                new SimpleEntry<>("table_data", "Alice"),
                new SimpleEntry<>("table_data", "30"),
                new SimpleEntry<>("table_data", "Footer Info")
                ));

        // Semantic Containers
        allAssertions.addAll(List.of(
                new SimpleEntry<>("static_text", "Semantic Containers"),
                new SimpleEntry<>("static_text", "Section content"),
                new SimpleEntry<>("static_text", "Article content"),
                new SimpleEntry<>("static_text", "Sidebar content"),
                new SimpleEntry<>("static_text", "Main content"),
                new SimpleEntry<>("static_text", "Page Header"),
                new SimpleEntry<>("static_text", "Page Footer"),
                new SimpleEntry<>("static_text", "This is a figure caption"),

                new SimpleEntry<>("image", "Figure"),
                new SimpleEntry<>("image", "testar_logo.png"),

                new SimpleEntry<>("panel", "Section content"),
                new SimpleEntry<>("panel", "Article content"),
                new SimpleEntry<>("panel", "Sidebar content"),
                new SimpleEntry<>("panel", "Main content"),
                new SimpleEntry<>("panel", "Page Header"),
                new SimpleEntry<>("panel", "Page Footer")
                ));
    }
}


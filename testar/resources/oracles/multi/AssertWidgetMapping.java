
package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.DslOracle;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class AssertWidgetMapping {

    public static class AssertWidgetSelector extends DslOracle {

        private final List<SimpleEntry<String, String>> allAssertions = new ArrayList<>();

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

                    new SimpleEntry<>("input_text", "Text:"),
                    new SimpleEntry<>("input_text", "Password:"),
                    new SimpleEntry<>("input_text", "Email:"),
                    new SimpleEntry<>("input_text", "Search:"),
                    new SimpleEntry<>("input_text", "Tel:"),
                    new SimpleEntry<>("input_text", "URL:"),
                    new SimpleEntry<>("input_text", "Textarea:"),

                    new SimpleEntry<>("input_numeric", "Number:"),
                    new SimpleEntry<>("input_numeric", "Range:"),

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
                    new SimpleEntry<>("dropdown", "Option B")
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

                    new SimpleEntry<>("image", "testar_logo_image"),
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

    public static class AssertWidgetContains extends DslOracle {

        private final List<BooleanAssertion> containAssertions = new ArrayList<>();

        private void addAssertContains(Object widget, String text, boolean expected) {
            String description = String.format("evaluateContains(widget, \"%s\")", text);
            if(widget instanceof Widget) description = description.concat(" of widget " + ((Widget)widget).get(Tags.Desc, ""));
            else description = description.concat(" unknown object maybe null widget");
            BooleanAssertion expression = new BooleanAssertion(description, () -> evaluateContains(widget, text), expected);
            containAssertions.add(expression);
        }

        @Override
        public String getMessage() {
            return "asserts that contains method works correctly";
        }

        @Override
        public Verdict getVerdict(State state) {
            Verdict verdict = Verdict.OK;
            StringBuilder failureLog = new StringBuilder();

            Object widget = getWidget("label", "Textarea:", state);

            addAssertContains(widget, "T", true);
            addAssertContains(widget, "Textarea", true);
            addAssertContains(widget, "area:", true);
            addAssertContains(widget, ":", true);
            addAssertContains(widget, "abc", false);
            addAssertContains(widget, "Txtarea", false);

            for (BooleanAssertion assertion : containAssertions) {
                boolean actual = assertion.condition.get();
                if (actual != assertion.expected) {
                    verdict = Verdict.FAIL;
                    failureLog.append("Assertion failed: ")
                    .append(assertion.description)
                    .append(" | expected: ").append(assertion.expected)
                    .append(", actual: ").append(actual)
                    .append("\n");
                }
            }

            if (verdict == Verdict.FAIL) {
                System.out.println("=== AssertWidgetContains Failures ===");
                System.out.print(failureLog);
            }

            return verdict;
        }
    }

    public static class AssertWidgetStatus extends DslOracle {

        private final List<BooleanAssertion> statusAssertions = new ArrayList<>();

        private void addAssertIsStatus(Object widget, String label, String property, boolean expected) {
            String description = String.format("evaluateIsStatus(widget, \"%s\")", property);
            if(widget instanceof Widget) description = description.concat(" of widget " + ((Widget)widget).get(Tags.Desc, ""));
            else description = description.concat(" unknown object for " + label + " maybe null widget");
            BooleanAssertion expression = new BooleanAssertion(description, () -> evaluateIsStatus(widget, property), expected);
            statusAssertions.add(expression);
        }

        @Override
        public String getMessage() {
            return "asserts that is status method works correctly";
        }

        @Override
        public Verdict getVerdict(State state) {
            Verdict verdict = Verdict.OK;
            Set<String> failureMessages = new LinkedHashSet<>();

            List<WidgetStatusExpectation> expectations = List.of(
                    // Map.entry("label", Set.of("visible", "focused", "onscreen", "offscreen"))
                    new WidgetStatusExpectation("label", "Textarea:", Map.of(
                            "visible", true, "focused", false, "onscreen", true, "offscreen:", false
                            )),

                    // Map.entry("image", Set.of("visible", "focused", "onscreen", "offscreen"))
                    new WidgetStatusExpectation("image", "testar_logo.png", Map.of(
                            "visible", true, "focused", false, "onscreen", false, "offscreen", true
                            )),

                    // Map.entry("input_text", Set.of("visible", "enabled", "focused", "readonly", "empty", "filled", "onscreen", "offscreen"))
                    new WidgetStatusExpectation("input_text", "Hidden Input:", Map.of(
                            "visible", false, "enabled", true, "focused", false, "readonly", false,
                            "empty", false, "filled", true, "onscreen", true, "offscreen", false
                            )),
                    new WidgetStatusExpectation("input_text", "Disabled Input:", Map.of(
                            "visible", true, "enabled", false, "focused", false, "readonly", false,
                            "empty", false, "filled", true, "onscreen", true, "offscreen", false
                            )),
                    new WidgetStatusExpectation("input_text", "Password:", Map.of(
                            "visible", true, "enabled", true, "focused", false, "readonly", false,
                            "empty", true, "filled", false, "onscreen", true, "offscreen", false
                            )),

                    // Map.entry("button", Set.of("visible", "enabled", "focused", "clickable", "onscreen", "offscreen"))
                    new WidgetStatusExpectation("button", "Click Me", Map.of(
                            "visible", true, "enabled", true, "focused", false,
                            "clickable", true, "onscreen", true, "offscreen", false
                            )),
                    new WidgetStatusExpectation("button", "Disabled Button", Map.of(
                            "visible", true, "enabled", false, "focused", false,
                            "clickable", true, "onscreen", false, "offscreen", true
                            )),

                    // Map.entry("menu_item", Set.of("visible", "enabled", "focused", "clickable", "empty", "onscreen", "offscreen"))
                    new WidgetStatusExpectation("menu_item", "List Item 1", Map.of(
                            "visible", true, "enabled", true, "focused", false,
                            "clickable", true, "empty", false, "onscreen", false, "offscreen", true
                            )),
                    new WidgetStatusExpectation("menu_item", "List Item 2", Map.of(
                            "visible", true, "enabled", true, "focused", false,
                            "clickable", false, "empty", false, "onscreen", false, "offscreen", true
                            )),

                    // Map.entry("dropdown", Set.of("visible", "enabled", "focused", "empty", "selected", "clickable", "onscreen", "offscreen"))
                    new WidgetStatusExpectation("dropdown", "Select Test:", Map.of(
                            "visible", true, "enabled", true, "focused", false, "empty", false,
                            "selected", false, "clickable", true, "onscreen", false, "offscreen", true
                            )),
                    new WidgetStatusExpectation("dropdown", "Beta (Selected)", Map.of(
                            "visible", true, "enabled", true, "focused", false, "empty", false,
                            "selected", true, "clickable", true, "onscreen", false, "offscreen", true
                            )),
                    new WidgetStatusExpectation("dropdown", "Empty Select:", Map.of(
                            "visible", true, "enabled", true, "focused", false, "empty", true,
                            "selected", false, "clickable", true, "onscreen", false, "offscreen", true
                            )),

                    // Map.entry("checkbox", Set.of("visible", "enabled", "focused", "checked", "clickable", "onscreen", "offscreen"))
                    new WidgetStatusExpectation("checkbox", "Accept Terms", Map.of(
                            "visible", true, "enabled", true, "focused", false,
                            "checked", true, "clickable", true, "onscreen", true, "offscreen", false
                            )),
                    new WidgetStatusExpectation("checkbox", "Disagree", Map.of(
                            "visible", true, "enabled", true, "focused", false,
                            "checked", false, "clickable", true, "onscreen", true, "offscreen", false
                            )),

                    // Map.entry("radio", Set.of("visible", "enabled", "focused", "checked", "clickable", "onscreen", "offscreen"))
                    new WidgetStatusExpectation("radio", "Male", Map.of(
                            "visible", true, "enabled", true, "focused", false,
                            "checked", true, "clickable", true, "onscreen", true, "offscreen", false
                            )),
                    new WidgetStatusExpectation("radio", "Female", Map.of(
                            "visible", true, "enabled", true, "focused", false,
                            "checked", false, "clickable", true, "onscreen", true, "offscreen", false
                            )),

                    //Map.entry("panel", Set.of("visible", "focused", "empty", "onscreen", "offscreen"))
                    new WidgetStatusExpectation("panel", "contentPanelSection", Map.of(
                            "visible", true, "focused", false,
                            "empty", false, "onscreen", false, "offscreen", true
                            )),
                    new WidgetStatusExpectation("panel", "emptyPanelSection", Map.of(
                            "visible", true, "focused", false,
                            "empty", true, "onscreen", false, "offscreen", true
                            ))
                    );

            // 2. Iterate and prepare the assertIsStatus
            for (WidgetStatusExpectation expectation : expectations) {
                Object widget = getWidget(expectation.type, expectation.label, state);
                for (Map.Entry<String, Boolean> entry : expectation.statusChecks.entrySet()) {
                    addAssertIsStatus(widget, expectation.label, entry.getKey(), entry.getValue());
                }
            }

            for (BooleanAssertion assertion : statusAssertions) {
                boolean actual = assertion.condition.get();
                if (actual != assertion.expected) {
                    verdict = Verdict.FAIL;
                    String failure = "Assertion failed: " +
                            assertion.description +
                            " | expected: " + assertion.expected +
                            ", actual: " + actual;
                    failureMessages.add(failure);
                }
            }

            if (verdict == Verdict.FAIL) {
                System.out.println("=== AssertWidgetStatus Failures ===");
                failureMessages.forEach(System.out::println);
            }

            return verdict;
        }
    }

    private static class WidgetStatusExpectation {
        String type;
        String label;
        Map<String, Boolean> statusChecks;

        public WidgetStatusExpectation(String type, String label, Map<String, Boolean> statusChecks) {
            this.type = type;
            this.label = label;
            this.statusChecks = statusChecks;
        }
    }

    private static class BooleanAssertion {
        String description;
        Supplier<Boolean> condition;
        boolean expected;

        BooleanAssertion(String description, Supplier<Boolean> condition, boolean expected) {
            this.description = description;
            this.condition = condition;
            this.expected = expected;
        }
    }
}

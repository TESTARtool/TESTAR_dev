# CSS Selectors and Select Actions

## Contract

### Selector Capture

WebDriver captures a unique CSS selector for each supported element and exposes it as the string tag `WebCssSelector`. Generation runs synchronously in the packaged state script, using an escaped unique ID or a parent/child path with `:nth-of-type(...)` where needed. The selector is verified against the captured element before being returned.

The selector describes the captured document at that moment. A DOM change can invalidate an ID or positional path. Elements inside iframe documents or shadow roots have an empty selector because a document-level CSS query cannot cross those boundaries. Unavailable CSS escaping or a generation failure also produces an empty selector while allowing state capture to continue.

Adding `cssSelector` to `WebIgnoredAttributes` skips selector computation and exposes an empty `WebCssSelector`. For example:

```properties
WebIgnoredAttributes = xpath;cssSelector
```

Selectors are widget metadata, available through tag enumeration, serialized states, and captured model widget trees. State-model JSON export includes the tag when the corresponding widget properties are selected; semantic defaults already include `WebCssSelector`. Selected abstract identification attributes remain unchanged.

### Select Targets and Option Values

Native HTML `select` actions use the actual DOM ID, then the actual DOM name, then `WebCssSelector` as their locator. Generated `WebName` descriptions are distinguished from real name attributes. A captured Selenium element also supports selection when all locators are absent or ignored.

Execution prefers the captured connected element. If it is unavailable or detached, the action resolves its locator in the current document. A Selenium stale-element error triggers one locator-based retry; element-only actions report failure in that case. Name and CSS lookups must identify one element. The resolved element must be a native `select`.

CLI and LLM selection share option matching: an exact label/value match takes precedence, and a case-insensitive match is used only when it identifies one option. The matched option's value retains its original case. The LLM parser retains the selected action's identity tags and reports missing input or an unavailable target through its parse result.

The executable action exposes its value through `InputText` and passes targets and values as JavaScript arguments. It requires one option with that exact value, sets the selection, and dispatches bubbling `input` followed by `change`, using the element's own document event constructor. Missing or ambiguous options leave the current selection unchanged. A script error or skipped execution becomes an `ActionFailedException` with feedback.

## Acceptance Scenarios

### Selectors Are Available on First Capture

Verification: [`testar.state.test.cjs`](../../webdriver/test/js/testar.state.test.cjs), [`WdCssSelectorTest.java`](../../webdriver/test/org/testar/webdriver/state/WdCssSelectorTest.java).

Given a page contains a `select` with ID `country`\
When WebDriver captures the element\
Then its packed `cssSelector` and widget `WebCssSelector` are `#country`\
And generation requires no remote script download or separate initialization\
And the selector remains available after state serialization

### Nameless Siblings Have Distinct Selectors

Verification: [`testar.state.test.cjs`](../../webdriver/test/js/testar.state.test.cjs).

Given form `shipping` contains two nameless `select` elements with an `input` between them\
When WebDriver captures the second select\
Then its selector is `#shipping > select:nth-of-type(2)`\
And the selector resolves only that element

### Special and Duplicated IDs Are Handled

Verification: [`testar.state.test.cjs`](../../webdriver/test/js/testar.state.test.cjs).

Given an element ID contains a CSS-special character such as `form:country`\
When WebDriver generates its selector\
Then the ID is CSS-escaped

Given two select elements have the same ID\
When WebDriver generates the second element's selector\
Then it uses a unique structural path rather than the duplicated ID

### Ignored or Unavailable Selectors Preserve Capture

Verification: [`testar.state.test.cjs`](../../webdriver/test/js/testar.state.test.cjs), [`WdCssSelectorTest.java`](../../webdriver/test/org/testar/webdriver/state/WdCssSelectorTest.java).

Given `WebIgnoredAttributes` contains `cssSelector`\
When WebDriver captures an element\
Then selector generation is skipped\
And `WebCssSelector` is empty

Given an element is inside an iframe or shadow root, CSS escaping is unavailable, or generation fails\
When WebDriver obtains its selector\
Then the selector is empty\
And the remaining state-capture logic can continue

### Select Actions Use Real Locators

Verification: [`TestWebdriverSelectListSupport.java`](../../webdriver/test/org/testar/webdriver/action/TestWebdriverSelectListSupport.java), [`WdCssSelectorTest.java`](../../webdriver/test/org/testar/webdriver/state/WdCssSelectorTest.java).

Given a select has a DOM ID, a DOM name, and a CSS selector\
When TESTAR creates a select action\
Then it prefers the ID, otherwise the name, otherwise the CSS selector

Given a nameless select has generated `WebName` text `Saab` and CSS selector `#shipping > select`\
When TESTAR creates a select action\
Then it uses the CSS selector rather than treating `Saab` as a DOM name

### Model Export Preserves Selected Selector Metadata

Verification: [`WdCssSelectorTest.java`](../../webdriver/test/org/testar/webdriver/state/WdCssSelectorTest.java), [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given a captured widget has `WebCssSelector = #cars`\
When its widget tree is stored in the state model\
Then the stored widget retains that tag

Given a prepared model widget tree contains a CSS selector\
When the user exports Concrete JSON with widget trees and semantic property defaults\
Then the widget's properties include `WebCssSelector`\
When the user clears selected widget properties\
Then the exported widget's optional properties omit the selector

### CLI and LLM Select Nameless Fields

Verification: [`TestWebdriverSelectActionResolution.java`](../../webdriver/test/org/testar/webdriver/action/TestWebdriverSelectActionResolution.java), [`TestLlmParseActionResponse.java`](../../llm/test/org/testar/llm/action/TestLlmParseActionResponse.java).

Given a derived select action has only a CSS locator and listed option values\
When the CLI resolver or LLM parser supplies a listed selection\
Then it creates a CSS-targeted select action using the matching option value\
And preserves the selected action's abstract and concrete identity tags

### Option Case Is Preserved

Verification: [`TestWebdriverSelectListSupport.java`](../../webdriver/test/org/testar/webdriver/action/TestWebdriverSelectListSupport.java), [`TestLlmParseActionResponse.java`](../../llm/test/org/testar/llm/action/TestLlmParseActionResponse.java), [`select-list.test.cjs`](../../webdriver/test/js/select-list.test.cjs).

Given a select contains label `Saab` with value `saab` and label/value `TESTAR`\
When the caller requests either label\
Then the action uses `saab` or `TESTAR`, respectively

Given two options have labels `Saab` and `saab` with different values\
When the caller requests an exact label\
Then it maps to that option's value\
When the caller instead requests `SAAB`\
Then the ambiguous case-insensitive match leaves the input unchanged

### Captured Elements Support Locator-Free Selection

Verification: [`TestWebdriverSelectListSupport.java`](../../webdriver/test/org/testar/webdriver/action/TestWebdriverSelectListSupport.java), [`select-list.test.cjs`](../../webdriver/test/js/select-list.test.cjs).

Given a select has a captured connected Selenium element\
And its DOM ID, name, and CSS selector are empty\
When TESTAR selects a listed value\
Then it interacts with the captured element

Given a captured select shares its name with another field\
When TESTAR selects a listed value\
Then only the captured field is changed

### Selection Notifies the Application Safely

Verification: [`select-list.test.cjs`](../../webdriver/test/js/select-list.test.cjs), [`WdSelectListActionTest.java`](../../webdriver/test/org/testar/webdriver/action/WdSelectListActionTest.java).

Given a select target or option value contains quotes or other JavaScript-sensitive text\
When the action executes\
Then that text is passed as data\
And the field receives the exact option value\
And bubbling `input` is dispatched before bubbling `change`

### Replaced Select Fields Use Their Locator

Verification: [`select-list.test.cjs`](../../webdriver/test/js/select-list.test.cjs), [`WdSelectListActionTest.java`](../../webdriver/test/org/testar/webdriver/action/WdSelectListActionTest.java).

Given a captured select has been replaced and its CSS locator identifies one current select\
When TESTAR executes the selection\
Then it selects the requested value in the replacement field\
And a Selenium stale-element error is retried once using the locator

Given a captured select has become stale and the action has only an element target\
When TESTAR executes the selection\
Then it reports an action failure

### Invalid Selection Produces Feedback

Verification: [`select-list.test.cjs`](../../webdriver/test/js/select-list.test.cjs), [`WdSelectListActionTest.java`](../../webdriver/test/org/testar/webdriver/action/WdSelectListActionTest.java), [`TestLlmParseActionResponse.java`](../../llm/test/org/testar/llm/action/TestLlmParseActionResponse.java).

Given a selection target is missing, ambiguous, or not a native select\
Or its option value is missing or ambiguous\
When the action executes\
Then it reports an action failure\
And it leaves the current selection unchanged

Given an LLM selects a native select but omits its input\
When TESTAR parses that response\
Then it reports `SL_MISSING_INPUT`

## Implementation

- Capture and tags: [`testar.state.js`](../../webdriver/resources/web-extension/js/testar.state.js), [`WdElement.java`](../../webdriver/src/org/testar/webdriver/state/WdElement.java), [`WdState.java`](../../webdriver/src/org/testar/webdriver/state/WdState.java), [`WdTags.java`](../../webdriver/src/org/testar/webdriver/tag/WdTags.java).
- Target and option resolution: [`WebdriverSelectListSupport.java`](../../webdriver/src/org/testar/webdriver/action/WebdriverSelectListSupport.java), [`LlmParseActionResponse.java`](../../llm/src/org/testar/llm/action/LlmParseActionResponse.java).
- Execution: [`WdSelectListAction.java`](../../webdriver/src/org/testar/webdriver/action/WdSelectListAction.java), [`select-list.js`](../../webdriver/resources/select-list.js).

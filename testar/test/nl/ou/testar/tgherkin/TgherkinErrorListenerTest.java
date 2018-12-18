/**
 *
 */
package nl.ou.testar.tgherkin;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import nl.ou.testar.tgherkin.gen.TgherkinParser;

/**
 * Test TgherkinErrorListener class.
 * This JUnit test verifies whether a text is recognized as a valid Tgherkin document.
 *
 */
public class TgherkinErrorListenerTest {

  // Map with text in Tgherkin grammar and expected result
  private Map<String, Boolean> testMap = new HashMap<String, Boolean>();

  /**
   * Set up test.
   * @throws Exception if a problem occurs
   */
  @Before
  public void setUp() throws Exception {
    setUpTestCase1();
    setUpTestCase2();
    setUpTestCase3();
    setUpTestCase4();
    setUpTestCase5();
  }

  private void setUpTestCase1() {
    // test case 1: valid
    testMap.put("Feature: Compute with Windows calculator. \r\n" +
        "\r\n" +
        "  Selection: click()\r\n" +
        "\r\n" +
        "  Scenario: Add two numbers\r\n" +
        "    Step: Step 1 \r\n" +
        "    When  $Title=\"Een\" click()\r\n" +
        "    Step: Step 2 \r\n" +
        "    When  $Title=\"Een\" or $Title=\"Twee\" or $Title=\"Drie\" click()\r\n"
        , true);
  }

  private void setUpTestCase2() {
    // test case 2: leftClick gesture is not defined in Tgherkin
    testMap.put("Feature: Compute with Windows calculator. \r\n" +
        "\r\n" +
        "  Selection: leftClick()\r\n" +
        "\r\n" +
        "  Scenario: Add two numbers\r\n" +
        "    Step: Step 1 \r\n" +
        "    When  $Title=\"Een\" click()\r\n" +
        "    Step: Step 2 \r\n" +
        "    When  $Title=\"Een\" or $Title=\"Twee\" or $Title=\"Drie\" click()\r\n"
        , false);
  }

  private void setUpTestCase3() {
    // test case 3: $VARABLE_X is an unknown variable
    testMap.put("Feature: Compute with Windows calculator. \r\n" +
        "\r\n" +
        "  Selection: click()\r\n" +
        "\r\n" +
        "  Scenario: Add two numbers\r\n" +
        "    Step: Step 1 \r\n" +
        "    When  $VARABLE_X =\"Een\" click()\r\n" +
        "    Step: Step 2 \r\n" +
        "    When  $Title=\"Een\" or $Title=\"Twee\" or $Title=\"Drie\" click()\r\n"
        , false);
  }

  private void setUpTestCase4() {
    // test case 4: feature is missing
    testMap.put(
        "  Scenario: Add two numbers\r\n" +
        "    Step: Step 1 \r\n" +
        "    When  $Title=\"Een\" click()\r\n" +
        "    Step: Step 2 \r\n" +
        "    When  $Title=\"Een\" or $Title=\"Twee\" or $Title=\"Drie\" click()\r\n"
        , false);
  }

  private void setUpTestCase5() {
    // test case 5: valid
    testMap.put("Feature: Uitvoeren berekeningen met windows calculator. \r\n" + "\r\n" +
        "  Background: Enter 5 en clear\r\n" +
        "    Step: Selecteer 5\r\n" +
        "    When  $Title=\"Vijf\" click()\r\n" +
        "    Step: Selecteer Clear\r\n" +
        "    When  $Title=\"Wissen\" click()\r\n" +   "    \r\n" +
        "  Scenario: Optellen twee getallen\r\n" +
        "    Step: Selecteer 2\r\n" +
        "    When  $Title=\"Twee\" click()\r\n" +
        "    Then  $Title=\"Weergave is 2\"\r\n" +
        "    Step: Selecteer +\r\n" +
        "    When  $Title=\"Plus\" click()\r\n" +
        "    Then  $Title=\"Expressie is 2 + \"\r\n" +
        "    Step: Selecteer 3\r\n" +
        "    When  $Title=\"Drie\" click()\r\n" +
        "    Then  $Title=\"Weergave is 3\"\r\n" +
        "    Step: Selecteer = \r\n" +
        "    When  $Title=\"Is gelijk aan\" click()\r\n" +
        "    Then  $Title=\"Weergave is 5\"\r\n" +   "\r\n" +
        "  Scenario Outline: Optellen twee getallen\r\n" +
        "    Step: Selecteer getal 1\r\n" +
        "    When  $Title=<getal1> click()\r\n" +
        "    Step: Selecteer +\r\n" +
        "    When  $Title=\"Plus\" click()\r\n" +
        "    Step: Selecteer getal 2\r\n" +
        "    When  $Title=<getal2> click()\r\n" +
        "    Step: Selecteer = \r\n" +
        "    When  $Title=\"Is gelijk aan\" click()\r\n" +
        "  Examples: Examples titel\r\n" +
        "    | getal1 | getal2 |\r\n" +
        "    |  Negen  |  Vier |\r\n" +
        "    |  Zeven  |  Zes  |  \r\n" +
        "  \r\n" +
        "  Scenario: Optellen twee getallen\r\n" +
        "    Step: Selecteer 8\r\n" +
        "    When  $Title=\"Acht\" click()\r\n" +
        "    Then  $Title=\"Weergave is 8\"\r\n" +
        "    Step: Selecteer +\r\n" +
        "    When  $Title=\"Plus\" click()\r\n" +
        "    Then  $Title=\"Expressie is 8 + \"\r\n" +
        "    Step: Selecteer 1\r\n" +
        "    When  $Title=\"Een\" click()\r\n" +
        "    Then  $Title=\"Weergave is 1\"\r\n" +
        "    Step: Selecteer = \r\n" +
        "    When  $Title=\"Is gelijk aan\" click()\r\n" +
        "    Then  $Title=\"Weergave is 9\"\r\n" + "\r\n" +  "\r\n"
        , true);
  }

  /**
   * Execute test.
   */
  @Test
  public void test() {
    Iterator<Entry<String,Boolean>> iterator = testMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Entry<String,Boolean> entry = iterator.next();
      String expression = entry.getKey();
      Boolean expectedResult = entry.getValue();
      TgherkinParser parser = Utils.getTgherkinParser(expression);
        TgherkinErrorListener errorListener = new TgherkinErrorListener();
      parser.addErrorListener(errorListener);
      parser.document(); // root of grammar
      Boolean result = errorListener.getErrorList().size() == 0;
      assertEquals(expectedResult, result);
    }

  }

}

package nl.ou.testar.tgherkin.functions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Singleton class responsible for the handling of regular expressions.
 * Patterns are only compiled once.
 * This class supports the Tgherkin matches function.
 *
 */
public class Matches {

  private static Matches matches = new Matches();
  private Map<String,Pattern> matchesMap = new HashMap<String,Pattern>();

  // private Constructor prevents instantiation by other classes.
  private Matches() {
  }

  /**
   * Retrieve singleton instance.
   * @return singleton instance
   */
  public static Matches getInstance( ) {
    return matches;
  }

  /**
   * Determine whether a string matches the regex pattern.
   * @param string to be evaluated string
   * @param regex regular expression
   * @return true if a match is found, otherwise false
   */
  public boolean isMatch(String string, String regex) {
    Pattern pattern;
    if (matchesMap.containsKey(regex)) {
      pattern = matchesMap.get(regex);

    } else {
      pattern = Pattern.compile(regex);
      matchesMap.put(regex, pattern);
    }
        return pattern.matcher(string).matches();
  }

}

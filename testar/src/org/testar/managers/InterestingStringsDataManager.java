package org.testar.managers;
/**
 * This class implements a DataManager that generates text output for widgets,
 * based on interesting strings (prefixes, suffixes, full strings, fragments) that
 * would have typically been captured by SUT instrumentation.
 */

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testar.managers.DataManager;

public class InterestingStringsDataManager extends DataManager {

    final String TYPE_FULLSTRING = "FULLSTRING";
    final String TYPE_PREFIX = "PREFIX";
    final String TYPE_SUFFIX = "SUFFIX";
    final String TYPE_FRAGMENT = "FRAGMENT";
    final String TYPE_FRAGMENT_OR_PREFIX = "FRAGMENT_OR_PREFIX";
    final String TYPE_FRAGMENT_OR_SUFFIX = "FRAGMENT_OR_SUFFIX";
    final String TYPE_ANY = "ANY";
    final String KEY_VALUE = "value";
    final String KEY_TYPE = "type";
    final int MIN_INPUT_STRINGS = 2;
    final int MAX_RANDOM_FRAGMENT_LENGTH = 5;

    float fullStringRate = 0.2f;
    float maxInputStrings = 5;
    float typeMatchRate = 1.0f;
    List<Map<String,String>> data = new Vector<Map<String,String>>();
    Map<String,List<String>> typeCache = new HashMap<String,List<String>>();
    private static final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

    Random rnd=new Random();

    public InterestingStringsDataManager (float fullStringRate, float maxInputStrings, float typeMatchRate) {
        super();
        assert(maxInputStrings >= maxInputStrings);
        this.maxInputStrings = maxInputStrings;
        assert(fullStringRate >= 0.0 && fullStringRate <= 1.0);
        this.fullStringRate = fullStringRate;
        assert(typeMatchRate >= 0.0 && typeMatchRate <= 1.0);
        this.typeMatchRate = typeMatchRate;
    }

        /**
      * Load input data from interesting string data collected by instrumentation
      */
      public void loadInput(Set<Map<String,String>> data) {

        // Add all elements with ASCII values (typing non-ASCII values into a
        // text field using WebDriver won't work).
        for ( Map<String,String> interestingString: data ) {
             if ( isAscii(interestingString.getOrDefault(this.KEY_VALUE, "¡") ) )  {
                  this.data.add(interestingString);
             }
        }

        // Reset the type cache
        this.typeCache = new HashMap<String,List<String>>();
   }

   private boolean isAscii(String input) {
        return asciiEncoder.canEncode(input);
   }

   /**
    * Loads some dummy data for testing purposes
   */
   public void loadTestInputs() {
        List<Map<String,String>> testdata=new Vector<Map<String,String>>();
        this.typeCache = new HashMap<String,List<String>>();

        Map<String, String> prefixMap1  = new HashMap<String, String>();
        prefixMap1.put(this.KEY_VALUE, "First");
        prefixMap1.put(this.KEY_TYPE, this.TYPE_PREFIX);
        testdata.add(prefixMap1);

        Map<String, String> prefixMap2  = new HashMap<String, String>();
        prefixMap2.put(this.KEY_VALUE, "Eerste");
        prefixMap2.put(this.KEY_TYPE, this.TYPE_PREFIX);
        testdata.add(prefixMap2);

        Map<String, String> fragmentMap1  = new HashMap<String, String>();
        fragmentMap1.put(this.KEY_VALUE, "Middle");
        fragmentMap1.put(this.KEY_TYPE, this.TYPE_FRAGMENT);
        testdata.add(fragmentMap1);

        Map<String, String> fragmentMap2  = new HashMap<String, String>();
        fragmentMap2.put(this.KEY_VALUE, "Midden");
        fragmentMap2.put(this.KEY_TYPE, this.TYPE_FRAGMENT);
        testdata.add(fragmentMap2);

        Map<String, String> suffixMap1  = new HashMap<String, String>();
        suffixMap1.put(this.KEY_VALUE, "Last");
        suffixMap1.put(this.KEY_TYPE, this.TYPE_SUFFIX);
        testdata.add(suffixMap1);

        Map<String, String> suffixMap2  = new HashMap<String, String>();
        suffixMap2.put(this.KEY_VALUE, "Laatste");
        suffixMap2.put(this.KEY_TYPE, this.TYPE_SUFFIX);
        testdata.add(suffixMap2);

        Map<String, String> fullMap1  = new HashMap<String, String>();
        fullMap1.put(this.KEY_VALUE, "Complete");
        fullMap1.put(this.KEY_TYPE, this.TYPE_FULLSTRING);
        testdata.add(fullMap1);

        Map<String, String> fullMap2  = new HashMap<String, String>();
        fullMap2.put(this.KEY_VALUE, "Volledig");
        fullMap2.put(this.KEY_TYPE, this.TYPE_FULLSTRING);
        testdata.add(fullMap2);

        this.data=testdata;
   }

     /**
      * Generates one random string value, based on the available interesting strings
      * and the initialization parameters.
      */
      public String generateTextInput() {
        Vector<String> result = new Vector<String>();
        if ( this.rnd.nextFloat() < fullStringRate ) {
                  return getSubstringPreferredType(this.TYPE_FULLSTRING);
        }
        int numberStrings = getRandomInputLength();
        for (int i=0; i < numberStrings; i++) {
             if ( i == 0 ) {
                  result.add(getSubstringPreferredType(this.TYPE_FRAGMENT_OR_PREFIX));
             }
             else if ( i == numberStrings - 1 ) {
                  result.add(getSubstringPreferredType(this.TYPE_FRAGMENT_OR_SUFFIX));
             }
             else {
                  result.add(getSubstringPreferredType(this.TYPE_FRAGMENT));
             }
        }
        LogManager.getLogger().info("Result random is: " + concatenateList(result));
        return concatenateList(result);
   }

     /**
      *  Generates a substring.
      *
      *  The substring has a preferred type (TYPE_FULLSTRING, TYPE_FRAGMENT, TYPE_PREFIX, TYPE_SUFFIX,
      *   TYPE_FRAGMENT_OR_PREFIX, TYPE_FRAGMENT_OR_SUFFIX, TYPE_ANY), with the following special cases:
      *  1. If no substring of the preferred type is available, a substring of another type
      *     is returned.
      *  2. If no substring of any type is available, a random string value will be returned.
      *  3. If typeMatchRate is set to less than 1.0, the method will ignore the preferred type
      *     and return a value of any type with probability 1.0 - typeMatchRate, so a string value of
      *     another type may be returned.
      */
      protected String getSubstringPreferredType(String preferredType) {

        if ( (! preferredType.equals(this.TYPE_ANY) ) &&
              typeMatchRate < 1.0f &&
              this.rnd.nextFloat() < ( 1.0f - typeMatchRate ) ){
             return getSubstringPreferredType(this.TYPE_ANY);
        }

        if ( ! this.typeCache.containsKey(preferredType) ) {
             fillCacheForType(preferredType);
        }

        List<String> stringsOfType = this.typeCache.get(preferredType);

        if ( stringsOfType.size() == 0 && ! this.TYPE_ANY.equals(preferredType)) {
             // If we don't have any strings, first fall back to strings of another type
             return getSubstringPreferredType(this.TYPE_ANY);
        }
        else if ( stringsOfType.size() == 0 ) {
             // If there are also no strings of another type, fall back to a random substring
             return getRandomSubstring();
        }
        else {
             return stringsOfType.get(rnd.nextInt(stringsOfType.size()));
        }
   }

   protected void fillCacheForType(String type) {
    List<String> result = new Vector<String>();
    for ( Map<String,String> element : this.data ) {
         if ( element.get("type").equals(this.TYPE_PREFIX ) &&
              (    type.equals(this.TYPE_ANY) ||
                   type.equals(this.TYPE_PREFIX) ||
                   type.equals(this.TYPE_FRAGMENT_OR_PREFIX) ) ) {
              result.add(element.get("value"));
         }
         else if ( element.get("type").equals(this.TYPE_FRAGMENT ) &&
              (    type.equals(this.TYPE_ANY) ||
                   type.equals(this.TYPE_FRAGMENT) ||
                   type.equals(this.TYPE_FRAGMENT_OR_PREFIX) ||
                   type.equals(this.TYPE_FRAGMENT_OR_SUFFIX) ) ) {
              result.add(element.get("value"));
         }
         else if ( element.get("type").equals(this.TYPE_SUFFIX ) &&
              (    type.equals(this.TYPE_ANY) ||
                   type.equals(this.TYPE_SUFFIX) ||
                   type.equals(this.TYPE_FRAGMENT_OR_SUFFIX) ) ) {
              result.add(element.get("value"));
         }
         else if ( element.get("type").equals(this.TYPE_FULLSTRING ) &&
         (    type.equals(this.TYPE_ANY) ||
              type.equals(this.TYPE_FULLSTRING) ) ) {
              result.add(element.get("value"));
         }
    }
    this.typeCache.put(type,result);
  }

    protected String getRandomSubstring() {
        int textLength = rnd.nextInt(MAX_RANDOM_FRAGMENT_LENGTH) + 1;
        StringBuffer sb = new StringBuffer(textLength);
        for (int i=0; i<textLength; i++) {
            sb.append((char)('a' + rnd.nextInt(LETTER_COUNT)));
        }
        return sb.toString();
    }

    /**
      *  Generates a random value for the number of strings to use, based on the
      *  minimum and maximum length.
      */
      public int getRandomInputLength() {
        return (int)( this.MIN_INPUT_STRINGS +
             Math.round( this.rnd.nextFloat() * ( this.maxInputStrings - this.MIN_INPUT_STRINGS ))
        );
   }

   /**
    * Concatenates the elements in a list of strings.
    */
   public String concatenateList(List<String> list) {
        String result = "";
        for ( String element : list) {
             result += element;
        }
        return result;
   }

}

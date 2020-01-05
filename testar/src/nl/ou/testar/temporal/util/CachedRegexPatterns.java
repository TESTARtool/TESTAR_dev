package nl.ou.testar.temporal.util;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class CachedRegexPatterns {
    private static Map<String, Pattern> cachedRegExPatterns = new HashMap<>();


    public static boolean add(String regexString ) {

        if (!contains(regexString)) {
            boolean success =true;
            Pattern p;
            try{
               p = Pattern.compile(regexString);
               cachedRegExPatterns.put(regexString, p);
            }
            catch (PatternSyntaxException e) {// case of illegal pattern
                success=false;
            }
            return success;

        }else
        {return true;}
    }

    public static boolean contains(String regexString) {
        return cachedRegExPatterns.containsKey(regexString);
    }

    public static Pattern addAndGet(String regexString) {
        if (!contains(regexString)){
            add(regexString);
        }
        return cachedRegExPatterns.get(regexString);
    }
}

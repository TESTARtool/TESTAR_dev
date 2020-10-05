package nl.ou.testar.temporal.util;
/*
@startuml

class CachedRegexPatterns {
    - Map<String, Pattern> cachedRegExPatterns;
    -{static} Map<String, Pattern> cachedRegExPatterns = new HashMap<>();
    -{static} boolean add(String regexString ) {}
    -{static} boolean contains(String regexString) {    }
    +{static} Pattern addAndGet(String regexString) {}
}
@enduml
*/

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class CachedRegexPatterns {
    private static Map<String, Pattern> cachedRegExPatterns = new HashMap<>();

    private static boolean add(String regexString ) {
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

    private static boolean contains(String regexString) {
        return cachedRegExPatterns.containsKey(regexString);
    }

    /**
     * Converts a string to a Pattern and adds the Pattern to the cache if not available in the cache
     * @param regexString regular expression string
     * @return Pattern derived from the cache
     */
    public static Pattern addAndGet(String regexString) {
        if (!contains(regexString)){
            add(regexString);
        }
        return cachedRegExPatterns.get(regexString);
    }
}

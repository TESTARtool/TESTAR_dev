package nl.ou.testar.temporal.util;
/*
@startuml

class CachedRegexPatterns {
    private static Map<String, Pattern> cachedRegExPatterns = new HashMap<>();
    private static int cacheHits=0;
    private static int cacheMisses=0;
--
    public static boolean add(String regexString ) {}
    public static boolean contains(String regexString) {    }
    public static Pattern addAndGet(String regexString) {}
}
@enduml
*/

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class CachedRegexPatterns {
    private static Map<String, Pattern> cachedRegExPatterns = new HashMap<>();
    private static int cacheHits=0;
    private static int cacheMisses=0;


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
            cacheMisses++;
            add(regexString);
        }
        else{
            cacheHits++;
        }
        return cachedRegExPatterns.get(regexString);
    }
}

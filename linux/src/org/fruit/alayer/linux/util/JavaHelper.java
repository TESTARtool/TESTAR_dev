package org.fruit.alayer.linux.util;


/**
 * Some Java utility functions.
 */
public class JavaHelper {


    /**
     * Tries to parse a String to an int.
     * @param value The string to parse as an int.
     * @return True if the string can be parsed as an int; False otherwise.
     */
    public static boolean tryParseInt(String value) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /**
     * Tries to parse a String to an int.
     * @param value The string to parse as an int.
     * @return True if the string can be parsed as an int; False otherwise.
     */
    public static boolean tryParseDouble(String value) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /***
     * Checks to see if a string is null or empty.
     * @param s string to check.
     * @return true if null or empty; otherwise false.
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }


    /***
     * Checks if a string is null or empty or contains only whitespace.
     * @param s string to check.
     * @return true if null or emptry or only whitespace; otherwise false.
     */
    public static boolean isNullOrWhitespace(String s) {
        if (isNullOrEmpty(s))
            return true;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }


}
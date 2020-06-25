package nl.ou.testar.temporal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFinder {

   public static int findClosingParenthesis(String data, int openPos) {

        char[] text=data.toCharArray();

        int closePos = openPos;
        int unmatchedCounter = 1;
        while (unmatchedCounter > 0) {
            char c = text[++closePos];
            if (c == '(') {
                unmatchedCounter++;
            } else if (c == ')') {
                unmatchedCounter--;
            }
        }
        return closePos;
    }
    public static int findOpeningParenthesis(String data, int closePos) {
        char[] text=data.toCharArray();
        int openPos = closePos;
        int unmatchedCounter = 1;
        while (unmatchedCounter > 0) {
            char c = text[--openPos];
            if (c == '(') {
                unmatchedCounter--;
            } else if (c == ')') {
                unmatchedCounter++;
            }
        }
        return openPos;
    }

    /**
     * Find any matching substring and surround all occurrences with 'replacestring' + 'substring' + 'closing'
     *
     * @param data          string that might contain substrings to search
     * @param toSearch      substring to search for
     * @param replaceStr    prefix of the embedding
     * @param closing       suffix of the embedding
     *
     *
     * inspired by  https://thispointer.com/find-and-replace-all-occurrences-of-a-sub-string-in-c \n
     * customized  for TESTAR
     */

    public static String findClosingAndInsert(String data, String toSearch, String replaceStr, String closing) {


        // Get the first occurrence
        int pos = data.indexOf(toSearch);
        while (pos != -1) { // Repeat till end is reached
            // Replace this occurrence of Sub String
            //find matching bracket
            int bracketpos = findClosingParenthesis(data, pos + toSearch.length() - 1); //assume last char is the "("
            String orginalblock = data.substring(pos + toSearch.length() - 1, bracketpos);
            String prepend = data.substring(0, pos);
            String append = data.substring(bracketpos);
            data = prepend + toSearch + replaceStr + orginalblock + closing + append;
            // Get the next occurrence from the current position
            //pos = data.indexOf(toSearch, pos + toSearch.length() + replaceStr.length() + orginalblock.length() + closing.length());
            pos = data.indexOf(toSearch, pos + toSearch.length());

        }
        return data;
    }
        public static String findUntilAndInsert(String data, String replaceAStr, String replaceEStr, String closing)    {

            String replaceStr="";
            int quantifierPosition;
            int untilPosition;
             String wordToFind = "\\)\\s*U\\s*\\(";
            Pattern word = Pattern.compile(wordToFind);
            Matcher match = word.matcher(data);


            while (match.find()) {
                untilPosition = match.start();
                int untilEndPosition = match.end() - 1;
                quantifierPosition = StringFinder.findOpeningParenthesis(data, untilPosition) - 1;
                String quantifier = data.substring(quantifierPosition, quantifierPosition+1);
                if (quantifier.equals("E")) { replaceStr=replaceEStr; }
                if (quantifier.equals("A")) { replaceStr=replaceAStr; }

                int bracketpos = StringFinder.findClosingParenthesis(data, untilEndPosition); //assume last char is the "("
                    String orginalblock = data.substring(untilEndPosition, bracketpos);
                    String prepend = data.substring(0, untilPosition);
                    String append = data.substring(bracketpos);
                    data = prepend + match.toString() + replaceStr + orginalblock + closing + append;
                match = word.matcher(data.substring(untilEndPosition));
            }
            return data;
        }


    /**
     * Find any matching substring and surround all occurrences with 'replacestring' + 'substring' + 'closing'
     * (search from right to left)
     * @param data          string that might contain substrings to search
     * @param toSearch      substring to search for
     * @param replaceStr    suffix of the embedding
     * @param opening       prefix of the embedding
     *
     *
     * inspired by https://thispointer.com/find-and-replace-all-occurrences-of-a-sub-string-in-c \n
     * customized  for TESTAR
     */
    public static String findOpeningAndInsert(String data, String toSearch, String replaceStr, String opening) {
        // Get the first occurrence
        int pos = data.indexOf(toSearch);
        while (pos != -1) {   // Repeat till end is reached
            // Replace this occurrence of Sub String
            //find matching bracket
            //not validated: check bracket position
            int bracketpos = findOpeningParenthesis(data, pos - toSearch.length() + 1); //assume first char is the ")"
            String orginalblock = data.substring(bracketpos, pos-1);
            String prepend=data.substring(0,bracketpos) ;
            String append= data.substring(pos+toSearch.length());
            data=prepend+opening + orginalblock + replaceStr + toSearch+append;
            // Get the next occurrence from the current position
            pos = data.indexOf(toSearch,bracketpos + opening.length() + orginalblock.length() + replaceStr.length() + toSearch.length());
        }
        return data;
    }

}


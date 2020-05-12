package nl.ou.testar.temporal.util;

public class StringFinder {

   public int findClosingParenthesis(String data, int openPos) {

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
    int findOpeningParenthesis(String data, int closePos) {
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

    void findClosingParenthesisAndInsert(String data, String toSearch, String replaceStr, String closing) {
        // Get the first occurrence
       int pos = data.indexOf(toSearch);

        // Repeat till end is reached
        while (pos != -1) {
            // Replace this occurrence of Sub String
            //find matching bracket
            int bracketpos = findClosingParenthesis(data, pos + toSearch.length() - 1); //assume last char is the "("
            String orginalblock = data.substring(pos + toSearch.length() - 1, bracketpos - pos - 1);
            data=data.replaceAll(toSearch + orginalblock+closing, toSearch + replaceStr + orginalblock + closing);
            // Get the next occurrence from the current position
            pos = data.indexOf(toSearch, pos + toSearch.length() + replaceStr.length() + orginalblock.length() + closing.length());
        }
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
    void findOpeningParenthesisAndInsert(String data, String toSearch, String replaceStr, String opening) {
        // Get the first occurrence
        int pos = data.indexOf(toSearch);

        // Repeat till end is reached
        while (pos != -1) {
            // Replace this occurrence of Sub String
            //find matching bracket
            int bracketpos = findOpeningParenthesis(data, pos - toSearch.length() + 1); //assume first char is the "("
            String orginalblock = data.substring(bracketpos, pos - bracketpos + 1);
            data=data.replaceAll(opening + orginalblock + toSearch,opening + orginalblock + replaceStr + toSearch);
            // Get the next occurrence from the current position
            pos = data.indexOf(toSearch,
                    bracketpos + opening.length() + orginalblock.length() + replaceStr.length() + toSearch.length());
        }
    }

//    findForwardAndInsertAll(ltlf_string, "F(", "(!" + ltlf_alive_ap + ")|", ")");
//    findForwardAndInsertAll(ltlf_string, "X(", "(!" + ltlf_alive_ap + ")|", ")");
//    findForwardAndInsertAll(ltlf_string, "U (", "(!" + ltlf_alive_ap + ")|", ")");
//    findBackwardAndInsertAll(ltlf_string, ") M", "|(!" + ltlf_alive_ap + ")", "(");
}


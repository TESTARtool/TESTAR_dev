package nl.ou.testar.tgherkin;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Listener to Tgherkin recognition errors.
 *
 */
public class TgherkinErrorListener extends BaseErrorListener {

	private List<String> errorList;

	/**
	 * Constructor.
	 */
	public TgherkinErrorListener() {
		super();
		errorList = new ArrayList<String>();
	}

	/**
     * Retrieve whether the Tgherkin text file is valid.
     * @return true if Tgherkin text file is valid, otherwise false
     */
	public boolean isValid() {
		return errorList.size() == 0;
	}

	/**
     * Retrieve error list.
     * @return list of error descriptions, empty list if no errors exist
     */
	public List<String> getErrorList() {
		return errorList;
	}
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		errorList.add("line " + line + ":" + charPositionInLine + " " + msg + "\n");
	}

}

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
     * Retrieve result.
     * @return true if Tgherkin document is valid, otherwise false
     */
	public boolean isValid() {
		return errorList.size() == 0;
	}

	/**
     * Retrieve error list.
     * @return List of error descriptions
     */
	public List<String> getErrorList() {
		return errorList;
	}
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e){
		errorList.add("line " + line + ":" + charPositionInLine + " " + msg + "\n");
	}

}

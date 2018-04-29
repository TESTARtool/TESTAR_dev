package nl.ou.testar.tgherkin;

import java.awt.Color;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.TgherkinParser;

/**
 * This class defines styles for a Tgherkin text document.
 * The Tgherkin parser result is visited to identify relevant tokens. 
 * All relevant tokens will be displayed in a particular style.
 */
public class TgherkinStyledDocument extends DefaultStyledDocument  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2831916204108462372L;
	private Style defaultStyle;
	private Style keywordStyle;
	private Style variableStyle;
	private Style literalStyle;
	private Style gestureStyle;    
	private Style functionStyle;   
	private Style placeholderStyle;
	private Style tableStyle;

	/**
	 * Constructor.
	 */
	public TgherkinStyledDocument() {
		StyleContext styleContext = new StyleContext();
		defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
		keywordStyle = styleContext.addStyle("ConstantWidth", null);
		StyleConstants.setForeground(keywordStyle, Color.BLUE);
		StyleConstants.setBold(keywordStyle, true);
		variableStyle = styleContext.addStyle("ConstantWidth", null);
		StyleConstants.setForeground(variableStyle, new Color(0, 153, 0));//dark green
		StyleConstants.setBold(variableStyle, true);
		literalStyle = styleContext.addStyle("ConstantWidth", null);
		StyleConstants.setForeground(literalStyle, Color.ORANGE);
		StyleConstants.setBold(literalStyle, true);
		gestureStyle = styleContext.addStyle("ConstantWidth", null);
		StyleConstants.setForeground(gestureStyle, Color.RED);
		StyleConstants.setBold(gestureStyle, true);
		functionStyle = styleContext.addStyle("ConstantWidth", null);
		StyleConstants.setForeground(functionStyle, Color.PINK);
		StyleConstants.setBold(functionStyle, true);
		placeholderStyle = styleContext.addStyle("ConstantWidth", null);
		StyleConstants.setForeground(placeholderStyle, new Color(102, 0, 153));//purple
		StyleConstants.setBold(placeholderStyle, true);
		tableStyle = styleContext.addStyle("ConstantWidth", null);
		StyleConstants.setForeground(tableStyle, new Color(153, 153, 153));//grey
	}

	@Override 
	public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offset, str, a);
		refreshDocument();
	}

	@Override 
	public void remove (int offs, int len) throws BadLocationException {
		super.remove(offs, len);
		refreshDocument();
	}

	private synchronized void refreshDocument() throws BadLocationException {
		String text = getText(0, getLength());
		setCharacterAttributes(0, text.length(), defaultStyle, true);
		List<Integer> indexRelevantTokens = getIndexRelevantTokens(text);
		ANTLRInputStream inputStream = new ANTLRInputStream(text);
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		int index = 0;
		for (Token token : lexer.getAllTokens()) {
			if (indexRelevantTokens.contains(index)) {
				setCharacterAttributes(token.getStartIndex(), token.getStopIndex() - token.getStartIndex() + 1, getTokenStyle(token), true);
			}
			index++;
		}
	}       

	private List<Integer> getIndexRelevantTokens(String expression) {
		TgherkinParser parser = Utils.getTgherkinParser(expression);
		return new TgherkinTokenAnalyzer().visitDocument(parser.document());
	}

	private Style getTokenStyle(Token token){
		switch (token.getType()) {
		case TgherkinLexer.ANY_NAME:
		case TgherkinLexer.CLICK_NAME:
		case TgherkinLexer.DOUBLE_CLICK_NAME:
		case TgherkinLexer.DRAG_NAME:
		case TgherkinLexer.DROP_DOWN_AT_NAME:				
		case TgherkinLexer.MOUSE_MOVE_NAME:
		case TgherkinLexer.RIGHT_CLICK_NAME:
		case TgherkinLexer.TRIPLE_CLICK_NAME:
		case TgherkinLexer.TYPE_NAME:
			return gestureStyle;
		case TgherkinLexer.MATCHES_NAME:
		case TgherkinLexer.XPATH_NAME:	
		case TgherkinLexer.IMAGE_NAME:
			return functionStyle;
		case TgherkinLexer.BACKGROUND_KEYWORD:
		case TgherkinLexer.EXAMPLES_KEYWORD:
		case TgherkinLexer.FEATURE_KEYWORD:
		case TgherkinLexer.ORACLE_KEYWORD:				
		case TgherkinLexer.SCENARIO_KEYWORD:
		case TgherkinLexer.SCENARIO_OUTLINE_KEYWORD:
		case TgherkinLexer.SELECTION_KEYWORD:
		case TgherkinLexer.STEP_ALSO_KEYWORD:
		case TgherkinLexer.STEP_EITHER_KEYWORD:
		case TgherkinLexer.STEP_GIVEN_KEYWORD:
		case TgherkinLexer.STEP_KEYWORD:
		case TgherkinLexer.STEP_RANGE_KEYWORD:
		case TgherkinLexer.STEP_THEN_KEYWORD:
		case TgherkinLexer.STEP_WHEN_KEYWORD:
			return keywordStyle;
		case TgherkinLexer.BOOLEAN_VARIABLE:
		case TgherkinLexer.NUMBER_VARIABLE:
		case TgherkinLexer.STRING_VARIABLE:	
			return variableStyle;
		case TgherkinLexer.PLACEHOLDER:
			return placeholderStyle;
		case TgherkinLexer.TABLE_ROW:
			return tableStyle;
		case TgherkinLexer.WS:
			return defaultStyle;
		case TgherkinLexer.FALSE:
		case TgherkinLexer.TRUE:
		case TgherkinLexer.DECIMAL_NUMBER:
		case TgherkinLexer.INTEGER_NUMBER:
		case TgherkinLexer.STRING:
			return literalStyle;
		case TgherkinLexer.COMMENT:
			return defaultStyle;
		default:
			return defaultStyle;
		}
	} 

}
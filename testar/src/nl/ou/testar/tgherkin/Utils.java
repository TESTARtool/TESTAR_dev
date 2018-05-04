package nl.ou.testar.tgherkin;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.gen.WidgetConditionParser;

/**
 * Tgherkin language processing utilities.
 *
 */
public class Utils {
	
	private Utils() {
	}
	
	/**
	 * Get document model from file.
	 * @param fileName name of Tgherkin text file
	 * @return document
	 */
	public static nl.ou.testar.tgherkin.model.Document getDocumentFromFile(String fileName) {
		ANTLRInputStream inputStream;
		try {
			inputStream = new ANTLRInputStream(new FileInputStream(fileName));
		}catch (IOException e) {
			throw new TgherkinException("Unable to open character stream for Tgherkin file: " + fileName);
		}        
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		return getDocument(lexer);
	}
	
	private static nl.ou.testar.tgherkin.model.Document getDocument(TgherkinLexer lexer) {
		nl.ou.testar.tgherkin.model.Document document = null;
	    TgherkinParser parser = getTgherkinParser(lexer);
		TgherkinErrorListener errorListener = new TgherkinErrorListener();
		parser.addErrorListener(errorListener);
		document = new nl.ou.testar.tgherkin.DocumentBuilder().visitDocument(parser.document());
		List<String> errorList = errorListener.getErrorList();
		if (errorList.size() == 0) {
			// post-processing check
			errorList = document.check();
		}
		if (errorList.size() != 0) {
			for(String errorText : errorList) {
				LogSerialiser.log(errorText, LogSerialiser.LogLevel.Info);
			}
			throw new TgherkinException("Invalid Tgherkin document, see log for details");
		}
		return document;
	}
	
	/**
	 * Get document model.
	 * @param code Tgherkin code
	 * @return document
	 */
	public static nl.ou.testar.tgherkin.model.Document getDocument(String code) {
		ANTLRInputStream inputStream = new ANTLRInputStream(code);
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		return getDocument(lexer);
	}
	
	
	/**
	 * Get Tgherkin parser.
	 * @param code Tgherkin code
	 * @return Tgherkin parser
	 */
	public static TgherkinParser getTgherkinParser(String code) {
		ANTLRInputStream inputStream = new ANTLRInputStream(code);
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		return getTgherkinParser(lexer);
	}
	
	private static TgherkinParser getTgherkinParser(TgherkinLexer lexer) {
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TgherkinParser parser = new TgherkinParser(tokens);
		// remove error listener that generates output to the console
		parser.removeErrorListeners();
		return parser;
	}

	
	/**
	 * Get widget condition parser.
	 * @param code widget condition code
	 * @return widget condtion parser
	 */
	public static WidgetConditionParser getWidgetConditionParser(String code) {
		ANTLRInputStream inputStream = new ANTLRInputStream(code);
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		WidgetConditionParser parser = new WidgetConditionParser(new CommonTokenStream(lexer));
		// remove error listener that generates output to the console
		parser.removeErrorListeners();
		return parser;
	}
	
	/**
	 * Retrieve Tgherkin source code from file.
	 * @param fileName name of Tgherkin source code file
	 * @return Tgherkin source code
	 */
	public static String readTgherkinSourceFile(String fileName) {
		String sourceCode = null;
	    try{
	        sourceCode = new String (Files.readAllBytes(Paths.get(fileName)));
	    }catch (IOException e){
			throw new TgherkinException("Unable to read Tgherkin file: " + fileName);
	    }
	    return sourceCode;
	}	

}	

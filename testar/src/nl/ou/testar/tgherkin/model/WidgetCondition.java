package nl.ou.testar.tgherkin.model;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.fruit.Assert;
import org.fruit.alayer.Widget;

import nl.ou.testar.tgherkin.WidgetConditionEvaluator;
import nl.ou.testar.tgherkin.WidgetConditionValidator;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.WidgetConditionParser;

/**
 * Tgherkin WidgetCondition.
 * 
 */
public class WidgetCondition {

	/**
	 * Type.
	 *
	 */
	public enum Type {
	    ALSO,
	    EITHER
	  }
	
	private final Type type;
	private final String code;
	
	/**
	 * WidgetCondition Constructor.
	 * @param code given Tgherkin source code
	 */
	public WidgetCondition(String code){
		Assert.notNull(code);		
		this.type = null;
		this.code = code;
	}

	/**
	 * WidgetCondition Constructor.
	 * @param type given type
	 * @param code given Tgherkin source code
	 */
	public WidgetCondition(Type type, String code){
		Assert.notNull(code);		
		this.type = type;
		this.code = code;
	}
	
	/**
	 * Retrieve type.
	 * @return type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Retrieve code.
	 * @return code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Evaluate widget condition.
	 * @param widget given widget
	 * @param dataTable given data table
	 * @return  true if condition is applicable for widget, otherwise false 
	 */
	public boolean evaluate(Widget widget, DataTable dataTable) {				
		ANTLRInputStream inputStream = new ANTLRInputStream(getCode());
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		WidgetConditionParser parser = new WidgetConditionParser(new CommonTokenStream(lexer));
		Boolean result = false;
		try {
			result = (Boolean)new WidgetConditionEvaluator(widget, dataTable).visit(parser.widget_condition());
		}catch (Exception e) {}
		return result;
	}
	
    /**
     * Check widget condition.
     * @param dataTable given data table
     * @return list of error descriptions
     */
	public List<String> check(DataTable dataTable) {
		ANTLRInputStream inputStream = new ANTLRInputStream(getCode());
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		WidgetConditionParser parser = new WidgetConditionParser(new CommonTokenStream(lexer));
		WidgetConditionValidator validator = new WidgetConditionValidator(dataTable);
		validator.visit(parser.widget_condition());
		return validator.getErrorList();
	}

	
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	if (getType() == Type.ALSO) {
    		result.append("Also ");
    	}else {
        	if (getType() == Type.EITHER) {
        		result.append("Either ");
        	}    		
    	}
    	result.append(getCode());    	
    	return result.toString();    	
    }
	
}

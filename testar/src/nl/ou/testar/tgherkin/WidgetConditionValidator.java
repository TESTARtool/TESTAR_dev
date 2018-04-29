package nl.ou.testar.tgherkin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.ou.testar.tgherkin.gen.WidgetConditionParser;
import nl.ou.testar.tgherkin.gen.WidgetConditionParserBaseVisitor;
import nl.ou.testar.tgherkin.model.DataTable;

/**
 * This visitor class validates a widget condition.
 * The validation is performed by calling the visit method. 
 * The visit validation result can afterwards be retrieved via the isValid method.  
 * The error list can afterwards be retrieved via the getErrorList method.
 * The base visitor super class has been auto-generated for the WidgetCondition grammar.
 */
public class WidgetConditionValidator extends WidgetConditionParserBaseVisitor<Object> {

	private final DataTable dataTable;
	private List<String> errorList;

	/**
	 * Constructor.
	 * @param dataTable given data table
	 */
	public WidgetConditionValidator(DataTable dataTable) {
		this.dataTable = dataTable;
		errorList = new ArrayList<String>();
	}

	/**
	 * Retrieve validation result.
	 * @return true if widget condition is valid, otherwise false
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
	public Object visitLogicalPlaceholder(WidgetConditionParser.LogicalPlaceholderContext ctx) { 
		// check whether placeholder is a valid column name of the data table
		String columnName = ctx.getText().substring(1, ctx.getText().length() - 1);
		if (dataTable == null){
			errorList.add("Widget condition validation error - no data table found for logical placeholder : " + columnName + System.getProperty("line.separator"));
		}else {
			if (!dataTable.isColumnName(columnName)){
				errorList.add("Widget condition validation error - invalid logical placeholder : " + columnName + System.getProperty("line.separator"));
			}else {
				checkTableContent(columnName, new Boolean(true));
			}
		}
		return visitChildren(ctx);
	}	


	@Override 
	public Object visitNumericPlaceholder(WidgetConditionParser.NumericPlaceholderContext ctx) { 
		// check whether placeholder is a valid column name of the data table
		String columnName = ctx.getText().substring(1, ctx.getText().length() - 1);
		if (dataTable == null){
			errorList.add("Widget condition validation error - no data table found for numeric placeholder : " + columnName + System.getProperty("line.separator"));
		}else {
			if (!dataTable.isColumnName(columnName)){
				errorList.add("Widget condition validation error - invalid numeric placeholder : " + columnName + System.getProperty("line.separator"));
			}else {
				checkTableContent(columnName, new Double(0));
			}
		}
		return visitChildren(ctx);
	}	

	@Override 
	public Object visitStringPlaceholder(WidgetConditionParser.StringPlaceholderContext ctx) { 
		// check whether placeholder is a valid column name of the data table
		String columnName = ctx.getText().substring(1, ctx.getText().length() - 1);
		if (dataTable == null){
			errorList.add("Widget condition validation error - no data table found for string placeholder : " + columnName + System.getProperty("line.separator"));
		}else {
			if (!dataTable.isColumnName(columnName)){
				errorList.add("Widget condition validation error - invalid string placeholder : " + columnName + System.getProperty("line.separator"));
			}else {
				checkTableContent(columnName, new String());
			}
		}
		return visitChildren(ctx);
	}		

	@Override
	public Boolean visitMatchesFunction(WidgetConditionParser.MatchesFunctionContext ctx) { 
		String regex = ctx.STRING().getText();
		// unquote regex
		regex = regex.substring(1, regex.length()-1);
		boolean result = false;
		try {
			Pattern.compile(regex);
		}catch(PatternSyntaxException e){
			errorList.add("Widget condition validation error - invalid regular expression : " + regex + System.getProperty("line.separator"));		
		}
		return result;
	}

	@Override 
	public Boolean visitXpathFunction(WidgetConditionParser.XpathFunctionContext ctx) { 
		String xpathExpr = ctx.STRING().getText();
		// unquote 
		xpathExpr = xpathExpr.substring(1, xpathExpr.length()-1);
		boolean result = false;
		try{
			// Create XPathFactory object
			XPathFactory xpathFactory = XPathFactory.newInstance();
			// Create XPath object
			XPath xpath = xpathFactory.newXPath();
			//create XPathExpression object
			xpath.compile(xpathExpr);
		}catch(XPathExpressionException e){
			errorList.add("Widget condition validation error - invalid xpath expression : " + xpathExpr + System.getProperty("line.separator"));		
		}
		return result;
	}	

	@Override
	public Boolean visitImageFunction(WidgetConditionParser.ImageFunctionContext ctx) { 
		String imageFile = ctx.STRING().getText();
		// unquote image file
		imageFile = imageFile.substring(1, imageFile.length()-1);
		File file = new File(imageFile); 
		if(file.exists() && !file.isDirectory()) {
			return true;
		}
		errorList.add("Widget condition validation error - image file not found : " + imageFile + System.getProperty("line.separator"));		
		return false;
	}	

	private void checkTableContent(String columnName, Object type) {
		if (dataTable != null){
			int rows = 0;
			while (dataTable.moreSequences()) {
				dataTable.beginSequence();
				rows++;
				try {
					String value = dataTable.getPlaceholderValue(columnName);
					if (type instanceof Boolean) {
						if (!("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))) {
							errorList.add("Widget condition validation error - invalid boolean table value at row " + rows + " for placeholder " + columnName + " : " + value + System.getProperty("line.separator"));
						}
					}else {
						if (type instanceof Double) {
							try {
								System.out.println("Checking double "  + value);								
								Double.valueOf(value);
							}catch(Exception e) {
								errorList.add("Widget condition validation error - invalid double table value at row " + rows + " for placeholder " + columnName + " : " + value + System.getProperty("line.separator"));					
							}
						}						
					}
				}catch(Exception e) {
					errorList.add("Widget condition validation error - invalid table value at row " + rows + " for placeholder " + columnName + System.getProperty("line.separator"));					
				}
			}
			dataTable.reset();
		}
	}

}
package nl.ou.testar.tgherkin;

import java.util.ArrayList;
import java.util.List;

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
			}
		}
		return visitChildren(ctx);
	}		
	
}
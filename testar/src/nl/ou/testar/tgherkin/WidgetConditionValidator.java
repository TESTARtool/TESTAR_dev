package nl.ou.testar.tgherkin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.ou.testar.tgherkin.gen.WidgetConditionParser;
import nl.ou.testar.tgherkin.gen.WidgetConditionParserBaseVisitor;
import nl.ou.testar.tgherkin.model.DataTable;

/**
 * Class that validates a WidgetCondition.
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
   * @param dataTable data table contained in the examples section of a scenario outline
   */
  public WidgetConditionValidator(DataTable dataTable) {
    this.dataTable = dataTable;
    errorList = new ArrayList<String>();
  }

  /**
   * Retrieve whether the condition is valid.
   * @return true if widget condition is valid, otherwise false
   */
  public boolean isValid() {
    return errorList.size() == 0;
  }

  /**
   * Retrieve error list.
   * @return List of error descriptions, empty list if no errors exist
   */
  public List<String> getErrorList() {
    return errorList;
  }

  @Override
  public Object visitLogicalPlaceholder(WidgetConditionParser.LogicalPlaceholderContext ctx) {
    // check whether placeholder is a valid column name of the data table
    String columnName = ctx.getText().substring(1, ctx.getText().length() - 1);
    if (dataTable == null) {
      errorList.add("Widget condition validation error - no data table found for logical placeholder: " + columnName + System.getProperty("line.separator"));
    } else {
      if (!dataTable.isColumnName(columnName)) {
        errorList.add("Widget condition validation error - invalid logical placeholder: " + columnName + System.getProperty("line.separator"));
      } else {
        checkTableContent(columnName, new Boolean(true));
      }
    }
    return visitChildren(ctx);
  }


  @Override
  public Object visitNumericPlaceholder(WidgetConditionParser.NumericPlaceholderContext ctx) {
    // check whether placeholder is a valid column name of the data table
    String columnName = ctx.getText().substring(1, ctx.getText().length() - 1);
    if (dataTable == null) {
      errorList.add("Widget condition validation error - no data table found for numeric placeholder: " + columnName + System.getProperty("line.separator"));
    } else {
      if (!dataTable.isColumnName(columnName)) {
        errorList.add("Widget condition validation error - invalid numeric placeholder: " + columnName + System.getProperty("line.separator"));
      } else {
        checkTableContent(columnName, new Double(0));
      }
    }
    return visitChildren(ctx);
  }

  @Override
  public Object visitStringPlaceholder(WidgetConditionParser.StringPlaceholderContext ctx) {
    // check whether placeholder is a valid column name of the data table
    String columnName = ctx.getText().substring(1, ctx.getText().length() - 1);
    if (dataTable == null) {
      errorList.add("Widget condition validation error - no data table found for string placeholder: " + columnName + System.getProperty("line.separator"));
    } else {
      if (!dataTable.isColumnName(columnName)) {
        errorList.add("Widget condition validation error - invalid string placeholder: " + columnName + System.getProperty("line.separator"));
      } else {
        checkTableContent(columnName, new String());
      }
    }
    return visitChildren(ctx);
  }

  @Override
  public Object visitMatchesFunction(WidgetConditionParser.MatchesFunctionContext ctx) {
    // unquote regex
    String regex = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
    try {
      Pattern.compile(regex);
    } catch(PatternSyntaxException e) {
      errorList.add("Widget condition validation error - invalid regular expression: " + regex + System.getProperty("line.separator"));
    }
    return visitChildren(ctx);
  }

  @Override
  public Object visitXpathFunction(WidgetConditionParser.XpathFunctionContext ctx) {
    // unquote
    String xpathExpr = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
    checkXPath(xpathExpr, XPathConstants.NODESET);
    return visitChildren(ctx);
  }

  @Override
  public Object visitXpathBooleanFunction(WidgetConditionParser.XpathBooleanFunctionContext ctx) {
    // unquote
    String xpathExpr = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
    checkXPath(xpathExpr, XPathConstants.BOOLEAN);
    return visitChildren(ctx);
  }

  @Override
  public Object visitXpathNumberFunction(WidgetConditionParser.XpathNumberFunctionContext ctx) {
    // unquote
    String xpathExpr = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
    checkXPath(xpathExpr, XPathConstants.NUMBER);
    return visitChildren(ctx);
  }

  @Override
  public Object visitXpathStringFunction(WidgetConditionParser.XpathStringFunctionContext ctx) {
    // unquote
    String xpathExpr = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
    checkXPath(xpathExpr, XPathConstants.STRING);
    return visitChildren(ctx);
  }

  @Override
  public Object visitImageFunction(WidgetConditionParser.ImageFunctionContext ctx) {
    // unquote image file
    String imageFile = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
    File file = new File(imageFile);
    if (!file.exists() || file.isDirectory()) {
      errorList.add("Widget condition validation error - image file not found: " + imageFile + System.getProperty("line.separator"));
    }
    return visitChildren(ctx);
  }

  private void checkTableContent(String columnName, Object type) {
    if (dataTable != null) {
      int rows = 0;
      while (dataTable.moreSequences()) {
        dataTable.beginSequence();
        rows++;
        try {
          String value = dataTable.getPlaceholderValue(columnName);
          if (type instanceof Boolean) {
            if (!("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))) {
              errorList.add("Widget condition validation error - invalid boolean table value at row " + rows + " for placeholder " + columnName + ": " + value + System.getProperty("line.separator"));
            }
          } else {
            if (type instanceof Double) {
              try {
                Double.valueOf(value);
              } catch(Exception e) {
                errorList.add("Widget condition validation error - invalid double table value at row " + rows + " for placeholder " + columnName + ": " + value + System.getProperty("line.separator"));
              }
            }
          }
        } catch(Exception e) {
          errorList.add("Widget condition validation error - invalid table value at row " + rows + " for placeholder " + columnName + System.getProperty("line.separator"));
        }
      }
      dataTable.reset();
    }
  }

  private void checkXPath(String xpathExpr, QName resultType) {
    String xpathExprXML = Utils.getXMLXpathExpression(xpathExpr);
    try {
      // Create XPathFactory object
      XPathFactory xpathFactory = XPathFactory.newInstance();
      // Create XPath object
      XPath xpath = xpathFactory.newXPath();
      //create XPathExpression object
      xpath.compile(xpathExprXML);
    } catch(XPathExpressionException e) {
      errorList.add("Widget condition validation error - invalid xpath expression: " + xpathExpr + System.getProperty("line.separator"));
      return;
    }
    try {
      // check correctness of result type with dummy xml data
      Utils.evaluateXPathExpresion(Utils.XML_HEADER + Utils.toXMLElement("widget","test"), xpathExpr, resultType);
    } catch (Exception e) {
      errorList.add(e.getMessage());
      }
  }


}

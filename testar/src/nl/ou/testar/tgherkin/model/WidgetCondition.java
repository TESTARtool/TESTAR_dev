package nl.ou.testar.tgherkin.model;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.fruit.Assert;
import org.fruit.alayer.Widget;

import nl.ou.testar.tgherkin.Utils;
import nl.ou.testar.tgherkin.WidgetConditionEvaluator;
import nl.ou.testar.tgherkin.WidgetConditionValidator;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.WidgetConditionParser;

/**
 * Representation of a widget condition that delegates evaluation to WidgetConditionEvaluator.
 *
 */
public class WidgetCondition {

  /**
   * Type.
   *
   */
  public enum Type {
      /**
       * Also type: logical and.
       */
      ALSO,
      /**
       * Either type: logical or.
       */
      EITHER
    }

  private final Type type;
  private final String code;
  private WidgetConditionEvaluator evaluator;

  /**
   * WidgetCondition Constructor.
   * @param code Tgherkin source code
   */
  public WidgetCondition(String code) {
    Assert.notNull(code);
    this.type = null;
    this.code = code;
  }

  /**
   * WidgetCondition Constructor.
   * @param type operator type
   * @param code Tgherkin source code
   */
  public WidgetCondition(Type type, String code) {
    Assert.notNull(code);
    this.type = type;
    this.code = code;
  }

  /**
   * Retrieve operator type.
   * @return operator type
   */
  public Type getType() {
    return type;
  }

  /**
   * Retrieve source code.
   * @return source code
   */
  public String getCode() {
    return code;
  }

  /**
   * Evaluate widget condition.
   * @param proxy document protocol proxy
   * @param widget to be evaluated widget
   * @param dataTable data table contained in the examples section of a scenario outline
   * @return  true if condition is applicable for widget, otherwise false
   */
  public boolean evaluate(ProtocolProxy proxy, Widget widget, DataTable dataTable) {
    if (evaluator == null) {
      evaluator = new WidgetConditionEvaluator(proxy, widget, dataTable);
    } else {
      evaluator.set(proxy, widget, dataTable);
    }
    WidgetConditionParser parser = Utils.getWidgetConditionParser(getCode());
    Boolean result = false;
    try {
      result = (Boolean)evaluator.visit(parser.widget_condition());
    } catch (Exception e) {
    }
    return result;
  }

    /**
     * Check widget condition.
     * @param dataTable data table contained in the examples section of a scenario outline
     * @return list of error descriptions, empty list if no errors exist
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
      } else {
          if (getType() == Type.EITHER) {
            result.append("Either ");
          }
      }
      result.append(getCode());
      return result.toString();
    }

}

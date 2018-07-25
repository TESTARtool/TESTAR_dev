package nl.ou.testar.tgherkin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.fruit.alayer.Shape;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.linux.AtSpiTags;
import org.fruit.alayer.windows.UIATags;

import es.upv.staq.testar.NativeLinker;
import nl.ou.testar.tgherkin.functions.Image;
import nl.ou.testar.tgherkin.functions.Matches;
import nl.ou.testar.tgherkin.functions.OCR;
import nl.ou.testar.tgherkin.functions.XPath;
import nl.ou.testar.tgherkin.gen.WidgetConditionParser;
import nl.ou.testar.tgherkin.gen.WidgetConditionParserBaseVisitor;
import nl.ou.testar.tgherkin.model.ProtocolProxy;
import nl.ou.testar.tgherkin.model.WidgetCondition;
import nl.ou.testar.tgherkin.model.WidgetTreeCondition;
import nl.ou.testar.tgherkin.model.DataTable;

/**
 * Class responsible for the evaluation of a WidgetCondition.
 * The base visitor super class has been auto-generated for the WidgetCondition grammar.
 */
public class WidgetConditionEvaluator extends WidgetConditionParserBaseVisitor<Object> {

	private static final double TOLERANCE = 5E-16;
	private static Map<String, Tag<?>> tagMap;
	
	private ProtocolProxy proxy;
	private Widget widget;
	private DataTable dataTable;

	private List<WidgetCondition> widgetConditions;
	private Queue<WidgetCondition.Type> operatorQueue = new LinkedList<WidgetCondition.Type>();

	/**
	 * Constructor.
	 * @param proxy document protocol proxy
	 * @param widget to be evaluated widget
	 * @param dataTable data table contained in the examples section of a scenario outline
	 */
	public WidgetConditionEvaluator(ProtocolProxy proxy, Widget widget, DataTable dataTable) {
		this.proxy = proxy;
		this.widget = widget;
		this.dataTable = dataTable;
		if (tagMap == null) {
			tagMap = new HashMap<String, Tag<?>>();
			// trigger class initialization, otherwise getNativeTags() could return the empty set
			@SuppressWarnings("unused")
			Object obj = Tags.Abstract_R_ID;
			obj = UIATags.UIAAcceleratorKey;
			obj = AtSpiTags.AtSpiCanScroll;
			for(Tag<?> nativeTag : NativeLinker.getNativeTags()) {
				tagMap.put(nativeTag.name(), nativeTag);
			}
		}
	}
	
	/**
	 * Set data attributes.
	 * @param proxy document protocol proxy
	 * @param widget to be evaluated widget
	 * @param dataTable data table contained in the examples section of a scenario outline
	 */
	public void set(ProtocolProxy proxy, Widget widget, DataTable dataTable) {
		this.proxy = proxy;
		this.widget = widget;
		this.dataTable = dataTable;
	}


	@Override 
	public Boolean visitWidgetConditionInParen(WidgetConditionParser.WidgetConditionInParenContext ctx) { 
		return asBoolean(ctx.widget_condition()); 
	}

	@Override 
	public Boolean visitNegationWidgetCondition(WidgetConditionParser.NegationWidgetConditionContext ctx) { 
		return !asBoolean(ctx.widget_condition());
	}

	@Override 
	public Boolean visitWidgetConditionAnd(WidgetConditionParser.WidgetConditionAndContext ctx) { 
		return asBoolean(ctx.left) && asBoolean(ctx.right); 
	}


	@Override 
	public Boolean visitWidgetConditionOr(WidgetConditionParser.WidgetConditionOrContext ctx) { 
		return asBoolean(ctx.left) || asBoolean(ctx.right);
	}

	@Override 
	public Boolean visitRelationalNumericExpressionWithOperator(WidgetConditionParser.RelationalNumericExpressionWithOperatorContext ctx) { 
		double leftDouble = asDouble(ctx.left);
		double rightDouble = asDouble(ctx.right);
		if (ctx.relational_operator().EQ() != null) {
			return approxEqual(leftDouble, rightDouble);
		}
		if (ctx.relational_operator().NE() != null) {
			return !approxEqual(leftDouble, rightDouble);
		}
		if (ctx.relational_operator().LE() != null) {
			return approxEqual(leftDouble, rightDouble) || leftDouble < rightDouble;
		}
		if (ctx.relational_operator().GE() != null) {
			return approxEqual(leftDouble, rightDouble) || leftDouble > rightDouble;
		}
		if (ctx.relational_operator().LT() != null) {
			return !approxEqual(leftDouble, rightDouble) && leftDouble < rightDouble;
		}
		if (ctx.relational_operator().GT() != null) {
			return !approxEqual(leftDouble, rightDouble) && leftDouble > rightDouble;
		} 
		throw new TgherkinException("unknown relational operator: " + ctx.relational_operator().getText());
	}

	@Override 
	public Boolean visitRelationalStringExpressionWithOperator(WidgetConditionParser.RelationalStringExpressionWithOperatorContext ctx) { 
		if (ctx.relational_operator().EQ() != null) {
			return asString(ctx.left).equals(asString(ctx.right));
		}
		if (ctx.relational_operator().NE() != null) {
			return !asString(ctx.left).equals(asString(ctx.right));
		}
		if (ctx.relational_operator().LE() != null) {
			return asString(ctx.left).compareTo(asString(ctx.right)) <= 0;
		}
		if (ctx.relational_operator().GE() != null) {
			return asString(ctx.left).compareTo(asString(ctx.right)) >= 0;
		}
		if (ctx.relational_operator().LT() != null) {
			return asString(ctx.left).compareTo(asString(ctx.right)) < 0;
		}
		if (ctx.relational_operator().GT() != null) {
			return asString(ctx.left).compareTo(asString(ctx.right)) > 0;
		}
		throw new TgherkinException("unknown relational operator: " + ctx.relational_operator().getText());
	}

	@Override 
	public Boolean visitRelationalExpressionParens(WidgetConditionParser.RelationalExpressionParensContext ctx) { 
		return asBoolean(ctx.relational_expr());
	}

	@Override 
	public Double visitArithmeticExpressionPow(WidgetConditionParser.ArithmeticExpressionPowContext ctx) { 
		return Math.pow(asDouble(ctx.left), asDouble(ctx.right));
	}

	@Override 
	public Double visitArithmeticExpressionParens(WidgetConditionParser.ArithmeticExpressionParensContext ctx) { 
		return asDouble(ctx.arithmetic_expr());
	}


	@Override 
	public Double visitArithmeticExpressionMultDivMod(WidgetConditionParser.ArithmeticExpressionMultDivModContext ctx) { 
		if (ctx.MULT() != null) {
			return asDouble(ctx.left) * asDouble(ctx.right);
		}
		if (ctx.DIV() != null) {
			return asDouble(ctx.left) / asDouble(ctx.right);
		}
		if (ctx.MOD() != null) {
			return asDouble(ctx.left) % asDouble(ctx.right);
		}
		throw new TgherkinException("unknown arithmetic operator: ");
	}

	@Override 
	public Double visitArithmeticExpressionNegation(WidgetConditionParser.ArithmeticExpressionNegationContext ctx) { 
		return asDouble(ctx.arithmetic_expr()) * -1; 
	}
	

	@Override 
	public Double visitArithmeticExpressionPlusMinus(WidgetConditionParser.ArithmeticExpressionPlusMinusContext ctx) { 
		if (ctx.PLUS() != null) {
			return asDouble(ctx.left) + asDouble(ctx.right);
		}
		if (ctx.MINUS() != null) {
			return asDouble(ctx.left) - asDouble(ctx.right);
		}
		throw new TgherkinException("unknown arithmetic operator: ");
	}
	
	@Override 
	public Boolean visitMatchesFunction(WidgetConditionParser.MatchesFunctionContext ctx) { 
		// retrieve value widget variable
		String str = (String)visit(ctx.string_entity()); 
		// unquote regex
		String regex = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
		return Matches.getInstance().isMatch(str, regex);
	}

	@Override 
	public Boolean visitXpathFunction(WidgetConditionParser.XpathFunctionContext ctx) { 
		// unquote 
		String xpathExpr = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
		return XPath.getInstance().isInXpathResult(proxy, widget, xpathExpr);
	}	
	
	@Override 
	public Boolean visitXpathBooleanFunction(WidgetConditionParser.XpathBooleanFunctionContext ctx) { 
		// unquote 
		String xpathExpr = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
		return XPath.getInstance().getXPathBooleanResult(proxy, xpathExpr);
	}

	@Override 
	public Double visitXpathNumberFunction(WidgetConditionParser.XpathNumberFunctionContext ctx) { 
		// unquote 
		String xpathExpr = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
		return XPath.getInstance().getXPathNumberResult(proxy, xpathExpr);
	}

	@Override public String visitXpathStringFunction(WidgetConditionParser.XpathStringFunctionContext ctx) { 
		// unquote 
		String xpathExpr = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
		return XPath.getInstance().getXPathStringResult(proxy, xpathExpr);
	}
	
	@Override 
	public Boolean visitImageFunction(WidgetConditionParser.ImageFunctionContext ctx) {
		// unquote 
		String imageFile = ctx.STRING().getText().substring(1, ctx.STRING().getText().length()-1);
		return Image.getInstance().isRecognized(proxy, widget, imageFile);
	}
	
	@Override 
	public String visitOcrFunction(WidgetConditionParser.OcrFunctionContext ctx) {
		String ocrResult = OCR.getInstance().getOCR(proxy, widget);
		if (ocrResult == null) {
			return "";
		}
		return ocrResult;
	}
	
	@Override 
	public Boolean visitStateFunction(WidgetConditionParser.StateFunctionContext ctx) { 
		widgetConditions = new ArrayList<WidgetCondition>();
		operatorQueue.add(null);
		visit(ctx.widget_tree_condition());
		WidgetTreeCondition widgetTreeCondition = new WidgetTreeCondition(widgetConditions);
		return widgetTreeCondition.evaluate(proxy, dataTable);
	}

	@Override public Object visitWidgetCondition(WidgetConditionParser.WidgetConditionContext ctx) { 
		WidgetCondition.Type  type = operatorQueue.poll();
		String code = ctx.getText();
		WidgetCondition widgetCondition = new WidgetCondition(type, code);
		widgetConditions.add(widgetCondition);
		return visitChildren(ctx); 
	}

	@Override 
	public Object visitWidgetTreeConditionEither(WidgetConditionParser.WidgetTreeConditionEitherContext ctx) { 
		operatorQueue.add(WidgetCondition.Type.EITHER);
		return visitChildren(ctx); 
	}

	@Override 
	public Object visitWidgetTreeConditionAlso(WidgetConditionParser.WidgetTreeConditionAlsoContext ctx) { 
		operatorQueue.add(WidgetCondition.Type.ALSO);
		return visitChildren(ctx); 
	}
	
	
	@Override 
	public Boolean visitLogicalConst(WidgetConditionParser.LogicalConstContext ctx) { 
		return Boolean.valueOf(ctx.bool().getText()); 
	}

	@Override 
	public Boolean visitLogicalVariable(WidgetConditionParser.LogicalVariableContext ctx) { 
		// retrieve value widget variable
		Boolean result = (Boolean)getTagValue(widget, ctx.BOOLEAN_VARIABLE().getText().substring(1));
		if (result == null) {
			throw new TgherkinException("Invalid logical variable");
		}
		return result;
	}
	
	@Override 
	public Boolean visitLogicalPlaceholder(WidgetConditionParser.LogicalPlaceholderContext ctx) { 
		// use placeholder name without enclosing angular brackets
		return Boolean.valueOf(dataTable.getPlaceholderValue(ctx.getText().substring(1, ctx.getText().length() - 1)));
	}	
	
	@Override 
	public Double visitIntegerConst(WidgetConditionParser.IntegerConstContext ctx) { 
		return Double.valueOf(ctx.INTEGER_NUMBER().getText()); 
	}

	@Override 
	public Double visitDecimalConst(WidgetConditionParser.DecimalConstContext ctx) { 
		return Double.valueOf(ctx.DECIMAL_NUMBER().getText()); 
	}
	
	@Override 
	public Double visitNumericVariable(WidgetConditionParser.NumericVariableContext ctx) { 
		// retrieve value widget variable		
		Double result = (Double)getTagValue(widget, ctx.NUMBER_VARIABLE().getText().substring(1));
		if (result == null) {
			throw new TgherkinException("Invalid numeric variable");
		}
		return result;
	}

	@Override 
	public Double visitNumericPlaceholder(WidgetConditionParser.NumericPlaceholderContext ctx) { 
		// use placeholder name without enclosing angular brackets
		return Double.valueOf(dataTable.getPlaceholderValue(ctx.getText().substring(1, ctx.getText().length() - 1)));
		
	}	

	@Override 
	public String visitStringConst(WidgetConditionParser.StringConstContext ctx) { 
		// string without enclosing quotes
		return ctx.STRING().getText().substring(1, ctx.STRING().getText().length() - 1);
	}

	@Override 
	public String visitStringVariable(WidgetConditionParser.StringVariableContext ctx) { 
		//retrieve value widget variable
		String result = getTagValue(widget, ctx.STRING_VARIABLE().getText().substring(1)).toString();
		if (result == null) {
			throw new TgherkinException("Invalid string variable");
		}
		return result;
	}

	@Override 
	public String visitStringPlaceholder(WidgetConditionParser.StringPlaceholderContext ctx) { 
		// use placeholder name without enclosing angular brackets
		return dataTable.getPlaceholderValue(ctx.getText().substring(1, ctx.getText().length() - 1));
	}
		
	private Boolean asBoolean(WidgetConditionParser.Widget_conditionContext ctx) {
		return (Boolean)visit(ctx);
	}
	
	private Boolean asBoolean(WidgetConditionParser.Relational_exprContext ctx) {
		return (Boolean)visit(ctx);
	}

	private Double asDouble(WidgetConditionParser.Arithmetic_exprContext ctx) {
		return (Double)visit(ctx);
	}
	
	private String asString(WidgetConditionParser.String_exprContext ctx) {
		return (String)visit(ctx);
	}
	
	private static boolean approxEqual(final double d1, final double d2) {
	    return Math.abs(d1 - d2) < TOLERANCE;
	}	
	
	private Object getTagValue(Widget widget, String tagName) {
		if ("Shape.x".equals(tagName)){
			Shape shape = widget.get(Tags.Shape, null);
			return shape.x();
		}
		if ("Shape.y".equals(tagName)){
			Shape shape = widget.get(Tags.Shape, null);
			return shape.y();
		}
		if ("Shape.width".equals(tagName)){
			Shape shape = widget.get(Tags.Shape, null);
			return shape.width();
		}
		if ("Shape.height".equals(tagName)){
			Shape shape = widget.get(Tags.Shape, null);
			return shape.height();
		}
		return widget.get(tagMap.get(tagName), null);		
	}

}
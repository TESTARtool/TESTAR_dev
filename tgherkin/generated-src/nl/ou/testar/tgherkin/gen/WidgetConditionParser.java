// Generated from WidgetConditionParser.g4 by ANTLR 4.5

package nl.ou.testar.tgherkin.gen;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WidgetConditionParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		OPTION_KEYWORD_INCLUDE=1, OPTION_KEYWORD_EXCLUDE=2, TAGNAME=3, FEATURE_KEYWORD=4, 
		BACKGROUND_KEYWORD=5, SCENARIO_KEYWORD=6, SCENARIO_OUTLINE_KEYWORD=7, 
		EXAMPLES_KEYWORD=8, SELECTION_KEYWORD=9, ORACLE_KEYWORD=10, STEP_KEYWORD=11, 
		STEP_RANGE_KEYWORD=12, STEP_GIVEN_KEYWORD=13, STEP_WHEN_KEYWORD=14, STEP_THEN_KEYWORD=15, 
		STEP_ALSO_KEYWORD=16, STEP_EITHER_KEYWORD=17, TABLE_ROW=18, DECIMAL_NUMBER=19, 
		INTEGER_NUMBER=20, PLACEHOLDER=21, STRING=22, COMMENT=23, AND=24, OR=25, 
		NOT=26, TRUE=27, FALSE=28, POW=29, MULT=30, DIV=31, MOD=32, PLUS=33, MINUS=34, 
		GT=35, GE=36, LT=37, LE=38, EQ=39, NE=40, LPAREN=41, RPAREN=42, COMMA=43, 
		MATCHES_NAME=44, CLICK_NAME=45, TYPE_NAME=46, DRAG_NAME=47, ANY_NAME=48, 
		DOUBLE_CLICK_NAME=49, TRIPLE_CLICK_NAME=50, RIGHT_CLICK_NAME=51, MOUSE_MOVE_NAME=52, 
		DROP_DOWN_AT_NAME=53, BOOLEAN_VARIABLE=54, NUMBER_VARIABLE=55, STRING_VARIABLE=56, 
		EOL=57, WS=58, OTHER=59, BOOLEAN_VARIABLE_NAME=60, NUMBER_VARIABLE_NAME=61, 
		STRING_VARIABLE_NAME=62;
	public static final int
		RULE_widget_condition = 0, RULE_relational_expr = 1, RULE_relational_operator = 2, 
		RULE_arithmetic_expr = 3, RULE_string_expr = 4, RULE_booleanFunction = 5, 
		RULE_matchesFunction = 6, RULE_bool = 7, RULE_logical_entity = 8, RULE_numeric_entity = 9, 
		RULE_string_entity = 10;
	public static final String[] ruleNames = {
		"widget_condition", "relational_expr", "relational_operator", "arithmetic_expr", 
		"string_expr", "booleanFunction", "matchesFunction", "bool", "logical_entity", 
		"numeric_entity", "string_entity"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'include:'", "'exclude:'", null, "'Feature:'", "'Background:'", 
		"'Scenario:'", "'Scenario Outline:'", "'Examples:'", "'Selection:'", "'Oracle:'", 
		"'Step:'", "'Range'", "'Given'", "'When'", "'Then'", "'Also'", "'Either'", 
		null, null, null, null, null, null, "'and'", "'or'", null, "'true'", "'false'", 
		"'^'", "'*'", "'/'", "'%'", "'+'", "'-'", "'>'", "'>='", "'<'", "'<='", 
		"'='", null, "'('", "')'", "','", "'matches'", "'click'", "'type'", "'drag'", 
		"'anyGesture'", "'doubleClick'", "'tripleClick'", "'rightClick'", "'mouseMove'", 
		"'dropDownAt'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "OPTION_KEYWORD_INCLUDE", "OPTION_KEYWORD_EXCLUDE", "TAGNAME", "FEATURE_KEYWORD", 
		"BACKGROUND_KEYWORD", "SCENARIO_KEYWORD", "SCENARIO_OUTLINE_KEYWORD", 
		"EXAMPLES_KEYWORD", "SELECTION_KEYWORD", "ORACLE_KEYWORD", "STEP_KEYWORD", 
		"STEP_RANGE_KEYWORD", "STEP_GIVEN_KEYWORD", "STEP_WHEN_KEYWORD", "STEP_THEN_KEYWORD", 
		"STEP_ALSO_KEYWORD", "STEP_EITHER_KEYWORD", "TABLE_ROW", "DECIMAL_NUMBER", 
		"INTEGER_NUMBER", "PLACEHOLDER", "STRING", "COMMENT", "AND", "OR", "NOT", 
		"TRUE", "FALSE", "POW", "MULT", "DIV", "MOD", "PLUS", "MINUS", "GT", "GE", 
		"LT", "LE", "EQ", "NE", "LPAREN", "RPAREN", "COMMA", "MATCHES_NAME", "CLICK_NAME", 
		"TYPE_NAME", "DRAG_NAME", "ANY_NAME", "DOUBLE_CLICK_NAME", "TRIPLE_CLICK_NAME", 
		"RIGHT_CLICK_NAME", "MOUSE_MOVE_NAME", "DROP_DOWN_AT_NAME", "BOOLEAN_VARIABLE", 
		"NUMBER_VARIABLE", "STRING_VARIABLE", "EOL", "WS", "OTHER", "BOOLEAN_VARIABLE_NAME", 
		"NUMBER_VARIABLE_NAME", "STRING_VARIABLE_NAME"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "WidgetConditionParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public WidgetConditionParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Widget_conditionContext extends ParserRuleContext {
		public Widget_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_widget_condition; }
	 
		public Widget_conditionContext() { }
		public void copyFrom(Widget_conditionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LogicalEntityContext extends Widget_conditionContext {
		public Logical_entityContext logical_entity() {
			return getRuleContext(Logical_entityContext.class,0);
		}
		public LogicalEntityContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitLogicalEntity(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelationalExpressionContext extends Widget_conditionContext {
		public Relational_exprContext relational_expr() {
			return getRuleContext(Relational_exprContext.class,0);
		}
		public RelationalExpressionContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitRelationalExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetConditionOrContext extends Widget_conditionContext {
		public Widget_conditionContext left;
		public Widget_conditionContext right;
		public TerminalNode OR() { return getToken(WidgetConditionParser.OR, 0); }
		public List<Widget_conditionContext> widget_condition() {
			return getRuleContexts(Widget_conditionContext.class);
		}
		public Widget_conditionContext widget_condition(int i) {
			return getRuleContext(Widget_conditionContext.class,i);
		}
		public WidgetConditionOrContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitWidgetConditionOr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NegationWidgetConditionContext extends Widget_conditionContext {
		public TerminalNode NOT() { return getToken(WidgetConditionParser.NOT, 0); }
		public Widget_conditionContext widget_condition() {
			return getRuleContext(Widget_conditionContext.class,0);
		}
		public NegationWidgetConditionContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitNegationWidgetCondition(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetConditionInParenContext extends Widget_conditionContext {
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public Widget_conditionContext widget_condition() {
			return getRuleContext(Widget_conditionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public WidgetConditionInParenContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitWidgetConditionInParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetConditionAndContext extends Widget_conditionContext {
		public Widget_conditionContext left;
		public Widget_conditionContext right;
		public TerminalNode AND() { return getToken(WidgetConditionParser.AND, 0); }
		public List<Widget_conditionContext> widget_condition() {
			return getRuleContexts(Widget_conditionContext.class);
		}
		public Widget_conditionContext widget_condition(int i) {
			return getRuleContext(Widget_conditionContext.class,i);
		}
		public WidgetConditionAndContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitWidgetConditionAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Widget_conditionContext widget_condition() throws RecognitionException {
		return widget_condition(0);
	}

	private Widget_conditionContext widget_condition(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Widget_conditionContext _localctx = new Widget_conditionContext(_ctx, _parentState);
		Widget_conditionContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_widget_condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(31);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				_localctx = new NegationWidgetConditionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(23);
				match(NOT);
				setState(24);
				widget_condition(4);
				}
				break;
			case 2:
				{
				_localctx = new LogicalEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(25);
				logical_entity();
				}
				break;
			case 3:
				{
				_localctx = new WidgetConditionInParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(26);
				match(LPAREN);
				setState(27);
				widget_condition(0);
				setState(28);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new RelationalExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(30);
				relational_expr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(41);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(39);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetConditionAndContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(33);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(34);
						match(AND);
						setState(35);
						((WidgetConditionAndContext)_localctx).right = widget_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetConditionOrContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(36);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(37);
						match(OR);
						setState(38);
						((WidgetConditionOrContext)_localctx).right = widget_condition(2);
						}
						break;
					}
					} 
				}
				setState(43);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Relational_exprContext extends ParserRuleContext {
		public Relational_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_expr; }
	 
		public Relational_exprContext() { }
		public void copyFrom(Relational_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RelationalNumericExpressionWithOperatorContext extends Relational_exprContext {
		public Arithmetic_exprContext left;
		public Arithmetic_exprContext right;
		public Relational_operatorContext relational_operator() {
			return getRuleContext(Relational_operatorContext.class,0);
		}
		public List<Arithmetic_exprContext> arithmetic_expr() {
			return getRuleContexts(Arithmetic_exprContext.class);
		}
		public Arithmetic_exprContext arithmetic_expr(int i) {
			return getRuleContext(Arithmetic_exprContext.class,i);
		}
		public RelationalNumericExpressionWithOperatorContext(Relational_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitRelationalNumericExpressionWithOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelationalStringExpressionWithOperatorContext extends Relational_exprContext {
		public String_exprContext left;
		public String_exprContext right;
		public Relational_operatorContext relational_operator() {
			return getRuleContext(Relational_operatorContext.class,0);
		}
		public List<String_exprContext> string_expr() {
			return getRuleContexts(String_exprContext.class);
		}
		public String_exprContext string_expr(int i) {
			return getRuleContext(String_exprContext.class,i);
		}
		public RelationalStringExpressionWithOperatorContext(Relational_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitRelationalStringExpressionWithOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelationalExpressionParensContext extends Relational_exprContext {
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public Relational_exprContext relational_expr() {
			return getRuleContext(Relational_exprContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public RelationalExpressionParensContext(Relational_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitRelationalExpressionParens(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Relational_exprContext relational_expr() throws RecognitionException {
		Relational_exprContext _localctx = new Relational_exprContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_relational_expr);
		try {
			setState(56);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new RelationalNumericExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(44);
				((RelationalNumericExpressionWithOperatorContext)_localctx).left = arithmetic_expr(0);
				setState(45);
				relational_operator();
				setState(46);
				((RelationalNumericExpressionWithOperatorContext)_localctx).right = arithmetic_expr(0);
				}
				break;
			case 2:
				_localctx = new RelationalStringExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(48);
				((RelationalStringExpressionWithOperatorContext)_localctx).left = string_expr();
				setState(49);
				relational_operator();
				setState(50);
				((RelationalStringExpressionWithOperatorContext)_localctx).right = string_expr();
				}
				break;
			case 3:
				_localctx = new RelationalExpressionParensContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(52);
				match(LPAREN);
				setState(53);
				relational_expr();
				setState(54);
				match(RPAREN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Relational_operatorContext extends ParserRuleContext {
		public TerminalNode GT() { return getToken(WidgetConditionParser.GT, 0); }
		public TerminalNode GE() { return getToken(WidgetConditionParser.GE, 0); }
		public TerminalNode LT() { return getToken(WidgetConditionParser.LT, 0); }
		public TerminalNode LE() { return getToken(WidgetConditionParser.LE, 0); }
		public TerminalNode EQ() { return getToken(WidgetConditionParser.EQ, 0); }
		public TerminalNode NE() { return getToken(WidgetConditionParser.NE, 0); }
		public Relational_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_operator; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitRelational_operator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Relational_operatorContext relational_operator() throws RecognitionException {
		Relational_operatorContext _localctx = new Relational_operatorContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_relational_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Arithmetic_exprContext extends ParserRuleContext {
		public Arithmetic_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arithmetic_expr; }
	 
		public Arithmetic_exprContext() { }
		public void copyFrom(Arithmetic_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ArithmeticExpressionPowContext extends Arithmetic_exprContext {
		public Arithmetic_exprContext left;
		public Arithmetic_exprContext right;
		public TerminalNode POW() { return getToken(WidgetConditionParser.POW, 0); }
		public List<Arithmetic_exprContext> arithmetic_expr() {
			return getRuleContexts(Arithmetic_exprContext.class);
		}
		public Arithmetic_exprContext arithmetic_expr(int i) {
			return getRuleContext(Arithmetic_exprContext.class,i);
		}
		public ArithmeticExpressionPowContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitArithmeticExpressionPow(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionParensContext extends Arithmetic_exprContext {
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public Arithmetic_exprContext arithmetic_expr() {
			return getRuleContext(Arithmetic_exprContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public ArithmeticExpressionParensContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitArithmeticExpressionParens(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionNumericEntityContext extends Arithmetic_exprContext {
		public Numeric_entityContext numeric_entity() {
			return getRuleContext(Numeric_entityContext.class,0);
		}
		public ArithmeticExpressionNumericEntityContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitArithmeticExpressionNumericEntity(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionMultDivModContext extends Arithmetic_exprContext {
		public Arithmetic_exprContext left;
		public Arithmetic_exprContext right;
		public List<Arithmetic_exprContext> arithmetic_expr() {
			return getRuleContexts(Arithmetic_exprContext.class);
		}
		public Arithmetic_exprContext arithmetic_expr(int i) {
			return getRuleContext(Arithmetic_exprContext.class,i);
		}
		public TerminalNode MULT() { return getToken(WidgetConditionParser.MULT, 0); }
		public TerminalNode DIV() { return getToken(WidgetConditionParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(WidgetConditionParser.MOD, 0); }
		public ArithmeticExpressionMultDivModContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitArithmeticExpressionMultDivMod(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionNegationContext extends Arithmetic_exprContext {
		public TerminalNode MINUS() { return getToken(WidgetConditionParser.MINUS, 0); }
		public Arithmetic_exprContext arithmetic_expr() {
			return getRuleContext(Arithmetic_exprContext.class,0);
		}
		public ArithmeticExpressionNegationContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitArithmeticExpressionNegation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionPlusMinusContext extends Arithmetic_exprContext {
		public Arithmetic_exprContext left;
		public Arithmetic_exprContext right;
		public List<Arithmetic_exprContext> arithmetic_expr() {
			return getRuleContexts(Arithmetic_exprContext.class);
		}
		public Arithmetic_exprContext arithmetic_expr(int i) {
			return getRuleContext(Arithmetic_exprContext.class,i);
		}
		public TerminalNode PLUS() { return getToken(WidgetConditionParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(WidgetConditionParser.MINUS, 0); }
		public ArithmeticExpressionPlusMinusContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitArithmeticExpressionPlusMinus(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Arithmetic_exprContext arithmetic_expr() throws RecognitionException {
		return arithmetic_expr(0);
	}

	private Arithmetic_exprContext arithmetic_expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Arithmetic_exprContext _localctx = new Arithmetic_exprContext(_ctx, _parentState);
		Arithmetic_exprContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_arithmetic_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			switch (_input.LA(1)) {
			case MINUS:
				{
				_localctx = new ArithmeticExpressionNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(61);
				match(MINUS);
				setState(62);
				arithmetic_expr(4);
				}
				break;
			case DECIMAL_NUMBER:
			case INTEGER_NUMBER:
			case PLACEHOLDER:
			case NUMBER_VARIABLE:
				{
				_localctx = new ArithmeticExpressionNumericEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(63);
				numeric_entity();
				}
				break;
			case LPAREN:
				{
				_localctx = new ArithmeticExpressionParensContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(64);
				match(LPAREN);
				setState(65);
				arithmetic_expr(0);
				setState(66);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(81);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(79);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticExpressionPowContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPowContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(70);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(71);
						match(POW);
						setState(72);
						((ArithmeticExpressionPowContext)_localctx).right = arithmetic_expr(4);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticExpressionMultDivModContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionMultDivModContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(73);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(74);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(75);
						((ArithmeticExpressionMultDivModContext)_localctx).right = arithmetic_expr(3);
						}
						break;
					case 3:
						{
						_localctx = new ArithmeticExpressionPlusMinusContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPlusMinusContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(76);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(77);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(78);
						((ArithmeticExpressionPlusMinusContext)_localctx).right = arithmetic_expr(2);
						}
						break;
					}
					} 
				}
				setState(83);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class String_exprContext extends ParserRuleContext {
		public String_entityContext string_entity() {
			return getRuleContext(String_entityContext.class,0);
		}
		public String_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_expr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitString_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final String_exprContext string_expr() throws RecognitionException {
		String_exprContext _localctx = new String_exprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_string_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			string_entity();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BooleanFunctionContext extends ParserRuleContext {
		public MatchesFunctionContext matchesFunction() {
			return getRuleContext(MatchesFunctionContext.class,0);
		}
		public BooleanFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitBooleanFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanFunctionContext booleanFunction() throws RecognitionException {
		BooleanFunctionContext _localctx = new BooleanFunctionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_booleanFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			matchesFunction();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MatchesFunctionContext extends ParserRuleContext {
		public TerminalNode MATCHES_NAME() { return getToken(WidgetConditionParser.MATCHES_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public TerminalNode STRING_VARIABLE() { return getToken(WidgetConditionParser.STRING_VARIABLE, 0); }
		public TerminalNode COMMA() { return getToken(WidgetConditionParser.COMMA, 0); }
		public TerminalNode STRING() { return getToken(WidgetConditionParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public MatchesFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchesFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitMatchesFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchesFunctionContext matchesFunction() throws RecognitionException {
		MatchesFunctionContext _localctx = new MatchesFunctionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_matchesFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(MATCHES_NAME);
			setState(89);
			match(LPAREN);
			setState(90);
			match(STRING_VARIABLE);
			setState(91);
			match(COMMA);
			setState(92);
			match(STRING);
			setState(93);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(WidgetConditionParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(WidgetConditionParser.FALSE, 0); }
		public BoolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bool; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitBool(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolContext bool() throws RecognitionException {
		BoolContext _localctx = new BoolContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_bool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Logical_entityContext extends ParserRuleContext {
		public Logical_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logical_entity; }
	 
		public Logical_entityContext() { }
		public void copyFrom(Logical_entityContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LogicalVariableContext extends Logical_entityContext {
		public TerminalNode BOOLEAN_VARIABLE() { return getToken(WidgetConditionParser.BOOLEAN_VARIABLE, 0); }
		public LogicalVariableContext(Logical_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitLogicalVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalFunctionContext extends Logical_entityContext {
		public BooleanFunctionContext booleanFunction() {
			return getRuleContext(BooleanFunctionContext.class,0);
		}
		public LogicalFunctionContext(Logical_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitLogicalFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalPlaceholderContext extends Logical_entityContext {
		public TerminalNode PLACEHOLDER() { return getToken(WidgetConditionParser.PLACEHOLDER, 0); }
		public LogicalPlaceholderContext(Logical_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitLogicalPlaceholder(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalConstContext extends Logical_entityContext {
		public BoolContext bool() {
			return getRuleContext(BoolContext.class,0);
		}
		public LogicalConstContext(Logical_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitLogicalConst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logical_entityContext logical_entity() throws RecognitionException {
		Logical_entityContext _localctx = new Logical_entityContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_logical_entity);
		try {
			setState(101);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
				_localctx = new LogicalConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(97);
				bool();
				}
				break;
			case BOOLEAN_VARIABLE:
				_localctx = new LogicalVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(98);
				match(BOOLEAN_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new LogicalPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(99);
				match(PLACEHOLDER);
				}
				break;
			case MATCHES_NAME:
				_localctx = new LogicalFunctionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(100);
				booleanFunction();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Numeric_entityContext extends ParserRuleContext {
		public Numeric_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numeric_entity; }
	 
		public Numeric_entityContext() { }
		public void copyFrom(Numeric_entityContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IntegerConstContext extends Numeric_entityContext {
		public TerminalNode INTEGER_NUMBER() { return getToken(WidgetConditionParser.INTEGER_NUMBER, 0); }
		public IntegerConstContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitIntegerConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DecimalConstContext extends Numeric_entityContext {
		public TerminalNode DECIMAL_NUMBER() { return getToken(WidgetConditionParser.DECIMAL_NUMBER, 0); }
		public DecimalConstContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitDecimalConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericVariableContext extends Numeric_entityContext {
		public TerminalNode NUMBER_VARIABLE() { return getToken(WidgetConditionParser.NUMBER_VARIABLE, 0); }
		public NumericVariableContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitNumericVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericPlaceholderContext extends Numeric_entityContext {
		public TerminalNode PLACEHOLDER() { return getToken(WidgetConditionParser.PLACEHOLDER, 0); }
		public NumericPlaceholderContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitNumericPlaceholder(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Numeric_entityContext numeric_entity() throws RecognitionException {
		Numeric_entityContext _localctx = new Numeric_entityContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_numeric_entity);
		try {
			setState(107);
			switch (_input.LA(1)) {
			case INTEGER_NUMBER:
				_localctx = new IntegerConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(103);
				match(INTEGER_NUMBER);
				}
				break;
			case DECIMAL_NUMBER:
				_localctx = new DecimalConstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(104);
				match(DECIMAL_NUMBER);
				}
				break;
			case NUMBER_VARIABLE:
				_localctx = new NumericVariableContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(105);
				match(NUMBER_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new NumericPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(106);
				match(PLACEHOLDER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class String_entityContext extends ParserRuleContext {
		public String_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_entity; }
	 
		public String_entityContext() { }
		public void copyFrom(String_entityContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StringPlaceholderContext extends String_entityContext {
		public TerminalNode PLACEHOLDER() { return getToken(WidgetConditionParser.PLACEHOLDER, 0); }
		public StringPlaceholderContext(String_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitStringPlaceholder(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringConstContext extends String_entityContext {
		public TerminalNode STRING() { return getToken(WidgetConditionParser.STRING, 0); }
		public StringConstContext(String_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitStringConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringVariableContext extends String_entityContext {
		public TerminalNode STRING_VARIABLE() { return getToken(WidgetConditionParser.STRING_VARIABLE, 0); }
		public StringVariableContext(String_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitStringVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final String_entityContext string_entity() throws RecognitionException {
		String_entityContext _localctx = new String_entityContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_string_entity);
		try {
			setState(112);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(109);
				match(STRING);
				}
				break;
			case STRING_VARIABLE:
				_localctx = new StringVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(110);
				match(STRING_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new StringPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(111);
				match(PLACEHOLDER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return widget_condition_sempred((Widget_conditionContext)_localctx, predIndex);
		case 3:
			return arithmetic_expr_sempred((Arithmetic_exprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean widget_condition_sempred(Widget_conditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean arithmetic_expr_sempred(Arithmetic_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 3);
		case 3:
			return precpred(_ctx, 2);
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3@u\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f"+
		"\t\f\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\"\n\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\7\2*\n\2\f\2\16\2-\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\5\3;\n\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5G\n\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5R\n\5\f\5\16\5U\13\5\3\6\3\6\3\7\3\7"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\5\nh\n\n\3\13\3\13"+
		"\3\13\3\13\5\13n\n\13\3\f\3\f\3\f\5\fs\n\f\3\f\2\4\2\b\r\2\4\6\b\n\f\16"+
		"\20\22\24\26\2\6\3\2%*\3\2 \"\3\2#$\3\2\35\36}\2!\3\2\2\2\4:\3\2\2\2\6"+
		"<\3\2\2\2\bF\3\2\2\2\nV\3\2\2\2\fX\3\2\2\2\16Z\3\2\2\2\20a\3\2\2\2\22"+
		"g\3\2\2\2\24m\3\2\2\2\26r\3\2\2\2\30\31\b\2\1\2\31\32\7\34\2\2\32\"\5"+
		"\2\2\6\33\"\5\22\n\2\34\35\7+\2\2\35\36\5\2\2\2\36\37\7,\2\2\37\"\3\2"+
		"\2\2 \"\5\4\3\2!\30\3\2\2\2!\33\3\2\2\2!\34\3\2\2\2! \3\2\2\2\"+\3\2\2"+
		"\2#$\f\4\2\2$%\7\32\2\2%*\5\2\2\5&\'\f\3\2\2\'(\7\33\2\2(*\5\2\2\4)#\3"+
		"\2\2\2)&\3\2\2\2*-\3\2\2\2+)\3\2\2\2+,\3\2\2\2,\3\3\2\2\2-+\3\2\2\2./"+
		"\5\b\5\2/\60\5\6\4\2\60\61\5\b\5\2\61;\3\2\2\2\62\63\5\n\6\2\63\64\5\6"+
		"\4\2\64\65\5\n\6\2\65;\3\2\2\2\66\67\7+\2\2\678\5\4\3\289\7,\2\29;\3\2"+
		"\2\2:.\3\2\2\2:\62\3\2\2\2:\66\3\2\2\2;\5\3\2\2\2<=\t\2\2\2=\7\3\2\2\2"+
		">?\b\5\1\2?@\7$\2\2@G\5\b\5\6AG\5\24\13\2BC\7+\2\2CD\5\b\5\2DE\7,\2\2"+
		"EG\3\2\2\2F>\3\2\2\2FA\3\2\2\2FB\3\2\2\2GS\3\2\2\2HI\f\5\2\2IJ\7\37\2"+
		"\2JR\5\b\5\6KL\f\4\2\2LM\t\3\2\2MR\5\b\5\5NO\f\3\2\2OP\t\4\2\2PR\5\b\5"+
		"\4QH\3\2\2\2QK\3\2\2\2QN\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2T\t\3\2"+
		"\2\2US\3\2\2\2VW\5\26\f\2W\13\3\2\2\2XY\5\16\b\2Y\r\3\2\2\2Z[\7.\2\2["+
		"\\\7+\2\2\\]\7:\2\2]^\7-\2\2^_\7\30\2\2_`\7,\2\2`\17\3\2\2\2ab\t\5\2\2"+
		"b\21\3\2\2\2ch\5\20\t\2dh\78\2\2eh\7\27\2\2fh\5\f\7\2gc\3\2\2\2gd\3\2"+
		"\2\2ge\3\2\2\2gf\3\2\2\2h\23\3\2\2\2in\7\26\2\2jn\7\25\2\2kn\79\2\2ln"+
		"\7\27\2\2mi\3\2\2\2mj\3\2\2\2mk\3\2\2\2ml\3\2\2\2n\25\3\2\2\2os\7\30\2"+
		"\2ps\7:\2\2qs\7\27\2\2ro\3\2\2\2rp\3\2\2\2rq\3\2\2\2s\27\3\2\2\2\f!)+"+
		":FQSgmr";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
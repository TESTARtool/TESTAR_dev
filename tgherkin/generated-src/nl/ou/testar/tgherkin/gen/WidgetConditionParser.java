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
		STEP_RANGE_KEYWORD=12, STEP_WHILE_KEYWORD=13, STEP_REPEAT_KEYWORD=14, 
		STEP_UNTIL_KEYWORD=15, STEP_NOP_KEYWORD=16, STEP_GIVEN_KEYWORD=17, STEP_WHEN_KEYWORD=18, 
		STEP_THEN_KEYWORD=19, STEP_ALSO_KEYWORD=20, STEP_EITHER_KEYWORD=21, TABLE_ROW=22, 
		DECIMAL_NUMBER=23, INTEGER_NUMBER=24, PLACEHOLDER=25, STRING=26, COMMENT=27, 
		AND=28, OR=29, NOT=30, TRUE=31, FALSE=32, POW=33, MULT=34, DIV=35, MOD=36, 
		PLUS=37, MINUS=38, GT=39, GE=40, LT=41, LE=42, EQ=43, NE=44, LPAREN=45, 
		RPAREN=46, COMMA=47, MATCHES_NAME=48, XPATH_NAME=49, XPATH_BOOLEAN_NAME=50, 
		XPATH_NUMBER_NAME=51, XPATH_STRING_NAME=52, IMAGE_NAME=53, OCR_NAME=54, 
		STATE_NAME=55, CLICK_NAME=56, TYPE_NAME=57, DRAG_SLIDER_NAME=58, ANY_NAME=59, 
		DOUBLE_CLICK_NAME=60, TRIPLE_CLICK_NAME=61, RIGHT_CLICK_NAME=62, MOUSE_MOVE_NAME=63, 
		DROP_DOWN_AT_NAME=64, HIT_KEY_NAME=65, DRAG_DROP_NAME=66, BOOLEAN_VARIABLE=67, 
		NUMBER_VARIABLE=68, STRING_VARIABLE=69, EOL=70, WS=71, OTHER=72, BOOLEAN_VARIABLE_NAME=73, 
		NUMBER_VARIABLE_NAME=74, STRING_VARIABLE_NAME=75, KB_KEY_NAME=76;
	public static final int
		RULE_widget_condition = 0, RULE_relational_expr = 1, RULE_relational_operator = 2, 
		RULE_arithmetic_expr = 3, RULE_string_expr = 4, RULE_booleanFunction = 5, 
		RULE_stringFunction = 6, RULE_numericFunction = 7, RULE_matchesFunction = 8, 
		RULE_xpathFunction = 9, RULE_xpathBooleanFunction = 10, RULE_xpathNumberFunction = 11, 
		RULE_xpathStringFunction = 12, RULE_imageFunction = 13, RULE_ocrFunction = 14, 
		RULE_stateFunction = 15, RULE_widget_tree_condition = 16, RULE_bool = 17, 
		RULE_logical_entity = 18, RULE_numeric_entity = 19, RULE_string_entity = 20;
	public static final String[] ruleNames = {
		"widget_condition", "relational_expr", "relational_operator", "arithmetic_expr", 
		"string_expr", "booleanFunction", "stringFunction", "numericFunction", 
		"matchesFunction", "xpathFunction", "xpathBooleanFunction", "xpathNumberFunction", 
		"xpathStringFunction", "imageFunction", "ocrFunction", "stateFunction", 
		"widget_tree_condition", "bool", "logical_entity", "numeric_entity", "string_entity"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'include:'", "'exclude:'", null, "'Feature:'", "'Background:'", 
		"'Scenario:'", "'Scenario Outline:'", "'Examples:'", "'Selection:'", "'Oracle:'", 
		"'Step:'", "'Range'", "'While'", "'Repeat'", "'until'", "'NOP'", "'Given'", 
		"'When'", "'Then'", "'Also'", "'Either'", null, null, null, null, null, 
		null, "'and'", "'or'", null, "'true'", "'false'", "'^'", "'*'", "'/'", 
		"'%'", "'+'", "'-'", "'>'", "'>='", "'<'", "'<='", "'='", null, "'('", 
		"')'", "','", "'matches'", "'xpath'", "'xpathBoolean'", "'xpathNumber'", 
		"'xpathString'", "'image'", "'ocr'", "'state'", "'click'", "'type'", "'dragSlider'", 
		"'anyGesture'", "'doubleClick'", "'tripleClick'", "'rightClick'", "'mouseMove'", 
		"'dropDownAt'", "'hitKey'", "'dragDrop'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "OPTION_KEYWORD_INCLUDE", "OPTION_KEYWORD_EXCLUDE", "TAGNAME", "FEATURE_KEYWORD", 
		"BACKGROUND_KEYWORD", "SCENARIO_KEYWORD", "SCENARIO_OUTLINE_KEYWORD", 
		"EXAMPLES_KEYWORD", "SELECTION_KEYWORD", "ORACLE_KEYWORD", "STEP_KEYWORD", 
		"STEP_RANGE_KEYWORD", "STEP_WHILE_KEYWORD", "STEP_REPEAT_KEYWORD", "STEP_UNTIL_KEYWORD", 
		"STEP_NOP_KEYWORD", "STEP_GIVEN_KEYWORD", "STEP_WHEN_KEYWORD", "STEP_THEN_KEYWORD", 
		"STEP_ALSO_KEYWORD", "STEP_EITHER_KEYWORD", "TABLE_ROW", "DECIMAL_NUMBER", 
		"INTEGER_NUMBER", "PLACEHOLDER", "STRING", "COMMENT", "AND", "OR", "NOT", 
		"TRUE", "FALSE", "POW", "MULT", "DIV", "MOD", "PLUS", "MINUS", "GT", "GE", 
		"LT", "LE", "EQ", "NE", "LPAREN", "RPAREN", "COMMA", "MATCHES_NAME", "XPATH_NAME", 
		"XPATH_BOOLEAN_NAME", "XPATH_NUMBER_NAME", "XPATH_STRING_NAME", "IMAGE_NAME", 
		"OCR_NAME", "STATE_NAME", "CLICK_NAME", "TYPE_NAME", "DRAG_SLIDER_NAME", 
		"ANY_NAME", "DOUBLE_CLICK_NAME", "TRIPLE_CLICK_NAME", "RIGHT_CLICK_NAME", 
		"MOUSE_MOVE_NAME", "DROP_DOWN_AT_NAME", "HIT_KEY_NAME", "DRAG_DROP_NAME", 
		"BOOLEAN_VARIABLE", "NUMBER_VARIABLE", "STRING_VARIABLE", "EOL", "WS", 
		"OTHER", "BOOLEAN_VARIABLE_NAME", "NUMBER_VARIABLE_NAME", "STRING_VARIABLE_NAME", 
		"KB_KEY_NAME"
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
			setState(51);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				_localctx = new NegationWidgetConditionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(43);
				match(NOT);
				setState(44);
				widget_condition(4);
				}
				break;
			case 2:
				{
				_localctx = new LogicalEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(45);
				logical_entity();
				}
				break;
			case 3:
				{
				_localctx = new WidgetConditionInParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(46);
				match(LPAREN);
				setState(47);
				widget_condition(0);
				setState(48);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new RelationalExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(50);
				relational_expr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(61);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(59);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetConditionAndContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(53);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(54);
						match(AND);
						setState(55);
						((WidgetConditionAndContext)_localctx).right = widget_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetConditionOrContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(56);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(57);
						match(OR);
						setState(58);
						((WidgetConditionOrContext)_localctx).right = widget_condition(2);
						}
						break;
					}
					} 
				}
				setState(63);
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
			setState(76);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new RelationalNumericExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(64);
				((RelationalNumericExpressionWithOperatorContext)_localctx).left = arithmetic_expr(0);
				setState(65);
				relational_operator();
				setState(66);
				((RelationalNumericExpressionWithOperatorContext)_localctx).right = arithmetic_expr(0);
				}
				break;
			case 2:
				_localctx = new RelationalStringExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(68);
				((RelationalStringExpressionWithOperatorContext)_localctx).left = string_expr();
				setState(69);
				relational_operator();
				setState(70);
				((RelationalStringExpressionWithOperatorContext)_localctx).right = string_expr();
				}
				break;
			case 3:
				_localctx = new RelationalExpressionParensContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(72);
				match(LPAREN);
				setState(73);
				relational_expr();
				setState(74);
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
			setState(78);
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
			setState(88);
			switch (_input.LA(1)) {
			case MINUS:
				{
				_localctx = new ArithmeticExpressionNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(81);
				match(MINUS);
				setState(82);
				arithmetic_expr(4);
				}
				break;
			case DECIMAL_NUMBER:
			case INTEGER_NUMBER:
			case PLACEHOLDER:
			case XPATH_NUMBER_NAME:
			case NUMBER_VARIABLE:
				{
				_localctx = new ArithmeticExpressionNumericEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(83);
				numeric_entity();
				}
				break;
			case LPAREN:
				{
				_localctx = new ArithmeticExpressionParensContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(84);
				match(LPAREN);
				setState(85);
				arithmetic_expr(0);
				setState(86);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(101);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(99);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticExpressionPowContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPowContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(90);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(91);
						match(POW);
						setState(92);
						((ArithmeticExpressionPowContext)_localctx).right = arithmetic_expr(4);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticExpressionMultDivModContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionMultDivModContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(93);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(94);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(95);
						((ArithmeticExpressionMultDivModContext)_localctx).right = arithmetic_expr(3);
						}
						break;
					case 3:
						{
						_localctx = new ArithmeticExpressionPlusMinusContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPlusMinusContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(96);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(97);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(98);
						((ArithmeticExpressionPlusMinusContext)_localctx).right = arithmetic_expr(2);
						}
						break;
					}
					} 
				}
				setState(103);
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
			setState(104);
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
		public XpathFunctionContext xpathFunction() {
			return getRuleContext(XpathFunctionContext.class,0);
		}
		public XpathBooleanFunctionContext xpathBooleanFunction() {
			return getRuleContext(XpathBooleanFunctionContext.class,0);
		}
		public ImageFunctionContext imageFunction() {
			return getRuleContext(ImageFunctionContext.class,0);
		}
		public StateFunctionContext stateFunction() {
			return getRuleContext(StateFunctionContext.class,0);
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
			setState(111);
			switch (_input.LA(1)) {
			case MATCHES_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(106);
				matchesFunction();
				}
				break;
			case XPATH_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(107);
				xpathFunction();
				}
				break;
			case XPATH_BOOLEAN_NAME:
				enterOuterAlt(_localctx, 3);
				{
				setState(108);
				xpathBooleanFunction();
				}
				break;
			case IMAGE_NAME:
				enterOuterAlt(_localctx, 4);
				{
				setState(109);
				imageFunction();
				}
				break;
			case STATE_NAME:
				enterOuterAlt(_localctx, 5);
				{
				setState(110);
				stateFunction();
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

	public static class StringFunctionContext extends ParserRuleContext {
		public OcrFunctionContext ocrFunction() {
			return getRuleContext(OcrFunctionContext.class,0);
		}
		public XpathStringFunctionContext xpathStringFunction() {
			return getRuleContext(XpathStringFunctionContext.class,0);
		}
		public StringFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitStringFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringFunctionContext stringFunction() throws RecognitionException {
		StringFunctionContext _localctx = new StringFunctionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_stringFunction);
		try {
			setState(115);
			switch (_input.LA(1)) {
			case OCR_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(113);
				ocrFunction();
				}
				break;
			case XPATH_STRING_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(114);
				xpathStringFunction();
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

	public static class NumericFunctionContext extends ParserRuleContext {
		public XpathNumberFunctionContext xpathNumberFunction() {
			return getRuleContext(XpathNumberFunctionContext.class,0);
		}
		public NumericFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitNumericFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumericFunctionContext numericFunction() throws RecognitionException {
		NumericFunctionContext _localctx = new NumericFunctionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_numericFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			xpathNumberFunction();
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
		public String_entityContext string_entity() {
			return getRuleContext(String_entityContext.class,0);
		}
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
		enterRule(_localctx, 16, RULE_matchesFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(MATCHES_NAME);
			setState(120);
			match(LPAREN);
			setState(121);
			string_entity();
			setState(122);
			match(COMMA);
			setState(123);
			match(STRING);
			setState(124);
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

	public static class XpathFunctionContext extends ParserRuleContext {
		public TerminalNode XPATH_NAME() { return getToken(WidgetConditionParser.XPATH_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(WidgetConditionParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public XpathFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpathFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitXpathFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final XpathFunctionContext xpathFunction() throws RecognitionException {
		XpathFunctionContext _localctx = new XpathFunctionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_xpathFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(XPATH_NAME);
			setState(127);
			match(LPAREN);
			setState(128);
			match(STRING);
			setState(129);
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

	public static class XpathBooleanFunctionContext extends ParserRuleContext {
		public TerminalNode XPATH_BOOLEAN_NAME() { return getToken(WidgetConditionParser.XPATH_BOOLEAN_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(WidgetConditionParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public XpathBooleanFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpathBooleanFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitXpathBooleanFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final XpathBooleanFunctionContext xpathBooleanFunction() throws RecognitionException {
		XpathBooleanFunctionContext _localctx = new XpathBooleanFunctionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_xpathBooleanFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			match(XPATH_BOOLEAN_NAME);
			setState(132);
			match(LPAREN);
			setState(133);
			match(STRING);
			setState(134);
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

	public static class XpathNumberFunctionContext extends ParserRuleContext {
		public TerminalNode XPATH_NUMBER_NAME() { return getToken(WidgetConditionParser.XPATH_NUMBER_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(WidgetConditionParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public XpathNumberFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpathNumberFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitXpathNumberFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final XpathNumberFunctionContext xpathNumberFunction() throws RecognitionException {
		XpathNumberFunctionContext _localctx = new XpathNumberFunctionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_xpathNumberFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(XPATH_NUMBER_NAME);
			setState(137);
			match(LPAREN);
			setState(138);
			match(STRING);
			setState(139);
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

	public static class XpathStringFunctionContext extends ParserRuleContext {
		public TerminalNode XPATH_STRING_NAME() { return getToken(WidgetConditionParser.XPATH_STRING_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(WidgetConditionParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public XpathStringFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpathStringFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitXpathStringFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final XpathStringFunctionContext xpathStringFunction() throws RecognitionException {
		XpathStringFunctionContext _localctx = new XpathStringFunctionContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_xpathStringFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(XPATH_STRING_NAME);
			setState(142);
			match(LPAREN);
			setState(143);
			match(STRING);
			setState(144);
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

	public static class ImageFunctionContext extends ParserRuleContext {
		public TerminalNode IMAGE_NAME() { return getToken(WidgetConditionParser.IMAGE_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(WidgetConditionParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public ImageFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_imageFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitImageFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImageFunctionContext imageFunction() throws RecognitionException {
		ImageFunctionContext _localctx = new ImageFunctionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_imageFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(IMAGE_NAME);
			setState(147);
			match(LPAREN);
			setState(148);
			match(STRING);
			setState(149);
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

	public static class OcrFunctionContext extends ParserRuleContext {
		public TerminalNode OCR_NAME() { return getToken(WidgetConditionParser.OCR_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public OcrFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ocrFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitOcrFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OcrFunctionContext ocrFunction() throws RecognitionException {
		OcrFunctionContext _localctx = new OcrFunctionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_ocrFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			match(OCR_NAME);
			setState(152);
			match(LPAREN);
			setState(153);
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

	public static class StateFunctionContext extends ParserRuleContext {
		public TerminalNode STATE_NAME() { return getToken(WidgetConditionParser.STATE_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(WidgetConditionParser.LPAREN, 0); }
		public Widget_tree_conditionContext widget_tree_condition() {
			return getRuleContext(Widget_tree_conditionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(WidgetConditionParser.RPAREN, 0); }
		public StateFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stateFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitStateFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StateFunctionContext stateFunction() throws RecognitionException {
		StateFunctionContext _localctx = new StateFunctionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_stateFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(STATE_NAME);
			setState(156);
			match(LPAREN);
			setState(157);
			widget_tree_condition(0);
			setState(158);
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

	public static class Widget_tree_conditionContext extends ParserRuleContext {
		public Widget_tree_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_widget_tree_condition; }
	 
		public Widget_tree_conditionContext() { }
		public void copyFrom(Widget_tree_conditionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class WidgetConditionContext extends Widget_tree_conditionContext {
		public Widget_conditionContext widget_condition() {
			return getRuleContext(Widget_conditionContext.class,0);
		}
		public WidgetConditionContext(Widget_tree_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitWidgetCondition(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetTreeConditionEitherContext extends Widget_tree_conditionContext {
		public Widget_tree_conditionContext left;
		public Widget_tree_conditionContext right;
		public TerminalNode STEP_EITHER_KEYWORD() { return getToken(WidgetConditionParser.STEP_EITHER_KEYWORD, 0); }
		public List<Widget_tree_conditionContext> widget_tree_condition() {
			return getRuleContexts(Widget_tree_conditionContext.class);
		}
		public Widget_tree_conditionContext widget_tree_condition(int i) {
			return getRuleContext(Widget_tree_conditionContext.class,i);
		}
		public WidgetTreeConditionEitherContext(Widget_tree_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitWidgetTreeConditionEither(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetTreeConditionAlsoContext extends Widget_tree_conditionContext {
		public Widget_tree_conditionContext left;
		public Widget_tree_conditionContext right;
		public TerminalNode STEP_ALSO_KEYWORD() { return getToken(WidgetConditionParser.STEP_ALSO_KEYWORD, 0); }
		public List<Widget_tree_conditionContext> widget_tree_condition() {
			return getRuleContexts(Widget_tree_conditionContext.class);
		}
		public Widget_tree_conditionContext widget_tree_condition(int i) {
			return getRuleContext(Widget_tree_conditionContext.class,i);
		}
		public WidgetTreeConditionAlsoContext(Widget_tree_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitWidgetTreeConditionAlso(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Widget_tree_conditionContext widget_tree_condition() throws RecognitionException {
		return widget_tree_condition(0);
	}

	private Widget_tree_conditionContext widget_tree_condition(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Widget_tree_conditionContext _localctx = new Widget_tree_conditionContext(_ctx, _parentState);
		Widget_tree_conditionContext _prevctx = _localctx;
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_widget_tree_condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new WidgetConditionContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(161);
			widget_condition(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(171);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(169);
					switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetTreeConditionAlsoContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionAlsoContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(163);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(164);
						match(STEP_ALSO_KEYWORD);
						setState(165);
						((WidgetTreeConditionAlsoContext)_localctx).right = widget_tree_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetTreeConditionEitherContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionEitherContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(166);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(167);
						match(STEP_EITHER_KEYWORD);
						setState(168);
						((WidgetTreeConditionEitherContext)_localctx).right = widget_tree_condition(2);
						}
						break;
					}
					} 
				}
				setState(173);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
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
		enterRule(_localctx, 34, RULE_bool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
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
		enterRule(_localctx, 36, RULE_logical_entity);
		try {
			setState(180);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
				_localctx = new LogicalConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(176);
				bool();
				}
				break;
			case BOOLEAN_VARIABLE:
				_localctx = new LogicalVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(177);
				match(BOOLEAN_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new LogicalPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(178);
				match(PLACEHOLDER);
				}
				break;
			case MATCHES_NAME:
			case XPATH_NAME:
			case XPATH_BOOLEAN_NAME:
			case IMAGE_NAME:
			case STATE_NAME:
				_localctx = new LogicalFunctionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(179);
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
	public static class NumberFunctionContext extends Numeric_entityContext {
		public NumericFunctionContext numericFunction() {
			return getRuleContext(NumericFunctionContext.class,0);
		}
		public NumberFunctionContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitNumberFunction(this);
			else return visitor.visitChildren(this);
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
		enterRule(_localctx, 38, RULE_numeric_entity);
		try {
			setState(187);
			switch (_input.LA(1)) {
			case INTEGER_NUMBER:
				_localctx = new IntegerConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(182);
				match(INTEGER_NUMBER);
				}
				break;
			case DECIMAL_NUMBER:
				_localctx = new DecimalConstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(183);
				match(DECIMAL_NUMBER);
				}
				break;
			case NUMBER_VARIABLE:
				_localctx = new NumericVariableContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(184);
				match(NUMBER_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new NumericPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(185);
				match(PLACEHOLDER);
				}
				break;
			case XPATH_NUMBER_NAME:
				_localctx = new NumberFunctionContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(186);
				numericFunction();
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
	public static class StrFunctionContext extends String_entityContext {
		public StringFunctionContext stringFunction() {
			return getRuleContext(StringFunctionContext.class,0);
		}
		public StrFunctionContext(String_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WidgetConditionParserVisitor ) return ((WidgetConditionParserVisitor<? extends T>)visitor).visitStrFunction(this);
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
		enterRule(_localctx, 40, RULE_string_entity);
		try {
			setState(193);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(189);
				match(STRING);
				}
				break;
			case STRING_VARIABLE:
				_localctx = new StringVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(190);
				match(STRING_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new StringPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(191);
				match(PLACEHOLDER);
				}
				break;
			case XPATH_STRING_NAME:
			case OCR_NAME:
				_localctx = new StrFunctionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(192);
				stringFunction();
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
		case 16:
			return widget_tree_condition_sempred((Widget_tree_conditionContext)_localctx, predIndex);
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
	private boolean widget_tree_condition_sempred(Widget_tree_conditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 2);
		case 6:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3N\u00c6\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\5\2\66\n\2\3\2\3\2\3\2\3\2\3\2\3\2\7\2>\n\2\f\2\16\2A\13\2\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3O\n\3\3\4\3\4\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\5\5[\n\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\7\5f\n\5\f\5\16\5i\13\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\5\7r\n\7\3\b\3\b"+
		"\5\bv\n\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\7\22\u00ac\n\22\f\22\16\22\u00af"+
		"\13\22\3\23\3\23\3\24\3\24\3\24\3\24\5\24\u00b7\n\24\3\25\3\25\3\25\3"+
		"\25\3\25\5\25\u00be\n\25\3\26\3\26\3\26\3\26\5\26\u00c4\n\26\3\26\2\5"+
		"\2\b\"\27\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*\2\6\3\2).\3\2"+
		"$&\3\2\'(\3\2!\"\u00cd\2\65\3\2\2\2\4N\3\2\2\2\6P\3\2\2\2\bZ\3\2\2\2\n"+
		"j\3\2\2\2\fq\3\2\2\2\16u\3\2\2\2\20w\3\2\2\2\22y\3\2\2\2\24\u0080\3\2"+
		"\2\2\26\u0085\3\2\2\2\30\u008a\3\2\2\2\32\u008f\3\2\2\2\34\u0094\3\2\2"+
		"\2\36\u0099\3\2\2\2 \u009d\3\2\2\2\"\u00a2\3\2\2\2$\u00b0\3\2\2\2&\u00b6"+
		"\3\2\2\2(\u00bd\3\2\2\2*\u00c3\3\2\2\2,-\b\2\1\2-.\7 \2\2.\66\5\2\2\6"+
		"/\66\5&\24\2\60\61\7/\2\2\61\62\5\2\2\2\62\63\7\60\2\2\63\66\3\2\2\2\64"+
		"\66\5\4\3\2\65,\3\2\2\2\65/\3\2\2\2\65\60\3\2\2\2\65\64\3\2\2\2\66?\3"+
		"\2\2\2\678\f\4\2\289\7\36\2\29>\5\2\2\5:;\f\3\2\2;<\7\37\2\2<>\5\2\2\4"+
		"=\67\3\2\2\2=:\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@\3\3\2\2\2A?\3\2"+
		"\2\2BC\5\b\5\2CD\5\6\4\2DE\5\b\5\2EO\3\2\2\2FG\5\n\6\2GH\5\6\4\2HI\5\n"+
		"\6\2IO\3\2\2\2JK\7/\2\2KL\5\4\3\2LM\7\60\2\2MO\3\2\2\2NB\3\2\2\2NF\3\2"+
		"\2\2NJ\3\2\2\2O\5\3\2\2\2PQ\t\2\2\2Q\7\3\2\2\2RS\b\5\1\2ST\7(\2\2T[\5"+
		"\b\5\6U[\5(\25\2VW\7/\2\2WX\5\b\5\2XY\7\60\2\2Y[\3\2\2\2ZR\3\2\2\2ZU\3"+
		"\2\2\2ZV\3\2\2\2[g\3\2\2\2\\]\f\5\2\2]^\7#\2\2^f\5\b\5\6_`\f\4\2\2`a\t"+
		"\3\2\2af\5\b\5\5bc\f\3\2\2cd\t\4\2\2df\5\b\5\4e\\\3\2\2\2e_\3\2\2\2eb"+
		"\3\2\2\2fi\3\2\2\2ge\3\2\2\2gh\3\2\2\2h\t\3\2\2\2ig\3\2\2\2jk\5*\26\2"+
		"k\13\3\2\2\2lr\5\22\n\2mr\5\24\13\2nr\5\26\f\2or\5\34\17\2pr\5 \21\2q"+
		"l\3\2\2\2qm\3\2\2\2qn\3\2\2\2qo\3\2\2\2qp\3\2\2\2r\r\3\2\2\2sv\5\36\20"+
		"\2tv\5\32\16\2us\3\2\2\2ut\3\2\2\2v\17\3\2\2\2wx\5\30\r\2x\21\3\2\2\2"+
		"yz\7\62\2\2z{\7/\2\2{|\5*\26\2|}\7\61\2\2}~\7\34\2\2~\177\7\60\2\2\177"+
		"\23\3\2\2\2\u0080\u0081\7\63\2\2\u0081\u0082\7/\2\2\u0082\u0083\7\34\2"+
		"\2\u0083\u0084\7\60\2\2\u0084\25\3\2\2\2\u0085\u0086\7\64\2\2\u0086\u0087"+
		"\7/\2\2\u0087\u0088\7\34\2\2\u0088\u0089\7\60\2\2\u0089\27\3\2\2\2\u008a"+
		"\u008b\7\65\2\2\u008b\u008c\7/\2\2\u008c\u008d\7\34\2\2\u008d\u008e\7"+
		"\60\2\2\u008e\31\3\2\2\2\u008f\u0090\7\66\2\2\u0090\u0091\7/\2\2\u0091"+
		"\u0092\7\34\2\2\u0092\u0093\7\60\2\2\u0093\33\3\2\2\2\u0094\u0095\7\67"+
		"\2\2\u0095\u0096\7/\2\2\u0096\u0097\7\34\2\2\u0097\u0098\7\60\2\2\u0098"+
		"\35\3\2\2\2\u0099\u009a\78\2\2\u009a\u009b\7/\2\2\u009b\u009c\7\60\2\2"+
		"\u009c\37\3\2\2\2\u009d\u009e\79\2\2\u009e\u009f\7/\2\2\u009f\u00a0\5"+
		"\"\22\2\u00a0\u00a1\7\60\2\2\u00a1!\3\2\2\2\u00a2\u00a3\b\22\1\2\u00a3"+
		"\u00a4\5\2\2\2\u00a4\u00ad\3\2\2\2\u00a5\u00a6\f\4\2\2\u00a6\u00a7\7\26"+
		"\2\2\u00a7\u00ac\5\"\22\5\u00a8\u00a9\f\3\2\2\u00a9\u00aa\7\27\2\2\u00aa"+
		"\u00ac\5\"\22\4\u00ab\u00a5\3\2\2\2\u00ab\u00a8\3\2\2\2\u00ac\u00af\3"+
		"\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae#\3\2\2\2\u00af\u00ad"+
		"\3\2\2\2\u00b0\u00b1\t\5\2\2\u00b1%\3\2\2\2\u00b2\u00b7\5$\23\2\u00b3"+
		"\u00b7\7E\2\2\u00b4\u00b7\7\33\2\2\u00b5\u00b7\5\f\7\2\u00b6\u00b2\3\2"+
		"\2\2\u00b6\u00b3\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b5\3\2\2\2\u00b7"+
		"\'\3\2\2\2\u00b8\u00be\7\32\2\2\u00b9\u00be\7\31\2\2\u00ba\u00be\7F\2"+
		"\2\u00bb\u00be\7\33\2\2\u00bc\u00be\5\20\t\2\u00bd\u00b8\3\2\2\2\u00bd"+
		"\u00b9\3\2\2\2\u00bd\u00ba\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00bc\3\2"+
		"\2\2\u00be)\3\2\2\2\u00bf\u00c4\7\34\2\2\u00c0\u00c4\7G\2\2\u00c1\u00c4"+
		"\7\33\2\2\u00c2\u00c4\5\16\b\2\u00c3\u00bf\3\2\2\2\u00c3\u00c0\3\2\2\2"+
		"\u00c3\u00c1\3\2\2\2\u00c3\u00c2\3\2\2\2\u00c4+\3\2\2\2\20\65=?NZegqu"+
		"\u00ab\u00ad\u00b6\u00bd\u00c3";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
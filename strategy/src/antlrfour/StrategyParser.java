// Generated from C:/Users/lh3/IdeaProjects/TESTAR/strategy/src/antlrfour\Strategy.g4 by ANTLR 4.9.2
package antlrfour;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class StrategyParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, SUT_TYPE=28, ACTION_TYPE=29, NOT=30, AND=31, 
		XOR=32, OR=33, GT=34, GE=35, LT=36, LE=37, EQ=38, NE=39, IF=40, THEN=41, 
		ELSE=42, NUMBER=43, BOOLEAN=44, LP=45, RP=46, COMMENT=47, WHITESPACE=48, 
		ANY=49;
	public static final int
		RULE_strategy_file = 0, RULE_strategy = 1, RULE_bool_expr = 2, RULE_number_expr = 3, 
		RULE_action_expr = 4, RULE_state_boolean = 5, RULE_number_of_actions = 6, 
		RULE_action = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"strategy_file", "strategy", "bool_expr", "number_expr", "action_expr", 
			"state_boolean", "number_of_actions", "action"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'available-actions-of-type'", "'sut-type-is'", "'state-changed'", 
			"'sibling-action-exists'", "'child-action-exists'", "'child-or-sibling-action-exists'", 
			"'sibling-or-child-action-exists'", "'total-n-actions'", "'total-n-unexecuted-actions'", 
			"'total-n-executed-actions'", "'n-actions-of-type'", "'n-executed-actions-of-type'", 
			"'n-unexecuted-actions-of-type'", "'random-action'", "'previous-action'", 
			"'r-unexecuted-action'", "'r-least-executed-action'", "'r-most-executed-action'", 
			"'r-action-of-type'", "'r-unexecuted-action-of-type'", "'r-action-not-of-type'", 
			"'r-unexecuted-action-not-of-type'", "'select-submit-action'", "'select-sibling-action'", 
			"'select-child-action'", "'select-child-or-sibling-action'", "'select-sibling-or-child-action'", 
			null, null, null, null, null, null, "'>'", "'>='", "'<'", "'<='", null, 
			"'!='", null, null, null, null, null, "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "SUT_TYPE", "ACTION_TYPE", "NOT", "AND", "XOR", 
			"OR", "GT", "GE", "LT", "LE", "EQ", "NE", "IF", "THEN", "ELSE", "NUMBER", 
			"BOOLEAN", "LP", "RP", "COMMENT", "WHITESPACE", "ANY"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
	public String getGrammarFileName() { return "Strategy.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public StrategyParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class Strategy_fileContext extends ParserRuleContext {
		public StrategyContext strategy() {
			return getRuleContext(StrategyContext.class,0);
		}
		public TerminalNode EOF() { return getToken(StrategyParser.EOF, 0); }
		public Strategy_fileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strategy_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterStrategy_file(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitStrategy_file(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitStrategy_file(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Strategy_fileContext strategy_file() throws RecognitionException {
		Strategy_fileContext _localctx = new Strategy_fileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_strategy_file);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			strategy();
			setState(17);
			match(EOF);
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

	public static class StrategyContext extends ParserRuleContext {
		public Bool_exprContext ifExpr;
		public Action_exprContext thenExpr;
		public Action_exprContext elseExpr;
		public TerminalNode IF() { return getToken(StrategyParser.IF, 0); }
		public TerminalNode THEN() { return getToken(StrategyParser.THEN, 0); }
		public TerminalNode ELSE() { return getToken(StrategyParser.ELSE, 0); }
		public Bool_exprContext bool_expr() {
			return getRuleContext(Bool_exprContext.class,0);
		}
		public List<Action_exprContext> action_expr() {
			return getRuleContexts(Action_exprContext.class);
		}
		public Action_exprContext action_expr(int i) {
			return getRuleContext(Action_exprContext.class,i);
		}
		public StrategyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strategy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterStrategy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitStrategy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitStrategy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrategyContext strategy() throws RecognitionException {
		StrategyContext _localctx = new StrategyContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_strategy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(19);
			match(IF);
			setState(20);
			((StrategyContext)_localctx).ifExpr = bool_expr(0);
			setState(21);
			match(THEN);
			setState(22);
			((StrategyContext)_localctx).thenExpr = action_expr();
			setState(23);
			match(ELSE);
			setState(24);
			((StrategyContext)_localctx).elseExpr = action_expr();
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

	public static class Bool_exprContext extends ParserRuleContext {
		public Bool_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bool_expr; }
	 
		public Bool_exprContext() { }
		public void copyFrom(Bool_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class GreaterThanExprContext extends Bool_exprContext {
		public Number_exprContext left;
		public Number_exprContext right;
		public TerminalNode GT() { return getToken(StrategyParser.GT, 0); }
		public List<Number_exprContext> number_expr() {
			return getRuleContexts(Number_exprContext.class);
		}
		public Number_exprContext number_expr(int i) {
			return getRuleContext(Number_exprContext.class,i);
		}
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public GreaterThanExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterGreaterThanExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitGreaterThanExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitGreaterThanExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BaseBoolContext extends Bool_exprContext {
		public TerminalNode BOOLEAN() { return getToken(StrategyParser.BOOLEAN, 0); }
		public BaseBoolContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterBaseBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitBaseBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitBaseBool(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotExprContext extends Bool_exprContext {
		public Bool_exprContext expr;
		public TerminalNode NOT() { return getToken(StrategyParser.NOT, 0); }
		public Bool_exprContext bool_expr() {
			return getRuleContext(Bool_exprContext.class,0);
		}
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public NotExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNotExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNotExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNotExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LessThanExprContext extends Bool_exprContext {
		public Number_exprContext left;
		public Number_exprContext right;
		public TerminalNode LT() { return getToken(StrategyParser.LT, 0); }
		public List<Number_exprContext> number_expr() {
			return getRuleContexts(Number_exprContext.class);
		}
		public Number_exprContext number_expr(int i) {
			return getRuleContext(Number_exprContext.class,i);
		}
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public LessThanExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterLessThanExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitLessThanExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitLessThanExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GreaterEqualThanExprContext extends Bool_exprContext {
		public Number_exprContext left;
		public Number_exprContext right;
		public TerminalNode GE() { return getToken(StrategyParser.GE, 0); }
		public List<Number_exprContext> number_expr() {
			return getRuleContexts(Number_exprContext.class);
		}
		public Number_exprContext number_expr(int i) {
			return getRuleContext(Number_exprContext.class,i);
		}
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public GreaterEqualThanExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterGreaterEqualThanExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitGreaterEqualThanExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitGreaterEqualThanExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotEqualExprContext extends Bool_exprContext {
		public Number_exprContext left;
		public Number_exprContext right;
		public TerminalNode NE() { return getToken(StrategyParser.NE, 0); }
		public List<Number_exprContext> number_expr() {
			return getRuleContexts(Number_exprContext.class);
		}
		public Number_exprContext number_expr(int i) {
			return getRuleContext(Number_exprContext.class,i);
		}
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public NotEqualExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNotEqualExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNotEqualExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNotEqualExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StateBoolContext extends Bool_exprContext {
		public State_booleanContext state_boolean() {
			return getRuleContext(State_booleanContext.class,0);
		}
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public StateBoolContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterStateBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitStateBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitStateBool(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OrExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Bool_exprContext right;
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode OR() { return getToken(StrategyParser.OR, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public OrExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterOrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitOrExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitOrExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LessEqualThanExprContext extends Bool_exprContext {
		public Number_exprContext left;
		public Number_exprContext right;
		public TerminalNode LE() { return getToken(StrategyParser.LE, 0); }
		public List<Number_exprContext> number_expr() {
			return getRuleContexts(Number_exprContext.class);
		}
		public Number_exprContext number_expr(int i) {
			return getRuleContext(Number_exprContext.class,i);
		}
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public LessEqualThanExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterLessEqualThanExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitLessEqualThanExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitLessEqualThanExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class XorExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Bool_exprContext right;
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode XOR() { return getToken(StrategyParser.XOR, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public XorExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterXorExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitXorExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitXorExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AndExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Bool_exprContext right;
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode AND() { return getToken(StrategyParser.AND, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public AndExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAndExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAndExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAndExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EqualExprContext extends Bool_exprContext {
		public Number_exprContext left;
		public Number_exprContext right;
		public TerminalNode EQ() { return getToken(StrategyParser.EQ, 0); }
		public List<Number_exprContext> number_expr() {
			return getRuleContexts(Number_exprContext.class);
		}
		public Number_exprContext number_expr(int i) {
			return getRuleContext(Number_exprContext.class,i);
		}
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public EqualExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterEqualExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitEqualExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitEqualExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Bool_exprContext bool_expr() throws RecognitionException {
		return bool_expr(0);
	}

	private Bool_exprContext bool_expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Bool_exprContext _localctx = new Bool_exprContext(_ctx, _parentState);
		Bool_exprContext _prevctx = _localctx;
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_bool_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(27);
				match(NOT);
				setState(28);
				((NotExprContext)_localctx).expr = bool_expr(23);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(29);
				match(LP);
				setState(30);
				match(NOT);
				setState(31);
				((NotExprContext)_localctx).expr = bool_expr(0);
				setState(32);
				match(RP);
				}
				break;
			case 3:
				{
				_localctx = new AndExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(34);
				match(LP);
				setState(35);
				((AndExprContext)_localctx).left = bool_expr(0);
				setState(36);
				match(AND);
				setState(37);
				((AndExprContext)_localctx).right = bool_expr(0);
				setState(38);
				match(RP);
				}
				break;
			case 4:
				{
				_localctx = new XorExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(40);
				match(LP);
				setState(41);
				((XorExprContext)_localctx).left = bool_expr(0);
				setState(42);
				match(XOR);
				setState(43);
				((XorExprContext)_localctx).right = bool_expr(0);
				setState(44);
				match(RP);
				}
				break;
			case 5:
				{
				_localctx = new OrExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(46);
				match(LP);
				setState(47);
				((OrExprContext)_localctx).left = bool_expr(0);
				setState(48);
				match(OR);
				setState(49);
				((OrExprContext)_localctx).right = bool_expr(0);
				setState(50);
				match(RP);
				}
				break;
			case 6:
				{
				_localctx = new GreaterThanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(52);
				((GreaterThanExprContext)_localctx).left = number_expr();
				setState(53);
				match(GT);
				setState(54);
				((GreaterThanExprContext)_localctx).right = number_expr();
				}
				break;
			case 7:
				{
				_localctx = new GreaterThanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(56);
				match(LP);
				setState(57);
				((GreaterThanExprContext)_localctx).left = number_expr();
				setState(58);
				match(GT);
				setState(59);
				((GreaterThanExprContext)_localctx).right = number_expr();
				setState(60);
				match(RP);
				}
				break;
			case 8:
				{
				_localctx = new GreaterEqualThanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(62);
				((GreaterEqualThanExprContext)_localctx).left = number_expr();
				setState(63);
				match(GE);
				setState(64);
				((GreaterEqualThanExprContext)_localctx).right = number_expr();
				}
				break;
			case 9:
				{
				_localctx = new GreaterEqualThanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(66);
				match(LP);
				setState(67);
				((GreaterEqualThanExprContext)_localctx).left = number_expr();
				setState(68);
				match(GE);
				setState(69);
				((GreaterEqualThanExprContext)_localctx).right = number_expr();
				setState(70);
				match(RP);
				}
				break;
			case 10:
				{
				_localctx = new LessThanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(72);
				((LessThanExprContext)_localctx).left = number_expr();
				setState(73);
				match(LT);
				setState(74);
				((LessThanExprContext)_localctx).right = number_expr();
				}
				break;
			case 11:
				{
				_localctx = new LessThanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(76);
				match(LP);
				setState(77);
				((LessThanExprContext)_localctx).left = number_expr();
				setState(78);
				match(LT);
				setState(79);
				((LessThanExprContext)_localctx).right = number_expr();
				setState(80);
				match(RP);
				}
				break;
			case 12:
				{
				_localctx = new LessEqualThanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(82);
				((LessEqualThanExprContext)_localctx).left = number_expr();
				setState(83);
				match(LE);
				setState(84);
				((LessEqualThanExprContext)_localctx).right = number_expr();
				}
				break;
			case 13:
				{
				_localctx = new LessEqualThanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(86);
				match(LP);
				setState(87);
				((LessEqualThanExprContext)_localctx).left = number_expr();
				setState(88);
				match(LE);
				setState(89);
				((LessEqualThanExprContext)_localctx).right = number_expr();
				setState(90);
				match(RP);
				}
				break;
			case 14:
				{
				_localctx = new EqualExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(92);
				((EqualExprContext)_localctx).left = number_expr();
				setState(93);
				match(EQ);
				setState(94);
				((EqualExprContext)_localctx).right = number_expr();
				}
				break;
			case 15:
				{
				_localctx = new EqualExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(96);
				match(LP);
				setState(97);
				((EqualExprContext)_localctx).left = number_expr();
				setState(98);
				match(EQ);
				setState(99);
				((EqualExprContext)_localctx).right = number_expr();
				setState(100);
				match(RP);
				}
				break;
			case 16:
				{
				_localctx = new NotEqualExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(102);
				((NotEqualExprContext)_localctx).left = number_expr();
				setState(103);
				match(NE);
				setState(104);
				((NotEqualExprContext)_localctx).right = number_expr();
				}
				break;
			case 17:
				{
				_localctx = new NotEqualExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(106);
				match(LP);
				setState(107);
				((NotEqualExprContext)_localctx).left = number_expr();
				setState(108);
				match(NE);
				setState(109);
				((NotEqualExprContext)_localctx).right = number_expr();
				setState(110);
				match(RP);
				}
				break;
			case 18:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(112);
				state_boolean();
				}
				break;
			case 19:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(113);
				match(LP);
				setState(114);
				state_boolean();
				setState(115);
				match(RP);
				}
				break;
			case 20:
				{
				_localctx = new BaseBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(117);
				match(BOOLEAN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(131);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(129);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new AndExprContext(new Bool_exprContext(_parentctx, _parentState));
						((AndExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(120);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(121);
						match(AND);
						setState(122);
						((AndExprContext)_localctx).right = bool_expr(22);
						}
						break;
					case 2:
						{
						_localctx = new XorExprContext(new Bool_exprContext(_parentctx, _parentState));
						((XorExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(123);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(124);
						match(XOR);
						setState(125);
						((XorExprContext)_localctx).right = bool_expr(20);
						}
						break;
					case 3:
						{
						_localctx = new OrExprContext(new Bool_exprContext(_parentctx, _parentState));
						((OrExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(126);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(127);
						match(OR);
						setState(128);
						((OrExprContext)_localctx).right = bool_expr(18);
						}
						break;
					}
					} 
				}
				setState(133);
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

	public static class Number_exprContext extends ParserRuleContext {
		public Number_of_actionsContext number_of_actions() {
			return getRuleContext(Number_of_actionsContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public Number_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNumber_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNumber_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNumber_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Number_exprContext number_expr() throws RecognitionException {
		Number_exprContext _localctx = new Number_exprContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_number_expr);
		try {
			setState(136);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
			case T__8:
			case T__9:
			case T__10:
			case T__11:
			case T__12:
				enterOuterAlt(_localctx, 1);
				{
				setState(134);
				number_of_actions();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(135);
				match(NUMBER);
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

	public static class Action_exprContext extends ParserRuleContext {
		public StrategyContext strategy() {
			return getRuleContext(StrategyContext.class,0);
		}
		public List<ActionContext> action() {
			return getRuleContexts(ActionContext.class);
		}
		public ActionContext action(int i) {
			return getRuleContext(ActionContext.class,i);
		}
		public Action_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAction_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAction_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAction_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_exprContext action_expr() throws RecognitionException {
		Action_exprContext _localctx = new Action_exprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_action_expr);
		int _la;
		try {
			setState(144);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				enterOuterAlt(_localctx, 1);
				{
				setState(138);
				strategy();
				}
				break;
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case T__25:
			case T__26:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(140); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(139);
					action();
					}
					}
					setState(142); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << T__26) | (1L << NUMBER))) != 0) );
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

	public static class State_booleanContext extends ParserRuleContext {
		public State_booleanContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_state_boolean; }
	 
		public State_booleanContext() { }
		public void copyFrom(State_booleanContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SutTypeContext extends State_booleanContext {
		public TerminalNode SUT_TYPE() { return getToken(StrategyParser.SUT_TYPE, 0); }
		public SutTypeContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSutType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSutType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSutType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AvailableActionsOftypeContext extends State_booleanContext {
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public AvailableActionsOftypeContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAvailableActionsOftype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAvailableActionsOftype(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAvailableActionsOftype(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SiblingActionExistsContext extends State_booleanContext {
		public SiblingActionExistsContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSiblingActionExists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSiblingActionExists(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSiblingActionExists(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ChildOrSiblingActionExistsContext extends State_booleanContext {
		public ChildOrSiblingActionExistsContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterChildOrSiblingActionExists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitChildOrSiblingActionExists(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitChildOrSiblingActionExists(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StateChangedContext extends State_booleanContext {
		public StateChangedContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterStateChanged(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitStateChanged(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitStateChanged(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ChildActionExistsContext extends State_booleanContext {
		public ChildActionExistsContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterChildActionExists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitChildActionExists(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitChildActionExists(this);
			else return visitor.visitChildren(this);
		}
	}

	public final State_booleanContext state_boolean() throws RecognitionException {
		State_booleanContext _localctx = new State_booleanContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_state_boolean);
		try {
			setState(155);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				_localctx = new AvailableActionsOftypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				match(T__0);
				setState(147);
				match(ACTION_TYPE);
				}
				break;
			case T__1:
				_localctx = new SutTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(148);
				match(T__1);
				setState(149);
				match(SUT_TYPE);
				}
				break;
			case T__2:
				_localctx = new StateChangedContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(150);
				match(T__2);
				}
				break;
			case T__3:
				_localctx = new SiblingActionExistsContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(151);
				match(T__3);
				}
				break;
			case T__4:
				_localctx = new ChildActionExistsContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(152);
				match(T__4);
				}
				break;
			case T__5:
				_localctx = new ChildOrSiblingActionExistsContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(153);
				match(T__5);
				}
				break;
			case T__6:
				_localctx = new ChildOrSiblingActionExistsContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(154);
				match(T__6);
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

	public static class Number_of_actionsContext extends ParserRuleContext {
		public Number_of_actionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number_of_actions; }
	 
		public Number_of_actionsContext() { }
		public void copyFrom(Number_of_actionsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TnUnexActionsContext extends Number_of_actionsContext {
		public TnUnexActionsContext(Number_of_actionsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterTnUnexActions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitTnUnexActions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitTnUnexActions(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NActionsOfTypeContext extends Number_of_actionsContext {
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public NActionsOfTypeContext(Number_of_actionsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNActionsOfType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNActionsOfType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNActionsOfType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NUnexActionsOfTypeContext extends Number_of_actionsContext {
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public NUnexActionsOfTypeContext(Number_of_actionsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNUnexActionsOfType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNUnexActionsOfType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNUnexActionsOfType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TnExActionsContext extends Number_of_actionsContext {
		public TnExActionsContext(Number_of_actionsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterTnExActions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitTnExActions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitTnExActions(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NExecActionsContext extends Number_of_actionsContext {
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public NExecActionsContext(Number_of_actionsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNExecActions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNExecActions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNExecActions(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TnActionsContext extends Number_of_actionsContext {
		public TnActionsContext(Number_of_actionsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterTnActions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitTnActions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitTnActions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Number_of_actionsContext number_of_actions() throws RecognitionException {
		Number_of_actionsContext _localctx = new Number_of_actionsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_number_of_actions);
		try {
			setState(166);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				_localctx = new TnActionsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(157);
				match(T__7);
				}
				break;
			case T__8:
				_localctx = new TnUnexActionsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
				match(T__8);
				}
				break;
			case T__9:
				_localctx = new TnExActionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(159);
				match(T__9);
				}
				break;
			case T__10:
				_localctx = new NActionsOfTypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(160);
				match(T__10);
				setState(161);
				match(ACTION_TYPE);
				}
				break;
			case T__11:
				_localctx = new NExecActionsContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(162);
				match(T__11);
				setState(163);
				match(ACTION_TYPE);
				}
				break;
			case T__12:
				_localctx = new NUnexActionsOfTypeContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(164);
				match(T__12);
				setState(165);
				match(ACTION_TYPE);
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

	public static class ActionContext extends ParserRuleContext {
		public ActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action; }
	 
		public ActionContext() { }
		public void copyFrom(ActionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RActionNotTypeContext extends ActionContext {
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public RActionNotTypeContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRActionNotType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRActionNotType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRActionNotType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SChildOrSiblingActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public SChildOrSiblingActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSChildOrSiblingAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSChildOrSiblingAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSChildOrSiblingAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RUnexActionOfTypeContext extends ActionContext {
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public RUnexActionOfTypeContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRUnexActionOfType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRUnexActionOfType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRUnexActionOfType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RUnexActionNotTypeContext extends ActionContext {
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public RUnexActionNotTypeContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRUnexActionNotType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRUnexActionNotType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRUnexActionNotType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PrevActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public PrevActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterPrevAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitPrevAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitPrevAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RUnexActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public RUnexActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRUnexAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRUnexAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRUnexAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SChildActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public SChildActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSChildAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSChildAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSChildAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SSiblingActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public SSiblingActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSSiblingAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSSiblingAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSSiblingAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public RActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RActionOfTypeContext extends ActionContext {
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public RActionOfTypeContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRActionOfType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRActionOfType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRActionOfType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RLeastExActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public RLeastExActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRLeastExAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRLeastExAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRLeastExAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RMostExActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public RMostExActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRMostExAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRMostExAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRMostExAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SSubmitActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public SSubmitActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSSubmitAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSSubmitAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSSubmitAction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionContext action() throws RecognitionException {
		ActionContext _localctx = new ActionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_action);
		int _la;
		try {
			setState(228);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new RActionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(169);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(168);
					match(NUMBER);
					}
				}

				setState(171);
				match(T__13);
				}
				break;
			case 2:
				_localctx = new PrevActionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(173);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(172);
					match(NUMBER);
					}
				}

				setState(175);
				match(T__14);
				}
				break;
			case 3:
				_localctx = new RUnexActionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(177);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(176);
					match(NUMBER);
					}
				}

				setState(179);
				match(T__15);
				}
				break;
			case 4:
				_localctx = new RLeastExActionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(181);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(180);
					match(NUMBER);
					}
				}

				setState(183);
				match(T__16);
				}
				break;
			case 5:
				_localctx = new RMostExActionContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(184);
					match(NUMBER);
					}
				}

				setState(187);
				match(T__17);
				}
				break;
			case 6:
				_localctx = new RActionOfTypeContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(189);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(188);
					match(NUMBER);
					}
				}

				setState(191);
				match(T__18);
				setState(192);
				match(ACTION_TYPE);
				}
				break;
			case 7:
				_localctx = new RUnexActionOfTypeContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(194);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(193);
					match(NUMBER);
					}
				}

				setState(196);
				match(T__19);
				setState(197);
				match(ACTION_TYPE);
				}
				break;
			case 8:
				_localctx = new RActionNotTypeContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(199);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(198);
					match(NUMBER);
					}
				}

				setState(201);
				match(T__20);
				setState(202);
				match(ACTION_TYPE);
				}
				break;
			case 9:
				_localctx = new RUnexActionNotTypeContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(203);
					match(NUMBER);
					}
				}

				setState(206);
				match(T__21);
				setState(207);
				match(ACTION_TYPE);
				}
				break;
			case 10:
				_localctx = new SSubmitActionContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(209);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(208);
					match(NUMBER);
					}
				}

				setState(211);
				match(T__22);
				}
				break;
			case 11:
				_localctx = new SSiblingActionContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(213);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(212);
					match(NUMBER);
					}
				}

				setState(215);
				match(T__23);
				}
				break;
			case 12:
				_localctx = new SChildActionContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(217);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(216);
					match(NUMBER);
					}
				}

				setState(219);
				match(T__24);
				}
				break;
			case 13:
				_localctx = new SChildOrSiblingActionContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(221);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(220);
					match(NUMBER);
					}
				}

				setState(223);
				match(T__25);
				}
				break;
			case 14:
				_localctx = new SChildOrSiblingActionContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(225);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(224);
					match(NUMBER);
					}
				}

				setState(227);
				match(T__26);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2:
			return bool_expr_sempred((Bool_exprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean bool_expr_sempred(Bool_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 21);
		case 1:
			return precpred(_ctx, 19);
		case 2:
			return precpred(_ctx, 17);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\63\u00e9\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4y\n\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\7\4\u0084\n\4\f\4\16\4\u0087\13\4\3\5\3\5\5\5\u008b"+
		"\n\5\3\6\3\6\6\6\u008f\n\6\r\6\16\6\u0090\5\6\u0093\n\6\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\5\7\u009e\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\5\b\u00a9\n\b\3\t\5\t\u00ac\n\t\3\t\3\t\5\t\u00b0\n\t\3\t\3\t\5\t\u00b4"+
		"\n\t\3\t\3\t\5\t\u00b8\n\t\3\t\3\t\5\t\u00bc\n\t\3\t\3\t\5\t\u00c0\n\t"+
		"\3\t\3\t\3\t\5\t\u00c5\n\t\3\t\3\t\3\t\5\t\u00ca\n\t\3\t\3\t\3\t\5\t\u00cf"+
		"\n\t\3\t\3\t\3\t\5\t\u00d4\n\t\3\t\3\t\5\t\u00d8\n\t\3\t\3\t\5\t\u00dc"+
		"\n\t\3\t\3\t\5\t\u00e0\n\t\3\t\3\t\5\t\u00e4\n\t\3\t\5\t\u00e7\n\t\3\t"+
		"\2\3\6\n\2\4\6\b\n\f\16\20\2\2\2\u011f\2\22\3\2\2\2\4\25\3\2\2\2\6x\3"+
		"\2\2\2\b\u008a\3\2\2\2\n\u0092\3\2\2\2\f\u009d\3\2\2\2\16\u00a8\3\2\2"+
		"\2\20\u00e6\3\2\2\2\22\23\5\4\3\2\23\24\7\2\2\3\24\3\3\2\2\2\25\26\7*"+
		"\2\2\26\27\5\6\4\2\27\30\7+\2\2\30\31\5\n\6\2\31\32\7,\2\2\32\33\5\n\6"+
		"\2\33\5\3\2\2\2\34\35\b\4\1\2\35\36\7 \2\2\36y\5\6\4\31\37 \7/\2\2 !\7"+
		" \2\2!\"\5\6\4\2\"#\7\60\2\2#y\3\2\2\2$%\7/\2\2%&\5\6\4\2&\'\7!\2\2\'"+
		"(\5\6\4\2()\7\60\2\2)y\3\2\2\2*+\7/\2\2+,\5\6\4\2,-\7\"\2\2-.\5\6\4\2"+
		"./\7\60\2\2/y\3\2\2\2\60\61\7/\2\2\61\62\5\6\4\2\62\63\7#\2\2\63\64\5"+
		"\6\4\2\64\65\7\60\2\2\65y\3\2\2\2\66\67\5\b\5\2\678\7$\2\289\5\b\5\29"+
		"y\3\2\2\2:;\7/\2\2;<\5\b\5\2<=\7$\2\2=>\5\b\5\2>?\7\60\2\2?y\3\2\2\2@"+
		"A\5\b\5\2AB\7%\2\2BC\5\b\5\2Cy\3\2\2\2DE\7/\2\2EF\5\b\5\2FG\7%\2\2GH\5"+
		"\b\5\2HI\7\60\2\2Iy\3\2\2\2JK\5\b\5\2KL\7&\2\2LM\5\b\5\2My\3\2\2\2NO\7"+
		"/\2\2OP\5\b\5\2PQ\7&\2\2QR\5\b\5\2RS\7\60\2\2Sy\3\2\2\2TU\5\b\5\2UV\7"+
		"\'\2\2VW\5\b\5\2Wy\3\2\2\2XY\7/\2\2YZ\5\b\5\2Z[\7\'\2\2[\\\5\b\5\2\\]"+
		"\7\60\2\2]y\3\2\2\2^_\5\b\5\2_`\7(\2\2`a\5\b\5\2ay\3\2\2\2bc\7/\2\2cd"+
		"\5\b\5\2de\7(\2\2ef\5\b\5\2fg\7\60\2\2gy\3\2\2\2hi\5\b\5\2ij\7)\2\2jk"+
		"\5\b\5\2ky\3\2\2\2lm\7/\2\2mn\5\b\5\2no\7)\2\2op\5\b\5\2pq\7\60\2\2qy"+
		"\3\2\2\2ry\5\f\7\2st\7/\2\2tu\5\f\7\2uv\7\60\2\2vy\3\2\2\2wy\7.\2\2x\34"+
		"\3\2\2\2x\37\3\2\2\2x$\3\2\2\2x*\3\2\2\2x\60\3\2\2\2x\66\3\2\2\2x:\3\2"+
		"\2\2x@\3\2\2\2xD\3\2\2\2xJ\3\2\2\2xN\3\2\2\2xT\3\2\2\2xX\3\2\2\2x^\3\2"+
		"\2\2xb\3\2\2\2xh\3\2\2\2xl\3\2\2\2xr\3\2\2\2xs\3\2\2\2xw\3\2\2\2y\u0085"+
		"\3\2\2\2z{\f\27\2\2{|\7!\2\2|\u0084\5\6\4\30}~\f\25\2\2~\177\7\"\2\2\177"+
		"\u0084\5\6\4\26\u0080\u0081\f\23\2\2\u0081\u0082\7#\2\2\u0082\u0084\5"+
		"\6\4\24\u0083z\3\2\2\2\u0083}\3\2\2\2\u0083\u0080\3\2\2\2\u0084\u0087"+
		"\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\7\3\2\2\2\u0087"+
		"\u0085\3\2\2\2\u0088\u008b\5\16\b\2\u0089\u008b\7-\2\2\u008a\u0088\3\2"+
		"\2\2\u008a\u0089\3\2\2\2\u008b\t\3\2\2\2\u008c\u0093\5\4\3\2\u008d\u008f"+
		"\5\20\t\2\u008e\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u008e\3\2\2\2"+
		"\u0090\u0091\3\2\2\2\u0091\u0093\3\2\2\2\u0092\u008c\3\2\2\2\u0092\u008e"+
		"\3\2\2\2\u0093\13\3\2\2\2\u0094\u0095\7\3\2\2\u0095\u009e\7\37\2\2\u0096"+
		"\u0097\7\4\2\2\u0097\u009e\7\36\2\2\u0098\u009e\7\5\2\2\u0099\u009e\7"+
		"\6\2\2\u009a\u009e\7\7\2\2\u009b\u009e\7\b\2\2\u009c\u009e\7\t\2\2\u009d"+
		"\u0094\3\2\2\2\u009d\u0096\3\2\2\2\u009d\u0098\3\2\2\2\u009d\u0099\3\2"+
		"\2\2\u009d\u009a\3\2\2\2\u009d\u009b\3\2\2\2\u009d\u009c\3\2\2\2\u009e"+
		"\r\3\2\2\2\u009f\u00a9\7\n\2\2\u00a0\u00a9\7\13\2\2\u00a1\u00a9\7\f\2"+
		"\2\u00a2\u00a3\7\r\2\2\u00a3\u00a9\7\37\2\2\u00a4\u00a5\7\16\2\2\u00a5"+
		"\u00a9\7\37\2\2\u00a6\u00a7\7\17\2\2\u00a7\u00a9\7\37\2\2\u00a8\u009f"+
		"\3\2\2\2\u00a8\u00a0\3\2\2\2\u00a8\u00a1\3\2\2\2\u00a8\u00a2\3\2\2\2\u00a8"+
		"\u00a4\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a9\17\3\2\2\2\u00aa\u00ac\7-\2\2"+
		"\u00ab\u00aa\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00e7"+
		"\7\20\2\2\u00ae\u00b0\7-\2\2\u00af\u00ae\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0"+
		"\u00b1\3\2\2\2\u00b1\u00e7\7\21\2\2\u00b2\u00b4\7-\2\2\u00b3\u00b2\3\2"+
		"\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00e7\7\22\2\2\u00b6"+
		"\u00b8\7-\2\2\u00b7\u00b6\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b9\3\2"+
		"\2\2\u00b9\u00e7\7\23\2\2\u00ba\u00bc\7-\2\2\u00bb\u00ba\3\2\2\2\u00bb"+
		"\u00bc\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00e7\7\24\2\2\u00be\u00c0\7"+
		"-\2\2\u00bf\u00be\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1"+
		"\u00c2\7\25\2\2\u00c2\u00e7\7\37\2\2\u00c3\u00c5\7-\2\2\u00c4\u00c3\3"+
		"\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c7\7\26\2\2\u00c7"+
		"\u00e7\7\37\2\2\u00c8\u00ca\7-\2\2\u00c9\u00c8\3\2\2\2\u00c9\u00ca\3\2"+
		"\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00cc\7\27\2\2\u00cc\u00e7\7\37\2\2\u00cd"+
		"\u00cf\7-\2\2\u00ce\u00cd\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d0\3\2"+
		"\2\2\u00d0\u00d1\7\30\2\2\u00d1\u00e7\7\37\2\2\u00d2\u00d4\7-\2\2\u00d3"+
		"\u00d2\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00e7\7\31"+
		"\2\2\u00d6\u00d8\7-\2\2\u00d7\u00d6\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8"+
		"\u00d9\3\2\2\2\u00d9\u00e7\7\32\2\2\u00da\u00dc\7-\2\2\u00db\u00da\3\2"+
		"\2\2\u00db\u00dc\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00e7\7\33\2\2\u00de"+
		"\u00e0\7-\2\2\u00df\u00de\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0\u00e1\3\2"+
		"\2\2\u00e1\u00e7\7\34\2\2\u00e2\u00e4\7-\2\2\u00e3\u00e2\3\2\2\2\u00e3"+
		"\u00e4\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e7\7\35\2\2\u00e6\u00ab\3"+
		"\2\2\2\u00e6\u00af\3\2\2\2\u00e6\u00b3\3\2\2\2\u00e6\u00b7\3\2\2\2\u00e6"+
		"\u00bb\3\2\2\2\u00e6\u00bf\3\2\2\2\u00e6\u00c4\3\2\2\2\u00e6\u00c9\3\2"+
		"\2\2\u00e6\u00ce\3\2\2\2\u00e6\u00d3\3\2\2\2\u00e6\u00d7\3\2\2\2\u00e6"+
		"\u00db\3\2\2\2\u00e6\u00df\3\2\2\2\u00e6\u00e3\3\2\2\2\u00e7\21\3\2\2"+
		"\2\31x\u0083\u0085\u008a\u0090\u0092\u009d\u00a8\u00ab\u00af\u00b3\u00b7"+
		"\u00bb\u00bf\u00c4\u00c9\u00ce\u00d3\u00d7\u00db\u00df\u00e3\u00e6";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
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
		T__17=18, T__18=19, T__19=20, ACTION_VISITED=21, EXIST=22, FILTER=23, 
		NOT=24, AND=25, XOR=26, OR=27, GT=28, GE=29, LT=30, LE=31, EQ=32, NE=33, 
		IF=34, THEN=35, ELSE=36, NUMBER=37, BOOLEAN=38, LP=39, RP=40, COMMENT=41, 
		WHITESPACE=42, ANY=43;
	public static final int
		RULE_strategy_file = 0, RULE_strategy = 1, RULE_bool_expr = 2, RULE_number_expr = 3, 
		RULE_number_of_actions = 4, RULE_state_boolean = 5, RULE_action_expr = 6, 
		RULE_action = 7, RULE_related_action = 8, RULE_sut_type = 9, RULE_action_type = 10;
	private static String[] makeRuleNames() {
		return new String[] {
			"strategy_file", "strategy", "bool_expr", "number_expr", "number_of_actions", 
			"state_boolean", "action_expr", "action", "related_action", "sut_type", 
			"action_type"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'n-actions'", "'state-changed'", "'any-actions'", "'sut'", "'select-previous-action'", 
			"'select-random'", "'select-by-relation'", "'sibling-action'", "'child-action'", 
			"'sibling-or-child-action'", "'windows'", "'linux'", "'android'", "'web'", 
			"'click-action'", "'type-action'", "'drag-action'", "'scroll-action'", 
			"'hit-key-action'", "'input-action'", null, null, null, null, null, null, 
			null, "'>'", "'>='", "'<'", "'<='", null, "'!='", null, null, null, null, 
			null, "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "ACTION_VISITED", 
			"EXIST", "FILTER", "NOT", "AND", "XOR", "OR", "GT", "GE", "LT", "LE", 
			"EQ", "NE", "IF", "THEN", "ELSE", "NUMBER", "BOOLEAN", "LP", "RP", "COMMENT", 
			"WHITESPACE", "ANY"
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
			setState(22);
			strategy();
			setState(23);
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
			setState(25);
			match(IF);
			setState(26);
			((StrategyContext)_localctx).ifExpr = bool_expr(0);
			setState(27);
			match(THEN);
			setState(28);
			((StrategyContext)_localctx).thenExpr = action_expr();
			setState(29);
			match(ELSE);
			setState(30);
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
	public static class BoolOprExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Token opr;
		public Bool_exprContext right;
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public TerminalNode AND() { return getToken(StrategyParser.AND, 0); }
		public TerminalNode XOR() { return getToken(StrategyParser.XOR, 0); }
		public TerminalNode OR() { return getToken(StrategyParser.OR, 0); }
		public BoolOprExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterBoolOprExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitBoolOprExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitBoolOprExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberOprExprContext extends Bool_exprContext {
		public Number_exprContext left;
		public Token opr;
		public Number_exprContext right;
		public List<Number_exprContext> number_expr() {
			return getRuleContexts(Number_exprContext.class);
		}
		public Number_exprContext number_expr(int i) {
			return getRuleContext(Number_exprContext.class,i);
		}
		public TerminalNode LT() { return getToken(StrategyParser.LT, 0); }
		public TerminalNode LE() { return getToken(StrategyParser.LE, 0); }
		public TerminalNode GT() { return getToken(StrategyParser.GT, 0); }
		public TerminalNode GE() { return getToken(StrategyParser.GE, 0); }
		public TerminalNode EQ() { return getToken(StrategyParser.EQ, 0); }
		public TerminalNode NE() { return getToken(StrategyParser.NE, 0); }
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public NumberOprExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNumberOprExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNumberOprExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNumberOprExpr(this);
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
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(33);
				match(NOT);
				setState(34);
				((NotExprContext)_localctx).expr = bool_expr(9);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(35);
				match(LP);
				setState(36);
				match(NOT);
				setState(37);
				((NotExprContext)_localctx).expr = bool_expr(0);
				setState(38);
				match(RP);
				}
				break;
			case 3:
				{
				_localctx = new BoolOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(40);
				match(LP);
				setState(41);
				((BoolOprExprContext)_localctx).left = bool_expr(0);
				setState(42);
				((BoolOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AND) | (1L << XOR) | (1L << OR))) != 0)) ) {
					((BoolOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(43);
				((BoolOprExprContext)_localctx).right = bool_expr(0);
				setState(44);
				match(RP);
				}
				break;
			case 4:
				{
				_localctx = new NumberOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(46);
				((NumberOprExprContext)_localctx).left = number_expr();
				setState(47);
				((NumberOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE))) != 0)) ) {
					((NumberOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(48);
				((NumberOprExprContext)_localctx).right = number_expr();
				}
				break;
			case 5:
				{
				_localctx = new NumberOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(50);
				match(LP);
				setState(51);
				((NumberOprExprContext)_localctx).left = number_expr();
				setState(52);
				((NumberOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE))) != 0)) ) {
					((NumberOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(53);
				((NumberOprExprContext)_localctx).right = number_expr();
				setState(54);
				match(RP);
				}
				break;
			case 6:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(56);
				state_boolean();
				}
				break;
			case 7:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(57);
				match(LP);
				setState(58);
				state_boolean();
				setState(59);
				match(RP);
				}
				break;
			case 8:
				{
				_localctx = new BaseBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(61);
				match(BOOLEAN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(69);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new BoolOprExprContext(new Bool_exprContext(_parentctx, _parentState));
					((BoolOprExprContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
					setState(64);
					if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
					setState(65);
					((BoolOprExprContext)_localctx).opr = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AND) | (1L << XOR) | (1L << OR))) != 0)) ) {
						((BoolOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(66);
					((BoolOprExprContext)_localctx).right = bool_expr(8);
					}
					} 
				}
				setState(71);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
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
			setState(74);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(72);
				number_of_actions();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(73);
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

	public static class Number_of_actionsContext extends ParserRuleContext {
		public TerminalNode ACTION_VISITED() { return getToken(StrategyParser.ACTION_VISITED, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public Action_typeContext action_type() {
			return getRuleContext(Action_typeContext.class,0);
		}
		public Number_of_actionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number_of_actions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNumber_of_actions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNumber_of_actions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNumber_of_actions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Number_of_actionsContext number_of_actions() throws RecognitionException {
		Number_of_actionsContext _localctx = new Number_of_actionsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_number_of_actions);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(T__0);
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(77);
				match(ACTION_VISITED);
				}
				break;
			}
			setState(82);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(80);
				match(FILTER);
				setState(81);
				action_type();
				}
				break;
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
	public static class AnyActionsExistsContext extends State_booleanContext {
		public TerminalNode EXIST() { return getToken(StrategyParser.EXIST, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public Action_typeContext action_type() {
			return getRuleContext(Action_typeContext.class,0);
		}
		public AnyActionsExistsContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAnyActionsExists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAnyActionsExists(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAnyActionsExists(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SutTypeContext extends State_booleanContext {
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public Sut_typeContext sut_type() {
			return getRuleContext(Sut_typeContext.class,0);
		}
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
	public static class RelatedActionExistsContext extends State_booleanContext {
		public Related_actionContext related_action() {
			return getRuleContext(Related_actionContext.class,0);
		}
		public TerminalNode EXIST() { return getToken(StrategyParser.EXIST, 0); }
		public RelatedActionExistsContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRelatedActionExists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRelatedActionExists(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRelatedActionExists(this);
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

	public final State_booleanContext state_boolean() throws RecognitionException {
		State_booleanContext _localctx = new State_booleanContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_state_boolean);
		int _la;
		try {
			setState(97);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new StateChangedContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(84);
				match(T__1);
				}
				break;
			case T__2:
				_localctx = new AnyActionsExistsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(85);
				match(T__2);
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(86);
					match(FILTER);
					setState(87);
					action_type();
					}
				}

				setState(90);
				match(EXIST);
				}
				break;
			case T__3:
				_localctx = new SutTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(91);
				match(T__3);
				setState(92);
				match(FILTER);
				setState(93);
				sut_type();
				}
				break;
			case T__7:
			case T__8:
			case T__9:
				_localctx = new RelatedActionExistsContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(94);
				related_action();
				setState(95);
				match(EXIST);
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
		public Action_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_expr; }
	 
		public Action_exprContext() { }
		public void copyFrom(Action_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ActionListContext extends Action_exprContext {
		public List<ActionContext> action() {
			return getRuleContexts(ActionContext.class);
		}
		public ActionContext action(int i) {
			return getRuleContext(ActionContext.class,i);
		}
		public ActionListContext(Action_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterActionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitActionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitActionList(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SubStrategyContext extends Action_exprContext {
		public StrategyContext strategy() {
			return getRuleContext(StrategyContext.class,0);
		}
		public SubStrategyContext(Action_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSubStrategy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSubStrategy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSubStrategy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_exprContext action_expr() throws RecognitionException {
		Action_exprContext _localctx = new Action_exprContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_action_expr);
		int _la;
		try {
			setState(105);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				_localctx = new SubStrategyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(99);
				strategy();
				}
				break;
			case T__4:
			case T__5:
			case T__6:
			case NUMBER:
				_localctx = new ActionListContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(101); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(100);
					action();
					}
					}
					setState(103); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << NUMBER))) != 0) );
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
	public static class SelectRelatedActionContext extends ActionContext {
		public Related_actionContext related_action() {
			return getRuleContext(Related_actionContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public SelectRelatedActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSelectRelatedAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSelectRelatedAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSelectRelatedAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SelectPreviousActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public SelectPreviousActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSelectPreviousAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSelectPreviousAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSelectPreviousAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SelectRandomActionContext extends ActionContext {
		public TerminalNode NUMBER() { return getToken(StrategyParser.NUMBER, 0); }
		public TerminalNode ACTION_VISITED() { return getToken(StrategyParser.ACTION_VISITED, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public Action_typeContext action_type() {
			return getRuleContext(Action_typeContext.class,0);
		}
		public SelectRandomActionContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSelectRandomAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSelectRandomAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSelectRandomAction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionContext action() throws RecognitionException {
		ActionContext _localctx = new ActionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_action);
		int _la;
		try {
			setState(127);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				_localctx = new SelectPreviousActionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(108);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(107);
					match(NUMBER);
					}
				}

				setState(110);
				match(T__4);
				}
				break;
			case 2:
				_localctx = new SelectRandomActionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(111);
					match(NUMBER);
					}
				}

				setState(114);
				match(T__5);
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ACTION_VISITED) {
					{
					setState(115);
					match(ACTION_VISITED);
					}
				}

				setState(120);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(118);
					match(FILTER);
					setState(119);
					action_type();
					}
				}

				}
				break;
			case 3:
				_localctx = new SelectRelatedActionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(122);
					match(NUMBER);
					}
				}

				setState(125);
				match(T__6);
				setState(126);
				related_action();
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

	public static class Related_actionContext extends ParserRuleContext {
		public Related_actionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_related_action; }
	 
		public Related_actionContext() { }
		public void copyFrom(Related_actionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SiblingActionContext extends Related_actionContext {
		public SiblingActionContext(Related_actionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSiblingAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSiblingAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSiblingAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ChildOrSiblingActionContext extends Related_actionContext {
		public ChildOrSiblingActionContext(Related_actionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterChildOrSiblingAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitChildOrSiblingAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitChildOrSiblingAction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ChildActionContext extends Related_actionContext {
		public ChildActionContext(Related_actionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterChildAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitChildAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitChildAction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Related_actionContext related_action() throws RecognitionException {
		Related_actionContext _localctx = new Related_actionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_related_action);
		try {
			setState(132);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				_localctx = new SiblingActionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(129);
				match(T__7);
				}
				break;
			case T__8:
				_localctx = new ChildActionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(130);
				match(T__8);
				}
				break;
			case T__9:
				_localctx = new ChildOrSiblingActionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(131);
				match(T__9);
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

	public static class Sut_typeContext extends ParserRuleContext {
		public Sut_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sut_type; }
	 
		public Sut_typeContext() { }
		public void copyFrom(Sut_typeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class WebContext extends Sut_typeContext {
		public WebContext(Sut_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterWeb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitWeb(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitWeb(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LinuxContext extends Sut_typeContext {
		public LinuxContext(Sut_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterLinux(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitLinux(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitLinux(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AndroidContext extends Sut_typeContext {
		public AndroidContext(Sut_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAndroid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAndroid(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAndroid(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WindowsContext extends Sut_typeContext {
		public WindowsContext(Sut_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterWindows(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitWindows(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitWindows(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sut_typeContext sut_type() throws RecognitionException {
		Sut_typeContext _localctx = new Sut_typeContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_sut_type);
		try {
			setState(138);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__10:
				_localctx = new WindowsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(134);
				match(T__10);
				}
				break;
			case T__11:
				_localctx = new LinuxContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(135);
				match(T__11);
				}
				break;
			case T__12:
				_localctx = new AndroidContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(136);
				match(T__12);
				}
				break;
			case T__13:
				_localctx = new WebContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(137);
				match(T__13);
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

	public static class Action_typeContext extends ParserRuleContext {
		public Action_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_type; }
	 
		public Action_typeContext() { }
		public void copyFrom(Action_typeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class InputContext extends Action_typeContext {
		public InputContext(Action_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterInput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitInput(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitInput(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TypingContext extends Action_typeContext {
		public TypingContext(Action_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterTyping(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitTyping(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitTyping(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ScrollContext extends Action_typeContext {
		public ScrollContext(Action_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterScroll(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitScroll(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitScroll(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class HitKeyContext extends Action_typeContext {
		public HitKeyContext(Action_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterHitKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitHitKey(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitHitKey(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DragContext extends Action_typeContext {
		public DragContext(Action_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterDrag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitDrag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitDrag(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ClickContext extends Action_typeContext {
		public ClickContext(Action_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterClick(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitClick(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitClick(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_typeContext action_type() throws RecognitionException {
		Action_typeContext _localctx = new Action_typeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_action_type);
		try {
			setState(146);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__14:
				_localctx = new ClickContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(140);
				match(T__14);
				}
				break;
			case T__15:
				_localctx = new TypingContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(141);
				match(T__15);
				}
				break;
			case T__16:
				_localctx = new DragContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(142);
				match(T__16);
				}
				break;
			case T__17:
				_localctx = new ScrollContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(143);
				match(T__17);
				}
				break;
			case T__18:
				_localctx = new HitKeyContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(144);
				match(T__18);
				}
				break;
			case T__19:
				_localctx = new InputContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(145);
				match(T__19);
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
		case 2:
			return bool_expr_sempred((Bool_exprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean bool_expr_sempred(Bool_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 7);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3-\u0097\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4A\n\4\3\4\3\4\3\4\7\4F\n\4\f\4\16"+
		"\4I\13\4\3\5\3\5\5\5M\n\5\3\6\3\6\5\6Q\n\6\3\6\3\6\5\6U\n\6\3\7\3\7\3"+
		"\7\3\7\5\7[\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7d\n\7\3\b\3\b\6\bh\n\b"+
		"\r\b\16\bi\5\bl\n\b\3\t\5\to\n\t\3\t\3\t\5\ts\n\t\3\t\3\t\5\tw\n\t\3\t"+
		"\3\t\5\t{\n\t\3\t\5\t~\n\t\3\t\3\t\5\t\u0082\n\t\3\n\3\n\3\n\5\n\u0087"+
		"\n\n\3\13\3\13\3\13\3\13\5\13\u008d\n\13\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u0095"+
		"\n\f\3\f\2\3\6\r\2\4\6\b\n\f\16\20\22\24\26\2\4\3\2\33\35\3\2\36#\2\u00ad"+
		"\2\30\3\2\2\2\4\33\3\2\2\2\6@\3\2\2\2\bL\3\2\2\2\nN\3\2\2\2\fc\3\2\2\2"+
		"\16k\3\2\2\2\20\u0081\3\2\2\2\22\u0086\3\2\2\2\24\u008c\3\2\2\2\26\u0094"+
		"\3\2\2\2\30\31\5\4\3\2\31\32\7\2\2\3\32\3\3\2\2\2\33\34\7$\2\2\34\35\5"+
		"\6\4\2\35\36\7%\2\2\36\37\5\16\b\2\37 \7&\2\2 !\5\16\b\2!\5\3\2\2\2\""+
		"#\b\4\1\2#$\7\32\2\2$A\5\6\4\13%&\7)\2\2&\'\7\32\2\2\'(\5\6\4\2()\7*\2"+
		"\2)A\3\2\2\2*+\7)\2\2+,\5\6\4\2,-\t\2\2\2-.\5\6\4\2./\7*\2\2/A\3\2\2\2"+
		"\60\61\5\b\5\2\61\62\t\3\2\2\62\63\5\b\5\2\63A\3\2\2\2\64\65\7)\2\2\65"+
		"\66\5\b\5\2\66\67\t\3\2\2\678\5\b\5\289\7*\2\29A\3\2\2\2:A\5\f\7\2;<\7"+
		")\2\2<=\5\f\7\2=>\7*\2\2>A\3\2\2\2?A\7(\2\2@\"\3\2\2\2@%\3\2\2\2@*\3\2"+
		"\2\2@\60\3\2\2\2@\64\3\2\2\2@:\3\2\2\2@;\3\2\2\2@?\3\2\2\2AG\3\2\2\2B"+
		"C\f\t\2\2CD\t\2\2\2DF\5\6\4\nEB\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2"+
		"H\7\3\2\2\2IG\3\2\2\2JM\5\n\6\2KM\7\'\2\2LJ\3\2\2\2LK\3\2\2\2M\t\3\2\2"+
		"\2NP\7\3\2\2OQ\7\27\2\2PO\3\2\2\2PQ\3\2\2\2QT\3\2\2\2RS\7\31\2\2SU\5\26"+
		"\f\2TR\3\2\2\2TU\3\2\2\2U\13\3\2\2\2Vd\7\4\2\2WZ\7\5\2\2XY\7\31\2\2Y["+
		"\5\26\f\2ZX\3\2\2\2Z[\3\2\2\2[\\\3\2\2\2\\d\7\30\2\2]^\7\6\2\2^_\7\31"+
		"\2\2_d\5\24\13\2`a\5\22\n\2ab\7\30\2\2bd\3\2\2\2cV\3\2\2\2cW\3\2\2\2c"+
		"]\3\2\2\2c`\3\2\2\2d\r\3\2\2\2el\5\4\3\2fh\5\20\t\2gf\3\2\2\2hi\3\2\2"+
		"\2ig\3\2\2\2ij\3\2\2\2jl\3\2\2\2ke\3\2\2\2kg\3\2\2\2l\17\3\2\2\2mo\7\'"+
		"\2\2nm\3\2\2\2no\3\2\2\2op\3\2\2\2p\u0082\7\7\2\2qs\7\'\2\2rq\3\2\2\2"+
		"rs\3\2\2\2st\3\2\2\2tv\7\b\2\2uw\7\27\2\2vu\3\2\2\2vw\3\2\2\2wz\3\2\2"+
		"\2xy\7\31\2\2y{\5\26\f\2zx\3\2\2\2z{\3\2\2\2{\u0082\3\2\2\2|~\7\'\2\2"+
		"}|\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080\7\t\2\2\u0080\u0082\5\22\n"+
		"\2\u0081n\3\2\2\2\u0081r\3\2\2\2\u0081}\3\2\2\2\u0082\21\3\2\2\2\u0083"+
		"\u0087\7\n\2\2\u0084\u0087\7\13\2\2\u0085\u0087\7\f\2\2\u0086\u0083\3"+
		"\2\2\2\u0086\u0084\3\2\2\2\u0086\u0085\3\2\2\2\u0087\23\3\2\2\2\u0088"+
		"\u008d\7\r\2\2\u0089\u008d\7\16\2\2\u008a\u008d\7\17\2\2\u008b\u008d\7"+
		"\20\2\2\u008c\u0088\3\2\2\2\u008c\u0089\3\2\2\2\u008c\u008a\3\2\2\2\u008c"+
		"\u008b\3\2\2\2\u008d\25\3\2\2\2\u008e\u0095\7\21\2\2\u008f\u0095\7\22"+
		"\2\2\u0090\u0095\7\23\2\2\u0091\u0095\7\24\2\2\u0092\u0095\7\25\2\2\u0093"+
		"\u0095\7\26\2\2\u0094\u008e\3\2\2\2\u0094\u008f\3\2\2\2\u0094\u0090\3"+
		"\2\2\2\u0094\u0091\3\2\2\2\u0094\u0092\3\2\2\2\u0094\u0093\3\2\2\2\u0095"+
		"\27\3\2\2\2\24@GLPTZciknrvz}\u0081\u0086\u008c\u0094";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
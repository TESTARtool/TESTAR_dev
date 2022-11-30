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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, VISIT_MODIFIER=8, 
		RELATED_ACTION=9, SUT_TYPE=10, ACTION_TYPE=11, EXIST=12, FILTER=13, NOT=14, 
		AND=15, XOR=16, OR=17, GT=18, GE=19, LT=20, LE=21, EQ=22, NE=23, IF=24, 
		THEN=25, ELSE=26, NUMBER=27, BOOLEAN=28, LP=29, RP=30, COMMENT=31, WHITESPACE=32, 
		ANY=33;
	public static final int
		RULE_strategy_file = 0, RULE_strategy = 1, RULE_bool_expr = 2, RULE_number_expr = 3, 
		RULE_number_of_actions = 4, RULE_state_boolean = 5, RULE_action_expr = 6, 
		RULE_action = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"strategy_file", "strategy", "bool_expr", "number_expr", "number_of_actions", 
			"state_boolean", "action_expr", "action"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'n-actions'", "'state-changed'", "'any-actions'", "'sut'", "'select-previous-action'", 
			"'select-random'", "'select-by-related'", null, null, null, null, null, 
			null, null, null, null, null, "'>'", "'>='", "'<'", "'<='", null, "'!='", 
			null, null, null, null, null, "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "VISIT_MODIFIER", "RELATED_ACTION", 
			"SUT_TYPE", "ACTION_TYPE", "EXIST", "FILTER", "NOT", "AND", "XOR", "OR", 
			"GT", "GE", "LT", "LE", "EQ", "NE", "IF", "THEN", "ELSE", "NUMBER", "BOOLEAN", 
			"LP", "RP", "COMMENT", "WHITESPACE", "ANY"
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
			setState(56);
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
				((NotExprContext)_localctx).expr = bool_expr(9);
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
				_localctx = new BoolOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(34);
				match(LP);
				setState(35);
				((BoolOprExprContext)_localctx).left = bool_expr(0);
				setState(36);
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
				setState(37);
				((BoolOprExprContext)_localctx).right = bool_expr(0);
				setState(38);
				match(RP);
				}
				break;
			case 4:
				{
				_localctx = new NumberOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(40);
				((NumberOprExprContext)_localctx).left = number_expr();
				setState(41);
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
				setState(42);
				((NumberOprExprContext)_localctx).right = number_expr();
				}
				break;
			case 5:
				{
				_localctx = new NumberOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(44);
				match(LP);
				setState(45);
				((NumberOprExprContext)_localctx).left = number_expr();
				setState(46);
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
				setState(47);
				((NumberOprExprContext)_localctx).right = number_expr();
				setState(48);
				match(RP);
				}
				break;
			case 6:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(50);
				state_boolean();
				}
				break;
			case 7:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(51);
				match(LP);
				setState(52);
				state_boolean();
				setState(53);
				match(RP);
				}
				break;
			case 8:
				{
				_localctx = new BaseBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(55);
				match(BOOLEAN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(63);
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
					setState(58);
					if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
					setState(59);
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
					setState(60);
					((BoolOprExprContext)_localctx).right = bool_expr(8);
					}
					} 
				}
				setState(65);
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
			setState(68);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(66);
				number_of_actions();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(67);
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
		public TerminalNode VISIT_MODIFIER() { return getToken(StrategyParser.VISIT_MODIFIER, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
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
			setState(70);
			match(T__0);
			setState(72);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(71);
				match(VISIT_MODIFIER);
				}
				break;
			}
			setState(76);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(74);
				match(FILTER);
				setState(75);
				match(ACTION_TYPE);
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
		public TerminalNode VISIT_MODIFIER() { return getToken(StrategyParser.VISIT_MODIFIER, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
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
	public static class RelatedActionExistsContext extends State_booleanContext {
		public TerminalNode RELATED_ACTION() { return getToken(StrategyParser.RELATED_ACTION, 0); }
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
			setState(93);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new StateChangedContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(78);
				match(T__1);
				}
				break;
			case T__2:
				_localctx = new AnyActionsExistsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(79);
				match(T__2);
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VISIT_MODIFIER) {
					{
					setState(80);
					match(VISIT_MODIFIER);
					}
				}

				setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(83);
					match(FILTER);
					setState(84);
					match(ACTION_TYPE);
					}
				}

				setState(87);
				match(EXIST);
				}
				break;
			case T__3:
				_localctx = new SutTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(88);
				match(T__3);
				setState(89);
				match(FILTER);
				setState(90);
				match(SUT_TYPE);
				}
				break;
			case RELATED_ACTION:
				_localctx = new RelatedActionExistsContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(91);
				match(RELATED_ACTION);
				setState(92);
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
			setState(101);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				_localctx = new SubStrategyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(95);
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
				setState(97); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(96);
					action();
					}
					}
					setState(99); 
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
		public TerminalNode RELATED_ACTION() { return getToken(StrategyParser.RELATED_ACTION, 0); }
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
		public TerminalNode VISIT_MODIFIER() { return getToken(StrategyParser.VISIT_MODIFIER, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
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
			setState(123);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				_localctx = new SelectPreviousActionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(103);
					match(NUMBER);
					}
				}

				setState(106);
				match(T__4);
				}
				break;
			case 2:
				_localctx = new SelectRandomActionContext(_localctx);
				enterOuterAlt(_localctx, 2);
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
				match(T__5);
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VISIT_MODIFIER) {
					{
					setState(111);
					match(VISIT_MODIFIER);
					}
				}

				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(114);
					match(FILTER);
					setState(115);
					match(ACTION_TYPE);
					}
				}

				}
				break;
			case 3:
				_localctx = new SelectRelatedActionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(118);
					match(NUMBER);
					}
				}

				setState(121);
				match(T__6);
				setState(122);
				match(RELATED_ACTION);
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
			return precpred(_ctx, 7);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3#\u0080\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\5\4;\n\4\3\4\3\4\3\4\7\4@\n\4\f\4\16\4C\13\4\3\5\3\5\5\5G\n\5\3\6"+
		"\3\6\5\6K\n\6\3\6\3\6\5\6O\n\6\3\7\3\7\3\7\5\7T\n\7\3\7\3\7\5\7X\n\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\5\7`\n\7\3\b\3\b\6\bd\n\b\r\b\16\be\5\bh\n\b\3"+
		"\t\5\tk\n\t\3\t\3\t\5\to\n\t\3\t\3\t\5\ts\n\t\3\t\3\t\5\tw\n\t\3\t\5\t"+
		"z\n\t\3\t\3\t\5\t~\n\t\3\t\2\3\6\n\2\4\6\b\n\f\16\20\2\4\3\2\21\23\3\2"+
		"\24\31\2\u0090\2\22\3\2\2\2\4\25\3\2\2\2\6:\3\2\2\2\bF\3\2\2\2\nH\3\2"+
		"\2\2\f_\3\2\2\2\16g\3\2\2\2\20}\3\2\2\2\22\23\5\4\3\2\23\24\7\2\2\3\24"+
		"\3\3\2\2\2\25\26\7\32\2\2\26\27\5\6\4\2\27\30\7\33\2\2\30\31\5\16\b\2"+
		"\31\32\7\34\2\2\32\33\5\16\b\2\33\5\3\2\2\2\34\35\b\4\1\2\35\36\7\20\2"+
		"\2\36;\5\6\4\13\37 \7\37\2\2 !\7\20\2\2!\"\5\6\4\2\"#\7 \2\2#;\3\2\2\2"+
		"$%\7\37\2\2%&\5\6\4\2&\'\t\2\2\2\'(\5\6\4\2()\7 \2\2);\3\2\2\2*+\5\b\5"+
		"\2+,\t\3\2\2,-\5\b\5\2-;\3\2\2\2./\7\37\2\2/\60\5\b\5\2\60\61\t\3\2\2"+
		"\61\62\5\b\5\2\62\63\7 \2\2\63;\3\2\2\2\64;\5\f\7\2\65\66\7\37\2\2\66"+
		"\67\5\f\7\2\678\7 \2\28;\3\2\2\29;\7\36\2\2:\34\3\2\2\2:\37\3\2\2\2:$"+
		"\3\2\2\2:*\3\2\2\2:.\3\2\2\2:\64\3\2\2\2:\65\3\2\2\2:9\3\2\2\2;A\3\2\2"+
		"\2<=\f\t\2\2=>\t\2\2\2>@\5\6\4\n?<\3\2\2\2@C\3\2\2\2A?\3\2\2\2AB\3\2\2"+
		"\2B\7\3\2\2\2CA\3\2\2\2DG\5\n\6\2EG\7\35\2\2FD\3\2\2\2FE\3\2\2\2G\t\3"+
		"\2\2\2HJ\7\3\2\2IK\7\n\2\2JI\3\2\2\2JK\3\2\2\2KN\3\2\2\2LM\7\17\2\2MO"+
		"\7\r\2\2NL\3\2\2\2NO\3\2\2\2O\13\3\2\2\2P`\7\4\2\2QS\7\5\2\2RT\7\n\2\2"+
		"SR\3\2\2\2ST\3\2\2\2TW\3\2\2\2UV\7\17\2\2VX\7\r\2\2WU\3\2\2\2WX\3\2\2"+
		"\2XY\3\2\2\2Y`\7\16\2\2Z[\7\6\2\2[\\\7\17\2\2\\`\7\f\2\2]^\7\13\2\2^`"+
		"\7\16\2\2_P\3\2\2\2_Q\3\2\2\2_Z\3\2\2\2_]\3\2\2\2`\r\3\2\2\2ah\5\4\3\2"+
		"bd\5\20\t\2cb\3\2\2\2de\3\2\2\2ec\3\2\2\2ef\3\2\2\2fh\3\2\2\2ga\3\2\2"+
		"\2gc\3\2\2\2h\17\3\2\2\2ik\7\35\2\2ji\3\2\2\2jk\3\2\2\2kl\3\2\2\2l~\7"+
		"\7\2\2mo\7\35\2\2nm\3\2\2\2no\3\2\2\2op\3\2\2\2pr\7\b\2\2qs\7\n\2\2rq"+
		"\3\2\2\2rs\3\2\2\2sv\3\2\2\2tu\7\17\2\2uw\7\r\2\2vt\3\2\2\2vw\3\2\2\2"+
		"w~\3\2\2\2xz\7\35\2\2yx\3\2\2\2yz\3\2\2\2z{\3\2\2\2{|\7\t\2\2|~\7\13\2"+
		"\2}j\3\2\2\2}n\3\2\2\2}y\3\2\2\2~\21\3\2\2\2\22:AFJNSW_egjnrvy}";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
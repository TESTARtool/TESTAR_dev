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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, VISIT_MODIFIER=7, RELATED_ACTION=8, 
		SUT_TYPE=9, ACTION_TYPE=10, FILTER=11, NOT=12, AND=13, XOR=14, OR=15, 
		IS=16, GT=17, GE=18, LT=19, LE=20, EQ=21, NE=22, IF=23, THEN=24, ELSE=25, 
		NUMBER=26, BOOLEAN=27, LP=28, RP=29, COMMENT=30, WHITESPACE=31, ANY=32;
	public static final int
		RULE_strategy_file = 0, RULE_strategy = 1, RULE_if_then_else = 2, RULE_bool_expr = 3, 
		RULE_number_expr = 4, RULE_number_of_actions = 5, RULE_state_boolean = 6, 
		RULE_action_expr = 7, RULE_action_list = 8, RULE_action = 9;
	private static String[] makeRuleNames() {
		return new String[] {
			"strategy_file", "strategy", "if_then_else", "bool_expr", "number_expr", 
			"number_of_actions", "state_boolean", "action_expr", "action_list", "action"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'n-actions'", "'state-changed'", "'any-exist'", "'sut'", "'select-previous'", 
			"'select-random'", null, null, null, null, null, null, null, null, null, 
			null, "'>'", "'>='", "'<'", "'<='", null, "'!='", null, null, null, null, 
			null, "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "VISIT_MODIFIER", "RELATED_ACTION", 
			"SUT_TYPE", "ACTION_TYPE", "FILTER", "NOT", "AND", "XOR", "OR", "IS", 
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
			setState(20);
			strategy();
			setState(21);
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
		public If_then_elseContext if_then_else() {
			return getRuleContext(If_then_elseContext.class,0);
		}
		public Action_listContext action_list() {
			return getRuleContext(Action_listContext.class,0);
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
			setState(25);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				enterOuterAlt(_localctx, 1);
				{
				setState(23);
				if_then_else();
				}
				break;
			case T__4:
			case T__5:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(24);
				action_list();
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

	public static class If_then_elseContext extends ParserRuleContext {
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
		public If_then_elseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_then_else; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterIf_then_else(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitIf_then_else(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitIf_then_else(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_then_elseContext if_then_else() throws RecognitionException {
		If_then_elseContext _localctx = new If_then_elseContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_if_then_else);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27);
			match(IF);
			setState(28);
			((If_then_elseContext)_localctx).ifExpr = bool_expr(0);
			setState(29);
			match(THEN);
			setState(30);
			((If_then_elseContext)_localctx).thenExpr = action_expr();
			setState(31);
			match(ELSE);
			setState(32);
			((If_then_elseContext)_localctx).elseExpr = action_expr();
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
	public static class PlainBoolContext extends Bool_exprContext {
		public TerminalNode BOOLEAN() { return getToken(StrategyParser.BOOLEAN, 0); }
		public PlainBoolContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterPlainBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitPlainBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitPlainBool(this);
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
		public TerminalNode IS() { return getToken(StrategyParser.IS, 0); }
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
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_bool_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(35);
				match(NOT);
				setState(36);
				((NotExprContext)_localctx).expr = bool_expr(9);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(37);
				match(LP);
				setState(38);
				match(NOT);
				setState(39);
				((NotExprContext)_localctx).expr = bool_expr(0);
				setState(40);
				match(RP);
				}
				break;
			case 3:
				{
				_localctx = new BoolOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(42);
				match(LP);
				setState(43);
				((BoolOprExprContext)_localctx).left = bool_expr(0);
				setState(44);
				((BoolOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AND) | (1L << XOR) | (1L << OR) | (1L << IS))) != 0)) ) {
					((BoolOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(45);
				((BoolOprExprContext)_localctx).right = bool_expr(0);
				setState(46);
				match(RP);
				}
				break;
			case 4:
				{
				_localctx = new NumberOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(48);
				((NumberOprExprContext)_localctx).left = number_expr();
				setState(49);
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
				setState(50);
				((NumberOprExprContext)_localctx).right = number_expr();
				}
				break;
			case 5:
				{
				_localctx = new NumberOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(52);
				match(LP);
				setState(53);
				((NumberOprExprContext)_localctx).left = number_expr();
				setState(54);
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
				setState(55);
				((NumberOprExprContext)_localctx).right = number_expr();
				setState(56);
				match(RP);
				}
				break;
			case 6:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(58);
				state_boolean();
				}
				break;
			case 7:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(59);
				match(LP);
				setState(60);
				state_boolean();
				setState(61);
				match(RP);
				}
				break;
			case 8:
				{
				_localctx = new PlainBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(63);
				match(BOOLEAN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(71);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new BoolOprExprContext(new Bool_exprContext(_parentctx, _parentState));
					((BoolOprExprContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
					setState(66);
					if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
					setState(67);
					((BoolOprExprContext)_localctx).opr = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AND) | (1L << XOR) | (1L << OR) | (1L << IS))) != 0)) ) {
						((BoolOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(68);
					((BoolOprExprContext)_localctx).right = bool_expr(8);
					}
					} 
				}
				setState(73);
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
		enterRule(_localctx, 8, RULE_number_expr);
		try {
			setState(76);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				number_of_actions();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(75);
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
		enterRule(_localctx, 10, RULE_number_of_actions);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			match(T__0);
			setState(80);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(79);
				match(VISIT_MODIFIER);
				}
				break;
			}
			setState(84);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(82);
				match(FILTER);
				setState(83);
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
	public static class AnyExistRelatedActionContext extends State_booleanContext {
		public TerminalNode RELATED_ACTION() { return getToken(StrategyParser.RELATED_ACTION, 0); }
		public TerminalNode VISIT_MODIFIER() { return getToken(StrategyParser.VISIT_MODIFIER, 0); }
		public AnyExistRelatedActionContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAnyExistRelatedAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAnyExistRelatedAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAnyExistRelatedAction(this);
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
	public static class AnyExistContext extends State_booleanContext {
		public TerminalNode VISIT_MODIFIER() { return getToken(StrategyParser.VISIT_MODIFIER, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public AnyExistContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAnyExist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAnyExist(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAnyExist(this);
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
		enterRule(_localctx, 12, RULE_state_boolean);
		int _la;
		try {
			setState(103);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new StateChangedContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(86);
				match(T__1);
				}
				break;
			case 2:
				_localctx = new AnyExistRelatedActionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(87);
				match(T__2);
				setState(89);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VISIT_MODIFIER) {
					{
					setState(88);
					match(VISIT_MODIFIER);
					}
				}

				setState(91);
				match(RELATED_ACTION);
				}
				break;
			case 3:
				_localctx = new AnyExistContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(92);
				match(T__2);
				setState(94);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					setState(93);
					match(VISIT_MODIFIER);
					}
					break;
				}
				setState(98);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
				case 1:
					{
					setState(96);
					match(FILTER);
					setState(97);
					match(ACTION_TYPE);
					}
					break;
				}
				}
				break;
			case 4:
				_localctx = new SutTypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(100);
				match(T__3);
				setState(101);
				match(FILTER);
				setState(102);
				match(SUT_TYPE);
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

	public static class Action_exprContext extends ParserRuleContext {
		public If_then_elseContext if_then_else() {
			return getRuleContext(If_then_elseContext.class,0);
		}
		public Action_listContext action_list() {
			return getRuleContext(Action_listContext.class,0);
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
		enterRule(_localctx, 14, RULE_action_expr);
		try {
			setState(107);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				if_then_else();
				}
				break;
			case T__4:
			case T__5:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				action_list();
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

	public static class Action_listContext extends ParserRuleContext {
		public List<ActionContext> action() {
			return getRuleContexts(ActionContext.class);
		}
		public ActionContext action(int i) {
			return getRuleContext(ActionContext.class,i);
		}
		public Action_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAction_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAction_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAction_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_listContext action_list() throws RecognitionException {
		Action_listContext _localctx = new Action_listContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_action_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(109);
				action();
				}
				}
				setState(112); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << NUMBER))) != 0) );
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
		public TerminalNode VISIT_MODIFIER() { return getToken(StrategyParser.VISIT_MODIFIER, 0); }
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
		enterRule(_localctx, 18, RULE_action);
		int _la;
		try {
			setState(137);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				_localctx = new SelectPreviousActionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(114);
					match(NUMBER);
					}
				}

				setState(117);
				match(T__4);
				}
				break;
			case 2:
				_localctx = new SelectRelatedActionContext(_localctx);
				enterOuterAlt(_localctx, 2);
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
				match(T__5);
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VISIT_MODIFIER) {
					{
					setState(122);
					match(VISIT_MODIFIER);
					}
				}

				setState(125);
				match(RELATED_ACTION);
				}
				break;
			case 3:
				_localctx = new SelectRandomActionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(126);
					match(NUMBER);
					}
				}

				setState(129);
				match(T__5);
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VISIT_MODIFIER) {
					{
					setState(130);
					match(VISIT_MODIFIER);
					}
				}

				setState(135);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(133);
					match(FILTER);
					setState(134);
					match(ACTION_TYPE);
					}
				}

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
		case 3:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\"\u008e\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\2\3\3\3\3\5\3\34\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5C\n\5\3\5\3\5\3\5\7\5H\n"+
		"\5\f\5\16\5K\13\5\3\6\3\6\5\6O\n\6\3\7\3\7\5\7S\n\7\3\7\3\7\5\7W\n\7\3"+
		"\b\3\b\3\b\5\b\\\n\b\3\b\3\b\3\b\5\ba\n\b\3\b\3\b\5\be\n\b\3\b\3\b\3\b"+
		"\5\bj\n\b\3\t\3\t\5\tn\n\t\3\n\6\nq\n\n\r\n\16\nr\3\13\5\13v\n\13\3\13"+
		"\3\13\5\13z\n\13\3\13\3\13\5\13~\n\13\3\13\3\13\5\13\u0082\n\13\3\13\3"+
		"\13\5\13\u0086\n\13\3\13\3\13\5\13\u008a\n\13\5\13\u008c\n\13\3\13\2\3"+
		"\b\f\2\4\6\b\n\f\16\20\22\24\2\4\3\2\17\22\3\2\23\30\2\u009f\2\26\3\2"+
		"\2\2\4\33\3\2\2\2\6\35\3\2\2\2\bB\3\2\2\2\nN\3\2\2\2\fP\3\2\2\2\16i\3"+
		"\2\2\2\20m\3\2\2\2\22p\3\2\2\2\24\u008b\3\2\2\2\26\27\5\4\3\2\27\30\7"+
		"\2\2\3\30\3\3\2\2\2\31\34\5\6\4\2\32\34\5\22\n\2\33\31\3\2\2\2\33\32\3"+
		"\2\2\2\34\5\3\2\2\2\35\36\7\31\2\2\36\37\5\b\5\2\37 \7\32\2\2 !\5\20\t"+
		"\2!\"\7\33\2\2\"#\5\20\t\2#\7\3\2\2\2$%\b\5\1\2%&\7\16\2\2&C\5\b\5\13"+
		"\'(\7\36\2\2()\7\16\2\2)*\5\b\5\2*+\7\37\2\2+C\3\2\2\2,-\7\36\2\2-.\5"+
		"\b\5\2./\t\2\2\2/\60\5\b\5\2\60\61\7\37\2\2\61C\3\2\2\2\62\63\5\n\6\2"+
		"\63\64\t\3\2\2\64\65\5\n\6\2\65C\3\2\2\2\66\67\7\36\2\2\678\5\n\6\289"+
		"\t\3\2\29:\5\n\6\2:;\7\37\2\2;C\3\2\2\2<C\5\16\b\2=>\7\36\2\2>?\5\16\b"+
		"\2?@\7\37\2\2@C\3\2\2\2AC\7\35\2\2B$\3\2\2\2B\'\3\2\2\2B,\3\2\2\2B\62"+
		"\3\2\2\2B\66\3\2\2\2B<\3\2\2\2B=\3\2\2\2BA\3\2\2\2CI\3\2\2\2DE\f\t\2\2"+
		"EF\t\2\2\2FH\5\b\5\nGD\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2J\t\3\2\2"+
		"\2KI\3\2\2\2LO\5\f\7\2MO\7\34\2\2NL\3\2\2\2NM\3\2\2\2O\13\3\2\2\2PR\7"+
		"\3\2\2QS\7\t\2\2RQ\3\2\2\2RS\3\2\2\2SV\3\2\2\2TU\7\r\2\2UW\7\f\2\2VT\3"+
		"\2\2\2VW\3\2\2\2W\r\3\2\2\2Xj\7\4\2\2Y[\7\5\2\2Z\\\7\t\2\2[Z\3\2\2\2["+
		"\\\3\2\2\2\\]\3\2\2\2]j\7\n\2\2^`\7\5\2\2_a\7\t\2\2`_\3\2\2\2`a\3\2\2"+
		"\2ad\3\2\2\2bc\7\r\2\2ce\7\f\2\2db\3\2\2\2de\3\2\2\2ej\3\2\2\2fg\7\6\2"+
		"\2gh\7\r\2\2hj\7\13\2\2iX\3\2\2\2iY\3\2\2\2i^\3\2\2\2if\3\2\2\2j\17\3"+
		"\2\2\2kn\5\6\4\2ln\5\22\n\2mk\3\2\2\2ml\3\2\2\2n\21\3\2\2\2oq\5\24\13"+
		"\2po\3\2\2\2qr\3\2\2\2rp\3\2\2\2rs\3\2\2\2s\23\3\2\2\2tv\7\34\2\2ut\3"+
		"\2\2\2uv\3\2\2\2vw\3\2\2\2w\u008c\7\7\2\2xz\7\34\2\2yx\3\2\2\2yz\3\2\2"+
		"\2z{\3\2\2\2{}\7\b\2\2|~\7\t\2\2}|\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177"+
		"\u008c\7\n\2\2\u0080\u0082\7\34\2\2\u0081\u0080\3\2\2\2\u0081\u0082\3"+
		"\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\7\b\2\2\u0084\u0086\7\t\2\2\u0085"+
		"\u0084\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0089\3\2\2\2\u0087\u0088\7\r"+
		"\2\2\u0088\u008a\7\f\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a"+
		"\u008c\3\2\2\2\u008bu\3\2\2\2\u008by\3\2\2\2\u008b\u0081\3\2\2\2\u008c"+
		"\25\3\2\2\2\25\33BINRV[`dimruy}\u0081\u0085\u0089\u008b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
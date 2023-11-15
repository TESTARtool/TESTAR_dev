// Generated from C:/Users/lh3/IdeaProjects/TESTAR/strategy/src/antlrfour\Strategy.g4 by ANTLR 4.12.0
package antlrfour;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class StrategyParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, MODIFIER=8, RELATION=9, 
		SUT=10, ACTION_TYPE=11, FILTER=12, NOT=13, AND=14, XOR=15, OR=16, EQUALS=17, 
		GT=18, GE=19, LT=20, LE=21, EQ=22, NE=23, INT=24, BOOL=25, IF=26, THEN=27, 
		ELSE=28, LP=29, RP=30, COMMENT=31, WHITESPACE=32, ANY=33;
	public static final int
		RULE_strategy_file = 0, RULE_strategy = 1, RULE_action = 2, RULE_cond_action = 3, 
		RULE_uncond_action = 4, RULE_bool_expr = 5, RULE_state_boolean = 6, RULE_int_expr = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"strategy_file", "strategy", "action", "cond_action", "uncond_action", 
			"bool_expr", "state_boolean", "int_expr"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'select-previous'", "'select-random'", "'state-changed'", "'any-exist'", 
			"'previous-exist'", "'sut'", "'n-actions'", null, null, null, null, null, 
			null, null, null, null, null, "'>'", "'>='", "'<'", "'<='", null, "'!='", 
			null, null, null, null, null, "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "MODIFIER", "RELATION", 
			"SUT", "ACTION_TYPE", "FILTER", "NOT", "AND", "XOR", "OR", "EQUALS", 
			"GT", "GE", "LT", "LE", "EQ", "NE", "INT", "BOOL", "IF", "THEN", "ELSE", 
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

	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
	public static class StrategyContext extends ParserRuleContext {
		public List<ActionContext> action() {
			return getRuleContexts(ActionContext.class);
		}
		public ActionContext action(int i) {
			return getRuleContext(ActionContext.class,i);
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
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(20); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(19);
					action();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(22); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	@SuppressWarnings("CheckReturnValue")
	public static class ActionContext extends ParserRuleContext {
		public Cond_actionContext cond_action() {
			return getRuleContext(Cond_actionContext.class,0);
		}
		public Uncond_actionContext uncond_action() {
			return getRuleContext(Uncond_actionContext.class,0);
		}
		public ActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionContext action() throws RecognitionException {
		ActionContext _localctx = new ActionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_action);
		try {
			setState(26);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(24);
				cond_action();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(25);
				uncond_action();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Cond_actionContext extends ParserRuleContext {
		public Bool_exprContext ifExpr;
		public StrategyContext thenExpr;
		public StrategyContext elseExpr;
		public TerminalNode IF() { return getToken(StrategyParser.IF, 0); }
		public TerminalNode THEN() { return getToken(StrategyParser.THEN, 0); }
		public TerminalNode ELSE() { return getToken(StrategyParser.ELSE, 0); }
		public Bool_exprContext bool_expr() {
			return getRuleContext(Bool_exprContext.class,0);
		}
		public List<StrategyContext> strategy() {
			return getRuleContexts(StrategyContext.class);
		}
		public StrategyContext strategy(int i) {
			return getRuleContext(StrategyContext.class,i);
		}
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public Cond_actionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cond_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterCond_action(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitCond_action(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitCond_action(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Cond_actionContext cond_action() throws RecognitionException {
		Cond_actionContext _localctx = new Cond_actionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_cond_action);
		int _la;
		try {
			setState(46);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(28);
					match(INT);
					}
				}

				setState(31);
				match(IF);
				setState(32);
				((Cond_actionContext)_localctx).ifExpr = bool_expr(0);
				setState(33);
				match(THEN);
				setState(34);
				((Cond_actionContext)_localctx).thenExpr = strategy();
				setState(35);
				match(ELSE);
				setState(36);
				((Cond_actionContext)_localctx).elseExpr = strategy();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(39);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(38);
					match(INT);
					}
				}

				setState(41);
				match(IF);
				setState(42);
				((Cond_actionContext)_localctx).ifExpr = bool_expr(0);
				setState(43);
				match(THEN);
				setState(44);
				((Cond_actionContext)_localctx).thenExpr = strategy();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Uncond_actionContext extends ParserRuleContext {
		public Uncond_actionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uncond_action; }
	 
		public Uncond_actionContext() { }
		public void copyFrom(Uncond_actionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SelectPreviousActionContext extends Uncond_actionContext {
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public SelectPreviousActionContext(Uncond_actionContext ctx) { copyFrom(ctx); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class SelectRandomActionContext extends Uncond_actionContext {
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public TerminalNode MODIFIER() { return getToken(StrategyParser.MODIFIER, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public SelectRandomActionContext(Uncond_actionContext ctx) { copyFrom(ctx); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class SelectRelationContext extends Uncond_actionContext {
		public TerminalNode RELATION() { return getToken(StrategyParser.RELATION, 0); }
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public TerminalNode MODIFIER() { return getToken(StrategyParser.MODIFIER, 0); }
		public SelectRelationContext(Uncond_actionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSelectRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSelectRelation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSelectRelation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Uncond_actionContext uncond_action() throws RecognitionException {
		Uncond_actionContext _localctx = new Uncond_actionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_uncond_action);
		int _la;
		try {
			setState(71);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				_localctx = new SelectPreviousActionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(49);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(48);
					match(INT);
					}
				}

				setState(51);
				match(T__0);
				}
				break;
			case 2:
				_localctx = new SelectRelationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(53);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(52);
					match(INT);
					}
				}

				setState(55);
				match(T__1);
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(56);
					match(MODIFIER);
					}
				}

				setState(59);
				match(RELATION);
				}
				break;
			case 3:
				_localctx = new SelectRandomActionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(60);
					match(INT);
					}
				}

				setState(63);
				match(T__1);
				setState(65);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(64);
					match(MODIFIER);
					}
				}

				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(67);
					match(FILTER);
					setState(68);
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

	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class PlainBoolContext extends Bool_exprContext {
		public TerminalNode BOOL() { return getToken(StrategyParser.BOOL, 0); }
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class IntOprExprContext extends Bool_exprContext {
		public Int_exprContext left;
		public Token opr;
		public Int_exprContext right;
		public List<Int_exprContext> int_expr() {
			return getRuleContexts(Int_exprContext.class);
		}
		public Int_exprContext int_expr(int i) {
			return getRuleContext(Int_exprContext.class,i);
		}
		public TerminalNode LT() { return getToken(StrategyParser.LT, 0); }
		public TerminalNode LE() { return getToken(StrategyParser.LE, 0); }
		public TerminalNode GT() { return getToken(StrategyParser.GT, 0); }
		public TerminalNode GE() { return getToken(StrategyParser.GE, 0); }
		public TerminalNode EQ() { return getToken(StrategyParser.EQ, 0); }
		public TerminalNode NE() { return getToken(StrategyParser.NE, 0); }
		public TerminalNode LP() { return getToken(StrategyParser.LP, 0); }
		public TerminalNode RP() { return getToken(StrategyParser.RP, 0); }
		public IntOprExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterIntOprExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitIntOprExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitIntOprExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
		public TerminalNode EQUALS() { return getToken(StrategyParser.EQUALS, 0); }
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

	public final Bool_exprContext bool_expr() throws RecognitionException {
		return bool_expr(0);
	}

	private Bool_exprContext bool_expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Bool_exprContext _localctx = new Bool_exprContext(_ctx, _parentState);
		Bool_exprContext _prevctx = _localctx;
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_bool_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(74);
				match(NOT);
				setState(75);
				((NotExprContext)_localctx).expr = bool_expr(9);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(76);
				match(LP);
				setState(77);
				match(NOT);
				setState(78);
				((NotExprContext)_localctx).expr = bool_expr(0);
				setState(79);
				match(RP);
				}
				break;
			case 3:
				{
				_localctx = new BoolOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(81);
				match(LP);
				setState(82);
				((BoolOprExprContext)_localctx).left = bool_expr(0);
				setState(83);
				((BoolOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 245760L) != 0)) ) {
					((BoolOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(84);
				((BoolOprExprContext)_localctx).right = bool_expr(0);
				setState(85);
				match(RP);
				}
				break;
			case 4:
				{
				_localctx = new IntOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(87);
				((IntOprExprContext)_localctx).left = int_expr();
				setState(88);
				((IntOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 16515072L) != 0)) ) {
					((IntOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(89);
				((IntOprExprContext)_localctx).right = int_expr();
				}
				break;
			case 5:
				{
				_localctx = new IntOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(91);
				match(LP);
				setState(92);
				((IntOprExprContext)_localctx).left = int_expr();
				setState(93);
				((IntOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 16515072L) != 0)) ) {
					((IntOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(94);
				((IntOprExprContext)_localctx).right = int_expr();
				setState(95);
				match(RP);
				}
				break;
			case 6:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(97);
				state_boolean();
				}
				break;
			case 7:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(98);
				match(LP);
				setState(99);
				state_boolean();
				setState(100);
				match(RP);
				}
				break;
			case 8:
				{
				_localctx = new PlainBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(102);
				match(BOOL);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(110);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new BoolOprExprContext(new Bool_exprContext(_parentctx, _parentState));
					((BoolOprExprContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
					setState(105);
					if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
					setState(106);
					((BoolOprExprContext)_localctx).opr = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 245760L) != 0)) ) {
						((BoolOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(107);
					((BoolOprExprContext)_localctx).right = bool_expr(8);
					}
					} 
				}
				setState(112);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class AnyExistRelatedActionContext extends State_booleanContext {
		public TerminalNode RELATION() { return getToken(StrategyParser.RELATION, 0); }
		public TerminalNode MODIFIER() { return getToken(StrategyParser.MODIFIER, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class PreviousExistContext extends State_booleanContext {
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public PreviousExistContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterPreviousExist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitPreviousExist(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitPreviousExist(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SutTypeContext extends State_booleanContext {
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode SUT() { return getToken(StrategyParser.SUT, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class AnyExistContext extends State_booleanContext {
		public TerminalNode MODIFIER() { return getToken(StrategyParser.MODIFIER, 0); }
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
	@SuppressWarnings("CheckReturnValue")
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
			setState(135);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				_localctx = new StateChangedContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(113);
				match(T__2);
				}
				break;
			case 2:
				_localctx = new AnyExistRelatedActionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(114);
				match(T__3);
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(115);
					match(MODIFIER);
					}
				}

				setState(118);
				match(RELATION);
				}
				break;
			case 3:
				_localctx = new AnyExistContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(119);
				match(T__3);
				setState(121);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
				case 1:
					{
					setState(120);
					match(MODIFIER);
					}
					break;
				}
				setState(125);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
				case 1:
					{
					setState(123);
					match(FILTER);
					setState(124);
					match(ACTION_TYPE);
					}
					break;
				}
				}
				break;
			case 4:
				_localctx = new PreviousExistContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(127);
				match(T__4);
				setState(130);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(128);
					match(FILTER);
					setState(129);
					match(ACTION_TYPE);
					}
					break;
				}
				}
				break;
			case 5:
				_localctx = new SutTypeContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(132);
				match(T__5);
				setState(133);
				match(FILTER);
				setState(134);
				match(SUT);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Int_exprContext extends ParserRuleContext {
		public Int_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_int_expr; }
	 
		public Int_exprContext() { }
		public void copyFrom(Int_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PlainIntContext extends Int_exprContext {
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public PlainIntContext(Int_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterPlainInt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitPlainInt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitPlainInt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NActionsContext extends Int_exprContext {
		public TerminalNode MODIFIER() { return getToken(StrategyParser.MODIFIER, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public NActionsContext(Int_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNActions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNActions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNActions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Int_exprContext int_expr() throws RecognitionException {
		Int_exprContext _localctx = new Int_exprContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_int_expr);
		try {
			setState(146);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				_localctx = new NActionsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(137);
				match(T__6);
				setState(139);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
				case 1:
					{
					setState(138);
					match(MODIFIER);
					}
					break;
				}
				setState(143);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
				case 1:
					{
					setState(141);
					match(FILTER);
					setState(142);
					match(ACTION_TYPE);
					}
					break;
				}
				}
				break;
			case INT:
				_localctx = new PlainIntContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(145);
				match(INT);
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
		case 5:
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
		"\u0004\u0001!\u0095\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0004\u0001\u0015\b\u0001\u000b"+
		"\u0001\f\u0001\u0016\u0001\u0002\u0001\u0002\u0003\u0002\u001b\b\u0002"+
		"\u0001\u0003\u0003\u0003\u001e\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"(\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u0003/\b\u0003\u0001\u0004\u0003\u00042\b\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u00046\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004:\b\u0004"+
		"\u0001\u0004\u0001\u0004\u0003\u0004>\b\u0004\u0001\u0004\u0001\u0004"+
		"\u0003\u0004B\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004F\b\u0004\u0003"+
		"\u0004H\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0003\u0005h\b\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0005\u0005m\b\u0005\n\u0005\f\u0005p\t\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0003\u0006u\b\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0003\u0006z\b\u0006\u0001\u0006\u0001\u0006\u0003\u0006~\b\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u0083\b\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0003\u0006\u0088\b\u0006\u0001\u0007\u0001\u0007"+
		"\u0003\u0007\u008c\b\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0090\b"+
		"\u0007\u0001\u0007\u0003\u0007\u0093\b\u0007\u0001\u0007\u0000\u0001\n"+
		"\b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0000\u0002\u0001\u0000\u000e\u0011"+
		"\u0001\u0000\u0012\u0017\u00ac\u0000\u0010\u0001\u0000\u0000\u0000\u0002"+
		"\u0014\u0001\u0000\u0000\u0000\u0004\u001a\u0001\u0000\u0000\u0000\u0006"+
		".\u0001\u0000\u0000\u0000\bG\u0001\u0000\u0000\u0000\ng\u0001\u0000\u0000"+
		"\u0000\f\u0087\u0001\u0000\u0000\u0000\u000e\u0092\u0001\u0000\u0000\u0000"+
		"\u0010\u0011\u0003\u0002\u0001\u0000\u0011\u0012\u0005\u0000\u0000\u0001"+
		"\u0012\u0001\u0001\u0000\u0000\u0000\u0013\u0015\u0003\u0004\u0002\u0000"+
		"\u0014\u0013\u0001\u0000\u0000\u0000\u0015\u0016\u0001\u0000\u0000\u0000"+
		"\u0016\u0014\u0001\u0000\u0000\u0000\u0016\u0017\u0001\u0000\u0000\u0000"+
		"\u0017\u0003\u0001\u0000\u0000\u0000\u0018\u001b\u0003\u0006\u0003\u0000"+
		"\u0019\u001b\u0003\b\u0004\u0000\u001a\u0018\u0001\u0000\u0000\u0000\u001a"+
		"\u0019\u0001\u0000\u0000\u0000\u001b\u0005\u0001\u0000\u0000\u0000\u001c"+
		"\u001e\u0005\u0018\u0000\u0000\u001d\u001c\u0001\u0000\u0000\u0000\u001d"+
		"\u001e\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f"+
		" \u0005\u001a\u0000\u0000 !\u0003\n\u0005\u0000!\"\u0005\u001b\u0000\u0000"+
		"\"#\u0003\u0002\u0001\u0000#$\u0005\u001c\u0000\u0000$%\u0003\u0002\u0001"+
		"\u0000%/\u0001\u0000\u0000\u0000&(\u0005\u0018\u0000\u0000\'&\u0001\u0000"+
		"\u0000\u0000\'(\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)*\u0005"+
		"\u001a\u0000\u0000*+\u0003\n\u0005\u0000+,\u0005\u001b\u0000\u0000,-\u0003"+
		"\u0002\u0001\u0000-/\u0001\u0000\u0000\u0000.\u001d\u0001\u0000\u0000"+
		"\u0000.\'\u0001\u0000\u0000\u0000/\u0007\u0001\u0000\u0000\u000002\u0005"+
		"\u0018\u0000\u000010\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u0000"+
		"23\u0001\u0000\u0000\u00003H\u0005\u0001\u0000\u000046\u0005\u0018\u0000"+
		"\u000054\u0001\u0000\u0000\u000056\u0001\u0000\u0000\u000067\u0001\u0000"+
		"\u0000\u000079\u0005\u0002\u0000\u00008:\u0005\b\u0000\u000098\u0001\u0000"+
		"\u0000\u00009:\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;H\u0005"+
		"\t\u0000\u0000<>\u0005\u0018\u0000\u0000=<\u0001\u0000\u0000\u0000=>\u0001"+
		"\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?A\u0005\u0002\u0000\u0000"+
		"@B\u0005\b\u0000\u0000A@\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000"+
		"BE\u0001\u0000\u0000\u0000CD\u0005\f\u0000\u0000DF\u0005\u000b\u0000\u0000"+
		"EC\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000FH\u0001\u0000\u0000"+
		"\u0000G1\u0001\u0000\u0000\u0000G5\u0001\u0000\u0000\u0000G=\u0001\u0000"+
		"\u0000\u0000H\t\u0001\u0000\u0000\u0000IJ\u0006\u0005\uffff\uffff\u0000"+
		"JK\u0005\r\u0000\u0000Kh\u0003\n\u0005\tLM\u0005\u001d\u0000\u0000MN\u0005"+
		"\r\u0000\u0000NO\u0003\n\u0005\u0000OP\u0005\u001e\u0000\u0000Ph\u0001"+
		"\u0000\u0000\u0000QR\u0005\u001d\u0000\u0000RS\u0003\n\u0005\u0000ST\u0007"+
		"\u0000\u0000\u0000TU\u0003\n\u0005\u0000UV\u0005\u001e\u0000\u0000Vh\u0001"+
		"\u0000\u0000\u0000WX\u0003\u000e\u0007\u0000XY\u0007\u0001\u0000\u0000"+
		"YZ\u0003\u000e\u0007\u0000Zh\u0001\u0000\u0000\u0000[\\\u0005\u001d\u0000"+
		"\u0000\\]\u0003\u000e\u0007\u0000]^\u0007\u0001\u0000\u0000^_\u0003\u000e"+
		"\u0007\u0000_`\u0005\u001e\u0000\u0000`h\u0001\u0000\u0000\u0000ah\u0003"+
		"\f\u0006\u0000bc\u0005\u001d\u0000\u0000cd\u0003\f\u0006\u0000de\u0005"+
		"\u001e\u0000\u0000eh\u0001\u0000\u0000\u0000fh\u0005\u0019\u0000\u0000"+
		"gI\u0001\u0000\u0000\u0000gL\u0001\u0000\u0000\u0000gQ\u0001\u0000\u0000"+
		"\u0000gW\u0001\u0000\u0000\u0000g[\u0001\u0000\u0000\u0000ga\u0001\u0000"+
		"\u0000\u0000gb\u0001\u0000\u0000\u0000gf\u0001\u0000\u0000\u0000hn\u0001"+
		"\u0000\u0000\u0000ij\n\u0007\u0000\u0000jk\u0007\u0000\u0000\u0000km\u0003"+
		"\n\u0005\bli\u0001\u0000\u0000\u0000mp\u0001\u0000\u0000\u0000nl\u0001"+
		"\u0000\u0000\u0000no\u0001\u0000\u0000\u0000o\u000b\u0001\u0000\u0000"+
		"\u0000pn\u0001\u0000\u0000\u0000q\u0088\u0005\u0003\u0000\u0000rt\u0005"+
		"\u0004\u0000\u0000su\u0005\b\u0000\u0000ts\u0001\u0000\u0000\u0000tu\u0001"+
		"\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000v\u0088\u0005\t\u0000\u0000"+
		"wy\u0005\u0004\u0000\u0000xz\u0005\b\u0000\u0000yx\u0001\u0000\u0000\u0000"+
		"yz\u0001\u0000\u0000\u0000z}\u0001\u0000\u0000\u0000{|\u0005\f\u0000\u0000"+
		"|~\u0005\u000b\u0000\u0000}{\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000"+
		"\u0000~\u0088\u0001\u0000\u0000\u0000\u007f\u0082\u0005\u0005\u0000\u0000"+
		"\u0080\u0081\u0005\f\u0000\u0000\u0081\u0083\u0005\u000b\u0000\u0000\u0082"+
		"\u0080\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083"+
		"\u0088\u0001\u0000\u0000\u0000\u0084\u0085\u0005\u0006\u0000\u0000\u0085"+
		"\u0086\u0005\f\u0000\u0000\u0086\u0088\u0005\n\u0000\u0000\u0087q\u0001"+
		"\u0000\u0000\u0000\u0087r\u0001\u0000\u0000\u0000\u0087w\u0001\u0000\u0000"+
		"\u0000\u0087\u007f\u0001\u0000\u0000\u0000\u0087\u0084\u0001\u0000\u0000"+
		"\u0000\u0088\r\u0001\u0000\u0000\u0000\u0089\u008b\u0005\u0007\u0000\u0000"+
		"\u008a\u008c\u0005\b\u0000\u0000\u008b\u008a\u0001\u0000\u0000\u0000\u008b"+
		"\u008c\u0001\u0000\u0000\u0000\u008c\u008f\u0001\u0000\u0000\u0000\u008d"+
		"\u008e\u0005\f\u0000\u0000\u008e\u0090\u0005\u000b\u0000\u0000\u008f\u008d"+
		"\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000\u0000\u0000\u0090\u0093"+
		"\u0001\u0000\u0000\u0000\u0091\u0093\u0005\u0018\u0000\u0000\u0092\u0089"+
		"\u0001\u0000\u0000\u0000\u0092\u0091\u0001\u0000\u0000\u0000\u0093\u000f"+
		"\u0001\u0000\u0000\u0000\u0016\u0016\u001a\u001d\'.159=AEGgnty}\u0082"+
		"\u0087\u008b\u008f\u0092";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
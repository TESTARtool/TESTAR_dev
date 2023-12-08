// Generated from C:/Users/testar/Desktop/TESTAR_dev/strategy/src/antlrfour/Strategy.g4 by ANTLR 4.13.1
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
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		VISIT_STATUS=10, RELATION=11, SUT=12, ACTION_TYPE=13, FILTER=14, NOT=15, 
		AND=16, XOR=17, OR=18, EQUALS=19, GT=20, GE=21, LT=22, LE=23, EQ=24, NE=25, 
		INT=26, BOOL=27, IF=28, THEN=29, ELSE=30, LP=31, RP=32, COMMENT=33, WHITESPACE=34, 
		ANY=35;
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
			null, "'repeat-previous'", "'select-previous'", "'select-random'", "'state-changed'", 
			"'sut'", "'any-exist'", "'previous-exist'", "'n-actions'", "'n-previous'", 
			null, null, null, null, null, null, null, null, null, null, "'>'", "'>='", 
			"'<'", "'<='", null, "'!='", null, null, null, null, null, "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "VISIT_STATUS", 
			"RELATION", "SUT", "ACTION_TYPE", "FILTER", "NOT", "AND", "XOR", "OR", 
			"EQUALS", "GT", "GE", "LT", "LE", "EQ", "NE", "INT", "BOOL", "IF", "THEN", 
			"ELSE", "LP", "RP", "COMMENT", "WHITESPACE", "ANY"
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
		public TerminalNode VISIT_STATUS() { return getToken(StrategyParser.VISIT_STATUS, 0); }
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
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
		public TerminalNode VISIT_STATUS() { return getToken(StrategyParser.VISIT_STATUS, 0); }
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
	public static class RepeatPreviousActionContext extends Uncond_actionContext {
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public RepeatPreviousActionContext(Uncond_actionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterRepeatPreviousAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitRepeatPreviousAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitRepeatPreviousAction(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SelectByRelationContext extends Uncond_actionContext {
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode RELATION() { return getToken(StrategyParser.RELATION, 0); }
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public TerminalNode VISIT_STATUS() { return getToken(StrategyParser.VISIT_STATUS, 0); }
		public SelectByRelationContext(Uncond_actionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterSelectByRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitSelectByRelation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitSelectByRelation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Uncond_actionContext uncond_action() throws RecognitionException {
		Uncond_actionContext _localctx = new Uncond_actionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_uncond_action);
		int _la;
		try {
			setState(81);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new RepeatPreviousActionContext(_localctx);
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
				_localctx = new SelectPreviousActionContext(_localctx);
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
				setState(56);
				match(VISIT_STATUS);
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(57);
					match(FILTER);
					setState(58);
					match(ACTION_TYPE);
					}
				}

				}
				break;
			case 3:
				_localctx = new SelectByRelationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(62);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(61);
					match(INT);
					}
				}

				setState(64);
				match(T__2);
				setState(66);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VISIT_STATUS) {
					{
					setState(65);
					match(VISIT_STATUS);
					}
				}

				setState(68);
				match(FILTER);
				setState(69);
				match(RELATION);
				}
				break;
			case 4:
				_localctx = new SelectRandomActionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(70);
					match(INT);
					}
				}

				setState(73);
				match(T__2);
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VISIT_STATUS) {
					{
					setState(74);
					match(VISIT_STATUS);
					}
				}

				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(77);
					match(FILTER);
					setState(78);
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
			setState(113);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(84);
				match(NOT);
				setState(85);
				((NotExprContext)_localctx).expr = bool_expr(9);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(86);
				match(LP);
				setState(87);
				match(NOT);
				setState(88);
				((NotExprContext)_localctx).expr = bool_expr(0);
				setState(89);
				match(RP);
				}
				break;
			case 3:
				{
				_localctx = new BoolOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(91);
				match(LP);
				setState(92);
				((BoolOprExprContext)_localctx).left = bool_expr(0);
				setState(93);
				((BoolOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 983040L) != 0)) ) {
					((BoolOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(94);
				((BoolOprExprContext)_localctx).right = bool_expr(0);
				setState(95);
				match(RP);
				}
				break;
			case 4:
				{
				_localctx = new IntOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(97);
				((IntOprExprContext)_localctx).left = int_expr();
				setState(98);
				((IntOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 66060288L) != 0)) ) {
					((IntOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(99);
				((IntOprExprContext)_localctx).right = int_expr();
				}
				break;
			case 5:
				{
				_localctx = new IntOprExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(101);
				match(LP);
				setState(102);
				((IntOprExprContext)_localctx).left = int_expr();
				setState(103);
				((IntOprExprContext)_localctx).opr = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 66060288L) != 0)) ) {
					((IntOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(104);
				((IntOprExprContext)_localctx).right = int_expr();
				setState(105);
				match(RP);
				}
				break;
			case 6:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(107);
				state_boolean();
				}
				break;
			case 7:
				{
				_localctx = new StateBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(108);
				match(LP);
				setState(109);
				state_boolean();
				setState(110);
				match(RP);
				}
				break;
			case 8:
				{
				_localctx = new PlainBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(112);
				match(BOOL);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(120);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new BoolOprExprContext(new Bool_exprContext(_parentctx, _parentState));
					((BoolOprExprContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
					setState(115);
					if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
					setState(116);
					((BoolOprExprContext)_localctx).opr = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 983040L) != 0)) ) {
						((BoolOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(117);
					((BoolOprExprContext)_localctx).right = bool_expr(8);
					}
					} 
				}
				setState(122);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
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
	public static class PreviousExistContext extends State_booleanContext {
		public TerminalNode VISIT_STATUS() { return getToken(StrategyParser.VISIT_STATUS, 0); }
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
	public static class AnyExistContext extends State_booleanContext {
		public TerminalNode VISIT_STATUS() { return getToken(StrategyParser.VISIT_STATUS, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class AnyExistByRelationContext extends State_booleanContext {
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode RELATION() { return getToken(StrategyParser.RELATION, 0); }
		public TerminalNode VISIT_STATUS() { return getToken(StrategyParser.VISIT_STATUS, 0); }
		public AnyExistByRelationContext(State_booleanContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterAnyExistByRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitAnyExistByRelation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitAnyExistByRelation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final State_booleanContext state_boolean() throws RecognitionException {
		State_booleanContext _localctx = new State_booleanContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_state_boolean);
		int _la;
		try {
			setState(149);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				_localctx = new StateChangedContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(123);
				match(T__3);
				}
				break;
			case 2:
				_localctx = new SutTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(124);
				match(T__4);
				setState(125);
				match(FILTER);
				setState(126);
				match(SUT);
				}
				break;
			case 3:
				_localctx = new AnyExistByRelationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(127);
				match(T__5);
				setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VISIT_STATUS) {
					{
					setState(128);
					match(VISIT_STATUS);
					}
				}

				setState(131);
				match(FILTER);
				setState(132);
				match(RELATION);
				}
				break;
			case 4:
				_localctx = new AnyExistContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(133);
				match(T__5);
				setState(135);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(134);
					match(VISIT_STATUS);
					}
					break;
				}
				setState(139);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
				case 1:
					{
					setState(137);
					match(FILTER);
					setState(138);
					match(ACTION_TYPE);
					}
					break;
				}
				}
				break;
			case 5:
				_localctx = new PreviousExistContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(141);
				match(T__6);
				setState(143);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
				case 1:
					{
					setState(142);
					match(VISIT_STATUS);
					}
					break;
				}
				setState(147);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
				case 1:
					{
					setState(145);
					match(FILTER);
					setState(146);
					match(ACTION_TYPE);
					}
					break;
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
	public static class NPreviousContext extends Int_exprContext {
		public TerminalNode VISIT_STATUS() { return getToken(StrategyParser.VISIT_STATUS, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public NPreviousContext(Int_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterNPrevious(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitNPrevious(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitNPrevious(this);
			else return visitor.visitChildren(this);
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
		public TerminalNode VISIT_STATUS() { return getToken(StrategyParser.VISIT_STATUS, 0); }
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
			setState(168);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				_localctx = new NActionsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(151);
				match(T__7);
				setState(153);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(152);
					match(VISIT_STATUS);
					}
					break;
				}
				setState(157);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
				case 1:
					{
					setState(155);
					match(FILTER);
					setState(156);
					match(ACTION_TYPE);
					}
					break;
				}
				}
				break;
			case T__8:
				_localctx = new NPreviousContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(159);
				match(T__8);
				setState(161);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
				case 1:
					{
					setState(160);
					match(VISIT_STATUS);
					}
					break;
				}
				setState(165);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(163);
					match(FILTER);
					setState(164);
					match(ACTION_TYPE);
					}
					break;
				}
				}
				break;
			case INT:
				_localctx = new PlainIntContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(167);
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
		"\u0004\u0001#\u00ab\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0004\u0001\u0015\b\u0001\u000b"+
		"\u0001\f\u0001\u0016\u0001\u0002\u0001\u0002\u0003\u0002\u001b\b\u0002"+
		"\u0001\u0003\u0003\u0003\u001e\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"(\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u0003/\b\u0003\u0001\u0004\u0003\u00042\b\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u00046\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u0004<\b\u0004\u0001\u0004\u0003\u0004?\b\u0004\u0001\u0004"+
		"\u0001\u0004\u0003\u0004C\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0003\u0004H\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004L\b\u0004\u0001"+
		"\u0004\u0001\u0004\u0003\u0004P\b\u0004\u0003\u0004R\b\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005"+
		"r\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005w\b\u0005\n\u0005"+
		"\f\u0005z\t\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0003\u0006\u0082\b\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0003\u0006\u0088\b\u0006\u0001\u0006\u0001\u0006\u0003"+
		"\u0006\u008c\b\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u0090\b\u0006"+
		"\u0001\u0006\u0001\u0006\u0003\u0006\u0094\b\u0006\u0003\u0006\u0096\b"+
		"\u0006\u0001\u0007\u0001\u0007\u0003\u0007\u009a\b\u0007\u0001\u0007\u0001"+
		"\u0007\u0003\u0007\u009e\b\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u00a2"+
		"\b\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u00a6\b\u0007\u0001\u0007"+
		"\u0003\u0007\u00a9\b\u0007\u0001\u0007\u0000\u0001\n\b\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0000\u0002\u0001\u0000\u0010\u0013\u0001\u0000\u0014"+
		"\u0019\u00c9\u0000\u0010\u0001\u0000\u0000\u0000\u0002\u0014\u0001\u0000"+
		"\u0000\u0000\u0004\u001a\u0001\u0000\u0000\u0000\u0006.\u0001\u0000\u0000"+
		"\u0000\bQ\u0001\u0000\u0000\u0000\nq\u0001\u0000\u0000\u0000\f\u0095\u0001"+
		"\u0000\u0000\u0000\u000e\u00a8\u0001\u0000\u0000\u0000\u0010\u0011\u0003"+
		"\u0002\u0001\u0000\u0011\u0012\u0005\u0000\u0000\u0001\u0012\u0001\u0001"+
		"\u0000\u0000\u0000\u0013\u0015\u0003\u0004\u0002\u0000\u0014\u0013\u0001"+
		"\u0000\u0000\u0000\u0015\u0016\u0001\u0000\u0000\u0000\u0016\u0014\u0001"+
		"\u0000\u0000\u0000\u0016\u0017\u0001\u0000\u0000\u0000\u0017\u0003\u0001"+
		"\u0000\u0000\u0000\u0018\u001b\u0003\u0006\u0003\u0000\u0019\u001b\u0003"+
		"\b\u0004\u0000\u001a\u0018\u0001\u0000\u0000\u0000\u001a\u0019\u0001\u0000"+
		"\u0000\u0000\u001b\u0005\u0001\u0000\u0000\u0000\u001c\u001e\u0005\u001a"+
		"\u0000\u0000\u001d\u001c\u0001\u0000\u0000\u0000\u001d\u001e\u0001\u0000"+
		"\u0000\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f \u0005\u001c\u0000"+
		"\u0000 !\u0003\n\u0005\u0000!\"\u0005\u001d\u0000\u0000\"#\u0003\u0002"+
		"\u0001\u0000#$\u0005\u001e\u0000\u0000$%\u0003\u0002\u0001\u0000%/\u0001"+
		"\u0000\u0000\u0000&(\u0005\u001a\u0000\u0000\'&\u0001\u0000\u0000\u0000"+
		"\'(\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)*\u0005\u001c\u0000"+
		"\u0000*+\u0003\n\u0005\u0000+,\u0005\u001d\u0000\u0000,-\u0003\u0002\u0001"+
		"\u0000-/\u0001\u0000\u0000\u0000.\u001d\u0001\u0000\u0000\u0000.\'\u0001"+
		"\u0000\u0000\u0000/\u0007\u0001\u0000\u0000\u000002\u0005\u001a\u0000"+
		"\u000010\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000023\u0001\u0000"+
		"\u0000\u00003R\u0005\u0001\u0000\u000046\u0005\u001a\u0000\u000054\u0001"+
		"\u0000\u0000\u000056\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u0000"+
		"78\u0005\u0002\u0000\u00008;\u0005\n\u0000\u00009:\u0005\u000e\u0000\u0000"+
		":<\u0005\r\u0000\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000"+
		"<R\u0001\u0000\u0000\u0000=?\u0005\u001a\u0000\u0000>=\u0001\u0000\u0000"+
		"\u0000>?\u0001\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@B\u0005\u0003"+
		"\u0000\u0000AC\u0005\n\u0000\u0000BA\u0001\u0000\u0000\u0000BC\u0001\u0000"+
		"\u0000\u0000CD\u0001\u0000\u0000\u0000DE\u0005\u000e\u0000\u0000ER\u0005"+
		"\u000b\u0000\u0000FH\u0005\u001a\u0000\u0000GF\u0001\u0000\u0000\u0000"+
		"GH\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000IK\u0005\u0003\u0000"+
		"\u0000JL\u0005\n\u0000\u0000KJ\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000"+
		"\u0000LO\u0001\u0000\u0000\u0000MN\u0005\u000e\u0000\u0000NP\u0005\r\u0000"+
		"\u0000OM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000PR\u0001\u0000"+
		"\u0000\u0000Q1\u0001\u0000\u0000\u0000Q5\u0001\u0000\u0000\u0000Q>\u0001"+
		"\u0000\u0000\u0000QG\u0001\u0000\u0000\u0000R\t\u0001\u0000\u0000\u0000"+
		"ST\u0006\u0005\uffff\uffff\u0000TU\u0005\u000f\u0000\u0000Ur\u0003\n\u0005"+
		"\tVW\u0005\u001f\u0000\u0000WX\u0005\u000f\u0000\u0000XY\u0003\n\u0005"+
		"\u0000YZ\u0005 \u0000\u0000Zr\u0001\u0000\u0000\u0000[\\\u0005\u001f\u0000"+
		"\u0000\\]\u0003\n\u0005\u0000]^\u0007\u0000\u0000\u0000^_\u0003\n\u0005"+
		"\u0000_`\u0005 \u0000\u0000`r\u0001\u0000\u0000\u0000ab\u0003\u000e\u0007"+
		"\u0000bc\u0007\u0001\u0000\u0000cd\u0003\u000e\u0007\u0000dr\u0001\u0000"+
		"\u0000\u0000ef\u0005\u001f\u0000\u0000fg\u0003\u000e\u0007\u0000gh\u0007"+
		"\u0001\u0000\u0000hi\u0003\u000e\u0007\u0000ij\u0005 \u0000\u0000jr\u0001"+
		"\u0000\u0000\u0000kr\u0003\f\u0006\u0000lm\u0005\u001f\u0000\u0000mn\u0003"+
		"\f\u0006\u0000no\u0005 \u0000\u0000or\u0001\u0000\u0000\u0000pr\u0005"+
		"\u001b\u0000\u0000qS\u0001\u0000\u0000\u0000qV\u0001\u0000\u0000\u0000"+
		"q[\u0001\u0000\u0000\u0000qa\u0001\u0000\u0000\u0000qe\u0001\u0000\u0000"+
		"\u0000qk\u0001\u0000\u0000\u0000ql\u0001\u0000\u0000\u0000qp\u0001\u0000"+
		"\u0000\u0000rx\u0001\u0000\u0000\u0000st\n\u0007\u0000\u0000tu\u0007\u0000"+
		"\u0000\u0000uw\u0003\n\u0005\bvs\u0001\u0000\u0000\u0000wz\u0001\u0000"+
		"\u0000\u0000xv\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000y\u000b"+
		"\u0001\u0000\u0000\u0000zx\u0001\u0000\u0000\u0000{\u0096\u0005\u0004"+
		"\u0000\u0000|}\u0005\u0005\u0000\u0000}~\u0005\u000e\u0000\u0000~\u0096"+
		"\u0005\f\u0000\u0000\u007f\u0081\u0005\u0006\u0000\u0000\u0080\u0082\u0005"+
		"\n\u0000\u0000\u0081\u0080\u0001\u0000\u0000\u0000\u0081\u0082\u0001\u0000"+
		"\u0000\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0084\u0005\u000e"+
		"\u0000\u0000\u0084\u0096\u0005\u000b\u0000\u0000\u0085\u0087\u0005\u0006"+
		"\u0000\u0000\u0086\u0088\u0005\n\u0000\u0000\u0087\u0086\u0001\u0000\u0000"+
		"\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u008b\u0001\u0000\u0000"+
		"\u0000\u0089\u008a\u0005\u000e\u0000\u0000\u008a\u008c\u0005\r\u0000\u0000"+
		"\u008b\u0089\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000\u0000\u0000"+
		"\u008c\u0096\u0001\u0000\u0000\u0000\u008d\u008f\u0005\u0007\u0000\u0000"+
		"\u008e\u0090\u0005\n\u0000\u0000\u008f\u008e\u0001\u0000\u0000\u0000\u008f"+
		"\u0090\u0001\u0000\u0000\u0000\u0090\u0093\u0001\u0000\u0000\u0000\u0091"+
		"\u0092\u0005\u000e\u0000\u0000\u0092\u0094\u0005\r\u0000\u0000\u0093\u0091"+
		"\u0001\u0000\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0096"+
		"\u0001\u0000\u0000\u0000\u0095{\u0001\u0000\u0000\u0000\u0095|\u0001\u0000"+
		"\u0000\u0000\u0095\u007f\u0001\u0000\u0000\u0000\u0095\u0085\u0001\u0000"+
		"\u0000\u0000\u0095\u008d\u0001\u0000\u0000\u0000\u0096\r\u0001\u0000\u0000"+
		"\u0000\u0097\u0099\u0005\b\u0000\u0000\u0098\u009a\u0005\n\u0000\u0000"+
		"\u0099\u0098\u0001\u0000\u0000\u0000\u0099\u009a\u0001\u0000\u0000\u0000"+
		"\u009a\u009d\u0001\u0000\u0000\u0000\u009b\u009c\u0005\u000e\u0000\u0000"+
		"\u009c\u009e\u0005\r\u0000\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009d"+
		"\u009e\u0001\u0000\u0000\u0000\u009e\u00a9\u0001\u0000\u0000\u0000\u009f"+
		"\u00a1\u0005\t\u0000\u0000\u00a0\u00a2\u0005\n\u0000\u0000\u00a1\u00a0"+
		"\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00a5"+
		"\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005\u000e\u0000\u0000\u00a4\u00a6"+
		"\u0005\r\u0000\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001"+
		"\u0000\u0000\u0000\u00a6\u00a9\u0001\u0000\u0000\u0000\u00a7\u00a9\u0005"+
		"\u001a\u0000\u0000\u00a8\u0097\u0001\u0000\u0000\u0000\u00a8\u009f\u0001"+
		"\u0000\u0000\u0000\u00a8\u00a7\u0001\u0000\u0000\u0000\u00a9\u000f\u0001"+
		"\u0000\u0000\u0000\u001b\u0016\u001a\u001d\'.15;>BGKOQqx\u0081\u0087\u008b"+
		"\u008f\u0093\u0095\u0099\u009d\u00a1\u00a5\u00a8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
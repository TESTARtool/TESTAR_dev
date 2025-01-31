// Generated from E:/lianne_dev/strategy/src/antlrfour/strategy/Strategy.g4 by ANTLR 4.13.1
package strategy;
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
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, RELATION=16, 
		SUT_TYPE=17, ACTION_TYPE=18, FILTER=19, NOT=20, AND=21, XOR=22, OR=23, 
		EQUALS=24, GT=25, GE=26, LT=27, LE=28, EQ=29, NE=30, INT=31, BOOL=32, 
		IF=33, THEN=34, ELSE=35, LP=36, RP=37, COMMENT=38, WHITESPACE=39, ANY=40;
	public static final int
		RULE_strategy_file = 0, RULE_strategy = 1, RULE_action = 2, RULE_bool_expr = 3, 
		RULE_state_boolean = 4, RULE_int_expr = 5, RULE_visit_status = 6;
	private static String[] makeRuleNames() {
		return new String[] {
			"strategy_file", "strategy", "action", "bool_expr", "state_boolean", 
			"int_expr", "visit_status"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'repeat-previous'", "'select-previous'", "'select-random'", "'state-changed'", 
			"'sut-is'", "'any-exist'", "'previous-exist'", "'n-actions'", "'n-previous'", 
			"'unvisited'", "'most-visited'", "'least-visited'", "'visited'", "'visited-over'", 
			"'visited-under'", null, null, null, null, null, null, null, null, null, 
			"'>'", "'>='", "'<'", "'<='", null, "'!='", null, null, null, null, null, 
			"'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "RELATION", "SUT_TYPE", "ACTION_TYPE", "FILTER", 
			"NOT", "AND", "XOR", "OR", "EQUALS", "GT", "GE", "LT", "LE", "EQ", "NE", 
			"INT", "BOOL", "IF", "THEN", "ELSE", "LP", "RP", "COMMENT", "WHITESPACE", 
			"ANY"
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
			setState(14);
			strategy();
			setState(15);
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
			setState(18); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(17);
					action();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(20); 
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
		public ActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action; }
	 
		public ActionContext() { }
		public void copyFrom(ActionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IfThenElseContext extends ActionContext {
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
		public IfThenElseContext(ActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterIfThenElse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitIfThenElse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitIfThenElse(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SelectPreviousActionContext extends ActionContext {
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public Visit_statusContext visit_status() {
			return getRuleContext(Visit_statusContext.class,0);
		}
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class SelectRandomActionContext extends ActionContext {
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public Visit_statusContext visit_status() {
			return getRuleContext(Visit_statusContext.class,0);
		}
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class RepeatPreviousActionContext extends ActionContext {
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public RepeatPreviousActionContext(ActionContext ctx) { copyFrom(ctx); }
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
	public static class SelectByRelationContext extends ActionContext {
		public TerminalNode RELATION() { return getToken(StrategyParser.RELATION, 0); }
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public Visit_statusContext visit_status() {
			return getRuleContext(Visit_statusContext.class,0);
		}
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
		public SelectByRelationContext(ActionContext ctx) { copyFrom(ctx); }
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

	public final ActionContext action() throws RecognitionException {
		ActionContext _localctx = new ActionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_action);
		int _la;
		try {
			setState(81);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				_localctx = new IfThenElseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(22);
					match(INT);
					}
				}

				setState(25);
				match(IF);
				setState(26);
				((IfThenElseContext)_localctx).ifExpr = bool_expr(0);
				setState(27);
				match(THEN);
				setState(28);
				((IfThenElseContext)_localctx).thenExpr = strategy();
				setState(29);
				match(ELSE);
				setState(30);
				((IfThenElseContext)_localctx).elseExpr = strategy();
				}
				break;
			case 2:
				_localctx = new IfThenElseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(32);
					match(INT);
					}
				}

				setState(35);
				match(IF);
				setState(36);
				((IfThenElseContext)_localctx).ifExpr = bool_expr(0);
				setState(37);
				match(THEN);
				setState(38);
				((IfThenElseContext)_localctx).thenExpr = strategy();
				}
				break;
			case 3:
				_localctx = new RepeatPreviousActionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(40);
					match(INT);
					}
				}

				setState(43);
				match(T__0);
				}
				break;
			case 4:
				_localctx = new SelectPreviousActionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(45);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(44);
					match(INT);
					}
				}

				setState(47);
				match(T__1);
				setState(49);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 64512L) != 0)) {
					{
					setState(48);
					visit_status();
					}
				}

				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ACTION_TYPE || _la==FILTER) {
					{
					setState(52);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FILTER) {
						{
						setState(51);
						match(FILTER);
						}
					}

					setState(54);
					match(ACTION_TYPE);
					}
				}

				}
				break;
			case 5:
				_localctx = new SelectByRelationContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(57);
					match(INT);
					}
				}

				setState(60);
				match(T__2);
				setState(62);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 64512L) != 0)) {
					{
					setState(61);
					visit_status();
					}
				}

				setState(65);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(64);
					match(FILTER);
					}
				}

				setState(67);
				match(RELATION);
				}
				break;
			case 6:
				_localctx = new SelectRandomActionContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(68);
					match(INT);
					}
				}

				setState(71);
				match(T__2);
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 64512L) != 0)) {
					{
					setState(72);
					visit_status();
					}
				}

				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ACTION_TYPE || _la==FILTER) {
					{
					setState(76);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FILTER) {
						{
						setState(75);
						match(FILTER);
						}
					}

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
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_bool_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
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
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 31457280L) != 0)) ) {
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
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 2113929216L) != 0)) ) {
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
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 2113929216L) != 0)) ) {
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
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
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
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 31457280L) != 0)) ) {
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
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
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
		public TerminalNode SUT_TYPE() { return getToken(StrategyParser.SUT_TYPE, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
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
		public Visit_statusContext visit_status() {
			return getRuleContext(Visit_statusContext.class,0);
		}
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
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
		public Visit_statusContext visit_status() {
			return getRuleContext(Visit_statusContext.class,0);
		}
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
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
		public TerminalNode RELATION() { return getToken(StrategyParser.RELATION, 0); }
		public Visit_statusContext visit_status() {
			return getRuleContext(Visit_statusContext.class,0);
		}
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
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
		enterRule(_localctx, 8, RULE_state_boolean);
		int _la;
		try {
			setState(157);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
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
				setState(126);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(125);
					match(FILTER);
					}
				}

				setState(128);
				match(SUT_TYPE);
				}
				break;
			case 3:
				_localctx = new AnyExistByRelationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(129);
				match(T__5);
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 64512L) != 0)) {
					{
					setState(130);
					visit_status();
					}
				}

				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FILTER) {
					{
					setState(133);
					match(FILTER);
					}
				}

				setState(136);
				match(RELATION);
				}
				break;
			case 4:
				_localctx = new AnyExistContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(137);
				match(T__5);
				setState(139);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
				case 1:
					{
					setState(138);
					visit_status();
					}
					break;
				}
				setState(145);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
				case 1:
					{
					setState(142);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FILTER) {
						{
						setState(141);
						match(FILTER);
						}
					}

					setState(144);
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
				setState(147);
				match(T__6);
				setState(149);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
				case 1:
					{
					setState(148);
					visit_status();
					}
					break;
				}
				setState(155);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					setState(152);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FILTER) {
						{
						setState(151);
						match(FILTER);
						}
					}

					setState(154);
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
		public Visit_statusContext visit_status() {
			return getRuleContext(Visit_statusContext.class,0);
		}
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
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
		public Visit_statusContext visit_status() {
			return getRuleContext(Visit_statusContext.class,0);
		}
		public TerminalNode ACTION_TYPE() { return getToken(StrategyParser.ACTION_TYPE, 0); }
		public TerminalNode FILTER() { return getToken(StrategyParser.FILTER, 0); }
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
		enterRule(_localctx, 10, RULE_int_expr);
		int _la;
		try {
			setState(180);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				_localctx = new NActionsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(159);
				match(T__7);
				setState(161);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
				case 1:
					{
					setState(160);
					visit_status();
					}
					break;
				}
				setState(167);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
				case 1:
					{
					setState(164);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FILTER) {
						{
						setState(163);
						match(FILTER);
						}
					}

					setState(166);
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
				setState(169);
				match(T__8);
				setState(171);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
				case 1:
					{
					setState(170);
					visit_status();
					}
					break;
				}
				setState(177);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
				case 1:
					{
					setState(174);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FILTER) {
						{
						setState(173);
						match(FILTER);
						}
					}

					setState(176);
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
				setState(179);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Visit_statusContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(StrategyParser.INT, 0); }
		public Visit_statusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_visit_status; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).enterVisit_status(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StrategyListener ) ((StrategyListener)listener).exitVisit_status(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StrategyVisitor ) return ((StrategyVisitor<? extends T>)visitor).visitVisit_status(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Visit_statusContext visit_status() throws RecognitionException {
		Visit_statusContext _localctx = new Visit_statusContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_visit_status);
		try {
			setState(191);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__9:
				enterOuterAlt(_localctx, 1);
				{
				setState(182);
				match(T__9);
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 2);
				{
				setState(183);
				match(T__10);
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 3);
				{
				setState(184);
				match(T__11);
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 4);
				{
				setState(185);
				match(T__12);
				setState(186);
				match(INT);
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 5);
				{
				setState(187);
				match(T__13);
				setState(188);
				match(INT);
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 6);
				{
				setState(189);
				match(T__14);
				setState(190);
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
		"\u0004\u0001(\u00c2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0004\u0001\u0013\b\u0001\u000b\u0001\f\u0001\u0014"+
		"\u0001\u0002\u0003\u0002\u0018\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002"+
		"\"\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0003\u0002*\b\u0002\u0001\u0002\u0001\u0002\u0003\u0002"+
		".\b\u0002\u0001\u0002\u0001\u0002\u0003\u00022\b\u0002\u0001\u0002\u0003"+
		"\u00025\b\u0002\u0001\u0002\u0003\u00028\b\u0002\u0001\u0002\u0003\u0002"+
		";\b\u0002\u0001\u0002\u0001\u0002\u0003\u0002?\b\u0002\u0001\u0002\u0003"+
		"\u0002B\b\u0002\u0001\u0002\u0001\u0002\u0003\u0002F\b\u0002\u0001\u0002"+
		"\u0001\u0002\u0003\u0002J\b\u0002\u0001\u0002\u0003\u0002M\b\u0002\u0001"+
		"\u0002\u0003\u0002P\b\u0002\u0003\u0002R\b\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003r\b\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003w\b\u0003\n\u0003\f\u0003"+
		"z\t\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u007f\b\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u0084\b\u0004\u0001\u0004"+
		"\u0003\u0004\u0087\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004"+
		"\u008c\b\u0004\u0001\u0004\u0003\u0004\u008f\b\u0004\u0001\u0004\u0003"+
		"\u0004\u0092\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u0096\b\u0004"+
		"\u0001\u0004\u0003\u0004\u0099\b\u0004\u0001\u0004\u0003\u0004\u009c\b"+
		"\u0004\u0003\u0004\u009e\b\u0004\u0001\u0005\u0001\u0005\u0003\u0005\u00a2"+
		"\b\u0005\u0001\u0005\u0003\u0005\u00a5\b\u0005\u0001\u0005\u0003\u0005"+
		"\u00a8\b\u0005\u0001\u0005\u0001\u0005\u0003\u0005\u00ac\b\u0005\u0001"+
		"\u0005\u0003\u0005\u00af\b\u0005\u0001\u0005\u0003\u0005\u00b2\b\u0005"+
		"\u0001\u0005\u0003\u0005\u00b5\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0003\u0006\u00c0\b\u0006\u0001\u0006\u0000\u0001\u0006\u0007\u0000\u0002"+
		"\u0004\u0006\b\n\f\u0000\u0002\u0001\u0000\u0015\u0018\u0001\u0000\u0019"+
		"\u001e\u00f0\u0000\u000e\u0001\u0000\u0000\u0000\u0002\u0012\u0001\u0000"+
		"\u0000\u0000\u0004Q\u0001\u0000\u0000\u0000\u0006q\u0001\u0000\u0000\u0000"+
		"\b\u009d\u0001\u0000\u0000\u0000\n\u00b4\u0001\u0000\u0000\u0000\f\u00bf"+
		"\u0001\u0000\u0000\u0000\u000e\u000f\u0003\u0002\u0001\u0000\u000f\u0010"+
		"\u0005\u0000\u0000\u0001\u0010\u0001\u0001\u0000\u0000\u0000\u0011\u0013"+
		"\u0003\u0004\u0002\u0000\u0012\u0011\u0001\u0000\u0000\u0000\u0013\u0014"+
		"\u0001\u0000\u0000\u0000\u0014\u0012\u0001\u0000\u0000\u0000\u0014\u0015"+
		"\u0001\u0000\u0000\u0000\u0015\u0003\u0001\u0000\u0000\u0000\u0016\u0018"+
		"\u0005\u001f\u0000\u0000\u0017\u0016\u0001\u0000\u0000\u0000\u0017\u0018"+
		"\u0001\u0000\u0000\u0000\u0018\u0019\u0001\u0000\u0000\u0000\u0019\u001a"+
		"\u0005!\u0000\u0000\u001a\u001b\u0003\u0006\u0003\u0000\u001b\u001c\u0005"+
		"\"\u0000\u0000\u001c\u001d\u0003\u0002\u0001\u0000\u001d\u001e\u0005#"+
		"\u0000\u0000\u001e\u001f\u0003\u0002\u0001\u0000\u001fR\u0001\u0000\u0000"+
		"\u0000 \"\u0005\u001f\u0000\u0000! \u0001\u0000\u0000\u0000!\"\u0001\u0000"+
		"\u0000\u0000\"#\u0001\u0000\u0000\u0000#$\u0005!\u0000\u0000$%\u0003\u0006"+
		"\u0003\u0000%&\u0005\"\u0000\u0000&\'\u0003\u0002\u0001\u0000\'R\u0001"+
		"\u0000\u0000\u0000(*\u0005\u001f\u0000\u0000)(\u0001\u0000\u0000\u0000"+
		")*\u0001\u0000\u0000\u0000*+\u0001\u0000\u0000\u0000+R\u0005\u0001\u0000"+
		"\u0000,.\u0005\u001f\u0000\u0000-,\u0001\u0000\u0000\u0000-.\u0001\u0000"+
		"\u0000\u0000./\u0001\u0000\u0000\u0000/1\u0005\u0002\u0000\u000002\u0003"+
		"\f\u0006\u000010\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000027\u0001"+
		"\u0000\u0000\u000035\u0005\u0013\u0000\u000043\u0001\u0000\u0000\u0000"+
		"45\u0001\u0000\u0000\u000056\u0001\u0000\u0000\u000068\u0005\u0012\u0000"+
		"\u000074\u0001\u0000\u0000\u000078\u0001\u0000\u0000\u00008R\u0001\u0000"+
		"\u0000\u00009;\u0005\u001f\u0000\u0000:9\u0001\u0000\u0000\u0000:;\u0001"+
		"\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<>\u0005\u0003\u0000\u0000"+
		"=?\u0003\f\u0006\u0000>=\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000"+
		"?A\u0001\u0000\u0000\u0000@B\u0005\u0013\u0000\u0000A@\u0001\u0000\u0000"+
		"\u0000AB\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CR\u0005\u0010"+
		"\u0000\u0000DF\u0005\u001f\u0000\u0000ED\u0001\u0000\u0000\u0000EF\u0001"+
		"\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GI\u0005\u0003\u0000\u0000"+
		"HJ\u0003\f\u0006\u0000IH\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000"+
		"JO\u0001\u0000\u0000\u0000KM\u0005\u0013\u0000\u0000LK\u0001\u0000\u0000"+
		"\u0000LM\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000NP\u0005\u0012"+
		"\u0000\u0000OL\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000PR\u0001"+
		"\u0000\u0000\u0000Q\u0017\u0001\u0000\u0000\u0000Q!\u0001\u0000\u0000"+
		"\u0000Q)\u0001\u0000\u0000\u0000Q-\u0001\u0000\u0000\u0000Q:\u0001\u0000"+
		"\u0000\u0000QE\u0001\u0000\u0000\u0000R\u0005\u0001\u0000\u0000\u0000"+
		"ST\u0006\u0003\uffff\uffff\u0000TU\u0005\u0014\u0000\u0000Ur\u0003\u0006"+
		"\u0003\tVW\u0005$\u0000\u0000WX\u0005\u0014\u0000\u0000XY\u0003\u0006"+
		"\u0003\u0000YZ\u0005%\u0000\u0000Zr\u0001\u0000\u0000\u0000[\\\u0005$"+
		"\u0000\u0000\\]\u0003\u0006\u0003\u0000]^\u0007\u0000\u0000\u0000^_\u0003"+
		"\u0006\u0003\u0000_`\u0005%\u0000\u0000`r\u0001\u0000\u0000\u0000ab\u0003"+
		"\n\u0005\u0000bc\u0007\u0001\u0000\u0000cd\u0003\n\u0005\u0000dr\u0001"+
		"\u0000\u0000\u0000ef\u0005$\u0000\u0000fg\u0003\n\u0005\u0000gh\u0007"+
		"\u0001\u0000\u0000hi\u0003\n\u0005\u0000ij\u0005%\u0000\u0000jr\u0001"+
		"\u0000\u0000\u0000kr\u0003\b\u0004\u0000lm\u0005$\u0000\u0000mn\u0003"+
		"\b\u0004\u0000no\u0005%\u0000\u0000or\u0001\u0000\u0000\u0000pr\u0005"+
		" \u0000\u0000qS\u0001\u0000\u0000\u0000qV\u0001\u0000\u0000\u0000q[\u0001"+
		"\u0000\u0000\u0000qa\u0001\u0000\u0000\u0000qe\u0001\u0000\u0000\u0000"+
		"qk\u0001\u0000\u0000\u0000ql\u0001\u0000\u0000\u0000qp\u0001\u0000\u0000"+
		"\u0000rx\u0001\u0000\u0000\u0000st\n\u0007\u0000\u0000tu\u0007\u0000\u0000"+
		"\u0000uw\u0003\u0006\u0003\bvs\u0001\u0000\u0000\u0000wz\u0001\u0000\u0000"+
		"\u0000xv\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000y\u0007\u0001"+
		"\u0000\u0000\u0000zx\u0001\u0000\u0000\u0000{\u009e\u0005\u0004\u0000"+
		"\u0000|~\u0005\u0005\u0000\u0000}\u007f\u0005\u0013\u0000\u0000~}\u0001"+
		"\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000"+
		"\u0000\u0000\u0080\u009e\u0005\u0011\u0000\u0000\u0081\u0083\u0005\u0006"+
		"\u0000\u0000\u0082\u0084\u0003\f\u0006\u0000\u0083\u0082\u0001\u0000\u0000"+
		"\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0086\u0001\u0000\u0000"+
		"\u0000\u0085\u0087\u0005\u0013\u0000\u0000\u0086\u0085\u0001\u0000\u0000"+
		"\u0000\u0086\u0087\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000\u0000"+
		"\u0000\u0088\u009e\u0005\u0010\u0000\u0000\u0089\u008b\u0005\u0006\u0000"+
		"\u0000\u008a\u008c\u0003\f\u0006\u0000\u008b\u008a\u0001\u0000\u0000\u0000"+
		"\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u0091\u0001\u0000\u0000\u0000"+
		"\u008d\u008f\u0005\u0013\u0000\u0000\u008e\u008d\u0001\u0000\u0000\u0000"+
		"\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000\u0000\u0000"+
		"\u0090\u0092\u0005\u0012\u0000\u0000\u0091\u008e\u0001\u0000\u0000\u0000"+
		"\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u009e\u0001\u0000\u0000\u0000"+
		"\u0093\u0095\u0005\u0007\u0000\u0000\u0094\u0096\u0003\f\u0006\u0000\u0095"+
		"\u0094\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096"+
		"\u009b\u0001\u0000\u0000\u0000\u0097\u0099\u0005\u0013\u0000\u0000\u0098"+
		"\u0097\u0001\u0000\u0000\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099"+
		"\u009a\u0001\u0000\u0000\u0000\u009a\u009c\u0005\u0012\u0000\u0000\u009b"+
		"\u0098\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000\u0000\u009c"+
		"\u009e\u0001\u0000\u0000\u0000\u009d{\u0001\u0000\u0000\u0000\u009d|\u0001"+
		"\u0000\u0000\u0000\u009d\u0081\u0001\u0000\u0000\u0000\u009d\u0089\u0001"+
		"\u0000\u0000\u0000\u009d\u0093\u0001\u0000\u0000\u0000\u009e\t\u0001\u0000"+
		"\u0000\u0000\u009f\u00a1\u0005\b\u0000\u0000\u00a0\u00a2\u0003\f\u0006"+
		"\u0000\u00a1\u00a0\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000"+
		"\u0000\u00a2\u00a7\u0001\u0000\u0000\u0000\u00a3\u00a5\u0005\u0013\u0000"+
		"\u0000\u00a4\u00a3\u0001\u0000\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000"+
		"\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a8\u0005\u0012\u0000"+
		"\u0000\u00a7\u00a4\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000"+
		"\u0000\u00a8\u00b5\u0001\u0000\u0000\u0000\u00a9\u00ab\u0005\t\u0000\u0000"+
		"\u00aa\u00ac\u0003\f\u0006\u0000\u00ab\u00aa\u0001\u0000\u0000\u0000\u00ab"+
		"\u00ac\u0001\u0000\u0000\u0000\u00ac\u00b1\u0001\u0000\u0000\u0000\u00ad"+
		"\u00af\u0005\u0013\u0000\u0000\u00ae\u00ad\u0001\u0000\u0000\u0000\u00ae"+
		"\u00af\u0001\u0000\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b2\u0005\u0012\u0000\u0000\u00b1\u00ae\u0001\u0000\u0000\u0000\u00b1"+
		"\u00b2\u0001\u0000\u0000\u0000\u00b2\u00b5\u0001\u0000\u0000\u0000\u00b3"+
		"\u00b5\u0005\u001f\u0000\u0000\u00b4\u009f\u0001\u0000\u0000\u0000\u00b4"+
		"\u00a9\u0001\u0000\u0000\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b5"+
		"\u000b\u0001\u0000\u0000\u0000\u00b6\u00c0\u0005\n\u0000\u0000\u00b7\u00c0"+
		"\u0005\u000b\u0000\u0000\u00b8\u00c0\u0005\f\u0000\u0000\u00b9\u00ba\u0005"+
		"\r\u0000\u0000\u00ba\u00c0\u0005\u001f\u0000\u0000\u00bb\u00bc\u0005\u000e"+
		"\u0000\u0000\u00bc\u00c0\u0005\u001f\u0000\u0000\u00bd\u00be\u0005\u000f"+
		"\u0000\u0000\u00be\u00c0\u0005\u001f\u0000\u0000\u00bf\u00b6\u0001\u0000"+
		"\u0000\u0000\u00bf\u00b7\u0001\u0000\u0000\u0000\u00bf\u00b8\u0001\u0000"+
		"\u0000\u0000\u00bf\u00b9\u0001\u0000\u0000\u0000\u00bf\u00bb\u0001\u0000"+
		"\u0000\u0000\u00bf\u00bd\u0001\u0000\u0000\u0000\u00c0\r\u0001\u0000\u0000"+
		"\u0000$\u0014\u0017!)-147:>AEILOQqx~\u0083\u0086\u008b\u008e\u0091\u0095"+
		"\u0098\u009b\u009d\u00a1\u00a4\u00a7\u00ab\u00ae\u00b1\u00b4\u00bf";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
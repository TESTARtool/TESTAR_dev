// Generated from E:/lianne_dev/strategy/src/antlrfour/oracles/Oracles.g4 by ANTLR 4.13.1
package antlrfour.oracles;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class OraclesParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, BOOL=3, CONTAINS=4, HAS=5, IN=6, IS=7, NOT=8, AND=9, COMMA=10, 
		OR=11, XOR=12, BASE_STRING=13, TRIPLE_SINGLE_QUOTE_STRING=14, TRIPLE_DOUBLE_QUOTE_STRING=15, 
		SINGLE_QUOTE_STRING=16, DOUBLE_QUOTE_STRING=17, INT=18, CHECK=19, FOR_ALL=20, 
		LIST=21, ORACLE=22, PROP=23, RAW=24, REGEX=25, TRIGGER=26, WIDGET=27, 
		LB=28, RB=29, LP=30, RP=31, COMMENT=32, WHITESPACE=33, ANY=34;
	public static final int
		RULE_oracles_file = 0, RULE_oracle = 1, RULE_select_block = 2, RULE_check_block = 3, 
		RULE_trigger_block = 4, RULE_property_block = 5, RULE_bool_expr = 6, RULE_property_line = 7, 
		RULE_string = 8, RULE_regex_string = 9, RULE_property_string = 10, RULE_raw_string = 11, 
		RULE_basic_string = 12, RULE_oracle_name = 13, RULE_list = 14;
	private static String[] makeRuleNames() {
		return new String[] {
			"oracles_file", "oracle", "select_block", "check_block", "trigger_block", 
			"property_block", "bool_expr", "property_line", "string", "regex_string", 
			"property_string", "raw_string", "basic_string", "oracle_name", "list"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "'=='", null, "'CONTAINS'", "'HAS'", "'IN'", "'IS'", null, 
			"'AND'", "','", null, null, null, null, null, null, null, null, "'CHECK'", 
			"'FOR_ALL'", "'LIST'", "'ORACLE'", null, "'RAW'", "'REGEX'", "'TRIGGER'", 
			"'WIDGET'", "'{'", "'}'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "BOOL", "CONTAINS", "HAS", "IN", "IS", "NOT", "AND", 
			"COMMA", "OR", "XOR", "BASE_STRING", "TRIPLE_SINGLE_QUOTE_STRING", "TRIPLE_DOUBLE_QUOTE_STRING", 
			"SINGLE_QUOTE_STRING", "DOUBLE_QUOTE_STRING", "INT", "CHECK", "FOR_ALL", 
			"LIST", "ORACLE", "PROP", "RAW", "REGEX", "TRIGGER", "WIDGET", "LB", 
			"RB", "LP", "RP", "COMMENT", "WHITESPACE", "ANY"
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
	public String getGrammarFileName() { return "Oracles.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public OraclesParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Oracles_fileContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(OraclesParser.EOF, 0); }
		public List<OracleContext> oracle() {
			return getRuleContexts(OracleContext.class);
		}
		public OracleContext oracle(int i) {
			return getRuleContext(OracleContext.class,i);
		}
		public List<TerminalNode> COMMENT() { return getTokens(OraclesParser.COMMENT); }
		public TerminalNode COMMENT(int i) {
			return getToken(OraclesParser.COMMENT, i);
		}
		public Oracles_fileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oracles_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterOracles_file(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitOracles_file(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitOracles_file(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Oracles_fileContext oracles_file() throws RecognitionException {
		Oracles_fileContext _localctx = new Oracles_fileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_oracles_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ORACLE || _la==COMMENT) {
				{
				setState(32);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ORACLE:
					{
					setState(30);
					oracle();
					}
					break;
				case COMMENT:
					{
					setState(31);
					match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(36);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(37);
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
	public static class OracleContext extends ParserRuleContext {
		public Oracle_nameContext oracle_name() {
			return getRuleContext(Oracle_nameContext.class,0);
		}
		public TerminalNode LB() { return getToken(OraclesParser.LB, 0); }
		public Check_blockContext check_block() {
			return getRuleContext(Check_blockContext.class,0);
		}
		public TerminalNode RB() { return getToken(OraclesParser.RB, 0); }
		public List<Select_blockContext> select_block() {
			return getRuleContexts(Select_blockContext.class);
		}
		public Select_blockContext select_block(int i) {
			return getRuleContext(Select_blockContext.class,i);
		}
		public List<Trigger_blockContext> trigger_block() {
			return getRuleContexts(Trigger_blockContext.class);
		}
		public Trigger_blockContext trigger_block(int i) {
			return getRuleContext(Trigger_blockContext.class,i);
		}
		public OracleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oracle; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterOracle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitOracle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitOracle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OracleContext oracle() throws RecognitionException {
		OracleContext _localctx = new OracleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_oracle);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			oracle_name();
			setState(40);
			match(LB);
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FOR_ALL) {
				{
				{
				setState(41);
				select_block();
				}
				}
				setState(46);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(47);
			check_block();
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TRIGGER) {
				{
				{
				setState(48);
				trigger_block();
				}
				}
				setState(53);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(54);
			match(RB);
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
	public static class Select_blockContext extends ParserRuleContext {
		public TerminalNode FOR_ALL() { return getToken(OraclesParser.FOR_ALL, 0); }
		public TerminalNode WIDGET() { return getToken(OraclesParser.WIDGET, 0); }
		public Property_blockContext property_block() {
			return getRuleContext(Property_blockContext.class,0);
		}
		public TerminalNode BASE_STRING() { return getToken(OraclesParser.BASE_STRING, 0); }
		public Select_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterSelect_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitSelect_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitSelect_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_blockContext select_block() throws RecognitionException {
		Select_blockContext _localctx = new Select_blockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_select_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(FOR_ALL);
			setState(57);
			match(WIDGET);
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BASE_STRING) {
				{
				setState(58);
				match(BASE_STRING);
				}
			}

			setState(61);
			property_block();
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
	public static class Check_blockContext extends ParserRuleContext {
		public TerminalNode CHECK() { return getToken(OraclesParser.CHECK, 0); }
		public Property_blockContext property_block() {
			return getRuleContext(Property_blockContext.class,0);
		}
		public Check_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_check_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterCheck_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitCheck_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitCheck_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Check_blockContext check_block() throws RecognitionException {
		Check_blockContext _localctx = new Check_blockContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_check_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(CHECK);
			setState(64);
			property_block();
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
	public static class Trigger_blockContext extends ParserRuleContext {
		public TerminalNode TRIGGER() { return getToken(OraclesParser.TRIGGER, 0); }
		public TerminalNode BOOL() { return getToken(OraclesParser.BOOL, 0); }
		public Basic_stringContext basic_string() {
			return getRuleContext(Basic_stringContext.class,0);
		}
		public Trigger_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trigger_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterTrigger_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitTrigger_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitTrigger_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Trigger_blockContext trigger_block() throws RecognitionException {
		Trigger_blockContext _localctx = new Trigger_blockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_trigger_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			match(TRIGGER);
			setState(67);
			match(BOOL);
			setState(68);
			basic_string();
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
	public static class Property_blockContext extends ParserRuleContext {
		public Bool_exprContext bool_expr() {
			return getRuleContext(Bool_exprContext.class,0);
		}
		public Property_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_property_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterProperty_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitProperty_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitProperty_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Property_blockContext property_block() throws RecognitionException {
		Property_blockContext _localctx = new Property_blockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_property_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			bool_expr(0);
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
		public TerminalNode BOOL() { return getToken(OraclesParser.BOOL, 0); }
		public PlainBoolContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPlainBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPlainBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPlainBool(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotExprContext extends Bool_exprContext {
		public TerminalNode NOT() { return getToken(OraclesParser.NOT, 0); }
		public Bool_exprContext bool_expr() {
			return getRuleContext(Bool_exprContext.class,0);
		}
		public NotExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterNotExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitNotExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitNotExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class OrOprExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Token opr;
		public Bool_exprContext right;
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public TerminalNode OR() { return getToken(OraclesParser.OR, 0); }
		public OrOprExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterOrOprExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitOrOprExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitOrOprExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class XorOprExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Token opr;
		public Bool_exprContext right;
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public TerminalNode XOR() { return getToken(OraclesParser.XOR, 0); }
		public XorOprExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterXorOprExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitXorOprExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitXorOprExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AndOprExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Token opr;
		public Bool_exprContext right;
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public TerminalNode AND() { return getToken(OraclesParser.AND, 0); }
		public TerminalNode COMMA() { return getToken(OraclesParser.COMMA, 0); }
		public AndOprExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterAndOprExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitAndOprExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitAndOprExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenExprContext extends Bool_exprContext {
		public TerminalNode LP() { return getToken(OraclesParser.LP, 0); }
		public Bool_exprContext bool_expr() {
			return getRuleContext(Bool_exprContext.class,0);
		}
		public TerminalNode RP() { return getToken(OraclesParser.RP, 0); }
		public ParenExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterParenExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitParenExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitParenExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BoolOprExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Token opr;
		public Bool_exprContext right;
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public TerminalNode IS() { return getToken(OraclesParser.IS, 0); }
		public BoolOprExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterBoolOprExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitBoolOprExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitBoolOprExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropertyBoolContext extends Bool_exprContext {
		public Property_lineContext property_line() {
			return getRuleContext(Property_lineContext.class,0);
		}
		public PropertyBoolContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropertyBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropertyBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropertyBool(this);
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
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_bool_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LP:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(73);
				match(LP);
				setState(74);
				bool_expr(0);
				setState(75);
				match(RP);
				}
				break;
			case NOT:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(77);
				match(NOT);
				setState(78);
				bool_expr(7);
				}
				break;
			case BOOL:
				{
				_localctx = new PlainBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(79);
				match(BOOL);
				}
				break;
			case HAS:
			case PROP:
			case WIDGET:
				{
				_localctx = new PropertyBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(80);
				property_line();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(97);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(95);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
					case 1:
						{
						_localctx = new AndOprExprContext(new Bool_exprContext(_parentctx, _parentState));
						((AndOprExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(83);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(84);
						((AndOprExprContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==AND || _la==COMMA) ) {
							((AndOprExprContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(85);
						((AndOprExprContext)_localctx).right = bool_expr(7);
						}
						break;
					case 2:
						{
						_localctx = new XorOprExprContext(new Bool_exprContext(_parentctx, _parentState));
						((XorOprExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(86);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(87);
						((XorOprExprContext)_localctx).opr = match(XOR);
						setState(88);
						((XorOprExprContext)_localctx).right = bool_expr(6);
						}
						break;
					case 3:
						{
						_localctx = new OrOprExprContext(new Bool_exprContext(_parentctx, _parentState));
						((OrOprExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(89);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(90);
						((OrOprExprContext)_localctx).opr = match(OR);
						setState(91);
						((OrOprExprContext)_localctx).right = bool_expr(5);
						}
						break;
					case 4:
						{
						_localctx = new BoolOprExprContext(new Bool_exprContext(_parentctx, _parentState));
						((BoolOprExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(92);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(93);
						((BoolOprExprContext)_localctx).opr = match(IS);
						setState(94);
						((BoolOprExprContext)_localctx).right = bool_expr(4);
						}
						break;
					}
					} 
				}
				setState(99);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
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
	public static class Property_lineContext extends ParserRuleContext {
		public Property_lineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_property_line; }
	 
		public Property_lineContext() { }
		public void copyFrom(Property_lineContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropContainsContext extends Property_lineContext {
		public Property_stringContext property_string() {
			return getRuleContext(Property_stringContext.class,0);
		}
		public TerminalNode CONTAINS() { return getToken(OraclesParser.CONTAINS, 0); }
		public TerminalNode BASE_STRING() { return getToken(OraclesParser.BASE_STRING, 0); }
		public PropContainsContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropContains(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropContains(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropContains(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropIsContext extends Property_lineContext {
		public Property_stringContext property_string() {
			return getRuleContext(Property_stringContext.class,0);
		}
		public TerminalNode IS() { return getToken(OraclesParser.IS, 0); }
		public TerminalNode BASE_STRING() { return getToken(OraclesParser.BASE_STRING, 0); }
		public TerminalNode BOOL() { return getToken(OraclesParser.BOOL, 0); }
		public PropIsContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropIs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropIs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropIs(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WidgetIsWidgetContext extends Property_lineContext {
		public List<TerminalNode> WIDGET() { return getTokens(OraclesParser.WIDGET); }
		public TerminalNode WIDGET(int i) {
			return getToken(OraclesParser.WIDGET, i);
		}
		public List<TerminalNode> BASE_STRING() { return getTokens(OraclesParser.BASE_STRING); }
		public TerminalNode BASE_STRING(int i) {
			return getToken(OraclesParser.BASE_STRING, i);
		}
		public WidgetIsWidgetContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterWidgetIsWidget(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitWidgetIsWidget(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitWidgetIsWidget(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class HasPropertyContext extends Property_lineContext {
		public TerminalNode HAS() { return getToken(OraclesParser.HAS, 0); }
		public Property_stringContext property_string() {
			return getRuleContext(Property_stringContext.class,0);
		}
		public HasPropertyContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterHasProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitHasProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitHasProperty(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropIsInListContext extends Property_lineContext {
		public Property_stringContext property_string() {
			return getRuleContext(Property_stringContext.class,0);
		}
		public TerminalNode IS() { return getToken(OraclesParser.IS, 0); }
		public TerminalNode IN() { return getToken(OraclesParser.IN, 0); }
		public ListContext list() {
			return getRuleContext(ListContext.class,0);
		}
		public PropIsInListContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropIsInList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropIsInList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropIsInList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Property_lineContext property_line() throws RecognitionException {
		Property_lineContext _localctx = new Property_lineContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_property_line);
		int _la;
		try {
			setState(120);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				_localctx = new HasPropertyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(100);
				match(HAS);
				setState(101);
				property_string();
				}
				break;
			case 2:
				_localctx = new PropIsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(102);
				property_string();
				setState(103);
				match(IS);
				setState(104);
				_la = _input.LA(1);
				if ( !(_la==BOOL || _la==BASE_STRING) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 3:
				_localctx = new PropContainsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(106);
				property_string();
				setState(107);
				match(CONTAINS);
				setState(108);
				match(BASE_STRING);
				}
				break;
			case 4:
				_localctx = new PropIsInListContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(110);
				property_string();
				setState(111);
				match(IS);
				setState(112);
				match(IN);
				setState(113);
				list();
				}
				break;
			case 5:
				_localctx = new WidgetIsWidgetContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(115);
				match(WIDGET);
				setState(116);
				match(BASE_STRING);
				setState(117);
				_la = _input.LA(1);
				if ( !(_la==T__0 || _la==T__1) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(118);
				match(WIDGET);
				setState(119);
				match(BASE_STRING);
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
	public static class StringContext extends ParserRuleContext {
		public Regex_stringContext regex_string() {
			return getRuleContext(Regex_stringContext.class,0);
		}
		public Property_stringContext property_string() {
			return getRuleContext(Property_stringContext.class,0);
		}
		public Raw_stringContext raw_string() {
			return getRuleContext(Raw_stringContext.class,0);
		}
		public Basic_stringContext basic_string() {
			return getRuleContext(Basic_stringContext.class,0);
		}
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_string);
		try {
			setState(126);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REGEX:
				enterOuterAlt(_localctx, 1);
				{
				setState(122);
				regex_string();
				}
				break;
			case PROP:
				enterOuterAlt(_localctx, 2);
				{
				setState(123);
				property_string();
				}
				break;
			case RAW:
				enterOuterAlt(_localctx, 3);
				{
				setState(124);
				raw_string();
				}
				break;
			case BASE_STRING:
				enterOuterAlt(_localctx, 4);
				{
				setState(125);
				basic_string();
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
	public static class Regex_stringContext extends ParserRuleContext {
		public TerminalNode REGEX() { return getToken(OraclesParser.REGEX, 0); }
		public TerminalNode BASE_STRING() { return getToken(OraclesParser.BASE_STRING, 0); }
		public Regex_stringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_regex_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterRegex_string(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitRegex_string(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitRegex_string(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Regex_stringContext regex_string() throws RecognitionException {
		Regex_stringContext _localctx = new Regex_stringContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_regex_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(REGEX);
			setState(129);
			match(BASE_STRING);
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
	public static class Property_stringContext extends ParserRuleContext {
		public TerminalNode PROP() { return getToken(OraclesParser.PROP, 0); }
		public TerminalNode BASE_STRING() { return getToken(OraclesParser.BASE_STRING, 0); }
		public Property_stringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_property_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterProperty_string(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitProperty_string(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitProperty_string(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Property_stringContext property_string() throws RecognitionException {
		Property_stringContext _localctx = new Property_stringContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_property_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			match(PROP);
			setState(132);
			match(BASE_STRING);
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
	public static class Raw_stringContext extends ParserRuleContext {
		public TerminalNode RAW() { return getToken(OraclesParser.RAW, 0); }
		public TerminalNode BASE_STRING() { return getToken(OraclesParser.BASE_STRING, 0); }
		public Raw_stringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_raw_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterRaw_string(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitRaw_string(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitRaw_string(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Raw_stringContext raw_string() throws RecognitionException {
		Raw_stringContext _localctx = new Raw_stringContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_raw_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(RAW);
			setState(135);
			match(BASE_STRING);
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
	public static class Basic_stringContext extends ParserRuleContext {
		public TerminalNode BASE_STRING() { return getToken(OraclesParser.BASE_STRING, 0); }
		public Basic_stringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basic_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterBasic_string(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitBasic_string(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitBasic_string(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Basic_stringContext basic_string() throws RecognitionException {
		Basic_stringContext _localctx = new Basic_stringContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_basic_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			match(BASE_STRING);
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
	public static class Oracle_nameContext extends ParserRuleContext {
		public TerminalNode ORACLE() { return getToken(OraclesParser.ORACLE, 0); }
		public TerminalNode BASE_STRING() { return getToken(OraclesParser.BASE_STRING, 0); }
		public Oracle_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oracle_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterOracle_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitOracle_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitOracle_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Oracle_nameContext oracle_name() throws RecognitionException {
		Oracle_nameContext _localctx = new Oracle_nameContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_oracle_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(ORACLE);
			setState(140);
			match(BASE_STRING);
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
	public static class ListContext extends ParserRuleContext {
		public TerminalNode LIST() { return getToken(OraclesParser.LIST, 0); }
		public TerminalNode LP() { return getToken(OraclesParser.LP, 0); }
		public List<TerminalNode> BASE_STRING() { return getTokens(OraclesParser.BASE_STRING); }
		public TerminalNode BASE_STRING(int i) {
			return getToken(OraclesParser.BASE_STRING, i);
		}
		public TerminalNode RP() { return getToken(OraclesParser.RP, 0); }
		public List<TerminalNode> COMMA() { return getTokens(OraclesParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(OraclesParser.COMMA, i);
		}
		public ListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListContext list() throws RecognitionException {
		ListContext _localctx = new ListContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			match(LIST);
			setState(143);
			match(LP);
			setState(144);
			match(BASE_STRING);
			setState(149);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(145);
				match(COMMA);
				setState(146);
				match(BASE_STRING);
				}
				}
				setState(151);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(152);
			match(RP);
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
		case 6:
			return bool_expr_sempred((Bool_exprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean bool_expr_sempred(Bool_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 5);
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\"\u009b\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001\u0000"+
		"\u0005\u0000!\b\u0000\n\u0000\f\u0000$\t\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001+\b\u0001\n\u0001\f\u0001"+
		".\t\u0001\u0001\u0001\u0001\u0001\u0005\u00012\b\u0001\n\u0001\f\u0001"+
		"5\t\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0003\u0002<\b\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006R\b\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0005\u0006`\b\u0006\n\u0006\f\u0006c\t\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0003\u0007y\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u007f\b"+
		"\b\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e\u0094\b\u000e\n"+
		"\u000e\f\u000e\u0097\t\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0000"+
		"\u0001\f\u000f\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u0000\u0003\u0001\u0000\t\n\u0002\u0000\u0003\u0003"+
		"\r\r\u0001\u0000\u0001\u0002\u009f\u0000\"\u0001\u0000\u0000\u0000\u0002"+
		"\'\u0001\u0000\u0000\u0000\u00048\u0001\u0000\u0000\u0000\u0006?\u0001"+
		"\u0000\u0000\u0000\bB\u0001\u0000\u0000\u0000\nF\u0001\u0000\u0000\u0000"+
		"\fQ\u0001\u0000\u0000\u0000\u000ex\u0001\u0000\u0000\u0000\u0010~\u0001"+
		"\u0000\u0000\u0000\u0012\u0080\u0001\u0000\u0000\u0000\u0014\u0083\u0001"+
		"\u0000\u0000\u0000\u0016\u0086\u0001\u0000\u0000\u0000\u0018\u0089\u0001"+
		"\u0000\u0000\u0000\u001a\u008b\u0001\u0000\u0000\u0000\u001c\u008e\u0001"+
		"\u0000\u0000\u0000\u001e!\u0003\u0002\u0001\u0000\u001f!\u0005 \u0000"+
		"\u0000 \u001e\u0001\u0000\u0000\u0000 \u001f\u0001\u0000\u0000\u0000!"+
		"$\u0001\u0000\u0000\u0000\" \u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000"+
		"\u0000#%\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000%&\u0005\u0000"+
		"\u0000\u0001&\u0001\u0001\u0000\u0000\u0000\'(\u0003\u001a\r\u0000(,\u0005"+
		"\u001c\u0000\u0000)+\u0003\u0004\u0002\u0000*)\u0001\u0000\u0000\u0000"+
		"+.\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000"+
		"\u0000-/\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000/3\u0003\u0006"+
		"\u0003\u000002\u0003\b\u0004\u000010\u0001\u0000\u0000\u000025\u0001\u0000"+
		"\u0000\u000031\u0001\u0000\u0000\u000034\u0001\u0000\u0000\u000046\u0001"+
		"\u0000\u0000\u000053\u0001\u0000\u0000\u000067\u0005\u001d\u0000\u0000"+
		"7\u0003\u0001\u0000\u0000\u000089\u0005\u0014\u0000\u00009;\u0005\u001b"+
		"\u0000\u0000:<\u0005\r\u0000\u0000;:\u0001\u0000\u0000\u0000;<\u0001\u0000"+
		"\u0000\u0000<=\u0001\u0000\u0000\u0000=>\u0003\n\u0005\u0000>\u0005\u0001"+
		"\u0000\u0000\u0000?@\u0005\u0013\u0000\u0000@A\u0003\n\u0005\u0000A\u0007"+
		"\u0001\u0000\u0000\u0000BC\u0005\u001a\u0000\u0000CD\u0005\u0003\u0000"+
		"\u0000DE\u0003\u0018\f\u0000E\t\u0001\u0000\u0000\u0000FG\u0003\f\u0006"+
		"\u0000G\u000b\u0001\u0000\u0000\u0000HI\u0006\u0006\uffff\uffff\u0000"+
		"IJ\u0005\u001e\u0000\u0000JK\u0003\f\u0006\u0000KL\u0005\u001f\u0000\u0000"+
		"LR\u0001\u0000\u0000\u0000MN\u0005\b\u0000\u0000NR\u0003\f\u0006\u0007"+
		"OR\u0005\u0003\u0000\u0000PR\u0003\u000e\u0007\u0000QH\u0001\u0000\u0000"+
		"\u0000QM\u0001\u0000\u0000\u0000QO\u0001\u0000\u0000\u0000QP\u0001\u0000"+
		"\u0000\u0000Ra\u0001\u0000\u0000\u0000ST\n\u0006\u0000\u0000TU\u0007\u0000"+
		"\u0000\u0000U`\u0003\f\u0006\u0007VW\n\u0005\u0000\u0000WX\u0005\f\u0000"+
		"\u0000X`\u0003\f\u0006\u0006YZ\n\u0004\u0000\u0000Z[\u0005\u000b\u0000"+
		"\u0000[`\u0003\f\u0006\u0005\\]\n\u0003\u0000\u0000]^\u0005\u0007\u0000"+
		"\u0000^`\u0003\f\u0006\u0004_S\u0001\u0000\u0000\u0000_V\u0001\u0000\u0000"+
		"\u0000_Y\u0001\u0000\u0000\u0000_\\\u0001\u0000\u0000\u0000`c\u0001\u0000"+
		"\u0000\u0000a_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000b\r\u0001"+
		"\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000de\u0005\u0005\u0000\u0000"+
		"ey\u0003\u0014\n\u0000fg\u0003\u0014\n\u0000gh\u0005\u0007\u0000\u0000"+
		"hi\u0007\u0001\u0000\u0000iy\u0001\u0000\u0000\u0000jk\u0003\u0014\n\u0000"+
		"kl\u0005\u0004\u0000\u0000lm\u0005\r\u0000\u0000my\u0001\u0000\u0000\u0000"+
		"no\u0003\u0014\n\u0000op\u0005\u0007\u0000\u0000pq\u0005\u0006\u0000\u0000"+
		"qr\u0003\u001c\u000e\u0000ry\u0001\u0000\u0000\u0000st\u0005\u001b\u0000"+
		"\u0000tu\u0005\r\u0000\u0000uv\u0007\u0002\u0000\u0000vw\u0005\u001b\u0000"+
		"\u0000wy\u0005\r\u0000\u0000xd\u0001\u0000\u0000\u0000xf\u0001\u0000\u0000"+
		"\u0000xj\u0001\u0000\u0000\u0000xn\u0001\u0000\u0000\u0000xs\u0001\u0000"+
		"\u0000\u0000y\u000f\u0001\u0000\u0000\u0000z\u007f\u0003\u0012\t\u0000"+
		"{\u007f\u0003\u0014\n\u0000|\u007f\u0003\u0016\u000b\u0000}\u007f\u0003"+
		"\u0018\f\u0000~z\u0001\u0000\u0000\u0000~{\u0001\u0000\u0000\u0000~|\u0001"+
		"\u0000\u0000\u0000~}\u0001\u0000\u0000\u0000\u007f\u0011\u0001\u0000\u0000"+
		"\u0000\u0080\u0081\u0005\u0019\u0000\u0000\u0081\u0082\u0005\r\u0000\u0000"+
		"\u0082\u0013\u0001\u0000\u0000\u0000\u0083\u0084\u0005\u0017\u0000\u0000"+
		"\u0084\u0085\u0005\r\u0000\u0000\u0085\u0015\u0001\u0000\u0000\u0000\u0086"+
		"\u0087\u0005\u0018\u0000\u0000\u0087\u0088\u0005\r\u0000\u0000\u0088\u0017"+
		"\u0001\u0000\u0000\u0000\u0089\u008a\u0005\r\u0000\u0000\u008a\u0019\u0001"+
		"\u0000\u0000\u0000\u008b\u008c\u0005\u0016\u0000\u0000\u008c\u008d\u0005"+
		"\r\u0000\u0000\u008d\u001b\u0001\u0000\u0000\u0000\u008e\u008f\u0005\u0015"+
		"\u0000\u0000\u008f\u0090\u0005\u001e\u0000\u0000\u0090\u0095\u0005\r\u0000"+
		"\u0000\u0091\u0092\u0005\n\u0000\u0000\u0092\u0094\u0005\r\u0000\u0000"+
		"\u0093\u0091\u0001\u0000\u0000\u0000\u0094\u0097\u0001\u0000\u0000\u0000"+
		"\u0095\u0093\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000"+
		"\u0096\u0098\u0001\u0000\u0000\u0000\u0097\u0095\u0001\u0000\u0000\u0000"+
		"\u0098\u0099\u0005\u001f\u0000\u0000\u0099\u001d\u0001\u0000\u0000\u0000"+
		"\u000b \",3;Q_ax~\u0095";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
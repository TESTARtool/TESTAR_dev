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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, STRING=30, INT=31, BOOL=32, 
		LB=33, RB=34, LP=35, RP=36, COMMENT=37, WHITESPACE=38, ANY_CHARACTERS=39;
	public static final int
		RULE_oracles_file = 0, RULE_oracle = 1, RULE_given_block = 2, RULE_group_block = 3, 
		RULE_check_block = 4, RULE_trigger_block = 5, RULE_property_block = 6, 
		RULE_bool_expr = 7, RULE_property_line = 8, RULE_list = 9, RULE_comparator = 10, 
		RULE_regex_string = 11, RULE_raw_string = 12, RULE_basic_string = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"oracles_file", "oracle", "given_block", "group_block", "check_block", 
			"trigger_block", "property_block", "bool_expr", "property_line", "list", 
			"comparator", "regex_string", "raw_string", "basic_string"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'ORACLE'", "'GIVEN'", "'STATE'", "'IN'", "'LAST_ACTION'", "'GROUP'", 
			"'WIDGET'", "'CHECK'", "'TRIGGER'", "'NOT'", "'AND'", "','", "'XOR'", 
			"'^'", "'OR'", "'|'", "'IS'", "'EQUALS'", "'PROP'", "'KEY'", "'VALUE'", 
			"'ANY'", "'LIST'", "'MATCHES'", "'CONTAINS'", "'STARTS_WITH'", "'ENDS_WITH'", 
			"'REGEX'", "'RAW'", null, null, null, "'{'", "'}'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "STRING", "INT", "BOOL", "LB", "RB", 
			"LP", "RP", "COMMENT", "WHITESPACE", "ANY_CHARACTERS"
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
			setState(30); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(30);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(28);
					oracle();
					}
					break;
				case COMMENT:
					{
					setState(29);
					match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(32); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 || _la==COMMENT );
			setState(34);
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
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public TerminalNode LB() { return getToken(OraclesParser.LB, 0); }
		public Check_blockContext check_block() {
			return getRuleContext(Check_blockContext.class,0);
		}
		public TerminalNode RB() { return getToken(OraclesParser.RB, 0); }
		public List<Given_blockContext> given_block() {
			return getRuleContexts(Given_blockContext.class);
		}
		public Given_blockContext given_block(int i) {
			return getRuleContext(Given_blockContext.class,i);
		}
		public List<Group_blockContext> group_block() {
			return getRuleContexts(Group_blockContext.class);
		}
		public Group_blockContext group_block(int i) {
			return getRuleContext(Group_blockContext.class,i);
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
			setState(36);
			match(T__0);
			setState(37);
			match(STRING);
			setState(38);
			match(LB);
			setState(42);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(39);
				given_block();
				}
				}
				setState(44);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(48);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(45);
				group_block();
				}
				}
				setState(50);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(51);
			check_block();
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(52);
				trigger_block();
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(58);
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
	public static class Given_blockContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public Property_blockContext property_block() {
			return getRuleContext(Property_blockContext.class,0);
		}
		public Given_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_given_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterGiven_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitGiven_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitGiven_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Given_blockContext given_block() throws RecognitionException {
		Given_blockContext _localctx = new Given_blockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_given_block);
		try {
			setState(67);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(60);
				match(T__1);
				setState(61);
				match(T__2);
				setState(62);
				match(T__3);
				setState(63);
				match(STRING);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(64);
				match(T__1);
				setState(65);
				match(T__4);
				setState(66);
				property_block();
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
	public static class Group_blockContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public Property_blockContext property_block() {
			return getRuleContext(Property_blockContext.class,0);
		}
		public Group_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterGroup_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitGroup_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitGroup_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_blockContext group_block() throws RecognitionException {
		Group_blockContext _localctx = new Group_blockContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_group_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(T__5);
			setState(70);
			match(T__6);
			setState(71);
			match(STRING);
			setState(72);
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
		enterRule(_localctx, 8, RULE_check_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(T__7);
			setState(75);
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
		public TerminalNode BOOL() { return getToken(OraclesParser.BOOL, 0); }
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
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
		enterRule(_localctx, 10, RULE_trigger_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(77);
			match(T__8);
			setState(78);
			match(BOOL);
			setState(79);
			match(STRING);
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
		enterRule(_localctx, 12, RULE_property_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
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
		public Bool_exprContext right;
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
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
	public static class IsOprExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Bool_exprContext right;
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public IsOprExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterIsOprExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitIsOprExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitIsOprExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class XorOprExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Bool_exprContext right;
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
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
		public Bool_exprContext right;
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
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
		int _startState = 14;
		enterRecursionRule(_localctx, 14, RULE_bool_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LP:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(84);
				match(LP);
				setState(85);
				bool_expr(0);
				setState(86);
				match(RP);
				}
				break;
			case T__9:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(88);
				match(T__9);
				setState(89);
				bool_expr(7);
				}
				break;
			case BOOL:
				{
				_localctx = new PlainBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(90);
				match(BOOL);
				}
				break;
			case T__18:
				{
				_localctx = new PropertyBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(91);
				property_line();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(108);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(106);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
					case 1:
						{
						_localctx = new AndOprExprContext(new Bool_exprContext(_parentctx, _parentState));
						((AndOprExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(94);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(95);
						_la = _input.LA(1);
						if ( !(_la==T__10 || _la==T__11) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(96);
						((AndOprExprContext)_localctx).right = bool_expr(7);
						}
						break;
					case 2:
						{
						_localctx = new XorOprExprContext(new Bool_exprContext(_parentctx, _parentState));
						((XorOprExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(97);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(98);
						_la = _input.LA(1);
						if ( !(_la==T__12 || _la==T__13) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(99);
						((XorOprExprContext)_localctx).right = bool_expr(6);
						}
						break;
					case 3:
						{
						_localctx = new OrOprExprContext(new Bool_exprContext(_parentctx, _parentState));
						((OrOprExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(100);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(101);
						_la = _input.LA(1);
						if ( !(_la==T__14 || _la==T__15) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(102);
						((OrOprExprContext)_localctx).right = bool_expr(5);
						}
						break;
					case 4:
						{
						_localctx = new IsOprExprContext(new Bool_exprContext(_parentctx, _parentState));
						((IsOprExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(103);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(104);
						_la = _input.LA(1);
						if ( !(_la==T__16 || _la==T__17) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(105);
						((IsOprExprContext)_localctx).right = bool_expr(4);
						}
						break;
					}
					} 
				}
				setState(110);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
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
	public static class PropIsBoolContext extends Property_lineContext {
		public TerminalNode BOOL() { return getToken(OraclesParser.BOOL, 0); }
		public PropIsBoolContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropIsBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropIsBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropIsBool(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropAnyContext extends Property_lineContext {
		public ComparatorContext comparator() {
			return getRuleContext(ComparatorContext.class,0);
		}
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public PropAnyContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropAny(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropAny(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropAny(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropValueContext extends Property_lineContext {
		public ComparatorContext comparator() {
			return getRuleContext(ComparatorContext.class,0);
		}
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public PropValueContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropValue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropKeyContext extends Property_lineContext {
		public ComparatorContext comparator() {
			return getRuleContext(ComparatorContext.class,0);
		}
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public PropKeyContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropKey(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropKey(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropIsInListContext extends Property_lineContext {
		public Token type;
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
	@SuppressWarnings("CheckReturnValue")
	public static class PropKeyValueContext extends Property_lineContext {
		public Token key;
		public Token value;
		public List<TerminalNode> LP() { return getTokens(OraclesParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(OraclesParser.LP, i);
		}
		public List<TerminalNode> RP() { return getTokens(OraclesParser.RP); }
		public TerminalNode RP(int i) {
			return getToken(OraclesParser.RP, i);
		}
		public ComparatorContext comparator() {
			return getRuleContext(ComparatorContext.class,0);
		}
		public List<TerminalNode> STRING() { return getTokens(OraclesParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(OraclesParser.STRING, i);
		}
		public PropKeyValueContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropKeyValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropKeyValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropKeyValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Property_lineContext property_line() throws RecognitionException {
		Property_lineContext _localctx = new Property_lineContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_property_line);
		int _la;
		try {
			setState(148);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new PropKeyValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(111);
				match(T__18);
				setState(112);
				match(LP);
				setState(113);
				match(T__19);
				setState(114);
				match(T__11);
				setState(115);
				match(T__20);
				setState(116);
				match(RP);
				setState(117);
				comparator();
				setState(118);
				match(LP);
				setState(119);
				((PropKeyValueContext)_localctx).key = match(STRING);
				setState(120);
				match(T__11);
				setState(121);
				((PropKeyValueContext)_localctx).value = match(STRING);
				setState(122);
				match(RP);
				}
				break;
			case 2:
				_localctx = new PropKeyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(124);
				match(T__18);
				setState(125);
				match(T__19);
				setState(126);
				comparator();
				setState(127);
				match(STRING);
				}
				break;
			case 3:
				_localctx = new PropValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(129);
				match(T__18);
				setState(130);
				match(T__20);
				setState(131);
				comparator();
				setState(132);
				match(STRING);
				}
				break;
			case 4:
				_localctx = new PropAnyContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(134);
				match(T__18);
				setState(135);
				match(T__21);
				setState(136);
				comparator();
				setState(137);
				match(STRING);
				}
				break;
			case 5:
				_localctx = new PropIsBoolContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(139);
				match(T__18);
				setState(140);
				match(T__20);
				setState(141);
				match(T__16);
				setState(142);
				match(BOOL);
				}
				break;
			case 6:
				_localctx = new PropIsInListContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(143);
				match(T__18);
				setState(144);
				((PropIsInListContext)_localctx).type = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 7340032L) != 0)) ) {
					((PropIsInListContext)_localctx).type = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(145);
				match(T__16);
				setState(146);
				match(T__3);
				setState(147);
				list();
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
	public static class ListContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(OraclesParser.LP, 0); }
		public List<TerminalNode> STRING() { return getTokens(OraclesParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(OraclesParser.STRING, i);
		}
		public TerminalNode RP() { return getToken(OraclesParser.RP, 0); }
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
		enterRule(_localctx, 18, RULE_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(T__22);
			setState(151);
			match(LP);
			setState(152);
			match(STRING);
			setState(157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(153);
				match(T__11);
				setState(154);
				match(STRING);
				}
				}
				setState(159);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(160);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ComparatorContext extends ParserRuleContext {
		public ComparatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparator; }
	 
		public ComparatorContext() { }
		public void copyFrom(ComparatorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Comparator_isContext extends ComparatorContext {
		public Comparator_isContext(ComparatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterComparator_is(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitComparator_is(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitComparator_is(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Comparator_matchesContext extends ComparatorContext {
		public Comparator_matchesContext(ComparatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterComparator_matches(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitComparator_matches(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitComparator_matches(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Comparator_startsWithContext extends ComparatorContext {
		public Comparator_startsWithContext(ComparatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterComparator_startsWith(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitComparator_startsWith(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitComparator_startsWith(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Comparator_endsWithContext extends ComparatorContext {
		public Comparator_endsWithContext(ComparatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterComparator_endsWith(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitComparator_endsWith(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitComparator_endsWith(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Comparator_containsContext extends ComparatorContext {
		public Comparator_containsContext(ComparatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterComparator_contains(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitComparator_contains(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitComparator_contains(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparatorContext comparator() throws RecognitionException {
		ComparatorContext _localctx = new ComparatorContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_comparator);
		try {
			setState(167);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__16:
				_localctx = new Comparator_isContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(162);
				match(T__16);
				}
				break;
			case T__23:
				_localctx = new Comparator_matchesContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(163);
				match(T__23);
				}
				break;
			case T__24:
				_localctx = new Comparator_containsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(164);
				match(T__24);
				}
				break;
			case T__25:
				_localctx = new Comparator_startsWithContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(165);
				match(T__25);
				}
				break;
			case T__26:
				_localctx = new Comparator_endsWithContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(166);
				match(T__26);
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
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
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
		enterRule(_localctx, 22, RULE_regex_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			match(T__27);
			setState(170);
			match(STRING);
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
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
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
		enterRule(_localctx, 24, RULE_raw_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			match(T__28);
			setState(173);
			match(STRING);
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
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
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
		enterRule(_localctx, 26, RULE_basic_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(175);
			match(STRING);
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
		case 7:
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
		"\u0004\u0001\'\u00b2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0004\u0000\u001f\b"+
		"\u0000\u000b\u0000\f\u0000 \u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0005\u0001)\b\u0001\n\u0001\f\u0001,\t"+
		"\u0001\u0001\u0001\u0005\u0001/\b\u0001\n\u0001\f\u00012\t\u0001\u0001"+
		"\u0001\u0001\u0001\u0005\u00016\b\u0001\n\u0001\f\u00019\t\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0003\u0002D\b\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007]\b\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005"+
		"\u0007k\b\u0007\n\u0007\f\u0007n\t\u0007\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u0095\b\b\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005\t\u009c\b\t\n\t\f\t\u009f\t\t"+
		"\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u00a8"+
		"\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001"+
		"\r\u0001\r\u0001\r\u0000\u0001\u000e\u000e\u0000\u0002\u0004\u0006\b\n"+
		"\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u0000\u0005\u0001\u0000\u000b"+
		"\f\u0001\u0000\r\u000e\u0001\u0000\u000f\u0010\u0001\u0000\u0011\u0012"+
		"\u0001\u0000\u0014\u0016\u00ba\u0000\u001e\u0001\u0000\u0000\u0000\u0002"+
		"$\u0001\u0000\u0000\u0000\u0004C\u0001\u0000\u0000\u0000\u0006E\u0001"+
		"\u0000\u0000\u0000\bJ\u0001\u0000\u0000\u0000\nM\u0001\u0000\u0000\u0000"+
		"\fQ\u0001\u0000\u0000\u0000\u000e\\\u0001\u0000\u0000\u0000\u0010\u0094"+
		"\u0001\u0000\u0000\u0000\u0012\u0096\u0001\u0000\u0000\u0000\u0014\u00a7"+
		"\u0001\u0000\u0000\u0000\u0016\u00a9\u0001\u0000\u0000\u0000\u0018\u00ac"+
		"\u0001\u0000\u0000\u0000\u001a\u00af\u0001\u0000\u0000\u0000\u001c\u001f"+
		"\u0003\u0002\u0001\u0000\u001d\u001f\u0005%\u0000\u0000\u001e\u001c\u0001"+
		"\u0000\u0000\u0000\u001e\u001d\u0001\u0000\u0000\u0000\u001f \u0001\u0000"+
		"\u0000\u0000 \u001e\u0001\u0000\u0000\u0000 !\u0001\u0000\u0000\u0000"+
		"!\"\u0001\u0000\u0000\u0000\"#\u0005\u0000\u0000\u0001#\u0001\u0001\u0000"+
		"\u0000\u0000$%\u0005\u0001\u0000\u0000%&\u0005\u001e\u0000\u0000&*\u0005"+
		"!\u0000\u0000\')\u0003\u0004\u0002\u0000(\'\u0001\u0000\u0000\u0000),"+
		"\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000*+\u0001\u0000\u0000"+
		"\u0000+0\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000-/\u0003\u0006"+
		"\u0003\u0000.-\u0001\u0000\u0000\u0000/2\u0001\u0000\u0000\u00000.\u0001"+
		"\u0000\u0000\u000001\u0001\u0000\u0000\u000013\u0001\u0000\u0000\u0000"+
		"20\u0001\u0000\u0000\u000037\u0003\b\u0004\u000046\u0003\n\u0005\u0000"+
		"54\u0001\u0000\u0000\u000069\u0001\u0000\u0000\u000075\u0001\u0000\u0000"+
		"\u000078\u0001\u0000\u0000\u00008:\u0001\u0000\u0000\u000097\u0001\u0000"+
		"\u0000\u0000:;\u0005\"\u0000\u0000;\u0003\u0001\u0000\u0000\u0000<=\u0005"+
		"\u0002\u0000\u0000=>\u0005\u0003\u0000\u0000>?\u0005\u0004\u0000\u0000"+
		"?D\u0005\u001e\u0000\u0000@A\u0005\u0002\u0000\u0000AB\u0005\u0005\u0000"+
		"\u0000BD\u0003\f\u0006\u0000C<\u0001\u0000\u0000\u0000C@\u0001\u0000\u0000"+
		"\u0000D\u0005\u0001\u0000\u0000\u0000EF\u0005\u0006\u0000\u0000FG\u0005"+
		"\u0007\u0000\u0000GH\u0005\u001e\u0000\u0000HI\u0003\f\u0006\u0000I\u0007"+
		"\u0001\u0000\u0000\u0000JK\u0005\b\u0000\u0000KL\u0003\f\u0006\u0000L"+
		"\t\u0001\u0000\u0000\u0000MN\u0005\t\u0000\u0000NO\u0005 \u0000\u0000"+
		"OP\u0005\u001e\u0000\u0000P\u000b\u0001\u0000\u0000\u0000QR\u0003\u000e"+
		"\u0007\u0000R\r\u0001\u0000\u0000\u0000ST\u0006\u0007\uffff\uffff\u0000"+
		"TU\u0005#\u0000\u0000UV\u0003\u000e\u0007\u0000VW\u0005$\u0000\u0000W"+
		"]\u0001\u0000\u0000\u0000XY\u0005\n\u0000\u0000Y]\u0003\u000e\u0007\u0007"+
		"Z]\u0005 \u0000\u0000[]\u0003\u0010\b\u0000\\S\u0001\u0000\u0000\u0000"+
		"\\X\u0001\u0000\u0000\u0000\\Z\u0001\u0000\u0000\u0000\\[\u0001\u0000"+
		"\u0000\u0000]l\u0001\u0000\u0000\u0000^_\n\u0006\u0000\u0000_`\u0007\u0000"+
		"\u0000\u0000`k\u0003\u000e\u0007\u0007ab\n\u0005\u0000\u0000bc\u0007\u0001"+
		"\u0000\u0000ck\u0003\u000e\u0007\u0006de\n\u0004\u0000\u0000ef\u0007\u0002"+
		"\u0000\u0000fk\u0003\u000e\u0007\u0005gh\n\u0003\u0000\u0000hi\u0007\u0003"+
		"\u0000\u0000ik\u0003\u000e\u0007\u0004j^\u0001\u0000\u0000\u0000ja\u0001"+
		"\u0000\u0000\u0000jd\u0001\u0000\u0000\u0000jg\u0001\u0000\u0000\u0000"+
		"kn\u0001\u0000\u0000\u0000lj\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000"+
		"\u0000m\u000f\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000op\u0005"+
		"\u0013\u0000\u0000pq\u0005#\u0000\u0000qr\u0005\u0014\u0000\u0000rs\u0005"+
		"\f\u0000\u0000st\u0005\u0015\u0000\u0000tu\u0005$\u0000\u0000uv\u0003"+
		"\u0014\n\u0000vw\u0005#\u0000\u0000wx\u0005\u001e\u0000\u0000xy\u0005"+
		"\f\u0000\u0000yz\u0005\u001e\u0000\u0000z{\u0005$\u0000\u0000{\u0095\u0001"+
		"\u0000\u0000\u0000|}\u0005\u0013\u0000\u0000}~\u0005\u0014\u0000\u0000"+
		"~\u007f\u0003\u0014\n\u0000\u007f\u0080\u0005\u001e\u0000\u0000\u0080"+
		"\u0095\u0001\u0000\u0000\u0000\u0081\u0082\u0005\u0013\u0000\u0000\u0082"+
		"\u0083\u0005\u0015\u0000\u0000\u0083\u0084\u0003\u0014\n\u0000\u0084\u0085"+
		"\u0005\u001e\u0000\u0000\u0085\u0095\u0001\u0000\u0000\u0000\u0086\u0087"+
		"\u0005\u0013\u0000\u0000\u0087\u0088\u0005\u0016\u0000\u0000\u0088\u0089"+
		"\u0003\u0014\n\u0000\u0089\u008a\u0005\u001e\u0000\u0000\u008a\u0095\u0001"+
		"\u0000\u0000\u0000\u008b\u008c\u0005\u0013\u0000\u0000\u008c\u008d\u0005"+
		"\u0015\u0000\u0000\u008d\u008e\u0005\u0011\u0000\u0000\u008e\u0095\u0005"+
		" \u0000\u0000\u008f\u0090\u0005\u0013\u0000\u0000\u0090\u0091\u0007\u0004"+
		"\u0000\u0000\u0091\u0092\u0005\u0011\u0000\u0000\u0092\u0093\u0005\u0004"+
		"\u0000\u0000\u0093\u0095\u0003\u0012\t\u0000\u0094o\u0001\u0000\u0000"+
		"\u0000\u0094|\u0001\u0000\u0000\u0000\u0094\u0081\u0001\u0000\u0000\u0000"+
		"\u0094\u0086\u0001\u0000\u0000\u0000\u0094\u008b\u0001\u0000\u0000\u0000"+
		"\u0094\u008f\u0001\u0000\u0000\u0000\u0095\u0011\u0001\u0000\u0000\u0000"+
		"\u0096\u0097\u0005\u0017\u0000\u0000\u0097\u0098\u0005#\u0000\u0000\u0098"+
		"\u009d\u0005\u001e\u0000\u0000\u0099\u009a\u0005\f\u0000\u0000\u009a\u009c"+
		"\u0005\u001e\u0000\u0000\u009b\u0099\u0001\u0000\u0000\u0000\u009c\u009f"+
		"\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009d\u009e"+
		"\u0001\u0000\u0000\u0000\u009e\u00a0\u0001\u0000\u0000\u0000\u009f\u009d"+
		"\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005$\u0000\u0000\u00a1\u0013\u0001"+
		"\u0000\u0000\u0000\u00a2\u00a8\u0005\u0011\u0000\u0000\u00a3\u00a8\u0005"+
		"\u0018\u0000\u0000\u00a4\u00a8\u0005\u0019\u0000\u0000\u00a5\u00a8\u0005"+
		"\u001a\u0000\u0000\u00a6\u00a8\u0005\u001b\u0000\u0000\u00a7\u00a2\u0001"+
		"\u0000\u0000\u0000\u00a7\u00a3\u0001\u0000\u0000\u0000\u00a7\u00a4\u0001"+
		"\u0000\u0000\u0000\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a7\u00a6\u0001"+
		"\u0000\u0000\u0000\u00a8\u0015\u0001\u0000\u0000\u0000\u00a9\u00aa\u0005"+
		"\u001c\u0000\u0000\u00aa\u00ab\u0005\u001e\u0000\u0000\u00ab\u0017\u0001"+
		"\u0000\u0000\u0000\u00ac\u00ad\u0005\u001d\u0000\u0000\u00ad\u00ae\u0005"+
		"\u001e\u0000\u0000\u00ae\u0019\u0001\u0000\u0000\u0000\u00af\u00b0\u0005"+
		"\u001e\u0000\u0000\u00b0\u001b\u0001\u0000\u0000\u0000\f\u001e *07C\\"+
		"jl\u0094\u009d\u00a7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
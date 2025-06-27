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
		T__0=1, T__1=2, STRING=3, INT=4, AND=5, ANY=6, APPLY_TO=7, CHECK=8, CONTAINS=9, 
		ENDS_WITH=10, EQUALS=11, FAIL_IF=12, GIVEN=13, GROUP=14, IGNORE=15, IN=16, 
		IS=17, KEY=18, LAST_ACTION=19, LIST=20, MATCHES=21, NOT=22, OR=23, ORACLE=24, 
		PASS_IF=25, PROP=26, RANGE=27, RAW=28, REGEX=29, STARTS_WITH=30, STATE=31, 
		TRIGGER=32, VALUE=33, WIDGET=34, XOR=35, BOOL=36, COMMA=37, COLON=38, 
		LB=39, RB=40, LP=41, RP=42, COMMENT=43, WHITESPACE=44, ANY_CHARACTERS=45;
	public static final int
		RULE_oracles_file = 0, RULE_oracle = 1, RULE_given_block = 2, RULE_group_block = 3, 
		RULE_check_block = 4, RULE_trigger_block = 5, RULE_check_type = 6, RULE_property_block = 7, 
		RULE_bool_expr = 8, RULE_operator = 9, RULE_property_line = 10, RULE_list = 11, 
		RULE_range = 12, RULE_location = 13, RULE_comparator = 14, RULE_regex_string = 15, 
		RULE_raw_string = 16, RULE_basic_string = 17;
	private static String[] makeRuleNames() {
		return new String[] {
			"oracles_file", "oracle", "given_block", "group_block", "check_block", 
			"trigger_block", "check_type", "property_block", "bool_expr", "operator", 
			"property_line", "list", "range", "location", "comparator", "regex_string", 
			"raw_string", "basic_string"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'^'", "'|'", null, null, "'AND'", "'ANY'", "'APPLY_TO'", "'CHECK'", 
			"'CONTAINS'", "'ENDS_WITH'", "'EQUALS'", "'FAIL_IF'", "'GIVEN'", "'GROUP'", 
			"'IGNORE'", "'IN'", "'IS'", "'KEY'", "'LAST_ACTION'", "'LIST'", "'MATCHES'", 
			"'NOT'", "'OR'", "'ORACLE'", "'PASS_IF'", null, "'RANGE'", "'RAW'", "'REGEX'", 
			"'STARTS_WITH'", "'STATE'", "'TRIGGER'", "'VALUE'", "'WIDGET'", "'XOR'", 
			null, "','", "':'", "'{'", "'}'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "STRING", "INT", "AND", "ANY", "APPLY_TO", "CHECK", 
			"CONTAINS", "ENDS_WITH", "EQUALS", "FAIL_IF", "GIVEN", "GROUP", "IGNORE", 
			"IN", "IS", "KEY", "LAST_ACTION", "LIST", "MATCHES", "NOT", "OR", "ORACLE", 
			"PASS_IF", "PROP", "RANGE", "RAW", "REGEX", "STARTS_WITH", "STATE", "TRIGGER", 
			"VALUE", "WIDGET", "XOR", "BOOL", "COMMA", "COLON", "LB", "RB", "LP", 
			"RP", "COMMENT", "WHITESPACE", "ANY_CHARACTERS"
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
			setState(38); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(38);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case IGNORE:
				case ORACLE:
					{
					setState(36);
					oracle();
					}
					break;
				case COMMENT:
					{
					setState(37);
					match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(40); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 8796109832192L) != 0) );
			setState(42);
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
		public TerminalNode ORACLE() { return getToken(OraclesParser.ORACLE, 0); }
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public TerminalNode LB() { return getToken(OraclesParser.LB, 0); }
		public Check_blockContext check_block() {
			return getRuleContext(Check_blockContext.class,0);
		}
		public TerminalNode RB() { return getToken(OraclesParser.RB, 0); }
		public TerminalNode IGNORE() { return getToken(OraclesParser.IGNORE, 0); }
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
		public Trigger_blockContext trigger_block() {
			return getRuleContext(Trigger_blockContext.class,0);
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
			setState(45);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IGNORE) {
				{
				setState(44);
				match(IGNORE);
				}
			}

			setState(47);
			match(ORACLE);
			setState(48);
			match(STRING);
			setState(49);
			match(LB);
			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==GIVEN) {
				{
				{
				setState(50);
				given_block();
				}
				}
				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==GROUP) {
				{
				{
				setState(56);
				group_block();
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(62);
			check_block();
			setState(64);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TRIGGER) {
				{
				setState(63);
				trigger_block();
				}
			}

			setState(66);
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
		public TerminalNode GIVEN() { return getToken(OraclesParser.GIVEN, 0); }
		public TerminalNode STATE() { return getToken(OraclesParser.STATE, 0); }
		public TerminalNode IN() { return getToken(OraclesParser.IN, 0); }
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public TerminalNode LAST_ACTION() { return getToken(OraclesParser.LAST_ACTION, 0); }
		public TerminalNode COLON() { return getToken(OraclesParser.COLON, 0); }
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
			setState(76);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(68);
				match(GIVEN);
				setState(69);
				match(STATE);
				setState(70);
				match(IN);
				setState(71);
				match(STRING);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(72);
				match(GIVEN);
				setState(73);
				match(LAST_ACTION);
				setState(74);
				match(COLON);
				setState(75);
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
		public TerminalNode GROUP() { return getToken(OraclesParser.GROUP, 0); }
		public TerminalNode WIDGET() { return getToken(OraclesParser.WIDGET, 0); }
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public TerminalNode COLON() { return getToken(OraclesParser.COLON, 0); }
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
			setState(78);
			match(GROUP);
			setState(79);
			match(WIDGET);
			setState(80);
			match(STRING);
			setState(81);
			match(COLON);
			setState(82);
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
		public Check_typeContext check_type() {
			return getRuleContext(Check_typeContext.class,0);
		}
		public List<TerminalNode> COLON() { return getTokens(OraclesParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(OraclesParser.COLON, i);
		}
		public Property_blockContext property_block() {
			return getRuleContext(Property_blockContext.class,0);
		}
		public TerminalNode APPLY_TO() { return getToken(OraclesParser.APPLY_TO, 0); }
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(CHECK);
			setState(85);
			check_type();
			setState(86);
			match(COLON);
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==APPLY_TO) {
				{
				setState(87);
				match(APPLY_TO);
				setState(88);
				match(STRING);
				setState(89);
				match(COLON);
				}
			}

			setState(92);
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
			setState(94);
			match(TRIGGER);
			setState(95);
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
	public static class Check_typeContext extends ParserRuleContext {
		public Check_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_check_type; }
	 
		public Check_typeContext() { }
		public void copyFrom(Check_typeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FailContext extends Check_typeContext {
		public TerminalNode FAIL_IF() { return getToken(OraclesParser.FAIL_IF, 0); }
		public FailContext(Check_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterFail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitFail(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitFail(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PassContext extends Check_typeContext {
		public TerminalNode PASS_IF() { return getToken(OraclesParser.PASS_IF, 0); }
		public PassContext(Check_typeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPass(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPass(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPass(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Check_typeContext check_type() throws RecognitionException {
		Check_typeContext _localctx = new Check_typeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_check_type);
		try {
			setState(99);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PASS_IF:
				_localctx = new PassContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(97);
				match(PASS_IF);
				}
				break;
			case FAIL_IF:
				_localctx = new FailContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(98);
				match(FAIL_IF);
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
		enterRule(_localctx, 14, RULE_property_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
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
	public static class OperatorExprContext extends Bool_exprContext {
		public Bool_exprContext left;
		public Bool_exprContext right;
		public OperatorContext operator() {
			return getRuleContext(OperatorContext.class,0);
		}
		public List<Bool_exprContext> bool_expr() {
			return getRuleContexts(Bool_exprContext.class);
		}
		public Bool_exprContext bool_expr(int i) {
			return getRuleContext(Bool_exprContext.class,i);
		}
		public OperatorExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterOperatorExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitOperatorExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitOperatorExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PlainBoolExprContext extends Bool_exprContext {
		public Bool_exprContext bool_expr() {
			return getRuleContext(Bool_exprContext.class,0);
		}
		public TerminalNode IS() { return getToken(OraclesParser.IS, 0); }
		public TerminalNode BOOL() { return getToken(OraclesParser.BOOL, 0); }
		public PlainBoolExprContext(Bool_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPlainBoolExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPlainBoolExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPlainBoolExpr(this);
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
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_bool_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LP:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(104);
				match(LP);
				setState(105);
				bool_expr(0);
				setState(106);
				match(RP);
				}
				break;
			case NOT:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(108);
				match(NOT);
				setState(109);
				bool_expr(4);
				}
				break;
			case PROP:
				{
				_localctx = new PropertyBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(110);
				property_line();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(122);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(120);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
					case 1:
						{
						_localctx = new OperatorExprContext(new Bool_exprContext(_parentctx, _parentState));
						((OperatorExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(113);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(114);
						operator();
						setState(115);
						((OperatorExprContext)_localctx).right = bool_expr(4);
						}
						break;
					case 2:
						{
						_localctx = new PlainBoolExprContext(new Bool_exprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_bool_expr);
						setState(117);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(118);
						match(IS);
						setState(119);
						match(BOOL);
						}
						break;
					}
					} 
				}
				setState(124);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
	public static class OperatorContext extends ParserRuleContext {
		public OperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator; }
	 
		public OperatorContext() { }
		public void copyFrom(OperatorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Operator_andContext extends OperatorContext {
		public TerminalNode AND() { return getToken(OraclesParser.AND, 0); }
		public TerminalNode COMMA() { return getToken(OraclesParser.COMMA, 0); }
		public Operator_andContext(OperatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterOperator_and(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitOperator_and(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitOperator_and(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Operator_equalsContext extends OperatorContext {
		public TerminalNode EQUALS() { return getToken(OraclesParser.EQUALS, 0); }
		public Operator_equalsContext(OperatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterOperator_equals(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitOperator_equals(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitOperator_equals(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Operator_xorContext extends OperatorContext {
		public TerminalNode XOR() { return getToken(OraclesParser.XOR, 0); }
		public Operator_xorContext(OperatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterOperator_xor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitOperator_xor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitOperator_xor(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Operator_orContext extends OperatorContext {
		public TerminalNode OR() { return getToken(OraclesParser.OR, 0); }
		public Operator_orContext(OperatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterOperator_or(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitOperator_or(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitOperator_or(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperatorContext operator() throws RecognitionException {
		OperatorContext _localctx = new OperatorContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_operator);
		int _la;
		try {
			setState(129);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AND:
			case COMMA:
				_localctx = new Operator_andContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(125);
				_la = _input.LA(1);
				if ( !(_la==AND || _la==COMMA) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case T__0:
			case XOR:
				_localctx = new Operator_xorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(126);
				_la = _input.LA(1);
				if ( !(_la==T__0 || _la==XOR) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case T__1:
			case OR:
				_localctx = new Operator_orContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(127);
				_la = _input.LA(1);
				if ( !(_la==T__1 || _la==OR) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case EQUALS:
				_localctx = new Operator_equalsContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(128);
				match(EQUALS);
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
	public static class PropStandardContext extends Property_lineContext {
		public TerminalNode PROP() { return getToken(OraclesParser.PROP, 0); }
		public LocationContext location() {
			return getRuleContext(LocationContext.class,0);
		}
		public ComparatorContext comparator() {
			return getRuleContext(ComparatorContext.class,0);
		}
		public TerminalNode STRING() { return getToken(OraclesParser.STRING, 0); }
		public PropStandardContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropStandard(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropStandard(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropStandard(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropIsBoolContext extends Property_lineContext {
		public TerminalNode PROP() { return getToken(OraclesParser.PROP, 0); }
		public TerminalNode VALUE() { return getToken(OraclesParser.VALUE, 0); }
		public TerminalNode IS() { return getToken(OraclesParser.IS, 0); }
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
	public static class PropIsInRangeContext extends Property_lineContext {
		public TerminalNode PROP() { return getToken(OraclesParser.PROP, 0); }
		public TerminalNode VALUE() { return getToken(OraclesParser.VALUE, 0); }
		public TerminalNode IS() { return getToken(OraclesParser.IS, 0); }
		public TerminalNode IN() { return getToken(OraclesParser.IN, 0); }
		public RangeContext range() {
			return getRuleContext(RangeContext.class,0);
		}
		public PropIsInRangeContext(Property_lineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterPropIsInRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitPropIsInRange(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitPropIsInRange(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropIsInListContext extends Property_lineContext {
		public TerminalNode PROP() { return getToken(OraclesParser.PROP, 0); }
		public LocationContext location() {
			return getRuleContext(LocationContext.class,0);
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
	@SuppressWarnings("CheckReturnValue")
	public static class PropKeyValueContext extends Property_lineContext {
		public Token key;
		public Token value;
		public TerminalNode PROP() { return getToken(OraclesParser.PROP, 0); }
		public List<TerminalNode> LP() { return getTokens(OraclesParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(OraclesParser.LP, i);
		}
		public TerminalNode KEY() { return getToken(OraclesParser.KEY, 0); }
		public List<TerminalNode> COMMA() { return getTokens(OraclesParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(OraclesParser.COMMA, i);
		}
		public TerminalNode VALUE() { return getToken(OraclesParser.VALUE, 0); }
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
		enterRule(_localctx, 20, RULE_property_line);
		try {
			setState(164);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new PropKeyValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(131);
				match(PROP);
				setState(132);
				match(LP);
				setState(133);
				match(KEY);
				setState(134);
				match(COMMA);
				setState(135);
				match(VALUE);
				setState(136);
				match(RP);
				setState(137);
				comparator();
				setState(138);
				match(LP);
				setState(139);
				((PropKeyValueContext)_localctx).key = match(STRING);
				setState(140);
				match(COMMA);
				setState(141);
				((PropKeyValueContext)_localctx).value = match(STRING);
				setState(142);
				match(RP);
				}
				break;
			case 2:
				_localctx = new PropIsBoolContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(144);
				match(PROP);
				setState(145);
				match(VALUE);
				setState(146);
				match(IS);
				setState(147);
				match(BOOL);
				}
				break;
			case 3:
				_localctx = new PropIsInListContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(148);
				match(PROP);
				setState(149);
				location();
				setState(150);
				match(IS);
				setState(151);
				match(IN);
				setState(152);
				list();
				}
				break;
			case 4:
				_localctx = new PropIsInRangeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(154);
				match(PROP);
				setState(155);
				match(VALUE);
				setState(156);
				match(IS);
				setState(157);
				match(IN);
				setState(158);
				range();
				}
				break;
			case 5:
				_localctx = new PropStandardContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(159);
				match(PROP);
				setState(160);
				location();
				setState(161);
				comparator();
				setState(162);
				match(STRING);
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
		public TerminalNode LIST() { return getToken(OraclesParser.LIST, 0); }
		public TerminalNode LP() { return getToken(OraclesParser.LP, 0); }
		public List<TerminalNode> STRING() { return getTokens(OraclesParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(OraclesParser.STRING, i);
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
		enterRule(_localctx, 22, RULE_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(LIST);
			setState(167);
			match(LP);
			setState(168);
			match(STRING);
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(169);
				match(COMMA);
				setState(170);
				match(STRING);
				}
				}
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(176);
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
	public static class RangeContext extends ParserRuleContext {
		public Token low;
		public Token high;
		public TerminalNode RANGE() { return getToken(OraclesParser.RANGE, 0); }
		public TerminalNode LP() { return getToken(OraclesParser.LP, 0); }
		public TerminalNode COMMA() { return getToken(OraclesParser.COMMA, 0); }
		public TerminalNode RP() { return getToken(OraclesParser.RP, 0); }
		public List<TerminalNode> INT() { return getTokens(OraclesParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(OraclesParser.INT, i);
		}
		public RangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitRange(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitRange(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RangeContext range() throws RecognitionException {
		RangeContext _localctx = new RangeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_range);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			match(RANGE);
			setState(179);
			match(LP);
			setState(180);
			((RangeContext)_localctx).low = match(INT);
			setState(181);
			match(COMMA);
			setState(182);
			((RangeContext)_localctx).high = match(INT);
			setState(183);
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
	public static class LocationContext extends ParserRuleContext {
		public LocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_location; }
	 
		public LocationContext() { }
		public void copyFrom(LocationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class KeyLocationContext extends LocationContext {
		public TerminalNode KEY() { return getToken(OraclesParser.KEY, 0); }
		public KeyLocationContext(LocationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterKeyLocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitKeyLocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitKeyLocation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AnyLocationContext extends LocationContext {
		public TerminalNode ANY() { return getToken(OraclesParser.ANY, 0); }
		public AnyLocationContext(LocationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterAnyLocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitAnyLocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitAnyLocation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ValueLocationContext extends LocationContext {
		public TerminalNode VALUE() { return getToken(OraclesParser.VALUE, 0); }
		public ValueLocationContext(LocationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterValueLocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitValueLocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitValueLocation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LocationContext location() throws RecognitionException {
		LocationContext _localctx = new LocationContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_location);
		try {
			setState(188);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case KEY:
				_localctx = new KeyLocationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(185);
				match(KEY);
				}
				break;
			case VALUE:
				_localctx = new ValueLocationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(186);
				match(VALUE);
				}
				break;
			case ANY:
				_localctx = new AnyLocationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(187);
				match(ANY);
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
	public static class Comparator_matchesContext extends ComparatorContext {
		public TerminalNode MATCHES() { return getToken(OraclesParser.MATCHES, 0); }
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
	public static class Comparator_equalsContext extends ComparatorContext {
		public TerminalNode EQUALS() { return getToken(OraclesParser.EQUALS, 0); }
		public Comparator_equalsContext(ComparatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).enterComparator_equals(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OraclesListener ) ((OraclesListener)listener).exitComparator_equals(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OraclesVisitor ) return ((OraclesVisitor<? extends T>)visitor).visitComparator_equals(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Comparator_startsWithContext extends ComparatorContext {
		public TerminalNode STARTS_WITH() { return getToken(OraclesParser.STARTS_WITH, 0); }
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
		public TerminalNode ENDS_WITH() { return getToken(OraclesParser.ENDS_WITH, 0); }
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
		public TerminalNode CONTAINS() { return getToken(OraclesParser.CONTAINS, 0); }
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
		enterRule(_localctx, 28, RULE_comparator);
		try {
			setState(195);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EQUALS:
				_localctx = new Comparator_equalsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(190);
				match(EQUALS);
				}
				break;
			case MATCHES:
				_localctx = new Comparator_matchesContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(191);
				match(MATCHES);
				}
				break;
			case CONTAINS:
				_localctx = new Comparator_containsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(192);
				match(CONTAINS);
				}
				break;
			case STARTS_WITH:
				_localctx = new Comparator_startsWithContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(193);
				match(STARTS_WITH);
				}
				break;
			case ENDS_WITH:
				_localctx = new Comparator_endsWithContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(194);
				match(ENDS_WITH);
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
		enterRule(_localctx, 30, RULE_regex_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(REGEX);
			setState(198);
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
		public TerminalNode RAW() { return getToken(OraclesParser.RAW, 0); }
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
		enterRule(_localctx, 32, RULE_raw_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			match(RAW);
			setState(201);
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
		enterRule(_localctx, 34, RULE_basic_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
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
		case 8:
			return bool_expr_sempred((Bool_exprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean bool_expr_sempred(Bool_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001-\u00ce\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0001\u0000\u0001\u0000"+
		"\u0004\u0000\'\b\u0000\u000b\u0000\f\u0000(\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0003\u0001.\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0005\u00014\b\u0001\n\u0001\f\u00017\t\u0001\u0001\u0001\u0005"+
		"\u0001:\b\u0001\n\u0001\f\u0001=\t\u0001\u0001\u0001\u0001\u0001\u0003"+
		"\u0001A\b\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003"+
		"\u0002M\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0003\u0004[\b\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0003\u0006d\b"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0003\bp\b\b\u0001\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0005\by\b\b\n\b\f\b|\t\b\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0003\t\u0082\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0003\n\u00a5\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0005\u000b\u00ac\b\u000b\n\u000b\f\u000b\u00af\t\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\r\u0001\r\u0001\r\u0003\r\u00bd\b\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0003\u000e\u00c4\b\u000e\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0000\u0001\u0010\u0012\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"\u0000\u0003\u0002"+
		"\u0000\u0005\u0005%%\u0002\u0000\u0001\u0001##\u0002\u0000\u0002\u0002"+
		"\u0017\u0017\u00d6\u0000&\u0001\u0000\u0000\u0000\u0002-\u0001\u0000\u0000"+
		"\u0000\u0004L\u0001\u0000\u0000\u0000\u0006N\u0001\u0000\u0000\u0000\b"+
		"T\u0001\u0000\u0000\u0000\n^\u0001\u0000\u0000\u0000\fc\u0001\u0000\u0000"+
		"\u0000\u000ee\u0001\u0000\u0000\u0000\u0010o\u0001\u0000\u0000\u0000\u0012"+
		"\u0081\u0001\u0000\u0000\u0000\u0014\u00a4\u0001\u0000\u0000\u0000\u0016"+
		"\u00a6\u0001\u0000\u0000\u0000\u0018\u00b2\u0001\u0000\u0000\u0000\u001a"+
		"\u00bc\u0001\u0000\u0000\u0000\u001c\u00c3\u0001\u0000\u0000\u0000\u001e"+
		"\u00c5\u0001\u0000\u0000\u0000 \u00c8\u0001\u0000\u0000\u0000\"\u00cb"+
		"\u0001\u0000\u0000\u0000$\'\u0003\u0002\u0001\u0000%\'\u0005+\u0000\u0000"+
		"&$\u0001\u0000\u0000\u0000&%\u0001\u0000\u0000\u0000\'(\u0001\u0000\u0000"+
		"\u0000(&\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)*\u0001\u0000"+
		"\u0000\u0000*+\u0005\u0000\u0000\u0001+\u0001\u0001\u0000\u0000\u0000"+
		",.\u0005\u000f\u0000\u0000-,\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000"+
		"\u0000./\u0001\u0000\u0000\u0000/0\u0005\u0018\u0000\u000001\u0005\u0003"+
		"\u0000\u000015\u0005\'\u0000\u000024\u0003\u0004\u0002\u000032\u0001\u0000"+
		"\u0000\u000047\u0001\u0000\u0000\u000053\u0001\u0000\u0000\u000056\u0001"+
		"\u0000\u0000\u00006;\u0001\u0000\u0000\u000075\u0001\u0000\u0000\u0000"+
		"8:\u0003\u0006\u0003\u000098\u0001\u0000\u0000\u0000:=\u0001\u0000\u0000"+
		"\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<>\u0001\u0000"+
		"\u0000\u0000=;\u0001\u0000\u0000\u0000>@\u0003\b\u0004\u0000?A\u0003\n"+
		"\u0005\u0000@?\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000AB\u0001"+
		"\u0000\u0000\u0000BC\u0005(\u0000\u0000C\u0003\u0001\u0000\u0000\u0000"+
		"DE\u0005\r\u0000\u0000EF\u0005\u001f\u0000\u0000FG\u0005\u0010\u0000\u0000"+
		"GM\u0005\u0003\u0000\u0000HI\u0005\r\u0000\u0000IJ\u0005\u0013\u0000\u0000"+
		"JK\u0005&\u0000\u0000KM\u0003\u000e\u0007\u0000LD\u0001\u0000\u0000\u0000"+
		"LH\u0001\u0000\u0000\u0000M\u0005\u0001\u0000\u0000\u0000NO\u0005\u000e"+
		"\u0000\u0000OP\u0005\"\u0000\u0000PQ\u0005\u0003\u0000\u0000QR\u0005&"+
		"\u0000\u0000RS\u0003\u000e\u0007\u0000S\u0007\u0001\u0000\u0000\u0000"+
		"TU\u0005\b\u0000\u0000UV\u0003\f\u0006\u0000VZ\u0005&\u0000\u0000WX\u0005"+
		"\u0007\u0000\u0000XY\u0005\u0003\u0000\u0000Y[\u0005&\u0000\u0000ZW\u0001"+
		"\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000"+
		"\\]\u0003\u000e\u0007\u0000]\t\u0001\u0000\u0000\u0000^_\u0005 \u0000"+
		"\u0000_`\u0005\u0003\u0000\u0000`\u000b\u0001\u0000\u0000\u0000ad\u0005"+
		"\u0019\u0000\u0000bd\u0005\f\u0000\u0000ca\u0001\u0000\u0000\u0000cb\u0001"+
		"\u0000\u0000\u0000d\r\u0001\u0000\u0000\u0000ef\u0003\u0010\b\u0000f\u000f"+
		"\u0001\u0000\u0000\u0000gh\u0006\b\uffff\uffff\u0000hi\u0005)\u0000\u0000"+
		"ij\u0003\u0010\b\u0000jk\u0005*\u0000\u0000kp\u0001\u0000\u0000\u0000"+
		"lm\u0005\u0016\u0000\u0000mp\u0003\u0010\b\u0004np\u0003\u0014\n\u0000"+
		"og\u0001\u0000\u0000\u0000ol\u0001\u0000\u0000\u0000on\u0001\u0000\u0000"+
		"\u0000pz\u0001\u0000\u0000\u0000qr\n\u0003\u0000\u0000rs\u0003\u0012\t"+
		"\u0000st\u0003\u0010\b\u0004ty\u0001\u0000\u0000\u0000uv\n\u0002\u0000"+
		"\u0000vw\u0005\u0011\u0000\u0000wy\u0005$\u0000\u0000xq\u0001\u0000\u0000"+
		"\u0000xu\u0001\u0000\u0000\u0000y|\u0001\u0000\u0000\u0000zx\u0001\u0000"+
		"\u0000\u0000z{\u0001\u0000\u0000\u0000{\u0011\u0001\u0000\u0000\u0000"+
		"|z\u0001\u0000\u0000\u0000}\u0082\u0007\u0000\u0000\u0000~\u0082\u0007"+
		"\u0001\u0000\u0000\u007f\u0082\u0007\u0002\u0000\u0000\u0080\u0082\u0005"+
		"\u000b\u0000\u0000\u0081}\u0001\u0000\u0000\u0000\u0081~\u0001\u0000\u0000"+
		"\u0000\u0081\u007f\u0001\u0000\u0000\u0000\u0081\u0080\u0001\u0000\u0000"+
		"\u0000\u0082\u0013\u0001\u0000\u0000\u0000\u0083\u0084\u0005\u001a\u0000"+
		"\u0000\u0084\u0085\u0005)\u0000\u0000\u0085\u0086\u0005\u0012\u0000\u0000"+
		"\u0086\u0087\u0005%\u0000\u0000\u0087\u0088\u0005!\u0000\u0000\u0088\u0089"+
		"\u0005*\u0000\u0000\u0089\u008a\u0003\u001c\u000e\u0000\u008a\u008b\u0005"+
		")\u0000\u0000\u008b\u008c\u0005\u0003\u0000\u0000\u008c\u008d\u0005%\u0000"+
		"\u0000\u008d\u008e\u0005\u0003\u0000\u0000\u008e\u008f\u0005*\u0000\u0000"+
		"\u008f\u00a5\u0001\u0000\u0000\u0000\u0090\u0091\u0005\u001a\u0000\u0000"+
		"\u0091\u0092\u0005!\u0000\u0000\u0092\u0093\u0005\u0011\u0000\u0000\u0093"+
		"\u00a5\u0005$\u0000\u0000\u0094\u0095\u0005\u001a\u0000\u0000\u0095\u0096"+
		"\u0003\u001a\r\u0000\u0096\u0097\u0005\u0011\u0000\u0000\u0097\u0098\u0005"+
		"\u0010\u0000\u0000\u0098\u0099\u0003\u0016\u000b\u0000\u0099\u00a5\u0001"+
		"\u0000\u0000\u0000\u009a\u009b\u0005\u001a\u0000\u0000\u009b\u009c\u0005"+
		"!\u0000\u0000\u009c\u009d\u0005\u0011\u0000\u0000\u009d\u009e\u0005\u0010"+
		"\u0000\u0000\u009e\u00a5\u0003\u0018\f\u0000\u009f\u00a0\u0005\u001a\u0000"+
		"\u0000\u00a0\u00a1\u0003\u001a\r\u0000\u00a1\u00a2\u0003\u001c\u000e\u0000"+
		"\u00a2\u00a3\u0005\u0003\u0000\u0000\u00a3\u00a5\u0001\u0000\u0000\u0000"+
		"\u00a4\u0083\u0001\u0000\u0000\u0000\u00a4\u0090\u0001\u0000\u0000\u0000"+
		"\u00a4\u0094\u0001\u0000\u0000\u0000\u00a4\u009a\u0001\u0000\u0000\u0000"+
		"\u00a4\u009f\u0001\u0000\u0000\u0000\u00a5\u0015\u0001\u0000\u0000\u0000"+
		"\u00a6\u00a7\u0005\u0014\u0000\u0000\u00a7\u00a8\u0005)\u0000\u0000\u00a8"+
		"\u00ad\u0005\u0003\u0000\u0000\u00a9\u00aa\u0005%\u0000\u0000\u00aa\u00ac"+
		"\u0005\u0003\u0000\u0000\u00ab\u00a9\u0001\u0000\u0000\u0000\u00ac\u00af"+
		"\u0001\u0000\u0000\u0000\u00ad\u00ab\u0001\u0000\u0000\u0000\u00ad\u00ae"+
		"\u0001\u0000\u0000\u0000\u00ae\u00b0\u0001\u0000\u0000\u0000\u00af\u00ad"+
		"\u0001\u0000\u0000\u0000\u00b0\u00b1\u0005*\u0000\u0000\u00b1\u0017\u0001"+
		"\u0000\u0000\u0000\u00b2\u00b3\u0005\u001b\u0000\u0000\u00b3\u00b4\u0005"+
		")\u0000\u0000\u00b4\u00b5\u0005\u0004\u0000\u0000\u00b5\u00b6\u0005%\u0000"+
		"\u0000\u00b6\u00b7\u0005\u0004\u0000\u0000\u00b7\u00b8\u0005*\u0000\u0000"+
		"\u00b8\u0019\u0001\u0000\u0000\u0000\u00b9\u00bd\u0005\u0012\u0000\u0000"+
		"\u00ba\u00bd\u0005!\u0000\u0000\u00bb\u00bd\u0005\u0006\u0000\u0000\u00bc"+
		"\u00b9\u0001\u0000\u0000\u0000\u00bc\u00ba\u0001\u0000\u0000\u0000\u00bc"+
		"\u00bb\u0001\u0000\u0000\u0000\u00bd\u001b\u0001\u0000\u0000\u0000\u00be"+
		"\u00c4\u0005\u000b\u0000\u0000\u00bf\u00c4\u0005\u0015\u0000\u0000\u00c0"+
		"\u00c4\u0005\t\u0000\u0000\u00c1\u00c4\u0005\u001e\u0000\u0000\u00c2\u00c4"+
		"\u0005\n\u0000\u0000\u00c3\u00be\u0001\u0000\u0000\u0000\u00c3\u00bf\u0001"+
		"\u0000\u0000\u0000\u00c3\u00c0\u0001\u0000\u0000\u0000\u00c3\u00c1\u0001"+
		"\u0000\u0000\u0000\u00c3\u00c2\u0001\u0000\u0000\u0000\u00c4\u001d\u0001"+
		"\u0000\u0000\u0000\u00c5\u00c6\u0005\u001d\u0000\u0000\u00c6\u00c7\u0005"+
		"\u0003\u0000\u0000\u00c7\u001f\u0001\u0000\u0000\u0000\u00c8\u00c9\u0005"+
		"\u001c\u0000\u0000\u00c9\u00ca\u0005\u0003\u0000\u0000\u00ca!\u0001\u0000"+
		"\u0000\u0000\u00cb\u00cc\u0005\u0003\u0000\u0000\u00cc#\u0001\u0000\u0000"+
		"\u0000\u0011&(-5;@LZcoxz\u0081\u00a4\u00ad\u00bc\u00c3";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
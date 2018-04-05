// Generated from TgherkinParser.g4 by ANTLR 4.5

package nl.ou.testar.tgherkin.gen;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TgherkinParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		OPTION_KEYWORD_INCLUDE=1, OPTION_KEYWORD_EXCLUDE=2, TAGNAME=3, FEATURE_KEYWORD=4, 
		BACKGROUND_KEYWORD=5, SCENARIO_KEYWORD=6, SCENARIO_OUTLINE_KEYWORD=7, 
		EXAMPLES_KEYWORD=8, SELECTION_KEYWORD=9, ORACLE_KEYWORD=10, STEP_KEYWORD=11, 
		STEP_RANGE_KEYWORD=12, STEP_GIVEN_KEYWORD=13, STEP_WHEN_KEYWORD=14, STEP_THEN_KEYWORD=15, 
		STEP_ALSO_KEYWORD=16, STEP_EITHER_KEYWORD=17, TABLE_ROW=18, DECIMAL_NUMBER=19, 
		INTEGER_NUMBER=20, PLACEHOLDER=21, STRING=22, COMMENT=23, AND=24, OR=25, 
		NOT=26, TRUE=27, FALSE=28, POW=29, MULT=30, DIV=31, MOD=32, PLUS=33, MINUS=34, 
		GT=35, GE=36, LT=37, LE=38, EQ=39, NE=40, LPAREN=41, RPAREN=42, COMMA=43, 
		MATCHES_NAME=44, CLICK_NAME=45, TYPE_NAME=46, DRAG_NAME=47, ANY_NAME=48, 
		DOUBLE_CLICK_NAME=49, TRIPLE_CLICK_NAME=50, RIGHT_CLICK_NAME=51, MOUSE_MOVE_NAME=52, 
		DROP_DOWN_AT_NAME=53, BOOLEAN_VARIABLE=54, NUMBER_VARIABLE=55, STRING_VARIABLE=56, 
		EOL=57, WS=58, OTHER=59, BOOLEAN_VARIABLE_NAME=60, NUMBER_VARIABLE_NAME=61, 
		STRING_VARIABLE_NAME=62;
	public static final int
		RULE_document = 0, RULE_execOptions = 1, RULE_execOptionExclude = 2, RULE_execOptionInclude = 3, 
		RULE_feature = 4, RULE_background = 5, RULE_scenarioDefiniton = 6, RULE_scenario = 7, 
		RULE_scenarioOutline = 8, RULE_examples = 9, RULE_table = 10, RULE_title = 11, 
		RULE_narrativeLine = 12, RULE_tagname = 13, RULE_selection = 14, RULE_oracle = 15, 
		RULE_step = 16, RULE_givenClause = 17, RULE_whenClause = 18, RULE_thenClause = 19, 
		RULE_stepRange = 20, RULE_widget_tree_condition = 21, RULE_conditional_gesture = 22, 
		RULE_gesture = 23, RULE_typeGesture = 24, RULE_clickGesture = 25, RULE_doubleClickGesture = 26, 
		RULE_tripleClickGesture = 27, RULE_anyGesture = 28, RULE_gestureName = 29, 
		RULE_parameterlessGesture = 30, RULE_widget_condition = 31, RULE_relational_expr = 32, 
		RULE_relational_operator = 33, RULE_arithmetic_expr = 34, RULE_string_expr = 35, 
		RULE_booleanFunction = 36, RULE_matchesFunction = 37, RULE_bool = 38, 
		RULE_logical_entity = 39, RULE_numeric_entity = 40, RULE_string_entity = 41;
	public static final String[] ruleNames = {
		"document", "execOptions", "execOptionExclude", "execOptionInclude", "feature", 
		"background", "scenarioDefiniton", "scenario", "scenarioOutline", "examples", 
		"table", "title", "narrativeLine", "tagname", "selection", "oracle", "step", 
		"givenClause", "whenClause", "thenClause", "stepRange", "widget_tree_condition", 
		"conditional_gesture", "gesture", "typeGesture", "clickGesture", "doubleClickGesture", 
		"tripleClickGesture", "anyGesture", "gestureName", "parameterlessGesture", 
		"widget_condition", "relational_expr", "relational_operator", "arithmetic_expr", 
		"string_expr", "booleanFunction", "matchesFunction", "bool", "logical_entity", 
		"numeric_entity", "string_entity"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'include:'", "'exclude:'", null, "'Feature:'", "'Background:'", 
		"'Scenario:'", "'Scenario Outline:'", "'Examples:'", "'Selection:'", "'Oracle:'", 
		"'Step:'", "'Range'", "'Given'", "'When'", "'Then'", "'Also'", "'Either'", 
		null, null, null, null, null, null, "'and'", "'or'", null, "'true'", "'false'", 
		"'^'", "'*'", "'/'", "'%'", "'+'", "'-'", "'>'", "'>='", "'<'", "'<='", 
		"'='", null, "'('", "')'", "','", "'matches'", "'click'", "'type'", "'drag'", 
		"'anyGesture'", "'doubleClick'", "'tripleClick'", "'rightClick'", "'mouseMove'", 
		"'dropDownAt'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "OPTION_KEYWORD_INCLUDE", "OPTION_KEYWORD_EXCLUDE", "TAGNAME", "FEATURE_KEYWORD", 
		"BACKGROUND_KEYWORD", "SCENARIO_KEYWORD", "SCENARIO_OUTLINE_KEYWORD", 
		"EXAMPLES_KEYWORD", "SELECTION_KEYWORD", "ORACLE_KEYWORD", "STEP_KEYWORD", 
		"STEP_RANGE_KEYWORD", "STEP_GIVEN_KEYWORD", "STEP_WHEN_KEYWORD", "STEP_THEN_KEYWORD", 
		"STEP_ALSO_KEYWORD", "STEP_EITHER_KEYWORD", "TABLE_ROW", "DECIMAL_NUMBER", 
		"INTEGER_NUMBER", "PLACEHOLDER", "STRING", "COMMENT", "AND", "OR", "NOT", 
		"TRUE", "FALSE", "POW", "MULT", "DIV", "MOD", "PLUS", "MINUS", "GT", "GE", 
		"LT", "LE", "EQ", "NE", "LPAREN", "RPAREN", "COMMA", "MATCHES_NAME", "CLICK_NAME", 
		"TYPE_NAME", "DRAG_NAME", "ANY_NAME", "DOUBLE_CLICK_NAME", "TRIPLE_CLICK_NAME", 
		"RIGHT_CLICK_NAME", "MOUSE_MOVE_NAME", "DROP_DOWN_AT_NAME", "BOOLEAN_VARIABLE", 
		"NUMBER_VARIABLE", "STRING_VARIABLE", "EOL", "WS", "OTHER", "BOOLEAN_VARIABLE_NAME", 
		"NUMBER_VARIABLE_NAME", "STRING_VARIABLE_NAME"
	};
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
	public String getGrammarFileName() { return "TgherkinParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TgherkinParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class DocumentContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(TgherkinParser.EOF, 0); }
		public ExecOptionsContext execOptions() {
			return getRuleContext(ExecOptionsContext.class,0);
		}
		public List<FeatureContext> feature() {
			return getRuleContexts(FeatureContext.class);
		}
		public FeatureContext feature(int i) {
			return getRuleContext(FeatureContext.class,i);
		}
		public DocumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_document; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitDocument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DocumentContext document() throws RecognitionException {
		DocumentContext _localctx = new DocumentContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_document);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			_la = _input.LA(1);
			if (_la==OPTION_KEYWORD_INCLUDE || _la==OPTION_KEYWORD_EXCLUDE) {
				{
				setState(84);
				execOptions();
				}
			}

			setState(88); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(87);
				feature();
				}
				}
				setState(90); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TAGNAME || _la==FEATURE_KEYWORD );
			setState(92);
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

	public static class ExecOptionsContext extends ParserRuleContext {
		public ExecOptionExcludeContext execOptionExclude() {
			return getRuleContext(ExecOptionExcludeContext.class,0);
		}
		public ExecOptionIncludeContext execOptionInclude() {
			return getRuleContext(ExecOptionIncludeContext.class,0);
		}
		public ExecOptionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_execOptions; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitExecOptions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExecOptionsContext execOptions() throws RecognitionException {
		ExecOptionsContext _localctx = new ExecOptionsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_execOptions);
		int _la;
		try {
			setState(102);
			switch (_input.LA(1)) {
			case OPTION_KEYWORD_EXCLUDE:
				enterOuterAlt(_localctx, 1);
				{
				setState(94);
				execOptionExclude();
				setState(96);
				_la = _input.LA(1);
				if (_la==OPTION_KEYWORD_INCLUDE) {
					{
					setState(95);
					execOptionInclude();
					}
				}

				}
				break;
			case OPTION_KEYWORD_INCLUDE:
				enterOuterAlt(_localctx, 2);
				{
				setState(98);
				execOptionInclude();
				setState(100);
				_la = _input.LA(1);
				if (_la==OPTION_KEYWORD_EXCLUDE) {
					{
					setState(99);
					execOptionExclude();
					}
				}

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

	public static class ExecOptionExcludeContext extends ParserRuleContext {
		public Token TAGNAME;
		public List<Token> tags = new ArrayList<Token>();
		public TerminalNode OPTION_KEYWORD_EXCLUDE() { return getToken(TgherkinParser.OPTION_KEYWORD_EXCLUDE, 0); }
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public List<TerminalNode> TAGNAME() { return getTokens(TgherkinParser.TAGNAME); }
		public TerminalNode TAGNAME(int i) {
			return getToken(TgherkinParser.TAGNAME, i);
		}
		public ExecOptionExcludeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_execOptionExclude; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitExecOptionExclude(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExecOptionExcludeContext execOptionExclude() throws RecognitionException {
		ExecOptionExcludeContext _localctx = new ExecOptionExcludeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_execOptionExclude);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			match(OPTION_KEYWORD_EXCLUDE);
			setState(106); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(105);
					((ExecOptionExcludeContext)_localctx).TAGNAME = match(TAGNAME);
					((ExecOptionExcludeContext)_localctx).tags.add(((ExecOptionExcludeContext)_localctx).TAGNAME);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(108); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(110);
				match(EOL);
				}
				}
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class ExecOptionIncludeContext extends ParserRuleContext {
		public Token TAGNAME;
		public List<Token> tags = new ArrayList<Token>();
		public TerminalNode OPTION_KEYWORD_INCLUDE() { return getToken(TgherkinParser.OPTION_KEYWORD_INCLUDE, 0); }
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public List<TerminalNode> TAGNAME() { return getTokens(TgherkinParser.TAGNAME); }
		public TerminalNode TAGNAME(int i) {
			return getToken(TgherkinParser.TAGNAME, i);
		}
		public ExecOptionIncludeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_execOptionInclude; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitExecOptionInclude(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExecOptionIncludeContext execOptionInclude() throws RecognitionException {
		ExecOptionIncludeContext _localctx = new ExecOptionIncludeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_execOptionInclude);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			match(OPTION_KEYWORD_INCLUDE);
			setState(118); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(117);
					((ExecOptionIncludeContext)_localctx).TAGNAME = match(TAGNAME);
					((ExecOptionIncludeContext)_localctx).tags.add(((ExecOptionIncludeContext)_localctx).TAGNAME);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(120); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(122);
				match(EOL);
				}
				}
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class FeatureContext extends ParserRuleContext {
		public TagnameContext tagname;
		public List<TagnameContext> tags = new ArrayList<TagnameContext>();
		public NarrativeLineContext narrativeLine;
		public List<NarrativeLineContext> narrative = new ArrayList<NarrativeLineContext>();
		public TerminalNode FEATURE_KEYWORD() { return getToken(TgherkinParser.FEATURE_KEYWORD, 0); }
		public TitleContext title() {
			return getRuleContext(TitleContext.class,0);
		}
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public SelectionContext selection() {
			return getRuleContext(SelectionContext.class,0);
		}
		public OracleContext oracle() {
			return getRuleContext(OracleContext.class,0);
		}
		public BackgroundContext background() {
			return getRuleContext(BackgroundContext.class,0);
		}
		public List<ScenarioDefinitonContext> scenarioDefiniton() {
			return getRuleContexts(ScenarioDefinitonContext.class);
		}
		public ScenarioDefinitonContext scenarioDefiniton(int i) {
			return getRuleContext(ScenarioDefinitonContext.class,i);
		}
		public List<TagnameContext> tagname() {
			return getRuleContexts(TagnameContext.class);
		}
		public TagnameContext tagname(int i) {
			return getRuleContext(TagnameContext.class,i);
		}
		public List<NarrativeLineContext> narrativeLine() {
			return getRuleContexts(NarrativeLineContext.class);
		}
		public NarrativeLineContext narrativeLine(int i) {
			return getRuleContext(NarrativeLineContext.class,i);
		}
		public FeatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_feature; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitFeature(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FeatureContext feature() throws RecognitionException {
		FeatureContext _localctx = new FeatureContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_feature);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(128);
				((FeatureContext)_localctx).tagname = tagname();
				((FeatureContext)_localctx).tags.add(((FeatureContext)_localctx).tagname);
				}
				}
				setState(133);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(134);
			match(FEATURE_KEYWORD);
			setState(135);
			title();
			setState(137); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(136);
				match(EOL);
				}
				}
				setState(139); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(141);
				((FeatureContext)_localctx).narrativeLine = narrativeLine();
				((FeatureContext)_localctx).narrative.add(((FeatureContext)_localctx).narrativeLine);
				}
				}
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(148);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(147);
				selection();
				}
			}

			setState(151);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(150);
				oracle();
				}
			}

			setState(154);
			_la = _input.LA(1);
			if (_la==BACKGROUND_KEYWORD) {
				{
				setState(153);
				background();
				}
			}

			setState(157); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(156);
					scenarioDefiniton();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(159); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
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

	public static class BackgroundContext extends ParserRuleContext {
		public NarrativeLineContext narrativeLine;
		public List<NarrativeLineContext> narrative = new ArrayList<NarrativeLineContext>();
		public StepContext step;
		public List<StepContext> steps = new ArrayList<StepContext>();
		public TerminalNode BACKGROUND_KEYWORD() { return getToken(TgherkinParser.BACKGROUND_KEYWORD, 0); }
		public TitleContext title() {
			return getRuleContext(TitleContext.class,0);
		}
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public SelectionContext selection() {
			return getRuleContext(SelectionContext.class,0);
		}
		public OracleContext oracle() {
			return getRuleContext(OracleContext.class,0);
		}
		public List<NarrativeLineContext> narrativeLine() {
			return getRuleContexts(NarrativeLineContext.class);
		}
		public NarrativeLineContext narrativeLine(int i) {
			return getRuleContext(NarrativeLineContext.class,i);
		}
		public List<StepContext> step() {
			return getRuleContexts(StepContext.class);
		}
		public StepContext step(int i) {
			return getRuleContext(StepContext.class,i);
		}
		public BackgroundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_background; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitBackground(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BackgroundContext background() throws RecognitionException {
		BackgroundContext _localctx = new BackgroundContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_background);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			match(BACKGROUND_KEYWORD);
			setState(163);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				setState(162);
				title();
				}
			}

			setState(166); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(165);
				match(EOL);
				}
				}
				setState(168); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(170);
				((BackgroundContext)_localctx).narrativeLine = narrativeLine();
				((BackgroundContext)_localctx).narrative.add(((BackgroundContext)_localctx).narrativeLine);
				}
				}
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(177);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(176);
				selection();
				}
			}

			setState(180);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(179);
				oracle();
				}
			}

			setState(183); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(182);
				((BackgroundContext)_localctx).step = step();
				((BackgroundContext)_localctx).steps.add(((BackgroundContext)_localctx).step);
				}
				}
				setState(185); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STEP_KEYWORD );
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

	public static class ScenarioDefinitonContext extends ParserRuleContext {
		public ScenarioContext scenario() {
			return getRuleContext(ScenarioContext.class,0);
		}
		public ScenarioOutlineContext scenarioOutline() {
			return getRuleContext(ScenarioOutlineContext.class,0);
		}
		public ScenarioDefinitonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scenarioDefiniton; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitScenarioDefiniton(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScenarioDefinitonContext scenarioDefiniton() throws RecognitionException {
		ScenarioDefinitonContext _localctx = new ScenarioDefinitonContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_scenarioDefiniton);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(187);
				scenario();
				}
				break;
			case 2:
				{
				setState(188);
				scenarioOutline();
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

	public static class ScenarioContext extends ParserRuleContext {
		public TagnameContext tagname;
		public List<TagnameContext> tags = new ArrayList<TagnameContext>();
		public NarrativeLineContext narrativeLine;
		public List<NarrativeLineContext> narrative = new ArrayList<NarrativeLineContext>();
		public StepContext step;
		public List<StepContext> steps = new ArrayList<StepContext>();
		public TerminalNode SCENARIO_KEYWORD() { return getToken(TgherkinParser.SCENARIO_KEYWORD, 0); }
		public TitleContext title() {
			return getRuleContext(TitleContext.class,0);
		}
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public SelectionContext selection() {
			return getRuleContext(SelectionContext.class,0);
		}
		public OracleContext oracle() {
			return getRuleContext(OracleContext.class,0);
		}
		public List<TagnameContext> tagname() {
			return getRuleContexts(TagnameContext.class);
		}
		public TagnameContext tagname(int i) {
			return getRuleContext(TagnameContext.class,i);
		}
		public List<NarrativeLineContext> narrativeLine() {
			return getRuleContexts(NarrativeLineContext.class);
		}
		public NarrativeLineContext narrativeLine(int i) {
			return getRuleContext(NarrativeLineContext.class,i);
		}
		public List<StepContext> step() {
			return getRuleContexts(StepContext.class);
		}
		public StepContext step(int i) {
			return getRuleContext(StepContext.class,i);
		}
		public ScenarioContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scenario; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitScenario(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScenarioContext scenario() throws RecognitionException {
		ScenarioContext _localctx = new ScenarioContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_scenario);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(191);
				((ScenarioContext)_localctx).tagname = tagname();
				((ScenarioContext)_localctx).tags.add(((ScenarioContext)_localctx).tagname);
				}
				}
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(197);
			match(SCENARIO_KEYWORD);
			setState(198);
			title();
			setState(200); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(199);
				match(EOL);
				}
				}
				setState(202); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(204);
				((ScenarioContext)_localctx).narrativeLine = narrativeLine();
				((ScenarioContext)_localctx).narrative.add(((ScenarioContext)_localctx).narrativeLine);
				}
				}
				setState(209);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(211);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(210);
				selection();
				}
			}

			setState(214);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(213);
				oracle();
				}
			}

			setState(217); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(216);
				((ScenarioContext)_localctx).step = step();
				((ScenarioContext)_localctx).steps.add(((ScenarioContext)_localctx).step);
				}
				}
				setState(219); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STEP_KEYWORD );
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

	public static class ScenarioOutlineContext extends ParserRuleContext {
		public TagnameContext tagname;
		public List<TagnameContext> tags = new ArrayList<TagnameContext>();
		public NarrativeLineContext narrativeLine;
		public List<NarrativeLineContext> narrative = new ArrayList<NarrativeLineContext>();
		public StepContext step;
		public List<StepContext> steps = new ArrayList<StepContext>();
		public TerminalNode SCENARIO_OUTLINE_KEYWORD() { return getToken(TgherkinParser.SCENARIO_OUTLINE_KEYWORD, 0); }
		public TitleContext title() {
			return getRuleContext(TitleContext.class,0);
		}
		public ExamplesContext examples() {
			return getRuleContext(ExamplesContext.class,0);
		}
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public SelectionContext selection() {
			return getRuleContext(SelectionContext.class,0);
		}
		public OracleContext oracle() {
			return getRuleContext(OracleContext.class,0);
		}
		public List<TagnameContext> tagname() {
			return getRuleContexts(TagnameContext.class);
		}
		public TagnameContext tagname(int i) {
			return getRuleContext(TagnameContext.class,i);
		}
		public List<NarrativeLineContext> narrativeLine() {
			return getRuleContexts(NarrativeLineContext.class);
		}
		public NarrativeLineContext narrativeLine(int i) {
			return getRuleContext(NarrativeLineContext.class,i);
		}
		public List<StepContext> step() {
			return getRuleContexts(StepContext.class);
		}
		public StepContext step(int i) {
			return getRuleContext(StepContext.class,i);
		}
		public ScenarioOutlineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scenarioOutline; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitScenarioOutline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScenarioOutlineContext scenarioOutline() throws RecognitionException {
		ScenarioOutlineContext _localctx = new ScenarioOutlineContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_scenarioOutline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(221);
				((ScenarioOutlineContext)_localctx).tagname = tagname();
				((ScenarioOutlineContext)_localctx).tags.add(((ScenarioOutlineContext)_localctx).tagname);
				}
				}
				setState(226);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(227);
			match(SCENARIO_OUTLINE_KEYWORD);
			setState(228);
			title();
			setState(230); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(229);
				match(EOL);
				}
				}
				setState(232); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(237);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(234);
				((ScenarioOutlineContext)_localctx).narrativeLine = narrativeLine();
				((ScenarioOutlineContext)_localctx).narrative.add(((ScenarioOutlineContext)_localctx).narrativeLine);
				}
				}
				setState(239);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(241);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(240);
				selection();
				}
			}

			setState(244);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(243);
				oracle();
				}
			}

			setState(247); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(246);
				((ScenarioOutlineContext)_localctx).step = step();
				((ScenarioOutlineContext)_localctx).steps.add(((ScenarioOutlineContext)_localctx).step);
				}
				}
				setState(249); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STEP_KEYWORD );
			setState(251);
			examples();
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

	public static class ExamplesContext extends ParserRuleContext {
		public NarrativeLineContext narrativeLine;
		public List<NarrativeLineContext> narrative = new ArrayList<NarrativeLineContext>();
		public TerminalNode EXAMPLES_KEYWORD() { return getToken(TgherkinParser.EXAMPLES_KEYWORD, 0); }
		public TableContext table() {
			return getRuleContext(TableContext.class,0);
		}
		public TitleContext title() {
			return getRuleContext(TitleContext.class,0);
		}
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public List<NarrativeLineContext> narrativeLine() {
			return getRuleContexts(NarrativeLineContext.class);
		}
		public NarrativeLineContext narrativeLine(int i) {
			return getRuleContext(NarrativeLineContext.class,i);
		}
		public ExamplesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_examples; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitExamples(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExamplesContext examples() throws RecognitionException {
		ExamplesContext _localctx = new ExamplesContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_examples);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(253);
			match(EXAMPLES_KEYWORD);
			setState(255);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				setState(254);
				title();
				}
			}

			setState(258); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(257);
				match(EOL);
				}
				}
				setState(260); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(265);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(262);
				((ExamplesContext)_localctx).narrativeLine = narrativeLine();
				((ExamplesContext)_localctx).narrative.add(((ExamplesContext)_localctx).narrativeLine);
				}
				}
				setState(267);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(268);
			table();
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

	public static class TableContext extends ParserRuleContext {
		public Token TABLE_ROW;
		public List<Token> rows = new ArrayList<Token>();
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public List<TerminalNode> TABLE_ROW() { return getTokens(TgherkinParser.TABLE_ROW); }
		public TerminalNode TABLE_ROW(int i) {
			return getToken(TgherkinParser.TABLE_ROW, i);
		}
		public TableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitTable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableContext table() throws RecognitionException {
		TableContext _localctx = new TableContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_table);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(270);
				((TableContext)_localctx).TABLE_ROW = match(TABLE_ROW);
				((TableContext)_localctx).rows.add(((TableContext)_localctx).TABLE_ROW);
				}
				}
				setState(273); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TABLE_ROW );
			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(275);
				match(EOL);
				}
				}
				setState(280);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class TitleContext extends ParserRuleContext {
		public List<TerminalNode> WS() { return getTokens(TgherkinParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(TgherkinParser.WS, i);
		}
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public TitleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_title; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitTitle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TitleContext title() throws RecognitionException {
		TitleContext _localctx = new TitleContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_title);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(281);
					match(WS);
					}
					} 
				}
				setState(286);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			}
			setState(288); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(287);
				_la = _input.LA(1);
				if ( _la <= 0 || (_la==EOL) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				}
				setState(290); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0) );
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

	public static class NarrativeLineContext extends ParserRuleContext {
		public TerminalNode TAGNAME() { return getToken(TgherkinParser.TAGNAME, 0); }
		public TerminalNode FEATURE_KEYWORD() { return getToken(TgherkinParser.FEATURE_KEYWORD, 0); }
		public TerminalNode BACKGROUND_KEYWORD() { return getToken(TgherkinParser.BACKGROUND_KEYWORD, 0); }
		public TerminalNode SCENARIO_KEYWORD() { return getToken(TgherkinParser.SCENARIO_KEYWORD, 0); }
		public TerminalNode SCENARIO_OUTLINE_KEYWORD() { return getToken(TgherkinParser.SCENARIO_OUTLINE_KEYWORD, 0); }
		public TerminalNode EXAMPLES_KEYWORD() { return getToken(TgherkinParser.EXAMPLES_KEYWORD, 0); }
		public TerminalNode SELECTION_KEYWORD() { return getToken(TgherkinParser.SELECTION_KEYWORD, 0); }
		public TerminalNode ORACLE_KEYWORD() { return getToken(TgherkinParser.ORACLE_KEYWORD, 0); }
		public TerminalNode STEP_KEYWORD() { return getToken(TgherkinParser.STEP_KEYWORD, 0); }
		public TerminalNode STEP_RANGE_KEYWORD() { return getToken(TgherkinParser.STEP_RANGE_KEYWORD, 0); }
		public TerminalNode STEP_GIVEN_KEYWORD() { return getToken(TgherkinParser.STEP_GIVEN_KEYWORD, 0); }
		public TerminalNode STEP_WHEN_KEYWORD() { return getToken(TgherkinParser.STEP_WHEN_KEYWORD, 0); }
		public TerminalNode STEP_THEN_KEYWORD() { return getToken(TgherkinParser.STEP_THEN_KEYWORD, 0); }
		public TerminalNode STEP_ALSO_KEYWORD() { return getToken(TgherkinParser.STEP_ALSO_KEYWORD, 0); }
		public TerminalNode STEP_EITHER_KEYWORD() { return getToken(TgherkinParser.STEP_EITHER_KEYWORD, 0); }
		public TerminalNode TABLE_ROW() { return getToken(TgherkinParser.TABLE_ROW, 0); }
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public NarrativeLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_narrativeLine; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitNarrativeLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NarrativeLineContext narrativeLine() throws RecognitionException {
		NarrativeLineContext _localctx = new NarrativeLineContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_narrativeLine);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			_la = _input.LA(1);
			if ( _la <= 0 || ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << EOL))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(296);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,42,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(293);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==EOL) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					} 
				}
				setState(298);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,42,_ctx);
			}
			setState(302);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(299);
				match(EOL);
				}
				}
				setState(304);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class TagnameContext extends ParserRuleContext {
		public TerminalNode TAGNAME() { return getToken(TgherkinParser.TAGNAME, 0); }
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public TagnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tagname; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitTagname(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TagnameContext tagname() throws RecognitionException {
		TagnameContext _localctx = new TagnameContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_tagname);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			match(TAGNAME);
			setState(309);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(306);
				match(EOL);
				}
				}
				setState(311);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class SelectionContext extends ParserRuleContext {
		public Conditional_gestureContext conditional_gesture;
		public List<Conditional_gestureContext> conditional_gestures = new ArrayList<Conditional_gestureContext>();
		public TerminalNode SELECTION_KEYWORD() { return getToken(TgherkinParser.SELECTION_KEYWORD, 0); }
		public List<Conditional_gestureContext> conditional_gesture() {
			return getRuleContexts(Conditional_gestureContext.class);
		}
		public Conditional_gestureContext conditional_gesture(int i) {
			return getRuleContext(Conditional_gestureContext.class,i);
		}
		public SelectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selection; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitSelection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectionContext selection() throws RecognitionException {
		SelectionContext _localctx = new SelectionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_selection);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			match(SELECTION_KEYWORD);
			setState(314); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(313);
				((SelectionContext)_localctx).conditional_gesture = conditional_gesture();
				((SelectionContext)_localctx).conditional_gestures.add(((SelectionContext)_localctx).conditional_gesture);
				}
				}
				setState(316); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << MINUS) | (1L << LPAREN) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE))) != 0) );
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

	public static class OracleContext extends ParserRuleContext {
		public TerminalNode ORACLE_KEYWORD() { return getToken(TgherkinParser.ORACLE_KEYWORD, 0); }
		public Widget_tree_conditionContext widget_tree_condition() {
			return getRuleContext(Widget_tree_conditionContext.class,0);
		}
		public OracleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oracle; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitOracle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OracleContext oracle() throws RecognitionException {
		OracleContext _localctx = new OracleContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_oracle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(318);
			match(ORACLE_KEYWORD);
			setState(319);
			widget_tree_condition(0);
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

	public static class StepContext extends ParserRuleContext {
		public TerminalNode STEP_KEYWORD() { return getToken(TgherkinParser.STEP_KEYWORD, 0); }
		public TitleContext title() {
			return getRuleContext(TitleContext.class,0);
		}
		public WhenClauseContext whenClause() {
			return getRuleContext(WhenClauseContext.class,0);
		}
		public List<TerminalNode> EOL() { return getTokens(TgherkinParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(TgherkinParser.EOL, i);
		}
		public StepRangeContext stepRange() {
			return getRuleContext(StepRangeContext.class,0);
		}
		public GivenClauseContext givenClause() {
			return getRuleContext(GivenClauseContext.class,0);
		}
		public ThenClauseContext thenClause() {
			return getRuleContext(ThenClauseContext.class,0);
		}
		public StepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_step; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStep(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepContext step() throws RecognitionException {
		StepContext _localctx = new StepContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_step);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(321);
			match(STEP_KEYWORD);
			setState(322);
			title();
			setState(324); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(323);
				match(EOL);
				}
				}
				setState(326); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(329);
			_la = _input.LA(1);
			if (_la==STEP_RANGE_KEYWORD) {
				{
				setState(328);
				stepRange();
				}
			}

			setState(332);
			_la = _input.LA(1);
			if (_la==STEP_GIVEN_KEYWORD) {
				{
				setState(331);
				givenClause();
				}
			}

			setState(334);
			whenClause();
			setState(336);
			_la = _input.LA(1);
			if (_la==STEP_THEN_KEYWORD) {
				{
				setState(335);
				thenClause();
				}
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

	public static class GivenClauseContext extends ParserRuleContext {
		public TerminalNode STEP_GIVEN_KEYWORD() { return getToken(TgherkinParser.STEP_GIVEN_KEYWORD, 0); }
		public Widget_tree_conditionContext widget_tree_condition() {
			return getRuleContext(Widget_tree_conditionContext.class,0);
		}
		public GivenClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_givenClause; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitGivenClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GivenClauseContext givenClause() throws RecognitionException {
		GivenClauseContext _localctx = new GivenClauseContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_givenClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(338);
			match(STEP_GIVEN_KEYWORD);
			setState(339);
			widget_tree_condition(0);
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

	public static class WhenClauseContext extends ParserRuleContext {
		public Conditional_gestureContext conditional_gesture;
		public List<Conditional_gestureContext> conditional_gestures = new ArrayList<Conditional_gestureContext>();
		public TerminalNode STEP_WHEN_KEYWORD() { return getToken(TgherkinParser.STEP_WHEN_KEYWORD, 0); }
		public List<Conditional_gestureContext> conditional_gesture() {
			return getRuleContexts(Conditional_gestureContext.class);
		}
		public Conditional_gestureContext conditional_gesture(int i) {
			return getRuleContext(Conditional_gestureContext.class,i);
		}
		public WhenClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whenClause; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitWhenClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhenClauseContext whenClause() throws RecognitionException {
		WhenClauseContext _localctx = new WhenClauseContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_whenClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(341);
			match(STEP_WHEN_KEYWORD);
			setState(343); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(342);
				((WhenClauseContext)_localctx).conditional_gesture = conditional_gesture();
				((WhenClauseContext)_localctx).conditional_gestures.add(((WhenClauseContext)_localctx).conditional_gesture);
				}
				}
				setState(345); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << MINUS) | (1L << LPAREN) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE))) != 0) );
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

	public static class ThenClauseContext extends ParserRuleContext {
		public TerminalNode STEP_THEN_KEYWORD() { return getToken(TgherkinParser.STEP_THEN_KEYWORD, 0); }
		public Widget_tree_conditionContext widget_tree_condition() {
			return getRuleContext(Widget_tree_conditionContext.class,0);
		}
		public ThenClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thenClause; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitThenClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ThenClauseContext thenClause() throws RecognitionException {
		ThenClauseContext _localctx = new ThenClauseContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_thenClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(347);
			match(STEP_THEN_KEYWORD);
			setState(348);
			widget_tree_condition(0);
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

	public static class StepRangeContext extends ParserRuleContext {
		public Token from;
		public Token to;
		public TerminalNode STEP_RANGE_KEYWORD() { return getToken(TgherkinParser.STEP_RANGE_KEYWORD, 0); }
		public List<TerminalNode> INTEGER_NUMBER() { return getTokens(TgherkinParser.INTEGER_NUMBER); }
		public TerminalNode INTEGER_NUMBER(int i) {
			return getToken(TgherkinParser.INTEGER_NUMBER, i);
		}
		public StepRangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stepRange; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStepRange(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepRangeContext stepRange() throws RecognitionException {
		StepRangeContext _localctx = new StepRangeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_stepRange);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(350);
			match(STEP_RANGE_KEYWORD);
			setState(351);
			((StepRangeContext)_localctx).from = match(INTEGER_NUMBER);
			setState(352);
			((StepRangeContext)_localctx).to = match(INTEGER_NUMBER);
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

	public static class Widget_tree_conditionContext extends ParserRuleContext {
		public Widget_tree_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_widget_tree_condition; }
	 
		public Widget_tree_conditionContext() { }
		public void copyFrom(Widget_tree_conditionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class WidgetConditionContext extends Widget_tree_conditionContext {
		public Widget_conditionContext widget_condition() {
			return getRuleContext(Widget_conditionContext.class,0);
		}
		public WidgetConditionContext(Widget_tree_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitWidgetCondition(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetTreeConditionEitherContext extends Widget_tree_conditionContext {
		public Widget_tree_conditionContext left;
		public Widget_tree_conditionContext right;
		public TerminalNode STEP_EITHER_KEYWORD() { return getToken(TgherkinParser.STEP_EITHER_KEYWORD, 0); }
		public List<Widget_tree_conditionContext> widget_tree_condition() {
			return getRuleContexts(Widget_tree_conditionContext.class);
		}
		public Widget_tree_conditionContext widget_tree_condition(int i) {
			return getRuleContext(Widget_tree_conditionContext.class,i);
		}
		public WidgetTreeConditionEitherContext(Widget_tree_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitWidgetTreeConditionEither(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetTreeConditionAlsoContext extends Widget_tree_conditionContext {
		public Widget_tree_conditionContext left;
		public Widget_tree_conditionContext right;
		public TerminalNode STEP_ALSO_KEYWORD() { return getToken(TgherkinParser.STEP_ALSO_KEYWORD, 0); }
		public List<Widget_tree_conditionContext> widget_tree_condition() {
			return getRuleContexts(Widget_tree_conditionContext.class);
		}
		public Widget_tree_conditionContext widget_tree_condition(int i) {
			return getRuleContext(Widget_tree_conditionContext.class,i);
		}
		public WidgetTreeConditionAlsoContext(Widget_tree_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitWidgetTreeConditionAlso(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Widget_tree_conditionContext widget_tree_condition() throws RecognitionException {
		return widget_tree_condition(0);
	}

	private Widget_tree_conditionContext widget_tree_condition(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Widget_tree_conditionContext _localctx = new Widget_tree_conditionContext(_ctx, _parentState);
		Widget_tree_conditionContext _prevctx = _localctx;
		int _startState = 42;
		enterRecursionRule(_localctx, 42, RULE_widget_tree_condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new WidgetConditionContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(355);
			widget_condition(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(365);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(363);
					switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetTreeConditionAlsoContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionAlsoContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(357);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(358);
						match(STEP_ALSO_KEYWORD);
						setState(359);
						((WidgetTreeConditionAlsoContext)_localctx).right = widget_tree_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetTreeConditionEitherContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionEitherContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(360);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(361);
						match(STEP_EITHER_KEYWORD);
						setState(362);
						((WidgetTreeConditionEitherContext)_localctx).right = widget_tree_condition(2);
						}
						break;
					}
					} 
				}
				setState(367);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
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

	public static class Conditional_gestureContext extends ParserRuleContext {
		public GestureContext gesture() {
			return getRuleContext(GestureContext.class,0);
		}
		public Widget_conditionContext widget_condition() {
			return getRuleContext(Widget_conditionContext.class,0);
		}
		public Conditional_gestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditional_gesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitConditional_gesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Conditional_gestureContext conditional_gesture() throws RecognitionException {
		Conditional_gestureContext _localctx = new Conditional_gestureContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_conditional_gesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << MINUS) | (1L << LPAREN) | (1L << MATCHES_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE))) != 0)) {
				{
				setState(368);
				widget_condition(0);
				}
			}

			setState(371);
			gesture();
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

	public static class GestureContext extends ParserRuleContext {
		public ParameterlessGestureContext parameterlessGesture() {
			return getRuleContext(ParameterlessGestureContext.class,0);
		}
		public TypeGestureContext typeGesture() {
			return getRuleContext(TypeGestureContext.class,0);
		}
		public ClickGestureContext clickGesture() {
			return getRuleContext(ClickGestureContext.class,0);
		}
		public DoubleClickGestureContext doubleClickGesture() {
			return getRuleContext(DoubleClickGestureContext.class,0);
		}
		public TripleClickGestureContext tripleClickGesture() {
			return getRuleContext(TripleClickGestureContext.class,0);
		}
		public AnyGestureContext anyGesture() {
			return getRuleContext(AnyGestureContext.class,0);
		}
		public GestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitGesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GestureContext gesture() throws RecognitionException {
		GestureContext _localctx = new GestureContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_gesture);
		try {
			setState(379);
			switch (_input.LA(1)) {
			case DRAG_NAME:
			case RIGHT_CLICK_NAME:
			case MOUSE_MOVE_NAME:
			case DROP_DOWN_AT_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(373);
				parameterlessGesture();
				}
				break;
			case TYPE_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(374);
				typeGesture();
				}
				break;
			case CLICK_NAME:
				enterOuterAlt(_localctx, 3);
				{
				setState(375);
				clickGesture();
				}
				break;
			case DOUBLE_CLICK_NAME:
				enterOuterAlt(_localctx, 4);
				{
				setState(376);
				doubleClickGesture();
				}
				break;
			case TRIPLE_CLICK_NAME:
				enterOuterAlt(_localctx, 5);
				{
				setState(377);
				tripleClickGesture();
				}
				break;
			case ANY_NAME:
				enterOuterAlt(_localctx, 6);
				{
				setState(378);
				anyGesture();
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

	public static class TypeGestureContext extends ParserRuleContext {
		public TerminalNode TYPE_NAME() { return getToken(TgherkinParser.TYPE_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public TerminalNode STRING() { return getToken(TgherkinParser.STRING, 0); }
		public TerminalNode PLACEHOLDER() { return getToken(TgherkinParser.PLACEHOLDER, 0); }
		public TypeGestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeGesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitTypeGesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeGestureContext typeGesture() throws RecognitionException {
		TypeGestureContext _localctx = new TypeGestureContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_typeGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(381);
			match(TYPE_NAME);
			setState(382);
			match(LPAREN);
			setState(384);
			_la = _input.LA(1);
			if (_la==PLACEHOLDER || _la==STRING) {
				{
				setState(383);
				_la = _input.LA(1);
				if ( !(_la==PLACEHOLDER || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(386);
			match(RPAREN);
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

	public static class ClickGestureContext extends ParserRuleContext {
		public TerminalNode CLICK_NAME() { return getToken(TgherkinParser.CLICK_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public TerminalNode FALSE() { return getToken(TgherkinParser.FALSE, 0); }
		public TerminalNode TRUE() { return getToken(TgherkinParser.TRUE, 0); }
		public TerminalNode PLACEHOLDER() { return getToken(TgherkinParser.PLACEHOLDER, 0); }
		public ClickGestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clickGesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitClickGesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClickGestureContext clickGesture() throws RecognitionException {
		ClickGestureContext _localctx = new ClickGestureContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_clickGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			match(CLICK_NAME);
			setState(389);
			match(LPAREN);
			setState(391);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(390);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(393);
			match(RPAREN);
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

	public static class DoubleClickGestureContext extends ParserRuleContext {
		public TerminalNode DOUBLE_CLICK_NAME() { return getToken(TgherkinParser.DOUBLE_CLICK_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public TerminalNode FALSE() { return getToken(TgherkinParser.FALSE, 0); }
		public TerminalNode TRUE() { return getToken(TgherkinParser.TRUE, 0); }
		public TerminalNode PLACEHOLDER() { return getToken(TgherkinParser.PLACEHOLDER, 0); }
		public DoubleClickGestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_doubleClickGesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitDoubleClickGesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DoubleClickGestureContext doubleClickGesture() throws RecognitionException {
		DoubleClickGestureContext _localctx = new DoubleClickGestureContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_doubleClickGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(395);
			match(DOUBLE_CLICK_NAME);
			setState(396);
			match(LPAREN);
			setState(398);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(397);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(400);
			match(RPAREN);
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

	public static class TripleClickGestureContext extends ParserRuleContext {
		public TerminalNode TRIPLE_CLICK_NAME() { return getToken(TgherkinParser.TRIPLE_CLICK_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public TerminalNode FALSE() { return getToken(TgherkinParser.FALSE, 0); }
		public TerminalNode TRUE() { return getToken(TgherkinParser.TRUE, 0); }
		public TerminalNode PLACEHOLDER() { return getToken(TgherkinParser.PLACEHOLDER, 0); }
		public TripleClickGestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tripleClickGesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitTripleClickGesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TripleClickGestureContext tripleClickGesture() throws RecognitionException {
		TripleClickGestureContext _localctx = new TripleClickGestureContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_tripleClickGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(402);
			match(TRIPLE_CLICK_NAME);
			setState(403);
			match(LPAREN);
			setState(405);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(404);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(407);
			match(RPAREN);
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

	public static class AnyGestureContext extends ParserRuleContext {
		public TerminalNode ANY_NAME() { return getToken(TgherkinParser.ANY_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public TerminalNode FALSE() { return getToken(TgherkinParser.FALSE, 0); }
		public TerminalNode TRUE() { return getToken(TgherkinParser.TRUE, 0); }
		public TerminalNode PLACEHOLDER() { return getToken(TgherkinParser.PLACEHOLDER, 0); }
		public AnyGestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_anyGesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitAnyGesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnyGestureContext anyGesture() throws RecognitionException {
		AnyGestureContext _localctx = new AnyGestureContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_anyGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(409);
			match(ANY_NAME);
			setState(410);
			match(LPAREN);
			setState(412);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(411);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(414);
			match(RPAREN);
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

	public static class GestureNameContext extends ParserRuleContext {
		public TerminalNode DRAG_NAME() { return getToken(TgherkinParser.DRAG_NAME, 0); }
		public TerminalNode RIGHT_CLICK_NAME() { return getToken(TgherkinParser.RIGHT_CLICK_NAME, 0); }
		public TerminalNode MOUSE_MOVE_NAME() { return getToken(TgherkinParser.MOUSE_MOVE_NAME, 0); }
		public TerminalNode DROP_DOWN_AT_NAME() { return getToken(TgherkinParser.DROP_DOWN_AT_NAME, 0); }
		public GestureNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gestureName; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitGestureName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GestureNameContext gestureName() throws RecognitionException {
		GestureNameContext _localctx = new GestureNameContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_gestureName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(416);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DRAG_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class ParameterlessGestureContext extends ParserRuleContext {
		public GestureNameContext gestureName() {
			return getRuleContext(GestureNameContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public ParameterlessGestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterlessGesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitParameterlessGesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterlessGestureContext parameterlessGesture() throws RecognitionException {
		ParameterlessGestureContext _localctx = new ParameterlessGestureContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_parameterlessGesture);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			gestureName();
			setState(419);
			match(LPAREN);
			setState(420);
			match(RPAREN);
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

	public static class Widget_conditionContext extends ParserRuleContext {
		public Widget_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_widget_condition; }
	 
		public Widget_conditionContext() { }
		public void copyFrom(Widget_conditionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LogicalEntityContext extends Widget_conditionContext {
		public Logical_entityContext logical_entity() {
			return getRuleContext(Logical_entityContext.class,0);
		}
		public LogicalEntityContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitLogicalEntity(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelationalExpressionContext extends Widget_conditionContext {
		public Relational_exprContext relational_expr() {
			return getRuleContext(Relational_exprContext.class,0);
		}
		public RelationalExpressionContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitRelationalExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetConditionOrContext extends Widget_conditionContext {
		public Widget_conditionContext left;
		public Widget_conditionContext right;
		public TerminalNode OR() { return getToken(TgherkinParser.OR, 0); }
		public List<Widget_conditionContext> widget_condition() {
			return getRuleContexts(Widget_conditionContext.class);
		}
		public Widget_conditionContext widget_condition(int i) {
			return getRuleContext(Widget_conditionContext.class,i);
		}
		public WidgetConditionOrContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitWidgetConditionOr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NegationWidgetConditionContext extends Widget_conditionContext {
		public TerminalNode NOT() { return getToken(TgherkinParser.NOT, 0); }
		public Widget_conditionContext widget_condition() {
			return getRuleContext(Widget_conditionContext.class,0);
		}
		public NegationWidgetConditionContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitNegationWidgetCondition(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetConditionInParenContext extends Widget_conditionContext {
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public Widget_conditionContext widget_condition() {
			return getRuleContext(Widget_conditionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public WidgetConditionInParenContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitWidgetConditionInParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WidgetConditionAndContext extends Widget_conditionContext {
		public Widget_conditionContext left;
		public Widget_conditionContext right;
		public TerminalNode AND() { return getToken(TgherkinParser.AND, 0); }
		public List<Widget_conditionContext> widget_condition() {
			return getRuleContexts(Widget_conditionContext.class);
		}
		public Widget_conditionContext widget_condition(int i) {
			return getRuleContext(Widget_conditionContext.class,i);
		}
		public WidgetConditionAndContext(Widget_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitWidgetConditionAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Widget_conditionContext widget_condition() throws RecognitionException {
		return widget_condition(0);
	}

	private Widget_conditionContext widget_condition(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Widget_conditionContext _localctx = new Widget_conditionContext(_ctx, _parentState);
		Widget_conditionContext _prevctx = _localctx;
		int _startState = 62;
		enterRecursionRule(_localctx, 62, RULE_widget_condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				_localctx = new NegationWidgetConditionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(423);
				match(NOT);
				setState(424);
				widget_condition(4);
				}
				break;
			case 2:
				{
				_localctx = new LogicalEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(425);
				logical_entity();
				}
				break;
			case 3:
				{
				_localctx = new WidgetConditionInParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(426);
				match(LPAREN);
				setState(427);
				widget_condition(0);
				setState(428);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new RelationalExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(430);
				relational_expr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(441);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(439);
					switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetConditionAndContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(433);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(434);
						match(AND);
						setState(435);
						((WidgetConditionAndContext)_localctx).right = widget_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetConditionOrContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(436);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(437);
						match(OR);
						setState(438);
						((WidgetConditionOrContext)_localctx).right = widget_condition(2);
						}
						break;
					}
					} 
				}
				setState(443);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
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

	public static class Relational_exprContext extends ParserRuleContext {
		public Relational_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_expr; }
	 
		public Relational_exprContext() { }
		public void copyFrom(Relational_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RelationalNumericExpressionWithOperatorContext extends Relational_exprContext {
		public Arithmetic_exprContext left;
		public Arithmetic_exprContext right;
		public Relational_operatorContext relational_operator() {
			return getRuleContext(Relational_operatorContext.class,0);
		}
		public List<Arithmetic_exprContext> arithmetic_expr() {
			return getRuleContexts(Arithmetic_exprContext.class);
		}
		public Arithmetic_exprContext arithmetic_expr(int i) {
			return getRuleContext(Arithmetic_exprContext.class,i);
		}
		public RelationalNumericExpressionWithOperatorContext(Relational_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitRelationalNumericExpressionWithOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelationalStringExpressionWithOperatorContext extends Relational_exprContext {
		public String_exprContext left;
		public String_exprContext right;
		public Relational_operatorContext relational_operator() {
			return getRuleContext(Relational_operatorContext.class,0);
		}
		public List<String_exprContext> string_expr() {
			return getRuleContexts(String_exprContext.class);
		}
		public String_exprContext string_expr(int i) {
			return getRuleContext(String_exprContext.class,i);
		}
		public RelationalStringExpressionWithOperatorContext(Relational_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitRelationalStringExpressionWithOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelationalExpressionParensContext extends Relational_exprContext {
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public Relational_exprContext relational_expr() {
			return getRuleContext(Relational_exprContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public RelationalExpressionParensContext(Relational_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitRelationalExpressionParens(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Relational_exprContext relational_expr() throws RecognitionException {
		Relational_exprContext _localctx = new Relational_exprContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_relational_expr);
		try {
			setState(456);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				_localctx = new RelationalNumericExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(444);
				((RelationalNumericExpressionWithOperatorContext)_localctx).left = arithmetic_expr(0);
				setState(445);
				relational_operator();
				setState(446);
				((RelationalNumericExpressionWithOperatorContext)_localctx).right = arithmetic_expr(0);
				}
				break;
			case 2:
				_localctx = new RelationalStringExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(448);
				((RelationalStringExpressionWithOperatorContext)_localctx).left = string_expr();
				setState(449);
				relational_operator();
				setState(450);
				((RelationalStringExpressionWithOperatorContext)_localctx).right = string_expr();
				}
				break;
			case 3:
				_localctx = new RelationalExpressionParensContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(452);
				match(LPAREN);
				setState(453);
				relational_expr();
				setState(454);
				match(RPAREN);
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

	public static class Relational_operatorContext extends ParserRuleContext {
		public TerminalNode GT() { return getToken(TgherkinParser.GT, 0); }
		public TerminalNode GE() { return getToken(TgherkinParser.GE, 0); }
		public TerminalNode LT() { return getToken(TgherkinParser.LT, 0); }
		public TerminalNode LE() { return getToken(TgherkinParser.LE, 0); }
		public TerminalNode EQ() { return getToken(TgherkinParser.EQ, 0); }
		public TerminalNode NE() { return getToken(TgherkinParser.NE, 0); }
		public Relational_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_operator; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitRelational_operator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Relational_operatorContext relational_operator() throws RecognitionException {
		Relational_operatorContext _localctx = new Relational_operatorContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_relational_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(458);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Arithmetic_exprContext extends ParserRuleContext {
		public Arithmetic_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arithmetic_expr; }
	 
		public Arithmetic_exprContext() { }
		public void copyFrom(Arithmetic_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ArithmeticExpressionPowContext extends Arithmetic_exprContext {
		public Arithmetic_exprContext left;
		public Arithmetic_exprContext right;
		public TerminalNode POW() { return getToken(TgherkinParser.POW, 0); }
		public List<Arithmetic_exprContext> arithmetic_expr() {
			return getRuleContexts(Arithmetic_exprContext.class);
		}
		public Arithmetic_exprContext arithmetic_expr(int i) {
			return getRuleContext(Arithmetic_exprContext.class,i);
		}
		public ArithmeticExpressionPowContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitArithmeticExpressionPow(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionParensContext extends Arithmetic_exprContext {
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public Arithmetic_exprContext arithmetic_expr() {
			return getRuleContext(Arithmetic_exprContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public ArithmeticExpressionParensContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitArithmeticExpressionParens(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionNumericEntityContext extends Arithmetic_exprContext {
		public Numeric_entityContext numeric_entity() {
			return getRuleContext(Numeric_entityContext.class,0);
		}
		public ArithmeticExpressionNumericEntityContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitArithmeticExpressionNumericEntity(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionMultDivModContext extends Arithmetic_exprContext {
		public Arithmetic_exprContext left;
		public Arithmetic_exprContext right;
		public List<Arithmetic_exprContext> arithmetic_expr() {
			return getRuleContexts(Arithmetic_exprContext.class);
		}
		public Arithmetic_exprContext arithmetic_expr(int i) {
			return getRuleContext(Arithmetic_exprContext.class,i);
		}
		public TerminalNode MULT() { return getToken(TgherkinParser.MULT, 0); }
		public TerminalNode DIV() { return getToken(TgherkinParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(TgherkinParser.MOD, 0); }
		public ArithmeticExpressionMultDivModContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitArithmeticExpressionMultDivMod(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionNegationContext extends Arithmetic_exprContext {
		public TerminalNode MINUS() { return getToken(TgherkinParser.MINUS, 0); }
		public Arithmetic_exprContext arithmetic_expr() {
			return getRuleContext(Arithmetic_exprContext.class,0);
		}
		public ArithmeticExpressionNegationContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitArithmeticExpressionNegation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticExpressionPlusMinusContext extends Arithmetic_exprContext {
		public Arithmetic_exprContext left;
		public Arithmetic_exprContext right;
		public List<Arithmetic_exprContext> arithmetic_expr() {
			return getRuleContexts(Arithmetic_exprContext.class);
		}
		public Arithmetic_exprContext arithmetic_expr(int i) {
			return getRuleContext(Arithmetic_exprContext.class,i);
		}
		public TerminalNode PLUS() { return getToken(TgherkinParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(TgherkinParser.MINUS, 0); }
		public ArithmeticExpressionPlusMinusContext(Arithmetic_exprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitArithmeticExpressionPlusMinus(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Arithmetic_exprContext arithmetic_expr() throws RecognitionException {
		return arithmetic_expr(0);
	}

	private Arithmetic_exprContext arithmetic_expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Arithmetic_exprContext _localctx = new Arithmetic_exprContext(_ctx, _parentState);
		Arithmetic_exprContext _prevctx = _localctx;
		int _startState = 68;
		enterRecursionRule(_localctx, 68, RULE_arithmetic_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(468);
			switch (_input.LA(1)) {
			case MINUS:
				{
				_localctx = new ArithmeticExpressionNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(461);
				match(MINUS);
				setState(462);
				arithmetic_expr(4);
				}
				break;
			case DECIMAL_NUMBER:
			case INTEGER_NUMBER:
			case PLACEHOLDER:
			case NUMBER_VARIABLE:
				{
				_localctx = new ArithmeticExpressionNumericEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(463);
				numeric_entity();
				}
				break;
			case LPAREN:
				{
				_localctx = new ArithmeticExpressionParensContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(464);
				match(LPAREN);
				setState(465);
				arithmetic_expr(0);
				setState(466);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(481);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(479);
					switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticExpressionPowContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPowContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(470);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(471);
						match(POW);
						setState(472);
						((ArithmeticExpressionPowContext)_localctx).right = arithmetic_expr(4);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticExpressionMultDivModContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionMultDivModContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(473);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(474);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(475);
						((ArithmeticExpressionMultDivModContext)_localctx).right = arithmetic_expr(3);
						}
						break;
					case 3:
						{
						_localctx = new ArithmeticExpressionPlusMinusContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPlusMinusContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(476);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(477);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(478);
						((ArithmeticExpressionPlusMinusContext)_localctx).right = arithmetic_expr(2);
						}
						break;
					}
					} 
				}
				setState(483);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
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

	public static class String_exprContext extends ParserRuleContext {
		public String_entityContext string_entity() {
			return getRuleContext(String_entityContext.class,0);
		}
		public String_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_expr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitString_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final String_exprContext string_expr() throws RecognitionException {
		String_exprContext _localctx = new String_exprContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_string_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(484);
			string_entity();
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

	public static class BooleanFunctionContext extends ParserRuleContext {
		public MatchesFunctionContext matchesFunction() {
			return getRuleContext(MatchesFunctionContext.class,0);
		}
		public BooleanFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitBooleanFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanFunctionContext booleanFunction() throws RecognitionException {
		BooleanFunctionContext _localctx = new BooleanFunctionContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_booleanFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(486);
			matchesFunction();
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

	public static class MatchesFunctionContext extends ParserRuleContext {
		public TerminalNode MATCHES_NAME() { return getToken(TgherkinParser.MATCHES_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode STRING_VARIABLE() { return getToken(TgherkinParser.STRING_VARIABLE, 0); }
		public TerminalNode COMMA() { return getToken(TgherkinParser.COMMA, 0); }
		public TerminalNode STRING() { return getToken(TgherkinParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public MatchesFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchesFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitMatchesFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchesFunctionContext matchesFunction() throws RecognitionException {
		MatchesFunctionContext _localctx = new MatchesFunctionContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_matchesFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(488);
			match(MATCHES_NAME);
			setState(489);
			match(LPAREN);
			setState(490);
			match(STRING_VARIABLE);
			setState(491);
			match(COMMA);
			setState(492);
			match(STRING);
			setState(493);
			match(RPAREN);
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

	public static class BoolContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(TgherkinParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(TgherkinParser.FALSE, 0); }
		public BoolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bool; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitBool(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolContext bool() throws RecognitionException {
		BoolContext _localctx = new BoolContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_bool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(495);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Logical_entityContext extends ParserRuleContext {
		public Logical_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logical_entity; }
	 
		public Logical_entityContext() { }
		public void copyFrom(Logical_entityContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LogicalVariableContext extends Logical_entityContext {
		public TerminalNode BOOLEAN_VARIABLE() { return getToken(TgherkinParser.BOOLEAN_VARIABLE, 0); }
		public LogicalVariableContext(Logical_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitLogicalVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalFunctionContext extends Logical_entityContext {
		public BooleanFunctionContext booleanFunction() {
			return getRuleContext(BooleanFunctionContext.class,0);
		}
		public LogicalFunctionContext(Logical_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitLogicalFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalPlaceholderContext extends Logical_entityContext {
		public TerminalNode PLACEHOLDER() { return getToken(TgherkinParser.PLACEHOLDER, 0); }
		public LogicalPlaceholderContext(Logical_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitLogicalPlaceholder(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalConstContext extends Logical_entityContext {
		public BoolContext bool() {
			return getRuleContext(BoolContext.class,0);
		}
		public LogicalConstContext(Logical_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitLogicalConst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logical_entityContext logical_entity() throws RecognitionException {
		Logical_entityContext _localctx = new Logical_entityContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_logical_entity);
		try {
			setState(501);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
				_localctx = new LogicalConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(497);
				bool();
				}
				break;
			case BOOLEAN_VARIABLE:
				_localctx = new LogicalVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(498);
				match(BOOLEAN_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new LogicalPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(499);
				match(PLACEHOLDER);
				}
				break;
			case MATCHES_NAME:
				_localctx = new LogicalFunctionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(500);
				booleanFunction();
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

	public static class Numeric_entityContext extends ParserRuleContext {
		public Numeric_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numeric_entity; }
	 
		public Numeric_entityContext() { }
		public void copyFrom(Numeric_entityContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IntegerConstContext extends Numeric_entityContext {
		public TerminalNode INTEGER_NUMBER() { return getToken(TgherkinParser.INTEGER_NUMBER, 0); }
		public IntegerConstContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitIntegerConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DecimalConstContext extends Numeric_entityContext {
		public TerminalNode DECIMAL_NUMBER() { return getToken(TgherkinParser.DECIMAL_NUMBER, 0); }
		public DecimalConstContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitDecimalConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericVariableContext extends Numeric_entityContext {
		public TerminalNode NUMBER_VARIABLE() { return getToken(TgherkinParser.NUMBER_VARIABLE, 0); }
		public NumericVariableContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitNumericVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericPlaceholderContext extends Numeric_entityContext {
		public TerminalNode PLACEHOLDER() { return getToken(TgherkinParser.PLACEHOLDER, 0); }
		public NumericPlaceholderContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitNumericPlaceholder(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Numeric_entityContext numeric_entity() throws RecognitionException {
		Numeric_entityContext _localctx = new Numeric_entityContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_numeric_entity);
		try {
			setState(507);
			switch (_input.LA(1)) {
			case INTEGER_NUMBER:
				_localctx = new IntegerConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(503);
				match(INTEGER_NUMBER);
				}
				break;
			case DECIMAL_NUMBER:
				_localctx = new DecimalConstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(504);
				match(DECIMAL_NUMBER);
				}
				break;
			case NUMBER_VARIABLE:
				_localctx = new NumericVariableContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(505);
				match(NUMBER_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new NumericPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(506);
				match(PLACEHOLDER);
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

	public static class String_entityContext extends ParserRuleContext {
		public String_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_entity; }
	 
		public String_entityContext() { }
		public void copyFrom(String_entityContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StringPlaceholderContext extends String_entityContext {
		public TerminalNode PLACEHOLDER() { return getToken(TgherkinParser.PLACEHOLDER, 0); }
		public StringPlaceholderContext(String_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStringPlaceholder(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringConstContext extends String_entityContext {
		public TerminalNode STRING() { return getToken(TgherkinParser.STRING, 0); }
		public StringConstContext(String_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStringConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringVariableContext extends String_entityContext {
		public TerminalNode STRING_VARIABLE() { return getToken(TgherkinParser.STRING_VARIABLE, 0); }
		public StringVariableContext(String_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStringVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final String_entityContext string_entity() throws RecognitionException {
		String_entityContext _localctx = new String_entityContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_string_entity);
		try {
			setState(512);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(509);
				match(STRING);
				}
				break;
			case STRING_VARIABLE:
				_localctx = new StringVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(510);
				match(STRING_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new StringPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(511);
				match(PLACEHOLDER);
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
		case 21:
			return widget_tree_condition_sempred((Widget_tree_conditionContext)_localctx, predIndex);
		case 31:
			return widget_condition_sempred((Widget_conditionContext)_localctx, predIndex);
		case 34:
			return arithmetic_expr_sempred((Arithmetic_exprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean widget_tree_condition_sempred(Widget_tree_conditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean widget_condition_sempred(Widget_conditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 2);
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean arithmetic_expr_sempred(Arithmetic_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 3);
		case 5:
			return precpred(_ctx, 2);
		case 6:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3@\u0205\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\3"+
		"\2\5\2X\n\2\3\2\6\2[\n\2\r\2\16\2\\\3\2\3\2\3\3\3\3\5\3c\n\3\3\3\3\3\5"+
		"\3g\n\3\5\3i\n\3\3\4\3\4\6\4m\n\4\r\4\16\4n\3\4\7\4r\n\4\f\4\16\4u\13"+
		"\4\3\5\3\5\6\5y\n\5\r\5\16\5z\3\5\7\5~\n\5\f\5\16\5\u0081\13\5\3\6\7\6"+
		"\u0084\n\6\f\6\16\6\u0087\13\6\3\6\3\6\3\6\6\6\u008c\n\6\r\6\16\6\u008d"+
		"\3\6\7\6\u0091\n\6\f\6\16\6\u0094\13\6\3\6\5\6\u0097\n\6\3\6\5\6\u009a"+
		"\n\6\3\6\5\6\u009d\n\6\3\6\6\6\u00a0\n\6\r\6\16\6\u00a1\3\7\3\7\5\7\u00a6"+
		"\n\7\3\7\6\7\u00a9\n\7\r\7\16\7\u00aa\3\7\7\7\u00ae\n\7\f\7\16\7\u00b1"+
		"\13\7\3\7\5\7\u00b4\n\7\3\7\5\7\u00b7\n\7\3\7\6\7\u00ba\n\7\r\7\16\7\u00bb"+
		"\3\b\3\b\5\b\u00c0\n\b\3\t\7\t\u00c3\n\t\f\t\16\t\u00c6\13\t\3\t\3\t\3"+
		"\t\6\t\u00cb\n\t\r\t\16\t\u00cc\3\t\7\t\u00d0\n\t\f\t\16\t\u00d3\13\t"+
		"\3\t\5\t\u00d6\n\t\3\t\5\t\u00d9\n\t\3\t\6\t\u00dc\n\t\r\t\16\t\u00dd"+
		"\3\n\7\n\u00e1\n\n\f\n\16\n\u00e4\13\n\3\n\3\n\3\n\6\n\u00e9\n\n\r\n\16"+
		"\n\u00ea\3\n\7\n\u00ee\n\n\f\n\16\n\u00f1\13\n\3\n\5\n\u00f4\n\n\3\n\5"+
		"\n\u00f7\n\n\3\n\6\n\u00fa\n\n\r\n\16\n\u00fb\3\n\3\n\3\13\3\13\5\13\u0102"+
		"\n\13\3\13\6\13\u0105\n\13\r\13\16\13\u0106\3\13\7\13\u010a\n\13\f\13"+
		"\16\13\u010d\13\13\3\13\3\13\3\f\6\f\u0112\n\f\r\f\16\f\u0113\3\f\7\f"+
		"\u0117\n\f\f\f\16\f\u011a\13\f\3\r\7\r\u011d\n\r\f\r\16\r\u0120\13\r\3"+
		"\r\6\r\u0123\n\r\r\r\16\r\u0124\3\16\3\16\7\16\u0129\n\16\f\16\16\16\u012c"+
		"\13\16\3\16\7\16\u012f\n\16\f\16\16\16\u0132\13\16\3\17\3\17\7\17\u0136"+
		"\n\17\f\17\16\17\u0139\13\17\3\20\3\20\6\20\u013d\n\20\r\20\16\20\u013e"+
		"\3\21\3\21\3\21\3\22\3\22\3\22\6\22\u0147\n\22\r\22\16\22\u0148\3\22\5"+
		"\22\u014c\n\22\3\22\5\22\u014f\n\22\3\22\3\22\5\22\u0153\n\22\3\23\3\23"+
		"\3\23\3\24\3\24\6\24\u015a\n\24\r\24\16\24\u015b\3\25\3\25\3\25\3\26\3"+
		"\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\7\27\u016e"+
		"\n\27\f\27\16\27\u0171\13\27\3\30\5\30\u0174\n\30\3\30\3\30\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\5\31\u017e\n\31\3\32\3\32\3\32\5\32\u0183\n\32\3"+
		"\32\3\32\3\33\3\33\3\33\5\33\u018a\n\33\3\33\3\33\3\34\3\34\3\34\5\34"+
		"\u0191\n\34\3\34\3\34\3\35\3\35\3\35\5\35\u0198\n\35\3\35\3\35\3\36\3"+
		"\36\3\36\5\36\u019f\n\36\3\36\3\36\3\37\3\37\3 \3 \3 \3 \3!\3!\3!\3!\3"+
		"!\3!\3!\3!\3!\5!\u01b2\n!\3!\3!\3!\3!\3!\3!\7!\u01ba\n!\f!\16!\u01bd\13"+
		"!\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\5\"\u01cb\n\"\3#\3#"+
		"\3$\3$\3$\3$\3$\3$\3$\3$\5$\u01d7\n$\3$\3$\3$\3$\3$\3$\3$\3$\3$\7$\u01e2"+
		"\n$\f$\16$\u01e5\13$\3%\3%\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3(\3(\3)"+
		"\3)\3)\3)\5)\u01f8\n)\3*\3*\3*\3*\5*\u01fe\n*\3+\3+\3+\5+\u0203\n+\3+"+
		"\2\5,@F,\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668"+
		":<>@BDFHJLNPRT\2\13\3\2;;\4\2\5\24;;\3\2\27\30\4\2\27\27\35\36\4\2\61"+
		"\61\65\67\3\2%*\3\2 \"\3\2#$\3\2\35\36\u022e\2W\3\2\2\2\4h\3\2\2\2\6j"+
		"\3\2\2\2\bv\3\2\2\2\n\u0085\3\2\2\2\f\u00a3\3\2\2\2\16\u00bf\3\2\2\2\20"+
		"\u00c4\3\2\2\2\22\u00e2\3\2\2\2\24\u00ff\3\2\2\2\26\u0111\3\2\2\2\30\u011e"+
		"\3\2\2\2\32\u0126\3\2\2\2\34\u0133\3\2\2\2\36\u013a\3\2\2\2 \u0140\3\2"+
		"\2\2\"\u0143\3\2\2\2$\u0154\3\2\2\2&\u0157\3\2\2\2(\u015d\3\2\2\2*\u0160"+
		"\3\2\2\2,\u0164\3\2\2\2.\u0173\3\2\2\2\60\u017d\3\2\2\2\62\u017f\3\2\2"+
		"\2\64\u0186\3\2\2\2\66\u018d\3\2\2\28\u0194\3\2\2\2:\u019b\3\2\2\2<\u01a2"+
		"\3\2\2\2>\u01a4\3\2\2\2@\u01b1\3\2\2\2B\u01ca\3\2\2\2D\u01cc\3\2\2\2F"+
		"\u01d6\3\2\2\2H\u01e6\3\2\2\2J\u01e8\3\2\2\2L\u01ea\3\2\2\2N\u01f1\3\2"+
		"\2\2P\u01f7\3\2\2\2R\u01fd\3\2\2\2T\u0202\3\2\2\2VX\5\4\3\2WV\3\2\2\2"+
		"WX\3\2\2\2XZ\3\2\2\2Y[\5\n\6\2ZY\3\2\2\2[\\\3\2\2\2\\Z\3\2\2\2\\]\3\2"+
		"\2\2]^\3\2\2\2^_\7\2\2\3_\3\3\2\2\2`b\5\6\4\2ac\5\b\5\2ba\3\2\2\2bc\3"+
		"\2\2\2ci\3\2\2\2df\5\b\5\2eg\5\6\4\2fe\3\2\2\2fg\3\2\2\2gi\3\2\2\2h`\3"+
		"\2\2\2hd\3\2\2\2i\5\3\2\2\2jl\7\4\2\2km\7\5\2\2lk\3\2\2\2mn\3\2\2\2nl"+
		"\3\2\2\2no\3\2\2\2os\3\2\2\2pr\7;\2\2qp\3\2\2\2ru\3\2\2\2sq\3\2\2\2st"+
		"\3\2\2\2t\7\3\2\2\2us\3\2\2\2vx\7\3\2\2wy\7\5\2\2xw\3\2\2\2yz\3\2\2\2"+
		"zx\3\2\2\2z{\3\2\2\2{\177\3\2\2\2|~\7;\2\2}|\3\2\2\2~\u0081\3\2\2\2\177"+
		"}\3\2\2\2\177\u0080\3\2\2\2\u0080\t\3\2\2\2\u0081\177\3\2\2\2\u0082\u0084"+
		"\5\34\17\2\u0083\u0082\3\2\2\2\u0084\u0087\3\2\2\2\u0085\u0083\3\2\2\2"+
		"\u0085\u0086\3\2\2\2\u0086\u0088\3\2\2\2\u0087\u0085\3\2\2\2\u0088\u0089"+
		"\7\6\2\2\u0089\u008b\5\30\r\2\u008a\u008c\7;\2\2\u008b\u008a\3\2\2\2\u008c"+
		"\u008d\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u0092\3\2"+
		"\2\2\u008f\u0091\5\32\16\2\u0090\u008f\3\2\2\2\u0091\u0094\3\2\2\2\u0092"+
		"\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0096\3\2\2\2\u0094\u0092\3\2"+
		"\2\2\u0095\u0097\5\36\20\2\u0096\u0095\3\2\2\2\u0096\u0097\3\2\2\2\u0097"+
		"\u0099\3\2\2\2\u0098\u009a\5 \21\2\u0099\u0098\3\2\2\2\u0099\u009a\3\2"+
		"\2\2\u009a\u009c\3\2\2\2\u009b\u009d\5\f\7\2\u009c\u009b\3\2\2\2\u009c"+
		"\u009d\3\2\2\2\u009d\u009f\3\2\2\2\u009e\u00a0\5\16\b\2\u009f\u009e\3"+
		"\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2"+
		"\13\3\2\2\2\u00a3\u00a5\7\7\2\2\u00a4\u00a6\5\30\r\2\u00a5\u00a4\3\2\2"+
		"\2\u00a5\u00a6\3\2\2\2\u00a6\u00a8\3\2\2\2\u00a7\u00a9\7;\2\2\u00a8\u00a7"+
		"\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab"+
		"\u00af\3\2\2\2\u00ac\u00ae\5\32\16\2\u00ad\u00ac\3\2\2\2\u00ae\u00b1\3"+
		"\2\2\2\u00af\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b3\3\2\2\2\u00b1"+
		"\u00af\3\2\2\2\u00b2\u00b4\5\36\20\2\u00b3\u00b2\3\2\2\2\u00b3\u00b4\3"+
		"\2\2\2\u00b4\u00b6\3\2\2\2\u00b5\u00b7\5 \21\2\u00b6\u00b5\3\2\2\2\u00b6"+
		"\u00b7\3\2\2\2\u00b7\u00b9\3\2\2\2\u00b8\u00ba\5\"\22\2\u00b9\u00b8\3"+
		"\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc"+
		"\r\3\2\2\2\u00bd\u00c0\5\20\t\2\u00be\u00c0\5\22\n\2\u00bf\u00bd\3\2\2"+
		"\2\u00bf\u00be\3\2\2\2\u00c0\17\3\2\2\2\u00c1\u00c3\5\34\17\2\u00c2\u00c1"+
		"\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5"+
		"\u00c7\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7\u00c8\7\b\2\2\u00c8\u00ca\5\30"+
		"\r\2\u00c9\u00cb\7;\2\2\u00ca\u00c9\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc"+
		"\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00d1\3\2\2\2\u00ce\u00d0\5\32"+
		"\16\2\u00cf\u00ce\3\2\2\2\u00d0\u00d3\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1"+
		"\u00d2\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d4\u00d6\5\36"+
		"\20\2\u00d5\u00d4\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00d8\3\2\2\2\u00d7"+
		"\u00d9\5 \21\2\u00d8\u00d7\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00db\3\2"+
		"\2\2\u00da\u00dc\5\"\22\2\u00db\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd"+
		"\u00db\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\21\3\2\2\2\u00df\u00e1\5\34\17"+
		"\2\u00e0\u00df\3\2\2\2\u00e1\u00e4\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e3"+
		"\3\2\2\2\u00e3\u00e5\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e5\u00e6\7\t\2\2\u00e6"+
		"\u00e8\5\30\r\2\u00e7\u00e9\7;\2\2\u00e8\u00e7\3\2\2\2\u00e9\u00ea\3\2"+
		"\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ef\3\2\2\2\u00ec"+
		"\u00ee\5\32\16\2\u00ed\u00ec\3\2\2\2\u00ee\u00f1\3\2\2\2\u00ef\u00ed\3"+
		"\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f2"+
		"\u00f4\5\36\20\2\u00f3\u00f2\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f6\3"+
		"\2\2\2\u00f5\u00f7\5 \21\2\u00f6\u00f5\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7"+
		"\u00f9\3\2\2\2\u00f8\u00fa\5\"\22\2\u00f9\u00f8\3\2\2\2\u00fa\u00fb\3"+
		"\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd"+
		"\u00fe\5\24\13\2\u00fe\23\3\2\2\2\u00ff\u0101\7\n\2\2\u0100\u0102\5\30"+
		"\r\2\u0101\u0100\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u0104\3\2\2\2\u0103"+
		"\u0105\7;\2\2\u0104\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0104\3\2"+
		"\2\2\u0106\u0107\3\2\2\2\u0107\u010b\3\2\2\2\u0108\u010a\5\32\16\2\u0109"+
		"\u0108\3\2\2\2\u010a\u010d\3\2\2\2\u010b\u0109\3\2\2\2\u010b\u010c\3\2"+
		"\2\2\u010c\u010e\3\2\2\2\u010d\u010b\3\2\2\2\u010e\u010f\5\26\f\2\u010f"+
		"\25\3\2\2\2\u0110\u0112\7\24\2\2\u0111\u0110\3\2\2\2\u0112\u0113\3\2\2"+
		"\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0118\3\2\2\2\u0115\u0117"+
		"\7;\2\2\u0116\u0115\3\2\2\2\u0117\u011a\3\2\2\2\u0118\u0116\3\2\2\2\u0118"+
		"\u0119\3\2\2\2\u0119\27\3\2\2\2\u011a\u0118\3\2\2\2\u011b\u011d\7<\2\2"+
		"\u011c\u011b\3\2\2\2\u011d\u0120\3\2\2\2\u011e\u011c\3\2\2\2\u011e\u011f"+
		"\3\2\2\2\u011f\u0122\3\2\2\2\u0120\u011e\3\2\2\2\u0121\u0123\n\2\2\2\u0122"+
		"\u0121\3\2\2\2\u0123\u0124\3\2\2\2\u0124\u0122\3\2\2\2\u0124\u0125\3\2"+
		"\2\2\u0125\31\3\2\2\2\u0126\u012a\n\3\2\2\u0127\u0129\n\2\2\2\u0128\u0127"+
		"\3\2\2\2\u0129\u012c\3\2\2\2\u012a\u0128\3\2\2\2\u012a\u012b\3\2\2\2\u012b"+
		"\u0130\3\2\2\2\u012c\u012a\3\2\2\2\u012d\u012f\7;\2\2\u012e\u012d\3\2"+
		"\2\2\u012f\u0132\3\2\2\2\u0130\u012e\3\2\2\2\u0130\u0131\3\2\2\2\u0131"+
		"\33\3\2\2\2\u0132\u0130\3\2\2\2\u0133\u0137\7\5\2\2\u0134\u0136\7;\2\2"+
		"\u0135\u0134\3\2\2\2\u0136\u0139\3\2\2\2\u0137\u0135\3\2\2\2\u0137\u0138"+
		"\3\2\2\2\u0138\35\3\2\2\2\u0139\u0137\3\2\2\2\u013a\u013c\7\13\2\2\u013b"+
		"\u013d\5.\30\2\u013c\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e\u013c\3\2"+
		"\2\2\u013e\u013f\3\2\2\2\u013f\37\3\2\2\2\u0140\u0141\7\f\2\2\u0141\u0142"+
		"\5,\27\2\u0142!\3\2\2\2\u0143\u0144\7\r\2\2\u0144\u0146\5\30\r\2\u0145"+
		"\u0147\7;\2\2\u0146\u0145\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u0146\3\2"+
		"\2\2\u0148\u0149\3\2\2\2\u0149\u014b\3\2\2\2\u014a\u014c\5*\26\2\u014b"+
		"\u014a\3\2\2\2\u014b\u014c\3\2\2\2\u014c\u014e\3\2\2\2\u014d\u014f\5$"+
		"\23\2\u014e\u014d\3\2\2\2\u014e\u014f\3\2\2\2\u014f\u0150\3\2\2\2\u0150"+
		"\u0152\5&\24\2\u0151\u0153\5(\25\2\u0152\u0151\3\2\2\2\u0152\u0153\3\2"+
		"\2\2\u0153#\3\2\2\2\u0154\u0155\7\17\2\2\u0155\u0156\5,\27\2\u0156%\3"+
		"\2\2\2\u0157\u0159\7\20\2\2\u0158\u015a\5.\30\2\u0159\u0158\3\2\2\2\u015a"+
		"\u015b\3\2\2\2\u015b\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c\'\3\2\2\2"+
		"\u015d\u015e\7\21\2\2\u015e\u015f\5,\27\2\u015f)\3\2\2\2\u0160\u0161\7"+
		"\16\2\2\u0161\u0162\7\26\2\2\u0162\u0163\7\26\2\2\u0163+\3\2\2\2\u0164"+
		"\u0165\b\27\1\2\u0165\u0166\5@!\2\u0166\u016f\3\2\2\2\u0167\u0168\f\4"+
		"\2\2\u0168\u0169\7\22\2\2\u0169\u016e\5,\27\5\u016a\u016b\f\3\2\2\u016b"+
		"\u016c\7\23\2\2\u016c\u016e\5,\27\4\u016d\u0167\3\2\2\2\u016d\u016a\3"+
		"\2\2\2\u016e\u0171\3\2\2\2\u016f\u016d\3\2\2\2\u016f\u0170\3\2\2\2\u0170"+
		"-\3\2\2\2\u0171\u016f\3\2\2\2\u0172\u0174\5@!\2\u0173\u0172\3\2\2\2\u0173"+
		"\u0174\3\2\2\2\u0174\u0175\3\2\2\2\u0175\u0176\5\60\31\2\u0176/\3\2\2"+
		"\2\u0177\u017e\5> \2\u0178\u017e\5\62\32\2\u0179\u017e\5\64\33\2\u017a"+
		"\u017e\5\66\34\2\u017b\u017e\58\35\2\u017c\u017e\5:\36\2\u017d\u0177\3"+
		"\2\2\2\u017d\u0178\3\2\2\2\u017d\u0179\3\2\2\2\u017d\u017a\3\2\2\2\u017d"+
		"\u017b\3\2\2\2\u017d\u017c\3\2\2\2\u017e\61\3\2\2\2\u017f\u0180\7\60\2"+
		"\2\u0180\u0182\7+\2\2\u0181\u0183\t\4\2\2\u0182\u0181\3\2\2\2\u0182\u0183"+
		"\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0185\7,\2\2\u0185\63\3\2\2\2\u0186"+
		"\u0187\7/\2\2\u0187\u0189\7+\2\2\u0188\u018a\t\5\2\2\u0189\u0188\3\2\2"+
		"\2\u0189\u018a\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u018c\7,\2\2\u018c\65"+
		"\3\2\2\2\u018d\u018e\7\63\2\2\u018e\u0190\7+\2\2\u018f\u0191\t\5\2\2\u0190"+
		"\u018f\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u0192\3\2\2\2\u0192\u0193\7,"+
		"\2\2\u0193\67\3\2\2\2\u0194\u0195\7\64\2\2\u0195\u0197\7+\2\2\u0196\u0198"+
		"\t\5\2\2\u0197\u0196\3\2\2\2\u0197\u0198\3\2\2\2\u0198\u0199\3\2\2\2\u0199"+
		"\u019a\7,\2\2\u019a9\3\2\2\2\u019b\u019c\7\62\2\2\u019c\u019e\7+\2\2\u019d"+
		"\u019f\t\5\2\2\u019e\u019d\3\2\2\2\u019e\u019f\3\2\2\2\u019f\u01a0\3\2"+
		"\2\2\u01a0\u01a1\7,\2\2\u01a1;\3\2\2\2\u01a2\u01a3\t\6\2\2\u01a3=\3\2"+
		"\2\2\u01a4\u01a5\5<\37\2\u01a5\u01a6\7+\2\2\u01a6\u01a7\7,\2\2\u01a7?"+
		"\3\2\2\2\u01a8\u01a9\b!\1\2\u01a9\u01aa\7\34\2\2\u01aa\u01b2\5@!\6\u01ab"+
		"\u01b2\5P)\2\u01ac\u01ad\7+\2\2\u01ad\u01ae\5@!\2\u01ae\u01af\7,\2\2\u01af"+
		"\u01b2\3\2\2\2\u01b0\u01b2\5B\"\2\u01b1\u01a8\3\2\2\2\u01b1\u01ab\3\2"+
		"\2\2\u01b1\u01ac\3\2\2\2\u01b1\u01b0\3\2\2\2\u01b2\u01bb\3\2\2\2\u01b3"+
		"\u01b4\f\4\2\2\u01b4\u01b5\7\32\2\2\u01b5\u01ba\5@!\5\u01b6\u01b7\f\3"+
		"\2\2\u01b7\u01b8\7\33\2\2\u01b8\u01ba\5@!\4\u01b9\u01b3\3\2\2\2\u01b9"+
		"\u01b6\3\2\2\2\u01ba\u01bd\3\2\2\2\u01bb\u01b9\3\2\2\2\u01bb\u01bc\3\2"+
		"\2\2\u01bcA\3\2\2\2\u01bd\u01bb\3\2\2\2\u01be\u01bf\5F$\2\u01bf\u01c0"+
		"\5D#\2\u01c0\u01c1\5F$\2\u01c1\u01cb\3\2\2\2\u01c2\u01c3\5H%\2\u01c3\u01c4"+
		"\5D#\2\u01c4\u01c5\5H%\2\u01c5\u01cb\3\2\2\2\u01c6\u01c7\7+\2\2\u01c7"+
		"\u01c8\5B\"\2\u01c8\u01c9\7,\2\2\u01c9\u01cb\3\2\2\2\u01ca\u01be\3\2\2"+
		"\2\u01ca\u01c2\3\2\2\2\u01ca\u01c6\3\2\2\2\u01cbC\3\2\2\2\u01cc\u01cd"+
		"\t\7\2\2\u01cdE\3\2\2\2\u01ce\u01cf\b$\1\2\u01cf\u01d0\7$\2\2\u01d0\u01d7"+
		"\5F$\6\u01d1\u01d7\5R*\2\u01d2\u01d3\7+\2\2\u01d3\u01d4\5F$\2\u01d4\u01d5"+
		"\7,\2\2\u01d5\u01d7\3\2\2\2\u01d6\u01ce\3\2\2\2\u01d6\u01d1\3\2\2\2\u01d6"+
		"\u01d2\3\2\2\2\u01d7\u01e3\3\2\2\2\u01d8\u01d9\f\5\2\2\u01d9\u01da\7\37"+
		"\2\2\u01da\u01e2\5F$\6\u01db\u01dc\f\4\2\2\u01dc\u01dd\t\b\2\2\u01dd\u01e2"+
		"\5F$\5\u01de\u01df\f\3\2\2\u01df\u01e0\t\t\2\2\u01e0\u01e2\5F$\4\u01e1"+
		"\u01d8\3\2\2\2\u01e1\u01db\3\2\2\2\u01e1\u01de\3\2\2\2\u01e2\u01e5\3\2"+
		"\2\2\u01e3\u01e1\3\2\2\2\u01e3\u01e4\3\2\2\2\u01e4G\3\2\2\2\u01e5\u01e3"+
		"\3\2\2\2\u01e6\u01e7\5T+\2\u01e7I\3\2\2\2\u01e8\u01e9\5L\'\2\u01e9K\3"+
		"\2\2\2\u01ea\u01eb\7.\2\2\u01eb\u01ec\7+\2\2\u01ec\u01ed\7:\2\2\u01ed"+
		"\u01ee\7-\2\2\u01ee\u01ef\7\30\2\2\u01ef\u01f0\7,\2\2\u01f0M\3\2\2\2\u01f1"+
		"\u01f2\t\n\2\2\u01f2O\3\2\2\2\u01f3\u01f8\5N(\2\u01f4\u01f8\78\2\2\u01f5"+
		"\u01f8\7\27\2\2\u01f6\u01f8\5J&\2\u01f7\u01f3\3\2\2\2\u01f7\u01f4\3\2"+
		"\2\2\u01f7\u01f5\3\2\2\2\u01f7\u01f6\3\2\2\2\u01f8Q\3\2\2\2\u01f9\u01fe"+
		"\7\26\2\2\u01fa\u01fe\7\25\2\2\u01fb\u01fe\79\2\2\u01fc\u01fe\7\27\2\2"+
		"\u01fd\u01f9\3\2\2\2\u01fd\u01fa\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fd\u01fc"+
		"\3\2\2\2\u01feS\3\2\2\2\u01ff\u0203\7\30\2\2\u0200\u0203\7:\2\2\u0201"+
		"\u0203\7\27\2\2\u0202\u01ff\3\2\2\2\u0202\u0200\3\2\2\2\u0202\u0201\3"+
		"\2\2\2\u0203U\3\2\2\2HW\\bfhnsz\177\u0085\u008d\u0092\u0096\u0099\u009c"+
		"\u00a1\u00a5\u00aa\u00af\u00b3\u00b6\u00bb\u00bf\u00c4\u00cc\u00d1\u00d5"+
		"\u00d8\u00dd\u00e2\u00ea\u00ef\u00f3\u00f6\u00fb\u0101\u0106\u010b\u0113"+
		"\u0118\u011e\u0124\u012a\u0130\u0137\u013e\u0148\u014b\u014e\u0152\u015b"+
		"\u016d\u016f\u0173\u017d\u0182\u0189\u0190\u0197\u019e\u01b1\u01b9\u01bb"+
		"\u01ca\u01d6\u01e1\u01e3\u01f7\u01fd\u0202";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
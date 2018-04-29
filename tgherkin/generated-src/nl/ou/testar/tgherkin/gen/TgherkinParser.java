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
		MATCHES_NAME=44, XPATH_NAME=45, IMAGE_NAME=46, CLICK_NAME=47, TYPE_NAME=48, 
		DRAG_NAME=49, ANY_NAME=50, DOUBLE_CLICK_NAME=51, TRIPLE_CLICK_NAME=52, 
		RIGHT_CLICK_NAME=53, MOUSE_MOVE_NAME=54, DROP_DOWN_AT_NAME=55, BOOLEAN_VARIABLE=56, 
		NUMBER_VARIABLE=57, STRING_VARIABLE=58, EOL=59, WS=60, OTHER=61, BOOLEAN_VARIABLE_NAME=62, 
		NUMBER_VARIABLE_NAME=63, STRING_VARIABLE_NAME=64;
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
		RULE_booleanFunction = 36, RULE_matchesFunction = 37, RULE_xpathFunction = 38, 
		RULE_imageFunction = 39, RULE_bool = 40, RULE_logical_entity = 41, RULE_numeric_entity = 42, 
		RULE_string_entity = 43;
	public static final String[] ruleNames = {
		"document", "execOptions", "execOptionExclude", "execOptionInclude", "feature", 
		"background", "scenarioDefiniton", "scenario", "scenarioOutline", "examples", 
		"table", "title", "narrativeLine", "tagname", "selection", "oracle", "step", 
		"givenClause", "whenClause", "thenClause", "stepRange", "widget_tree_condition", 
		"conditional_gesture", "gesture", "typeGesture", "clickGesture", "doubleClickGesture", 
		"tripleClickGesture", "anyGesture", "gestureName", "parameterlessGesture", 
		"widget_condition", "relational_expr", "relational_operator", "arithmetic_expr", 
		"string_expr", "booleanFunction", "matchesFunction", "xpathFunction", 
		"imageFunction", "bool", "logical_entity", "numeric_entity", "string_entity"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'include:'", "'exclude:'", null, "'Feature:'", "'Background:'", 
		"'Scenario:'", "'Scenario Outline:'", "'Examples:'", "'Selection:'", "'Oracle:'", 
		"'Step:'", "'Range'", "'Given'", "'When'", "'Then'", "'Also'", "'Either'", 
		null, null, null, null, null, null, "'and'", "'or'", null, "'true'", "'false'", 
		"'^'", "'*'", "'/'", "'%'", "'+'", "'-'", "'>'", "'>='", "'<'", "'<='", 
		"'='", null, "'('", "')'", "','", "'matches'", "'xpath'", "'image'", "'click'", 
		"'type'", "'drag'", "'anyGesture'", "'doubleClick'", "'tripleClick'", 
		"'rightClick'", "'mouseMove'", "'dropDownAt'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "OPTION_KEYWORD_INCLUDE", "OPTION_KEYWORD_EXCLUDE", "TAGNAME", "FEATURE_KEYWORD", 
		"BACKGROUND_KEYWORD", "SCENARIO_KEYWORD", "SCENARIO_OUTLINE_KEYWORD", 
		"EXAMPLES_KEYWORD", "SELECTION_KEYWORD", "ORACLE_KEYWORD", "STEP_KEYWORD", 
		"STEP_RANGE_KEYWORD", "STEP_GIVEN_KEYWORD", "STEP_WHEN_KEYWORD", "STEP_THEN_KEYWORD", 
		"STEP_ALSO_KEYWORD", "STEP_EITHER_KEYWORD", "TABLE_ROW", "DECIMAL_NUMBER", 
		"INTEGER_NUMBER", "PLACEHOLDER", "STRING", "COMMENT", "AND", "OR", "NOT", 
		"TRUE", "FALSE", "POW", "MULT", "DIV", "MOD", "PLUS", "MINUS", "GT", "GE", 
		"LT", "LE", "EQ", "NE", "LPAREN", "RPAREN", "COMMA", "MATCHES_NAME", "XPATH_NAME", 
		"IMAGE_NAME", "CLICK_NAME", "TYPE_NAME", "DRAG_NAME", "ANY_NAME", "DOUBLE_CLICK_NAME", 
		"TRIPLE_CLICK_NAME", "RIGHT_CLICK_NAME", "MOUSE_MOVE_NAME", "DROP_DOWN_AT_NAME", 
		"BOOLEAN_VARIABLE", "NUMBER_VARIABLE", "STRING_VARIABLE", "EOL", "WS", 
		"OTHER", "BOOLEAN_VARIABLE_NAME", "NUMBER_VARIABLE_NAME", "STRING_VARIABLE_NAME"
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
			setState(89);
			_la = _input.LA(1);
			if (_la==OPTION_KEYWORD_INCLUDE || _la==OPTION_KEYWORD_EXCLUDE) {
				{
				setState(88);
				execOptions();
				}
			}

			setState(92); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(91);
				feature();
				}
				}
				setState(94); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TAGNAME || _la==FEATURE_KEYWORD );
			setState(96);
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
			setState(106);
			switch (_input.LA(1)) {
			case OPTION_KEYWORD_EXCLUDE:
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				execOptionExclude();
				setState(100);
				_la = _input.LA(1);
				if (_la==OPTION_KEYWORD_INCLUDE) {
					{
					setState(99);
					execOptionInclude();
					}
				}

				}
				break;
			case OPTION_KEYWORD_INCLUDE:
				enterOuterAlt(_localctx, 2);
				{
				setState(102);
				execOptionInclude();
				setState(104);
				_la = _input.LA(1);
				if (_la==OPTION_KEYWORD_EXCLUDE) {
					{
					setState(103);
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
			setState(108);
			match(OPTION_KEYWORD_EXCLUDE);
			setState(110); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(109);
					((ExecOptionExcludeContext)_localctx).TAGNAME = match(TAGNAME);
					((ExecOptionExcludeContext)_localctx).tags.add(((ExecOptionExcludeContext)_localctx).TAGNAME);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(112); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(114);
				match(EOL);
				}
				}
				setState(119);
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
			setState(120);
			match(OPTION_KEYWORD_INCLUDE);
			setState(122); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(121);
					((ExecOptionIncludeContext)_localctx).TAGNAME = match(TAGNAME);
					((ExecOptionIncludeContext)_localctx).tags.add(((ExecOptionIncludeContext)_localctx).TAGNAME);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(124); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(126);
				match(EOL);
				}
				}
				setState(131);
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
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(132);
				((FeatureContext)_localctx).tagname = tagname();
				((FeatureContext)_localctx).tags.add(((FeatureContext)_localctx).tagname);
				}
				}
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(138);
			match(FEATURE_KEYWORD);
			setState(139);
			title();
			setState(141); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(140);
				match(EOL);
				}
				}
				setState(143); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (OPTION_KEYWORD_INCLUDE - 1)) | (1L << (OPTION_KEYWORD_EXCLUDE - 1)) | (1L << (DECIMAL_NUMBER - 1)) | (1L << (INTEGER_NUMBER - 1)) | (1L << (PLACEHOLDER - 1)) | (1L << (STRING - 1)) | (1L << (COMMENT - 1)) | (1L << (AND - 1)) | (1L << (OR - 1)) | (1L << (NOT - 1)) | (1L << (TRUE - 1)) | (1L << (FALSE - 1)) | (1L << (POW - 1)) | (1L << (MULT - 1)) | (1L << (DIV - 1)) | (1L << (MOD - 1)) | (1L << (PLUS - 1)) | (1L << (MINUS - 1)) | (1L << (GT - 1)) | (1L << (GE - 1)) | (1L << (LT - 1)) | (1L << (LE - 1)) | (1L << (EQ - 1)) | (1L << (NE - 1)) | (1L << (LPAREN - 1)) | (1L << (RPAREN - 1)) | (1L << (COMMA - 1)) | (1L << (MATCHES_NAME - 1)) | (1L << (XPATH_NAME - 1)) | (1L << (IMAGE_NAME - 1)) | (1L << (CLICK_NAME - 1)) | (1L << (TYPE_NAME - 1)) | (1L << (DRAG_NAME - 1)) | (1L << (ANY_NAME - 1)) | (1L << (DOUBLE_CLICK_NAME - 1)) | (1L << (TRIPLE_CLICK_NAME - 1)) | (1L << (RIGHT_CLICK_NAME - 1)) | (1L << (MOUSE_MOVE_NAME - 1)) | (1L << (DROP_DOWN_AT_NAME - 1)) | (1L << (BOOLEAN_VARIABLE - 1)) | (1L << (NUMBER_VARIABLE - 1)) | (1L << (STRING_VARIABLE - 1)) | (1L << (WS - 1)) | (1L << (OTHER - 1)) | (1L << (BOOLEAN_VARIABLE_NAME - 1)) | (1L << (NUMBER_VARIABLE_NAME - 1)) | (1L << (STRING_VARIABLE_NAME - 1)))) != 0)) {
				{
				{
				setState(145);
				((FeatureContext)_localctx).narrativeLine = narrativeLine();
				((FeatureContext)_localctx).narrative.add(((FeatureContext)_localctx).narrativeLine);
				}
				}
				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(152);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(151);
				selection();
				}
			}

			setState(155);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(154);
				oracle();
				}
			}

			setState(158);
			_la = _input.LA(1);
			if (_la==BACKGROUND_KEYWORD) {
				{
				setState(157);
				background();
				}
			}

			setState(161); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(160);
					scenarioDefiniton();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(163); 
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
			setState(165);
			match(BACKGROUND_KEYWORD);
			setState(167);
			_la = _input.LA(1);
			if (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (OPTION_KEYWORD_INCLUDE - 1)) | (1L << (OPTION_KEYWORD_EXCLUDE - 1)) | (1L << (TAGNAME - 1)) | (1L << (FEATURE_KEYWORD - 1)) | (1L << (BACKGROUND_KEYWORD - 1)) | (1L << (SCENARIO_KEYWORD - 1)) | (1L << (SCENARIO_OUTLINE_KEYWORD - 1)) | (1L << (EXAMPLES_KEYWORD - 1)) | (1L << (SELECTION_KEYWORD - 1)) | (1L << (ORACLE_KEYWORD - 1)) | (1L << (STEP_KEYWORD - 1)) | (1L << (STEP_RANGE_KEYWORD - 1)) | (1L << (STEP_GIVEN_KEYWORD - 1)) | (1L << (STEP_WHEN_KEYWORD - 1)) | (1L << (STEP_THEN_KEYWORD - 1)) | (1L << (STEP_ALSO_KEYWORD - 1)) | (1L << (STEP_EITHER_KEYWORD - 1)) | (1L << (TABLE_ROW - 1)) | (1L << (DECIMAL_NUMBER - 1)) | (1L << (INTEGER_NUMBER - 1)) | (1L << (PLACEHOLDER - 1)) | (1L << (STRING - 1)) | (1L << (COMMENT - 1)) | (1L << (AND - 1)) | (1L << (OR - 1)) | (1L << (NOT - 1)) | (1L << (TRUE - 1)) | (1L << (FALSE - 1)) | (1L << (POW - 1)) | (1L << (MULT - 1)) | (1L << (DIV - 1)) | (1L << (MOD - 1)) | (1L << (PLUS - 1)) | (1L << (MINUS - 1)) | (1L << (GT - 1)) | (1L << (GE - 1)) | (1L << (LT - 1)) | (1L << (LE - 1)) | (1L << (EQ - 1)) | (1L << (NE - 1)) | (1L << (LPAREN - 1)) | (1L << (RPAREN - 1)) | (1L << (COMMA - 1)) | (1L << (MATCHES_NAME - 1)) | (1L << (XPATH_NAME - 1)) | (1L << (IMAGE_NAME - 1)) | (1L << (CLICK_NAME - 1)) | (1L << (TYPE_NAME - 1)) | (1L << (DRAG_NAME - 1)) | (1L << (ANY_NAME - 1)) | (1L << (DOUBLE_CLICK_NAME - 1)) | (1L << (TRIPLE_CLICK_NAME - 1)) | (1L << (RIGHT_CLICK_NAME - 1)) | (1L << (MOUSE_MOVE_NAME - 1)) | (1L << (DROP_DOWN_AT_NAME - 1)) | (1L << (BOOLEAN_VARIABLE - 1)) | (1L << (NUMBER_VARIABLE - 1)) | (1L << (STRING_VARIABLE - 1)) | (1L << (WS - 1)) | (1L << (OTHER - 1)) | (1L << (BOOLEAN_VARIABLE_NAME - 1)) | (1L << (NUMBER_VARIABLE_NAME - 1)) | (1L << (STRING_VARIABLE_NAME - 1)))) != 0)) {
				{
				setState(166);
				title();
				}
			}

			setState(170); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(169);
				match(EOL);
				}
				}
				setState(172); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (OPTION_KEYWORD_INCLUDE - 1)) | (1L << (OPTION_KEYWORD_EXCLUDE - 1)) | (1L << (DECIMAL_NUMBER - 1)) | (1L << (INTEGER_NUMBER - 1)) | (1L << (PLACEHOLDER - 1)) | (1L << (STRING - 1)) | (1L << (COMMENT - 1)) | (1L << (AND - 1)) | (1L << (OR - 1)) | (1L << (NOT - 1)) | (1L << (TRUE - 1)) | (1L << (FALSE - 1)) | (1L << (POW - 1)) | (1L << (MULT - 1)) | (1L << (DIV - 1)) | (1L << (MOD - 1)) | (1L << (PLUS - 1)) | (1L << (MINUS - 1)) | (1L << (GT - 1)) | (1L << (GE - 1)) | (1L << (LT - 1)) | (1L << (LE - 1)) | (1L << (EQ - 1)) | (1L << (NE - 1)) | (1L << (LPAREN - 1)) | (1L << (RPAREN - 1)) | (1L << (COMMA - 1)) | (1L << (MATCHES_NAME - 1)) | (1L << (XPATH_NAME - 1)) | (1L << (IMAGE_NAME - 1)) | (1L << (CLICK_NAME - 1)) | (1L << (TYPE_NAME - 1)) | (1L << (DRAG_NAME - 1)) | (1L << (ANY_NAME - 1)) | (1L << (DOUBLE_CLICK_NAME - 1)) | (1L << (TRIPLE_CLICK_NAME - 1)) | (1L << (RIGHT_CLICK_NAME - 1)) | (1L << (MOUSE_MOVE_NAME - 1)) | (1L << (DROP_DOWN_AT_NAME - 1)) | (1L << (BOOLEAN_VARIABLE - 1)) | (1L << (NUMBER_VARIABLE - 1)) | (1L << (STRING_VARIABLE - 1)) | (1L << (WS - 1)) | (1L << (OTHER - 1)) | (1L << (BOOLEAN_VARIABLE_NAME - 1)) | (1L << (NUMBER_VARIABLE_NAME - 1)) | (1L << (STRING_VARIABLE_NAME - 1)))) != 0)) {
				{
				{
				setState(174);
				((BackgroundContext)_localctx).narrativeLine = narrativeLine();
				((BackgroundContext)_localctx).narrative.add(((BackgroundContext)_localctx).narrativeLine);
				}
				}
				setState(179);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(181);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(180);
				selection();
				}
			}

			setState(184);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(183);
				oracle();
				}
			}

			setState(187); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(186);
				((BackgroundContext)_localctx).step = step();
				((BackgroundContext)_localctx).steps.add(((BackgroundContext)_localctx).step);
				}
				}
				setState(189); 
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
			setState(193);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(191);
				scenario();
				}
				break;
			case 2:
				{
				setState(192);
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
			setState(198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(195);
				((ScenarioContext)_localctx).tagname = tagname();
				((ScenarioContext)_localctx).tags.add(((ScenarioContext)_localctx).tagname);
				}
				}
				setState(200);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(201);
			match(SCENARIO_KEYWORD);
			setState(202);
			title();
			setState(204); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(203);
				match(EOL);
				}
				}
				setState(206); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(211);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (OPTION_KEYWORD_INCLUDE - 1)) | (1L << (OPTION_KEYWORD_EXCLUDE - 1)) | (1L << (DECIMAL_NUMBER - 1)) | (1L << (INTEGER_NUMBER - 1)) | (1L << (PLACEHOLDER - 1)) | (1L << (STRING - 1)) | (1L << (COMMENT - 1)) | (1L << (AND - 1)) | (1L << (OR - 1)) | (1L << (NOT - 1)) | (1L << (TRUE - 1)) | (1L << (FALSE - 1)) | (1L << (POW - 1)) | (1L << (MULT - 1)) | (1L << (DIV - 1)) | (1L << (MOD - 1)) | (1L << (PLUS - 1)) | (1L << (MINUS - 1)) | (1L << (GT - 1)) | (1L << (GE - 1)) | (1L << (LT - 1)) | (1L << (LE - 1)) | (1L << (EQ - 1)) | (1L << (NE - 1)) | (1L << (LPAREN - 1)) | (1L << (RPAREN - 1)) | (1L << (COMMA - 1)) | (1L << (MATCHES_NAME - 1)) | (1L << (XPATH_NAME - 1)) | (1L << (IMAGE_NAME - 1)) | (1L << (CLICK_NAME - 1)) | (1L << (TYPE_NAME - 1)) | (1L << (DRAG_NAME - 1)) | (1L << (ANY_NAME - 1)) | (1L << (DOUBLE_CLICK_NAME - 1)) | (1L << (TRIPLE_CLICK_NAME - 1)) | (1L << (RIGHT_CLICK_NAME - 1)) | (1L << (MOUSE_MOVE_NAME - 1)) | (1L << (DROP_DOWN_AT_NAME - 1)) | (1L << (BOOLEAN_VARIABLE - 1)) | (1L << (NUMBER_VARIABLE - 1)) | (1L << (STRING_VARIABLE - 1)) | (1L << (WS - 1)) | (1L << (OTHER - 1)) | (1L << (BOOLEAN_VARIABLE_NAME - 1)) | (1L << (NUMBER_VARIABLE_NAME - 1)) | (1L << (STRING_VARIABLE_NAME - 1)))) != 0)) {
				{
				{
				setState(208);
				((ScenarioContext)_localctx).narrativeLine = narrativeLine();
				((ScenarioContext)_localctx).narrative.add(((ScenarioContext)_localctx).narrativeLine);
				}
				}
				setState(213);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(215);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(214);
				selection();
				}
			}

			setState(218);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(217);
				oracle();
				}
			}

			setState(221); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(220);
				((ScenarioContext)_localctx).step = step();
				((ScenarioContext)_localctx).steps.add(((ScenarioContext)_localctx).step);
				}
				}
				setState(223); 
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
			setState(228);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(225);
				((ScenarioOutlineContext)_localctx).tagname = tagname();
				((ScenarioOutlineContext)_localctx).tags.add(((ScenarioOutlineContext)_localctx).tagname);
				}
				}
				setState(230);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(231);
			match(SCENARIO_OUTLINE_KEYWORD);
			setState(232);
			title();
			setState(234); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(233);
				match(EOL);
				}
				}
				setState(236); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (OPTION_KEYWORD_INCLUDE - 1)) | (1L << (OPTION_KEYWORD_EXCLUDE - 1)) | (1L << (DECIMAL_NUMBER - 1)) | (1L << (INTEGER_NUMBER - 1)) | (1L << (PLACEHOLDER - 1)) | (1L << (STRING - 1)) | (1L << (COMMENT - 1)) | (1L << (AND - 1)) | (1L << (OR - 1)) | (1L << (NOT - 1)) | (1L << (TRUE - 1)) | (1L << (FALSE - 1)) | (1L << (POW - 1)) | (1L << (MULT - 1)) | (1L << (DIV - 1)) | (1L << (MOD - 1)) | (1L << (PLUS - 1)) | (1L << (MINUS - 1)) | (1L << (GT - 1)) | (1L << (GE - 1)) | (1L << (LT - 1)) | (1L << (LE - 1)) | (1L << (EQ - 1)) | (1L << (NE - 1)) | (1L << (LPAREN - 1)) | (1L << (RPAREN - 1)) | (1L << (COMMA - 1)) | (1L << (MATCHES_NAME - 1)) | (1L << (XPATH_NAME - 1)) | (1L << (IMAGE_NAME - 1)) | (1L << (CLICK_NAME - 1)) | (1L << (TYPE_NAME - 1)) | (1L << (DRAG_NAME - 1)) | (1L << (ANY_NAME - 1)) | (1L << (DOUBLE_CLICK_NAME - 1)) | (1L << (TRIPLE_CLICK_NAME - 1)) | (1L << (RIGHT_CLICK_NAME - 1)) | (1L << (MOUSE_MOVE_NAME - 1)) | (1L << (DROP_DOWN_AT_NAME - 1)) | (1L << (BOOLEAN_VARIABLE - 1)) | (1L << (NUMBER_VARIABLE - 1)) | (1L << (STRING_VARIABLE - 1)) | (1L << (WS - 1)) | (1L << (OTHER - 1)) | (1L << (BOOLEAN_VARIABLE_NAME - 1)) | (1L << (NUMBER_VARIABLE_NAME - 1)) | (1L << (STRING_VARIABLE_NAME - 1)))) != 0)) {
				{
				{
				setState(238);
				((ScenarioOutlineContext)_localctx).narrativeLine = narrativeLine();
				((ScenarioOutlineContext)_localctx).narrative.add(((ScenarioOutlineContext)_localctx).narrativeLine);
				}
				}
				setState(243);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(245);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(244);
				selection();
				}
			}

			setState(248);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(247);
				oracle();
				}
			}

			setState(251); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(250);
				((ScenarioOutlineContext)_localctx).step = step();
				((ScenarioOutlineContext)_localctx).steps.add(((ScenarioOutlineContext)_localctx).step);
				}
				}
				setState(253); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STEP_KEYWORD );
			setState(255);
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
			setState(257);
			match(EXAMPLES_KEYWORD);
			setState(259);
			_la = _input.LA(1);
			if (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (OPTION_KEYWORD_INCLUDE - 1)) | (1L << (OPTION_KEYWORD_EXCLUDE - 1)) | (1L << (TAGNAME - 1)) | (1L << (FEATURE_KEYWORD - 1)) | (1L << (BACKGROUND_KEYWORD - 1)) | (1L << (SCENARIO_KEYWORD - 1)) | (1L << (SCENARIO_OUTLINE_KEYWORD - 1)) | (1L << (EXAMPLES_KEYWORD - 1)) | (1L << (SELECTION_KEYWORD - 1)) | (1L << (ORACLE_KEYWORD - 1)) | (1L << (STEP_KEYWORD - 1)) | (1L << (STEP_RANGE_KEYWORD - 1)) | (1L << (STEP_GIVEN_KEYWORD - 1)) | (1L << (STEP_WHEN_KEYWORD - 1)) | (1L << (STEP_THEN_KEYWORD - 1)) | (1L << (STEP_ALSO_KEYWORD - 1)) | (1L << (STEP_EITHER_KEYWORD - 1)) | (1L << (TABLE_ROW - 1)) | (1L << (DECIMAL_NUMBER - 1)) | (1L << (INTEGER_NUMBER - 1)) | (1L << (PLACEHOLDER - 1)) | (1L << (STRING - 1)) | (1L << (COMMENT - 1)) | (1L << (AND - 1)) | (1L << (OR - 1)) | (1L << (NOT - 1)) | (1L << (TRUE - 1)) | (1L << (FALSE - 1)) | (1L << (POW - 1)) | (1L << (MULT - 1)) | (1L << (DIV - 1)) | (1L << (MOD - 1)) | (1L << (PLUS - 1)) | (1L << (MINUS - 1)) | (1L << (GT - 1)) | (1L << (GE - 1)) | (1L << (LT - 1)) | (1L << (LE - 1)) | (1L << (EQ - 1)) | (1L << (NE - 1)) | (1L << (LPAREN - 1)) | (1L << (RPAREN - 1)) | (1L << (COMMA - 1)) | (1L << (MATCHES_NAME - 1)) | (1L << (XPATH_NAME - 1)) | (1L << (IMAGE_NAME - 1)) | (1L << (CLICK_NAME - 1)) | (1L << (TYPE_NAME - 1)) | (1L << (DRAG_NAME - 1)) | (1L << (ANY_NAME - 1)) | (1L << (DOUBLE_CLICK_NAME - 1)) | (1L << (TRIPLE_CLICK_NAME - 1)) | (1L << (RIGHT_CLICK_NAME - 1)) | (1L << (MOUSE_MOVE_NAME - 1)) | (1L << (DROP_DOWN_AT_NAME - 1)) | (1L << (BOOLEAN_VARIABLE - 1)) | (1L << (NUMBER_VARIABLE - 1)) | (1L << (STRING_VARIABLE - 1)) | (1L << (WS - 1)) | (1L << (OTHER - 1)) | (1L << (BOOLEAN_VARIABLE_NAME - 1)) | (1L << (NUMBER_VARIABLE_NAME - 1)) | (1L << (STRING_VARIABLE_NAME - 1)))) != 0)) {
				{
				setState(258);
				title();
				}
			}

			setState(262); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(261);
				match(EOL);
				}
				}
				setState(264); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (OPTION_KEYWORD_INCLUDE - 1)) | (1L << (OPTION_KEYWORD_EXCLUDE - 1)) | (1L << (DECIMAL_NUMBER - 1)) | (1L << (INTEGER_NUMBER - 1)) | (1L << (PLACEHOLDER - 1)) | (1L << (STRING - 1)) | (1L << (COMMENT - 1)) | (1L << (AND - 1)) | (1L << (OR - 1)) | (1L << (NOT - 1)) | (1L << (TRUE - 1)) | (1L << (FALSE - 1)) | (1L << (POW - 1)) | (1L << (MULT - 1)) | (1L << (DIV - 1)) | (1L << (MOD - 1)) | (1L << (PLUS - 1)) | (1L << (MINUS - 1)) | (1L << (GT - 1)) | (1L << (GE - 1)) | (1L << (LT - 1)) | (1L << (LE - 1)) | (1L << (EQ - 1)) | (1L << (NE - 1)) | (1L << (LPAREN - 1)) | (1L << (RPAREN - 1)) | (1L << (COMMA - 1)) | (1L << (MATCHES_NAME - 1)) | (1L << (XPATH_NAME - 1)) | (1L << (IMAGE_NAME - 1)) | (1L << (CLICK_NAME - 1)) | (1L << (TYPE_NAME - 1)) | (1L << (DRAG_NAME - 1)) | (1L << (ANY_NAME - 1)) | (1L << (DOUBLE_CLICK_NAME - 1)) | (1L << (TRIPLE_CLICK_NAME - 1)) | (1L << (RIGHT_CLICK_NAME - 1)) | (1L << (MOUSE_MOVE_NAME - 1)) | (1L << (DROP_DOWN_AT_NAME - 1)) | (1L << (BOOLEAN_VARIABLE - 1)) | (1L << (NUMBER_VARIABLE - 1)) | (1L << (STRING_VARIABLE - 1)) | (1L << (WS - 1)) | (1L << (OTHER - 1)) | (1L << (BOOLEAN_VARIABLE_NAME - 1)) | (1L << (NUMBER_VARIABLE_NAME - 1)) | (1L << (STRING_VARIABLE_NAME - 1)))) != 0)) {
				{
				{
				setState(266);
				((ExamplesContext)_localctx).narrativeLine = narrativeLine();
				((ExamplesContext)_localctx).narrative.add(((ExamplesContext)_localctx).narrativeLine);
				}
				}
				setState(271);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(272);
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
			setState(275); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(274);
				((TableContext)_localctx).TABLE_ROW = match(TABLE_ROW);
				((TableContext)_localctx).rows.add(((TableContext)_localctx).TABLE_ROW);
				}
				}
				setState(277); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TABLE_ROW );
			setState(282);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(279);
				match(EOL);
				}
				}
				setState(284);
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
			setState(288);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(285);
					match(WS);
					}
					} 
				}
				setState(290);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			}
			setState(292); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(291);
				_la = _input.LA(1);
				if ( _la <= 0 || (_la==EOL) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				}
				setState(294); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( ((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (OPTION_KEYWORD_INCLUDE - 1)) | (1L << (OPTION_KEYWORD_EXCLUDE - 1)) | (1L << (TAGNAME - 1)) | (1L << (FEATURE_KEYWORD - 1)) | (1L << (BACKGROUND_KEYWORD - 1)) | (1L << (SCENARIO_KEYWORD - 1)) | (1L << (SCENARIO_OUTLINE_KEYWORD - 1)) | (1L << (EXAMPLES_KEYWORD - 1)) | (1L << (SELECTION_KEYWORD - 1)) | (1L << (ORACLE_KEYWORD - 1)) | (1L << (STEP_KEYWORD - 1)) | (1L << (STEP_RANGE_KEYWORD - 1)) | (1L << (STEP_GIVEN_KEYWORD - 1)) | (1L << (STEP_WHEN_KEYWORD - 1)) | (1L << (STEP_THEN_KEYWORD - 1)) | (1L << (STEP_ALSO_KEYWORD - 1)) | (1L << (STEP_EITHER_KEYWORD - 1)) | (1L << (TABLE_ROW - 1)) | (1L << (DECIMAL_NUMBER - 1)) | (1L << (INTEGER_NUMBER - 1)) | (1L << (PLACEHOLDER - 1)) | (1L << (STRING - 1)) | (1L << (COMMENT - 1)) | (1L << (AND - 1)) | (1L << (OR - 1)) | (1L << (NOT - 1)) | (1L << (TRUE - 1)) | (1L << (FALSE - 1)) | (1L << (POW - 1)) | (1L << (MULT - 1)) | (1L << (DIV - 1)) | (1L << (MOD - 1)) | (1L << (PLUS - 1)) | (1L << (MINUS - 1)) | (1L << (GT - 1)) | (1L << (GE - 1)) | (1L << (LT - 1)) | (1L << (LE - 1)) | (1L << (EQ - 1)) | (1L << (NE - 1)) | (1L << (LPAREN - 1)) | (1L << (RPAREN - 1)) | (1L << (COMMA - 1)) | (1L << (MATCHES_NAME - 1)) | (1L << (XPATH_NAME - 1)) | (1L << (IMAGE_NAME - 1)) | (1L << (CLICK_NAME - 1)) | (1L << (TYPE_NAME - 1)) | (1L << (DRAG_NAME - 1)) | (1L << (ANY_NAME - 1)) | (1L << (DOUBLE_CLICK_NAME - 1)) | (1L << (TRIPLE_CLICK_NAME - 1)) | (1L << (RIGHT_CLICK_NAME - 1)) | (1L << (MOUSE_MOVE_NAME - 1)) | (1L << (DROP_DOWN_AT_NAME - 1)) | (1L << (BOOLEAN_VARIABLE - 1)) | (1L << (NUMBER_VARIABLE - 1)) | (1L << (STRING_VARIABLE - 1)) | (1L << (WS - 1)) | (1L << (OTHER - 1)) | (1L << (BOOLEAN_VARIABLE_NAME - 1)) | (1L << (NUMBER_VARIABLE_NAME - 1)) | (1L << (STRING_VARIABLE_NAME - 1)))) != 0) );
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
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
			_la = _input.LA(1);
			if ( _la <= 0 || ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << EOL))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(300);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (OPTION_KEYWORD_INCLUDE - 1)) | (1L << (OPTION_KEYWORD_EXCLUDE - 1)) | (1L << (TAGNAME - 1)) | (1L << (FEATURE_KEYWORD - 1)) | (1L << (BACKGROUND_KEYWORD - 1)) | (1L << (SCENARIO_KEYWORD - 1)) | (1L << (SCENARIO_OUTLINE_KEYWORD - 1)) | (1L << (EXAMPLES_KEYWORD - 1)) | (1L << (SELECTION_KEYWORD - 1)) | (1L << (ORACLE_KEYWORD - 1)) | (1L << (STEP_KEYWORD - 1)) | (1L << (STEP_RANGE_KEYWORD - 1)) | (1L << (STEP_GIVEN_KEYWORD - 1)) | (1L << (STEP_WHEN_KEYWORD - 1)) | (1L << (STEP_THEN_KEYWORD - 1)) | (1L << (STEP_ALSO_KEYWORD - 1)) | (1L << (STEP_EITHER_KEYWORD - 1)) | (1L << (TABLE_ROW - 1)) | (1L << (DECIMAL_NUMBER - 1)) | (1L << (INTEGER_NUMBER - 1)) | (1L << (PLACEHOLDER - 1)) | (1L << (STRING - 1)) | (1L << (COMMENT - 1)) | (1L << (AND - 1)) | (1L << (OR - 1)) | (1L << (NOT - 1)) | (1L << (TRUE - 1)) | (1L << (FALSE - 1)) | (1L << (POW - 1)) | (1L << (MULT - 1)) | (1L << (DIV - 1)) | (1L << (MOD - 1)) | (1L << (PLUS - 1)) | (1L << (MINUS - 1)) | (1L << (GT - 1)) | (1L << (GE - 1)) | (1L << (LT - 1)) | (1L << (LE - 1)) | (1L << (EQ - 1)) | (1L << (NE - 1)) | (1L << (LPAREN - 1)) | (1L << (RPAREN - 1)) | (1L << (COMMA - 1)) | (1L << (MATCHES_NAME - 1)) | (1L << (XPATH_NAME - 1)) | (1L << (IMAGE_NAME - 1)) | (1L << (CLICK_NAME - 1)) | (1L << (TYPE_NAME - 1)) | (1L << (DRAG_NAME - 1)) | (1L << (ANY_NAME - 1)) | (1L << (DOUBLE_CLICK_NAME - 1)) | (1L << (TRIPLE_CLICK_NAME - 1)) | (1L << (RIGHT_CLICK_NAME - 1)) | (1L << (MOUSE_MOVE_NAME - 1)) | (1L << (DROP_DOWN_AT_NAME - 1)) | (1L << (BOOLEAN_VARIABLE - 1)) | (1L << (NUMBER_VARIABLE - 1)) | (1L << (STRING_VARIABLE - 1)) | (1L << (WS - 1)) | (1L << (OTHER - 1)) | (1L << (BOOLEAN_VARIABLE_NAME - 1)) | (1L << (NUMBER_VARIABLE_NAME - 1)) | (1L << (STRING_VARIABLE_NAME - 1)))) != 0)) {
				{
				{
				setState(297);
				_la = _input.LA(1);
				if ( _la <= 0 || (_la==EOL) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				}
				setState(302);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(304); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(303);
				match(EOL);
				}
				}
				setState(306); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
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
			setState(308);
			match(TAGNAME);
			setState(312);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(309);
				match(EOL);
				}
				}
				setState(314);
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
			setState(315);
			match(SELECTION_KEYWORD);
			setState(317); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(316);
				((SelectionContext)_localctx).conditional_gesture = conditional_gesture();
				((SelectionContext)_localctx).conditional_gestures.add(((SelectionContext)_localctx).conditional_gesture);
				}
				}
				setState(319); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << MINUS) | (1L << LPAREN) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << IMAGE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE))) != 0) );
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
			setState(321);
			match(ORACLE_KEYWORD);
			setState(322);
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
			setState(324);
			match(STEP_KEYWORD);
			setState(325);
			title();
			setState(327); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(326);
				match(EOL);
				}
				}
				setState(329); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(332);
			_la = _input.LA(1);
			if (_la==STEP_RANGE_KEYWORD) {
				{
				setState(331);
				stepRange();
				}
			}

			setState(335);
			_la = _input.LA(1);
			if (_la==STEP_GIVEN_KEYWORD) {
				{
				setState(334);
				givenClause();
				}
			}

			setState(337);
			whenClause();
			setState(339);
			_la = _input.LA(1);
			if (_la==STEP_THEN_KEYWORD) {
				{
				setState(338);
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
			setState(341);
			match(STEP_GIVEN_KEYWORD);
			setState(342);
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
			setState(344);
			match(STEP_WHEN_KEYWORD);
			setState(346); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(345);
				((WhenClauseContext)_localctx).conditional_gesture = conditional_gesture();
				((WhenClauseContext)_localctx).conditional_gestures.add(((WhenClauseContext)_localctx).conditional_gesture);
				}
				}
				setState(348); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << MINUS) | (1L << LPAREN) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << IMAGE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE))) != 0) );
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
			setState(350);
			match(STEP_THEN_KEYWORD);
			setState(351);
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
			setState(353);
			match(STEP_RANGE_KEYWORD);
			setState(354);
			((StepRangeContext)_localctx).from = match(INTEGER_NUMBER);
			setState(355);
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

			setState(358);
			widget_condition(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(368);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(366);
					switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetTreeConditionAlsoContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionAlsoContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(360);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(361);
						match(STEP_ALSO_KEYWORD);
						setState(362);
						((WidgetTreeConditionAlsoContext)_localctx).right = widget_tree_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetTreeConditionEitherContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionEitherContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(363);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(364);
						match(STEP_EITHER_KEYWORD);
						setState(365);
						((WidgetTreeConditionEitherContext)_localctx).right = widget_tree_condition(2);
						}
						break;
					}
					} 
				}
				setState(370);
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
			setState(372);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << MINUS) | (1L << LPAREN) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << IMAGE_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE))) != 0)) {
				{
				setState(371);
				widget_condition(0);
				}
			}

			setState(374);
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
			setState(382);
			switch (_input.LA(1)) {
			case DRAG_NAME:
			case RIGHT_CLICK_NAME:
			case MOUSE_MOVE_NAME:
			case DROP_DOWN_AT_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(376);
				parameterlessGesture();
				}
				break;
			case TYPE_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(377);
				typeGesture();
				}
				break;
			case CLICK_NAME:
				enterOuterAlt(_localctx, 3);
				{
				setState(378);
				clickGesture();
				}
				break;
			case DOUBLE_CLICK_NAME:
				enterOuterAlt(_localctx, 4);
				{
				setState(379);
				doubleClickGesture();
				}
				break;
			case TRIPLE_CLICK_NAME:
				enterOuterAlt(_localctx, 5);
				{
				setState(380);
				tripleClickGesture();
				}
				break;
			case ANY_NAME:
				enterOuterAlt(_localctx, 6);
				{
				setState(381);
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
			setState(384);
			match(TYPE_NAME);
			setState(385);
			match(LPAREN);
			setState(387);
			_la = _input.LA(1);
			if (_la==PLACEHOLDER || _la==STRING) {
				{
				setState(386);
				_la = _input.LA(1);
				if ( !(_la==PLACEHOLDER || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(389);
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
			setState(391);
			match(CLICK_NAME);
			setState(392);
			match(LPAREN);
			setState(394);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(393);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(396);
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
			setState(398);
			match(DOUBLE_CLICK_NAME);
			setState(399);
			match(LPAREN);
			setState(401);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(400);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(403);
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
			setState(405);
			match(TRIPLE_CLICK_NAME);
			setState(406);
			match(LPAREN);
			setState(408);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(407);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(410);
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
			setState(412);
			match(ANY_NAME);
			setState(413);
			match(LPAREN);
			setState(415);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(414);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(417);
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
			setState(419);
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
			setState(421);
			gestureName();
			setState(422);
			match(LPAREN);
			setState(423);
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
			setState(434);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				_localctx = new NegationWidgetConditionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(426);
				match(NOT);
				setState(427);
				widget_condition(4);
				}
				break;
			case 2:
				{
				_localctx = new LogicalEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(428);
				logical_entity();
				}
				break;
			case 3:
				{
				_localctx = new WidgetConditionInParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(429);
				match(LPAREN);
				setState(430);
				widget_condition(0);
				setState(431);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new RelationalExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(433);
				relational_expr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(444);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(442);
					switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetConditionAndContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(436);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(437);
						match(AND);
						setState(438);
						((WidgetConditionAndContext)_localctx).right = widget_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetConditionOrContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(439);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(440);
						match(OR);
						setState(441);
						((WidgetConditionOrContext)_localctx).right = widget_condition(2);
						}
						break;
					}
					} 
				}
				setState(446);
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
			setState(459);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				_localctx = new RelationalNumericExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(447);
				((RelationalNumericExpressionWithOperatorContext)_localctx).left = arithmetic_expr(0);
				setState(448);
				relational_operator();
				setState(449);
				((RelationalNumericExpressionWithOperatorContext)_localctx).right = arithmetic_expr(0);
				}
				break;
			case 2:
				_localctx = new RelationalStringExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(451);
				((RelationalStringExpressionWithOperatorContext)_localctx).left = string_expr();
				setState(452);
				relational_operator();
				setState(453);
				((RelationalStringExpressionWithOperatorContext)_localctx).right = string_expr();
				}
				break;
			case 3:
				_localctx = new RelationalExpressionParensContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(455);
				match(LPAREN);
				setState(456);
				relational_expr();
				setState(457);
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
			setState(461);
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
			setState(471);
			switch (_input.LA(1)) {
			case MINUS:
				{
				_localctx = new ArithmeticExpressionNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(464);
				match(MINUS);
				setState(465);
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
				setState(466);
				numeric_entity();
				}
				break;
			case LPAREN:
				{
				_localctx = new ArithmeticExpressionParensContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(467);
				match(LPAREN);
				setState(468);
				arithmetic_expr(0);
				setState(469);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(484);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(482);
					switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticExpressionPowContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPowContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(473);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(474);
						match(POW);
						setState(475);
						((ArithmeticExpressionPowContext)_localctx).right = arithmetic_expr(4);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticExpressionMultDivModContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionMultDivModContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(476);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(477);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(478);
						((ArithmeticExpressionMultDivModContext)_localctx).right = arithmetic_expr(3);
						}
						break;
					case 3:
						{
						_localctx = new ArithmeticExpressionPlusMinusContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPlusMinusContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(479);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(480);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(481);
						((ArithmeticExpressionPlusMinusContext)_localctx).right = arithmetic_expr(2);
						}
						break;
					}
					} 
				}
				setState(486);
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
			setState(487);
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
		public XpathFunctionContext xpathFunction() {
			return getRuleContext(XpathFunctionContext.class,0);
		}
		public ImageFunctionContext imageFunction() {
			return getRuleContext(ImageFunctionContext.class,0);
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
			setState(492);
			switch (_input.LA(1)) {
			case MATCHES_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(489);
				matchesFunction();
				}
				break;
			case XPATH_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(490);
				xpathFunction();
				}
				break;
			case IMAGE_NAME:
				enterOuterAlt(_localctx, 3);
				{
				setState(491);
				imageFunction();
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
			setState(494);
			match(MATCHES_NAME);
			setState(495);
			match(LPAREN);
			setState(496);
			match(STRING_VARIABLE);
			setState(497);
			match(COMMA);
			setState(498);
			match(STRING);
			setState(499);
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

	public static class XpathFunctionContext extends ParserRuleContext {
		public TerminalNode XPATH_NAME() { return getToken(TgherkinParser.XPATH_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(TgherkinParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public XpathFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpathFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitXpathFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final XpathFunctionContext xpathFunction() throws RecognitionException {
		XpathFunctionContext _localctx = new XpathFunctionContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_xpathFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(501);
			match(XPATH_NAME);
			setState(502);
			match(LPAREN);
			setState(503);
			match(STRING);
			setState(504);
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

	public static class ImageFunctionContext extends ParserRuleContext {
		public TerminalNode IMAGE_NAME() { return getToken(TgherkinParser.IMAGE_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(TgherkinParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public ImageFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_imageFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitImageFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImageFunctionContext imageFunction() throws RecognitionException {
		ImageFunctionContext _localctx = new ImageFunctionContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_imageFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(506);
			match(IMAGE_NAME);
			setState(507);
			match(LPAREN);
			setState(508);
			match(STRING);
			setState(509);
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
		enterRule(_localctx, 80, RULE_bool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(511);
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
		enterRule(_localctx, 82, RULE_logical_entity);
		try {
			setState(517);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
				_localctx = new LogicalConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(513);
				bool();
				}
				break;
			case BOOLEAN_VARIABLE:
				_localctx = new LogicalVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(514);
				match(BOOLEAN_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new LogicalPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(515);
				match(PLACEHOLDER);
				}
				break;
			case MATCHES_NAME:
			case XPATH_NAME:
			case IMAGE_NAME:
				_localctx = new LogicalFunctionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(516);
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
		enterRule(_localctx, 84, RULE_numeric_entity);
		try {
			setState(523);
			switch (_input.LA(1)) {
			case INTEGER_NUMBER:
				_localctx = new IntegerConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(519);
				match(INTEGER_NUMBER);
				}
				break;
			case DECIMAL_NUMBER:
				_localctx = new DecimalConstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(520);
				match(DECIMAL_NUMBER);
				}
				break;
			case NUMBER_VARIABLE:
				_localctx = new NumericVariableContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(521);
				match(NUMBER_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new NumericPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(522);
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
		enterRule(_localctx, 86, RULE_string_entity);
		try {
			setState(528);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(525);
				match(STRING);
				}
				break;
			case STRING_VARIABLE:
				_localctx = new StringVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(526);
				match(STRING_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new StringPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(527);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3B\u0215\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\3\2\5\2\\\n\2\3\2\6\2_\n\2\r\2\16\2`\3\2\3\2\3\3\3\3\5\3g\n"+
		"\3\3\3\3\3\5\3k\n\3\5\3m\n\3\3\4\3\4\6\4q\n\4\r\4\16\4r\3\4\7\4v\n\4\f"+
		"\4\16\4y\13\4\3\5\3\5\6\5}\n\5\r\5\16\5~\3\5\7\5\u0082\n\5\f\5\16\5\u0085"+
		"\13\5\3\6\7\6\u0088\n\6\f\6\16\6\u008b\13\6\3\6\3\6\3\6\6\6\u0090\n\6"+
		"\r\6\16\6\u0091\3\6\7\6\u0095\n\6\f\6\16\6\u0098\13\6\3\6\5\6\u009b\n"+
		"\6\3\6\5\6\u009e\n\6\3\6\5\6\u00a1\n\6\3\6\6\6\u00a4\n\6\r\6\16\6\u00a5"+
		"\3\7\3\7\5\7\u00aa\n\7\3\7\6\7\u00ad\n\7\r\7\16\7\u00ae\3\7\7\7\u00b2"+
		"\n\7\f\7\16\7\u00b5\13\7\3\7\5\7\u00b8\n\7\3\7\5\7\u00bb\n\7\3\7\6\7\u00be"+
		"\n\7\r\7\16\7\u00bf\3\b\3\b\5\b\u00c4\n\b\3\t\7\t\u00c7\n\t\f\t\16\t\u00ca"+
		"\13\t\3\t\3\t\3\t\6\t\u00cf\n\t\r\t\16\t\u00d0\3\t\7\t\u00d4\n\t\f\t\16"+
		"\t\u00d7\13\t\3\t\5\t\u00da\n\t\3\t\5\t\u00dd\n\t\3\t\6\t\u00e0\n\t\r"+
		"\t\16\t\u00e1\3\n\7\n\u00e5\n\n\f\n\16\n\u00e8\13\n\3\n\3\n\3\n\6\n\u00ed"+
		"\n\n\r\n\16\n\u00ee\3\n\7\n\u00f2\n\n\f\n\16\n\u00f5\13\n\3\n\5\n\u00f8"+
		"\n\n\3\n\5\n\u00fb\n\n\3\n\6\n\u00fe\n\n\r\n\16\n\u00ff\3\n\3\n\3\13\3"+
		"\13\5\13\u0106\n\13\3\13\6\13\u0109\n\13\r\13\16\13\u010a\3\13\7\13\u010e"+
		"\n\13\f\13\16\13\u0111\13\13\3\13\3\13\3\f\6\f\u0116\n\f\r\f\16\f\u0117"+
		"\3\f\7\f\u011b\n\f\f\f\16\f\u011e\13\f\3\r\7\r\u0121\n\r\f\r\16\r\u0124"+
		"\13\r\3\r\6\r\u0127\n\r\r\r\16\r\u0128\3\16\3\16\7\16\u012d\n\16\f\16"+
		"\16\16\u0130\13\16\3\16\6\16\u0133\n\16\r\16\16\16\u0134\3\17\3\17\7\17"+
		"\u0139\n\17\f\17\16\17\u013c\13\17\3\20\3\20\6\20\u0140\n\20\r\20\16\20"+
		"\u0141\3\21\3\21\3\21\3\22\3\22\3\22\6\22\u014a\n\22\r\22\16\22\u014b"+
		"\3\22\5\22\u014f\n\22\3\22\5\22\u0152\n\22\3\22\3\22\5\22\u0156\n\22\3"+
		"\23\3\23\3\23\3\24\3\24\6\24\u015d\n\24\r\24\16\24\u015e\3\25\3\25\3\25"+
		"\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\7\27"+
		"\u0171\n\27\f\27\16\27\u0174\13\27\3\30\5\30\u0177\n\30\3\30\3\30\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\5\31\u0181\n\31\3\32\3\32\3\32\5\32\u0186\n"+
		"\32\3\32\3\32\3\33\3\33\3\33\5\33\u018d\n\33\3\33\3\33\3\34\3\34\3\34"+
		"\5\34\u0194\n\34\3\34\3\34\3\35\3\35\3\35\5\35\u019b\n\35\3\35\3\35\3"+
		"\36\3\36\3\36\5\36\u01a2\n\36\3\36\3\36\3\37\3\37\3 \3 \3 \3 \3!\3!\3"+
		"!\3!\3!\3!\3!\3!\3!\5!\u01b5\n!\3!\3!\3!\3!\3!\3!\7!\u01bd\n!\f!\16!\u01c0"+
		"\13!\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\5\"\u01ce\n\"\3#"+
		"\3#\3$\3$\3$\3$\3$\3$\3$\3$\5$\u01da\n$\3$\3$\3$\3$\3$\3$\3$\3$\3$\7$"+
		"\u01e5\n$\f$\16$\u01e8\13$\3%\3%\3&\3&\3&\5&\u01ef\n&\3\'\3\'\3\'\3\'"+
		"\3\'\3\'\3\'\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3*\3*\3+\3+\3+\3+\5+\u0208"+
		"\n+\3,\3,\3,\3,\5,\u020e\n,\3-\3-\3-\5-\u0213\n-\3-\2\5,@F.\2\4\6\b\n"+
		"\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVX\2"+
		"\13\3\2==\4\2\5\24==\3\2\27\30\4\2\27\27\35\36\4\2\63\63\679\3\2%*\3\2"+
		" \"\3\2#$\3\2\35\36\u023e\2[\3\2\2\2\4l\3\2\2\2\6n\3\2\2\2\bz\3\2\2\2"+
		"\n\u0089\3\2\2\2\f\u00a7\3\2\2\2\16\u00c3\3\2\2\2\20\u00c8\3\2\2\2\22"+
		"\u00e6\3\2\2\2\24\u0103\3\2\2\2\26\u0115\3\2\2\2\30\u0122\3\2\2\2\32\u012a"+
		"\3\2\2\2\34\u0136\3\2\2\2\36\u013d\3\2\2\2 \u0143\3\2\2\2\"\u0146\3\2"+
		"\2\2$\u0157\3\2\2\2&\u015a\3\2\2\2(\u0160\3\2\2\2*\u0163\3\2\2\2,\u0167"+
		"\3\2\2\2.\u0176\3\2\2\2\60\u0180\3\2\2\2\62\u0182\3\2\2\2\64\u0189\3\2"+
		"\2\2\66\u0190\3\2\2\28\u0197\3\2\2\2:\u019e\3\2\2\2<\u01a5\3\2\2\2>\u01a7"+
		"\3\2\2\2@\u01b4\3\2\2\2B\u01cd\3\2\2\2D\u01cf\3\2\2\2F\u01d9\3\2\2\2H"+
		"\u01e9\3\2\2\2J\u01ee\3\2\2\2L\u01f0\3\2\2\2N\u01f7\3\2\2\2P\u01fc\3\2"+
		"\2\2R\u0201\3\2\2\2T\u0207\3\2\2\2V\u020d\3\2\2\2X\u0212\3\2\2\2Z\\\5"+
		"\4\3\2[Z\3\2\2\2[\\\3\2\2\2\\^\3\2\2\2]_\5\n\6\2^]\3\2\2\2_`\3\2\2\2`"+
		"^\3\2\2\2`a\3\2\2\2ab\3\2\2\2bc\7\2\2\3c\3\3\2\2\2df\5\6\4\2eg\5\b\5\2"+
		"fe\3\2\2\2fg\3\2\2\2gm\3\2\2\2hj\5\b\5\2ik\5\6\4\2ji\3\2\2\2jk\3\2\2\2"+
		"km\3\2\2\2ld\3\2\2\2lh\3\2\2\2m\5\3\2\2\2np\7\4\2\2oq\7\5\2\2po\3\2\2"+
		"\2qr\3\2\2\2rp\3\2\2\2rs\3\2\2\2sw\3\2\2\2tv\7=\2\2ut\3\2\2\2vy\3\2\2"+
		"\2wu\3\2\2\2wx\3\2\2\2x\7\3\2\2\2yw\3\2\2\2z|\7\3\2\2{}\7\5\2\2|{\3\2"+
		"\2\2}~\3\2\2\2~|\3\2\2\2~\177\3\2\2\2\177\u0083\3\2\2\2\u0080\u0082\7"+
		"=\2\2\u0081\u0080\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083"+
		"\u0084\3\2\2\2\u0084\t\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0088\5\34\17"+
		"\2\u0087\u0086\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a"+
		"\3\2\2\2\u008a\u008c\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008d\7\6\2\2\u008d"+
		"\u008f\5\30\r\2\u008e\u0090\7=\2\2\u008f\u008e\3\2\2\2\u0090\u0091\3\2"+
		"\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0096\3\2\2\2\u0093"+
		"\u0095\5\32\16\2\u0094\u0093\3\2\2\2\u0095\u0098\3\2\2\2\u0096\u0094\3"+
		"\2\2\2\u0096\u0097\3\2\2\2\u0097\u009a\3\2\2\2\u0098\u0096\3\2\2\2\u0099"+
		"\u009b\5\36\20\2\u009a\u0099\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009d\3"+
		"\2\2\2\u009c\u009e\5 \21\2\u009d\u009c\3\2\2\2\u009d\u009e\3\2\2\2\u009e"+
		"\u00a0\3\2\2\2\u009f\u00a1\5\f\7\2\u00a0\u009f\3\2\2\2\u00a0\u00a1\3\2"+
		"\2\2\u00a1\u00a3\3\2\2\2\u00a2\u00a4\5\16\b\2\u00a3\u00a2\3\2\2\2\u00a4"+
		"\u00a5\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\13\3\2\2"+
		"\2\u00a7\u00a9\7\7\2\2\u00a8\u00aa\5\30\r\2\u00a9\u00a8\3\2\2\2\u00a9"+
		"\u00aa\3\2\2\2\u00aa\u00ac\3\2\2\2\u00ab\u00ad\7=\2\2\u00ac\u00ab\3\2"+
		"\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"\u00b3\3\2\2\2\u00b0\u00b2\5\32\16\2\u00b1\u00b0\3\2\2\2\u00b2\u00b5\3"+
		"\2\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b7\3\2\2\2\u00b5"+
		"\u00b3\3\2\2\2\u00b6\u00b8\5\36\20\2\u00b7\u00b6\3\2\2\2\u00b7\u00b8\3"+
		"\2\2\2\u00b8\u00ba\3\2\2\2\u00b9\u00bb\5 \21\2\u00ba\u00b9\3\2\2\2\u00ba"+
		"\u00bb\3\2\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00be\5\"\22\2\u00bd\u00bc\3"+
		"\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0"+
		"\r\3\2\2\2\u00c1\u00c4\5\20\t\2\u00c2\u00c4\5\22\n\2\u00c3\u00c1\3\2\2"+
		"\2\u00c3\u00c2\3\2\2\2\u00c4\17\3\2\2\2\u00c5\u00c7\5\34\17\2\u00c6\u00c5"+
		"\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9"+
		"\u00cb\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb\u00cc\7\b\2\2\u00cc\u00ce\5\30"+
		"\r\2\u00cd\u00cf\7=\2\2\u00ce\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0"+
		"\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d5\3\2\2\2\u00d2\u00d4\5\32"+
		"\16\2\u00d3\u00d2\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d5"+
		"\u00d6\3\2\2\2\u00d6\u00d9\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d8\u00da\5\36"+
		"\20\2\u00d9\u00d8\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00dc\3\2\2\2\u00db"+
		"\u00dd\5 \21\2\u00dc\u00db\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00df\3\2"+
		"\2\2\u00de\u00e0\5\"\22\2\u00df\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\21\3\2\2\2\u00e3\u00e5\5\34\17"+
		"\2\u00e4\u00e3\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e7"+
		"\3\2\2\2\u00e7\u00e9\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e9\u00ea\7\t\2\2\u00ea"+
		"\u00ec\5\30\r\2\u00eb\u00ed\7=\2\2\u00ec\u00eb\3\2\2\2\u00ed\u00ee\3\2"+
		"\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f3\3\2\2\2\u00f0"+
		"\u00f2\5\32\16\2\u00f1\u00f0\3\2\2\2\u00f2\u00f5\3\2\2\2\u00f3\u00f1\3"+
		"\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f6"+
		"\u00f8\5\36\20\2\u00f7\u00f6\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00fa\3"+
		"\2\2\2\u00f9\u00fb\5 \21\2\u00fa\u00f9\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb"+
		"\u00fd\3\2\2\2\u00fc\u00fe\5\"\22\2\u00fd\u00fc\3\2\2\2\u00fe\u00ff\3"+
		"\2\2\2\u00ff\u00fd\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\u0101\3\2\2\2\u0101"+
		"\u0102\5\24\13\2\u0102\23\3\2\2\2\u0103\u0105\7\n\2\2\u0104\u0106\5\30"+
		"\r\2\u0105\u0104\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0108\3\2\2\2\u0107"+
		"\u0109\7=\2\2\u0108\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a\u0108\3\2"+
		"\2\2\u010a\u010b\3\2\2\2\u010b\u010f\3\2\2\2\u010c\u010e\5\32\16\2\u010d"+
		"\u010c\3\2\2\2\u010e\u0111\3\2\2\2\u010f\u010d\3\2\2\2\u010f\u0110\3\2"+
		"\2\2\u0110\u0112\3\2\2\2\u0111\u010f\3\2\2\2\u0112\u0113\5\26\f\2\u0113"+
		"\25\3\2\2\2\u0114\u0116\7\24\2\2\u0115\u0114\3\2\2\2\u0116\u0117\3\2\2"+
		"\2\u0117\u0115\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u011c\3\2\2\2\u0119\u011b"+
		"\7=\2\2\u011a\u0119\3\2\2\2\u011b\u011e\3\2\2\2\u011c\u011a\3\2\2\2\u011c"+
		"\u011d\3\2\2\2\u011d\27\3\2\2\2\u011e\u011c\3\2\2\2\u011f\u0121\7>\2\2"+
		"\u0120\u011f\3\2\2\2\u0121\u0124\3\2\2\2\u0122\u0120\3\2\2\2\u0122\u0123"+
		"\3\2\2\2\u0123\u0126\3\2\2\2\u0124\u0122\3\2\2\2\u0125\u0127\n\2\2\2\u0126"+
		"\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0126\3\2\2\2\u0128\u0129\3\2"+
		"\2\2\u0129\31\3\2\2\2\u012a\u012e\n\3\2\2\u012b\u012d\n\2\2\2\u012c\u012b"+
		"\3\2\2\2\u012d\u0130\3\2\2\2\u012e\u012c\3\2\2\2\u012e\u012f\3\2\2\2\u012f"+
		"\u0132\3\2\2\2\u0130\u012e\3\2\2\2\u0131\u0133\7=\2\2\u0132\u0131\3\2"+
		"\2\2\u0133\u0134\3\2\2\2\u0134\u0132\3\2\2\2\u0134\u0135\3\2\2\2\u0135"+
		"\33\3\2\2\2\u0136\u013a\7\5\2\2\u0137\u0139\7=\2\2\u0138\u0137\3\2\2\2"+
		"\u0139\u013c\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013b\35"+
		"\3\2\2\2\u013c\u013a\3\2\2\2\u013d\u013f\7\13\2\2\u013e\u0140\5.\30\2"+
		"\u013f\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u013f\3\2\2\2\u0141\u0142"+
		"\3\2\2\2\u0142\37\3\2\2\2\u0143\u0144\7\f\2\2\u0144\u0145\5,\27\2\u0145"+
		"!\3\2\2\2\u0146\u0147\7\r\2\2\u0147\u0149\5\30\r\2\u0148\u014a\7=\2\2"+
		"\u0149\u0148\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u0149\3\2\2\2\u014b\u014c"+
		"\3\2\2\2\u014c\u014e\3\2\2\2\u014d\u014f\5*\26\2\u014e\u014d\3\2\2\2\u014e"+
		"\u014f\3\2\2\2\u014f\u0151\3\2\2\2\u0150\u0152\5$\23\2\u0151\u0150\3\2"+
		"\2\2\u0151\u0152\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0155\5&\24\2\u0154"+
		"\u0156\5(\25\2\u0155\u0154\3\2\2\2\u0155\u0156\3\2\2\2\u0156#\3\2\2\2"+
		"\u0157\u0158\7\17\2\2\u0158\u0159\5,\27\2\u0159%\3\2\2\2\u015a\u015c\7"+
		"\20\2\2\u015b\u015d\5.\30\2\u015c\u015b\3\2\2\2\u015d\u015e\3\2\2\2\u015e"+
		"\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015f\'\3\2\2\2\u0160\u0161\7\21\2"+
		"\2\u0161\u0162\5,\27\2\u0162)\3\2\2\2\u0163\u0164\7\16\2\2\u0164\u0165"+
		"\7\26\2\2\u0165\u0166\7\26\2\2\u0166+\3\2\2\2\u0167\u0168\b\27\1\2\u0168"+
		"\u0169\5@!\2\u0169\u0172\3\2\2\2\u016a\u016b\f\4\2\2\u016b\u016c\7\22"+
		"\2\2\u016c\u0171\5,\27\5\u016d\u016e\f\3\2\2\u016e\u016f\7\23\2\2\u016f"+
		"\u0171\5,\27\4\u0170\u016a\3\2\2\2\u0170\u016d\3\2\2\2\u0171\u0174\3\2"+
		"\2\2\u0172\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173-\3\2\2\2\u0174\u0172"+
		"\3\2\2\2\u0175\u0177\5@!\2\u0176\u0175\3\2\2\2\u0176\u0177\3\2\2\2\u0177"+
		"\u0178\3\2\2\2\u0178\u0179\5\60\31\2\u0179/\3\2\2\2\u017a\u0181\5> \2"+
		"\u017b\u0181\5\62\32\2\u017c\u0181\5\64\33\2\u017d\u0181\5\66\34\2\u017e"+
		"\u0181\58\35\2\u017f\u0181\5:\36\2\u0180\u017a\3\2\2\2\u0180\u017b\3\2"+
		"\2\2\u0180\u017c\3\2\2\2\u0180\u017d\3\2\2\2\u0180\u017e\3\2\2\2\u0180"+
		"\u017f\3\2\2\2\u0181\61\3\2\2\2\u0182\u0183\7\62\2\2\u0183\u0185\7+\2"+
		"\2\u0184\u0186\t\4\2\2\u0185\u0184\3\2\2\2\u0185\u0186\3\2\2\2\u0186\u0187"+
		"\3\2\2\2\u0187\u0188\7,\2\2\u0188\63\3\2\2\2\u0189\u018a\7\61\2\2\u018a"+
		"\u018c\7+\2\2\u018b\u018d\t\5\2\2\u018c\u018b\3\2\2\2\u018c\u018d\3\2"+
		"\2\2\u018d\u018e\3\2\2\2\u018e\u018f\7,\2\2\u018f\65\3\2\2\2\u0190\u0191"+
		"\7\65\2\2\u0191\u0193\7+\2\2\u0192\u0194\t\5\2\2\u0193\u0192\3\2\2\2\u0193"+
		"\u0194\3\2\2\2\u0194\u0195\3\2\2\2\u0195\u0196\7,\2\2\u0196\67\3\2\2\2"+
		"\u0197\u0198\7\66\2\2\u0198\u019a\7+\2\2\u0199\u019b\t\5\2\2\u019a\u0199"+
		"\3\2\2\2\u019a\u019b\3\2\2\2\u019b\u019c\3\2\2\2\u019c\u019d\7,\2\2\u019d"+
		"9\3\2\2\2\u019e\u019f\7\64\2\2\u019f\u01a1\7+\2\2\u01a0\u01a2\t\5\2\2"+
		"\u01a1\u01a0\3\2\2\2\u01a1\u01a2\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01a4"+
		"\7,\2\2\u01a4;\3\2\2\2\u01a5\u01a6\t\6\2\2\u01a6=\3\2\2\2\u01a7\u01a8"+
		"\5<\37\2\u01a8\u01a9\7+\2\2\u01a9\u01aa\7,\2\2\u01aa?\3\2\2\2\u01ab\u01ac"+
		"\b!\1\2\u01ac\u01ad\7\34\2\2\u01ad\u01b5\5@!\6\u01ae\u01b5\5T+\2\u01af"+
		"\u01b0\7+\2\2\u01b0\u01b1\5@!\2\u01b1\u01b2\7,\2\2\u01b2\u01b5\3\2\2\2"+
		"\u01b3\u01b5\5B\"\2\u01b4\u01ab\3\2\2\2\u01b4\u01ae\3\2\2\2\u01b4\u01af"+
		"\3\2\2\2\u01b4\u01b3\3\2\2\2\u01b5\u01be\3\2\2\2\u01b6\u01b7\f\4\2\2\u01b7"+
		"\u01b8\7\32\2\2\u01b8\u01bd\5@!\5\u01b9\u01ba\f\3\2\2\u01ba\u01bb\7\33"+
		"\2\2\u01bb\u01bd\5@!\4\u01bc\u01b6\3\2\2\2\u01bc\u01b9\3\2\2\2\u01bd\u01c0"+
		"\3\2\2\2\u01be\u01bc\3\2\2\2\u01be\u01bf\3\2\2\2\u01bfA\3\2\2\2\u01c0"+
		"\u01be\3\2\2\2\u01c1\u01c2\5F$\2\u01c2\u01c3\5D#\2\u01c3\u01c4\5F$\2\u01c4"+
		"\u01ce\3\2\2\2\u01c5\u01c6\5H%\2\u01c6\u01c7\5D#\2\u01c7\u01c8\5H%\2\u01c8"+
		"\u01ce\3\2\2\2\u01c9\u01ca\7+\2\2\u01ca\u01cb\5B\"\2\u01cb\u01cc\7,\2"+
		"\2\u01cc\u01ce\3\2\2\2\u01cd\u01c1\3\2\2\2\u01cd\u01c5\3\2\2\2\u01cd\u01c9"+
		"\3\2\2\2\u01ceC\3\2\2\2\u01cf\u01d0\t\7\2\2\u01d0E\3\2\2\2\u01d1\u01d2"+
		"\b$\1\2\u01d2\u01d3\7$\2\2\u01d3\u01da\5F$\6\u01d4\u01da\5V,\2\u01d5\u01d6"+
		"\7+\2\2\u01d6\u01d7\5F$\2\u01d7\u01d8\7,\2\2\u01d8\u01da\3\2\2\2\u01d9"+
		"\u01d1\3\2\2\2\u01d9\u01d4\3\2\2\2\u01d9\u01d5\3\2\2\2\u01da\u01e6\3\2"+
		"\2\2\u01db\u01dc\f\5\2\2\u01dc\u01dd\7\37\2\2\u01dd\u01e5\5F$\6\u01de"+
		"\u01df\f\4\2\2\u01df\u01e0\t\b\2\2\u01e0\u01e5\5F$\5\u01e1\u01e2\f\3\2"+
		"\2\u01e2\u01e3\t\t\2\2\u01e3\u01e5\5F$\4\u01e4\u01db\3\2\2\2\u01e4\u01de"+
		"\3\2\2\2\u01e4\u01e1\3\2\2\2\u01e5\u01e8\3\2\2\2\u01e6\u01e4\3\2\2\2\u01e6"+
		"\u01e7\3\2\2\2\u01e7G\3\2\2\2\u01e8\u01e6\3\2\2\2\u01e9\u01ea\5X-\2\u01ea"+
		"I\3\2\2\2\u01eb\u01ef\5L\'\2\u01ec\u01ef\5N(\2\u01ed\u01ef\5P)\2\u01ee"+
		"\u01eb\3\2\2\2\u01ee\u01ec\3\2\2\2\u01ee\u01ed\3\2\2\2\u01efK\3\2\2\2"+
		"\u01f0\u01f1\7.\2\2\u01f1\u01f2\7+\2\2\u01f2\u01f3\7<\2\2\u01f3\u01f4"+
		"\7-\2\2\u01f4\u01f5\7\30\2\2\u01f5\u01f6\7,\2\2\u01f6M\3\2\2\2\u01f7\u01f8"+
		"\7/\2\2\u01f8\u01f9\7+\2\2\u01f9\u01fa\7\30\2\2\u01fa\u01fb\7,\2\2\u01fb"+
		"O\3\2\2\2\u01fc\u01fd\7\60\2\2\u01fd\u01fe\7+\2\2\u01fe\u01ff\7\30\2\2"+
		"\u01ff\u0200\7,\2\2\u0200Q\3\2\2\2\u0201\u0202\t\n\2\2\u0202S\3\2\2\2"+
		"\u0203\u0208\5R*\2\u0204\u0208\7:\2\2\u0205\u0208\7\27\2\2\u0206\u0208"+
		"\5J&\2\u0207\u0203\3\2\2\2\u0207\u0204\3\2\2\2\u0207\u0205\3\2\2\2\u0207"+
		"\u0206\3\2\2\2\u0208U\3\2\2\2\u0209\u020e\7\26\2\2\u020a\u020e\7\25\2"+
		"\2\u020b\u020e\7;\2\2\u020c\u020e\7\27\2\2\u020d\u0209\3\2\2\2\u020d\u020a"+
		"\3\2\2\2\u020d\u020b\3\2\2\2\u020d\u020c\3\2\2\2\u020eW\3\2\2\2\u020f"+
		"\u0213\7\30\2\2\u0210\u0213\7<\2\2\u0211\u0213\7\27\2\2\u0212\u020f\3"+
		"\2\2\2\u0212\u0210\3\2\2\2\u0212\u0211\3\2\2\2\u0213Y\3\2\2\2I[`fjlrw"+
		"~\u0083\u0089\u0091\u0096\u009a\u009d\u00a0\u00a5\u00a9\u00ae\u00b3\u00b7"+
		"\u00ba\u00bf\u00c3\u00c8\u00d0\u00d5\u00d9\u00dc\u00e1\u00e6\u00ee\u00f3"+
		"\u00f7\u00fa\u00ff\u0105\u010a\u010f\u0117\u011c\u0122\u0128\u012e\u0134"+
		"\u013a\u0141\u014b\u014e\u0151\u0155\u015e\u0170\u0172\u0176\u0180\u0185"+
		"\u018c\u0193\u019a\u01a1\u01b4\u01bc\u01be\u01cd\u01d9\u01e4\u01e6\u01ee"+
		"\u0207\u020d\u0212";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
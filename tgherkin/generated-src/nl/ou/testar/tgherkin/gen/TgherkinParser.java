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
		STEP_RANGE_KEYWORD=12, STEP_WHILE_KEYWORD=13, STEP_REPEAT_KEYWORD=14, 
		STEP_UNTIL_KEYWORD=15, STEP_GIVEN_KEYWORD=16, STEP_WHEN_KEYWORD=17, STEP_THEN_KEYWORD=18, 
		STEP_ALSO_KEYWORD=19, STEP_EITHER_KEYWORD=20, TABLE_ROW=21, DECIMAL_NUMBER=22, 
		INTEGER_NUMBER=23, PLACEHOLDER=24, STRING=25, COMMENT=26, AND=27, OR=28, 
		NOT=29, TRUE=30, FALSE=31, POW=32, MULT=33, DIV=34, MOD=35, PLUS=36, MINUS=37, 
		GT=38, GE=39, LT=40, LE=41, EQ=42, NE=43, LPAREN=44, RPAREN=45, COMMA=46, 
		MATCHES_NAME=47, XPATH_NAME=48, XPATH_BOOLEAN_NAME=49, XPATH_NUMBER_NAME=50, 
		XPATH_STRING_NAME=51, IMAGE_NAME=52, OCR_NAME=53, STATE_NAME=54, CLICK_NAME=55, 
		TYPE_NAME=56, DRAG_SLIDER_NAME=57, ANY_NAME=58, DOUBLE_CLICK_NAME=59, 
		TRIPLE_CLICK_NAME=60, RIGHT_CLICK_NAME=61, MOUSE_MOVE_NAME=62, DROP_DOWN_AT_NAME=63, 
		HIT_KEY_NAME=64, DRAG_DROP_NAME=65, BOOLEAN_VARIABLE=66, NUMBER_VARIABLE=67, 
		STRING_VARIABLE=68, EOL=69, WS=70, OTHER=71, BOOLEAN_VARIABLE_NAME=72, 
		NUMBER_VARIABLE_NAME=73, STRING_VARIABLE_NAME=74, KB_KEY_NAME=75;
	public static final int
		RULE_document = 0, RULE_execOptions = 1, RULE_execOptionExclude = 2, RULE_execOptionInclude = 3, 
		RULE_feature = 4, RULE_background = 5, RULE_scenarioDefiniton = 6, RULE_scenario = 7, 
		RULE_scenarioOutline = 8, RULE_examples = 9, RULE_table = 10, RULE_title = 11, 
		RULE_narrativeLine = 12, RULE_tagname = 13, RULE_selection = 14, RULE_oracle = 15, 
		RULE_step = 16, RULE_givenClause = 17, RULE_whenClause = 18, RULE_thenClause = 19, 
		RULE_stepIteration = 20, RULE_stepRange = 21, RULE_stepWhile = 22, RULE_stepRepeatUntil = 23, 
		RULE_conditional_gesture = 24, RULE_gesture = 25, RULE_typeGesture = 26, 
		RULE_clickGesture = 27, RULE_doubleClickGesture = 28, RULE_tripleClickGesture = 29, 
		RULE_anyGesture = 30, RULE_hitKeyGesture = 31, RULE_hitKeyArgument = 32, 
		RULE_dragDropGesture = 33, RULE_gestureName = 34, RULE_parameterlessGesture = 35, 
		RULE_widget_condition = 36, RULE_relational_expr = 37, RULE_relational_operator = 38, 
		RULE_arithmetic_expr = 39, RULE_string_expr = 40, RULE_booleanFunction = 41, 
		RULE_stringFunction = 42, RULE_numericFunction = 43, RULE_matchesFunction = 44, 
		RULE_xpathFunction = 45, RULE_xpathBooleanFunction = 46, RULE_xpathNumberFunction = 47, 
		RULE_xpathStringFunction = 48, RULE_imageFunction = 49, RULE_ocrFunction = 50, 
		RULE_stateFunction = 51, RULE_widget_tree_condition = 52, RULE_bool = 53, 
		RULE_logical_entity = 54, RULE_numeric_entity = 55, RULE_string_entity = 56;
	public static final String[] ruleNames = {
		"document", "execOptions", "execOptionExclude", "execOptionInclude", "feature", 
		"background", "scenarioDefiniton", "scenario", "scenarioOutline", "examples", 
		"table", "title", "narrativeLine", "tagname", "selection", "oracle", "step", 
		"givenClause", "whenClause", "thenClause", "stepIteration", "stepRange", 
		"stepWhile", "stepRepeatUntil", "conditional_gesture", "gesture", "typeGesture", 
		"clickGesture", "doubleClickGesture", "tripleClickGesture", "anyGesture", 
		"hitKeyGesture", "hitKeyArgument", "dragDropGesture", "gestureName", "parameterlessGesture", 
		"widget_condition", "relational_expr", "relational_operator", "arithmetic_expr", 
		"string_expr", "booleanFunction", "stringFunction", "numericFunction", 
		"matchesFunction", "xpathFunction", "xpathBooleanFunction", "xpathNumberFunction", 
		"xpathStringFunction", "imageFunction", "ocrFunction", "stateFunction", 
		"widget_tree_condition", "bool", "logical_entity", "numeric_entity", "string_entity"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'include:'", "'exclude:'", null, "'Feature:'", "'Background:'", 
		"'Scenario:'", "'Scenario Outline:'", "'Examples:'", "'Selection:'", "'Oracle:'", 
		"'Step:'", "'Range'", "'While'", "'Repeat'", "'until'", "'Given'", "'When'", 
		"'Then'", "'Also'", "'Either'", null, null, null, null, null, null, "'and'", 
		"'or'", null, "'true'", "'false'", "'^'", "'*'", "'/'", "'%'", "'+'", 
		"'-'", "'>'", "'>='", "'<'", "'<='", "'='", null, "'('", "')'", "','", 
		"'matches'", "'xpath'", "'xpathBoolean'", "'xpathNumber'", "'xpathString'", 
		"'image'", "'ocr'", "'state'", "'click'", "'type'", "'dragSlider'", "'anyGesture'", 
		"'doubleClick'", "'tripleClick'", "'rightClick'", "'mouseMove'", "'dropDownAt'", 
		"'hitKey'", "'dragDrop'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "OPTION_KEYWORD_INCLUDE", "OPTION_KEYWORD_EXCLUDE", "TAGNAME", "FEATURE_KEYWORD", 
		"BACKGROUND_KEYWORD", "SCENARIO_KEYWORD", "SCENARIO_OUTLINE_KEYWORD", 
		"EXAMPLES_KEYWORD", "SELECTION_KEYWORD", "ORACLE_KEYWORD", "STEP_KEYWORD", 
		"STEP_RANGE_KEYWORD", "STEP_WHILE_KEYWORD", "STEP_REPEAT_KEYWORD", "STEP_UNTIL_KEYWORD", 
		"STEP_GIVEN_KEYWORD", "STEP_WHEN_KEYWORD", "STEP_THEN_KEYWORD", "STEP_ALSO_KEYWORD", 
		"STEP_EITHER_KEYWORD", "TABLE_ROW", "DECIMAL_NUMBER", "INTEGER_NUMBER", 
		"PLACEHOLDER", "STRING", "COMMENT", "AND", "OR", "NOT", "TRUE", "FALSE", 
		"POW", "MULT", "DIV", "MOD", "PLUS", "MINUS", "GT", "GE", "LT", "LE", 
		"EQ", "NE", "LPAREN", "RPAREN", "COMMA", "MATCHES_NAME", "XPATH_NAME", 
		"XPATH_BOOLEAN_NAME", "XPATH_NUMBER_NAME", "XPATH_STRING_NAME", "IMAGE_NAME", 
		"OCR_NAME", "STATE_NAME", "CLICK_NAME", "TYPE_NAME", "DRAG_SLIDER_NAME", 
		"ANY_NAME", "DOUBLE_CLICK_NAME", "TRIPLE_CLICK_NAME", "RIGHT_CLICK_NAME", 
		"MOUSE_MOVE_NAME", "DROP_DOWN_AT_NAME", "HIT_KEY_NAME", "DRAG_DROP_NAME", 
		"BOOLEAN_VARIABLE", "NUMBER_VARIABLE", "STRING_VARIABLE", "EOL", "WS", 
		"OTHER", "BOOLEAN_VARIABLE_NAME", "NUMBER_VARIABLE_NAME", "STRING_VARIABLE_NAME", 
		"KB_KEY_NAME"
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
			setState(115);
			_la = _input.LA(1);
			if (_la==OPTION_KEYWORD_INCLUDE || _la==OPTION_KEYWORD_EXCLUDE) {
				{
				setState(114);
				execOptions();
				}
			}

			setState(118); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(117);
				feature();
				}
				}
				setState(120); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TAGNAME || _la==FEATURE_KEYWORD );
			setState(122);
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
			setState(132);
			switch (_input.LA(1)) {
			case OPTION_KEYWORD_EXCLUDE:
				enterOuterAlt(_localctx, 1);
				{
				setState(124);
				execOptionExclude();
				setState(126);
				_la = _input.LA(1);
				if (_la==OPTION_KEYWORD_INCLUDE) {
					{
					setState(125);
					execOptionInclude();
					}
				}

				}
				break;
			case OPTION_KEYWORD_INCLUDE:
				enterOuterAlt(_localctx, 2);
				{
				setState(128);
				execOptionInclude();
				setState(130);
				_la = _input.LA(1);
				if (_la==OPTION_KEYWORD_EXCLUDE) {
					{
					setState(129);
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
			setState(134);
			match(OPTION_KEYWORD_EXCLUDE);
			setState(136); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(135);
					((ExecOptionExcludeContext)_localctx).TAGNAME = match(TAGNAME);
					((ExecOptionExcludeContext)_localctx).tags.add(((ExecOptionExcludeContext)_localctx).TAGNAME);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(138); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(140);
				match(EOL);
				}
				}
				setState(145);
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
			setState(146);
			match(OPTION_KEYWORD_INCLUDE);
			setState(148); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(147);
					((ExecOptionIncludeContext)_localctx).TAGNAME = match(TAGNAME);
					((ExecOptionIncludeContext)_localctx).tags.add(((ExecOptionIncludeContext)_localctx).TAGNAME);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(150); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(152);
				match(EOL);
				}
				}
				setState(157);
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
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(158);
				((FeatureContext)_localctx).tagname = tagname();
				((FeatureContext)_localctx).tags.add(((FeatureContext)_localctx).tagname);
				}
				}
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(164);
			match(FEATURE_KEYWORD);
			setState(165);
			title();
			setState(167); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(166);
				match(EOL);
				}
				}
				setState(169); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD) | (1L << STEP_UNTIL_KEYWORD) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << XPATH_BOOLEAN_NAME) | (1L << XPATH_NUMBER_NAME) | (1L << XPATH_STRING_NAME) | (1L << IMAGE_NAME) | (1L << OCR_NAME) | (1L << STATE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_SLIDER_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (HIT_KEY_NAME - 64)) | (1L << (DRAG_DROP_NAME - 64)) | (1L << (BOOLEAN_VARIABLE - 64)) | (1L << (NUMBER_VARIABLE - 64)) | (1L << (STRING_VARIABLE - 64)) | (1L << (WS - 64)) | (1L << (OTHER - 64)) | (1L << (BOOLEAN_VARIABLE_NAME - 64)) | (1L << (NUMBER_VARIABLE_NAME - 64)) | (1L << (STRING_VARIABLE_NAME - 64)) | (1L << (KB_KEY_NAME - 64)))) != 0)) {
				{
				{
				setState(171);
				((FeatureContext)_localctx).narrativeLine = narrativeLine();
				((FeatureContext)_localctx).narrative.add(((FeatureContext)_localctx).narrativeLine);
				}
				}
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(178);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(177);
				selection();
				}
			}

			setState(181);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(180);
				oracle();
				}
			}

			setState(184);
			_la = _input.LA(1);
			if (_la==BACKGROUND_KEYWORD) {
				{
				setState(183);
				background();
				}
			}

			setState(187); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(186);
					scenarioDefiniton();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(189); 
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
			setState(191);
			match(BACKGROUND_KEYWORD);
			setState(193);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD) | (1L << STEP_UNTIL_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << XPATH_BOOLEAN_NAME) | (1L << XPATH_NUMBER_NAME) | (1L << XPATH_STRING_NAME) | (1L << IMAGE_NAME) | (1L << OCR_NAME) | (1L << STATE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_SLIDER_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (HIT_KEY_NAME - 64)) | (1L << (DRAG_DROP_NAME - 64)) | (1L << (BOOLEAN_VARIABLE - 64)) | (1L << (NUMBER_VARIABLE - 64)) | (1L << (STRING_VARIABLE - 64)) | (1L << (WS - 64)) | (1L << (OTHER - 64)) | (1L << (BOOLEAN_VARIABLE_NAME - 64)) | (1L << (NUMBER_VARIABLE_NAME - 64)) | (1L << (STRING_VARIABLE_NAME - 64)) | (1L << (KB_KEY_NAME - 64)))) != 0)) {
				{
				setState(192);
				title();
				}
			}

			setState(196); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(195);
				match(EOL);
				}
				}
				setState(198); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(203);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD) | (1L << STEP_UNTIL_KEYWORD) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << XPATH_BOOLEAN_NAME) | (1L << XPATH_NUMBER_NAME) | (1L << XPATH_STRING_NAME) | (1L << IMAGE_NAME) | (1L << OCR_NAME) | (1L << STATE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_SLIDER_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (HIT_KEY_NAME - 64)) | (1L << (DRAG_DROP_NAME - 64)) | (1L << (BOOLEAN_VARIABLE - 64)) | (1L << (NUMBER_VARIABLE - 64)) | (1L << (STRING_VARIABLE - 64)) | (1L << (WS - 64)) | (1L << (OTHER - 64)) | (1L << (BOOLEAN_VARIABLE_NAME - 64)) | (1L << (NUMBER_VARIABLE_NAME - 64)) | (1L << (STRING_VARIABLE_NAME - 64)) | (1L << (KB_KEY_NAME - 64)))) != 0)) {
				{
				{
				setState(200);
				((BackgroundContext)_localctx).narrativeLine = narrativeLine();
				((BackgroundContext)_localctx).narrative.add(((BackgroundContext)_localctx).narrativeLine);
				}
				}
				setState(205);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(207);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(206);
				selection();
				}
			}

			setState(210);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(209);
				oracle();
				}
			}

			setState(213); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(212);
				((BackgroundContext)_localctx).step = step();
				((BackgroundContext)_localctx).steps.add(((BackgroundContext)_localctx).step);
				}
				}
				setState(215); 
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
			setState(219);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(217);
				scenario();
				}
				break;
			case 2:
				{
				setState(218);
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
			setState(224);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(221);
				((ScenarioContext)_localctx).tagname = tagname();
				((ScenarioContext)_localctx).tags.add(((ScenarioContext)_localctx).tagname);
				}
				}
				setState(226);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(227);
			match(SCENARIO_KEYWORD);
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD) | (1L << STEP_UNTIL_KEYWORD) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << XPATH_BOOLEAN_NAME) | (1L << XPATH_NUMBER_NAME) | (1L << XPATH_STRING_NAME) | (1L << IMAGE_NAME) | (1L << OCR_NAME) | (1L << STATE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_SLIDER_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (HIT_KEY_NAME - 64)) | (1L << (DRAG_DROP_NAME - 64)) | (1L << (BOOLEAN_VARIABLE - 64)) | (1L << (NUMBER_VARIABLE - 64)) | (1L << (STRING_VARIABLE - 64)) | (1L << (WS - 64)) | (1L << (OTHER - 64)) | (1L << (BOOLEAN_VARIABLE_NAME - 64)) | (1L << (NUMBER_VARIABLE_NAME - 64)) | (1L << (STRING_VARIABLE_NAME - 64)) | (1L << (KB_KEY_NAME - 64)))) != 0)) {
				{
				{
				setState(234);
				((ScenarioContext)_localctx).narrativeLine = narrativeLine();
				((ScenarioContext)_localctx).narrative.add(((ScenarioContext)_localctx).narrativeLine);
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
				((ScenarioContext)_localctx).step = step();
				((ScenarioContext)_localctx).steps.add(((ScenarioContext)_localctx).step);
				}
				}
				setState(249); 
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
			setState(254);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(251);
				((ScenarioOutlineContext)_localctx).tagname = tagname();
				((ScenarioOutlineContext)_localctx).tags.add(((ScenarioOutlineContext)_localctx).tagname);
				}
				}
				setState(256);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(257);
			match(SCENARIO_OUTLINE_KEYWORD);
			setState(258);
			title();
			setState(260); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(259);
				match(EOL);
				}
				}
				setState(262); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(267);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD) | (1L << STEP_UNTIL_KEYWORD) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << XPATH_BOOLEAN_NAME) | (1L << XPATH_NUMBER_NAME) | (1L << XPATH_STRING_NAME) | (1L << IMAGE_NAME) | (1L << OCR_NAME) | (1L << STATE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_SLIDER_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (HIT_KEY_NAME - 64)) | (1L << (DRAG_DROP_NAME - 64)) | (1L << (BOOLEAN_VARIABLE - 64)) | (1L << (NUMBER_VARIABLE - 64)) | (1L << (STRING_VARIABLE - 64)) | (1L << (WS - 64)) | (1L << (OTHER - 64)) | (1L << (BOOLEAN_VARIABLE_NAME - 64)) | (1L << (NUMBER_VARIABLE_NAME - 64)) | (1L << (STRING_VARIABLE_NAME - 64)) | (1L << (KB_KEY_NAME - 64)))) != 0)) {
				{
				{
				setState(264);
				((ScenarioOutlineContext)_localctx).narrativeLine = narrativeLine();
				((ScenarioOutlineContext)_localctx).narrative.add(((ScenarioOutlineContext)_localctx).narrativeLine);
				}
				}
				setState(269);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(271);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(270);
				selection();
				}
			}

			setState(274);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(273);
				oracle();
				}
			}

			setState(277); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(276);
				((ScenarioOutlineContext)_localctx).step = step();
				((ScenarioOutlineContext)_localctx).steps.add(((ScenarioOutlineContext)_localctx).step);
				}
				}
				setState(279); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STEP_KEYWORD );
			setState(281);
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
			setState(283);
			match(EXAMPLES_KEYWORD);
			setState(285);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD) | (1L << STEP_UNTIL_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << XPATH_BOOLEAN_NAME) | (1L << XPATH_NUMBER_NAME) | (1L << XPATH_STRING_NAME) | (1L << IMAGE_NAME) | (1L << OCR_NAME) | (1L << STATE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_SLIDER_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (HIT_KEY_NAME - 64)) | (1L << (DRAG_DROP_NAME - 64)) | (1L << (BOOLEAN_VARIABLE - 64)) | (1L << (NUMBER_VARIABLE - 64)) | (1L << (STRING_VARIABLE - 64)) | (1L << (WS - 64)) | (1L << (OTHER - 64)) | (1L << (BOOLEAN_VARIABLE_NAME - 64)) | (1L << (NUMBER_VARIABLE_NAME - 64)) | (1L << (STRING_VARIABLE_NAME - 64)) | (1L << (KB_KEY_NAME - 64)))) != 0)) {
				{
				setState(284);
				title();
				}
			}

			setState(288); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(287);
				match(EOL);
				}
				}
				setState(290); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(295);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD) | (1L << STEP_UNTIL_KEYWORD) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << XPATH_BOOLEAN_NAME) | (1L << XPATH_NUMBER_NAME) | (1L << XPATH_STRING_NAME) | (1L << IMAGE_NAME) | (1L << OCR_NAME) | (1L << STATE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_SLIDER_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (HIT_KEY_NAME - 64)) | (1L << (DRAG_DROP_NAME - 64)) | (1L << (BOOLEAN_VARIABLE - 64)) | (1L << (NUMBER_VARIABLE - 64)) | (1L << (STRING_VARIABLE - 64)) | (1L << (WS - 64)) | (1L << (OTHER - 64)) | (1L << (BOOLEAN_VARIABLE_NAME - 64)) | (1L << (NUMBER_VARIABLE_NAME - 64)) | (1L << (STRING_VARIABLE_NAME - 64)) | (1L << (KB_KEY_NAME - 64)))) != 0)) {
				{
				{
				setState(292);
				((ExamplesContext)_localctx).narrativeLine = narrativeLine();
				((ExamplesContext)_localctx).narrative.add(((ExamplesContext)_localctx).narrativeLine);
				}
				}
				setState(297);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(298);
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
			setState(301); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(300);
				((TableContext)_localctx).TABLE_ROW = match(TABLE_ROW);
				((TableContext)_localctx).rows.add(((TableContext)_localctx).TABLE_ROW);
				}
				}
				setState(303); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TABLE_ROW );
			setState(308);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(305);
				match(EOL);
				}
				}
				setState(310);
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
			setState(314);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(311);
					match(WS);
					}
					} 
				}
				setState(316);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			}
			setState(318); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(317);
				_la = _input.LA(1);
				if ( _la <= 0 || (_la==EOL) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				}
				setState(320); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD) | (1L << STEP_UNTIL_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << XPATH_BOOLEAN_NAME) | (1L << XPATH_NUMBER_NAME) | (1L << XPATH_STRING_NAME) | (1L << IMAGE_NAME) | (1L << OCR_NAME) | (1L << STATE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_SLIDER_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (HIT_KEY_NAME - 64)) | (1L << (DRAG_DROP_NAME - 64)) | (1L << (BOOLEAN_VARIABLE - 64)) | (1L << (NUMBER_VARIABLE - 64)) | (1L << (STRING_VARIABLE - 64)) | (1L << (WS - 64)) | (1L << (OTHER - 64)) | (1L << (BOOLEAN_VARIABLE_NAME - 64)) | (1L << (NUMBER_VARIABLE_NAME - 64)) | (1L << (STRING_VARIABLE_NAME - 64)) | (1L << (KB_KEY_NAME - 64)))) != 0) );
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
			setState(322);
			_la = _input.LA(1);
			if ( _la <= 0 || ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW))) != 0) || _la==EOL) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(326);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTION_KEYWORD_INCLUDE) | (1L << OPTION_KEYWORD_EXCLUDE) | (1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD) | (1L << STEP_UNTIL_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << XPATH_NAME) | (1L << XPATH_BOOLEAN_NAME) | (1L << XPATH_NUMBER_NAME) | (1L << XPATH_STRING_NAME) | (1L << IMAGE_NAME) | (1L << OCR_NAME) | (1L << STATE_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_SLIDER_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (HIT_KEY_NAME - 64)) | (1L << (DRAG_DROP_NAME - 64)) | (1L << (BOOLEAN_VARIABLE - 64)) | (1L << (NUMBER_VARIABLE - 64)) | (1L << (STRING_VARIABLE - 64)) | (1L << (WS - 64)) | (1L << (OTHER - 64)) | (1L << (BOOLEAN_VARIABLE_NAME - 64)) | (1L << (NUMBER_VARIABLE_NAME - 64)) | (1L << (STRING_VARIABLE_NAME - 64)) | (1L << (KB_KEY_NAME - 64)))) != 0)) {
				{
				{
				setState(323);
				_la = _input.LA(1);
				if ( _la <= 0 || (_la==EOL) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				}
				setState(328);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(330); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(329);
				match(EOL);
				}
				}
				setState(332); 
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
			setState(334);
			match(TAGNAME);
			setState(338);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(335);
				match(EOL);
				}
				}
				setState(340);
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
			setState(341);
			match(SELECTION_KEYWORD);
			setState(343); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(342);
				((SelectionContext)_localctx).conditional_gesture = conditional_gesture();
				((SelectionContext)_localctx).conditional_gestures.add(((SelectionContext)_localctx).conditional_gesture);
				}
				}
				setState(345); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( ((((_la - 22)) & ~0x3f) == 0 && ((1L << (_la - 22)) & ((1L << (DECIMAL_NUMBER - 22)) | (1L << (INTEGER_NUMBER - 22)) | (1L << (PLACEHOLDER - 22)) | (1L << (STRING - 22)) | (1L << (NOT - 22)) | (1L << (TRUE - 22)) | (1L << (FALSE - 22)) | (1L << (MINUS - 22)) | (1L << (LPAREN - 22)) | (1L << (MATCHES_NAME - 22)) | (1L << (XPATH_NAME - 22)) | (1L << (XPATH_BOOLEAN_NAME - 22)) | (1L << (XPATH_NUMBER_NAME - 22)) | (1L << (XPATH_STRING_NAME - 22)) | (1L << (IMAGE_NAME - 22)) | (1L << (OCR_NAME - 22)) | (1L << (STATE_NAME - 22)) | (1L << (CLICK_NAME - 22)) | (1L << (TYPE_NAME - 22)) | (1L << (DRAG_SLIDER_NAME - 22)) | (1L << (ANY_NAME - 22)) | (1L << (DOUBLE_CLICK_NAME - 22)) | (1L << (TRIPLE_CLICK_NAME - 22)) | (1L << (RIGHT_CLICK_NAME - 22)) | (1L << (MOUSE_MOVE_NAME - 22)) | (1L << (DROP_DOWN_AT_NAME - 22)) | (1L << (HIT_KEY_NAME - 22)) | (1L << (DRAG_DROP_NAME - 22)) | (1L << (BOOLEAN_VARIABLE - 22)) | (1L << (NUMBER_VARIABLE - 22)) | (1L << (STRING_VARIABLE - 22)))) != 0) );
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
			setState(347);
			match(ORACLE_KEYWORD);
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
		public StepIterationContext stepIteration() {
			return getRuleContext(StepIterationContext.class,0);
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
			setState(350);
			match(STEP_KEYWORD);
			setState(351);
			title();
			setState(353); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(352);
				match(EOL);
				}
				}
				setState(355); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(358);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STEP_RANGE_KEYWORD) | (1L << STEP_WHILE_KEYWORD) | (1L << STEP_REPEAT_KEYWORD))) != 0)) {
				{
				setState(357);
				stepIteration();
				}
			}

			setState(361);
			_la = _input.LA(1);
			if (_la==STEP_GIVEN_KEYWORD) {
				{
				setState(360);
				givenClause();
				}
			}

			setState(363);
			whenClause();
			setState(365);
			_la = _input.LA(1);
			if (_la==STEP_THEN_KEYWORD) {
				{
				setState(364);
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
			setState(367);
			match(STEP_GIVEN_KEYWORD);
			setState(368);
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
			setState(370);
			match(STEP_WHEN_KEYWORD);
			setState(372); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(371);
				((WhenClauseContext)_localctx).conditional_gesture = conditional_gesture();
				((WhenClauseContext)_localctx).conditional_gestures.add(((WhenClauseContext)_localctx).conditional_gesture);
				}
				}
				setState(374); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( ((((_la - 22)) & ~0x3f) == 0 && ((1L << (_la - 22)) & ((1L << (DECIMAL_NUMBER - 22)) | (1L << (INTEGER_NUMBER - 22)) | (1L << (PLACEHOLDER - 22)) | (1L << (STRING - 22)) | (1L << (NOT - 22)) | (1L << (TRUE - 22)) | (1L << (FALSE - 22)) | (1L << (MINUS - 22)) | (1L << (LPAREN - 22)) | (1L << (MATCHES_NAME - 22)) | (1L << (XPATH_NAME - 22)) | (1L << (XPATH_BOOLEAN_NAME - 22)) | (1L << (XPATH_NUMBER_NAME - 22)) | (1L << (XPATH_STRING_NAME - 22)) | (1L << (IMAGE_NAME - 22)) | (1L << (OCR_NAME - 22)) | (1L << (STATE_NAME - 22)) | (1L << (CLICK_NAME - 22)) | (1L << (TYPE_NAME - 22)) | (1L << (DRAG_SLIDER_NAME - 22)) | (1L << (ANY_NAME - 22)) | (1L << (DOUBLE_CLICK_NAME - 22)) | (1L << (TRIPLE_CLICK_NAME - 22)) | (1L << (RIGHT_CLICK_NAME - 22)) | (1L << (MOUSE_MOVE_NAME - 22)) | (1L << (DROP_DOWN_AT_NAME - 22)) | (1L << (HIT_KEY_NAME - 22)) | (1L << (DRAG_DROP_NAME - 22)) | (1L << (BOOLEAN_VARIABLE - 22)) | (1L << (NUMBER_VARIABLE - 22)) | (1L << (STRING_VARIABLE - 22)))) != 0) );
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
			setState(376);
			match(STEP_THEN_KEYWORD);
			setState(377);
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

	public static class StepIterationContext extends ParserRuleContext {
		public StepRangeContext stepRange() {
			return getRuleContext(StepRangeContext.class,0);
		}
		public StepWhileContext stepWhile() {
			return getRuleContext(StepWhileContext.class,0);
		}
		public StepRepeatUntilContext stepRepeatUntil() {
			return getRuleContext(StepRepeatUntilContext.class,0);
		}
		public StepIterationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stepIteration; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStepIteration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepIterationContext stepIteration() throws RecognitionException {
		StepIterationContext _localctx = new StepIterationContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_stepIteration);
		try {
			setState(382);
			switch (_input.LA(1)) {
			case STEP_RANGE_KEYWORD:
				enterOuterAlt(_localctx, 1);
				{
				setState(379);
				stepRange();
				}
				break;
			case STEP_WHILE_KEYWORD:
				enterOuterAlt(_localctx, 2);
				{
				setState(380);
				stepWhile();
				}
				break;
			case STEP_REPEAT_KEYWORD:
				enterOuterAlt(_localctx, 3);
				{
				setState(381);
				stepRepeatUntil();
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
		enterRule(_localctx, 42, RULE_stepRange);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(384);
			match(STEP_RANGE_KEYWORD);
			setState(385);
			((StepRangeContext)_localctx).from = match(INTEGER_NUMBER);
			setState(386);
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

	public static class StepWhileContext extends ParserRuleContext {
		public TerminalNode STEP_WHILE_KEYWORD() { return getToken(TgherkinParser.STEP_WHILE_KEYWORD, 0); }
		public Widget_tree_conditionContext widget_tree_condition() {
			return getRuleContext(Widget_tree_conditionContext.class,0);
		}
		public StepWhileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stepWhile; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStepWhile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepWhileContext stepWhile() throws RecognitionException {
		StepWhileContext _localctx = new StepWhileContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_stepWhile);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			match(STEP_WHILE_KEYWORD);
			setState(389);
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

	public static class StepRepeatUntilContext extends ParserRuleContext {
		public TerminalNode STEP_REPEAT_KEYWORD() { return getToken(TgherkinParser.STEP_REPEAT_KEYWORD, 0); }
		public TerminalNode STEP_UNTIL_KEYWORD() { return getToken(TgherkinParser.STEP_UNTIL_KEYWORD, 0); }
		public Widget_tree_conditionContext widget_tree_condition() {
			return getRuleContext(Widget_tree_conditionContext.class,0);
		}
		public StepRepeatUntilContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stepRepeatUntil; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStepRepeatUntil(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepRepeatUntilContext stepRepeatUntil() throws RecognitionException {
		StepRepeatUntilContext _localctx = new StepRepeatUntilContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_stepRepeatUntil);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(391);
			match(STEP_REPEAT_KEYWORD);
			setState(392);
			match(STEP_UNTIL_KEYWORD);
			setState(393);
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
		enterRule(_localctx, 48, RULE_conditional_gesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(396);
			_la = _input.LA(1);
			if (((((_la - 22)) & ~0x3f) == 0 && ((1L << (_la - 22)) & ((1L << (DECIMAL_NUMBER - 22)) | (1L << (INTEGER_NUMBER - 22)) | (1L << (PLACEHOLDER - 22)) | (1L << (STRING - 22)) | (1L << (NOT - 22)) | (1L << (TRUE - 22)) | (1L << (FALSE - 22)) | (1L << (MINUS - 22)) | (1L << (LPAREN - 22)) | (1L << (MATCHES_NAME - 22)) | (1L << (XPATH_NAME - 22)) | (1L << (XPATH_BOOLEAN_NAME - 22)) | (1L << (XPATH_NUMBER_NAME - 22)) | (1L << (XPATH_STRING_NAME - 22)) | (1L << (IMAGE_NAME - 22)) | (1L << (OCR_NAME - 22)) | (1L << (STATE_NAME - 22)) | (1L << (BOOLEAN_VARIABLE - 22)) | (1L << (NUMBER_VARIABLE - 22)) | (1L << (STRING_VARIABLE - 22)))) != 0)) {
				{
				setState(395);
				widget_condition(0);
				}
			}

			setState(398);
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
		public HitKeyGestureContext hitKeyGesture() {
			return getRuleContext(HitKeyGestureContext.class,0);
		}
		public DragDropGestureContext dragDropGesture() {
			return getRuleContext(DragDropGestureContext.class,0);
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
		enterRule(_localctx, 50, RULE_gesture);
		try {
			setState(408);
			switch (_input.LA(1)) {
			case DRAG_SLIDER_NAME:
			case RIGHT_CLICK_NAME:
			case MOUSE_MOVE_NAME:
			case DROP_DOWN_AT_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(400);
				parameterlessGesture();
				}
				break;
			case TYPE_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(401);
				typeGesture();
				}
				break;
			case CLICK_NAME:
				enterOuterAlt(_localctx, 3);
				{
				setState(402);
				clickGesture();
				}
				break;
			case DOUBLE_CLICK_NAME:
				enterOuterAlt(_localctx, 4);
				{
				setState(403);
				doubleClickGesture();
				}
				break;
			case TRIPLE_CLICK_NAME:
				enterOuterAlt(_localctx, 5);
				{
				setState(404);
				tripleClickGesture();
				}
				break;
			case ANY_NAME:
				enterOuterAlt(_localctx, 6);
				{
				setState(405);
				anyGesture();
				}
				break;
			case HIT_KEY_NAME:
				enterOuterAlt(_localctx, 7);
				{
				setState(406);
				hitKeyGesture();
				}
				break;
			case DRAG_DROP_NAME:
				enterOuterAlt(_localctx, 8);
				{
				setState(407);
				dragDropGesture();
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
		enterRule(_localctx, 52, RULE_typeGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(410);
			match(TYPE_NAME);
			setState(411);
			match(LPAREN);
			setState(413);
			_la = _input.LA(1);
			if (_la==PLACEHOLDER || _la==STRING) {
				{
				setState(412);
				_la = _input.LA(1);
				if ( !(_la==PLACEHOLDER || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(415);
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
		enterRule(_localctx, 54, RULE_clickGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(417);
			match(CLICK_NAME);
			setState(418);
			match(LPAREN);
			setState(420);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(419);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(422);
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
		enterRule(_localctx, 56, RULE_doubleClickGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(424);
			match(DOUBLE_CLICK_NAME);
			setState(425);
			match(LPAREN);
			setState(427);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(426);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(429);
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
		enterRule(_localctx, 58, RULE_tripleClickGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			match(TRIPLE_CLICK_NAME);
			setState(432);
			match(LPAREN);
			setState(434);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(433);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(436);
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
		enterRule(_localctx, 60, RULE_anyGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(438);
			match(ANY_NAME);
			setState(439);
			match(LPAREN);
			setState(441);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) {
				{
				setState(440);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLACEHOLDER) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(443);
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

	public static class HitKeyGestureContext extends ParserRuleContext {
		public TerminalNode HIT_KEY_NAME() { return getToken(TgherkinParser.HIT_KEY_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public List<HitKeyArgumentContext> hitKeyArgument() {
			return getRuleContexts(HitKeyArgumentContext.class);
		}
		public HitKeyArgumentContext hitKeyArgument(int i) {
			return getRuleContext(HitKeyArgumentContext.class,i);
		}
		public HitKeyGestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hitKeyGesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitHitKeyGesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HitKeyGestureContext hitKeyGesture() throws RecognitionException {
		HitKeyGestureContext _localctx = new HitKeyGestureContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_hitKeyGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(445);
			match(HIT_KEY_NAME);
			setState(446);
			match(LPAREN);
			setState(450);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLACEHOLDER || _la==KB_KEY_NAME) {
				{
				{
				setState(447);
				hitKeyArgument();
				}
				}
				setState(452);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(453);
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

	public static class HitKeyArgumentContext extends ParserRuleContext {
		public TerminalNode KB_KEY_NAME() { return getToken(TgherkinParser.KB_KEY_NAME, 0); }
		public TerminalNode PLACEHOLDER() { return getToken(TgherkinParser.PLACEHOLDER, 0); }
		public HitKeyArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hitKeyArgument; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitHitKeyArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HitKeyArgumentContext hitKeyArgument() throws RecognitionException {
		HitKeyArgumentContext _localctx = new HitKeyArgumentContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_hitKeyArgument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(455);
			_la = _input.LA(1);
			if ( !(_la==PLACEHOLDER || _la==KB_KEY_NAME) ) {
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

	public static class DragDropGestureContext extends ParserRuleContext {
		public TerminalNode DRAG_DROP_NAME() { return getToken(TgherkinParser.DRAG_DROP_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public Widget_conditionContext widget_condition() {
			return getRuleContext(Widget_conditionContext.class,0);
		}
		public DragDropGestureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dragDropGesture; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitDragDropGesture(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DragDropGestureContext dragDropGesture() throws RecognitionException {
		DragDropGestureContext _localctx = new DragDropGestureContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_dragDropGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(457);
			match(DRAG_DROP_NAME);
			setState(458);
			match(LPAREN);
			setState(460);
			_la = _input.LA(1);
			if (((((_la - 22)) & ~0x3f) == 0 && ((1L << (_la - 22)) & ((1L << (DECIMAL_NUMBER - 22)) | (1L << (INTEGER_NUMBER - 22)) | (1L << (PLACEHOLDER - 22)) | (1L << (STRING - 22)) | (1L << (NOT - 22)) | (1L << (TRUE - 22)) | (1L << (FALSE - 22)) | (1L << (MINUS - 22)) | (1L << (LPAREN - 22)) | (1L << (MATCHES_NAME - 22)) | (1L << (XPATH_NAME - 22)) | (1L << (XPATH_BOOLEAN_NAME - 22)) | (1L << (XPATH_NUMBER_NAME - 22)) | (1L << (XPATH_STRING_NAME - 22)) | (1L << (IMAGE_NAME - 22)) | (1L << (OCR_NAME - 22)) | (1L << (STATE_NAME - 22)) | (1L << (BOOLEAN_VARIABLE - 22)) | (1L << (NUMBER_VARIABLE - 22)) | (1L << (STRING_VARIABLE - 22)))) != 0)) {
				{
				setState(459);
				widget_condition(0);
				}
			}

			setState(462);
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
		public TerminalNode DRAG_SLIDER_NAME() { return getToken(TgherkinParser.DRAG_SLIDER_NAME, 0); }
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
		enterRule(_localctx, 68, RULE_gestureName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(464);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DRAG_SLIDER_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0)) ) {
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
		enterRule(_localctx, 70, RULE_parameterlessGesture);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(466);
			gestureName();
			setState(467);
			match(LPAREN);
			setState(468);
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
		int _startState = 72;
		enterRecursionRule(_localctx, 72, RULE_widget_condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(479);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				_localctx = new NegationWidgetConditionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(471);
				match(NOT);
				setState(472);
				widget_condition(4);
				}
				break;
			case 2:
				{
				_localctx = new LogicalEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(473);
				logical_entity();
				}
				break;
			case 3:
				{
				_localctx = new WidgetConditionInParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(474);
				match(LPAREN);
				setState(475);
				widget_condition(0);
				setState(476);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new RelationalExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(478);
				relational_expr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(489);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(487);
					switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetConditionAndContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(481);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(482);
						match(AND);
						setState(483);
						((WidgetConditionAndContext)_localctx).right = widget_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetConditionOrContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(484);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(485);
						match(OR);
						setState(486);
						((WidgetConditionOrContext)_localctx).right = widget_condition(2);
						}
						break;
					}
					} 
				}
				setState(491);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
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
		enterRule(_localctx, 74, RULE_relational_expr);
		try {
			setState(504);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				_localctx = new RelationalNumericExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(492);
				((RelationalNumericExpressionWithOperatorContext)_localctx).left = arithmetic_expr(0);
				setState(493);
				relational_operator();
				setState(494);
				((RelationalNumericExpressionWithOperatorContext)_localctx).right = arithmetic_expr(0);
				}
				break;
			case 2:
				_localctx = new RelationalStringExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(496);
				((RelationalStringExpressionWithOperatorContext)_localctx).left = string_expr();
				setState(497);
				relational_operator();
				setState(498);
				((RelationalStringExpressionWithOperatorContext)_localctx).right = string_expr();
				}
				break;
			case 3:
				_localctx = new RelationalExpressionParensContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(500);
				match(LPAREN);
				setState(501);
				relational_expr();
				setState(502);
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
		enterRule(_localctx, 76, RULE_relational_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(506);
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
		int _startState = 78;
		enterRecursionRule(_localctx, 78, RULE_arithmetic_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(516);
			switch (_input.LA(1)) {
			case MINUS:
				{
				_localctx = new ArithmeticExpressionNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(509);
				match(MINUS);
				setState(510);
				arithmetic_expr(4);
				}
				break;
			case DECIMAL_NUMBER:
			case INTEGER_NUMBER:
			case PLACEHOLDER:
			case XPATH_NUMBER_NAME:
			case NUMBER_VARIABLE:
				{
				_localctx = new ArithmeticExpressionNumericEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(511);
				numeric_entity();
				}
				break;
			case LPAREN:
				{
				_localctx = new ArithmeticExpressionParensContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(512);
				match(LPAREN);
				setState(513);
				arithmetic_expr(0);
				setState(514);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(529);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(527);
					switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticExpressionPowContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPowContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(518);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(519);
						match(POW);
						setState(520);
						((ArithmeticExpressionPowContext)_localctx).right = arithmetic_expr(4);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticExpressionMultDivModContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionMultDivModContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(521);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(522);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(523);
						((ArithmeticExpressionMultDivModContext)_localctx).right = arithmetic_expr(3);
						}
						break;
					case 3:
						{
						_localctx = new ArithmeticExpressionPlusMinusContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPlusMinusContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(524);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(525);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(526);
						((ArithmeticExpressionPlusMinusContext)_localctx).right = arithmetic_expr(2);
						}
						break;
					}
					} 
				}
				setState(531);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
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
		enterRule(_localctx, 80, RULE_string_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532);
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
		public XpathBooleanFunctionContext xpathBooleanFunction() {
			return getRuleContext(XpathBooleanFunctionContext.class,0);
		}
		public ImageFunctionContext imageFunction() {
			return getRuleContext(ImageFunctionContext.class,0);
		}
		public StateFunctionContext stateFunction() {
			return getRuleContext(StateFunctionContext.class,0);
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
		enterRule(_localctx, 82, RULE_booleanFunction);
		try {
			setState(539);
			switch (_input.LA(1)) {
			case MATCHES_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(534);
				matchesFunction();
				}
				break;
			case XPATH_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(535);
				xpathFunction();
				}
				break;
			case XPATH_BOOLEAN_NAME:
				enterOuterAlt(_localctx, 3);
				{
				setState(536);
				xpathBooleanFunction();
				}
				break;
			case IMAGE_NAME:
				enterOuterAlt(_localctx, 4);
				{
				setState(537);
				imageFunction();
				}
				break;
			case STATE_NAME:
				enterOuterAlt(_localctx, 5);
				{
				setState(538);
				stateFunction();
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

	public static class StringFunctionContext extends ParserRuleContext {
		public OcrFunctionContext ocrFunction() {
			return getRuleContext(OcrFunctionContext.class,0);
		}
		public XpathStringFunctionContext xpathStringFunction() {
			return getRuleContext(XpathStringFunctionContext.class,0);
		}
		public StringFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStringFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringFunctionContext stringFunction() throws RecognitionException {
		StringFunctionContext _localctx = new StringFunctionContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_stringFunction);
		try {
			setState(543);
			switch (_input.LA(1)) {
			case OCR_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(541);
				ocrFunction();
				}
				break;
			case XPATH_STRING_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(542);
				xpathStringFunction();
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

	public static class NumericFunctionContext extends ParserRuleContext {
		public XpathNumberFunctionContext xpathNumberFunction() {
			return getRuleContext(XpathNumberFunctionContext.class,0);
		}
		public NumericFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitNumericFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumericFunctionContext numericFunction() throws RecognitionException {
		NumericFunctionContext _localctx = new NumericFunctionContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_numericFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(545);
			xpathNumberFunction();
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
		public String_entityContext string_entity() {
			return getRuleContext(String_entityContext.class,0);
		}
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
		enterRule(_localctx, 88, RULE_matchesFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(547);
			match(MATCHES_NAME);
			setState(548);
			match(LPAREN);
			setState(549);
			string_entity();
			setState(550);
			match(COMMA);
			setState(551);
			match(STRING);
			setState(552);
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
		enterRule(_localctx, 90, RULE_xpathFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(554);
			match(XPATH_NAME);
			setState(555);
			match(LPAREN);
			setState(556);
			match(STRING);
			setState(557);
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

	public static class XpathBooleanFunctionContext extends ParserRuleContext {
		public TerminalNode XPATH_BOOLEAN_NAME() { return getToken(TgherkinParser.XPATH_BOOLEAN_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(TgherkinParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public XpathBooleanFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpathBooleanFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitXpathBooleanFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final XpathBooleanFunctionContext xpathBooleanFunction() throws RecognitionException {
		XpathBooleanFunctionContext _localctx = new XpathBooleanFunctionContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_xpathBooleanFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(559);
			match(XPATH_BOOLEAN_NAME);
			setState(560);
			match(LPAREN);
			setState(561);
			match(STRING);
			setState(562);
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

	public static class XpathNumberFunctionContext extends ParserRuleContext {
		public TerminalNode XPATH_NUMBER_NAME() { return getToken(TgherkinParser.XPATH_NUMBER_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(TgherkinParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public XpathNumberFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpathNumberFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitXpathNumberFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final XpathNumberFunctionContext xpathNumberFunction() throws RecognitionException {
		XpathNumberFunctionContext _localctx = new XpathNumberFunctionContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_xpathNumberFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(564);
			match(XPATH_NUMBER_NAME);
			setState(565);
			match(LPAREN);
			setState(566);
			match(STRING);
			setState(567);
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

	public static class XpathStringFunctionContext extends ParserRuleContext {
		public TerminalNode XPATH_STRING_NAME() { return getToken(TgherkinParser.XPATH_STRING_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(TgherkinParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public XpathStringFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpathStringFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitXpathStringFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final XpathStringFunctionContext xpathStringFunction() throws RecognitionException {
		XpathStringFunctionContext _localctx = new XpathStringFunctionContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_xpathStringFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(569);
			match(XPATH_STRING_NAME);
			setState(570);
			match(LPAREN);
			setState(571);
			match(STRING);
			setState(572);
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
		enterRule(_localctx, 98, RULE_imageFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(574);
			match(IMAGE_NAME);
			setState(575);
			match(LPAREN);
			setState(576);
			match(STRING);
			setState(577);
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

	public static class OcrFunctionContext extends ParserRuleContext {
		public TerminalNode OCR_NAME() { return getToken(TgherkinParser.OCR_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public OcrFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ocrFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitOcrFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OcrFunctionContext ocrFunction() throws RecognitionException {
		OcrFunctionContext _localctx = new OcrFunctionContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_ocrFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(579);
			match(OCR_NAME);
			setState(580);
			match(LPAREN);
			setState(581);
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

	public static class StateFunctionContext extends ParserRuleContext {
		public TerminalNode STATE_NAME() { return getToken(TgherkinParser.STATE_NAME, 0); }
		public TerminalNode LPAREN() { return getToken(TgherkinParser.LPAREN, 0); }
		public Widget_tree_conditionContext widget_tree_condition() {
			return getRuleContext(Widget_tree_conditionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(TgherkinParser.RPAREN, 0); }
		public StateFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stateFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStateFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StateFunctionContext stateFunction() throws RecognitionException {
		StateFunctionContext _localctx = new StateFunctionContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_stateFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(583);
			match(STATE_NAME);
			setState(584);
			match(LPAREN);
			setState(585);
			widget_tree_condition(0);
			setState(586);
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
		int _startState = 104;
		enterRecursionRule(_localctx, 104, RULE_widget_tree_condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new WidgetConditionContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(589);
			widget_condition(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(599);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(597);
					switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetTreeConditionAlsoContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionAlsoContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(591);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(592);
						match(STEP_ALSO_KEYWORD);
						setState(593);
						((WidgetTreeConditionAlsoContext)_localctx).right = widget_tree_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetTreeConditionEitherContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionEitherContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(594);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(595);
						match(STEP_EITHER_KEYWORD);
						setState(596);
						((WidgetTreeConditionEitherContext)_localctx).right = widget_tree_condition(2);
						}
						break;
					}
					} 
				}
				setState(601);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
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
		enterRule(_localctx, 106, RULE_bool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(602);
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
		enterRule(_localctx, 108, RULE_logical_entity);
		try {
			setState(608);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
				_localctx = new LogicalConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(604);
				bool();
				}
				break;
			case BOOLEAN_VARIABLE:
				_localctx = new LogicalVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(605);
				match(BOOLEAN_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new LogicalPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(606);
				match(PLACEHOLDER);
				}
				break;
			case MATCHES_NAME:
			case XPATH_NAME:
			case XPATH_BOOLEAN_NAME:
			case IMAGE_NAME:
			case STATE_NAME:
				_localctx = new LogicalFunctionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(607);
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
	public static class NumberFunctionContext extends Numeric_entityContext {
		public NumericFunctionContext numericFunction() {
			return getRuleContext(NumericFunctionContext.class,0);
		}
		public NumberFunctionContext(Numeric_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitNumberFunction(this);
			else return visitor.visitChildren(this);
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
		enterRule(_localctx, 110, RULE_numeric_entity);
		try {
			setState(615);
			switch (_input.LA(1)) {
			case INTEGER_NUMBER:
				_localctx = new IntegerConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(610);
				match(INTEGER_NUMBER);
				}
				break;
			case DECIMAL_NUMBER:
				_localctx = new DecimalConstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(611);
				match(DECIMAL_NUMBER);
				}
				break;
			case NUMBER_VARIABLE:
				_localctx = new NumericVariableContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(612);
				match(NUMBER_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new NumericPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(613);
				match(PLACEHOLDER);
				}
				break;
			case XPATH_NUMBER_NAME:
				_localctx = new NumberFunctionContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(614);
				numericFunction();
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
	public static class StrFunctionContext extends String_entityContext {
		public StringFunctionContext stringFunction() {
			return getRuleContext(StringFunctionContext.class,0);
		}
		public StrFunctionContext(String_entityContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TgherkinParserVisitor ) return ((TgherkinParserVisitor<? extends T>)visitor).visitStrFunction(this);
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
		enterRule(_localctx, 112, RULE_string_entity);
		try {
			setState(621);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(617);
				match(STRING);
				}
				break;
			case STRING_VARIABLE:
				_localctx = new StringVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(618);
				match(STRING_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new StringPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(619);
				match(PLACEHOLDER);
				}
				break;
			case XPATH_STRING_NAME:
			case OCR_NAME:
				_localctx = new StrFunctionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(620);
				stringFunction();
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
		case 36:
			return widget_condition_sempred((Widget_conditionContext)_localctx, predIndex);
		case 39:
			return arithmetic_expr_sempred((Arithmetic_exprContext)_localctx, predIndex);
		case 52:
			return widget_tree_condition_sempred((Widget_tree_conditionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean widget_condition_sempred(Widget_conditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean arithmetic_expr_sempred(Arithmetic_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 3);
		case 3:
			return precpred(_ctx, 2);
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean widget_tree_condition_sempred(Widget_tree_conditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 2);
		case 6:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3M\u0272\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\3\2\5\2v\n\2\3\2\6"+
		"\2y\n\2\r\2\16\2z\3\2\3\2\3\3\3\3\5\3\u0081\n\3\3\3\3\3\5\3\u0085\n\3"+
		"\5\3\u0087\n\3\3\4\3\4\6\4\u008b\n\4\r\4\16\4\u008c\3\4\7\4\u0090\n\4"+
		"\f\4\16\4\u0093\13\4\3\5\3\5\6\5\u0097\n\5\r\5\16\5\u0098\3\5\7\5\u009c"+
		"\n\5\f\5\16\5\u009f\13\5\3\6\7\6\u00a2\n\6\f\6\16\6\u00a5\13\6\3\6\3\6"+
		"\3\6\6\6\u00aa\n\6\r\6\16\6\u00ab\3\6\7\6\u00af\n\6\f\6\16\6\u00b2\13"+
		"\6\3\6\5\6\u00b5\n\6\3\6\5\6\u00b8\n\6\3\6\5\6\u00bb\n\6\3\6\6\6\u00be"+
		"\n\6\r\6\16\6\u00bf\3\7\3\7\5\7\u00c4\n\7\3\7\6\7\u00c7\n\7\r\7\16\7\u00c8"+
		"\3\7\7\7\u00cc\n\7\f\7\16\7\u00cf\13\7\3\7\5\7\u00d2\n\7\3\7\5\7\u00d5"+
		"\n\7\3\7\6\7\u00d8\n\7\r\7\16\7\u00d9\3\b\3\b\5\b\u00de\n\b\3\t\7\t\u00e1"+
		"\n\t\f\t\16\t\u00e4\13\t\3\t\3\t\3\t\6\t\u00e9\n\t\r\t\16\t\u00ea\3\t"+
		"\7\t\u00ee\n\t\f\t\16\t\u00f1\13\t\3\t\5\t\u00f4\n\t\3\t\5\t\u00f7\n\t"+
		"\3\t\6\t\u00fa\n\t\r\t\16\t\u00fb\3\n\7\n\u00ff\n\n\f\n\16\n\u0102\13"+
		"\n\3\n\3\n\3\n\6\n\u0107\n\n\r\n\16\n\u0108\3\n\7\n\u010c\n\n\f\n\16\n"+
		"\u010f\13\n\3\n\5\n\u0112\n\n\3\n\5\n\u0115\n\n\3\n\6\n\u0118\n\n\r\n"+
		"\16\n\u0119\3\n\3\n\3\13\3\13\5\13\u0120\n\13\3\13\6\13\u0123\n\13\r\13"+
		"\16\13\u0124\3\13\7\13\u0128\n\13\f\13\16\13\u012b\13\13\3\13\3\13\3\f"+
		"\6\f\u0130\n\f\r\f\16\f\u0131\3\f\7\f\u0135\n\f\f\f\16\f\u0138\13\f\3"+
		"\r\7\r\u013b\n\r\f\r\16\r\u013e\13\r\3\r\6\r\u0141\n\r\r\r\16\r\u0142"+
		"\3\16\3\16\7\16\u0147\n\16\f\16\16\16\u014a\13\16\3\16\6\16\u014d\n\16"+
		"\r\16\16\16\u014e\3\17\3\17\7\17\u0153\n\17\f\17\16\17\u0156\13\17\3\20"+
		"\3\20\6\20\u015a\n\20\r\20\16\20\u015b\3\21\3\21\3\21\3\22\3\22\3\22\6"+
		"\22\u0164\n\22\r\22\16\22\u0165\3\22\5\22\u0169\n\22\3\22\5\22\u016c\n"+
		"\22\3\22\3\22\5\22\u0170\n\22\3\23\3\23\3\23\3\24\3\24\6\24\u0177\n\24"+
		"\r\24\16\24\u0178\3\25\3\25\3\25\3\26\3\26\3\26\5\26\u0181\n\26\3\27\3"+
		"\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\32\5\32\u018f\n\32"+
		"\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u019b\n\33\3\34"+
		"\3\34\3\34\5\34\u01a0\n\34\3\34\3\34\3\35\3\35\3\35\5\35\u01a7\n\35\3"+
		"\35\3\35\3\36\3\36\3\36\5\36\u01ae\n\36\3\36\3\36\3\37\3\37\3\37\5\37"+
		"\u01b5\n\37\3\37\3\37\3 \3 \3 \5 \u01bc\n \3 \3 \3!\3!\3!\7!\u01c3\n!"+
		"\f!\16!\u01c6\13!\3!\3!\3\"\3\"\3#\3#\3#\5#\u01cf\n#\3#\3#\3$\3$\3%\3"+
		"%\3%\3%\3&\3&\3&\3&\3&\3&\3&\3&\3&\5&\u01e2\n&\3&\3&\3&\3&\3&\3&\7&\u01ea"+
		"\n&\f&\16&\u01ed\13&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5"+
		"\'\u01fb\n\'\3(\3(\3)\3)\3)\3)\3)\3)\3)\3)\5)\u0207\n)\3)\3)\3)\3)\3)"+
		"\3)\3)\3)\3)\7)\u0212\n)\f)\16)\u0215\13)\3*\3*\3+\3+\3+\3+\3+\5+\u021e"+
		"\n+\3,\3,\5,\u0222\n,\3-\3-\3.\3.\3.\3.\3.\3.\3.\3/\3/\3/\3/\3/\3\60\3"+
		"\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3\62\3"+
		"\63\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3"+
		"\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\7\66\u0258\n\66\f\66\16\66"+
		"\u025b\13\66\3\67\3\67\38\38\38\38\58\u0263\n8\39\39\39\39\39\59\u026a"+
		"\n9\3:\3:\3:\3:\5:\u0270\n:\3:\2\5JPj;\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnpr\2\f\3\2GG\5"+
		"\2\5\16\22\27GG\3\2\32\33\4\2\32\32 !\4\2\32\32MM\4\2;;?A\3\2(-\3\2#%"+
		"\3\2&\'\3\2 !\u0299\2u\3\2\2\2\4\u0086\3\2\2\2\6\u0088\3\2\2\2\b\u0094"+
		"\3\2\2\2\n\u00a3\3\2\2\2\f\u00c1\3\2\2\2\16\u00dd\3\2\2\2\20\u00e2\3\2"+
		"\2\2\22\u0100\3\2\2\2\24\u011d\3\2\2\2\26\u012f\3\2\2\2\30\u013c\3\2\2"+
		"\2\32\u0144\3\2\2\2\34\u0150\3\2\2\2\36\u0157\3\2\2\2 \u015d\3\2\2\2\""+
		"\u0160\3\2\2\2$\u0171\3\2\2\2&\u0174\3\2\2\2(\u017a\3\2\2\2*\u0180\3\2"+
		"\2\2,\u0182\3\2\2\2.\u0186\3\2\2\2\60\u0189\3\2\2\2\62\u018e\3\2\2\2\64"+
		"\u019a\3\2\2\2\66\u019c\3\2\2\28\u01a3\3\2\2\2:\u01aa\3\2\2\2<\u01b1\3"+
		"\2\2\2>\u01b8\3\2\2\2@\u01bf\3\2\2\2B\u01c9\3\2\2\2D\u01cb\3\2\2\2F\u01d2"+
		"\3\2\2\2H\u01d4\3\2\2\2J\u01e1\3\2\2\2L\u01fa\3\2\2\2N\u01fc\3\2\2\2P"+
		"\u0206\3\2\2\2R\u0216\3\2\2\2T\u021d\3\2\2\2V\u0221\3\2\2\2X\u0223\3\2"+
		"\2\2Z\u0225\3\2\2\2\\\u022c\3\2\2\2^\u0231\3\2\2\2`\u0236\3\2\2\2b\u023b"+
		"\3\2\2\2d\u0240\3\2\2\2f\u0245\3\2\2\2h\u0249\3\2\2\2j\u024e\3\2\2\2l"+
		"\u025c\3\2\2\2n\u0262\3\2\2\2p\u0269\3\2\2\2r\u026f\3\2\2\2tv\5\4\3\2"+
		"ut\3\2\2\2uv\3\2\2\2vx\3\2\2\2wy\5\n\6\2xw\3\2\2\2yz\3\2\2\2zx\3\2\2\2"+
		"z{\3\2\2\2{|\3\2\2\2|}\7\2\2\3}\3\3\2\2\2~\u0080\5\6\4\2\177\u0081\5\b"+
		"\5\2\u0080\177\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0087\3\2\2\2\u0082\u0084"+
		"\5\b\5\2\u0083\u0085\5\6\4\2\u0084\u0083\3\2\2\2\u0084\u0085\3\2\2\2\u0085"+
		"\u0087\3\2\2\2\u0086~\3\2\2\2\u0086\u0082\3\2\2\2\u0087\5\3\2\2\2\u0088"+
		"\u008a\7\4\2\2\u0089\u008b\7\5\2\2\u008a\u0089\3\2\2\2\u008b\u008c\3\2"+
		"\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u0091\3\2\2\2\u008e"+
		"\u0090\7G\2\2\u008f\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2"+
		"\2\2\u0091\u0092\3\2\2\2\u0092\7\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0096"+
		"\7\3\2\2\u0095\u0097\7\5\2\2\u0096\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098"+
		"\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009d\3\2\2\2\u009a\u009c\7G"+
		"\2\2\u009b\u009a\3\2\2\2\u009c\u009f\3\2\2\2\u009d\u009b\3\2\2\2\u009d"+
		"\u009e\3\2\2\2\u009e\t\3\2\2\2\u009f\u009d\3\2\2\2\u00a0\u00a2\5\34\17"+
		"\2\u00a1\u00a0\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4"+
		"\3\2\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a6\u00a7\7\6\2\2\u00a7"+
		"\u00a9\5\30\r\2\u00a8\u00aa\7G\2\2\u00a9\u00a8\3\2\2\2\u00aa\u00ab\3\2"+
		"\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00b0\3\2\2\2\u00ad"+
		"\u00af\5\32\16\2\u00ae\u00ad\3\2\2\2\u00af\u00b2\3\2\2\2\u00b0\u00ae\3"+
		"\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b3"+
		"\u00b5\5\36\20\2\u00b4\u00b3\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b7\3"+
		"\2\2\2\u00b6\u00b8\5 \21\2\u00b7\u00b6\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8"+
		"\u00ba\3\2\2\2\u00b9\u00bb\5\f\7\2\u00ba\u00b9\3\2\2\2\u00ba\u00bb\3\2"+
		"\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00be\5\16\b\2\u00bd\u00bc\3\2\2\2\u00be"+
		"\u00bf\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\13\3\2\2"+
		"\2\u00c1\u00c3\7\7\2\2\u00c2\u00c4\5\30\r\2\u00c3\u00c2\3\2\2\2\u00c3"+
		"\u00c4\3\2\2\2\u00c4\u00c6\3\2\2\2\u00c5\u00c7\7G\2\2\u00c6\u00c5\3\2"+
		"\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9"+
		"\u00cd\3\2\2\2\u00ca\u00cc\5\32\16\2\u00cb\u00ca\3\2\2\2\u00cc\u00cf\3"+
		"\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00d1\3\2\2\2\u00cf"+
		"\u00cd\3\2\2\2\u00d0\u00d2\5\36\20\2\u00d1\u00d0\3\2\2\2\u00d1\u00d2\3"+
		"\2\2\2\u00d2\u00d4\3\2\2\2\u00d3\u00d5\5 \21\2\u00d4\u00d3\3\2\2\2\u00d4"+
		"\u00d5\3\2\2\2\u00d5\u00d7\3\2\2\2\u00d6\u00d8\5\"\22\2\u00d7\u00d6\3"+
		"\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9\u00da\3\2\2\2\u00da"+
		"\r\3\2\2\2\u00db\u00de\5\20\t\2\u00dc\u00de\5\22\n\2\u00dd\u00db\3\2\2"+
		"\2\u00dd\u00dc\3\2\2\2\u00de\17\3\2\2\2\u00df\u00e1\5\34\17\2\u00e0\u00df"+
		"\3\2\2\2\u00e1\u00e4\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3"+
		"\u00e5\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e5\u00e6\7\b\2\2\u00e6\u00e8\5\30"+
		"\r\2\u00e7\u00e9\7G\2\2\u00e8\u00e7\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea"+
		"\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ef\3\2\2\2\u00ec\u00ee\5\32"+
		"\16\2\u00ed\u00ec\3\2\2\2\u00ee\u00f1\3\2\2\2\u00ef\u00ed\3\2\2\2\u00ef"+
		"\u00f0\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f2\u00f4\5\36"+
		"\20\2\u00f3\u00f2\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f6\3\2\2\2\u00f5"+
		"\u00f7\5 \21\2\u00f6\u00f5\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f9\3\2"+
		"\2\2\u00f8\u00fa\5\"\22\2\u00f9\u00f8\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb"+
		"\u00f9\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc\21\3\2\2\2\u00fd\u00ff\5\34\17"+
		"\2\u00fe\u00fd\3\2\2\2\u00ff\u0102\3\2\2\2\u0100\u00fe\3\2\2\2\u0100\u0101"+
		"\3\2\2\2\u0101\u0103\3\2\2\2\u0102\u0100\3\2\2\2\u0103\u0104\7\t\2\2\u0104"+
		"\u0106\5\30\r\2\u0105\u0107\7G\2\2\u0106\u0105\3\2\2\2\u0107\u0108\3\2"+
		"\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u010d\3\2\2\2\u010a"+
		"\u010c\5\32\16\2\u010b\u010a\3\2\2\2\u010c\u010f\3\2\2\2\u010d\u010b\3"+
		"\2\2\2\u010d\u010e\3\2\2\2\u010e\u0111\3\2\2\2\u010f\u010d\3\2\2\2\u0110"+
		"\u0112\5\36\20\2\u0111\u0110\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0114\3"+
		"\2\2\2\u0113\u0115\5 \21\2\u0114\u0113\3\2\2\2\u0114\u0115\3\2\2\2\u0115"+
		"\u0117\3\2\2\2\u0116\u0118\5\"\22\2\u0117\u0116\3\2\2\2\u0118\u0119\3"+
		"\2\2\2\u0119\u0117\3\2\2\2\u0119\u011a\3\2\2\2\u011a\u011b\3\2\2\2\u011b"+
		"\u011c\5\24\13\2\u011c\23\3\2\2\2\u011d\u011f\7\n\2\2\u011e\u0120\5\30"+
		"\r\2\u011f\u011e\3\2\2\2\u011f\u0120\3\2\2\2\u0120\u0122\3\2\2\2\u0121"+
		"\u0123\7G\2\2\u0122\u0121\3\2\2\2\u0123\u0124\3\2\2\2\u0124\u0122\3\2"+
		"\2\2\u0124\u0125\3\2\2\2\u0125\u0129\3\2\2\2\u0126\u0128\5\32\16\2\u0127"+
		"\u0126\3\2\2\2\u0128\u012b\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2"+
		"\2\2\u012a\u012c\3\2\2\2\u012b\u0129\3\2\2\2\u012c\u012d\5\26\f\2\u012d"+
		"\25\3\2\2\2\u012e\u0130\7\27\2\2\u012f\u012e\3\2\2\2\u0130\u0131\3\2\2"+
		"\2\u0131\u012f\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0136\3\2\2\2\u0133\u0135"+
		"\7G\2\2\u0134\u0133\3\2\2\2\u0135\u0138\3\2\2\2\u0136\u0134\3\2\2\2\u0136"+
		"\u0137\3\2\2\2\u0137\27\3\2\2\2\u0138\u0136\3\2\2\2\u0139\u013b\7H\2\2"+
		"\u013a\u0139\3\2\2\2\u013b\u013e\3\2\2\2\u013c\u013a\3\2\2\2\u013c\u013d"+
		"\3\2\2\2\u013d\u0140\3\2\2\2\u013e\u013c\3\2\2\2\u013f\u0141\n\2\2\2\u0140"+
		"\u013f\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0140\3\2\2\2\u0142\u0143\3\2"+
		"\2\2\u0143\31\3\2\2\2\u0144\u0148\n\3\2\2\u0145\u0147\n\2\2\2\u0146\u0145"+
		"\3\2\2\2\u0147\u014a\3\2\2\2\u0148\u0146\3\2\2\2\u0148\u0149\3\2\2\2\u0149"+
		"\u014c\3\2\2\2\u014a\u0148\3\2\2\2\u014b\u014d\7G\2\2\u014c\u014b\3\2"+
		"\2\2\u014d\u014e\3\2\2\2\u014e\u014c\3\2\2\2\u014e\u014f\3\2\2\2\u014f"+
		"\33\3\2\2\2\u0150\u0154\7\5\2\2\u0151\u0153\7G\2\2\u0152\u0151\3\2\2\2"+
		"\u0153\u0156\3\2\2\2\u0154\u0152\3\2\2\2\u0154\u0155\3\2\2\2\u0155\35"+
		"\3\2\2\2\u0156\u0154\3\2\2\2\u0157\u0159\7\13\2\2\u0158\u015a\5\62\32"+
		"\2\u0159\u0158\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u0159\3\2\2\2\u015b\u015c"+
		"\3\2\2\2\u015c\37\3\2\2\2\u015d\u015e\7\f\2\2\u015e\u015f\5j\66\2\u015f"+
		"!\3\2\2\2\u0160\u0161\7\r\2\2\u0161\u0163\5\30\r\2\u0162\u0164\7G\2\2"+
		"\u0163\u0162\3\2\2\2\u0164\u0165\3\2\2\2\u0165\u0163\3\2\2\2\u0165\u0166"+
		"\3\2\2\2\u0166\u0168\3\2\2\2\u0167\u0169\5*\26\2\u0168\u0167\3\2\2\2\u0168"+
		"\u0169\3\2\2\2\u0169\u016b\3\2\2\2\u016a\u016c\5$\23\2\u016b\u016a\3\2"+
		"\2\2\u016b\u016c\3\2\2\2\u016c\u016d\3\2\2\2\u016d\u016f\5&\24\2\u016e"+
		"\u0170\5(\25\2\u016f\u016e\3\2\2\2\u016f\u0170\3\2\2\2\u0170#\3\2\2\2"+
		"\u0171\u0172\7\22\2\2\u0172\u0173\5j\66\2\u0173%\3\2\2\2\u0174\u0176\7"+
		"\23\2\2\u0175\u0177\5\62\32\2\u0176\u0175\3\2\2\2\u0177\u0178\3\2\2\2"+
		"\u0178\u0176\3\2\2\2\u0178\u0179\3\2\2\2\u0179\'\3\2\2\2\u017a\u017b\7"+
		"\24\2\2\u017b\u017c\5j\66\2\u017c)\3\2\2\2\u017d\u0181\5,\27\2\u017e\u0181"+
		"\5.\30\2\u017f\u0181\5\60\31\2\u0180\u017d\3\2\2\2\u0180\u017e\3\2\2\2"+
		"\u0180\u017f\3\2\2\2\u0181+\3\2\2\2\u0182\u0183\7\16\2\2\u0183\u0184\7"+
		"\31\2\2\u0184\u0185\7\31\2\2\u0185-\3\2\2\2\u0186\u0187\7\17\2\2\u0187"+
		"\u0188\5j\66\2\u0188/\3\2\2\2\u0189\u018a\7\20\2\2\u018a\u018b\7\21\2"+
		"\2\u018b\u018c\5j\66\2\u018c\61\3\2\2\2\u018d\u018f\5J&\2\u018e\u018d"+
		"\3\2\2\2\u018e\u018f\3\2\2\2\u018f\u0190\3\2\2\2\u0190\u0191\5\64\33\2"+
		"\u0191\63\3\2\2\2\u0192\u019b\5H%\2\u0193\u019b\5\66\34\2\u0194\u019b"+
		"\58\35\2\u0195\u019b\5:\36\2\u0196\u019b\5<\37\2\u0197\u019b\5> \2\u0198"+
		"\u019b\5@!\2\u0199\u019b\5D#\2\u019a\u0192\3\2\2\2\u019a\u0193\3\2\2\2"+
		"\u019a\u0194\3\2\2\2\u019a\u0195\3\2\2\2\u019a\u0196\3\2\2\2\u019a\u0197"+
		"\3\2\2\2\u019a\u0198\3\2\2\2\u019a\u0199\3\2\2\2\u019b\65\3\2\2\2\u019c"+
		"\u019d\7:\2\2\u019d\u019f\7.\2\2\u019e\u01a0\t\4\2\2\u019f\u019e\3\2\2"+
		"\2\u019f\u01a0\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1\u01a2\7/\2\2\u01a2\67"+
		"\3\2\2\2\u01a3\u01a4\79\2\2\u01a4\u01a6\7.\2\2\u01a5\u01a7\t\5\2\2\u01a6"+
		"\u01a5\3\2\2\2\u01a6\u01a7\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01a9\7/"+
		"\2\2\u01a99\3\2\2\2\u01aa\u01ab\7=\2\2\u01ab\u01ad\7.\2\2\u01ac\u01ae"+
		"\t\5\2\2\u01ad\u01ac\3\2\2\2\u01ad\u01ae\3\2\2\2\u01ae\u01af\3\2\2\2\u01af"+
		"\u01b0\7/\2\2\u01b0;\3\2\2\2\u01b1\u01b2\7>\2\2\u01b2\u01b4\7.\2\2\u01b3"+
		"\u01b5\t\5\2\2\u01b4\u01b3\3\2\2\2\u01b4\u01b5\3\2\2\2\u01b5\u01b6\3\2"+
		"\2\2\u01b6\u01b7\7/\2\2\u01b7=\3\2\2\2\u01b8\u01b9\7<\2\2\u01b9\u01bb"+
		"\7.\2\2\u01ba\u01bc\t\5\2\2\u01bb\u01ba\3\2\2\2\u01bb\u01bc\3\2\2\2\u01bc"+
		"\u01bd\3\2\2\2\u01bd\u01be\7/\2\2\u01be?\3\2\2\2\u01bf\u01c0\7B\2\2\u01c0"+
		"\u01c4\7.\2\2\u01c1\u01c3\5B\"\2\u01c2\u01c1\3\2\2\2\u01c3\u01c6\3\2\2"+
		"\2\u01c4\u01c2\3\2\2\2\u01c4\u01c5\3\2\2\2\u01c5\u01c7\3\2\2\2\u01c6\u01c4"+
		"\3\2\2\2\u01c7\u01c8\7/\2\2\u01c8A\3\2\2\2\u01c9\u01ca\t\6\2\2\u01caC"+
		"\3\2\2\2\u01cb\u01cc\7C\2\2\u01cc\u01ce\7.\2\2\u01cd\u01cf\5J&\2\u01ce"+
		"\u01cd\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cf\u01d0\3\2\2\2\u01d0\u01d1\7/"+
		"\2\2\u01d1E\3\2\2\2\u01d2\u01d3\t\7\2\2\u01d3G\3\2\2\2\u01d4\u01d5\5F"+
		"$\2\u01d5\u01d6\7.\2\2\u01d6\u01d7\7/\2\2\u01d7I\3\2\2\2\u01d8\u01d9\b"+
		"&\1\2\u01d9\u01da\7\37\2\2\u01da\u01e2\5J&\6\u01db\u01e2\5n8\2\u01dc\u01dd"+
		"\7.\2\2\u01dd\u01de\5J&\2\u01de\u01df\7/\2\2\u01df\u01e2\3\2\2\2\u01e0"+
		"\u01e2\5L\'\2\u01e1\u01d8\3\2\2\2\u01e1\u01db\3\2\2\2\u01e1\u01dc\3\2"+
		"\2\2\u01e1\u01e0\3\2\2\2\u01e2\u01eb\3\2\2\2\u01e3\u01e4\f\4\2\2\u01e4"+
		"\u01e5\7\35\2\2\u01e5\u01ea\5J&\5\u01e6\u01e7\f\3\2\2\u01e7\u01e8\7\36"+
		"\2\2\u01e8\u01ea\5J&\4\u01e9\u01e3\3\2\2\2\u01e9\u01e6\3\2\2\2\u01ea\u01ed"+
		"\3\2\2\2\u01eb\u01e9\3\2\2\2\u01eb\u01ec\3\2\2\2\u01ecK\3\2\2\2\u01ed"+
		"\u01eb\3\2\2\2\u01ee\u01ef\5P)\2\u01ef\u01f0\5N(\2\u01f0\u01f1\5P)\2\u01f1"+
		"\u01fb\3\2\2\2\u01f2\u01f3\5R*\2\u01f3\u01f4\5N(\2\u01f4\u01f5\5R*\2\u01f5"+
		"\u01fb\3\2\2\2\u01f6\u01f7\7.\2\2\u01f7\u01f8\5L\'\2\u01f8\u01f9\7/\2"+
		"\2\u01f9\u01fb\3\2\2\2\u01fa\u01ee\3\2\2\2\u01fa\u01f2\3\2\2\2\u01fa\u01f6"+
		"\3\2\2\2\u01fbM\3\2\2\2\u01fc\u01fd\t\b\2\2\u01fdO\3\2\2\2\u01fe\u01ff"+
		"\b)\1\2\u01ff\u0200\7\'\2\2\u0200\u0207\5P)\6\u0201\u0207\5p9\2\u0202"+
		"\u0203\7.\2\2\u0203\u0204\5P)\2\u0204\u0205\7/\2\2\u0205\u0207\3\2\2\2"+
		"\u0206\u01fe\3\2\2\2\u0206\u0201\3\2\2\2\u0206\u0202\3\2\2\2\u0207\u0213"+
		"\3\2\2\2\u0208\u0209\f\5\2\2\u0209\u020a\7\"\2\2\u020a\u0212\5P)\6\u020b"+
		"\u020c\f\4\2\2\u020c\u020d\t\t\2\2\u020d\u0212\5P)\5\u020e\u020f\f\3\2"+
		"\2\u020f\u0210\t\n\2\2\u0210\u0212\5P)\4\u0211\u0208\3\2\2\2\u0211\u020b"+
		"\3\2\2\2\u0211\u020e\3\2\2\2\u0212\u0215\3\2\2\2\u0213\u0211\3\2\2\2\u0213"+
		"\u0214\3\2\2\2\u0214Q\3\2\2\2\u0215\u0213\3\2\2\2\u0216\u0217\5r:\2\u0217"+
		"S\3\2\2\2\u0218\u021e\5Z.\2\u0219\u021e\5\\/\2\u021a\u021e\5^\60\2\u021b"+
		"\u021e\5d\63\2\u021c\u021e\5h\65\2\u021d\u0218\3\2\2\2\u021d\u0219\3\2"+
		"\2\2\u021d\u021a\3\2\2\2\u021d\u021b\3\2\2\2\u021d\u021c\3\2\2\2\u021e"+
		"U\3\2\2\2\u021f\u0222\5f\64\2\u0220\u0222\5b\62\2\u0221\u021f\3\2\2\2"+
		"\u0221\u0220\3\2\2\2\u0222W\3\2\2\2\u0223\u0224\5`\61\2\u0224Y\3\2\2\2"+
		"\u0225\u0226\7\61\2\2\u0226\u0227\7.\2\2\u0227\u0228\5r:\2\u0228\u0229"+
		"\7\60\2\2\u0229\u022a\7\33\2\2\u022a\u022b\7/\2\2\u022b[\3\2\2\2\u022c"+
		"\u022d\7\62\2\2\u022d\u022e\7.\2\2\u022e\u022f\7\33\2\2\u022f\u0230\7"+
		"/\2\2\u0230]\3\2\2\2\u0231\u0232\7\63\2\2\u0232\u0233\7.\2\2\u0233\u0234"+
		"\7\33\2\2\u0234\u0235\7/\2\2\u0235_\3\2\2\2\u0236\u0237\7\64\2\2\u0237"+
		"\u0238\7.\2\2\u0238\u0239\7\33\2\2\u0239\u023a\7/\2\2\u023aa\3\2\2\2\u023b"+
		"\u023c\7\65\2\2\u023c\u023d\7.\2\2\u023d\u023e\7\33\2\2\u023e\u023f\7"+
		"/\2\2\u023fc\3\2\2\2\u0240\u0241\7\66\2\2\u0241\u0242\7.\2\2\u0242\u0243"+
		"\7\33\2\2\u0243\u0244\7/\2\2\u0244e\3\2\2\2\u0245\u0246\7\67\2\2\u0246"+
		"\u0247\7.\2\2\u0247\u0248\7/\2\2\u0248g\3\2\2\2\u0249\u024a\78\2\2\u024a"+
		"\u024b\7.\2\2\u024b\u024c\5j\66\2\u024c\u024d\7/\2\2\u024di\3\2\2\2\u024e"+
		"\u024f\b\66\1\2\u024f\u0250\5J&\2\u0250\u0259\3\2\2\2\u0251\u0252\f\4"+
		"\2\2\u0252\u0253\7\25\2\2\u0253\u0258\5j\66\5\u0254\u0255\f\3\2\2\u0255"+
		"\u0256\7\26\2\2\u0256\u0258\5j\66\4\u0257\u0251\3\2\2\2\u0257\u0254\3"+
		"\2\2\2\u0258\u025b\3\2\2\2\u0259\u0257\3\2\2\2\u0259\u025a\3\2\2\2\u025a"+
		"k\3\2\2\2\u025b\u0259\3\2\2\2\u025c\u025d\t\13\2\2\u025dm\3\2\2\2\u025e"+
		"\u0263\5l\67\2\u025f\u0263\7D\2\2\u0260\u0263\7\32\2\2\u0261\u0263\5T"+
		"+\2\u0262\u025e\3\2\2\2\u0262\u025f\3\2\2\2\u0262\u0260\3\2\2\2\u0262"+
		"\u0261\3\2\2\2\u0263o\3\2\2\2\u0264\u026a\7\31\2\2\u0265\u026a\7\30\2"+
		"\2\u0266\u026a\7E\2\2\u0267\u026a\7\32\2\2\u0268\u026a\5X-\2\u0269\u0264"+
		"\3\2\2\2\u0269\u0265\3\2\2\2\u0269\u0266\3\2\2\2\u0269\u0267\3\2\2\2\u0269"+
		"\u0268\3\2\2\2\u026aq\3\2\2\2\u026b\u0270\7\33\2\2\u026c\u0270\7F\2\2"+
		"\u026d\u0270\7\32\2\2\u026e\u0270\5V,\2\u026f\u026b\3\2\2\2\u026f\u026c"+
		"\3\2\2\2\u026f\u026d\3\2\2\2\u026f\u026e\3\2\2\2\u0270s\3\2\2\2Muz\u0080"+
		"\u0084\u0086\u008c\u0091\u0098\u009d\u00a3\u00ab\u00b0\u00b4\u00b7\u00ba"+
		"\u00bf\u00c3\u00c8\u00cd\u00d1\u00d4\u00d9\u00dd\u00e2\u00ea\u00ef\u00f3"+
		"\u00f6\u00fb\u0100\u0108\u010d\u0111\u0114\u0119\u011f\u0124\u0129\u0131"+
		"\u0136\u013c\u0142\u0148\u014e\u0154\u015b\u0165\u0168\u016b\u016f\u0178"+
		"\u0180\u018e\u019a\u019f\u01a6\u01ad\u01b4\u01bb\u01c4\u01ce\u01e1\u01e9"+
		"\u01eb\u01fa\u0206\u0211\u0213\u021d\u0221\u0257\u0259\u0262\u0269\u026f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
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
		TAGNAME=1, FEATURE_KEYWORD=2, BACKGROUND_KEYWORD=3, SCENARIO_KEYWORD=4, 
		SCENARIO_OUTLINE_KEYWORD=5, EXAMPLES_KEYWORD=6, SELECTION_KEYWORD=7, ORACLE_KEYWORD=8, 
		STEP_KEYWORD=9, STEP_RANGE_KEYWORD=10, STEP_GIVEN_KEYWORD=11, STEP_WHEN_KEYWORD=12, 
		STEP_THEN_KEYWORD=13, STEP_ALSO_KEYWORD=14, STEP_EITHER_KEYWORD=15, TABLE_ROW=16, 
		DECIMAL_NUMBER=17, INTEGER_NUMBER=18, PLACEHOLDER=19, STRING=20, COMMENT=21, 
		AND=22, OR=23, NOT=24, TRUE=25, FALSE=26, POW=27, MULT=28, DIV=29, MOD=30, 
		PLUS=31, MINUS=32, GT=33, GE=34, LT=35, LE=36, EQ=37, NE=38, LPAREN=39, 
		RPAREN=40, COMMA=41, MATCHES_NAME=42, CLICK_NAME=43, TYPE_NAME=44, DRAG_NAME=45, 
		ANY_NAME=46, DOUBLE_CLICK_NAME=47, TRIPLE_CLICK_NAME=48, RIGHT_CLICK_NAME=49, 
		MOUSE_MOVE_NAME=50, DROP_DOWN_AT_NAME=51, BOOLEAN_VARIABLE=52, NUMBER_VARIABLE=53, 
		STRING_VARIABLE=54, EOL=55, WS=56, OTHER=57, BOOLEAN_VARIABLE_NAME=58, 
		NUMBER_VARIABLE_NAME=59, STRING_VARIABLE_NAME=60;
	public static final int
		RULE_document = 0, RULE_feature = 1, RULE_background = 2, RULE_scenarioDefiniton = 3, 
		RULE_scenario = 4, RULE_scenarioOutline = 5, RULE_examples = 6, RULE_table = 7, 
		RULE_title = 8, RULE_narrativeLine = 9, RULE_tagname = 10, RULE_selection = 11, 
		RULE_oracle = 12, RULE_step = 13, RULE_givenClause = 14, RULE_whenClause = 15, 
		RULE_thenClause = 16, RULE_stepRange = 17, RULE_widget_tree_condition = 18, 
		RULE_conditional_gesture = 19, RULE_gesture = 20, RULE_typeGesture = 21, 
		RULE_gestureName = 22, RULE_parameterlessGesture = 23, RULE_widget_condition = 24, 
		RULE_relational_expr = 25, RULE_relational_operator = 26, RULE_arithmetic_expr = 27, 
		RULE_string_expr = 28, RULE_booleanFunction = 29, RULE_matchesFunction = 30, 
		RULE_bool = 31, RULE_logical_entity = 32, RULE_numeric_entity = 33, RULE_string_entity = 34;
	public static final String[] ruleNames = {
		"document", "feature", "background", "scenarioDefiniton", "scenario", 
		"scenarioOutline", "examples", "table", "title", "narrativeLine", "tagname", 
		"selection", "oracle", "step", "givenClause", "whenClause", "thenClause", 
		"stepRange", "widget_tree_condition", "conditional_gesture", "gesture", 
		"typeGesture", "gestureName", "parameterlessGesture", "widget_condition", 
		"relational_expr", "relational_operator", "arithmetic_expr", "string_expr", 
		"booleanFunction", "matchesFunction", "bool", "logical_entity", "numeric_entity", 
		"string_entity"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'Feature:'", "'Background:'", "'Scenario:'", "'Scenario Outline:'", 
		"'Examples:'", "'Selection:'", "'Oracle:'", "'Step:'", "'Range'", "'Given'", 
		"'When'", "'Then'", "'Also'", "'Either'", null, null, null, null, null, 
		null, "'and'", "'or'", null, "'true'", "'false'", "'^'", "'*'", "'/'", 
		"'%'", "'+'", "'-'", "'>'", "'>='", "'<'", "'<='", "'='", null, "'('", 
		"')'", "','", "'matches'", "'click'", "'type'", "'drag'", "'anyGesture'", 
		"'doubleClick'", "'tripleClick'", "'rightClick'", "'mouseMove'", "'dropDownAt'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "TAGNAME", "FEATURE_KEYWORD", "BACKGROUND_KEYWORD", "SCENARIO_KEYWORD", 
		"SCENARIO_OUTLINE_KEYWORD", "EXAMPLES_KEYWORD", "SELECTION_KEYWORD", "ORACLE_KEYWORD", 
		"STEP_KEYWORD", "STEP_RANGE_KEYWORD", "STEP_GIVEN_KEYWORD", "STEP_WHEN_KEYWORD", 
		"STEP_THEN_KEYWORD", "STEP_ALSO_KEYWORD", "STEP_EITHER_KEYWORD", "TABLE_ROW", 
		"DECIMAL_NUMBER", "INTEGER_NUMBER", "PLACEHOLDER", "STRING", "COMMENT", 
		"AND", "OR", "NOT", "TRUE", "FALSE", "POW", "MULT", "DIV", "MOD", "PLUS", 
		"MINUS", "GT", "GE", "LT", "LE", "EQ", "NE", "LPAREN", "RPAREN", "COMMA", 
		"MATCHES_NAME", "CLICK_NAME", "TYPE_NAME", "DRAG_NAME", "ANY_NAME", "DOUBLE_CLICK_NAME", 
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
			setState(71); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(70);
				feature();
				}
				}
				setState(73); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TAGNAME || _la==FEATURE_KEYWORD );
			setState(75);
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
		enterRule(_localctx, 2, RULE_feature);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(77);
				((FeatureContext)_localctx).tagname = tagname();
				((FeatureContext)_localctx).tags.add(((FeatureContext)_localctx).tagname);
				}
				}
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(83);
			match(FEATURE_KEYWORD);
			setState(84);
			title();
			setState(86); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(85);
				match(EOL);
				}
				}
				setState(88); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(90);
				((FeatureContext)_localctx).narrativeLine = narrativeLine();
				((FeatureContext)_localctx).narrative.add(((FeatureContext)_localctx).narrativeLine);
				}
				}
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(97);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(96);
				selection();
				}
			}

			setState(100);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(99);
				oracle();
				}
			}

			setState(103);
			_la = _input.LA(1);
			if (_la==BACKGROUND_KEYWORD) {
				{
				setState(102);
				background();
				}
			}

			setState(106); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(105);
					scenarioDefiniton();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(108); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
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
		enterRule(_localctx, 4, RULE_background);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(BACKGROUND_KEYWORD);
			setState(112);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				setState(111);
				title();
				}
			}

			setState(115); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(114);
				match(EOL);
				}
				}
				setState(117); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(119);
				((BackgroundContext)_localctx).narrativeLine = narrativeLine();
				((BackgroundContext)_localctx).narrative.add(((BackgroundContext)_localctx).narrativeLine);
				}
				}
				setState(124);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(126);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(125);
				selection();
				}
			}

			setState(129);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(128);
				oracle();
				}
			}

			setState(132); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(131);
				((BackgroundContext)_localctx).step = step();
				((BackgroundContext)_localctx).steps.add(((BackgroundContext)_localctx).step);
				}
				}
				setState(134); 
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
		enterRule(_localctx, 6, RULE_scenarioDefiniton);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(136);
				scenario();
				}
				break;
			case 2:
				{
				setState(137);
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
		enterRule(_localctx, 8, RULE_scenario);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(140);
				((ScenarioContext)_localctx).tagname = tagname();
				((ScenarioContext)_localctx).tags.add(((ScenarioContext)_localctx).tagname);
				}
				}
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(146);
			match(SCENARIO_KEYWORD);
			setState(147);
			title();
			setState(149); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(148);
				match(EOL);
				}
				}
				setState(151); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(156);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(153);
				((ScenarioContext)_localctx).narrativeLine = narrativeLine();
				((ScenarioContext)_localctx).narrative.add(((ScenarioContext)_localctx).narrativeLine);
				}
				}
				setState(158);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(160);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(159);
				selection();
				}
			}

			setState(163);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(162);
				oracle();
				}
			}

			setState(166); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(165);
				((ScenarioContext)_localctx).step = step();
				((ScenarioContext)_localctx).steps.add(((ScenarioContext)_localctx).step);
				}
				}
				setState(168); 
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
		enterRule(_localctx, 10, RULE_scenarioOutline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TAGNAME) {
				{
				{
				setState(170);
				((ScenarioOutlineContext)_localctx).tagname = tagname();
				((ScenarioOutlineContext)_localctx).tags.add(((ScenarioOutlineContext)_localctx).tagname);
				}
				}
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(176);
			match(SCENARIO_OUTLINE_KEYWORD);
			setState(177);
			title();
			setState(179); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(178);
				match(EOL);
				}
				}
				setState(181); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(183);
				((ScenarioOutlineContext)_localctx).narrativeLine = narrativeLine();
				((ScenarioOutlineContext)_localctx).narrative.add(((ScenarioOutlineContext)_localctx).narrativeLine);
				}
				}
				setState(188);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(190);
			_la = _input.LA(1);
			if (_la==SELECTION_KEYWORD) {
				{
				setState(189);
				selection();
				}
			}

			setState(193);
			_la = _input.LA(1);
			if (_la==ORACLE_KEYWORD) {
				{
				setState(192);
				oracle();
				}
			}

			setState(196); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(195);
				((ScenarioOutlineContext)_localctx).step = step();
				((ScenarioOutlineContext)_localctx).steps.add(((ScenarioOutlineContext)_localctx).step);
				}
				}
				setState(198); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STEP_KEYWORD );
			setState(200);
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
		enterRule(_localctx, 12, RULE_examples);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			match(EXAMPLES_KEYWORD);
			setState(204);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				setState(203);
				title();
				}
			}

			setState(207); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(206);
				match(EOL);
				}
				}
				setState(209); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0)) {
				{
				{
				setState(211);
				((ExamplesContext)_localctx).narrativeLine = narrativeLine();
				((ExamplesContext)_localctx).narrative.add(((ExamplesContext)_localctx).narrativeLine);
				}
				}
				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(217);
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
		enterRule(_localctx, 14, RULE_table);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(220); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(219);
				((TableContext)_localctx).TABLE_ROW = match(TABLE_ROW);
				((TableContext)_localctx).rows.add(((TableContext)_localctx).TABLE_ROW);
				}
				}
				setState(222); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TABLE_ROW );
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(224);
				match(EOL);
				}
				}
				setState(229);
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
		enterRule(_localctx, 16, RULE_title);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(230);
					match(WS);
					}
					} 
				}
				setState(235);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			}
			setState(237); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(236);
				_la = _input.LA(1);
				if ( _la <= 0 || (_la==EOL) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				}
				setState(239); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << COMMENT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << POW) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << LPAREN) | (1L << RPAREN) | (1L << COMMA) | (1L << MATCHES_NAME) | (1L << CLICK_NAME) | (1L << TYPE_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE) | (1L << WS) | (1L << OTHER) | (1L << BOOLEAN_VARIABLE_NAME) | (1L << NUMBER_VARIABLE_NAME) | (1L << STRING_VARIABLE_NAME))) != 0) );
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
		enterRule(_localctx, 18, RULE_narrativeLine);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			_la = _input.LA(1);
			if ( _la <= 0 || ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TAGNAME) | (1L << FEATURE_KEYWORD) | (1L << BACKGROUND_KEYWORD) | (1L << SCENARIO_KEYWORD) | (1L << SCENARIO_OUTLINE_KEYWORD) | (1L << EXAMPLES_KEYWORD) | (1L << SELECTION_KEYWORD) | (1L << ORACLE_KEYWORD) | (1L << STEP_KEYWORD) | (1L << STEP_RANGE_KEYWORD) | (1L << STEP_GIVEN_KEYWORD) | (1L << STEP_WHEN_KEYWORD) | (1L << STEP_THEN_KEYWORD) | (1L << STEP_ALSO_KEYWORD) | (1L << STEP_EITHER_KEYWORD) | (1L << TABLE_ROW) | (1L << EOL))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(245);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(242);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==EOL) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					} 
				}
				setState(247);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			}
			setState(251);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(248);
				match(EOL);
				}
				}
				setState(253);
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
		enterRule(_localctx, 20, RULE_tagname);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(254);
			match(TAGNAME);
			setState(258);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOL) {
				{
				{
				setState(255);
				match(EOL);
				}
				}
				setState(260);
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
		enterRule(_localctx, 22, RULE_selection);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(SELECTION_KEYWORD);
			setState(263); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(262);
				((SelectionContext)_localctx).conditional_gesture = conditional_gesture();
				((SelectionContext)_localctx).conditional_gestures.add(((SelectionContext)_localctx).conditional_gesture);
				}
				}
				setState(265); 
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
		enterRule(_localctx, 24, RULE_oracle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267);
			match(ORACLE_KEYWORD);
			setState(268);
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
		enterRule(_localctx, 26, RULE_step);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(270);
			match(STEP_KEYWORD);
			setState(271);
			title();
			setState(273); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(272);
				match(EOL);
				}
				}
				setState(275); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==EOL );
			setState(278);
			_la = _input.LA(1);
			if (_la==STEP_RANGE_KEYWORD) {
				{
				setState(277);
				stepRange();
				}
			}

			setState(281);
			_la = _input.LA(1);
			if (_la==STEP_GIVEN_KEYWORD) {
				{
				setState(280);
				givenClause();
				}
			}

			setState(283);
			whenClause();
			setState(285);
			_la = _input.LA(1);
			if (_la==STEP_THEN_KEYWORD) {
				{
				setState(284);
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
		enterRule(_localctx, 28, RULE_givenClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(287);
			match(STEP_GIVEN_KEYWORD);
			setState(288);
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
		enterRule(_localctx, 30, RULE_whenClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			match(STEP_WHEN_KEYWORD);
			setState(292); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(291);
				((WhenClauseContext)_localctx).conditional_gesture = conditional_gesture();
				((WhenClauseContext)_localctx).conditional_gestures.add(((WhenClauseContext)_localctx).conditional_gesture);
				}
				}
				setState(294); 
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
		enterRule(_localctx, 32, RULE_thenClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
			match(STEP_THEN_KEYWORD);
			setState(297);
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
		enterRule(_localctx, 34, RULE_stepRange);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			match(STEP_RANGE_KEYWORD);
			setState(300);
			((StepRangeContext)_localctx).from = match(INTEGER_NUMBER);
			setState(301);
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
		int _startState = 36;
		enterRecursionRule(_localctx, 36, RULE_widget_tree_condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new WidgetConditionContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(304);
			widget_condition(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(314);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(312);
					switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetTreeConditionAlsoContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionAlsoContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(306);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(307);
						match(STEP_ALSO_KEYWORD);
						setState(308);
						((WidgetTreeConditionAlsoContext)_localctx).right = widget_tree_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetTreeConditionEitherContext(new Widget_tree_conditionContext(_parentctx, _parentState));
						((WidgetTreeConditionEitherContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_tree_condition);
						setState(309);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(310);
						match(STEP_EITHER_KEYWORD);
						setState(311);
						((WidgetTreeConditionEitherContext)_localctx).right = widget_tree_condition(2);
						}
						break;
					}
					} 
				}
				setState(316);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
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
		enterRule(_localctx, 38, RULE_conditional_gesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(318);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DECIMAL_NUMBER) | (1L << INTEGER_NUMBER) | (1L << PLACEHOLDER) | (1L << STRING) | (1L << NOT) | (1L << TRUE) | (1L << FALSE) | (1L << MINUS) | (1L << LPAREN) | (1L << MATCHES_NAME) | (1L << BOOLEAN_VARIABLE) | (1L << NUMBER_VARIABLE) | (1L << STRING_VARIABLE))) != 0)) {
				{
				setState(317);
				widget_condition(0);
				}
			}

			setState(320);
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
		enterRule(_localctx, 40, RULE_gesture);
		try {
			setState(324);
			switch (_input.LA(1)) {
			case CLICK_NAME:
			case DRAG_NAME:
			case ANY_NAME:
			case DOUBLE_CLICK_NAME:
			case TRIPLE_CLICK_NAME:
			case RIGHT_CLICK_NAME:
			case MOUSE_MOVE_NAME:
			case DROP_DOWN_AT_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(322);
				parameterlessGesture();
				}
				break;
			case TYPE_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(323);
				typeGesture();
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
		enterRule(_localctx, 42, RULE_typeGesture);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			match(TYPE_NAME);
			setState(327);
			match(LPAREN);
			setState(329);
			_la = _input.LA(1);
			if (_la==PLACEHOLDER || _la==STRING) {
				{
				setState(328);
				_la = _input.LA(1);
				if ( !(_la==PLACEHOLDER || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(331);
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
		public TerminalNode CLICK_NAME() { return getToken(TgherkinParser.CLICK_NAME, 0); }
		public TerminalNode DRAG_NAME() { return getToken(TgherkinParser.DRAG_NAME, 0); }
		public TerminalNode ANY_NAME() { return getToken(TgherkinParser.ANY_NAME, 0); }
		public TerminalNode DOUBLE_CLICK_NAME() { return getToken(TgherkinParser.DOUBLE_CLICK_NAME, 0); }
		public TerminalNode TRIPLE_CLICK_NAME() { return getToken(TgherkinParser.TRIPLE_CLICK_NAME, 0); }
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
		enterRule(_localctx, 44, RULE_gestureName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(333);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CLICK_NAME) | (1L << DRAG_NAME) | (1L << ANY_NAME) | (1L << DOUBLE_CLICK_NAME) | (1L << TRIPLE_CLICK_NAME) | (1L << RIGHT_CLICK_NAME) | (1L << MOUSE_MOVE_NAME) | (1L << DROP_DOWN_AT_NAME))) != 0)) ) {
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
		enterRule(_localctx, 46, RULE_parameterlessGesture);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			gestureName();
			setState(336);
			match(LPAREN);
			setState(337);
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
		int _startState = 48;
		enterRecursionRule(_localctx, 48, RULE_widget_condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				{
				_localctx = new NegationWidgetConditionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(340);
				match(NOT);
				setState(341);
				widget_condition(4);
				}
				break;
			case 2:
				{
				_localctx = new LogicalEntityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(342);
				logical_entity();
				}
				break;
			case 3:
				{
				_localctx = new WidgetConditionInParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(343);
				match(LPAREN);
				setState(344);
				widget_condition(0);
				setState(345);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new RelationalExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(347);
				relational_expr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(358);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(356);
					switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
					case 1:
						{
						_localctx = new WidgetConditionAndContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(350);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(351);
						match(AND);
						setState(352);
						((WidgetConditionAndContext)_localctx).right = widget_condition(3);
						}
						break;
					case 2:
						{
						_localctx = new WidgetConditionOrContext(new Widget_conditionContext(_parentctx, _parentState));
						((WidgetConditionOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_widget_condition);
						setState(353);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(354);
						match(OR);
						setState(355);
						((WidgetConditionOrContext)_localctx).right = widget_condition(2);
						}
						break;
					}
					} 
				}
				setState(360);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
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
		enterRule(_localctx, 50, RULE_relational_expr);
		try {
			setState(373);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				_localctx = new RelationalNumericExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(361);
				((RelationalNumericExpressionWithOperatorContext)_localctx).left = arithmetic_expr(0);
				setState(362);
				relational_operator();
				setState(363);
				((RelationalNumericExpressionWithOperatorContext)_localctx).right = arithmetic_expr(0);
				}
				break;
			case 2:
				_localctx = new RelationalStringExpressionWithOperatorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(365);
				((RelationalStringExpressionWithOperatorContext)_localctx).left = string_expr();
				setState(366);
				relational_operator();
				setState(367);
				((RelationalStringExpressionWithOperatorContext)_localctx).right = string_expr();
				}
				break;
			case 3:
				_localctx = new RelationalExpressionParensContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(369);
				match(LPAREN);
				setState(370);
				relational_expr();
				setState(371);
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
		enterRule(_localctx, 52, RULE_relational_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(375);
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
		int _startState = 54;
		enterRecursionRule(_localctx, 54, RULE_arithmetic_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			switch (_input.LA(1)) {
			case MINUS:
				{
				_localctx = new ArithmeticExpressionNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(378);
				match(MINUS);
				setState(379);
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
				setState(380);
				numeric_entity();
				}
				break;
			case LPAREN:
				{
				_localctx = new ArithmeticExpressionParensContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(381);
				match(LPAREN);
				setState(382);
				arithmetic_expr(0);
				setState(383);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(398);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(396);
					switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticExpressionPowContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPowContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(387);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(388);
						match(POW);
						setState(389);
						((ArithmeticExpressionPowContext)_localctx).right = arithmetic_expr(4);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticExpressionMultDivModContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionMultDivModContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(390);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(391);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(392);
						((ArithmeticExpressionMultDivModContext)_localctx).right = arithmetic_expr(3);
						}
						break;
					case 3:
						{
						_localctx = new ArithmeticExpressionPlusMinusContext(new Arithmetic_exprContext(_parentctx, _parentState));
						((ArithmeticExpressionPlusMinusContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_arithmetic_expr);
						setState(393);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(394);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(395);
						((ArithmeticExpressionPlusMinusContext)_localctx).right = arithmetic_expr(2);
						}
						break;
					}
					} 
				}
				setState(400);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
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
		enterRule(_localctx, 56, RULE_string_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(401);
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
		enterRule(_localctx, 58, RULE_booleanFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(403);
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
		enterRule(_localctx, 60, RULE_matchesFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(405);
			match(MATCHES_NAME);
			setState(406);
			match(LPAREN);
			setState(407);
			match(STRING_VARIABLE);
			setState(408);
			match(COMMA);
			setState(409);
			match(STRING);
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
		enterRule(_localctx, 62, RULE_bool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
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
		enterRule(_localctx, 64, RULE_logical_entity);
		try {
			setState(418);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
				_localctx = new LogicalConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(414);
				bool();
				}
				break;
			case BOOLEAN_VARIABLE:
				_localctx = new LogicalVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(415);
				match(BOOLEAN_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new LogicalPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(416);
				match(PLACEHOLDER);
				}
				break;
			case MATCHES_NAME:
				_localctx = new LogicalFunctionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(417);
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
		enterRule(_localctx, 66, RULE_numeric_entity);
		try {
			setState(424);
			switch (_input.LA(1)) {
			case INTEGER_NUMBER:
				_localctx = new IntegerConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(420);
				match(INTEGER_NUMBER);
				}
				break;
			case DECIMAL_NUMBER:
				_localctx = new DecimalConstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(421);
				match(DECIMAL_NUMBER);
				}
				break;
			case NUMBER_VARIABLE:
				_localctx = new NumericVariableContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(422);
				match(NUMBER_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new NumericPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(423);
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
		enterRule(_localctx, 68, RULE_string_entity);
		try {
			setState(429);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(426);
				match(STRING);
				}
				break;
			case STRING_VARIABLE:
				_localctx = new StringVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(427);
				match(STRING_VARIABLE);
				}
				break;
			case PLACEHOLDER:
				_localctx = new StringPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(428);
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
		case 18:
			return widget_tree_condition_sempred((Widget_tree_conditionContext)_localctx, predIndex);
		case 24:
			return widget_condition_sempred((Widget_conditionContext)_localctx, predIndex);
		case 27:
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3>\u01b2\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\3\2\6\2J\n\2\r\2\16\2K\3\2\3\2\3\3\7\3Q\n\3\f"+
		"\3\16\3T\13\3\3\3\3\3\3\3\6\3Y\n\3\r\3\16\3Z\3\3\7\3^\n\3\f\3\16\3a\13"+
		"\3\3\3\5\3d\n\3\3\3\5\3g\n\3\3\3\5\3j\n\3\3\3\6\3m\n\3\r\3\16\3n\3\4\3"+
		"\4\5\4s\n\4\3\4\6\4v\n\4\r\4\16\4w\3\4\7\4{\n\4\f\4\16\4~\13\4\3\4\5\4"+
		"\u0081\n\4\3\4\5\4\u0084\n\4\3\4\6\4\u0087\n\4\r\4\16\4\u0088\3\5\3\5"+
		"\5\5\u008d\n\5\3\6\7\6\u0090\n\6\f\6\16\6\u0093\13\6\3\6\3\6\3\6\6\6\u0098"+
		"\n\6\r\6\16\6\u0099\3\6\7\6\u009d\n\6\f\6\16\6\u00a0\13\6\3\6\5\6\u00a3"+
		"\n\6\3\6\5\6\u00a6\n\6\3\6\6\6\u00a9\n\6\r\6\16\6\u00aa\3\7\7\7\u00ae"+
		"\n\7\f\7\16\7\u00b1\13\7\3\7\3\7\3\7\6\7\u00b6\n\7\r\7\16\7\u00b7\3\7"+
		"\7\7\u00bb\n\7\f\7\16\7\u00be\13\7\3\7\5\7\u00c1\n\7\3\7\5\7\u00c4\n\7"+
		"\3\7\6\7\u00c7\n\7\r\7\16\7\u00c8\3\7\3\7\3\b\3\b\5\b\u00cf\n\b\3\b\6"+
		"\b\u00d2\n\b\r\b\16\b\u00d3\3\b\7\b\u00d7\n\b\f\b\16\b\u00da\13\b\3\b"+
		"\3\b\3\t\6\t\u00df\n\t\r\t\16\t\u00e0\3\t\7\t\u00e4\n\t\f\t\16\t\u00e7"+
		"\13\t\3\n\7\n\u00ea\n\n\f\n\16\n\u00ed\13\n\3\n\6\n\u00f0\n\n\r\n\16\n"+
		"\u00f1\3\13\3\13\7\13\u00f6\n\13\f\13\16\13\u00f9\13\13\3\13\7\13\u00fc"+
		"\n\13\f\13\16\13\u00ff\13\13\3\f\3\f\7\f\u0103\n\f\f\f\16\f\u0106\13\f"+
		"\3\r\3\r\6\r\u010a\n\r\r\r\16\r\u010b\3\16\3\16\3\16\3\17\3\17\3\17\6"+
		"\17\u0114\n\17\r\17\16\17\u0115\3\17\5\17\u0119\n\17\3\17\5\17\u011c\n"+
		"\17\3\17\3\17\5\17\u0120\n\17\3\20\3\20\3\20\3\21\3\21\6\21\u0127\n\21"+
		"\r\21\16\21\u0128\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3"+
		"\24\3\24\3\24\3\24\3\24\3\24\7\24\u013b\n\24\f\24\16\24\u013e\13\24\3"+
		"\25\5\25\u0141\n\25\3\25\3\25\3\26\3\26\5\26\u0147\n\26\3\27\3\27\3\27"+
		"\5\27\u014c\n\27\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3\31\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u015f\n\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\7\32\u0167\n\32\f\32\16\32\u016a\13\32\3\33\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u0178\n\33\3\34\3\34\3\35\3\35"+
		"\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u0184\n\35\3\35\3\35\3\35\3\35\3\35"+
		"\3\35\3\35\3\35\3\35\7\35\u018f\n\35\f\35\16\35\u0192\13\35\3\36\3\36"+
		"\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3!\3!\3\"\3\"\3\"\3\"\5\"\u01a5\n\"\3"+
		"#\3#\3#\3#\5#\u01ab\n#\3$\3$\3$\5$\u01b0\n$\3$\2\5&\628%\2\4\6\b\n\f\16"+
		"\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDF\2\n\3\299\4\2\3"+
		"\2299\3\2\25\26\4\2--/\65\3\2#(\3\2\36 \3\2!\"\3\2\33\34\u01d2\2I\3\2"+
		"\2\2\4R\3\2\2\2\6p\3\2\2\2\b\u008c\3\2\2\2\n\u0091\3\2\2\2\f\u00af\3\2"+
		"\2\2\16\u00cc\3\2\2\2\20\u00de\3\2\2\2\22\u00eb\3\2\2\2\24\u00f3\3\2\2"+
		"\2\26\u0100\3\2\2\2\30\u0107\3\2\2\2\32\u010d\3\2\2\2\34\u0110\3\2\2\2"+
		"\36\u0121\3\2\2\2 \u0124\3\2\2\2\"\u012a\3\2\2\2$\u012d\3\2\2\2&\u0131"+
		"\3\2\2\2(\u0140\3\2\2\2*\u0146\3\2\2\2,\u0148\3\2\2\2.\u014f\3\2\2\2\60"+
		"\u0151\3\2\2\2\62\u015e\3\2\2\2\64\u0177\3\2\2\2\66\u0179\3\2\2\28\u0183"+
		"\3\2\2\2:\u0193\3\2\2\2<\u0195\3\2\2\2>\u0197\3\2\2\2@\u019e\3\2\2\2B"+
		"\u01a4\3\2\2\2D\u01aa\3\2\2\2F\u01af\3\2\2\2HJ\5\4\3\2IH\3\2\2\2JK\3\2"+
		"\2\2KI\3\2\2\2KL\3\2\2\2LM\3\2\2\2MN\7\2\2\3N\3\3\2\2\2OQ\5\26\f\2PO\3"+
		"\2\2\2QT\3\2\2\2RP\3\2\2\2RS\3\2\2\2SU\3\2\2\2TR\3\2\2\2UV\7\4\2\2VX\5"+
		"\22\n\2WY\79\2\2XW\3\2\2\2YZ\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2[_\3\2\2\2\\^"+
		"\5\24\13\2]\\\3\2\2\2^a\3\2\2\2_]\3\2\2\2_`\3\2\2\2`c\3\2\2\2a_\3\2\2"+
		"\2bd\5\30\r\2cb\3\2\2\2cd\3\2\2\2df\3\2\2\2eg\5\32\16\2fe\3\2\2\2fg\3"+
		"\2\2\2gi\3\2\2\2hj\5\6\4\2ih\3\2\2\2ij\3\2\2\2jl\3\2\2\2km\5\b\5\2lk\3"+
		"\2\2\2mn\3\2\2\2nl\3\2\2\2no\3\2\2\2o\5\3\2\2\2pr\7\5\2\2qs\5\22\n\2r"+
		"q\3\2\2\2rs\3\2\2\2su\3\2\2\2tv\79\2\2ut\3\2\2\2vw\3\2\2\2wu\3\2\2\2w"+
		"x\3\2\2\2x|\3\2\2\2y{\5\24\13\2zy\3\2\2\2{~\3\2\2\2|z\3\2\2\2|}\3\2\2"+
		"\2}\u0080\3\2\2\2~|\3\2\2\2\177\u0081\5\30\r\2\u0080\177\3\2\2\2\u0080"+
		"\u0081\3\2\2\2\u0081\u0083\3\2\2\2\u0082\u0084\5\32\16\2\u0083\u0082\3"+
		"\2\2\2\u0083\u0084\3\2\2\2\u0084\u0086\3\2\2\2\u0085\u0087\5\34\17\2\u0086"+
		"\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2"+
		"\2\2\u0089\7\3\2\2\2\u008a\u008d\5\n\6\2\u008b\u008d\5\f\7\2\u008c\u008a"+
		"\3\2\2\2\u008c\u008b\3\2\2\2\u008d\t\3\2\2\2\u008e\u0090\5\26\f\2\u008f"+
		"\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2"+
		"\2\2\u0092\u0094\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0095\7\6\2\2\u0095"+
		"\u0097\5\22\n\2\u0096\u0098\79\2\2\u0097\u0096\3\2\2\2\u0098\u0099\3\2"+
		"\2\2\u0099\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009e\3\2\2\2\u009b"+
		"\u009d\5\24\13\2\u009c\u009b\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3"+
		"\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1"+
		"\u00a3\5\30\r\2\u00a2\u00a1\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a5\3"+
		"\2\2\2\u00a4\u00a6\5\32\16\2\u00a5\u00a4\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6"+
		"\u00a8\3\2\2\2\u00a7\u00a9\5\34\17\2\u00a8\u00a7\3\2\2\2\u00a9\u00aa\3"+
		"\2\2\2\u00aa\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\13\3\2\2\2\u00ac"+
		"\u00ae\5\26\f\2\u00ad\u00ac\3\2\2\2\u00ae\u00b1\3\2\2\2\u00af\u00ad\3"+
		"\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b2\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2"+
		"\u00b3\7\7\2\2\u00b3\u00b5\5\22\n\2\u00b4\u00b6\79\2\2\u00b5\u00b4\3\2"+
		"\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8"+
		"\u00bc\3\2\2\2\u00b9\u00bb\5\24\13\2\u00ba\u00b9\3\2\2\2\u00bb\u00be\3"+
		"\2\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be"+
		"\u00bc\3\2\2\2\u00bf\u00c1\5\30\r\2\u00c0\u00bf\3\2\2\2\u00c0\u00c1\3"+
		"\2\2\2\u00c1\u00c3\3\2\2\2\u00c2\u00c4\5\32\16\2\u00c3\u00c2\3\2\2\2\u00c3"+
		"\u00c4\3\2\2\2\u00c4\u00c6\3\2\2\2\u00c5\u00c7\5\34\17\2\u00c6\u00c5\3"+
		"\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9"+
		"\u00ca\3\2\2\2\u00ca\u00cb\5\16\b\2\u00cb\r\3\2\2\2\u00cc\u00ce\7\b\2"+
		"\2\u00cd\u00cf\5\22\n\2\u00ce\u00cd\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf"+
		"\u00d1\3\2\2\2\u00d0\u00d2\79\2\2\u00d1\u00d0\3\2\2\2\u00d2\u00d3\3\2"+
		"\2\2\u00d3\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d8\3\2\2\2\u00d5"+
		"\u00d7\5\24\13\2\u00d6\u00d5\3\2\2\2\u00d7\u00da\3\2\2\2\u00d8\u00d6\3"+
		"\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00db\3\2\2\2\u00da\u00d8\3\2\2\2\u00db"+
		"\u00dc\5\20\t\2\u00dc\17\3\2\2\2\u00dd\u00df\7\22\2\2\u00de\u00dd\3\2"+
		"\2\2\u00df\u00e0\3\2\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\u00e5\3\2\2\2\u00e2\u00e4\79\2\2\u00e3\u00e2\3\2\2\2\u00e4\u00e7\3\2"+
		"\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\21\3\2\2\2\u00e7\u00e5"+
		"\3\2\2\2\u00e8\u00ea\7:\2\2\u00e9\u00e8\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb"+
		"\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb\3\2"+
		"\2\2\u00ee\u00f0\n\2\2\2\u00ef\u00ee\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1"+
		"\u00ef\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\23\3\2\2\2\u00f3\u00f7\n\3\2"+
		"\2\u00f4\u00f6\n\2\2\2\u00f5\u00f4\3\2\2\2\u00f6\u00f9\3\2\2\2\u00f7\u00f5"+
		"\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00fd\3\2\2\2\u00f9\u00f7\3\2\2\2\u00fa"+
		"\u00fc\79\2\2\u00fb\u00fa\3\2\2\2\u00fc\u00ff\3\2\2\2\u00fd\u00fb\3\2"+
		"\2\2\u00fd\u00fe\3\2\2\2\u00fe\25\3\2\2\2\u00ff\u00fd\3\2\2\2\u0100\u0104"+
		"\7\3\2\2\u0101\u0103\79\2\2\u0102\u0101\3\2\2\2\u0103\u0106\3\2\2\2\u0104"+
		"\u0102\3\2\2\2\u0104\u0105\3\2\2\2\u0105\27\3\2\2\2\u0106\u0104\3\2\2"+
		"\2\u0107\u0109\7\t\2\2\u0108\u010a\5(\25\2\u0109\u0108\3\2\2\2\u010a\u010b"+
		"\3\2\2\2\u010b\u0109\3\2\2\2\u010b\u010c\3\2\2\2\u010c\31\3\2\2\2\u010d"+
		"\u010e\7\n\2\2\u010e\u010f\5&\24\2\u010f\33\3\2\2\2\u0110\u0111\7\13\2"+
		"\2\u0111\u0113\5\22\n\2\u0112\u0114\79\2\2\u0113\u0112\3\2\2\2\u0114\u0115"+
		"\3\2\2\2\u0115\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0118\3\2\2\2\u0117"+
		"\u0119\5$\23\2\u0118\u0117\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011b\3\2"+
		"\2\2\u011a\u011c\5\36\20\2\u011b\u011a\3\2\2\2\u011b\u011c\3\2\2\2\u011c"+
		"\u011d\3\2\2\2\u011d\u011f\5 \21\2\u011e\u0120\5\"\22\2\u011f\u011e\3"+
		"\2\2\2\u011f\u0120\3\2\2\2\u0120\35\3\2\2\2\u0121\u0122\7\r\2\2\u0122"+
		"\u0123\5&\24\2\u0123\37\3\2\2\2\u0124\u0126\7\16\2\2\u0125\u0127\5(\25"+
		"\2\u0126\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0126\3\2\2\2\u0128\u0129"+
		"\3\2\2\2\u0129!\3\2\2\2\u012a\u012b\7\17\2\2\u012b\u012c\5&\24\2\u012c"+
		"#\3\2\2\2\u012d\u012e\7\f\2\2\u012e\u012f\7\24\2\2\u012f\u0130\7\24\2"+
		"\2\u0130%\3\2\2\2\u0131\u0132\b\24\1\2\u0132\u0133\5\62\32\2\u0133\u013c"+
		"\3\2\2\2\u0134\u0135\f\4\2\2\u0135\u0136\7\20\2\2\u0136\u013b\5&\24\5"+
		"\u0137\u0138\f\3\2\2\u0138\u0139\7\21\2\2\u0139\u013b\5&\24\4\u013a\u0134"+
		"\3\2\2\2\u013a\u0137\3\2\2\2\u013b\u013e\3\2\2\2\u013c\u013a\3\2\2\2\u013c"+
		"\u013d\3\2\2\2\u013d\'\3\2\2\2\u013e\u013c\3\2\2\2\u013f\u0141\5\62\32"+
		"\2\u0140\u013f\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0143"+
		"\5*\26\2\u0143)\3\2\2\2\u0144\u0147\5\60\31\2\u0145\u0147\5,\27\2\u0146"+
		"\u0144\3\2\2\2\u0146\u0145\3\2\2\2\u0147+\3\2\2\2\u0148\u0149\7.\2\2\u0149"+
		"\u014b\7)\2\2\u014a\u014c\t\4\2\2\u014b\u014a\3\2\2\2\u014b\u014c\3\2"+
		"\2\2\u014c\u014d\3\2\2\2\u014d\u014e\7*\2\2\u014e-\3\2\2\2\u014f\u0150"+
		"\t\5\2\2\u0150/\3\2\2\2\u0151\u0152\5.\30\2\u0152\u0153\7)\2\2\u0153\u0154"+
		"\7*\2\2\u0154\61\3\2\2\2\u0155\u0156\b\32\1\2\u0156\u0157\7\32\2\2\u0157"+
		"\u015f\5\62\32\6\u0158\u015f\5B\"\2\u0159\u015a\7)\2\2\u015a\u015b\5\62"+
		"\32\2\u015b\u015c\7*\2\2\u015c\u015f\3\2\2\2\u015d\u015f\5\64\33\2\u015e"+
		"\u0155\3\2\2\2\u015e\u0158\3\2\2\2\u015e\u0159\3\2\2\2\u015e\u015d\3\2"+
		"\2\2\u015f\u0168\3\2\2\2\u0160\u0161\f\4\2\2\u0161\u0162\7\30\2\2\u0162"+
		"\u0167\5\62\32\5\u0163\u0164\f\3\2\2\u0164\u0165\7\31\2\2\u0165\u0167"+
		"\5\62\32\4\u0166\u0160\3\2\2\2\u0166\u0163\3\2\2\2\u0167\u016a\3\2\2\2"+
		"\u0168\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169\63\3\2\2\2\u016a\u0168"+
		"\3\2\2\2\u016b\u016c\58\35\2\u016c\u016d\5\66\34\2\u016d\u016e\58\35\2"+
		"\u016e\u0178\3\2\2\2\u016f\u0170\5:\36\2\u0170\u0171\5\66\34\2\u0171\u0172"+
		"\5:\36\2\u0172\u0178\3\2\2\2\u0173\u0174\7)\2\2\u0174\u0175\5\64\33\2"+
		"\u0175\u0176\7*\2\2\u0176\u0178\3\2\2\2\u0177\u016b\3\2\2\2\u0177\u016f"+
		"\3\2\2\2\u0177\u0173\3\2\2\2\u0178\65\3\2\2\2\u0179\u017a\t\6\2\2\u017a"+
		"\67\3\2\2\2\u017b\u017c\b\35\1\2\u017c\u017d\7\"\2\2\u017d\u0184\58\35"+
		"\6\u017e\u0184\5D#\2\u017f\u0180\7)\2\2\u0180\u0181\58\35\2\u0181\u0182"+
		"\7*\2\2\u0182\u0184\3\2\2\2\u0183\u017b\3\2\2\2\u0183\u017e\3\2\2\2\u0183"+
		"\u017f\3\2\2\2\u0184\u0190\3\2\2\2\u0185\u0186\f\5\2\2\u0186\u0187\7\35"+
		"\2\2\u0187\u018f\58\35\6\u0188\u0189\f\4\2\2\u0189\u018a\t\7\2\2\u018a"+
		"\u018f\58\35\5\u018b\u018c\f\3\2\2\u018c\u018d\t\b\2\2\u018d\u018f\58"+
		"\35\4\u018e\u0185\3\2\2\2\u018e\u0188\3\2\2\2\u018e\u018b\3\2\2\2\u018f"+
		"\u0192\3\2\2\2\u0190\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u01919\3\2\2\2"+
		"\u0192\u0190\3\2\2\2\u0193\u0194\5F$\2\u0194;\3\2\2\2\u0195\u0196\5> "+
		"\2\u0196=\3\2\2\2\u0197\u0198\7,\2\2\u0198\u0199\7)\2\2\u0199\u019a\7"+
		"8\2\2\u019a\u019b\7+\2\2\u019b\u019c\7\26\2\2\u019c\u019d\7*\2\2\u019d"+
		"?\3\2\2\2\u019e\u019f\t\t\2\2\u019fA\3\2\2\2\u01a0\u01a5\5@!\2\u01a1\u01a5"+
		"\7\66\2\2\u01a2\u01a5\7\25\2\2\u01a3\u01a5\5<\37\2\u01a4\u01a0\3\2\2\2"+
		"\u01a4\u01a1\3\2\2\2\u01a4\u01a2\3\2\2\2\u01a4\u01a3\3\2\2\2\u01a5C\3"+
		"\2\2\2\u01a6\u01ab\7\24\2\2\u01a7\u01ab\7\23\2\2\u01a8\u01ab\7\67\2\2"+
		"\u01a9\u01ab\7\25\2\2\u01aa\u01a6\3\2\2\2\u01aa\u01a7\3\2\2\2\u01aa\u01a8"+
		"\3\2\2\2\u01aa\u01a9\3\2\2\2\u01abE\3\2\2\2\u01ac\u01b0\7\26\2\2\u01ad"+
		"\u01b0\78\2\2\u01ae\u01b0\7\25\2\2\u01af\u01ac\3\2\2\2\u01af\u01ad\3\2"+
		"\2\2\u01af\u01ae\3\2\2\2\u01b0G\3\2\2\2<KRZ_cfinrw|\u0080\u0083\u0088"+
		"\u008c\u0091\u0099\u009e\u00a2\u00a5\u00aa\u00af\u00b7\u00bc\u00c0\u00c3"+
		"\u00c8\u00ce\u00d3\u00d8\u00e0\u00e5\u00eb\u00f1\u00f7\u00fd\u0104\u010b"+
		"\u0115\u0118\u011b\u011f\u0128\u013a\u013c\u0140\u0146\u014b\u015e\u0166"+
		"\u0168\u0177\u0183\u018e\u0190\u01a4\u01aa\u01af";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
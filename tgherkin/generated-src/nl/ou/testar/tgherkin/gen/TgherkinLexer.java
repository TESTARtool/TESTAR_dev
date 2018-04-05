// Generated from TgherkinLexer.g4 by ANTLR 4.5
package nl.ou.testar.tgherkin.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TgherkinLexer extends Lexer {
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
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"OPTION_KEYWORD_INCLUDE", "OPTION_KEYWORD_EXCLUDE", "TAGNAME", "FEATURE_KEYWORD", 
		"BACKGROUND_KEYWORD", "SCENARIO_KEYWORD", "SCENARIO_OUTLINE_KEYWORD", 
		"EXAMPLES_KEYWORD", "SELECTION_KEYWORD", "ORACLE_KEYWORD", "STEP_KEYWORD", 
		"STEP_RANGE_KEYWORD", "STEP_GIVEN_KEYWORD", "STEP_WHEN_KEYWORD", "STEP_THEN_KEYWORD", 
		"STEP_ALSO_KEYWORD", "STEP_EITHER_KEYWORD", "TABLE_ROW", "DECIMAL_NUMBER", 
		"INTEGER_NUMBER", "PLACEHOLDER", "STRING", "COMMENT", "AND", "OR", "NOT", 
		"TRUE", "FALSE", "POW", "MULT", "DIV", "MOD", "PLUS", "MINUS", "GT", "GE", 
		"LT", "LE", "EQ", "NE", "LPAREN", "RPAREN", "COMMA", "MATCHES_NAME", "CLICK_NAME", 
		"TYPE_NAME", "DRAG_NAME", "ANY_NAME", "DOUBLE_CLICK_NAME", "TRIPLE_CLICK_NAME", 
		"RIGHT_CLICK_NAME", "MOUSE_MOVE_NAME", "DROP_DOWN_AT_NAME", "BOOLEAN_VARIABLE", 
		"NUMBER_VARIABLE", "STRING_VARIABLE", "VARIABLE_PREFIX", "EOL", "WS", 
		"OTHER", "ESC", "BOOLEAN_VARIABLE_NAME", "NUMBER_VARIABLE_NAME", "STRING_VARIABLE_NAME"
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


	boolean ignoreEOL=false;
	boolean ignoreWS=true;


	public TgherkinLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TgherkinLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 2:
			TAGNAME_action((RuleContext)_localctx, actionIndex);
			break;
		case 3:
			FEATURE_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 4:
			BACKGROUND_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 5:
			SCENARIO_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 6:
			SCENARIO_OUTLINE_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 7:
			EXAMPLES_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 8:
			SELECTION_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 9:
			ORACLE_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 10:
			STEP_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 11:
			STEP_RANGE_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 12:
			STEP_GIVEN_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 13:
			STEP_WHEN_KEYWORD_action((RuleContext)_localctx, actionIndex);
			break;
		case 17:
			TABLE_ROW_action((RuleContext)_localctx, actionIndex);
			break;
		case 22:
			COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 57:
			EOL_action((RuleContext)_localctx, actionIndex);
			break;
		case 58:
			WS_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void TAGNAME_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			ignoreWS=true;
			break;
		}
	}
	private void FEATURE_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			ignoreEOL=false;ignoreWS=false;
			break;
		}
	}
	private void BACKGROUND_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:
			ignoreEOL=false;ignoreWS=false;
			break;
		}
	}
	private void SCENARIO_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:
			ignoreEOL=false;ignoreWS=false;
			break;
		}
	}
	private void SCENARIO_OUTLINE_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4:
			ignoreEOL=false;ignoreWS=false;
			break;
		}
	}
	private void EXAMPLES_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 5:
			ignoreEOL=false;ignoreWS=false;
			break;
		}
	}
	private void SELECTION_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 6:
			ignoreEOL=true;ignoreWS=true;
			break;
		}
	}
	private void ORACLE_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 7:
			ignoreEOL=true;ignoreWS=true;
			break;
		}
	}
	private void STEP_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 8:
			ignoreEOL=false;ignoreWS=false;
			break;
		}
	}
	private void STEP_RANGE_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 9:
			ignoreEOL=true;ignoreWS=true;
			break;
		}
	}
	private void STEP_GIVEN_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 10:
			ignoreEOL=true;ignoreWS=true;
			break;
		}
	}
	private void STEP_WHEN_KEYWORD_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 11:
			ignoreEOL=true;ignoreWS=true;
			break;
		}
	}
	private void TABLE_ROW_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 12:
			ignoreWS=true;
			break;
		}
	}
	private void COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 13:
			skip();
			break;
		}
	}
	private void EOL_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 14:
			 if(ignoreEOL) skip();
			break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 15:
			 if(ignoreWS) skip();
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2@\u06b5\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\6\4\u0098\n\4\r\4\16\4\u0099\3\4\3"+
		"\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\7\23\u012d"+
		"\n\23\f\23\16\23\u0130\13\23\3\23\6\23\u0133\n\23\r\23\16\23\u0134\3\23"+
		"\7\23\u0138\n\23\f\23\16\23\u013b\13\23\3\23\3\23\5\23\u013f\n\23\3\24"+
		"\6\24\u0142\n\24\r\24\16\24\u0143\3\24\3\24\6\24\u0148\n\24\r\24\16\24"+
		"\u0149\3\25\6\25\u014d\n\25\r\25\16\25\u014e\3\26\3\26\6\26\u0153\n\26"+
		"\r\26\16\26\u0154\3\26\3\26\3\27\3\27\3\27\7\27\u015c\n\27\f\27\16\27"+
		"\u015f\13\27\3\27\3\27\3\30\3\30\7\30\u0165\n\30\f\30\16\30\u0168\13\30"+
		"\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\33\3\33"+
		"\5\33\u0178\n\33\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35"+
		"\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3%\3&\3&\3"+
		"\'\3\'\3\'\3(\3(\3)\3)\3)\3)\5)\u01a1\n)\3*\3*\3+\3+\3,\3,\3-\3-\3-\3"+
		"-\3-\3-\3-\3-\3.\3.\3.\3.\3.\3.\3/\3/\3/\3/\3/\3\60\3\60\3\60\3\60\3\60"+
		"\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\62\3\62\3\62"+
		"\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\63"+
		"\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3\64\3\64\3\64"+
		"\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65"+
		"\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\67\3\67\3\67"+
		"\38\38\38\39\39\39\3:\3:\3;\7;\u0210\n;\f;\16;\u0213\13;\3;\5;\u0216\n"+
		";\3;\3;\5;\u021a\n;\3;\7;\u021d\n;\f;\16;\u0220\13;\3;\3;\3<\6<\u0225"+
		"\n<\r<\16<\u0226\3<\3<\3=\6=\u022c\n=\r=\16=\u022d\3>\3>\3>\3>\5>\u0234"+
		"\n>\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?"+
		"\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\5?"+
		"\u035f\n?\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@"+
		"\3@\3@\3@\3@\3@\3@\3@\3@\5@\u04a8\n@\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\3A\3A\5A\u06b4\nA\4\u015d\u022d\2B\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26"+
		"+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S"+
		"+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s\2u;w<y={\2}>\177?\u0081"+
		"@\3\2\7\5\2\13\f\17\17\"\"\4\2\f\f\17\17\3\2\62;\6\2\13\f\17\17\"\"@@"+
		"\4\2\13\13\"\"\u0721\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2"+
		"\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3"+
		"\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2"+
		"\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2"+
		"\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2"+
		"\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2"+
		"\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q"+
		"\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2"+
		"\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2"+
		"\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y"+
		"\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\3\u0083\3\2\2\2\5\u008c"+
		"\3\2\2\2\7\u0095\3\2\2\2\t\u009d\3\2\2\2\13\u00a8\3\2\2\2\r\u00b6\3\2"+
		"\2\2\17\u00c2\3\2\2\2\21\u00d6\3\2\2\2\23\u00e2\3\2\2\2\25\u00ef\3\2\2"+
		"\2\27\u00f9\3\2\2\2\31\u0101\3\2\2\2\33\u0109\3\2\2\2\35\u0111\3\2\2\2"+
		"\37\u0118\3\2\2\2!\u011d\3\2\2\2#\u0122\3\2\2\2%\u0129\3\2\2\2\'\u0141"+
		"\3\2\2\2)\u014c\3\2\2\2+\u0150\3\2\2\2-\u0158\3\2\2\2/\u0162\3\2\2\2\61"+
		"\u016c\3\2\2\2\63\u0170\3\2\2\2\65\u0177\3\2\2\2\67\u0179\3\2\2\29\u017e"+
		"\3\2\2\2;\u0184\3\2\2\2=\u0186\3\2\2\2?\u0188\3\2\2\2A\u018a\3\2\2\2C"+
		"\u018c\3\2\2\2E\u018e\3\2\2\2G\u0190\3\2\2\2I\u0192\3\2\2\2K\u0195\3\2"+
		"\2\2M\u0197\3\2\2\2O\u019a\3\2\2\2Q\u01a0\3\2\2\2S\u01a2\3\2\2\2U\u01a4"+
		"\3\2\2\2W\u01a6\3\2\2\2Y\u01a8\3\2\2\2[\u01b0\3\2\2\2]\u01b6\3\2\2\2_"+
		"\u01bb\3\2\2\2a\u01c0\3\2\2\2c\u01cb\3\2\2\2e\u01d7\3\2\2\2g\u01e3\3\2"+
		"\2\2i\u01ee\3\2\2\2k\u01f8\3\2\2\2m\u0203\3\2\2\2o\u0206\3\2\2\2q\u0209"+
		"\3\2\2\2s\u020c\3\2\2\2u\u0211\3\2\2\2w\u0224\3\2\2\2y\u022b\3\2\2\2{"+
		"\u0233\3\2\2\2}\u035e\3\2\2\2\177\u04a7\3\2\2\2\u0081\u06b3\3\2\2\2\u0083"+
		"\u0084\7k\2\2\u0084\u0085\7p\2\2\u0085\u0086\7e\2\2\u0086\u0087\7n\2\2"+
		"\u0087\u0088\7w\2\2\u0088\u0089\7f\2\2\u0089\u008a\7g\2\2\u008a\u008b"+
		"\7<\2\2\u008b\4\3\2\2\2\u008c\u008d\7g\2\2\u008d\u008e\7z\2\2\u008e\u008f"+
		"\7e\2\2\u008f\u0090\7n\2\2\u0090\u0091\7w\2\2\u0091\u0092\7f\2\2\u0092"+
		"\u0093\7g\2\2\u0093\u0094\7<\2\2\u0094\6\3\2\2\2\u0095\u0097\7B\2\2\u0096"+
		"\u0098\n\2\2\2\u0097\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u0097\3\2"+
		"\2\2\u0099\u009a\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009c\b\4\2\2\u009c"+
		"\b\3\2\2\2\u009d\u009e\7H\2\2\u009e\u009f\7g\2\2\u009f\u00a0\7c\2\2\u00a0"+
		"\u00a1\7v\2\2\u00a1\u00a2\7w\2\2\u00a2\u00a3\7t\2\2\u00a3\u00a4\7g\2\2"+
		"\u00a4\u00a5\7<\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a7\b\5\3\2\u00a7\n\3"+
		"\2\2\2\u00a8\u00a9\7D\2\2\u00a9\u00aa\7c\2\2\u00aa\u00ab\7e\2\2\u00ab"+
		"\u00ac\7m\2\2\u00ac\u00ad\7i\2\2\u00ad\u00ae\7t\2\2\u00ae\u00af\7q\2\2"+
		"\u00af\u00b0\7w\2\2\u00b0\u00b1\7p\2\2\u00b1\u00b2\7f\2\2\u00b2\u00b3"+
		"\7<\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b5\b\6\4\2\u00b5\f\3\2\2\2\u00b6"+
		"\u00b7\7U\2\2\u00b7\u00b8\7e\2\2\u00b8\u00b9\7g\2\2\u00b9\u00ba\7p\2\2"+
		"\u00ba\u00bb\7c\2\2\u00bb\u00bc\7t\2\2\u00bc\u00bd\7k\2\2\u00bd\u00be"+
		"\7q\2\2\u00be\u00bf\7<\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\b\7\5\2\u00c1"+
		"\16\3\2\2\2\u00c2\u00c3\7U\2\2\u00c3\u00c4\7e\2\2\u00c4\u00c5\7g\2\2\u00c5"+
		"\u00c6\7p\2\2\u00c6\u00c7\7c\2\2\u00c7\u00c8\7t\2\2\u00c8\u00c9\7k\2\2"+
		"\u00c9\u00ca\7q\2\2\u00ca\u00cb\7\"\2\2\u00cb\u00cc\7Q\2\2\u00cc\u00cd"+
		"\7w\2\2\u00cd\u00ce\7v\2\2\u00ce\u00cf\7n\2\2\u00cf\u00d0\7k\2\2\u00d0"+
		"\u00d1\7p\2\2\u00d1\u00d2\7g\2\2\u00d2\u00d3\7<\2\2\u00d3\u00d4\3\2\2"+
		"\2\u00d4\u00d5\b\b\6\2\u00d5\20\3\2\2\2\u00d6\u00d7\7G\2\2\u00d7\u00d8"+
		"\7z\2\2\u00d8\u00d9\7c\2\2\u00d9\u00da\7o\2\2\u00da\u00db\7r\2\2\u00db"+
		"\u00dc\7n\2\2\u00dc\u00dd\7g\2\2\u00dd\u00de\7u\2\2\u00de\u00df\7<\2\2"+
		"\u00df\u00e0\3\2\2\2\u00e0\u00e1\b\t\7\2\u00e1\22\3\2\2\2\u00e2\u00e3"+
		"\7U\2\2\u00e3\u00e4\7g\2\2\u00e4\u00e5\7n\2\2\u00e5\u00e6\7g\2\2\u00e6"+
		"\u00e7\7e\2\2\u00e7\u00e8\7v\2\2\u00e8\u00e9\7k\2\2\u00e9\u00ea\7q\2\2"+
		"\u00ea\u00eb\7p\2\2\u00eb\u00ec\7<\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00ee"+
		"\b\n\b\2\u00ee\24\3\2\2\2\u00ef\u00f0\7Q\2\2\u00f0\u00f1\7t\2\2\u00f1"+
		"\u00f2\7c\2\2\u00f2\u00f3\7e\2\2\u00f3\u00f4\7n\2\2\u00f4\u00f5\7g\2\2"+
		"\u00f5\u00f6\7<\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f8\b\13\t\2\u00f8\26"+
		"\3\2\2\2\u00f9\u00fa\7U\2\2\u00fa\u00fb\7v\2\2\u00fb\u00fc\7g\2\2\u00fc"+
		"\u00fd\7r\2\2\u00fd\u00fe\7<\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0100\b\f\n"+
		"\2\u0100\30\3\2\2\2\u0101\u0102\7T\2\2\u0102\u0103\7c\2\2\u0103\u0104"+
		"\7p\2\2\u0104\u0105\7i\2\2\u0105\u0106\7g\2\2\u0106\u0107\3\2\2\2\u0107"+
		"\u0108\b\r\13\2\u0108\32\3\2\2\2\u0109\u010a\7I\2\2\u010a\u010b\7k\2\2"+
		"\u010b\u010c\7x\2\2\u010c\u010d\7g\2\2\u010d\u010e\7p\2\2\u010e\u010f"+
		"\3\2\2\2\u010f\u0110\b\16\f\2\u0110\34\3\2\2\2\u0111\u0112\7Y\2\2\u0112"+
		"\u0113\7j\2\2\u0113\u0114\7g\2\2\u0114\u0115\7p\2\2\u0115\u0116\3\2\2"+
		"\2\u0116\u0117\b\17\r\2\u0117\36\3\2\2\2\u0118\u0119\7V\2\2\u0119\u011a"+
		"\7j\2\2\u011a\u011b\7g\2\2\u011b\u011c\7p\2\2\u011c \3\2\2\2\u011d\u011e"+
		"\7C\2\2\u011e\u011f\7n\2\2\u011f\u0120\7u\2\2\u0120\u0121\7q\2\2\u0121"+
		"\"\3\2\2\2\u0122\u0123\7G\2\2\u0123\u0124\7k\2\2\u0124\u0125\7v\2\2\u0125"+
		"\u0126\7j\2\2\u0126\u0127\7g\2\2\u0127\u0128\7t\2\2\u0128$\3\2\2\2\u0129"+
		"\u012a\b\23\16\2\u012a\u0132\7~\2\2\u012b\u012d\n\3\2\2\u012c\u012b\3"+
		"\2\2\2\u012d\u0130\3\2\2\2\u012e\u012c\3\2\2\2\u012e\u012f\3\2\2\2\u012f"+
		"\u0131\3\2\2\2\u0130\u012e\3\2\2\2\u0131\u0133\7~\2\2\u0132\u012e\3\2"+
		"\2\2\u0133\u0134\3\2\2\2\u0134\u0132\3\2\2\2\u0134\u0135\3\2\2\2\u0135"+
		"\u0139\3\2\2\2\u0136\u0138\5w<\2\u0137\u0136\3\2\2\2\u0138\u013b\3\2\2"+
		"\2\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u013e\3\2\2\2\u013b\u0139"+
		"\3\2\2\2\u013c\u013f\5u;\2\u013d\u013f\7\2\2\3\u013e\u013c\3\2\2\2\u013e"+
		"\u013d\3\2\2\2\u013f&\3\2\2\2\u0140\u0142\t\4\2\2\u0141\u0140\3\2\2\2"+
		"\u0142\u0143\3\2\2\2\u0143\u0141\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145"+
		"\3\2\2\2\u0145\u0147\7\60\2\2\u0146\u0148\t\4\2\2\u0147\u0146\3\2\2\2"+
		"\u0148\u0149\3\2\2\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a(\3"+
		"\2\2\2\u014b\u014d\t\4\2\2\u014c\u014b\3\2\2\2\u014d\u014e\3\2\2\2\u014e"+
		"\u014c\3\2\2\2\u014e\u014f\3\2\2\2\u014f*\3\2\2\2\u0150\u0152\7>\2\2\u0151"+
		"\u0153\n\5\2\2\u0152\u0151\3\2\2\2\u0153\u0154\3\2\2\2\u0154\u0152\3\2"+
		"\2\2\u0154\u0155\3\2\2\2\u0155\u0156\3\2\2\2\u0156\u0157\7@\2\2\u0157"+
		",\3\2\2\2\u0158\u015d\7$\2\2\u0159\u015c\5{>\2\u015a\u015c\13\2\2\2\u015b"+
		"\u0159\3\2\2\2\u015b\u015a\3\2\2\2\u015c\u015f\3\2\2\2\u015d\u015e\3\2"+
		"\2\2\u015d\u015b\3\2\2\2\u015e\u0160\3\2\2\2\u015f\u015d\3\2\2\2\u0160"+
		"\u0161\7$\2\2\u0161.\3\2\2\2\u0162\u0166\7%\2\2\u0163\u0165\n\3\2\2\u0164"+
		"\u0163\3\2\2\2\u0165\u0168\3\2\2\2\u0166\u0164\3\2\2\2\u0166\u0167\3\2"+
		"\2\2\u0167\u0169\3\2\2\2\u0168\u0166\3\2\2\2\u0169\u016a\5u;\2\u016a\u016b"+
		"\b\30\17\2\u016b\60\3\2\2\2\u016c\u016d\7c\2\2\u016d\u016e\7p\2\2\u016e"+
		"\u016f\7f\2\2\u016f\62\3\2\2\2\u0170\u0171\7q\2\2\u0171\u0172\7t\2\2\u0172"+
		"\64\3\2\2\2\u0173\u0174\7p\2\2\u0174\u0175\7q\2\2\u0175\u0178\7v\2\2\u0176"+
		"\u0178\7#\2\2\u0177\u0173\3\2\2\2\u0177\u0176\3\2\2\2\u0178\66\3\2\2\2"+
		"\u0179\u017a\7v\2\2\u017a\u017b\7t\2\2\u017b\u017c\7w\2\2\u017c\u017d"+
		"\7g\2\2\u017d8\3\2\2\2\u017e\u017f\7h\2\2\u017f\u0180\7c\2\2\u0180\u0181"+
		"\7n\2\2\u0181\u0182\7u\2\2\u0182\u0183\7g\2\2\u0183:\3\2\2\2\u0184\u0185"+
		"\7`\2\2\u0185<\3\2\2\2\u0186\u0187\7,\2\2\u0187>\3\2\2\2\u0188\u0189\7"+
		"\61\2\2\u0189@\3\2\2\2\u018a\u018b\7\'\2\2\u018bB\3\2\2\2\u018c\u018d"+
		"\7-\2\2\u018dD\3\2\2\2\u018e\u018f\7/\2\2\u018fF\3\2\2\2\u0190\u0191\7"+
		"@\2\2\u0191H\3\2\2\2\u0192\u0193\7@\2\2\u0193\u0194\7?\2\2\u0194J\3\2"+
		"\2\2\u0195\u0196\7>\2\2\u0196L\3\2\2\2\u0197\u0198\7>\2\2\u0198\u0199"+
		"\7?\2\2\u0199N\3\2\2\2\u019a\u019b\7?\2\2\u019bP\3\2\2\2\u019c\u019d\7"+
		"#\2\2\u019d\u01a1\7?\2\2\u019e\u019f\7>\2\2\u019f\u01a1\7@\2\2\u01a0\u019c"+
		"\3\2\2\2\u01a0\u019e\3\2\2\2\u01a1R\3\2\2\2\u01a2\u01a3\7*\2\2\u01a3T"+
		"\3\2\2\2\u01a4\u01a5\7+\2\2\u01a5V\3\2\2\2\u01a6\u01a7\7.\2\2\u01a7X\3"+
		"\2\2\2\u01a8\u01a9\7o\2\2\u01a9\u01aa\7c\2\2\u01aa\u01ab\7v\2\2\u01ab"+
		"\u01ac\7e\2\2\u01ac\u01ad\7j\2\2\u01ad\u01ae\7g\2\2\u01ae\u01af\7u\2\2"+
		"\u01afZ\3\2\2\2\u01b0\u01b1\7e\2\2\u01b1\u01b2\7n\2\2\u01b2\u01b3\7k\2"+
		"\2\u01b3\u01b4\7e\2\2\u01b4\u01b5\7m\2\2\u01b5\\\3\2\2\2\u01b6\u01b7\7"+
		"v\2\2\u01b7\u01b8\7{\2\2\u01b8\u01b9\7r\2\2\u01b9\u01ba\7g\2\2\u01ba^"+
		"\3\2\2\2\u01bb\u01bc\7f\2\2\u01bc\u01bd\7t\2\2\u01bd\u01be\7c\2\2\u01be"+
		"\u01bf\7i\2\2\u01bf`\3\2\2\2\u01c0\u01c1\7c\2\2\u01c1\u01c2\7p\2\2\u01c2"+
		"\u01c3\7{\2\2\u01c3\u01c4\7I\2\2\u01c4\u01c5\7g\2\2\u01c5\u01c6\7u\2\2"+
		"\u01c6\u01c7\7v\2\2\u01c7\u01c8\7w\2\2\u01c8\u01c9\7t\2\2\u01c9\u01ca"+
		"\7g\2\2\u01cab\3\2\2\2\u01cb\u01cc\7f\2\2\u01cc\u01cd\7q\2\2\u01cd\u01ce"+
		"\7w\2\2\u01ce\u01cf\7d\2\2\u01cf\u01d0\7n\2\2\u01d0\u01d1\7g\2\2\u01d1"+
		"\u01d2\7E\2\2\u01d2\u01d3\7n\2\2\u01d3\u01d4\7k\2\2\u01d4\u01d5\7e\2\2"+
		"\u01d5\u01d6\7m\2\2\u01d6d\3\2\2\2\u01d7\u01d8\7v\2\2\u01d8\u01d9\7t\2"+
		"\2\u01d9\u01da\7k\2\2\u01da\u01db\7r\2\2\u01db\u01dc\7n\2\2\u01dc\u01dd"+
		"\7g\2\2\u01dd\u01de\7E\2\2\u01de\u01df\7n\2\2\u01df\u01e0\7k\2\2\u01e0"+
		"\u01e1\7e\2\2\u01e1\u01e2\7m\2\2\u01e2f\3\2\2\2\u01e3\u01e4\7t\2\2\u01e4"+
		"\u01e5\7k\2\2\u01e5\u01e6\7i\2\2\u01e6\u01e7\7j\2\2\u01e7\u01e8\7v\2\2"+
		"\u01e8\u01e9\7E\2\2\u01e9\u01ea\7n\2\2\u01ea\u01eb\7k\2\2\u01eb\u01ec"+
		"\7e\2\2\u01ec\u01ed\7m\2\2\u01edh\3\2\2\2\u01ee\u01ef\7o\2\2\u01ef\u01f0"+
		"\7q\2\2\u01f0\u01f1\7w\2\2\u01f1\u01f2\7u\2\2\u01f2\u01f3\7g\2\2\u01f3"+
		"\u01f4\7O\2\2\u01f4\u01f5\7q\2\2\u01f5\u01f6\7x\2\2\u01f6\u01f7\7g\2\2"+
		"\u01f7j\3\2\2\2\u01f8\u01f9\7f\2\2\u01f9\u01fa\7t\2\2\u01fa\u01fb\7q\2"+
		"\2\u01fb\u01fc\7r\2\2\u01fc\u01fd\7F\2\2\u01fd\u01fe\7q\2\2\u01fe\u01ff"+
		"\7y\2\2\u01ff\u0200\7p\2\2\u0200\u0201\7C\2\2\u0201\u0202\7v\2\2\u0202"+
		"l\3\2\2\2\u0203\u0204\5s:\2\u0204\u0205\5}?\2\u0205n\3\2\2\2\u0206\u0207"+
		"\5s:\2\u0207\u0208\5\177@\2\u0208p\3\2\2\2\u0209\u020a\5s:\2\u020a\u020b"+
		"\5\u0081A\2\u020br\3\2\2\2\u020c\u020d\7&\2\2\u020dt\3\2\2\2\u020e\u0210"+
		"\5w<\2\u020f\u020e\3\2\2\2\u0210\u0213\3\2\2\2\u0211\u020f\3\2\2\2\u0211"+
		"\u0212\3\2\2\2\u0212\u0219\3\2\2\2\u0213\u0211\3\2\2\2\u0214\u0216\7\17"+
		"\2\2\u0215\u0214\3\2\2\2\u0215\u0216\3\2\2\2\u0216\u0217\3\2\2\2\u0217"+
		"\u021a\7\f\2\2\u0218\u021a\7\17\2\2\u0219\u0215\3\2\2\2\u0219\u0218\3"+
		"\2\2\2\u021a\u021e\3\2\2\2\u021b\u021d\5w<\2\u021c\u021b\3\2\2\2\u021d"+
		"\u0220\3\2\2\2\u021e\u021c\3\2\2\2\u021e\u021f\3\2\2\2\u021f\u0221\3\2"+
		"\2\2\u0220\u021e\3\2\2\2\u0221\u0222\b;\20\2\u0222v\3\2\2\2\u0223\u0225"+
		"\t\6\2\2\u0224\u0223\3\2\2\2\u0225\u0226\3\2\2\2\u0226\u0224\3\2\2\2\u0226"+
		"\u0227\3\2\2\2\u0227\u0228\3\2\2\2\u0228\u0229\b<\21\2\u0229x\3\2\2\2"+
		"\u022a\u022c\13\2\2\2\u022b\u022a\3\2\2\2\u022c\u022d\3\2\2\2\u022d\u022e"+
		"\3\2\2\2\u022d\u022b\3\2\2\2\u022ez\3\2\2\2\u022f\u0230\7^\2\2\u0230\u0234"+
		"\7$\2\2\u0231\u0232\7^\2\2\u0232\u0234\7^\2\2\u0233\u022f\3\2\2\2\u0233"+
		"\u0231\3\2\2\2\u0234|\3\2\2\2\u0235\u0236\7T\2\2\u0236\u0237\7g\2\2\u0237"+
		"\u0238\7p\2\2\u0238\u0239\7f\2\2\u0239\u023a\7g\2\2\u023a\u023b\7t\2\2"+
		"\u023b\u023c\7g\2\2\u023c\u035f\7f\2\2\u023d\u023e\7W\2\2\u023e\u023f"+
		"\7K\2\2\u023f\u0240\7C\2\2\u0240\u0241\7K\2\2\u0241\u0242\7u\2\2\u0242"+
		"\u0243\7E\2\2\u0243\u0244\7q\2\2\u0244\u0245\7p\2\2\u0245\u0246\7v\2\2"+
		"\u0246\u0247\7g\2\2\u0247\u0248\7p\2\2\u0248\u0249\7v\2\2\u0249\u024a"+
		"\7G\2\2\u024a\u024b\7n\2\2\u024b\u024c\7g\2\2\u024c\u024d\7o\2\2\u024d"+
		"\u024e\7g\2\2\u024e\u024f\7p\2\2\u024f\u035f\7v\2\2\u0250\u0251\7W\2\2"+
		"\u0251\u0252\7K\2\2\u0252\u0253\7C\2\2\u0253\u0254\7X\2\2\u0254\u0255"+
		"\7g\2\2\u0255\u0256\7t\2\2\u0256\u0257\7v\2\2\u0257\u0258\7k\2\2\u0258"+
		"\u0259\7e\2\2\u0259\u025a\7c\2\2\u025a\u025b\7n\2\2\u025b\u025c\7n\2\2"+
		"\u025c\u025d\7{\2\2\u025d\u025e\7U\2\2\u025e\u025f\7e\2\2\u025f\u0260"+
		"\7t\2\2\u0260\u0261\7q\2\2\u0261\u0262\7n\2\2\u0262\u0263\7n\2\2\u0263"+
		"\u0264\7c\2\2\u0264\u0265\7d\2\2\u0265\u0266\7n\2\2\u0266\u035f\7g\2\2"+
		"\u0267\u0268\7W\2\2\u0268\u0269\7K\2\2\u0269\u026a\7C\2\2\u026a\u026b"+
		"\7J\2\2\u026b\u026c\7q\2\2\u026c\u026d\7t\2\2\u026d\u026e\7k\2\2\u026e"+
		"\u026f\7|\2\2\u026f\u0270\7q\2\2\u0270\u0271\7p\2\2\u0271\u0272\7v\2\2"+
		"\u0272\u0273\7c\2\2\u0273\u0274\7n\2\2\u0274\u0275\7n\2\2\u0275\u0276"+
		"\7{\2\2\u0276\u0277\7U\2\2\u0277\u0278\7e\2\2\u0278\u0279\7t\2\2\u0279"+
		"\u027a\7q\2\2\u027a\u027b\7n\2\2\u027b\u027c\7n\2\2\u027c\u027d\7c\2\2"+
		"\u027d\u027e\7d\2\2\u027e\u027f\7n\2\2\u027f\u035f\7g\2\2\u0280\u0281"+
		"\7J\2\2\u0281\u0282\7c\2\2\u0282\u0283\7u\2\2\u0283\u0284\7U\2\2\u0284"+
		"\u0285\7v\2\2\u0285\u0286\7c\2\2\u0286\u0287\7p\2\2\u0287\u0288\7f\2\2"+
		"\u0288\u0289\7c\2\2\u0289\u028a\7t\2\2\u028a\u028b\7f\2\2\u028b\u028c"+
		"\7O\2\2\u028c\u028d\7q\2\2\u028d\u028e\7w\2\2\u028e\u028f\7u\2\2\u028f"+
		"\u035f\7g\2\2\u0290\u0291\7J\2\2\u0291\u0292\7c\2\2\u0292\u0293\7u\2\2"+
		"\u0293\u0294\7U\2\2\u0294\u0295\7v\2\2\u0295\u0296\7c\2\2\u0296\u0297"+
		"\7p\2\2\u0297\u0298\7f\2\2\u0298\u0299\7c\2\2\u0299\u029a\7t\2\2\u029a"+
		"\u029b\7f\2\2\u029b\u029c\7M\2\2\u029c\u029d\7g\2\2\u029d\u029e\7{\2\2"+
		"\u029e\u029f\7d\2\2\u029f\u02a0\7q\2\2\u02a0\u02a1\7c\2\2\u02a1\u02a2"+
		"\7t\2\2\u02a2\u035f\7f\2\2\u02a3\u02a4\7W\2\2\u02a4\u02a5\7K\2\2\u02a5"+
		"\u02a6\7C\2\2\u02a6\u02a7\7K\2\2\u02a7\u02a8\7u\2\2\u02a8\u02a9\7E\2\2"+
		"\u02a9\u02aa\7q\2\2\u02aa\u02ab\7p\2\2\u02ab\u02ac\7v\2\2\u02ac\u02ad"+
		"\7t\2\2\u02ad\u02ae\7q\2\2\u02ae\u02af\7n\2\2\u02af\u02b0\7G\2\2\u02b0"+
		"\u02b1\7n\2\2\u02b1\u02b2\7g\2\2\u02b2\u02b3\7o\2\2\u02b3\u02b4\7g\2\2"+
		"\u02b4\u02b5\7p\2\2\u02b5\u035f\7v\2\2\u02b6\u02b7\7D\2\2\u02b7\u02b8"+
		"\7n\2\2\u02b8\u02b9\7q\2\2\u02b9\u02ba\7e\2\2\u02ba\u02bb\7m\2\2\u02bb"+
		"\u02bc\7g\2\2\u02bc\u035f\7f\2\2\u02bd\u02be\7W\2\2\u02be\u02bf\7K\2\2"+
		"\u02bf\u02c0\7C\2\2\u02c0\u02c1\7K\2\2\u02c1\u02c2\7u\2\2\u02c2\u02c3"+
		"\7G\2\2\u02c3\u02c4\7p\2\2\u02c4\u02c5\7c\2\2\u02c5\u02c6\7d\2\2\u02c6"+
		"\u02c7\7n\2\2\u02c7\u02c8\7g\2\2\u02c8\u035f\7f\2\2\u02c9\u02ca\7K\2\2"+
		"\u02ca\u02cb\7u\2\2\u02cb\u02cc\7T\2\2\u02cc\u02cd\7w\2\2\u02cd\u02ce"+
		"\7p\2\2\u02ce\u02cf\7p\2\2\u02cf\u02d0\7k\2\2\u02d0\u02d1\7p\2\2\u02d1"+
		"\u035f\7i\2\2\u02d2\u02d3\7W\2\2\u02d3\u02d4\7K\2\2\u02d4\u02d5\7C\2\2"+
		"\u02d5\u02d6\7U\2\2\u02d6\u02d7\7e\2\2\u02d7\u02d8\7t\2\2\u02d8\u02d9"+
		"\7q\2\2\u02d9\u02da\7n\2\2\u02da\u02db\7n\2\2\u02db\u02dc\7R\2\2\u02dc"+
		"\u02dd\7c\2\2\u02dd\u02de\7v\2\2\u02de\u02df\7v\2\2\u02df\u02e0\7g\2\2"+
		"\u02e0\u02e1\7t\2\2\u02e1\u035f\7p\2\2\u02e2\u02e3\7W\2\2\u02e3\u02e4"+
		"\7K\2\2\u02e4\u02e5\7C\2\2\u02e5\u02e6\7K\2\2\u02e6\u02e7\7u\2\2\u02e7"+
		"\u02e8\7M\2\2\u02e8\u02e9\7g\2\2\u02e9\u02ea\7{\2\2\u02ea\u02eb\7d\2\2"+
		"\u02eb\u02ec\7q\2\2\u02ec\u02ed\7c\2\2\u02ed\u02ee\7t\2\2\u02ee\u02ef"+
		"\7f\2\2\u02ef\u02f0\7H\2\2\u02f0\u02f1\7q\2\2\u02f1\u02f2\7e\2\2\u02f2"+
		"\u02f3\7w\2\2\u02f3\u02f4\7u\2\2\u02f4\u02f5\7c\2\2\u02f5\u02f6\7d\2\2"+
		"\u02f6\u02f7\7n\2\2\u02f7\u035f\7g\2\2\u02f8\u02f9\7P\2\2\u02f9\u02fa"+
		"\7q\2\2\u02fa\u02fb\7v\2\2\u02fb\u02fc\7T\2\2\u02fc\u02fd\7g\2\2\u02fd"+
		"\u02fe\7u\2\2\u02fe\u02ff\7r\2\2\u02ff\u0300\7q\2\2\u0300\u0301\7p\2\2"+
		"\u0301\u0302\7f\2\2\u0302\u0303\7k\2\2\u0303\u0304\7p\2\2\u0304\u035f"+
		"\7i\2\2\u0305\u0306\7W\2\2\u0306\u0307\7K\2\2\u0307\u0308\7C\2\2\u0308"+
		"\u0309\7K\2\2\u0309\u030a\7u\2\2\u030a\u030b\7Y\2\2\u030b\u030c\7k\2\2"+
		"\u030c\u030d\7p\2\2\u030d\u030e\7f\2\2\u030e\u030f\7q\2\2\u030f\u0310"+
		"\7y\2\2\u0310\u0311\7O\2\2\u0311\u0312\7q\2\2\u0312\u0313\7f\2\2\u0313"+
		"\u0314\7c\2\2\u0314\u035f\7n\2\2\u0315\u0316\7G\2\2\u0316\u0317\7p\2\2"+
		"\u0317\u0318\7c\2\2\u0318\u0319\7d\2\2\u0319\u031a\7n\2\2\u031a\u031b"+
		"\7g\2\2\u031b\u035f\7f\2\2\u031c\u031d\7W\2\2\u031d\u031e\7K\2\2\u031e"+
		"\u031f\7C\2\2\u031f\u0320\7K\2\2\u0320\u0321\7u\2\2\u0321\u0322\7Q\2\2"+
		"\u0322\u0323\7h\2\2\u0323\u0324\7h\2\2\u0324\u0325\7u\2\2\u0325\u0326"+
		"\7e\2\2\u0326\u0327\7t\2\2\u0327\u0328\7g\2\2\u0328\u0329\7g\2\2\u0329"+
		"\u035f\7p\2\2\u032a\u032b\7H\2\2\u032b\u032c\7q\2\2\u032c\u032d\7t\2\2"+
		"\u032d\u032e\7g\2\2\u032e\u032f\7i\2\2\u032f\u0330\7t\2\2\u0330\u0331"+
		"\7q\2\2\u0331\u0332\7w\2\2\u0332\u0333\7p\2\2\u0333\u035f\7f\2\2\u0334"+
		"\u0335\7W\2\2\u0335\u0336\7K\2\2\u0336\u0337\7C\2\2\u0337\u0338\7K\2\2"+
		"\u0338\u0339\7u\2\2\u0339\u033a\7V\2\2\u033a\u033b\7q\2\2\u033b\u033c"+
		"\7r\2\2\u033c\u033d\7o\2\2\u033d\u033e\7q\2\2\u033e\u033f\7u\2\2\u033f"+
		"\u0340\7v\2\2\u0340\u0341\7Y\2\2\u0341\u0342\7k\2\2\u0342\u0343\7p\2\2"+
		"\u0343\u0344\7f\2\2\u0344\u0345\7q\2\2\u0345\u035f\7y\2\2\u0346\u0347"+
		"\7W\2\2\u0347\u0348\7K\2\2\u0348\u0349\7C\2\2\u0349\u034a\7J\2\2\u034a"+
		"\u034b\7c\2\2\u034b\u034c\7u\2\2\u034c\u034d\7M\2\2\u034d\u034e\7g\2\2"+
		"\u034e\u034f\7{\2\2\u034f\u0350\7d\2\2\u0350\u0351\7q\2\2\u0351\u0352"+
		"\7c\2\2\u0352\u0353\7t\2\2\u0353\u0354\7f\2\2\u0354\u0355\7H\2\2\u0355"+
		"\u0356\7q\2\2\u0356\u0357\7e\2\2\u0357\u0358\7w\2\2\u0358\u035f\7u\2\2"+
		"\u0359\u035a\7O\2\2\u035a\u035b\7q\2\2\u035b\u035c\7f\2\2\u035c\u035d"+
		"\7c\2\2\u035d\u035f\7n\2\2\u035e\u0235\3\2\2\2\u035e\u023d\3\2\2\2\u035e"+
		"\u0250\3\2\2\2\u035e\u0267\3\2\2\2\u035e\u0280\3\2\2\2\u035e\u0290\3\2"+
		"\2\2\u035e\u02a3\3\2\2\2\u035e\u02b6\3\2\2\2\u035e\u02bd\3\2\2\2\u035e"+
		"\u02c9\3\2\2\2\u035e\u02d2\3\2\2\2\u035e\u02e2\3\2\2\2\u035e\u02f8\3\2"+
		"\2\2\u035e\u0305\3\2\2\2\u035e\u0315\3\2\2\2\u035e\u031c\3\2\2\2\u035e"+
		"\u032a\3\2\2\2\u035e\u0334\3\2\2\2\u035e\u0346\3\2\2\2\u035e\u0359\3\2"+
		"\2\2\u035f~\3\2\2\2\u0360\u0361\7C\2\2\u0361\u0362\7e\2\2\u0362\u0363"+
		"\7v\2\2\u0363\u0364\7k\2\2\u0364\u0365\7q\2\2\u0365\u0366\7p\2\2\u0366"+
		"\u0367\7F\2\2\u0367\u0368\7g\2\2\u0368\u0369\7n\2\2\u0369\u036a\7c\2\2"+
		"\u036a\u04a8\7{\2\2\u036b\u036c\7W\2\2\u036c\u036d\7K\2\2\u036d\u036e"+
		"\7C\2\2\u036e\u036f\7E\2\2\u036f\u0370\7q\2\2\u0370\u0371\7p\2\2\u0371"+
		"\u0372\7v\2\2\u0372\u0373\7t\2\2\u0373\u0374\7q\2\2\u0374\u0375\7n\2\2"+
		"\u0375\u0376\7V\2\2\u0376\u0377\7{\2\2\u0377\u0378\7r\2\2\u0378\u04a8"+
		"\7g\2\2\u0379\u037a\7U\2\2\u037a\u037b\7j\2\2\u037b\u037c\7c\2\2\u037c"+
		"\u037d\7r\2\2\u037d\u037e\7g\2\2\u037e\u037f\7\60\2\2\u037f\u0380\7j\2"+
		"\2\u0380\u0381\7g\2\2\u0381\u0382\7k\2\2\u0382\u0383\7i\2\2\u0383\u0384"+
		"\7j\2\2\u0384\u04a8\7v\2\2\u0385\u0386\7U\2\2\u0386\u0387\7j\2\2\u0387"+
		"\u0388\7c\2\2\u0388\u0389\7r\2\2\u0389\u038a\7g\2\2\u038a\u038b\7\60\2"+
		"\2\u038b\u038c\7y\2\2\u038c\u038d\7k\2\2\u038d\u038e\7f\2\2\u038e\u038f"+
		"\7v\2\2\u038f\u04a8\7j\2\2\u0390\u0391\7C\2\2\u0391\u0392\7e\2\2\u0392"+
		"\u0393\7v\2\2\u0393\u0394\7k\2\2\u0394\u0395\7q\2\2\u0395\u0396\7p\2\2"+
		"\u0396\u0397\7F\2\2\u0397\u0398\7w\2\2\u0398\u0399\7t\2\2\u0399\u039a"+
		"\7c\2\2\u039a\u039b\7v\2\2\u039b\u039c\7k\2\2\u039c\u039d\7q\2\2\u039d"+
		"\u04a8\7p\2\2\u039e\u039f\7V\2\2\u039f\u03a0\7k\2\2\u03a0\u03a1\7o\2\2"+
		"\u03a1\u03a2\7g\2\2\u03a2\u03a3\7U\2\2\u03a3\u03a4\7v\2\2\u03a4\u03a5"+
		"\7c\2\2\u03a5\u03a6\7o\2\2\u03a6\u04a8\7r\2\2\u03a7\u03a8\7W\2\2\u03a8"+
		"\u03a9\7K\2\2\u03a9\u03aa\7C\2\2\u03aa\u03ab\7R\2\2\u03ab\u03ac\7t\2\2"+
		"\u03ac\u03ad\7q\2\2\u03ad\u03ae\7e\2\2\u03ae\u03af\7g\2\2\u03af\u03b0"+
		"\7u\2\2\u03b0\u03b1\7u\2\2\u03b1\u03b2\7K\2\2\u03b2\u04a8\7f\2\2\u03b3"+
		"\u03b4\7W\2\2\u03b4\u03b5\7K\2\2\u03b5\u03b6\7C\2\2\u03b6\u03b7\7U\2\2"+
		"\u03b7\u03b8\7e\2\2\u03b8\u03b9\7t\2\2\u03b9\u03ba\7q\2\2\u03ba\u03bb"+
		"\7n\2\2\u03bb\u03bc\7n\2\2\u03bc\u03bd\7X\2\2\u03bd\u03be\7g\2\2\u03be"+
		"\u03bf\7t\2\2\u03bf\u03c0\7v\2\2\u03c0\u03c1\7k\2\2\u03c1\u03c2\7e\2\2"+
		"\u03c2\u03c3\7c\2\2\u03c3\u03c4\7n\2\2\u03c4\u03c5\7R\2\2\u03c5\u03c6"+
		"\7g\2\2\u03c6\u03c7\7t\2\2\u03c7\u03c8\7e\2\2\u03c8\u03c9\7g\2\2\u03c9"+
		"\u03ca\7p\2\2\u03ca\u04a8\7v\2\2\u03cb\u03cc\7J\2\2\u03cc\u03cd\7C\2\2"+
		"\u03cd\u03ce\7P\2\2\u03ce\u03cf\7F\2\2\u03cf\u03d0\7N\2\2\u03d0\u04a8"+
		"\7G\2\2\u03d1\u03d2\7O\2\2\u03d2\u03d3\7c\2\2\u03d3\u03d4\7z\2\2\u03d4"+
		"\u03d5\7\\\2\2\u03d5\u03d6\7K\2\2\u03d6\u03d7\7p\2\2\u03d7\u03d8\7f\2"+
		"\2\u03d8\u03d9\7g\2\2\u03d9\u04a8\7z\2\2\u03da\u03db\7W\2\2\u03db\u03dc"+
		"\7K\2\2\u03dc\u03dd\7C\2\2\u03dd\u03de\7U\2\2\u03de\u03df\7e\2\2\u03df"+
		"\u03e0\7t\2\2\u03e0\u03e1\7q\2\2\u03e1\u03e2\7n\2\2\u03e2\u03e3\7n\2\2"+
		"\u03e3\u03e4\7X\2\2\u03e4\u03e5\7g\2\2\u03e5\u03e6\7t\2\2\u03e6\u03e7"+
		"\7v\2\2\u03e7\u03e8\7k\2\2\u03e8\u03e9\7e\2\2\u03e9\u03ea\7c\2\2\u03ea"+
		"\u03eb\7n\2\2\u03eb\u03ec\7X\2\2\u03ec\u03ed\7k\2\2\u03ed\u03ee\7g\2\2"+
		"\u03ee\u03ef\7y\2\2\u03ef\u03f0\7U\2\2\u03f0\u03f1\7k\2\2\u03f1\u03f2"+
		"\7|\2\2\u03f2\u04a8\7g\2\2\u03f3\u03f4\7W\2\2\u03f4\u03f5\7K\2\2\u03f5"+
		"\u03f6\7C\2\2\u03f6\u03f7\7Y\2\2\u03f7\u03f8\7k\2\2\u03f8\u03f9\7p\2\2"+
		"\u03f9\u03fa\7f\2\2\u03fa\u03fb\7q\2\2\u03fb\u03fc\7y\2\2\u03fc\u03fd"+
		"\7K\2\2\u03fd\u03fe\7p\2\2\u03fe\u03ff\7v\2\2\u03ff\u0400\7g\2\2\u0400"+
		"\u0401\7t\2\2\u0401\u0402\7c\2\2\u0402\u0403\7e\2\2\u0403\u0404\7v\2\2"+
		"\u0404\u0405\7k\2\2\u0405\u0406\7q\2\2\u0406\u0407\7p\2\2\u0407\u0408"+
		"\7U\2\2\u0408\u0409\7v\2\2\u0409\u040a\7c\2\2\u040a\u040b\7v\2\2\u040b"+
		"\u04a8\7g\2\2\u040c\u040d\7W\2\2\u040d\u040e\7K\2\2\u040e\u040f\7C\2\2"+
		"\u040f\u0410\7U\2\2\u0410\u0411\7e\2\2\u0411\u0412\7t\2\2\u0412\u0413"+
		"\7q\2\2\u0413\u0414\7n\2\2\u0414\u0415\7n\2\2\u0415\u0416\7J\2\2\u0416"+
		"\u0417\7q\2\2\u0417\u0418\7t\2\2\u0418\u0419\7k\2\2\u0419\u041a\7|\2\2"+
		"\u041a\u041b\7q\2\2\u041b\u041c\7p\2\2\u041c\u041d\7v\2\2\u041d\u041e"+
		"\7c\2\2\u041e\u041f\7n\2\2\u041f\u0420\7X\2\2\u0420\u0421\7k\2\2\u0421"+
		"\u0422\7g\2\2\u0422\u0423\7y\2\2\u0423\u0424\7U\2\2\u0424\u0425\7k\2\2"+
		"\u0425\u0426\7|\2\2\u0426\u04a8\7g\2\2\u0427\u0428\7\\\2\2\u0428\u0429"+
		"\7K\2\2\u0429\u042a\7p\2\2\u042a\u042b\7f\2\2\u042b\u042c\7g\2\2\u042c"+
		"\u04a8\7z\2\2\u042d\u042e\7W\2\2\u042e\u042f\7K\2\2\u042f\u0430\7C\2\2"+
		"\u0430\u0431\7Y\2\2\u0431\u0432\7k\2\2\u0432\u0433\7p\2\2\u0433\u0434"+
		"\7f\2\2\u0434\u0435\7q\2\2\u0435\u0436\7y\2\2\u0436\u0437\7X\2\2\u0437"+
		"\u0438\7k\2\2\u0438\u0439\7u\2\2\u0439\u043a\7w\2\2\u043a\u043b\7c\2\2"+
		"\u043b\u043c\7n\2\2\u043c\u043d\7U\2\2\u043d\u043e\7v\2\2\u043e\u043f"+
		"\7c\2\2\u043f\u0440\7v\2\2\u0440\u04a8\7g\2\2\u0441\u0442\7W\2\2\u0442"+
		"\u0443\7K\2\2\u0443\u0444\7C\2\2\u0444\u0445\7Q\2\2\u0445\u0446\7t\2\2"+
		"\u0446\u0447\7k\2\2\u0447\u0448\7g\2\2\u0448\u0449\7p\2\2\u0449\u044a"+
		"\7v\2\2\u044a\u044b\7c\2\2\u044b\u044c\7v\2\2\u044c\u044d\7k\2\2\u044d"+
		"\u044e\7q\2\2\u044e\u04a8\7p\2\2\u044f\u0450\7O\2\2\u0450\u0451\7k\2\2"+
		"\u0451\u0452\7p\2\2\u0452\u0453\7\\\2\2\u0453\u0454\7K\2\2\u0454\u0455"+
		"\7p\2\2\u0455\u0456\7f\2\2\u0456\u0457\7g\2\2\u0457\u04a8\7z\2\2\u0458"+
		"\u0459\7W\2\2\u0459\u045a\7K\2\2\u045a\u045b\7C\2\2\u045b\u045c\7E\2\2"+
		"\u045c\u045d\7w\2\2\u045d\u045e\7n\2\2\u045e\u045f\7v\2\2\u045f\u0460"+
		"\7w\2\2\u0460\u0461\7t\2\2\u0461\u04a8\7g\2\2\u0462\u0463\7U\2\2\u0463"+
		"\u0464\7j\2\2\u0464\u0465\7c\2\2\u0465\u0466\7r\2\2\u0466\u0467\7g\2\2"+
		"\u0467\u0468\7\60\2\2\u0468\u04a8\7z\2\2\u0469\u046a\7U\2\2\u046a\u046b"+
		"\7j\2\2\u046b\u046c\7c\2\2\u046c\u046d\7r\2\2\u046d\u046e\7g\2\2\u046e"+
		"\u046f\7\60\2\2\u046f\u04a8\7{\2\2\u0470\u0471\7R\2\2\u0471\u0472\7K\2"+
		"\2\u0472\u04a8\7F\2\2\u0473\u0474\7C\2\2\u0474\u0475\7p\2\2\u0475\u0476"+
		"\7i\2\2\u0476\u0477\7n\2\2\u0477\u04a8\7g\2\2\u0478\u0479\7W\2\2\u0479"+
		"\u047a\7K\2\2\u047a\u047b\7C\2\2\u047b\u047c\7P\2\2\u047c\u047d\7c\2\2"+
		"\u047d\u047e\7v\2\2\u047e\u047f\7k\2\2\u047f\u0480\7x\2\2\u0480\u0481"+
		"\7g\2\2\u0481\u0482\7Y\2\2\u0482\u0483\7k\2\2\u0483\u0484\7p\2\2\u0484"+
		"\u0485\7f\2\2\u0485\u0486\7q\2\2\u0486\u0487\7y\2\2\u0487\u0488\7J\2\2"+
		"\u0488\u0489\7c\2\2\u0489\u048a\7p\2\2\u048a\u048b\7f\2\2\u048b\u048c"+
		"\7n\2\2\u048c\u04a8\7g\2\2\u048d\u048e\7W\2\2\u048e\u048f\7K\2\2\u048f"+
		"\u0490\7C\2\2\u0490\u0491\7U\2\2\u0491\u0492\7e\2\2\u0492\u0493\7t\2\2"+
		"\u0493\u0494\7q\2\2\u0494\u0495\7n\2\2\u0495\u0496\7n\2\2\u0496\u0497"+
		"\7J\2\2\u0497\u0498\7q\2\2\u0498\u0499\7t\2\2\u0499\u049a\7k\2\2\u049a"+
		"\u049b\7|\2\2\u049b\u049c\7q\2\2\u049c\u049d\7p\2\2\u049d\u049e\7v\2\2"+
		"\u049e\u049f\7c\2\2\u049f\u04a0\7n\2\2\u04a0\u04a1\7R\2\2\u04a1\u04a2"+
		"\7g\2\2\u04a2\u04a3\7t\2\2\u04a3\u04a4\7e\2\2\u04a4\u04a5\7g\2\2\u04a5"+
		"\u04a6\7p\2\2\u04a6\u04a8\7v\2\2\u04a7\u0360\3\2\2\2\u04a7\u036b\3\2\2"+
		"\2\u04a7\u0379\3\2\2\2\u04a7\u0385\3\2\2\2\u04a7\u0390\3\2\2\2\u04a7\u039e"+
		"\3\2\2\2\u04a7\u03a7\3\2\2\2\u04a7\u03b3\3\2\2\2\u04a7\u03cb\3\2\2\2\u04a7"+
		"\u03d1\3\2\2\2\u04a7\u03da\3\2\2\2\u04a7\u03f3\3\2\2\2\u04a7\u040c\3\2"+
		"\2\2\u04a7\u0427\3\2\2\2\u04a7\u042d\3\2\2\2\u04a7\u0441\3\2\2\2\u04a7"+
		"\u044f\3\2\2\2\u04a7\u0458\3\2\2\2\u04a7\u0462\3\2\2\2\u04a7\u0469\3\2"+
		"\2\2\u04a7\u0470\3\2\2\2\u04a7\u0473\3\2\2\2\u04a7\u0478\3\2\2\2\u04a7"+
		"\u048d\3\2\2\2\u04a8\u0080\3\2\2\2\u04a9\u04aa\7X\2\2\u04aa\u04ab\7c\2"+
		"\2\u04ab\u04ac\7n\2\2\u04ac\u04ad\7w\2\2\u04ad\u04ae\7g\2\2\u04ae\u04af"+
		"\7R\2\2\u04af\u04b0\7c\2\2\u04b0\u04b1\7v\2\2\u04b1\u04b2\7v\2\2\u04b2"+
		"\u04b3\7g\2\2\u04b3\u04b4\7t\2\2\u04b4\u06b4\7p\2\2\u04b5\u04b6\7T\2\2"+
		"\u04b6\u04b7\7w\2\2\u04b7\u04b8\7p\2\2\u04b8\u04b9\7p\2\2\u04b9\u04ba"+
		"\7k\2\2\u04ba\u04bb\7p\2\2\u04bb\u04bc\7i\2\2\u04bc\u04bd\7R\2\2\u04bd"+
		"\u04be\7t\2\2\u04be\u04bf\7q\2\2\u04bf\u04c0\7e\2\2\u04c0\u04c1\7g\2\2"+
		"\u04c1\u04c2\7u\2\2\u04c2\u04c3\7u\2\2\u04c3\u04c4\7g\2\2\u04c4\u06b4"+
		"\7u\2\2\u04c5\u04c6\7V\2\2\u04c6\u04c7\7q\2\2\u04c7\u04c8\7q\2\2\u04c8"+
		"\u04c9\7n\2\2\u04c9\u04ca\7V\2\2\u04ca\u04cb\7k\2\2\u04cb\u04cc\7r\2\2"+
		"\u04cc\u04cd\7V\2\2\u04cd\u04ce\7g\2\2\u04ce\u04cf\7z\2\2\u04cf\u06b4"+
		"\7v\2\2\u04d0\u04d1\7V\2\2\u04d1\u04d2\7c\2\2\u04d2\u04d3\7t\2\2\u04d3"+
		"\u04d4\7i\2\2\u04d4\u04d5\7g\2\2\u04d5\u04d6\7v\2\2\u04d6\u04d7\7K\2\2"+
		"\u04d7\u06b4\7F\2\2\u04d8\u04d9\7U\2\2\u04d9\u04da\7v\2\2\u04da\u04db"+
		"\7c\2\2\u04db\u04dc\7p\2\2\u04dc\u04dd\7f\2\2\u04dd\u04de\7c\2\2\u04de"+
		"\u04df\7t\2\2\u04df\u04e0\7f\2\2\u04e0\u04e1\7M\2\2\u04e1\u04e2\7g\2\2"+
		"\u04e2\u04e3\7{\2\2\u04e3\u04e4\7d\2\2\u04e4\u04e5\7q\2\2\u04e5\u04e6"+
		"\7c\2\2\u04e6\u04e7\7t\2\2\u04e7\u06b4\7f\2\2\u04e8\u04e9\7F\2\2\u04e9"+
		"\u04ea\7g\2\2\u04ea\u04eb\7u\2\2\u04eb\u06b4\7e\2\2\u04ec\u04ed\7U\2\2"+
		"\u04ed\u04ee\7v\2\2\u04ee\u04ef\7c\2\2\u04ef\u04f0\7p\2\2\u04f0\u04f1"+
		"\7f\2\2\u04f1\u04f2\7c\2\2\u04f2\u04f3\7t\2\2\u04f3\u04f4\7f\2\2\u04f4"+
		"\u04f5\7O\2\2\u04f5\u04f6\7q\2\2\u04f6\u04f7\7w\2\2\u04f7\u04f8\7u\2\2"+
		"\u04f8\u06b4\7g\2\2\u04f9\u04fa\7W\2\2\u04fa\u04fb\7K\2\2\u04fb\u04fc"+
		"\7C\2\2\u04fc\u04fd\7H\2\2\u04fd\u04fe\7t\2\2\u04fe\u04ff\7c\2\2\u04ff"+
		"\u0500\7o\2\2\u0500\u0501\7g\2\2\u0501\u0502\7y\2\2\u0502\u0503\7q\2\2"+
		"\u0503\u0504\7t\2\2\u0504\u0505\7m\2\2\u0505\u0506\7K\2\2\u0506\u06b4"+
		"\7f\2\2\u0507\u0508\7W\2\2\u0508\u0509\7K\2\2\u0509\u050a\7C\2\2\u050a"+
		"\u050b\7C\2\2\u050b\u050c\7e\2\2\u050c\u050d\7e\2\2\u050d\u050e\7g\2\2"+
		"\u050e\u050f\7u\2\2\u050f\u0510\7u\2\2\u0510\u0511\7M\2\2\u0511\u0512"+
		"\7g\2\2\u0512\u06b4\7{\2\2\u0513\u0514\7C\2\2\u0514\u0515\7d\2\2\u0515"+
		"\u0516\7u\2\2\u0516\u0517\7*\2\2\u0517\u0518\7T\2\2\u0518\u0519\7.\2\2"+
		"\u0519\u051a\7V\2\2\u051a\u051b\7+\2\2\u051b\u051c\7K\2\2\u051c\u06b4"+
		"\7F\2\2\u051d\u051e\7J\2\2\u051e\u051f\7k\2\2\u051f\u0520\7v\2\2\u0520"+
		"\u0521\7V\2\2\u0521\u0522\7g\2\2\u0522\u0523\7u\2\2\u0523\u0524\7v\2\2"+
		"\u0524\u0525\7g\2\2\u0525\u06b4\7t\2\2\u0526\u0527\7W\2\2\u0527\u0528"+
		"\7K\2\2\u0528\u0529\7C\2\2\u0529\u052a\7R\2\2\u052a\u052b\7t\2\2\u052b"+
		"\u052c\7q\2\2\u052c\u052d\7x\2\2\u052d\u052e\7k\2\2\u052e\u052f\7f\2\2"+
		"\u052f\u0530\7g\2\2\u0530\u0531\7t\2\2\u0531\u0532\7F\2\2\u0532\u0533"+
		"\7g\2\2\u0533\u0534\7u\2\2\u0534\u0535\7e\2\2\u0535\u0536\7t\2\2\u0536"+
		"\u0537\7k\2\2\u0537\u0538\7r\2\2\u0538\u0539\7v\2\2\u0539\u053a\7k\2\2"+
		"\u053a\u053b\7q\2\2\u053b\u06b4\7p\2\2\u053c\u053d\7W\2\2\u053d\u053e"+
		"\7K\2\2\u053e\u053f\7C\2\2\u053f\u0540\7C\2\2\u0540\u0541\7w\2\2\u0541"+
		"\u0542\7v\2\2\u0542\u0543\7q\2\2\u0543\u0544\7o\2\2\u0544\u0545\7c\2\2"+
		"\u0545\u0546\7v\2\2\u0546\u0547\7k\2\2\u0547\u0548\7q\2\2\u0548\u0549"+
		"\7p\2\2\u0549\u054a\7K\2\2\u054a\u06b4\7f\2\2\u054b\u054c\7U\2\2\u054c"+
		"\u054d\7e\2\2\u054d\u054e\7t\2\2\u054e\u054f\7g\2\2\u054f\u0550\7g\2\2"+
		"\u0550\u0551\7p\2\2\u0551\u0552\7u\2\2\u0552\u0553\7j\2\2\u0553\u0554"+
		"\7q\2\2\u0554\u0555\7v\2\2\u0555\u0556\7R\2\2\u0556\u0557\7c\2\2\u0557"+
		"\u0558\7v\2\2\u0558\u06b4\7j\2\2\u0559\u055a\7W\2\2\u055a\u055b\7K\2\2"+
		"\u055b\u055c\7C\2\2\u055c\u055d\7N\2\2\u055d\u055e\7q\2\2\u055e\u055f"+
		"\7e\2\2\u055f\u0560\7c\2\2\u0560\u0561\7n\2\2\u0561\u0562\7k\2\2\u0562"+
		"\u0563\7|\2\2\u0563\u0564\7g\2\2\u0564\u0565\7f\2\2\u0565\u0566\7E\2\2"+
		"\u0566\u0567\7q\2\2\u0567\u0568\7p\2\2\u0568\u0569\7v\2\2\u0569\u056a"+
		"\7t\2\2\u056a\u056b\7q\2\2\u056b\u056c\7n\2\2\u056c\u056d\7V\2\2\u056d"+
		"\u056e\7{\2\2\u056e\u056f\7r\2\2\u056f\u06b4\7g\2\2\u0570\u0571\7T\2\2"+
		"\u0571\u0572\7g\2\2\u0572\u0573\7r\2\2\u0573\u0574\7t\2\2\u0574\u0575"+
		"\7g\2\2\u0575\u0576\7u\2\2\u0576\u0577\7g\2\2\u0577\u0578\7p\2\2\u0578"+
		"\u0579\7v\2\2\u0579\u057a\7c\2\2\u057a\u057b\7v\2\2\u057b\u057c\7k\2\2"+
		"\u057c\u057d\7q\2\2\u057d\u06b4\7p\2\2\u057e\u057f\7Q\2\2\u057f\u0580"+
		"\7t\2\2\u0580\u0581\7c\2\2\u0581\u0582\7e\2\2\u0582\u0583\7n\2\2\u0583"+
		"\u0584\7g\2\2\u0584\u0585\7X\2\2\u0585\u0586\7g\2\2\u0586\u0587\7t\2\2"+
		"\u0587\u0588\7f\2\2\u0588\u0589\7k\2\2\u0589\u058a\7e\2\2\u058a\u06b4"+
		"\7v\2\2\u058b\u058c\7T\2\2\u058c\u058d\7q\2\2\u058d\u058e\7n\2\2\u058e"+
		"\u06b4\7g\2\2\u058f\u0590\7W\2\2\u0590\u0591\7K\2\2\u0591\u06b4\7F\2\2"+
		"\u0592\u0593\7X\2\2\u0593\u0594\7k\2\2\u0594\u0595\7u\2\2\u0595\u0596"+
		"\7w\2\2\u0596\u0597\7c\2\2\u0597\u0598\7n\2\2\u0598\u0599\7k\2\2\u0599"+
		"\u059a\7|\2\2\u059a\u059b\7g\2\2\u059b\u06b4\7t\2\2\u059c\u059d\7T\2\2"+
		"\u059d\u059e\7g\2\2\u059e\u059f\7u\2\2\u059f\u05a0\7q\2\2\u05a0\u05a1"+
		"\7w\2\2\u05a1\u05a2\7t\2\2\u05a2\u05a3\7e\2\2\u05a3\u05a4\7g\2\2\u05a4"+
		"\u06b4\7u\2\2\u05a5\u05a6\7C\2\2\u05a6\u05a7\7d\2\2\u05a7\u05a8\7u\2\2"+
		"\u05a8\u05a9\7v\2\2\u05a9\u05aa\7t\2\2\u05aa\u05ab\7c\2\2\u05ab\u05ac"+
		"\7e\2\2\u05ac\u05ad\7v\2\2\u05ad\u05ae\7K\2\2\u05ae\u06b4\7F\2\2\u05af"+
		"\u05b0\7U\2\2\u05b0\u05b1\7{\2\2\u05b1\u05b2\7u\2\2\u05b2\u05b3\7v\2\2"+
		"\u05b3\u05b4\7g\2\2\u05b4\u05b5\7o\2\2\u05b5\u05b6\7U\2\2\u05b6\u05b7"+
		"\7v\2\2\u05b7\u05b8\7c\2\2\u05b8\u05b9\7v\2\2\u05b9\u06b4\7g\2\2\u05ba"+
		"\u05bb\7U\2\2\u05bb\u05bc\7j\2\2\u05bc\u05bd\7c\2\2\u05bd\u05be\7r\2\2"+
		"\u05be\u06b4\7g\2\2\u05bf\u05c0\7U\2\2\u05c0\u05c1\7n\2\2\u05c1\u05c2"+
		"\7k\2\2\u05c2\u05c3\7f\2\2\u05c3\u05c4\7g\2\2\u05c4\u06b4\7t\2\2\u05c5"+
		"\u05c6\7W\2\2\u05c6\u05c7\7K\2\2\u05c7\u05c8\7C\2\2\u05c8\u05c9\7E\2\2"+
		"\u05c9\u05ca\7n\2\2\u05ca\u05cb\7c\2\2\u05cb\u05cc\7u\2\2\u05cc\u05cd"+
		"\7u\2\2\u05cd\u05ce\7P\2\2\u05ce\u05cf\7c\2\2\u05cf\u05d0\7o\2\2\u05d0"+
		"\u06b4\7g\2\2\u05d1\u05d2\7Y\2\2\u05d2\u05d3\7k\2\2\u05d3\u05d4\7f\2\2"+
		"\u05d4\u05d5\7i\2\2\u05d5\u05d6\7g\2\2\u05d6\u05d7\7v\2\2\u05d7\u05d8"+
		"\7O\2\2\u05d8\u05d9\7c\2\2\u05d9\u06b4\7r\2\2\u05da\u05db\7W\2\2\u05db"+
		"\u05dc\7K\2\2\u05dc\u05dd\7C\2\2\u05dd\u05de\7K\2\2\u05de\u05df\7v\2\2"+
		"\u05df\u05e0\7g\2\2\u05e0\u05e1\7o\2\2\u05e1\u05e2\7U\2\2\u05e2\u05e3"+
		"\7v\2\2\u05e3\u05e4\7c\2\2\u05e4\u05e5\7v\2\2\u05e5\u05e6\7w\2\2\u05e6"+
		"\u06b4\7u\2\2\u05e7\u05e8\7C\2\2\u05e8\u05e9\7d\2\2\u05e9\u05ea\7u\2\2"+
		"\u05ea\u05eb\7*\2\2\u05eb\u05ec\7T\2\2\u05ec\u05ed\7.\2\2\u05ed\u05ee"+
		"\7V\2\2\u05ee\u05ef\7.\2\2\u05ef\u05f0\7R\2\2\u05f0\u05f1\7+\2\2\u05f1"+
		"\u05f2\7K\2\2\u05f2\u06b4\7F\2\2\u05f3\u05f4\7U\2\2\u05f4\u05f5\7v\2\2"+
		"\u05f5\u05f6\7f\2\2\u05f6\u05f7\7K\2\2\u05f7\u06b4\7p\2\2\u05f8\u05f9"+
		"\7U\2\2\u05f9\u05fa\7v\2\2\u05fa\u05fb\7f\2\2\u05fb\u05fc\7G\2\2\u05fc"+
		"\u05fd\7t\2\2\u05fd\u06b4\7t\2\2\u05fe\u05ff\7W\2\2\u05ff\u0600\7K\2\2"+
		"\u0600\u0601\7C\2\2\u0601\u0602\7J\2\2\u0602\u0603\7g\2\2\u0603\u0604"+
		"\7n\2\2\u0604\u0605\7r\2\2\u0605\u0606\7V\2\2\u0606\u0607\7g\2\2\u0607"+
		"\u0608\7z\2\2\u0608\u06b4\7v\2\2\u0609\u060a\7F\2\2\u060a\u060b\7{\2\2"+
		"\u060b\u060c\7p\2\2\u060c\u060d\7F\2\2\u060d\u060e\7g\2\2\u060e\u060f"+
		"\7u\2\2\u060f\u06b4\7e\2\2\u0610\u0611\7W\2\2\u0611\u0612\7K\2\2\u0612"+
		"\u0613\7C\2\2\u0613\u0614\7C\2\2\u0614\u0615\7e\2\2\u0615\u0616\7e\2\2"+
		"\u0616\u0617\7g\2\2\u0617\u0618\7n\2\2\u0618\u0619\7g\2\2\u0619\u061a"+
		"\7t\2\2\u061a\u061b\7c\2\2\u061b\u061c\7v\2\2\u061c\u061d\7q\2\2\u061d"+
		"\u061e\7t\2\2\u061e\u061f\7M\2\2\u061f\u0620\7g\2\2\u0620\u06b4\7{\2\2"+
		"\u0621\u0622\7R\2\2\u0622\u0623\7c\2\2\u0623\u0624\7v\2\2\u0624\u06b4"+
		"\7j\2\2\u0625\u0626\7W\2\2\u0626\u0627\7K\2\2\u0627\u0628\7C\2\2\u0628"+
		"\u0629\7T\2\2\u0629\u062a\7w\2\2\u062a\u062b\7p\2\2\u062b\u062c\7v\2\2"+
		"\u062c\u062d\7k\2\2\u062d\u062e\7o\2\2\u062e\u062f\7g\2\2\u062f\u0630"+
		"\7K\2\2\u0630\u06b4\7f\2\2\u0631\u0632\7W\2\2\u0632\u0633\7K\2\2\u0633"+
		"\u0634\7C\2\2\u0634\u0635\7D\2\2\u0635\u0636\7q\2\2\u0636\u0637\7w\2\2"+
		"\u0637\u0638\7p\2\2\u0638\u0639\7f\2\2\u0639\u063a\7k\2\2\u063a\u063b"+
		"\7p\2\2\u063b\u063c\7i\2\2\u063c\u063d\7T\2\2\u063d\u063e\7g\2\2\u063e"+
		"\u063f\7e\2\2\u063f\u0640\7v\2\2\u0640\u0641\7c\2\2\u0641\u0642\7p\2\2"+
		"\u0642\u0643\7i\2\2\u0643\u0644\7n\2\2\u0644\u06b4\7g\2\2\u0645\u0646"+
		"\7G\2\2\u0646\u0647\7z\2\2\u0647\u0648\7g\2\2\u0648\u0649\7e\2\2\u0649"+
		"\u064a\7w\2\2\u064a\u064b\7v\2\2\u064b\u064c\7g\2\2\u064c\u064d\7f\2\2"+
		"\u064d\u064e\7C\2\2\u064e\u064f\7e\2\2\u064f\u0650\7v\2\2\u0650\u0651"+
		"\7k\2\2\u0651\u0652\7q\2\2\u0652\u06b4\7p\2\2\u0653\u0654\7W\2\2\u0654"+
		"\u0655\7K\2\2\u0655\u0656\7C\2\2\u0656\u0657\7P\2\2\u0657\u0658\7c\2\2"+
		"\u0658\u0659\7o\2\2\u0659\u06b4\7g\2\2\u065a\u065b\7V\2\2\u065b\u065c"+
		"\7k\2\2\u065c\u065d\7v\2\2\u065d\u065e\7n\2\2\u065e\u06b4\7g\2\2\u065f"+
		"\u0660\7V\2\2\u0660\u0661\7g\2\2\u0661\u0662\7z\2\2\u0662\u06b4\7v\2\2"+
		"\u0663\u0664\7V\2\2\u0664\u0665\7c\2\2\u0665\u0666\7t\2\2\u0666\u0667"+
		"\7i\2\2\u0667\u0668\7g\2\2\u0668\u0669\7v\2\2\u0669\u06b4\7u\2\2\u066a"+
		"\u066b\7C\2\2\u066b\u066c\7d\2\2\u066c\u066d\7u\2\2\u066d\u066e\7*\2\2"+
		"\u066e\u066f\7T\2\2\u066f\u0670\7+\2\2\u0670\u0671\7K\2\2\u0671\u06b4"+
		"\7F\2\2\u0672\u0673\7C\2\2\u0673\u0674\7e\2\2\u0674\u0675\7v\2\2\u0675"+
		"\u0676\7k\2\2\u0676\u0677\7q\2\2\u0677\u0678\7p\2\2\u0678\u0679\7U\2\2"+
		"\u0679\u067a\7g\2\2\u067a\u06b4\7v\2\2\u067b\u067c\7W\2\2\u067c\u067d"+
		"\7K\2\2\u067d\u067e\7C\2\2\u067e\u067f\7K\2\2\u067f\u0680\7v\2\2\u0680"+
		"\u0681\7g\2\2\u0681\u0682\7o\2\2\u0682\u0683\7V\2\2\u0683\u0684\7{\2\2"+
		"\u0684\u0685\7r\2\2\u0685\u06b4\7g\2\2\u0686\u0687\7U\2\2\u0687\u0688"+
		"\7{\2\2\u0688\u0689\7u\2\2\u0689\u068a\7v\2\2\u068a\u068b\7g\2\2\u068b"+
		"\u068c\7o\2\2\u068c\u068d\7C\2\2\u068d\u068e\7e\2\2\u068e\u068f\7v\2\2"+
		"\u068f\u0690\7k\2\2\u0690\u0691\7x\2\2\u0691\u0692\7c\2\2\u0692\u0693"+
		"\7v\2\2\u0693\u0694\7q\2\2\u0694\u06b4\7t\2\2\u0695\u0696\7U\2\2\u0696"+
		"\u0697\7v\2\2\u0697\u0698\7f\2\2\u0698\u0699\7Q\2\2\u0699\u069a\7w\2\2"+
		"\u069a\u06b4\7v\2\2\u069b\u069c\7E\2\2\u069c\u069d\7q\2\2\u069d\u069e"+
		"\7p\2\2\u069e\u069f\7e\2\2\u069f\u06a0\7t\2\2\u06a0\u06a1\7g\2\2\u06a1"+
		"\u06a2\7v\2\2\u06a2\u06a3\7g\2\2\u06a3\u06a4\7K\2\2\u06a4\u06b4\7F\2\2"+
		"\u06a5\u06a6\7R\2\2\u06a6\u06a7\7t\2\2\u06a7\u06a8\7q\2\2\u06a8\u06a9"+
		"\7e\2\2\u06a9\u06aa\7g\2\2\u06aa\u06ab\7u\2\2\u06ab\u06ac\7u\2\2\u06ac"+
		"\u06ad\7J\2\2\u06ad\u06ae\7c\2\2\u06ae\u06af\7p\2\2\u06af\u06b0\7f\2\2"+
		"\u06b0\u06b1\7n\2\2\u06b1\u06b2\7g\2\2\u06b2\u06b4\7u\2\2\u06b3\u04a9"+
		"\3\2\2\2\u06b3\u04b5\3\2\2\2\u06b3\u04c5\3\2\2\2\u06b3\u04d0\3\2\2\2\u06b3"+
		"\u04d8\3\2\2\2\u06b3\u04e8\3\2\2\2\u06b3\u04ec\3\2\2\2\u06b3\u04f9\3\2"+
		"\2\2\u06b3\u0507\3\2\2\2\u06b3\u0513\3\2\2\2\u06b3\u051d\3\2\2\2\u06b3"+
		"\u0526\3\2\2\2\u06b3\u053c\3\2\2\2\u06b3\u054b\3\2\2\2\u06b3\u0559\3\2"+
		"\2\2\u06b3\u0570\3\2\2\2\u06b3\u057e\3\2\2\2\u06b3\u058b\3\2\2\2\u06b3"+
		"\u058f\3\2\2\2\u06b3\u0592\3\2\2\2\u06b3\u059c\3\2\2\2\u06b3\u05a5\3\2"+
		"\2\2\u06b3\u05af\3\2\2\2\u06b3\u05ba\3\2\2\2\u06b3\u05bf\3\2\2\2\u06b3"+
		"\u05c5\3\2\2\2\u06b3\u05d1\3\2\2\2\u06b3\u05da\3\2\2\2\u06b3\u05e7\3\2"+
		"\2\2\u06b3\u05f3\3\2\2\2\u06b3\u05f8\3\2\2\2\u06b3\u05fe\3\2\2\2\u06b3"+
		"\u0609\3\2\2\2\u06b3\u0610\3\2\2\2\u06b3\u0621\3\2\2\2\u06b3\u0625\3\2"+
		"\2\2\u06b3\u0631\3\2\2\2\u06b3\u0645\3\2\2\2\u06b3\u0653\3\2\2\2\u06b3"+
		"\u065a\3\2\2\2\u06b3\u065f\3\2\2\2\u06b3\u0663\3\2\2\2\u06b3\u066a\3\2"+
		"\2\2\u06b3\u0672\3\2\2\2\u06b3\u067b\3\2\2\2\u06b3\u0686\3\2\2\2\u06b3"+
		"\u0695\3\2\2\2\u06b3\u069b\3\2\2\2\u06b3\u06a5\3\2\2\2\u06b4\u0082\3\2"+
		"\2\2\33\2\u0099\u012e\u0134\u0139\u013e\u0143\u0149\u014e\u0154\u015b"+
		"\u015d\u0166\u0177\u01a0\u0211\u0215\u0219\u021e\u0226\u022d\u0233\u035e"+
		"\u04a7\u06b3\22\3\4\2\3\5\3\3\6\4\3\7\5\3\b\6\3\t\7\3\n\b\3\13\t\3\f\n"+
		"\3\r\13\3\16\f\3\17\r\3\23\16\3\30\17\3;\20\3<\21";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
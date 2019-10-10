// Generated from Function.g4 by ANTLR 4.4
package parsing;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class FunctionLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WHITESPACE=1, NUMBER=2, VARIABLE=3, OP=4, LP=5, RP=6, STOP=7;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'"
	};
	public static final String[] ruleNames = {
		"WHITESPACE", "NUMBER", "VARIABLE", "OP", "LP", "RP", "STOP"
	};


	public FunctionLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Function.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\t-\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\6\2\23\n\2\r\2\16\2"+
		"\24\3\3\6\3\30\n\3\r\3\16\3\31\3\4\6\4\35\n\4\r\4\16\4\36\3\4\6\4\"\n"+
		"\4\r\4\16\4#\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\2\2\t\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\3\2\6\5\2\13\f\17\17\"\"\3\2\62;\4\2C\\c|\5\2,-//\61\61\60"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\3\22\3\2\2\2\5\27\3\2\2\2\7\34\3\2\2\2\t%\3\2\2\2\13"+
		"\'\3\2\2\2\r)\3\2\2\2\17+\3\2\2\2\21\23\t\2\2\2\22\21\3\2\2\2\23\24\3"+
		"\2\2\2\24\22\3\2\2\2\24\25\3\2\2\2\25\4\3\2\2\2\26\30\t\3\2\2\27\26\3"+
		"\2\2\2\30\31\3\2\2\2\31\27\3\2\2\2\31\32\3\2\2\2\32\6\3\2\2\2\33\35\t"+
		"\4\2\2\34\33\3\2\2\2\35\36\3\2\2\2\36\34\3\2\2\2\36\37\3\2\2\2\37!\3\2"+
		"\2\2 \"\t\3\2\2! \3\2\2\2\"#\3\2\2\2#!\3\2\2\2#$\3\2\2\2$\b\3\2\2\2%&"+
		"\t\5\2\2&\n\3\2\2\2\'(\7*\2\2(\f\3\2\2\2)*\7+\2\2*\16\3\2\2\2+,\7=\2\2"+
		",\20\3\2\2\2\7\2\24\31\36#\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
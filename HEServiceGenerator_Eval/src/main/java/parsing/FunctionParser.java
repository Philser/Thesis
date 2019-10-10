// Generated from Function.g4 by ANTLR 4.4
package parsing;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class FunctionParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WHITESPACE=1, NUMBER=2, VARIABLE=3, OP=4, LP=5, RP=6, STOP=7;
	public static final String[] tokenNames = {
		"<INVALID>", "WHITESPACE", "NUMBER", "VARIABLE", "OP", "'('", "')'", "';'"
	};
	public static final int
		RULE_func = 0, RULE_add = 1, RULE_mul = 2, RULE_term = 3;
	public static final String[] ruleNames = {
		"func", "add", "mul", "term"
	};

	@Override
	public String getGrammarFileName() { return "Function.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public FunctionParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FuncContext extends ParserRuleContext {
		public AddContext add() {
			return getRuleContext(AddContext.class,0);
		}
		public FuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_func; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FunctionVisitor ) return ((FunctionVisitor<? extends T>)visitor).visitFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncContext func() throws RecognitionException {
		FuncContext _localctx = new FuncContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_func);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(8); add(0);
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

	public static class AddContext extends ParserRuleContext {
		public MulContext mul() {
			return getRuleContext(MulContext.class,0);
		}
		public TerminalNode OP() { return getToken(FunctionParser.OP, 0); }
		public AddContext add() {
			return getRuleContext(AddContext.class,0);
		}
		public AddContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_add; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FunctionVisitor ) return ((FunctionVisitor<? extends T>)visitor).visitAdd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AddContext add() throws RecognitionException {
		return add(0);
	}

	private AddContext add(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AddContext _localctx = new AddContext(_ctx, _parentState);
		AddContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_add, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(11); mul(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(18);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AddContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_add);
					setState(13);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(14); match(OP);
					setState(15); mul(0);
					}
					} 
				}
				setState(20);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
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

	public static class MulContext extends ParserRuleContext {
		public MulContext mul() {
			return getRuleContext(MulContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public TerminalNode OP() { return getToken(FunctionParser.OP, 0); }
		public MulContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mul; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FunctionVisitor ) return ((FunctionVisitor<? extends T>)visitor).visitMul(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MulContext mul() throws RecognitionException {
		return mul(0);
	}

	private MulContext mul(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		MulContext _localctx = new MulContext(_ctx, _parentState);
		MulContext _prevctx = _localctx;
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_mul, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(22); term();
			}
			_ctx.stop = _input.LT(-1);
			setState(29);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MulContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_mul);
					setState(24);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(25); match(OP);
					setState(26); term();
					}
					} 
				}
				setState(31);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
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

	public static class TermContext extends ParserRuleContext {
		public TerminalNode RP() { return getToken(FunctionParser.RP, 0); }
		public TerminalNode LP() { return getToken(FunctionParser.LP, 0); }
		public TerminalNode NUMBER() { return getToken(FunctionParser.NUMBER, 0); }
		public TerminalNode VARIABLE() { return getToken(FunctionParser.VARIABLE, 0); }
		public AddContext add() {
			return getRuleContext(AddContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FunctionVisitor ) return ((FunctionVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_term);
		try {
			setState(38);
			switch (_input.LA(1)) {
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(32); match(NUMBER);
				}
				break;
			case VARIABLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(33); match(VARIABLE);
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 3);
				{
				setState(34); match(LP);
				setState(35); add(0);
				setState(36); match(RP);
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
		case 1: return add_sempred((AddContext)_localctx, predIndex);
		case 2: return mul_sempred((MulContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean add_sempred(AddContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean mul_sempred(MulContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1: return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\t+\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\7\3\23\n\3\f\3\16\3"+
		"\26\13\3\3\4\3\4\3\4\3\4\3\4\3\4\7\4\36\n\4\f\4\16\4!\13\4\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\5\5)\n\5\3\5\2\4\4\6\6\2\4\6\b\2\2*\2\n\3\2\2\2\4\f\3\2\2"+
		"\2\6\27\3\2\2\2\b(\3\2\2\2\n\13\5\4\3\2\13\3\3\2\2\2\f\r\b\3\1\2\r\16"+
		"\5\6\4\2\16\24\3\2\2\2\17\20\f\4\2\2\20\21\7\6\2\2\21\23\5\6\4\2\22\17"+
		"\3\2\2\2\23\26\3\2\2\2\24\22\3\2\2\2\24\25\3\2\2\2\25\5\3\2\2\2\26\24"+
		"\3\2\2\2\27\30\b\4\1\2\30\31\5\b\5\2\31\37\3\2\2\2\32\33\f\4\2\2\33\34"+
		"\7\6\2\2\34\36\5\b\5\2\35\32\3\2\2\2\36!\3\2\2\2\37\35\3\2\2\2\37 \3\2"+
		"\2\2 \7\3\2\2\2!\37\3\2\2\2\")\7\4\2\2#)\7\5\2\2$%\7\7\2\2%&\5\4\3\2&"+
		"\'\7\b\2\2\')\3\2\2\2(\"\3\2\2\2(#\3\2\2\2($\3\2\2\2)\t\3\2\2\2\5\24\37"+
		"(";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
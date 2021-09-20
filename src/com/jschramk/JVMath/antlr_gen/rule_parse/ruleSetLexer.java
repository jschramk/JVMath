// Generated from C:/Users/Jacob Schramkowski/OneDrive/Projects/Solvable/JVMath/src/com/jschramk/JVMath/parse\ruleSet.g4 by ANTLR 4.9.1
package com.jschramk.JVMath.antlr_gen.rule_parse;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ruleSetLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, ACTION=3, ID=4, VARIABLE=5, NESTED_PARENS=6, NESTED_BRACKETS=7, 
		WS=8, COMMENT=9, LINE_COMMENT=10;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "ACTION", "ID", "VARIABLE", "NESTED_PARENS", "NESTED_BRACKETS", 
			"WS", "COMMENT", "LINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'where'", "'for'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "ACTION", "ID", "VARIABLE", "NESTED_PARENS", "NESTED_BRACKETS", 
			"WS", "COMMENT", "LINE_COMMENT"
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


	public ruleSetLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ruleSet.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\fn\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4/\n\4\3\5\3\5\3\5\3\6\3\6\7\6\66\n\6\f\6"+
		"\16\69\13\6\3\7\3\7\3\7\7\7>\n\7\f\7\16\7A\13\7\3\7\3\7\3\b\3\b\3\b\7"+
		"\bH\n\b\f\b\16\bK\13\b\3\b\3\b\3\t\6\tP\n\t\r\t\16\tQ\3\t\3\t\3\n\3\n"+
		"\3\n\3\n\7\nZ\n\n\f\n\16\n]\13\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\7\13h\n\13\f\13\16\13k\13\13\3\13\3\13\3[\2\f\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\3\2\b\5\2C\\aac|\6\2\62;C\\aac|\3\2*+\4\2}}"+
		"\177\177\5\2\13\f\17\17\"\"\4\2\f\f\17\17\2v\2\3\3\2\2\2\2\5\3\2\2\2\2"+
		"\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2"+
		"\2\2\2\23\3\2\2\2\2\25\3\2\2\2\3\27\3\2\2\2\5\35\3\2\2\2\7.\3\2\2\2\t"+
		"\60\3\2\2\2\13\63\3\2\2\2\r:\3\2\2\2\17D\3\2\2\2\21O\3\2\2\2\23U\3\2\2"+
		"\2\25c\3\2\2\2\27\30\7y\2\2\30\31\7j\2\2\31\32\7g\2\2\32\33\7t\2\2\33"+
		"\34\7g\2\2\34\4\3\2\2\2\35\36\7h\2\2\36\37\7q\2\2\37 \7t\2\2 \6\3\2\2"+
		"\2!\"\7u\2\2\"#\7q\2\2#$\7n\2\2$%\7x\2\2%/\7g\2\2&\'\7u\2\2\'(\7k\2\2"+
		"()\7o\2\2)*\7r\2\2*+\7n\2\2+,\7k\2\2,-\7h\2\2-/\7{\2\2.!\3\2\2\2.&\3\2"+
		"\2\2/\b\3\2\2\2\60\61\7B\2\2\61\62\5\13\6\2\62\n\3\2\2\2\63\67\t\2\2\2"+
		"\64\66\t\3\2\2\65\64\3\2\2\2\669\3\2\2\2\67\65\3\2\2\2\678\3\2\2\28\f"+
		"\3\2\2\29\67\3\2\2\2:?\7*\2\2;>\n\4\2\2<>\5\r\7\2=;\3\2\2\2=<\3\2\2\2"+
		">A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@B\3\2\2\2A?\3\2\2\2BC\7+\2\2C\16\3\2\2"+
		"\2DI\7}\2\2EH\n\5\2\2FH\5\17\b\2GE\3\2\2\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2"+
		"\2IJ\3\2\2\2JL\3\2\2\2KI\3\2\2\2LM\7\177\2\2M\20\3\2\2\2NP\t\6\2\2ON\3"+
		"\2\2\2PQ\3\2\2\2QO\3\2\2\2QR\3\2\2\2RS\3\2\2\2ST\b\t\2\2T\22\3\2\2\2U"+
		"V\7\61\2\2VW\7,\2\2W[\3\2\2\2XZ\13\2\2\2YX\3\2\2\2Z]\3\2\2\2[\\\3\2\2"+
		"\2[Y\3\2\2\2\\^\3\2\2\2][\3\2\2\2^_\7,\2\2_`\7\61\2\2`a\3\2\2\2ab\b\n"+
		"\2\2b\24\3\2\2\2cd\7\61\2\2de\7\61\2\2ei\3\2\2\2fh\n\7\2\2gf\3\2\2\2h"+
		"k\3\2\2\2ig\3\2\2\2ij\3\2\2\2jl\3\2\2\2ki\3\2\2\2lm\b\13\2\2m\26\3\2\2"+
		"\2\f\2.\67=?GIQ[i\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
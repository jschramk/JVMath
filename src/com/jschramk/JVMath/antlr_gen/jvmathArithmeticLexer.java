// Generated from C:/Users/Jacob Schramkowski/OneDrive/Projects/Solvable/JVMath/src/com/jschramk/JVMath/parse\jvmathArithmetic.g4 by ANTLR 4.9
package com.jschramk.JVMath.antlr_gen;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class jvmathArithmeticLexer extends Lexer {

  public static final int VARIABLE = 1, SCIENTIFIC_NUMBER = 2, SPACE = 3, LPAREN = 4, RPAREN = 5, PLUS = 6, MINUS = 7, TIMES = 8, DIV = 9, GT = 10, LT = 11, EQ = 12, POINT = 13, POW = 14;
  public static final String[] ruleNames = makeRuleNames();
  /**
   * @deprecated Use {@link #VOCABULARY} instead.
   */
  @Deprecated
  public static final String[] tokenNames;
  public static final String _serializedATN = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\20l\b\1\4\2\t\2\4" + "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t" + "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" + "\4\23\t\23\4\24\t\24\3\2\3\2\7\2,\n\2\f\2\16\2/\13\2\3\3\5\3\62\n\3\3" + "\4\3\4\5\4\66\n\4\3\5\3\5\3\5\5\5;\n\5\3\5\3\5\5\5?\n\5\3\6\6\6B\n\6\r" + "\6\16\6C\3\6\3\6\6\6H\n\6\r\6\16\6I\5\6L\n\6\3\7\3\7\3\b\3\b\3\t\6\tS" + "\n\t\r\t\16\tT\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3" + "\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\2\2\25\3\3\5\2\7\2\t" + "\4\13\2\r\2\17\2\21\5\23\6\25\7\27\b\31\t\33\n\35\13\37\f!\r#\16%\17\'" + "\20\3\2\b\5\2C\\aac|\4\2GGgg\4\2--//\4\2//\u2214\u2214\5\2,,\u00d9\u00d9" + "\u22c7\u22c7\4\2\61\61\u00f9\u00f9\2n\2\3\3\2\2\2\2\t\3\2\2\2\2\21\3\2" + "\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2" + "\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2" + "\2\3)\3\2\2\2\5\61\3\2\2\2\7\65\3\2\2\2\t\67\3\2\2\2\13A\3\2\2\2\rM\3" + "\2\2\2\17O\3\2\2\2\21R\3\2\2\2\23V\3\2\2\2\25X\3\2\2\2\27Z\3\2\2\2\31" + "\\\3\2\2\2\33^\3\2\2\2\35`\3\2\2\2\37b\3\2\2\2!d\3\2\2\2#f\3\2\2\2%h\3" + "\2\2\2\'j\3\2\2\2)-\5\5\3\2*,\5\7\4\2+*\3\2\2\2,/\3\2\2\2-+\3\2\2\2-." + "\3\2\2\2.\4\3\2\2\2/-\3\2\2\2\60\62\t\2\2\2\61\60\3\2\2\2\62\6\3\2\2\2" + "\63\66\5\5\3\2\64\66\4\62;\2\65\63\3\2\2\2\65\64\3\2\2\2\66\b\3\2\2\2" + "\67>\5\13\6\28:\5\r\7\29;\5\17\b\2:9\3\2\2\2:;\3\2\2\2;<\3\2\2\2<=\5\13" + "\6\2=?\3\2\2\2>8\3\2\2\2>?\3\2\2\2?\n\3\2\2\2@B\4\62;\2A@\3\2\2\2BC\3" + "\2\2\2CA\3\2\2\2CD\3\2\2\2DK\3\2\2\2EG\7\60\2\2FH\4\62;\2GF\3\2\2\2HI" + "\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JL\3\2\2\2KE\3\2\2\2KL\3\2\2\2L\f\3\2\2\2" + "MN\t\3\2\2N\16\3\2\2\2OP\t\4\2\2P\20\3\2\2\2QS\7\"\2\2RQ\3\2\2\2ST\3\2" + "\2\2TR\3\2\2\2TU\3\2\2\2U\22\3\2\2\2VW\7*\2\2W\24\3\2\2\2XY\7+\2\2Y\26" + "\3\2\2\2Z[\7-\2\2[\30\3\2\2\2\\]\t\5\2\2]\32\3\2\2\2^_\t\6\2\2_\34\3\2" + "\2\2`a\t\7\2\2a\36\3\2\2\2bc\7@\2\2c \3\2\2\2de\7>\2\2e\"\3\2\2\2fg\7" + "?\2\2g$\3\2\2\2hi\7\60\2\2i&\3\2\2\2jk\7`\2\2k(\3\2\2\2\f\2-\61\65:>C" + "IKT\2";
  public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
  private static final String[] _LITERAL_NAMES = makeLiteralNames();
  private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
  public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
  public static String[] channelNames = {
    "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
  };
  public static String[] modeNames = {
    "DEFAULT_MODE"
  };

  static {
    RuntimeMetaData.checkVersion("4.9", RuntimeMetaData.VERSION);
  }

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

  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }

  public jvmathArithmeticLexer(CharStream input) {
    super(input);
    _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
  }

  private static String[] makeRuleNames() {
    return new String[]{
      "VARIABLE",
      "VALID_ID_START",
      "VALID_ID_CHAR",
      "SCIENTIFIC_NUMBER",
      "NUMBER",
      "E",
      "SIGN",
      "SPACE",
      "LPAREN",
      "RPAREN",
      "PLUS",
      "MINUS",
      "TIMES",
      "DIV",
      "GT",
      "LT",
      "EQ",
      "POINT",
      "POW"
    };
  }

  private static String[] makeLiteralNames() {
    return new String[]{
      null,
      null,
      null,
      null,
      "'('",
      "')'",
      "'+'",
      null,
      null,
      null,
      "'>'",
      "'<'",
      "'='",
      "'.'",
      "'^'"
    };
  }

  private static String[] makeSymbolicNames() {
    return new String[]{
      null,
      "VARIABLE",
      "SCIENTIFIC_NUMBER",
      "SPACE",
      "LPAREN",
      "RPAREN",
      "PLUS",
      "MINUS",
      "TIMES",
      "DIV",
      "GT",
      "LT",
      "EQ",
      "POINT",
      "POW"
    };
  }

  @Override
  public String[] getRuleNames() {
    return ruleNames;
  }

  @Override

  public Vocabulary getVocabulary() {
    return VOCABULARY;
  }

  @Override
  public String getSerializedATN() {
    return _serializedATN;
  }

  @Override
  public String getGrammarFileName() {
    return "jvmathArithmetic.g4";
  }

  @Override
  public ATN getATN() {
    return _ATN;
  }

  @Override
  public String[] getChannelNames() {
    return channelNames;
  }

  @Override
  public String[] getModeNames() {
    return modeNames;
  }

  @Override
  @Deprecated
  public String[] getTokenNames() {
    return tokenNames;
  }
}
// Generated from C:/Users/Jacob Schramkowski/OneDrive/Projects/Solvable/JVMath/src/com/jschramk/JVMath/parse\jvmathArithmetic.g4 by ANTLR 4.9
package com.jschramk.JVMath.utilities.antlr_gen;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class jvmathArithmeticParser extends Parser {

  public static final int VARIABLE = 1, SCIENTIFIC_NUMBER = 2, SPACE = 3, LPAREN = 4, RPAREN = 5, PLUS = 6, MINUS = 7, TIMES = 8, DIV = 9, GT = 10, LT = 11, EQ = 12, POINT = 13, POW = 14;
  public static final int RULE_equation = 0, RULE_sum = 1, RULE_product = 2, RULE_signedFactor = 3, RULE_factor = 4, RULE_signedAtom = 5, RULE_atom = 6, RULE_literal = 7, RULE_variable = 8, RULE_comparator = 9;
  public static final String[] ruleNames = makeRuleNames();
  /**
   * @deprecated Use {@link #VOCABULARY} instead.
   */
  @Deprecated
  public static final String[] tokenNames;
  public static final String _serializedATN = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\20\u008c\4\2\t\2" + "\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" + "\t\13\3\2\5\2\30\n\2\3\2\3\2\5\2\34\n\2\3\2\3\2\5\2 \n\2\3\2\3\2\5\2$" + "\n\2\3\3\3\3\5\3(\n\3\3\3\3\3\5\3,\n\3\3\3\7\3/\n\3\f\3\16\3\62\13\3\3" + "\4\3\4\5\4\66\n\4\3\4\5\49\n\4\3\4\5\4<\n\4\3\4\7\4?\n\4\f\4\16\4B\13" + "\4\3\4\3\4\5\4F\n\4\3\4\3\4\5\4J\n\4\3\4\7\4M\n\4\f\4\16\4P\13\4\5\4R" + "\n\4\3\5\3\5\5\5V\n\5\3\5\3\5\5\5Z\n\5\3\5\7\5]\n\5\f\5\16\5`\13\5\3\6" + "\3\6\5\6d\n\6\3\6\3\6\5\6h\n\6\3\6\7\6k\n\6\f\6\16\6n\13\6\3\7\3\7\5\7" + "r\n\7\3\7\3\7\5\7v\n\7\3\b\3\b\3\b\3\b\5\b|\n\b\3\b\3\b\5\b\u0080\n\b" + "\3\b\3\b\5\b\u0084\n\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\2\2\f\2\4\6\b\n" + "\f\16\20\22\24\2\6\3\2\b\t\4\2\5\5\n\13\3\2\n\13\3\2\f\16\2\u009c\2\27" + "\3\2\2\2\4%\3\2\2\2\6Q\3\2\2\2\bS\3\2\2\2\na\3\2\2\2\fu\3\2\2\2\16\u0083" + "\3\2\2\2\20\u0085\3\2\2\2\22\u0087\3\2\2\2\24\u0089\3\2\2\2\26\30\7\5" + "\2\2\27\26\3\2\2\2\27\30\3\2\2\2\30\31\3\2\2\2\31\33\5\4\3\2\32\34\7\5" + "\2\2\33\32\3\2\2\2\33\34\3\2\2\2\34\35\3\2\2\2\35\37\5\24\13\2\36 \7\5" + "\2\2\37\36\3\2\2\2\37 \3\2\2\2 !\3\2\2\2!#\5\4\3\2\"$\7\5\2\2#\"\3\2\2" + "\2#$\3\2\2\2$\3\3\2\2\2%\60\5\6\4\2&(\7\5\2\2\'&\3\2\2\2\'(\3\2\2\2()" + "\3\2\2\2)+\t\2\2\2*,\7\5\2\2+*\3\2\2\2+,\3\2\2\2,-\3\2\2\2-/\5\6\4\2." + "\'\3\2\2\2/\62\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\5\3\2\2\2\62\60\3" + "\2\2\2\63@\5\b\5\2\64\66\7\5\2\2\65\64\3\2\2\2\65\66\3\2\2\2\668\3\2\2" + "\2\679\t\3\2\28\67\3\2\2\289\3\2\2\29;\3\2\2\2:<\7\5\2\2;:\3\2\2\2;<\3" + "\2\2\2<=\3\2\2\2=?\5\n\6\2>\65\3\2\2\2?B\3\2\2\2@>\3\2\2\2@A\3\2\2\2A" + "R\3\2\2\2B@\3\2\2\2CN\5\b\5\2DF\7\5\2\2ED\3\2\2\2EF\3\2\2\2FG\3\2\2\2" + "GI\t\4\2\2HJ\7\5\2\2IH\3\2\2\2IJ\3\2\2\2JK\3\2\2\2KM\5\b\5\2LE\3\2\2\2" + "MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2OR\3\2\2\2PN\3\2\2\2Q\63\3\2\2\2QC\3\2\2" + "\2R\7\3\2\2\2S^\5\f\7\2TV\7\5\2\2UT\3\2\2\2UV\3\2\2\2VW\3\2\2\2WY\7\20" + "\2\2XZ\7\5\2\2YX\3\2\2\2YZ\3\2\2\2Z[\3\2\2\2[]\5\f\7\2\\U\3\2\2\2]`\3" + "\2\2\2^\\\3\2\2\2^_\3\2\2\2_\t\3\2\2\2`^\3\2\2\2al\5\16\b\2bd\7\5\2\2" + "cb\3\2\2\2cd\3\2\2\2de\3\2\2\2eg\7\20\2\2fh\7\5\2\2gf\3\2\2\2gh\3\2\2" + "\2hi\3\2\2\2ik\5\f\7\2jc\3\2\2\2kn\3\2\2\2lj\3\2\2\2lm\3\2\2\2m\13\3\2" + "\2\2nl\3\2\2\2oq\7\t\2\2pr\7\5\2\2qp\3\2\2\2qr\3\2\2\2rs\3\2\2\2sv\5\f" + "\7\2tv\5\16\b\2uo\3\2\2\2ut\3\2\2\2v\r\3\2\2\2w\u0084\5\20\t\2x\u0084" + "\5\22\n\2y{\7\6\2\2z|\7\5\2\2{z\3\2\2\2{|\3\2\2\2|}\3\2\2\2}\177\5\4\3" + "\2~\u0080\7\5\2\2\177~\3\2\2\2\177\u0080\3\2\2\2\u0080\u0081\3\2\2\2\u0081" + "\u0082\7\7\2\2\u0082\u0084\3\2\2\2\u0083w\3\2\2\2\u0083x\3\2\2\2\u0083" + "y\3\2\2\2\u0084\17\3\2\2\2\u0085\u0086\7\4\2\2\u0086\21\3\2\2\2\u0087" + "\u0088\7\3\2\2\u0088\23\3\2\2\2\u0089\u008a\t\5\2\2\u008a\25\3\2\2\2\34" + "\27\33\37#\'+\60\658;@EINQUY^cglqu{\177\u0083";
  public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
  private static final String[] _LITERAL_NAMES = makeLiteralNames();
  private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
  public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

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

  public jvmathArithmeticParser(TokenStream input) {
    super(input);
    _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
  }

  private static String[] makeRuleNames() {
    return new String[]{
      "equation",
      "sum",
      "product",
      "signedFactor",
      "factor",
      "signedAtom",
      "atom",
      "literal",
      "variable",
      "comparator"
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
  @Deprecated
  public String[] getTokenNames() {
    return tokenNames;
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

  public final EquationContext equation() throws RecognitionException {
    EquationContext _localctx = new EquationContext(_ctx, getState());
    enterRule(_localctx, 0, RULE_equation);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(21);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == SPACE) {
          {
            setState(20);
            match(SPACE);
          }
        }

        setState(23);
        sum();
        setState(25);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == SPACE) {
          {
            setState(24);
            match(SPACE);
          }
        }

        setState(27);
        comparator();
        setState(29);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == SPACE) {
          {
            setState(28);
            match(SPACE);
          }
        }

        setState(31);
        sum();
        setState(33);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == SPACE) {
          {
            setState(32);
            match(SPACE);
          }
        }

      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public final SumContext sum() throws RecognitionException {
    SumContext _localctx = new SumContext(_ctx, getState());
    enterRule(_localctx, 2, RULE_sum);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(35);
        product();
        setState(46);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 6, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(37);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SPACE) {
                  {
                    setState(36);
                    match(SPACE);
                  }
                }

                setState(39);
                _la = _input.LA(1);
                if (!(_la == PLUS || _la == MINUS)) {
                  _errHandler.recoverInline(this);
                } else {
                  if (_input.LA(1) == Token.EOF) matchedEOF = true;
                  _errHandler.reportMatch(this);
                  consume();
                }
                setState(41);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SPACE) {
                  {
                    setState(40);
                    match(SPACE);
                  }
                }

                setState(43);
                product();
              }
            }
          }
          setState(48);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 6, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public final ProductContext product() throws RecognitionException {
    ProductContext _localctx = new ProductContext(_ctx, getState());
    enterRule(_localctx, 4, RULE_product);
    int _la;
    try {
      int _alt;
      setState(79);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 14, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
        {
          setState(49);
          signedFactor();
          setState(62);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 10, _ctx);
          while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
            if (_alt == 1) {
              {
                {
                  setState(51);
                  _errHandler.sync(this);
                  switch (getInterpreter().adaptivePredict(_input, 7, _ctx)) {
                    case 1: {
                      setState(50);
                      match(SPACE);
                    }
                    break;
                  }
                  setState(54);
                  _errHandler.sync(this);
                  switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
                    case 1: {
                      setState(53);
                      _la = _input.LA(1);
                      if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SPACE) | (1L << TIMES) | (1L << DIV))) != 0))) {
                        _errHandler.recoverInline(this);
                      } else {
                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
                        _errHandler.reportMatch(this);
                        consume();
                      }
                    }
                    break;
                  }
                  setState(57);
                  _errHandler.sync(this);
                  _la = _input.LA(1);
                  if (_la == SPACE) {
                    {
                      setState(56);
                      match(SPACE);
                    }
                  }

                  setState(59);
                  factor();
                }
              }
            }
            setState(64);
            _errHandler.sync(this);
            _alt = getInterpreter().adaptivePredict(_input, 10, _ctx);
          }
        }
        break;
        case 2:
          enterOuterAlt(_localctx, 2);
        {
          setState(65);
          signedFactor();
          setState(76);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 13, _ctx);
          while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
            if (_alt == 1) {
              {
                {
                  setState(67);
                  _errHandler.sync(this);
                  _la = _input.LA(1);
                  if (_la == SPACE) {
                    {
                      setState(66);
                      match(SPACE);
                    }
                  }

                  setState(69);
                  _la = _input.LA(1);
                  if (!(_la == TIMES || _la == DIV)) {
                    _errHandler.recoverInline(this);
                  } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                  }
                  setState(71);
                  _errHandler.sync(this);
                  _la = _input.LA(1);
                  if (_la == SPACE) {
                    {
                      setState(70);
                      match(SPACE);
                    }
                  }

                  setState(73);
                  signedFactor();
                }
              }
            }
            setState(78);
            _errHandler.sync(this);
            _alt = getInterpreter().adaptivePredict(_input, 13, _ctx);
          }
        }
        break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public final SignedFactorContext signedFactor() throws RecognitionException {
    SignedFactorContext _localctx = new SignedFactorContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_signedFactor);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(81);
        signedAtom();
        setState(92);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 17, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(83);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SPACE) {
                  {
                    setState(82);
                    match(SPACE);
                  }
                }

                setState(85);
                match(POW);
                setState(87);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SPACE) {
                  {
                    setState(86);
                    match(SPACE);
                  }
                }

                setState(89);
                signedAtom();
              }
            }
          }
          setState(94);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 17, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public final FactorContext factor() throws RecognitionException {
    FactorContext _localctx = new FactorContext(_ctx, getState());
    enterRule(_localctx, 8, RULE_factor);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(95);
        atom();
        setState(106);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 20, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(97);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SPACE) {
                  {
                    setState(96);
                    match(SPACE);
                  }
                }

                setState(99);
                match(POW);
                setState(101);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SPACE) {
                  {
                    setState(100);
                    match(SPACE);
                  }
                }

                setState(103);
                signedAtom();
              }
            }
          }
          setState(108);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 20, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public final SignedAtomContext signedAtom() throws RecognitionException {
    SignedAtomContext _localctx = new SignedAtomContext(_ctx, getState());
    enterRule(_localctx, 10, RULE_signedAtom);
    int _la;
    try {
      setState(115);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case MINUS:
          enterOuterAlt(_localctx, 1);
        {
          setState(109);
          match(MINUS);
          setState(111);
          _errHandler.sync(this);
          _la = _input.LA(1);
          if (_la == SPACE) {
            {
              setState(110);
              match(SPACE);
            }
          }

          setState(113);
          signedAtom();
        }
        break;
        case VARIABLE:
        case SCIENTIFIC_NUMBER:
        case LPAREN:
          enterOuterAlt(_localctx, 2);
        {
          setState(114);
          atom();
        }
        break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public final AtomContext atom() throws RecognitionException {
    AtomContext _localctx = new AtomContext(_ctx, getState());
    enterRule(_localctx, 12, RULE_atom);
    int _la;
    try {
      setState(129);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case SCIENTIFIC_NUMBER:
          enterOuterAlt(_localctx, 1);
        {
          setState(117);
          literal();
        }
        break;
        case VARIABLE:
          enterOuterAlt(_localctx, 2);
        {
          setState(118);
          variable();
        }
        break;
        case LPAREN:
          enterOuterAlt(_localctx, 3);
        {
          setState(119);
          match(LPAREN);
          setState(121);
          _errHandler.sync(this);
          _la = _input.LA(1);
          if (_la == SPACE) {
            {
              setState(120);
              match(SPACE);
            }
          }

          setState(123);
          sum();
          setState(125);
          _errHandler.sync(this);
          _la = _input.LA(1);
          if (_la == SPACE) {
            {
              setState(124);
              match(SPACE);
            }
          }

          setState(127);
          match(RPAREN);
        }
        break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public final LiteralContext literal() throws RecognitionException {
    LiteralContext _localctx = new LiteralContext(_ctx, getState());
    enterRule(_localctx, 14, RULE_literal);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(131);
        match(SCIENTIFIC_NUMBER);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public final VariableContext variable() throws RecognitionException {
    VariableContext _localctx = new VariableContext(_ctx, getState());
    enterRule(_localctx, 16, RULE_variable);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(133);
        match(VARIABLE);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public final ComparatorContext comparator() throws RecognitionException {
    ComparatorContext _localctx = new ComparatorContext(_ctx, getState());
    enterRule(_localctx, 18, RULE_comparator);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(135);
        _la = _input.LA(1);
        if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << LT) | (1L << EQ))) != 0))) {
          _errHandler.recoverInline(this);
        } else {
          if (_input.LA(1) == Token.EOF) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }



  public static class EquationContext extends ParserRuleContext {

    public EquationContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public List<SumContext> sum() {
      return getRuleContexts(SumContext.class);
    }

    public SumContext sum(int i) {
      return getRuleContext(SumContext.class, i);
    }

    public ComparatorContext comparator() {
      return getRuleContext(ComparatorContext.class, 0);
    }

    public List<TerminalNode> SPACE() {
      return getTokens(jvmathArithmeticParser.SPACE);
    }

    public TerminalNode SPACE(int i) {
      return getToken(jvmathArithmeticParser.SPACE, i);
    }

    @Override
    public int getRuleIndex() {
      return RULE_equation;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitEquation(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }



  public static class SumContext extends ParserRuleContext {

    public SumContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public List<ProductContext> product() {
      return getRuleContexts(ProductContext.class);
    }

    public ProductContext product(int i) {
      return getRuleContext(ProductContext.class, i);
    }

    public List<TerminalNode> PLUS() {
      return getTokens(jvmathArithmeticParser.PLUS);
    }

    public TerminalNode PLUS(int i) {
      return getToken(jvmathArithmeticParser.PLUS, i);
    }

    public List<TerminalNode> MINUS() {
      return getTokens(jvmathArithmeticParser.MINUS);
    }

    public TerminalNode MINUS(int i) {
      return getToken(jvmathArithmeticParser.MINUS, i);
    }

    public List<TerminalNode> SPACE() {
      return getTokens(jvmathArithmeticParser.SPACE);
    }

    public TerminalNode SPACE(int i) {
      return getToken(jvmathArithmeticParser.SPACE, i);
    }

    @Override
    public int getRuleIndex() {
      return RULE_sum;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitSum(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }



  public static class ProductContext extends ParserRuleContext {

    public ProductContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public List<SignedFactorContext> signedFactor() {
      return getRuleContexts(SignedFactorContext.class);
    }

    public SignedFactorContext signedFactor(int i) {
      return getRuleContext(SignedFactorContext.class, i);
    }

    public List<FactorContext> factor() {
      return getRuleContexts(FactorContext.class);
    }

    public FactorContext factor(int i) {
      return getRuleContext(FactorContext.class, i);
    }

    public List<TerminalNode> SPACE() {
      return getTokens(jvmathArithmeticParser.SPACE);
    }

    public TerminalNode SPACE(int i) {
      return getToken(jvmathArithmeticParser.SPACE, i);
    }

    public List<TerminalNode> TIMES() {
      return getTokens(jvmathArithmeticParser.TIMES);
    }

    public TerminalNode TIMES(int i) {
      return getToken(jvmathArithmeticParser.TIMES, i);
    }

    public List<TerminalNode> DIV() {
      return getTokens(jvmathArithmeticParser.DIV);
    }

    public TerminalNode DIV(int i) {
      return getToken(jvmathArithmeticParser.DIV, i);
    }

    @Override
    public int getRuleIndex() {
      return RULE_product;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitProduct(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }



  public static class SignedFactorContext extends ParserRuleContext {

    public SignedFactorContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public List<SignedAtomContext> signedAtom() {
      return getRuleContexts(SignedAtomContext.class);
    }

    public SignedAtomContext signedAtom(int i) {
      return getRuleContext(SignedAtomContext.class, i);
    }

    public List<TerminalNode> POW() {
      return getTokens(jvmathArithmeticParser.POW);
    }

    public TerminalNode POW(int i) {
      return getToken(jvmathArithmeticParser.POW, i);
    }

    public List<TerminalNode> SPACE() {
      return getTokens(jvmathArithmeticParser.SPACE);
    }

    public TerminalNode SPACE(int i) {
      return getToken(jvmathArithmeticParser.SPACE, i);
    }

    @Override
    public int getRuleIndex() {
      return RULE_signedFactor;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitSignedFactor(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }



  public static class FactorContext extends ParserRuleContext {

    public FactorContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public AtomContext atom() {
      return getRuleContext(AtomContext.class, 0);
    }

    public List<TerminalNode> POW() {
      return getTokens(jvmathArithmeticParser.POW);
    }

    public TerminalNode POW(int i) {
      return getToken(jvmathArithmeticParser.POW, i);
    }

    public List<SignedAtomContext> signedAtom() {
      return getRuleContexts(SignedAtomContext.class);
    }

    public SignedAtomContext signedAtom(int i) {
      return getRuleContext(SignedAtomContext.class, i);
    }

    public List<TerminalNode> SPACE() {
      return getTokens(jvmathArithmeticParser.SPACE);
    }

    public TerminalNode SPACE(int i) {
      return getToken(jvmathArithmeticParser.SPACE, i);
    }

    @Override
    public int getRuleIndex() {
      return RULE_factor;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitFactor(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }



  public static class SignedAtomContext extends ParserRuleContext {

    public SignedAtomContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public TerminalNode MINUS() {
      return getToken(jvmathArithmeticParser.MINUS, 0);
    }

    public SignedAtomContext signedAtom() {
      return getRuleContext(SignedAtomContext.class, 0);
    }

    public TerminalNode SPACE() {
      return getToken(jvmathArithmeticParser.SPACE, 0);
    }

    public AtomContext atom() {
      return getRuleContext(AtomContext.class, 0);
    }

    @Override
    public int getRuleIndex() {
      return RULE_signedAtom;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitSignedAtom(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }



  public static class AtomContext extends ParserRuleContext {

    public AtomContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public LiteralContext literal() {
      return getRuleContext(LiteralContext.class, 0);
    }

    public VariableContext variable() {
      return getRuleContext(VariableContext.class, 0);
    }

    public TerminalNode LPAREN() {
      return getToken(jvmathArithmeticParser.LPAREN, 0);
    }

    public SumContext sum() {
      return getRuleContext(SumContext.class, 0);
    }

    public TerminalNode RPAREN() {
      return getToken(jvmathArithmeticParser.RPAREN, 0);
    }

    public List<TerminalNode> SPACE() {
      return getTokens(jvmathArithmeticParser.SPACE);
    }

    public TerminalNode SPACE(int i) {
      return getToken(jvmathArithmeticParser.SPACE, i);
    }

    @Override
    public int getRuleIndex() {
      return RULE_atom;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitAtom(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }



  public static class LiteralContext extends ParserRuleContext {

    public LiteralContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public TerminalNode SCIENTIFIC_NUMBER() {
      return getToken(jvmathArithmeticParser.SCIENTIFIC_NUMBER, 0);
    }

    @Override
    public int getRuleIndex() {
      return RULE_literal;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitLiteral(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }



  public static class VariableContext extends ParserRuleContext {

    public VariableContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public TerminalNode VARIABLE() {
      return getToken(jvmathArithmeticParser.VARIABLE, 0);
    }

    @Override
    public int getRuleIndex() {
      return RULE_variable;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitVariable(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }



  public static class ComparatorContext extends ParserRuleContext {

    public ComparatorContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    public TerminalNode EQ() {
      return getToken(jvmathArithmeticParser.EQ, 0);
    }

    public TerminalNode GT() {
      return getToken(jvmathArithmeticParser.GT, 0);
    }

    public TerminalNode LT() {
      return getToken(jvmathArithmeticParser.LT, 0);
    }

    @Override
    public int getRuleIndex() {
      return RULE_comparator;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof jvmathArithmeticVisitor) {
        return ((jvmathArithmeticVisitor<? extends T>) visitor).visitComparator(this);
      } else {
        return visitor.visitChildren(this);
      }
    }

  }
}
// Generated from C:/Users/Jacob Schramkowski/OneDrive/Projects/Solvable/JVMath/src/com/jschramk/JVMath/parse\ruleSet.g4 by ANTLR 4.9.1
package com.jschramk.JVMath.utilities.antlr_gen.rule_parse;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ruleSetParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, ACTION=3, ID=4, VARIABLE=5, NESTED_PARENS=6, NESTED_BRACKETS=7, 
		WS=8, COMMENT=9, LINE_COMMENT=10;
	public static final int
		RULE_parse = 0, RULE_r_rule = 1, RULE_r_definition = 2, RULE_r_filter = 3, 
		RULE_r_target_specifier = 4, RULE_r_action_content = 5, RULE_id = 6;
	private static String[] makeRuleNames() {
		return new String[] {
			"parse", "r_rule", "r_definition", "r_filter", "r_target_specifier", 
			"r_action_content", "id"
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

	@Override
	public String getGrammarFileName() { return "ruleSet.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ruleSetParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ParseContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(ruleSetParser.EOF, 0); }
		public List<R_ruleContext> r_rule() {
			return getRuleContexts(R_ruleContext.class);
		}
		public R_ruleContext r_rule(int i) {
			return getRuleContext(R_ruleContext.class,i);
		}
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ruleSetVisitor ) return ((ruleSetVisitor<? extends T>)visitor).visitParse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(15); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(14);
				r_rule();
				}
				}
				setState(17); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ACTION || _la==ID );
			setState(19);
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

	public static class R_ruleContext extends ParserRuleContext {
		public R_definitionContext r_definition() {
			return getRuleContext(R_definitionContext.class,0);
		}
		public R_filterContext r_filter() {
			return getRuleContext(R_filterContext.class,0);
		}
		public R_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_r_rule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ruleSetVisitor ) return ((ruleSetVisitor<? extends T>)visitor).visitR_rule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final R_ruleContext r_rule() throws RecognitionException {
		R_ruleContext _localctx = new R_ruleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_r_rule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21);
			r_definition();
			setState(23);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(22);
				r_filter();
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

	public static class R_definitionContext extends ParserRuleContext {
		public TerminalNode ACTION() { return getToken(ruleSetParser.ACTION, 0); }
		public TerminalNode NESTED_PARENS() { return getToken(ruleSetParser.NESTED_PARENS, 0); }
		public R_action_contentContext r_action_content() {
			return getRuleContext(R_action_contentContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public R_target_specifierContext r_target_specifier() {
			return getRuleContext(R_target_specifierContext.class,0);
		}
		public R_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_r_definition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ruleSetVisitor ) return ((ruleSetVisitor<? extends T>)visitor).visitR_definition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final R_definitionContext r_definition() throws RecognitionException {
		R_definitionContext _localctx = new R_definitionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_r_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(25);
				id();
				}
			}

			setState(28);
			match(ACTION);
			setState(29);
			match(NESTED_PARENS);
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(30);
				r_target_specifier();
				}
			}

			setState(33);
			r_action_content();
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

	public static class R_filterContext extends ParserRuleContext {
		public TerminalNode NESTED_BRACKETS() { return getToken(ruleSetParser.NESTED_BRACKETS, 0); }
		public R_filterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_r_filter; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ruleSetVisitor ) return ((ruleSetVisitor<? extends T>)visitor).visitR_filter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final R_filterContext r_filter() throws RecognitionException {
		R_filterContext _localctx = new R_filterContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_r_filter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			match(T__0);
			setState(36);
			match(NESTED_BRACKETS);
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

	public static class R_target_specifierContext extends ParserRuleContext {
		public TerminalNode NESTED_PARENS() { return getToken(ruleSetParser.NESTED_PARENS, 0); }
		public R_target_specifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_r_target_specifier; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ruleSetVisitor ) return ((ruleSetVisitor<? extends T>)visitor).visitR_target_specifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final R_target_specifierContext r_target_specifier() throws RecognitionException {
		R_target_specifierContext _localctx = new R_target_specifierContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_r_target_specifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(T__1);
			setState(39);
			match(NESTED_PARENS);
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

	public static class R_action_contentContext extends ParserRuleContext {
		public TerminalNode NESTED_BRACKETS() { return getToken(ruleSetParser.NESTED_BRACKETS, 0); }
		public R_action_contentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_r_action_content; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ruleSetVisitor ) return ((ruleSetVisitor<? extends T>)visitor).visitR_action_content(this);
			else return visitor.visitChildren(this);
		}
	}

	public final R_action_contentContext r_action_content() throws RecognitionException {
		R_action_contentContext _localctx = new R_action_contentContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_r_action_content);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(NESTED_BRACKETS);
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

	public static class IdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(ruleSetParser.ID, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ruleSetVisitor ) return ((ruleSetVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			match(ID);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\f\60\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\6\2\22\n\2\r\2\16\2\23"+
		"\3\2\3\2\3\3\3\3\5\3\32\n\3\3\4\5\4\35\n\4\3\4\3\4\3\4\5\4\"\n\4\3\4\3"+
		"\4\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\2\2\t\2\4\6\b\n\f\16\2"+
		"\2\2,\2\21\3\2\2\2\4\27\3\2\2\2\6\34\3\2\2\2\b%\3\2\2\2\n(\3\2\2\2\f+"+
		"\3\2\2\2\16-\3\2\2\2\20\22\5\4\3\2\21\20\3\2\2\2\22\23\3\2\2\2\23\21\3"+
		"\2\2\2\23\24\3\2\2\2\24\25\3\2\2\2\25\26\7\2\2\3\26\3\3\2\2\2\27\31\5"+
		"\6\4\2\30\32\5\b\5\2\31\30\3\2\2\2\31\32\3\2\2\2\32\5\3\2\2\2\33\35\5"+
		"\16\b\2\34\33\3\2\2\2\34\35\3\2\2\2\35\36\3\2\2\2\36\37\7\5\2\2\37!\7"+
		"\b\2\2 \"\5\n\6\2! \3\2\2\2!\"\3\2\2\2\"#\3\2\2\2#$\5\f\7\2$\7\3\2\2\2"+
		"%&\7\3\2\2&\'\7\t\2\2\'\t\3\2\2\2()\7\4\2\2)*\7\b\2\2*\13\3\2\2\2+,\7"+
		"\t\2\2,\r\3\2\2\2-.\7\6\2\2.\17\3\2\2\2\6\23\31\34!";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
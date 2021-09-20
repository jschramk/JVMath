package com.jschramk.JVMath.runtime.syntax;

import com.jschramk.JVMath.runtime.parse.ParserUtils;
import lexer.DFA;
import lexer.LabeledSegment;
import lexer.Lexer;

import java.util.List;
import java.util.Set;

public class SyntaxFinder {

  private final Lexer<Character, ParserUtils.Label> lexer;

  public SyntaxFinder(Set<String> functions) {
    DFA<Character, ParserUtils.Label> primitivesDFA = ParserUtils.getPrimitivesDFA(functions);
    lexer = new Lexer<>(primitivesDFA);
  }

  public List<LabeledSegment<Character, ParserUtils.Label>> findPrimitives(String input) {
    return lexer.findAll(LabeledSegment.characterListOf(input));
  }

}

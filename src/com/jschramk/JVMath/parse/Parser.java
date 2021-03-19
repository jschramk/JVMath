package com.jschramk.JVMath.parse;

import com.jschramk.JVMath.components.FunctionDomain;
import com.jschramk.JVMath.exceptions.ParserException;

public abstract class Parser {

  protected FunctionDomain functionDomain;

  public Parser(FunctionDomain functionDomain) {
    this.functionDomain = functionDomain;
  }

  public static Parser getDefault() {
    return DefaultParser.MAIN_INSTANCE;
  }

  public FunctionDomain getFunctionDomain() {
    return functionDomain;
  }

  public <T> T parse(String input, Class<T> type) throws ParserException {
    return parse(input).to(type);
  }

  public abstract ParseResult parse(String input) throws ParserException;

}

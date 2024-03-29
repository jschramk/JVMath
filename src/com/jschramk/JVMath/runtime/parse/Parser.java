package com.jschramk.JVMath.runtime.parse;

import com.jschramk.JVMath.runtime.components.FunctionDomain;
import com.jschramk.JVMath.runtime.exceptions.ParserException;

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

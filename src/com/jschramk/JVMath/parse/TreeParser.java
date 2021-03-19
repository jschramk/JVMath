package com.jschramk.JVMath.parse;

import com.jschramk.JVMath.exceptions.ParserException;

public abstract class TreeParser {

  public <T> T parse(String input, Class<T> type) throws ParserException {
    return parse(input).to(type);
  }

  public abstract ParseResult parse(String input) throws ParserException;

}

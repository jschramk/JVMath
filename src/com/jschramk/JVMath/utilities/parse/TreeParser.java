package com.jschramk.JVMath.utilities.parse;

import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.ParseResult;

public abstract class TreeParser {

  public <T> T parse(String input, Class<T> type) throws ParserException {
    return parse(input).to(type);
  }

  public abstract ParseResult parse(String input) throws ParserException;

}

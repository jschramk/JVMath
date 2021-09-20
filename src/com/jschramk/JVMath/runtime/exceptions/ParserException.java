package com.jschramk.JVMath.runtime.exceptions;

public class ParserException extends Exception {

  public ParserException() {
  }

  public ParserException(String message) {
    super(message);
  }

  public static ParserException unknownInput(String input) {

    if (input.isEmpty()) {
      return new ParserException("Unable to parse empty input");
    } else {
      return new ParserException("Input \"" + input + "\" is malformed");
    }

  }

}

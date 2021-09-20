package com.jschramk.JVMath.runtime.parse;

import com.jschramk.JVMath.runtime.utils.Utils;
import mathutils.MathUtils;

public class ParseResult {

  private final String input;
  private final Object result;
  private final long parseNanoTime;

  public ParseResult(String input, Object result, long parseTimeNanos) {

    verifyResult(result);

    if (parseTimeNanos <= 0) {
      throw new IllegalArgumentException("Parse time must be set to a positive value");
    }

    this.input = input;
    this.result = result;
    this.parseNanoTime = parseTimeNanos;

  }

  private void verifyResult(Object result) {
    if (result == null) {
      throw new IllegalArgumentException("Result cannot be null");
    }
  }

  public <T> T to(Class<T> type) {
    return type.cast(result);
  }

  public boolean is(Class<?> type) {
    return Utils.classExtends(result.getClass(), type);
  }

  public String getInput() {
    return input;
  }

  public String getResultString() {
    return result.toString();
  }

  @Override
  public String toString() {

    StringBuilder s = new StringBuilder();

    s.append("Input: \"");
    s.append(input);
    s.append("\", Result: \"");
    s.append(result);
    s.append('\"');
    s.append(", Type: ");
    s.append(result.getClass().getSimpleName());

    s.append(", Time: ");
    s.append(MathUtils.format(getParseTimeMillis()));
    s.append(" ms");


    return s.toString();
  }

  public double getParseTimeMillis() {
    return parseNanoTime / 1e6;
  }

}

package com.jschramk.JVMath.runtime.exceptions;

import com.jschramk.JVMath.runtime.components.Operand;

import java.util.Arrays;

public class OperatorException extends RuntimeException {

  public OperatorException(String symbol, Operand... operands) {
    super("Operator \"" + symbol + "\" cannot take operands: " + Arrays.toString(operands));
  }

}

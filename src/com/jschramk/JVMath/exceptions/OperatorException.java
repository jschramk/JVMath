package com.jschramk.JVMath.exceptions;

import com.jschramk.JVMath.components.Operand;

import java.util.Arrays;

public class OperatorException extends RuntimeException {

  public OperatorException(String symbol, Operand... operands) {
    super("Operator \"" + symbol + "\" cannot take operands: " + Arrays.toString(operands));
  }

}

package com.jschramk.JVMath.runtime.components;

public interface UnaryOperator {

  String getPrefix();

  String getPostfix();

  boolean canEvaluate(Operand o);

  Operand evaluate(Operand o);

  default Class<?> returnType(Operand o) {
    return Operand.class;
  }

  Enums.OperandType getType();

}

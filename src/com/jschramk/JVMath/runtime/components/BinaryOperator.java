package com.jschramk.JVMath.runtime.components;

public interface BinaryOperator {

  default String getSymbolLaTeX() {
    return getSymbol();
  }

  String getSymbol();

  boolean canEvaluate(Operand o1, Operand o2);

  Operand evaluate(Operand o1, Operand o2);

  Enums.OperandType getType();

  default Class<?> returnType(Operand o) {
    return Operand.class;
  }

  default boolean isCommutative() {
    return getAssociativity() == Associativity.COMMUTATIVE;
  }

  Associativity getAssociativity();

  enum Associativity {
    COMMUTATIVE, LEFT_TO_RIGHT, RIGHT_TO_LEFT
  }

}

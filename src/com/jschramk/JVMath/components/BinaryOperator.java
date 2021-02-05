package com.jschramk.JVMath.components;

public interface BinaryOperator {

  String getSymbol();

  default String getSymbolLaTeX() {
    return getSymbol();
  }

  boolean canEvaluate(Operand o1, Operand o2);

  Operand evaluate(Operand o1, Operand o2);

  Operand.Type getType();

  default Class<?> returnType(Operand o) {
    return Operand.class;
  }

  Associativity getAssociativity();

  default boolean isCommutative() {
    return getAssociativity() == Associativity.COMMUTATIVE;
  }

  enum Associativity {
    COMMUTATIVE, LEFT_TO_RIGHT, RIGHT_TO_LEFT
  }

}

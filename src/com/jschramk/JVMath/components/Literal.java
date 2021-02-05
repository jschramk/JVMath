package com.jschramk.JVMath.components;

import mathutils.MathUtils;

public class Literal extends Operand {

  protected final double value;

  public Literal(double value) {
    this.value = value;
  }

  @Override public int hashCode() {
    return Double.hashCode(value);
  }

  @Override public boolean equals(Object o) {
    if (!(o instanceof Operand)) {
      return false;
    }

    Operand operand = (Operand) o;

    if (operand.getType() != Type.LITERAL) {
      return false;
    }

    Literal literal = (Literal) operand;

    return literal.value == value;
  }

  public double getValue() {
    return value;
  }

  @Override public Type getType() {
    return Type.LITERAL;
  }

  @Override public Operand evaluate() {
    return this;
  }

  @Override protected Operand shallowCopy() {
    return new Literal(value);
  }

  @Override public String toString() {
    return MathUtils.format(value, JVMathSettings.DECIMAL_PLACES);
  }
}

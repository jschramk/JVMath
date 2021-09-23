package com.jschramk.JVMath.runtime.components;

import mathutils.MathUtils;

public class Literal extends Operand {

  protected final double value;

  public Literal(double value) {
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  @Override
  public Operand evaluate() {
    return this;
  }

  @Override
  protected Operand shallowCopy() {
    return new Literal(value);
  }

  @Override
  public String toString() {
    return MathUtils.format(value, JVMathSettings.DECIMAL_PLACES);
  }

  /*@Override
  public JsonObject toShallowJson() {

    JsonObject object = new JsonObject();

    object.addProperty("value", value);

    return object;
  }*/

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Operand)) {
      return false;
    }

    Operand operand = (Operand) o;

    if (operand.getType() != Enums.OperandType.LITERAL) {
      return false;
    }

    Literal literal = (Literal) operand;

    return literal.value == value;
  }

  @Override
  public int hashCode() {
    return Double.hashCode(value);
  }

  @Override
  public Enums.OperandType getType() {
    return Enums.OperandType.LITERAL;
  }


}

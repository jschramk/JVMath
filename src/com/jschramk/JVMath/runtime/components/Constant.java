package com.jschramk.JVMath.runtime.components;

public class Constant extends Literal {

  private String displayName;

  private Constant(String displayName, double value) {
    super(value);
    this.displayName = displayName;
  }

  public static Constant getInstance(String name) {

    if (name.equals("e")) {
      return e();
    } else if (name.equals("pi") || name.equals("π")) {
      return pi();
    }

    throw new IllegalArgumentException("Unknown constant name: \"" + name + "\"");

  }

  public static Constant e() {
    return new Constant("e", Math.E);
  }

  public static Constant pi() {
    return new Constant("π", Math.PI);
  }

  @Override
  protected Operand shallowCopy() {
    return new Constant(displayName, getValue());
  }

  @Override
  public String toString() {
    return displayName;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Operand)) {
      return false;
    }

    Operand operand = (Operand) o;

    if (operand.getType() != Enums.OperandType.CONSTANT) {
      return false;
    }

    Constant constant = (Constant) operand;

    return constant.value == value && constant.displayName.equals(displayName);
  }

  @Override
  public Enums.OperandType getType() {
    return Enums.OperandType.CONSTANT;
  }

  /*@Override public Operand evaluate() {
    return new Literal(getValue());
  }*/
}

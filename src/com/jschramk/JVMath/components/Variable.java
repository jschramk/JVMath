package com.jschramk.JVMath.components;

import java.util.Objects;

public class Variable extends Operand {

  private final String name;

  private VariableDomain variableDomain;

  public Variable(String name) {
    this.name = name;
  }

  @Override
  public Type getType() {
    return Type.VARIABLE;
  }

  public void setVariableDomain(VariableDomain variableDomain) {
    this.variableDomain = variableDomain;
  }

  @Override
  public double computeToDouble() throws UnsupportedOperationException {

    if (variableDomain == null) {
      throw new IllegalStateException("Variable domain not set for: " + toInfoString());
    }

    return variableDomain.get(name).computeToDouble();
  }

  @Override
  public Operand evaluate() {

    if (variableDomain == null) {
      throw new IllegalStateException("Variable domain not set for: " + toInfoString());
    }

    return variableDomain.get(name).evaluate();
  }

  @Override
  protected Operand shallowCopy() {
    return new Variable(name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Variable)) return false;
    Variable variable = (Variable) o;
    return Objects.equals(name, variable.name);
  }

  @Override
  public String toString() {
    return name;
  }

  public String getName() {
    return name;
  }

}

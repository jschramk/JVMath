package com.jschramk.JVMath.runtime.components;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Equation {

  private final Operand leftSide;
  private final Operand rightSide;

  public Equation(Operand leftSide, Operand rightSide) {
    this.leftSide = leftSide;
    this.rightSide = rightSide;
  }

  public void setVariableDomain(VariableDomain domain) {
    leftSide.setVariableDomain(domain);
    rightSide.setVariableDomain(domain);
  }

  public Equation fixedCopy() {
    return new Equation(getLeftSide().fixedCopy(), getRightSide().fixedCopy());
  }

  public Operand getLeftSide() {
    return leftSide;
  }

  public Operand getRightSide() {
    return rightSide;
  }

  public void fixTree() {
    leftSide.fixTree();
    rightSide.fixTree();
  }

  public int variableCount(String name) {
    return leftSide.variableInstanceCount(name) + rightSide.variableInstanceCount(name);
  }

  public boolean isSolvedFor(String variable) {

    if (!isSolved()) {
      return false;
    }

    Variable var = (Variable) leftSide;

    return var.getName().equals(variable);

  }

  public boolean isSolved() {

    if (leftSide instanceof Variable) {

      Variable variable = (Variable) leftSide;

      return !rightSide.getVariables().contains(variable.getName());

    } else {

      return false;

    }

  }

  public Equation reversed() {
    return new Equation(rightSide, leftSide);
  }

  public Equation copy() {
    return new Equation(leftSide.copy(), rightSide.copy());
  }

  public Set<String> getVariables() {
    Set<String> variables = new HashSet<>();
    variables.addAll(leftSide.getVariables());
    variables.addAll(rightSide.getVariables());
    return variables;
  }

  @Override
  public int hashCode() {
    return Objects.hash(leftSide, rightSide);
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) return true;
    if (!(o instanceof Equation)) return false;
    Equation equation = (Equation) o;
    return Objects.equals(leftSide, equation.leftSide) && Objects.equals(
      rightSide,
      equation.rightSide
    );
  }

  @Override
  public String toString() {
    return leftSide + " = " + rightSide;
  }

  public String toLaTex() {
    return leftSide.toLaTeX() + " = " + rightSide.toLaTeX();
  }

}

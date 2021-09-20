package com.jschramk.JVMath.components;

public class FunctionOperation extends Operand {

  private FunctionOperator operator;

  public FunctionOperation(FunctionOperator operator, Operand arg) {
    this(operator);
    operator.checkArg(arg);
    setChild(arg);
  }

  private FunctionOperation(FunctionOperator operator) {
    this.operator = operator;
  }

  public FunctionOperator getOperator() {
    return operator;
  }

  @Override
  public Enums.OperandType getType() {
    return Enums.OperandType.FUNCTION;
  }

  @Override
  public Operand evaluate() {
    return operator.evaluate(getChild(0));
  }

  @Override
  public boolean canEvaluate() {
    return operator.canEvaluate(getChild(0));
  }

  @Override
  protected Operand shallowCopy() {
    return new FunctionOperation(operator);
  }

  @Override
  public int hashCode() {
    return childrenHashCode();
  }

  @Override
  public boolean equals(Object o) {

    if (o instanceof FunctionOperation) {

      FunctionOperation operation = (FunctionOperation) o;

      return operation.operator == operator && childrenEquals(operation);

    }

    return false;

  }

  @Override
  public String toString() {
    return operator.getName() + (getChild(0).isScalar() ? "(" + getChild(0) + ")" : getChild(0));
  }

  /*@Override
  public JsonObject toShallowJson() {

    JsonObject object = new JsonObject();

    object.addProperty("function", operator.getName());

    return object;
  }*/

}

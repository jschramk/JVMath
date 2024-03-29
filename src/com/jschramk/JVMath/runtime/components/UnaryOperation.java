package com.jschramk.JVMath.runtime.components;

import java.util.ArrayList;
import java.util.List;

public class UnaryOperation extends Operand {

private UnaryOperator operator;

public UnaryOperation(UnaryOperator operator, Operand arg) {
    this(operator);
    setChild(arg);
}

private UnaryOperation(UnaryOperator operator) {
    this.operator = operator;
}

public static UnaryOperation negation(Operand o) {
    return new UnaryOperation(Operators.NEGATION, o);
}

public UnaryOperator getOperator() {
    return operator;
}

@Override
public String toLaTeX() {
    return operator.getPrefix() + childPriorityString(this, getChild(0), true) + operator.getPostfix();
}

@Override
public Enums.OperandType getType() {
    return operator.getType();
}


@Override
public Operand evaluate() {

    Operand child = getChild(0);

    if (child.isScalar()) {
        return operator.evaluate(child);
    }

    Matrix m = (Matrix) child;

    List<Operand> children = new ArrayList<>();

    for (Operand element : m) {
        children.add(operator.evaluate(element));
    }

    return new Matrix(children, m.rowCount());

}

@Override
protected Operand shallowCopy() {
    return new UnaryOperation(operator);
}

@Override
public int hashCode() {
    return childrenHashCode();
}

@Override
public boolean equals(Object o) {
    if (o instanceof UnaryOperation) {
        UnaryOperation operation = (UnaryOperation) o;
        return operation.operator == operator && childrenEquals(operation);
    }
    return false;
}

@Override
public String toString() {
    return operator.getPrefix() + childPriorityString(this, getChild(0)) + operator.getPostfix();
}

  /*@Override
  public JsonObject toShallowJson() {

    JsonObject object = new JsonObject();

    object.addProperty("prefix", operator.getPrefix());

    return null;
  }*/

}

package com.jschramk.JVMath.runtime.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinaryOperation extends Operand {

    private BinaryOperator operator;

    public BinaryOperation(BinaryOperator operator, List<Operand> operands) {
        this(operator, operands.toArray(new Operand[0]));
    }

    public BinaryOperation(BinaryOperator operator, Operand... operands) {

        this(operator);

        if (operands.length < 2) {
            throw new IllegalArgumentException("BinaryOperation must be given at least 2 operands");
        }

        setChildren(operands);

    }

    private BinaryOperation(BinaryOperator operator) {
        this.operator = operator;
    }

    public static Operand product(Operand... operands) {
        return new BinaryOperation(Operators.MULTIPLICATION, operands);
    }

    public static Operand sum(Operand... operands) {
        return new BinaryOperation(Operators.ADDITION, operands);
    }

    public static Operand division(Operand numerator, Operand denominator) {
        return new BinaryOperation(Operators.DIVISION, numerator, denominator);
    }

    public static Operand exponent(Operand base, Operand power) {
        return new BinaryOperation(Operators.EXPONENT, base, power);
    }

    @Override public String toLaTeX() {

    /*if (getOperator() == Operators.DIVISION) {

        return "\\frac{" + getChild(0).toLaTeX() + "}{" + getChild(1).toLaTeX() + "}";

    } else {*/

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < childCount(); i++) {

            if (i > 0) {
                s.append(operator.getSymbolLaTeX());
            }

            s.append(childPriorityString(this, getChild(i), true));

        }

        return s.toString();
        //}

    }

    public BinaryOperator getOperator() {
        return operator;
    }

    @Override public Enums.OperandType getType() {
        return operator.getType();
    }


    @Override public Operand evaluate() {

        if (operator.getAssociativity()
            != BinaryOperator.Associativity.LEFT_TO_RIGHT) { // eval left to right

            int size = childCount();

            Operand curr =
                operator.evaluate(getChild(size - 2).evaluate(), getChild(size - 1).evaluate());

            for (int i = size - 3; i >= 0; i--) {
                curr = operator.evaluate(getChild(i).evaluate(), curr.evaluate());
            }

            return curr;

        } else { // eval right to left

            Operand curr = operator.evaluate(getChild(0).evaluate(), getChild(1).evaluate());

            for (int i = 2; i < childCount(); i++) {
                curr = operator.evaluate(curr.evaluate(), getChild(i).evaluate());
            }

            return curr;

        }

    }

    @Override protected Operand shallowCopy() {
        return new BinaryOperation(operator);
    }

    //TODO: test
    @Override public int hashCode() {

        if (operator.isCommutative()) {
            return childCountMap(this).hashCode();
        }

        return childrenHashCode();

    }

    //TODO: test
    @Override public boolean equals(Object o) {

        if (o instanceof BinaryOperation) {

            BinaryOperation operation = (BinaryOperation) o;

            if (operator != operation.operator) {
                return false;
            }

            if (operator.isCommutative()) {
                return childCountMap(operation).equals(childCountMap(this));
            } else {
                return childrenEquals(operation);
            }

        }

        return false;

    }

    private static Map<Operand, Integer> childCountMap(Operand operand) {
        Map<Operand, Integer> countMap = new HashMap<>();
        for (Operand child : operand) {
            if (!countMap.containsKey(child)) {
                countMap.put(child, 0);
            }
            countMap.put(child, countMap.get(child) + 1);
        }
        return countMap;
    }

    @Override public String toString() {

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < childCount(); i++) {

            Operand child = getChild(i);

            if (i > 0) {

                if (operator == Operators.ADDITION
                    && child.getType() == Enums.OperandType.NEGATION) {
                    s.append(" ");
                } else {
                    s.append(operator.getSymbol());
                }

            }
            s.append(childPriorityString(this, child));
        }

        return s.toString();
    }

  /*@Override
  public JsonObject toShallowJson() {
    return new JsonObject();
  }*/

}

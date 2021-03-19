package com.jschramk.JVMath.components;

import java.util.List;

public interface FunctionOperator {

  List<String> getDescriptions();

  void checkArg(Operand arg);

  boolean canEvaluate(Operand arg);

  Operand evaluate(Operand arg);

  default Class<?> returnType(Operand arg) {
    return Operand.class;
  }

  default void allowVectorLengths(Operand arg, int... acceptedLengths) {

    for (int i : acceptedLengths) {

      if (arg.isScalar()) {

        if (i == 1) {
          return;
        }

      } else {

        Matrix m = (Matrix) arg;

        if (m.rowCount() == 1 && m.colCount() == i) return;

      }

    }

    throw new IllegalArgumentException("Function \"" + getName() + "\" cannot take argument(s): " + arg);

  }

  String getName();

}

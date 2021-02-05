package com.jschramk.JVMath.components;


import modules.Functions;

import java.util.Arrays;
import java.util.List;

public class Operators {

  // unary

  public static final UnaryOperator NEGATION = new UnaryOperator() {
    @Override public String getPrefix() {
      return "-";
    }

    @Override public String getPostfix() {
      return "";
    }

    @Override public boolean canEvaluate(Operand o) {
      return o.hasNoVariables();
    }

    @Override public Operand evaluate(Operand o) {

      return new Literal(-o.computeToDouble());

    }

    @Override public Operand.Type getType() {
      return Operand.Type.NEGATION;
    }
  };

  public static final UnaryOperator FACTORIAL = new UnaryOperator() {
    @Override public String getPrefix() {
      return "";
    }

    @Override public String getPostfix() {
      return "!";
    }

    @Override public boolean canEvaluate(Operand o) {
      return o.hasNoVariables();
    }

    @Override public Operand evaluate(Operand o) {

      double d = o.computeToDouble();

      return new Literal(Functions.factorial(d));

    }

    @Override public Operand.Type getType() {
      return Operand.Type.FACTORIAL;
    }

  };

  // binary

  public static final BinaryOperator ADDITION = new BinaryOperator() {
    @Override public String getSymbol() {
      return " + ";
    }

    @Override public boolean canEvaluate(Operand o1, Operand o2) {
      return o1.hasNoVariables() && o2.hasNoVariables();
    }

    @Override public Operand evaluate(Operand o1, Operand o2) {

      return new Literal(o1.computeToDouble() + o2.computeToDouble());

    }

    @Override public Operand.Type getType() {
      return Operand.Type.SUM;
    }

    @Override public Associativity getAssociativity() {
      return Associativity.COMMUTATIVE;
    }
  };

  public static final BinaryOperator MULTIPLICATION = new BinaryOperator() {
    @Override public String getSymbol() {
      return "⋅";
    }

    @Override public String getSymbolLaTeX() {
      return " \\cdot ";
    }

    @Override public boolean canEvaluate(Operand o1, Operand o2) {
      return o1.hasNoVariables() && o2.hasNoVariables();
    }

    @Override public Operand evaluate(Operand o1, Operand o2) {

      return new Literal(o1.computeToDouble() * o2.computeToDouble());
    }

    @Override public Operand.Type getType() {
      return Operand.Type.PRODUCT;
    }

    @Override public Associativity getAssociativity() {
      return Associativity.COMMUTATIVE;
    }
  };

  public static final BinaryOperator DIVISION = new BinaryOperator() {
    @Override public String getSymbol() {
      return "/";
    }

    @Override public boolean canEvaluate(Operand o1, Operand o2) {
      return o1.hasNoVariables() && o2.hasNoVariables();
    }

    @Override public Operand evaluate(Operand o1, Operand o2) {

      return new Literal(o1.computeToDouble() / o2.computeToDouble());

    }

    @Override public Operand.Type getType() {
      return Operand.Type.DIVISION;
    }

    @Override public Associativity getAssociativity() {
      return Associativity.LEFT_TO_RIGHT;
    }
  };

  public static final BinaryOperator EXPONENT = new BinaryOperator() {
    @Override public String getSymbol() {
      return "^";
    }

    @Override public boolean canEvaluate(Operand o1, Operand o2) {
      return o1.hasNoVariables() && o2.hasNoVariables();
    }

    @Override public Operand evaluate(Operand o1, Operand o2) {

      return new Literal(Math.pow(o1.computeToDouble(), o2.computeToDouble()));

    }

    @Override public Operand.Type getType() {
      return Operand.Type.EXPONENT;
    }

    @Override public Associativity getAssociativity() {
      return Associativity.RIGHT_TO_LEFT;
    }
  };



  // functions

  public static final FunctionOperator SINE = new FunctionOperator() {
    @Override public String getName() {
      return "sin";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${sin(x)}$: returns the sine of ${x}$");
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1);
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public Operand evaluate(Operand arg) {

      double input = arg.computeToDouble();

      if (JVMathSettings.ANGULAR_UNIT == JVMathSettings.AngularUnit.DEGREES) {
        input = Math.toRadians(input);
      }

      return new Literal(Math.sin(input));
    }

  };

  public static final FunctionOperator INVERSE_SINE = new FunctionOperator() {
    @Override public String getName() {
      return "asin";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${asin(x)}$: returns the inverse sine of ${x}$");
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1);
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public Operand evaluate(Operand arg) {

      double val = Math.asin(arg.computeToDouble());

      if (JVMathSettings.ANGULAR_UNIT == JVMathSettings.AngularUnit.DEGREES) {
        val = Math.toDegrees(val);
      }

      return new Literal(val);

    }

  };

  public static final FunctionOperator COSINE = new FunctionOperator() {
    @Override public String getName() {
      return "cos";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${cos(x)}$: returns the cosine of ${x}$");
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1);
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public Operand evaluate(Operand arg) {
      double input = arg.computeToDouble();

      if (JVMathSettings.ANGULAR_UNIT == JVMathSettings.AngularUnit.DEGREES) {
        input = Math.toRadians(input);
      }

      return new Literal(Math.cos(input));
    }

  };

  public static final FunctionOperator INVERSE_COSINE = new FunctionOperator() {
    @Override public String getName() {
      return "acos";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${acos(x)}$: returns the inverse cosine of ${x}$");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1);
    }

    @Override public Operand evaluate(Operand arg) {
      double val = Math.acos(arg.computeToDouble());

      if (JVMathSettings.ANGULAR_UNIT == JVMathSettings.AngularUnit.DEGREES) {
        val = Math.toDegrees(val);
      }

      return new Literal(val);
    }

  };

  public static final FunctionOperator TANGENT = new FunctionOperator() {
    @Override public String getName() {
      return "tan";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${tan(x)}$: returns the tangent of ${x}$");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1);
    }

    @Override public Operand evaluate(Operand arg) {

      double input = arg.computeToDouble();

      if (JVMathSettings.ANGULAR_UNIT == JVMathSettings.AngularUnit.DEGREES) {
        input = Math.toRadians(input);
      }

      return new Literal(Math.tan(input));
    }

  };

  public static final FunctionOperator INVERSE_TANGENT = new FunctionOperator() {
    @Override public String getName() {
      return "atan";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${atan(x)}$: returns the inverse tangent of ${x}$");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1, 2);
    }

    @Override public Operand evaluate(Operand arg) {

      double val;

      if (arg.isScalar()) {
        val = Math.atan(arg.computeToDouble());
      } else {
        val = Math.atan2(arg.getChild(0).computeToDouble(), arg.getChild(1).computeToDouble());
      }

      if (JVMathSettings.ANGULAR_UNIT == JVMathSettings.AngularUnit.DEGREES) {
        val = Math.toDegrees(val);
      }

      return new Literal(val);

    }

  };

  public static final FunctionOperator TO_DEGREES = new FunctionOperator() {
    @Override public String getName() {
      return "deg";
    }

    @Override public List<String> getDescriptions() {
      return Arrays
          .asList("${deg(x)}$: returns the value in degrees of ${x}$ where ${x}$ is in radians");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1);
    }

    @Override public Operand evaluate(Operand arg) {
      return new Literal(Math.toDegrees(arg.computeToDouble()));
    }

  };

  public static final FunctionOperator TO_RADIANS = new FunctionOperator() {
    @Override public String getName() {
      return "rad";
    }

    @Override public List<String> getDescriptions() {
      return Arrays
          .asList("${rad(x)}$: returns the value in radians of ${x}$ where ${x}$ is in degrees");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1);
    }

    @Override public Operand evaluate(Operand arg) {
      return new Literal(Math.toRadians(arg.computeToDouble()));
    }

  };

  public static final FunctionOperator LOG = new FunctionOperator() {
    @Override public String getName() {
      return "log";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${log(x)}$: returns log base ${e}$ of ${x}$ or natural log of ${x}$",
          "${log(b, x)}$: returns log base ${b}$ of ${x}$");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1, 2);
    }

    @Override public Operand evaluate(Operand arg) {

      if (arg.isScalar()) {
        return new Literal(Math.log(arg.computeToDouble()));
      } else {
        return new Literal(Math.log(arg.getChild(1).computeToDouble()) / Math
            .log(arg.getChild(0).computeToDouble()));
      }

    }

  };

  public static final FunctionOperator ROOT = new FunctionOperator() {
    @Override public String getName() {
      return "root";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${root(x)}$: returns the square root of ${x}$",
          "${root(n, x)}$: returns the ${n}$th root of ${x}$");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1, 2);
    }

    @Override public Operand evaluate(Operand arg) {
      if (arg.isScalar()) {
        return new Literal(BinaryOperation.exponent(arg, new Literal(1 / 2f)).computeToDouble());
      } else {
        return new Literal(BinaryOperation
            .exponent(arg.getChild(1), BinaryOperation.division(new Literal(1), arg.getChild(0)))
            .computeToDouble());
      }
    }

  };


  public static final FunctionOperator DERIVE = new FunctionOperator() {
    @Override public String getName() {
      return "derive";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList(
          "${derive(3x + 5, x)}$: returns the derivative of ${3x + 5}$ with respect to ${x}$");
    }

    @Override public void checkArg(Operand arg) {

      allowVectorLengths(arg, 2);

      if (!(arg.getChild(1) instanceof Variable)) {
        throw new IllegalArgumentException("Second argument of derive should be a variable");
      }

    }

    @Override public boolean canEvaluate(Operand arg) {
      return false;
    }

    @Override public Operand evaluate(Operand arg) {
      throw new UnsupportedOperationException("Cannot evaluate derivatives");
    }
  };

  public static final FunctionOperator STEP = new FunctionOperator() {
    @Override public String getName() {
      return "step";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${step(x)}$: returns ${1}$ if ${x}$ ≥ ${0}$, else returns ${0}$");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1);
    }

    @Override public Operand evaluate(Operand arg) {
      double in = arg.computeToDouble();

      return in >= 0 ? new Literal(1) : new Literal(0);
    }

  };



  public static final FunctionOperator RAMP = new FunctionOperator() {
    @Override public String getName() {
      return "ramp";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList("${ramp(x)}$: returns ${x}$ if ${x}$ ≥ ${0}$, else returns ${0}$");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 1);
    }

    @Override public Operand evaluate(Operand arg) {
      double in = arg.computeToDouble();

      return in >= 0 ? new Literal(in) : new Literal(0);
    }

  };

  public static final FunctionOperator NCR = new FunctionOperator() {
    @Override public String getName() {
      return "nCr";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList(
          "${nCr(n, r)}$: returns the number of ways to combine ${r}$ items from a set of ${n}$ items where "
              + "${n}$ and ${r}$ are integers");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 2);
    }

    @Override public Operand evaluate(Operand arg) {

      return new Literal(
          Functions.nCr(arg.getChild(0).computeToDouble(), arg.getChild(1).computeToDouble()));

    }

  };

  public static final FunctionOperator NPR = new FunctionOperator() {
    @Override public String getName() {
      return "nPr";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList(
          "${nPr(n, r)}$: returns the number of ways to permute ${r}$ items from a set of ${n}$ "
              + "items where ${n}$ and ${r}$ are integers");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg.hasNoVariables();
    }

    @Override public void checkArg(Operand arg) {
      allowVectorLengths(arg, 2);
    }

    @Override public Operand evaluate(Operand arg) {

      return new Literal(
          Functions.nPr(arg.getChild(0).computeToDouble(), arg.getChild(1).computeToDouble()));

    }

  };

  public static final FunctionOperator TRANSPOSE = new FunctionOperator() {
    @Override public String getName() {
      return "tran";
    }

    @Override public List<String> getDescriptions() {
      return Arrays.asList(
          "${tran(A)}$: returns the matrix ${A}$ reflected about its main diagonal, or the transpose of ${A}$");
    }

    @Override public boolean canEvaluate(Operand arg) {
      return arg instanceof Matrix;
    }

    @Override public Class<?> returnType(Operand arg) {
      return Matrix.class;
    }

    @Override public void checkArg(Operand arg) {
      /*if(arg.isScalar() && arg.hasNoVariables()) {
        throw new IllegalArgumentException("Cannot transpose a scalar value");
      }*/
    }

    @Override public Operand evaluate(Operand arg) {

      if (!(arg instanceof Matrix)) {
        arg = arg.evaluate();
      }

      if (arg instanceof Matrix) {
        Matrix m = (Matrix) arg;
        return m.transpose();
      }

      throw new IllegalArgumentException("Cannot transpose a scalar value: " + arg);

    }

  };


}

package com.jschramk.JVMath.utilities.test;

import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.Parser;
import com.jschramk.JVMath.runtime.rewrite_engine.RewriteEngine;

public class TestRuleSpeed {

  public static void main(String[] args) throws ParserException {

    Parser p = Parser.getDefault();

    testSimplify(p, "a/(b/(c/d))");
    testSimplify(p, "a x + b x + c x + d x");
    testSimplify(p, "x x x x x x x x x x x x");

    testSolve(p, "y = m x + b");
    testSolve(p, "y = a x^2 + b x + c");

  }


  private static void testSimplify(Parser p, String operand) throws ParserException {

    Operand in = p.parse(operand).to(Operand.class);

    long t1, t2, dt;

    t1 = System.currentTimeMillis();

    Operand result = RewriteEngine.simplify(in, null, true).getResult();

    t2 = System.currentTimeMillis();

    dt = t2 - t1;

    System.out.println(String.format(
      "Input: %s, Simplified: %s, Time: %d ms",
      in.toString(),
      result.toString(),
      dt
    ));

  }

  private static void testSolve(Parser p, String equation) throws ParserException {

    Equation in = p.parse(equation).to(Equation.class);

    long t1, t2, dt;

    t1 = System.currentTimeMillis();

    Equation result = RewriteEngine.solve(in, "x", true).getResult();

    t2 = System.currentTimeMillis();

    dt = t2 - t1;

    System.out.println(String.format(
      "Input: %s, Solved: %s, Time: %d ms",
      in.toString(),
      result.toString(),
      dt
    ));

  }

}

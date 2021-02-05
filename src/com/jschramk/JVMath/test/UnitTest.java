package com.jschramk.JVMath.test;

import com.jschramk.JVMath.components.Equation;
import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.parse.Parser;
import com.jschramk.JVMath.rewrite_engine.RewriteEngine;
import util.PerformanceTimer;

public class UnitTest {

  private static Parser parser = Parser.getDefault();
  private static PerformanceTimer timer = new PerformanceTimer();

  public static void main(String[] args) throws ParserException {

    testSolve(eq("y = x"));
    testSolve(eq("0 = (x - y - z)/4 + (y - 6)/3 + (x + y)/2"));
    testSolve(eq("y = m x + b"));
    testSolve(eq("gain = 10log(10, Pout/Pin)"));
    testSolve(eq("3/n^2 = (n - 4)/(3n^2) + 2/(3n^2)"));
    testSolve(eq("3/n^2 = (n - 4)/n^2 + 2/(3n^2)"));
    testSolve(eq("3/n^2 = (n - 4)/(3n^2) + 2/n^2"));
    testSolve(eq("10root(9x) = 6"));
    testSolve(eq("-6log(3, x - 3) = -24"));
    testSolve(eq("x^2 - 4x - 5 = 0"));
    testSolve(eq("x = x0 + v0 t + 1/2a t^2"));
    testSolve(eq("y = a x^2 + b x + c"));
    testSolve(eq("y = a x^2 + x + c"));
    testSolve(eq("y = x^2 + b x + c"));
    testSolve(eq("y = x^2 + x + c"));
    testSolve(eq("y = x^2 + x"));
    testSolve(eq("0 = 4x^2 - 7x + 2"));
    testSolve(eq("1/p + 1/q = 1/f"));
    testSolve(eq("v^2 = v0^2 + 2 a x"));
    testSolve(eq("n^2 = 1/n"));
    testSolve(eq("(5x-1)/5 - (1 + x)/2 = 3 - (x - 1)/4"));
    testSolve(eq("Rtotal = R1 R2/(R1 + R2)"));
    testSolve(eq("P1 V1 = P2 V2"));
    testSolve(eq("y = a^2 + 2 a b + b^2"));
    testSolve(eq("Rtot = 1/(1/R1 + 1/R2 + 1/R3 + 1/R4)"));
    testSolve(eq("F = G M m/r^2"));

  }

  private static Operand op(String input) throws ParserException {
    return parser.parse(input, Operand.class);
  }

  private static Equation eq(String input) throws ParserException {
    return parser.parse(input, Equation.class);
  }

  private static void testSolve(Equation equation, String variable) {

    System.out.println("\tSolve for " + variable + ": " + equation);

    timer.start();

    RewriteEngine.Output<Equation> solved = RewriteEngine.solve(equation, variable, true);

    timer.stop();



    System.out.println("\t\t" + solved.getResult());

    if (timer.ms() > 100) {

      //System.out.println("------------------------------------------");
      System.out.println("WARNING: Solve time: " + timer.ms() + " ms");
      //System.out.println("------------------------------------------");

    }

  }

  private static void testSolve(Equation equation) {

    for (String s : equation.getVariables()) {

      if (!equation.isSolvedFor(s)) {

        testSolve(equation, s);

      }

    }

    System.out.println();

  }


}

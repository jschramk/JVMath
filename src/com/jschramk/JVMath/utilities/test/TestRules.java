package com.jschramk.JVMath.utilities.test;

import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.exceptions.UnsolvableException;
import com.jschramk.JVMath.runtime.parse.Parser;
import com.jschramk.JVMath.runtime.math_engine.MathEngine;

import java.util.Scanner;

public class TestRules {

  public static void main(String[] args) {

    Scanner s = new Scanner(System.in);

    while (true) {

      System.out.println("Choose a mode: ");
      System.out.println("1: Simplify");
      System.out.println("2: Solve");

      System.out.print("\nMode: ");

      String response = s.nextLine().trim();

      if (response.equals("1")) {
        testSimplify();
        break;
      } else if (response.equals("2")) {
        testSolve();
        break;
      } else {
        System.out.println("Please choose a mode from the available options\n");
      }

    }
  }

  private static void testSimplify() {

    Scanner s = new Scanner(System.in);
    Parser p = Parser.getDefault();

    while (true) {

      System.out.print("Type an expression to simplify: ");

      String line = s.nextLine();

      try {

        Operand operand = p.parse(line, Operand.class);

        MathEngine.Output<Operand> out = MathEngine.simplify(operand, null, true);

        System.out.println("Simplify " + operand + "\n");

        if (out != null) {
          out.printSteps();
        } else {
          System.out.println("Unable to simplify further");
        }

      } catch (ParserException e) {
        System.out.println(e.getMessage());
      }

      System.out.println("\n");

    }

  }

  private static void testSolve() {

    Scanner s = new Scanner(System.in);
    Parser p = Parser.getDefault();

    while (true) {

      System.out.print("Type an equation to solve for x: ");

      String line = s.nextLine();

      try {

        Equation equation = p.parse(line, Equation.class);

        try {

          MathEngine.Output<Equation> out = MathEngine.solve(equation, "x", true);

          System.out.println("Solve " + equation + " for x\n");
          out.printSteps();

        } catch (UnsolvableException e) {
          System.out.println(e.getMessage());
        }

      } catch (ParserException e) {
        System.out.println(e.getMessage());
      }

      System.out.println("\n");

    }

  }

}

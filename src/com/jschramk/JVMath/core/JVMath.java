package com.jschramk.JVMath.core;

import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.math_engine.MathEngine;
import com.jschramk.JVMath.runtime.parse.Parser;

public class JVMath {

  public static void main(String[] args) throws ParserException {

    MathEngine.Output<Equation> output = solve("y = m x + b", "b");

    output.printSteps();

  }

  private static Parser parser = Parser.getDefault();

  public static MathEngine.Output<Equation> solve(String equation, String variable)
      throws ParserException {

    Equation input = parser.parse(equation, Equation.class);

    return MathEngine.solve(input, variable, true);

  }

}

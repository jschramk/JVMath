package com.jschramk.JVMath.script;

import com.jschramk.JVMath.components.Equation;
import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.components.Variable;
import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.exceptions.UnsolvableException;
import com.jschramk.JVMath.parse.Parser;
import com.jschramk.JVMath.rewrite_engine.RewriteEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solver {

  private List<Operand> find = new ArrayList<>();
  private List<Equation> given = new ArrayList<>();
  private Parser parser;
  private Map<String, Operand> found = new HashMap<>();

  public void setParser(Parser parser) {
    this.parser = parser;
  }

  public Solver find(String s) throws ParserException {

    if (parser == null) {
      throw new IllegalStateException("Parser not set");
    }

    find(parser.parse(s, Operand.class));

    return this;

  }

  public Solver find(Operand operand) {
    find.add(operand);
    return this;
  }

  public Solver given(String s) throws ParserException {

    if (parser == null) {
      throw new IllegalStateException("Parser not set");
    }

    given(parser.parse(s, Equation.class));

    return this;

  }

  public Solver given(Equation equation) {
    given.add(equation);
    return this;
  }


  /*
  public List<Equation> compute() {

    Set<String> neededVars = new HashSet<>();

    for (Operand o : find) {
      neededVars.addAll(o.getVariables());
    }

    for (String s : neededVars) {

      get(s);

    }

    List<Equation> ret = new ArrayList<>();

    for (Operand op : find) {

      Operand replaced = op.copy().replaceMap(found);

      RewriteEngine.Output<Operand> out = RewriteEngine.simplifySteps(replaced);

      if (out != null) {
        replaced = out.getResult();
      }

      ret.add(new Equation(op, replaced));

    }

    return ret;

  }*/

  private void get(String s) {

    Equation toUse = null;

    for (Equation equation : given) {

      if (equation.getVariables().contains(s)) {
        toUse = equation;
      }

    }

    if (toUse == null) return;

    System.out.println("using " + toUse + " to find " + s);

    if (toUse.isSolvedFor(s)) {

      given.remove(toUse);

      Operand right = substitute(toUse.getRightSide());

      found.put(s, right);

      given.add(new Equation(new Variable(s), right));

    } else {

      try {

        toUse.fixTree();

        Equation solve = RewriteEngine.solve(toUse, s, true).getResult();

        given.remove(toUse);

        Operand right = substitute(solve.getRightSide());

        found.put(s, right);

        given.add(new Equation(new Variable(s), right));

      } catch (UnsolvableException e) {
        System.out.println(e.getMessage());
      }

    }

    if (found.get(s) == null) {
      throw new RuntimeException("unable to find: " + s);
    }

  }

  private Operand substitute(Operand operand) {

    for (String s : operand.getVariables()) {

      get(s);

    }

    return operand.replaceMap(found);

  }


/*
  public List<Equation> compute() {

    Set<String> neededVars = new HashSet<>();

    for (Operand o : find) {
      neededVars.addAll(o.getVariables());
    }

    System.out.println(neededVars);



    for (String var : neededVars) {

      Equation equation = bestEquationFor(var);

      System.out.println("best for " + var + ": " + equation);

      Equation solved = RewriteEngine.solveSteps(equation, var).getResult();

      System.out.println("solved: " + solved);

      given.remove(equation);

      Equation substituted =
          new Equation(
              equation.getLeftSide().copy().replace(var, solved.getRightSide()),
              equation.getRightSide().copy().replace(var, solved.getRightSide())
          );

      substituted.fixTree();


      given.add(substituted);

    }



    return null;

  }*/

  /*
  private Equation bestEquationFor(String var) {

    int min = Integer.MAX_VALUE;

    Equation best = null;

    for (Equation equation : given) {

      Set<String> vars = equation.getVariables();

      if (vars.contains(var) && vars.size() < min) {
        best = equation;
      }

    }

    if (best == null) {
      throw new RuntimeException("No equation with variable: " + var);
    }

    return best;

  }
   */
}

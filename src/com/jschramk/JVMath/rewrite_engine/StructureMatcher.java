package com.jschramk.JVMath.rewrite_engine;

import com.jschramk.JVMath.components.*;
import util.PerformanceTimer;

import java.util.Map;

public class StructureMatcher {

  private static PerformanceTimer timer = new PerformanceTimer();

  private static boolean matches(Operand rule, Operand check, Map<String, Requirement> requirements,
      OperandMapper mapper, String solveVariable) {

    boolean matches = false; // default to false

    if (rule.treeDepth() > check.treeDepth()) {

      // skip to end

    } else if (rule instanceof Variable) { // rule operand is variable, check requirements

      Variable var = (Variable) rule;

      matches = Requirement.meetsPrerequisites(var.getName(), solveVariable, check, requirements);

    } else if (rule instanceof Literal) { // rule operand is literal, check if actual is the same literal

      if (check instanceof Literal) {

        Literal ruleLiteral = (Literal) rule;
        Literal actualLiteral = (Literal) check;

        matches = ruleLiteral.getValue() == actualLiteral.getValue();

      }

    } else if (Operand.sameType(rule, check)) { // rule and actual operands are the  same type

      if (check instanceof BinaryOperation
          && ((BinaryOperation) check).getOperator().getAssociativity()
          == BinaryOperator.Associativity.COMMUTATIVE) {

        // add all possible matches for mapper to solve
        for (Operand matchChild : rule) {

          for (Operand operandChild : check) {

            if (matches(matchChild, operandChild, requirements, mapper, solveVariable)) {
              matches = true;
            }

          }

        }

      } else { // the operand is not a commutative operation, check operands in order

        if (check.childCount() == rule.childCount()) {

          matches = true; // default to true and check for mismatch

          for (int i = 0; i < check.childCount(); i++) {

            Operand ruleChild = rule.getChild(i);
            Operand actualChild = check.getChild(i);

            if (!matches(ruleChild, actualChild, requirements, mapper, solveVariable)) {

              matches = false;

              break; // break on mismatch

            }

          }

        }

      }

    }

    if (matches) {
      mapper.add(rule, check);
    } else {
      mapper.remove(rule, check);
    }

    return matches;

  }

  public static Match getMatch(Operand ruleOperand, Operand actualOperand,
      Map<String, Requirement> requirements, String solveVariable) {

    OperandMapper mapper = new OperandMapper();

    if (!matches(ruleOperand, actualOperand, requirements, mapper, solveVariable)) {
      return null;
    }

    Knowns knowns = mapper.getResult();

    if (knowns == null) {
      return null;
    }

    return new Match(actualOperand, knowns);

  }

  public static Match findMatch(Operand find, Operand in, Map<String, Requirement> requirements,
      String target) {

    timer.start();

    Match match = recursiveFindMatch(find, in, requirements, target);

    timer.stop();

    /*if (timer.ms() > 0.25) {
      System.out.println("Find: " + find);
      System.out.println("In: " + in);
      System.out.println("Found: " + (match != null));
      timer.printDelta("findMatch() time: ");
      System.out.println("\n");
    }*/

    return match;
  }

  private static Match recursiveFindMatch(Operand find, Operand in,
      Map<String, Requirement> requirements, String target) {

    Match thisMatch = getMatch(find, in, requirements, target);

    if (thisMatch != null) {

      return thisMatch;

    } else {

      if (!in.hasChildren()) {
        return null;
      }

      for (Operand child : in) {

        Match childMatch = recursiveFindMatch(find, child, requirements, target);

        if (childMatch != null) {
          return childMatch;
        }

      }

    }

    return null;

  }

  public static class Match {

    private Operand original;
    private Knowns knowns;

    public Match(Operand original, Knowns knowns) {
      this.original = original;
      this.knowns = knowns;
    }

    public Knowns getKnowns() {
      return knowns;
    }

    public Operand getOriginal() {
      return original;
    }

  }

}
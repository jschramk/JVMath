package com.jschramk.JVMath.rewrite_engine;

import com.jschramk.JVMath.components.*;

import java.util.Map;

public class StructureMatcher {

  private static boolean matches(Operand ruleOperand, Operand actualOperand,
      Map<String, Requirement> requirements, AnalogMapper mapper, String solveVariable) {

    boolean matches = false; // default to false

    if (ruleOperand instanceof Variable) { // rule operand is variable, check requirements

      Variable variableMatch = (Variable) ruleOperand;

      matches = Requirement
          .meetsPrerequisites(variableMatch.getName(), solveVariable, actualOperand, requirements);

    } else if (ruleOperand instanceof Literal) { // rule operand is literal, check if actual is the same literal

      if (actualOperand instanceof Literal) {

        Literal ruleLiteral = (Literal) ruleOperand;
        Literal actualLiteral = (Literal) actualOperand;

        matches = ruleLiteral.getValue() == actualLiteral.getValue();

      }

    } else if (Operand
        .sameType(ruleOperand, actualOperand)) { // rule and actual operands are the  same type

      if (actualOperand instanceof BinaryOperation
          && ((BinaryOperation) actualOperand).getOperator().getAssociativity()
          == BinaryOperator.Associativity.COMMUTATIVE) {

        // add all possible matches for mapper to solve
        for (Operand matchChild : ruleOperand) {

          for (Operand operandChild : actualOperand) {

            if (matches(matchChild, operandChild, requirements, mapper, solveVariable)) {
              matches = true;
            }

          }

        }

      } else { // the operand is not a commutative operation, check operands in order

        if (actualOperand.childCount() == ruleOperand.childCount()) {

          matches = true; // default to true and check for mismatch

          for (int i = 0; i < actualOperand.childCount(); i++) {

            Operand ruleChild = ruleOperand.getChild(i);
            Operand actualChild = actualOperand.getChild(i);

            if (!matches(ruleChild, actualChild, requirements, mapper, solveVariable)) {

              matches = false;

              break; // break on mismatch

            }

          }

        }

      }

    }

    if (matches) {
      mapper.add(ruleOperand, actualOperand);
    } else {
      mapper.remove(ruleOperand, actualOperand);
    }

    return matches;

  }

  public static Match getMatch(Operand ruleOperand, Operand actualOperand,
      Map<String, Requirement> requirements, String solveVariable) {

    AnalogMapper mapper = new AnalogMapper();

    if (!matches(ruleOperand, actualOperand, requirements, mapper, solveVariable)) {
      return null;
    }

    Knowns knowns = mapper.getResult();

    if (knowns == null) {
      return null;
    }

    return new Match(actualOperand, knowns);

  }

  public static Match findMatch(Operand ruleOperand, Operand actualOperand,
      Map<String, Requirement> requirements) {

    return recursiveFindMatch(actualOperand, ruleOperand, requirements, null);
  }

  private static Match recursiveFindMatch(Operand ruleOperand, Operand actualOperand,
      Map<String, Requirement> requirements, String solveVariable) {

    Match thisMatch = getMatch(ruleOperand, actualOperand, requirements, solveVariable);

    if (thisMatch != null) {

      return thisMatch;

    } else {

      if (!actualOperand.hasChildren()) {
        return null;
      }

      for (Operand child : actualOperand) {

        Match childMatch = recursiveFindMatch(ruleOperand, child, requirements, solveVariable);

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
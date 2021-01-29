package com.jschramk.JVMath.rewrite_engine;

import com.jschramk.JVMath.components.BinaryOperation;
import com.jschramk.JVMath.components.BinaryOperator;
import com.jschramk.JVMath.components.Equation;
import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.exceptions.UnsolvableException;
import com.jschramk.JVMath.rewrite_resources.RewriteResources;
import com.jschramk.JVMath.rewrite_resources.Rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.jschramk.JVMath.rewrite_engine.StructureMatcher.findMatch;

public class RewriteEngine {

  private static final int MAX_SINGLE_RULE_APPLICATIONS = 100, MAX_ALL_RULE_CHECKS = 100;

  private static Operand getPopulatedReplacement(Operand original, Operand replacement,
      Knowns knowns) {

    Operand populated = replacement.copy().replace(knowns);

    if (!(original instanceof BinaryOperation)) {
      return populated;
    }

    BinaryOperation operation = (BinaryOperation) original;

    if (operation.getOperator().isCommutative()) {

      BinaryOperator operator = operation.getOperator();

      List<Operand> unusedChildren = new ArrayList<>();

      for (Operand child : original) {
        if (!knowns.getUsedIds().contains(child.getId())) {
          unusedChildren.add(child.copy());
        }
      }

      if (!unusedChildren.isEmpty()) {
        unusedChildren.add(populated);
        populated = new BinaryOperation(operator, unusedChildren);
      }

    }

    return populated;

  }

  public static Output<Operand> applyExpressionRule(Operand to, Rule<Operand> rule, boolean steps) {

    to = to.fixedCopy();

    StructureMatcher.Match match = findMatch(to, rule.getFind(), rule.getRequirements());

    if (match == null) {
      return null;
    }

    if (!Requirement.meetsMappingRequirements(match.getKnowns(), rule.getRequirements())) {
      return null;
    }

    Map<String, Operand> variables = match.getKnowns().getVariables();

    Output<Operand> output = new Output<>();

    Operand operandStep;

    output.incrementOperationCount(rule.stepCount());

    if (steps) {

      for (Step<Operand> step : rule.getSteps()) {

        operandStep =
            getPopulatedReplacement(match.getOriginal(), step.getReplace(), match.getKnowns());

        operandStep = to.replaceCopy(match.getOriginal().getId(), operandStep);

        String desc = step.getDescription();

        if (desc != null) {
          for (String name : variables.keySet()) {
            desc = desc.replaceAll("\\$" + name, variables.get(name).toString());
          }
        }

        Step<Operand> outStep = new Step<>(operandStep, desc);

        output.addStep(outStep);

      }

    } else {

      Step<Operand> step = rule.getLastStep();

      operandStep =
          getPopulatedReplacement(match.getOriginal(), step.getReplace(), match.getKnowns());

      operandStep = to.replaceCopy(match.getOriginal().getId(), operandStep);

      Step<Operand> outStep = new Step<>(operandStep, null);

      output.addStep(outStep);

    }

    return output;

  }

  public static Output<Equation> applyEquationRule(Equation to, Rule<Equation> rule,
      String solveFor, boolean steps) {

    to = to.fixedCopy();

    StructureMatcher.Match leftMatch = StructureMatcher
        .getMatch(rule.getFind().getLeftSide(), to.getLeftSide(), rule.getRequirements(), solveFor);

    StructureMatcher.Match rightMatch = StructureMatcher
        .getMatch(rule.getFind().getRightSide(), to.getRightSide(), rule.getRequirements(),
            solveFor);

    if (leftMatch == null || rightMatch == null) {
      return null;
    }

    if (!Knowns.sameVariables(leftMatch.getKnowns(), rightMatch.getKnowns())) {
      return null;
    }

    Knowns allKnowns = Knowns.combine(leftMatch.getKnowns(), rightMatch.getKnowns());

    if (!Requirement.meetsMappingRequirements(allKnowns, rule.getRequirements())) {
      return null;
    }

    Output<Equation> output = new Output<>();

    Map<String, Operand> variables = allKnowns.getVariables();

    Equation equationStep;

    output.incrementOperationCount(rule.stepCount());

    if (steps) {

      for (Step<Equation> step : rule.getSteps()) {

        Operand leftReplacement = step.getReplace().getLeftSide().replaceCopy(allKnowns);
        Operand rightReplacement = step.getReplace().getRightSide().replaceCopy(allKnowns);

        equationStep = new Equation(leftReplacement, rightReplacement);
        equationStep.fixTree();

        String desc = step.getDescription();

        if (desc != null) {
          for (String name : variables.keySet()) {
            desc = desc.replaceAll("\\$" + name, variables.get(name).toString());
          }
        }

        Step<Equation> outStep = new Step<>(equationStep, desc);

        output.addStep(outStep);

      }

    } else {

      Step<Equation> step = rule.getLastStep();

      Operand leftReplacement = step.getReplace().getLeftSide().replaceCopy(allKnowns);
      Operand rightReplacement = step.getReplace().getRightSide().replaceCopy(allKnowns);

      equationStep = new Equation(leftReplacement, rightReplacement);
      equationStep.fixTree();

      Step<Equation> outStep = new Step<>(equationStep, null);

      output.addStep(outStep);

    }

    if (output.isEmpty()) {
      return null;
    }

    return output;

  }

  public static Output<Operand> applyExpressionRules(Operand operand,
      List<Rule<Operand>> expressionRules, boolean condense, boolean steps) {

    Operand curr = operand;

    Output<Operand> ret = new Output<>();

    boolean appliedAny = false;

    for (int i = 0; i < MAX_ALL_RULE_CHECKS; i++) {

      boolean appliedRule = false;

      for (Rule<Operand> expressionRule : expressionRules) { // loop through each rule and try to apply

        int j = 0;

        do { // apply the rule as many times as it can be applied

          Output<Operand> applied = applyExpressionRule(curr, expressionRule, steps);

          if (applied == null)
            break;

          appliedRule = true;
          appliedAny = true;

          curr = applied.getResult();

          ret.incrementOperationCount(applied);

          if (steps)
            ret.addStep(new Step<>(curr, null));

          if (condense) {

            Operand consolidated = curr.condenseLiterals();

            if (!consolidated.equals(curr)) {

              curr = consolidated;

              //ret.incrementOperationCount();

              if (steps)
                ret.addStep(new Step<>(curr, null));

            }

          }

          j++;

        } while (j < MAX_SINGLE_RULE_APPLICATIONS);

      }

      if (!appliedRule)
        break;

    }


    if (condense) {

      Operand consolidated = curr.condenseLiterals();

      if (!consolidated.equals(curr)) {

        appliedAny = true;

        curr = consolidated;

        //ret.incrementOperationCount();

        if (steps) {
          ret.addStep(new Step<>(curr, null));
        }


      }

    }

    if (!steps && appliedAny) {
      ret.addStep(new Step<>(curr, null));
    }

    if (ret.isEmpty()) {
      return null;
    }

    return ret;

  }

  public static Output<Equation> applyEquationRules(Equation equation, List<Rule<Equation>> rules,
      String solveFor, boolean steps) {

    Operand.validateTree(equation);

    Output<Equation> ret = new Output<>();

    Equation curr = equation;

    boolean appliedAny = false;

    for (int i = 0; i < MAX_ALL_RULE_CHECKS; i++) {

      boolean appliedRule = false;

      for (Rule<Equation> rule : rules) {

        int j = 0;

        do {

          Output<Equation> applied = applyEquationRule(curr, rule, solveFor, steps);

          if (applied == null)
            break;

          appliedRule = true;
          appliedAny = true;

          ret.incrementOperationCount(applied);

          if (steps)
            ret.appendSteps(applied);

          curr = applied.getResult();

          Output<Equation> simplifiedOutput = simplify(curr);

          if (simplifiedOutput != null) {

            curr = simplifiedOutput.getResult();

            ret.incrementOperationCount(simplifiedOutput);

            if (steps)
              ret.appendSteps(simplifiedOutput);

          }

          j++;

        } while (j < MAX_SINGLE_RULE_APPLICATIONS);

      }

      if (!appliedRule) {

        curr.fixTree();

        Output<Equation> simplify = simplify(curr);

        if (simplify != null) {

          curr = simplify.getResult();

          ret.incrementOperationCount(simplify);

          if (steps)
            ret.appendSteps(simplify);

        } else {

          break;

        }

      }

    }

    if (!steps && appliedAny) {
      ret.addStep(new Step<>(curr, null));
    }

    if (ret.isEmpty()) {
      return null;
    }

    return ret;

  }

  public static Output<Operand> simplify(Operand operand) {
    return simplify(operand, false);
  }

  public static Output<Operand> simplify(Operand operand, boolean steps) {
    return applyExpressionRules(operand, RewriteResources.getRuleSet("simplify", Operand.class),
        true, steps);
  }

  public static Output<Equation> simplify(Equation equation) {
    return simplify(equation, false);
  }

  public static Output<Equation> simplify(Equation equation, boolean steps) {

    Output<Equation> ret = new Output<>();

    Operand left = equation.getLeftSide();
    Operand right = equation.getRightSide();

    Equation curr = equation;

    Output<Operand> leftOut = simplify(left, steps);
    Output<Operand> rightOut = simplify(right, steps);

    if (leftOut != null) {

      ret.incrementOperationCount(leftOut);

      if (steps) {

        for (Step<Operand> step : leftOut) {

          curr = new Equation(step.getReplace(), curr.getRightSide());

          Step<Equation> equationStep = new Step<>(curr, step.getDescription());

          ret.addStep(equationStep);

        }

      } else {

        curr = new Equation(leftOut.getResult(), curr.getRightSide());

        ret.addStep(new Step<>(curr, "Simplify the left side"));

      }

    }

    if (rightOut != null) {

      ret.incrementOperationCount(rightOut);

      if (steps) {

        for (Step<Operand> step : rightOut) {

          curr = new Equation(curr.getLeftSide(), step.getReplace());

          Step<Equation> equationStep = new Step<>(curr, step.getDescription());

          ret.addStep(equationStep);

        }

      } else {

        curr = new Equation(curr.getLeftSide(), rightOut.getResult());

        ret.addStep(new Step<>(curr, "Simplify the right side"));

      }

    }

    if (ret.isEmpty())
      return null;

    return ret;

  }

  public static Output<Equation> solve(Equation equation, String solveFor)
      throws UnsolvableException {
    return solve(equation, solveFor, false);
  }

  public static Output<Equation> solve(Equation equation, String solveFor, boolean steps)
      throws UnsolvableException {

    if (equation.isSolvedFor(solveFor)) {
      throw new UnsolvableException("Equation is already solved for " + solveFor);
    }

    Output<Equation> equationOutput =
        applyEquationRules(equation, RewriteResources.getRuleSet("solve", Equation.class), solveFor,
            steps);

    if (equationOutput == null || !equationOutput.getResult().isSolvedFor(solveFor)) {

      if (equationOutput != null) {
        System.out.println("\n\n\nlast equation step: " + equationOutput.getResult());
      }

      throw new UnsolvableException("Unable to solve for " + solveFor + " in " + equation);
    }

    return equationOutput;

  }


  public static class Output<T> implements Iterable<Step<T>> {

    private int operationCount = 0;
    private List<Step<T>> steps = new ArrayList<>();

    public Output() {
    }

    public Output(List<Step<T>> steps) {
      this.steps = steps;
    }

    @Override public Iterator<Step<T>> iterator() {
      return new OutputIterator(this);
    }

    public boolean isEmpty() {
      return steps.isEmpty();
    }

    public int stepCount() {
      return steps.size();
    }

    public int operationCount() {
      return operationCount;
    }

    public void incrementOperationCount(Output<?> output) {
      incrementOperationCount(output.operationCount);
    }

    public void incrementOperationCount() {
      incrementOperationCount(1);
    }

    public void incrementOperationCount(int i) {
      operationCount += i;
    }

    public Step<T> getStep(int i) {
      return steps.get(i);
    }

    public void addStep(Step<T> step) {
      steps.add(step);
    }

    public T getResult() {
      return steps.get(steps.size() - 1).getReplace();
    }

    private void appendSteps(Output<T> moreSteps) {
      steps.addAll(moreSteps.steps);
    }

    public void print() {

      for (Step<T> step : steps) {

        if (step.getDescription() != null) {
          System.out.println(step.getDescription() + "\n");
        }
        System.out.println("\t" + step.getReplace() + "\n");

      }

    }


    public class OutputIterator implements Iterator<Step<T>> {

      private Output<T> output;
      private int i = 0;

      public OutputIterator(Output<T> output) {
        this.output = output;
      }

      @Override public boolean hasNext() {
        return i < output.stepCount();
      }

      @Override public Step<T> next() {
        return output.getStep(i++);
      }
    }

  }



}

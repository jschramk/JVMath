package core.old;

import core.components.Equation;
import core.components.Operand;
import core.exceptions.ParserException;
import core.parse.Parser;
import core.rewrite.Requirement;

import java.io.File;
import java.util.*;

@Deprecated
public class ExpressionRule {



  private final Operand find;
  private final Operand replaceWith;
  private final Map<String, Requirement> variableRequirements;

  public ExpressionRule(Operand find, Operand replaceWith,
      Map<String, Requirement> variableRequirements) {
    this.find = find;
    this.replaceWith = replaceWith;
    this.variableRequirements = variableRequirements;
  }

  public ExpressionRule(Operand find, Operand replaceWith) {
    this(find, replaceWith, new HashMap<>());
  }

  public static ExpressionRule from(Parser p, String rule) throws ParserException {
    Equation equation = p.parse(rule).toEquation();
    return new ExpressionRule(equation.getLeftSide(), equation.getRightSide());
  }

  public static ExpressionRule from(StringRule rule, Parser p) throws ParserException {
    Operand find = p.parse(rule.getFind()).toOperand().copy();
    Operand replaceWith = p.parse(rule.getReplace()).toOperand().copy();
    return new ExpressionRule(find, replaceWith, rule.getVariableRequirements());
  }

  public static boolean meetsRequirements(String variable, String solveVariable, Operand operand,
      Map<String, Requirement> requirements) {

    if (requirements == null) {
      return true;
    }

    if (requirements.containsKey(variable)) {
      return requirements.get(variable).meetsMatchRequirements(operand, solveVariable);
    }

    return true;

  }

  public static List<ExpressionRule> getRules(List<StringRule> stringRules, Parser parser)
      throws ParserException {
    List<ExpressionRule> expressionRules = new ArrayList<>();
    for (StringRule stringRule : stringRules) {
      expressionRules.add(from(stringRule, parser));
    }
    return expressionRules;
  }

  public static List<ExpressionRule> initRules(Parser p, File file) {

    List<ExpressionRule> expressionRules = new ArrayList<>();

    try {

      Scanner s = new Scanner(file);

      while (s.hasNextLine()) {

        String[] line = s.nextLine().trim().split("//", 2);

        if (line[0].isEmpty())
          continue;

        expressionRules.add(from(p, line[0]));

      }

    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    return expressionRules;
  }

  public Map<String, Requirement> requirements() {
    return variableRequirements;
  }

  public ExpressionRule require(Requirement requirement) {
    assert !variableRequirements.containsKey(requirement.getVariable());
    variableRequirements.put(requirement.getVariable(), requirement);
    return this;
  }

  public Operand getFind() {
    return find;
  }

  public Operand getReplaceWith() {
    return replaceWith;
  }

  @Override public String toString() {
    return find + " = " + replaceWith;
  }
}

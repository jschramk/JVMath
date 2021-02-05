package core.old;

import core.components.Equation;
import core.exceptions.ParserException;
import core.parse.Parser;
import core.rewrite.Requirement;

import java.util.*;

@Deprecated
public class SolveRule {

  private final Equation find;
  private final Equation replaceWith;
  private final Map<String, Requirement> variableRequirements;

  public SolveRule(Equation find, Equation replaceWith,
      Map<String, Requirement> variableRequirements) {

    this.find = find;
    this.replaceWith = replaceWith;
    this.variableRequirements = variableRequirements;
  }

  public SolveRule(Equation find, Equation replaceWith) {
    this(find, replaceWith, new HashMap<>());
  }

  public static SolveRule from(Parser p, String rule) throws ParserException {
    String[] sides = rule.split("=>");
    assert sides.length == 2;
    Equation find = p.parse(sides[0]).toEquation();
    Equation replaceWith = p.parse(sides[1]).toEquation();
    return new SolveRule(find, replaceWith);
  }

  public static SolveRule from(StringRule rule, Parser p) throws ParserException {
    Equation find = p.parse(rule.getFind()).toEquation().copy();
    Equation replaceWith = p.parse(rule.getReplace()).toEquation().copy();
    return new SolveRule(find, replaceWith, rule.getVariableRequirements());
  }

  public static List<SolveRule> getSolveRules(List<StringRule> stringRules, Parser parser)
      throws ParserException {
    List<SolveRule> rules = new ArrayList<>();
    for (StringRule stringRule : stringRules) {
      rules.add(from(stringRule, parser));
    }
    return rules;
  }

  public Equation getFind() {
    return find;
  }

  public Equation getReplaceWith() {
    return replaceWith;
  }

  public Map<String, Requirement> requirements() {
    return variableRequirements;
  }

  public SolveRule require(Requirement requirement) {
    assert !variableRequirements.containsKey(requirement.getVariable());
    variableRequirements.put(requirement.getVariable(), requirement);
    return this;
  }

  @Override public String toString() {
    return find + " => " + replaceWith;
  }

  //TODO: make this more elegant
  public String toSentence() {

    StringBuilder s = new StringBuilder();

    s.append(find);
    s.append(" => ");
    s.append(replaceWith);

    if (!variableRequirements.isEmpty()) {

      List<String> containsSolve = new ArrayList<>();
      List<String> notContainsSolve = new ArrayList<>();

      for (Map.Entry<String, Requirement> entry : variableRequirements.entrySet()) {

        if (entry.getValue().containsSolveVariable()) {
          containsSolve.add(entry.getKey());
        } else {
          notContainsSolve.add(entry.getKey());
        }

      }

      Collections.sort(containsSolve);
      Collections.sort(notContainsSolve);

      s.append(" where ");

      if (containsSolve.size() == 1) {

        s.append(containsSolve.get(0));
        s.append(" is a function of the variable being solved for");

      } else if (containsSolve.size() > 1) {

        for (int i = 0; i < containsSolve.size(); i++) {

          if (i == containsSolve.size() - 1) {

            if (containsSolve.size() == 2) {
              s.append(' ');
            }

            s.append("and ");
          }

          s.append(containsSolve.get(i));

          if (i < containsSolve.size() - 1 && containsSolve.size() > 2) {
            s.append(", ");
          }

        }

        s.append(" are functions of the variable being solved for");

      }

      if (notContainsSolve.size() == 1) {

        if (!containsSolve.isEmpty()) {
          s.append(" and ");
        }
        s.append(notContainsSolve.get(0));
        if (containsSolve.isEmpty()) {
          s.append(" is not a function of the variable being solved for");
        } else {
          s.append(" is not");
        }

      } else if (notContainsSolve.size() > 1) {

        if (!containsSolve.isEmpty()) {
          s.append(" and ");
        }

        for (int i = 0; i < notContainsSolve.size(); i++) {

          if (i == notContainsSolve.size() - 1) {

            if (notContainsSolve.size() == 2) {
              s.append(' ');
            }

            s.append("and ");
          }

          s.append(notContainsSolve.get(i));

          if (i < notContainsSolve.size() - 1 && notContainsSolve.size() > 2) {
            s.append(", ");
          }

        }

        if (containsSolve.isEmpty()) {
          s.append(" are not functions of the variable being solved for");
        } else {
          s.append(" are not");
        }

      }

    }

    return s.toString();

  }

}

package com.jschramk.JVMath.rewrite_engine;

import com.google.gson.JsonObject;
import com.jschramk.JVMath.components.Literal;
import com.jschramk.JVMath.components.Operand;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Requirement {

  private static String CONTAINS_TARGET_VARIABLE = "contains solve";
  private static String REQUIRED_TYPE = "type";
  private static String REQUIRED_NOT_TYPE = "not type";
  private static String REQUIRED_VALUE = "value";
  private static String REQUIRED_NOT_VALUE = "not value";
  private static String VARIABLE_NAME = "variable";
  /*private static String REQUIRED_CONTAINS = "contains";
  private static String REQUIRED_NOT_CONTAINS = "not contains";
  private static String SHARES_VARIABLES = "shares with";*/

  private String variable;
  private Operand.Type requiredType;
  private Operand.Type requiredNotType;
  private boolean containsTargetVariable;
  private Double requiredValue = null;
  private Double requiredNotValue = null;
  private Set<String> notContains;
  private Set<String> contains;
  private String sharesWith;

  public Requirement(String name) {
    this.variable = name;
  }

  public static Requirement fromJson(JsonObject object) {

    Requirement requirement = new Requirement(object.get(VARIABLE_NAME).getAsString());

    /*if (object.has(SHARES_VARIABLES)) {
      requirement.sharesWith(object.get(SHARES_VARIABLES).getAsString());
    }*/
    if (object.has(REQUIRED_TYPE)) {
      requirement.type(Operand.Type.valueOf(object.get(REQUIRED_TYPE).getAsString()));
    }
    if (object.has(REQUIRED_NOT_TYPE)) {
      requirement.notType(Operand.Type.valueOf(object.get(REQUIRED_NOT_TYPE).getAsString()));
    }
    if (object.has(CONTAINS_TARGET_VARIABLE)) {
      requirement.containsTarget(object.get(CONTAINS_TARGET_VARIABLE).getAsBoolean());
    }
    if (object.has(REQUIRED_VALUE)) {
      requirement.value(object.get(REQUIRED_VALUE).getAsDouble());
    }
    if (object.has(REQUIRED_NOT_VALUE)) {
      requirement.notValue(object.get(REQUIRED_NOT_VALUE).getAsDouble());
    }
    /*if (object.has(REQUIRED_CONTAINS)) {
      JsonArray array = object.get(REQUIRED_CONTAINS).getAsJsonArray();
      Set<String> contains = new HashSet<>();
      for (JsonElement o : array) {
        contains.add(o.getAsString());
      }
      requirement.contains(contains);
    }*/

    /*if (object.has(REQUIRED_NOT_CONTAINS)) {
      JsonArray array = object.get(REQUIRED_NOT_CONTAINS).getAsJsonArray();
      Set<String> notContains = new HashSet<>();
      for (JsonElement o : array) {
        notContains.add(o.getAsString());
      }
      requirement.notContains(notContains);
    }*/

    return requirement;

  }

  public static boolean meetsPrerequisites(String variable, String solveVariable, Operand operand,
      Map<String, Requirement> requirements) {

    if (requirements == null) {
      return true;
    }

    if (requirements.containsKey(variable)) {
      return requirements.get(variable).meetsMatchRequirements(operand, solveVariable);
    }

    return true;

  }

  public static boolean meetsMappingRequirements(Knowns knowns,
      Map<String, Requirement> requirements) {

    Map<String, Operand> vars = knowns.getVariables();

    for (Requirement r : requirements.values()) {
      if (!r.meetsMappingRequirements(vars)) {
        return false;
      }
    }

    return true;

  }

  public Requirement type(Operand.Type requiredType) {
    this.requiredType = requiredType;
    return this;
  }

  public Requirement notType(Operand.Type requiredType) {
    this.requiredNotType = requiredType;
    return this;
  }

  public Requirement containsTarget(boolean containsSolveVariable) {
    this.containsTargetVariable = containsSolveVariable;
    return this;
  }

  public Requirement value(double value) {
    requiredValue = value;
    return this;
  }

  public Requirement notValue(double value) {
    requiredNotValue = value;
    return this;
  }

  public Requirement contains(Set<String> ruleVars) {
    contains = ruleVars;
    return this;
  }

  /*
  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    object.addProperty(VARIABLE_NAME, variable);
    if (requiredType != null) {
      object.addProperty(REQUIRED_TYPE, requiredType.toString());
    }
    if (requiredValue != null) {
      object.addProperty(REQUIRED_VALUE, requiredValue);
    }
    if (requiredNotValue != null) {
      object.addProperty(REQUIRED_NOT_VALUE, requiredNotValue);
    }
    object.addProperty(CONTAINS_TARGET_VARIABLE, containsTargetVariable);

    if (contains != null && !contains.isEmpty()) {
      JsonArray array = new JsonArray();
      for (String s : contains) {
        array.add(s);
      }
      object.add(REQUIRED_CONTAINS, array);
    }

    if (notContains != null && !notContains.isEmpty()) {
      JsonArray array = new JsonArray();
      for (String s : notContains) {
        array.add(s);
      }
      object.add(REQUIRED_NOT_CONTAINS, array);
    }

    return object;
  }*/

  public Requirement sharesWith(String ruleVar) {
    sharesWith = ruleVar;
    return this;
  }

  public Requirement notContains(Set<String> ruleVars) {
    notContains = ruleVars;
    return this;
  }

  public String getVariable() {
    return variable;
  }

  public boolean containsSolveVariable() {
    return containsTargetVariable;
  }

  public boolean meetsMappingRequirements(Map<String, Operand> mappedVariables) {

    Operand mappedOperand = mappedVariables.get(variable);

    if (contains != null) {
      for (String var : contains) {

        if (!mappedVariables.containsKey(var)) {
          throw new IllegalArgumentException("Variable \"" + var + "\" not in mapped variables");
        }

        if (!mappedOperand.treeContainsSimilar(mappedVariables.get(var))) {
          return false;
        }

      }
    }

    if (notContains != null) {
      for (String var : notContains) {
        if (!mappedVariables.containsKey(var)) {
          throw new IllegalArgumentException("Variable \"" + var + "\" not in mapped variables");
        }

        if (mappedOperand.treeContainsSimilar(mappedVariables.get(var))) {
          return false;
        }
      }
    }

    if (sharesWith != null) {

      Set<String> s1 = mappedVariables.get(variable).getVariables();
      Set<String> s2 = mappedVariables.get(sharesWith).getVariables();

      return !Collections.disjoint(s1, s2);

    }

    return true;

  }

  public boolean meetsMatchRequirements(Operand operand, String targetVariable) {
    if (requiredType != null && operand.getType() != requiredType) {
      return false;
    }

    if (requiredNotType != null && operand.getType() == requiredNotType) {
      return false;
    }

    if (targetVariable != null && containsTargetVariable != operand.getVariables()
        .contains(targetVariable)) {
      return false;
    }

    if (operand instanceof Literal && requiredValue != null
        && operand.computeToDouble() != requiredValue) {
      return false;
    }

    return !(operand instanceof Literal) || requiredNotValue == null
        || operand.computeToDouble() != requiredNotValue;

    //System.out.println(operand + " ("+operand.getType()+") meets requirements");
  }

  //TODO: update
  @Override public String toString() {
    return "Variable: " + variable + ", Type: " + requiredType + ", Contains Solve: "
        + containsTargetVariable;
  }

}
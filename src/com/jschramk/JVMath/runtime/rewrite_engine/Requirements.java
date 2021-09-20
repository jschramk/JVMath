package com.jschramk.JVMath.runtime.rewrite_engine;

import com.jschramk.JVMath.runtime.components.Operand;

import java.util.HashMap;
import java.util.Map;

public class Requirements {

  private Map<String, Requirement> requirementMap = new HashMap<>();

  public void add(Requirement... requirements) {
    for (Requirement requirement : requirements) {
      if (requirementMap.containsKey(requirement.getVariable())) {
        throw new IllegalArgumentException("Duplicate requirement variable: " + requirement.getVariable());
      }
      requirementMap.put(requirement.getVariable(), requirement);
    }
  }

  public Requirement get(String variable) {
    if (!requirementMap.containsKey(variable)) {
      throw new IllegalArgumentException("No requirement for variable: " + variable);
    }
    return requirementMap.get(variable);
  }

  public boolean meetsPrerequisites(String variable, String solveVariable, Operand operand) {
    if (requirementMap.containsKey(variable)) {
      return requirementMap.get(variable).meetsMatchRequirements(operand, solveVariable);
    }
    return true;
  }

}

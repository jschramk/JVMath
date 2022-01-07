package com.jschramk.JVMath.runtime.math_engine;

import com.jschramk.JVMath.runtime.components.Operand;

import java.util.HashMap;
import java.util.Map;

public class VariableFilterMap {

  private Map<String, VariableFilter> requirementMap = new HashMap<>();

  public void add(VariableFilter... variableFilters) {
    for (VariableFilter variableFilter : variableFilters) {
      if (requirementMap.containsKey(variableFilter.getVariable())) {
        throw new IllegalArgumentException("Duplicate requirement variable: " + variableFilter.getVariable());
      }
      requirementMap.put(variableFilter.getVariable(), variableFilter);
    }
  }

  public VariableFilter get(String variable) {
    if (!requirementMap.containsKey(variable)) {
      throw new IllegalArgumentException("No requirement for variable: " + variable);
    }
    return requirementMap.get(variable);
  }

  public boolean passes(String variable, String solveVariable, Operand operand) {
    if (requirementMap.containsKey(variable)) {
      return requirementMap.get(variable).passes(operand, solveVariable);
    }
    return true;
  }

}

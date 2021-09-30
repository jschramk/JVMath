package com.jschramk.JVMath.runtime.math_engine;

import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.components.Variable;
import com.jschramk.JVMath.runtime.components.VariableDomain;

import java.util.*;

public class SolvedMappings {

  private final Set<Integer> usedIds = new HashSet<>();
  private final Map<Integer, Operand> instanceMappings = new HashMap<>();
  private final Map<Operand, Operand> generalMappings = new HashMap<>();

  public static boolean sameVariables(SolvedMappings k1, SolvedMappings k2) {
    for (Operand known : k1.generalMappings.keySet()) {
      if (known instanceof Variable && k2.hasGeneralMapping(known)) {
        if (!k1.getGeneralMapping(known).equals(k2.getGeneralMapping(known))) return false;
      }
    }
    return true;
  }

  public boolean hasGeneralMapping(Operand operand) {
    return generalMappings.containsKey(operand);
  }

  public Operand getGeneralMapping(Operand operand) {
    return generalMappings.get(operand);
  }

  public static SolvedMappings combine(SolvedMappings k1, SolvedMappings k2) {
    SolvedMappings result = new SolvedMappings();
    result.generalMappings.putAll(k1.generalMappings);
    result.generalMappings.putAll(k2.generalMappings);
    result.instanceMappings.putAll(k1.instanceMappings);
    result.instanceMappings.putAll(k2.instanceMappings);
    result.usedIds.addAll(k1.usedIds);
    result.usedIds.addAll(k2.usedIds);
    return result;
  }

  public boolean hasInstanceMapping(Operand operand) {
    return instanceMappings.containsKey(operand.getId());
  }

  public Operand getInstanceMapping(Operand operand) {
    return instanceMappings.get(operand.getId());
  }

  public Operand getInstanceMapping(Integer id) {
    return instanceMappings.get(id);
  }

  public void putMapping(Operand operand, Operand analog) {
    instanceMappings.put(operand.getId(), analog);
    if (!generalMappings.containsKey(operand) || generalMappings.get(operand).equals(analog)) {
      generalMappings.put(operand, analog);
      usedIds.add(analog.getId());
    }
  }

  public void addUsed(Collection<Operand> operands) {
    for (Operand operand : operands) {
      usedIds.add(operand.getId());
    }
  }

  public Set<Integer> getUsedIds() {
    return usedIds;
  }

  public Set<Integer> getMappedIds() {
    return instanceMappings.keySet();
  }

  @Override
  public String toString() {
    return instanceMappings.toString();
  }

  public VariableDomain toVariableDomain() {

    VariableDomain variableDomain = new VariableDomain();

    for (String s : getVariables().keySet()) {
      variableDomain.put(s, getVariables().get(s));
    }

    return variableDomain;

  }

  public Map<String, Operand> getVariables() {

    Map<String, Operand> variables = new HashMap<>();

    for (Map.Entry<Operand, Operand> entry : generalMappings.entrySet()) {

      if (entry.getKey() instanceof Variable) {
        variables.put(((Variable) entry.getKey()).getName(), entry.getValue().copy());
      }

    }

    return variables;

  }

}

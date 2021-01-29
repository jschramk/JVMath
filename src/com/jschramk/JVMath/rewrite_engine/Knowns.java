package com.jschramk.JVMath.rewrite_engine;

import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.components.Variable;
import com.jschramk.JVMath.components.VariableDomain;

import java.util.*;

//TODO: refactor using instance ID instead of IdentityHashMap
public class Knowns {

  private final Set<Integer> usedIds = new HashSet<>();
  private final IdentityHashMap<Operand, Operand> instanceAnalogs = new IdentityHashMap<>();
  private final Map<Operand, Operand> generalAnalogs = new HashMap<>();

  public static boolean sameVariables(Knowns k1, Knowns k2) {
    for (Operand known : k1.generalAnalogs.keySet()) {
      if (known instanceof Variable && k2.containsGeneral(known)) {
        if (!k1.getGeneral(known).equals(k2.getGeneral(known)))
          return false;
      }
    }
    return true;
  }

  public static Knowns combine(Knowns k1, Knowns k2) {
    Knowns result = new Knowns();
    result.generalAnalogs.putAll(k1.generalAnalogs);
    result.generalAnalogs.putAll(k2.generalAnalogs);
    result.instanceAnalogs.putAll(k1.instanceAnalogs);
    result.instanceAnalogs.putAll(k2.instanceAnalogs);
    result.usedIds.addAll(k1.usedIds);
    result.usedIds.addAll(k2.usedIds);
    return result;
  }

  public boolean containsGeneral(Operand operand) {
    return generalAnalogs.containsKey(operand);
  }

  public boolean containsInstance(Operand operand) {
    return instanceAnalogs.containsKey(operand);
  }

  public Operand getGeneral(Operand operand) {
    return generalAnalogs.get(operand);
  }

  public Operand getInstance(Operand operand) {
    return instanceAnalogs.get(operand);
  }

  public void add(Operand operand, Operand analog) {
    if (!generalAnalogs.containsKey(operand) || generalAnalogs.get(operand).equals(analog)) {
      instanceAnalogs.put(operand, analog);
      generalAnalogs.put(operand, analog);
      usedIds.add(analog.getId());
    }
  }

  public void addUsed(Collection<Operand> operands) {
    for (Operand operand : operands) {
      usedIds.add(operand.getId());
    }
  }

  public Map<String, Operand> getVariables() {

    Map<String, Operand> variables = new HashMap<>();

    for (Map.Entry<Operand, Operand> entry : generalAnalogs.entrySet()) {

      if (entry.getKey() instanceof Variable) {
        variables.put(((Variable) entry.getKey()).getName(), entry.getValue().copy());
      }

    }

    return variables;

  }

  public Set<Integer> getUsedIds() {
    return usedIds;
  }

  @Override public String toString() {
    return instanceAnalogs.toString();
  }

  public void print() {
    System.out.println("Knowns:");
    if (instanceAnalogs.isEmpty()) {
      System.out.println("none");
    } else {
      for (Map.Entry<Operand, Operand> entry : instanceAnalogs.entrySet()) {
        System.out.println(entry.getKey().toInfoString() + " = " + entry.getValue().toInfoString());
      }
    }

  }


  public VariableDomain toVariableDomain() {

    VariableDomain variableDomain = new VariableDomain();

    for (String s : getVariables().keySet()) {
      variableDomain.put(s, getVariables().get(s));
    }

    return variableDomain;

  }

}

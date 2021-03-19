package com.jschramk.JVMath.components;

import structures.DirectedGraph;

import java.util.*;

public class VariableDomain {

  private final Map<String, Operand> values = new HashMap<>();
  private final DirectedGraph<String> dependencies = new DirectedGraph<>();

  public static VariableDomain getDefault() {
    VariableDomain vd = new VariableDomain();
    vd.addConstants();
    return vd;
  }

  public void removeAll(Collection<String> variables) {
    for (String name : variables) {
      remove(name);
    }
  }

  public void remove(String variable) {
    values.remove(variable);
    dependencies.removeVertex(variable);
  }

  public void clear() {
    values.clear();
    dependencies.clear();
    addConstants();
  }

  private void addConstants() {
    put("e", Math.E);
    put("pi", Math.PI);
  }

  public void put(String variable, Operand value) {

    for (String s : value.getVariables()) {
      dependencies.addEdge(variable, s);
    }

    if (dependencies.hasCycles()) {

      clear();

      throw new IllegalArgumentException("Cannot add circular variable dependency");

    }

    values.put(variable, value.copy());
    value.setVariableDomain(this);
  }

  public void put(String variable, double value) {
    put(variable, new Literal(value));
  }

  public Operand get(String variable) {

    if (!contains(variable)) {
      throw new IllegalArgumentException(String.format(
        "Variable \"%s\" not in variable domain",
        variable
      ));
    }

    if (!dependencies.getAdjacentVerticesOf(variable).isEmpty()) {

      List<String> order = dependencies.getTopologicalOrder(variable);

      if (!order.isEmpty()) {

        Operand curr = new Variable(variable);

        for (int i = order.size() - 1; i >= 0; i--) {

          String name = order.get(i);

          if (values.containsKey(name)) {
            curr = curr.replace(name, values.get(name));
          }

        }

        return curr;

      }

    }

    return values.get(variable);

  }

  public boolean contains(String variable) {
    return values.get(variable) != null;
  }

  public boolean containsAll(Set<String> variables) {
    return values.keySet().containsAll(variables);
  }

  @Override
  public String toString() {

    StringBuilder s = new StringBuilder();

    List<String> sortedNames = new ArrayList<>(values.keySet());

    for (String variable : sortedNames) {
      if (s.length() > 0) {
        s.append(", ");
      }
      s.append(variable);
      s.append(" = ");
      s.append(values.get(variable).toString());
    }

    return s.toString();
  }

}

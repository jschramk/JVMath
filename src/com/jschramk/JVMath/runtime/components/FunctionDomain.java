package com.jschramk.JVMath.runtime.components;

import java.util.*;

public class FunctionDomain {

  private static final FunctionDomain MAIN_INSTANCE = buildDefault();

  private final Map<String, FunctionOperator> functionOperators = new HashMap<>();
  private final Set<String> allFunctions = new HashSet<>();
  private List<String> general = new ArrayList<>();
  private List<String> categoryList = new ArrayList<>();
  private Map<String, List<String>> categories = new HashMap<>();

  public static FunctionDomain getDefault() {
    return MAIN_INSTANCE;
  }

  private static FunctionDomain buildDefault() {

    FunctionDomain fd = new FunctionDomain();

    fd.addCategory("Trigonometry");
    fd.put("Trigonometry", Operators.SINE);
    fd.put("Trigonometry", Operators.COSINE);
    fd.put("Trigonometry", Operators.TANGENT);
    fd.put("Trigonometry", Operators.INVERSE_SINE);
    fd.put("Trigonometry", Operators.INVERSE_COSINE);
    fd.put("Trigonometry", Operators.INVERSE_TANGENT);

    fd.addCategory("Exponential");
    fd.put("Exponential", Operators.LOG);
    fd.put("Exponential", Operators.ROOT);

    fd.addCategory("Statistics");
    fd.put("Statistics", Operators.NCR);
    fd.put("Statistics", Operators.NPR);

    fd.addCategory("Matrices");
    fd.put("Matrices", Operators.TRANSPOSE);

    /*fd.addCategory("Calculus");
    fd.put("Calculus", Operators.DERIVE);*/

    fd.addCategory("More");
    fd.put("More", Operators.TO_RADIANS);
    fd.put("More", Operators.TO_DEGREES);
    fd.put("More", Operators.STEP);
    fd.put("More", Operators.RAMP);

    return fd;

  }

  private void addCategory(String name) {
    categoryList.add(name);
  }

  private void put(String category, FunctionOperator operator) {
    if (category == null) {
      general.add(operator.getName());
    } else {
      if (!categories.containsKey(category)) {
        categories.put(category, new ArrayList<>());
      }
      categories.get(category).add(operator.getName());
    }
    functionOperators.put(operator.getName(), operator);
    allFunctions.add(operator.getName());
  }

  public void print() {
    if (!getCommonFunctions().isEmpty()) {

      for (String name : getCommonFunctions()) {

        System.out.println(name);

        if (hasDescriptions(name)) {
          for (String desc : getDescriptions(name)) {
            System.out.println("  " + desc);
          }
        }

      }

      System.out.println();

    }

    for (String category : getCategories()) {

      System.out.println(category);

      for (String name : getFunctionsInCategory(category)) {

        System.out.println("  " + name);

        if (hasDescriptions(name)) {
          for (String desc : getDescriptions(name)) {
            System.out.println("    " + desc);
          }
        }
      }

      System.out.println();

    }
  }

  public List<String> getCommonFunctions() {
    return general;
  }

  public boolean hasDescriptions(String name) {
    if (!functionOperators.containsKey(name)) {
      throw new IllegalArgumentException("Unknown function: " + name);
    }
    return functionOperators.get(name).getDescriptions() != null;
  }

  public List<String> getDescriptions(String name) {
    if (!functionOperators.containsKey(name)) {
      throw new IllegalArgumentException("Unknown function: " + name);
    }
    return functionOperators.get(name).getDescriptions();
  }

  public List<String> getCategories() {
    return categoryList;
  }

  public List<String> getFunctionsInCategory(String name) {
    if (!categories.containsKey(name)) {
      throw new IllegalArgumentException("No function category \"" + name + "\"");
    }
    return categories.get(name);
  }

  private void put(FunctionOperator operator) {
    put(null, operator);
  }

  public Set<String> getAllFunctions() {
    return allFunctions;
  }

  public Operand getFunctionInstance(String name, Operand arg) {

    if (functionOperators.containsKey(name)) {

      return new FunctionOperation(functionOperators.get(name), arg);

    } else {

      throw new IllegalArgumentException("Unknown function: " + name);

    }

  }

}



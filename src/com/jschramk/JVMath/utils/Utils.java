package com.jschramk.JVMath.utils;


import com.jschramk.JVMath.components.BinaryOperation;
import com.jschramk.JVMath.components.Literal;
import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.components.Variable;

import java.util.*;

public class Utils {

  private static Random random = new Random();

  public static Operand randomExpression(int depth) {

    if (depth == 0) {
      throw new IllegalArgumentException("Random expression cannot have depth < 1");
    }

    if (depth == 1) {

      if (random.nextBoolean()) {

        return new Literal(2 * random.nextDouble() - 1);

      } else {

        return new Variable("var" + random.nextInt(26));

      }

    } else {

      switch (random.nextInt(4)) {

        case 0:
        default:
          return BinaryOperation.sum(randomExpression(depth - 1), randomExpression(depth - 1));
        case 1:
          return BinaryOperation.product(randomExpression(depth - 1), randomExpression(depth - 1));
        case 2:
          return BinaryOperation.division(randomExpression(depth - 1), randomExpression(depth - 1));
        case 3:
          return BinaryOperation.exponent(randomExpression(depth - 1), randomExpression(depth - 1));

      }

    }

  }



  public static String[] splitVector(String s) {
    return splitLevel(s, '(', ')', ',');
  }

  public static String[] splitMatrixRows(String s) {
    return splitLevel(s, '(', ')', ';');
  }

  public static String[] splitLevel(String input, char inc, char dec, char split) {

    List<String> segments = new ArrayList<>();

    int level = 0;
    int last = 0;
    for (int i = 0; i < input.length(); i++) {

      if (input.charAt(i) == inc) {
        level++;
      } else if (input.charAt(i) == dec) {
        level--;
      } else if (input.charAt(i) == split && level == 0) {
        segments.add(input.substring(last, i));
        last = i + 1;
      }

    }

    segments.add(input.substring(last));

    String[] arr = new String[segments.size()];

    segments.toArray(arr);

    return arr;

  }

  public static String toTreeString(Operand operand) {

    List<StringBuilder> lines = makeTreeLines(operand);

    StringBuilder sb = new StringBuilder(lines.size() * 20);

    for (StringBuilder line : lines) {
      sb.append(line);
      sb.append('\n');
    }

    return sb.toString();
  }

  private static List<StringBuilder> makeTreeLines(Operand operand) {

    List<StringBuilder> result = new LinkedList<>();

    result.add(new StringBuilder().append(operand.toInfoString()));

    if (operand.hasChildren()) {

      for (int i = 0; i < operand.childCount(); i++) {

        List<StringBuilder> subtree = makeTreeLines(operand.getChild(i));
        if (i < operand.childCount() - 1) {
          addNonTerminal(result, subtree);
        } else {
          addTerminal(result, subtree);
        }

      }

    }

    return result;
  }

  private static void addNonTerminal(List<StringBuilder> result, List<StringBuilder> subtree) {

    Iterator<StringBuilder> iterator = subtree.iterator();

    result.add(iterator.next().insert(0, "├── "));

    while (iterator.hasNext()) {

      result.add(iterator.next().insert(0, "│   "));

    }

  }

  private static void addTerminal(List<StringBuilder> result, List<StringBuilder> subtree) {

    Iterator<StringBuilder> iterator = subtree.iterator();

    result.add(iterator.next().insert(0, "└── "));

    while (iterator.hasNext()) {

      result.add(iterator.next().insert(0, "    "));

    }
  }

  public static boolean classExtends(Class<?> subclass, Class<?> superclass) {
    return superclass.isAssignableFrom(subclass);
  }

}

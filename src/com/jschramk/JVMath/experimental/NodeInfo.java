package com.jschramk.JVMath.experimental;

public class NodeInfo {

  public static final int TYPE_LITERAL = 0;
  public static final int TYPE_VARIABLE = 1;
  public static final int TYPE_ADDITION = 2;
  public static final int TYPE_SUBTRACTION = 3;
  public static final int TYPE_MULTIPLICATION = 4;
  public static final int TYPE_DIVISION = 5;
  public static final int TYPE_EXPONENT = 6;
  public static final int TYPE_NEGATION = 7;
  public static final int TYPE_FUNCTION = 8;
  public static final int TYPE_MATRIX = 9;
  public static final int TYPE_OTHER = 10;

  // if child priority is less than parent priority,
  // parentheses will be added to the string
  private static final int[] priorities = {
    100, // literal
    100, // variable
    1, // addition
    1, // subtraction
    2, // multiplication
    2, // division
    3, // exponent
    2, // negation
    4, // function
    100, // matrix
    -1 // other
  };

  public static int getPriority(int type) {
    return priorities[type];
  }

}

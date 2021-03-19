package com.jschramk.JVMath.experimental;

public abstract class TreeOperand {

  public static TreeOperand from(double value) {
    return new TreeLiteral(value);
  }

  public static TreeOperand from(String name) {
    return new TreeVariable(name);
  }

  public abstract TreeNode evaluate(TreeNode... children);

  public abstract String string(TreeNode... children);

  public abstract Class<?> returnType(TreeNode... children);

}

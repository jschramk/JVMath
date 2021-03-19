package com.jschramk.JVMath.experimental;

public class TreeVariable extends TreeOperand {

  private String name;

  public TreeVariable(String name) {
    this.name = name;
  }

  @Override
  public TreeNode evaluate(TreeNode... children) {
    return new TreeNode(this);
  }

  @Override
  public String string(TreeNode... children) {
    return name;
  }

  @Override
  public Class<?> returnType(TreeNode... children) {
    return TreeVariable.class;
  }

}

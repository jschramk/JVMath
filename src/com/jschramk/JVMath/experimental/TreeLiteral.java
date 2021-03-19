package com.jschramk.JVMath.experimental;

import com.jschramk.JVMath.components.JVMathSettings;
import mathutils.MathUtils;

public class TreeLiteral extends TreeOperand {

  private double value;

  public TreeLiteral(double value) {
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  @Override
  public TreeNode evaluate(TreeNode... children) {
    return new TreeNode(this);
  }

  @Override
  public String string(TreeNode... children) {
    return MathUtils.format(value, JVMathSettings.DECIMAL_PLACES);
  }

  @Override
  public Class<?> returnType(TreeNode... children) {
    return TreeLiteral.class;
  }

}

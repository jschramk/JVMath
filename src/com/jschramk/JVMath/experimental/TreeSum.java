package com.jschramk.JVMath.experimental;

import com.jschramk.JVMath.utils.Utils;

public class TreeSum extends TreeOperand {

  @Override
  public TreeNode evaluate(TreeNode... children) {

    double sum = 0;

    for (TreeNode child : children) {

      if (Utils.classExtends(child.returnType(), TreeLiteral.class)) {

        TreeLiteral literal = (TreeLiteral) child.evaluate().getOperand();

        sum += literal.getValue();

      } else {

        return new TreeNode(this, children);

      }

    }

    return new TreeNode(new TreeLiteral(sum));
  }

  @Override
  public String string(TreeNode... children) {

    StringBuilder s = new StringBuilder();

    int l = s.length();

    for (TreeNode child : children) {

      if (s.length() > l) {
        s.append(" + ");
      }

      s.append(child);

    }

    return s.toString();
  }

  @Override
  public Class<?> returnType(TreeNode... children) {

    for (TreeNode child : children) {

      if (!Utils.classExtends(child.returnType(), TreeLiteral.class)) {

        return TreeSum.class;

      }

    }

    return TreeLiteral.class;
  }

}

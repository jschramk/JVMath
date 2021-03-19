package com.jschramk.JVMath.experimental;

public class TreeNode {

  private int id = -1;
  private TreeOperand operand;
  private TreeNode parent;
  private TreeNode[] children;

  public TreeNode(TreeOperand operand, TreeNode... children) {
    setOperand(operand);
    setChildren(children);
  }

  public void setChildren(TreeNode... children) {
    if (children.length == 0) {
      this.children = null;
    } else {
      this.children = children;
    }

    for (TreeNode child : children) {
      child.setParent(this);
    }

  }

  public void serialize() {
    serialize(null, 0);
  }

  private int serialize(TreeNode parent, int id) {

    this.parent = parent;
    this.id = id++;

    if (hasChildren()) {

      for (TreeNode child : children) {

        id = child.serialize(this, id);

      }

    }

    return id;

  }

  public int getId() {
    return id;
  }

  public boolean hasParent() {
    return parent != null;
  }

  public TreeNode getParent() {
    return parent;
  }

  private void setParent(TreeNode parent) {
    this.parent = parent;
  }

  public TreeNode getChild(int i) {
    return children[i];
  }

  public Class<?> returnType() {
    return operand.returnType(children);
  }

  public TreeNode evaluate() {
    return operand.evaluate(children);
  }

  public TreeOperand getOperand() {
    return operand;
  }

  public void setOperand(TreeOperand operand) {
    this.operand = operand;
  }

  public boolean hasChildren() {
    return children != null && children.length != 0;
  }

  @Override
  public String toString() {
    return operand.string(children);
  }

  public String infoString() {
    return toString() + " #" + Integer.toHexString(id);
  }

  public void print() {
    print("");
  }

  private void print(String indent) {
    System.out.println(indent + infoString());

    if (hasChildren()) {
      for (TreeNode child : children) {
        child.print(indent + "   ");
      }
    }

  }

}

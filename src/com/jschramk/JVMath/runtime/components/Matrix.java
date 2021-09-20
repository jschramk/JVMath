package com.jschramk.JVMath.runtime.components;

import java.util.ArrayList;
import java.util.List;

public class Matrix extends Operand {

  private int rows, cols;

  public Matrix(List<Operand> elements, int rows) {

    if (elements.size() == 1) {
      throw new IllegalArgumentException("Cannot create 1 by 1 matrix");
    }

    if (elements.size() % rows != 0) {
      throw new IllegalArgumentException("Matrix has invalid dimensions");
    }

    this.rows = rows;
    this.cols = elements.size() / rows;

    setChildren(elements);

  }

  private Matrix(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
  }

  public Matrix transpose() {

    List<Operand> children = new ArrayList<>();

    for (int j = 0; j < colCount(); j++) {

      for (int i = 0; i < rowCount(); i++) {

        children.add(getElement(i, j).copy());

      }

    }

    return new Matrix(children, colCount());

  }

  public int colCount() {
    return cols;
  }

  public int rowCount() {
    return rows;
  }

  public Operand getElement(int row, int col) {

    if (row >= rows) {
      throw new IndexOutOfBoundsException("Row index: " + row + ", Rows: " + rows);
    }

    if (col >= cols) {
      throw new IndexOutOfBoundsException("Col index: " + col + ", Cols: " + cols);
    }

    return getChild(cols * row + col);

  }

  @Override
  public Operand evaluate() {

    List<Operand> evaluatedChildren = new ArrayList<>();

    for (Operand child : this) {
      evaluatedChildren.add(child.evaluate());
    }

    return new Matrix(evaluatedChildren, rowCount());
  }

  @Override
  protected Operand shallowCopy() {
    return new Matrix(rows, cols);
  }

  @Override
  public String toString() {

    StringBuilder s = new StringBuilder();
    s.append('(');

    for (int i = 0; i < childCount(); i++) {

      if (i > 0) {
        if (rows > 0 && i % cols == 0) {
          s.append("; ");
        } else {
          s.append(", ");
        }
      }

      s.append(getChild(i));

    }

    s.append(')');

    return s.toString();
  }

  /*@Override
  public JsonObject toShallowJson() {

    JsonObject object = new JsonObject();

    object.addProperty("rows", rows);
    object.addProperty("cols", cols);

    return object;

  }*/

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof Matrix)) {
      return false;
    }

    Matrix matrix = (Matrix) o;

    return matrix.rowCount() == rowCount() && childrenEquals(matrix);

  }

  @Override
  public int hashCode() {
    return childrenHashCode();
  }

  @Override
  public Enums.OperandType getType() {
    return Enums.OperandType.MATRIX;
  }

}

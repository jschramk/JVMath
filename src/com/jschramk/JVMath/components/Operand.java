package com.jschramk.JVMath.components;

import com.jschramk.JVMath.rewrite_engine.Knowns;
import com.jschramk.JVMath.utils.Utils;

import java.util.*;

/**
 * Notes:
 * Avoid methods that take a full copy of the tree, it wastes memory like crazy
 */
public abstract class Operand implements Iterable<Operand> {

  private static int NEXT_ID = 0;

  private short level = -1;
  private int id = getNextId();
  private Operand parent;
  private Operand[] children;

  private static int getNextId() {
    return NEXT_ID++;
  }

  private static Map<Integer, Operand> getVariableReplaceMap(
    Operand operand, VariableDomain domain
  ) {

    Map<Integer, Operand> map = new HashMap<>();

    if (operand instanceof Variable) {

      Variable variable = (Variable) operand;

      if (domain.contains(variable.getName())) {
        map.put(operand.id, domain.get(variable.getName()));
      }

    } else if (operand.children != null) {

      for (Operand child : operand.children) {

        map.putAll(getVariableReplaceMap(child, domain));

      }

    }

    return map;

  }

  protected static String childPriorityString(Operand parent, Operand child) {
    return childPriorityString(parent, child, false);
  }

  protected static String childPriorityString(Operand parent, Operand child, boolean latex) {
    if (needsParentheses(parent, child)) {
      if (latex) {
        return " \\left( " + child.toLaTeX() + " \\right) ";
      } else {
        return "(" + child.toString() + ")";
      }
    } else {
      if (latex) {
        return child.toLaTeX();
      } else {
        return child.toString();
      }
    }
  }

  public static boolean needsParentheses(Operand parent, Operand child) {
    return needsParentheses(parent, child, true);
  }

  public String toLaTeX() {
    return toString();
  }

  public static boolean needsParentheses(Operand parent, Operand child, boolean needsOnEquals) {

    if (needsOnEquals) {
      return typePriority(child.getType()) <= typePriority(parent.getType());
    } else {
      return typePriority(child.getType()) < typePriority(parent.getType());
    }

  }

  private static int typePriority(Enums.OperandType type) {

    switch (type) {

      case MATRIX:
        return 5;
      case CONSTANT:
      case VARIABLE:
      case LITERAL:
        return 4;
      case FUNCTION:
        return 3;
      case FACTORIAL:
      case EXPONENT:
        return 2;
      case PRODUCT:
      case DIVISION:
      case NEGATION:
        return 1;
      case SUM:
        return 0;

    }

    return -1;

  }

  public abstract Enums.OperandType getType();

  public static boolean sameType(Operand o1, Operand o2) {

    if (o1 instanceof Literal) {
      return o2 instanceof Literal;
    } else if (o1 instanceof Variable) {
      return o2 instanceof Variable;
    } else if (o1 instanceof UnaryOperation) {
      return o2 instanceof UnaryOperation &&
        ((UnaryOperation) o1).getOperator() == ((UnaryOperation) o2).getOperator();
    } else if (o1 instanceof BinaryOperation) {
      return o2 instanceof BinaryOperation &&
        ((BinaryOperation) o1).getOperator() == ((BinaryOperation) o2).getOperator();
    } else if (o1 instanceof FunctionOperation) {
      return o2 instanceof FunctionOperation &&
        ((FunctionOperation) o1).getOperator() == ((FunctionOperation) o2).getOperator();
    } else if (o1 instanceof Matrix) {
      return o2 instanceof Matrix;
    }

    throw new IllegalArgumentException("Unable to verify type sameness: " + o1 + ", " + o2);

  }

  public static boolean sameParent(Collection<Operand> operands) {
    Operand parent = operands.iterator().next().parent;
    for (Operand operand : operands) {
      if (operand.parent != parent || operand.parent == null) return false;
    }
    return true;
  }

  public static void validateTree(Operand operand) {
    recursiveValidateTree(operand, null, new HashSet<>());
  }

  public static void validateTree(Equation equation) {
    validateTree(equation.getLeftSide());
    validateTree(equation.getRightSide());
  }

  private static void recursiveValidateTree(
    Operand child, Operand parent, Set<Integer> visitedIds
  ) {

    if (child.parent != parent) {

      if (parent == null) {
        throw new IllegalArgumentException("Operand to validate must be top level parent");
      } else {

        throw new IllegalStateException("\n\nOperand:\n\t" +
          child.toInfoString() +
          "\nhas parent:\n\t" +
          child.parent.toInfoString() +
          "\nbut its parent should be:\n\t" +
          parent.toInfoString() +
          "\n\n" +
          Utils.toTreeString(parent));
      }

    }

    if (visitedIds.contains(child.id)) {
      throw new IllegalStateException("Operand \"" +
        child +
        "\" with ID " +
        child.id +
        " was used more than once in tree");
    }

    visitedIds.add(child.id);

    if (child.level == -1) {

      System.out.println(child.level);

      throw new IllegalStateException("Operand \"" +
        child +
        "\" level number is not set, ID: " +
        child.getId());
    }

    /*if (visitedOrderNumbers.contains(child.treeNumber)) {
      throw new IllegalStateException(
          "Operand \"" + child + "\" has order number that was used more than once in tree");
    }

    visitedOrderNumbers.add(child.treeNumber);

    if (child.parent == null && child.treeNumber != 0) {
      throw new IllegalStateException(
          "Operand \"" + child + "\" has no parent but has order number of " + child.treeNumber
              + ", should be 0");
    } else if (child.parent != null && child.treeNumber <= child.parent.treeNumber) {
      throw new IllegalStateException(
          "Operand \"" + child + "\" has a lower order number than its parent \"" + child.parent
              + "\"");
    }*/

    if (child.parent == null && child.level != 0) {
      throw new IllegalStateException("Operand \"" +
        child +
        "\" has no parent but has level number of " +
        child.level +
        ", should be 0");
    } else if (child.parent != null && child.level != child.parent.level + 1) {
      throw new IllegalStateException("Operand \"" + child + "\" has an illegal level number\"");
    }

    if (child.children != null) {

      for (Operand c : child.children) {
        recursiveValidateTree(c, child, visitedIds);
      }

    }

  }

  private static Operand[] copyArray(Operand[] array) {
    Operand[] ret = new Operand[array.length];
    for (int i = 0; i < array.length; i++) {
      ret[i] = array[i].copy();
    }
    return ret;
  }

  private static Operand[] replaceCopyArray(Operand[] list, Map<Integer, Operand> replacements) {

    Operand[] result = new Operand[list.length];

    for (int i = 0; i < list.length; i++) {
      result[i] = list[i].recursiveReplaceCopy(replacements);
    }

    return result;
  }

  private static Operand[] replaceCopyArray(Operand[] list, Knowns replacements) {

    Operand[] result = new Operand[list.length];

    for (int i = 0; i < list.length; i++) {
      result[i] = list[i].recursiveReplaceCopy(replacements);
    }

    return result;
  }

  private static Operand condenseBinaryOperation(BinaryOperation operation) {

    BinaryOperator operator = operation.getOperator();

    List<Operand> condensedChildren = new ArrayList<>();

    if (operator.isCommutative()) {

      List<Operand> noVars = new ArrayList<>();

      for (int i = 0; i < operation.childCount(); i++) {

        Operand child = operation.getChild(i);

        if (child.getVariables().isEmpty()) {

          noVars.add(child);

        } else {

          condensedChildren.add(child.condenseLiterals());

        }

      }

      if (!noVars.isEmpty()) {

        Operand noVar;

        if (noVars.size() > 1) {
          noVar = new BinaryOperation(operator, noVars).evaluate();
        } else {
          noVar = noVars.get(0).evaluate();
        }

        condensedChildren.add(0, noVar);
      }

      Operand ret = new BinaryOperation(operator, condensedChildren);

      ret.flatten();

      return ret;

    } else {

      return operation;

    }

  }

  public static boolean isCommutativeBinaryOperation(Operand operand) {

    if (operand instanceof BinaryOperation) {

      BinaryOperation operation = (BinaryOperation) operand;

      return operation.getOperator().isCommutative();

    }

    return false;
  }

  public int variableCount(String variable) {

    if (this instanceof Variable) {

      Variable var = (Variable) this;

      if (var.getName().equals(variable)) {

        return 1;

      } else {

        return 0;

      }

    } else if (hasChildren()) {

      int sum = 0;

      for (Operand child : this) {

        sum += child.variableCount(variable);

      }

      return sum;

    } else {

      return 0;

    }

  }

  public void setVariableDomain(VariableDomain domain) {
    if (getParent() != null) {
      throw new IllegalArgumentException("Operand must be top level parent");
    }
    recursiveSetVariableDomain(domain);
  }

  private void recursiveSetVariableDomain(VariableDomain domain) {

    if (hasChildren()) {

      for (Operand child : this) {

        if (child instanceof Variable) {
          child.setVariableDomain(domain); // defer to variable method
        } else {
          child.recursiveSetVariableDomain(domain);
        }

      }

    }

  }

  public Operand condenseLiterals() {

    Operand ret;

    if (getVariables().isEmpty()) {

      ret = evaluate();

    } else if (this instanceof BinaryOperation) {

      BinaryOperation operation = (BinaryOperation) this;

      if (operation.getOperator().isCommutative()) {

        ret = condenseBinaryOperation(operation);

      } else {

        List<Operand> consolidatedChildren = new ArrayList<>();

        for (Operand child : operation) {

          Operand consolidatedChild = child.condenseLiterals();

          consolidatedChildren.add(consolidatedChild);
        }

        BinaryOperation newOperation = new BinaryOperation(operation.getOperator(),
          consolidatedChildren
        );

        newOperation.flatten();

        ret = newOperation;

      }

    } else {

      ret = this;

    }

    return ret;

  }

  public Operand fixedCopy() {

    Operand copy = copy();

    copy.fixTree();

    return copy;
  }

  public void fixTree() {

    if (parent != null) {
      throw new RuntimeException("fixTree() can only be called on top level parent, parent: " +
        parent.toInfoString());
    }

    recursiveFixTree((short) 0);

    validateTree(this);

  }

  private void recursiveFixTree(short currLevelNumber) {

    this.level = currLevelNumber++;

    if (hasChildren()) {

      for (Operand child : this) {

        child.parent = this;

        child.recursiveFixTree(currLevelNumber);

      }

    }

  }

  public int treeDepth() {
    return recursiveTreeDepth();
  }

  private int recursiveTreeDepth() {

    if (!hasChildren()) {

      return 0;

    } else {

      int max = 0;

      for (Operand child : this) {

        max = Math.max(max, child.treeDepth());

      }

      return max + 1;

    }

  }

  public int treeSize() {

    if (!hasChildren()) {

      return 1;

    } else {

      int sum = 1;

      for (Operand child : this) {
        sum += child.treeSize();
      }

      return sum;

    }

  }

  @Override
  public Iterator<Operand> iterator() {
    return new ChildIterator(this);
  }

  public Operand replace(int replaceId, Operand replaceWith) {

    if (id == replaceId) {
      return replaceWith;
    }

    if (children != null) {
      for (int i = 0; i < childCount(); i++) {
        changeChild(i, getChild(i).replace(replaceId, replaceWith));
      }

      flatten();

    }

    return this;

  }

  public Operand replace(String variable, Operand replaceWith) {

    if (this instanceof Variable) {

      Variable var = (Variable) this;

      if (var.getName().equals(variable)) {
        return replaceWith;
      }

    }

    if (children != null) {
      for (int i = 0; i < childCount(); i++) {
        changeChild(i, getChild(i).replace(variable, replaceWith));
      }

      flatten();

    }

    return this;

  }

  public Operand replace(Map<Integer, Operand> replacements) {

    if (replacements.containsKey(id)) {
      return replacements.get(id);
    }

    if (children != null) {
      for (int i = 0; i < childCount(); i++) {
        changeChild(i, getChild(i).replace(replacements));
      }

      flatten();

    }


    return this;

  }

  public Operand replace(Knowns replacements) {

    if (replacements.hasGeneralMapping(this)) {

      return replacements.getGeneralMapping(this).copy();

    }

    if (children != null) {

      for (int i = 0; i < childCount(); i++) {
        changeChild(i, getChild(i).replace(replacements));
      }

      flatten();

    }

    return this;

  }

  public Operand replaceCopy(Map<Integer, Operand> replacements) {

    Operand replaced = recursiveReplaceCopy(replacements);
    replaced.fixTree();

    return replaced;
  }

  public Operand replaceCopy(int replaceId, Operand replaceWith) {

    Operand replaced = recursiveReplaceCopy(replaceId, replaceWith);
    replaced.fixTree();

    return replaced;
  }

  public Operand replaceCopy(Knowns replacements) {

    Operand replaced = recursiveReplaceCopy(replacements);
    replaced.fixTree();

    return replaced;
  }

  /*public boolean containsMatrices() {

    if (!isScalar())
      return true;

    if (hasChildren()) {

      for (Operand child : children) {
        if (!child.isScalar())
          return true;
      }

    }

    return false;

  }*/

  private Operand recursiveReplaceCopy(Map<Integer, Operand> replacements) {

    if (replacements.containsKey(id)) {
      return replacements.get(id).copy();
    }

    if (hasChildren()) {

      Operand shallow = shallowCopy();

      shallow.setChildren(replaceCopyArray(children, replacements));

      return shallow;

    } else {
      return copy();
    }

  }

  private Operand recursiveReplaceCopy(Integer replaceId, Operand replaceWith) {

    Map<Integer, Operand> replacements = new HashMap<>();

    replacements.put(replaceId, replaceWith);

    return recursiveReplaceCopy(replacements);

  }

  private Operand recursiveReplaceCopy(Knowns replacements) {

    if (replacements.hasGeneralMapping(this)) {
      return replacements.getGeneralMapping(this).copy();
    }

    if (hasChildren()) {

      Operand shallow = shallowCopy();

      shallow.setChildren(replaceCopyArray(children, replacements));

      return shallow;

    } else {
      return copy();
    }

  }

  public double computeToDouble() throws UnsupportedOperationException {

    Operand evaluated = evaluate();

    if (evaluated instanceof Literal) {

      Literal literal = (Literal) evaluated;

      return literal.getValue();

    } else {

      throw new UnsupportedOperationException("Unable to compute double value for class " +
        evaluated.getClass().getName());

    }

  }

  public Operand evaluate() {
    throw new UnsupportedOperationException("evaluate() not implemented for " +
      this.getClass().getName());
  }

  public Operand importVariables(VariableDomain domain) {

    Map<Integer, Operand> map = getVariableReplaceMap(this, domain);

    return replaceCopy(map);

  }

  public Operand replaceMap(Map<String, Operand> variables) {

    Operand ret = recursiveReplaceMap(variables);

    ret.fixTree();

    return ret;

  }

  public Operand recursiveReplaceMap(Map<String, Operand> variables) {

    if (this instanceof Variable) {

      Variable variable = (Variable) this;

      if (variables.containsKey(variable.getName())) {
        return variables.get(variable.getName()).copy();
      }

    } else if (hasChildren()) {

      for (int i = 0; i < childCount(); i++) {

        changeChild(i, getChild(i).recursiveReplaceMap(variables));

      }

    }

    return this;

  }

  public boolean hasNoVariables() {
    return !hasVariables();
  }

  public boolean hasVariables() {

    if (this instanceof Variable) {
      return true;
    }

    if (hasChildren()) {

      for (Operand child : this) {

        if (child.hasChildren()) {
          return true;
        }

      }

    }

    return false;

  }

  public boolean hasChildren() {
    return childCount() > 0;
  }

  public int childCount() {
    return children == null ? 0 : children.length;
  }

  public boolean canEvaluate() {
    return true;
  }

  public Class<?> returnType() {
    return Operand.class;
  }

  public Operand getParent() {
    return parent;
  }

  public int getId() {
    return id;
  }

  public short getLevel() {
    return level;
  }

  protected abstract Operand shallowCopy();

  public Operand copy() {

    Operand ret = shallowCopy();

    if (ret.hasChildren()) {
      throw new IllegalStateException("Shallow copy of operand: \"" +
        this +
        "\" already has children, should be null");
    }

    if (hasChildren()) {
      ret.setChildren(copyArray(children));
    }

    return ret;

  }

  public Set<String> getVariables() {
    Set<String> vars = new HashSet<>();

    if (this instanceof Variable) {
      vars.add(((Variable) this).getName());
    }

    if (children == null) {
      return vars;
    }
    for (Operand child : children) {
      vars.addAll(child.getVariables());
    }
    return vars;
  }

  public boolean treeContainsSimilar(Operand operand) {

    if (operand == null) {
      throw new IllegalArgumentException("Operand to check for cannot be null");
    }

    if (children == null) {

      return equals(operand);

    } else {

      for (Operand child : children) {

        if (child.treeContainsSimilar(operand)) {
          return true;
        }

      }

      return false;

    }

  }

  public Operand getChild(int i) {
    return children[i];
  }

  public boolean hasChildInstance(Operand operand) {

    if (!hasChildren()) {
      return false;
    }

    for (Operand child : children) {
      if (child == operand) return true;
    }

    return false;

  }

  public String toInfoString() {
    return this + " {Lvl: " + level + ", ID: " + Integer.toHexString(id) + "}";
  }

  public String toIdString() {
    return this + " #" + Integer.toHexString(id);
  }

  public String toTreeString() {
    return Utils.toTreeString(this);
  }

  public boolean childrenEquals(Operand operand) {

    if (children == null) {
      return operand.children == null;
    }

    return Arrays.equals(children, operand.children);
  }

  public boolean isScalar() {
    return !(this instanceof Matrix);
  }

  protected int childrenHashCode() {
    return Arrays.hashCode(children);
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException("hashCode() not implemented for " +
      this.getClass().getName());
  }

  @Override
  public boolean equals(Object o) {
    throw new UnsupportedOperationException("equals() not implemented for " +
      this.getClass().getName());
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("toString() not implemented for " +
      this.getClass().getName());
  }

  protected void setChildren(List<Operand> children) {

    if (hasChildren()) {
      throw new IllegalStateException("Operand already has children: " + this);
    }

    this.children = childArray(children);

    for (Operand child : this) {
      child.parent = this;
    }

    flatten();

  }

  private static Operand[] childArray(List<Operand> childList) {
    return childList.toArray(new Operand[0]);
  }

  protected void flatten() {

    if (children == null || !(this instanceof BinaryOperation)) {
      return;
    }

    BinaryOperator operator = ((BinaryOperation) this).getOperator();

    if (!operator.isCommutative()) {
      return;
    }

    List<Operand> newChildren = new ArrayList<>();

    for (Operand operand : children) {

      if (operand instanceof BinaryOperation) {

        BinaryOperation operation = (BinaryOperation) operand;

        if (operation.getOperator() == operator) {

          for (Operand child : operation) {
            newChildren.add(child);
            child.parent = this;
          }

        } else {

          newChildren.add(operand);

        }

      } else {

        newChildren.add(operand);

      }

    }

    children = childArray(newChildren);

  }

  protected void setChildren(Operand... children) {

    if (hasChildren()) {
      throw new IllegalStateException("Operand already has children: " + this);
    }

    this.children = children;

    for (Operand child : this) {
      child.parent = this;
    }

    flatten();

  }

  protected void setChild(Operand child) {

    if (hasChildren()) {
      throw new IllegalStateException("Operand already has children: " + this);
    }

    this.children = childArray(child);

    child.parent = this;

  }

  private static Operand[] childArray(Operand singleChild) {

    Operand[] arr = new Operand[1];

    arr[0] = singleChild;

    return arr;

  }

  private void changeChild(int i, Operand newChild) {
    children[i] = newChild;
    newChild.parent = this;
  }



  /*public abstract JsonObject toShallowJson();

  public JsonObject toJson() {

    JsonObject object = toShallowJson();

    object.addProperty("type", getType().toString());

    if(hasChildren()) {

      JsonArray childrenArray = new JsonArray();

      for(Operand child : this) {

        childrenArray.add(child.toJson());

      }

      object.add("children", childrenArray);

    }

    return object;

  }*/



  public static class ChildIterator implements Iterator<Operand> {

    private Operand operand;
    private int i = 0;

    private ChildIterator(Operand operand) {
      this.operand = operand;
    }

    @Override
    public boolean hasNext() {
      return i < operand.childCount();
    }

    @Override
    public Operand next() {
      return operand.getChild(i++);
    }

  }

}

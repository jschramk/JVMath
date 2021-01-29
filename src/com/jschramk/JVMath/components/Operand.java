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

  protected int levelNumber = -1, id = NEXT_ID++;
  protected Operand parent;
  private Operand[] children;

  private static Map<Class<?>, Integer> setupPriorities(Class<?>... order) {
    Map<Class<?>, Integer> priorities = new HashMap<>();
    for (int i = 0; i < order.length; i++) {
      int pri = order.length - i;
      System.out.println(pri);
      priorities.put(order[i], pri);
    }
    return priorities;
  }

  public static boolean collectionCheck(Iterable<Operand> collection, boolean requireAll,
      CollectionBoolean collectionBoolean) {

    for (Operand o : collection) {

      if (collectionBoolean.check(o)) {
        if (!requireAll) {
          return true;
        }
      } else {
        if (requireAll) {
          return false;
        }
      }

    }

    return requireAll;

  }

  public static List<Operand> collectionCreate(Iterable<Operand> collection,
      CollectionCreate collectionCreate) {

    List<Operand> result = new ArrayList<>();

    for (Operand o : collection) {

      result.add(collectionCreate.makeFrom(o));

    }

    return result;

  }

  private static Map<Integer, Operand> getVariableReplaceMap(Operand operand,
      VariableDomain domain) {

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

    if (needsParentheses(parent, child)) {
      return "(" + child.toString() + ")";
    } else {
      return child.toString();
    }

  }

  public static boolean sameType(Operand o1, Operand o2) {

    if (o1 instanceof Literal) {
      return o2 instanceof Literal;
    } else if (o1 instanceof Variable) {
      return o2 instanceof Variable;
    } else if (o1 instanceof UnaryOperation) {
      return o2 instanceof UnaryOperation
          && ((UnaryOperation) o1).getOperator() == ((UnaryOperation) o2).getOperator();
    } else if (o1 instanceof BinaryOperation) {
      return o2 instanceof BinaryOperation
          && ((BinaryOperation) o1).getOperator() == ((BinaryOperation) o2).getOperator();
    } else if (o1 instanceof FunctionOperation) {
      return o2 instanceof FunctionOperation
          && ((FunctionOperation) o1).getOperator() == ((FunctionOperation) o2).getOperator();
    } else if (o1 instanceof Matrix) {
      return o2 instanceof Matrix;
    }

    throw new IllegalArgumentException("Unable to verify type sameness: " + o1 + ", " + o2);

  }

  public static boolean sameParent(Collection<Operand> operands) {
    Operand parent = operands.iterator().next().parent;
    for (Operand operand : operands) {
      if (operand.parent != parent || operand.parent == null)
        return false;
    }
    return true;
  }

  public static String toInfoString(Collection<Operand> operands) {

    StringBuilder s = new StringBuilder();

    s.append("[");

    int start = s.length();
    for (Operand operand : operands) {
      if (s.length() > start)
        s.append(", ");
      s.append(operand.toInfoString());
    }

    s.append("]");
    return s.toString();
  }

  private static boolean hasValidTree(Operand operand) {
    try {
      validateTree(operand);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static void validateTree(Operand operand) {
    recursiveValidateTree(operand, null, new HashSet<>());
  }

  public static void validateTree(Equation equation) {
    validateTree(equation.getLeftSide());
    validateTree(equation.getRightSide());
  }

  private static void recursiveValidateTree(Operand child, Operand parent,
      Set<Integer> visitedIds) {

    if (child.parent != parent) {

      if (parent == null) {
        throw new IllegalArgumentException("Operand to validate must be top level parent");
      } else {

        throw new IllegalStateException(
            "\n\nOperand:\n\t" + child.toInfoString() + "\nhas parent:\n\t" + child.parent
                .toInfoString() + "\nbut its parent should be:\n\t" + parent.toInfoString() + "\n\n"
                + Utils.toTreeString(parent));
      }

    }

    if (visitedIds.contains(child.id)) {
      throw new IllegalStateException(
          "Operand \"" + child + "\" with ID " + child.id + " was used more than once in tree");
    }

    visitedIds.add(child.id);

    /*if (child.treeNumber == -1) {

      throw new IllegalStateException(
          "Operand \"" + child + "\" order number is not set, ID: " + child.getId());
    }*/

    if (child.levelNumber == -1) {

      throw new IllegalStateException(
          "Operand \"" + child + "\" level number is not set, ID: " + child.getId());
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

    if (child.parent == null && child.levelNumber != 0) {
      throw new IllegalStateException(
          "Operand \"" + child + "\" has no parent but has level number of " + child.levelNumber
              + ", should be 0");
    } else if (child.parent != null && child.levelNumber != child.parent.levelNumber + 1) {
      throw new IllegalStateException("Operand \"" + child + "\" has an illegal level number\"");
    }

    if (child.children != null) {

      for (Operand c : child.children) {
        recursiveValidateTree(c, child, visitedIds);
      }

    }

  }

  public static boolean needsParentheses(Operand parent, Operand child) {
    return needsParentheses(parent, child, true);
  }

  public static boolean needsParentheses(Operand parent, Operand child, boolean needsOnEquals) {

    if (needsOnEquals) {
      return typePriority(child.getType()) <= typePriority(parent.getType());
    } else {
      return typePriority(child.getType()) < typePriority(parent.getType());
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

  private static Operand[] childArray(List<Operand> childList) {
    return childList.toArray(new Operand[0]);
  }

  private static Operand[] childArray(Operand singleChild) {

    Operand[] arr = new Operand[1];

    arr[0] = singleChild;

    return arr;

  }

  private static int typePriority(Type type) {

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

        BinaryOperation newOperation =
            new BinaryOperation(operation.getOperator(), consolidatedChildren);

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
      throw new RuntimeException(
          "fixTree() can only be called on top level parent, parent: " + parent.toInfoString());
    }

    recursiveFixTree(0, 0);

    validateTree(this);

  }

  private int recursiveFixTree(int currOrderNumber, int currLevelNumber) {

    //this.treeNumber = currOrderNumber++;
    this.levelNumber = currLevelNumber;

    if (hasChildren()) {

      for (Operand child : this) {

        child.parent = this;

        currOrderNumber = child.recursiveFixTree(currOrderNumber, currLevelNumber + 1);

      }

    }

    return currOrderNumber;

  }

  public int treeSize() {
    return recursiveTreeSize();
  }

  private int recursiveTreeSize() {

    if (!hasChildren()) {

      return 1;

    } else {

      int sum = 1;

      for (Operand child : this) {
        sum += child.recursiveTreeSize();
      }

      return sum;

    }

  }

  @Override public Iterator<Operand> iterator() {
    return new ChildIterator(this);
  }

  public Set<Integer> getChildIds() {
    Set<Integer> ids = new HashSet<>();
    for (Operand child : this) {
      ids.add(child.id);
    }
    return ids;
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

    if (replacements.containsGeneral(this)) {

      return replacements.getGeneral(this).copy();

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

  public boolean containsMatrices() {

    if (!isScalar())
      return true;

    if (hasChildren()) {

      for (Operand child : children) {
        if (!child.isScalar())
          return true;
      }

    }

    return false;

  }

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

    if (replacements.containsGeneral(this)) {
      return replacements.getGeneral(this).copy();
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

      throw new UnsupportedOperationException(
          "Unable to compute double value for class " + evaluated.getClass().getName());

    }

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

  public boolean canEvaluate() {
    return true;
  }

  public Class<?> returnType() {
    return Operand.class;
  }

  public Operand evaluate() {
    throw new UnsupportedOperationException(
        "evaluate() not implemented for " + this.getClass().getName());
  }

  public Operand getParent() {
    return parent;
  }

  public int getId() {
    return id;
  }

  public int getLevelNumber() {
    return levelNumber;
  }

  protected abstract Operand shallowCopy();

  public Operand copy() {

    Operand ret = shallowCopy();

    if (ret.hasChildren()) {
      throw new IllegalStateException(
          "Shallow copy of operand: \"" + this + "\" already has children, should be null");
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

  public int childCount() {
    return children == null ? 0 : children.length;
  }

  public boolean hasChildren() {
    return childCount() > 0;
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

    if (children == null) {
      return false;
    }

    for (Operand child : children) {
      if (child == operand)
        return true;
    }

    return false;

  }

  @Override public String toString() {
    throw new UnsupportedOperationException(
        "toString() not implemented for " + this.getClass().getName());
  }

  public String toInfoString() {
    return this + " {Lvl: " + levelNumber + ", ID: " + Integer.toHexString(id) + "}";
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

  @Override public boolean equals(Object o) {
    throw new UnsupportedOperationException(
        "equals() not implemented for " + this.getClass().getName());
  }

  @Override public int hashCode() {
    throw new UnsupportedOperationException(
        "hashCode() not implemented for " + this.getClass().getName());
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

  private void changeChild(int i, Operand newChild) {
    children[i] = newChild;
    newChild.parent = this;
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

  public abstract Type getType();

  public enum Type {
    VARIABLE, LITERAL, EXPONENT, FUNCTION, SUM, PRODUCT, DIVISION, NEGATION, MATRIX, FACTORIAL, CONSTANT
  }


  public interface CollectionCreate {
    Operand makeFrom(Operand o);
  }


  public interface CollectionBoolean {
    boolean check(Operand o);
  }


  public static class ChildIterator implements Iterator<Operand> {

    private Operand operand;
    private int i = 0;

    public ChildIterator(Operand operand) {
      this.operand = operand;
    }

    @Override public boolean hasNext() {
      return i < operand.childCount();
    }

    @Override public Operand next() {
      return operand.getChild(i++);
    }

  }

}

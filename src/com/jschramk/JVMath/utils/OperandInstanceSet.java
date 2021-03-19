package com.jschramk.JVMath.utils;

import com.jschramk.JVMath.components.Operand;

import java.util.*;

public class OperandInstanceSet implements Set<Operand> {

  private Map<Integer, Operand> operandMap = new HashMap<>();

  @Override
  public int size() {
    return operandMap.size();
  }

  @Override
  public boolean isEmpty() {
    return operandMap.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    if (!(o instanceof Operand)) {
      return false;
    }
    Operand operand = (Operand) o;
    return operandMap.containsKey(operand.getId());
  }

  @Override
  public Iterator<Operand> iterator() {
    return operandMap.values().iterator();
  }

  @Override
  public Object[] toArray() {
    return operandMap.values().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return operandMap.values().toArray(a);
  }

  @Override
  public boolean add(Operand operand) {
    return operandMap.put(operand.getId(), operand) == null;
  }

  @Override
  public boolean remove(Object o) {
    if (!(o instanceof Operand)) {
      return false;
    }
    Operand operand = (Operand) o;
    return operandMap.remove(operand.getId()) != null;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object o : c) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends Operand> c) {

    boolean allNew = true;
    for (Operand op : c) {
      if (!add(op)) {
        allNew = false;
      }
    }

    return allNew;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
    //return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
    //return false;
  }

  @Override
  public void clear() {
    operandMap.clear();
  }

  @Override
  public int hashCode() {
    return Objects.hash(operandMap);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OperandInstanceSet)) return false;
    OperandInstanceSet operands = (OperandInstanceSet) o;
    return Objects.equals(operandMap, operands.operandMap);
  }

}

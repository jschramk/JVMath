package com.jschramk.JVMath.rewrite_engine;

import com.jschramk.JVMath.components.Operand;

import java.util.*;

public class OperandSet implements Set<Operand> {

  private Map<Integer, Operand> map = new HashMap<>(10, 0.8f);

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean contains(Object o) {

    if (o instanceof Operand) {

      Operand op = (Operand) o;

      return map.containsKey(op.getId());

    }

    return false;
  }

  @Override
  public Iterator<Operand> iterator() {
    return map.values().iterator();
  }

  @Override
  public Object[] toArray() {
    return map.values().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return map.values().toArray(a);
  }

  @Override
  public boolean add(Operand operand) {
    return map.put(operand.getId(), operand) == null;
  }

  @Override
  public boolean remove(Object o) {

    if (o instanceof Operand) {

      Operand op = (Operand) o;

      return map.remove(op.getId()) != null;

    }

    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends Operand> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public int hashCode() {
    return Objects.hash(map);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OperandSet)) return false;
    OperandSet operands = (OperandSet) o;
    return Objects.equals(map, operands.map);
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();

    s.append("{");

    int length = s.length();

    for (Operand op : this) {

      if (s.length() > length) {
        s.append(",");
      }

      s.append(op.toIdString());

    }

    s.append("}");

    return s.toString();

  }

}

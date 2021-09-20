package com.jschramk.JVMath.runtime.utils;

import java.util.*;

public class IdentityHashSet<T> implements Set<T> {

  private final Set<T> set = Collections.newSetFromMap(new IdentityHashMap<>());

  public IdentityHashSet(Set<T> set) {
    addAll(set);
  }

  public IdentityHashSet() {

  }

  @Override
  public int hashCode() {
    return Objects.hash(set);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IdentityHashSet<?> that = (IdentityHashSet<?>) o;
    return Objects.equals(set, that.set);
  }

  @Override
  public String toString() {
    return set.toString();
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return set.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return set.iterator();
  }

  @Override
  public Object[] toArray() {
    return set.toArray();
  }

  @Override
  public <T1> T1[] toArray(T1[] a) {
    return set.toArray(a);
  }

  @Override
  public boolean add(T t) {
    return set.add(t);
  }

  @Override
  public boolean remove(Object o) {
    return set.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return set.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    return set.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return set.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return set.removeAll(c);
  }

  @Override
  public void clear() {
    set.clear();
  }

}

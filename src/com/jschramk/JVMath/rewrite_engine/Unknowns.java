package com.jschramk.JVMath.rewrite_engine;

import com.jschramk.JVMath.components.BinaryOperation;
import com.jschramk.JVMath.components.BinaryOperator;
import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.components.Variable;
import com.jschramk.JVMath.utils.IdentityHashSet;

import java.util.*;

public class Unknowns {

  private static final int MAX_ITERATIONS = 20;
  private static final boolean DEBUG = false;
  private final Map<Operand, Unknown> unknownMap = new HashMap<>();

  private static boolean allSameOperand(Collection<Operand> operands) {
    Iterator<Operand> iterator = operands.iterator();
    Operand first = iterator.next();
    while (iterator.hasNext()) {
      if (!iterator.next().equals(first)) {
        return false;
      }
    }
    return true;
  }

  private static boolean singleVariable(Collection<Operand> operands) {

    if (operands.size() != 1)
      return false;

    return operands.iterator().next() instanceof Variable;

  }

  public void add(Operand unknown, Operand... choices) {
    Unknown u;
    if (unknownMap.containsKey(unknown)) {
      u = unknownMap.get(unknown);
    } else {
      u = new Unknown();
      unknownMap.put(unknown, u);
    }
    u.add(unknown, choices);
  }

  //TODO: make sure this works
  public void remove(Operand unknown, Operand choice) {
    if (unknownMap.containsKey(unknown) && unknownMap.get(unknown).instances.containsKey(unknown)) {
      unknownMap.get(unknown).instances.get(unknown).removeIf(operand -> operand.equals(choice));
    } else
      add(unknown);
  }

  public void removeKnown(Knowns knowns) {
    Iterator<Map.Entry<Operand, Unknown>> iterator = unknownMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Operand, Unknown> entry = iterator.next();
      entry.getValue().instances.entrySet().removeIf(operandIdentityHashSetEntry -> knowns
          .containsInstance(operandIdentityHashSetEntry.getKey()));
      if (entry.getValue().instances.isEmpty()) {
        iterator.remove();
      }
    }
  }

  public void removeUsed(Knowns knowns) {
    for (Map.Entry<Operand, Unknown> entry : unknownMap.entrySet()) {
      for (Map.Entry<Operand, IdentityHashSet<Operand>> instance : entry.getValue().instances
          .entrySet()) {
        instance.getValue().removeIf(operand -> knowns.getUsedIds().contains(operand.getId()));
      }
    }
  }

  public void removeImpossible(Knowns knowns) {

    for (Map.Entry<Operand, Unknown> entry : unknownMap.entrySet()) {
      for (Map.Entry<Operand, IdentityHashSet<Operand>> instance : entry.getValue().instances
          .entrySet()) {

        Operand unknown = instance.getKey();

        instance.getValue().removeIf(choice -> {

          boolean knownMismatch =
              knowns.containsGeneral(unknown) && !knowns.getGeneral(unknown).equals(choice);

          Operand parent = unknown.getParent();

          boolean notChildOfParent = parent != null && knowns.containsInstance(parent) && !knowns
              .getInstance(unknown.getParent()).hasChildInstance(choice);


          //TODO: make sure this works
          boolean notParentOfChild = false;

          if (unknown.hasChildren()) {

            for (Operand child : unknown) {

              if (knowns.containsInstance(child) && !choice
                  .hasChildInstance(knowns.getInstance(child))) {

                notParentOfChild = true;

                //TODO: verify break is ok here
                break;

              }

            }

          }


          if (DEBUG && (knownMismatch || notChildOfParent || notParentOfChild)) {

            System.out.print(unknown.toInfoString() + " ≠ " + choice.toInfoString() + ": ");

            if (knownMismatch) {
              System.out.println(unknown + " = " + knowns.getGeneral(unknown));
            } else if (notChildOfParent) {

              Operand analogParent = knowns.getInstance(unknown.getParent());

              System.out.println(
                  choice.toInfoString() + " ∉ " + analogParent + " " + analogParent.getChildIds());

            } /*else if (notParentOfChild) {}*/

          }

          return knownMismatch || notChildOfParent || notParentOfChild;

        });

      }
    }

  }

  public void applyFilters() {
    for (Map.Entry<Operand, Unknown> entry : unknownMap.entrySet()) {
      entry.getValue().applyIntersectionFilter();
      entry.getValue().applyCountFilter();
    }
  }

  // returns false iff mapping is impossible
  public boolean makeNextChoice(Knowns knowns) {

    Map<IdentityHashSet<Operand>, IdentityHashSet<Operand>> choicesToUnknowns = new HashMap<>();

    for (Map.Entry<Operand, Unknown> entry : unknownMap.entrySet()) {
      for (Map.Entry<Operand, IdentityHashSet<Operand>> instance : entry.getValue().instances
          .entrySet()) {
        if (!choicesToUnknowns.containsKey(instance.getValue())) {
          choicesToUnknowns.put(instance.getValue(), new IdentityHashSet<>());
        }
        choicesToUnknowns.get(instance.getValue()).add(instance.getKey());
      }
    }

    IdentityHashSet<Operand> key = null;
    int minLevelNumber = Integer.MAX_VALUE;
    int minSizeDiff = Integer.MAX_VALUE;

    for (Map.Entry<IdentityHashSet<Operand>, IdentityHashSet<Operand>> entry : choicesToUnknowns
        .entrySet()) {

      int diff = entry.getKey().size() - entry.getValue().size();

      // if there are more unknowns than there are choices, return false
      if (diff < 0)
        return false;

      if (entry.getKey().size() == 1 && entry.getValue().size() == 1) {

        // only one choice, do this next

        key = entry.getKey();
        break;

      } else if (singleVariable(entry.getValue()) && key == null) {

        // there is just one unknown and it is a variable, take note and keep looking
        key = entry.getKey();

      } else if (allSameOperand(entry.getValue()) && entry.getValue().size() > 1) {

        key = entry.getKey();

        break;

      } else if (diff < minSizeDiff) {

        minSizeDiff = diff;
        key = entry.getKey();

        for (Operand unknown : entry.getValue()) {
          if (unknown.getLevelNumber() < minLevelNumber) {
            minLevelNumber = unknown.getLevelNumber();
          }
        }

      } else if (diff == minSizeDiff) {

        for (Operand unknown : entry.getValue()) {

          if (unknown.getLevelNumber() < minLevelNumber) {
            minLevelNumber = unknown.getLevelNumber();
            key = entry.getKey();
          }

        }

      }

    }

    if (DEBUG)
      System.out.println("Min level number: " + minLevelNumber);

    assert key != null;

    if (key.isEmpty()) {
      return false;
    }

    List<Operand> choiceList = new ArrayList<>(key);
    List<Operand> assignList = new ArrayList<>(choicesToUnknowns.get(key));

    if (DEBUG) {
      System.out.println();
      System.out.println("Choice Pool -> Assignment Pool:");
      for (Map.Entry<IdentityHashSet<Operand>, IdentityHashSet<Operand>> entry : choicesToUnknowns
          .entrySet()) {
        System.out.println("\t" + Operand.toInfoString(entry.getKey()) + " -> " + Operand
            .toInfoString(entry.getValue()));
      }
      System.out.println("Next: " + Operand.toInfoString(assignList));
      System.out.println();
    }

    if (assignList.size() > choiceList.size()) {
      return true;
    }

    boolean sameOperand = allSameOperand(assignList) && assignList.size() > 1;

    if (sameOperand) {
      Operand choice = choiceList.get(0);
      choiceList.removeIf(operand -> !operand.equals(choice));
    }

    for (int i = 0; i < assignList.size(); i++) {

      if (i == assignList.size() - 1) {

        if (choiceList.size() == assignList.size() || sameOperand) {
          knowns.add(assignList.get(i), choiceList.get(i));
        } else {

          int end = choiceList.size();
          int start = assignList.size() - 1;

          Operand assign = assignList.get(i);

          List<Operand> leftover = choiceList.subList(start, end);

          if (!Operand.sameParent(leftover)) {
            knowns.add(assignList.get(i), choiceList.get(i));
            continue;
          }

          Operand.Type parentType = leftover.get(0).getParent().getType();

          Operand parent = leftover.get(0).getParent();

          if (assign.getType() != parentType && assign.getType() != Operand.Type.VARIABLE) {
            knowns.add(assign, choiceList.get(i));
            continue;
          }

          if (parent instanceof BinaryOperation
              && ((BinaryOperation) parent).getOperator().getAssociativity()
              == BinaryOperator.Associativity.COMMUTATIVE) {

            Operand operation =
                new BinaryOperation(((BinaryOperation) parent).getOperator(), leftover);

            knowns.add(assignList.get(i), operation);
            knowns.addUsed(leftover);

          } else {
            throw new IllegalArgumentException("Could not create sub-operation");
          }

        }

      } else {

        knowns.add(assignList.get(i), choiceList.get(i));

      }

    }

    return true;

  }

  public boolean isEmpty() {
    return unknownMap.isEmpty();
  }

  // returns false iff mapping is impossible
  public boolean mapAll(Knowns knowns) {

    if (DEBUG) {

      System.out.println("----------------------- START ------------------------");

      knowns.print();
      System.out.println();
      print();

    }

    for (int i = 0; i < MAX_ITERATIONS; i++) {

      removeUsed(knowns);
      removeKnown(knowns);
      removeImpossible(knowns);
      applyFilters();

      if (DEBUG) {

        System.out.println("Intermediate:");

        print();
      }

      if (isEmpty())
        break;

      if (!makeNextChoice(knowns)) {
        return false;
      }

      if (DEBUG) {
        removeUsed(knowns);
        removeKnown(knowns);
        removeImpossible(knowns);
        applyFilters();

        System.out.println("-------------------- i = " + i + " RESULT --------------------");
        knowns.print();
        System.out.println();
        print();
      }

    }

    if (!isEmpty()) {
      throw new RuntimeException("Unable to compute or reject mapping within iteration limit");
    }

    return true;

  }

  public void print() {
    System.out.println("Unknowns:");
    if (unknownMap.isEmpty()) {
      System.out.println("none");
    } else {
      for (Map.Entry<Operand, Unknown> entry : unknownMap.entrySet()) {
        System.out.println(entry.getKey() + ": " + entry.getValue());
      }
    }
  }

  public static class Unknown {

    private final IdentityHashMap<Operand, IdentityHashSet<Operand>> instances =
        new IdentityHashMap<>();

    public void add(Operand instance, Operand... choices) {
      IdentityHashSet<Operand> choiceSet;
      if (instances.containsKey(instance)) {
        choiceSet = instances.get(instance);
      } else {
        choiceSet = new IdentityHashSet<>();
        instances.put(instance, choiceSet);
      }
      choiceSet.addAll(Arrays.asList(choices));
    }

    @Override public String toString() {
      StringBuilder s = new StringBuilder();

      for (Map.Entry<Operand, IdentityHashSet<Operand>> entry : instances.entrySet()) {

        if (s.length() > 0) {
          s.append(", ");
        }

        s.append('#');
        s.append(entry.getKey().getId());
        s.append(" = ");
        s.append(Operand.toInfoString(entry.getValue()));

      }

      return s.toString();
    }

    public void applyIntersectionFilter() {
      Set<Operand> shared = new HashSet<>();
      if (!instances.isEmpty()) {
        shared.addAll(instances.values().iterator().next());
      } else {
        System.out.println("EMPTY INSTANCES");
      }

      // find all shared values
      for (Map.Entry<Operand, IdentityHashSet<Operand>> entry : instances.entrySet()) {

        Set<Operand> general = new HashSet<>(entry.getValue());

        shared.removeIf(operand -> !general.contains(operand));
      }

      // remove unshared values
      for (Map.Entry<Operand, IdentityHashSet<Operand>> entry : instances.entrySet()) {

        entry.getValue().removeIf(operand -> !shared.contains(operand));

      }

    }

    public void applyCountFilter() {

      Map<Operand, Integer> counts = new HashMap<>();
      Set<Integer> used = new HashSet<>();

      for (Map.Entry<Operand, IdentityHashSet<Operand>> entry : instances.entrySet()) {

        for (Operand choice : entry.getValue()) {

          if (used.contains(choice.getId())) {
            continue;
          }

          if (!counts.containsKey(choice)) {
            counts.put(choice, 0);
          }

          counts.put(choice, counts.get(choice) + 1);
          used.add(choice.getId());

        }

      }

      for (Map.Entry<Operand, IdentityHashSet<Operand>> entry : instances.entrySet()) {

        entry.getValue().removeIf(choice -> {


          Operand unknown = entry.getKey();

          int count = counts.get(choice);

          boolean notEnough = count < instances.size();

          if (DEBUG && notEnough) {
            System.out.println(
                unknown.toInfoString() + " ≠ " + choice.toInfoString() + ": " + choice
                    + " only has " + count + " instance(s)");
          }

          return notEnough;

        });

      }

    }

  }

}
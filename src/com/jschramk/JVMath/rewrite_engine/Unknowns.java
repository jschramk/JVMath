package com.jschramk.JVMath.rewrite_engine;

import com.jschramk.JVMath.components.BinaryOperation;
import com.jschramk.JVMath.components.BinaryOperator;
import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.components.Variable;

import java.util.*;

public class Unknowns {

  private static final int UNKNOWN_MAP_INITIAL_CAPACITY = 10;
  private static final float UNKNOWN_MAP_LOAD_FACTOR = 0.8f;

  private static final int MAX_ITERATIONS = 20;
  private final Map<Operand, Choices> unknownMap = new HashMap<>();

  // returns true if all operands in the collection are he same (not the same instance)
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

  // returns true if the collection is a single variable
  private static boolean singleVariable(Collection<Operand> operands) {
    return operands.size() == 1 && operands.iterator().next() instanceof Variable;
  }

  public void add(Operand unknown, Operand... choices) {
    Choices u;
    if (unknownMap.containsKey(unknown)) {
      u = unknownMap.get(unknown);
    } else {
      u = new Choices();
      unknownMap.put(unknown, u);
    }
    u.add(unknown, choices);
  }

  public void remove(Operand unknown, Operand choice) {

    if (unknownMap.containsKey(unknown)) {

      unknownMap.get(unknown).removeChoice(unknown, choice);

    } else {

      // TODO: figure out why this is crucial
      add(unknown);

    }

  }

  public void removeKnown(Knowns knowns) {

    Iterator<Map.Entry<Operand, Choices>> iterator = unknownMap.entrySet().iterator();

    while (iterator.hasNext()) {

      Map.Entry<Operand, Choices> entry = iterator.next();

      entry.getValue().removeKnown(knowns.getMappedIds());

      if (entry.getValue().isEmpty()) {
        iterator.remove();
      }

    }
  }

  public void removeUsed(Knowns knowns) {
    for (Map.Entry<Operand, Choices> entry : unknownMap.entrySet()) {
      entry.getValue().removeUsed(knowns.getUsedIds());
    }
  }

  public void removeImpossible(Knowns knowns) {

    for (Map.Entry<Operand, Choices> entry : unknownMap.entrySet()) {

      for (Map.Entry<Integer, OperandSet> instance : entry.getValue().instanceChoices.entrySet()) {

        int unknownKey = instance.getKey();
        Operand unknown = entry.getValue().getInstance(unknownKey);

        instance.getValue().removeIf(choice -> {

          boolean knownMismatch =
              knowns.hasGeneralMapping(unknown) && !knowns.getGeneralMapping(unknown)
                  .equals(choice);

          Operand parent = unknown.getParent();

          boolean notChildOfParent = parent != null && knowns.hasInstanceMapping(parent) && !knowns
              .getInstanceMapping(parent).hasChildInstance(choice);

          boolean notParentOfChild = false;

          if (unknown.hasChildren()) {

            for (Operand child : unknown) {

              if (knowns.hasInstanceMapping(child) && !choice
                  .hasChildInstance(knowns.getInstanceMapping(child))) {

                notParentOfChild = true;

                break;

              }

            }

          }

          return knownMismatch || notChildOfParent || notParentOfChild;

        });

      }
    }

  }

  public void applyFilters() {
    for (Map.Entry<Operand, Choices> entry : unknownMap.entrySet()) {
      entry.getValue().applyIntersectionFilter();
      entry.getValue().applyCountFilter();
    }
  }

  // returns false iff mapping is impossible
  public boolean makeNextChoice(Knowns knowns) {

    Map<OperandSet, OperandSet> choicesToUnknowns = new HashMap<>();

    for (Map.Entry<Operand, Choices> unknownEntry : unknownMap.entrySet()) {

      Choices operandChoices = unknownEntry.getValue();

      for (Map.Entry<Integer, OperandSet> choicesEntry : operandChoices.instanceChoices
          .entrySet()) {

        Operand instance = operandChoices.getInstance(choicesEntry.getKey());
        OperandSet instanceChoices = choicesEntry.getValue();

        if (!choicesToUnknowns.containsKey(instanceChoices)) {
          choicesToUnknowns.put(instanceChoices, new OperandSet());
        }

        choicesToUnknowns.get(instanceChoices).add(instance);

      }

    }

    OperandSet key = null;
    int minLevelNumber = Integer.MAX_VALUE;
    int minSizeDiff = Integer.MAX_VALUE;

    for (Map.Entry<OperandSet, OperandSet> entry : choicesToUnknowns.entrySet()) {

      int diff = entry.getKey().size() - entry.getValue().size();

      // if there are more unknowns than there are choices, return false
      if (diff < 0) {
        return false;
      }

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
          if (unknown.getLevel() < minLevelNumber) {
            minLevelNumber = unknown.getLevel();
          }
        }

      } else if (diff == minSizeDiff) {

        for (Operand unknown : entry.getValue()) {

          if (unknown.getLevel() < minLevelNumber) {
            minLevelNumber = unknown.getLevel();
            key = entry.getKey();
          }

        }

      }

    }

    if (key == null) {
      throw new RuntimeException("null key");
    }

    if (key.isEmpty()) {

      System.out.println("empty key");

      return false;
    }

    List<Operand> choiceList = new ArrayList<>(key);
    List<Operand> assignList = new ArrayList<>(choicesToUnknowns.get(key));

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
          knowns.putMapping(assignList.get(i), choiceList.get(i));
        } else {

          int end = choiceList.size();
          int start = assignList.size() - 1;

          Operand assign = assignList.get(i);

          List<Operand> leftover = choiceList.subList(start, end);

          if (!Operand.sameParent(leftover)) {
            knowns.putMapping(assignList.get(i), choiceList.get(i));
            continue;
          }

          Operand.Type parentType = leftover.get(0).getParent().getType();

          Operand parent = leftover.get(0).getParent();

          if (assign.getType() != parentType && assign.getType() != Operand.Type.VARIABLE) {
            knowns.putMapping(assign, choiceList.get(i));
            continue;
          }

          if (parent instanceof BinaryOperation
              && ((BinaryOperation) parent).getOperator().getAssociativity()
              == BinaryOperator.Associativity.COMMUTATIVE) {

            Operand operation =
                new BinaryOperation(((BinaryOperation) parent).getOperator(), leftover);

            knowns.putMapping(assignList.get(i), operation);
            knowns.addUsed(leftover);

          } else {
            throw new IllegalArgumentException("Could not create sub-operation");
          }

        }

      } else {

        knowns.putMapping(assignList.get(i), choiceList.get(i));

      }

    }

    return true;

  }

  public boolean isEmpty() {
    return unknownMap.isEmpty();
  }

  // returns false iff mapping is impossible
  public boolean mapAll(Knowns knowns) {

    for (int i = 0; i < MAX_ITERATIONS; i++) {

      removeKnown(knowns);
      removeUsed(knowns);
      removeImpossible(knowns);
      applyFilters();

      if (isEmpty()) {
        break;
      }

      if (!makeNextChoice(knowns)) {
        return false;
      }

    }

    if (!isEmpty()) {
      throw new RuntimeException("Unable to compute or reject mapping within iteration limit");
    }

    return true;

  }

  // class that represents a single operand's possible mappings
  private static class Choices {

    // map of each instance of the operand for this unknown mapped to another map of the possible
    // choices keyed on ID
    private final Map<Integer, OperandSet> instanceChoices =
        new HashMap<>(UNKNOWN_MAP_INITIAL_CAPACITY, UNKNOWN_MAP_LOAD_FACTOR);

    private final Map<Integer, Operand> idsToOperands = new HashMap<>();

    public boolean containsInstance(Operand operand) {
      return instanceChoices.containsKey(operand.getId());
    }

    public Operand getInstance(int id) {
      return idsToOperands.get(id);
    }

    public void removeChoice(Operand instance, Operand choice) {
      if (containsInstance(instance)) {
        // TODO: check if should compare operand itself rather than ID
        instanceChoices.get(instance.getId())
            .removeIf(operand -> operand.getId() == choice.getId());
      }
    }

    public boolean isEmpty() {
      return instanceChoices.isEmpty();
    }

    public void removeUsed(Collection<Integer> ids) {

      for (Map.Entry<Integer, OperandSet> entry : instanceChoices.entrySet()) {

        entry.getValue().removeIf(operand -> ids.contains(operand.getId()));

      }

    }

    public void removeKnown(Collection<Integer> ids) {
      instanceChoices.entrySet()
          .removeIf(integerOperandSetEntry -> ids.contains(integerOperandSetEntry.getKey()));
    }

    public void add(Operand instance, Operand... choices) {

      idsToOperands.put(instance.getId(), instance);

      OperandSet choiceSet;

      if (instanceChoices.containsKey(instance.getId())) {

        choiceSet = instanceChoices.get(instance.getId());

      } else {

        choiceSet = new OperandSet();

        instanceChoices.put(instance.getId(), choiceSet);

      }

      // populate choice map with operands keyed by ID
      for (Operand op : choices) {
        choiceSet.add(op);
      }

    }

    @Override public String toString() {
      StringBuilder s = new StringBuilder();

      for (Map.Entry<Integer, OperandSet> entry : instanceChoices.entrySet()) {

        if (s.length() > 0) {
          s.append(", ");
        }

        s.append(getInstance(entry.getKey()).toIdString());
        s.append(" = ");
        s.append(entry.getValue());

      }

      return s.toString();
    }

    // removes all impossible mappings based on the set difference of all choices for this operand
    public void applyIntersectionFilter() {

      if (instanceChoices.isEmpty()) {
        return;
      }

      // start off by adding all of the choices from one instance to the set
      Set<Operand> shared = new HashSet<>(instanceChoices.values().iterator().next());

      // remove all values from initial set that are not in each instance's choices
      for (Map.Entry<Integer, OperandSet> entry : instanceChoices.entrySet()) {

        Set<Operand> general = new HashSet<>(entry.getValue());

        shared.removeIf(operand -> !general.contains(operand));

      }

      // remove unshared values from each instance's choice map
      for (Map.Entry<Integer, OperandSet> entry : instanceChoices.entrySet()) {

        entry.getValue().removeIf(entry1 -> !shared.contains(entry1));

      }

    }

    // removes all impossible mappings based on the number of instances of each choice relative
    // to the number of instances that need to be mapped
    public void applyCountFilter() {

      Map<Operand, Integer> counts = new HashMap<>();
      Set<Integer> usedIds = new HashSet<>();

      // populate count map with the number of instances of each operand found in the choices for
      // all instances that need to be mapped
      for (Map.Entry<Integer, OperandSet> entry : instanceChoices.entrySet()) {

        for (Operand choice : entry.getValue()) {

          if (usedIds.contains(choice.getId())) {
            continue;
          }

          if (!counts.containsKey(choice)) {
            counts.put(choice, 0);
          }

          counts.put(choice, counts.get(choice) + 1);
          usedIds.add(choice.getId());

        }

      }

      for (Map.Entry<Integer, OperandSet> entry : instanceChoices.entrySet()) {

        entry.getValue().removeIf(operand -> {

          int count = counts.get(operand);

          return count < instanceChoices.size();

        });

      }

    }

  }

}
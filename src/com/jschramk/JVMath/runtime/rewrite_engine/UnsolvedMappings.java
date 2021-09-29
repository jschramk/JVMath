package com.jschramk.JVMath.runtime.rewrite_engine;

import com.jschramk.JVMath.runtime.components.BinaryOperation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.components.Variable;
import util.PerformanceTimer;
import util.PrettyPrinter;

import java.util.*;

public class UnsolvedMappings {

    private static final PerformanceTimer timer = new PerformanceTimer();

    private static final int MAX_ITERATIONS = 20;
    private final Map<Operand, Choices> unknownMap = new HashMap<>();
    private final SolvedMappings solvedMappings;
    private final MappingAssigner mappingAssigner;

    public UnsolvedMappings(SolvedMappings solvedMappings) {
        this.solvedMappings = solvedMappings;
        this.mappingAssigner = new MappingAssigner(solvedMappings, this);
    }

    // returns true if all operands in the collection are the same (not the same instance)
    private static boolean isMultipleSameOperand(Collection<Operand> operands) {

        if (operands.size() < 2) {
            return false;
        }

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
    private static boolean isSingleVariable(Collection<Operand> operands) {
        return operands.size() == 1 && operands.iterator().next() instanceof Variable;
    }

    public void remove(Operand unknown, Operand choice) {

        if (unknownMap.containsKey(unknown)) {

            unknownMap.get(unknown).removeChoice(unknown, choice);

        } else {

            // this is crucial because it allows the mapping to fail in some cases, while still
            // allowing it to pass if another choice is added later
            add(unknown);

        }

    }

    public void add(Operand unknown, Operand... possibleMappings) {

        Choices choices;

        if (unknownMap.containsKey(unknown)) {

            choices = unknownMap.get(unknown);

        } else {

            choices = new Choices();

            unknownMap.put(unknown, choices);

        }

        choices.add(unknown, possibleMappings);
    }

    // returns false if mapping is impossible
    public boolean mapAll() {

        for (int i = 0; i < MAX_ITERATIONS; i++) {

            //System.out.println(assigner);

            removeKnown();
            removeUsed();
            removeImpossible();
            applyFilters();

            if (timer.ms() > 1) {
                timer.printDelta("filters:");
            }

            if (isEmpty()) {
                break;
            }

            if (!mappingAssigner.assignNext()) {
                return false;
            }

        }

        if (!isEmpty()) {
            throw new RuntimeException(
                "Unable to compute or reject mapping within iteration limit");
        }

        if (timer.ms() > 1) {
            timer.printDelta("mapping accepted:");
        }

        return true;

    }

    public void removeKnown() {

        Iterator<Map.Entry<Operand, Choices>> iterator = unknownMap.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<Operand, Choices> entry = iterator.next();

            entry.getValue().removeKnown(solvedMappings.getMappedIds());

            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }

        }

    }

    public void removeUsed() {
        for (Map.Entry<Operand, Choices> entry : unknownMap.entrySet()) {
            entry.getValue().removeUsed(solvedMappings.getUsedIds());
        }
    }

    public void removeImpossible() {

        for (Map.Entry<Operand, Choices> entry : unknownMap.entrySet()) {

            for (Map.Entry<Integer, OperandSet> instance : entry.getValue().instanceChoices
                .entrySet()) {

                int unknownKey = instance.getKey();
                Operand unknown = entry.getValue().getInstance(unknownKey);

                instance.getValue().removeIf(choice -> {

                    boolean c1 = notEqualToGeneralKnown(choice, unknown);

                    boolean c2 = notChildOfKnownParent(choice, unknown);

                    boolean c3 = notParentOfKnownChild(choice, unknown);

                    return c1 || c2 || c3;

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

    public boolean isEmpty() {
        return unknownMap.isEmpty();
    }

    private boolean notEqualToGeneralKnown(Operand choice, Operand unknown) {

        if (!solvedMappings.hasGeneralMapping(unknown)) {
            return false;
        }

        return !solvedMappings.getGeneralMapping(unknown).equals(choice);
    }

    private boolean notChildOfKnownParent(Operand choice, Operand unknown) {

        Operand parent = unknown.getParent();

        if (parent == null) {
            return false;
        }

        if (!solvedMappings.hasInstanceMapping(parent)) {
            return false;
        }

        return !solvedMappings.getInstanceMapping(parent).hasChildInstance(choice);
    }

    private boolean notParentOfKnownChild(Operand choice, Operand unknown) {

        if (unknown.hasChildren()) {

            for (Operand child : unknown) {

                if (!solvedMappings.hasInstanceMapping(child)) {
                    continue;
                }

                if (!choice.hasChildInstance(solvedMappings.getInstanceMapping(child))) {
                    return true;
                }

            }

        }

        return false;

    }



    // class that represents a single operand's possible mappings
    private static class Choices {

        // map of each instance of the operand for this unknown mapped to another map of the possible
        // choices keyed on ID
        private final Map<Integer, OperandSet> instanceChoices = new HashMap<>(10, 0.8f);

        private final Map<Integer, Operand> idsToOperands = new HashMap<>();

        public void removeChoice(Operand instance, Operand choice) {
            if (containsInstance(instance)) {
                // TODO: check if should compare operand itself rather than ID
                instanceChoices.get(instance.getId())
                    .removeIf(operand -> operand.getId() == choice.getId());
            }
        }

        public boolean containsInstance(Operand operand) {
            return instanceChoices.containsKey(operand.getId());
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

        public Operand getInstance(int id) {
            return idsToOperands.get(id);
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



    // class that represents a set of choices that must be mapped to a set of unknowns
    private static class MappingAssigner {

        private SolvedMappings solvedMappings;
        private UnsolvedMappings unsolvedMappings;
        private Map<OperandSet, OperandSet> map = new HashMap<>(10, 0.8f);

        private MappingAssigner(SolvedMappings solvedMappings, UnsolvedMappings unsolvedMappings) {
            this.solvedMappings = solvedMappings;
            this.unsolvedMappings = unsolvedMappings;
            reset();
        }

        private void reset() {

            map.clear();

            for (Map.Entry<Operand, Choices> unknownEntry : unsolvedMappings.unknownMap.entrySet()) {

                Choices operandChoices = unknownEntry.getValue();

                for (Map.Entry<Integer, OperandSet> choicesEntry : operandChoices.instanceChoices
                    .entrySet()) {

                    Operand instance = operandChoices.getInstance(choicesEntry.getKey());
                    OperandSet instanceChoices = choicesEntry.getValue();

                    if (!map.containsKey(instanceChoices)) {
                        map.put(instanceChoices, new OperandSet());
                    }

                    map.get(instanceChoices).add(instance);

                }

            }

        }

        // returns false if mapping is impossible
        private boolean assignNext() {

            reset();

            //System.out.println(this);
            //System.out.println();

            OperandSet key = chooseNextMappingKey();

            if (key == null || key.isEmpty()) {
                return false;
            }

            List<Operand> choiceList = new ArrayList<>(key);
            List<Operand> assignList = new ArrayList<>(map.get(key));

            if (assignList.size() > choiceList.size()) {
                return true;
            }

            assign(choiceList, assignList);

            return true;

        }

        private OperandSet chooseNextMappingKey() {

            OperandSet key = null;
            int minLevelNumber = Integer.MAX_VALUE;
            int minTreeSize = Integer.MAX_VALUE;
            int minSizeDiff = Integer.MAX_VALUE;

            for (Map.Entry<OperandSet, OperandSet> entry : map.entrySet()) {

                OperandSet choices = entry.getKey();
                OperandSet unknowns = entry.getValue();

                int diff = entry.getKey().size() - entry.getValue().size();

                // if there are more unknowns than there are choices, return null
                if (diff < 0) {
                    return null;
                }

                if (choices.size() == 1 && unknowns.size() == 1) {

                    // only one choice, do this next
                    key = entry.getKey();

                    break;

                } else if (isSingleVariable(unknowns) && key == null) {

                    // there is just one unknown and it is a variable, take note and keep looking
                    key = choices;

                } else if (isMultipleSameOperand(unknowns)) {

                    // two of the same operand
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
                        } else if(unknown.getLevel() == minLevelNumber && unknown.treeSize() < minTreeSize) {
                            minTreeSize = unknown.treeSize();
                            key = entry.getKey();
                        }

                    }

                }

            }

            if (key == null) {
                throw new RuntimeException("next mapping key never set");
            }

            //System.out.println("key: " + key);

            return key;

        }

        private void assign(List<Operand> choiceList, List<Operand> assignList) {

            //System.out.println("Assigning: " + choiceList.toString() + " -> " + assignList.toString());

            boolean sameOperand = isMultipleSameOperand(assignList);

            if (sameOperand) {
                Operand choice = choiceList.get(0);
                choiceList.removeIf(operand -> !operand.equals(choice));
            }

            //System.out.print("[");

            for (int i = 0; i < assignList.size(); i++) {

                if (i == assignList.size() - 1) {

                    if (choiceList.size() == assignList.size() || sameOperand) {

                        //System.out.print(assignList.get(i) + " -> " + choiceList.get(i) + ", ");

                        solvedMappings.putMapping(assignList.get(i), choiceList.get(i));

                    } else {

                        int start = i;
                        int end = choiceList.size();

                        Operand assign = assignList.get(i);

                        List<Operand> leftover = choiceList.subList(start, end);

                        //System.out.println("leftover: " + leftover);

                        if (!Operand.sameParent(leftover)) {

                            solvedMappings.putMapping(assignList.get(i), choiceList.get(i));

                            continue;

                        }

                        Operand parent = leftover.get(0).getParent();

                        if (!Operand.sameType(assign, parent) && !(assign instanceof Variable)) {

                            //System.out.print(assignList + " -> " + choiceList.get(i) + ", ");

                            solvedMappings.putMapping(assign, choiceList.get(i));

                            continue;

                        }

                        if (Operand.isCommutativeBinaryOperation(parent)) {

                            BinaryOperation parentOperation = (BinaryOperation) parent;

                            Operand operation =
                                new BinaryOperation(parentOperation.getOperator(), leftover);

                            //System.out.println("Parent is commutative, new operation: " + operation);

                            //System.out.print(assignList.get(i) + " -> " + operation + ", ");

                            solvedMappings.putMapping(assignList.get(i), operation);
                            solvedMappings.addUsed(leftover);

                        } else {

                            throw new RuntimeException("Could not create sub-operation");

                        }

                        // TODO: verify this works
                        //knowns.putMapping(assignList.get(i), leftover.get(0));
                        //System.out.print(assignList.get(i) + " -> " + choiceList.get(i) + ", ");


                        //break;

                    }

                } else {

                    //System.out.print(assignList.get(i) + " -> " + choiceList.get(i) + ", ");

                    solvedMappings.putMapping(assignList.get(i), choiceList.get(i));

                }



            }

            //System.out.println("]");

        }

        @Override public String toString() {

            PrettyPrinter p = new PrettyPrinter();
            p.setSpace(3);

            for (Map.Entry<OperandSet, OperandSet> entry : map.entrySet()) {

                p.row(entry.getKey());
                p.col("->");
                p.col(entry.getValue());

            }

            return p.toString();

        }

    }

}

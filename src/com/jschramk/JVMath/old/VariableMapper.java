package core.old;

import core.components.Operand;
import core.components.Variable;
import core.utils.IdentityHashSet;
import core.utils.Utils;

import java.util.*;
import java.util.function.Predicate;

import static core.components.Operand.Type.PRODUCT;
import static core.components.Operand.Type.SUM;

@Deprecated
public class VariableMapper {

    private static final boolean DEBUG = true;
    private static final int MAX_ITERATIONS = 10;

    private final IdentityHashMap<Operand, IdentityHashSet<Operand>> possibleAnalogs =
            new IdentityHashMap<>();

    private Map<String, Operand> variableMap;


    private static boolean removeImpossibleChoices(IdentityHashMap<Operand, Operand> knowns,
                                                   IdentityHashMap<Operand, Unknown> unknowns, Set<Integer> usedIds) {
        for (Operand unknown : unknowns.keySet()) {

            Set<Operand> possibleValues = unknowns.get(unknown).getChoices();

            possibleValues.removeIf(new Predicate<Operand>() {
                @Override
                public boolean test(Operand possible) {

                    print("checking whether " + possible + " is a valid value for " + unknown);

                    boolean notPossible =
                            unknown.getParent() != null && knowns.containsKey(unknown.getParent()) && !knowns
                                    .get(unknown.getParent()).hasChildWithId(possible.getInstanceId());

                    if (notPossible) {
                        print(possible + " is not a possible value for " + unknown);
                    }

                    //print("used: "+usedIds.contains(operand.getInstanceId()));

                    return usedIds.contains(possible.getInstanceId()) || notPossible;
                }
            });

            if (possibleValues.isEmpty()) {
                print("no possible values for " + unknown);
                return true;
            }

        }

        return false;

    }

    private static boolean makeGroupChoice(
            IdentityHashMap<Operand, Operand> result,
            Set<Operand> mapThese,
            Set<Operand> toThese,
            Set<Integer> usedIds
    ) {

        if (mapThese.size() > toThese.size()) {
            return true;
        }

        if (mapThese.size() == toThese.size()) {

            Iterator<Operand> mapTheseIterator = mapThese.iterator();
            Iterator<Operand> toTheseIterator = toThese.iterator();

            while (mapTheseIterator.hasNext()) {

                Operand mapThis = mapTheseIterator.next();
                Operand toThis = toTheseIterator.next();

                result.put(mapThis, toThis);

                //usedIds.add(toThis.getInstanceId());

            }

        } else {

            int sizeDiff = toThese.size() - mapThese.size();

            List<Operand> chooseFrom = new ArrayList<>(toThese);

            Set<Operand> matchGroup = new IdentityHashSet<>();

            for (int i = 0; i < chooseFrom.size(); i++) {

                Operand matchTo = chooseFrom.get(i);

                matchGroup.add(matchTo);

                for (int j = 0; j < chooseFrom.size(); j++) {

                    if (j == i) continue;

                    Operand curr = chooseFrom.get(j);

                    if (curr.getParent() == matchTo.getParent()) {
                        matchGroup.add(curr);
                        if (matchGroup.size() == sizeDiff + 1) {
                            break;
                        }
                    }

                }

                if (matchGroup.size() == sizeDiff + 1) {
                    break;
                }

            }

            if (matchGroup.size() == sizeDiff + 1) {

                System.out.println("Match group: " + matchGroup);

            }


        }

        return false;
    }

    private static IdentityHashMap<Operand, Operand> makeArbitraryChoices(
            IdentityHashMap<Operand, Unknown> unknowns, IdentityHashMap<Operand, Operand> knowns,
            boolean allowSubMatch) {

        Map<Integer, Operand> idsToOperands = new HashMap<>();
        HashMap<Set<Integer>, IdentityHashSet<Operand>> choicesMap = new HashMap<>();

        IdentityHashMap<Operand, Operand> result = new IdentityHashMap<>();

        for (Map.Entry<Operand, Unknown> entry : unknowns.entrySet()) {

            print("entry: " + entry);

            Operand assign = entry.getKey();
            Set<Operand> choices = entry.getValue().getChoices();
            Set<Integer> choiceIdsKey = Operand.getIds(choices);

            if (!knowns.containsKey(assign.getParent())) {
                continue;
            }

            print("choice IDs: " + choiceIdsKey);

            for (Operand choice : choices) {
                idsToOperands.put(choice.getInstanceId(), choice);
            }

            if (!choicesMap.containsKey(choiceIdsKey)) {
                choicesMap.put(choiceIdsKey, new IdentityHashSet<>());
            }

            choicesMap.get(choiceIdsKey).add(assign);

        }

        print("ID=OP: " + idsToOperands);
        print("choices map: " + choicesMap);

        for (Set<Integer> key : choicesMap.keySet()) {

            if (key.size() == 1 && choicesMap.get(key).size() == 1) {
                return result;
            }

        }




    /*for(Map.Entry<Set<Integer>, IdentityHashSet<Operand>> entry : choicesMap.entrySet()) {

      makeGroupChoice(result, entry.getKey(), entry.getValue(), null);

    }*/



    /*
    for (Map.Entry<Operand, Set<Operand>> entry : unknowns.entrySet()) {

      Operand assign = entry.getKey();

      for(Operand choice : entry.getValue()) {

        if(unknowns.containsKey(assign.getParent())) {
          continue;
        }

        if(arbitraryChoiceCanBe(choice, assign, knowns, unknowns)) {
          result.put(assign, choice);
          break;
        }


      }

    }*/


        int minSize = Integer.MAX_VALUE;
        Set<Integer> chooseIds = null;
        Set<Operand> assign = null;

        for (Map.Entry<Set<Integer>, IdentityHashSet<Operand>> choiceEntry : choicesMap.entrySet()) {

            if (choiceEntry.getValue().size() == 0) {
                continue;
            }

            if (choiceEntry.getValue().size() < minSize) {
                minSize = choiceEntry.getValue().size();
                assign = choiceEntry.getValue();
                chooseIds = choiceEntry.getKey();
            }

        }

        if (chooseIds == null || assign == null)
            return result;

        assert chooseIds != null;
        assert assign != null;

        int s0 = assign.size();
        int s1 = chooseIds.size();

        //TODO: check if the s0 > 0 causes issues (was s0 > 1)
        print("Sub-match allowed: " + allowSubMatch);
        boolean makeArbitraryChoices = allowSubMatch ? (s0 > 0 && s0 <= s1) : (s0 == s1);

        Operand nextAssign = assign.iterator().next();

        if (makeArbitraryChoices && !unknowns.containsKey(nextAssign.getParent())) {

            for (int i : chooseIds) {

                Operand choice = idsToOperands.get(i);

                if (arbitraryChoiceCanBe(choice, nextAssign, knowns, unknowns)) {
                    result.put(nextAssign, choice);
                    break;
                }


            }


        }




    /*
    boolean chose = false;

    for (Map.Entry<Set<Integer>, IdentityHashSet<Operand>> choiceEntry : choicesMap.entrySet()) {

      IdentityHashSet<Operand> toAssign = choiceEntry.getValue();
      Set<Integer> choiceIds = new HashSet<>(choiceEntry.getKey());

      int s0 = toAssign.size();
      int s1 = choiceIds.size();

      //TODO: check if the s0 > 0 causes issues (was s0 > 1)
      print("Sub-match allowed: " + allowSubMatch);
      boolean makeArbitraryChoices = allowSubMatch ? (s0 > 0 && s0 <= s1) : (s0 == s1);

      Operand nextAssign = toAssign.iterator().next();

      if (makeArbitraryChoices && !unknowns.containsKey(nextAssign.getParent())) {

        for(int i : choiceIds) {

          Operand choice = idsToOperands.get(i);

          if(arbitraryChoiceCanBe(choice, nextAssign, knowns, unknowns)) {
            result.put(nextAssign, choice);
            chose = true;
            break;
          }



        }



      }

      if (chose) {
        break;
      }

    }*/

        print("arbitrary assignments: " + result);

        return result;

    }

    // TODO: clean this mess up
    private static void applySetDifference2(IdentityHashMap<Operand, Operand> knowns,
                                            IdentityHashMap<Operand, Set<Operand>> unknowns, Set<Integer> usedIds) {

        Map<Operand, Set<Operand>> possiblesMap = new HashMap<>();

        for (Map.Entry<Operand, Set<Operand>> entry : unknowns.entrySet()) {

            if (!possiblesMap.containsKey(entry.getKey())) {

                possiblesMap.put(entry.getKey(), new HashSet<>(entry.getValue()));

            } else {

                Iterator<Operand> unknownPossiblesIterator = entry.getValue().iterator();

                Set<Operand> sharedPossibles = possiblesMap.get(entry.getKey());

                while (unknownPossiblesIterator.hasNext()) {

                    Operand possible = unknownPossiblesIterator.next();

                    if (!sharedPossibles.contains(possible)) {

                        //print("removing for "+entry.getKey()+": "+possible);

                        unknownPossiblesIterator.remove();
                    }

                }


            }

            Set<Operand> sharedPossibles = possiblesMap.get(entry.getKey());

            entry.getValue().removeIf(new Predicate<Operand>() {
                @Override
                public boolean test(Operand operand) {

                    Operand knownVal = null;

                    for (Operand op : knowns.keySet()) {

                        if (op.equals(entry.getKey())) {

                            if (knownVal != null) {

                                assert knownVal.equals(knowns.get(op));

                            } else {
                                knownVal = knowns.get(op);
                            }

                        }

                    }

                    return !sharedPossibles.contains(operand) || (knownVal != null && !operand
                            .equals(knownVal));
                }
            });


        }

    }


    private static void applySetDifference(
            IdentityHashMap<Operand, Operand> knowns,
            IdentityHashMap<Operand, Unknown> unknowns
    ) {

        Map<Operand, IdentityHashSet<Operand>> possibles = new HashMap<>();

        for (Map.Entry<Operand, Unknown> entry : unknowns.entrySet()) {

            if (possibles.containsKey(entry.getKey())) {

                Utils.removeNotShared(possibles.get(entry.getKey()), entry.getValue().getChoices());

            } else {

                possibles.put(entry.getKey(), entry.getValue().getChoices());

            }

        }

        for (Map.Entry<Operand, Unknown> entry : unknowns.entrySet()) {

            Utils.removeNotShared(entry.getValue().getChoices(), possibles.get(entry.getKey()));

        }

    }

    private static <K, V> void print(Map<K, V> map) {
        for (K key : map.keySet()) {
            print(key + " = " + map.get(key));
        }
    }

    private static void printKnownsAndUnknowns(IdentityHashMap<Operand, Operand> knowns,
                                               IdentityHashMap<Operand, Unknown> unknowns) {

        //print("----------------------------------");
        print("Knowns:");
        if (knowns.isEmpty()) {
            print("none");
        } else {
            print(knowns);
        }
        print("Unknowns:");
        if (unknowns.isEmpty()) {
            print("none");
        } else {
            for (Unknown unknown : unknowns.values()) {
                print(unknown);
            }
        }
        print();
        //print("----------------------------------");

    }

    private static boolean arbitraryChoiceCanBe(Operand operand, Operand match,
                                                IdentityHashMap<Operand, Operand> knowns, IdentityHashMap<Operand, Unknown> unknowns) {

        boolean structureMatch = false;

        if (match.getChildren() == null) {

            if (knowns.containsKey(match)) {

                //TODO: check if .equals() should be replaced with ==
                structureMatch = knowns.get(match).equals(operand);

            } else {

                assert unknowns.containsKey(match) : "Operand " + match + " not in knowns or unknowns";

                structureMatch = unknowns.get(match).getChoices().contains(operand);
            }

        } else if (operand.getType() == match.getType()) {

            // if operation is the same, check if children match

            if ((operand.getType() == PRODUCT || operand.getType() == SUM)) {

                PermutationChooser permutationChooser =
                        new PermutationChooser(operand.getChildren(), match.getChildren(),
                                new PermutationChooser.Matcher<Operand>() {
                                    @Override
                                    public boolean match(Operand item, Operand match, int itemIndex, int matchIndex) {
                                        return arbitraryChoiceCanBe(item, match, knowns, unknowns);
                                    }
                                });


                structureMatch = permutationChooser.getFirst() != null;

            } else {

                // order-dependent match
                if (operand.getChildren().size() == match.getChildren().size()) {

                    structureMatch = true;

                    for (int i = 0; i < operand.getChildren().size(); i++) {

                        if (!arbitraryChoiceCanBe(operand.getChildren().get(i), match.getChildren().get(i),
                                knowns, unknowns)) {
                            structureMatch = false;
                            break;
                        }

                    }

                }

            }

        }

        return structureMatch;

    }

    private static void print(Object o) {
        if (DEBUG) System.out.println(o);
    }

    private static void print() {
        System.out.println();
    }

    public void add(Operand match, Operand operand) {

        assert match.getInstanceId() != -1 : "Match ID not set: " + match;
        assert operand.getInstanceId() != -1 : "Operand ID not set: " + operand;

        if (!possibleAnalogs.containsKey(match)) {
            possibleAnalogs.put(match, new IdentityHashSet<>());
        }
        possibleAnalogs.get(match).add(operand);

    }

    public void addEmpty(Operand match) {

        if (!possibleAnalogs.containsKey(match)) {
            possibleAnalogs.put(match, new IdentityHashSet<>());
        }

    }

    @Override
    public String toString() {
        return String.valueOf(possibleAnalogs);
    }

    public Map<String, Operand> getVariableMap() {

        assert variableMap != null : "Variable map was not computed";

        return variableMap.isEmpty() ? null : variableMap;

    }

    public Set<Integer> computeVariableMap(boolean allowSubMatch) {

        variableMap = new HashMap<>();

        Set<Integer> usedIds = new HashSet<>();
        IdentityHashMap<Operand, Operand> knowns = new IdentityHashMap<>();
        IdentityHashMap<Operand, Unknown> unknowns = new IdentityHashMap<>();

        // populate knowns an unknowns from all possibles
        for (Operand op : possibleAnalogs.keySet()) {
            Set<Operand> set = possibleAnalogs.get(op);
            if (set.size() == 1) {
                Operand known = possibleAnalogs.get(op).iterator().next();
                knowns.put(op, known);
                if (!usedIds.add(known.getInstanceId())) {
                    return null;
                }
            } else {
                Unknown unknown = new Unknown(op);
                unknown.getChoices().addAll(set);
                unknowns.put(op, unknown);
            }
        }


        // loop over possibles for each unknown and try to deduce an arrangement that maps all
        // variables in the rule to the operands in the input expression
        for (int i = 0; !unknowns.isEmpty() && i < MAX_ITERATIONS; i++) {

            applySetDifference(knowns, unknowns);

            print("---------------------------- ITERATION " + i + " ----------------------------");
            printKnownsAndUnknowns(knowns, unknowns);

            Iterator<Map.Entry<Operand, Unknown>> unknownIterator = unknowns.entrySet().iterator();

            // loop through all unknowns
            while (unknownIterator.hasNext()) {

                Map.Entry<Operand, Unknown> nextUnknown = unknownIterator.next();

                Operand unknown = nextUnknown.getKey();
                Set<Operand> possibleValues = nextUnknown.getValue().getChoices();

                if (possibleValues.size() == 1) {

                    // only one possible value

                    Operand foundValue = possibleValues.iterator().next();

                    if (usedIds.add(foundValue.getInstanceId())) {
                        knowns.put(unknown, foundValue);
                        unknownIterator.remove();
                    }

                }


            }

            // remove used and known operands from unknowns
            unknowns.entrySet().removeIf(new Predicate<Map.Entry<Operand, Unknown>>() {
                @Override
                public boolean test(Map.Entry<Operand, Unknown> operandUnknownEntry) {
                    return knowns.containsKey(operandUnknownEntry.getKey());
                }
            });


            print("REMOVE IMPOSSIBLES BEFORE:");
            printKnownsAndUnknowns(knowns, unknowns);


            if (removeImpossibleChoices(knowns, unknowns, usedIds)) {
                return null;
            }

            print();

            print("SET DIFFERENCE BEFORE:");
            printKnownsAndUnknowns(knowns, unknowns);


            applySetDifference(knowns, unknowns);


            print("SET DIFFERENCE RESULT:");
            printKnownsAndUnknowns(knowns, unknowns);


            IdentityHashMap<Operand, Operand> choices =
                    makeArbitraryChoices(unknowns, knowns, allowSubMatch);

            for (Map.Entry<Operand, Operand> entry : choices.entrySet()) {
                unknowns.remove(entry.getKey());
                knowns.put(entry.getKey(), entry.getValue());
                usedIds.add(entry.getValue().getInstanceId());
            }

        }


        print("FINAL:");
        printKnownsAndUnknowns(knowns, unknowns);

        for (Map.Entry<Operand, Operand> entry : knowns.entrySet()) {

            if (entry.getKey() instanceof Variable) {
                variableMap.put(((Variable) entry.getKey()).getName(), entry.getValue());
            }

        }

        return usedIds;

    }

    private static class Unknown {

        private final Operand operand;
        private final IdentityHashSet<Operand> choices = new IdentityHashSet<>();

        public Unknown(Operand operand) {
            this.operand = operand;
        }

        public void addChoice(Operand choice) {
            choices.add(choice);
        }

        public IdentityHashSet<Operand> getChoices() {
            return choices;
        }

        public Operand getOperand() {
            return operand;
        }

        @Override
        public String toString() {

            StringBuilder s = new StringBuilder();

            s.append(operand);
            s.append("[");
            s.append(operand.getInstanceId());
            s.append("]");
            s.append(" = ");

            s.append('{');

            int start = s.length();

            for (Operand op : choices) {

                if (s.length() > start) s.append(", ");

                s.append(op);
                s.append("[");
                s.append(op.getInstanceId());
                s.append("]");

            }

            s.append('}');

            return s.toString();

        }

    }


}

package core.old;

import core.components.*;

import java.util.*;

import static core.components.Operand.Type.PRODUCT;
import static core.components.Operand.Type.SUM;

@Deprecated
public class StructureMatcher {

  /*
  public static boolean matchesStructure(Operand operand, Operand match) {
    OperandMatcher matcher = new OperandMatcher();
    boolean structureMatch = matchesStructureRecur(operand, match, matcher, true);
    Set<Integer> usedIds = matcher.computeVariableMap(true);
    Map<String, Operand> mapping = matcher.getVariableMap();
    System.out.println(mapping);
    if (mapping == null) {
      return false;
    }
    return structureMatch && mapping.keySet().equals(match.getVariables());
  }*/

    public static Map<String, Operand> getMatchVariables(Operand operand, Operand match) {
        VariableMapper matcher = new VariableMapper();
        boolean structureMatch = testStructureMatchRecur(operand, match, matcher, true);

        Set<Integer> usedIds = matcher.computeVariableMap(true);

        if (usedIds == null) {
            return null;
        }

        Map<String, Operand> mapping = matcher.getVariableMap();

        if (!structureMatch) {
            return null;
        }

        return mapping;
    }

    private static MatchingStructure testStructureMatch(Operand operand, Operand match) {

        VariableMapper matcher = new VariableMapper();

        if (!testStructureMatchRecur(operand, match, matcher, true)) {
            return null;
        }

        Set<Integer> usedIds = matcher.computeVariableMap(true);

        if (usedIds == null) {
            return null;
        }

        Map<String, Operand> variableMap = matcher.getVariableMap();

        if (variableMap == null) {
            return null;
        } else if (!variableMap.keySet().equals(match.getVariables())) {
            return null;
        }

        return new MatchingStructure(operand, variableMap, usedIds);

    }

    private static boolean testStructureMatchRecur(Operand operand, Operand match,
                                                   VariableMapper variableMapper, boolean allowSubMatch) {

        boolean structureMatch = false;

        if (match.getType() == Operand.Type.VARIABLE) {

            // if match is variable, check if operand meets requirements
            structureMatch = matchesVariable(match, operand);

        } else if (match.getType() == Operand.Type.LITERAL) {

            // if match is literal, check if operand is also literal with same value
            structureMatch = matchesLiteral(match, operand);

        } else if (operand.getType() == match.getType()) {

            // if operation is the same, check if children match

            if ((operand.getType() == PRODUCT || operand.getType() == SUM)) {

                // order-independent match
                for (Operand c0 : match.getChildren()) {
                    for (Operand c1 : operand.getChildren()) {

                        //TODO: make sure allow sub-match here does not cause issues
                        if (!testStructureMatchRecur(c1, c0, variableMapper, false)) {
                            //operandMatcher.add(c0, c1);
                        }

                    }
                }

                // TODO: restrict child sizes to be equal if sub-matching is not allowed
                structureMatch =
                        allowSubMatch || match.getChildren().size() == operand.getChildren().size();
                structureMatch = true;

            } else {

                // order-dependent match
                if (operand.getChildren().size() == match.getChildren().size()) {

                    structureMatch = true;

                    for (int i = 0; i < operand.getChildren().size(); i++) {

                        //TODO: make sure allow sub-match here does not cause issues

                        if (!testStructureMatchRecur(operand.getChildren().get(i), match.getChildren().get(i),
                                variableMapper, false)) {
                            structureMatch = false;
                            break;
                        }

                    }

                }

            }

        }


        if (structureMatch) {
            variableMapper.add(match, operand);
        } else {
            variableMapper.addEmpty(match);
        }

        return structureMatch;

    }


    /**
     * Use this going forward, assume sub-match is always allowed
     */
    private static boolean matchesRecur(Operand operand, Operand match, VariableMapper vm) {

        boolean matches = false;

        if (match.getType() == Operand.Type.VARIABLE) {

            matches = matchesVariable(match, operand);

        } else if (match.getType() == Operand.Type.LITERAL) {

            matches = matchesLiteral(match, operand);

        } else if (operand.getType() == match.getType()) {

            if ((operand.getType() == PRODUCT || operand.getType() == SUM)) {

                // add all possible matches for variable mapper to solve
                for (Operand c0 : match.getChildren()) {
                    for (Operand c1 : operand.getChildren()) {
                        matchesRecur(c1, c0, vm);
                    }
                }

                matches = true;

            } else {

                // order-dependent match
                if (operand.getChildren().size() == match.getChildren().size()) {

                    matches = true;

                    for (int i = 0; i < operand.getChildren().size(); i++) {

                        if (!matchesRecur(operand.getChildren().get(i), match.getChildren().get(i), vm)) {
                            matches = false;
                            break;
                        }

                    }

                }

            }

        }


        if (matches) {
            vm.add(match, operand);
        } else {
            vm.addEmpty(match);
        }

        return matches;

    }

    private static boolean matchesVariable(Operand variable, Operand operand) {

        assert variable instanceof Variable;

        Variable variable2 = (Variable) variable;

        String name = variable2.getName();


        boolean matches = true;

        Set<MatchRequirement> requirements = getRequirements(name);

        if (requirements != null) {

            if (requirements.contains(MatchRequirement.VARIABLE)) {

                matches = operand.getType() == Operand.Type.VARIABLE;

            } else if (requirements.contains(MatchRequirement.LITERAL)) {

                matches = operand.getType() == Operand.Type.LITERAL;

            }

        }

        return matches;

    }

    public static MatchingStructure findMatchingStructure(Operand operand, Operand match) {

        return findMatchingStructureRecur(operand, match);
    }

    private static MatchingStructure findMatchingStructureRecur(Operand operand, Operand match) {

        MatchingStructure thisMatch = testStructureMatch(operand, match);

        if (thisMatch != null) {

            return thisMatch;

        } else {

            // search children

            if (operand.getChildren() == null) {
                return null;
            }

            for (Operand child : operand.getChildren()) {


                MatchingStructure childMatch = findMatchingStructureRecur(child, match);

                if (childMatch != null) {
                    return childMatch;
                }

            }

        }

        return null;

    }

    private static boolean matchesLiteral(Operand literal, Operand operand) {

        assert literal instanceof Literal;

        return operand.getType() == Operand.Type.LITERAL && operand.evaluate() == literal.evaluate();

    }

    public static Set<MatchRequirement> getRequirements(String variableName) {

        String[] args = variableName.split("_", 2);

        if (args.length == 1) {
            return null;
        }

        String rqs = args[0];

        Set<MatchRequirement> requirements = new HashSet<>();

        for (int i = 0; i < rqs.length(); i++) {

            switch (rqs.charAt(i)) {

                case 'V':
                    requirements.add(MatchRequirement.VARIABLE);
                    break;
                case 'L':
                    requirements.add(MatchRequirement.LITERAL);
                    break;
                case 'U':
                    requirements.add(MatchRequirement.UNIQUE);
                    break;

            }

        }

        return requirements.size() > 0 ? requirements : null;

    }

    public static Operand applyRule(Operand operand, Rule rule) {

        MatchingStructure matchingStructure = findMatchingStructure(operand, rule.getFind());

        if (matchingStructure == null) {
            return null;
        }

        VariableDomain replaceDomain = new VariableDomain();
        for (String s : matchingStructure.variables.keySet()) {
            replaceDomain.put(s, matchingStructure.variables.get(s));
        }

        Operand replaceOperand = rule.getReplaceWith().copy();
        replaceOperand.setVariableDomain(replaceDomain);
        replaceOperand = replaceOperand.importFromVariableDomain();

        if (matchingStructure.original.getChildren() != null && operand.getType() == PRODUCT
                || operand.getType() == SUM) {

            List<Operand> unusedChildren = new ArrayList<>();

            for (Operand child : matchingStructure.original.getChildren()) {

                if (!matchingStructure.usedIds.contains(child.getInstanceId())) {
                    unusedChildren.add(child.copy());
                }

            }

            if (!unusedChildren.isEmpty()) {

                unusedChildren.add(replaceOperand);

                if (matchingStructure.original.getType() == PRODUCT) {
                    replaceOperand = new Product(unusedChildren);
                } else {
                    replaceOperand = new Sum(unusedChildren);
                }

            }

        }

        Operand originalCopy = operand.copy();
        Operand.assignInstanceIds(originalCopy);

        Operand result = Operand.replace(originalCopy, matchingStructure.original.getInstanceId(), replaceOperand);

        Operand.assignInstanceIds(result);

        return result;

    }

    public static Operand applyRules(Operand operand, List<Rule> rules) {

        if (!Operand.hasValidIds(operand)) {
            throw new IllegalArgumentException(
                    "Operand \"" + operand + "\" has not been assigned valid IDs");
        }

        Operand curr = operand;

        int count = 0;
        for (int i = 0; i < rules.size() && count < 200; i++) {

            System.out.println(
                    "//////////////////////////////////////////////////////////////////////////////////////////////////////");
            System.out.println("CHECKING RULE: " + rules.get(i) + " for " + curr);
            System.out.println(
                    "//////////////////////////////////////////////////////////////////////////////////////////////////////");

            Operand applied = applyRule(curr, rules.get(i));

            if (applied != null) {

                while (applied != null) {

                    System.out.println("===================================================================");
                    System.out.println("APPLIED: " + rules.get(i) + " to " + curr);
                    curr = applied;
                    System.out.println("RESULT: " + curr);

                    applied = applyRule(curr, rules.get(i));

                }

                i = 0;

            } else {
                System.out.println("NO RULE MATCH");
            }

            count++;

        }

        return curr;

    }

    enum MatchRequirement {
        VARIABLE, LITERAL, UNIQUE
    }


    private static class MatchingStructure {

        Operand original;
        Map<String, Operand> variables;
        Set<Integer> usedIds;

        public MatchingStructure(Operand original, Map<String, Operand> variables,
                                 Set<Integer> usedIds) {
            this.original = original;
            this.variables = variables;
            this.usedIds = usedIds;
        }

        @Override
        public String toString() {
            return "operand: " + original + ", variables: " + variables + ", usedIds: " + usedIds;
        }

    }

}
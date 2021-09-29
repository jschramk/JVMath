package com.jschramk.JVMath.runtime.rewrite_engine;

import com.jschramk.JVMath.runtime.components.BinaryOperation;
import com.jschramk.JVMath.runtime.components.Literal;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.components.Variable;

public class StructureMatcher {

    private static boolean matches(Operand rule, Operand check, Requirements requirements,
        OperandMapper mapper, String solveVariable) {

        boolean matches = false; // default to false

        if (rule.treeDepth() > check.treeDepth()) {

            // skip to end

        } else if (rule instanceof Variable) { // rule operand is variable, check requirements

            Variable var = (Variable) rule;

            matches = requirements == null || requirements
                .meetsPrerequisites(var.getName(), solveVariable, check);

        } else if (rule instanceof Literal) { // rule operand is literal, check if actual is the same literal

            if (check instanceof Literal) {

                Literal ruleLiteral = (Literal) rule;
                Literal actualLiteral = (Literal) check;

                matches = ruleLiteral.getValue() == actualLiteral.getValue();

            }

        } else if (Operand.sameType(rule, check)) { // rule and actual operands are the  same type

            if (check instanceof BinaryOperation && ((BinaryOperation) check).getOperator()
                .isCommutative()) {

                // add all possible matches for mapper to solve
                for (Operand matchChild : rule) {

                    for (Operand operandChild : check) {

                        if (matches(matchChild, operandChild, requirements, mapper,
                            solveVariable)) {
                            matches = true;
                        }

                    }

                }

            } else { // the operand is not a commutative operation, check operands in order

                if (check.childCount() == rule.childCount()) {

                    matches = true; // default to true and check for mismatch

                    for (int i = 0; i < check.childCount(); i++) {

                        Operand ruleChild = rule.getChild(i);
                        Operand actualChild = check.getChild(i);

                        if (!matches(ruleChild, actualChild, requirements, mapper, solveVariable)) {

                            matches = false;

                            break; // break on mismatch

                        }

                    }

                }

            }

        }

        if (matches) {
            mapper.add(rule, check);
        } else {
            mapper.remove(rule, check);
        }

        return matches;

    }

    public static Match getMatch(Operand ruleOperand, Operand actualOperand,
        Requirements requirements, String solveVariable) {

        OperandMapper mapper = new OperandMapper();

        if (!matches(ruleOperand, actualOperand, requirements, mapper, solveVariable)) {
            return null;
        }

        Knowns knowns = mapper.getResult();

        if (knowns == null) {
            return null;
        }

        return new Match(actualOperand, knowns);

    }

    public static Match findMatch(Operand find, Operand in, Requirements requirements,
        String target) {

        return recursiveFindMatch(find, in, requirements, target);
    }

    private static Match recursiveFindMatch(Operand find, Operand in, Requirements requirements,
        String target) {

        Match thisMatch = getMatch(find, in, requirements, target);

        if (thisMatch != null) {

            return thisMatch;

        } else {

            if (!in.hasChildren()) {
                return null;
            }

            for (Operand child : in) {

                Match childMatch = recursiveFindMatch(find, child, requirements, target);

                if (childMatch != null) {
                    return childMatch;
                }

            }

        }

        return null;

    }

    public static class Match {

        private Operand original;
        private Knowns knowns;

        public Match(Operand original, Knowns knowns) {
            this.original = original;
            this.knowns = knowns;
        }

        public Knowns getKnowns() {
            return knowns;
        }

        public Operand getOriginal() {
            return original;
        }

    }

}

package com.jschramk.JVMath.runtime.math_engine;

import com.jschramk.JVMath.runtime.components.BinaryOperation;
import com.jschramk.JVMath.runtime.components.Literal;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.components.Variable;

public class PermutationSearch {

    private static boolean matches(Operand rule, Operand check, Requirements requirements,
        String solveVariable) {

        boolean matches = false; // default to false

        if (rule.treeDepth() > check.treeDepth()) {

            // skip to end

        } else if (rule instanceof Variable) { // rule operand is variable, check requirements

            Variable var = (Variable) rule;

            matches = requirements == null || requirements.meetsPrerequisites(var.getName(),
                solveVariable, check);

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

                        if (matches(matchChild, operandChild, requirements, solveVariable)) {
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

                        if (!matches(ruleChild, actualChild, requirements, solveVariable)) {

                            matches = false;

                            break; // break on mismatch

                        }

                    }

                }

            }

        }

        return matches;

    }


}

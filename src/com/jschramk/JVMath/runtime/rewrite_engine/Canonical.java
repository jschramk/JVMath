package com.jschramk.JVMath.runtime.rewrite_engine;

import com.jschramk.JVMath.runtime.components.*;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.Parser;
import com.jschramk.JVMath.runtime.rewrite_engine.RewriteEngine;

import java.util.*;

public class Canonical {

    private static int getOrderNumber(Operand o) {

        switch (o.getType()) {

            case FUNCTION:
                return 0;
            case VARIABLE:
                return 1;
            case CONSTANT:
                return 2;
            case LITERAL:
                return 3;

            default:
                throw new IllegalStateException("Unsupported operand type: " + o.getType());
        }

    }

    private static Operand getEquivalentRandom(int depth) {
        return makeEquivalentRandom(depth, new Random(0));
    }

    private static Operand makeEquivalentRandom(int depth, Random r) {

        if (depth == 0) {

            switch (r.nextInt(2)) {

                case 0:
                    return new Literal(r.nextInt(100));

                case 1:
                    return new Variable("var" + r.nextInt(100));

            }

        } else {

            int numChildren = 2 + r.nextInt(4);

            List<Operand> children = new ArrayList<>();

            for (int i = 0; i < numChildren; i++) {

                children.add(makeEquivalentRandom(depth - 1, r));

            }

            BinaryOperator b = null;

            switch (r.nextInt(4)) {

                case 0: {
                    b = Operators.ADDITION;
                    break;
                }

                case 1: {
                    b = Operators.MULTIPLICATION;
                    break;
                }

                case 2: {
                    b = Operators.DIVISION;
                    break;
                }

                case 3: {
                    b = Operators.EXPONENT;
                    break;
                }

            }

            assert b != null : "Binary operator is null";

            if (b.isCommutative()) {
                Collections.shuffle(children);
            }

            return new BinaryOperation(b, children);

        }

        throw new RuntimeException("Random expression generation failed");

    }


    public static void main(String[] args) throws ParserException {

        int numTest = 100;

        Operand[] tests = new Operand[numTest];

        for (int i = 0; i < numTest; i++) {
            tests[i] = getEquivalentRandom(5);
        }

        String expected = null;

        for (int i = 0; i < tests.length; i++) {

            Operand o = tests[i];

            o.canonify();

            String actual = o.toString();

            if (expected == null) {

                expected = o.toString();

                System.out.println("Expected: " + expected);

            } else if (!actual.equals(expected)) {

                throw new RuntimeException(
                    String.format("Result mismatch: expected %s, got %s", expected, actual));

            }

        }

    }

    // TODO: make a solution that will work with mapping algorithm
    public static Comparator<Operand> CANONICAL_COMPARATOR = new Comparator<Operand>() {
        @Override public int compare(Operand o1, Operand o2) {

            int childCountComp = -Integer.compare(o1.treeSize(), o2.treeSize());

            if (childCountComp != 0)
                return childCountComp;

            // force variables to come before literals
            if ((o1.getType() == Enums.OperandType.LITERAL) && (o2.getType()
                == Enums.OperandType.VARIABLE))
                return -1;
            if ((o1.getType() == Enums.OperandType.VARIABLE) && (o2.getType()
                == Enums.OperandType.LITERAL))
                return 1;

            int typeComp = o1.getType().compareTo(o2.getType());

            if (typeComp != 0)
                return typeComp;

            return o1.toString().compareTo(o2.toString());

        }

    };

}

package com.jschramk.JVMath.runtime.components;

import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.Parser;

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


        Parser p = Parser.getDefault();

        Operand o = p.parse("2 + z + 1.99999 - a^2", Operand.class);

        o.beautify();

        System.out.println(o);

    }

    // TODO: make a solution that will work with mapping algorithm
    public static Comparator<Operand> BEAUTIFY_COMPARATOR = (o1, o2) -> {

        switch (o1.getType()) {

            case VARIABLE: {

                switch (o2.getType()) {

                    case VARIABLE:
                        return o1.toString().compareTo(o2.toString());

                    case LITERAL:
                        return -1;

                    default:
                        return 1;

                }

            }

            case LITERAL: {

                if (o2.getType() == Enums.OperandType.LITERAL) {
                    return (int) -Math.signum(o2.computeToDouble() - o1.computeToDouble());
                }

                return 1;

            }

            default:
                return o2.treeSize() - o1.treeSize();


        }

    };

}

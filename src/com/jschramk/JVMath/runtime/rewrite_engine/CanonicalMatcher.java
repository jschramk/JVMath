package com.jschramk.JVMath.runtime.rewrite_engine;

import com.jschramk.JVMath.runtime.components.*;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.Parser;

import java.util.*;

public class CanonicalMatcher {


    private static class VariableStack {

        private LinkedList<Map<String, Operand>> stack = new LinkedList<>();

        public boolean hasFrame() {
            return !stack.isEmpty();
        }

        public void enterNewFrame() {

            Map<String, Operand> vars = new HashMap<>();

            stack.addFirst(vars);

        }

        public void popFrame() {

            stack.removeFirst();

        }

        public void addMapping(String var, Operand o) {

            if(stack.isEmpty()) {
                throw new RuntimeException("No stack frame");
            }

            if (get(var) != null) {
                throw new IllegalArgumentException(
                    String.format("Variable \"%s\" already used", var));
            }

            stack.getFirst().put(var, o);

        }

        public Operand get(String s) {

            for (Map<String, Operand> frame : stack) {

                Operand o = frame.get(s);

                if (o != null)
                    return o;

            }

            return null;

        }

        @Override public String toString() {
            return stack.toString();
        }
    }

    public static void main(String[] args) throws ParserException {

        Operand matchOperand = Parser.getDefault().parse("a x^(a + 3) + b x^2 + c x + d").to(Operand.class);
        Operand searchOperand = Parser.getDefault().parse("5z^2 + 4z + 12 + 8z^(8 + 3)").to(Operand.class);

        matchOperand.canonify();
        searchOperand.canonify();

        System.out
            .println(String.format("Attempting to map %s to %s", matchOperand, searchOperand));

        System.out.println(matches(matchOperand, searchOperand, 0, new VariableStack()));


    }

    static boolean matches(Operand match, Operand search, int level, VariableStack v) {

        System.out.print("\t".repeat(level));

        System.out.println(String.format("[ %s ] -> [ %s ] ?", match, search));

        boolean ret = false;

        if (match.getType() == Enums.OperandType.VARIABLE) {

            Operand o = v.get(match.toString());

            if (o == null) {

                if(v.hasFrame()) v.addMapping(match.toString(), search);

                ret = true;

            } else {

                if(o.equals(search)) {

                    ret = true;

                }

            }

        } else if (match.getType() == Enums.OperandType.LITERAL) {

            if (search.getType() == Enums.OperandType.LITERAL) {

                Literal l1 = (Literal) match;
                Literal l2 = (Literal) search;

                if (l1.getValue() == l2.getValue()) {
                    ret = true;
                }

            }

        } else if (match instanceof UnaryOperation) {

            if (search instanceof UnaryOperation) {

                UnaryOperation u1 = (UnaryOperation) match;
                UnaryOperation u2 = (UnaryOperation) search;

                if (u1.getOperator() == u2.getOperator()) {
                    ret = true;
                }

            }

        } else if (match instanceof BinaryOperation) {

            if (search.childCount() < match.childCount())
                return false;

            if (search instanceof BinaryOperation) {

                BinaryOperation u1 = (BinaryOperation) match;
                BinaryOperation u2 = (BinaryOperation) search;

                if (u1.getOperator() == u2.getOperator()) {

                    if (u1.getOperator().isCommutative()) {
                        // order independent match (match group can split up but not change order)

                        int matchCount = 0;

                        int offset = 0;

                        for (int i = 0; i < match.childCount(); i++) {

                            for (int j = offset; j < search.childCount(); j++) {

                                v.enterNewFrame();

                                boolean matches =
                                    matches(match.getChild(i), search.getChild(j), level + 1, v);

                                if (matches) {
                                    offset = j + 1;
                                    matchCount++;
                                    break;
                                } else {
                                    v.popFrame();
                                }

                            }

                            if (matchCount == match.childCount()) {
                                ret = true;
                                break;
                            }

                        }



                    } else {
                        // order dependent match (match group must stay adjacent and in order)

                        for (int offset = 0;
                             offset < search.childCount() - match.childCount() + 1; offset++) {

                            int matchCount = 0;

                            v.enterNewFrame();

                            for (int i = 0; i < match.childCount(); i++) {

                                boolean matches =
                                    matches(match.getChild(i), search.getChild(i), level + 1, v);

                                if (!matches) {
                                    v.popFrame();
                                    break;
                                }

                                matchCount++;

                                if (matchCount == match.childCount()) {
                                    ret = true;
                                    break;
                                }

                            }




                        }


                    }

                }

            }

        }

        System.out.println("\t".repeat(level) + ret);

        if (ret) {
            System.out.print("\t".repeat(level));
            System.out.println(match + " -> " + search);
        }

        System.out.println(v);



        return ret;

    }

}

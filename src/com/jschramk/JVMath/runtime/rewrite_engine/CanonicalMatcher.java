package com.jschramk.JVMath.runtime.rewrite_engine;

import com.jschramk.JVMath.runtime.components.*;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.Parser;
import util.PerformanceTimer;

import java.util.*;

public class CanonicalMatcher {

    private static PerformanceTimer t = new PerformanceTimer();

    public static void main(String[] args) throws ParserException {

        Parser p = Parser.getDefault();

        compareMapping(p.parse("1 + 2 + a + 4 + b").to(Operand.class),
            p.parse("1 + 2 + 3 + 4 + 5").to(Operand.class));

    }

    private static void compareMapping(Operand match, Operand search) {

        t.start();

        match.canonify();
        search.canonify();

        t.stop();

        //System.out.println(match.toTreeString());
        //System.out.println(search.toTreeString());

        System.out.println("Canonify time: " + t.ms() + " ms");

        System.out.println(String.format("Attempting to map %s to %s", match, search));

        // Old method -----------------------------------------------------------------------------

        t.start();

        StructureMatcher.Match oldMatch = StructureMatcher.getMatch(match, search, null, null);

        Map<String, Operand> vars1 = null;

        if (oldMatch != null) {
            vars1 = oldMatch.getKnowns().getVariables();
        }

        t.stop();

        System.out.println("Match time (old method): " + t.ms() + " ms");

        if (vars1 != null) {
            System.out.println("Result: " + vars1);
        } else {
            System.out.println("Old method did not produce a result");
        }

        // New method -----------------------------------------------------------------------------

        t.start();

        VariableStack v = new VariableStack();

        boolean matches = checkMatch(match, search, 0, v);

        t.stop();

        Map<String, Operand> vars2 = null;

        if (matches) {
            vars2 = v.collapse();
        }

        System.out.println("Match time (new method): " + t.ms() + " ms");

        if (vars2 != null) {
            System.out.println("Result: " + vars2);
        } else {
            System.out.println("New method did not produce a result");
        }

        if (!Objects.equals(vars1, vars2)) {
            System.err.println("Error: new and old methods produced different results");
        }

    }



    private static boolean checkMatch(Operand match, Operand search, int level, VariableStack v) {

        //System.out.print("\t".repeat(level));

        //System.out.println(String.format("[ %s ] -> [ %s ] ?", match, search));

        boolean ret = false;

        if (match.getType() == Enums.OperandType.VARIABLE) {

            Operand o = v.get(match.toString());

            if (o == null) {

                if (v.hasFrame())
                    v.addMapping(match.toString(), search);

                ret = true;

            } else {

                if (o.equals(search)) {

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
                                    checkMatch(match.getChild(i), search.getChild(j), level + 1, v);

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
                                    checkMatch(match.getChild(i), search.getChild(i), level + 1, v);

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

        //System.out.println("\t".repeat(level) + ret);

        if (ret) {
            //System.out.print("\t".repeat(level));
            //System.out.println(match + " -> " + search);
        }

        //System.out.println(v);

        return ret;

    }



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

            if (stack.isEmpty()) {
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

        public Map<String, Operand> collapse() {

            Map<String, Operand> ret = new HashMap<>();

            for (Map<String, Operand> frame : stack) {

                ret.putAll(frame);

            }

            return ret;

        }

        @Override public String toString() {
            return stack.toString();
        }
    }

}

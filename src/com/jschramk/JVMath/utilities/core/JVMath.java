package com.jschramk.JVMath.utilities.core;

import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.math_engine.MathEngine;
import com.jschramk.JVMath.runtime.parse.Parser;

public class JVMath {

    public static void main(String[] args) throws ParserException {

        MathEngine.Output<Operand> output = simplify("(x - y - z)/4 + (y - 6)/3 + (x + y)/2");

        output.printSteps();

    }

    private static Parser parser = Parser.getDefault();

    public static MathEngine.Output<Equation> solve(String equation, String variable)
        throws ParserException {

        Equation input = parser.parse(equation, Equation.class);

        return MathEngine.solve(input, variable, true);

    }



    public static MathEngine.Output<Operand> simplify(String expression) throws ParserException {
        return simplify(expression, null);
    }

    public static MathEngine.Output<Operand> simplify(String expression, String variable)
        throws ParserException {

        Operand input = parser.parse(expression, Operand.class);

        return MathEngine.simplify(input, variable, true);

    }

}

package com.jschramk.JVMath.utilities.test;

import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.parse.ParseResult;
import com.jschramk.JVMath.runtime.parse.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Console {

    public static void main(String[] args) {

        Parser p = Parser.getDefault();

        Scanner scanner = new Scanner(System.in);

        while (true) {

            try {

                System.out.print("Enter an expression or equation: ");

                String input0 = scanner.nextLine();

                ParseResult result = p.parse(input0);

                if (result.is(Equation.class)) {

                    Equation equation = result.to(Equation.class);

                    System.out.println("Your equation: " + equation);

                    List<String> vars = new ArrayList<>(equation.getVariables());

                    Collections.sort(vars);

                } else if (result.is(Operand.class)) {

                    Operand operand = result.to(Operand.class);

                    System.out.println("Your expression: " + operand);

                }


            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }


        }



    }


}

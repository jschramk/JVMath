package com.jschramk.JVMath.utilities.script;

import com.jschramk.JVMath.runtime.components.*;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.DefaultParser;
import com.jschramk.JVMath.runtime.parse.ParseResult;
import com.jschramk.JVMath.runtime.parse.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Script {

  private final VariableDomain variableDomain = VariableDomain.getDefault();
  private final Parser parser = new DefaultParser(FunctionDomain.getDefault());
  private final List<String> outputs = new ArrayList<>();

  public static List<String> execute(String input) throws ParserException {
    return execute(new Scanner(input));
  }

  private static List<String> execute(Scanner scanner) throws ParserException {
    Script script = new Script();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      script.readLine(line);
    }
    return script.outputs;
  }

  private void readLine(String line) throws ParserException {

    line = line.trim();

    line = line.split("//", 2)[0];

    if (line.isEmpty()) {
      return;
    }

    if (line.startsWith("clear")) {
      variableDomain.clear();
    }

    if (line.startsWith("print ")) {

      String rest = line.replaceFirst("print ", "");

      Operand toPrint = parser.parse(rest, Operand.class);
      toPrint.setVariableDomain(variableDomain);

      Operand imported = toPrint.importVariables(variableDomain);

      String print = toPrint + " = ";

      if (imported.getVariables()
        .isEmpty() || variableDomain.containsAll(imported.getVariables())) {

        print += imported.evaluate();

      } else {

        print += imported;

      }

      outputs.add(print);

    } else if (line.startsWith("show ")) {

      String rest = line.replaceFirst("show ", "");

      Operand toPrint = parser.parse(rest, Operand.class);
      toPrint.setVariableDomain(variableDomain);

      Operand imported = toPrint.importVariables(variableDomain);

      String print = "";

      if (!toPrint.getVariables().isEmpty()) {
        print += toPrint + " = ";
      }

      if (imported.getVariables()
        .isEmpty() || variableDomain.containsAll(imported.getVariables())) {

        if (!(imported instanceof Literal)) {
          print += imported + " = ";
        }

        print += imported.evaluate();

      } else {

        print += imported;

      }

      outputs.add(print);

    } else {

      ParseResult result = parser.parse(line);

      if (result.is(Equation.class)) {

        Equation equation = result.to(Equation.class);

        equation.setVariableDomain(variableDomain);

        if (equation.getLeftSide() instanceof Variable) {

          String name = ((Variable) equation.getLeftSide()).getName();

          variableDomain.put(name, equation.getRightSide());

        }

      }

    }

  }

  public static List<String> execute(File input) throws ParserException, FileNotFoundException {
    return execute(new Scanner(input));
  }

}

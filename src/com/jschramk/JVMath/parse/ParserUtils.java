package com.jschramk.JVMath.parse;

import lexer.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParserUtils {

  public static DFA<Character, Label> getPrimitivesDFA(Set<String> functions) {

    DFAConstructor<Character, Label> c = new DFAConstructor<>();

    c.add(Regex.parse(RegexCollection.NUMBER), Label.LITERAL);
    c.add(Regex.parse(RegexCollection.LONG_VARIABLE), Label.VARIABLE);
    c.add(Regex.parse(RegexCollection.SCIENTIFIC_NOTATION), Label.LITERAL);
    c.add(Regex.parse(RegexCollection.SCIENTIFIC_NOTATION_SIGNED), Label.LITERAL);
    c.add(Regex.parse("π|pi|e"), Label.CONSTANT);
    c.add(Regex.parse("<" + RegexCollection.INTEGER + ">"), Label.OPERAND);

    for (String s : functions) {
      c.add(Regex.parse(s), Label.FUNCTION);
    }

    return c.buildDFA();

  }

  public static DFA<Character, Label> getOperationsDFA(Set<String> functions) {

    DFAConstructor<Character, Label> c = new DFAConstructor<>();

    c.add(Regex.parse("+"), Label.PLUS);
    c.add(Regex.parse("-|−"), Label.MINUS);
    c.add(Regex.parse("*|×|⋅"), Label.TIMES);
    c.add(Regex.parse("/|÷"), Label.DIVIDED_BY);
    c.add(Regex.parse("^"), Label.TO_THE);
    c.add(Regex.parse("!"), Label.EXCLAMATION);
    c.add(Regex.parse("<" + RegexCollection.INTEGER + ">"), Label.OPERAND);

    for (String s : functions) {
      c.add(Regex.parse(s), Label.FUNCTION_NAME);
    }

    return c.buildDFA();

  }

  public static DFA<Label, Label> getFunctionsDFA() {

    DFAConstructor<Label, Label> c = new DFAConstructor<>();

    Map<Character, Label> conversion = new HashMap<>();
    conversion.put('f', Label.FUNCTION_NAME);
    conversion.put('o', Label.OPERAND);

    c.add(Regex.parseConversion("fo", conversion), Label.FUNCTION);

    return c.buildDFA();

  }

  public static DFA<Label, Label> getUnaryOperationsDFA() {

    DFAConstructor<Label, Label> c = new DFAConstructor<>();

    Map<Character, Label> conversion = new HashMap<>();
    conversion.put('o', Label.OPERAND);
    conversion.put('!', Label.EXCLAMATION);

    c.add(Regex.parseConversion("o!", conversion), Label.FACTORIAL);

    return c.buildDFA();

  }

  public static DFA<Label, Label> getExponentsDFA() {

    DFAConstructor<Label, Label> c = new DFAConstructor<>();

    Map<Character, Label> conversion = new HashMap<>();
    conversion.put('o', Label.OPERAND);
    conversion.put('-', Label.MINUS);
    conversion.put('^', Label.TO_THE);

    c.add(Regex.parseConversion("o^-o", conversion), Label.EXPONENT_NEG);
    c.add(Regex.parseConversion("o^o", conversion), Label.EXPONENT_POS);

    return c.buildDFA();

  }

  public static DFA<Label, Label> getMultiplicationDivisionDFA() {

    DFAConstructor<Label, Label> c = new DFAConstructor<>();

    Map<Character, Label> conversion = new HashMap<>();
    conversion.put('o', Label.OPERAND);
    conversion.put('*', Label.TIMES);
    conversion.put('-', Label.MINUS);
    conversion.put('/', Label.DIVIDED_BY);

    c.add(Regex.parseConversion("o*o", conversion), Label.MULTIPLY_POS_POS);
    c.add(Regex.parseConversion("o*-o", conversion), Label.MULTIPLY_POS_NEG);
    //c.add(Regex.parseConversion("-o*o", conversion), Label.MULTIPLY_NEG_POS);
    //c.add(Regex.parseConversion("-o*-o", conversion), Label.MULTIPLY_NEG_NEG);

    c.add(Regex.parseConversion("o/o", conversion), Label.DIVIDE_POS_POS);
    c.add(Regex.parseConversion("o/-o", conversion), Label.DIVIDE_POS_NEG);
    //c.add(Regex.parseConversion("-o/o", conversion), Label.DIVIDE_NEG_POS);
    //c.add(Regex.parseConversion("-o/-o", conversion), Label.DIVIDE_NEG_NEG);

    c.add(Regex.parseConversion("oo", conversion), Label.MULTIPLY_POS_POS_IMPLICIT);

    return c.buildDFA();

  }

  public static DFA<Label, Label> getAdditionsDFA() {

    DFAConstructor<Label, Label> c = new DFAConstructor<>();

    Map<Character, Label> conversion = new HashMap<>();
    conversion.put('o', Label.OPERAND);
    conversion.put('+', Label.PLUS);
    conversion.put('-', Label.MINUS);

    //c.add(Regex.parseConversion("o-o", conversion), Label.SUBTRACT_POS_POS);
    //c.add(Regex.parseConversion("o--o", conversion), Label.SUBTRACT_POS_NEG);
    //c.add(Regex.parseConversion("-o-o", conversion), Label.SUBTRACT_NEG_POS);
    //c.add(Regex.parseConversion("-o--o", conversion), Label.SUBTRACT_NEG_NEG);

    c.add(Regex.parseConversion("o+o", conversion), Label.ADDITION_POS_POS);
    //c.add(Regex.parseConversion("o+-o", conversion), Label.ADDITION_POS_NEG);
    //c.add(Regex.parseConversion("-o+o", conversion), Label.ADDITION_NEG_POS);
    //c.add(Regex.parseConversion("-o+-o", conversion), Label.ADDITION_NEG_NEG);

    c.add(Regex.parseConversion("oo", conversion), Label.ADDITION_IMPLICIT);

    return c.buildDFA();

  }

  public static DFA<Label, Label> getNegationsDFA() {

    DFAConstructor<Label, Label> c = new DFAConstructor<>();

    Map<Character, Label> conversion = new HashMap<>();
    conversion.put('o', Label.OPERAND);
    conversion.put('-', Label.MINUS);

    c.add(Regex.parseConversion("-o", conversion), Label.NEGATION);

    return c.buildDFA();

  }

  public static LabeledSegment<Character, Void> getNextParentheses(List<Character> input) {

    int level = 0;
    int start = 0;
    for (int i = 0; i < input.size(); i++) {

      if (input.get(i) == '(') {
        if (level == 0)
          start = i;
        level++;
      } else if (input.get(i) == ')') {
        level--;
        if (level == 0)
          return new LabeledSegment<>(start, i + 1, null);
      }

    }

    return null;

  }


  public enum Label {

    CONSTANT, EXCLAMATION, FACTORIAL, MULTIPLY_NEG_POS, MULTIPLY_NEG_NEG, DIVIDE_NEG_POS, DIVIDE_NEG_NEG, MULTIPLY_NEG_POS_IMPLICIT, SUBTRACT_POS_NEG, SUBTRACT_NEG_POS, ADDITION_POS_NEG, ADDITION_NEG_NEG, ADDITION_NEG_POS, SUBTRACT_NEG_NEG, SUBTRACT_POS_POS, ADDITION_IMPLICIT, LITERAL, VARIABLE, OPERAND, FUNCTION, PLUS, MINUS, TIMES, DIVIDED_BY, TO_THE, FUNCTION_NAME, EXPONENT_POS, EXPONENT_NEG, MULTIPLY_POS_POS, MULTIPLY_POS_NEG, MULTIPLY_POS_POS_IMPLICIT, DIVIDE_POS_POS, DIVIDE_POS_NEG, ADDITION_POS_POS, NEGATION

  }

}

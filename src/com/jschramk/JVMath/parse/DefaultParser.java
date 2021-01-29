package com.jschramk.JVMath.parse;

import com.jschramk.JVMath.components.*;
import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.utils.Utils;
import lexer.DFA;
import lexer.LabeledSegment;
import lexer.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultParser extends Parser {


  protected static final DefaultParser MAIN_INSTANCE =
      new DefaultParser(FunctionDomain.getDefault());

  private static final DFA<ParserUtils.Label, ParserUtils.Label> functionsDFA =
      ParserUtils.getFunctionsDFA();
  private static final DFA<ParserUtils.Label, ParserUtils.Label> exponentsDFA =
      ParserUtils.getExponentsDFA();
  private static final DFA<ParserUtils.Label, ParserUtils.Label> multiplicationsDivisionsDFA =
      ParserUtils.getMultiplicationDivisionDFA();
  private static final DFA<ParserUtils.Label, ParserUtils.Label> additionsDFA =
      ParserUtils.getAdditionsDFA();
  private static final DFA<ParserUtils.Label, ParserUtils.Label> negationsDFA =
      ParserUtils.getNegationsDFA();
  private final DFA<Character, ParserUtils.Label> primitivesDFA =
      ParserUtils.getPrimitivesDFA(functionDomain.getAllFunctions());
  private final DFA<Character, ParserUtils.Label> binaryOperationsDFA =
      ParserUtils.getOperationsDFA(functionDomain.getAllFunctions());
  private final DFA<ParserUtils.Label, ParserUtils.Label> unaryOperationsDFA =
      ParserUtils.getUnaryOperationsDFA();

  private final Map<String, Operand> operands = new HashMap<>();
  //private final Map<String, String> variableKeys = new HashMap<>();
  private int nextOperandNum = 0;

  public DefaultParser(FunctionDomain functionDomain) {
    super(functionDomain);
  }

  @Override public ParseResult parse(String input) throws ParserException {

    long t1 = System.nanoTime();

    String[] sides = input.split("=");

    long t2;
    if (sides.length == 1) {

      Operand result = parseOperand(input);
      result.fixTree();

      t2 = System.nanoTime();

      reset();

      return new ParseResult(input, result, t2 - t1);

    } else if (sides.length == 2) {

      Equation result = parseEquation(sides[0], sides[1]);
      result.fixTree();

      t2 = System.nanoTime();

      reset();

      return new ParseResult(input, result, t2 - t1);

    } else {
      throw new ParserException("Equations must contain a single \"=\": " + input);
    }

  }

  private void reset() {
    operands.clear();
    //variableKeys.clear();
    nextOperandNum = 0;
  }

  private String getNextOperandKey() {
    return "<" + nextOperandNum++ + ">";
  }

  private Equation parseEquation(String left, String right) throws ParserException {
    return new Equation(parseOperand(left), parseOperand(right));
  }

  private List<Character> parseParentheses(List<Character> input) throws ParserException {

    LabeledSegment<Character, Void> parens = ParserUtils.getNextParentheses(input);

    while (parens != null) {

      String key = getNextOperandKey();

      String inside =
          LabeledSegment.stringOf(input).substring(parens.getStart() + 1, parens.getEnd() - 1);

      operands.put(key, parseMatrix(inside));

      input = LabeledSegment.replace(input, parens, LabeledSegment.characterListOf(key));

      parens = ParserUtils.getNextParentheses(input);

    }

    return input;

  }

  private List<Character> parsePrimitives(List<Character> input) {

    Lexer<Character, ParserUtils.Label> ic = new Lexer<>(primitivesDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> classifications = ic.findAll(input);

    List<LabeledSegment<Character, ParserUtils.Label>> sections = new ArrayList<>();

    List<List<Character>> replacements = new ArrayList<>();

    for (LabeledSegment<Character, ParserUtils.Label> c : classifications) {

      if (c.getLabel() == null) {
        continue;
      }

      if (c.getLabel() == ParserUtils.Label.LITERAL) {

        sections.add(c);

        String text = LabeledSegment.stringOf(c.sectionOf(input));

        Operand number = new Literal(Double.parseDouble(text));

        String key = getNextOperandKey();

        operands.put(key, number);

        replacements.add(LabeledSegment.characterListOf(key));

      } else if (c.getLabel() == ParserUtils.Label.VARIABLE) {

        sections.add(c);

        String text = LabeledSegment.stringOf(c.sectionOf(input));

        Operand variable;

        String key;
        // TODO: reuse same variable instance someday? maybe?
        /*if (variableKeys.containsKey(text)) {
          key = variableKeys.get(text);
          variable = operands.get(key);
        } else {*/

        key = getNextOperandKey();
        //variableKeys.put(text, key);
        variable = new Variable(text);

        //}

        operands.put(key, variable);

        replacements.add(LabeledSegment.characterListOf(key));

      } else if (c.getLabel() == ParserUtils.Label.CONSTANT) {

        sections.add(c);

        String text = LabeledSegment.stringOf(c.sectionOf(input));

        String key = getNextOperandKey();

        Constant constant = Constant.getInstance(text);

        operands.put(key, constant);

        replacements.add(LabeledSegment.characterListOf(key));

      }

    }

    List<Character> result = LabeledSegment.replace(input, sections, replacements);

    result = LabeledSegment.characterListOf(LabeledSegment.stringOf(result).replaceAll(" ", ""));

    return result;

  }

  private List<Character> parseUnaryOperations(List<Character> input) {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(unaryOperationsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> first = ic1.findFirst(classes);

    while (first != null) {

      if (first.getLabel() == null) {
        continue;
      }

      String key = getNextOperandKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(first.getStart(), first.getEnd());

      if (first.getLabel() == ParserUtils.Label.FACTORIAL) {

        Operand arg = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));

        Operand result = new UnaryOperation(Operators.FACTORIAL, arg);

        operands.put(key, result);

      }

      input = LabeledSegment.replace(input, types.get(first.getStart()).getStart(),
          types.get(first.getEnd() - 1).getEnd(), LabeledSegment.characterListOf(key));

      types = ic0.findAll(input);
      classes = LabeledSegment.labelListOf(types);
      first = ic1.findLast(classes);

    }

    return input;

  }

  private List<Character> parseFunctions(List<Character> input) throws ParserException {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(functionsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> first = ic1.findFirst(classes);

    while (first != null) {

      String key = getNextOperandKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(first.getStart(), first.getEnd());

      if (first.getLabel() == ParserUtils.Label.FUNCTION) {

        String name = LabeledSegment.stringOf(section.get(0).sectionOf(input));
        String argsKey = LabeledSegment.stringOf(section.get(1).sectionOf(input));

        Operand function;

        try {

          /*if (operands.containsKey(argsKey)) {
          } else {
            function = functionDomain.getFunctionInstance(name, vectors.get(argsKey));
          }*/

          Operand arg = operands.get(argsKey);

          function = functionDomain.getFunctionInstance(name, arg);

        } catch (IllegalArgumentException e) {
          throw new ParserException(e.getMessage());
        }

        operands.put(key, function);

      }

      input = LabeledSegment.replace(input, types.get(first.getStart()).getStart(),
          types.get(first.getEnd() - 1).getEnd(), LabeledSegment.characterListOf(key));

      types = ic0.findAll(input);
      classes = LabeledSegment.labelListOf(types);
      first = ic1.findLast(classes);

    }

    return input;

  }

  private List<Character> parseExponents(List<Character> input) {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(exponentsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> last = ic1.findLast(classes);

    while (last != null) {

      if (last.getLabel() == null) {
        continue;
      }

      String key = getNextOperandKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(last.getStart(), last.getEnd());

      if (last.getLabel() == ParserUtils.Label.EXPONENT_POS) {

        Operand base = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand power = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        Operand result = BinaryOperation.exponent(base, power);

        operands.put(key, result);

      } else if (last.getLabel() == ParserUtils.Label.EXPONENT_NEG) {

        Operand base = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand power = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        Operand result = BinaryOperation.exponent(base, UnaryOperation.negation(power));

        operands.put(key, result);

      }

      input = LabeledSegment.replace(input, types.get(last.getStart()).getStart(),
          types.get(last.getEnd() - 1).getEnd(), LabeledSegment.characterListOf(key));

      types = ic0.findAll(input);
      classes = LabeledSegment.labelListOf(types);
      last = ic1.findLast(classes);

    }

    return input;

  }

  private List<Character> parseMultiplicationDivision(List<Character> input) {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(multiplicationsDivisionsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> first = ic1.findFirst(classes);

    while (first != null) {

      if (first.getLabel() == null) {
        continue;
      }

      String key = getNextOperandKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(first.getStart(), first.getEnd());

      if (first.getLabel() == ParserUtils.Label.MULTIPLY_POS_POS_IMPLICIT) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));

        Operand result = BinaryOperation.product(o0, o1);

        operands.put(key, result);

      }
      if (first.getLabel() == ParserUtils.Label.MULTIPLY_NEG_POS_IMPLICIT) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        Operand result = BinaryOperation.product(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.MULTIPLY_POS_POS) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        Operand result = BinaryOperation.product(o0, o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.MULTIPLY_POS_NEG) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        Operand result = BinaryOperation.product(o0, UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.MULTIPLY_NEG_POS) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        Operand result = BinaryOperation.product(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.MULTIPLY_NEG_NEG) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(4).sectionOf(input)));

        Operand result =
            BinaryOperation.product(UnaryOperation.negation(o0), UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.DIVIDE_POS_POS) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));



        Operand result = BinaryOperation.division(o0, o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.DIVIDE_POS_NEG) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        Operand result = BinaryOperation.division(o0, UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.DIVIDE_NEG_POS) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        Operand result = BinaryOperation.division(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.DIVIDE_NEG_NEG) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(4).sectionOf(input)));

        Operand result =
            BinaryOperation.division(UnaryOperation.negation(o0), UnaryOperation.negation(o1));

        operands.put(key, result);

      }

      input = LabeledSegment.replace(input, types.get(first.getStart()).getStart(),
          types.get(first.getEnd() - 1).getEnd(), LabeledSegment.characterListOf(key));

      types = ic0.findAll(input);
      classes = LabeledSegment.labelListOf(types);
      first = ic1.findFirst(classes);

    }

    return input;

  }

  private List<Character> parseAddition(List<Character> input) {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(additionsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> first = ic1.findFirst(classes);

    while (first != null) {

      if (first.getLabel() == null) {
        continue;
      }

      String key = getNextOperandKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(first.getStart(), first.getEnd());

      if (first.getLabel() == ParserUtils.Label.ADDITION_POS_POS) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        Operand result = BinaryOperation.sum(o0, o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.ADDITION_POS_NEG) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        Operand result = BinaryOperation.sum(o0, UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.ADDITION_NEG_POS) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        Operand result = BinaryOperation.sum(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.ADDITION_NEG_NEG) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(4).sectionOf(input)));

        Operand result =
            BinaryOperation.sum(UnaryOperation.negation(o0), UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.SUBTRACT_POS_POS) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        Operand result = BinaryOperation.sum(o0, UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.SUBTRACT_POS_NEG) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        Operand result = BinaryOperation.sum(o0, o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.SUBTRACT_NEG_POS) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        Operand result =
            BinaryOperation.sum(UnaryOperation.negation(o0), UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.SUBTRACT_NEG_NEG) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(4).sectionOf(input)));

        Operand result = BinaryOperation.sum(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.ADDITION_IMPLICIT) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        Operand o1 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));

        Operand result = BinaryOperation.sum(o0, o1);

        operands.put(key, result);

      }

      input = LabeledSegment.replace(input, types.get(first.getStart()).getStart(),
          types.get(first.getEnd() - 1).getEnd(), LabeledSegment.characterListOf(key));

      types = ic0.findAll(input);
      classes = LabeledSegment.labelListOf(types);
      first = ic1.findFirst(classes);

    }

    return input;

  }

  private List<Character> parseNegation(List<Character> input) {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(negationsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> first = ic1.findFirst(classes);

    while (first != null) {

      if (first.getLabel() == null) {
        continue;
      }

      String key = getNextOperandKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(first.getStart(), first.getEnd());

      if (first.getLabel() == ParserUtils.Label.NEGATION) {

        Operand o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));

        Operand result = UnaryOperation.negation(o0);

        operands.put(key, result);

      }

      input = LabeledSegment.replace(input, types.get(first.getStart()).getStart(),
          types.get(first.getEnd() - 1).getEnd(), LabeledSegment.characterListOf(key));

      types = ic0.findAll(input);
      classes = LabeledSegment.labelListOf(types);
      first = ic1.findFirst(classes);

    }

    return input;

  }

  private Operand parseMatrix(String input) throws ParserException {

    String[] rows = Utils.splitMatrixRows(input);

    List<Operand> matrixArgs = new ArrayList<>();

    for (String row : rows) {

      String[] cols = Utils.splitVector(row);

      if (rows.length == 1 && cols.length == 1) {
        return parseOperand(cols[0]);
      }

      for (String col : cols) {

        matrixArgs.add(parseOperand(col));

      }

    }

    return new Matrix(matrixArgs, rows.length);
  }

  private Operand parseOperand(String input) throws ParserException {

    List<Character> curr = LabeledSegment.characterListOf(input);

    curr = parseParentheses(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    curr = parsePrimitives(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    curr = parseFunctions(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    curr = parseUnaryOperations(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    curr = parseExponents(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    curr = parseMultiplicationDivision(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    curr = parseNegation(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    curr = parseAddition(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    //System.out.println(operands);
    Operand result = operands.get(LabeledSegment.stringOf(curr));

    if (result == null) {
      throw ParserException.unknownInput(input);
    }

    return result;

  }

}

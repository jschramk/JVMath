package com.jschramk.JVMath.parse;

import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.experimental.TreeLiteral;
import com.jschramk.JVMath.experimental.TreeNode;
import com.jschramk.JVMath.experimental.TreeSum;
import com.jschramk.JVMath.experimental.TreeVariable;
import com.jschramk.JVMath.utils.Utils;
import lexer.DFA;
import lexer.LabeledSegment;
import lexer.Lexer;

import java.util.*;

public class DefaultTreeParser extends TreeParser {

  private static final DFA<ParserUtils.Label, ParserUtils.Label> functionsDFA = ParserUtils.getFunctionsDFA();
  private static final DFA<ParserUtils.Label, ParserUtils.Label> exponentsDFA = ParserUtils.getExponentsDFA();
  private static final DFA<ParserUtils.Label, ParserUtils.Label> multiplicationsDivisionsDFA = ParserUtils
    .getMultiplicationDivisionDFA();
  private static final DFA<ParserUtils.Label, ParserUtils.Label> additionsDFA = ParserUtils.getAdditionsDFA();
  private static final DFA<ParserUtils.Label, ParserUtils.Label> negationsDFA = ParserUtils.getNegationsDFA();
  private final DFA<Character, ParserUtils.Label> primitivesDFA = ParserUtils.getPrimitivesDFA(new HashSet<>());
  private final DFA<Character, ParserUtils.Label> binaryOperationsDFA = ParserUtils.getOperationsDFA(
    new HashSet<>());
  private final DFA<ParserUtils.Label, ParserUtils.Label> unaryOperationsDFA = ParserUtils.getUnaryOperationsDFA();

  private final Map<String, TreeNode> operands = new HashMap<>();
  //private final Map<String, String> variableKeys = new HashMap<>();
  private int nextTreeNodeNum = 0;

  @Override
  public ParseResult parse(String input) throws ParserException {

    long t1 = System.nanoTime();

    String[] sides = input.split("=");

    long t2;
    if (sides.length == 1) {

      TreeNode result = parseTreeNode(input);
      result.serialize();

      t2 = System.nanoTime();

      reset();

      return new ParseResult(input, result, t2 - t1);

    } /*else if (sides.length == 2) {

      Equation result = parseEquation(sides[0], sides[1]);
      result.fixTree();

      t2 = System.nanoTime();

      reset();

      return new ParseResult(input, result, t2 - t1);

    } */ else {
      throw new ParserException("Equations must contain a single \"=\": " + input);
    }

  }

  private void reset() {
    operands.clear();
    //variableKeys.clear();
    nextTreeNodeNum = 0;
  }

  private String getNextTreeNodeKey() {
    return "<" + nextTreeNodeNum++ + ">";
  }

  private List<Character> parseParentheses(List<Character> input) throws ParserException {

    LabeledSegment<Character, Void> parens = ParserUtils.getNextParentheses(input);

    while (parens != null) {

      String key = getNextTreeNodeKey();

      String inside = LabeledSegment.stringOf(input)
        .substring(parens.getStart() + 1, parens.getEnd() - 1);

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

        TreeNode number = new TreeNode(new TreeLiteral(Double.parseDouble(text)));

        String key = getNextTreeNodeKey();

        operands.put(key, number);

        replacements.add(LabeledSegment.characterListOf(key));

      } else if (c.getLabel() == ParserUtils.Label.VARIABLE) {

        sections.add(c);

        String text = LabeledSegment.stringOf(c.sectionOf(input));

        TreeNode variable;

        String key;
        // TODO: reuse same variable instance someday? maybe?
        /*if (variableKeys.containsKey(text)) {
          key = variableKeys.get(text);
          variable = operands.get(key);
        } else {*/

        key = getNextTreeNodeKey();
        //variableKeys.put(text, key);

        variable = new TreeNode(new TreeVariable(text));

        //}

        operands.put(key, variable);

        replacements.add(LabeledSegment.characterListOf(key));

      } /*else if (c.getLabel() == ParserUtils.Label.CONSTANT) {

        sections.add(c);

        String text = LabeledSegment.stringOf(c.sectionOf(input));

        String key = getNextTreeNodeKey();

        Constant constant = Constant.getInstance(text);

        operands.put(key, constant);

        replacements.add(LabeledSegment.characterListOf(key));

      }*/

    }

    List<Character> result = LabeledSegment.replace(input, sections, replacements);

    result = LabeledSegment.characterListOf(LabeledSegment.stringOf(result).replaceAll(" ", ""));

    return result;

  }

  /*private List<Character> parseUnaryOperations(List<Character> input) {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(unaryOperationsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> first = ic1.findFirst(classes);

    while (first != null) {

      if (first.getLabel() == null) {
        continue;
      }

      String key = getNextTreeNodeKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(first.getStart(), first.getEnd());

      if (first.getLabel() == ParserUtils.Label.FACTORIAL) {

        TreeNode arg = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));

        TreeNode result = new UnaryOperation(Operators.FACTORIAL, arg);

        operands.put(key, result);

      }

      input = LabeledSegment.replace(input, types.get(first.getStart()).getStart(),
          types.get(first.getEnd() - 1).getEnd(), LabeledSegment.characterListOf(key));

      types = ic0.findAll(input);
      classes = LabeledSegment.labelListOf(types);
      first = ic1.findLast(classes);

    }

    return input;

  }*/

  /*private List<Character> parseFunctions(List<Character> input) throws ParserException {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(functionsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> first = ic1.findFirst(classes);

    while (first != null) {

      String key = getNextTreeNodeKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(first.getStart(), first.getEnd());

      if (first.getLabel() == ParserUtils.Label.FUNCTION) {

        String name = LabeledSegment.stringOf(section.get(0).sectionOf(input));
        String argsKey = LabeledSegment.stringOf(section.get(1).sectionOf(input));

        TreeNode function;

        try {

          //if (operands.containsKey(argsKey)) {
          //} else {
          //  function = functionDomain.getFunctionInstance(name, vectors.get(argsKey));
          //}

          TreeNode arg = operands.get(argsKey);

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

  }*/

  /*private List<Character> parseExponents(List<Character> input) {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(exponentsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> last = ic1.findLast(classes);

    while (last != null) {

      if (last.getLabel() == null) {
        continue;
      }

      String key = getNextTreeNodeKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(last.getStart(), last.getEnd());

      if (last.getLabel() == ParserUtils.Label.EXPONENT_POS) {

        TreeNode base = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode power = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        TreeNode result = BinaryOperation.exponent(base, power);

        operands.put(key, result);

      } else if (last.getLabel() == ParserUtils.Label.EXPONENT_NEG) {

        TreeNode base = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode power = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        TreeNode result = BinaryOperation.exponent(base, UnaryOperation.negation(power));

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

      String key = getNextTreeNodeKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(first.getStart(), first.getEnd());

      if (first.getLabel() == ParserUtils.Label.MULTIPLY_POS_POS_IMPLICIT) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));

        TreeNode result = BinaryOperation.product(o0, o1);

        operands.put(key, result);

      }
      if (first.getLabel() == ParserUtils.Label.MULTIPLY_NEG_POS_IMPLICIT) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        TreeNode result = BinaryOperation.product(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.MULTIPLY_POS_POS) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        TreeNode result = BinaryOperation.product(o0, o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.MULTIPLY_POS_NEG) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        TreeNode result = BinaryOperation.product(o0, UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.MULTIPLY_NEG_POS) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        TreeNode result = BinaryOperation.product(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.MULTIPLY_NEG_NEG) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(4).sectionOf(input)));

        TreeNode result =
            BinaryOperation.product(UnaryOperation.negation(o0), UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.DIVIDE_POS_POS) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));



        TreeNode result = BinaryOperation.division(o0, o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.DIVIDE_POS_NEG) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        TreeNode result = BinaryOperation.division(o0, UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.DIVIDE_NEG_POS) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        TreeNode result = BinaryOperation.division(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.DIVIDE_NEG_NEG) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(4).sectionOf(input)));

        TreeNode result =
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

  }*/

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

      String key = getNextTreeNodeKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section = types.subList(
        first.getStart(),
        first.getEnd()
      );

      if (first.getLabel() == ParserUtils.Label.ADDITION_POS_POS) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        TreeNode result = new TreeNode(new TreeSum(), o0, o1);

        operands.put(key, result);

      } /*else if (first.getLabel() == ParserUtils.Label.ADDITION_POS_NEG) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        TreeNode result = BinaryOperation.sum(o0, UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.ADDITION_NEG_POS) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        TreeNode result = BinaryOperation.sum(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.ADDITION_NEG_NEG) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(4).sectionOf(input)));

        TreeNode result =
            BinaryOperation.sum(UnaryOperation.negation(o0), UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.SUBTRACT_POS_POS) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(2).sectionOf(input)));

        TreeNode result = BinaryOperation.sum(o0, UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.SUBTRACT_POS_NEG) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        TreeNode result = BinaryOperation.sum(o0, o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.SUBTRACT_NEG_POS) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(3).sectionOf(input)));

        TreeNode result =
            BinaryOperation.sum(UnaryOperation.negation(o0), UnaryOperation.negation(o1));

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.SUBTRACT_NEG_NEG) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(4).sectionOf(input)));

        TreeNode result = BinaryOperation.sum(UnaryOperation.negation(o0), o1);

        operands.put(key, result);

      } else if (first.getLabel() == ParserUtils.Label.ADDITION_IMPLICIT) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(0).sectionOf(input)));
        TreeNode o1 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));

        TreeNode result = BinaryOperation.sum(o0, o1);

        operands.put(key, result);

      }*/

      input = LabeledSegment.replace(input,
        types.get(first.getStart()).getStart(),
        types.get(first.getEnd() - 1).getEnd(),
        LabeledSegment.characterListOf(key)
      );

      types = ic0.findAll(input);
      classes = LabeledSegment.labelListOf(types);
      first = ic1.findFirst(classes);

    }

    return input;

  }

  /*private List<Character> parseNegation(List<Character> input) {

    Lexer<Character, ParserUtils.Label> ic0 = new Lexer<>(binaryOperationsDFA);
    Lexer<ParserUtils.Label, ParserUtils.Label> ic1 = new Lexer<>(negationsDFA);

    List<LabeledSegment<Character, ParserUtils.Label>> types = ic0.findAll(input);

    List<ParserUtils.Label> classes = LabeledSegment.labelListOf(types);

    LabeledSegment<ParserUtils.Label, ParserUtils.Label> first = ic1.findFirst(classes);

    while (first != null) {

      if (first.getLabel() == null) {
        continue;
      }

      String key = getNextTreeNodeKey();

      List<LabeledSegment<Character, ParserUtils.Label>> section =
          types.subList(first.getStart(), first.getEnd());

      if (first.getLabel() == ParserUtils.Label.NEGATION) {

        TreeNode o0 = operands.get(LabeledSegment.stringOf(section.get(1).sectionOf(input)));

        TreeNode result = UnaryOperation.negation(o0);

        operands.put(key, result);

      }

      input = LabeledSegment.replace(input, types.get(first.getStart()).getStart(),
          types.get(first.getEnd() - 1).getEnd(), LabeledSegment.characterListOf(key));

      types = ic0.findAll(input);
      classes = LabeledSegment.labelListOf(types);
      first = ic1.findFirst(classes);

    }

    return input;

  }*/

  private TreeNode parseMatrix(String input) throws ParserException {

    String[] rows = Utils.splitMatrixRows(input);

    List<TreeNode> matrixArgs = new ArrayList<>();

    for (String row : rows) {

      String[] cols = Utils.splitVector(row);

      if (rows.length == 1 && cols.length == 1) {
        return parseTreeNode(cols[0]);
      }

      /*for (String col : cols) {

        matrixArgs.add(TreeNode(col));

      }*/

    }

    return null;//new Matrix(matrixArgs, rows.length);
  }

  private TreeNode parseTreeNode(String input) throws ParserException {

    List<Character> curr = LabeledSegment.characterListOf(input);

    curr = parseParentheses(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    curr = parsePrimitives(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    //curr = parseFunctions(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    //curr = parseUnaryOperations(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    //curr = parseExponents(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    //curr = parseMultiplicationDivision(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    //curr = parseNegation(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    curr = parseAddition(curr);
    //System.out.println(LabeledSegment.stringOf(curr));
    //System.out.println(operands);
    TreeNode result = operands.get(LabeledSegment.stringOf(curr));

    if (result == null) {
      throw ParserException.unknownInput(input);
    }

    return result;

  }

}

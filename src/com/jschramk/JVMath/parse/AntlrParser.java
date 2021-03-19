package com.jschramk.JVMath.parse;

import com.jschramk.JVMath.antlr_gen.jvmathArithmeticLexer;
import com.jschramk.JVMath.antlr_gen.jvmathArithmeticParser;
import com.jschramk.JVMath.antlr_gen.jvmathArithmeticVisitor;
import com.jschramk.JVMath.components.*;
import com.jschramk.JVMath.exceptions.ParserException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import util.PerformanceTimer;

import java.util.*;

public class AntlrParser extends Parser {

  private static PerformanceTimer timer = new PerformanceTimer();
  private static Set<String> mult = getGrammars("*", "×", "⋅");
  private static Set<String> div = getGrammars("/", "÷");
  private static Set<String> neg = getGrammars("-", "−");
  private static jvmathArithmeticVisitor<Operand> visitor = new jvmathArithmeticVisitor<Operand>() {

    @Override
    public Operand visit(ParseTree parseTree) {
      describe(parseTree);
      return null;
    }

    @Override
    public Operand visitChildren(RuleNode ruleNode) {
      return null;
    }

    @Override
    public Operand visitTerminal(TerminalNode terminalNode) {
      return null;
    }

    @Override
    public Operand visitErrorNode(ErrorNode errorNode) {
      throw new IllegalArgumentException("Unexpected input: " + errorNode.getText());
    }

    @Override
    public Operand visitEquation(jvmathArithmeticParser.EquationContext ctx) {
      return null;
    }

    @Override
    public Operand visitSum(jvmathArithmeticParser.SumContext ctx) {

      List<Operand> sumTerms = new ArrayList<>();

      boolean negate = false;

      for (ParseTree child : ctx.children) {

        if (child instanceof TerminalNode) {

          TerminalNode node = (TerminalNode) child;

          if (isNeg(node.getText())) {
            negate = true;
          }

        } else if (child instanceof jvmathArithmeticParser.ProductContext) {

          Operand product = visitProduct((jvmathArithmeticParser.ProductContext) child);

          if (negate) {
            product = UnaryOperation.negation(product);
            negate = false;
          }

          sumTerms.add(product);

        }

      }

      if (sumTerms.size() == 1) {
        return sumTerms.get(0);
      }

      return BinaryOperation.sum(sumTerms.toArray(new Operand[0]));

    }

    @Override
    public Operand visitProduct(jvmathArithmeticParser.ProductContext ctx) {

      Operand curr = null;

      boolean divide = false;

      for (ParseTree child : ctx.children) {

        if (child instanceof TerminalNode) {

          TerminalNode node = (TerminalNode) child;

          if (isDiv(node.getText())) {
            divide = true;
          }

        } else if (child instanceof jvmathArithmeticParser.SignedFactorContext) {

          Operand factor = visitSignedFactor((jvmathArithmeticParser.SignedFactorContext) child);

          if (curr == null) {
            curr = factor;
          } else if (divide) {
            curr = BinaryOperation.division(curr, factor);
            divide = false;
          } else {
            curr = BinaryOperation.product(curr, factor);
          }

        } else if (child instanceof jvmathArithmeticParser.FactorContext) {

          Operand factor = visitFactor((jvmathArithmeticParser.FactorContext) child);

          if (curr == null) {
            curr = factor;
          } else if (divide) {
            curr = BinaryOperation.division(curr, factor);
            divide = false;
          } else {
            curr = BinaryOperation.product(curr, factor);
          }

        }

      }

      return curr;

    }

    @Override
    public Operand visitSignedFactor(jvmathArithmeticParser.SignedFactorContext ctx) {

      List<Operand> powerTerms = new ArrayList<>();

      for (ParseTree child : ctx.children) {

        if (child instanceof jvmathArithmeticParser.AtomContext) {

          powerTerms.add(visitAtom((jvmathArithmeticParser.AtomContext) child));

        } else if (child instanceof jvmathArithmeticParser.SignedAtomContext) {

          powerTerms.add(visitSignedAtom((jvmathArithmeticParser.SignedAtomContext) child));

        }

      }

      if (powerTerms.size() == 1) {
        return powerTerms.get(0);
      }

      Operand exponent = powerTerms.get(powerTerms.size() - 1);

      for (int i = powerTerms.size() - 2; i >= 0; i--) {

        Operand base = powerTerms.get(i);

        exponent = BinaryOperation.exponent(base, exponent);

      }

      return exponent;
    }

    @Override
    public Operand visitFactor(jvmathArithmeticParser.FactorContext ctx) {

      List<Operand> powerTerms = new ArrayList<>();

      for (ParseTree child : ctx.children) {

        if (child instanceof jvmathArithmeticParser.AtomContext) {

          powerTerms.add(visitAtom((jvmathArithmeticParser.AtomContext) child));

        } else if (child instanceof jvmathArithmeticParser.SignedAtomContext) {

          powerTerms.add(visitSignedAtom((jvmathArithmeticParser.SignedAtomContext) child));

        }

      }

      if (powerTerms.size() == 1) {
        return powerTerms.get(0);
      }

      Operand exponent = powerTerms.get(powerTerms.size() - 1);

      for (int i = powerTerms.size() - 2; i >= 0; i--) {

        Operand base = powerTerms.get(i);

        exponent = BinaryOperation.exponent(base, exponent);

      }

      return exponent;
    }

    @Override
    public Operand visitSignedAtom(jvmathArithmeticParser.SignedAtomContext ctx) {

      boolean negate = false;

      for (ParseTree child : ctx.children) {

        if (child instanceof TerminalNode) {

          TerminalNode node = (TerminalNode) child;

          if (isNeg(node.getText())) {
            negate = true;
          }

        } else if (child instanceof jvmathArithmeticParser.AtomContext) {

          Operand operand = visitAtom((jvmathArithmeticParser.AtomContext) child);

          if (negate) {
            operand = UnaryOperation.negation(operand);
          }

          return operand;

        } else if (child instanceof jvmathArithmeticParser.SignedAtomContext) {

          Operand operand = visitSignedAtom((jvmathArithmeticParser.SignedAtomContext) child);

          if (negate) {
            operand = UnaryOperation.negation(operand);
          }

          return operand;

        }

      }

      throw new RuntimeException("visitSignedAtom() error");

    }



    @Override
    public Operand visitAtom(jvmathArithmeticParser.AtomContext ctx) {

      if (ctx.getChildCount() == 1) {

        ParseTree child = ctx.getChild(0);

        if (child instanceof jvmathArithmeticParser.LiteralContext) {

          return visitLiteral((jvmathArithmeticParser.LiteralContext) child);

        } else if (child instanceof jvmathArithmeticParser.VariableContext) {

          return visitVariable((jvmathArithmeticParser.VariableContext) child);

        } else {
          throw new RuntimeException("atom has unsupported type");
        }

      } else if (ctx.getChildCount() == 3) {

        ParseTree child = ctx.getChild(1);

        if (child instanceof jvmathArithmeticParser.SumContext) {

          return visitSum((jvmathArithmeticParser.SumContext) child);

        }

        throw new RuntimeException("atom has unsupported type");

      }

      throw new RuntimeException("visitAtom() error");

    }

    @Override
    public Operand visitLiteral(jvmathArithmeticParser.LiteralContext ctx) {
      return new Literal(Double.parseDouble(ctx.getText()));
    }

    @Override
    public Operand visitVariable(jvmathArithmeticParser.VariableContext ctx) {
      return new Variable(ctx.getText());
    }

    @Override
    public Operand visitComparator(jvmathArithmeticParser.ComparatorContext ctx) {
      return null;
    }

  };
  public AntlrParser(FunctionDomain functionDomain) {
    super(functionDomain);
  }

  private static Set<String> getGrammars(String... strings) {
    return new HashSet<>(Arrays.asList(strings));
  }

  public static void main(String[] args) {
    printGrammar("MINUS", neg);
    printGrammar("TIMES", mult);
    printGrammar("DIV", div);
  }

  public static void printGrammar(String name, Set<String> grammar) {

    StringBuilder s = new StringBuilder();

    for (String string : grammar) {

      if (string.length() == 1) {

        char c = string.charAt(0);

        if (s.length() > 0) {
          s.append(" | ");
        }

        s.append("'");
        s.append(unicode(c));

      } else {
        s.append("'");
        s.append(string);
      }
      s.append("'");

    }

    System.out.println(name + ": " + s.toString() + " ;");

  }

  public static String unicode(char c) {
    return "\\u" + Integer.toHexString(c | 0x10000).substring(1);
  }

  private static boolean isDiv(String s) {
    return div.contains(s);
  }

  private static boolean isNeg(String s) {
    return neg.contains(s);
  }

  private static void describe(ParseTree tree) {
    describe(tree, 0);
  }

  private static void describe(ParseTree tree, int level) {

    StringBuilder s = new StringBuilder();

    for (int i = 0; i < level; i++) {
      s.append('\t');
    }

    System.out.println(s.toString() + "\"" + tree.getText() + "\" (" + tree.getClass()
      .getSimpleName() + ")");
    for (int i = 0; i < tree.getChildCount(); i++) {

      ParseTree child = tree.getChild(i);

      if (child instanceof ErrorNode) {
        throw new IllegalArgumentException("Unexpected Input: \"" + child.getText() + "\"");
      }

      describe(child, level + 1);
    }
  }

  @Override
  public ParseResult parse(String input) throws ParserException {

    timer.start();

    CharStream in = CharStreams.fromString(input);
    jvmathArithmeticLexer lexer = new jvmathArithmeticLexer(in);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    jvmathArithmeticParser parser = new jvmathArithmeticParser(tokens);

    Operand result = visitor.visitSum(parser.sum());

    timer.stop();

    return new ParseResult(input, result, timer.ns());

  }

}

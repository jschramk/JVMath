/*package core.test;

import core.components.Equation;
import core.components.FunctionDomain;
import core.components.Operand;
import core.components.Variable;
import core.exceptions.ParserException;
import core.parse.DefaultParser;
import core.parse.Parser;
import core.rewrite.StructureMatcher2;

import java.util.HashMap;
import java.util.Map;

public class TestRuleMatch {

  //TODO: change matching rules to accept sums out of order when matching directly i.e. x + y & y + x
  //TODO: make better test code to allow for recognition of correct arbitrary assignments
  public static void main(String[] args) throws ParserException {

    Parser p = new DefaultParser(FunctionDomain.getInstanceWithBuiltIns());

    testVariableAssignment(p, "7 + 2^(x + 2y)*5", "a b^(c + d) + e",
        "a = 5; b = 2; c = x; d = 2y; e = 7");
    testVariableAssignment(p, "q + z q + z^2*12", "a x^2 + b x + c", "a = 12; b = q; c = q; x = z");
    testVariableAssignment(p, "(x+y) * (x+y)^2", "a * a^2", "a = x + y");
    testVariableAssignment(p, "5x + 4x", "a x + b x", "a = 5; b = 4; x = x");
    testVariableAssignment(p, "5x^2 + 4x", "x^a + x^b", null);
    testVariableAssignment(p, "2^(3+4+5)", "x^(a+b)", null);

    System.out.println("\n\n\n! ! !   ALL TESTS PASSED   ! ! !");

  }


  private static void testVariableAssignment(Parser p, String input, String rule, String variables)
      throws ParserException {

    System.out.println("///////////////////////////////////////////////////////////////////////");
    System.out.println("Testing: " + input + ", " + rule);
    System.out.println("///////////////////////////////////////////////////////////////////////");

    Operand inputOp = p.parse(input).getOperand().copy();
    Operand ruleOp = p.parse(rule).getOperand().copy();

    Operand.assignInstanceIds(inputOp);
    Operand.assignInstanceIds(ruleOp);

    Map<String, Operand> expectedVariables = variables != null ? getOperandMap(p, variables) : null;

    Map<String, Operand> foundVariables = StructureMatcher2.getMatchVariables(inputOp, ruleOp);

    if (foundVariables == null || expectedVariables == null) {
      if (foundVariables != expectedVariables) {
        throw new RuntimeException(
            "Test failed: found: " + foundVariables + ", expected: " + expectedVariables);
      } else {
        return;
      }
    }

    System.out.println(foundVariables);

    for (String var : foundVariables.keySet()) {


      Operand expected = expectedVariables.get(var);
      Operand actual = foundVariables.get(var);

      if (!expected.equals(actual)) {
        throw new RuntimeException(
            "Variable \"" + var + "\" does not match: expected: " + expected + ", actual: "
                + actual);
      }


    }

  }

  private static Map<String, Operand> getOperandMap(Parser p, String listedVariables)
      throws ParserException {

    Map<String, Operand> variables = new HashMap<>();

    String[] assignments = listedVariables.split(";");

    for (String assign : assignments) {

      Equation equation = p.parse(assign).getEquation();

      assert equation.getLeftSide() instanceof Variable;

      variables.put(((Variable) equation.getLeftSide()).getName(), equation.getRightSide());

    }

    return variables;

  }


}*/

package core.old;

import core.components.FunctionOperand;
import core.components.Operand;

import java.util.List;

@Deprecated
public class Rad extends FunctionOperand {

  @Override public String getName() {
    return "rad";
  }

  @Override public Operand newInstance(List<Operand> args) {
    return new Rad().withChildren(args);
  }

  @Override public double evaluate() {
    return Math.toRadians(getChild(0).evaluate());
  }

}

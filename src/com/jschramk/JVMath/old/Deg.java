package core.old;

import core.components.FunctionOperand;
import core.components.Operand;

import java.util.List;

@Deprecated
public class Deg extends FunctionOperand {

  @Override public String getName() {
    return "deg";
  }

  @Override public Operand newInstance(List<Operand> args) {
    return new Deg().withChildren(args);
  }

  @Override public double evaluate() {
    return Math.toDegrees(getChild(0).evaluate());
  }

}

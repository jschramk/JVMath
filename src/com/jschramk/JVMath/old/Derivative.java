package core.old;

import core.components.FunctionOperand;
import core.components.Operand;
import core.components.Variable;

import java.util.List;

@Deprecated
public class Derivative extends FunctionOperand {

  @Override public String getName() {
    return "dr";
  }

  @Override public Operand newInstance(List<Operand> args) {

    assert args.get(1) instanceof Variable;

    return new Derivative().withChildren(args);
  }

  @Override public double evaluate() {
    return 0;
  }
}

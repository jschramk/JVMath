package core.old;

import core.components.FunctionOperand;
import core.components.Operand;

import java.util.List;

@Deprecated
public class Log extends FunctionOperand {

  @Override public String getName() {
    return "log";
  }

  @Override public Operand newInstance(List<Operand> args) {
    return new Log().withChildren(args);
  }

  @Override public double evaluate() {
    return Math.log(getChild(1).evaluate()) / Math.log(getChild(0).evaluate());
  }

}

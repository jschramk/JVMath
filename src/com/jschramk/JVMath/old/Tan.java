package core.old;

import core.components.Function;
import core.components.Operand;

import java.util.List;

@Deprecated
public class Tan extends Function {

    @Override
    public String getName() {
        return "tan";
    }

    @Override
    public Operand newInstance(List<Operand> args) {
        return new Tan().withChildren(args);
    }

    @Override
    public double evaluate() {
        return Math.tan(getChild(0).evaluate());
    }

}

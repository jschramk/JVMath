package core.old;

import core.components.Function;
import core.components.Operand;

import java.util.List;

@Deprecated
public class Cos extends Function {

    @Override
    public String getName() {
        return "cos";
    }

    @Override
    public Operand newInstance(List<Operand> args) {
        return new Cos().withChildren(args);
    }

    @Override
    public double evaluate() {
        return Math.cos(getChild(0).evaluate());
    }

}

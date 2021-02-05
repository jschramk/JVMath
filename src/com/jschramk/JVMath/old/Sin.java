package core.old;

import core.components.Function;
import core.components.Operand;

import java.util.List;

@Deprecated
public class Sin extends Function {

    @Override
    public String getName() {
        return "sin";
    }

    @Override
    public Operand newInstance(List<Operand> args) {
        return new Sin().withChildren(args);
    }

    @Override
    public double evaluate() {
        return Math.sin(getChild(0).evaluate());
    }

}

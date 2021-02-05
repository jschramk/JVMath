package core.old;

import core.components.Function;
import core.components.Operand;

import java.util.List;

@Deprecated
public class ArcCos extends Function {

    @Override
    public String getName() {
        return "acos";
    }

    @Override
    public Operand newInstance(List<Operand> args) {
        return new ArcCos().withChildren(args);
    }

    @Override
    public double evaluate() {
        return Math.acos(getChild(0).evaluate());
    }

}

package core.old;

import core.components.Function;
import core.components.Operand;

import java.util.List;

@Deprecated
public class ArcTan extends Function {

    @Override
    public String getName() {
        return "atan";
    }

    @Override
    public Operand newInstance(List<Operand> args) {
        return new ArcTan().withChildren(args);
    }

    @Override
    public double evaluate() {
        return Math.atan(getChild(0).evaluate());
    }

}

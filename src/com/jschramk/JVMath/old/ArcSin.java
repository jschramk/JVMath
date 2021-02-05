package core.old;

import core.components.Function;
import core.components.Operand;

import java.util.List;

@Deprecated
public class ArcSin extends Function {

    @Override
    public String getName() {
        return "asin";
    }

    @Override
    public Operand newInstance(List<Operand> args) {
        return new ArcSin().withChildren(args);
    }

    @Override
    public double evaluate() {
        return Math.asin(getChild(0).evaluate());
    }

}

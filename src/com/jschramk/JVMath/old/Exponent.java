package core.old;

import core.components.Literal;
import core.components.Operand;
import core.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Deprecated
public class Exponent extends Operand {

    public Exponent(Operand base, Operand power) {
        children = Arrays.asList(base.setParent(this), power.setParent(this));
    }

    public static boolean sameBase(Operand o0, Operand o1) {
        if (o0 instanceof Exponent) {
            if (o1 instanceof Exponent) {
                return ((Exponent) o0).getBase().equals(((Exponent) o1).getBase());
            } else {
                return ((Exponent) o0).getBase().equals(o1);
            }
        } else {
            if (o1 instanceof Exponent) {
                return ((Exponent) o1).getBase().equals(o0);
            } else {
                return o0.equals(o1);
            }
        }
    }

    public static Exponent productOfSameBases(List<Operand> operandList) {

        List<Operand> powers = new ArrayList<>();

        Operand base = getBase(operandList.get(0));

        for (Operand op : operandList) {

            if (!getBase(op).equals(base)) {
                throw new IllegalArgumentException("Not all bases are the same");
            }

            powers.add(getPower(op));

        }

        return new Exponent(base, new Sum(powers));

    }

    public static Operand getPower(Operand operand) {
        if (operand instanceof Exponent) {
            return ((Exponent) operand).getPower();
        } else {
            return new Literal(1);
        }
    }

    public static Operand getBase(Operand operand) {
        if (operand instanceof Exponent) {
            return ((Exponent) operand).getBase();
        } else {
            return operand;
        }
    }

    @Override
    public double evaluate() {
        return Math.pow(getBase().evaluate(), getPower().evaluate());
    }

    @Override
    public Type getType() {
        return Type.EXPONENT;
    }

    @Override
    public String toDisplayString() {
        return "(" + getBase().toString() + ")^" + Operand.childPriorityString(this, getPower(), null);
    }

    @Override
    public Operand importFromVariableDomain() {
        return new Exponent(getBase().importFromVariableDomain(),
                getPower().importFromVariableDomain());
    }

    @Override
    public Operand getCanonical() {

        if (getBase().getType() == Type.LITERAL && getBase().evaluate() == 0) {
            return getBase();
        }

        if (getPower().getType() == Type.LITERAL && getPower().evaluate() == 0) {
            return new Literal(1);
        }

        if (getPower().getType() == Type.LITERAL && getPower().evaluate() == 1) {
            return getBase().getCanonical();
        }

        if (getPower().getType() == Type.LITERAL && getBase().getType() != Type.VARIABLE) {

            double pow = getPower().evaluate();

            if (Utils.isInteger(pow)) {

                List<Operand> operandList = new ArrayList<>();

                int absPow = (int) Math.abs(pow);

                for (int i = 0; i < absPow; i++) {
                    operandList.add(getBase());
                }

                Operand product = new Product(Operand.canonicalList(operandList)).getCanonical();

                if (pow > 0) {
                    return product.getCanonical();
                } else {
                    return Product.division(new Literal(1), product.getCanonical());
                }

            } else {

                Operand canonicalPower = getPower().getCanonical();

                if (canonicalPower instanceof Literal && canonicalPower.evaluate() == 1) {
                    return getBase().getCanonical();
                } else {
                    return new Exponent(getBase().getCanonical(), canonicalPower);
                }

            }

        } else {

            Operand canonicalPower = getPower().getCanonical();

            if (canonicalPower instanceof Literal && canonicalPower.evaluate() == 1) {
                return getBase().getCanonical();
            } else {
                return new Exponent(getBase().getCanonical(), canonicalPower);
            }

        }

    }

    @Override
    public Operand copy() {
        return new Exponent(getBase().copy(), getPower().copy());
    }

    public Operand getBase() {
        return getChild(0);
    }

    public Operand getPower() {
        return getChild(1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBase(), getPower());
    }

    public boolean baseEquals(Operand operand) {
        return getBase().equals(operand);
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Exponent)) {
            return false;
        }

        Exponent exponent = (Exponent) o;

        return exponent.getBase().equals(getBase()) && exponent.getPower().equals(getPower());

    }

}

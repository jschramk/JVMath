package core.old;

import core.components.Literal;
import core.components.Operand;

import java.util.*;

import static core.components.Operand.Type.PRODUCT;

@Deprecated
public class Product extends Operand {

    public Product(Operand... children) {
        this(Arrays.asList(children));
    }

    public Product(List<Operand> children) {

        if (children.size() == 0) {
            throw new IllegalArgumentException("Product must have at least one child");
        }

        this.children = new ArrayList<>();

        for (Operand multipliedOperand : children) {

            if (multipliedOperand.getType() == PRODUCT) {

                for (Operand child : multipliedOperand.children) {
                    this.children.add(child.setParent(this));
                }

            } else {
                this.children.add(multipliedOperand.setParent(this));
            }

        }

    }

    public static Product negation(Operand operand) {
        return new Product(new Literal(-1), operand);
    }

    public static Product division(Operand numerator, Operand denominator) {
        return new Product(numerator, new Exponent(denominator, new Literal(-1)));
    }

    @Override
    public double evaluate() {

        double product = 1;

        for (Operand op : children) {
            product *= op.evaluate();
        }

        return product;
    }

    @Override
    public Type getType() {
        return Type.PRODUCT;
    }

    @Override
    public Operand importFromVariableDomain() {
        return new Product(Operand.importedList(children));
    }

    @Override public Operand getCanonical() {
        return null;
    }

    @Override
    public String toDisplayString() {

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < children.size(); i++) {

            if (i > 0) {
                s.append("*");
            }

            //s.append("(");
            s.append(Operand.childPriorityString(this, children.get(i), null));
            //s.append(")");

        }

        return s.toString();
    }


    /*
    @Override
    public Operand getCanonical() {

        Product product = new Product(Operand.canonicalList(children));

        for (int i = 0; i < product.children.size() - 1; ) {

            Operand o0 = product.children.get(i);
            Operand o1 = product.children.get(i + 1);

            if (o0 instanceof Sum) {

                Sum sum = (Sum) o0;

                Sum distributed = sum.distribute(o1);

                product.children = product.children.subList(i + 2, product.children.size());

                product.children.add(0, distributed);

            } else if (o1 instanceof Sum) {

                Sum sum = (Sum) o1;

                Sum distributed = sum.distribute(o0);

                product.children = product.children.subList(i + 2, product.children.size());

                product.children.add(0, distributed);

            } else {
                i++;
            }

        }

        if (product.children.size() == 2) {

            Operand o0 = product.children.get(0);
            Operand o1 = product.children.get(1);

            if (o0 instanceof Sum) {

                Sum sum = (Sum) o0;

                return sum.distribute(o1).getCanonical();

            } else if (o1 instanceof Sum) {

                Sum sum = (Sum) o1;

                return sum.distribute(o0).getCanonical();

            }

        }

        if (product.children.size() == 1) {
            return product.children.get(0).getCanonical();
        }

        return product.combineLikeTerms();

    }*/

    @Override
    public Operand copy() {
        return new Product(Operand.copyList(children));
    }

    /*
    public Operand combineLikeTerms() {

        Map<Operand, List<Operand>> powerMap = new HashMap<>();

        double literalTotal = 1;

        for (Operand operand : children) {

            if (operand instanceof Literal) {

                literalTotal *= operand.evaluate();

            } else if (operand instanceof Exponent) {

                Exponent exponent = (Exponent) operand;

                if (!powerMap.containsKey(exponent.getBase())) {
                    powerMap.put(exponent.getBase(), new ArrayList<>());
                }

                powerMap.get(exponent.getBase()).add(exponent.getPower());

            } else {

                if (!powerMap.containsKey(operand)) {
                    powerMap.put(operand, new ArrayList<>());
                }

                powerMap.get(operand).add(new Literal(1));

            }

        }

        List<Operand> newTerms = new ArrayList<>();

        for (Operand operand : powerMap.keySet()) {

            List<Operand> powerSumList = powerMap.get(operand);

            if (powerSumList.size() == 1 && powerSumList.get(0) instanceof Literal
                    && powerSumList.get(0).evaluate() == 1) {
                newTerms.add(operand);
            } else {
                newTerms.add(new Exponent(operand, new Sum(powerSumList).combineLikeTerms()));
            }

        }

        if (literalTotal != 1 || newTerms.isEmpty()) {
            newTerms.add(new Literal(literalTotal));
        }

        if (newTerms.size() == 1) {
            return newTerms.get(0);
        }

        return new Product(newTerms);

    }*/

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Product)) {
            return false;
        }

        Product product = (Product) o;

        return Objects.equals(children, product.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

}

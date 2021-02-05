package core.old;

import core.components.Literal;
import core.components.Operand;

import java.util.*;

import static core.components.Operand.Type.SUM;

@Deprecated
public class Sum extends Operand {

    public Sum(List<Operand> children) {

        if (children.size() == 0) {
            throw new IllegalArgumentException("Sum must have at least one child");
        }

        this.children = new ArrayList<>();

        for (Operand summedOperand : children) {

            if (summedOperand.getType() == SUM) {

                for (Operand child : summedOperand.getChildren()) {
                    this.children.add(child.setParent(this));
                }

            } else {
                this.children.add(summedOperand.setParent(this));
            }

        }

    }

    public Sum(Operand... children) {
        this(Arrays.asList(children));
    }

    @Override
    public double evaluate() {

        double sum = 0;

        for (Operand op : children) {
            sum += op.evaluate();
        }

        return sum;
    }

    @Override
    public Type getType() {
        return SUM;
    }

    @Override
    public Operand importFromVariableDomain() {
        return new Sum(Operand.importedList(children));
    }

    @Override
    public String toDisplayString() {

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < children.size(); i++) {
            if (i > 0) {
                s.append(" + ");
            }
            s.append(Operand.childPriorityString(this, children.get(i), null));
        }

        return s.toString();
    }


    @Override
    public Operand getCanonical() {

        Sum sum = new Sum(Operand.canonicalList(children));

        if (sum.children.size() == 1) {
            return sum.children.get(0).getCanonical();
        }

        return sum.combineLikeTerms();
    }

    @Override
    public Operand copy() {
        return new Sum(Operand.copyList(children));
    }

    public Operand combineLikeTerms() {

        Map<Operand, Integer> countMap = new HashMap<>();

        double literalTotal = 0;

        for (Operand operand : children) {

            if (operand instanceof Literal) {

                literalTotal += operand.evaluate();

            } else if (!countMap.containsKey(operand)) {
                countMap.put(operand, 1);
            } else {
                countMap.put(operand, countMap.get(operand) + 1);
            }

        }

        List<Operand> newTerms = new ArrayList<>();

        for (Operand operand : countMap.keySet()) {

            int count = countMap.get(operand);

            if (count > 1) {
                newTerms.add(new Product(new Literal(count), operand));
            } else {
                newTerms.add(operand);
            }

        }

        if (literalTotal != 0 || newTerms.isEmpty()) {
            newTerms.add(new Literal(literalTotal));
        }

        if (newTerms.size() == 1) {
            return newTerms.get(0);
        }

        return new Sum(newTerms);

    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Sum)) {
            return false;
        }

        Sum sum = (Sum) o;

        return Objects.equals(children, sum.children);

    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

    public Sum distribute(Operand operand) {

        List<Operand> distributedList = new ArrayList<>();

        if (operand.getType() == SUM) {

            Sum sum = (Sum) operand;

            for (int i = 0; i < children.size(); i++) {
                for (int j = 0; j < sum.children.size(); j++) {
                    distributedList.add(new Product(children.get(i), sum.children.get(j)));
                }
            }

        } else {

            for (Operand op : children) {
                distributedList.add(new Product(operand, op));
            }

        }

        return new Sum(distributedList);

    }

}

package com.jschramk.JVMath.runtime.math_engine;

import com.google.gson.JsonObject;
import com.jschramk.JVMath.runtime.components.Enums;
import com.jschramk.JVMath.runtime.components.Literal;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.rewrite_resources.ExternalRequirements;

public class VariableFilter {

    private static final String EXTERNAL = "external";
    private static final String CONTAINS_TARGET_VARIABLE = "contains solve";
    private static final String REQUIRED_TYPE = "type";
    private static final String REQUIRED_NOT_TYPE = "not type";
    private static final String REQUIRED_VALUE = "value";
    private static final String REQUIRED_NOT_VALUE = "not value";
    private static final String VARIABLE_NAME = "variable";

    private String variable;
    private Enums.OperandType requiredType;
    private Enums.OperandType requiredNotType;
    private Boolean containsTargetVariable = null;
    private Double requiredValue = null;
    private Double requiredNotValue = null;
    private FilterChecker checker;

    private VariableFilter(String name) {
        this.variable = name;
    }

    public static VariableFilter fromJson(JsonObject object) {

        String variable = object.get(VARIABLE_NAME).getAsString();

        VariableFilter variableFilter = new VariableFilter(variable);

        if (object.has(EXTERNAL)) {

            String name = object.get(EXTERNAL).getAsString();

            try {

                variableFilter.checker =
                    (FilterChecker) ExternalRequirements.class.getField(name).get(null);

                return variableFilter;

            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException("Unable to access external FilterChecker: " + name);
            }

        }

        if (object.has(REQUIRED_TYPE)) {
            variableFilter.type(Enums.OperandType.valueOf(object.get(REQUIRED_TYPE).getAsString()));
        }
        if (object.has(REQUIRED_NOT_TYPE)) {
            variableFilter.notType(
                Enums.OperandType.valueOf(object.get(REQUIRED_NOT_TYPE).getAsString()));
        }
        if (object.has(CONTAINS_TARGET_VARIABLE)) {
            variableFilter.containsTarget(object.get(CONTAINS_TARGET_VARIABLE).getAsBoolean());
        }
        if (object.has(REQUIRED_VALUE)) {
            variableFilter.value(object.get(REQUIRED_VALUE).getAsDouble());
        }
        if (object.has(REQUIRED_NOT_VALUE)) {
            variableFilter.notValue(object.get(REQUIRED_NOT_VALUE).getAsDouble());
        }

        return variableFilter;

    }

    public VariableFilter type(Enums.OperandType requiredType) {
        this.requiredType = requiredType;
        return this;
    }

    public VariableFilter notType(Enums.OperandType requiredType) {
        this.requiredNotType = requiredType;
        return this;
    }

    public VariableFilter containsTarget(boolean containsSolveVariable) {
        this.containsTargetVariable = containsSolveVariable;
        return this;
    }

    public VariableFilter value(double value) {
        requiredValue = value;
        return this;
    }

    public VariableFilter notValue(double value) {
        requiredNotValue = value;
        return this;
    }

    public String getVariable() {
        return variable;
    }

    public boolean passes(Operand operand, String targetVariable) {

        if (checker != null) {
            return checker.passes(operand, targetVariable);
        }

        if (requiredType != null && operand.getType() != requiredType) {
            return false;
        }

        if (requiredNotType != null && operand.getType() == requiredNotType) {
            return false;
        }

        if (containsTargetVariable != null && targetVariable != null
            && containsTargetVariable != operand.getVariables().contains(targetVariable)) {
            return false;
        }

        //TODO: make sure this works, basically makes it so rules that require a target variable do not get used when there is no target
        if (containsTargetVariable != null && containsTargetVariable && targetVariable == null) {
            return false;
        }

        if (requiredValue != null && operand instanceof Literal
            && operand.computeToDouble() != requiredValue) {
            return false;
        }

        if (requiredNotValue != null && operand instanceof Literal
            && operand.computeToDouble() == requiredNotValue) {
            return false;
        }

        return true;

    }

    public interface FilterChecker {

        boolean passes(Operand operand, String targetVariable);

    }

}

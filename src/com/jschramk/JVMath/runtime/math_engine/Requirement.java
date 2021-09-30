package com.jschramk.JVMath.runtime.math_engine;

import com.google.gson.JsonObject;
import com.jschramk.JVMath.runtime.components.Enums;
import com.jschramk.JVMath.runtime.components.Literal;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.rewrite_resources.ExternalRequirements;

public class Requirement {

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
    private RequirementChecker checker;

    private Requirement(String name) {
        this.variable = name;
    }

    public static Requirement fromJson(JsonObject object) {

        String variable = object.get(VARIABLE_NAME).getAsString();

        Requirement requirement = new Requirement(variable);

        if (object.has(EXTERNAL)) {

            String name = object.get(EXTERNAL).getAsString();

            try {

                requirement.checker =
                    (RequirementChecker) ExternalRequirements.class.getField(name).get(null);

                return requirement;

            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException("Unable to access external RequirementChecker: " + name);
            }

        }

        if (object.has(REQUIRED_TYPE)) {
            requirement.type(Enums.OperandType.valueOf(object.get(REQUIRED_TYPE).getAsString()));
        }
        if (object.has(REQUIRED_NOT_TYPE)) {
            requirement
                .notType(Enums.OperandType.valueOf(object.get(REQUIRED_NOT_TYPE).getAsString()));
        }
        if (object.has(CONTAINS_TARGET_VARIABLE)) {
            requirement.containsTarget(object.get(CONTAINS_TARGET_VARIABLE).getAsBoolean());
        }
        if (object.has(REQUIRED_VALUE)) {
            requirement.value(object.get(REQUIRED_VALUE).getAsDouble());
        }
        if (object.has(REQUIRED_NOT_VALUE)) {
            requirement.notValue(object.get(REQUIRED_NOT_VALUE).getAsDouble());
        }

        return requirement;

    }

    public Requirement type(Enums.OperandType requiredType) {
        this.requiredType = requiredType;
        return this;
    }

    public Requirement notType(Enums.OperandType requiredType) {
        this.requiredNotType = requiredType;
        return this;
    }

    public Requirement containsTarget(boolean containsSolveVariable) {
        this.containsTargetVariable = containsSolveVariable;
        return this;
    }

    public Requirement value(double value) {
        requiredValue = value;
        return this;
    }

    public Requirement notValue(double value) {
        requiredNotValue = value;
        return this;
    }

    public String getVariable() {
        return variable;
    }

    public boolean meetsMatchRequirements(Operand operand, String targetVariable) {

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

    public interface RequirementChecker {

        boolean passes(Operand operand, String targetVariable);

    }

}

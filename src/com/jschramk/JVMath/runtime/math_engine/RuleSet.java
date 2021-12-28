package com.jschramk.JVMath.runtime.math_engine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.parse.Parser;

import java.util.ArrayList;
import java.util.List;

public class RuleSet {

    //private Map<String, Integer>
    private List<String> fileNames = new ArrayList<>();
    private List<Rule<Operand>> operandRules = new ArrayList<>();
    private List<Rule<Equation>> equationRules = new ArrayList<>();

    public void addJSON(Parser parser, JsonObject jsonObject) {

        JsonArray array = jsonObject.getAsJsonArray();

        for (JsonElement element : array) {

            Rule<String> stringRule = Rule.fromJson(element.getAsJsonObject());



        }

    }

    public List<Rule<Operand>> getOperandRules() {
        return operandRules;
    }

    public List<Rule<Equation>> getEquationRules() {
        return equationRules;
    }
}

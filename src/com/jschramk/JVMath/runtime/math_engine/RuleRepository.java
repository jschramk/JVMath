package com.jschramk.JVMath.runtime.math_engine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.parse.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleRepository {

    //private Map<String, Integer>
    private Map<Integer, String> fileNames = new HashMap<>();
    private Map<String, List<Rule<?>>> rules = new HashMap<>();

    public void addJSON(Parser parser, JsonObject jsonObject) {

        JsonArray array = jsonObject.getAsJsonArray();

        for (JsonElement element : array) {

            Rule<String> stringRule = Rule.fromJson(element.getAsJsonObject());

        }

    }

}

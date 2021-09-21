package com.jschramk.JVMath.runtime.rewrite_packages;

import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.Parser;
import com.jschramk.JVMath.runtime.rewrite_engine.Requirement;
import com.jschramk.JVMath.runtime.rewrite_engine.Rule;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageLoader {

private static Map<String, Object> ruleSets = new HashMap<>();
private static Map<Integer, Rule<?>> rulesById = new HashMap<>();
private static Map<Integer, Requirement> requirementsById = new HashMap<>();

static {

    try {

        InputStream simplify = PackageLoader.class.getResourceAsStream(
            "simplify.processed.json");
        InputStream solve = PackageLoader.class.getResourceAsStream(
            "solve.processed.json");

        Parser parser = Parser.getDefault();

        loadRuleSet("simplify", parser, simplify, Operand.class);
        loadRuleSet("solve", parser, solve, Equation.class);

    } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

}

public static <T> Rule<T> getRule(int id, Class<T> type) {

    if (rulesById.containsKey(id)) {
        return (Rule<T>) rulesById.get(id);
    }

    throw new IllegalArgumentException("No operand rule with ID: " + id);

}

public static <T> List<Rule<T>> getRuleSet(String name, Class<T> type) {
    if (!ruleSets.containsKey(name)) {
        throw new IllegalArgumentException("No operand rule set \"" +
            name +
            "\"");
    }
    return (List<Rule<T>>) ruleSets.get(name);
}



private static <T> void loadRuleSet(
    String name, Parser parser, InputStream inputStream, Class<T> type
) throws ParserException {

    List<Rule<String>> stringRules = Rule.getRules(inputStream);

    List<Rule<T>> equationRules = Rule.convertRules(parser, stringRules, type);

    for (Rule<T> rule : equationRules) {

        if (rule.getId() != -1) {
            rulesById.put(rule.getId(), rule);
        }
    }

    ruleSets.put(name, equationRules);
}

}

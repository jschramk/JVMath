package com.jschramk.JVMath.rewrite_packages;

import com.jschramk.JVMath.components.Equation;
import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.parse.Parser;
import com.jschramk.JVMath.rewrite_engine.Requirement;
import com.jschramk.JVMath.rewrite_resources.Rule;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewriteResources {

private static Map<String, Object> ruleSets = new HashMap<>();
private static Map<Integer, Rule<?>> rulesById = new HashMap<>();
private static Map<Integer, Requirement> requirementsById = new HashMap<>();

static {

    try {

        InputStream simplify = RewriteResources.class.getResourceAsStream(
            "simplify_processed.json");
        InputStream solve = RewriteResources.class.getResourceAsStream(
            "solve_processed.json");

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

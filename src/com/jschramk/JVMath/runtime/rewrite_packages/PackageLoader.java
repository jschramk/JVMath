package com.jschramk.JVMath.runtime.rewrite_packages;

import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.math_engine.VariableFilter;
import com.jschramk.JVMath.runtime.math_engine.Rule;
import com.jschramk.JVMath.runtime.parse.Parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageLoader {

    private static Map<String, Object> ruleSets = new HashMap<>();
    private static Map<Integer, Rule<?>> rulesById = new HashMap<>();
    private static Map<Integer, VariableFilter> requirementsById = new HashMap<>();

    static {

        try {

            Parser parser = Parser.getDefault();

            /*InputStream loadOrder = PackageLoader.class.getResourceAsStream("load_order.txt");

            Scanner sc = new Scanner(loadOrder);


            while (sc.hasNextLine()) {

                String s = sc.nextLine().trim().replaceAll("//.*", "");

                if(s.isEmpty()) continue;

                InputStream rules = PackageLoader.class.getResourceAsStream(s);

                JsonObject obj = JsonParser.parseReader(new InputStreamReader(rules)).getAsJsonObject();

            }*/

            InputStream simplify =
                PackageLoader.class.getResourceAsStream("simplify.processed.json");
            InputStream solve = PackageLoader.class.getResourceAsStream("solve.processed.json");



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
            throw new IllegalArgumentException("No operand rule set \"" + name + "\"");
        }
        return (List<Rule<T>>) ruleSets.get(name);
    }



    private static <T> void loadRuleSet(String name, Parser parser, InputStream inputStream,
        Class<T> type) throws ParserException {

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

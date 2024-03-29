package com.jschramk.JVMath.runtime.math_engine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.Parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Rule<T> {

    private static final String FILE = "file";
    private static final String LINE = "line";
    private static final String FIND = "find";
    private static final String STEPS = "steps";
    private static final String DESCRIPTION = "description";
    private static final String REPLACE = "replace";
    private static final String REQUIRE = "require";
    private static final String ID = "id";
    private static final String NEXT = "next";

    private final List<Step<T>> steps = new ArrayList<>();
    private T find;
    private int id = -1, nextId = -1, line = -1, fileId = -1;
    private VariableFilterMap filters;

    public Rule(T find) {
        this.find = find;
    }

    public static List<Rule<String>> getRules(InputStream inputStream) {
        JsonObject object = (JsonObject) JsonParser.parseReader(new InputStreamReader(inputStream));
        JsonArray array = object.get("rules").getAsJsonArray();
        List<Rule<String>> rules = new ArrayList<>();
        for (JsonElement element : array) {
            rules.add(fromJson((JsonObject) element));
        }
        return rules;
    }



    public static Rule<String> fromJson(JsonObject jsonObject) {

        String find = jsonObject.get(FIND).getAsString();

        Rule<String> ret = new Rule<>(find);

        JsonArray stepsArray = (JsonArray) jsonObject.get(STEPS);

        // iterate thru steps and add them to the rule
        for (JsonElement stepElement : stepsArray) {

            JsonObject stepObject = (JsonObject) stepElement;

            String replace = stepObject.get(REPLACE).getAsString();

            String description = null;
            if (stepObject.has(DESCRIPTION)) {
                description = stepObject.get(DESCRIPTION).getAsString();
            }

            Step<String> step = new Step<>(replace, description);
            ret.addStep(step);
        }

        // parse id
        if (jsonObject.has(ID)) {
            ret.setId(jsonObject.get(ID).getAsInt());
        }

        // parse follow-up rule
        if (jsonObject.has(NEXT)) {
            ret.setNextId(jsonObject.get(NEXT).getAsInt());
        }

        // parse requirements
        if (jsonObject.has(REQUIRE)) {

            JsonArray filters = (JsonArray) jsonObject.get(REQUIRE);

            for (JsonElement requirementElement : filters) {

                VariableFilter filter = VariableFilter.fromJson((JsonObject) requirementElement);

                ret.addFilter(filter);

            }

        }

        return ret;

    }

    public void addStep(Step<T> step) {
        steps.add(step);
    }

    public void addFilter(VariableFilter variableFilter) {
        if (filters == null) {
            filters = new VariableFilterMap();
        }
        filters.add(variableFilter);
    }

    public static <T> List<Rule<T>> convertRules(Parser parser, List<Rule<String>> stringRules,
        Class<T> type) throws ParserException {
        List<Rule<T>> rules = new ArrayList<>();
        for (Rule<String> stringRule : stringRules) {
            rules.add(convertRule(parser, stringRule, type));
        }
        return rules;
    }

    public static <T> Rule<T> convertRule(Parser parser, Rule<String> stringRule, Class<T> type)
        throws ParserException {
        T find = parser.parse(stringRule.find, type);
        Rule<T> rule = new Rule<>(find);
        for (Step<String> step : stringRule.steps) {
            rule.addStep(parser.parse(step.getReplace(), type), step.getDescription());
        }
        rule.filters = stringRule.filters;
        rule.id = stringRule.id;
        rule.nextId = stringRule.nextId;
        return rule;
    }

    public void addStep(T replace, String description) {
        steps.add(new Step<>(replace, description));
    }

    public static String toRuleDefinition(JsonObject jsonObject, Parser parser)
        throws ParserException {

        StringBuilder s = new StringBuilder();

        if (jsonObject.has(ID)) {
            s.append("@");
            s.append(jsonObject.get(ID).getAsString());
            s.append("\n");
        }

        String find = jsonObject.get(FIND).getAsString();

        if (parser.parse(find).is(Equation.class)) {
            s.append("solve");
        } else {
            s.append("simplify");
        }

        s.append(" (");
        s.append(find);
        s.append(") {\n");

        JsonArray stepsArray = (JsonArray) jsonObject.get(STEPS);

        for (JsonElement stepElement : stepsArray) {

            JsonObject stepObject = (JsonObject) stepElement;

            String replace = stepObject.get(REPLACE).getAsString();

            s.append("    ");
            s.append(replace);

            if (stepObject.has(DESCRIPTION)) {
                s.append(" : ");
                s.append(stepObject.get(DESCRIPTION).getAsString());
            }

            s.append("\n");

        }

        if (jsonObject.has(NEXT)) {
            s.append("    @");
            s.append(jsonObject.get(NEXT).getAsString());
            s.append("\n");
        }

        s.append("}");

        if (jsonObject.has(REQUIRE)) {

            s.append(" where {\n");

            JsonArray reqs = (JsonArray) jsonObject.get(REQUIRE);

            for (JsonElement ele : reqs) {

                JsonObject req = (JsonObject) ele;

                s.append("    ");
                s.append(req.get("variable").getAsString());
                s.append(": ");

                int numReqs = 0;

                if (req.has("contains solve")) {

                    if (numReqs > 0) {
                        s.append(", ");
                    }

                    s.append(req.get("contains solve").getAsBoolean() ? "is" : "not");
                    s.append(" f(#target)");

                    numReqs++;

                }

                if (req.has("type")) {

                    if (numReqs > 0) {
                        s.append(", ");
                    }

                    s.append("is ");
                    s.append(req.get("type").getAsString());

                    numReqs++;

                }

                if (req.has("not type")) {

                    if (numReqs > 0) {
                        s.append(", ");
                    }

                    s.append("not ");
                    s.append(req.get("not type").getAsString());

                    numReqs++;

                }

                if (req.has("value")) {

                    if (numReqs > 0) {
                        s.append(", ");
                    }

                    s.append("is = ");
                    s.append(req.get("value").getAsString());

                    numReqs++;

                }

                if (req.has("not value")) {

                    if (numReqs > 0) {
                        s.append(", ");
                    }

                    s.append("not = ");
                    s.append(req.get("value").getAsString());

                    numReqs++;

                }

                s.append("\n");

                Set<String> unsupportedKeys = req.keySet();
                unsupportedKeys.remove("contains solve");
                unsupportedKeys.remove("variable");
                unsupportedKeys.remove("type");
                unsupportedKeys.remove("not type");
                unsupportedKeys.remove("value");
                unsupportedKeys.remove("not value");

                if (unsupportedKeys.size() > 1) {
                    throw new IllegalArgumentException(
                        "ERROR: Unsupported keys: " + unsupportedKeys);
                }

            }

            s.append("}");

        }

        return s.toString();

    }

    public Class<?> getType() {
        return find.getClass();
    }

    public boolean hasNext() {
        return nextId != -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public List<Step<T>> getSteps() {
        return steps;
    }

    public int stepCount() {
        return steps.size();
    }

    public Step<T> getFinalStep() {
        return steps.get(steps.size() - 1);
    }

    public T getFind() {
        return find;
    }

    public VariableFilterMap getFilters() {
        return filters;
    }

}

package com.jschramk.JVMath.rewrite_resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.parse.Parser;
import com.jschramk.JVMath.rewrite_engine.Requirement;
import com.jschramk.JVMath.rewrite_engine.Step;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rule<T> {

  private static final String FIND = "find";
  private static final String STEPS = "steps";
  private static final String DESCRIPTION = "description";
  private static final String REPLACE = "replace";
  private static final String REQUIRE = "require";
  private static final String ID = "id";
  private final List<Step<T>> steps = new ArrayList<>();
  private T find;
  private int id = -1;
  private Map<String, Requirement> requirements = new HashMap<>();

  public Rule(T find) {
    this.find = find;
  }

  public static List<Rule<String>> getRules(InputStream inputStream) {
    JsonArray array = (JsonArray) JsonParser.parseReader(new InputStreamReader(inputStream));
    List<Rule<String>> rules = new ArrayList<>();
    for (JsonElement element : array) {
      rules.add(fromJson((JsonObject) element));
    }
    return rules;
  }

  public Class<?> getType() {
    return find.getClass();
  }

  public static <T> Rule<T> convertRule(Parser parser, Rule<String> stringRule, Class<T> type)
      throws ParserException {
    T find = parser.parse(stringRule.find, type);
    Rule<T> rule = new Rule<>(find);
    for (Step<String> step : stringRule.steps) {
      rule.step(parser.parse(step.getReplace(), type), step.getDescription());
    }
    rule.requirements = stringRule.requirements;
    rule.id = stringRule.id;
    return rule;
  }

  public static <T> List<Rule<T>> convertRules(Parser parser, List<Rule<String>> stringRules,
      Class<T> type) throws ParserException {
    List<Rule<T>> rules = new ArrayList<>();
    for (Rule<String> stringRule : stringRules) {
      rules.add(convertRule(parser, stringRule, type));
    }
    return rules;
  }

  public static Rule<String> fromJson(JsonObject jsonObject) {

    String find = jsonObject.get(FIND).getAsString();

    Rule<String> ret = new Rule<>(find);

    JsonArray stepsArray = (JsonArray) jsonObject.get(STEPS);

    for (JsonElement stepElement : stepsArray) {

      JsonObject stepObject = (JsonObject) stepElement;

      String replace = stepObject.get(REPLACE).getAsString();

      String description = null;

      if (stepObject.has(DESCRIPTION)) {
        description = stepObject.get(DESCRIPTION).getAsString();
      }

      Step<String> step = new Step<>(replace, description);

      ret.step(step);

    }

    if (jsonObject.has(ID)) {

      ret.setId(jsonObject.get(ID).getAsInt());

    }

    if (jsonObject.has(REQUIRE)) {

      JsonArray requireArray = (JsonArray) jsonObject.get(REQUIRE);

      for (JsonElement requirementElement : requireArray) {

        ret.require(Requirement.fromJson((JsonObject) requirementElement));

      }

    }

    return ret;

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Rule<T> step(Step<T> step) {
    steps.add(step);
    return this;
  }

  public Rule<T> step(T replace) {
    return step(replace, null);
  }

  public Rule<T> step(T replace, String description) {
    steps.add(new Step<>(replace, description));
    return this;
  }

  public Rule<T> require(Requirement requirement) {
    assert !requirements.containsKey(requirement.getVariable());
    requirements.put(requirement.getVariable(), requirement);
    return this;
  }

  public List<Step<T>> getSteps() {
    return steps;
  }

  public int stepCount() {
    return steps.size();
  }

  public Step<T> getLastStep() {
    return steps.get(steps.size() - 1);
  }

  public T getFind() {
    return find;
  }

  public Map<String, Requirement> getRequirements() {
    return requirements;
  }

  @Override public String toString() {

    StringBuilder s = new StringBuilder();

    s.append("Start: ");
    s.append(find);
    s.append('\n');

    for (Step<T> step : steps) {

      s.append(step);
      s.append('\n');

    }

    if (!requirements.isEmpty()) {

      s.append("Requirements: \n");

      for (Map.Entry<String, Requirement> entry : requirements.entrySet()) {

        s.append(entry.getValue());
        s.append('\n');

      }

    }

    return s.toString();

  }



}

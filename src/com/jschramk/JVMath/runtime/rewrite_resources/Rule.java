package com.jschramk.JVMath.runtime.rewrite_resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.Parser;
import com.jschramk.JVMath.runtime.rewrite_engine.Requirement;
import com.jschramk.JVMath.runtime.rewrite_engine.Requirements;
import com.jschramk.JVMath.runtime.rewrite_engine.Step;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Rule<T> {

  private static final String FIND = "find";
  private static final String STEPS = "steps";
  private static final String DESCRIPTION = "description";
  private static final String REPLACE = "replace";
  private static final String REQUIRE = "require";
  private static final String ID = "id";
  private static final String NEXT = "next";
  private final List<Step<T>> steps = new ArrayList<>();
  private T find;
  private int id = -1, nextId = -1;
  private Requirements requirements;

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

    if (jsonObject.has(NEXT)) {

      ret.setNextId(jsonObject.get(NEXT).getAsInt());

    }

    if (jsonObject.has(REQUIRE)) {

      JsonArray requireArray = (JsonArray) jsonObject.get(REQUIRE);

      for (JsonElement requirementElement : requireArray) {

        ret.require(Requirement.fromJson((JsonObject) requirementElement));

      }

    }

    return ret;

  }

  public Rule<T> step(Step<T> step) {
    steps.add(step);
    return this;
  }

  public Rule<T> require(Requirement requirement) {
    if (requirements == null) {
      requirements = new Requirements();
    }
    requirements.add(requirement);
    return this;
  }

  public static <T> List<Rule<T>> convertRules(
    Parser parser, List<Rule<String>> stringRules, Class<T> type
  ) throws ParserException {
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
      rule.step(parser.parse(step.getReplace(), type), step.getDescription());
    }
    rule.requirements = stringRule.requirements;
    rule.id = stringRule.id;
    rule.nextId = stringRule.nextId;
    return rule;
  }

  public Rule<T> step(T replace, String description) {
    steps.add(new Step<>(replace, description));
    return this;
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

  public Rule<T> step(T replace) {
    return step(replace, null);
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

  public Requirements getRequirements() {
    return requirements;
  }

}

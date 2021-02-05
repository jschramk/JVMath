package core.old;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.rewrite.Requirement;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class StringRule {

  private static final String FIND = "find";
  private static final String REPLACE = "replace";
  private static final String REQUIRE = "require";

  private final String find, replace;
  private final Map<String, Requirement> variableRequirements = new HashMap<>();

  public StringRule(String find, String replace) {
    this.find = find;
    this.replace = replace;
  }

  public static StringRule fromJson(JsonObject jsonObject) {
    String find = jsonObject.get(FIND).getAsString();
    String replace = jsonObject.get(REPLACE).getAsString();
    StringRule r = new StringRule(find, replace);
    if (jsonObject.has(REQUIRE)) {
      JsonArray array = (JsonArray) jsonObject.get(REQUIRE);
      for (int i = 0; i < array.size(); i++) {
        r.require(Requirement.fromJson((JsonObject) array.get(i)));
      }
    }
    return r;
  }

  public static List<StringRule> getRules(InputStream inputStream) {
    JsonArray array = (JsonArray) JsonParser.parseReader(new InputStreamReader(inputStream));
    List<StringRule> rules = new ArrayList<>();
    for (int i = 0; i < array.size(); i++) {
      rules.add(StringRule.fromJson((JsonObject) array.get(i)));
    }
    return rules;
  }

  public String getFind() {
    return find;
  }

  public String getReplace() {
    return replace;
  }

  public Map<String, Requirement> getVariableRequirements() {
    return variableRequirements;
  }

  public StringRule require(Requirement requirement) {
    assert !variableRequirements.containsKey(requirement.getVariable());
    variableRequirements.put(requirement.getVariable(), requirement);
    return this;
  }

  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    object.addProperty(FIND, find);
    object.addProperty(REQUIRE, replace);
    if (!variableRequirements.isEmpty()) {
      JsonArray array = new JsonArray();
      for (Map.Entry<String, Requirement> entry : variableRequirements.entrySet()) {
        array.add(entry.getValue().toJson());
      }
      object.add(REQUIRE, array);
    }
    return object;
  }

  @Override public String toString() {
    return "Find: " + find + ", Replace: " + replace;
  }
}

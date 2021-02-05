package com.jschramk.JVMath.rewrite_resources;

import com.google.gson.*;
import com.jschramk.JVMath.components.Equation;
import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.parse.ParseResult;
import com.jschramk.JVMath.parse.Parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RuleProcessor {

  private static int NEXT_ID = 0;

  private static StringBuilder classStringBuilder = new StringBuilder();
  private static Map<String, Integer> operandIds = new HashMap<>();
  private static Map<String, Integer> equationIds = new HashMap<>();

  public static void processFiles(File... files) throws IOException, ParserException {

    Parser p = Parser.getDefault();

    for (File f : files) {
      processFile(f, p);
    }

    writeIdFile();

  }

  private static void processFile(File file, Parser parser) throws IOException, ParserException {

    JsonArray array = (JsonArray) JsonParser.parseReader(new FileReader(file));

    editJsonArray(array, parser);

    Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    String path = file.getCanonicalPath();

    path = path.replaceFirst(".json", "_processed.json");

    File out = new File(path);

    if (!out.exists() && !out.createNewFile()) {
      throw new IOException("Unable to create file: " + out);
    }

    if (!out.canWrite() && !out.setWritable(true)) {
      throw new IOException("Unable to set file to writable: " + out);
    }

    FileWriter writer = new FileWriter(out);

    writer.write(gson.toJson(array));

    writer.close();

    if (!out.setWritable(false)) {
      System.out.println("WARNING: Unable to set file to read only: " + out);
    }

  }

  private static void writeIdFile() throws IOException {

    startTopClass();
    startClassDef();

    startSubclass("operand");
    startClassDef();
    addMap(operandIds);
    endClassDef();

    startSubclass("equation");
    startClassDef();
    addMap(equationIds);
    endClassDef();

    endClassDef();

    File file = new File("src/com/jschramk/JVMath/rewrite_resources/RuleId.java");

    if (!file.exists() && !file.createNewFile()) {
      throw new IOException("Unable to create file: " + file);
    }

    if (!file.canWrite() && !file.setWritable(true)) {
      throw new IOException("Unable to set file to writable: " + file);
    }

    FileWriter writer = new FileWriter(file);
    writer.write(classStringBuilder.toString());
    writer.close();

    if (!file.setWritable(false)) {
      System.out.println("WARNING: Unable to set file to read only: " + file);
    }

  }

  private static void startTopClass() {
    classStringBuilder.append("package com.jschramk.JVMath.rewrite_resources").append(';');
    classStringBuilder.append("public class ").append("RuleId");
  }

  private static void startClassDef() {
    classStringBuilder.append('{');
  }

  private static void endClassDef() {
    classStringBuilder.append('}');
  }

  private static void startSubclass(String name) {
    classStringBuilder.append("public static class ").append(name);
  }

  private static void addMap(Map<String, Integer> map) {
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      classStringBuilder.append("public static final int ").append(entry.getKey()).append(" = ")
          .append(entry.getValue()).append(";");
    }
  }

  private static void editJsonArray(JsonArray array, Parser parser) throws ParserException {

    for (JsonElement element : array) {

      JsonObject object = (JsonObject) element;

      String find = object.get("find").getAsString();

      ParseResult r;

      r = parser.parse(find);

      object.addProperty("find", r.getResultString());

      if (object.has("id")) {

        String idString = object.get("id").getAsString();

        int id = NEXT_ID++;

        if (r.is(Operand.class)) {
          operandIds.put(idString, id);
        } else {
          equationIds.put(idString, id);
        }

        object.addProperty("id", id);

      }

      JsonArray steps = (JsonArray) object.get("steps");

      for (JsonElement element1 : steps) {

        JsonObject object1 = (JsonObject) element1;

        String replace = object1.get("replace").getAsString();

        r = parser.parse(replace);

        object1.addProperty("replace", r.getResultString());

        if (!object1.has("description") && r.is(Equation.class)) {

          System.out.println(
              "WARNING: No description for find: \"" + find + "\", step replace \"" + replace
                  + "\"");

        }

      }

    }

    for (JsonElement element : array) {

      JsonObject object = (JsonObject) element;

      if (object.has("next")) {

        String idString = object.get("next").getAsString();

        int id = operandIds.getOrDefault(idString, equationIds.getOrDefault(idString, -1));

        if (id == -1) {
          throw new RuntimeException("Unknown next rule ID: " + idString);
        }

        object.addProperty("next", id);

      }

    }

  }

}

package com.jschramk.JVMath.utilities.compile;

import com.google.gson.*;
import com.jschramk.JVMath.runtime.components.Equation;
import com.jschramk.JVMath.runtime.components.Operand;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.ParseResult;
import com.jschramk.JVMath.runtime.parse.Parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonRuleProcessor {

private static int NEXT_ID = 0;

private static StringBuilder classStringBuilder = new StringBuilder();
private static Map<String, Integer> operandIds = new HashMap<>();
private static Map<String, Integer> equationIds = new HashMap<>();

public static void processFiles(String inputDir, String outputDir)
    throws IOException, ParserException {

    Path inDir = Paths.get(inputDir);

    Path outDir = Paths.get(outputDir);

    Parser p = Parser.getDefault();

    List<String> paths = Compile.getFilesOfType(inDir, "json");

    for (String s : paths) {

        File in = new File(s);

        String outName = outDir.toAbsolutePath() +
            "\\" +
            in.getName().replaceFirst("\\.json", ".processed.json");

        File out = new File(outName);

        processFile(in, out, p);
    }

    //writeIdFile();

}

private static void processFile(File input, File output, Parser parser)
    throws IOException, ParserException {

    JsonArray array = (JsonArray) JsonParser.parseReader(new FileReader(input));

    processJson(input, array, parser);

    Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    if (!output.exists() && !output.createNewFile()) {
        throw new IOException("Unable to create file: " + output);
    }

    if (!output.canWrite() && !output.setWritable(true)) {
        throw new IOException("Unable to set file to writable: " + output);
    }

    FileWriter writer = new FileWriter(output);

    writer.write(gson.toJson(array));

    writer.close();

    if (!output.setWritable(false)) {
        System.out.println("WARNING: Unable to set file to read only: " +
            output);
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

private static void processJson(File f, JsonArray array, Parser parser)
    throws ParserException {

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

                String msg = String.format(
                    "WARNING (%s): No description for rule: FIND[ %s ], step: REPLACE[ %s ]",
                    f.getName(),
                    find,
                    replace
                );

                System.out.println(msg);

            }

        }

    }

    for (JsonElement element : array) {

        JsonObject object = (JsonObject) element;

        if (object.has("next")) {

            String idString = object.get("next").getAsString();

            int id = operandIds.getOrDefault(idString,
                equationIds.getOrDefault(idString, -1)
            );

            if (id == -1) {
                throw new RuntimeException("Unknown next rule ID: " + idString);
            }

            object.addProperty("next", id);

        }

    }

}

private static void startTopClass() {
    classStringBuilder.append(
        "package com.jschramk.JVMath.runtime.rewrite_resources").append(';');
    classStringBuilder.append("public class ").append("RuleId");
}

private static void startClassDef() {
    classStringBuilder.append('{');
}

private static void startSubclass(String name) {
    classStringBuilder.append("public static class ").append(name);
}

private static void addMap(Map<String, Integer> map) {
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
        classStringBuilder.append("public static final int ")
            .append(entry.getKey())
            .append(" = ")
            .append(entry.getValue())
            .append(";");
    }
}

private static void endClassDef() {
    classStringBuilder.append('}');
}

}

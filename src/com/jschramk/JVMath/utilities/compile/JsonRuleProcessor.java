package com.jschramk.JVMath.utilities.compile;

import com.google.gson.*;
import com.jschramk.JVMath.runtime.components.Equation;
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

    private int nextRuleId = 0;
    private int nextFileId = 0;
    private Map<String, Integer> ruleIds = new HashMap<>();
    private Map<String, Integer> fileIds = new HashMap<>();
    private Map<String, JsonArray> rulesByAction = new HashMap<>();

    public void reset() {
        nextRuleId = 0;
        nextFileId = 0;
        ruleIds.clear();
        fileIds.clear();
    }

    public static void processFiles(String inputDir, String outputDir)
        throws IOException, ParserException {

        Path inDir = Paths.get(inputDir);

        Path outDir = Paths.get(outputDir);

        Parser p = Parser.getDefault();

        List<String> paths = Compile.getFilesOfType(inDir, "json");

        JsonRuleProcessor ruleProcessor = new JsonRuleProcessor();

        for (String s : paths) {

            File in = new File(s);

            ruleProcessor.addFile(in, p);
        }

        File out = new File(outDir.toAbsolutePath() + "\\rules.json");

        ruleProcessor.writeToFile(out);

    }

    private void writeToFile(File output) throws IOException {
        if (!output.exists() && !output.createNewFile()) {
            throw new IOException("Unable to create file: " + output);
        }

        if (!output.canWrite() && !output.setWritable(true)) {
            throw new IOException("Unable to set file to writable: " + output);
        }

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        JsonObject outputObject = new JsonObject();

        JsonObject fileMapping = new JsonObject();

        for (String fileName : fileIds.keySet()) {
            fileMapping.addProperty(fileIds.get(fileName) + "", fileName);
        }

        outputObject.add("files", fileMapping);

        JsonObject rulesObject = new JsonObject();

        for (String action : rulesByAction.keySet()) {

            rulesObject.add(action, rulesByAction.get(action));

        }

        outputObject.add("rules", rulesObject);

        FileWriter writer = new FileWriter(output);

        writer.write(gson.toJson(outputObject));

        writer.close();

        if (!output.setWritable(false)) {
            System.out.println("WARNING: Unable to set file to read only: " + output);
        }

    }

    private void addFile(File input, Parser parser) throws IOException, ParserException {

        JsonObject inputObject = (JsonObject) JsonParser.parseReader(new FileReader(input));

        addJSON(inputObject, parser);

    }

    private void addJSON(JsonObject inputObject, Parser parser) throws ParserException {

        String sourceFileName = inputObject.get("file").getAsString();

        if (!fileIds.containsKey(sourceFileName)) {
            fileIds.put(sourceFileName, nextFileId++);
        }

        JsonArray array = inputObject.get("rules").getAsJsonArray();

        // first pass to create elements
        for (JsonElement element : array) {

            JsonObject object = (JsonObject) element;

            int sourceFileLine = object.get("line").getAsInt();

            String find = object.get("find").getAsString();

            ParseResult r = parser.parse(find);

            object.addProperty("find", r.getResultString());

            if (object.has("id")) {

                String idString = object.get("id").getAsString();

                int id = nextRuleId++;

                ruleIds.put(idString, id);

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
                        "WARNING (%s:%d): Step has no description.\n\tRule: FIND[ %s ]\n\tStep: REPLACE[ %s ]",
                        sourceFileName, sourceFileLine, find, replace);

                    System.out.println(msg);

                }

            }

        }

        // second pass to link IDs
        for (JsonElement element : array) {

            JsonObject object = (JsonObject) element;

            if (object.has("next")) {

                String idString = object.get("next").getAsString();

                int id = ruleIds.getOrDefault(idString, -1);

                if (id == -1) {
                    throw new RuntimeException("Unknown next rule ID: " + idString);
                }

                object.addProperty("next", id);

            }

            String action = object.get("action").getAsString();

            if (!rulesByAction.containsKey(action)) {

                //                String msg = String.format(
                //                    "INFO (%s:%d): New.\n\tRule: FIND[ %s ]\n\tStep: REPLACE[ %s ]",
                //                    sourceFileName, sourceFileLine, find, replace);
                //
                //                System.out.println(msg);

                rulesByAction.put(action, new JsonArray());

            }

            object.remove("action");

            object.addProperty("file", fileIds.get(sourceFileName));

            rulesByAction.get(action).add(object);

        }



    }



}

package com.jschramk.JVMath.utilities.compile;

import com.google.gson.*;
import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.runtime.parse.Parser;
import com.jschramk.JVMath.runtime.rewrite_engine.Rule;
import com.jschramk.JVMath.utilities.antlr_gen.rule_parse.ruleSetLexer;
import com.jschramk.JVMath.utilities.antlr_gen.rule_parse.ruleSetParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleSetCompiler {

private static class Step {

    private String replace, description;

    public Step(String replace, String description) {
        this.replace = replace;
        this.description = description;
    }

    public String getReplace() {
        return replace;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return replace + (description != null ? " : " + description : "");
    }

}



private static class Requirement {

    private boolean requireContainsTarget;

    public Requirement(boolean requireContainsTarget) {
        this.requireContainsTarget = requireContainsTarget;
    }

    public boolean isRequireContainsTarget() {
        return requireContainsTarget;
    }

}



private static class Filter {

    private Map<String, Requirement> variableRequirements = new HashMap<>();

    public Requirement getRequirement(String variable) {
        return variableRequirements.get(variable);
    }

    public void putRequirement(String variable, Requirement requirement) {
        variableRequirements.put(variable, requirement);
    }

    public Set<String> getVariables() {
        return variableRequirements.keySet();
    }

}



private static class CodeRule {

    private String id, action, find, target, next;
    private List<Step> steps = new ArrayList<>();
    private Filter filter;

    public CodeRule(
        String id, String action, String find, String target, String... steps
    ) {
        this.id = id;
        this.action = action;
        this.find = find;
        this.target = target;




        for (int i = 0; i < steps.length; i++) {

            String step = steps[i].trim();

            if (step.matches("@[aA-zZ_][aA-zZ_0-9]*")) {
                this.next = step.replaceAll("@", "");
                continue;
            }

            String[] replaceDesc = step.split("[ \\t]*:[ \\t]*", 2);

            String replace = replaceDesc[0];

            String desc = replaceDesc.length > 1 ? replaceDesc[1] : null;




            this.steps.add(new Step(replace, desc));

        }

    }

    public JsonObject toJson() {

        JsonObject ret = new JsonObject();

        ret.addProperty("find", find);

        if (id != null) {
            ret.addProperty("id", id);
        }

        if (next != null) {
            ret.addProperty("next", next);
        }

        JsonArray steps = new JsonArray();

        for (Step s : this.steps) {

            JsonObject step = new JsonObject();

            step.addProperty("replace", s.getReplace());

            if (s.getDescription() != null) {

                String desc = s.getDescription();

                // TODO: make sure to update all code to new inline render syntax
                Pattern newInlineRender = Pattern.compile("\\$\\{.*?}");

                Matcher m = newInlineRender.matcher(desc);

                StringBuilder builder = new StringBuilder();

                int end = 0;

                while (m.find()) {

                    String found = m.group();

                    builder.append(desc.substring(end, m.start()));

                    end = m.end();

                    String inside = found.substring(2, found.length()-1);

                    // TEMPORARY SOLUTION... just converting new syntax from .rules file to old syntax for .json
                    String replacement = "${" + inside.replaceAll("#", Matcher.quoteReplacement("$")) + "}$";

                    builder.append(replacement);

                }

                builder.append(desc.substring(end));

                step.addProperty("description", builder.toString());
            }

            steps.add(step);

        }

        ret.add("steps", steps);

        if (filter != null) {

            JsonArray requirements = new JsonArray();

            Set<String> vars = filter.getVariables();

            for (String var : vars) {

                JsonObject req = new JsonObject();

                req.addProperty("variable", var);

                Requirement r = filter.getRequirement(var);

                req.addProperty("contains solve", r.isRequireContainsTarget());

                requirements.add(req);

            }

            ret.add("require", requirements);

        }

        return ret;

    }

    public String getId() {
        return id;
    }

    public String getFind() {
        return find;
    }

    public String getTarget() {
        return target;
    }

    public String getAction() {
        return action;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "Definition{" +
            "id='" +
            id +
            '\'' +
            ", action='" +
            action +
            '\'' +
            ", find='" +
            find +
            '\'' +
            ", target='" +
            target +
            '\'' +
            ", steps=" +
            steps +
            '}';
    }

}

public static void convertJsonToCode(File input, File output)
    throws IOException, ParserException {

    System.out.println(String.format(
        "Converting %s to rule set %s...",
        input.getName(),
        output.getName()
    ));

    // get
    JsonArray array = (JsonArray) JsonParser.parseReader(new FileReader(input));

    Parser p = Parser.getDefault();

    FileWriter f = new FileWriter(output);

    for (int i = 0; i < array.size(); i++) {

        String s = Rule.convertToCode(array.get(i).getAsJsonObject(), p);

        f.write(s);
        f.write("\n\n");

    }

    f.close();

    System.out.println("Conversion complete.");

}

public static CodeRule compileDefinition(ruleSetParser.R_definitionContext ctx) {

    int i = 0;

    String id = null, action, find, target = null, stepsString;

    if (ctx.getChild(i) instanceof ruleSetParser.IdContext) {
        id = ctx.getChild(i++).getText().replaceAll("@", "");
    }

    action = ctx.getChild(i++).getText();
    find = ctx.getChild(i++).getText();
    find = find.substring(1, find.length() - 1).trim();


    if (ctx.getChild(i) instanceof ruleSetParser.R_target_specifierContext) {
        ParseTree specifier = ctx.getChild(i++);
        target = specifier.getChild(1).getText();
        target = target.substring(1, target.length() - 1).trim();
    }

    stepsString = ctx.getChild(i++).getText();
    stepsString = stepsString.substring(1, stepsString.length() - 1).trim();

    return new CodeRule(
        id,
        action,
        find,
        target,
        stepsString.split("[\\r\\n]+")
    );

}

public static Filter compileFilter(ruleSetParser.R_filterContext ctx) {

    String filterText = ctx.getChild(1).getText();
    filterText = filterText.substring(1, filterText.length() - 1).trim();

    String[] filterLines = filterText.split("[\\r\\n]+");

    Pattern functionPattern = Pattern.compile("f\\(.*?\\)");

    Filter f = new Filter();

    for (int i = 0; i < filterLines.length; i++) {

        String[] nameRequirements = filterLines[i].trim()
            .split("[ \\t]*:[ \\t]*", 2);

        String name = nameRequirements[0];

        String requirements = nameRequirements[1];

        if (requirements.matches("is[ \\t]+f\\(.*?\\)")) {

            Matcher m = functionPattern.matcher(requirements);

            if (m.find()) {

                String match = m.group();

                String targetVariable = match.substring(2, match.length() - 1)
                    .trim();

                if (targetVariable.equals("#target")) {
                    f.putRequirement(name, new Requirement(true));
                } else {

                    //System.out.println("WARNING: ");

                }

            }

        } else if (requirements.matches("not[ \\t]+f\\(.*?\\)")) {

            Matcher m = functionPattern.matcher(requirements);

            if (m.find()) {

                String match = m.group();

                String targetVariable = match.substring(2, match.length() - 1)
                    .trim();

                if (targetVariable.equals("#target")) {
                    f.putRequirement(name, new Requirement(false));
                }

            }

        } else {
            throw new IllegalArgumentException("Bad requirement: \"" +
                requirements +
                "\"");
        }

    }

    return f;

}

public static CodeRule compileRule(ruleSetParser.R_ruleContext ctx) {

    ruleSetParser.R_definitionContext def = (ruleSetParser.R_definitionContext) ctx
        .getChild(0);

    CodeRule defObj = compileDefinition(def);

    if (ctx.getChildCount() > 1) {
        ruleSetParser.R_filterContext filter = (ruleSetParser.R_filterContext) ctx
            .getChild(1);

        Filter filterObj = compileFilter(filter);

        defObj.setFilter(filterObj);

    }

    return defObj;

}

public static List<CodeRule> compile(ruleSetParser.ParseContext ctx) {

    List<CodeRule> codeRules = new ArrayList<>();

    for (int i = 0; i < ctx.getChildCount(); i++) {

        ParseTree p = ctx.getChild(i);

        if (p instanceof ruleSetParser.R_ruleContext) {

            CodeRule r = compileRule((ruleSetParser.R_ruleContext) p);

            codeRules.add(r);

        }

    }

    return codeRules;

}

public static void compileFiles(String inputDir, String outputDir)
    throws IOException {

    Path inDir = Paths.get(inputDir);

    Path outDir = Paths.get(outputDir);

    List<String> paths = Compile.getFilesOfType(inDir, "rules");

    for (String s : paths) {

        File in = new File(s);

        String outName = outDir.toAbsolutePath() +
            "\\" +
            in.getName().replaceFirst("\\.rules", ".json");

        File out = new File(outName);

        compileFile(in, out);

    }
}

public static void compileFile(File input, File output) throws IOException {

    System.out.println("Compiling:\t" + input.getName());

    // get input stream
    CharStream in = CharStreams.fromPath(input.toPath());

    // lexer and tokens
    ruleSetLexer lexer = new ruleSetLexer(in);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // parser from tokens
    ruleSetParser parser = new ruleSetParser(tokens);

    // parse
    ruleSetParser.ParseContext parse = parser.parse();

    // compile rules
    List<CodeRule> codeRules = compile(parse);

    // create json output
    JsonArray jsonOutput = new JsonArray();

    for (CodeRule r : codeRules) {
        jsonOutput.add(r.toJson());
    }

    FileWriter f = new FileWriter(output);

    Gson gson = new GsonBuilder().setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    f.write(gson.toJson(jsonOutput));

    f.close();

    System.out.println("Wrote:\t\t" + output.getName());

}



private static void describe(ParseTree tree) {
    describe(tree, 0);
}

private static void describe(ParseTree tree, int level) {

    StringBuilder s = new StringBuilder();

    for (int i = 0; i < level; i++) {
        s.append('\t');
    }

    System.out.println(

        s.toString() +
            "(" +
            tree.getClass().getSimpleName() +
            "): " +
            "\"" +
            tree.getText()
                .replaceAll("[\\r\\n]+", " ")
                .replaceAll("[\\t ]+", " ") +
            "\"");
    for (int i = 0; i < tree.getChildCount(); i++) {

        ParseTree child = tree.getChild(i);

        describe(child, level + 1);

    }
}

}

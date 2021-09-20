package com.jschramk.JVMath.parse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jschramk.JVMath.antlr_gen.rule_parse.ruleSetLexer;
import com.jschramk.JVMath.antlr_gen.rule_parse.ruleSetParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleSetCompiler {


public static void main(String[] args) throws IOException {

    compileFile(
        "src/com/jschramk/JVMath/rule_sets/basic.rules",
        "src/com/jschramk/JVMath/rewrite_jsons/test_out.json"
    );

}



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



private static class Rule {

    private String id, action, find, target, next;
    private List<Step> steps = new ArrayList<>();
    private Filter filter;

    public Rule(
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
                step.addProperty("description", s.getDescription());
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

public static Rule compileDefinition(ruleSetParser.R_definitionContext ctx) {

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

    return new Rule(id, action, find, target, stepsString.split("[\\r\\n]+"));

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

                /*System.out.println(name +
                    " must be a function of " +
                    targetVariable);*/

                if (targetVariable.equals("#target")) {
                    f.putRequirement(name, new Requirement(true));
                }

            }

        } else if (requirements.matches("not[ \\t]+f\\(.*?\\)")) {

            Matcher m = functionPattern.matcher(requirements);

            if (m.find()) {

                String match = m.group();

                String targetVariable = match.substring(2, match.length() - 1)
                    .trim();

                /*System.out.println(name +
                    " must not be a function of " +
                    targetVariable);*/

                if (targetVariable.equals("#target")) {
                    f.putRequirement(name, new Requirement(true));
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

public static Rule compileRule(ruleSetParser.R_ruleContext ctx) {

    ruleSetParser.R_definitionContext def = (ruleSetParser.R_definitionContext) ctx
        .getChild(0);

    Rule defObj = compileDefinition(def);


    List<Step> steps = defObj.getSteps();

    //System.out.println(steps);


    if (ctx.getChildCount() > 1) {
        ruleSetParser.R_filterContext filter = (ruleSetParser.R_filterContext) ctx
            .getChild(1);

        Filter filterObj = compileFilter(filter);

        defObj.setFilter(filterObj);

    }

    return defObj;

}

public static List<Rule> compile(ruleSetParser.ParseContext ctx) {

    List<Rule> rules = new ArrayList<>();

    for (int i = 0; i < ctx.getChildCount(); i++) {

        ParseTree p = ctx.getChild(i);

        if (p instanceof ruleSetParser.R_ruleContext) {

            Rule r = compileRule((ruleSetParser.R_ruleContext) p);

            rules.add(r);

        }

    }

    return rules;

}

public static void compileFile(String inputPath, String outputPath)
    throws IOException {


    // get input and output files
    File input = new File(inputPath);
    File output = new File(outputPath);

    System.out.println("Compiling file: " + input.getName());

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
    List<Rule> rules = compile(parse);

    // create json output
    JsonArray jsonOutput = new JsonArray();

    for (Rule r : rules) {
        jsonOutput.add(r.toJson());
    }

    FileWriter f = new FileWriter(output);

    Gson gson = new GsonBuilder().setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    f.write(gson.toJson(jsonOutput));

    f.close();

    System.out.println("Wrote file: " + output.getName());

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

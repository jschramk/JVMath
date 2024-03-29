package com.jschramk.JVMath.utilities.compile;

import com.jschramk.JVMath.runtime.exceptions.ParserException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Compile {

public static void main(String[] args) throws IOException, ParserException {


    /*RuleSetCompiler.convertJsonToCode(
        new File(
            "src\\com\\jschramk\\JVMath\\utilities\\rewrite_jsons\\simplify.json"),
        new File(
            "src\\com\\jschramk\\JVMath\\utilities\\rule_sets\\simplify.rules")
    );*/



    System.out.println("Starting to compile source rule sets...\n");

    RuleSetCompiler.compileFiles("src/com/jschramk/JVMath/utilities/rule_sets",
        "src/com/jschramk/JVMath/utilities/rewrite_jsons"
    );

    System.out.println("\nRule sets compiled.\n");

    System.out.println("Starting to package JSON rule sets...\n");

    JsonRuleProcessor.processFiles("src/com/jschramk/JVMath/utilities/rewrite_jsons",
        "src/com/jschramk/JVMath/runtime/rewrite_packages"
    );

    System.out.println("\nRule set packaging completed successfully.");

}

public static List<String> getFilesOfType(Path path, String fileExtension) throws IOException {

    if (!Files.isDirectory(path)) {
        throw new IllegalArgumentException("Path must be a directory!");
    }

    List<String> result;

    try (Stream<Path> walk = Files.walk(path)) {
        result = walk.filter(p -> !Files.isDirectory(p))
            .map(p -> p.toString().toLowerCase())
            .filter(f -> f.endsWith(fileExtension))
            .collect(Collectors.toList());
    }

    return result;
}

}

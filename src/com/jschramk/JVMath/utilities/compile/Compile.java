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

    System.out.println("Beginning to compile rule sets...\n");

    RuleSetCompiler.compileFiles(
        "src/com/jschramk/JVMath/utilities/rule_sets",
        "src/com/jschramk/JVMath/utilities/rewrite_jsons"
    );

    System.out.println("\nRule sets compiled.\n");

    System.out.println("Beginning to process rewrite JSON files...\n");

    JsonRuleProcessor.processFiles(
        "src/com/jschramk/JVMath/utilities/rewrite_jsons",
        "src/com/jschramk/JVMath/runtime/rewrite_packages"
    );

    System.out.println("\nRule set packaging completed successfully.");

}

public static List<String> getFilesOfType(Path path, String fileExtension)
    throws IOException {

    if (!Files.isDirectory(path)) {
        throw new IllegalArgumentException("Path must be a directory!");
    }

    List<String> result;

    try (Stream<Path> walk = Files.walk(path)) {
        result = walk.filter(p -> !Files.isDirectory(p))
            // this is a path, not string,
            // this only test if path end with a certain path
            //.filter(p -> p.endsWith(fileExtension))
            // convert path to string first
            .map(p -> p.toString().toLowerCase())
            .filter(f -> f.endsWith(fileExtension))
            .collect(Collectors.toList());
    }

    return result;
}

}

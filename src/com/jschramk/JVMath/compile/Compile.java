package com.jschramk.JVMath.compile;

import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.parse.RuleSetCompiler;
import com.jschramk.JVMath.rewrite_resources.RuleProcessor;

import java.io.IOException;

public class Compile {

public static void main(String[] args) throws IOException, ParserException {


    RuleProcessor.processFiles(
        "src/com/jschramk/JVMath/rewrite_resources/simplify.json",
        "src/com/jschramk/JVMath/rewrite_resources/solve.json"
    );

    System.out.println("Build prep completed successfully");

}

}

package com.jschramk.JVMath.buildprep;

import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.rewrite_resources.RuleProcessor;

import java.io.File;
import java.io.IOException;

public class BuildPrep {

  public static void main(String[] args) throws IOException, ParserException {

    RuleProcessor.processFiles(
        new File("src/com/jschramk/JVMath/rewrite_resources/simplify.json"),
        new File("src/com/jschramk/JVMath/rewrite_resources/solve.json")
    );

    System.out.println("Build prep completed successfully");

  }

}

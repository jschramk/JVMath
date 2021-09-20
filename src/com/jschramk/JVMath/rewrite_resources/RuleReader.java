package com.jschramk.JVMath.rewrite_resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleReader {

  // read a .rules file and covert to a .json file
  public static void main(String[] args) throws IOException {

    String path = "C:\\Users\\Jacob Schramkowski\\OneDrive\\Projects\\Solvable\\JVMath\\src\\com\\jschramk\\JVMath\\rewrite_resources\\test.rules";

    String fileContent = new String(Files.readAllBytes(new File(path).toPath()));

    Pattern filePattern = Pattern.compile("(#[aA-zZ_][aA-zZ0-9_](.*?))?solve(.*?)\\{[^}$]*}", Pattern.DOTALL);
    Pattern idPattern = Pattern.compile("#[aA-zZ_][aA-zZ0-9_]", Pattern.DOTALL);
    Pattern stepsPattern = Pattern.compile("solve(.*?)\\{(^(//.*\\n))*}", Pattern.DOTALL);
    Pattern requirementsPattern = Pattern.compile("where(.*?)\\{[^}$]*}", Pattern.DOTALL);

    Matcher fileMatcher = filePattern.matcher(fileContent);

    while (fileMatcher.find()) {

      String group = fileMatcher.group();

      System.out.println("GROUP ================================================");
      System.out.println(group);

      Matcher stepsMatcher = stepsPattern.matcher(group);

      if (stepsMatcher.find()) {

        String steps = stepsMatcher.group();

        System.out.println("steps:\n=====================================\n" + steps);

      }


    }

  }

}

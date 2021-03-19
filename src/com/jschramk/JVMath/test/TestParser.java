package com.jschramk.JVMath.test;

import com.jschramk.JVMath.components.FunctionDomain;
import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.parse.DefaultParser;
import com.jschramk.JVMath.parse.ParseResult;
import com.jschramk.JVMath.parse.Parser;
import com.jschramk.JVMath.utils.Utils;
import mathutils.MathUtils;

public class TestParser {

  private static double sumTime = 0;
  private static long sumChars = 0;
  private static int numParsed = 0;

  public static void main(String[] args) throws ParserException {

    Parser p = new DefaultParser(FunctionDomain.getDefault());

    for (int depth = 1; depth <= 15; depth++) {

      clearStats();

      for (int i = 0; i < 200; i++) {

        String input = Utils.randomExpression(depth).toString();

        test(p.parse(input));

      }

      System.out.println("Expression Depth: " + depth);
      printStats();
      System.out.println();

    }

  }

  private static void clearStats() {
    sumTime = 0;
    sumChars = 0;
    numParsed = 0;
  }

  private static void test(ParseResult result) {
    numParsed++;
    sumTime += result.getParseTimeMillis();
    sumChars += result.getInput().length();
  }

  private static void printStats() {

    double avgTime = sumTime / (double) numParsed;
    double avgLength = sumChars / (double) numParsed;
    double totalKb = sumChars / 1024d;
    double msPerKb = sumTime / (totalKb);

    //System.out.println("\tStatistics: ");
    System.out.println("\tNumber of strings parsed:   " + numParsed);
    System.out.println("\tTotal parse time:           " + MathUtils.format(sumTime) + " ms");
    System.out.println("\tAverage input length:       " + MathUtils.format(avgLength) + " chars");
    System.out.println("\tAverage parse time:         " + MathUtils.format(avgTime) + " ms");
    System.out.println("\tTotal memory parsed:        " + MathUtils.format(totalKb) + " kB");
    System.out.println("\tAverage parse time per kB:  " + MathUtils.format(msPerKb) + " ms");

  }

}

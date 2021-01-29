package com.jschramk.JVMath.test;

import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.parse.ParseResult;
import com.jschramk.JVMath.parse.Parser;

public class Test {

  public static void main(String[] args) throws ParserException {

    Parser p = Parser.getDefault();

    ParseResult result = p.parse("1 2 3 4");

    System.out.println(result);

  }



}
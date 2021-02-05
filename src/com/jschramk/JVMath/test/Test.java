package com.jschramk.JVMath.test;

import com.jschramk.JVMath.components.Operand;
import com.jschramk.JVMath.exceptions.ParserException;
import com.jschramk.JVMath.parse.Parser;

public class Test {

  public static void main(String[] args) throws ParserException {

    Parser p = Parser.getDefault();

    Operand o1 = p.parse("(1 + 2) * (3 + 4)").to(Operand.class);

    System.out.println(o1.toTreeString());
    System.out.println(o1.treeDepth());


  }



}
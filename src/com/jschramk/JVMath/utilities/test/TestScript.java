package com.jschramk.JVMath.utilities.test;

import com.jschramk.JVMath.runtime.exceptions.ParserException;
import com.jschramk.JVMath.utilities.script.Script;

import java.util.List;

public class TestScript {

  public static void main(String[] args) throws ParserException {

    String script = "x = e\nprint x + 5";

    List<String> outs = Script.execute(script);

    for (String s : outs) {
      System.out.println(s);
    }

  }

}

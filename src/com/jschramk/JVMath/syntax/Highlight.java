package com.jschramk.JVMath.syntax;

import java.util.ArrayList;
import java.util.List;

public class Highlight {

  private boolean highlight;
  private String string;

  public Highlight(String s, boolean highlight) {
    string = s;
    this.highlight = highlight;
  }

  public static String removeTokens(String string) {

    List<Highlight> list = sectionsIn(string);

    StringBuilder s = new StringBuilder();

    for (Highlight section : list) {
      s.append(section.getString());
    }

    return s.toString();

  }

  public static List<Highlight> sectionsIn(String input) {
    return sectionsIn(input, "${", "}$");
  }

  public String getString() {
    return string;
  }

  private static List<Highlight> sectionsIn(String input, String start, String end) {

    List<Highlight> sections = new ArrayList<>();

    int last = 0;

    String sub;

    while (true) {

      int startPos = input.indexOf(start, last);

      if (startPos == -1) break;

      sub = input.substring(last, startPos);

      if (sub.length() > 0) {
        sections.add(new Highlight(sub, false));
      }

      int endPos = input.indexOf(end, startPos);

      if (endPos == -1) break;

      last = endPos + end.length();

      sub = input.substring(startPos + start.length(), endPos);

      if (sub.length() > 0) {
        sections.add(new Highlight(sub, true));
      }

    }

    sub = input.substring(last);

    if (sub.length() > 0) {
      sections.add(new Highlight(sub, false));
    }

    return sections;

  }

  public boolean shouldHighlight() {
    return highlight;
  }

  @Override
  public String toString() {
    return "\"" + string + "\": " + highlight;
  }

}

package com.jschramk.JVMath.runtime.rewrite_engine;

public class Step<T> {

  private T replace;
  private String description;

  public Step(T replace, String description) {
    this.replace = replace;
    this.description = description;
  }

  public Step(T replace) {
    this.replace = replace;
  }

  public T getReplace() {
    return replace;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {

    if (description != null) {
      return description + "\n" + replace;
    }

    return replace.toString();

  }

}

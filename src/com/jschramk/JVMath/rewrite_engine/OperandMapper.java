package com.jschramk.JVMath.rewrite_engine;

import com.jschramk.JVMath.components.Operand;

public class OperandMapper {

  private Knowns knowns = new Knowns();
  private Unknowns unknowns = new Unknowns();

  public void add(Operand unknown, Operand choice) {
    unknowns.add(unknown, choice);
  }

  public void remove(Operand unknown, Operand choice) {
    unknowns.remove(unknown, choice);
  }

  public Knowns getResult() {
    if (!unknowns.mapAll(knowns)) {
      return null;
    }
    return knowns;
  }

}

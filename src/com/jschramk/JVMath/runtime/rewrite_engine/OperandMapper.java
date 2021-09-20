package com.jschramk.JVMath.runtime.rewrite_engine;

import com.jschramk.JVMath.runtime.components.Operand;

public class OperandMapper {

  private Knowns knowns = new Knowns();
  private Unknowns unknowns = new Unknowns(knowns);

  public void add(Operand unknown, Operand choice) {
    unknowns.add(unknown, choice);
  }

  public void remove(Operand unknown, Operand choice) {
    unknowns.remove(unknown, choice);
  }

  public Knowns getResult() {
    if (!unknowns.mapAll()) {
      return null;
    }
    return knowns;
  }

}

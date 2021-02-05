package com.jschramk.JVMath.old;

import com.jschramk.JVMath.components.Operand;

@Deprecated
public class AnalogMapper {

  private Knowns_Old knownsOld = new Knowns_Old();
  private Unknowns_Old unknownsOld = new Unknowns_Old();

  public void add(Operand unknown, Operand choice) {
    unknownsOld.add(unknown, choice);
  }

  public void remove(Operand unknown, Operand choice) {
    unknownsOld.remove(unknown, choice);
  }

  public Knowns_Old getResult() {
    if (!unknownsOld.mapAll(knownsOld)) {
      return null;
    }
    return knownsOld;
  }

}

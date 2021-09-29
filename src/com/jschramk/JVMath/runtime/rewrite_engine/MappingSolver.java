package com.jschramk.JVMath.runtime.rewrite_engine;

import com.jschramk.JVMath.runtime.components.Operand;

public class MappingSolver {

  private SolvedMappings solvedMappings = new SolvedMappings();
  private UnsolvedMappings unsolvedMappings = new UnsolvedMappings(solvedMappings);

  public void add(Operand unknown, Operand choice) {
    unsolvedMappings.add(unknown, choice);
  }

  public void remove(Operand unknown, Operand choice) {
    unsolvedMappings.remove(unknown, choice);
  }

  public SolvedMappings compute() {
    if (!unsolvedMappings.mapAll()) {
      return null;
    }
    return solvedMappings;
  }

}

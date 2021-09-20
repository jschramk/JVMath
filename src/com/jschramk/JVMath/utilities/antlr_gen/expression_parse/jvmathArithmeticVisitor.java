// Generated from C:/Users/Jacob Schramkowski/OneDrive/Projects/Solvable/JVMath/src/com/jschramk/JVMath/parse\jvmathArithmetic.g4 by ANTLR 4.9
package com.jschramk.JVMath.utilities.antlr_gen.expression_parse;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link jvmathArithmeticParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface jvmathArithmeticVisitor<T> extends ParseTreeVisitor<T> {

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#equation}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitEquation(jvmathArithmeticParser.EquationContext ctx);

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#sum}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitSum(jvmathArithmeticParser.SumContext ctx);

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#product}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitProduct(jvmathArithmeticParser.ProductContext ctx);

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#signedFactor}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitSignedFactor(jvmathArithmeticParser.SignedFactorContext ctx);

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#factor}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitFactor(jvmathArithmeticParser.FactorContext ctx);

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#signedAtom}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitSignedAtom(jvmathArithmeticParser.SignedAtomContext ctx);

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#atom}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitAtom(jvmathArithmeticParser.AtomContext ctx);

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#literal}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitLiteral(jvmathArithmeticParser.LiteralContext ctx);

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#variable}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitVariable(jvmathArithmeticParser.VariableContext ctx);

  /**
   * Visit a parse tree produced by {@link jvmathArithmeticParser#comparator}.
   *
   * @param ctx the parse tree
   *
   * @return the visitor result
   */
  T visitComparator(jvmathArithmeticParser.ComparatorContext ctx);

}
// Generated from C:/Users/Jacob Schramkowski/OneDrive/Projects/Solvable/JVMath/src/com/jschramk/JVMath/parse\ruleSet.g4 by ANTLR 4.9.1
package com.jschramk.JVMath.antlr_gen.rule_parse;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ruleSetParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ruleSetVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ruleSetParser#parse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParse(ruleSetParser.ParseContext ctx);
	/**
	 * Visit a parse tree produced by {@link ruleSetParser#r_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitR_rule(ruleSetParser.R_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link ruleSetParser#r_definition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitR_definition(ruleSetParser.R_definitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ruleSetParser#r_filter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitR_filter(ruleSetParser.R_filterContext ctx);
	/**
	 * Visit a parse tree produced by {@link ruleSetParser#r_target_specifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitR_target_specifier(ruleSetParser.R_target_specifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link ruleSetParser#r_action_content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitR_action_content(ruleSetParser.R_action_contentContext ctx);
	/**
	 * Visit a parse tree produced by {@link ruleSetParser#id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(ruleSetParser.IdContext ctx);
}
// Generated from Function.g4 by ANTLR 4.4
package parsing;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link FunctionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface FunctionVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link FunctionParser#add}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdd(@NotNull FunctionParser.AddContext ctx);
	/**
	 * Visit a parse tree produced by {@link FunctionParser#func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc(@NotNull FunctionParser.FuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link FunctionParser#mul}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMul(@NotNull FunctionParser.MulContext ctx);
	/**
	 * Visit a parse tree produced by {@link FunctionParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(@NotNull FunctionParser.TermContext ctx);
}
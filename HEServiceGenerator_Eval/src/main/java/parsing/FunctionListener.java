// Generated from Function.g4 by ANTLR 4.4
package parsing;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link FunctionParser}.
 */
public interface FunctionListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link FunctionParser#add}.
	 * @param ctx the parse tree
	 */
	void enterAdd(@NotNull FunctionParser.AddContext ctx);
	/**
	 * Exit a parse tree produced by {@link FunctionParser#add}.
	 * @param ctx the parse tree
	 */
	void exitAdd(@NotNull FunctionParser.AddContext ctx);
	/**
	 * Enter a parse tree produced by {@link FunctionParser#func}.
	 * @param ctx the parse tree
	 */
	void enterFunc(@NotNull FunctionParser.FuncContext ctx);
	/**
	 * Exit a parse tree produced by {@link FunctionParser#func}.
	 * @param ctx the parse tree
	 */
	void exitFunc(@NotNull FunctionParser.FuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link FunctionParser#mul}.
	 * @param ctx the parse tree
	 */
	void enterMul(@NotNull FunctionParser.MulContext ctx);
	/**
	 * Exit a parse tree produced by {@link FunctionParser#mul}.
	 * @param ctx the parse tree
	 */
	void exitMul(@NotNull FunctionParser.MulContext ctx);
	/**
	 * Enter a parse tree produced by {@link FunctionParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(@NotNull FunctionParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link FunctionParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(@NotNull FunctionParser.TermContext ctx);
}
package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.CommonTokenStream;

/**
 * An abstract implementation of Dijkstra's shunting-yard algorithm to convert an arithmetic function into reverse polish-notation that
 * can then be processed further.
 * @author Philip Kaiser
 *
 */
public abstract class OperatorPrecedenceParser {
	
	Map<String, Integer> precedences = new HashMap<>();
	
	public OperatorPrecedenceParser() {
		precedences.put("*", 2);
		precedences.put("/", 2);
		precedences.put("+", 1);
		precedences.put("-", 1);
	}

	public abstract ParsingResult parseTokenStream(CommonTokenStream tokenStream);

	/**
	 * Converts tokens into RPN.
	 * @param tokenStream Stream of read tokens
	 * @return An instance of {@link IntermediateResult}.
	 */
	protected IntermediateResult createRpnFromStream(CommonTokenStream tokenStream) {
		Stack<String> stack = new Stack<>();
		//StringBuilder output = new StringBuilder();
		List<String> output = new ArrayList<>();
		Map<String, String> constantsMap = new HashMap<>();
		Map<String, String> constantsMapReverse = new HashMap<>();
		List<String> variables = new ArrayList<>();		
		
		tokenStream.fill();
		for(int i = 0; i < tokenStream.size(); i++) {
			int tokenType = tokenStream.get(i).getType();
			String token = tokenStream.get(i).getText();
			switch(tokenType) {
			case FunctionLexer.NUMBER:
				String constant = token;
				constant = constant.replace(constant, "con" + (constantsMap.keySet().size() + 1)); 
				if(!constantsMap.containsValue(token)) {
					System.out.println("Parser: Constant " + constant + " is new. Adding to map.");
					constantsMap.put(constant, token);
					constantsMapReverse.put(token,  constant);
				}
				else {
					constant = constantsMapReverse.get(token);
				}
				output.add(constant);
				break;
			case FunctionLexer.VARIABLE:
				if(!variables.contains(token))
					variables.add(token);
				output.add(token);
				break;
			case FunctionLexer.OP:
				while(!stack.empty()) {
					String op = stack.peek();
					if(!op.equals("(") && precedences.get(op) > precedences.get(token)) {
						//output.append(stack.pop());
						output.add(stack.pop());
					}
					else
						break;
				}
				stack.push(token);
				break;
			case FunctionLexer.LP:
				stack.push(token);
				break;
			case FunctionLexer.RP:
				while(!stack.peek().equals("(")) {
					output.add(stack.pop());
				}	
				stack.pop(); //pop left bracket
				break;
			}
		}
		
		while(!stack.empty()) {
			//output.append(stack.pop());
			output.add(stack.pop());
		}
		System.out.println(String.join("", output));
		IntermediateResult result = new IntermediateResult(output, variables, constantsMap);
		return result;
	}
	
	
	/**
	 * Convert the RPN stack to a specific code implementation.
	 * @param tokenList List of parsed tokens
	 * @param precedences Precedence rules, associating each operator with a priority
	 * @param enableBootstrapping Indicates whether the code needs to include bootstrapping
	 * @return
	 */
	protected abstract List<String> convertRPNToCode(List<String> tokenList, Map<String, Integer> precedences, boolean enableBootstrapping);
	
	/**
	 * Generates a line of code representation for the given generic operation
	 * @param left Left side of the operation
	 * @param right Right side of the operation
	 * @param token Operator
	 * @return Code representation of the operation
	 */
	protected abstract String getCodeExpression(String left, String right, String token);
	
	/**
	 * Class representing a parsing result of the {@link OperatorPrecedenceParser}.
	 * @author Philip Kaiser
	 *
	 */
	protected class IntermediateResult {
		List<String> rpnStack;
		List<String> parsedVariables;
		Map<String, String> constantsMap;
		
		/**
		 * Class representing a parsing result of the {@link OperatorPrecedenceParser}.
		 * @param rpnStack Stack of the parsed tokens in RPN in order of occurrence
		 * @param parsedVariables List of all variable names
		 * @param constantsMap List of all constants and their associated values
		 */
		public IntermediateResult(List<String> rpnStack, List<String> parsedVariables, Map<String, String> constantsMap) {
			this.rpnStack = rpnStack;
			this.parsedVariables = parsedVariables;
			this.constantsMap = constantsMap;
		}
		public List<String> getRpnStack() {
			return rpnStack;
		}
		public void setRpnStack(List<String> rpnStack) {
			this.rpnStack = rpnStack;
		}
		public List<String> getParsedVariables() {
			return parsedVariables;
		}
		public void setParsedVariables(List<String> parsedVariables) {
			this.parsedVariables = parsedVariables;
		}
		
		public Map<String, String> getConstantsMap() {
			return constantsMap;
		}
		public void setConstantsMap(Map<String, String> constantsMap) {
			this.constantsMap = constantsMap;
		}
	}
}


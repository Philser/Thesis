package parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.CommonTokenStream;

import misc.Constants;
import predictor.HELibPredictor;

public class HELibParser extends OperatorPrecedenceParser {
	
	@Override
	public ParsingResult parseTokenStream(CommonTokenStream tokenStream) {

		IntermediateResult imResult = createRpnFromStream(tokenStream);
		List<String> rpnTokens = imResult.getRpnStack();
		List<String> variables = imResult.getParsedVariables();
		Map<String, String> constantsMap = imResult.getConstantsMap();
		
		// Is the non-bootstrappable context suited for this computation?
		HELibPredictor predictor = new HELibPredictor();
		predictor.predictNeedsBootstrapping(rpnTokens, Constants.ContextNonBootstrappable);
		
		if(predictor.needsBootstrapping())
			predictor.predictRuntime(rpnTokens, Constants.ContextBootstrappable);
		else
			predictor.predictRuntime(rpnTokens, Constants.ContextNonBootstrappable);
		
		List<String> generatedCodeLines = convertRPNToCode(rpnTokens, precedences, predictor.needsBootstrapping());
		System.out.println("Final code: " + generatedCodeLines);
		ParsingResult result = new ParsingResult(predictor, variables, constantsMap, generatedCodeLines);
		return result;
	}

	@Override
	protected List<String> convertRPNToCode(List<String> tokenList, Map<String, Integer> precedences,
			boolean enableBootstrapping) {
		List<String> codeLines = new ArrayList<>();
		
		Stack<String> stack = new Stack<>();
		String refVar = "";
		String expression;
		for(String token: tokenList) {
			if(precedences.containsKey(token)) {//token is an operator
				String left = stack.pop();
				String right = stack.pop();
				if(enableBootstrapping) {
					expression = getCodeExpression(left, right, "recrypt"); // add recrypt check before every op
					codeLines.add(expression);
				}
				expression = getCodeExpression(left, right, token);
				codeLines.add(expression);
				stack.push(left);
				refVar = left;
			}
			else {
				stack.push(token);
			}
		}
		codeLines.add("return " + refVar + ";");
		return codeLines;
	}

	@Override
	protected String getCodeExpression(String left, String right, String token) {
		StringBuilder expression = new StringBuilder();
		switch(token) {
		case "*": 
			expression.append("firstL = -" + left + ".log_of_ratio()/log(2.0);\n");
			expression.append("cout << \"Before MUL: \" << firstL << endl;\n");
			expression.append(left + ".multiplyBy(" + right + ");");
			expression.append("firstL = -" + left + ".log_of_ratio()/log(2.0);\n");
			expression.append("cout << \"After MUL: \" << firstL << endl;\n");
			break;
		case "/":
			expression.append(left + ".divideBy(" + right + ");");
			break;
		case "+":
			expression.append(left + ".addCtxt(" + right + ", false);");
			break;
		case "-":
			expression.append(left + ".addCtxt(" + right + ", true);");
			break;
		case "recrypt":
			// Find out if we need to recrypt one of the ciphertexts
			// Since noise gets computed just as the values in the ciphertexts, we
			// 	need to check both ciphertexts if their level is capable of another operation
			//	and if not we need to recrypt.
			if(left.equals(right)) {
				expression.append("firstL = -" + left + ".log_of_ratio()/log(2.0);\n");
				//expression.append("if(firstL <= recryptThreshold) {\n");
				expression.append("cout << \" firstL: \" << firstL << endl;\n");
				expression.append("if(firstL <= 50.0) {\n");
					expression.append("\tcout << \"###### L too low, recrypting\" << endl;\n");
					expression.append("\tpublicKey.thinReCrypt(" + left + ");\n");
					expression.append("\tcout << \"Recrypted. Proceeding\" << endl;\n");
				expression.append("}\n");
			}
			else {
				expression.append("firstL = -" + left + ".log_of_ratio()/log(2.0);\n");
				expression.append("secondL = -" + right + ".log_of_ratio()/log(2.0);\n");
				expression.append("cout << \" firstL: \" << firstL << endl; \n");
				expression.append("cout << \" secondL: \" << secondL << endl; \n");
				//expression.append("if(firstL <= secondL && firstL <= recryptThreshold) {\n");
				expression.append("if(firstL <= secondL && firstL <= 50.0) {\n");
				expression.append("\tcout << \"###### L too low, recrypting " + left + "\" << endl;\n");
					expression.append("\tpublicKey.thinReCrypt(" + left + ");\n");
					expression.append("\tcout << \"Recrypted. Proceeding\" << endl;\n");
				expression.append("} else if(firstL > secondL && secondL <= recryptThreshold){\n");
					expression.append("\tcout << \"###### L too low, recrypting " + right + "\" << endl;\n");
					expression.append("\tpublicKey.thinReCrypt(" + right + ");\n");
					expression.append("\tcout << \"Recrypted. Proceeding\" << endl;\n");
				expression.append("}\n");
			}
			break;
		}
		return expression.toString();
	}
}

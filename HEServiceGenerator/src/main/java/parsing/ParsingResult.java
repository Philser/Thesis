package parsing;

import java.util.List;
import java.util.Map;

import predictor.Predictor;

/**
 * Represents the result of parsing an arithmetic function. 
 * Contains lists of found variables, constants, the generated library specific code, and various predictions.
 * @author D072531
 *
 */
public class ParsingResult {
	private List<String> variablesList;
	private Map<String, String> constantsMap;
	private String function;
	private List<String> generatedCodeLines;
	private Predictor predictor;
	

	/**
	 * Represents the result of parsing an arithmetic function. 
	 * Contains lists of found variables, constants, the generated library specific code, and various predictions.
	 * @param predictor
	 * @param variablesList
	 * @param constantsList
	 * @param generatedCode
	 */
	public ParsingResult(Predictor predictor, List<String> variablesList, Map<String, String> constantsList, List<String> generatedCode) {
		this.variablesList = variablesList;
		this.constantsMap = constantsList;
		this.generatedCodeLines = generatedCode;
		this.predictor = predictor;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public List<String> getVariablesList() {
		return variablesList;
	}

	public void setVariablesList(List<String> variablesList) {
		this.variablesList = variablesList;
	}

	public Map<String, String> getConstantsList() {
		return constantsMap;
	}

	public void setConstantsList(Map<String, String> constantsList) {
		this.constantsMap = constantsList;
	}

	public List<String> getGeneratedCode() {
		return generatedCodeLines;
	}

	public void setGeneratedCode(List<String> generatedCodeLines) {
		this.generatedCodeLines = generatedCodeLines;
	}

	public Predictor getPredictor() {
		return predictor;
	}

	public void setPredictor(Predictor predictor) {
		this.predictor = predictor;
	}
}

